# Sinadura Sign Services
Sinadura Sign Services are available via sinaduraCloud.war deployed in a Tomcat container.

## Testing Sinadura Sign Services

Once deployed, you can test if available Sign Services via curl command.

```
curl http://localhost:8080/sinaduraCloud/rest/v1/version/get
``` 
It must return 1.0
