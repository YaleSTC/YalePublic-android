#!/bin/bash
# Script to copy over files to the correct directory
# Made by Jason Liu, October 31 2014.
# The script will stop on any error (set -e) and echo each line first (set -x)
set -ex

wget https://github.com/jasonkliu/DummyAPI-YalePublic/archive/1.0.tar.gz
tar xvfz 1.0.tar.gz

cp -iv DummyAPI-YalePublic-1.0/api_keys.xml res/values
cp -iv DummyAPI-YalePublic-1.0/DeveloperKey.java src/edu/yalestc/yalepublic
