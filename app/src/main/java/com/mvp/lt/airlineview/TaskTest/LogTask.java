package com.mvp.lt.airlineview.TaskTest;

import android.util.Log;

import com.mvp.lt.airlineview.TimerTask.base.BaseTask;


/**
 * $activityName
 *
 * @author LiuTao
 * @date 2019/1/5/005
 */


public class LogTask extends BaseTask {
    String name;
    private TaskTestActivity mTestActivity;

    public LogTask(TaskTestActivity activity, String name) {
        this.mTestActivity = activity;
        this.name = name;
    }

    //执行任务方法，在这里实现你的任务具体内容
    @Override
    public void doTask() {
        super.doTask();
        Log.e("LogTask", "--doTask-" + name);
        mTestActivity.updateShowText("----doTask----" + name);
        //如果这个Task的执行时间是不确定的，比如上传图片，那么在上传成功后需要手动调用
        //unLockBlock方法解除阻塞，例如：

    }

    //任务执行完的回调，在这里你可以做些释放资源或者埋点之类的操作
    @Override
    public void finishTask() {
        super.finishTask();
        Log.i("LogTask", "----finishTask----" + name);
        mTestActivity.updateShowText("----finishTask----" + name);
    }

}
