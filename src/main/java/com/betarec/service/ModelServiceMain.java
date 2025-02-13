package com.betarec.service;

import gen.service.ModelReq;
import gen.service.ModelResp;
import gen.service.ModelService;
import org.apache.thrift.TException;

public class ModelServiceMain implements ModelService.Iface {
    @Override
    public String ping(String ping) throws TException {
        return "PONG";
    }

    @Override
    public ModelResp movieModelRank(ModelReq req) throws TException {
        return null;
    }
}
