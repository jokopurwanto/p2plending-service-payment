package com.p2plending.payment.configuration;

import com.p2plending.payment.service.MyUserAuthDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Primary
    @Bean
    public UserDetailsService userDetailsService(){
        return new MyUserAuthDetailService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//        http
//                .httpBasic()
//                .and()
//                .authorizeHttpRequests()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin().permitAll()
//                .defaultSuccessUrl("/employee/all")
//                .and()
//                .logout().permitAll();
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf().disable()
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET,"/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/**").hasAnyAuthority("admin","user")
                .requestMatchers(HttpMethod.PUT,"/**").hasAnyAuthority("admin")
                .requestMatchers(HttpMethod.DELETE,"/**").hasAnyAuthority("admin")
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll()
                .and()
                .logout().permitAll();
        return http.build();
    }

}
