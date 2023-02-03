package com.dezhi.simple.compress;

/**
 * 编解码器接口
 * @author liaodezhi
 * @date 2023/2/3
 */
public interface Compress {

    /**
     * 压缩
     * @param bytes 压缩前的字节数组
     * @return 压缩后的字节数组
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压
     * @param bytes 解压前的字节数组
     * @return 解压后的字节数组
     */
    byte[] decompress(byte[] bytes);
}
