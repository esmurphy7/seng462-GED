#!/bin/bash

installDirectory=/seng/scratch/group4
programName=redis
redisVersion="3.0.7"
numberCores=$(nproc)

if [ ! -d "$installDirectory/$programName" ]; then

    echo "Installing $programName"
    
    # Change to install directory
    cd $installDirectory

    # Download source and unzip
    wget http://download.redis.io/releases/redis-$redisVersion.tar.gz
    tar -xzf redis-$redisVersion.tar.gz

    # Remove tar file
    rm redis-$redisVersion.tar.gz

    # Change to source directory
    cd redis-$redisVersion

    # Compile and install
    make -j $numberCores
    PREFIX=$installDirectory/$programName make install

    cd $installDirectory

    # Remove source folder
    rm redis-$redisVersion -r
    
    # Add write permissions to folder and contents
    chmod -R +w $installDirectory/$programName
    
    if [ -d "$installDirectory/$programName" ]; then
    
        echo "$programName installed successfully"
        
    else
    
        echo "$programName failed to install. Please check the logs for details."
        
    fi
    
else
    
    echo "$programName already installed"
    
fi
