#!/bin/sh
java -jar -Dspring.profiles.active=${SPRING_PROFILE_ACTIVE} jar/${ARTIFACT_NAME}