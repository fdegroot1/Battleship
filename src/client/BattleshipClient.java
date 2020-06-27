package client;

import data.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BattleshipClient extends Application {

    private boolean continueGame = true;
    private boolean waiting = true;
    public boolean myturn;
    private int player;

    private ArrayList<Ship> ships = new ArrayList<>();

    private Cell[][] cell = new Cell[10][10];

    private String host = "localhost";

    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private Point2D point2D;

    private ArrayList<Point2D> pickedMoves = new ArrayList<>();

    Label labelTitle = new Label("Battleship");
    Label labelStatus = new Label("Test");

    @Override
    public void start(Stage primaryStage) throws Exception {
        generateBoats();

        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();

        for(int x=0; x<10; x++){
            for(int y=0; y<10; y++){
                gridPane.add(cell[x][y] = new Cell(x,y), x, y);
            }
        }

        borderPane.setTop(labelTitle);
        borderPane.setCenter(gridPane);
        borderPane.setBottom(labelStatus);

        Scene scene = new Scene(borderPane, 600,600);
        primaryStage.setTitle("BattleshipClient");
        primaryStage.setScene(scene);
        primaryStage.show();

        drawShips();
        connect();
    }

    private void connect(){
        try {
            Socket socket = new Socket(host, 44444);

            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataInputStream = new DataInputStream(socket.getInputStream());

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectInputStream = new ObjectInputStream(socket.getInputStream());

            objectOutputStream.writeObject(ships);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(() ->{
            try {
                player = dataInputStream.readInt();
                if(player==1){
                    myturn = true;
                }
                else {
                    myturn = false;
                }
                Platform.runLater(() -> labelStatus.setText("Player: "+player));

                System.out.println("Play game");

                while (continueGame){
                    if(player == 1){
                        waitForPlayer();
                        sendMove();
                        receiveInfo();
                    }
                    else if(player == 2){
                        receiveInfo();
                        waitForPlayer();
                        sendMove();
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void receiveInfo() throws IOException, ClassNotFoundException {
        System.out.println("wait for data");
        Platform.runLater(() -> labelStatus.setText("Waiting for other player"));
        String text = dataInputStream.readUTF();
        System.out.println(text);

        Platform.runLater(() -> labelStatus.setText(text));

        if(text.equals("Player 1 has won")){
            continueGame = false;
            Platform.runLater(() -> labelStatus.setText(text));
        }
        else if(text.equals("Player 2 has won")){
            continueGame = false;
            Platform.runLater(() -> labelStatus.setText(text));
        }
        else {
            receiveMove();
            myturn=true;
        }
    }

    private void receiveMove() throws IOException, ClassNotFoundException {
        //Point2D point2D = (Point2D)objectInputStream.readObject();
//        String text = dataInputStream.readUTF();
//        if(text.equals("hit")){
//            Platform.runLater(() -> cell[(int)point2D.getX()][(int)point2D.getY()].repaint());
//        }
//        else if(text.equals("miss")){
//            Platform.runLater(() -> cell[(int)point2D.getX()][(int)point2D.getY()].repaint());
//        }

    }

    private void sendMove() throws IOException {
        objectOutputStream.writeObject(pickedMoves.get(pickedMoves.size()-1));
    }

    private void waitForPlayer() throws InterruptedException {
        Platform.runLater(() -> labelStatus.setText("Pick a move"));
        while (waiting){
            Thread.sleep(100);
        }
        waiting = true;
    }

    private void generateBoats(){
        ships.add(new Aircarrier(6,new Point2D.Double(
                0,0),true));
        ships.add(new Battleship(4,new Point2D.Double(5,1),true));
        ships.add(new Submarine(3,new Point2D.Double(3,3),true));
        ships.add(new Patrolship(2, new Point2D.Double(9,7),true));
    }

    private void drawShips(){
        for(Ship ship : ships){
            for(Point2D point : ship.getPosition()){
                cell[(int)point.getX()][(int)point.getY()].colorShip();
            }
        }
    }

    public class Cell extends Pane {

        private Point2D position;

        public Cell(int x, int y) {
            this.position = new Point2D.Double(x, y);
            this.setPrefSize(200, 200);
            setStyle("-fx-border-color: black");
            this.setOnMouseClicked(event -> handleMouseCLick());
        }

        private void handleMouseCLick(){
            //TODO handle mouse click
            if(myturn){
                if(!pickedMoves.contains(new Point2D.Double(position.getX(),position.getY()))){
                    pickedMoves.add(new Point2D.Double(position.getX(),position.getY()));
                    System.out.println("Added");
                    myturn = false;
                    waiting = false;
                }
            }
            System.out.println(position.getX()+" "+position.getY());

            
        }

        protected void repaint(){
            //TODO hit or miss
        }

        protected void colorShip(){
            Rectangle rectangle = new Rectangle();
            rectangle.setWidth(this.getWidth());
            rectangle.setHeight(this.getHeight());
            rectangle.setFill(Color.valueOf("#000000"));

            getChildren().add(rectangle);
        }
    }
}


