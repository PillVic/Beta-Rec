import polars as pl

ratings_lazy = pl.scan_csv('./ml-latest/ratings.csv')

user_rating_count = ratings_lazy.select(
    pl.col('userId'),
    pl.col('movieId')
).group_by(
    pl.col('userId')
).agg(pl.col('movieId').unique().count().alias('count')).sort('count').collect()
user_rating_count.write_csv("user_rating_count.csv")

movie_rating_count = ratings_lazy.select(
    pl.col('userId'),
    pl.col('movieId')
).group_by(
    pl.col('movieId')
).agg(pl.col('userId').unique().count().alias('count')).sort('count').collect()
movie_rating_count.write_csv("movie_rating_count.csv")
