#!/bin/bash
cd TransactionDeploy
java -Xmx2g -cp .:../gson-2.6.2.jar com/teamged/txserver/TxMain $HOSTNAME