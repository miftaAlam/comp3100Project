./ds-server -c ../../configs/sample-configs/ds-sample-config01.xml
./ds-server -c ../../configs/sample-configs/ds-sample-config02.xml -n -v all 
./ds-server -c ../../configs/sample-configs/ds-sample-config02.xml -n -v all -i (for interactive mode)

To run the given ds-client
./ds-client -a bf

- always open server first as they are able to sit and wait 


Instead of compiling them inside of src, put the compiled .class files inside the bin folder 
 javac src\*.java -d bin //so place all the compiled .class files in the bin folder (d for destination)

To run the .class files
 java -cp bin Client //cp says where to find the compiled Client.class file
