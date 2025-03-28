package spot.spot.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

@Slf4j
public record AwsS3ObjectStorage(AmazonS3 amazonS3, String bucket) {

    public String uploadFile(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String fileName = UUID.randomUUID() + "_" + originalFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        try {
            // Input 순서: 버킷 이름, KEY (==UUID), 파일 내용, 메타데이터
            amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new GlobalException(ErrorCode.S3_INPUT_ERROR);
        }
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        try {
            // URL에서 URI 뽑기
            URI uri = new URI(fileUrl);
            // 첫번째 '/'를 제거하여 객체 키 얻기
            String key = uri.getPath().substring(1);

            if(amazonS3.doesObjectExist(bucket, key)) {
                amazonS3.deleteObject(bucket, key);
                log.info("✅ 파일 삭제 완료: {}", key);
            }else {
                log.error(ErrorCode.FIELD_NOT_FOUND.getMessage());
                throw new GlobalException(ErrorCode.FILE_NOT_FOUND);
            }

        } catch (Exception e) {
            log.error(ErrorCode.S3_SEVER_ERROR.getMessage(), e);
            throw new GlobalException(ErrorCode.S3_INPUT_ERROR);
        }
    }
}
