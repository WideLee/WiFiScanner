package com.example.scanwifi.utils;

import android.content.Context;

public class LanguageAdapter {
    public final static int LANGUAGE_ZH_CN = 1;
    public final static int LANGUAGE_ZH_HK = 2;
    public final static int LANGUAGE_EN = 0;
    private Context mContext;
    private int mCurrentLanguage;
    private static LanguageAdapter mInstance;

    private LanguageAdapter(Context context) {
	PreferenceUtils.initPreference(context);
	mCurrentLanguage = PreferenceUtils.getIntValue(context,
		PreferenceUtils.KEY_LANGUAGE);

	mContext = context;
    }

    public static LanguageAdapter getInstance(Context context) {
	if (mInstance == null)
	    mInstance = new LanguageAdapter(context);
	return mInstance;
    }

    public void setCurrentLanguage(int language) {
	PreferenceUtils.saveIntValue(mContext, PreferenceUtils.KEY_LANGUAGE,
		language);
	mCurrentLanguage = language;
    }

    public int getIdWithLanguageAdaptation(String src, String fileName) {
	String fullFileName = getFullFileName(fileName);
	return mContext.getResources().getIdentifier(fullFileName, src,
		mContext.getPackageName());
    }

    public int getLanguage() {
	return mCurrentLanguage;
    }

    public String getLanguageString() {
	String result = new String();
	if (mCurrentLanguage == LANGUAGE_ZH_CN) {
	    result = "简体中文";
	} else if (mCurrentLanguage == LANGUAGE_ZH_HK) {
	    result = "�?體中文";
	} else if (mCurrentLanguage == LANGUAGE_EN) {
	    result = "English";
	}
	return result;
    }

    private String getFullFileName(String fileName) {
	int point = fileName.indexOf('.');
	String suffix = null;
	if (mCurrentLanguage == LANGUAGE_ZH_CN) {
	    suffix = "_zh";
	} else if (mCurrentLanguage == LANGUAGE_ZH_HK) {
	    suffix = "_zh_hk";
	} else if (mCurrentLanguage == LANGUAGE_EN) {
	    suffix = "_en";
	}
	if (point == -1) {
	    return fileName + suffix;
	} else {
	    suffix += ".";
	    return fileName.replace(".", suffix);
	}
    }

    public String getString(String fileName) {
	return mContext.getResources().getString(
		getIdWithLanguageAdaptation("string", fileName));
    }

}
