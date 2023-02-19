package fr.openobservatory.backend.configuration.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
public class CookieJwtFilter extends OncePerRequestFilter {

  public static final String COOKIE_NAME = "AUTH_TOKEN";
  private final AuthenticationManager authenticationManager;

  // ---

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var cookies = request.getCookies();
    if (cookies != null && !"/login".equals(request.getServletPath())) {
      var cookie =
          Arrays.stream(cookies)
              .filter(f -> f.getName().equals(CookieJwtFilter.COOKIE_NAME))
              .findFirst();
      if (cookie.isPresent()) {
        var token = new BearerTokenAuthenticationToken(cookie.get().getValue());
        try {
          var auth = authenticationManager.authenticate(token);
          if (auth != null) {
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
          }
        } catch (Exception ignored) {
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
