#!/bin/bash

installDirectory=/seng/scratch/group4
sourceDirectory=/seng/seng462/group4/local/src
programName=postgres
postgresVersion="9.3.2"
numberCores=$(nproc)

if [ ! -d "$installDirectory/$programName" ]; then

    echo "Installing $programName"
    
    # Change to install directory
    cd $installDirectory

    # Download source to shared folder if it doesn't exist
    if [ ! -f "$sourceDirectory/postgresql-$postgresVersion.tar.bz2" ]; then
        
        echo "Download $programName to shared folder"
        
        wget ftp://ftp.postgresql.org/pub/source/v$postgresVersion/postgresql-$postgresVersion.tar.bz2 \
        -P $sourceDirectory
    fi

    # Copy source and unzip
    cp $sourceDirectory/postgresql-$postgresVersion.tar.bz2 ./

    tar -xjf postgresql-$postgresVersion.tar.bz2

    # Remove tar file
    rm postgresql-$postgresVersion.tar.bz2

    # Change to source directory
    cd postgresql-$postgresVersion

    # Configure nginx to install at our install directory
    ./configure \
    --prefix=$installDirectory/$programName

    # Compile and install
    make -j $numberCores
    make install

    cd $installDirectory

    # Remove source folder
    rm -r postgresql-$postgresVersion

    # Add write permissions so multiple people can edit contents
    chmod -R +w $programName
    
    if [ -d "$installDirectory/$programName" ]; then
    
        echo "$programName installed successfully"
        
    else
    
        echo "$programName failed to install. Please check the logs for details."
        
    fi
    
else
    
    echo "$programName already installed"
    
fi
