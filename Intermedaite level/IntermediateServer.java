import java.io.*;
import java.net.*;
import java.util.Hashtable;

public class IntermediateServer {
    static Hashtable<String, PrintWriter> users = new Hashtable<>();

    public static void main(String[] args) {
        System.out.println("Server is getting ready!\n");

        try (ServerSocket ss = new ServerSocket(5001)) {
            System.out.println("Server is ready!");

            while (true) {
                try {
                    Socket cs = ss.accept();
                    new ClientHandler(cs, users).start();
                } catch (SocketException se) {
                    se.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket cs;
    private String username;
    private BufferedReader in;
    private PrintWriter out;
    private Hashtable<String, PrintWriter> users;

    ClientHandler(Socket cs, Hashtable<String, PrintWriter> users) {
        this.cs = cs;
        this.users = users;
    }

    public void run() {
        try {
            // Initialize input and output streams
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            out = new PrintWriter(cs.getOutputStream(), true);

            // Ask for username
            out.println("Welcome! Enter your username:");
            username = in.readLine();

            if (username != null && !users.containsKey(username)) {
                users.put(username, out);
                out.println("Username '" + username + "' added!");
                System.out.println("User '" + username + "' joined the server.");
                
                // Broadcast to other users
                for (PrintWriter writer : users.values()) {
                    if (writer != out) {
                        writer.println(username + " joined the chat!");
                    }
                }
            } else {
                out.println("Username already exists or invalid.");
                cs.close();
                return;
            }

            // Keep connection open for communication
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                
                System.out.println(username + ": " + message);
                
                // Broadcast message to all users
                for (PrintWriter writer : users.values()) {
                    if (writer != out) {
                        writer.println(username + ": " + message);
                    }
                }
            }

            // Remove user and close connection
            users.remove(username);
            System.out.println("User '" + username + "' left the server.");
            
            // Notify other users
            for (PrintWriter writer : users.values()) {
                writer.println(username + " left the chat!");
            }
            
            cs.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (username != null) {
                users.remove(username);
            }
        }
    }
}