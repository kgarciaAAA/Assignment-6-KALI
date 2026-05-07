package utilities;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
    private static final SecureRandom RNG = new SecureRandom();
    // arbitrary at the moment, can change later if too costly
    private static final int ITERATIONS = 120_000;
    private static final int SALT_LEN = 16;
    private static final int HASH_LEN_BITS = 256;

    // overloaded methods for String password
    public static String hashPassword(String password) { return hashPassword(password.toCharArray()); }
    public static boolean verifyPassword(String password, String storedHash) { return verifyPassword(password.toCharArray(), storedHash); }
    
    public static String hashPassword(char[] password) {
        // dictionary attack prevention
        byte[] salt = new byte[SALT_LEN];
        RNG.nextBytes(salt);
        byte[] hash = derive(password, salt);

        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(char[] password, String storedHash) {
        String[] parts = storedHash.split(":", 2);
        
        if (parts.length != 2) 
          return false;

        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);
            byte[] actual = derive(password, salt);
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            System.err.println("Password hashing failed: " + e.getMessage());
            return false;
        }
    }

    private static byte[] derive(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, HASH_LEN_BITS);

        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        } finally {
            spec.clearPassword();
        }
    }
}