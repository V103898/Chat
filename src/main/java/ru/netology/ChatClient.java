package ru.netology;
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class ChatClient {
    private static final String SETTINGS_FILE = "settings.txt";
    private static final String LOG_FILE = "client.log";
    public String username;
    public Socket socket;
    public PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        new ChatClient().start();
    }

    public void start() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите ваше имя: ");
            username = scanner.nextLine();
            System.out.print("Введите сообщение: " +  "или /exit чтобы выйти: ");
            int port = readPortFromSettings();
            socket = new Socket("localhost", port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("CONNECT:" + username);

            new Thread(new MessageReceiver()).start();
            new Thread(new MessageSender()).start();
        } catch (IOException e) {
            log("Connection error: " + e.getMessage());
        }
    }

    private class MessageSender implements Runnable {
        @Override
        public void run() {
            try (Scanner scanner = new Scanner(System.in)) {
                String message;
                while (true) {
                    message = scanner.nextLine();
                    if ("/exit".equalsIgnoreCase(message)) {
                        out.println("EXIT");
                        System.out.println("Вы покинули чат");
                        break;
                    }
                    out.println("MESSAGE:" + message);
                    log("[" + new Date() + "] You: " + message);
                }
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                    log(message);
                }
            } catch (IOException e) {
                log("Disconnected from server");
            }
        }
    }

    private static int readPortFromSettings() {
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