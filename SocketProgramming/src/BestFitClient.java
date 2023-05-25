import java.net.*;

import java.io.*;


public class BestFitClient {
    Socket s;
    DataOutputStream outStream;
    BufferedReader inputStream;
    String lastMessageFromServer = "";
    int noOfServers = 0; //Stores the no of server from the DATA message received from servers
    String currentMessage = null;
    String[] storingData = new String[3]; //an array of Strings that stores the "DATA 5 123" sent by the server, but split into individual strings 
    String[] currentServer = null; //used to store the current Server details we are reading from the input stream line by line,
                                // amongst all the other server details sent
    
    // Server Information (Change this to a Server Class)
    String actualBestFitServerType = ""; //store the actual largest type of server, is updated by comparing itself to currentServerType, whether we can find a bigger one
    String currentServerType = ""; //stores the current server's type
    int currentServerCore = 0;
    int currentServerMemory = 0;
    int currentServerDisk = 0;
    int smallestFitnessValue = Integer.MAX_VALUE; //stores the actual smallest fitness value
    int currentFitnessValue = 0; //stores the fitness value between the job and server we are currently checking
                                    // (Number of remaining cores for server - core requirement of job)
    // Job Information (Change this to Job Class)
    String [] jobString = null; //an array of Strings that stores the "JOBN 101 3 380 2 900 2500" from the Server
    int currentJobIDs = 0; //stores the jobID from the "JOBN 101 3 380 2 900 2500" message we get from Server
    int jobCore = 0;
    int jobMemory = 0;
    int jobDisk = 0;
    String [] firstCapableServerinfo = null;
    int firstCapableServerID = 0;
    String firstCapableServerType = null;

    //Constructor 
    public BestFitClient(String address, int port){
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
            BestFitClient c = new BestFitClient("192.168.138.221",50000);
            c.FCalgorithm();;
            c.s.close();
            c.inputStream.close();
            c.outStream.close();
        } catch (
            Exception e){
            System.out.println(e);
        }
    }

    public void BFalgorithm(){
        try{
            authenticate();
            while(!(lastMessageFromServer.equals("NONE"))){ 
                sendMessage("REDY"); 
                lastMessageFromServer = receiveMessageFromServer(); //JOBN details or can be JCPL
                findBestFitServer();
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    

    public void findBestFitServer(){
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
            for(int i = 0; i < noOfServers; i++){
                currentMessage = receiveMessageFromServer();
                currentServer = convertStringtoArray(currentMessage);
                currentServerCore = Integer.parseInt(currentServer[4]); //No of Cores is in the 4th index (5th position) in the whole message
                currentServerType = currentServer[0];
                // if the current server is in booting stage - do LSTJ on it and see
                // if not do fitnessvalue calculation
                if(currentServerCore >= jobCore){
                    calculateFitnessValue();
                } else {
                    // or do LSTJ here
                    // if that capable but currently lacking in cores server is booting
                    continue; //server cannot be used as too little cores, but...
                }   
            }
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void calculateFitnessValue(){
        currentFitnessValue = currentServerCore - jobCore;
        if(currentFitnessValue < smallestFitnessValue){
            smallestFitnessValue = currentFitnessValue;
            actualBestFitServerType = currentServerType;
        }
    }

    public void LSTJSudo(){

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