package src;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseOperations {
    private static DatabaseOperations instance;
    private static final String MESSAGE_DATA_FILE = "messages.xml";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String USERNAME = "your_username";
    private static final String PASSWORD = "your_password";
    private Connection connection;

    private DatabaseOperations() {
        // Khởi tạo kết nối đến cơ sở dữ liệu ở đây
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseOperations getInstance() {
        if (instance == null) {
            instance = new DatabaseOperations();
        }
        return instance;
    }

    public void addMessage(String sender, String receiver, String message) {
        try {
            File file = new File(MESSAGE_DATA_FILE);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;

            if (!file.exists()) {
                // Nếu tệp không tồn tại, tạo một tệp mới và tạo ra một tài liệu mới.
                file.createNewFile();
                dBuilder = dbFactory.newDocumentBuilder();
                dBuilder.newDocument().appendChild(dBuilder.newDocument().createElement("messages"));
            }

            // Load tài liệu từ tệp XML
            dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            // Tạo các nút cho tin nhắn mới
            org.w3c.dom.Element messageElement = doc.createElement("message");
            org.w3c.dom.Element senderElement = doc.createElement("sender");
            senderElement.appendChild(doc.createTextNode(sender));
            org.w3c.dom.Element receiverElement = doc.createElement("receiver");
            receiverElement.appendChild(doc.createTextNode(receiver));
            org.w3c.dom.Element textElement = doc.createElement("text");
            textElement.appendChild(doc.createTextNode(message));
            messageElement.appendChild(senderElement);
            messageElement.appendChild(receiverElement);
            messageElement.appendChild(textElement);

            // Thêm tin nhắn mới vào tài liệu
            doc.getDocumentElement().appendChild(messageElement);

            // Ghi lại tài liệu vào tệp XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            ex.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
