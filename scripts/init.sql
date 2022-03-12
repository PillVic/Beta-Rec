--创建库和表

USE MovieLens;

CREATE TABLE IF NOT EXISTS `links`(
    `movieId` INTEGER NOT NULL,
    `imdbId` INTEGER,
    `tmdbId` INTEGER,
    PRIMARY KEY (`movieId`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `movies`(
    `movieId` INTEGER NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `genres` VARCHAR(200),
    `year` INTEGER NOT NULL,
    PRIMARY KEY (`movieId`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `tags`(
    `userId` INTEGER NOT NULL,
    `movieId` INTEGER NOT NULL,
    `tagId` DOUBLE NOT NULL,
    `timestamp` TIMESTAMP NOT NULL,
    PRIMARY KEY (`userId`, `timestamp`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `genome_tags`(
    `tagId` INTEGER NOT NULL,
    `tag` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`tagId`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;

--ratings和 genome_scores表数据量太大，决定分成一百张
CREATE TABLE IF NOT EXISTS `genome_scores_sample`(
    `movieId` INTEGER NOT NULL,
    `tagId` INTEGER NOT NULL,
    `relavance` DOUBLE NOT NULL,
    PRIMARY KEY (`movieId`, `tagId`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `ratings_sample`(
    `userId` INTEGER NOT NULL,
    `movieId` INTEGER NOT NULL,
    `rating` DOUBLE NOT NULL,
    `timestamp` TIMESTAMP NOT NULL,
    PRIMARY KEY (`movieId`, `userId`, `timestamp`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;
