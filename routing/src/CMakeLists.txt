ADD_EXECUTABLE(${PROJECT_NAME}
    "main.cpp"
)

SET_TARGET_PROPERTIES(${PROJECT_NAME}
    PROPERTIES RUNTIME_OUTPUT_DIRECTORY
    ${PROJECT_SOURCE_DIR}/dist
)

TARGET_LINK_LIBRARIES(${PROJECT_NAME} ${OSMIUM_LIBRARIES})
