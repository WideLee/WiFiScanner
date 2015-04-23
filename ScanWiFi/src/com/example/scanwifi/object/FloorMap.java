package com.example.scanwifi.object;

public class FloorMap {
    private MapInfo info;
    private String map_img_path;
    private double scale_rate;
    private double init_scale;
    private double init_rotation;

    public FloorMap(int _id, String _name, String _path, double _scale_rate,
	    double _init_scale, double _init_rotation) {
	info = new MapInfo(_id, _name);
	map_img_path = _path;
	scale_rate = _scale_rate;
	init_scale = _init_scale;
	init_rotation = _init_rotation;
    }

    public FloorMap() {
    }

    public MapInfo getInfo() {
	return info;
    }

    public void setInfo(MapInfo info) {
	this.info = info;
    }

    public String getMap_img_path() {
	return map_img_path;
    }

    public void setMap_img_path(String map_img_path) {
	this.map_img_path = map_img_path;
    }

    public double getScale_rate() {
	return scale_rate;
    }

    public void setScale_rate(double scale_rate) {
	this.scale_rate = scale_rate;
    }

    public double getInit_scale() {
	return init_scale;
    }

    public void setInit_scale(double init_scale) {
	this.init_scale = init_scale;
    }

    public double getInit_rotation() {
	return init_rotation;
    }

    public void setInit_rotation(double init_rotation) {
	this.init_rotation = init_rotation;
    }

}
