package com.baidu.ueditor.upload;

import com.baidu.qiniu.QiniuUtils;
import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import org.apache.tomcat.util.codec.binary.Base64;

import java.util.Map;

public final class Base64Uploader {

    public static State save(String content, Map<String, Object> conf) {
        byte[] data = decode(content);
        long maxSize = ((Long) conf.get("maxSize")).longValue();
        if (!validSize(data, maxSize)) {
            return new BaseState(false, AppInfo.MAX_SIZE);
        }
        String suffix = FileType.getSuffix("JPG");
        String savePath = PathFormat.parse((String) conf.get("savePath"),
                (String) conf.get("filename"));
        savePath = savePath + suffix;
        String url = QiniuUtils.upload(content, savePath);
        if (url == null) {
            return new BaseState(false, AppInfo.FAILED_CREATE_FILE);
        }
        State storageState = new BaseState();
        storageState.putInfo("url", url);
        storageState.putInfo("type", suffix);
        storageState.putInfo("original", "");
        return storageState;
    }

    private static byte[] decode(String content) {
        return Base64.decodeBase64(content);
    }

    private static boolean validSize(byte[] data, long length) {
        return data.length <= length;
    }

}