package com.betarec.utils;

import com.betarec.data.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class ParseFile {
    private static final int QUEUE_SIZE = 10000;
    private static final Logger logger = LoggerFactory.getLogger(ParseFile.class);

    private static final int CONSUME_THREADS = 10;
    private static final int MIN_BATCH_SIZE = 500;

    private static void readFile(String fileName,
                                 BlockingQueue<String> queue, AtomicBoolean shutdown) {
        try {
            InputStreamReader ir = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(ir);
            long produceNum = 0;
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

            br.close();
            logger.info("[{}]:BufferedReader close", fileName);
            ir.close();
            logger.info("[{}]:InputStreamReader close", fileName);
        } catch (Exception e) {
            logger.error("readFile error, fileName:{}", fileName, e);
        }
    }

    private static void parseLines(Consumer<List<String>> consumer,
                                   BlockingQueue<String> queue,
                                   AtomicBoolean shutdown, AtomicLong consumeNum) {
        try {
            List<String> lines = new ArrayList<>(QUEUE_SIZE);
            while (!shutdown.get() || !queue.isEmpty()) {
                String line = queue.poll(500, TimeUnit.MILLISECONDS);
                if(!ObjectUtils.isEmpty(line)){
                    lines.add(line);
                }
                if (lines.size() >= MIN_BATCH_SIZE) {
                    consumer.accept(lines);
                    consumeNum.addAndGet(lines.size());
                    lines.clear();
                    long consumeNumPresent = consumeNum.get();
                    if(consumeNumPresent %1000 <= 20){
                        logger.info("consume num:{}", consumeNumPresent);
                    }
                }
            }
            if (!lines.isEmpty()) {
                consumer.accept(lines);
                consumeNum.addAndGet(lines.size());
            }
            logger.info("consume done");
        } catch (Exception e) {
            logger.error("parseLines error, ", e);
        }
    }

    public static void parse(String fileName, Consumer<String> consumer) {
        try {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
            ThreadPoolExecutor pool = Resource.getResource().utilPool;
            AtomicBoolean shutdown = new AtomicBoolean(false);
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
            AtomicBoolean shutdown = new AtomicBoolean(false);
            ThreadPoolExecutor pool = Resource.getResource().utilPool;
            pool.submit(() -> readFile(fileName, queue, shutdown));
            List<Future<?>> parseFutures = new ArrayList<>(CONSUME_THREADS);
            logger.info("start parse:{}", queue.take());
            AtomicLong consumeNum = new AtomicLong(0);
            for (int thread = 0; thread < CONSUME_THREADS; thread++) {
                parseFutures.add(pool.submit(() -> parseLines(consumer, queue, shutdown, consumeNum)));
            }
            int threadNum = 0;
            for (var future : parseFutures) {
                future.get();
                threadNum +=1;
                logger.info("consume thread done:{}/{}", threadNum, CONSUME_THREADS);
            }
            logger.info("total consume line:{}", consumeNum.get());
        } catch (Exception e) {
            logger.error("batchParse ERROR", e);
        }
        logger.info("parse file done");
    }
}
