package study.goorm.storage;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    private final S3Template s3Template;

    // application.yml에 설정한 버킷 이름 주입
    @Value("goorm")
    private String bucketName;

    // S3Template은 Spring Cloud AWS가 자동으로 빈으로 등록해 줍니다.
    public FileStorageService(S3Template s3Template) {
        this.s3Template = s3Template;
    }

    /**
     * 파일 업로드
     * @param file MultipartFile
     * @return 업로드된 파일의 URL
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 중복을 피하기 위해 파일 이름에 UUID를 추가
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // S3Template을 사용하여 파일을 버킷에 업로드
        // 반환값은 S3Resource 객체로, 파일의 URL 등을 얻을 수 있음
        S3Resource resource = s3Template.upload(bucketName, uniqueFilename, file.getInputStream());

        return resource.getURL().toString();
    }

    /**
     * 파일 다운로드
     * @param fileName 파일 이름
     * @return S3Resource 객체 (파일 내용을 포함)
     */
    public S3Resource downloadFile(String fileName) {
        // S3Template을 사용하여 버킷에서 파일을 리소스로 가져옴
        return s3Template.download(bucketName, fileName);
    }

    /**
     * 파일 삭제
     * @param fileName 파일 이름
     */
    public void deleteFile(String fileName) {
        s3Template.deleteObject(bucketName, fileName);
    }
}