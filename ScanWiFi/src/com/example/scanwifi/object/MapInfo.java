package com.example.scanwifi.object;

public class MapInfo {
    private int map_id;
    private String map_name;

    public MapInfo(int _map_id, String _map_name) {
	map_id = _map_id;
	map_name = _map_name;
    }

    public MapInfo() {

    }

    public int getMap_id() {
	return map_id;
    }

    public void setMap_id(int map_id) {
	this.map_id = map_id;
    }

    public String getMap_name() {
	return map_name;
    }

    public void setMap_name(String map_name) {
	this.map_name = map_name;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("map_id: " + map_id + "\n");
	builder.append("map_name: " + map_name + "\n");
	return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof MapInfo) {
	    MapInfo other = (MapInfo) o;
	    return getMap_id() == other.getMap_id();
	}
	return super.equals(o);
    }
}
