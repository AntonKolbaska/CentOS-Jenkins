package com.gproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gproject.annotations.Controller;
import com.gproject.annotations.HttpMethod;
import com.gproject.annotations.RequestMapping;
import com.gproject.entity.Credentials;
import com.gproject.entity.Roles;
import com.gproject.services.impl.JWTServiceImpl;
import com.gproject.services.impl.UserServiceImpl;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class AuthController {
    private static final String AUTH_COOKIE = "auth";


    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final UserServiceImpl userService = UserServiceImpl.getInstance();

    private HttpServletResponse sendError(int errorCode, String errorReason, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(errorCode);
        PrintWriter out = resp.getWriter();
        out.println(errorReason);
        return resp;
    }


    @RequestMapping(url = "/login", method = HttpMethod.POST)
    public HttpServletResponse login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //get credentials from req
        Credentials anonymous = jsonMapper.readValue(req.getReader(), Credentials.class);
        anonymous.setRole(userService.findUserByUsername(anonymous.getUsername()).getRole());
        if (userService.verifyUser(anonymous.getUsername(),anonymous.getPassword())) {
            JWTServiceImpl jwtService = JWTServiceImpl.getInstance();
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            String token = jwtService.buildUserToken(anonymous);
            out.println(token);
            return resp;
        } else {
            System.out.println("=========Login and/or password is incorrect===========");
            return sendError(401, "Login and/or password is incorrect", resp);
        }
    }
}
