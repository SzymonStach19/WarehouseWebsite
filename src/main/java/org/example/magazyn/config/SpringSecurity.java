package org.example.magazyn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
@EnableWebSecurity
public class SpringSecurity implements WebMvcConfigurer{

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/magazyn/uploads/**")
                .addResourceLocations("file:magazyn/uploads/");
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login", "/register/**").permitAll()
                        .requestMatchers("/products").hasAnyRole("USER", "WAREHOUSEMAN", "MANAGER", "ADMIN")
                        .requestMatchers("/products/add/**", "/products/edit/**", "/products/delete/**", "/products/reserve/**").hasAnyRole("ADMIN", "WAREHOUSEMAN", "MANAGER")
                        .requestMatchers("/zones", "/zones/add/**", "/zones/edit/**", "/zones/delete/", "/zones/details/**", "/zones/assignProduct/**", "/zones/removeProduct/**").hasAnyRole("ADMIN", "WAREHOUSEMAN", "MANAGER")
                        .requestMatchers("/reservations").hasAnyRole("ADMIN", "WAREHOUSEMAN", "MANAGER")
                        .requestMatchers("/users", "/updateRoles").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/history/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/magazyn/uploads/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/login");
                        })
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}