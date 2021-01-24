package com.mach.bff.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.cloud.gcp.core.GcpProjectIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GCPConfiguration {

    @Bean
    public GcpProjectIdProvider gcpProjectIdProvider(final CredentialsProvider credentialsProvider) throws IOException {
        final String projectId = ((ServiceAccountCredentials) credentialsProvider.getCredentials()).getProjectId();
        return () -> projectId;
    }
}
