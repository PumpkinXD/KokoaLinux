#!/usr/bin/env sh

# $1 == "os-arch"
cd libkokoa/build/$1

rm -rf ./CMakeFiles
rm -rf ./cmake_install.cmake
rm -rf ./CMakeCache.txt
rm -rf ./Makefile
