#!/bin/bash
cd TransactionDeploy
java -Xmx2g -cp .:../gson-2.6.2.jar:../mongo-java-driver-2.13.3.jar:../morphia-1.1.0.jar com/teamged/txserver/TxMain $HOSTNAME