#!/bin/bash

echo "Start Test HTML using python SimpleHTTPServer ... "

cd org.purl.rvl.tooling.d3vis/src/main/resources

python -m SimpleHTTPServer 8585

cd ..

