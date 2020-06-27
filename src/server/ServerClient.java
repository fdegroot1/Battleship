package server;

import data.Ship;

import java.awt.geom.Point2D;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerClient implements Runnable {

    private Socket player1;
    private Socket player2;

    private DataInputStream dataFromPlayer1;
    private DataOutputStream dataToPlayer1;
    private DataInputStream dataFromPlayer2;
    private DataOutputStream dataToPlayer2;

    private ObjectOutputStream objectToPlayer1;
    private ObjectOutputStream objectToPlayer2;
    private ObjectInputStream objectFromPlayer1;
    private ObjectInputStream objectFromPlayer2;

    private ArrayList<Ship> player1ships;
    private ArrayList<Ship> player2ships;

    public ServerClient(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;

        this.player1ships = new ArrayList<>();
        this.player2ships = new ArrayList<>();

        try {
            dataToPlayer1 = new DataOutputStream(player1.getOutputStream());
            dataToPlayer2 = new DataOutputStream(player2.getOutputStream());

            dataFromPlayer1 = new DataInputStream(player1.getInputStream());
            dataFromPlayer2 = new DataInputStream(player2.getInputStream());

            objectToPlayer1 = new ObjectOutputStream(player1.getOutputStream());
            objectToPlayer2 = new ObjectOutputStream(player2.getOutputStream());

            objectFromPlayer1 = new ObjectInputStream(player1.getInputStream());
            objectFromPlayer2 = new ObjectInputStream(player2.getInputStream());

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            dataToPlayer1.writeInt(1);
            dataToPlayer2.writeInt(2);

            player1ships = (ArrayList<Ship>) objectFromPlayer1.readObject();
            player2ships = (ArrayList<Ship>) objectFromPlayer2.readObject();

            for(Ship ship : player1ships){
                System.out.println(ship.getPosition());
            }
            for(Ship ship : player2ships){
                System.out.println(ship.getPosition());
            }


            while (true){
                System.out.println("Waiting for point");
                Point2D positionPlayer1 = (Point2D) objectFromPlayer1.readObject();
                System.out.println("Received "+positionPlayer1);

                if(isHit(positionPlayer1, player2ships)){
                    System.out.println("Hit");
                    dataToPlayer1.writeUTF("hit");
                    if(hasWon(player2ships)){
                        System.out.println("Player 1 won");
                        dataToPlayer1.writeUTF("Player 1 has won");
                        dataToPlayer2.writeUTF("Player 1 has won");
                        stop();
                        break;
                    }
                    dataToPlayer2.writeUTF("Pick a move");
                    sendMove(objectToPlayer2, positionPlayer1);
                }
                else{
                    dataToPlayer1.writeUTF("miss");
                    dataToPlayer2.writeUTF("Pick a move");

                    sendMove(objectToPlayer2, positionPlayer1);

                }

                Point2D positionPlayer2 = (Point2D) objectFromPlayer2.readObject();

                if(isHit(positionPlayer2, player1ships)){
                    dataToPlayer2.writeUTF("hit");
                    if(hasWon(player1ships)){
                        System.out.println("Player 2 won");
                        dataToPlayer1.writeUTF("Player 2 has won");
                        dataToPlayer2.writeUTF("Player 2 has won");
                        stop();
                        break;
                    }
                    dataToPlayer1.writeUTF("Pick a move");
                    sendMove(objectToPlayer1, positionPlayer2);
                }
                else {
                    dataToPlayer2.writeUTF("miss");
                    dataToPlayer1.writeUTF("Pick a move");

                    sendMove(objectToPlayer1, positionPlayer2);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private boolean isHit(Point2D position, ArrayList<Ship> list){
        boolean isRemoved = false;
        Ship hitShip = null;
        Point2D point2DtoRemove = null;
        for(Ship ship : list){

            for(Point2D point2D : ship.getPosition()){
                if(position.equals(point2D)){
                    point2DtoRemove = point2D;
                    isRemoved = true;
                    hitShip = ship;
                    break;
                }
            }

        }
        if(isRemoved){
            hitShip.getPosition().remove(point2DtoRemove);
            if(hitShip.getPosition().isEmpty()){
                list.remove(hitShip);
                shipDestroyedMessage(hitShip);
            }
        }
        return isRemoved;
    }

    private boolean hasWon(ArrayList<Ship> list){
        if(list.isEmpty()){
            return true;
        }
        return false;
    }

    private void sendMove(ObjectOutputStream objectOutputStream, Point2D position) throws IOException{
        objectOutputStream.writeObject(position);
    }

    private void shipDestroyedMessage(Ship ship){
//        try {
            System.out.println("Destroyed");
            //dataToPlayer1.writeUTF(ship.getName()+" destroyed");
            //dataToPlayer2.writeUTF(ship.getName()+" destroyed");
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }

    private void stop() throws IOException {
        player1.close();
        player2.close();
    }
}
