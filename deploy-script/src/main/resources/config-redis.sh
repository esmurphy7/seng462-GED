#!/bin/bash

installDirectory=/seng/scratch/group4
programName=redis

if [ -d "$installDirectory/$programName" ]; then

    echo "Configuring $programName"
    
    # Move config file to program directory
    mv $installDirectory/redis.conf $installDirectory/$programName
    
    echo "$programName configured"
    
else
    
    echo "$programName not installed yet"
    
fi
