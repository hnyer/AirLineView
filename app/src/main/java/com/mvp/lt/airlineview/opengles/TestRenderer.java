package com.mvp.lt.airlineview.opengles;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/12/26/026
 */


public class TestRenderer extends AbstractPRenderer {
    //    float x = -0.5f, y = -0.5f, z = -0.5f;
    float x = 1f, y = 1.5f, z = 1f;
    float xx = 1f, yy = 1f, zz = 1f;
    private float r = 0;
    private float rotateValue = 0;
    Handler handler, handler2;
    private Timer timer = new Timer();
    private TimerTask task;

    // 定义Open GL ES绘制所需要的Buffer对象
    FloatBuffer lineVerticesBuffer;
    FloatBuffer xyzVerticesBuffer;
    ByteBuffer lineFacetsBuffer;
    ByteBuffer xiangliangFacetsBuffer;
    ByteBuffer XFacetsBuffer;
    ByteBuffer YFacetsBuffer;
    ByteBuffer ZFacetsBuffer;
    private float[] mLineVertices;
    private GL10 mGl;
    private float mY = -1f;

    //    // 创建GLSurfaceView的内容绘制器
//    Jiasudu myRender = new Jiasudu(drawlineHandler);
//    // 为GLSurfaceView设置绘制器
//    glView.setRenderer(myRender);
    void updateXYZ() {
        // 定义立方体的8个顶点                                                       
        // 上顶面正方形的四个顶点
//0
//1
//2
//3
// 下底面正方形的四个顶点
//4
//5
//6原点
//7 
        mLineVertices = new float[]{
                // 上顶面正方形的四个顶点
                x, y, z,//0
                x, 0, z,//1
                0, 0, z,//2
                0, y, z,//3

                // 下底面正方形的四个顶点
                x, y, 0,//4
                x, 0, 0,//5
                0, 0, 0,//6原点
                0, y, 0,//7 

        };
        //定义XYZ坐标和显示的字
        float xyzVertices[] = new float[]{
                -1.5f, 0f, 0f,//0 x起点，画坐标轴的

                1.2f, 0f, 0f,//1 X轴的终点
                1.0f, 0.1f, 0f,//2 X轴箭头1
                1.0f, -0.1f, 0f,//3 X轴箭头2

                0f, -2.5f, 0f,//4 Y轴起点
                0f, 2.5f, 0f,//5 Y轴终点
                0.1f, 2.3f, 0f,//6 Y轴箭头1
                -0.1f, 2.3f, 0f,//7 Y轴箭头2

                0f, 0f, -1.2f,//8 Z轴起点
                0f, 0f, 1.2f,//9 Z轴终点
                0f, 0.1f, 1.0f,//10 Z轴箭头1
                0f, -0.1f, 1.0f,//11 Z轴箭头2

                1.3f, 0f, 0f,//12 绘制字X
                1.35f, 0.1f, 0f,//13
                1.25f, 0.1f, 0f,//14
                1.25f, -0.1f, 0f,//15
                1.35f, -0.1f, 0f,//16

                0f, 2.7f, 0f,//17 绘制字Y
                0f, 2.6f, 0f,//18
                0.05f, 2.8f, 0f,//19
                -0.05f, 2.8f, 0f,//20

                -0.05f, 0.05f, 1.25f,//21  绘制字Z
                0.05f, 0.05f, 1.25f,//22
                -0.05f, -0.05f, 1.25f,//23
                0.05f, -0.05f, 1.25f,//24

                //刻度X轴刻度
                0.5f, 0f, 0f,//25 起
                0.5f, 0.1f, 0f,//26 终


                -0.5f, 0f, 0f,//27  起
                -0.5f, 0.1f, 0f,//28 终


                //刻度y轴刻度
                0f, 0.5f, 0f,//29
                -0.1f, 0.5f, 0f,//30

                0f, -0.5f, 0f,//31
                -0.1f, -0.5f, 0f,//32


                //刻度Z轴刻度
                0f, 0f, 0.5f,//33
                0f, 0.1f, 0.5f,//34
                0f, 0f, -0.5f,//35
                0f, 0.1f, -0.5f//36

        };
        //画长方体的12条边     里面表示的是从上面数组中第几个点到第几个点画直线
        byte[] lineFacets = new byte[]{
                0, 1,
                0, 3,
                0, 4,
                1, 2,
                1, 5,
                2, 3,
                2, 6,
                3, 7,
                4, 5,
                4, 7,
                5, 6,
                6, 7
        };
        //向量从原点6指向长方体的0点  
        byte[] xiangliangFacets = new byte[]{
                6, 0//6,0
        };

        //X坐标及其箭头  
        byte[] XFacets = new byte[]{
                //起终点
                0, 1,
                //箭头
                1, 2,
                1, 3,
                //X
                12, 13,
                12, 14,
                12, 15,
                12, 16,
                //X坐标
                25, 26,
                27, 28

        };
        //Y坐标及其箭头  
        byte[] YFacets = new byte[]{
                //起终点
                4, 5,
                //箭头
                5, 6,
                5, 7,
                //字Y
                17, 18,
                17, 19,
                17, 20,
                //Y轴刻度
                29, 30,
                31, 32

        };
        //Z坐标及其箭头  
        byte[] ZFacets = new byte[]{
                //起终点
                8, 9,
                //箭头
                9, 10,
                9, 11,
                //字Z
                21, 22,
                22, 23,
                23, 24,
                //Z轴刻度
                33, 34,
                35, 36
        };
        // 将立方体的顶点位置数据数组包装成FloatBuffer;
        lineVerticesBuffer = floatBufferUtil(mLineVertices);
        xyzVerticesBuffer = floatBufferUtil(xyzVertices);
        // 将直线的数组包装成ByteBuffer
        lineFacetsBuffer = ByteBuffer.wrap(lineFacets);
        xiangliangFacetsBuffer = ByteBuffer.wrap(xiangliangFacets);
        XFacetsBuffer = ByteBuffer.wrap(XFacets);
        YFacetsBuffer = ByteBuffer.wrap(YFacets);
        ZFacetsBuffer = ByteBuffer.wrap(ZFacets);
    }

    //构造函数带了参数，是因为不同类间传递参数，将接受方的handler实例传递过来
    @SuppressLint("HandlerLeak")
    public TestRenderer(Handler handler_zjk) {
        updateXYZ();
        handler2 = handler_zjk;//实际上handler2就是接收方的实例了，巧妙的转换方法实现不同类间handler传递数据
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 200) {
                    // updateXYZ();
                }//这是接收本类中定时器发送过来的信号用来更新正方体

            }

        };
        //定时器任务中发送了两个信号，给本类中发送了一个，给activity类中发了一个
        task = new TimerTask() {


            public void run() {
                String[] xyz = new String[3];//发送给activity用的
                //2s生成xyz的随机数
                x = (float) (Math.random() * (-2) + 1);
                y = (float) (Math.random() * (-2) + 1);
                z = (float) (Math.random() * (-2) + 1);
                //设定一下要显示的XYZ位数,不管正负都显示小数点后两位
                if (x > 0)
                    xyz[0] = String.valueOf(x).substring(0, 4);
                else
                    xyz[0] = String.valueOf(x).substring(0, 5);
                if (y > 0)
                    xyz[1] = String.valueOf(y).substring(0, 4);
                else
                    xyz[1] = String.valueOf(y).substring(0, 5);
                if (z > 0)
                    xyz[2] = String.valueOf(z).substring(0, 4);
                else
                    xyz[2] = String.valueOf(z).substring(0, 5);
                Message msg = new Message();
                msg.what = 200;//这是发送给当前类中用来更新立方体的
                handler.sendEmptyMessage(msg.what);

                msg.what = 0x123;//这是发送给activity类中用来更新文本框中XYZ值得
                Bundle bundle = new Bundle();//对数据包装后用MSG发送
                //要发送的是一个字符串数组，第一个参数是指定数据的名，当接收数据时可以用这个名来选择获取哪个数据，很方便，第二个参数是发送的数组
                bundle.putStringArray("xyz", xyz);
                msg.setData(bundle);
                handler2.sendMessage(msg);//handler2指的是接收处的Handler实例，这里因为不是同一类，所以要用构造函数将接受类的handler实例传递过来

            }

        };
        // timer.schedule(task, 0, 5000);
    }

    //2.3 实现接口里的三个方法

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 关闭抗抖动
        gl.glDisable(GL10.GL_DITHER);

        // 设置系统对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glClearColor(0, 0, 0, 0);
        // 设置阴影平滑模式
        gl.glShadeModel(GL10.GL_SMOOTH);
        // 启用深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // 设置深度测试的类型
        gl.glDepthFunc(GL10.GL_LEQUAL);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置3D视窗的大小及位置
        gl.glViewport(0, 0, width, height);
        // 将当前矩阵模式设为投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // 初始化单位矩阵
        gl.glLoadIdentity();
        // 计算透视视窗的宽度、高度比
        float ratio = (float) width / height;
        // 调用此方法设置透视视窗的空间大小。
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
    }


    // 绘制图形的方法
    @Override
    public void onDrawFrame(GL10 gl) {
        mGl = gl;
        // 清除屏幕缓存和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // 启用顶点座标数据
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);  //2.3.3.1
        // 启用顶点颜色数据
        //gl.glEnableClientState(GL10.GL_COLOR_ARRAY);                   //2.3.3.2
        // 设置当前矩阵模式为模型视图。
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        // --------------------绘制正方体---------------------
        // 重置当前的模型视图矩阵
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, -0.0f, -3.0f);//移动中心
        // 沿着Y轴旋转

        gl.glRotatef(r, 0f, mY, 0.0f);

        gl.glScalef(xx, yy, zz);
        // r++;
        // 沿着X轴旋转
        //gl.glRotatef(0f, 0.1f, 0f, 0f);
        gl.glLineWidth(2.0f);
        // 设置顶点的位置数据 因为所有的数据都在次数组中，所以长方体和向量的只要设置这一次就好
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVerticesBuffer); //2.3.3.3
        // 设置顶点的颜色数据
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f); // 2.3.3.4
        //gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, 18);//这里不用二维，用三维的画法，注意是GL_LINES三维中画线             
        gl.glDrawElements(GL10.GL_LINES, lineFacetsBuffer.remaining(),//2.3.3.5
                GL10.GL_UNSIGNED_BYTE, lineFacetsBuffer);
        // --------------------绘制向量---------------------
        //绘制向量
        gl.glLineWidth(6.0f);//直线宽度 5倍于其他线
        //无需再设置点了，都是用的上面的数组中的
        // gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVerticesBuffer);//向量
        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);//向量
        gl.glDrawElements(GL10.GL_LINES, xiangliangFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, xiangliangFacetsBuffer);//向量
        // --------------------绘制X坐标---------------------
        //绘制x坐标
        gl.glLineWidth(3.0f);//直线宽度
        //设置XYZ的顶点 因为所有XYZ的数据都在次数组中，所以XYZ的只要设置这一次就好
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, xyzVerticesBuffer);
        // 设置顶点的颜色数据
        gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);//X
        gl.glDrawElements(GL10.GL_LINES, XFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, XFacetsBuffer);//X
        // --------------------绘制Y坐标---------------------
        //绘制Y坐标
        //无需再设置点了，都是用的上面的数组中的
        // gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVerticesBufferY);//Y
        // 设置顶点的颜色数据
        gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);//Y
        gl.glDrawElements(GL10.GL_LINES, YFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, YFacetsBuffer);//Y
        // --------------------绘制Z坐标---------------------
        //绘制Z坐标
        //无需再设置点了，都是用的上面的数组中的
        // gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVerticesBufferZ);//Y
        // 设置顶点的颜色数据
        gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);//z
        gl.glDrawElements(GL10.GL_LINES, ZFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, ZFacetsBuffer);//Z
        // 绘制结束
        gl.glFinish();//2.3.3.6
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // 旋转角度增加1
        //rotate+=1;
    }

    // 定义一个工具方法，将float[]数组转换为OpenGL ES所需的FloatBuffer
    private FloatBuffer floatBufferUtil(float[] arr) {
        FloatBuffer mBuffer;
        // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个int占4个字节
        ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
        // 数组排列用nativeOrder
        qbb.order(ByteOrder.nativeOrder());
        mBuffer = qbb.asFloatBuffer();
        mBuffer.put(arr);
        mBuffer.position(0);
        return mBuffer;
    }

    //缩放变换
    public void scale(float x, float y, float z) {
        xx = xx * x;
        yy = yy * y;
        zz = zz * z;
        // onDrawFrame(mGl);
        // scaleM(mLineVertices,0,x,y,z);
    }

    public void glRotate(float rv, float d) {
        r = r + 1;
        Log.e("glRotate", r + "");
        mY = d;
//        onDrawFrame(mGl);
    }
}
