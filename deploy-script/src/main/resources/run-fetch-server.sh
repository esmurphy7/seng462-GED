#!/bin/bash
cd QuoteFetchDeploy
java -cp .:../gson-2.6.2.jar com/teamged/fetchserver/FetchMain $HOSTNAME -V