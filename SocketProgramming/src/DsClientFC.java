import java.net.*;

import java.io.*;


public class DsClientFC {
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
    
    int jobCore = 0;
    int jobMemory = 0;
    int jobDisk = 0;
    String [] firstCapableServerinfo = null;
    int firstCapableServerID = 0;
    String firstCapableServerType = null;

    //Constructor 
    public DsClientFC(String address, int port){
        try{
            s = new Socket(address, port);
            outStream = new DataOutputStream(s.getOutputStream());
            inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));  
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args){
        try{
            DsClientFC c = new DsClientFC("192.168.138.221",50000);
            c.FCalgorithm();;
            c.s.close();
            c.inputStream.close();
            c.outStream.close();
        } catch (
            Exception e){
            System.out.println(e);
        }
    }

    public void FCalgorithm(){
        try{
            authenticate();
            while(!(lastMessageFromServer.equals("NONE"))){ 
                sendMessage("REDY"); 
                lastMessageFromServer = receiveMessageFromServer(); //JOBN details or can be JCPL
                findFirstCapable();
            }
            endConnection();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void findFirstCapable(){
        try{
           jobString = convertStringtoArray(lastMessageFromServer); 
           if(jobString[0].equals("JOBN")){
            currentJobIDs = Integer.parseInt(jobString[2]);
            jobCore = Integer.parseInt(jobString[4]);
            jobMemory =  Integer.parseInt(jobString[5]);
            jobDisk = Integer.parseInt(jobString[6]);
            sendMessage("GETS Capable " + jobCore + " " + jobMemory + " " + jobDisk); //Send GETS Capable _ _ _
            currentMessage = receiveMessageFromServer();  //receive DATA message
            storingData = convertStringtoArray(currentMessage); //DATA message converted into an array of Strings
            noOfServers = Integer.parseInt(storingData[1]);
            
            // we do not need the number of servers anymore, so nothing to extract from currentMessage?
            // as we are assigning the current Job to the current first server from the Gets Capable message
            sendMessage("OK");
           // receive list of servers
            firstCapableServerinfo = convertStringtoArray(receiveMessageFromServer()); //receive the first server message
            firstCapableServerID = Integer.parseInt(firstCapableServerinfo[1]);
            firstCapableServerType = firstCapableServerinfo[0];
            //receive the rest server messages
            for(int i = 0; i < noOfServers-1; i++){
                receiveMessageFromServer();
            }
            sendMessage("OK");
            receiveMessageFromServer(); //receive dot
            sendMessage("SCHD " + currentJobIDs + " " +firstCapableServerType + " " + firstCapableServerID);
            receiveMessageFromServer(); //receive OK
           }
          }catch(Exception e){
            System.out.println(e);
          }
    }

    

    public void sendMessage(String message){
        try{
            this.outStream.write( (message + "\n").getBytes("UTF-8"));
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public String receiveMessageFromServer() {
        String msg = "";
        try {
            msg = this.inputStream.readLine();
            // System.out.println("Server says: " +msg);
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return msg;
    }

    //Used to convert the string being passed in as parameter, into a array of Strings
    //if the String passed in has 3 words, we will have an array of 3 elements returned, where each element is String
    public String[] convertStringtoArray (String s){
        return s.split(" ");
    }

    public void authenticate(){
        try{
            sendMessage("HELO"); //Send HELO
            receiveMessageFromServer(); //Receive OK
            String username = System.getProperty("user.name"); 
            sendMessage("AUTH " +username); //SEND AUTH
            receiveMessageFromServer(); //Receive OK
        }catch(Exception e){
            System.out.println(e);
        }
    }


    public void endConnection(){
        try{
            sendMessage("QUIT");
            receiveMessageFromServer(); //Receive Quit
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
}