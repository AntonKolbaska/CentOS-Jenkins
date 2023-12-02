package com.gproject.controllers;

import com.gproject.dao.impl.JdbcConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;

import static junit.framework.Assert.assertEquals;

public class AuthControllerTest {
    private static Connection conn;

    //TOKENS HAVE TO BE UPDATED BEFORE TESTS LAUNCHING
    private static final String ADMIN_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJHUDAxIiwidXNlciI6ImFibGUiLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE2ODA0NDE2NjIsImV4cCI6MTY4MDQ0NTI2Mn0.oXZ1Ihiuw1xtvxOoBYd0ljA5UYX5riqyWnQii25Mufg";
    private static final String CLIENT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJHUDAxIiwidXNlciI6ImRvZyIsInJvbGUiOiJFTVBMT1lFRSIsImlhdCI6MTY4MDQ0MTYzOSwiZXhwIjoxNjgwNDQ1MjM5fQ.j2uh2M_2lKJf2-IwzljtcXGxkCNuoig7P3xPODuh6_E";



    private HttpResponse<String> sendGet(String url, String token) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("Authorization", token)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDelete(String url, String token) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("Authorization", token)
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPost(String url, String token, String body) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("Authorization", token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPut(String url, String token, String body) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("Authorization", token)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }



    @BeforeAll
    public static void openDBConnection() throws ClassNotFoundException, SQLException {
        conn = JdbcConnection.getInstance().getConnection();
    }

    @BeforeEach
    public void populateDatabase() throws SQLException, ClassNotFoundException {
        String query = "TRUNCATE TABLE users RESTART IDENTITY;\n" +
                "INSERT INTO users(username, password, email, first_name, last_name, role, company) VALUES ('able', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'mail@mail.mail', 'Austin', 'Kowalski', CAST('ADMIN' AS enum_role), 'MMM');\n" +        //12345
                "INSERT INTO users(username, password, email, first_name, last_name, role, company) VALUES ('baker', '65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5', 'email@email.email', 'Grzegorz', 'Brzeczyszczykiewicz', CAST('EMPLOYEE' AS enum_role), 'MMM');\n" +        //qwerty
                "INSERT INTO users(username, password, email, first_name, last_name, role, company) VALUES ('charlie', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'a@b.c', 'Name1', 'Surname2', CAST('ADMIN' AS enum_role), 'MMM');\n" +       //12345678
                "INSERT INTO users(username, password, email, first_name, last_name, role, company) VALUES ('dog', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'test@test.test', 'Robert', 'Lafore', CAST('EMPLOYEE' AS enum_role), 'MMM');\n" +        //12345
                "INSERT INTO users(username, password, email, first_name, last_name, role, company) VALUES ('easy', 'f6ee94ecb014f74f887b9dcc52daecf73ab3e3333320cadd98bcb59d895c52f5', 'lazy@lazy.lazy', 'Robin', 'Williams', CAST('EMPLOYEE' AS enum_role), 'MMM');";         //qwerty12345
        PreparedStatement statement = conn.prepareStatement(query);
        statement.execute();
        statement.close();
    }

    @AfterAll
    public static void closeDBConnection() throws ClassNotFoundException, SQLException {
        conn.close();
    }



    @Test
    void getSingleUserSuccessAsClient() throws Exception {
        HttpResponse<String> response = sendGet("http://localhost:8080/test/workstation?id=1", CLIENT_TOKEN);
        assertEquals(200, response.statusCode());
        assertEquals("application/json", response.headers().map().get("Content-Type").get(0).split(";")[0]);
        assertEquals("{\"id\":1,\"username\":\"able\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"mail@mail.mail\",\"firstName\":\"Austin\",\"lastName\":\"Kowalski\",\"role\":\"ADMIN\",\"company\":\"MMM\"}", response.body().trim());
    }


    @Test
    void getAllUserSuccessAsClient() throws Exception {
        HttpResponse<String> response = sendGet("http://localhost:8080/test/workstation", CLIENT_TOKEN);
        assertEquals(200, response.statusCode());
        assertEquals("application/json", response.headers().map().get("Content-Type").get(0).split(";")[0]);
        assertEquals("[{\"id\":1,\"username\":\"able\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"mail@mail.mail\",\"firstName\":\"Austin\",\"lastName\":\"Kowalski\",\"role\":\"ADMIN\",\"company\":\"MMM\"},{\"id\":2,\"username\":\"baker\",\"password\":\"65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5\",\"email\":\"email@email.email\",\"firstName\":\"Grzegorz\",\"lastName\":\"Brzeczyszczykiewicz\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"},{\"id\":3,\"username\":\"charlie\",\"password\":\"ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f\",\"email\":\"a@b.c\",\"firstName\":\"Name1\",\"lastName\":\"Surname2\",\"role\":\"ADMIN\",\"company\":\"MMM\"},{\"id\":4,\"username\":\"dog\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"test@test.test\",\"firstName\":\"Robert\",\"lastName\":\"Lafore\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"},{\"id\":5,\"username\":\"easy\",\"password\":\"f6ee94ecb014f74f887b9dcc52daecf73ab3e3333320cadd98bcb59d895c52f5\",\"email\":\"lazy@lazy.lazy\",\"firstName\":\"Robin\",\"lastName\":\"Williams\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"}]", response.body().trim());
    }

    @Test
    void getSingleUserSuccessAsAdmin() throws Exception {
        HttpResponse<String> response = sendGet("http://localhost:8080/test/workstation?id=1", ADMIN_TOKEN);
        assertEquals(200, response.statusCode());
        assertEquals("application/json", response.headers().map().get("Content-Type").get(0).split(";")[0]);
        assertEquals("{\"id\":1,\"username\":\"able\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"mail@mail.mail\",\"firstName\":\"Austin\",\"lastName\":\"Kowalski\",\"role\":\"ADMIN\",\"company\":\"MMM\"}", response.body().trim());
    }


    @Test
    void getAllUserSuccessAsAdmin() throws Exception {
        HttpResponse<String> response = sendGet("http://localhost:8080/test/workstation", ADMIN_TOKEN);
        assertEquals(200, response.statusCode());
        assertEquals("application/json", response.headers().map().get("Content-Type").get(0).split(";")[0]);
        assertEquals("[{\"id\":1,\"username\":\"able\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"mail@mail.mail\",\"firstName\":\"Austin\",\"lastName\":\"Kowalski\",\"role\":\"ADMIN\",\"company\":\"MMM\"},{\"id\":2,\"username\":\"baker\",\"password\":\"65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5\",\"email\":\"email@email.email\",\"firstName\":\"Grzegorz\",\"lastName\":\"Brzeczyszczykiewicz\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"},{\"id\":3,\"username\":\"charlie\",\"password\":\"ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f\",\"email\":\"a@b.c\",\"firstName\":\"Name1\",\"lastName\":\"Surname2\",\"role\":\"ADMIN\",\"company\":\"MMM\"},{\"id\":4,\"username\":\"dog\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"test@test.test\",\"firstName\":\"Robert\",\"lastName\":\"Lafore\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"},{\"id\":5,\"username\":\"easy\",\"password\":\"f6ee94ecb014f74f887b9dcc52daecf73ab3e3333320cadd98bcb59d895c52f5\",\"email\":\"lazy@lazy.lazy\",\"firstName\":\"Robin\",\"lastName\":\"Williams\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"}]", response.body().trim());
    }

    //
    @Test
    void getSingleNonExistentUser() throws Exception {
        HttpResponse<String> response = sendGet("http://localhost:8080/api/employee?id=10", CLIENT_TOKEN);
        assertEquals(404, response.statusCode());
    }


    @Test
    void saveNewUserAsAdminSuccess() throws Exception {
        HttpResponse<String> response = sendPost("http://localhost:8080/test/workstation", ADMIN_TOKEN,
                "{\"id\":1,\"username\":\"newUsername\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"newUserTest@mail.mail\",\"firstName\":\"Austin\",\"lastName\":\"Kowalski\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"}");
        assertEquals(201, response.statusCode());
    }


    @Test
    void saveNewUserAsUserFailed() throws Exception {
        HttpResponse<String> response = sendPost("http://localhost:8080/test/workstation", CLIENT_TOKEN,
                "{\"id\":1,\"username\":\"newUsername\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"newUserTest@mail.mail\",\"firstName\":\"Austin\",\"lastName\":\"Kowalski\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"}");
        assertEquals(403, response.statusCode());
    }


    @Test
    void updateUserAsAdminSuccess() throws Exception {
        HttpResponse<String> response = sendPut("http://localhost:8080/test/workstation?id=1",
                ADMIN_TOKEN,
                "{\"id\":1,\"username\":\"updated\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"newUserTest@mail.mail\",\"firstName\":\"Austin\",\"lastName\":\"Kowalski\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"}");
        assertEquals(200,response.statusCode());
    }

//    @Test
//    void updateUserAsAdminWrongId() throws Exception {
//        HttpResponse<String> response = sendPut("http://localhost:8080/test/workstation?id=10",
//                ADMIN_TOKEN,
//                "{\"id\":1,\"username\":\"newUsername\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"newUserTest@mail.mail\",\"firstName\":\"Austin\",\"lastName\":\"Kowalski\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"}");
//        assertEquals(404,response.statusCode());
//    }
//
    @Test
    void updateUserAsAdminUndefinedUser() throws Exception {
        HttpResponse<String> response = sendPut("http://localhost:8080/test/workstation",
                ADMIN_TOKEN,
                "{\"id\":1,\"username\":\"newUsername\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"newUserTest@mail.mail\",\"firstName\":\"Austin\",\"lastName\":\"Kowalski\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"}");
        assertEquals(400,response.statusCode());
    }

    @Test
    void updateUserAsClientForbidden() throws Exception {
        HttpResponse<String> response = sendPut("http://localhost:8080/test/workstation?id=2",
                CLIENT_TOKEN,
                "{\"id\":1,\"username\":\"newUsername\",\"password\":\"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5\",\"email\":\"newUserTest@mail.mail\",\"firstName\":\"Austin\",\"lastName\":\"Kowalski\",\"role\":\"EMPLOYEE\",\"company\":\"MMM\"}");
        assertEquals(403,response.statusCode());
    }

    @Test
    void deleteUserAsAdminSucsess() throws Exception {
        HttpResponse<String> response = sendDelete("http://localhost:8080/test/workstation?id=3",ADMIN_TOKEN);
        assertEquals(200,response.statusCode());
    }

    @Test
    void deleteNonExistentUserAsAdmin() throws Exception {
        HttpResponse<String> response = sendDelete("http://localhost:8080/test/workstation?id=30",ADMIN_TOKEN);
        assertEquals(404,response.statusCode());
    }

    @Test
    void deleteUndefinedUserAsAdmin() throws Exception {
        HttpResponse<String> response = sendDelete("http://localhost:8080/test/workstation",ADMIN_TOKEN);
        assertEquals(400,response.statusCode());
    }

    @Test
    void deleteUserAsClientForbidden() throws Exception {
        HttpResponse<String> response = sendDelete("http://localhost:8080/test/workstation?id=3",CLIENT_TOKEN);
        assertEquals(403,response.statusCode());
    }

}