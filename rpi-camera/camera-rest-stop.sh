#!/bin/bash

PID=`ps -ef | grep camera-rest | grep python3 | awk '{ print $2 }'`

kill $PID

