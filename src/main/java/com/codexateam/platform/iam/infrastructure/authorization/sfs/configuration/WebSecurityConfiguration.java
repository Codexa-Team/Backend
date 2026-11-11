package com.codexateam.platform.iam.infrastructure.authorization.sfs.configuration;

import com.codexateam.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.codexateam.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.codexateam.platform.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Main Spring Security configuration class.
 * Configures CORS, CSRF, session management, and authorization rules.
 * JWT authentication is fully enabled.
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final HashingService hashingService;
    private final AuthenticationEntryPoint unauthorizedRequestHandler;

    /**
     * Creates security configuration with required user details, token, hashing, and entry point beans.
     *
     * @param userDetailsService user details loader
     * @param tokenService JWT token service
     * @param hashingService password encoder service
     * @param unauthorizedRequestHandler handler for unauthorized requests
     */
    public WebSecurityConfiguration(
            @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
            TokenService tokenService,
            HashingService hashingService,
            @Qualifier("unauthorizedRequestHandlerEntryPoint")
            AuthenticationEntryPoint unauthorizedRequestHandler) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.hashingService = hashingService;
        this.unauthorizedRequestHandler = unauthorizedRequestHandler;
    }

    /**
     * Registers the JWT bearer authorization filter bean.
     *
     * @return configured {@link BearerAuthorizationRequestFilter}
     */
    @Bean
    public BearerAuthorizationRequestFilter authorizationRequestFilter() {
        return new BearerAuthorizationRequestFilter(tokenService, userDetailsService);
    }

    /**
     * Exposes the Spring {@link AuthenticationManager} bean.
     *
     * @param authenticationConfiguration Spring authentication configuration
     * @return authentication manager instance
     * @throws Exception if retrieval fails
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures DAO authentication provider backed by custom user details and password encoder.
     *
     * @return configured {@link DaoAuthenticationProvider}
     */
    @Bean
    @SuppressWarnings("deprecation")
    public DaoAuthenticationProvider authenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Adapts {@link HashingService} to Spring Security's {@link PasswordEncoder}.
     *
     * @return password encoder delegating to the hashing service
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return hashingService.encode(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return hashingService.matches(rawPassword, encodedPassword);
            }
        };
    }

    /**
     * Builds the HTTP security filter chain with stateless JWT authentication and CORS policy.
     *
     * @param http HTTP security builder
     * @return configured security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    @SuppressWarnings({"java:S4502", "java:S5122"})
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(configurer -> configurer.configurationSource(request -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOriginPatterns(List.of("*"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        }));
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(unauthorizedRequestHandler))
                .sessionManagement(customizer ->
                        customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/api/v1/authentication/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/error",
                                "/").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/vehicles",
                                "/api/v1/vehicles/**",
                                "/api/v1/reviews/vehicle/**").permitAll()
                        .requestMatchers(HttpMethod.HEAD,
                                "/api/v1/vehicles/**").permitAll()
                        .anyRequest().authenticated());
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
