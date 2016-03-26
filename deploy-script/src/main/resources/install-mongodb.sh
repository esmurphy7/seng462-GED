#!/bin/bash

installDirectory=/seng/scratch/group4
programName=mongodb
mongoVersion="3.2.4"

if [ ! -d "$installDirectory/$programName" ]; then

    echo "Installing $programName"

    # Change to install directory
    cd $installDirectory

    # Download binary and unzip
    wget https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-$mongoVersion.tgz
    tar -xzf mongodb-linux-x86_64-$mongoVersion.tgz
    mv mongodb-linux-x86_64-$mongoVersion $programName

    # Add database data directory
    mkdir $installDirectory/$programName/data

    # Remove tar file
    rm mongodb-linux-x86_64-$mongoVersion.tgz

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