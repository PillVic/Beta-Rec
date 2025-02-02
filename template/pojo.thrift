
namespace java gen.data.pojo

struct Movie {
    1: required i32 movieId
    2: optional string title
    3: optional string genres
    4: optional i32 year
}

struct GenomeScore {
    1: required i32 movieId
    2: required i32 tagId
    3: optional double relevance
}

struct GenomeTag {
    1: required i32 tagId
    2: optional string tag
}

struct Link {
    1: required i32 movieId
    2: optional i32 imdbId
    3: optional i32 tmdbId
}

struct Rating {
    1: required i32 userId
    2: required i32 movieId
    3: optional double rating
    4: optional i64 timestamp
}

struct Tag {
    1: required i32 userId
    2: required i32 movieId
    3: optional string tag
    4: optional i64 timestamp
}
