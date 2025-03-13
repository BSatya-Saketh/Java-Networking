import java.net.*;
import java.io.*;
public class Server
{

public static void main(String args[]) throws Exception
{
ServerSocket ss = new ServerSocket(4000); // 4000 port no.
System.out.println("Server is Ready");
Socket cs = ss.accept();
// communication between client 
PrintStream ps = new PrintStream(cs.getOutputStream(),true);
ps.println("Hello Client");
ps.close();
cs.close();
ss.close();

}
}

