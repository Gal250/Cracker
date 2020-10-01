/* My implementation is based on Client-Server Programming.
   This class represents a Client worker - Minion.
   Each Minion gets a hash and a range of potential passwords (phone numbers) from the Master.
   It converts the potential passwords into MD5 hashes, and then compares them to the given hash.
   If there is a match, it returns the original password (the phone number).
*/

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Minion {

    static final int portNumber = 8080;
    static final String serverIP = "127.0.0.1"; // local host

    static Socket clientSocket;
    //static int MinionID = 0; // to check that it changes from one Minion to another
    static InputStream stream;
    static InputStreamReader reader;
    static BufferedReader buffered;
    static OutputStream out;
    static PrintStream printer;
    static String hashPassword;
    static int range;

    public static void main(String args[]) throws NoSuchAlgorithmException {
        System.out.println("********* Minion *********");
        String realPassword;

        connectToMaster();
        //MinionID++; // the unique ID of the current Minion

        while(true) { // maybe to add break if the password found by another Minion
            getInfoFromMaster();
            realPassword = crackThePassword();

            if (realPassword.equals("")) {
                System.out.println("Password wasn't found");
                printer.println("fail"); // write the result to the Master
            } else {
                System.out.println("Password found!");
                System.out.println("The password is " + realPassword);
                // write the results to the Master
                printer.println("success"); // to check if is suppose to be println or write
                printer.println(realPassword);
                break;
            }
        }

        disconnectFromMaster();
    }

    // Getting connected to the server (Master)
    public static void connectToMaster() {
        try {
            clientSocket = new Socket(serverIP, portNumber);
            System.out.println("System connection established");

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

    public static void getInfoFromMaster() {
        //String rangeString;
        try {
            hashPassword = buffered.readLine();
            range = Integer.parseInt(buffered.readLine());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* the function goes over million of phone numbers according to the range of this Minion.
       for each phone number calculates its MD5 hash, and compare the result to the given hash.
       if there is a match -> the function returns the phone number that had the match.
       otherwise -> returns empty string.
     */
    public static String crackThePassword() throws NoSuchAlgorithmException {
        String phoneSuffix, phoneNumber, hashPhoneNumber;
        // maybe to put in a function
        StringBuilder phonePrefix = new StringBuilder();
        StringBuilder phone = new StringBuilder();
        phonePrefix.append("05"); // first two digits in the phone number from the left
        //phonePrefix.append(MinionID / 10);
        phonePrefix.append(range / 10); // third digit
        //phonePrefix.append("-");
        //phonePrefix.append(MinionID % 10);
        phonePrefix.append(range % 10); // fourth digit

        // looking on range of million phone numbers
        for(int i = 0; i <= 999999; i++) { // to change to macros as lower and upper bound
            phone.append(phonePrefix); // to check it's doesn't change the phoneNumber
            phoneSuffix = String.valueOf(i);
            while(phoneSuffix.length() != 6) { // to change 6 to macro
                phoneSuffix = "0" + phoneSuffix; // add zeros in the prefix of phoneSuffix
            }
            phone.append(phoneSuffix);
            phoneNumber = phone.toString();
            // now we have specific phone number is in the shape of: 05X-XXXXXXX
            System.out.println("checking the phone number: " + phoneNumber);
            hashPhoneNumber = calculateMD5(phoneNumber); // convert the phone number to MD5 hash
            System.out.println("the hash of the phone number : " + hashPhoneNumber); // to delete

            if(hashPhoneNumber.equals(hashPassword)) // check if the hash we got equals to the given hash
                return phoneNumber;
        }
        return "";
    }

    public static String calculateMD5(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes()); // to add ASCII or UTF-8?
        byte[] digest = md.digest();
        String MD5Hash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        return MD5Hash;
    }

    // Getting connected to the server (Master)
    public static void disconnectFromMaster() {
        try {
            clientSocket.close();
            buffered.close();
            printer.close();
            System.out.println("Disconnecting from Master...");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
