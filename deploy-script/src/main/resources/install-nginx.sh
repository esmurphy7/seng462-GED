#!/bin/bash

installDirectory=/seng/scratch/group4
programName=nginx
nginxVersion="1.9.12"
numberCores=$(nproc)

if [ ! -d "$installDirectory/$programName" ]; then

    echo "Installing $programName"
    
    # Change to install directory
    cd $installDirectory

    # Download nginx source and unzip
    wget http://nginx.org/download/nginx-$nginxVersion.tar.gz
    tar -xzf nginx-$nginxVersion.tar.gz

    # Remove tar file
    rm nginx-$nginxVersion.tar.gz

    # Change to nginx source directory
    cd nginx-$nginxVersion

    # Configure nginx to install at our install directory
    ./configure \
    --prefix=$installDirectory/$programName \
    --with-http_ssl_module \
    --with-file-aio \
    --with-threads

    # Compile and install
    make -j $numberCores
    make install

    cd $installDirectory

    # Remove nginx source folder
    rm nginx-$nginxVersion -r

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
