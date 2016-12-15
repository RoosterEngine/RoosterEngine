package bricklets;

import gameengine.entities.Entity;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;

public class GameServer extends WebSocketServer {
    public GameServer(int port) {
        this(new InetSocketAddress(port));
    }

    public GameServer(InetSocketAddress address) {
        super(address);
        WebSocketImpl.DEBUG = false;
        this.start();
        System.out.println("Game server started on port: " + getPort());
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