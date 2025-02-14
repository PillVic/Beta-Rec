import argparse

import polars as pl
import torch
import torch.nn as nn

"""
标准调用模式
1. 创建模型 python --mode create
2. 训练模型 python --mode train
"""

USER_MODEL_PATH = "./models/mlp_rank_user_model"
MOVIE_MODEL_PATH = "./models/mlp_rank_movie_model"
RANK_MODEL_PATH = "./models/mlp_rank_rank_user_movie_model"

RATING_TRAINNING_DATA_SET = "./data-set/auto_rec_ratings_train_set.csv"
RATING_VALIDATE_DATA_SET = "./data-set/auto_rec_ratings_test_set.csv"

maxUserId = (
    pl.scan_csv("./user_rating_count.csv")
    .select(pl.col("userId"))
    .collect()["userId"]
    .max()
)
maxMovieId = (
    pl.scan_csv("./movie_rating_count.csv")
    .select(pl.col("movieId"))
    .collect()["movieId"]
    .max()
)
genresNum = (
    pl.scan_csv("./genres.csv").select(pl.col("genres")).collect()["genres"].count()
)

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# hypter parameters
USER_MOVIE_VEC_LENGTH = genresNum * 5
SUB_MODEL_LAYER_NUM = 3
RANK_MODEL_LAYER_NUM = 10
RANK_MODEL_VEC_LENGTH = genresNum
#######


class MlpRankModel(nn.Module):
    def __init__(self, userNum, movieNum):
        super().__init__()

        self.userNum = userNum
        self.movieNum = movieNum

        self.subMovieModel = nn.Sequential(
            nn.Embedding(movieNum, USER_MOVIE_VEC_LENGTH),
        )
        self.subUserModel = nn.Sequential(
            nn.Embedding(userNum, USER_MOVIE_VEC_LENGTH),
        )
        for _ in range(SUB_MODEL_LAYER_NUM):
            self.subUserModel.append(
                nn.Sequential(
                    nn.Linear(USER_MOVIE_VEC_LENGTH, USER_MOVIE_VEC_LENGTH),
                    nn.BatchNorm1d(USER_MOVIE_VEC_LENGTH),
                    nn.LeakyReLU(0.1),
                )
            )
            self.subMovieModel.append(
                nn.Sequential(
                    nn.Linear(USER_MOVIE_VEC_LENGTH, USER_MOVIE_VEC_LENGTH),
                    nn.BatchNorm1d(USER_MOVIE_VEC_LENGTH),
                    nn.LeakyReLU(0.1),
                )
            )
        self.rankUserMovieModel = nn.Sequential(
            nn.Linear(USER_MOVIE_VEC_LENGTH * 2, RANK_MODEL_VEC_LENGTH),
            nn.BatchNorm1d(RANK_MODEL_VEC_LENGTH),
            nn.LeakyReLU(0.1),
        )
        for _ in range(RANK_MODEL_LAYER_NUM - 1):
            self.rankUserMovieModel.append(
                nn.Sequential(
                    nn.Linear(RANK_MODEL_VEC_LENGTH, RANK_MODEL_VEC_LENGTH),
                    nn.BatchNorm1d(RANK_MODEL_VEC_LENGTH),
                    nn.LeakyReLU(0.1),
                )
            )
        self.rankUserMovieModel.append(
            nn.Sequential(
                nn.Linear(RANK_MODEL_VEC_LENGTH, 1),
                nn.Tanh(),
            )
        )

    def load(self, userModelPath, movieModelPath, rankUserMovieModelPath):
        self.subUserModel.load_state_dict(
            torch.load(userModelPath, map_location=device, weights_only=False)
        )
        self.subUserModel.to(device)

        self.subMovieModel.load_state_dict(
            torch.load(movieModelPath, map_location=device, weights_only=False)
        )
        self.subMovieModel.to(device)

        self.rankUserMovieModel.load_state_dict(
            torch.load(rankUserMovieModelPath, map_location=device, weights_only=False)
        )
        self.rankUserMovieModel.to(device)

    def save(self, userModelPath, movieModelPath, rankUserMovieModelPath):
        torch.save(self.subUserModel.state_dict(), userModelPath)
        torch.save(self.subMovieModel.state_dict(), movieModelPath)
        torch.save(self.rankUserMovieModel.state_dict(), rankUserMovieModelPath)

    def forward(self, xs):
        userIds = xs[:, 0]
        movieIds = xs[:, 1]
        userVecs = self.subUserModel(userIds)
        movieVecs = self.subMovieModel(movieIds)

        userMovieVec = torch.cat((userVecs, movieVecs), dim=1)

        ranks = (self.rankUserMovieModel(userMovieVec) + 1) * 2.5
        return ranks


def validate(model, ques, ans, valNum):
    xs = torch.randint(0, ques.shape[0], (valNum,))
    inputs = ques[xs]
    targets = ans[xs]
    outputs = model(inputs)
    loss = nn.functional.mse_loss(outputs, targets)
    return loss


# 在colab没法保证一口气学完所有数据, 因此直接随机采样学更合理
def train(model, ques, ans, valQues, valAns, batchSize=200, trainNum=200, valSize=300):
    criterion = nn.MSELoss()
    adamOptimizer = torch.optim.Adam(model.parameters(), lr=1e-3, weight_decay=1e-5)
    for i in range(trainNum):
        xs = torch.randint(0, ques.shape[0], (batchSize,))
        inputs = ques[xs]
        targets = ans[xs]
        outputs = model(inputs)

        loss = criterion(outputs, targets)
        if i % 100 == 0:
            # if i % 500 == 0:
            complectRate = i * 1.0 / trainNum * 100
            valLoss = validate(model, valQues, valAns, valSize)
            print(
                "{}/{}, {:.2f}%, batchSize:{}, loss:{}, val loss:{}".format(
                    i, trainNum, complectRate, batchSize, loss, valLoss
                )
            )
        if i % 10000 == 0:
            model.save(userModelPath=USER_MODEL_PATH, movieModelPath=MOVIE_MODEL_PATH, rankUserMovieModelPath=RANK_MODEL_PATH)

        adamOptimizer.zero_grad()
        loss.backward()
        adamOptimizer.step()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--mode", type=str, required=True, help="create|train|write")
    parser.add_argument(
        "-batchSize", type=int, required=False, default=40, help="train batch size"
    )
    parser.add_argument(
        "-trainNum", type=int, required=False, default=200, help="train model time"
    )
    parser.add_argument(
        "-valSize", type=int, required=False, default=400, help="validate batch size"
    )

    args = parser.parse_args()

    mode = args.mode
    batchSize = args.batchSize
    trainNum = args.trainNum
    valSize = args.valSize

    model = MlpRankModel(userNum=maxUserId, movieNum=maxMovieId)

    print(
        "maxUserId:{}, maxMovieId:{}, userMovieVecLen:{}".format(
            maxUserId, maxMovieId, USER_MOVIE_VEC_LENGTH
        )
    )

    if mode == "create":
        model.save(
            userModelPath=USER_MODEL_PATH,
            movieModelPath=MOVIE_MODEL_PATH,
            rankUserMovieModelPath=RANK_MODEL_PATH,
        )

    elif mode == "train":
        model.load(
            userModelPath=USER_MODEL_PATH,
            movieModelPath=MOVIE_MODEL_PATH,
            rankUserMovieModelPath=RANK_MODEL_PATH,
        )

        rating_trainning_data_set = pl.scan_csv(RATING_TRAINNING_DATA_SET)
        ques = (
            rating_trainning_data_set.select(pl.col("userId"), pl.col("movieId"))
            .collect()
            .to_torch(dtype=pl.Int64)
            .to(device)
        )
        ans = (
            rating_trainning_data_set.select(pl.col("rating"))
            .collect()
            .to_torch(dtype=pl.Float32)
            .float()
            .to(device)
        )
        rating_validate_data_set = pl.scan_csv(RATING_VALIDATE_DATA_SET)
        valQues = (
            rating_validate_data_set.select(pl.col("userId"), pl.col("movieId"))
            .collect()
            .to_torch(dtype=pl.Int64)
            .to(device)
        )
        valAns = (
            rating_validate_data_set.select(pl.col("rating"))
            .collect()
            .to_torch(dtype=pl.Float32)
            .float()
            .to(device)
        )

        train(
            model=model,
            ques=ques,
            ans=ans,
            valAns=valAns,
            valQues=valQues,
            batchSize=batchSize,
            trainNum=trainNum,
            valSize=valSize,
        )
        model.save(
            userModelPath=USER_MODEL_PATH,
            movieModelPath=MOVIE_MODEL_PATH,
            rankUserMovieModelPath=RANK_MODEL_PATH,
        )
