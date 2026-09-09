package AI.Job.Application.Platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/register",
                                "/api/jobs/**",
                                "/api/applications/**",
                                "/api/profiles/**",
                                "/api/resumes/**",
                                "/api/resume-analysis/**",
                                "/api/resume-analysis/resume/**",
                                "/api/job-matches/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}