package bumblebees.hobee.parser;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;

import bumblebees.hobee.objects.Hobby;

/**
 * Created by amandahoffstrom on 2016-11-29.
 */

public class JSONParser {

    Hobby hobby = null;


    public void readJSONFile(String jsonPath){
        readFileAsStream(jsonPath);
        File jsonFile = new File(jsonPath);
        ObjectMapper mapper = new ObjectMapper();
        try {
            hobby = mapper.readValue(jsonFile, Hobby.class);
        } catch (IOException e){
            System.out.println("Can't read the file: " + jsonPath);
        }
    }

    public void readFileAsStream(String file){
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(file);
        String jsonString = IOUtils.toString(inputStream);
    }



    //ObjectMapper objectMapper = new ObjectMapper();

}
