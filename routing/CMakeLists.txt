CMAKE_MINIMUM_REQUIRED(VERSION 2.8)
PROJECT(routing)

SET(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake/Modules/")

set(CMAKE_BUILD_TYPE MinSizeRel)
INCLUDE(OsmiumOptions)

FIND_PACKAGE(Boost 1.38)
FIND_PACKAGE(Osmium COMPONENTS io)

INCLUDE_DIRECTORIES(
    ${PROJECT_SOURCE_DIR}/include
)

ADD_SUBDIRECTORY(src)
