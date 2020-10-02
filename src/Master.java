/* My implementation is based on Client-Server Programming.
   This class represents the Server */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Master {

    static final int portNumber = 8080; // Master is listening on this port
    static String hashPassword;
    static int range = 0; // used for determine a range of potential passwords


    public static void main(String args[]) throws IOException {
        System.out.println("********* Master *********");
        String line;
        int minionID = 0; // counter for Minions

        ServerSocket serverSocket = new ServerSocket(portNumber);

        /* assumption: the name of the input file is given in the command line,
           and the file is located in the same directory as the program */
        FileReader file = new FileReader(args[0]);
        try (BufferedReader br = new BufferedReader(file)) {
            while ((line = br.readLine()) != null) {
                // read one hash from the file and try to crack it
                hashPassword = line;
                System.out.println("Hash password to crack: " + hashPassword);

                // getting clients requests
                while(true) {
                    //System.out.println("Waiting for client response...\n");
                    Socket clientSocket =serverSocket.accept(); // accept the incoming request
                    MinionHandler minion = new MinionHandler(clientSocket, minionID++);
                    minion.start();

                }
            }
        }

    }


    // A class for handling multiple Minions
    public static class MinionHandler extends Thread {
        Socket clientSocket;
        int clientID;
        BufferedReader buffered;
        PrintStream printer;
        String sendInfo;
        String receiveResult;

        // a builder
        MinionHandler(Socket socket, int id) {
            clientSocket = socket;
            clientID = id;
        }

        // overrides the run function in the Thread class
        public void run() {
            try {
                System.out.println("Minion " + clientID + " is running");
                buffered = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // for reading
                printer = new PrintStream(clientSocket.getOutputStream()); // for writing

                sendInfo = range + "\n" + hashPassword;
                printer.println(sendInfo);
                range++;

                receiveResult = buffered.readLine();
                if(!receiveResult.equals("")) {
                    System.out.println("The password was cracked successfully");
                    System.out.println("The original password is: " + receiveResult);
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
