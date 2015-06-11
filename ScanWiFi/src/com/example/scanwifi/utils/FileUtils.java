package com.example.scanwifi.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;

public class FileUtils {

    public static final String[] okFileExtensions = new String[] { "jpg",
	    "png", "gif", "jpeg" };

    public static String[] getMapfileList(Context mContext) {
	String dirPath = getAppDir();
	File dir = new File(dirPath);
	if (!dir.exists()) {
	    dir.mkdirs();
	}
	return dir.list(new FilenameFilter() {

	    @Override
	    public boolean accept(File dir, String filename) {
		for (String extension : okFileExtensions) {
		    if (filename.toLowerCase(Locale.CHINA).endsWith(extension)) {
			return true;
		    }
		}
		return false;
	    }
	});
    }

    public static String getAppDir() {
	return Environment.getExternalStorageDirectory() + "/ScanWiFi/";
    }

}
