package com.mach.bff.config;

import com.mach.commercetools.config.CTConfiguration;
import com.mach.core.config.CoreConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@Import({CTConfiguration.class,
        CoreConfiguration.class})
@EnableSwagger2
public class ApplicationConfiguration {

}
