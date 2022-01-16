package com.betarec.utils;

import com.betarec.data.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ParseFile {
    private static final int QUEUE_SIZE = 10000;
    private static final Logger logger = LoggerFactory.getLogger(ParseFile.class);

    public static void parse(String fileName, Consumer<String> consumer) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
            ThreadPoolExecutor pool = Resource.getResource().utilPool;
            AtomicBoolean shutdown = new AtomicBoolean(false);
            pool.submit(() -> {
                try {
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        logger.info("produce line:{}", line);
                        queue.put(line);
                    }
                    shutdown.set(true);
                } catch (Exception e) {
                    logger.error("parse produce error", e);
                }
            });
            pool.submit(() -> {
                try {
                    logger.info("start consume:{}", queue.take());
                    while (!shutdown.get() || !queue.isEmpty()) {
                        int queueSize = queue.size();
                        for (int i = 0; i < queueSize; i++) {
                            String line = queue.take();
                            logger.info("consume line:{}", line);
                            consumer.accept(line);
                        }
                    }
                } catch (Exception e) {
                    logger.error("parse consume error", e);
                }
            }).get();
        } catch (Exception e) {
            logger.error("parse error", e);
        }
    }
}
