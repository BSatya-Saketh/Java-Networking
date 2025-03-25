import java.io.*;
import java.net.*;

public class IntermediateClient {
    public static void main(String args[]) {
        try (Socket cs = new Socket("192.168.29.122", 5001)) {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                BufferedReader userinput = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out = new PrintWriter(cs.getOutputStream(), true)
            ) {
                // Read welcome message from server
                String serverMsg = in.readLine();
                System.out.println(serverMsg);
                
                // Entering Username
                String username = userinput.readLine();
                out.println(username);
                
                // Read server response about username
                serverMsg = in.readLine();
                System.out.println(serverMsg);

                // Thread to Receive messages
                Thread RecieverThread = new Thread(() -> {
                    try {
                        String inmsg;
                        while ((inmsg = in.readLine()) != null) {
                            System.out.println(inmsg);
                        }
                    } catch (IOException e) {
                        System.out.println("Error receiving message: " + e.getMessage());
                    }
                });

                // Thread to Send messages
                Thread WriterThread = new Thread(() -> {
                    try {
                        String outmsg;
                        while (true) { 
                            outmsg = userinput.readLine();
                            if (outmsg.equalsIgnoreCase("exit")) {
                                out.println("exit");
                                break;
                            }
                            out.println(outmsg);
                        }
                    } catch (IOException e) {
                        System.out.println("Error sending message: " + e.getMessage());
                    }
                });

                // Start threads
                RecieverThread.start();
                WriterThread.start();

                // Wait for threads to finish before closing the socket
                RecieverThread.join();
                WriterThread.join();
            } catch (Exception e) {
                System.out.println("Client error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}