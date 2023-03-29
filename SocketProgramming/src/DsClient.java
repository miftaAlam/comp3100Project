import java.net.*;  
import java.io.*; 

public class DsClient {
    Socket s;
    DataOutputStream outStream;
    BufferedReader inputStream;
    String lastMessageFromServer = "";
    int noOfServers = 0; //Stores the no of server from the DATA message received from servers
    String currentMessage = null;
    String[] storingData = new String[3]; //an array of Strings that stores the "DATA 5 123" sent by the server, but split into individual strings 
    String[] eachServer = null; //used to store the current Server details we are reading from the input stream line by line,
                                // amongst all the other server details sent
    
    String actualLargestType = ""; //store the actual largest type of server, is updated by comparing itself to currentServerType, whether we can find a bigger one
    String currentServerType = ""; //stores the current server's Type we are checking
    
    int actualLargestTypeCount = 0; //counts the total number of servers in the largest type, reset back to 1 whenevever we find a larger type
    int actualMaxCores = 0; //stores the actual largest number of cores in the largest server type
    int currentServerCores = 0; //stores the number of cores in the current server we are checking
    int iterations = 0; //used to make sure we only GETS during the first iteration, to identify the largest group
    int currentJobIDs = 0; //stores the jobID from the "JOBN 101 3 380 2 900 2500" message we get from Server
    String [] jobString = null; //an array of Strings that stores the "JOBN 101 3 380 2 900 2500" from the Server
    int atServer = 0; // keeps track of the serverID of the current server we are assigning the Job to 
    
    //Constructor 
    public DsClient(String address, int port) throws Exception{
          s = new Socket(address, port);
          outStream = new DataOutputStream(s.getOutputStream());
          inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));  
    }

    public static void main(String[] args) throws Exception{
        DsClient c = new DsClient("10.126.137.170",50000);
        c.LRRalgorithm();
        c.s.close();
        c.inputStream.close();
        c.outStream.close();
    }

    public void LRRalgorithm(){
        try{
            while(!(lastMessageFromServer.equals("NONE"))){ 
                sendMessage("REDY"); 
                lastMessageFromServer = this.inputStream.readLine(); //JOBN details or can be JCPL
                if(iterations == 0){
                   findLargestServerType();
                }
                iterations++;
                    jobString = convertStringtoArray(lastMessageFromServer); 
                    System.out.println(lastMessageFromServer);
                    if(jobString[0].equals("JOBN")){ // as instead of JOBN, it can be JCPL, we only want to schedule a job when we receive one
                       currentJobIDs = Integer.parseInt(jobString[2]);
                       System.out.println("Server says : "+ lastMessageFromServer);
                       sendMessage("SCHD " + currentJobIDs + " " +actualLargestType + " " + atServer);
                       System.out.println("Server says: "+ this.inputStream.readLine());
                       //as there can be more jobs than the no of servers in the largest server type
                       //so we use mod to wrap around, once we reach the last server, we wrap back to serverID 0 
                       atServer = (atServer+1) % actualLargestTypeCount; 
                    }
            }
    
    
            sendMessage("QUIT");
            System.out.println("Server says: "+ this.inputStream.readLine());
        }catch(Exception e){

        }
    }

    public void sendMessage(String message ) throws Exception{
        this.outStream.write( (message + "\n").getBytes("UTF-8"));
    }

    //Used to convert the string being passed in as parameter, into a array of Strings
    //if the String passed in has 3 words, we will have an array of 3 elements returned, where each element is String
    public String[] convertStringtoArray (String s){
        return s.split(" ");
    }

    public void authenticate(){
        try{
            sendMessage("HELO"); //Send HELO
            System.out.println("Server says 0: "+ this.inputStream.readLine()); //Receive OK
            String username = System.getProperty("user.name"); 
            sendMessage("AUTH " +username); //SEND AUTH
            System.out.println("Server says 1: "+ this.inputStream.readLine()); //Receive OK
        }catch(Exception e){

        }
        
    }

    public void findLargestServerType(){
          try{
            sendMessage("GETS All");
            currentMessage = this.inputStream.readLine(); //server sends the DATA message 
            storingData = convertStringtoArray(currentMessage); //DATA message converted into an array of Strings
            noOfServers = Integer.parseInt(storingData[1]); //the 2nd word in the DATA message is always the number of servers,
                                                            //so it would be the element at index 1 in the array storingData
            sendMessage("OK");
                //we receive a LIST of all the servers, and we read them line by line 
                for(int i = 0; i<noOfServers; i++){
                    currentMessage = this.inputStream.readLine(); 
                    eachServer = convertStringtoArray(currentMessage);
                    currentServerCores = Integer.parseInt(eachServer[4]); //No of Cores is in the 4th index (5th position) in the whole message
                    currentServerType = eachServer[0]; //server type is in the 0th index (1st Position) in the whole message
                    actualLargestTypeCount++;
   
                   if(currentServerCores>actualMaxCores && !(currentServerType.equals(actualLargestType))){
                         actualLargestType = currentServerType;
                         actualMaxCores = currentServerCores;
                         actualLargestTypeCount = 1; //reset 
                   }
                   
                   //if we find another server type, that has the same number of cores as the previous type
                   //we ignore it, as we want the first largest type (they are all in sequential order via no of cores)
                   if(currentServerCores == actualMaxCores && !(currentServerType.equals(actualLargestType))){
                       continue; //move on to the next server
                   }
               }
               sendMessage("OK");
          }catch(Exception e){

          }
    }




}