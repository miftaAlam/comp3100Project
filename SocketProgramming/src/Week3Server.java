import java.io.*;
import java.net.*;

public class Week3Server {
    public static void main(String[] args){
      try{
         // listening in and accepting a connection
         ServerSocket ss = new ServerSocket(6666);  // when we start the server, just listens for new connections
         Socket s = ss.accept();  // when the Client sends the SYN, the serversocket accepts the client connection --> which returns a socket
                                  // to the connection sent by that particular client (as the serversocket can listen for multiple connections)
         
         //.getOutputStream() creates a stream for the socket s to send output (stream of data)
         //the stream returned by .getOutputStrem is stored in the dout object
         DataOutputStream dout = new DataOutputStream(s.getOutputStream());  // sending output  
         // BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); //Do not use System.in 
         
         //.getInputStream() creating a stream for the socket s to receive input (stream of data)
         //the input stream returned by the s.getInputStream() function is stored in the din object 
         //DataInputStream din = new DataInputStream(s.getInputStream());  // receiving input
         //but we wrap that din object with a BufferedReader 
         BufferedReader inbr = new BufferedReader(new InputStreamReader(s.getInputStream())); 
          System.out.println(inbr.readLine());
          dout.writeUTF("GDAY\n"); //UTF does not work well, adds in silly character in the beginning 
          System.out.println(inbr.readLine());
          dout.writeUTF("BYE\n");


          //We are not meant to use System.in to type in the console and send messages back and forth
          //everything needs to be automated as there are specific COMMANDS that needs to be sent back and forth automatically

         // String str = "HELO";
         // String str2 = "GDAY";

         // while(!str.equals("BYE")){  
         //  //  str=din.readUTF();  
         //  //  str = inbr.readLine();
         //    System.out.println("client says: "+inbr.readLine());  
         //   // str2= inbr.readLine();  
         //    dout.writeUTF(inbr.readLine());  

          //dout.flush();  
         // }  


         inbr.close();  
         s.close();  
         ss.close();  

      } catch(Exception e){
        System.out.println(e);
      }
   }
}
