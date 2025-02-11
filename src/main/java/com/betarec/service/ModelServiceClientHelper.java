package com.betarec.service;

import gen.service.ModelService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class ModelServiceClientHelper {
    public static ModelService.Client buildModelServiceClient(String host, int port) throws TTransportException {
        TTransport transport = new TBinaryProtocol(new TSocket(host, port)).getTransport();
        ModelService.Client client = new ModelService.Client(new TBinaryProtocol(transport));

        Runtime.getRuntime().addShutdownHook(new Thread(transport::close));

        transport.open();
        return client;
    }
}
