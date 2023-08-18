package com.devee.devhive.global.s3;

import static com.devee.devhive.global.exception.ErrorCode.S3_DELETE_ERROR;
import static com.devee.devhive.global.exception.ErrorCode.S3_FILE_SIZE_EXCEEDED;
import static com.devee.devhive.global.exception.ErrorCode.S3_NOT_FOUND_IMAGE;
import static com.devee.devhive.global.exception.ErrorCode.S3_NOT_SUPPORT_IMAGE_TYPE;
import static com.devee.devhive.global.exception.ErrorCode.S3_UPLOAD_ERROR;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.devee.devhive.global.exception.CustomException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList("PNG", "JPG", "JPEG");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes

    public String upload(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException(S3_NOT_FOUND_IMAGE);
        }
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(S3_FILE_SIZE_EXCEEDED);
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String type = Objects.requireNonNull(originalFilename)
            .substring(originalFilename.lastIndexOf(".") + 1);

        validateFileType(type.toUpperCase());

        String fileName = generateUniqueFileName(originalFilename);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);
            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw new CustomException(S3_UPLOAD_ERROR);
        }
    }

    public void delete(String filename) {
        try {
            amazonS3.deleteObject(bucket, filename);
        } catch (AmazonS3Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw new CustomException(S3_DELETE_ERROR);
        }
    }

    private void validateFileType(String type) {
        if (!SUPPORTED_IMAGE_TYPES.contains(type)) {
            throw new CustomException(S3_NOT_SUPPORT_IMAGE_TYPE);
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID() + originalFilename;
    }
}
