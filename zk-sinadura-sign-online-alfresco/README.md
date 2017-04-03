# Sinadura 5 for Alfresco ECM 

Protocol based digital signatures for Alfresco

## Packaging repo AMP

```
git clone https://github.com/zylklab/alfresco-sinadura/edit/master/zk-sinadura-sign-online-alfresco
cd zk-sinadura-sign-online-alfresco
mvn clean -Ppurge
mvn package -DskipTests=true
```

HINT: There is also a build.xml file (ant clean && ant package)


## Packaging Share AMP

```
git clone https://github.com/zylklab/alfresco-sinadura/edit/master/zk-sinadura-sign-online-share
cd zk-sinadura-sign-online-share
mvn clean -Ppurge
mvn package -DskipTests=true
```

HINT: There is also a build.xml file (ant clean && ant package)

AMP files should be packaged in target directories if everything goes ok.

## Installing 

Copy the corresponding files in $ALF_HOME/amps and $ALF_HOME/amps_share and apply $ALF_HOME/bin/apply_amps.sh script (with Alfresco stopped). Then restart Alfresco service
