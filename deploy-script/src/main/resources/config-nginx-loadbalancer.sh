#!/bin/bash

installDirectory=/seng/scratch/group4
programName=nginx

if [ -d "$installDirectory/$programName" ]; then

    echo "Configuring $programName"
    
    # Move config file to program directory
    mv $installDirectory/nginx.conf.loadbalancer $installDirectory/$programName/conf/

    # Create ssl directory if it doesn't exist
    mkdir -p $installDirectory/$programName/ssl

    # Move SSL cert/key to ssl directory
    mv $installDirectory/cert.pem $installDirectory/$programName/ssl/
    mv $installDirectory/key.pem $installDirectory/$programName/ssl/

    echo "$programName configured"
    
else
    
    echo "$programName not installed yet"
    
fi
