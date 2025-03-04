package com.example;

import com.example.chat.Chat;
import com.example.chat.Peer;

import javafx.application.Application;
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

    Peer peer1 = new Peer(12345, "localhost", "Ivan");
    Peer peer2 = new Peer(54321, "localhost", "Anna");
    Chat chat = new Chat(peer1, peer2);

    @Override
    public void start(Stage primaryStage) 
    {
        chat.connectUsers();

        // Create ViewModels for each user
        ChatViewModel ivanViewModel = new ChatViewModel(peer1, "Anna");
        ChatViewModel annaViewModel = new ChatViewModel(peer2, "Ivan");

        // Setup Ivan’s UI
        VBox primaryRoot = new VBox();
        primaryRoot.setSpacing(10);
        primaryRoot.setAlignment(Pos.CENTER);
        VBox primaryTextInputSection = createTextInputSection(ivanViewModel);
        primaryRoot.getChildren().add(primaryTextInputSection);

        Scene primaryScene = createScene(primaryRoot);
        primaryStage.setScene(primaryScene);
        primaryStage.setTitle("Ivan");
        primaryStage.show();

        // Setup Anna’s UI
        Stage secondaryStage = new Stage();
        VBox secondaryRoot = new VBox();
        secondaryRoot.setSpacing(10);
        secondaryRoot.setAlignment(Pos.CENTER);
        VBox secondaryTextInputSection = createTextInputSection(annaViewModel);
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

    private VBox createTextInputSection(ChatViewModel viewModel) 
    {
        TextField textField = new TextField();
        textField.setPromptText("Enter your message");
        textField.setPrefWidth(200);

        Button submitButton = new Button("Submit");

        ListView<String> listView = new ListView<>(viewModel.getMessages());
        listView.setPrefHeight(150);

        EventHandler<ActionEvent> submitHandler = event -> {
            String message = textField.getText().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message); // Delegate to ViewModel
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

    public static void main(String[] args) 
    {
        launch(args);
    }
}