import polars as pl

movies = pl.read_csv("./ml-latest/movies.csv")

genres = movies.select(pl.col("genres").str.split("|").explode().unique())
genres.write_csv("genres.csv")
