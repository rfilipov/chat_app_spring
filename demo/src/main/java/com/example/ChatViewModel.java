package com.example;

import java.io.File;

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

    public ObservableList<String> getMessages() 
    {
        return messages;
    }

    public void sendMessage(String input) 
    {
        if (input.startsWith("file:")) 
        {
            String[] parts = input.substring(5).split(":");
            if (parts.length >= 1) 
            {
                String filePath = parts[0].trim();
                String fileName = new File(filePath).getName();
                peer.addFileForSending(filePath);
                messages.add("Me: Queued file " + fileName + " for sending.");            }
            else
            {
                System.out.println("Invalid file format. Use 'file:<path>.");
            }
        } 
        else
        {
            messages.add("Me: " + input); 
            peer.addMsg(input);           
        }
    }


    private void startMessageReceiver() 
    {
        Thread receiverThread = new Thread(() -> {
            while (true) 
            {
                String msg = peer.getRecivedMessage();
                if (msg != null && !msg.isEmpty()) 
                {

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