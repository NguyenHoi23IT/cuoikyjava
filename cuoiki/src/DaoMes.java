package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DaoMes {
    private Connection connection;

    public DaoMes() throws SQLException {
        this.connection = DatabaseOperations.getInstance().getConnection();
    }

    public void insert(Message message) {
        String sql = "INSERT INTO messages (message) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, message.getMessage());
            preparedStatement.executeUpdate();
            System.out.println("Message inserted successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Message> show() {
        ArrayList<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                System.out.println(rs.getString("message"));
                messages.add(new Message(rs.getString("message")));
            }
            System.out.println("Messages displayed successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }
}
