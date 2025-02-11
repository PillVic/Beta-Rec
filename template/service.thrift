
namespace py gen.service
namespace java gen.service

struct ModelReq {
    1: required i32 userId
    2: required list<i32> movieIds
    3: required string modelName
}

struct MovieRankItem{
    1: required i32 movieId
    2: required double score
}

struct ModelResp {
    1: required i32 userId
    2: required list<MovieRankItem> movieRanks
}

service ModelService{
    string ping(1:string ping)
    ModelResp movieModelRank(1: ModelReq req)
}
