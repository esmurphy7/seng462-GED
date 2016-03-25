#!/bin/bash

installDirectory=/seng/scratch/group4
programName=mongodb

# Get database port from config file
port=$(cat config.json | python -c 'import json,sys;config=json.load(sys.stdin);print config["deployments"]["databaseServer"]["port"]')

# If it couldn't find the port, exit
if ! [ $? -eq 0 ]; then
    echo "Couldn't find database port in config file"
    exit 1
fi

$installDirectory/$programName/bin/mongod --dbpath $installDirectory/$programName/data --port $port