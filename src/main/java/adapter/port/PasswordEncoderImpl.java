package adapter.port;

import config.MyProperties;
import usecase.port.PasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static java.util.Objects.isNull;

public class PasswordEncoderImpl implements PasswordEncoder {
    private static PasswordEncoderImpl instance = null;


    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 192; // bits


    private PasswordEncoderImpl() {

    }

    public static PasswordEncoderImpl getEncoder() {
        if (instance == null)
            instance = new PasswordEncoderImpl();

        return instance;
    }

    @Override
    public String encrypt(String password, String salt) {
        if (isNull(salt)) {
            SecureRandom random = new SecureRandom();
            byte[] saltByte = new byte[MyProperties.SALT_BYTES_COUNT];
            random.nextBytes(saltByte);

            byte[] encoded = Base64.getEncoder().encode(saltByte);
            salt = new String(encoded, StandardCharsets.UTF_8);
        }

        return getPasswordHash(password, salt);
    }

    private String getPasswordHash(String password, String salt) {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(
                passwordChars,
                saltBytes,
                ITERATIONS,
                KEY_LENGTH
        );
        SecretKeyFactory key = null;
        try {
            key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] hashedPassword = new byte[0];

        try {
            hashedPassword = key.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.format("%x", new BigInteger(hashedPassword)) + salt;
    }

    @Override
    public String getToken(String strToken) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(strToken.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        StringBuilder md5Hex = new StringBuilder(bigInt.toString(16));

        while( md5Hex.length() < 32 ){
            md5Hex.insert(0, "0");
        }

        return md5Hex.toString();
    }

    public String getSHA256(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashByte = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(hashByte);
    }

    public static int getITERATIONS() {
        return ITERATIONS;
    }

    public static int getKeyLength() {
        return KEY_LENGTH;
    }
}
