#!/bin/bash

URL="http://localhost:8585/semvis/gen/html/index.html"

./start-fe-server.bash &

./open-fe.bash &

./jar.bash

./console-projects.bash