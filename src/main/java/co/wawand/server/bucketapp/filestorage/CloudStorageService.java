package co.wawand.server.bucketapp.filestorage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudStorageService {

    public void uploadFile(String keyName, MultipartFile file) throws IOException;

    Object getFile(String keyName);
}
