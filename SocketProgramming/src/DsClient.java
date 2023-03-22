import java.net.*;  
import java.io.*; 
public class DsClient {
    Socket s;
    DataOutputStream outStream;
    BufferedReader inputStream;

    //Constructor 

    public DsClient(String address, int port) throws Exception{
          s = new Socket(address, port);
          outStream = new DataOutputStream(s.getOutputStream());
          inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));  

    }

    public static void main(String[] args) throws Exception{
        DsClient c = new DsClient("10.126.133.191",50000);
        c.byClient();

        c.s.close();
        c.inputStream.close();
        c.outStream.close();
    }


    public void byClient() throws Exception{
        sendMessage("HELO");
        System.out.println("Server says: "+this.inputStream.readLine());
        String username = System.getProperty("user.name"); //systemcall to opsys to give me the user executing this program
        sendMessage("AUTH " +username);
        System.out.println("Server says: "+ this.inputStream.readLine());

        //we closed the connection with ds-sim without telling it that I am going to quit 
        sendMessage("REDY"); //when we send ready server sends us an update, usually a job from the USER side of the server
                                     //if I need get JCPL -> message from the Server Side of the simulator 
        System.out.println("Server says: "+ this.inputStream.readLine());
        sendMessage("QUIT");
        System.out.println("Server says: "+ this.inputStream.readLine());

    }

    public void sendMessage(String message ) throws Exception{
        this.outStream.write( (message + "\n").getBytes("UTF-8"));
    }
}
