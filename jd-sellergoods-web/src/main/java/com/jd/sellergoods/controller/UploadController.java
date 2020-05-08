package com.jd.sellergoods.controller;

import com.jd.common.pojo.JdResult;
import com.jd.common.utils.FastDFSClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

   @RequestMapping("upload")
   public JdResult upload(MultipartFile file){
      //读取要上传文件的名
      String originalFilename = file.getOriginalFilename();
      //获取到文件的扩展名
      String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
      try{
         //创建封装的对象
         FastDFSClient client = new FastDFSClient("classpath:conf/fdfs_client.conf");
         //执行上传
         String path = client.uploadFile(file.getBytes(), extName);
         //拼接ip地址和url
         String url = "http://192.168.126.133:8888/"+path;
         return new JdResult(true,url,null);
      }catch (Exception e){
         e.printStackTrace();
         return new JdResult(false,"文件上传失败",null);
      }
   }
}
