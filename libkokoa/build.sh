#!/usr/bin/env sh
cd libkokoa

#cmake . && make
# $1 == "os-arch"
cmake -S . -B ./build/$1
cd ./build/$1
make