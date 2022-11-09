cmake_minimum_required(VERSION 3.14)
project(kokoa C)

#include_directories(/usr/local/include)#for freebsd

set(CMAKE_C_STANDARD 11)

add_library(kokoa SHARED library.c)
