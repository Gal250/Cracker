import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class main {

    // to delete
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String hash = "35454B055CC325EA1AF2126E27707052";
        String password = "ILoveJava";
        //String password = "054-7597873";
        MessageDigest md = MessageDigest.getInstance("MD5");
        /*byte[] barr=password.getBytes();
        for(int i=0;i<barr.length;i++) {
            System.out.println(barr[i]);
        }*/
        md.update(password.getBytes()); // to add ASCII or UTF-8?
        byte[] digest = md.digest();
        String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        System.out.println(myHash);
        System.out.println(myHash.equals(hash));
    }
}
