package cc.baka9.catseedlogin.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

public class Util {
    private static final Pattern passwordDifficultyRegex = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,16}$");

    public static boolean passwordIsDifficulty(String pwd) {
        return !passwordDifficultyRegex.matcher(pwd).matches();
    }

    public static String time2Str(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(time));
    }

    public static boolean checkMail(String e_mail) {
        return e_mail.matches("[a-zA-Z0-9_]+@[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)+");
    }

    public static String randomStr() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + random.nextInt(rightLimit - leftLimit + 1);
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public static boolean isOSLinux() {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        return os != null && os.toLowerCase().contains("linux");
    }
}