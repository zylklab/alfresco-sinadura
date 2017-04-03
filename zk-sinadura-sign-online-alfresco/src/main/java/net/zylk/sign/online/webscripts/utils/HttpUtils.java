package net.zylk.sign.online.webscripts.utils;


import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


public class HttpUtils {

	public static final int TIMEOUT_DEFAULT = 5000; 
	
	
	public static InputStream getHttp(String url) throws MalformedURLException, IOException {
		
//		log.info("getHttp: " + url);
		
		HttpURLConnection con = getHttpURLConnection(url);
		con.setReadTimeout(TIMEOUT_DEFAULT);
		con.setConnectTimeout(TIMEOUT_DEFAULT);
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		con.connect();
		if (con.getResponseCode() / 100 != 2) {
			throw new IOException("HTTP_ERROR::" + con.getResponseMessage() + "::code" + con.getResponseCode());
		}

		InputStream in = con.getInputStream();
//		log.info("getHttp ok ");
		
		return in;
	}

	public static InputStream postFormHttp(String url, Map<String, String> formObjects) throws MalformedURLException, IOException {

//		log.info("postFormHttp: " + url);
		
		// formObjects to URL
		StringBuffer urlParams = new StringBuffer();
		if (formObjects != null && formObjects.size() > 0) {
			for (String formParam : formObjects.keySet()) {
				urlParams.append(formParam).append("=").append(URLEncoder.encode(formObjects.get(formParam), "utf-8")).append("&");
			}
			urlParams.deleteCharAt(urlParams.length() - 1);
		}
		String urlParamsTxt = urlParams.toString();

		HttpURLConnection con = getHttpURLConnection(url);
		con.setReadTimeout(TIMEOUT_DEFAULT);
		con.setConnectTimeout(TIMEOUT_DEFAULT);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Accept", "*");
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Length", "" + Integer.toString(urlParamsTxt.length()));
		con.setDoOutput(true);
		OutputStream out = con.getOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
		dataOut.write(urlParamsTxt.getBytes("utf-8"));
		dataOut.flush();
		dataOut.close();

		if (con.getResponseCode() / 100 != 2) {
			throw new IOException("HTTP_ERROR::" + con.getResponseMessage() + "::code" + con.getResponseCode());
		}

		InputStream is = con.getInputStream();
		
//		log.info("postFormHttp ok ");

		return is;
	}
	

	private static HttpURLConnection getHttpURLConnection(String url) throws MalformedURLException, IOException {
		
		HttpURLConnection uRLConnection = (HttpURLConnection) new URL(url).openConnection();

		return uRLConnection;
	}

}




