package com.photory.service.image;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.photory.common.exception.model.ForbiddenException;
import com.photory.common.exception.model.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.photory.common.exception.ErrorCode.FORBIDDEN_FILE_TYPE_EXCEPTION;


@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public List<String> uploadFile(List<MultipartFile> multipartFile) {
        List<String> fileUrlList = new ArrayList<>();

        multipartFile.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new InternalServerException(String.format("파일 (%s) 입력 스트림을 가져오는 중 에러가 발생하였습니다", file.getOriginalFilename()));
            }

            fileUrlList.add(amazonS3.getUrl(bucket, fileName).toString());
        });

        return fileUrlList;
    }

    public void deleteFile(String fileName) {
        try {
            //Delete 객체 생성
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucket, fileName);

            //Delete
            this.amazonS3.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException e) {
            throw new InternalServerException(String.format("파일 (%s) 을 삭제하는 중 에러가 발생하였습니다", fileName));
        } catch (SdkClientException e) {
            throw new InternalServerException(String.format("파일 (%s) 을 삭제하는 중 에러가 발생하였습니다", fileName));
        }
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ForbiddenException(String.format("허용되지 않은 파일 형식 (%s) 입니다.", fileName), FORBIDDEN_FILE_TYPE_EXCEPTION);
        }
    }
}
