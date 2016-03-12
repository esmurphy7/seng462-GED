#!/bin/bash

installDirectory=/seng/scratch/group4
programName=tomcat

# move the config.json file into the tomcat installation for web server to reference
cp $installDirectory/config.json $installDirectory/$programName/bin

# run the tomcat startup script
$installDirectory/$programName/bin/startup.sh
