/* My implementation is based on Client-Server Programming.
   This class represents the Server */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Master {

    static final int portNumber = 8080; // Master is listening on this port
    static String hashPassword; // the current hash that the minions are trying to crack
    static int range; // used to determine a range of potential passwords
    static boolean foundPassword = false; // true if the current hash was cracked, false otherwise
    //static Vector<MinionHandler> minions = new Vector<>();
    static Vector<String> hashPasswords = new Vector<>(); // store all the hashes from the input file
    static int indexHash = 0; // the index of the current hash
    static boolean toBreak = false; // true if all the passwords were cracked, false otherwise

    public static void main(String []args) throws IOException {
        System.out.println("********* Master *********");
        String line; BufferedReader buffered = null; PrintStream printer = null;
        int minionID = 0; // counter for Minions

        ServerSocket serverSocket = new ServerSocket(portNumber);

        /* assumption: the name of the input file is given in the command line,
           and the file is located in the same directory as the program */
        FileReader file = new FileReader(args[0]);
        try (BufferedReader br = new BufferedReader(file)) {
            while ((line = br.readLine()) != null) {
                // read a hash from the file and add it to the hashes vector
                //System.out.println("Hash password to the vector: " + line);
                hashPasswords.add(line);
            }
        }
        hashPassword = hashPasswords.elementAt(0);
        range = -1;

        while(!toBreak) {
            Socket clientSocket = serverSocket.accept(); // accept the incoming request
            buffered = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // for reading
            printer = new PrintStream(clientSocket.getOutputStream()); // for writing
            MinionHandler minion = new MinionHandler(clientSocket, minionID++, buffered, printer); // create a new minion
            Thread thread = new Thread(minion);
            //minions.add(minion); // add the new minion to the minions vector
            thread.start();
        }
        System.out.println("All passwords were cracked!");
        closeMaster(serverSocket, buffered, printer); // to check that these are not pointers
    }

    public static void closeMaster(ServerSocket serverSocket, BufferedReader buffered, PrintStream printer) throws IOException {
        if(buffered != null)
            buffered.close();
        if(printer != null)
            printer.close();
        serverSocket.close();
    }


    // A class for handling multiple Minions
    public static class MinionHandler implements Runnable {
        Socket clientSocket;
        int clientID;
        BufferedReader buffered;
        PrintStream printer;
        String sendInfo;
        String receiveResult;

        // a builder
        MinionHandler(Socket socket, int id, BufferedReader buffered, PrintStream printer) {
            this.clientSocket = socket;
            this.clientID = id;
            this.buffered = buffered;
            this.printer = printer;
        }

        @Override
        public void run() {
            while (!toBreak) {
                range++;
                if(foundPassword) {
                    indexHash++;
                    if(indexHash == hashPasswords.size()) {
                        toBreak = true;
                        break;
                    }
                    hashPassword = hashPasswords.elementAt(indexHash);
                    range = 0;
                    foundPassword = false;
                }
                sendInfo = hashPassword + "\n" + range;
                printer.println(sendInfo);
                System.out.println("sending to Minion " + clientID +" the hashPassword:" + hashPassword + ", trial number" + range);

                try {
                    receiveResult = buffered.readLine();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                if (!receiveResult.equals("fail")) {
                    foundPassword = true;
                    System.out.println("Password " + (indexHash+1) +" was cracked successfully by Minion " + clientID);
                    System.out.println("The original password is: " + receiveResult);
                    System.out.println();
                }
            }
            try {
                this.clientSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}




