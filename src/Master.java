/* My implementation is based on Client-Server Programming.
   This class represents the Server */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Master {

    static final int portNumber = 8080;
    static String hashPassword;

    public static void main(String args[]) throws IOException {
        System.out.println("********* Master *********");

        /* here needs to get the hashes from the file and store them in an efficiency data structure
           or maybe each Minion will get one hash, so the number of Minions will be the number of hashes
           multiply the number of ranges
         */

        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Waiting for client response...\n");

        while(true) {
            Socket clientSocket =serverSocket.accept(); // ready to get communication on the socket

            /* to complete */
        }
    }


    // A class for handling multiple Minions
    public static class MinionHandler extends Thread {
        Socket clientSocket;
        int clientID;

        // a builder
        MinionHandler(Socket socket, int id) {
            clientSocket = socket;
            clientID = id;
        }

        // overrides the run function in the Thread class
        public void run() {
            try {
                // starts accepting requests
                InputStream stream = clientSocket.getInputStream(); // create an input stream from the socket
                InputStreamReader reader = new InputStreamReader(stream);
                BufferedReader buffered = new BufferedReader(reader);
                // now we can start reading requests

                /* to complete */

            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
