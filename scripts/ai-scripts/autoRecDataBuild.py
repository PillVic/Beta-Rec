import polars as pl

'''
auto-rec 仅根据用户评分数据，而不读取其他数据
'''
ratings_lazy = pl.scan_csv('./ml-latest/ratings.csv')

ratings_data = ratings_lazy.select(
        pl.col('userId'),
        pl.col('movieId'),
        pl.col('rating')
        ).select(pl.all().shuffle(seed=8848)).collect()

#dev:train:test=1:7:2
ratingsLength = ratings_data.shape[0]
devLength = int(ratingsLength*0.1)
trainLength = int(ratingsLength*0.7)
testLength = ratingsLength - devLength - trainLength

print("ratingsLength:{}, devLength:{}, trainLength:{}, testLength:{}".format(ratingsLength, devLength, trainLength, testLength))

ratings_dev_set = ratings_data[:devLength]
ratings_train_set = ratings_data[devLength:devLength+trainLength]
ratings_test_set = ratings_data[devLength+trainLength:]

ratings_dev_set.write_csv("./data-set/auto_rec_ratings_dev_set.csv")
ratings_train_set.write_csv("./data-set/auto_rec_ratings_train_set.csv")
ratings_test_set.write_csv("./data-set/auto_rec_ratings_test_set.csv")
