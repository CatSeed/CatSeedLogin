package cc.baka9.catseedlogin.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Util {
    private static Pattern passwordDifficultyRegex = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$");

    public static boolean passwordIsDifficulty(String pwd){
        return passwordDifficultyRegex.matcher(pwd).find();
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String time2Str(long time){
        return sdf.format(new Date(time));
    }

}
