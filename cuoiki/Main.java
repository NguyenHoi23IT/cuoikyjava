import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class Student {
    String id;
    String name;
    String address;
    String dateOfBirth;

    public Student(String id, String name, String address, String dateOfBirth) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }
}

public class Main {
    static List<Student> students = new ArrayList<>();

    public static void main(String[] args) {
        readStudentFile("student.xml");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(() -> calculateAge());
        executor.submit(() -> hashDateOfBirth());
        executor.submit(() -> checkIfSumIsPrime());

        executor.shutdown();
    }

    static void readStudentFile(String filename) {
        try {
            File inputFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Student");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String id = eElement.getElementsByTagName("id").item(0).getTextContent();
                    String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                    String address = eElement.getElementsByTagName("address").item(0).getTextContent();
                    String dateOfBirth = eElement.getElementsByTagName("dateOfBirth").item(0).getTextContent();
                    Student student = new Student(id, name, address, dateOfBirth);
                    students.add(student);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    static void calculateAge() {
        for (Student student : students) {
            String[] dobParts = student.dateOfBirth.split("-");
            int age = 2024 - Integer.parseInt(dobParts[0]);
            int months = 4 - Integer.parseInt(dobParts[1]);
            int days = 10 - Integer.parseInt(dobParts[2]);
            String ageStr = age + "-" + months + "-" + days;
            System.out.println("Age of student " + student.id + ": " + ageStr);
        }
    }

    static void hashDateOfBirth() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (Student student : students) {
                md.update(student.dateOfBirth.getBytes());
                byte[] hashedDateOfBirth = md.digest();
                System.out.println("Hashed date of birth of student " + student.id + ": " + bytesToHex(hashedDateOfBirth));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    static void checkIfSumIsPrime() {
        for (Student student : students) {
            String dob = student.dateOfBirth.replace("-", "");
            int digitSum = 0;
            for (int i = 0; i < dob.length(); i++) {
                digitSum += Character.getNumericValue(dob.charAt(i));
            }
            System.out.println("Sum of digits of date of birth of student " + student.id + ": " + digitSum);
            boolean isPrime = digitSum > 1;
            for (int i = 2; i <= Math.sqrt(digitSum); i++) {
                if (digitSum % i == 0) {
                    isPrime = false;
                    break;
                }
            }
            System.out.println("Is sum of digits of date of birth prime? " + isPrime);
        }
    }

    static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
}