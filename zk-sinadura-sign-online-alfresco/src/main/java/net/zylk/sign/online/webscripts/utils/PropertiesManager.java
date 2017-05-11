package net.zylk.sign.online.webscripts.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {

	// valores: PDF o XADES
	private String signPdfSignatureType;
	private String signUploadPathRelative;
	private String signUploadPathNode;
	private String signSinaduraCloudUrl;

	public String getSignPdfSignatureType() {
		return signPdfSignatureType;
	}

	public String getSignUploadPathRelative() {
		return signUploadPathRelative;
	}

	public String getSignUploadPathNode() {
		return signUploadPathNode;
	}

	public String getSignSinaduraCloudUrl() {
		return signSinaduraCloudUrl;
	}

	public void setSignPdfSignatureType(String signPdfSignatureType) {
		this.signPdfSignatureType = signPdfSignatureType;
	}

	public void setSignUploadPathRelative(String signUploadPathRelative) {
		this.signUploadPathRelative = signUploadPathRelative;
	}

	public void setSignUploadPathNode(String signUploadPathNode) {
		this.signUploadPathNode = signUploadPathNode;
	}

	public void setSignSinaduraCloudUrl(String signSinaduraCloudUrl) {
		this.signSinaduraCloudUrl = signSinaduraCloudUrl;
	}
}