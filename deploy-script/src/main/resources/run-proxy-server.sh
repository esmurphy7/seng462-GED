#!/bin/bash
cd QuoteProxyDeploy
java -Xmx4g -cp .:../gson-2.6.2.jar com/teamged/proxyserver/ProxyMain $HOSTNAME -V