package com.simplehttpserver.httpserver;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jndi.toolkit.url.Uri;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.SQLException;

public class HttpRequestHandler implements HttpHandler {
    private static final String F_NAME = "fname";
    private static final String ID = "id";
    private static final String L_NAME = "lname";

    private static final int PARAM_NAME_IDX = 0;
    private static final int PARAM_VALUE_IDX = 1;

    private static final int HTTP_OK_STATUS = 200;
    private static final int HTTP_BAD_REQUEST_STATUS = 400;

    private static final String AND_DELIMITER = "&";
    private static final String EQUAL_DELIMITER = "=";

    public void handle(HttpExchange t) throws IOException{
        URI uri = t.getRequestURI();
        String url = uri.toString();
        System.out.println(url);


        //get request body...
        int c;
        InputStream requestBody = t.getRequestBody();
        String body ="";
        while( (c=requestBody.read()) != -1){
//            System.out.print((char)c);
            body=body+(char)c;
        }
        System.out.println(body);


        String requestMethod = t.getRequestMethod();
        //check method.
        if (requestMethod.contains("GET")){
            String[] link = url.split("/");

            // http://localhost:8000/app/records/?id=1
            if (link[2].contains("records")){
                getRecords(uri,t);
            }

        }
        else if (requestMethod.contains("POST")){
            String[] link = url.split("/");

            // http://localhost:8000/app/register/
            if (link[2].contains("register")){
                try {
                    Signup(body, t);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // http://localhost:8000/app/login/
            else if (link[2].contains("login")){
                try {
                    Login(body, t);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // http://localhost:8000/app/search/
            else if (link[2].contains("search")){
                try {
                    numberSearch(body, t);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // http://localhost:8000/app/newrecord/
            else if (link[2].contains("newrecord")){
                try {
                    newRecord(body, t);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

             //http://localhost:8000/app/newpatient/
            else if (link[2].contains("newpatient")){
                try {
                    addPatient(body, t);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

//        System.out.println("Method: "+requestMethod);
//        String response = createResponse(uri);
//        System.out.println(response);

//        InputStream requestBody = t.getRequestBody().read();
//        System.out.println(requestBody);


//        t.sendResponseHandlers
    }

    private void Signup(String body, HttpExchange t) throws SQLException, IOException {
        ClientHandler clientHandler = new ClientHandler();
        String response = clientHandler.userSignup(body);
        if (response.contains("Logged")){
            t.sendResponseHeaders(HTTP_OK_STATUS, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        else {
            t.sendResponseHeaders(HTTP_BAD_REQUEST_STATUS, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

        }


    }

    private void Login(String body, HttpExchange t) throws SQLException, IOException {
        ClientHandler clientHandler = new ClientHandler();
        String response = clientHandler.userLogin(body);
        if (response.contains("Logged")){
            t.sendResponseHeaders(HTTP_OK_STATUS, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        else {
            t.sendResponseHeaders(HTTP_BAD_REQUEST_STATUS, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

        }


    }

    private void newRecord(String body, HttpExchange t) throws SQLException, IOException {
        System.out.println("newRec..");
        ClientHandler clientHandler = new ClientHandler();
        boolean response = clientHandler.addRecord(body);
        String result="Success";
        if (response){
            t.sendResponseHeaders(HTTP_OK_STATUS, result.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(result.getBytes());
            os.close();
        }
        else {
            result = "Unsuccessful";
            t.sendResponseHeaders(HTTP_BAD_REQUEST_STATUS, result.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(result.getBytes());
            os.close();

        }
    }

    private void addPatient(String body, HttpExchange t) throws SQLException, IOException {

        ClientHandler clientHandler = new ClientHandler();
        String response = clientHandler.addPatient(body);

        if (!response.equals("Something went wrong...")){
            t.sendResponseHeaders(HTTP_OK_STATUS, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        else {
            t.sendResponseHeaders(HTTP_BAD_REQUEST_STATUS, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private void numberSearch(String body, HttpExchange t) throws SQLException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Phone phoneObj = objectMapper.readValue(body, Phone.class);

        ClientHandler clientHandler = new ClientHandler();
        String response = clientHandler.numberSearch(phoneObj.getPhone());
        if (!response.contains("Phone number is not exist")){
            t.sendResponseHeaders(HTTP_OK_STATUS, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        else {
            t.sendResponseHeaders(HTTP_BAD_REQUEST_STATUS, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }


    }

    private void getRecords(URI uri, HttpExchange t) throws IOException {
        String query = uri.getQuery();
        System.out.println(query);
        String id="";

        if (query != null) {
            System.out.println("Query: " + query);
                    String[] param = query.split(EQUAL_DELIMITER);
                    if (param.length > 0) {
                        for (int i = 0; i < param.length; i++) {
                            if (ID.equalsIgnoreCase(param[PARAM_NAME_IDX])) {
                                id = param[PARAM_VALUE_IDX];
                            }
                        }
                    }
        }
        if (!id.isEmpty()){
            int _id = Integer.parseInt(id);
            ClientHandler clientHandler = new ClientHandler();
            String response = clientHandler.RecordsById(_id);
            t.sendResponseHeaders(HTTP_OK_STATUS, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        else {
            t.sendResponseHeaders(HTTP_BAD_REQUEST_STATUS, id.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(id.getBytes());
            os.close();
        }
    }


    private String createResponse(URI uri){
        String fName = "";
        String lName = "";


        //Get the request query
        String query = uri.getQuery();
        if (query != null) {
            System.out.println("Query: " + query);
            String[] queryParams = query.split(AND_DELIMITER);
            if (queryParams.length > 0) {
                for (String qParam : queryParams) {
                    String[] param = qParam.split(EQUAL_DELIMITER);
                    if (param.length > 0) {
                        for (int i = 0; i < param.length; i++) {
                            if (F_NAME.equalsIgnoreCase(param[PARAM_NAME_IDX])) {
                                fName = param[PARAM_VALUE_IDX];
                            }
                            if (L_NAME.equalsIgnoreCase(param[PARAM_NAME_IDX])) {
                                lName = param[PARAM_VALUE_IDX];
                            }
                        }
                    }
                }
            }
        }

        return "Hello, " + fName + " " + lName;
    }

}
