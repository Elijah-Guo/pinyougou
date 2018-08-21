package cn.itcast.core.service;

public interface UploadService {

    public String uploadFile(byte[] file, String fileName, long fileSize) throws Exception;
}
