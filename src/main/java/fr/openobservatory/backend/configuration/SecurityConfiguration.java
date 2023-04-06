package fr.openobservatory.backend.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.openobservatory.backend.configuration.jwt.CookieJwtFilter;
import fr.openobservatory.backend.configuration.jwt.JwtConfigurer;
import fr.openobservatory.backend.configuration.jwt.JwtService;
import fr.openobservatory.backend.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@AllArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;

  // ---

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.apply(new JwtConfigurer())
        .and()
        .authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
        .cors(withDefaults())
        .csrf(CsrfConfigurer::disable)
        .exceptionHandling(
            exception ->
                exception
                    .accessDeniedHandler(accessDeniedHandler())
                    .authenticationEntryPoint(authenticationEntryPoint()))
        .formLogin(
            login ->
                login
                    .loginProcessingUrl("/login")
                    .successHandler(authenticationSuccessHandler())
                    .failureHandler(authenticationFailureHandler()))
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessHandler(logoutSuccessHandler())
                    .deleteCookies(CookieJwtFilter.COOKIE_NAME))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      var user =
          userRepository
              .findByUsernameIgnoreCase(username)
              .orElseThrow(() -> new UsernameNotFoundException((username)));
      var authorities = List.of(new SimpleGrantedAuthority(user.getType().name()));
      return new User(user.getUsername(), user.getPassword(), authorities);
    };
  }

  // ---

  private AuthenticationSuccessHandler authenticationSuccessHandler() {
    return (request, response, authentication) -> {
      var token = jwtService.generateToken(authentication);
      var cookie = new Cookie(CookieJwtFilter.COOKIE_NAME, token);
      cookie.setMaxAge(3600);
      cookie.setPath("/");
      response.addCookie(cookie);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(HttpStatus.OK.value());
    };
  }

  private AuthenticationFailureHandler authenticationFailureHandler() {
    return (request, response, exception) -> {
      var problem =
          ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
      response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      objectMapper.writeValue(response.getWriter(), problem);
    };
  }

  private AccessDeniedHandler accessDeniedHandler() {
    return (request, response, exception) -> {
      var problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
      response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      response.setStatus(HttpStatus.FORBIDDEN.value());
      objectMapper.writeValue(response.getWriter(), problem);
    };
  }

  private AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, exception) -> {
      var problem =
          ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
      response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      objectMapper.writeValue(response.getWriter(), problem);
    };
  }

  private LogoutSuccessHandler logoutSuccessHandler() {
    return (request, response, authentication) -> response.setStatus(HttpStatus.NO_CONTENT.value());
  }
}
