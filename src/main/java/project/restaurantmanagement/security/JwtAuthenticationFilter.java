package project.restaurantmanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 요청이 들어올 때마다 실행되어 JWT 토큰을 검사하고 인증을 수행하는 필터입니다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;
    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.resolveTokenFromRequest(request);

        if(StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
            Authentication authentication = this.authenticationService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info(String.format("[%s] -> %s", this.tokenProvider.getUsername(token), request.getRequestURI()));
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청에서 JWT 토큰을 추출합니다.
     */
    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if(!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length()).trim();
        }

        return null;
    }
}
