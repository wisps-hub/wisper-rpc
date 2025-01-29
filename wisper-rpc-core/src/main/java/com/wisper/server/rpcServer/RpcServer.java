package com.wisper.server.rpcServer;

public interface RpcServer {
    void start(int port);
    void start();
    void stop();
}
