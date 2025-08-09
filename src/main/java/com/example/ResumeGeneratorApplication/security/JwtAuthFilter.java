package com.example.ResumeGeneratorApplication.security;

import com.example.ResumeGeneratorApplication.entity.User;
import com.example.ResumeGeneratorApplication.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;


    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestPath = request.getServletPath();

        List<String> publicRoutes = List.of("/auth/","/v3/api-docs", "/swagger-ui", "/webhook");
        return publicRoutes.stream().anyMatch(r->requestPath.startsWith(r));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1. If there is no authorization token, then throw AuthenticationCredentialsNotFoundException
        //2. extract token from Header
        //3. If user is present and still not authenticated then
            //i. assign role
            //ii. set request details
            //iii set Security Context Holder
        //4. pass request and response in the filter chain

        try{
            final String requestTokenHeader = request.getHeader("Authorization");

            if(requestTokenHeader==null||!requestTokenHeader.startsWith("Bearer")){
                throw new AuthenticationCredentialsNotFoundException("Authorization header is not found");
            }

            String token = requestTokenHeader.split("Bearer ")[1];
            Long userId = jwtService.getUserIdFromToken(token);

            if(userId!=null && SecurityContextHolder.getContext().getAuthentication() == null){
                User user = userService.getUserById(userId);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request,response);
        }
        catch (Exception ex){
            handlerExceptionResolver.resolveException(request,response,null,ex);
        }
    }
}
