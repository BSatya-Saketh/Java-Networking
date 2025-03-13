import java.io.*;
import java.net.*;

public class Client
{
public static void main(String args[]) throws Exception
{
Socket cs = new Socket("192.168.29.120",4000);
BufferedReader br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
String msg = br.readLine();
System.out.println("Server :" + msg);
br.close();
cs.close();
}
}