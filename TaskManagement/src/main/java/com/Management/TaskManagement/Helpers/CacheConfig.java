package com.Management.TaskManagement.Helpers;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableCaching
@EnableRetry
public class CacheConfig {
}
