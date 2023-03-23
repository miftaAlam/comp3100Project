import java.net.*;  
import java.io.*; 
import java.util.ArrayList;

public class DsClient {
    Socket s;
    DataOutputStream outStream;
    BufferedReader inputStream;
    // int serverID = 0;
    int jobID = 0;

    // ArrayList<String> eachServer;
    ArrayList<String[]> allServers;
    // = new ArrayList<>();
    

    

    //Constructor 

    public DsClient(String address, int port) throws Exception{
          s = new Socket(address, port);
          outStream = new DataOutputStream(s.getOutputStream());
          inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));  
          allServers = new ArrayList<>();

    }

    public static void main(String[] args) throws Exception{
        DsClient c = new DsClient("10.126.133.191",50000);
        c.algorithm();

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
        String lastMessageFromServer = "";
        int noOfServers = 0;
        String currentMessage = null;
        String[] storingData = new String[3];
        String[] eachServer = null;
        
        String actualLargestType = "";
        String currentLargestType = "";
        // int actualLargestTypeCount = 0;
        int currentLargestTypeCount = 0;
        
        int actualMaxCores = 0;
        int currentHighestCores = 0;
        int iterations = 0;
        int currentJobIDs = 0;
        String [] jobString = null;
        int atServer = 0;
        
       
        while(!(lastMessageFromServer.equals("NONE"))){ 


            sendMessage("REDY"); //when we send ready server sends us an update, usually a job from the USER side of the server
            //if I need get JCPL -> message from the Server Side of the simulator
            lastMessageFromServer = this.inputStream.readLine(); //JOB details 
            if(iterations == 0){
            // sendMessage("REDY"); //when we send ready server sends us an update, usually a job from the USER side of the server
            // //if I need get JCPL -> message from the Server Side of the simulator
            // lastMessageFromServer = this.inputStream.readLine(); //JOB details 

            sendMessage("GETS All");
            currentMessage = this.inputStream.readLine();
            storingData = convertStringtoArray(currentMessage);
            noOfServers = Integer.parseInt(storingData[1]);
            sendMessage("OK");
            
                for(int i = 0; i<noOfServers; i++){
                    currentMessage = this.inputStream.readLine();
                    eachServer = convertStringtoArray(currentMessage);
                    currentHighestCores = Integer.parseInt(eachServer[4]);
                    currentLargestType = eachServer[0];
                    currentLargestTypeCount++;
   
                   if(currentHighestCores>actualMaxCores && !(currentLargestType.equals(actualLargestType))){
                         actualLargestType = currentLargestType;
                         actualMaxCores = currentHighestCores;
                         currentLargestTypeCount = 1;
                   }
                   
                   if(currentHighestCores == actualMaxCores && !(currentLargestType.equals(actualLargestType))){
                       continue;
                   }
   
   
               }
               sendMessage("OK");
   
            }
            iterations++;

            //get the JOB number, see where to save that 
            // int atServer = 0;
            // while(lastMessageFromServer!= "NONE"){
                // sendMessage("REDY"); //when we send ready server sends us an update, usually a job from the USER side of the server
                // //if I need get JCPL -> message from the Server Side of the simulator 
                // lastMessageFromServer = this.inputStream.readLine(); //JOB details 
                jobString = convertStringtoArray(lastMessageFromServer);
                System.out.println(lastMessageFromServer + "man");
                if(jobString[0].equals("JOBN")){
                // jobString = convertStringtoArray(lastMessageFromServer);
                currentJobIDs = Integer.parseInt(jobString[2]);
                System.out.println("Server says : "+ lastMessageFromServer);
                // if(jobString[0].equals("JOBN")){
                 sendMessage("SCHD " + currentJobIDs + " " +actualLargestType + " "+ atServer);
                 System.out.println("Server says: "+ this.inputStream.readLine());
                 

                // atServer++;
                atServer = (atServer+1) % currentLargestTypeCount;

                }
                
          //  }
            
            // System.out.println("Server says : "+ lastMessageFromServer);
            // sendMessage("SCHD " + jobID + " xlarge " + 0);
            // jobID++;
            
            
        }
        // sendMessage("SCHD 0 xlarge 0");


        sendMessage("QUIT");
        System.out.println("Server says: "+ this.inputStream.readLine());


    }

    public void sendMessage(String message ) throws Exception{
        this.outStream.write( (message + "\n").getBytes("UTF-8"));
    }

    // public int extractNoOfServers(String s){
    //     // int countSpace = 0;
    //     char currChar = ' ';
    //     int index = 0;
    //     String storingNum = null;
    //     int noOfServers = 0;
    
        
    //     while(currChar!= ' '){
    //         currChar = s.charAt(index);
    //         index++;
    //     }

    //     while(currChar!= ' '){
    //         storingNum = storingNum + currChar;
    //     }

    //     noOfServers = Integer.parseInt(storingNum);

    //     return noOfServers;
    // }

    public String[] convertStringtoArray (String s){
        return s.split(" ");
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