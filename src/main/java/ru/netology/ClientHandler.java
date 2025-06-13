package ru.netology;
import java.net.Socket;
import java.io.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private final PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            username = in.readLine().split(":")[1];
            ChatServer.broadcast(username + " joined the chat", "Server");

            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith("MESSAGE:")) {
                    String message = input.substring(8);
                    ChatServer.broadcast(message, username);
                } else if (input.equals("EXIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getUsername() {
        return username;
    }
}