package src;

import src.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerForm extends JFrame {
    private JTextArea userListArea;
    private JButton startButton;
    private JButton stopButton;
    private JTextField portField;
    private Server server;

    public ServerForm() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Chat Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Port:"));
        portField = new JTextField("12345", 10);
        topPanel.add(portField);

        startButton = new JButton("Start Server");
        startButton.addActionListener(e -> startServer());
        topPanel.add(startButton);

        stopButton = new JButton("Stop Server");
        stopButton.addActionListener(e -> stopServer());
        stopButton.setEnabled(false);
        topPanel.add(stopButton);

        add(topPanel, BorderLayout.NORTH);

        userListArea = new JTextArea();
        userListArea.setEditable(false);
        add(new JScrollPane(userListArea), BorderLayout.CENTER);
    }

    private void startServer() {
        int port = Integer.parseInt(portField.getText().trim());
        server = new Server(port, userListArea);
        new Thread(() -> server.start()).start();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopServer() {
        if (server != null) {
            server.stopServer();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerForm serverForm = new ServerForm();
            serverForm.setVisible(true);
        });
    }
}
