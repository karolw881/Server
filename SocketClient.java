import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class SocketClient extends JFrame {
    private JTextField userIdField;
    private JTextArea messageArea;
    private JButton sendButton;

    public SocketClient() {
        setTitle("Socket Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Layout components
        setLayout(new BorderLayout());
        
        // User ID Panel
        JPanel userPanel = new JPanel();
        userPanel.add(new JLabel("Your Nickname:"));
        userIdField = new JTextField(15);
        userPanel.add(userIdField);
        add(userPanel, BorderLayout.NORTH);
        
        // Message Area
        messageArea = new JTextArea(10, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Send Button
        sendButton = new JButton("Send Message");
        sendButton.addActionListener(e -> sendMessage());
        add(sendButton, BorderLayout.SOUTH);
    }

    private void sendMessage() {
        String userId = userIdField.getText().trim();
        String message = messageArea.getText().trim();
        
        if (userId.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter User ID and Message");
            return;
        }

        try {
            // Socket connection details
            String host = "localhost";
            int port = 6196;
            
            Socket connection = new Socket(host, port);
            
            // Prepare message with user ID
            String fullMessage = userId + ": " + message;
            
            // Send message
            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
            osw.write(fullMessage + (char)13);
            osw.flush();
            
            // Read response
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");
            
            StringBuilder response = new StringBuilder();
            int c;
            while ((c = isr.read()) != 13) {
                response.append((char)c);
            }
            
            // Show server response
            JOptionPane.showMessageDialog(this, "Server Response: " + response.toString());
            
            connection.close();
            
            // Clear message area after successful send
            messageArea.setText("");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error sending message: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SocketClient().setVisible(true);
        });
    }
}
