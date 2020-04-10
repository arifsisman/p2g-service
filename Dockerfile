FROM adoptopenjdk:8-openj9
ARG ARTIFACT_NAME
ENV ARTIFACT_NAME ${ARTIFACT_NAME}
ENV SPRING_PROFILE_ACTIVE ${SPRING_PROFILE_ACTIVE:-prod}
ADD target/${ARTIFACT_NAME} jar/${ARTIFACT_NAME}
ADD initial.sh initial.sh
RUN ["chmod", "+x", "initial.sh"]
CMD ["./initial.sh"]