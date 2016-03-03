#!/bin/bash

installDirectory=/seng/scratch/group4
programName=nginx

if [ -d "$installDirectory/$programName" ]; then

    echo "Configuring $programName"
    
    # Move config file to program directory
    mv $installDirectory/nginx.conf.loadbalancer $installDirectory/$programName/conf/
    
    echo "$programName configured"
    
else
    
    echo "$programName not installed yet"
    
fi
