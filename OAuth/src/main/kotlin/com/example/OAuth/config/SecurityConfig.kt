package com.example.OAuth.config

import com.example.OAuth.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(private val customOAuth2UserService: CustomOAuth2UserService) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/", "/login", "/oauth2/**", "/loginError").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { oauth2Login ->
                oauth2Login
                    .loginPage("/login")
                    .userInfoEndpoint { userInfoEndpoint ->
                        userInfoEndpoint
                            .userService(customOAuth2UserService)
                    }
                    .defaultSuccessUrl("/loginSuccess", true)
                    .failureUrl("/loginError")
            }
        return http.build()
    }
}