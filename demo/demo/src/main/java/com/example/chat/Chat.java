package com.example.chat;

public class Chat 
{
    private final Peer p1, p2;

    public Chat(Peer p1, Peer p2) 
    {
        this.p1 = p1;
        this.p2 = p2;
    }

    public void connectUsers()
    {
        new Thread(() -> p1.startServer()).start();

        try 
        {
            Thread.sleep(2000);
        } catch (InterruptedException e) 
        {
            System.err.println("Interrupted while waiting for server to start: " + e.getMessage());
            Thread.currentThread().interrupt(); 
            return;
        }

        new Thread(() -> p2.startConnection(p1.get_host(), p1.get_port())).start();     
    }

    public void getUserToSendMsg(String username, String input) 
    {

        System.out.println("Sending message " + username + ": " + input);
        Peer sender = null;
        if (username.equals(p1.get_name())) 
            sender = p1;

        else if (username.equals(p2.get_name())) 
            sender = p2;
        
        else 
        {
            System.out.println("Unknown username. Please use " + p1.get_name() + " or " + p2.get_name() + ".");
        }

        if (input.startsWith("file:")) 
        {
            String[] parts = input.substring(5).split(":");
            if (parts.length >= 1) 
            {
                String filePath = parts[0].trim();
                sender.addFileForSending(filePath);
                System.out.println(sender.get_name() + " queued file: " + filePath);
            } 
            
            else 
            {
                System.out.println("Invalid file format. Use 'file:<path>.");
            }
        } 
        else 
        {
            sender.addMsg(input);
        }    
    }

    public void killChat() 
    {
        p1.closeConnection();
        p2.closeConnection();
    }
}