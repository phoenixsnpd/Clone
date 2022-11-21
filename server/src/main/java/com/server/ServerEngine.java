package com.server;

import lombok.SneakyThrows;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;


public class ServerEngine {
    private static final int SERVER_PORT = 8888;

    private final ServerSocket serverSocket;
    private CopyOnWriteArrayList<Client> activeConnections = new CopyOnWriteArrayList<>();
    private int clientsCount;


    @SneakyThrows
    public ServerEngine() {
        serverSocket = new ServerSocket(SERVER_PORT);
        while (true) {
            new Thread(multiThreadClient()).start();
        }
    }

    @SneakyThrows
    private Runnable multiThreadClient() {
        clientsCount++;
        Socket client = serverSocket.accept();
        String name = "Client-" + clientsCount;
        Date connectionTime = new Date();

        Client newConnections = new Client(name, connectionTime.getTime(), client);
        activeConnections.add(newConnections);
        return () -> {
            try {
                DataInputStream in = new DataInputStream(client.getInputStream());
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                for (Client connection : activeConnections) {
                    DataOutputStream messageAboutConect =
                            new DataOutputStream(connection.getSocket().getOutputStream());
                    messageAboutConect.writeUTF("[SERVER] " + newConnections.getName() + " was connected");
                }
                String inBoundMessage;

                while (true) {
                    inBoundMessage = in.readUTF();
                    System.out.println(inBoundMessage);
                    if (inBoundMessage.equals("-exit")) {
                        out.writeUTF("Thank you for the session. Bye!");
                        for (Client connection : activeConnections) {
                            DataOutputStream messageAboutDisconect =
                                    new DataOutputStream(connection.getSocket().getOutputStream());
                            if (connection.getName().equals(newConnections.getName())) {
                                activeConnections.remove(connection);
                            }
                            messageAboutDisconect.writeUTF("[SERVER] " + newConnections.getName()
                                    + " was disconnected");
                        }
                        break;
                    }
                    if (inBoundMessage.toLowerCase().contains("-file")) {
                        getFile(in);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private void getFile(DataInputStream in) {
        File file = new File("copy_file.txt");
        byte[] data = new byte[64];

        try {
            InputStream input = in;
            FileOutputStream fos = new FileOutputStream(file);

            int num;
            while (file.length() < 64) {
                in.read(data);
                fos.write(data);
                fos.flush();
                System.out.println((int) file.length());
            }
            System.out.println("Hello from server"); /////////////////////////////////
            fos.close();
            //input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
