package com.example.scanwifi.object;

public class Position {
    private float x;
    private float y;

    public Position(float d, float e) {
	x = d;
	y = e;
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
}
