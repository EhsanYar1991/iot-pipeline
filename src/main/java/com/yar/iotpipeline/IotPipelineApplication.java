package com.yar.iotpipeline;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

@SpringBootApplication
@RequiredArgsConstructor
public class IotPipelineApplication {

    public static void main(String... args) throws IOException {
        Resource resource = new ClassPathResource("application.properties");
        Properties props = PropertiesLoaderUtils.loadProperties(resource);
        Optional<String> argActiveProfileOpt =
                Stream.of(args).filter(s -> s.startsWith("-Dspring.profiles.active="))
                        .map(s -> s.split("=")[1])
                        .findFirst();
        String activeProfile = argActiveProfileOpt.orElseGet(() -> props.getProperty("spring.profiles.active"));
        new SpringApplicationBuilder(IotPipelineApplication.class)
                .web(
                        Set.of(activeProfile.split(",")).contains("mqtt-producer") ?
                                WebApplicationType.REACTIVE :
                                WebApplicationType.NONE
                )
                .run(args);
    }
}
