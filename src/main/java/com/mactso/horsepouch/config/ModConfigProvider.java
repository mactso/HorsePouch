package com.mactso.horsepouch.config;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ModConfigProvider implements SimpleConfig.DefaultConfig {

    private String configContents = "";
    private final List<Pair<String,?>> configsList = new ArrayList<>();
    private final List<String> commentValues = new ArrayList<>();
    private final List<Object> defaultValues = new ArrayList<>();
    
    
    public List<Pair<String,?>> getConfigsList() {
    	return configsList;
    }

    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment) {
        configsList.add(keyValuePair);
        commentValues.add(comment);
        defaultValues.add(keyValuePair.getSecond());
         
    }
    
    public void setKeyValuePair(String key, Object value) {
    	for (int i = 0;i < configsList.size();++i) {
    		if (configsList.get(i).getFirst().equals(key)) {
    			configsList.set(i, new Pair<>(key,value));
    			return;
    		}
    	}
    }

    
    @Override
    public String get(String namespace) {
    	configContents="";
    	for (int i = 0; i< configsList.size();++i) {
    		Pair<String, ?> keyValuePair = configsList.get(i);
    		String commentvalue = commentValues.get(i);
    		Object defaultvalue = defaultValues.get(i);
            configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " #"
                    + commentvalue + " | default: " + defaultvalue + "\n";
    	}
        return configContents;
    }
}