package study.goorm.domain.history.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    public String uploadFile(MultipartFile file);

    void deleteFile(String fileUrl);
}
