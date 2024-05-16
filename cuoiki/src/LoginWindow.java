package src;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private static final String USER_DATA_FILE = "users.xml";

    public LoginWindow() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);
        add(panel);

        loginButton.addActionListener(new LoginAction());
        registerButton.addActionListener(new RegisterAction());
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (validateLogin(username, password)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                dispose();
                try {
                    new ClientForm(username).setVisible(true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password.");
            }
        }

        private boolean validateLogin(String username, String password) {
            try {
                File file = new File(USER_DATA_FILE);
                if (!file.exists()) return false;

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("user");
                for (int i = 0; i < nList.getLength(); i++) {
                    Element element = (Element) nList.item(i);
                    String storedUsername = element.getElementsByTagName("username").item(0).getTextContent();
                    String storedPassword = element.getElementsByTagName("password").item(0).getTextContent();
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        return true;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (registerUser(username, password)) {
                JOptionPane.showMessageDialog(null, "Registration successful!");
            } else {
                JOptionPane.showMessageDialog(null, "Registration failed. User may already exist.");
            }
        }

        private boolean registerUser(String username, String password) {
            try {
                File file = new File(USER_DATA_FILE);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc;
                Element rootElement;

                if (!file.exists()) {
                    doc = dBuilder.newDocument();
                    rootElement = doc.createElement("users");
                    doc.appendChild(rootElement);
                } else {
                    doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();
                    rootElement = doc.getDocumentElement();
                }

                NodeList nList = doc.getElementsByTagName("user");
                for (int i = 0; i < nList.getLength(); i++) {
                    Element element = (Element) nList.item(i);
                    String storedUsername = element.getElementsByTagName("username").item(0).getTextContent();
                    if (storedUsername.equals(username)) {
                        return false; // User already exists
                    }
                }

                Element userElement = doc.createElement("user");
                Element usernameElement = doc.createElement("username");
                usernameElement.appendChild(doc.createTextNode(username));
                Element passwordElement = doc.createElement("password");
                passwordElement.appendChild(doc.createTextNode(password));
                userElement.appendChild(usernameElement);
                userElement.appendChild(passwordElement);
                rootElement.appendChild(userElement);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);
                return true;
            } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }
}
