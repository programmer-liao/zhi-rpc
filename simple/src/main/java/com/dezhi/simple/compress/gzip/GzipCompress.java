package com.dezhi.simple.compress.gzip;

import com.dezhi.simple.compress.Compress;
import jdk.nashorn.internal.ir.CallNode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 使用Gzip实现解压缩
 *
 * @author liaodezhi
 * @date 2023/2/4
 */
public class GzipCompress implements Compress {

    /**
     * 缓冲区大小
     */
    private static final int BUFFER_SIZE = 1024 * 4;

    /**
     * 压缩
     * @param bytes 压缩前的字节数组
     * @return 压缩后的字节数组
     */
    @Override
    public byte[] compress(byte[] bytes) {
        // 如果字节数组为空
        if (bytes == null) {
            throw new NullPointerException("字节数组为空");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            // 压缩
            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            // 返回压缩结果
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解压
     * @param bytes 解压前的字节数组
     * @return 解压后的字节数组
     */
    @Override
    public byte[] decompress(byte[] bytes) {
        // 如果字节数组为空
        if (bytes == null) {
            throw new NullPointerException("字节数组为空");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream gunzip = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            // 循环读取
            while ((n = gunzip.read(buffer))> -1) {
                out.write(buffer, 0, n);
            }
            // 返回
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
