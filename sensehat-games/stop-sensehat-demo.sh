#!/bin/bash

SENSE_PID=`ps -ef | grep launcher | grep python3 | awk '{ print $2 }'`
kill ${SENSE_PID}

