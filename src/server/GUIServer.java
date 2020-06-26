package server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;

public class GUIServer extends Application {

    private Server server;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("Start");
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(button);

        TextArea textArea = new TextArea();
        borderPane.setCenter(textArea);
        Scene scene = new Scene(borderPane);

        primaryStage.setTitle("Server");
        primaryStage.setWidth(600);
        primaryStage.setHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();

        server = new Server(44444);
        server.start();
    }
}
