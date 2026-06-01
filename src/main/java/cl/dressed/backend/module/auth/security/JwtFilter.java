package cl.dressed.backend.module.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payloadJson = new String(
                        Base64.getUrlDecoder().decode(addPadding(parts[1])),
                        StandardCharsets.UTF_8
                    );

                    Long userId = extractUid(payloadJson);
                    String role = extractRole(payloadJson);

                    if (userId != null) {
                        var auth = new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        System.out.println("JWT OK - userId=" + userId + " role=" + role);
                    }
                }
            } catch (Exception e) {
                System.out.println("JWT ERROR: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private Long extractUid(String payload) {
        int idx = payload.indexOf("\"uid\":");
        if (idx == -1) return null;
        String from = payload.substring(idx + 6).trim();
        String val = from.split("[,}]")[0].trim();
        return Long.parseLong(val);
    }

    private String extractRole(String payload) {
        int idx = payload.indexOf("\"role\":\"");
        if (idx == -1) return "user";
        String from = payload.substring(idx + 8);
        return from.substring(0, from.indexOf("\""));
    }

    private String addPadding(String s) {
        int pad = 4 - (s.length() % 4);
        return pad == 4 ? s : s + "=".repeat(pad);
    }
}