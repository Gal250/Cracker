/* My implementation is based on Client-Server Programming.
   This class represents the Server */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Master {

    static final int portNumber = 8080; // Master is listening on this port
    static String hashPassword;
    static int range; // used for determine a range of potential passwords
    static boolean foundPassword = false;


    public static void main(String []args) throws IOException {
        System.out.println("********* Master *********");
        String line;
        int minionID = 1; // counter for Minions

        ServerSocket serverSocket = new ServerSocket(portNumber);

        /* assumption: the name of the input file is given in the command line,
           and the file is located in the same directory as the program */
        FileReader file = new FileReader(args[0]);
        try (BufferedReader br = new BufferedReader(file)) {
            while ((line = br.readLine()) != null) {
                // read one hash from the file and try to crack it
                hashPassword = line;
                System.out.println("Hash password to crack: " + hashPassword);
                foundPassword = false;
                range = 0;

                // getting clients requests
                while (!foundPassword) {
                    //System.out.println("Waiting for client response...\n");
                    Socket clientSocket = serverSocket.accept(); // accept the incoming request
                    MinionHandler minion = new MinionHandler(clientSocket, minionID++);
                    //Thread thread = new Thread(minion);
                    //thread.start();
                    //minion.start();
                    minion.run();
                    System.out.println("foundPassword=" + foundPassword); // to delete
                }
                //System.out.println("I'm here 1"); // to delete
            }
            //System.out.println("I'm here 2"); // to delete
        }
        System.out.println("All passwords found! disconnecting...");

    }


    // A class for handling multiple Minions
    public static class MinionHandler extends Thread {
    //public static class MinionHandler implements Runnable {
        Socket clientSocket;
        int clientID;
        BufferedReader buffered;
        PrintStream printer;
        String sendInfo;
        String receiveResult;
        //private boolean running; // a boolean flag to stop the thread

        // a builder
        MinionHandler(Socket socket, int id) {
            clientSocket = socket;
            clientID = id;
            //running = true;
        }

        // overrides the run function in the Thread class
        @Override
        public void run() {
            while (!foundPassword) {
                try {
                    System.out.println("Minion " + clientID + " is running");
                    buffered = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // for reading
                    printer = new PrintStream(clientSocket.getOutputStream()); // for writing

                    sendInfo = hashPassword + "\n" + range;
                    printer.println(sendInfo);
                    range++;

                    receiveResult = buffered.readLine();
                    if (!receiveResult.equals("fail")) {
                        System.out.println("The password was cracked successfully");
                        System.out.println("The original password is: " + receiveResult);
                        System.out.println();
                        foundPassword = true;
                        // To make a function for this:
                        clientSocket.close();
                        buffered.close();
                        printer.close();
                        //running = false;
                        //System.out.println("Disconnecting...");
                    }
                    //System.out.println("Disconnecting...");
                    // maybe to add else with informative failure message
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("Disconnecting...");
        }

        // for stopping the thread
        /*public void stop() {
            running = false;
        }*/

    }

}
