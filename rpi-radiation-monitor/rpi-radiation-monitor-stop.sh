#!/bin/bash

PID=`ps -ef | grep radiation | grep python3 | awk '{ print $2 }'`

kill $PID