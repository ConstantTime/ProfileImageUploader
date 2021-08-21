package com.rakshit.aws.s3.service;

import com.rakshit.aws.s3.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User saveUser(String title, String description, MultipartFile file);

    byte[] downloadUserImage(String id);

    List<User> getAllUsers();
}
