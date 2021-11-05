package com.simplehttpserver.httpserver;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.sql.SQLException;

public class ClientHandler {

    //user sign up
    public String userSignup(String body) throws IOException, SQLException {


        String input = body;
        System.out.println(input);
        ObjectMapper mapper = new ObjectMapper();

        LoginStruct loginStruct = mapper.readValue(input, LoginStruct.class);
        System.out.println(loginStruct.getUsername());

        Signup signup = new Signup(loginStruct.getName(), loginStruct.getUsername(), loginStruct.getPassword());

        if (signup.isDbConnected()){
            String result = signup.addUser();
            String[] resultSet = result.split("/");
            if (resultSet[0].contains("true")){
                return ("Logged/"+resultSet[1]);
            }
            else if(resultSet[0].contains("false-username")){
                return ("Already exist");
            }
            else {
                return ("invalid");
            }
        }
        else {
            System.out.println("Something went wrong!..");
            return ("Database not connected.");
        }

    }

    public String userLogin(String body) throws IOException, SQLException {

        ObjectMapper mapper = new ObjectMapper();

        LoginStruct loginStruct = mapper.readValue(body, LoginStruct.class);
        System.out.println(loginStruct.getUsername());
        Login login = new Login(loginStruct.getUsername(),loginStruct.getPassword());
        if (login.isDbConnected()){
            String result = login.checkLogin();
            String[] resultSet = result.split("/");
            if (resultSet[0].contains("true")){
                return ("Logged/"+resultSet[1]);
            }
            else {
                return ("invalid");
            }
        }
        else {
            System.out.println("Database not connected.");
            return ("Something went wrong!..");
        }
    }

    //using for search patient numbers..
    public String numberSearch(String number) throws IOException, SQLException {
//        List<Patient> data = new ArrayList<Patient>();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientData patientData = new PatientData();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        System.out.println(number);
            String phone = number;
            List<Patient> data = patientData.customerSet(phone);
            if (data.isEmpty()) {
                return ("Phone number is not exist");
            } else {
                for(int x=0; x<data.size(); x++){
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    int year = data.get(x).year;
                    int age = currentYear - year;
                    data.get(x).year = age;

                }
                //Set pretty printing of json
                //objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


                //1. Convert List of Person objects to JSON
                String arrayToJson = objectMapper.writeValueAsString(data);
                System.out.println("1. Convert List of person objects to JSON :");
                System.out.println(arrayToJson);
                return (arrayToJson);
            }


    }

    public  String RecordsById(int id) throws JsonProcessingException {
        int p_id = id;
        PatientData patientData = new PatientData();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        List<RecordFetch> records = patientData.fetchRecords(p_id);
        String recordsToJson = objectMapper.writeValueAsString(records);
        System.out.println(recordsToJson);
        return (recordsToJson);
    }

    //Add new Record
    public  boolean  addRecord(String body) throws IOException, SQLException {
//        String[] p_idSet = link[1].split("%");
//        int p_id = Integer.parseInt(p_idSet[1]);
//        String[] recordSet = link[2].split("%");
//        String[] d_idSet = link[3].split("%");
//        int d_id = Integer.parseInt(d_idSet[1]);

        ObjectMapper mapper = new ObjectMapper();
        RecordStruct recordStruct = mapper.readValue(body, RecordStruct.class);

        //Record record = new Record(p_id,recordSet[1], "", d_id);

        PatientData patientData = new PatientData();
        boolean result = patientData.newRecord(recordStruct);
        if(result){
            System.out.println("Record added");
            return true;
        }
        else {
            System.out.println("Something went wrong");
            return false;
        }

    }

    //Add new patient
    public  String  addPatient(String body) throws IOException, SQLException {

        String input = body;

        ObjectMapper mapper = new ObjectMapper();
        PatientStructServer patient = mapper.readValue(input, PatientStructServer.class);
        String p_name = patient.getName();
        String p_age = patient.getAge();
        String p_phone = patient.getPhone();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int newAge = Integer.parseInt(p_age);
        int bornYear = year - newAge;
        Patient patientAdd = new Patient(p_name,0,bornYear,p_phone);
        PatientData patientData = new PatientData();
        int p_id = patientData.addPatient(patientAdd);
        if (p_id != 0){
            return ("Patient added successfully/p_id%"+p_id);
        }
        else {
            return ("Something went wrong...");
        }

    }


}
