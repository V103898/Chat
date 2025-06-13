import org.junit.jupiter.api.Test;
import ru.netology.ChatServer;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatServerTest {
    @Test
    public void testServerStart() throws IOException {
        ChatServer server = new ChatServer();
        new Thread(() -> server.main(new String[]{})).start();

        try (Socket client = new Socket("localhost", 8080)) {
            assertTrue(client.isConnected());
        }
    }
}