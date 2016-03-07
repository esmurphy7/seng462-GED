#!/bin/bash

installDirectory=/seng/scratch/group4
programName=postgres

if [ -d "$installDirectory/$programName" ]; then

    echo "Configuring $programName"

    # Remove data directory if it exists    
    rm -r $programName/data

    # Initialize database
    $programName/bin/initdb -D $programName/data

    # Set permissions on data directory so others can access
    #chmod -R 770 $programName/data
    
    # Move config file to data directory
    mv postgresql.conf $programName/data

    # Temporarily run postgres to set it up
    $programName/bin/postgres -D $programName/data &
    postgresPid=$!

    echo $postgresPid
    
    # Wait for process to start
    sleep 2
    
    # Create system administrator (sa) superuser
    $programName/bin/psql -U $USER postgres -c "CREATE USER sa WITH SUPERUSER CREATEROLE CREATEDB REPLICATION"

    # Kill postgres process
    kill $postgresPid

    # Wait for process to stop
    sleep 2

    echo "$programName configured"
    
else
    
    echo "$programName not installed yet"
    
fi
