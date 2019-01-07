package com.mvp.lt.airlineview.opengles;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/12/28/028
 */


public abstract class AbstractPRenderer implements GLSurfaceView.Renderer {
    public  float ratio;
    public float xrotate = 0f;//围绕x轴旋转角度
    public float yrotate = 0f;//围绕x轴旋转角度
    public float XScalef = 1f;//缩放大小
    public float YScalef = 1f;//缩放大小
    public float ZScalef = 1f;//缩放大小

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置背景颜色
        gl.glClearColor(0,0,0,1);
        //启用顶点缓冲区
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        onSurfaceCreateds( gl, config);
    }

    public abstract void onSurfaceCreateds(GL10 gl, EGLConfig config);

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口
        gl.glViewport(0, 0, width, height);
        ratio = (float)width / (float)height;
        //投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        //加载单位矩阵
        gl.glLoadIdentity();
        //设置平截头体
        gl.glFrustumf(-ratio, ratio, -1, 1, 3f, 7f);
        onSurfaceChangeds(gl,width,height);
    }

    public abstract void onSurfaceChangeds(GL10 gl, int width, int height);

    @Override
    public abstract void onDrawFrame(GL10 gl) ;
}
