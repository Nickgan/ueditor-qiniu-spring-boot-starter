package com.baidu.ueditor;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 配置文件
 *
 * @author lihy
 * @version 2018/6/12
 */
@ConfigurationProperties(prefix = "ue", ignoreInvalidFields = true)
public class UdeitorProperties {

    /**
     * config.json 路径
     */
    private String configFile;

    /**
     * ueditor服务器统一请求接口路径
     */
    private String serverUrl;

    private Map<String, String> qiniu;

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Map<String, String> getQiniu() {
        return qiniu;
    }

    public void setQiniu(Map<String, String> qiniu) {
        this.qiniu = qiniu;
    }
}
