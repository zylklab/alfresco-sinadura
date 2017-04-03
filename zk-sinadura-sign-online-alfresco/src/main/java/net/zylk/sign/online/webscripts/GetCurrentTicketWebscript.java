package net.zylk.sign.online.webscripts;

import java.io.IOException;

import org.alfresco.service.ServiceRegistry;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class GetCurrentTicketWebscript extends AbstractWebScript {

	private ServiceRegistry registry;

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		JSONObject obj = new JSONObject();

		String ticket = registry.getAuthenticationService().getCurrentTicket();
		obj.put("ticket", ticket);

		String jsonString = obj.toString();
		res.getWriter().write(jsonString);
	}

	public ServiceRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(ServiceRegistry registry) {
		this.registry = registry;
	}
}
