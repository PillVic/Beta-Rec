package com.betarec.utils;

import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ArgMainBase implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ArgMainBase.class);

    public void parseArgsAndRun(String[] args){
        CmdLineParser parser = new CmdLineParser(this);
        try{
            logger.info("[ArgMainBase]: parsing args.");
            parser.parseArgument(args);

            run();
        }catch (Exception e){
            logger.error("[ArgMainBase ERROR]:{}", e.getLocalizedMessage());
        }
    }
}
