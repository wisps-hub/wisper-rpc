package com.wipser;


import com.wipser.service.UserService;
import com.wipser.service.impl.UserServiceImpl;
import com.wisper.server.provider.ServerProvider;
import com.wisper.server.rpcServer.RpcServer;
import com.wisper.server.rpcServer.impl.NettyRpcServer;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServerProvider serverProvider = new ServerProvider();
        serverProvider.registService(userService, false);

        RpcServer rpcServer = new NettyRpcServer(serverProvider);
        rpcServer.start();
    }
}
