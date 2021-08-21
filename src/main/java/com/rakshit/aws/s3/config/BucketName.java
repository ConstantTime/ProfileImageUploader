package com.rakshit.aws.s3.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketName {
    USER_IMAGE("rakshit-testing");
    private final String bucketName;
}