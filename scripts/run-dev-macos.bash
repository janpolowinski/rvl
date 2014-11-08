#!/bin/bash

script_path=`pwd`
osascript -e 'on run {arg1}' -e 'tell application "Terminal" to do script "
    cd " & arg1 & "
    pwd
    ./python-test-server.bash" ' -e 'end run' $script_path

osascript -e 'on run {arg1}' -e 'tell application "Terminal" to do script "
    cd " & arg1 & "
    pwd
    ./start-be-server.bash" ' -e 'end run' $script_path

python -mwebbrowser http://localhost:8585/web/example-html/single-page/index-rest.html


# example of using apple script
#the_url="http://stackoverflow.com/questions/1521462/looping-through-the-content-of-a-file-in-bash"
#osascript -e 'on run {theURL}' -e 'tell application "Safari" to set URL of the front document to theURL' -e 'end run' $the_url
