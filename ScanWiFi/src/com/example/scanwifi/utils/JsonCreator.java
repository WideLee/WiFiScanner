package com.example.scanwifi.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.scanwifi.object.MapInfo;

public class JsonCreator {
    public static String createMapInfoJsonFromArray(ArrayList<MapInfo> infos) {
	try {
	    JSONObject jo = new JSONObject();
	    jo.put("timestamp", System.currentTimeMillis());
	    JSONArray array = new JSONArray();
	    for (MapInfo info : infos) {
		JSONObject object = new JSONObject();
		object.put("map_id", info.getMap_id());
		object.put("map_name", info.getMap_name());
		array.put(object);
	    }
	    jo.put("data", array);
	    return jo.toString(4);
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return null;
    }

}
