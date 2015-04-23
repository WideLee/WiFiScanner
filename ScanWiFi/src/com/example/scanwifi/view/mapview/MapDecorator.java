package com.example.scanwifi.view.mapview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;

import com.example.scanwifi.object.FloorMap;
import com.example.scanwifi.object.Position;

public class MapDecorator {

    private HashMap<Integer, FloorMap> mFloorMaps;
    private MapView mMapView;

    @SuppressLint("UseSparseArrays")
    public MapDecorator() {
	mFloorMaps = new HashMap<Integer, FloorMap>();
    }

    public HashMap<Integer, FloorMap> getmFloorMaps() {
	return mFloorMaps;
    }

    public void setmFloorMaps(HashMap<Integer, FloorMap> mFloorMaps) {
	this.mFloorMaps = mFloorMaps;
    }

    public void addFloorMap(FloorMap map) {
	mFloorMaps.put(map.getInfo().getMap_id(), map);
    }

    public List<FloorMap> getFloorList() {
	Iterator<FloorMap> iterator = mFloorMaps.values().iterator();
	List<FloorMap> result = new ArrayList<FloorMap>();
	while (iterator.hasNext()) {
	    result.add(iterator.next());
	}
	return result;
    }

    public void setMapView(MapView mapView) {
	mMapView = mapView;
    }

    public void changeFloor(FloorMap floor) {
	mMapView.changeFloor(floor);
    }

    public float[] transformToViewCoordinate(float[] mapCoordinate) {
	return mMapView.transformToViewCoordinate(mapCoordinate);
    }

    public float[] transformToMapCoordinate(float[] viewCoordinate) {
	return mMapView.transformToMapCoordinate(viewCoordinate);
    }

    public void centerSpecificLocation(Position location) {
	if (location.getMap_id() != mMapView.getFloorId()) {
	    changeFloor(mFloorMaps.get(location.getMap_id()));
	}
	mMapView.centerSpecificLocation(location);
    }

    public void centerSpecificSymbol(BaseMapSymbol mapSymbol) {
	if (mapSymbol.getLocation().getMap_id() != mMapView.getFloorId()) {
	    changeFloor(mFloorMaps.get(mapSymbol.getLocation().getMap_id()));
	}
	mMapView.centerSpecificLocation(mapSymbol.getLocation());
    }

    public long getCurrentMapFloorId() {
	return mMapView.getFloorId();
    }

    public void updateMyLocation(Position location) {
	mMapView.updateMyLocation(location);
    }

    public boolean removeMapSymbol(BaseMapSymbol mapSymbol) {
	return mMapView.getMapSymbols().remove(mapSymbol);
    }

    public void clearMapSymbols() {
	mMapView.getMapSymbols().clear();
    }

    public void reDrawMap() {
	mMapView.invalidate();
    }

    public List<BaseMapSymbol> getMapSymbols() {
	return mMapView.getMapSymbols();
    }

    public boolean trackPosition() {
	if (mMapView.getmMyLocationSymbol().getLocation() != null) {
	    mMapView.centerMyLocation();
	}
	mMapView.setTrackPosition();
	return mMapView.getmMyLocationSymbol().getLocation() != null;
    }
}
