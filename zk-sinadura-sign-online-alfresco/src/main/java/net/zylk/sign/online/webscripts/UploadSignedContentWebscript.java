package net.zylk.sign.online.webscripts;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import net.zylk.sign.online.webscripts.utils.HttpUtils;
import net.zylk.sign.online.webscripts.utils.PropertiesManager;

public class UploadSignedContentWebscript extends AbstractWebScript {

	private static Log logger = LogFactory.getLog(UploadSignedContentWebscript.class);

	private ServiceRegistry registry;
	private PropertiesManager propertiesManager;

	private final static QName PROP_FIRMADO = QName.createQName("{zylk.sign.model}firmado");
	private final static QName ASSOC_SIGNED_BY = QName.createQName("{zylk.sign.model}signedBy");
	
	
	@Override	
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		String nodeRefparam = req.getParameter("nodeRef");		
		String token = req.getParameter("token");
		logger.debug("token: " + token);
		logger.debug("nodeRefparam: " + nodeRefparam);

		String serverUrl = propertiesManager.getSignSinaduraCloudUrl();
		String apiUrl =  serverUrl + "/rest/v1";
		
		String getSignatureUrl =  apiUrl + "/transactions/{transaction-id}/signaturefile/get?idDocument={id-document}";
		getSignatureUrl = getSignatureUrl.replace("{transaction-id}", token);
		getSignatureUrl = getSignatureUrl.replace("{id-document}", nodeRefparam);
		
		logger.debug("getSignatureUrl: " + getSignatureUrl);
		
		InputStream isContent = HttpUtils.getHttp(getSignatureUrl);
		
		NodeRef nodeRef = new NodeRef(nodeRefparam);
		
		ContentReader nodeContent = getReader(registry, nodeRef);
		String mimetype = nodeContent.getMimetype();
		
		if (mimetype.equals(MimetypeMap.MIMETYPE_PDF)) {
			
			if (propertiesManager.getSignPdfSignatureType().equals("PDF")) {
				savePdfSignature(nodeRef, isContent);
			} else {
				saveXadesDetachedSignature(nodeRef, isContent);
			}
			
		} else {
			saveXadesDetachedSignature(nodeRef, isContent);
		}
		
//		String removeTsUrl =  apiUrl + "/transactions/{transaction-id}/remove";
//		removeTsUrl = removeTsUrl.replace("{transaction-id}", token);
//		HttpUtils.postFormHttp(removeTsUrl, null);
	}
	
	private static ContentReader getReader(ServiceRegistry registry, NodeRef nodeRef) {
		
		// First check that the node is a sub-type of content
		QName typeQName = registry.getNodeService().getType(nodeRef);
		if (registry.getDictionaryService().isSubClass(typeQName, ContentModel.TYPE_CONTENT) == false) {
			// it is not content, so can't transform
			return null;
		}
		// Get the content reader
		ContentReader contentReader = registry.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);

		return contentReader;
	}

	
	private void savePdfSignature(NodeRef nodeRef, InputStream isContent) {

		ContentWriter writer = registry.getContentService().getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
		writer.putContent(isContent);

		if (!registry.getNodeService().hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE)) {
			registry.getNodeService().addAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE, null);
		}

		registry.getNodeService().setProperty(nodeRef, PROP_FIRMADO, true);
	}
	
	
	private void saveXadesDetachedSignature(NodeRef nodeRef, InputStream isContent) throws IOException {
		
		String documentName = (String)registry.getNodeService().getProperties(nodeRef).get(ContentModel.PROP_NAME);
		
		ChildAssociationRef childAssociationRef = registry.getNodeService().getPrimaryParent(nodeRef);
		
		NodeRef folderNodeRef;
		if (propertiesManager.getSignUploadPathRelative().equals("true")) {
			folderNodeRef = childAssociationRef.getParentRef();
		} else {			
			folderNodeRef = new NodeRef(propertiesManager.getSignUploadPathNode());
		}

		logger.debug("folderNodeRef: " + folderNodeRef);

		// create new content node
		String name = documentName + "-" + System.currentTimeMillis() + ".xml";
//		String title = documentNameWithoutExtension + " " + System.currentTimeMillis();
//		String description = "Esta firma se ha generado mediante el modulo ZK Sign Online";
		Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>();
		contentProps.put(ContentModel.PROP_NAME, name);

		// create content node
		NodeService nodeService = registry.getNodeService();
		ChildAssociationRef association = nodeService.createNode(folderNodeRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), ContentModel.TYPE_CONTENT, contentProps);
		NodeRef signatureNodeRef = association.getChildRef();

		// add titled aspect (for Web Client display)
//		Map<QName, Serializable> titledProps = new HashMap<QName, Serializable>();
//		titledProps.put(ContentModel.PROP_TITLE, title);
//		titledProps.put(ContentModel.PROP_DESCRIPTION, description);
//		nodeService.addAspect(signatureNodeRef, ContentModel.ASPECT_TITLED, titledProps);

		// write content to new node
		ContentService contentService = registry.getContentService();
		ContentWriter writer = contentService.getWriter(signatureNodeRef, ContentModel.PROP_CONTENT, true);
		
		// pruebas con texto plano
//		writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
//		writer.setEncoding("UTF-8");
//		String text = "The quick brown fox jumps over the lazy dog";
//		writer.putContent(text);

		// save contenido de la firma
		writer.putContent(isContent);
		
		// crear la asociacion
		nodeService.createAssociation(nodeRef, signatureNodeRef, ASSOC_SIGNED_BY);
		
		registry.getNodeService().setProperty(nodeRef, PROP_FIRMADO, true);
	}
	

	public ServiceRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(ServiceRegistry registry) {
		this.registry = registry;
	}

	public void setPropertiesManager(PropertiesManager propertiesManager) {
		this.propertiesManager = propertiesManager;
	}

}