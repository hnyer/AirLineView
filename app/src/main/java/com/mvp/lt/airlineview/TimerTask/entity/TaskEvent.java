package com.mvp.lt.airlineview.TimerTask.entity;


import com.mvp.lt.airlineview.TimerTask.interfaces.ITask;

import java.lang.ref.WeakReference;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2019/1/5/005
 */


public class TaskEvent {
    private WeakReference<ITask> mTask;
    int mEventType;

    public ITask getTask() {
        return mTask.get();
    }

    public void setTask(ITask mTask) {
        this.mTask = new WeakReference<>(mTask);
    }

    public int getEventType() {
        return mEventType;
    }

    public void setEventType(int mEventType) {
        this.mEventType = mEventType;
    }

    public static class EventType {
        public static final int DO = 0X00;
        public static final int FINISH = 0X01;
    }
}
