package ru.netology;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        // Чтение порта из настроек (если необходимо)
        try {
            port = readPortFromSettings();
        } catch (Exception e) {
            System.out.println("Не удалось прочитать настройки порта, используется порт по умолчанию: " + port);
        }

        System.out.println("Запуск сервера на порту: " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен и ожидает подключений...");

            while (true) {
                // Принимаем входящее соединение
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новое подключение: " + clientSocket.getRemoteSocketAddress());

                // Создаем и запускаем обработчик клиента
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }

    private static int readPortFromSettings() {
        return ChatServer.readPortFromSettings(); // предполагается, что этот метод есть в вашем классе ChatServer
    }
}
