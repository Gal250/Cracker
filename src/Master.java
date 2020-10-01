/* My implementation is based on Client-Server Programming.
   This class represents the Server */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Master {

    static final int portNumber = 8080;
    static String hashPassword;
    static int range = 0; // used for determine a range of potential passwords


    public static void main(String args[]) throws IOException {
        System.out.println("********* Master *********");
        String line;
        /* here needs to get the hashes from the file.
           Each Minion will get one hash.
         */

        /* assumption: the name of the input file is given in the command line,
           and the file is located in the same directory as the program */
        FileReader file = new FileReader(args[0]);
        try (BufferedReader br = new BufferedReader(file)) {
            while ((line = br.readLine()) != null) {
                // read one hash from the file and try to crack it
                hashPassword = line;
                System.out.println("Hash password to crack: " + hashPassword);

                ServerSocket serverSocket = new ServerSocket(portNumber);
                System.out.println("Waiting for client response...\n");

                while(true) {
                    Socket clientSocket =serverSocket.accept(); // ready to get communication on the socket
                    MinionHandler minion = new MinionHandler(clientSocket);
                    minion.start();

                }
            }
        }


    }


    // A class for handling multiple Minions
    public static class MinionHandler extends Thread {
        Socket clientSocket;
        //int clientID;
        InputStream stream;
        InputStreamReader reader;
        BufferedReader buffered;
        OutputStream out;
        PrintStream printer;
        String sendInfo;
        String recieveResult;

        // a builder
        MinionHandler(Socket socket) {
            clientSocket = socket;
            //clientID = id;
        }

        // overrides the run function in the Thread class
        public void run() {
            try {
                // starts accepting requests
                stream = clientSocket.getInputStream(); // create an input stream from the socket
                reader = new InputStreamReader(stream);
                buffered = new BufferedReader(reader);
                // now we can start reading requests // to delete note

                // for writing
                out = clientSocket.getOutputStream();
                printer = new PrintStream(out);

                sendInfo = range + "\n" + hashPassword;
                printer.println(sendInfo);
                range++;

                recieveResult = buffered.readLine();
                if(!recieveResult.equals("")) {
                    System.out.println("The password was cracked successfully");
                    System.out.println("The original password is: " + recieveResult);
                    // To make a function for this:
                    clientSocket.close();
                    buffered.close();
                    printer.close();
                    //System.out.println("Disconnecting...");
                }
                // maybe to add else with informative failure message


            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}
