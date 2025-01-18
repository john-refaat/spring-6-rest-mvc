package guru.springframework.spring6restmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author john
 * @since 24/08/2024
 */
@Profile("!restassured")
@Configuration
public class SpringSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                //.anyRequest().permitAll())
                                .requestMatchers("/v3/api-docs**", "/swagger-ui/index.html", "/swagger-ui.html").permitAll()
                                    .anyRequest().authenticated())
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->  {
                    httpSecurityOAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults());
                });
               /* .httpBasic(Customizer.withDefaults())
                .csrf(httpSecurityCsrfConfigurer ->
                        httpSecurityCsrfConfigurer.ignoringRequestMatchers("/api/**"));*/
        return http.build();
    }
}
