import java.io.*;
import java.net.*;
import java.util.*;

class ClientHandler implements Runnable {

    private Socket cs;

    ClientHandler(Socket cs) {
        this.cs = cs;
    }

    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            PrintWriter out = new PrintWriter(cs.getOutputStream(), true);
            Scanner sc = new Scanner(System.in)
        ) {
            out.println("Welcome! Enter 'exit' to close the connection.");
            String mssg;

            while (true) {
                mssg = in.readLine(); // Read message from client
                if (mssg == null || mssg.equalsIgnoreCase("Exit")) {
                    System.out.println("Client disconnected!");
                    break;
                }
                System.out.println("Client: " + mssg);

                System.out.print("Server: ");
                mssg = sc.nextLine(); // Read message from server
                out.println(mssg);

                if (mssg.equalsIgnoreCase("Exit")) {
                    System.out.println("Server is closing connection.");
                    break;
                }
                System.out.println("Server: "+mssg);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                cs.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}

public class MultiClient_OneWay_Server {
    public static void main(String args[]) {
        try (ServerSocket ss = new ServerSocket(5000)) {
            System.out.println("Server is ready!\n");

            while (true) {
                Socket cs = ss.accept();
                System.out.println("Client connected: " + cs.getInetAddress() + ", Port: " + cs.getPort());

                ClientHandler ch = new ClientHandler(cs);
                new Thread(ch).start(); // Start a new thread for each client
            }

        } catch (IOException io) {
            System.out.println("IO Error: " + io.getMessage());
        } catch (SecurityException se) {
            System.out.println("Security Error: " + se.getMessage());
        } catch (IllegalArgumentException iae) {
            System.out.println("Invalid Port: " + iae.getMessage());
        }
    }
}