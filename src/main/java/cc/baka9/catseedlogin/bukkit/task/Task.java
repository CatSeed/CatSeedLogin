package cc.baka9.catseedlogin.bukkit.task;

import cc.baka9.catseedlogin.bukkit.CatSeedLogin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Task implements Runnable {
    protected Task(){
    }

    private static TaskAutoKick taskAutoKick;
    private static TaskSendLoginMessage taskSendLoginMessage;
    private static final List<BukkitTask> bukkitTaskList = new ArrayList<>();

    public static TaskAutoKick getTaskAutoKick(){
        if (taskAutoKick == null) {
            taskAutoKick = new TaskAutoKick();
        }
        return taskAutoKick;

    }

    public static TaskSendLoginMessage getTaskSendLoginMessage(){
        if (taskSendLoginMessage == null) {
            taskSendLoginMessage = new TaskSendLoginMessage();
        }
        return taskSendLoginMessage;

    }

    private static final CatSeedLogin plugin = CatSeedLogin.instance;

    public static void runAll() {
        runTaskTimer(Task.getTaskSendLoginMessage(), 0, 20 * 5);
        runTaskTimer(Task.getTaskAutoKick(), 0, 20 * 5);
    }

    public static void cancelAll(){
        Iterator<BukkitTask> iterator = bukkitTaskList.iterator();
        while (iterator.hasNext()) {
            iterator.next().cancel();
            iterator.remove();
        }

    }

    public static void runTaskTimer(Runnable runnable, long l, int i){
        bukkitTaskList.add(plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, 0, l));

    }
}
