package com.example.scanwifi.object;

public class Position {
    private float x;
    private float y;
    private int map_id;

    public Position(float d, float e, int _map_id) {
	x = d;
	y = e;
	map_id = _map_id;
    }

    public float getX() {
	return x;
    }

    public void setX(float x) {
	this.x = x;
    }

    public float getY() {
	return y;
    }

    public void setY(float y) {
	this.y = y;
    }

    public int getMap_id() {
	return map_id;
    }

    public void setMap_id(int map_id) {
	this.map_id = map_id;
    }

}
