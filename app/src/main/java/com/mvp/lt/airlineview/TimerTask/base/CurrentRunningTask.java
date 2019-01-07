package com.mvp.lt.airlineview.TimerTask.base;


import com.mvp.lt.airlineview.TimerTask.interfaces.ITask;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2019/1/5/005
 */


public class CurrentRunningTask {
    private static ITask sCurrentShowingTask;

    public static void setCurrentShowingTask(ITask task) {
        sCurrentShowingTask = task;
    }

    public static void removeCurrentShowingTask() {
        sCurrentShowingTask = null;
    }

    public static ITask getCurrentShowingTask() {
        return sCurrentShowingTask;
    }
    public static boolean getCurrentShowingStatus() {
        return sCurrentShowingTask != null && sCurrentShowingTask.getStatus();
    }

}
