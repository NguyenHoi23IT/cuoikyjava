package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatPanel extends JPanel {
	private ClientForm parentForm;
	private JTextArea messageArea;
	private JTextField messageField;

	public ChatPanel(ClientForm parentForm) {
		this.parentForm = parentForm;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		// Panel for displaying messages
		messageArea = new JTextArea();
		messageArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(messageArea);
		add(scrollPane, BorderLayout.CENTER);

		// Panel for inputting new messages
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());

		messageField = new JTextField();
		inputPanel.add(messageField, BorderLayout.CENTER);

		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendAction());
		inputPanel.add(sendButton, BorderLayout.EAST);

		add(inputPanel, BorderLayout.SOUTH);
	}

	// Method to append a new message to the message area
	public void appendMessage(String message) {
		messageArea.append(message + "\n");
	}

	// Action listener for the send button
	private class SendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String message = messageField.getText();
			if (!message.trim().isEmpty()) {
				// Format the message and send it to the server via the parent form
				String formattedMessage = parentForm.username + ": " + message;
				parentForm.sendMessageToServer(formattedMessage);
				appendMessage("You: " + message);
				messageField.setText("");
			}
		}
	}
}
