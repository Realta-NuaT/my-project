package org.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.entity.dto.Account;
import org.example.service.AccountService;
import org.example.utils.Const;
import org.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 用于对请求头中Jwt令牌进行校验的工具，为当前请求添加用户验证信息
 * 并将用户的ID存放在请求对象属性中，方便后续使用
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    JwtUtils utils;

    @Resource
    AccountService service;

    @Resource
    StringRedisTemplate template;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        DecodedJWT jwt = utils.resolveJwt(authorization);
        if(jwt != null) {
            Account account = service.findAccountById(utils.toId(jwt));
            if(!template.hasKey(Const.BANNED_BLOCK + utils.toId(jwt))) {
                if (account == null) {
                    utils.invalidateJwt(authorization);
                    // 设置 HTTP 状态码，401 表示认证失败，需要重新登录
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    // 设置响应内容类型和编码
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    // 写入结构化错误信息（方便前端解析）
                    String errorMsg = "{\"code\": 401, \"message\": \"账户不存在或已被删除，请重新登录\"}";
                    response.getWriter().write(errorMsg);

                    return;  // 直接返回，不继续调用 filterChain
                }
                UserDetails user = utils.toUser(jwt);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute(Const.ATTR_USER_ID, utils.toId(jwt));
            }else{
                utils.invalidateJwt(authorization);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // 设置响应内容类型和编码
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                // 写入结构化错误信息（方便前端解析）
                String errorMsg = "{\"code\": 401, \"message\": \"账户不存在或已被删除，请重新登录\"}";
                response.getWriter().write(errorMsg);

                return;  // 直接返回，不继续调用 filterChain
            }
        }
        filterChain.doFilter(request, response);
    }
}