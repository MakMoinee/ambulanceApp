package com.ambulanceapp.client.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class MapForm {

    public static Map<String, Object> convertObjectToMap(Object data) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(data, Map.class);
        return param;
    }
}
