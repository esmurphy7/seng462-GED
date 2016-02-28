#!/bin/bash

installDirectory=/seng/scratch/group4
programName=nginx

$installDirectory/$programName/sbin/nginx -c $installDirectory/$programName/conf/nginx.conf.loadbalancer -s quit
