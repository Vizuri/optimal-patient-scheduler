#!/bin/sh


export WEB_APP_NAME="scheduler-webapp"

#export JBOSS_HOME="/usr/local/jboss/jboss-eap-7.0.3-brms-6.4"

echo "do: $1"]
echo "JBOSS_HOME is $JBOSS_HOME"
if [ -z "$JBOSS_HOME" ]; then
    export JBOSS_HOME="/usr/local/jboss/jboss-eap-7.0.3-brms-6.4"
    echo "Will default to JBOSS_HOME: $JBOSS_HOME"
fi

if [ "$1" = "build"  ]; then
    mvn clean package -DskipTests
    rm -rf $JBOSS_HOME/standalone/deployments/${WEB_APP_NAME}.*
    eval "./fast_deploy.sh copy"
    eval "./fast_deploy.sh deploy"
fi

if [ "$1" = "copy" ]; then
	# script to copy static content to jboss expanded war folder
    cp -r ../${WEB_APP_NAME}/src/main/webapp/ $JBOSS_HOME/standalone/deployments/${WEB_APP_NAME}.war
	echo "copied"
elif [ "$1" = "deploy" ]; then

    rm -rf $JBOSS_HOME/standalone/deployments/${WEB_APP_NAME}.war/WEB-INF
	cp -r ../${WEB_APP_NAME}/target/${WEB_APP_NAME}/WEB-INF/ $JBOSS_HOME/standalone/deployments/${WEB_APP_NAME}.war/WEB-INF
	# to redeploy everything after static copy (use this if you modified any rest services or other java code
	touch $JBOSS_HOME/standalone/deployments/${WEB_APP_NAME}.war.dodeploy
	echo "deployed" 
fi

#$JBOSS_HOME

