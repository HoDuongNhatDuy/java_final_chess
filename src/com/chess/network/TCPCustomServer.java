package com.chess.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by hoduo on 6/12/2017.
 */
public class TCPCustomServer extends TCPCustom
{
    final ServerSocket serverSocket;

    public TCPCustomServer() throws IOException
    {
        serverSocket = new ServerSocket(PORT);
    }

    @Override
    Socket findPartner() throws IOException
    {
        System.out.println("Waiting for a Client");

        partner = serverSocket.accept();

        System.out.println("Client found");
        return partner;
    }
}
