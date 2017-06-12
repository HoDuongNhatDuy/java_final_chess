package com.chess.network;

import java.io.*;
import java.net.Socket;

/**
 * Created by hoduo on 6/12/2017.
 */
public class TCPCustomClient extends TCPCustom
{
    public TCPCustomClient()
    {
    }

    @Override
    Socket findPartner() throws IOException
    {
        try
        {
            partner = new Socket("localhost", PORT);
            System.out.println("Server found");
            return partner;
        } catch (IOException e)
        {
            System.out.println("Server not found");
            return null;
        }
    }
}
