package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.UploadService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Reference
    private UploadService uploadService;

    @Value("${FILE_SERVER_URL}")
    private String FILES_ERVER;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file) {
        try {
            String path = uploadService.uploadFile(file.getBytes(), file.getOriginalFilename(), file.getSize());

            return  new Result(true, FILES_ERVER + path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败!");
        }
    }
}
