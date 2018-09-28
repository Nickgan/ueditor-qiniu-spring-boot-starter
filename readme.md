## 百度编辑器向七牛云存储上传图片资源<br>
1. #### 文件导入<br>
   * 下载百度编辑器源码 链接：[最新版本1.4.3.3 Jsp UTF-8版本](https://ueditor.baidu.com/build/build_down.php?n=ueditor&v=1_4_3_3-utf8-jsp)<br>
   * 创建ueditor目录 resources > static > ueditor 将源码拷贝到目录中
      * jsp目录只保留 config.json 文件即可
   * 构建项目<br>
       下载本项目 执行maven命令 install ，成功后会将生成的jar包放入本地仓库
   * pom文件引入 ueditor-qiniu-spring-boot-start
      ``` 下载源码 maven install 会将jar包放到本地仓库
           <dependency>
                    <groupId>com.dcssn</groupId>
                    <artifactId>ueditor-qiniu-spring-boot-start</artifactId>
                    <version>0.0.1</version>
           </dependency>
      ```
2. #### 项目配置<br>
   * application.yml
      ```application.yml
         ue:
           config-file: static/ueditor/jsp/config.json #resources目录下配置文件的位置
           server-url: /ueditor.do #服务器统一请求接口路径和ueditor.config.js中的serverUrl要一致
           qiniu:
             accessKey: 8Dw03nJLiST7RvsWtPca1JHDgeu8O0BA******
             secretKey: LHkGDHPZCyrUk5BxG7vC5sLY9LmDxf******
             cdn: http://image.xxx.com/ #CDN 加速域名 最后面的斜杠（/）不能少
             bucket: image #存储空间
             zone: zone0 #zone代表机房的位置
      ```
      其中关于Zone对象和机房的关系如下：<br>
      
      | 机房           | Zone对象      | 
      | ------------- |:-------------:| 
      | 华东          | zone0         | 
      | 华北          | zone1         | 
      | 华南          | zone2         | 
      | 北美          | zoneNa0       | 
      | 东南亚        | zoneAs0       | 
   
   * static/ueditor/ueditor.config.js <br>
      将serverUrl 改为application.yml 中ue.server-url 的值
   * config.json <br>
      图片访问路径前缀（imageUrlPrefix）、视频访问路径前缀、文件访问路径前缀不要赋值，会影响回显，其余参数可以按照百度文档修改
   * 上传文件大小 <br>
      spring上传文件默认最大1MB，上传文件大小会先被spring限制，config.json文件大小限制要小于spring的设置，我们可以将spring的限制设大点
      ```
        spring:
          servlet:
            multipart:
              max-file-size: 100MB
      ```
3. #### 测试     
   * 新建Controller 添加mapping
      ```
         @GetMapping("/ue")
         public String index() {
             return "ue";
         }
      ```
   * 在templates下新建页面ue.html
      ```ue.html
         <!DOCTYPE html>
         <html lang="UTF-8" xmlns:th="http://www.springframework.org/schema/jdbc">
         <head>
             <meta charset="UTF-8"/>
             <title>ueditor</title>
             <style>
                 #editor {
                     width: 1024px;
                     height: 500px;
                 }
             </style>
         </head>
         <body>
         <div id="editor" type="text/plain"></div>
         <script th:src="@{/ueditor/ueditor.config.js}"></script>
         <script th:src="@{/ueditor/ueditor.all.min.js}"></script>
         <script th:src="@{/ueditor/lang/zh-cn/zh-cn.js}"></script>
         <script>
             UE.getEditor('editor');
         </script>
         </body>
         </html>
      ```
      如有问题可以加群：806893930 ，我第一次建群，里面就几个人，欢迎你的加入
4. #### 参考百度文档
    代码只修改了上传和获取文件列表的方法，添加了服务器统一请求接口路径的拦截器，没有别的改动，[百度文档](http://fex.baidu.com/ueditor/)
