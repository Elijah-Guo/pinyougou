package cn.itcast.core.service;

import cn.itcast.core.util.FastDFSClient;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class UploadServiceImpl implements UploadService {

    @Override
    public String uploadFile(byte[] file, String fileName, long fileSize) throws Exception{

        FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
        String path = fastDFSClient.uploadFile(file, fileName, fileSize);
        return path;
    }
}
