import java.io.*;
import java.net.*;
import java.util.*;

public class MultiClient_OneWay_Client {
    public static void main(String args[]) {
        try {
            Socket cs = new Socket("192.168.100.192", 5000);
            System.out.println("Connected to Server: " + cs.getInetAddress());

            PrintWriter out = new PrintWriter(cs.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            Scanner sc = new Scanner(System.in);

            String mssg;
            while (true) {
                System.out.print("Me: ");
                mssg = sc.nextLine();
                out.println(mssg); // Send message to server

                if (mssg.equalsIgnoreCase("Exit")) {
                    System.out.println("Client disconnected!");
                    break;
                }

                mssg = in.readLine(); // Receive message from server
                if (mssg == null) {
                    System.out.println("Server disconnected!");
                    break;
                }
                System.out.println(mssg);
            }

            cs.close();
            sc.close();

        } catch (Exception e) {
            System.out.println("Server is offline!");
        }
    }
}