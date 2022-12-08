cmake_minimum_required(VERSION 3.14)
project(kokoa C)

if(CMAKE_SYSTEM_NAME MATCHES CMAKE_HOST_SYSTEM_NAME AND CMAKE_SYSTEM_PROCESSOR MATCHES CMAKE_HOST_SYSTEM_PROCESSOR)

    if(CMAKE_SYSTEM_NAME MATCHES "FreeBSD")

        include_directories(/usr/local/include)#for freebsd

    endif()

endif()

#set(CMAKE_C_COMPILER clang)#clang
#set(CMAKE_CXX_COMPILER clang++)#clang++

set(CMAKE_C_STANDARD 11)

add_library(kokoa SHARED library.c)
