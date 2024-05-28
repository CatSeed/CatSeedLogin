package cc.baka9.catseedlogin.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypt {

    private static final char[] CRYPTCHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String encrypt(final String name, final String password) {
        final String text = "ÜÄaeut//&/=I " + password + "7421€547" + name + "__+IÄIH§%NK " + password;
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(text.getBytes(StandardCharsets.UTF_8), 0, text.getBytes(StandardCharsets.UTF_8).length);
            return byteArrayToHexString(md.digest());
        } catch (final NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String byteArrayToHexString(byte... args) {
        char[] hexChars = new char[args.length * 2];
        for (int i = 0; i < args.length; i++) {
            hexChars[(i * 2)] = HEX_DIGITS[(args[i] >> 4 & 0xF)];
            hexChars[(i * 2 + 1)] = HEX_DIGITS[(args[i] & 0xF)];
        }
        return new String(hexChars);
    }

    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    public boolean match(final String name, final String password, final String encrypted){
        try {
            return encrypted.equals(encrypt(name, password));
        } catch (final Exception e) {
            return false;
        }
    }

}
