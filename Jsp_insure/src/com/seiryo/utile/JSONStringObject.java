package com.seiryo.utile;

import org.json.JSONString;


/**
 *  JSONUtil 的专属辅助类
 * @ClassName: JSONStringObject  
 * @Description: 给一段已经是 JSON 格式的字符串贴上“免检标签”，防止 org.json 库在拼接 JSON 时，对它进行重复转义。  
 * @author 李 
 * @date 2026年10月14日  
 *
 */
public class JSONStringObject implements JSONString { 

    private String jsonString = null; 
    
    public JSONStringObject(String jsonString) { 
        this.jsonString = jsonString; 
    } 

    @Override 
    public String toString() { 
        return jsonString; 
    } 

    public String toJSONString() { 
        return jsonString; 
    } 
} 
