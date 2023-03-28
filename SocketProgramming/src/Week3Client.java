import java.net.*;  
import java.io.*; 

public class Week3Client {
    public static void main(String args[])throws Exception{  
        Socket s = new Socket("localhost",6666);  
        //we cannot send raw data from client to server directly
        //convert the data into stream --> socketObject.getOutput
      //  DataInputStream din=new DataInputStream(s.getInputStream());  
        DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
        BufferedReader inbr=new BufferedReader(new InputStreamReader(s.getInputStream()));  
        

        //getInputStream() returns an input stream, after reading bytes from the socket
           //that stream of bytes is stored in din
        
        
      // System.out.println("checking");
       dout.writeUTF("nON\n"); //Client says Hello 
       System.out.println("Server says: "+inbr.readLine()); 
       dout.writeUTF("BYE\n");  
       System.out.println("Server says: "+inbr.readLine()); 

       

    //     // String str="",str2="";  
    //     String str = "HELLO";
    //     String str2 = "GDAY";
    //     while(!str.equals("BYE")){  
    //   //  str=inbr.readLine();  //converts bytes to characters
    //     dout.writeUTF(inbr.readLine());  
     //   dout.flush();  

    //   //  str2 =din.readUTF();
    //     // str2=inbr.readLine();  
    //     System.out.println("Server says: "+inbr.readLine());  
    //     }  
          
        dout.close();  
        s.close();  
        }
}

