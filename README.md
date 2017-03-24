# Sinadura 5 for Alfresco ECM

Protocol based digital signatures for Alfresco

## Intro

Sinadura 5 for Alfresco is an addon for digital signature in Alfresco 5. The addon provides:

- Actions for signing one or several files in Alfresco Share client.
- Previews of the digital signatures in Alfresco Share Preview (for PDF only).
- The possibility of signing any type of file. Pades signatures are used for PDF files and Xades dettached for the other mimetypes.
- Advanced signature properties, such as timestamps or digital signature validations.
- Digital signature profiles, depending on your corporate certificates using custom images for signature stamps (for PDF only). You can - also place these visible stamps in a customized position of the document.

## Screenshots

TODO

## Components
 
- Sinadura 5 Desktop (in your local machine): download desktop client from www.sinadura.net
- Sinadura Cloud Services (in Alfresco server)
- Alfresco AMPs (in Alfresco server) 

The installers of Sinadura 5 are available for Linux and Windows, and they include the installation of sinadura protocol in the operating system. The sinadura protocol works via a third party service (sinaduraCloud.war) installed in a servlet container that can be part of your Alfresco installation. Also, two AMP files are needed for Alfresco repository and Alfresco Share respectively. Another two AMPs are available for the validation of the digital signatures and the visualization of properties in Alfresco Share.

## Installation

In the client:

- Download sinadura 5 Desktop (for Windows or Linux)
- Install your software certificates or smartcards in your OS 
- Check that Sinadura Desktop is able to sign locally 

In the server:

- Install AMPs in Alfresco repo and Alfresco Share
- Copy sinaduraCloud.war into $ALF_HOME/tomcat/webapps
- Restart Alfresco service

## Configuration

In Share AMP you have to change zylk.lib.ftl for pointing to your corresponding Sinadura services url. For example, if you deploy sinaduraCloud.war in the tomcat servers with alfresco.war 

```
function getSinaduraServicesUrl() {
	return "http://<alfresco-host-frontend-url>/sinaduraCloud";
}
```

### Cluster mode

By the moment, in a clustered setup we need to configure a hot standby (active-passive). For example with an Apache frontend:

```
<VirtualHost *:8080>
	ServerName alfserver

	ErrorLog /var/log/apache2/alf-error.log
	CustomLog /var/log/apache2/alf-access.log combined
	
	<Proxy balancer://sinaduraCloud>
		BalancerMember ajp://alfnode2:8009 retry=30
		# the hot standby
		BalancerMember ajp://alfnode1:8009 status=+H retry=0
	</Proxy>

	ProxyPass /sinaduraCloud balancer://sinaduraCloud/sinaduraCloud lbmethod=byrequests stickysession=JSESSIONID|jsessionid
	ProxyPassReverse /sinaduraCloud ajp://alfnode1:8009/sinaduraCloud
	ProxyPassReverse /sinaduraCloud ajp://alfnode2:8009/sinaduraCloud
	ProxyPass / ajp://alfnode1:8009/
	ProxyPassReverse / ajp://alfnode2:8009/
</VirtualHost>
```

Please consider contribute to Sinadura for an improved cluster mode or new types of signature compatibility.

## TODO

- Make sinaduraCloud services endpoint configurable via properties or config file
- Change signature indicators
- Clustering mode
- Documentation
- Test

## Target setup 

- Sinadura 5 Desktop 
- Alfresco 201602GA
- Alfresco 5.0.3
- Alfresco 5.1.2

## Contributors
- Alfredo Sanchez
- Cesar Capillas

## Links
- www.sinadura.net
- http://www.zylk.net/es/web-2-0/blog/-/blogs/sinadura-5-for-alfresco
