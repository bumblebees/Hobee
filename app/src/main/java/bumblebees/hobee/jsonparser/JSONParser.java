package bumblebees.hobee.jsonparser;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;

import bumblebees.hobee.objects.Hobby;

/**
 * Created by amandahoffstrom on 2016-11-29.
 */

public class JSONParser {

    Hobby hobby = null;
    JSONObject jsonObject;


    public void writeJSONFile(String jsonPath, Hobby newHobby){
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(new File("something.json"), newHobby);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readJSONFile(String jsonPath){
        File jsonFile = new File(jsonPath);
        ObjectMapper mapper = new ObjectMapper();
        try {
            hobby = mapper.readValue(jsonFile, Hobby.class);
        } catch (IOException e){
            System.out.println("Can't read the file: " + jsonPath);
        }

        try {
            hobby = mapper.readValue(jsonFile, Hobby.class);
        } catch (IOException e) {
            System.out.println("Can't parse the file: " + jsonPath);
        }
    }

}
