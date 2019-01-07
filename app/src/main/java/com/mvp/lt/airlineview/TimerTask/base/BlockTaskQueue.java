package com.mvp.lt.airlineview.TimerTask.base;

import android.util.Log;

import com.mvp.lt.airlineview.TimerTask.interfaces.ITask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2019/1/5/005
 */


public class BlockTaskQueue {
    private String TAG = "BlockTaskQueue";
    private AtomicInteger mAtomicInteger = new AtomicInteger();
    //阻塞队列
    private final BlockingQueue<ITask> mTaskQueue = new PriorityBlockingQueue<>();

    private BlockTaskQueue() {
    }
    //单例模式
    private static class BlockTaskQueueHolder {
        private final static BlockTaskQueue INSTANCE = new BlockTaskQueue();
    }

    public static BlockTaskQueue getInstance() {
        return BlockTaskQueueHolder.INSTANCE;
    }

    /**
     * 插入时 因为每一个Task都实现了comparable接口 所以队列会按照Task复写的compare()方法定义的优先级次序进行插入
     * 当优先级相同时，使用AtomicInteger原子类自增 来为每一个task 设置sequence，
     * sequence的作用是标记两个相同优先级的任务入队的次序
     */
    public <T extends ITask> int add(T task) {
        if (!mTaskQueue.contains(task)) {
            task.setSequence(mAtomicInteger.incrementAndGet());
            mTaskQueue.add(task);
        }
        return mTaskQueue.size();
    }

    public <T extends ITask> void remove(T task) {
        if (mTaskQueue.contains(task)) {
            Log.d(TAG, "\n" + "task has been finished. remove it from task queue");
            mTaskQueue.remove(task);
        }
        if (mTaskQueue.size() == 0) {
            mAtomicInteger.set(0);
        }
    }

    public ITask poll() {
        return mTaskQueue.poll();
    }

    public ITask take() throws InterruptedException {
        return mTaskQueue.take();
    }

    public void clear() {
        mTaskQueue.clear();
    }

    public int size() {
        return mTaskQueue.size();
    }

}
