package cn.sinscry.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import static cn.sinscry.common.utils.ConfigUtils.CHUNK_SIZE;

public class ConvertUtil {
    public static byte[] file2Byte(String fileAddress) throws Exception{
        InputStream in = new FileInputStream(fileAddress);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[CHUNK_SIZE];
        while((nRead = in.read(data, 0, CHUNK_SIZE))!=-1){
            buffer.write(data,0,nRead);
        }
        buffer.flush();
        in.close();
        return buffer.toByteArray();
    }
}
