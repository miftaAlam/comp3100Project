import java.net.*;  
import java.io.*; 
public class Week4DsClient {
    Socket s;
    DataOutputStream outStream;
    BufferedReader inputStream;

    //Constructor 

    public Week4DsClient(String address, int port) throws Exception{
          s = new Socket(address, port);
          outStream = new DataOutputStream(s.getOutputStream());
          inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));  

    }

    public static void main(String[] args) throws Exception{

        //USED Bridged adapter in VM and got the IP address of the VM machine 
        //The port number used by the ds-server is 50000 by default 
        Week4DsClient c = new Week4DsClient("10.126.133.191",50000);
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
                                     //if I need get JCPL -> message from the Server Side of the simulator --> means a job is done 
        System.out.println("Server says: "+ this.inputStream.readLine());
        sendMessage("QUIT");
        System.out.println("Server says: "+ this.inputStream.readLine());

    }

    public void sendMessage(String message ) throws Exception{
        this.outStream.write( (message + "\n").getBytes("UTF-8")); //must have a newline character so the Server knows that there
                                                                               //is nothing more to read
    }
}