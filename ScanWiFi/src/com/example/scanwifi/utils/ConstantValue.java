package com.example.scanwifi.utils;

public class ConstantValue {

    public final static float MAP_MIN_SCALE = 0.4f;
    public final static float MAP_MAX_SCALE = 2f;

    public static final int MYLOCATION_CIRCLE_COLOR = 0xFFFF0000;
    public static final int NORMAL_CIRCLE_EDGE_COLOR = 0xFFFFFFFF;
    public static final int MYLOCATION_RANGE_COLOR = 0x1EFF0000;
    public static final float MYLOCATION_RADIUS_TO_SCREEN = 64;
    public static final float MYLOCATION_RANGE_RADIUS_TO_SCREEN = 8;

    public static final String LOCATION_RESULT_URL = "http://192.168.1.10:8080/apbl-websrv/getLoc.action";

    public static final int MSG_ACTIVITY_REMOTE_LOCATE_RESULT = 0x4ffffffe;
    public static final int MSG_ACTIVITY_LOCAL_CHANGE_MAP = 0x3fffffff;
}
