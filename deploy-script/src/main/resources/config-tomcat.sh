#!/bin/bash

installDirectory=/seng/scratch/group4
programName=tomcat

if [ -d "$installDirectory/$programName" ]; then

    echo "Configuring $programName"
    
    # Move config file to program directory
    mv $installDirectory/server.xml $installDirectory/$programName/conf/

    # Remove existing WAR file and exploded WAR directory
    rm -r $installDirectory/$programName/webapps/ROOT
    rm $installDirectory/$programName/webapps/ROOT.war

    # Move new WAR to webapps
    mv $installDirectory/ROOT.war $installDirectory/$programName/webapps/
    
    echo "$programName configured"
    
else
    
    echo "$programName not installed yet"
    
fi
