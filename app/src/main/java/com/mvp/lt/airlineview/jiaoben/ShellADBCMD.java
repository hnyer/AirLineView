package com.mvp.lt.airlineview.jiaoben;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/11/8/008
 */


public class ShellADBCMD {


    /**
     * 执行shell命令
     *
     * @param cmd
     */
    private static void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
