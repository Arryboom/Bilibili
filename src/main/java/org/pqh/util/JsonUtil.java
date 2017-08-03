package org.pqh.util;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by reborn on 2017/5/27.
 */
public class JsonUtil{
    public static JsonNode findNodeByPath(JsonNode jsonNode,String ...fieldName){
        for(String name:fieldName){
            jsonNode=jsonNode.get(name);
            if(jsonNode==null){
                return null;
            }
        }
        return jsonNode;
    }
}
