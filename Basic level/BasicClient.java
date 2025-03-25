import java.io.*;
import java.net.*;
import java.util.*;

public class BasicClient {
    public static void main(String args[]) {
        try (Socket cs = new Socket("192.168.100.192", 5000)) {

            PrintWriter out = new PrintWriter(cs.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            Scanner sc = new Scanner(System.in);

            String outmsg = "";
            String inmsg = "";
            System.out.println();
            do {
                // Receiving message from server
                inmsg = in.readLine();
                System.out.println("Server: " + inmsg);
                if (inmsg.equals("over")) break;

                // Sending message to server
                System.out.print("Me: ");
                outmsg = sc.nextLine();  // Corrected input reading
                out.println(outmsg);
                System.out.println();
            } while (!outmsg.equals("over") && !inmsg.equals("over"));

            in.close();
            out.close();
            sc.close();
            
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}