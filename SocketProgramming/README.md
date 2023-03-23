1. Create a connection between a client and a server using TCP. The end to end connections are created using Sockets
2. a Socket object takes in 2 parameters, "local host" and port number --> used to create a connection through this particular port number
3. 



For the algorithm:

1. Receive DATA 5 124
2. Create a function, that places that in an ArrayList  of strings called data;
   where data.get(0) is "DATA"
         data.get(1) --> which is "5" -> the number of servers we have to loop through


         create another function that returns all the characters of the String, until you reach the character ""
          --> make use of charAt(index) --> and increments the index
          --> while loop terminates when character is ""
        and then we restart the loop? and store to the next element of the arrayList? 
        or just after you reach the first "" and add it to the 
