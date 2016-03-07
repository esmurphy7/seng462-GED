#!/bin/bash

installDirectory=/seng/scratch/group4
programName=postgres

$installDirectory/$programName/bin/postgres -D $installDirectory/$programName/data "$@"
