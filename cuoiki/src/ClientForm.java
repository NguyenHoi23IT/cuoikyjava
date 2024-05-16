package src;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientForm extends JFrame {
    String username;
    private ChatPanel chatPanel;

    public ClientForm(String username) throws IOException {
        this.username = username;
        initialize();
    }

    private void initialize() {
        setTitle("Client - " + username);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        chatPanel = new ChatPanel(this);
        add(chatPanel, BorderLayout.CENTER);
    }

    // Method to send message to the server
    public void sendMessageToServer(String message) {
        // Placeholder for actual server communication
        System.out.println("Sending message to server: " + message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Test initialization of ClientForm
                ClientForm clientForm = new ClientForm("TestUser");
                clientForm.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
