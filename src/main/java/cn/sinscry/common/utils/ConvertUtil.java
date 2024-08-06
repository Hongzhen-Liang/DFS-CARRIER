package cn.sinscry.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import static cn.sinscry.common.utils.ConfigUtils.CHUNK_SIZE;

public class ConvertUtil {
    public static byte[] file2Byte(String fileAddress) throws Exception{
        byte[] res=null;
        try(InputStream in = new FileInputStream(fileAddress)){
            res = file2Byte(in,CHUNK_SIZE);
        }
        return res;
    }

    public static byte[] file2Byte(InputStream in, int length) throws Exception{
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[length];
        while((nRead = in.read(data, 0, length))!=-1){
            buffer.write(data,0,nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

}
