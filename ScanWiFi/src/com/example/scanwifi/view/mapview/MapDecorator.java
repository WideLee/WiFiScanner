package com.example.scanwifi.view.mapview;

import java.io.FileInputStream;
import java.util.List;

import com.example.scanwifi.object.Position;

public class MapDecorator {

    private MapView mMapView;

    public MapDecorator() {
    }

    public void setMapView(MapView mapView) {
	mMapView = mapView;
    }

    public float[] transformToViewCoordinate(float[] mapCoordinate) {
	return mMapView.transformToViewCoordinate(mapCoordinate);
    }

    public float[] transformToMapCoordinate(float[] viewCoordinate) {
	return mMapView.transformToMapCoordinate(viewCoordinate);
    }

    public void centerSpecificLocation(Position location) {
	mMapView.centerSpecificLocation(location);
    }

    public void centerSpecificSymbol(BaseMapSymbol mapSymbol) {
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

    public void initNewMap(FileInputStream inputStream) {
	mMapView.initNewMap(inputStream, 1, 0);
    }
}
