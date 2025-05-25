package study.goorm.domain.history.application;

import com.amazonaws.AmazonServiceException; // v1의 일반적인 AWS 서비스 예외
import com.amazonaws.services.s3.AmazonS3; // AmazonS3 임포트
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.history.exception.HistoryException;
import study.goorm.global.error.code.status.ErrorStatus; // 예외 처리용 ErrorStatus

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder; // URL 디코딩을 위해 임포트
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service{

    private final AmazonS3 amazonS3; // AmazonS3 클라이언트 주입

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * MultipartFile을 S3에 업로드하고, 저장된 파일의 URL을 반환합니다.
     * @param file 업로드할 MultipartFile
     * @return 업로드된 파일의 S3 URL
     * @throws IOException 파일 처리 중 발생할 수 있는 예외
     */
    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectKey = UUID.randomUUID().toString() + extension; // S3 객체 키 (고유한 파일 이름)

        // 메타데이터 설정 (파일 크기, 콘텐츠 타입 등)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            // PutObjectRequest 생성 및 파일 업로드 (v1 방식)
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, inputStream, metadata);
            amazonS3.putObject(putObjectRequest);

            log.info("File '{}' uploaded to S3 bucket '{}' as '{}'", originalFilename, bucketName, objectKey);

            // S3 퍼블릭 URL 반환 (v1에서는 getUrl() 메소드 사용)
            return amazonS3.getUrl(bucketName, objectKey).toString();
        } catch (AmazonServiceException e) { // S3 관련 예외는 AmazonServiceException
            log.error("Error uploading file to S3: {}", e.getErrorMessage());
            throw new HistoryException(ErrorStatus.S3_FILE_UPLOAD_FAILED);
        } catch (IOException e) {
            log.error("IO error during S3 upload: {}", e.getMessage(), e);
        }
        return originalFilename;
    }

    /**
     * S3에 저장된 파일을 URL을 통해 삭제합니다.
     * @param fileUrl 삭제할 파일의 S3 URL
     */
    public void deleteFile(String fileUrl) {
        String objectKey;
        try {
            // S3 URL에서 objectKey 추출
            // 예: https://your-bucket.s3.ap-northeast-2.amazonaws.com/your-object-key.jpg
            String path = new java.net.URL(fileUrl).getPath();
            // URL 디코딩이 필요할 수 있습니다 (파일 이름에 특수 문자 포함 시)
            // UTF-8로 디코딩, 프로젝트 인코딩에 따라 변경될 수 있음
            objectKey = URLDecoder.decode(path.substring(path.indexOf('/') + 1), "UTF-8");

            // 객체 키가 버킷 이름으로 시작하는 경우 제거 (URL 형식에 따라 다름)
            // 예: /your-bucket/your-object-key.jpg -> your-object-key.jpg
            if (objectKey.startsWith(bucketName + "/")) {
                objectKey = objectKey.substring(bucketName.length() + 1);
            }

            if (objectKey.isEmpty()) {
                throw new IllegalArgumentException("Object key could not be extracted from URL: " + fileUrl);
            }
        } catch (Exception e) {
            log.error("Invalid file URL for deletion: {}", fileUrl);
            throw new HistoryException(ErrorStatus.S3_FILE_URL_PARSE_FAILED);
        }

        try {
            // DeleteObjectRequest 생성 및 파일 삭제 (v1 방식)
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, objectKey);
            amazonS3.deleteObject(deleteObjectRequest);
            log.info("File '{}' deleted from S3 bucket '{}'", objectKey, bucketName);
        } catch (AmazonServiceException e) {
            log.error("Error deleting file from S3: {}", e.getErrorMessage());
            throw new HistoryException(ErrorStatus.S3_FILE_DELETE_FAILED);
        }
    }
}