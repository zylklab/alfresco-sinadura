package net.zylk.sign.online.webscripts.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {

	// valores: PDF o XADES
	public static final String SIGN_PDF_SIGNATURE_TYPE = "zk.sign.pdf.signature.type";
	public static final String SIGN_UPLOAD_PATH_RELATIVE = "zk.sign.upload.path.relative";
	public static final String SIGN_UPLOAD_PATH_NODE = "zk.sign.upload.path.node";
	public static final String SIGN_SINADURA_CLOUD_URL = "zk.sign.sinadura.cloud.url";
	
	private static final String CONFIG_FILENAME = "alfresco/extension/zk-sign-online.properties";
	private static Properties properties = null;

	
	public static String getProperty(String property) throws IOException {

		String value = getProperties().getProperty(property);

		return value;
	}

	private static Properties getProperties() throws IOException {

		if (properties == null) {
			
			properties = new Properties();
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			InputStream is = cl.getResourceAsStream(CONFIG_FILENAME);
			properties.load(is);
		}

		return properties;
	}

}