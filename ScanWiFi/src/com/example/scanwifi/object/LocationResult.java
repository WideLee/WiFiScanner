package com.example.scanwifi.object;

public class LocationResult {
    private long time;
    private int x;
    private int y;
    private boolean isSuccess;

    public long getTime() {
	return time;
    }

    public void setTime(long time) {
	this.time = time;
    }

    public int getX() {
	return x;
    }

    public void setX(int x) {
	this.x = x;
    }

    public int getY() {
	return y;
    }

    public void setY(int y) {
	this.y = y;
    }

    public boolean isSuccess() {
	return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
	this.isSuccess = isSuccess;
    }
}
