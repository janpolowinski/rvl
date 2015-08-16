#!/bin/bash

export MAVEN_OPTS="-Xmx2048M"

cd ..

mvn clean install -U
