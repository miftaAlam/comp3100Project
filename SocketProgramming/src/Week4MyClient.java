import java.net.*;  
import java.io.*; 

public class MyClient {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("192.168.230.221", 6666);
        DataOutputStream outStream = new DataOutputStream(s.getOutputStream());
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));
        
        outStream.write(("HELO\n").getBytes("UTF-8"));
        System.out.println("Server says: "+inputStream.readLine());
        outStream.write(("GDAY\n").getBytes("UTF-8"));
        System.out.println("Server says: "+ inputStream.readLine());


        outStream.close();
        inputStream.close();
        s.close();



    }
}
