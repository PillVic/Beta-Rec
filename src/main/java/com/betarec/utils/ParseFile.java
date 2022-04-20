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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ParseFile {
    private static final int QUEUE_SIZE = 10000;
    private static final Logger logger = LoggerFactory.getLogger(ParseFile.class);

    private static final int CONSUME_THREADS = 10;
    private static final int MIN_BATCH_SIZE = 50;

    private static void readFile(String fileName,
                                 BlockingQueue<String> queue, AtomicBoolean shutdown) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int produceNum = 0;
            List<String> lines = new ArrayList<>();
            shutdown.set(false);
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                if (lines.size() >= MIN_BATCH_SIZE) {
                    for (String s : lines) {
                        queue.put(s);
                        produceNum += lines.size();
                    }
                    lines.clear();
                }
            }
            if (!lines.isEmpty()) {
                for (var s : lines) {
                    queue.put(s);
                }
                lines.clear();
            }
            logger.info("produceNum {}", produceNum);
            shutdown.set(true);
        } catch (Exception e) {
            logger.error("readFile error, fileName:{}", fileName, e);
        }
    }

    private static void parseLines(Consumer<List<String>> consumer,
                                   BlockingQueue<String> queue, AtomicBoolean shutdownNum) {
        try {
            List<String> lines = new ArrayList<>(queue.size());
            while (!shutdownNum.get() || !queue.isEmpty()) {
                for (int i = 0; i < queue.size(); i++) {
                    String line = queue.poll(500, TimeUnit.MILLISECONDS);
                    lines.add(line);
                }
                if (lines.size() >= MIN_BATCH_SIZE) {
                    consumer.accept(lines);
                    lines.clear();
                }
            }
            if (!lines.isEmpty()) {
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
            AtomicBoolean shutdown = new AtomicBoolean(true);
            pool.submit(() -> readFile(fileName, queue, shutdown));
            pool.submit(() -> {
                try {
                    logger.info("start consume:{}", queue.take());
                    int consumeNum = 0;
                    while (!shutdown.get() || !queue.isEmpty()) {
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

    public static void batchParse(String fileName, Consumer<List<String>> consumer) {
        try {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
            AtomicBoolean shutdown = new AtomicBoolean(true);
            ThreadPoolExecutor pool = Resource.getResource().utilPool;
            pool.submit(() -> readFile(fileName, queue, shutdown));
            for (int thread = 0; thread < CONSUME_THREADS; thread++) {
                pool.submit(() -> parseLines(consumer, queue, shutdown));
            }
        } catch (Exception e) {
            logger.error("batchParse ERROR", e);
        }
    }
}
