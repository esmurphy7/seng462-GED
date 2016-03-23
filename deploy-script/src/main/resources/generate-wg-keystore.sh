#!/bin/bash

installDirectory=/seng/scratch/group4

# Generate the keystore required by Java to accept self-sign SSL certificates

# Create SSL directory if it doesn't exist
mkdir -p $installDirectory/ssl

# Move certificate to ssl directory if it exists
mv $installDirectory/cert.pem $installDirectory/ssl

# Remove keystore file
rm $installDirectory/ssl/keystore

# Generate a keystore for the certificate
keytool -import -alias teamged -keystore $installDirectory/ssl/keystore -storepass teamged -file $installDirectory/ssl/cert.pem -noprompt