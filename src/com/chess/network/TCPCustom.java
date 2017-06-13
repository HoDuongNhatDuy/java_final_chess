package com.chess.network;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by hoduo on 6/12/2017.
 */
public abstract class TCPCustom
{
    final static int PORT = 3200;
    protected Socket partner = null;

    public TCPCustom()
    {
    }

    public Socket getPartner()
    {
        return partner;
    }

    public void setPartner(Socket partner)
    {
        this.partner = partner;
    }

    String getMessage()
    {
        try
        {
            if (partner == null)
                return "";

            InputStream is = partner.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String receivedMessage = br.readLine();

            System.out.println("Received message: " + receivedMessage);

            return receivedMessage;
        }
        catch (SocketException e){
            System.out.println("Partner has gone!");
            // e.printStackTrace();
            return "";
        } catch (IOException e)
        {
            System.out.println("Partner has gone!");
            // e.printStackTrace();
            return "";
        }
    }

    void sendMessage(String message)
    {
        try
        {
            OutputStream os = partner.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

            bw.write(message);
            bw.newLine();
            bw.flush();

            System.out.println("Sent message: " + message);
        }
        catch (SocketException e){
            e.printStackTrace();
            System.out.println("Partner has gone!");
        } catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Partner has gone!");
        }
    }

    public void destroy() throws IOException {
        System.out.println("Close current TCP connection");
        if (partner != null)
        {
            partner.close();
        }
    }

    abstract Socket findPartner() throws IOException;
}
