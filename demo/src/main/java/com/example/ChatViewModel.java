package com.example;

import com.example.chat.Peer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatViewModel
{
    private final Peer peer;
    private final String otherUserName;
    private final ObservableList<String> messages = FXCollections.observableArrayList();

    public ChatViewModel(Peer peer, String otherUserName) 
    {
        this.peer = peer;
        this.otherUserName = otherUserName;
        startMessageReceiver();
    }

    // Expose the message list for binding
    public ObservableList<String> getMessages() 
    {
        return messages;
    }

    // Handle sending messages or files
    public void sendMessage(String input) 
    {
        if (input.startsWith("file:")) 
        {
            String[] parts = input.substring(5).split(":");
            if (parts.length >= 1) 
            {
                String filePath = parts[0].trim();
                peer.addFileForSending(filePath);
                // Note: Original code doesn’t update UI for files, so we don’t here either
            }
            else
            {
                System.out.println("Invalid file format. Use 'file:<path>.");
            }
        } 
        else
        {
            messages.add("Me: " + input); // Update UI with sent message
            peer.addMsg(input);           // Send message via Model
        }
    }

    // Poll for received messages in a background thread
    private void startMessageReceiver() 
    {
        Thread receiverThread = new Thread(() -> {
            while (true) 
            {
                String msg = peer.getRecivedMessage();
                if (msg != null && !msg.isEmpty()) 
                {
                    // Update UI on JavaFX thread
                    Platform.runLater(() -> messages.add(otherUserName + ": " + msg));
                }
                try 
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
            }
        });
        receiverThread.setDaemon(true);
        receiverThread.start();
    }
}