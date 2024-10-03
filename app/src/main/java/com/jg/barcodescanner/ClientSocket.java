package com.jg.barcodescanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSocket {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isConnected = false;
    PrintWriter outputStream;
    Socket socket;


    public CompletableFuture<Boolean> connect(String IP, int port) {
        CompletableFuture<Boolean> connectionFuture = new CompletableFuture<>();

        try{
            CompletableFuture.runAsync(() -> {
                try {
                    socket = new Socket(IP, port);
                    outputStream = new PrintWriter(socket.getOutputStream(), true);
                    isConnected = true;  // Mark as connected
                    connectionFuture.complete(true); // Complete future successfully
                } catch (IOException e) {
                    System.err.println("Failed to connect: " + e.getMessage());
                    connectionFuture.complete(false); // Complete future with failure
                }
            }, executorService);
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


        return connectionFuture; // Return the CompletableFuture
    }


    // Method to send a message (after connection is established)
    public void sendMessage(String message) {



        executorService.execute(() -> {
            outputStream.println(message);
        });
    }

    public void close() {
        executorService.execute(() -> {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Failed to close connection");
                e.printStackTrace();
            }
        });
    }


}
