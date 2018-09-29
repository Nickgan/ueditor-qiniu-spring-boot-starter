package com.baidu.ueditor.upload;

import com.baidu.qiniu.QiniuUtils;
import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BinaryUploader {

    public static final State save(HttpServletRequest request,
                                   Map<String, Object> conf) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
        }
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        String fileName = (String) conf.get("fieldName");
        if (fileName == null) {
            return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
        }
        MultipartFile file = mRequest.getFile(fileName);
        if (file.isEmpty()) {
            return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
        }
        String savePath = (String) conf.get("savePath");
        String originFileName = file.getOriginalFilename();
        String suffix = FileType.getSuffixByFilename(originFileName);
        originFileName = originFileName.substring(0,
                originFileName.length() - suffix.length());
        savePath = savePath + suffix;
        savePath = PathFormat.parse(savePath, originFileName);
        long maxSize = ((Long) conf.get("maxSize")).longValue();
        if (maxSize < file.getSize()) {
            return new BaseState(false, AppInfo.MAX_SIZE);
        }
        if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
            return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
        }
        String url = QiniuUtils.upload(file, savePath);
        if (url == null) {
            return new BaseState(false, AppInfo.FAILED_CREATE_FILE);
        }
        State storageState = new BaseState();
        storageState.putInfo("url", url);
        storageState.putInfo("type", suffix);
        storageState.putInfo("original", originFileName + suffix);
        return storageState;
    }

    private static boolean validType(String type, String[] allowTypes) {
        List<String> list = Arrays.asList(allowTypes);
        return list.contains(type);
    }
}
