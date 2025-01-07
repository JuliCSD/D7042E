#!/bin/bash

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_path"

time_to_sleep=10s

echo Starting Smart City

cd light-sensors/target
nohup java -jar $(find . -maxdepth 1 -name light-sensors-\*.jar | sort | tail -n1) >/dev/null 2>&1 &
echo light-provider started


cd ../../weather-sensors/target
nohup java -jar $(find . -maxdepth 1 -name weather-sensors-\*.jar | sort | tail -n1) >/dev/null 2>&1 &
echo weather-provider started

cd ../../controller/target
nohup java -jar $(find . -maxdepth 1 -name controller-\*.jar | sort | tail -n1) >/dev/null 2>&1 &
echo controller started

echo wait..
sleep ${time_to_sleep}

cd ../../street-lighting/target
java -jar $(find . -maxdepth 1 -name street-lighting-\*.jar | sort | tail -n1)
