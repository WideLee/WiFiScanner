package com.example.scanwifi.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.scanwifi.object.FloorMap;
import com.example.scanwifi.object.MapInfo;

public class JsonParser {
    private static String ERROR_MESSAGE = new String();

    public static ArrayList<MapInfo> parseMapInfoResultJson(String json) {
	try {
	    JSONObject object = new JSONObject(json);
	    int result = object.getInt("result");
	    if (result == 0) {
		JSONArray array = object.getJSONArray("data");
		return parseMapInfoArray(array);
	    } else {
		ERROR_MESSAGE = object.getString("info");
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	    ERROR_MESSAGE = e.getMessage();
	}
	return null;
    }

    public static String parseResultMessageJson(String json) {
	try {
	    JSONObject object = new JSONObject(json);
	    return object.getString("info");
	} catch (JSONException e) {
	    e.printStackTrace();
	    ERROR_MESSAGE = e.getMessage();
	}
	return null;
    }

    public static ArrayList<MapInfo> parseMapInfoArray(JSONArray ja) {
	try {
	    ArrayList<MapInfo> mapInfos = new ArrayList<MapInfo>();
	    for (int i = 0; i < ja.length(); i++) {
		MapInfo info = parseMapInfoJson(ja.getJSONObject(i));
		if (info != null) {
		    mapInfos.add(info);
		}
	    }
	    return mapInfos;
	} catch (JSONException e) {
	    e.printStackTrace();
	    ERROR_MESSAGE = e.getMessage();
	}
	return null;
    }

    public static MapInfo parseMapInfoJson(JSONObject jo) {
	try {
	    int map_id = jo.getInt("map_id");
	    String map_name = jo.getString("map_name");
	    MapInfo info = new MapInfo(map_id, map_name);
	    return info;
	} catch (JSONException e) {
	    e.printStackTrace();
	    ERROR_MESSAGE = e.getMessage();
	}
	return null;
    }

    public static FloorMap parseMapFloorJson(String data) {
	try {
	    JSONObject jo = new JSONObject(data);
	    int map_id = jo.getInt("map_id");
	    String map_name = jo.getString("map_name");
	    String map_image_path = jo.getString("map_image_path");
	    double scale_rate = jo.getDouble("scale_rate");
	    double init_scale = jo.getDouble("init_scale");
	    double init_rotation = jo.getDouble("init_rotation");
	    return new FloorMap(map_id, map_name, map_image_path, scale_rate,
		    init_scale, init_rotation);
	} catch (JSONException e) {
	    e.printStackTrace();
	    ERROR_MESSAGE = e.getMessage();
	}
	return null;
    }

    public static String getErrorMessage() {
	return ERROR_MESSAGE;
    }
}
