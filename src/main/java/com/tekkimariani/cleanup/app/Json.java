package com.tekkimariani.cleanup.app;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekkimariani.cleanup.net.Host;

public class Json {
    
    public static void save(String filename, Map<String, Host> hosts) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(filename), hosts);
            System.out.println("JSON gespeichert!");
        } catch (IOException e) {
            e.printStackTrace();
        }   	
    }
    
    public static Map<String, Host> load(String filename) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(new File(filename), new TypeReference<Map<String, Host>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
