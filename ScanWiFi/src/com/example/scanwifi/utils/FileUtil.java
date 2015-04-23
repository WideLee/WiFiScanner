package com.example.scanwifi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.scanwifi.object.MapInfo;

import android.content.Context;
import android.util.Log;

public class FileUtil {

    public static boolean saveMapInfoToFile(Context context,
	    ArrayList<MapInfo> info, String filename) {
	String data = JsonCreator.createMapInfoJsonFromArray(info);
	if (data != null) {
	    return saveStringInfoToFile(context, data, filename);
	}
	return false;
    }

    public static boolean saveStringInfoToFile(Context context, String info,
	    String filename) {
	try {
	    FileOutputStream outputStream = context.openFileOutput(filename,
		    Context.MODE_PRIVATE);
	    byte[] bytes = info.getBytes();
	    outputStream.write(bytes);
	    outputStream.close();
	    return true;
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return false;
    }

    public static String getStringFromDataFile(Context context, String filename) {
	try {
	    String result = new String();
	    FileInputStream inputStream = context.openFileInput(filename);
	    int length = inputStream.available();
	    byte[] buffer = new byte[length];
	    inputStream.read(buffer);
	    result = EncodingUtils.getString(buffer, "UTF-8");
	    return result;
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static long getTimestampFromFile(Context context, String filename) {
	try {
	    String string = getStringFromDataFile(context, filename);
	    if (string != null) {
		JSONObject jo = new JSONObject(string);
		return jo.getLong("timestamp");
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return 0;
    }

    public static long getMapUpdateTimestamp(Context context, int map_id) {
	File dirFile = context.getDir(ConstantValue.FILE_MAP_DIRECTORY,
		Context.MODE_PRIVATE);
	String dataDir = dirFile.getPath() + "/" + map_id + "/";
	File mapJson = new File(dataDir + "map.json");
	try {
	    FileInputStream inputStream = new FileInputStream(mapJson);
	    int length = inputStream.available();
	    byte[] buffer = new byte[length];
	    inputStream.read(buffer);
	    String result = EncodingUtils.getString(buffer, "UTF-8");
	    inputStream.close();
	    JSONObject jo = new JSONObject(result);
	    long timestamp = jo.getLong("timestamp");
	    return timestamp;
	} catch (JSONException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return 0;
    }

    public static long getCurMapUpdateTimestamp(Context context) {
	MapInfo info = getCurrentMapInfoFromFile(context);
	if (info != null) {
	    int map_id = info.getMap_id();
	    long ts = getMapUpdateTimestamp(context, map_id);
	    return ts;
	}
	return 0;
    }

    public static MapInfo getCurrentMapInfoFromFile(Context context) {
	try {
	    String string = getStringFromDataFile(context,
		    ConstantValue.FILE_LOCAL_MAP_LIST_NAME);
	    if (string != null) {
		JSONObject jo = new JSONObject(string);
		MapInfo info = new MapInfo(jo.getInt("cur_map_id"),
			jo.getString("cur_map_name"));
		return info;
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static boolean setCurrentMapInfo(Context context, MapInfo mapInfo) {
	try {
	    String string = getStringFromDataFile(context,
		    ConstantValue.FILE_LOCAL_MAP_LIST_NAME);
	    if (string != null) {
		JSONObject jo = new JSONObject(string);
		jo.put("cur_map_id", mapInfo.getMap_id());
		jo.put("cur_map_name", mapInfo.getMap_name());
		return saveStringInfoToFile(context, jo.toString(4),
			ConstantValue.FILE_LOCAL_MAP_LIST_NAME);
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return false;
    }

    public static ArrayList<MapInfo> getLocalMapListFromFile(Context context) {
	ArrayList<MapInfo> infos = new ArrayList<MapInfo>();
	try {
	    String string = getStringFromDataFile(context,
		    ConstantValue.FILE_LOCAL_MAP_LIST_NAME);
	    if (string != null) {
		JSONObject jo = new JSONObject(string);
		JSONArray ja = jo.getJSONArray("local_maps");
		for (int i = 0; i < ja.length(); i++) {
		    JSONObject map = ja.getJSONObject(i);
		    int map_id = map.getInt("map_id");
		    String map_name = map.getString("map_name");
		    MapInfo info = new MapInfo(map_id, map_name);
		    infos.add(info);
		}
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return infos;
    }

    public static boolean saveLocalMapListToFile(Context context,
	    ArrayList<MapInfo> infos) {
	try {
	    String string = getStringFromDataFile(context,
		    ConstantValue.FILE_LOCAL_MAP_LIST_NAME);
	    JSONObject jo = new JSONObject();
	    if (string != null) {
		jo = new JSONObject(string);
	    }
	    JSONArray ja = new JSONArray();
	    for (int i = 0; i < infos.size(); i++) {
		JSONObject map = new JSONObject();
		MapInfo info = infos.get(i);
		map.put("map_id", info.getMap_id());
		map.put("map_name", info.getMap_name());
		ja.put(map);
	    }
	    jo.put("local_maps", ja);
	    return saveStringInfoToFile(context, jo.toString(4),
		    ConstantValue.FILE_LOCAL_MAP_LIST_NAME);
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return false;
    }

    public static String getMapNameByMapID(Context context, int map_id) {
	File dirFile = context.getDir(ConstantValue.FILE_MAP_DIRECTORY,
		Context.MODE_PRIVATE);
	String dataDir = dirFile.getPath() + "/" + map_id + "/";
	File mapJson = new File(dataDir + "map.json");
	try {
	    FileInputStream inputStream = new FileInputStream(mapJson);
	    int length = inputStream.available();
	    byte[] buffer = new byte[length];
	    inputStream.read(buffer);
	    String result = EncodingUtils.getString(buffer, "UTF-8");
	    inputStream.close();
	    JSONObject jo = new JSONObject(result);
	    String map_name = jo.getString("map_name");
	    return map_name;
	} catch (JSONException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static String getMapJsonByID(Context context, int map_id) {
	File dirFile = context.getDir(ConstantValue.FILE_MAP_DIRECTORY,
		Context.MODE_PRIVATE);
	String dataDir = dirFile.getPath() + "/" + map_id + "/";
	File mapJson = new File(dataDir + "map.json");
	try {
	    FileInputStream inputStream = new FileInputStream(mapJson);
	    int length = inputStream.available();
	    byte[] buffer = new byte[length];
	    inputStream.read(buffer);
	    String result = EncodingUtils.getString(buffer, "UTF-8");
	    inputStream.close();
	    return result;
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static String getMapImgPathByMapID(Context context, int map_id) {
	File dirFile = context.getDir(ConstantValue.FILE_MAP_DIRECTORY,
		Context.MODE_PRIVATE);
	String dataDir = dirFile.getPath() + "/" + map_id + "/";
	File mapJson = new File(dataDir + "map.json");
	try {
	    FileInputStream inputStream = new FileInputStream(mapJson);
	    int length = inputStream.available();
	    byte[] buffer = new byte[length];
	    inputStream.read(buffer);
	    String result = EncodingUtils.getString(buffer, "UTF-8");
	    inputStream.close();
	    JSONObject jo = new JSONObject(result);
	    String img_path = jo.getString("map_image_path");
	    return img_path;
	} catch (JSONException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * 获�?�response header中Content-Disposition中的filename值
     * 
     * @param response
     * @return
     */
    public static String getFileName(HttpResponse response) {
	Header contentHeader = response.getFirstHeader("Content-Disposition");
	String filename = null;
	if (contentHeader != null) {
	    HeaderElement[] values = contentHeader.getElements();
	    if (values.length == 1) {
		NameValuePair param = values[0].getParameterByName("filename");
		if (param != null) {
		    try {
			filename = param.getValue();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	}
	return filename;
    }

    public static boolean unZip(String zipFilePath, String unzipFilePath) {
	File rootFile = new File(unzipFilePath);
	rootFile.mkdirs();

	ZipInputStream input = null;
	try {
	    Log.v("unzip", "unzip begin");
	    input = new ZipInputStream(new FileInputStream(zipFilePath));
	    ZipEntry entry;
	    while ((entry = input.getNextEntry()) != null) {
		if (entry.isDirectory()) {
		    new File(unzipFilePath + entry.getName()).mkdirs();
		} else {
		    new File(unzipFilePath + entry.getName()).getParentFile()
			    .mkdirs();
		    FileOutputStream fileOutputStream = new FileOutputStream(
			    unzipFilePath + entry.getName());

		    copy(input, fileOutputStream);
		    fileOutputStream.close();
		}
		input.closeEntry();
	    }
	    input.close();
	    Log.v("unzip", "unzip finished");
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    Log.v("unzip", "fileNotFoundException:" + e.getMessage()
		    + " cause:" + e.getCause());
	    return false;
	}
    }

    private static void copy(InputStream inputStream, OutputStream outputStream)
	    throws Exception {
	int length = 0;
	byte[] buffer = new byte[2048];
	while ((length = inputStream.read(buffer)) != -1) {
	    outputStream.write(buffer, 0, length);
	}
	outputStream.flush();
    }

    public static boolean handleNewMapData(Context context, int map_id) {
	File dirFile = context.getDir(ConstantValue.FILE_MAP_DIRECTORY,
		Context.MODE_PRIVATE);
	String dataDir = dirFile.getPath() + "/" + map_id + "/";
	File mapJson = new File(dataDir + "map.json");
	try {
	    FileInputStream inputStream = new FileInputStream(mapJson);
	    int length = inputStream.available();
	    byte[] buffer = new byte[length];
	    inputStream.read(buffer);
	    String result = EncodingUtils.getString(buffer, "UTF-8");
	    inputStream.close();
	    JSONObject jo = new JSONObject(result);
	    jo.put("map_id", map_id);
	    jo.put("map_image_path", dataDir + "map.jpg");
	    jo.put("timestamp", System.currentTimeMillis());
	    String map_name = jo.getString("map_name");
	    String modify = jo.toString(4);
	    FileOutputStream outputStream = new FileOutputStream(mapJson);
	    outputStream.write(modify.getBytes());
	    outputStream.flush();
	    outputStream.close();

	    MapInfo info = new MapInfo(map_id, map_name);
	    ArrayList<MapInfo> infos = getLocalMapListFromFile(context);
	    if (!infos.contains(info)) {
		infos.add(info);
	    }
	    return saveLocalMapListToFile(context, infos);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return false;
    }
}
