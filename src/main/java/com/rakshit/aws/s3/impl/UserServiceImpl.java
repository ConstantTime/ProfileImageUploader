package com.rakshit.aws.s3.impl;

import com.rakshit.aws.s3.config.BucketName;
import com.rakshit.aws.s3.domain.User;
import com.rakshit.aws.s3.repository.UserRepository;
import com.rakshit.aws.s3.service.S3StorageService;
import com.rakshit.aws.s3.service.SqsService;
import com.rakshit.aws.s3.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static java.util.UUID.randomUUID;
import static org.apache.http.entity.ContentType.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final S3StorageService fileStore;
    private final UserRepository repository;
    private final SqsService sqsService;

    @Override
    public User saveUser(String title, String description, MultipartFile file) {
        //check if the file is empty
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        //Check if the file is an image
        if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_BMP.getMimeType(),
                IMAGE_GIF.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("FIle uploaded is not an image");
        }
        //get file metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        //Save Image in S3 and then save User in the database
        String path = String.format("%s", BucketName.USER_IMAGE.getBucketName());
        String fileName = String.format("%s", UUID.randomUUID());
        try {
            fileStore.upload(path, fileName, Optional.of(metadata), file.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file", e);
        }
        User user = User.builder()
                .description(description)
                .title(title)
                .imagePath(path)
                .imageFileName(fileName)
                .id(UUID.randomUUID().toString())
                .build();
        repository.save(user);
        return repository.findByTitle(user.getTitle());
    }

    @Override
    public byte[] downloadUserImage(String fileName) {
        User user = repository.findByImageFileName(fileName).get();
        return fileStore.download(user.getImagePath(), user.getImageFileName());
    }

    @Override
    public List<User> getAllUsers() {
        List<User> Users = new ArrayList<>();
        repository.findAll().forEach(Users::add);
        return Users;
    }
}