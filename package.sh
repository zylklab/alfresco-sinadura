#! /bin/bash
PROJECTS="zk-sinadura-sign-online-alfresco zk-sinadura-sign-online-share"

for i in `echo $PROJECTS`;
 do 
   echo "Generating JAR for $i"
   (cd $i; ant clean; ant package)
   # (cd $i; mvn clean -Ppurge; mvn package -DskipTests=true) 
 done 
