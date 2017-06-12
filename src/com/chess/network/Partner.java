package com.chess.network;

import com.chess.Alliance;
import com.chess.Coordinate;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by hoduo on 6/12/2017.
 */
public class Partner
{
    TCPCustom connection;
    Alliance alliance;

    public Partner() throws Exception
    {
        if (!init())
            throw new Exception("Network error");
    }

    private boolean init()
    {
        try
        {
            connection = new TCPCustomClient();
            Socket s = connection.findPartner();
            alliance = Alliance.WHITE;

            if (s == null)
            {
                connection = new TCPCustomServer();
                connection.findPartner();
                alliance = Alliance.BLACK;
            }

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public Alliance getAlliance()
    {
        return alliance;
    }

    public Coordinate[] getMoveCoordinate()
    {
        String message = connection.getMessage();

        System.out.println("Get move: " + message);

        String[] map = message.split("--");

        Coordinate from = new Coordinate(map[0]);
        Coordinate to = new Coordinate(map[1]);

        return new Coordinate[]{from, to};
    }

    public void sendMoveCoordinate(Coordinate from, Coordinate to)
    {
        int fromX = 7 - from.getX();
        int fromY = 7 - from.getY();
        int toX = 7 - to.getX();
        int toY = 7 - to.getY();

        String message = fromX + "-" + fromY + "--" + toX + "-" + toY;

        System.out.println("Send move: " + message);

        connection.sendMessage(message);
    }
}
