package com.betarec.utils;

import com.betarec.data.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ParseFile {
    private static final int QUEUE_SIZE = 10000;
    private static final Logger logger = LoggerFactory.getLogger(ParseFile.class);

    private static final int CONSUME_THREADS = 10;

    private static void readFile(String fileName,
                                 BlockingQueue<String> queue, AtomicInteger shutdownNum) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int produceNum = 0;
            String line;
            while ((line = br.readLine()) != null) {
                queue.put(line);
                produceNum++;
            }
            logger.info("produceNum {}", produceNum);
            shutdownNum.incrementAndGet();
        } catch (Exception e) {
            logger.error("readFile error, fileName:{}", fileName, e);
        }
    }

    private static void parseLines(Consumer<List<String>> consumer,
                                   BlockingQueue<String> queue, AtomicInteger shutdownNum,
                                   int fileNum) {
        try {
            while (shutdownNum.get() != fileNum || !queue.isEmpty()) {
                List<String> lines = new ArrayList<>(queue.size());
                for (int i = 0; i < queue.size(); i++) {
                    String line = queue.poll(500, TimeUnit.MILLISECONDS);
                    lines.add(line);
                }
                consumer.accept(lines);
            }
        } catch (Exception e) {
            logger.error("parseLines error, ", e);
        }
    }

    public static void parse(String fileName, Consumer<String> consumer) {
        try {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
            ThreadPoolExecutor pool = Resource.getResource().utilPool;
            AtomicInteger shutdownNum = new AtomicInteger(0);
            pool.submit(() -> readFile(fileName, queue, shutdownNum));
            pool.submit(() -> {
                try {
                    logger.info("start consume:{}", queue.take());
                    int consumeNum = 0;
                    while (shutdownNum.get() == 0 || !queue.isEmpty()) {
                        int queueSize = queue.size();
                        for (int i = 0; i < queueSize; i++) {
                            String line = queue.take();
                            consumer.accept(line);
                            consumeNum++;
                        }
                        logger.info("queueSize:{}, consumeNum:{}", queueSize, consumeNum);
                    }
                    logger.info("consumeNum {}", consumeNum);
                } catch (Exception e) {
                    logger.error("parse consume error", e);
                }
            }).get();
        } catch (Exception e) {
            logger.error("parse error", e);
        }
    }

    public void batchParse(List<String> fileNames, Consumer<List<String>> consumer) {
        try {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
            AtomicInteger shutdownNum = new AtomicInteger(0);
            ThreadPoolExecutor pool = Resource.getResource().utilPool;
            for (var fileName : fileNames) {
                pool.submit(() -> readFile(fileName, queue, shutdownNum));
            }
            for (int thread = 0; thread < CONSUME_THREADS; thread++) {
                pool.submit(() -> parseLines(consumer, queue, shutdownNum, fileNames.size()));
            }
        } catch (Exception e) {
            logger.error("batchParse ERROR", e);
        }
    }
}
