package com.saturn.configuration.jwt;

import com.saturn.configuration.CustomUserDetails;
import com.saturn.configuration.CustomUserDetailsService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

import static org.springframework.util.StringUtils.hasText;

/**
 * Класс для фильтрации токенп
 */
@Component
@Log
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION = "Authorization";

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    /**
     * Фильтрация токена
     * @param servletRequest Запрос
     * @param servletResponse Ответ
     * @param filterChain Цепь фильтрации
     * @throws IOException Ошибка
     * @throws ServletException Ошибка
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        if (token != null && jwtProvider.validateToken(token)) {
            String userLogin = jwtProvider.getLoginFromToken(token);
            CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(userLogin);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Достаем токен из куки
     * @param request Запрос
     * @return Токен
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "auth");
        if (cookie != null) {
            return cookie.getValue();
        }
//        String bearer = request.getHeader(AUTHORIZATION);
//        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
//            return bearer.substring(7);
//        }
        return null;
    }
}