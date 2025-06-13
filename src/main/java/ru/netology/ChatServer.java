package ru.netology;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class ChatServer {
    private static final String SETTINGS_FILE = "settings.txt";
    private static final String LOG_FILE = "server.log";
    private static final List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        int port = readPortFromSettings();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            log("Server error: " + e.getMessage());
        }
    }

    static synchronized void broadcast(String message, String sender) {
        String formatted = "[" + new Date() + "] " + sender + ": " + message;
        log(formatted);

        for (ClientHandler client : clients) {
            if (!client.getUsername().equals(sender)) {
                client.sendMessage(formatted);
            }
        }
    }

    static synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcast(client.getUsername() + " left the chat", "Server");
    }

    static int readPortFromSettings() {
        try {
            Properties props = new Properties();
            props.load(new FileReader(SETTINGS_FILE));
            return Integer.parseInt(props.getProperty("port"));
        } catch (IOException e) {
            return 8080; // default
        }
    }

    private static void log(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}