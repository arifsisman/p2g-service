#mvn clean package

#ARTIFACT_ID=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.artifactId | grep -ov "INFO" | grep -ov "Down" | grep -ov "Prog")
#POM_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -ov "INFO" | grep -ov "Down" | grep -ov "Prog")
#
#ARTIFACT_NAME="${ARTIFACT_ID}-${POM_VERSION}.jar"
#echo ${ARTIFACT_NAME}
#
#IMAGE_NAME="mustafasisman/${ARTIFACT_ID}:${POM_VERSION}"
#CONTAINER_NAME=${ARTIFACT_ID}

#docker build -t ${IMAGE_NAME} --build-arg ARTIFACT_NAME=${ARTIFACT_NAME} --no-cache .
#docker rm -vf ${CONTAINER_NAME}
#docker run -d --name ${CONTAINER_NAME} -p 8080:8080 ${IMAGE_NAME}