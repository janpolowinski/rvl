#!/bin/bash

echo "Start RVL tooling console ... "

cd build

java -cp rvl-tooling.jar org.purl.rvl.tooling.process.OGVICConsoleFiles
