package com.client;

import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientEngine {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    @SneakyThrows
    public ClientEngine() {
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());


        new Thread(getListener()).start();
        Scanner scan = new Scanner(System.in);
        String outBondMessage;

        while (true) {
            outBondMessage = scan.nextLine();
            out.writeUTF(outBondMessage);
            if (outBondMessage.toLowerCase().contains("-file")) {
                sendFile(outBondMessage.substring(6));
            }
            if (outBondMessage.equalsIgnoreCase("-exit")) {
                break;
            }
        }
    }

    private Runnable getListener() {
        return () -> {
            try {
                while (true) {
                    String inboundMessage = in.readUTF();
                    System.out.println(inboundMessage);
                    if (inboundMessage.toLowerCase().contains("bye")) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }


    private void sendFile(String filePath) {
        File transferFile = new File(filePath);
        byte[] data = new byte[1024];
        try {
            InputStream input = new FileInputStream(transferFile);
            int num;
            while ((num = input.read(data)) != -1) {
                out.write(data);
                out.flush();
            }
            System.out.println(num);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

