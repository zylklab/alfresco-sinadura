package net.zylk.sign.online.webscripts;

import org.alfresco.service.ServiceRegistry;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;

/**
 * Created by dani on 12/05/17.
 */
public class GetSinaduraCloudParametersWebscript extends AbstractWebScript {

    protected ServiceRegistry registry;
    protected String sinaduraCloudUrl;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        JSONObject obj = new JSONObject();

        String ticket = registry.getAuthenticationService().getCurrentTicket();
        obj.put("ticket", ticket);
        obj.put("sinaduraCloudUrl",sinaduraCloudUrl);

        String jsonString = obj.toString();
        res.getWriter().write(jsonString);
    }


    public void setRegistry(ServiceRegistry registry) {
        this.registry = registry;
    }

    public void setSinaduraCloudUrl(String sinaduraCloudUrl) {
        this.sinaduraCloudUrl = sinaduraCloudUrl;
    }
}
