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
    static boolean running = true;

    static Socket clientSocket;
    static BufferedReader buffered;
    static PrintStream printer;
    static String hashPassword;
    static int range;

    public static void main(String []args) throws NoSuchAlgorithmException {
        System.out.println("********* Minion *********");
        String realPassword;

        connectToMaster();

        while(true) { // maybe to add break if the password found by another Minion
            getInfoFromMaster();
            if(!running) {
                System.out.println("All passwords were cracked");
                break;
            }

            realPassword = crackThePassword();

            if (realPassword.equals("")) {
                System.out.println("Password wasn't found");
                printer.println("fail"); // write the result to the Master
            } else {
                System.out.println("Password found!");
                System.out.println("The password is: " + realPassword);
                // write the results to the Master
                printer.println(realPassword);
            }
        }
        //System.out.println("I'm in the exit"); // to delete
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
                //System.out.println("hashPassword is null");
                running = false;
                return;
            }
            System.out.println("hashPassword=" + hashPassword); // to delete
            range = Integer.parseInt(buffered.readLine());
            System.out.println("range=" + range); // to delete
        }
        catch (IOException e) {
            System.out.println("I'm in catch"); // to delete
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
        // maybe to put in a function - phonePrefix
        StringBuilder phonePrefix = new StringBuilder();
        phonePrefix.append("05"); // first two digits in the phone number from the left
        phonePrefix.append(range / 10); // third digit
        phonePrefix.append(range % 10); // fourth digit

        // looking on range of million phone numbers
        for(int i = 0; i <= 999999; i++) { // to change to macros as lower and upper bound
            StringBuilder phone = new StringBuilder();
            phone.append(phonePrefix);
            phoneSuffix = String.valueOf(i);
            while(phoneSuffix.length() != 6) { // to change 6 to macro
                phoneSuffix = "0" + phoneSuffix; // add zeros in the prefix of phoneSuffix
            }
            //System.out.println("range= " + range); // to delete
            phone.append(phoneSuffix);
            phoneNumber = phone.toString();
            // now we have specific phone number is in the shape of: 05X-XXXXXXX
            System.out.println("checking the phone number: " + phoneNumber); // to delete
            hashPhoneNumber = calculateMD5(phoneNumber); // convert the phone number to MD5 hash
            System.out.println("the hash of the phone number : " + hashPhoneNumber); // to delete

            if(hashPhoneNumber.equals(hashPassword)) { // check if the hash we got equals to the given hash
                System.out.println("found a match"); // to delete
                return phoneNumber;
            }
        }
        return "";
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
