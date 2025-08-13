package com.trustai.common_base.config;

import com.trustai.common_base.constants.CommonConstants;
import com.trustai.common_base.security.filter.InternalTokenAuthFilter;
import com.trustai.common_base.security.filter.JwtAuthenticationFilter;
import com.trustai.common_base.security.jwt.JwtProvider;
import com.trustai.common_base.security.CustomAuthenticationFailureHandler;
import com.trustai.common_base.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final com.trustai.trustai_common.security.AuthEntryPoint unauthorizedHandler;
    private final CustomAuthenticationFailureHandler failureHandler ; // centralize error handle for form base login

    @Value("${security.auth.internal-token}")
    private String internalToken;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 1) API chain - stateless, uses CustomUserDetailsService and JwtAuthenticationFilter
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider, userDetailsService);
        InternalTokenAuthFilter internalTokenAuthFilter = new InternalTokenAuthFilter(internalToken);

        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler))
                //.authenticationProvider(daoProvider) // keep this if not globally registered
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Order is important: check internal token before JWT
                // The order of these two lines matters — whichever you call last will run first. So to run internalTokenAuthFilter before JWT, make sure it’s added after JWT:
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(internalTokenAuthFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    // 2) Form-login chain - stateful, uses an *in-memory* user store created locally
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        // If you want form login to authenticate against this in-memory store,
        // set it explicitly on the HttpSecurity:
        http
                .securityMatcher("/**")
                //.csrf(csrf -> csrf.enable()) // CSRF protection enabled for forms
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/h2-console/**"))
                //.userDetailsService(inMemory)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/public/**",
                                "/favicon.ico",
                                "/h2-console",
                                CommonConstants.IMAGE_PATH + "/**" // "/images/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                    // GET -> your Thymeleaf form
                        .loginProcessingUrl("/perform_login")   // POST -> processed by Spring Security
                        .defaultSuccessUrl("/", true)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .logout(l -> l.logoutUrl("/logout").permitAll());

        return http.build();
    }


    /*
    // If you want fully separate chains (recommended for clarity), create two SecurityFilterChain beans: one
    // matching /api/** (stateless) and one for UI (stateful). Example is left as an exercise but straightforward.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider, userDetailsService);

        http
                .cors(cors -> cors.disable())
                // CSRF: enable for form logins; disable for stateless API endpoints if necessary
                //.csrf(csrf -> csrf.disable())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                // Session for form UI only:
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                //.exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler))
                .securityMatcher("/**") // apply rules globally; we'll separate endpoints below
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/public/**", "/api/auth/**", "/favicon.ico", "/h2-console").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                //.httpBasic(Customizer.withDefaults()) // basic Auth ==> username, password
//                .formLogin(Customizer.withDefaults())
                .formLogin(form -> form
                        //.loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(l -> l.logoutUrl("/logout").permitAll())
                // Add JWT filter before UsernamePasswordAuthenticationFilter so API calls are processed
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // For API statelessness you could also define two http security filter chains separated by request matchers.
        return http.build();
    }*/

}
