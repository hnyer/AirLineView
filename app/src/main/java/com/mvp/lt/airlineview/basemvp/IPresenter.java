package com.mvp.lt.airlineview.basemvp;

/**
 * $activityName
 *
 * @author ${LiuTao}
 * @date 2018/5/25/025
 */

public interface IPresenter<V extends IView> {
    /**
     * @param view 绑定
     */
    void attachView(V view);


    /**
     * 防止内存的泄漏,清楚presenter与activity之间的绑定
     */
    void detachView();


    /**
     *
     * @return 获取View
     */
    IView getIView();
}
