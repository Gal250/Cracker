/* My implementation is based on Client-Server Programming.
   This class represents a Client worker - Minion.
   Each Minion gets a hash and a range of potential passwords (phone numbers) from the Master.
   It converts the potential passwords into MD5 hashes, and then compares them to the given hash.
   If there is a match, it returns the original password (the phone number).
*/

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Minion {

    static final int portNumber = 8080;
    static final String serverIP = "127.0.0.1"; // local host

    static Socket clientSocket;
    static InputStream stream;
    static InputStreamReader reader;
    static BufferedReader buffered;
    static OutputStream out;
    static PrintStream printer;
    static String hashPassword;
    static int range;

    public static void main(String args[]) {
        System.out.println("********* Minion *********");

        connectToMaster();
        // maybe need to add a while loop
        getRangeFromMaster();

    }

    // Getting connected to the server (Master)
    public static void connectToMaster() {
        try {
            clientSocket = new Socket(serverIP, portNumber);
            System.out.println("System connection established\n");

            // for reading
            stream = clientSocket.getInputStream();
            reader = new InputStreamReader(stream);
            buffered = new BufferedReader(reader);

            // for writing
            out = clientSocket.getOutputStream();
            printer = new PrintStream(out);
        }
        /*catch (UnknownHostException e) {
            e.printStackTrace();
        }*/
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getRangeFromMaster() {
        //String rangeString;
        try {
            hashPassword = buffered.readLine();
            range = Integer.parseInt(buffered.readLine());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
