import java.io.*;
import java.net.*;
public class Week4MyServer {
    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(6666); 
        Socket s = ss.accept();
        DataOutputStream outStream = new DataOutputStream(s.getOutputStream());
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));
        
        System.out.println("Client says:" + inputStream.readLine());
        outStream.write(("GDAY\n").getBytes("UTF-8"));
        System.out.println("Client says:" +inputStream.readLine());
        outStream.write(("BYE\n").getBytes("UTF-8"));


        outStream.close();
        inputStream.close();
        s.close();
        ss.close();
    }
}
