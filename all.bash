#!/bin/bash

./start-fe-server.bash &

./open-fe.bash &

./jar.bash

./console-projects.bash