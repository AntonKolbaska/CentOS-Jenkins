package com.gproject.filters;

import com.gproject.entity.Roles;
import com.gproject.services.impl.JWTServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebFilter(filterName = "AuthenticationFilter",
        urlPatterns = {"/*"} )
public class AuthFilter extends HttpFilter {

    private final List<String> ALLOWED_URL = List.of("/login", "/workstation");

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (ALLOWED_URL.contains(pathInfo)){
            chain.doFilter(request, response);
        }
        else {
            String authorization = request.getHeader("Authorization");

            if (authorization == null || !authorization.matches("Bearer .+")){
                System.out.println("Auth token is absent");
                response.setContentType("application/json");
                response.setStatus(401);
                PrintWriter out = response.getWriter();
                out.println("Auth token is absent");
                return;
            }

            String token = authorization.replaceAll("(Bearer)", "").trim();

            JWTServiceImpl jwtService = JWTServiceImpl.getInstance();
            Jws<Claims> claims;

            //check token
            try {
                claims = jwtService.verifyUserToken(token);
            } catch (JwtException e) {
                System.out.println("Bad token");
                response.setContentType("application/json");
                response.setStatus(401);
                PrintWriter out = response.getWriter();
                out.println("Bad token");
                return;
            }

            Roles role = Roles.valueOf(claims.getBody().get("role", String.class));
            System.out.println("ROLE==================="+role);
            System.out.println("method==================="+request.getMethod());
            //restrict actions for default user
            System.out.println("comparing: " + (role == Roles.EMPLOYEE));
            if(role.equals(Roles.EMPLOYEE) & !request.getMethod().equals("GET")) {
                response.setContentType("application/json");
                response.setStatus(403);
                PrintWriter out = response.getWriter();
                out.println("Access denied");
                return;
            }
            chain.doFilter(request, response);
        }


    }
}
