package com.example;

import com.example.chat.Chat;
import com.example.chat.Peer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application 
{
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    
    private final ObservableList<String> ivanMessages = FXCollections.observableArrayList();
    private final ObservableList<String> annaMessages = FXCollections.observableArrayList();

    Peer peer1 = new Peer(12345, "localhost", "Ivan");
    Peer peer2 = new Peer(54321, "localhost", "Anna");
    Chat chat = new Chat(peer1, peer2);

    @Override
    public void start(Stage primaryStage) 
    {
        chat.connectUsers();
        
        startMessageReceiver(peer1, ivanMessages);
        startMessageReceiver(peer2, annaMessages);

        /////////////////////////////////Stage1////////////////////////////////////////
        VBox primaryRoot = new VBox();
        primaryRoot.setSpacing(10);
        primaryRoot.setAlignment(Pos.CENTER);
        VBox primaryTextInputSection = createTextInputSection("Ivan", ivanMessages);
        primaryRoot.getChildren().add(primaryTextInputSection);
       
        Scene primaryScene = createScene(primaryRoot);
        primaryStage.setScene(primaryScene);
        primaryStage.setTitle("Ivan");
        primaryStage.show();

        /////////////////////////////////Stage2////////////////////////////////////////
        Stage secondaryStage = new Stage();
        VBox secondaryRoot = new VBox();
        secondaryRoot.setSpacing(10);
        secondaryRoot.setAlignment(Pos.CENTER);
        VBox secondaryTextInputSection = createTextInputSection("Anna", annaMessages);
        secondaryRoot.getChildren().add(secondaryTextInputSection); 

        Scene secondaryScene = createScene(secondaryRoot);
        secondaryStage.setScene(secondaryScene);
        secondaryStage.setTitle("Anna");
        secondaryStage.show();
    }

    private Scene createScene(VBox container) 
    {
        return new Scene(container, WIDTH, HEIGHT);
    }
   
    private VBox createTextInputSection(String userName, ObservableList<String> messagesList) 
    {
        TextField textField = new TextField();
        textField.setPromptText("Enter your message");
        textField.setPrefWidth(200);
        
        Button submitButton = new Button("Submit");
        
        ListView<String> listView = new ListView<>(messagesList);
        listView.setPrefHeight(150);

        EventHandler<ActionEvent> submitHandler = event -> {
            String message = textField.getText().trim();
            if (!message.isEmpty()) 
            {
                chat.getUserToSendMsg(userName, message);
                if(userName.equals("Ivan"))
                    ivanMessages.add("Me: " + message);
                else
                    annaMessages.add("Me: " + message);
                textField.clear();
            }
        };

        submitButton.setOnAction(submitHandler);
        textField.setOnAction(submitHandler);

        VBox textInputBox = new VBox(10);
        textInputBox.setAlignment(Pos.CENTER);
        textInputBox.getChildren().addAll(textField, submitButton, listView);
        return textInputBox;
    }

    private void startMessageReceiver(Peer peer, ObservableList<String> messagesList) 
    {
        Thread receiverThread = new Thread(() -> {

            String name = peer == peer2 ? "Ivan" : "Anna";
            while (true)
            {
                String msg = peer.getRecivedMessage();
                if (msg != null && !msg.isEmpty()) 
                    Platform.runLater(() -> messagesList.add(name + ": " + msg));
                
                try 
                {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
}
