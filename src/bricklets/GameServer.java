package bricklets;

import gameengine.GameController;
import gameengine.context.Context;
import gameengine.entities.Entity;
import jdk.nashorn.internal.parser.JSONParser;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class GameServer extends WebSocketServer {
    private GameController controller;
    public GameServer(int port, GameController controller) {
        this(new InetSocketAddress(port), controller);
    }

    public GameServer(InetSocketAddress address, GameController controller) {
        super(address);
        this.controller = controller;
        GameServer that = this;

        Thread inputThread = new Thread(() -> {
            WebSocketImpl.DEBUG = false;
            int port = 8887;

            try {
                that.start();
                System.out.println("ChatServer started on port: " + that.getPort());

                BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String in = sysin.readLine();
                    //s.sendToAll(in);
                    if (in.equals("exit")) {
                        that.stop();
                        break;
                    } else if (in.equals("restart")) {
                        that.stop();
                        that.start();
                        break;
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
        inputThread.start();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        JSONObject msg = new JSONObject().put("msg", "User Connected!");
        conn.send(msg.toString());
        System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered game!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        JSONObject msg = new JSONObject().put("msg", conn + " has left");
        this.sendToAll(msg.toString());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    public void sendEntities(ArrayList<Entity> entities) {
        JSONArray es = new JSONArray();
        for (Entity entity: entities) {
            JSONObject e = new JSONObject();
            e.put("x", entity.getX());
            e.put("y", entity.getY());
            e.put("vx", entity.getDX());
            e.put("vy", entity.getDY());
            es.put(e);
        }
        JSONObject msg = new JSONObject();
        msg.put("points", es);
        sendToAll(msg.toString());
    }

    public void sendToAll(String text) {
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for (WebSocket c : con) {
                c.send(text);
            }
        }
    }
}