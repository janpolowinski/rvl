#!/bin/bash

script_path=`pwd`
osascript -e 'on run {arg1}' -e 'tell application "Terminal" to do script "
    cd " & arg1 & "
    pwd
    ./start-fe-server-without-UI.bash" ' -e 'end run' $script_path

osascript -e 'on run {arg1}' -e 'tell application "Terminal" to do script "
    cd " & arg1 & "
    pwd
    ./start-be-server.bash" ' -e 'end run' $script_path

./open-fe.bash