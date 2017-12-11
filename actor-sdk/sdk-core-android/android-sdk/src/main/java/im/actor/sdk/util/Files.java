package im.actor.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;

import im.actor.runtime.android.AndroidContext;

public class Files {

    public static String getExternalTempFile(String prefix, String postfix) {
        File externalFile = AndroidContext.getContext().getExternalFilesDir(null);
        if (externalFile == null) {
            return null;
        }
        String externalPath = externalFile.getAbsolutePath();

        File dest = new File(externalPath + "/actor/tmp/");
        dest.mkdirs();

        File outputFile = new File(dest, prefix + "_" + Randoms.randomId() + "" + postfix);

        return outputFile.getAbsolutePath();
    }

    public static String getInternalTempFile(String prefix, String postfix) {
        String externalPath;
        File externalFile = AndroidContext.getContext().getFilesDir();
        if (externalFile == null) {
            externalPath = "data/data/".concat(AndroidContext.getContext().getPackageName()).concat("/files");
        } else {
            externalPath = externalFile.getAbsolutePath();
        }

        File dest = new File(externalPath + "/actor/tmp/");
        dest.mkdirs();
        if (!dest.exists()) return null;

        File outputFile = new File(dest, prefix + "_" + Randoms.randomId() + "" + postfix);
        return outputFile.getAbsolutePath();
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


    // 把登录信息通过base64编码存入xml文件中
    public static void saveLoginInfo(Object bean, Context con, String cacheName) {
        SharedPreferences preferences = con.getSharedPreferences("iGemCache",
                con.MODE_PRIVATE);
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(bean);
            // 将字节流编码成base64的字符窜
            String oAuth_Base64 = new String(Base64.encodeToString(
                    baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(cacheName, oAuth_Base64);
            editor.commit();
        } catch (IOException e) {
            // TODO Auto-generated
            e.printStackTrace();
        }
        Log.i("ok", "存储成功");
    }

    // 把登录信息进行解码
    public static Object readLoginInfo(Context con, String cacheName) {
        Object bean = null;
        SharedPreferences preferences = con.getSharedPreferences("iGemCache",
                con.MODE_PRIVATE);
        String productBase64 = preferences.getString(cacheName, "");

        // 读取字节
        byte[] base64 = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);

        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            // 再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            try {
                // 读取对象
                bean = bis.readObject();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                System.out.println("---没有读取对象--");
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("---没有读取对象--");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("---没有读取对象--");
        }
        return bean;
    }
}
