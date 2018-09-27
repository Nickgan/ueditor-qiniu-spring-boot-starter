package com.baidu.qiniu;

import com.baidu.ueditor.UdeitorProperties;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.common.ZoneReqInfo;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.UrlSafeBase64;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 七牛云工具类
 *
 * @author lihy
 * @version 2018/9/26
 */
public class QiniuUtils {

    public static UdeitorProperties properties;


    /**
     * 文件上传
     *
     * @param file     上传的文件
     * @param fileName 自定义文件名
     * @return java.lang.String
     * @author lihy
     */
    public static String upload(MultipartFile file, String fileName) {
        String zoneStr = properties.getQiniu().get("zone");
        Zone zone = getByName(zoneStr);
        Configuration cfg = new Configuration(zone);
        UploadManager uploadManager = new UploadManager(cfg);
        String accessKey = properties.getQiniu().get("accessKey");
        String secretKey = properties.getQiniu().get("secretKey");
        String bucket = properties.getQiniu().get("bucket");
        try {
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(file.getBytes());
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(byteInputStream, fileName, upToken, null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                return properties.getQiniu().get("cdn") + putRet.key;
            } catch (QiniuException ex) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * base64图片上传
     *
     * @param content  图片内筒
     * @param fileName 自定义文件名
     * @return java.lang.String
     * @author lihy
     */
    public static String upload(String content, String fileName) {
        String zoneStr = properties.getQiniu().get("zone");
        Zone zone = getByName(zoneStr);
        String accessKey = properties.getQiniu().get("accessKey");
        String secretKey = properties.getQiniu().get("secretKey");
        String bucket = properties.getQiniu().get("bucket");
        String url = zone.getUpBackupHttp(new ZoneReqInfo(accessKey, bucket)) + "/putb64/" + "-1" + "/key/" + UrlSafeBase64.encodeToString(fileName);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        RequestBody rb = RequestBody.create(null, content);
        Request request = new Request.Builder().
                url(url).
                addHeader("Content-Type", "application/octet-stream")
                .addHeader("Authorization", "UpToken " + upToken)
                .post(rb).build();
        System.out.println(request.headers());
        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DefaultPutRet putRet = new Gson().fromJson(response.body().charStream(), DefaultPutRet.class);
        if (putRet.key == null) {
            return null;
        }
        return properties.getQiniu().get("cdn") + putRet.key;
    }

    /**
     * 根据名称获取对应的上传区域
     *
     * @param name zone name
     * @return com.qiniu.common.Zone
     * @author lihy
     */
    static Zone getByName(String name) {
        Zone zone = null;
        switch (name) {
            case "zone0":
                zone = Zone.zone0();
                break;
            case "zone1":
                zone = Zone.zone1();
                break;
            case "zone2":
                zone = Zone.zone2();
                break;
            case "zoneNa0":
                zone = Zone.zoneNa0();
                break;
            case "zoneAs0":
                zone = Zone.zoneAs0();
                break;
            default:
                throw new RuntimeException("百度编辑七牛云zone配置错误，https://developer.qiniu.com/kodo/sdk/1239/java#server-upload");
        }
        return zone;
    }
}
