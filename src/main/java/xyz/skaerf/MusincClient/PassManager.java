package xyz.skaerf.MusincClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PassManager {

    /**
     * Hashes the raw password that is provided for transmission to the server. This is the only location where the raw password
     * is used. It does not get stored anywhere at all; it never leaves main memory and nor is it transmitted in raw form to
     * the server at any point.
     * Unfortunately, this system means that one password has one hash, e.g. 'password' will always result in the same hash.
     * It is however not stored in this manner; I am simply unable to confirm that the password is correct if the password
     * is transmitted to the server with a salt already included. This is, of course, unless I had another salt that was
     * stored on the client end, however I would prefer to keep potentially sensitive data on this end to a minimum.
     * Additionally, a salt that was stored in plaintext in a file would not help anything security-wise.
     * @param password the raw password to be hashed
     * @return the hashed password to be transmitted
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] passHash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : passHash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
