import java.net.*;
import java.io.*;
import java.util.Scanner;

public class BasicServer {
    public static void main(String args[]) {
        System.out.println("Server is being prepared!\n");
        try (ServerSocket ss = new ServerSocket(5000)) {

            System.out.println("Server is Ready! Clients can be connected.\n");
            Socket cs = ss.accept(); // Socket object gets created if a client is connected.
            System.out.println(cs.getInetAddress() + ", " + cs.getPort() + " Client is connected!\n");

            PrintWriter out = new PrintWriter(cs.getOutputStream(), true); // Used to send mssg to the client
            BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            Scanner sc = new Scanner(System.in);

            String outmsg = "";
            String inmsg = "";

            do {
                System.out.print("Server: ");
                outmsg = sc.nextLine();  
                out.println(outmsg); 

                if (outmsg.equals("over")) break; 

                inmsg = in.readLine();  
                System.out.println("Client: " + inmsg);
				System.out.println();
            } while (!inmsg.equals("over") && !outmsg.equals("over"));

            in.close();
            out.close();
            cs.close();
            sc.close();
            
        } catch (Exception ex) {
            System.out.println("Server cannot be connected!");
        }
    }
}
