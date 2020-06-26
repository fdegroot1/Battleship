package server;

import javafx.application.Platform;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int sessionNumber = 1;
    private int port;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean running = true;

    public Server(int port) {
        this.port = port;

    }

    public boolean start() {
        try {
            this.running = true;
            this.serverSocket = new ServerSocket(port);
            this.serverThread = new Thread(() -> {
                while (running) {
                    System.out.println("Waiting for clients to connect...");
                    try {
                        //1.1 wait for first player
                        Socket player1 = this.serverSocket.accept();

                        System.out.println("Connected");

                        //1.2 Notify player he is player 1
//                        DataOutputStream dataOutputStream1 = new DataOutputStream(player1.getOutputStream());
//                        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(player1.getOutputStream());
//                        dataOutputStream1.writeInt(1);

                        //1.3 wait for second player
                        Socket player2 = this.serverSocket.accept();

                        System.out.println("Connected p2");

                        //1.4
//                        DataOutputStream dataOutputStream2 = new DataOutputStream(player2.getOutputStream());
//                        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(player2.getOutputStream());
//                        dataOutputStream2.writeInt(2);

                        new Thread(new ServerClient(player1, player2)).start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            this.serverThread.start();
            System.out.println("ComputerShop_Server is started and listening on port " + this.port);

        } catch (IOException e) {
            System.out.println("Could not connect: " + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean stop() {
        try {
            this.serverSocket.close();
            this.running = false;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return false;
        }
        return true;
    }
}
