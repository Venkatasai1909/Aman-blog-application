package com.aman.BlogPost;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "SELECT username, password, enabled FROM usertable WHERE username=?");

        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "SELECT username, authority FROM usertable WHERE username=?");

        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/editComment/**", "/deleteComment/**").hasAnyRole("AUTHOR", "ADMIN")
                .requestMatchers("/addComment").permitAll()
                .requestMatchers("/allPublishedPost", "/Draft").authenticated()
                .requestMatchers("/createForm").authenticated()
                .anyRequest().permitAll()

        ).formLogin(form -> form.
                loginPage("/loginForm")
                .loginProcessingUrl("/authenticateTheUser")
                .defaultSuccessUrl("/posts", true)
                .permitAll()

        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/posts")
                .permitAll()

        ).exceptionHandling(configurer ->
                configurer.accessDeniedPage("/access-denied")
        );

        return http.build();
    }
}





