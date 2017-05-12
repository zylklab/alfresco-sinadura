package net.zylk.sign.online.webscripts;

import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.http.MediaType;

import java.io.IOException;

/**
 * Created by dani on 12/05/17.
 */
public class GetSinaduraCloudUrlWebscript extends AbstractWebScript {

    private String sinaduraCloudUrl;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

        JSONObject urlJson = new JSONObject();
        urlJson.put("sinaduraCloudUrl",sinaduraCloudUrl);

        String jsonStr = urlJson.toString();
        res.setContentEncoding("UTF-8");
        res.setContentType(MediaType.APPLICATION_JSON.toString());
        res.getWriter().write(jsonStr);

    }


    public void setSinaduraCloudUrl(String sinaduraCloudUrl) {
        this.sinaduraCloudUrl = sinaduraCloudUrl;
    }

}
