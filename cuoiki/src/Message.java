package src;

public class Message {
    private String message;

    public Message(String message) {
        this.message = message;
    }
    public Message() {
    }
    public String getMessage() {
        return message;
    }

    public void setMesage(String mesage) {
        this.message = mesage;
    }

    @Override
    public String toString() {
        return  message + '\n';
    }
}
