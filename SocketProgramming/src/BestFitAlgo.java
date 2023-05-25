import java.net.*;
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
    int currentFitnessValue = 0; //stores the fitness value between the job and server we are currently checking
                                    // (Number of remaining cores for server - core requirement of job)
    // Job Information (Change this to Job Class)
    String [] jobString = null; //an array of Strings that stores the "JOBN 101 3 380 2 900 2500" from the Server
    NormalJob currentJob; //stores all the information about the current job
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
            BestFitAlgo c = new BestFitAlgo("10.126.131.233",50000);
            c.BFImprovedAlgorithm();;
            c.s.close();
            c.inputStream.close();
            c.outStream.close();
        } catch (
            Exception e){
            System.out.println(e);
        }
    }

    public void BFImprovedAlgorithm(){
        try{
            authenticate();
            while(!(lastMessageFromServer.equals("NONE"))){ 
                sendMessage("REDY"); 
                lastMessageFromServer = receiveMessageFromServer(); //JOBN details or can be JCPL
                findBestFitServer();
            }
            endConnection();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    public void scheduleJobs(){
        try{
            sendMessage("SCHD " + currentJob.jobID + " " +actualBestFitServer.serverType + " " + actualBestFitServer.serverID);
            receiveMessageFromServer(); //Receive OK 
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    public void findBestFitServer(){
        try{
            jobString = convertStringtoArray(lastMessageFromServer); 
            if(jobString[0].equals("JOBN")){
            int smallestFitnessValue = Integer.MAX_VALUE; //stores the actual smallest fitness value
            // local variable to see if we found a CAPABLE FOR JOB + INACTIVE/IDLE SERVER
            //boolean capableInactive = false;
            currentJob = new NormalJob(jobString);
            sendMessage("GETS Avail " + currentJob.jobCore + " " + currentJob.jobMemory + " " + currentJob.jobDisk); //Send GETS Avail _ _ _
            setUpDataArrays();
            // receive list of servers
            if(noOfServers != 0){
                for(int i = 0; i < noOfServers; i++){
                    setUpServerArrays();
                    currentFitnessValue = currentServer.serverCore - currentJob.jobCore;
                    if(currentFitnessValue < smallestFitnessValue){
                        smallestFitnessValue = currentFitnessValue;
                        actualBestFitServer = currentServer;
                    } 
                }
                sendMessage("OK");
                receiveMessageFromServer(); //receive dot
            }
            // receive dot regardless 
           
            if(noOfServers == 0){
                receiveMessageFromServer(); //receive dot
                int actualShortestLocalQueue = Integer.MAX_VALUE;
                sendMessage("GETS Capable " + currentJob.jobCore + " " + currentJob.jobMemory + " " + currentJob.jobDisk);
                setUpDataArrays();
                 // Find the capable server with the SHORTEST queue
                for(int i = 0; i < noOfServers; i++){
                    setUpServerArrays();  
                    if(currentServer.totalJobs < actualShortestLocalQueue){
                        actualShortestLocalQueue = currentServer.totalJobs;
                        actualBestFitServer = currentServer;
                    }
                }
                sendMessage("OK");
                // receive dot regardless 
                receiveMessageFromServer(); //receive dot
            }
            scheduleJobs();
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void setUpDataArrays(){
        currentMessage = receiveMessageFromServer();  //receive DATA message
        storingDataArray = convertStringtoArray(currentMessage); //DATA message converted into an array of Strings
        noOfServers = Integer.parseInt(storingDataArray[1]);
        sendMessage("OK");
    }

    public void setUpServerArrays(){
        currentMessage = receiveMessageFromServer();
        currentServerInfoArray = convertStringtoArray(currentMessage);
        currentServer = new Server(currentServerInfoArray);  
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