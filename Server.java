package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        int port = ConsoleHelper.readInt();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Server running");
        while (true) {
            try {
                Socket s = serverSocket.accept();
                if (s != null) {
                    new Handler(s).start();
                }
            } catch (Exception ex) {
                try {
                    serverSocket.close();
                    System.out.println(ex);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void sendBroadcastMessage(Message message) {
        for (Map.Entry<String, Connection> stringConnectionEntry : connectionMap.entrySet()) {
            try {
                stringConnectionEntry.getValue().send(message);
            } catch (IOException e) {
                System.out.println("error send");
            }
        }
    }
    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            Message reciveMessage = null;

            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                reciveMessage = connection.receive();
                if (reciveMessage.getType() != MessageType.USER_NAME) continue;
                if (reciveMessage.getData().isEmpty()) continue;
                if (connectionMap.containsKey(reciveMessage.getData())) continue;
                connectionMap.put(reciveMessage.getData(), connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED));
                break;
            }
            return reciveMessage.getData();
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException{
            for (String s : connectionMap.keySet()) {
                if (!s.equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED,s));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message reciveMessage = connection.receive();
                if (reciveMessage.getType()!=MessageType.TEXT) {
                    ConsoleHelper.writeMessage("error");
                } else {
                    sendBroadcastMessage(new Message(MessageType.TEXT,userName + ": " + reciveMessage.getData()));
                }
            }
        }
        public void run() {
            try {
                System.out.println(socket.getRemoteSocketAddress());/*
                ConsoleHelper.writeMessage("Connection");
                socket.getRemoteSocketAddress();*/
                Connection connection = new Connection(socket);

                String userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                sendListOfUsers(connection,userName);
                serverMainLoop(connection,userName);
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                ConsoleHelper.writeMessage("Connection close");
            } catch (IOException | ClassNotFoundException ex) {
                ConsoleHelper.writeMessage("Errror");
            }

        }
    }

}
