import org.junit.jupiter.api.Test;
import ru.netology.ChatClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatClientTest {
    @Test
    public void testClientConnection() throws IOException {
        try (ServerSocket testServer = new ServerSocket(9090)) {
            new Thread(() -> {
                try {
                    Socket client = testServer.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    assertEquals("CONNECT:testuser", in.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            ChatClient client = new ChatClient();
            client.username = "testuser";
            client.socket = new Socket("localhost", 9090);
            client.out = new PrintWriter(client.socket.getOutputStream(), true);
            client.out.println("CONNECT:testuser");
        }
    }
}