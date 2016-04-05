#!/bin/bash
cd AuditDeploy
java -Xmx4g -cp .:../gson-2.6.2.jar com/teamged/auditserver/AuditMain $HOSTNAME