#!/bin/bash

# FVT launcher
cd FVT
java -Dlogging.config='./Resources/log4j2.xml' -jar FVT-1.0.0.jar -classpath ./Resources/* --spring.config.location=./Resources/application.properties
