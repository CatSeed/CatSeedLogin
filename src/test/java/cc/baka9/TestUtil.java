package cc.baka9;

public class TestUtil {
    public static void ThreadRun(Runnable runnable){
        new Thread(runnable).start();
    }
    public static void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
