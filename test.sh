ARTIFACT_ID_SCRIPT="mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.artifactId | grep -Eiv 'INFO|Down|Prog'"
POM_VERSION_SCRIPT="mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -Eiv 'INFO|Down|Prog'"

eval ARTIFACT_ID=\$\($ARTIFACT_ID_SCRIPT\)
echo $ARTIFACT_ID
eval POM_VERSION=\$\($POM_VERSION_SCRIPT\)
echo $POM_VERSION
ARTIFACT_NAME=${ARTIFACT_ID}-${POM_VERSION}-jar-with-dependencies.jar
echo $ARTIFACT_NAME
IMAGE_NAME=mustafasisman/${ARTIFACT_ID}:${POM_VERSION}
echo $IMAGE_NAME