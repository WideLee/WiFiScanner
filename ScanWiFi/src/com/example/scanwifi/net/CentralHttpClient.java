package com.example.scanwifi.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class CentralHttpClient {
    public static final String CHARSET = HTTP.UTF_8;
    private static final String UA = "Mozilla/5.0 (Linux; Android 4.0.4; "
	    + "Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 "
	    + "(KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19";
    private static HttpClient mCentralHttpClient;

    private CentralHttpClient() {
    }

    public static synchronized HttpClient getHttpClient() {
	if (mCentralHttpClient == null) {
	    HttpParams params = new BasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, CHARSET);
	    HttpProtocolParams.setUseExpectContinue(params, true);
	    HttpProtocolParams.setUserAgent(params, UA);

	    ConnManagerParams.setTimeout(params, 1000);
	    HttpConnectionParams.setConnectionTimeout(params, 2000);
	    HttpConnectionParams.setSoTimeout(params, 4000);

	    SchemeRegistry schemeRegistry = new SchemeRegistry();
	    schemeRegistry.register(new Scheme("http", PlainSocketFactory
		    .getSocketFactory(), 80));
	    ClientConnectionManager conManager = new ThreadSafeClientConnManager(
		    params, schemeRegistry);
	    mCentralHttpClient = new DefaultHttpClient(conManager, params);
	    mCentralHttpClient.getParams().setParameter(
		    CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
	}
	return mCentralHttpClient;
    }

    public static String post(String url, NameValuePair... params) {

	List<NameValuePair> paramList = new ArrayList<NameValuePair>();
	for (NameValuePair pair : params) {
	    paramList.add(pair);
	}

	try {
	    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList,
		    CHARSET);
	    HttpPost request = new HttpPost(url);
	    request.setEntity(entity);

	    HttpClient client = getHttpClient();
	    HttpResponse response = client.execute(request);
	    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		throw new RuntimeException("Request Fail!");
	    }
	    HttpEntity result = response.getEntity();
	    return (result == null) ? null : EntityUtils.toString(result,
		    CHARSET);
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return null;
    }
}
