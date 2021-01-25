package com.mach.bff.config;

import com.mach.bff.security.JwtTokenProvider;
import com.mach.bff.security.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .requestMatchers(req -> req.getRequestURI().contains("swagger"))
                .requestMatchers(req -> req.getRequestURI().contains("api-docs"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .logout()
//                .logoutUrl("/customers/logout")
//                .addLogoutHandler(cookieClearingLogoutHandler)
//                .clearAuthentication(true)
//                .invalidateHttpSession(true)
                .and()
                .authorizeRequests()
                .antMatchers("/customers/**")
                .access("hasAuthority('GUEST')")
                .antMatchers("/categories/**")
                .access("hasAnyAuthority('GUEST', 'CUSTOMER')")
                .antMatchers("/wish-lists/**")
                .access("hasAuthority('CUSTOMER')")
                .and()
                .addFilterBefore(jwtTokenFilter(), LogoutFilter.class);
    }

    private JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtTokenProvider);
    }
}
