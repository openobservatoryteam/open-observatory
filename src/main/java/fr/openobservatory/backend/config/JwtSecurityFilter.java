package fr.openobservatory.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


@AllArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var cookies = request.getCookies();
        if (cookies != null && !"login".equals(request.getServletPath())) {
            var cookie = Arrays.stream(cookies).filter(f -> f.getName().equals("JWT_TOKEN")).findFirst();
            if (cookie.isPresent()) {
                var token = new BearerTokenAuthenticationToken(cookie.get().getValue());
                try  {
                    var auth = authenticationManager.authenticate(token).isAuthenticated();
                    if (auth) {
                        var context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(token);
                        SecurityContextHolder.setContext(context);
                    }
                } catch (Exception ignored) {}
            }
        }
        filterChain.doFilter(request, response);
    }
}
