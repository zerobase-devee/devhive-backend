package com.devee.devhive.global.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.devee.devhive.global.config.S3MockConfig;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * S3 Mock Test : 실제 S3에 저장되지 않음
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Import(S3MockConfig.class)
public class S3Test {
    @Autowired
    private S3Mock s3Mock;
    @Autowired
    private S3Service awsS3Uploader;

    @AfterEach
    public void shutdownMockS3() {
        s3Mock.stop();
    }

    @Test
    void upload() {
        // given
        String path = "test.png";
        String contentType = "image/png";

        MockMultipartFile file = new MockMultipartFile("test", path, contentType, "test".getBytes());

        // when
        String urlPath = awsS3Uploader.upload(file);

        // then
        assertThat(urlPath).contains(path);
    }
}


