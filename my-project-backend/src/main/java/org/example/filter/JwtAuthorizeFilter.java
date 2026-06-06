package org.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.utils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

    @Resource
    private JwtUtils jwtUtils;
    /**
     * JWT认证过滤器核心方法
     * 作用：每次请求进来，先校验Token，解析用户信息，放入Spring Security上下文
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 从请求头中获取 Authorization 字段（存储Token的标准头）
        String authorization = request.getHeader("Authorization");
        // 2. 解析Token，验证签名、过期时间等（如果无效会返回null/抛出异常）
        DecodedJWT jwt = jwtUtils.resolveJwt(authorization);
        // 3. 如果Token存在且合法，进行用户认证处理
        if (authorization != null) {
            // 3.1 从解析后的JWT中，构建Spring Security需要的用户对象 UserDetails
            UserDetails user = jwtUtils.toUser(jwt);
            // 3.2 创建认证对象：参数(用户信息, 密码, 权限列表)，密码置空
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            // 3.3 将当前请求的详细信息（如IP、SessionId）绑定到认证对象
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 3.4 【核心】将认证信息存入Spring Security上下文，后续接口可直接获取当前登录用户
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 3.5 将用户ID存入request域，方便Controller中直接通过@RequestAttribute获取
            request.setAttribute("id", jwtUtils.toId(jwt));
        }
        // 4. 放行请求，继续执行后面的过滤器/控制器
        filterChain.doFilter(request, response);
    }
}
