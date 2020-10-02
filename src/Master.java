/* My implementation is based on Client-Server Programming.
   This class represents the Server */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Master {

    static final int portNumber = 8080; // Master is listening on this port
    static String hashPassword;
    static int range; // used for determine a range of potential passwords
    //static boolean foundPassword = false;
    static Vector<MinionHandler> minions = new Vector<>();
    static Vector<String> hashPasswords = new Vector<>();

    public static void main(String []args) throws IOException {
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
                //hashPassword = line;
                //System.out.println("Hash password to crack: " + hashPassword);
                hashPasswords.add(line);
                //System.out.println("Hash password to crack: " + line);
                //foundPassword = false;
                //range = 0;
            }
            // getting clients requests
            //while (!foundPassword) {
            //int numOfMinions = Integer.parseInt(args[1]);
            while (true) {
            //for(int i = 0; i < numOfMinions; i++) {
                //System.out.println("Waiting for client response...\n");
                Socket clientSocket = serverSocket.accept(); // accept the incoming request
                BufferedReader buffered = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // for reading
                PrintStream printer = new PrintStream(clientSocket.getOutputStream()); // for writing
                MinionHandler minion = new MinionHandler(clientSocket, minionID++, buffered, printer);
                Thread thread = new Thread(minion);
                minions.add(minion);
                thread.start();
                //minion.start();
                //minion.run();
                //System.out.println("foundPassword=" + foundPassword); // to delete
                //}
            }

            //System.out.println("All passwords found! disconnecting...");

        }
    }


    // A class for handling multiple Minions
    public static class MinionHandler implements Runnable {
    //public static class MinionHandler implements Runnable {
        Socket clientSocket;
        int clientID;
        BufferedReader buffered;
        PrintStream printer;
        String sendInfo;
        String receiveResult;
        boolean toBreak = false;
        //private boolean running; // a boolean flag to stop the thread

        // a builder
        MinionHandler(Socket socket, int id, BufferedReader buffered, PrintStream printer) {
            this.clientSocket = socket;
            this.clientID = id;
            this.buffered = buffered;
            this.printer = printer;
            //running = true;
        }

        // overrides the run function in the Thread class
        @Override
        public void run() {
            //int range;
            boolean foundPassword;
            //System.out.println("I'm in run");
            for(String hashPassword : Master.hashPasswords) {
                System.out.println("Hash password to crack: " + hashPassword);
                //Master.range = -1;
                foundPassword = false;
                //while (!foundPassword) {
                    //for (MinionHandler minion : Master.minions) {
                for(int i = clientID; i < 100; i+=Master.hashPasswords.size()) {
                        //Master.range++;
                        try {
                            //System.out.println("Minion " + minion.clientID + " is running");
                            //buffered = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // for reading
                            //printer = new PrintStream(clientSocket.getOutputStream()); // for writing

                            sendInfo = hashPassword + "\n" + i;
                            printer.println(sendInfo);
                            System.out.println("sent: hashPassword=" + hashPassword + ", range=" + range);

                            /*sendInfo = hashPassword + "\n" + (++Master.range);
                            printer.println(sendInfo);
                            System.out.println("sent: hashPassword=" + hashPassword + ", range=" + range);*/

                            receiveResult = buffered.readLine();
                            if (!receiveResult.equals("fail")) {
                                foundPassword = true;
                                System.out.println("The password was cracked successfully");
                                System.out.println("The original password is: " + receiveResult);
                                System.out.println();
                                // To make a function for this:
                                /*clientSocket.close();
                                buffered.close();
                                printer.close();*/
                                //toBreak = true;
                                break;
                                //running = false;
                                //System.out.println("Disconnecting...");
                            }
                            // maybe to add else with informative failure message
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    /*if (toBreak)
                        break;*/
                }
            //}
        }

        // for stopping the thread
        /*public void stop() {
            running = false;
        }*/

    }



    /*public void run() {
        while (!foundPassword) {
            for (MinionHandler minion : Master.minions) {
                try {
                    //System.out.println("Minion " + minion.clientID + " is running");
                    buffered = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // for reading
                    printer = new PrintStream(clientSocket.getOutputStream()); // for writing

                    sendInfo = hashPassword + "\n" + range;
                    printer.println(sendInfo);
                    System.out.println("sent: hashPassword=" + hashPassword + ", range=" + range);
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
                        toBreak = true;
                        break;
                        //running = false;
                        //System.out.println("Disconnecting...");
                    }
                    // maybe to add else with informative failure message
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(toBreak)
                break;
        }
    }

    // for stopping the thread
        public void stop() {
            running = false;
        }

}*/

}
