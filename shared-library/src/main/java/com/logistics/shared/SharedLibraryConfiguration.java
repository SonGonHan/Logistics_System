package com.logistics.shared;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.logistics.shared")
@EnableJpaRepositories("com.logistics.shared")
public class SharedLibraryConfiguration {

}