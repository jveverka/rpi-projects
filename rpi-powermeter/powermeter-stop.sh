#!/bin/bash

PID=`ps -ef | grep powermeter | grep python3 | awk '{ print $2 }'`

kill $PID