import java.net.*;  
import java.io.*; 
import java.util.ArrayList;

public class DsClient {
    Socket s;
    DataOutputStream outStream;
    BufferedReader inputStream;
    // int serverID = 0;
    int jobID = 0;

    ArrayList<String> eachServer;
    ArrayList<ArrayList<String>> allServers;
    // = new ArrayList<>();
    

    

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
    public void algorithm() throws Exception{

      //HANDSHAKE/AUTHERTICATING 
        sendMessage("HELO"); //Send HELO
        System.out.println("Server says 0: "+this.inputStream.readLine()); //Receive OK
        String username = System.getProperty("user.name"); 
        sendMessage("AUTH " +username); //SEND AUTH
        System.out.println("Server says 1: "+ this.inputStream.readLine()); //Receive OK
        String lastMessageFromServer = null;
        String[] storingData = new String[3];
        int counter = 0;
        while(lastMessageFromServer != "NONE"){
            sendMessage("REDY"); //when we send ready server sends us an update, usually a job from the USER side of the server
            //if I need get JCPL -> message from the Server Side of the simulator 
            lastMessageFromServer = this.inputStream.readLine();
            System.out.println("Server says : "+ lastMessageFromServer);
            sendMessage("SCHD " + jobID + " xlarge " + 0);
            jobID++;
            
            System.out.println("Server says: "+ this.inputStream.readLine());
        }
        // sendMessage("SCHD 0 xlarge 0");


        sendMessage("QUIT");
        System.out.println("Server says: "+ this.inputStream.readLine());


    }

    public void sendMessage(String message ) throws Exception{
        this.outStream.write( (message + "\n").getBytes("UTF-8"));
    }


    // while(lastMessageFromServer != "NONE"){
    //     sendMessage("REDY"); //when we send ready server sends us an update, usually a job from the USER side of the server
    //     //if I need get JCPL -> message from the Server Side of the simulator 
    //     lastMessageFromServer = this.inputStream.readLine();
    //     System.out.println("Server says : "+ lastMessageFromServer);
    //     sendMessage("SCHD " + jobID + " xlarge " + 0);
    //     jobID++;
        
    //     System.out.println("Server says: "+ this.inputStream.readLine());
    // }
}