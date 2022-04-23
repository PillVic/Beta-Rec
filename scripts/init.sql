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
    `tag` VARCHAR(500) NOT NULL,
    `timestamp` TIMESTAMP NOT NULL,
    PRIMARY KEY (`userId`, `movieId`,`tag`, `timestamp`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `genome_tags`(
    `tagId` INTEGER NOT NULL,
    `tag` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`tagId`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;

--ratings和 genome_scores表数据量太大，不分表，改为分区
CREATE TABLE IF NOT EXISTS `genome_scores`(
    `movieId` INTEGER NOT NULL,
    `tagId` INTEGER NOT NULL,
    `relevance` DOUBLE NOT NULL,
    PRIMARY KEY (`movieId`, `tagId`),
    KEY (`movieId`),
    KEY(`tagId`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `ratings`(
    `userId` INTEGER NOT NULL,
    `movieId` INTEGER NOT NULL,
    `rating` DOUBLE NOT NULL,
    `timestamp` TIMESTAMP NOT NULL,
    PRIMARY KEY (`movieId`, `userId`, `timestamp`),
    KEY (`movieId`),
    KEY (`userId`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;
