package com.rakshit.aws.s3.repository;

import com.rakshit.aws.s3.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByTitle(String title);

    Optional<User> findByImageFileName(String imageFileName);
}