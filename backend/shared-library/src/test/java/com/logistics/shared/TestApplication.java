package com.logistics.shared;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SharedLibraryConfiguration.class)
public class TestApplication {

}