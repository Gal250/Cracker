import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class main {

    // to delete
    public static String calculateMD5(String password)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes()); // to add ASCII or UTF-8?
        byte[] digest = md.digest();
        String MD5Hash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        return MD5Hash;
    }

    // to delete
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String phoneNumber = "0501234567";
        String hash = calculateMD5(phoneNumber);
        System.out.println("hash: " + hash);

        /*String hash = "35454B055CC325EA1AF2126E27707052";
        String password = "ILoveJava";
        //String password = "054-7597873";
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] barr=password.getBytes();
        for(int i=0;i<barr.length;i++) {
            System.out.println(barr[i]);
        }
        md.update(password.getBytes()); // to add ASCII or UTF-8?
        byte[] digest = md.digest();
        String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        System.out.println(myHash);
        System.out.println(myHash.equals(hash));*/
        /*StringBuilder phoneNumber = new StringBuilder();
        phoneNumber.append("05");
        phoneNumber.append(40/10);
        phoneNumber.append("-");
        String password = phoneNumber.toString();
        System.out.println(password);*/
    }

}
