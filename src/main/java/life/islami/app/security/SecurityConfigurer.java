package life.islami.app.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import life.islami.app.enums.Role;

@EnableWebSecurity
@Configuration
public class SecurityConfigurer {

  public SecurityConfigurer() {
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    AuthenticationConfiguration authenticationConfiguration = httpSecurity
        .getSharedObject(AuthenticationConfiguration.class);
    httpSecurity
        .cors().and()
        .csrf().disable()
        .authorizeHttpRequests(authorize -> authorize
            .antMatchers(HttpMethod.POST, "/users/admin").hasAnyAuthority(Role.ADMIN.name())
            .antMatchers(HttpMethod.POST, "/audios").hasAnyAuthority(Role.ADMIN.name(), Role.ARTIST.name())
            .antMatchers(HttpMethod.POST, "/artists").hasAnyAuthority(Role.ADMIN.name(), Role.ARTIST.name())
            .antMatchers(HttpMethod.PUT, "/artists/{artistId}").hasAnyAuthority(Role.ADMIN.name(), Role.ARTIST.name())
            .antMatchers(HttpMethod.GET, "/audios/in-review").hasAnyAuthority(Role.ADMIN.name())
            .antMatchers(HttpMethod.PUT, "/audios/{audioId}/approve").hasAnyAuthority(Role.ADMIN.name())
            .antMatchers(HttpMethod.PUT, "/audios/{audioId}/reject").hasAnyAuthority(Role.ADMIN.name())
            .antMatchers(HttpMethod.PUT, "/audios/{audioId}").hasAnyAuthority(Role.ADMIN.name(), Role.ARTIST.name())
            .antMatchers(HttpMethod.POST, "/subscriptions").hasAnyAuthority(Role.ADMIN.name())
            .antMatchers(HttpMethod.POST, "/users/{userId}/subscription").hasAnyAuthority(Role.USER.name())
            .antMatchers(HttpMethod.DELETE, "/subscriptions/{subscriptionId}").hasAnyAuthority(Role.ADMIN.name())
            .antMatchers(HttpMethod.DELETE, "/audios/{audioId}").hasAnyAuthority(Role.ADMIN.name(), Role.ARTIST.name())
            .antMatchers(HttpMethod.POST, "/audios/{audioId}/listen-time").hasAnyAuthority(Role.USER.name())
            .antMatchers(HttpMethod.PUT, "/audios/{audioId}/reactions").hasAnyAuthority(Role.USER.name())
            .antMatchers(HttpMethod.GET, "/audios/{audioId}/reactions").hasAnyAuthority(Role.USER.name())
            .antMatchers(HttpMethod.PUT, "/audios/{audioId}/comments").hasAnyAuthority(Role.USER.name())
            .antMatchers(HttpMethod.DELETE, "/comments/{commentId}").hasAnyAuthority(Role.USER.name())
            .antMatchers(HttpMethod.DELETE, "/comments/{commentId}/replies/{replyId}").hasAnyAuthority(Role.USER.name())
            .antMatchers(HttpMethod.PUT, "/comments/{commentId}/reactions").hasAnyAuthority(Role.USER.name())
            .antMatchers(HttpMethod.POST, "/covers").hasAnyAuthority(Role.ADMIN.name())
            .antMatchers(HttpMethod.DELETE, "/covers/{coverId}").hasAnyAuthority(Role.ADMIN.name())
            .antMatchers(HttpMethod.POST, "/playlists")
            .hasAnyAuthority(Role.ADMIN.name(), Role.ARTIST.name(), Role.USER.name())
            .antMatchers(HttpMethod.PUT, "/playlists")
            .hasAnyAuthority(Role.ADMIN.name(), Role.ARTIST.name(), Role.USER.name())
            .antMatchers(HttpMethod.DELETE, "/playlists")
            .hasAnyAuthority(Role.ADMIN.name(), Role.ARTIST.name(), Role.USER.name())
            .antMatchers(HttpMethod.DELETE, "/playlists/{playlistId}/audios")
            .hasAnyAuthority(Role.ADMIN.name(), Role.ARTIST.name(), Role.USER.name())
            .antMatchers(HttpMethod.POST, "/playlists/{playlistId}/audios")
            .hasAnyAuthority(Role.ADMIN.name(), Role.ARTIST.name(), Role.USER.name())

            .antMatchers(HttpMethod.PUT, "/comments/{commentId}/replies/{replyId}/reactions")
            .hasAnyAuthority(Role.USER.name())
            .antMatchers(HttpMethod.POST, "/comments/{commentId}/replies")
            .hasAnyAuthority(Role.USER.name())
            .antMatchers(HttpMethod.GET, "/comments/{commentId}/replies").permitAll()
            .antMatchers(HttpMethod.GET, "/audios/{audioId}/comments").permitAll()
            .antMatchers(HttpMethod.GET, "/subscriptions").permitAll()
            .antMatchers(HttpMethod.POST, "/users").permitAll()
            .antMatchers(HttpMethod.POST, "/users/sign-in").permitAll()
            .antMatchers(HttpMethod.GET, "/artists/{artistId}/audios").permitAll()
            .antMatchers(HttpMethod.GET, "/audios").permitAll()
            .antMatchers(HttpMethod.GET, "/audios/{audioId}").permitAll()
            .antMatchers(HttpMethod.GET, "/users").permitAll()
            .antMatchers(HttpMethod.GET, "/covers").permitAll()
            .antMatchers(HttpMethod.GET, "/playlists").permitAll()
            .antMatchers(HttpMethod.GET, "/artists").permitAll()
            .antMatchers(HttpMethod.GET, "/artists/{artistId}").permitAll()
            .antMatchers(HttpMethod.GET, "/login").permitAll()
            .antMatchers(HttpMethod.POST, "/users/{userId}/password-reset").permitAll()
            .antMatchers(HttpMethod.POST, "/users/{userId}/password-reset-token").permitAll()
            .antMatchers(HttpMethod.POST, "/users/artist").permitAll()
            .antMatchers(HttpMethod.GET, "/users/{userId}").permitAll()
            .anyRequest().authenticated())
        .addFilter(new AuthenticationFilter(authenticationManager(authenticationConfiguration)))
        .addFilter(new AuthorizationFilter(authenticationManager(authenticationConfiguration)))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return httpSecurity.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    System.out.println("cors headers added.");
    final CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE",
        "PUT", "OPTIONS"));
    corsConfiguration.setAllowCredentials(false);
    corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
    corsConfiguration.setExposedHeaders(Arrays.asList("Authorization",
        "UserId"));

    Map<String, CorsConfiguration> mapping = new HashMap<>();
    mapping.put("/**", corsConfiguration);
    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
    urlBasedCorsConfigurationSource.setCorsConfigurations(mapping);
    return urlBasedCorsConfigurationSource;
  }

}
