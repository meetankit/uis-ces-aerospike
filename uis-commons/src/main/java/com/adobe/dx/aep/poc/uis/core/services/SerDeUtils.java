package com.adobe.dx.aep.poc.uis.core.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class SerDeUtils {

    /**
     * Serializes specified object to a JSON string. Returns empty string if
     * object is null.
     *
     * @param o
     *          - object to be serialized
     *
     * @return serialized JSON string
     */
    public static String serializeToJson(Object o)
    {
        ObjectMapper om = new ObjectMapper();
        String s = "";
        try
        {
            if (o != null)
            {
                s = om.writeValueAsString(o);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("failed", e);
        }

        return s;
    }

    /**
     * De-serializes map from a JSON string. Returns null if string is null or
     * empty. The values are represented as string, so if they are objects they
     * will be stored in the map as JSON strings.
     *
     * @param json
     *          - map to be de-serialized
     *
     * @return map after de-serialization
     */
    public static Map<String, Object> deserializeMapFromJson(String json,
                                                             Class<?> valueClass)
    {
        Map<String, Object> ret = null;
        ObjectMapper om = new ObjectMapper();

        if (json!= null && !json.equals(""))
        {
            try
            {
                ret = new HashMap<>();
                JsonNode o = om.readTree(json);
                Iterator<String> allFields = o.fieldNames();
                while (allFields.hasNext())
                {
                    String key = allFields.next();
                    Object value = om.treeToValue(o.get(key), valueClass);
                    ret.put(key, value);
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed", e);
            }
        }

        return ret;
    }
}

