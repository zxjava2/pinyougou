package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {

    /*@RequestMapping("/upload")*/
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;//文件服务器地址


    @RequestMapping("/upload")
    public Result upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();//获取文件名
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);//截取后缀名
        try {
            //创建一个FastDFS户端对象
            FastDFSClient client = new FastDFSClient("classpath:/config/fdfs_client.conf");
            //3、执行上传处理
            String path = client.uploadFile(file.getBytes(),extName);
            //拼装返回的url和ip地址，拼装成完整的url
            String url=FILE_SERVER_URL+path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }

    }
}

