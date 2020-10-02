import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainAux {

    // The function gets a password (phone number), and calculates its MD5 hash
    /*public static String calculateMD5(String password)
        throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes()); // to add ASCII or UTF-8?
            byte[] digest = md.digest();
            String MD5Hash = DatatypeConverter.printHexBinary(digest).toUpperCase();
            return MD5Hash;
    }*/

    // The function go over all possible phone numbers,
    // calculates their MD5 hash, and check which is equal to the given hash
    public String guessPassword() {
        String phone_number = "05";


        return "";
    }
}
