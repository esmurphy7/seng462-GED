#!/bin/bash

installDirectory=/seng/scratch/group4
programName=tomcat
tomcatMajorVersion="9"
tomcatVersion="9.0.0.M3"
numberCores=$(nproc)

if [ ! -d "$installDirectory/$programName" ]; then

    echo "Installing $programName"
    
    # Change to install directory
    cd $installDirectory

    # Download tomcat source and unzip
    wget http://mirror.csclub.uwaterloo.ca/apache/tomcat/tomcat-$tomcatMajorVersion/v$tomcatVersion/src/apache-tomcat-$tomcatVersion-src.tar.gz
    tar -xzf apache-tomcat-$tomcatVersion-src.tar.gz

    # Remove tar file
    rm apache-tomcat-$tomcatVersion-src.tar.gz
    
    # Move build.properties in source folder
    mv build.properties apache-tomcat-$tomcatVersion-src
    
    # Change to tomcat source directory
    cd apache-tomcat-$tomcatVersion-src

    # Build
    ant

    # Move build directory to installation location
    mv output/build $installDirectory/$programName

    cd $installDirectory
    
    # Remove tomcat build dependency directory
    rm -r tomcat-dependencies
    
    # Remove tomcat source folder
    rm -r apache-tomcat-$tomcatVersion-src

    # Add write permissions so multiple people can edit contents
    chmod -R +w $programName
    
    if [ -d "$installDirectory/$programName" ]; then
    
        echo "$programName installed successfully"
        
    else
    
        echo "$programName failed to install. Please check the logs for details."
        
    fi
    
else
    
    echo "$programName already installed"

    # Remove unused build.properties file
    rm $installDirectory/build.properties
    
fi
