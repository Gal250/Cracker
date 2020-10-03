/* My implementation is based on Client-Server Programming.
   This class represents a Client worker - Minion.
   Each Minion gets a hash and a range of potential passwords (phone numbers) from the Master.
   It converts the potential passwords into MD5 hashes, and then compares them to the given hash.
   If there is a match, it returns the original password (the phone number).
*/

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Minion {

    static final int portNumber = 8080;
    static final String serverIP = "127.0.0.1"; // local host
    static boolean running = true; // a boolean flag to stop the thread

    // check if suppose to be static or not
    static Socket clientSocket;
    static BufferedReader buffered;
    static PrintStream printer;
    static String hashPassword;
    static int range;

    // constants
    private static final int lowerBound = 0;
    private static final int upperBound = 999999;
    private static final int numOfDigits = 6;


    public static void main(String []args) throws NoSuchAlgorithmException {
        System.out.println("********* Minion *********");
        String realPassword;

        connectToMaster();

        while(true) { // maybe to add break if the password found by another Minion
            getInfoFromMaster();
            if(!running) {
                //System.out.println("All passwords were cracked");
                break;
            }

            realPassword = crackThePassword();

            if (realPassword.equals("")) {
                System.out.println("Password wasn't found\n");
                printer.println("fail"); // write the result to the Master
            }
            else {
                System.out.println("Password found!");
                System.out.println("The password is: " + realPassword + "\n");
                printer.println(realPassword); // write the result to the Master
            }
        }
        disconnectFromMaster();
    }

    // Getting connected to the server (Master)
    public static void connectToMaster() {
        try {
            clientSocket = new Socket(serverIP, portNumber);
            System.out.println("System connection established");

            buffered = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // for reading
            printer = new PrintStream(clientSocket.getOutputStream()); // for writing
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getInfoFromMaster() {
        try {
            hashPassword = buffered.readLine();
            if(hashPassword == null) {
                running = false;
                return;
            }
            System.out.println("received the hashPassword: " + hashPassword + "\n");
            range = Integer.parseInt(buffered.readLine());
        }
        catch (SocketException e) { // handling with master crashing
            running = false;
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
        StringBuilder phonePrefix = createPhonePrefix();

        // looking on a range of phone numbers
        for(int i = lowerBound; i <= upperBound; i++) {
            StringBuilder phone = new StringBuilder();
            phone.append(phonePrefix); // adding the prefix of the phone
            phoneSuffix = createPhoneSuffix(i);
            phone.append(phoneSuffix); // adding the suffix of the phone

            phoneNumber = phone.toString(); // now we have specific phone number is in the shape of: 05XXXXXXXX

            if(i == lowerBound)
                System.out.println("start checking from: " + phoneNumber);

            hashPhoneNumber = calculateMD5(phoneNumber); // convert the phone number to MD5 hash

            if(hashPhoneNumber.equals(hashPassword)) { // check if the hash we got equals to the given hash
                return phoneNumber;
            }
            if(i == upperBound)
                System.out.println("finished checking " + (upperBound+1) + " phone numbers and stopped at: " + phoneNumber);
        }
        return "";
    }

    // create the prefix of a phone number according to the range, in the shape of: 05XX
    public static StringBuilder createPhonePrefix() {
        StringBuilder phonePrefix = new StringBuilder();
        phonePrefix.append("05"); // first two digits of the phone number from the left
        phonePrefix.append(range / 10); // third digit
        phonePrefix.append(range % 10); // fourth digit
        return phonePrefix;
    }

    // create the suffix (6 last digits) of a phone number according to the range
    public static String createPhoneSuffix(int digits) {
        String phoneSuffix = String.valueOf(digits);
        while(phoneSuffix.length() != numOfDigits) {
            phoneSuffix = "0" + phoneSuffix; // add zeros in the beginning of phoneSuffix
        }
        return phoneSuffix;
    }

    public static String calculateMD5(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String MD5Hash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        return MD5Hash;
    }

    // Getting disconnected from the server (Master)
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
