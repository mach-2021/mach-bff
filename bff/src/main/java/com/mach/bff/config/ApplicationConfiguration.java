package com.mach.bff.config;

import com.mach.commercetools.config.CTConfiguration;
import com.mach.core.config.CoreConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CTConfiguration.class,
        CoreConfiguration.class})
public class ApplicationConfiguration {

}
