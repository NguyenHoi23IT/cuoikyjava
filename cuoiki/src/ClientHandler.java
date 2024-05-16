package src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private BufferedReader bufferedReader;
    private DataOutputStream dataOutputStream;
    private String userName;
    private DatabaseOperations dbOperations;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.dbOperations = DatabaseOperations.getInstance();
        initializeStreams();
    }

    private void initializeStreams() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            userName = bufferedReader.readLine();
            server.broadcastMessage("User " + userName + " connected");
            server.updateClientList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = bufferedReader.readLine()) != null) {
                server.broadcastMessage(message);
                // Tách sender, receiver và message từ giao thức bạn sử dụng
                String[] parts = message.split(":", 3);
                if (parts.length == 3) {
                    dbOperations.addMessage(parts[0], parts[1], parts[2]);  // Thêm tin nhắn vào file XML
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.removeClient(this);
            closeResources();
        }
    }

    public void sendMessage(String message) {
        try {
            dataOutputStream.writeBytes(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public void stopClient() {
        closeResources();
    }

    private void closeResources() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (dataOutputStream != null) dataOutputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
