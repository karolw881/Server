import java.net.*;
import java.io.*;
import java.util.*;

public class MultipleSocketServer implements Runnable {
    private Socket connection;
    private int ID;
    private String connectionTime;

    public static void main(String[] args) {
        int port = 6196;
        int count = 0;
        try {
            ServerSocket socket1 = new ServerSocket(port);
            System.out.println("MultipleSocketServer Initialized");
            while (true) {
                Socket connection = socket1.accept();
                Runnable runnable = new MultipleSocketServer(connection, ++count);
                Thread thread = new Thread(runnable);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MultipleSocketServer(Socket s, int i) {
        this.connection = s;
        this.ID = i;
        this.connectionTime = new java.util.Date().toString();
    }

    public void run() {
        try {
            // Log connection time
            System.out.println("Client connected at: " + connectionTime);

            // Read incoming message
            BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
            InputStreamReader isr = new InputStreamReader(is);
            int character;
            StringBuffer process = new StringBuffer();
            while ((character = isr.read()) != 13) {
                process.append((char) character);
            }
            String message = process.toString();
            
            // Extract user ID from message
            String[] parts = message.split(": ", 2);
            String userId = parts[0];
            String userMessage = parts.length > 1 ? parts[1] : "";

            // Log message to user-specific log file
            logMessage(userId, userMessage);

            // Print message to console
            System.out.println(message);

            // Wait 10 seconds
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Prepare and send response with connection time
            String returnCode = "Client connected at: " + connectionTime + " - Current time: " + new java.util.Date().toString() + (char) 13;
            BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
            osw.write(returnCode);
            osw.flush();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void logMessage(String userId, String message) {
        try {
            // Create log file for the user if it doesn't exist
            File logFile = new File(userId + ".log");
            
            // Append message to log file
            try (FileWriter fw = new FileWriter(logFile, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                String timestamp = new java.util.Date().toString();
                out.println(timestamp + ": " + message);
            }
        } catch (IOException e) {
            System.err.println("Error logging message for user " + userId + ": " + e.getMessage());
        }
    }
}
