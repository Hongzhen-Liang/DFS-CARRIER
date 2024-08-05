package cn.sinscry.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {
    private static String byte2String(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte aByte:bytes){
            String tmp = (Integer.toHexString(aByte&0XFF));
            if(tmp.length()==1){
                sb.append("0").append(tmp);
            }else {
                sb.append(tmp);
            }
        }
        return sb.toString().toUpperCase();
    }
    public static String getMd5(byte[] bytes) throws Exception{
        MessageDigest md5 = MessageDigest.getInstance("md5");
        md5.update(bytes);
        byte[] code = md5.digest();
        return byte2String(code);
    }
    public static String getMd5(String fileName) throws Exception {
        File file = new File(fileName);
        if (file.exists()) {

            MessageDigest md5 = MessageDigest.getInstance("md5");
            InputStream input = new FileInputStream(file);

            int fileLen = (int) file.length();
            byte[] buffer = new byte[fileLen];
            int length;
            while ((length = input.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            byte[] code = md5.digest();
            input.close();
            return byte2String(code);
        }
        return null;
    }
}
