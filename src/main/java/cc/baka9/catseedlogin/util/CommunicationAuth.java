package cc.baka9.catseedlogin.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommunicationAuth {

    public static String encryption(String... args) {
        String paramString = String.join("", args);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(paramString.getBytes());
            byte[] arrayOfByte = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte value : arrayOfByte) {
                byte b = value;
                if (b < 0) b += 256;
                if (b < 16) stringBuilder.append("0");
                stringBuilder.append(Integer.toHexString(b));
            }
            String str = stringBuilder.toString();
            return str.toLowerCase();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            return null;
        }
    }

}
