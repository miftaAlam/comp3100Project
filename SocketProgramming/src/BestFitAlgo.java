import java.net.*;
import java.util.ArrayList;
import java.io.*;


public class BestFitAlgo {
    Socket s;
    DataOutputStream outStream;
    BufferedReader inputStream;
    String lastMessageFromServer = "";
    int noOfServers = 0; //Stores the no of server from the DATA message received from servers
    String currentMessage = null;
    String[] storingDataArray = new String[3]; //an array of Strings that stores the "DATA 5 123" sent by the server, but split into individual strings 
    
    // Server Information (Change this to a Server Class)
    String[] currentServerInfoArray = null; //used to store the current Server details we are reading from the input stream line by line,
                                                // amongst all the other server details sent
    Server actualBestFitServer; //store the actual best fit server, is updated by comparing itself to currentServer, whether we can find a server with a smaller best fit
    Server currentServer; //stores the Current Server we are iterating through
    int smallestFitnessValue = Integer.MAX_VALUE; //stores the actual smallest fitness value
    int currentFitnessValue = 0; //stores the fitness value between the job and server we are currently checking
                                    // (Number of remaining cores for server - core requirement of job)
    // Job Information (Change this to Job Class)
    String [] jobString = null; //an array of Strings that stores the "JOBN 101 3 380 2 900 2500" from the Server
    NormalJob currentJob; //stores all the information about the current job
    ArrayList<WaitingRunningJob> lstjResult = new ArrayList<WaitingRunningJob>();

    //Constructor 
    public BestFitAlgo(String address, int port){
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
            BestFitAlgo c = new BestFitAlgo("192.168.138.221",50000);
            //c.FCalgorithm();;
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
            currentJob = new NormalJob(jobString);
            if(jobString[0].equals("JOBN")){
            currentJob = new NormalJob(jobString);
            sendMessage("GETS Capable " + currentJob.jobCore + " " + currentJob.jobMemory + " " + currentJob.jobDisk); //Send GETS Capable _ _ _
            currentMessage = receiveMessageFromServer();  //receive DATA message
            storingDataArray = convertStringtoArray(currentMessage); //DATA message converted into an array of Strings
            noOfServers = Integer.parseInt(storingDataArray[1]);
            sendMessage("OK");
            // receive list of servers
            for(int i = 0; i < noOfServers; i++){
                currentMessage = receiveMessageFromServer();
                currentServerInfoArray = convertStringtoArray(currentMessage);
                currentServer = new Server(currentServerInfoArray);
                // if the current server is in booting stage - do LSTJ on it and see
                // if not do fitnessvalue calculation
                if(currentServer.serverCore >= currentJob.jobCore){
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
        currentFitnessValue = currentServer.serverCore - currentJob.jobCore;
        if(currentFitnessValue < smallestFitnessValue){
            smallestFitnessValue = currentFitnessValue;
            actualBestFitServer = currentServer;
        }
    }

    public void LSTJSudo(){

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
