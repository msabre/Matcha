package usecase.port;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface PasswordEncoder {
    String encrypt(String password, String salt);

    String getToken(String str);

    String getSHA256(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException;
}
