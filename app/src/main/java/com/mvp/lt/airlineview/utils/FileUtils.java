package com.mvp.lt.airlineview.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/8/6/006
 */


public class FileUtils {
    public static String readtext(String pathfile) {
        File f = new File(pathfile);
        if (!f.exists()) {
            return null;
        }
        FileInputStream is;
        String result = null;
        try {
            is = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] array = new byte[1024];
            int len = -1;
            while ((len = is.read(array)) > 0 - 1) {
                bos.write(array, 0, len);
            }
            byte[] data = bos.toByteArray(); // 取内存中保存的数据
            result = new String(data, "ASCII");
            bos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
