package com.example.scanwifi.utils;

public class ConstantValue {

    public static final int BEACON_SCAN_PERIOD = 1000;
    public static final int CHECK_BEACON_TTL_PERIOD = 5000;
    public static final int BEACON_TTL = 20000;

    public static final int BLE_NOT_SUPPORT = -1;
    public static final int BLE_NOT_ENABLE = 0;
    public static final int BLE_ENABLE = 1;

    public final static float MAP_MIN_SCALE = 0.4f;
    public final static float MAP_MAX_SCALE = 2f;
    public final static double RSSI_THRESHOLD = -80;

    public static final int MYLOCATION_CIRCLE_COLOR = 0xFFFF0000;
    public static final int NORMAL_CIRCLE_EDGE_COLOR = 0xFFFFFFFF;
    public static final int MYLOCATION_RANGE_COLOR = 0x1EFF0000;
    public static final float MYLOCATION_RADIUS_TO_SCREEN = 64;
    public static final float MYLOCATION_RANGE_RADIUS_TO_SCREEN = 8;

    public static final int ADVVERTISING_THREADHOLD = 240;

    public static final String EXTERNAL_SD_DIR_NAME = "/BLELocate/";
    public static final String FILE_BEACON_INFO_NAME = "beacon_info.json";
    public static final String FILE_AD_INFO_NAME = "ad_info.json";
    public static final String FILE_LOCAL_MAP_LIST_NAME = "local_map_list.json";
    public static final String FILE_MAP_DIRECTORY = "maps";

    public static final String LOCATION_RESULT_URL = "http://143.89.145.138/app/Hello/index/get_position";
    public static final String SERVER_IP = "http://123.57.34.195";
    public static final String URL_GER_ABOUT_MESSAGE = SERVER_IP
	    + "/beacon/home/client/get_about_message";
    public static final String URL_GET_BEACON_MESSAGE = SERVER_IP
	    + "/beacon/home/client/get_all_beacon_info";
    public static final String URL_GET_AD_MESSAGE = SERVER_IP
	    + "/beacon/home/client/get_all_ad_info";
    public static final String URL_GET_MAP_LIST_MESSAGE = SERVER_IP
	    + "/beacon/home/client/get_map_list";
    public static final String URL_DOWNLOAD_MAP = SERVER_IP
	    + "/beacon/home/client/download_map";

    public static final int MSG_SERVICE_REMOTE_HELLO = 0x7fffffff;
    public static final int MSG_SERVICE_REMOTE_UPDATE_INFOMATION = 0x7ffffffc;

    public static final int MSG_SERVICE_LOCAL_BEACON_RESULT = 0x6fffffff;
    public static final int MSG_SERVICE_LOCAL_LOCATE_RESULT = 0x6ffffffe;

    public static final int MSG_ACTIVITY_REMOTE_BEACON_RESULT = 0x4fffffff;
    public static final int MSG_ACTIVITY_REMOTE_LOCATE_RESULT = 0x4ffffffe;

    public static final int MSG_SETTING_GET_MESSAGE_FAIL = 0x2fffffff;
    public static final int MSG_SETTING_GET_MESSAGE_SUCCESS = 0x2ffffffe;
    public static final int MSG_ABOUT_MESSAGE = 0x2ffffffd;
    public static final int MSG_BEACON_MESSAGE = 0x2ffffffc;
    public static final int MSG_AD_MESSAGE = 0x2ffffffb;
    public static final int MSG_MAP_MESSAGE = 0x2ffffffa;
    public static final int MSG_MAP_LIST_MESSAGE = 0x2ffffff9;
    public static final int MSG_SETTING_MAP_LIST_SELECTED = 0x2ffffff8;
    public static final int MSG_SETTING_DOWNLOAD_MAP_FAIL = 0x2ffffff7;
    public static final int MSG_SETTING_DOWNLOAD_MAP_SUCCESS = 0x2ffffff6;
    public static final int MSG_SETTING_DOWNLOAD_MAP_ING = 0x2ffffff5;
    public static final int MSG_SETTING_MAP_INFO_PREPARED = 0x2ffffff4;
    public static final int MSG_SETTING_UPDATE_LANGUAGE = 0x2fffff3;

    public static final int ACTION_REQUEST_ENABLE_BLE = 0x00000000;
    public static final int ACTION_UPDATE_DATA = 0x00000001;
}
