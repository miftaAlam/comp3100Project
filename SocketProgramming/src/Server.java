import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args){
      try{
         // listening in and accepting a connection
         ServerSocket ss = new ServerSocket(6666);  // when we start the server, just listens for new connections
         Socket s = ss.accept();  // when the Client sends the SYN, the serversocket accepts the client connection --> which returns a socket
                                  // to the connection sent by that particular client (as the serversocket can listen for multiple connections)
         
         //.getInputStream() creating a stream for the socket s to receive input (stream of data)
         //the input stream eturned by the .getInputStream() function is stored in the din object 

     //    DataInputStream din = new DataInputStream(s.getInputStream());  // receiving input
         
         DataOutputStream dout = new DataOutputStream(s.getOutputStream());  // sending output  
        // BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 

         BufferedReader inbr = new BufferedReader(new InputStreamReader(s.getInputStream())); 
       
      //    System.out.println("checking");
          System.out.println(inbr.readLine());
          dout.writeUTF("GDAY\n");
          System.out.println(inbr.readLine());
          dout.writeUTF("BYE\n");

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
