package com.baidu.ueditor.hunter;

import com.baidu.qiniu.QiniuUtils;
import com.baidu.qiniu.Total;
import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FileManager {

    private String dir = null;
    private String[] allowFiles = null;
    private int count = 0;

    public FileManager(Map<String, Object> conf) {
        this.dir = (String) conf.get("dir");
        this.allowFiles = this.getAllowFiles(conf.get("allowFiles"));
        this.count = (Integer) conf.get("count");

    }

    public State listFile(int index) {
        Total total=new Total();
        List<String> listFile = QiniuUtils.listFile(this.dir, index, this.count,total);
        State state = null;
        if (listFile == null) {
            state = new MultiState(true);
        } else {
            state = new MultiState(true);
            BaseState fileState = null;
            for (String key : listFile) {
                fileState = new BaseState(true);
                fileState.putInfo("url", key);
                ((MultiState) state).addState(fileState);

            }
        }
        state.putInfo("start", index);
        state.putInfo("total",total.getTotal());
        return state;
    }

    private String[] getAllowFiles(Object fileExt) {

        String[] exts = null;
        String ext = null;

        if (fileExt == null) {
            return new String[0];
        }

        exts = (String[]) fileExt;

        for (int i = 0, len = exts.length; i < len; i++) {

            ext = exts[i];
            exts[i] = ext.replace(".", "");

        }

        return exts;

    }

}
