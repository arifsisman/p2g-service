ARTIFACT_ID_SCRIPT="mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.artifactId | grep -Eiv 'INFO|Down|Prog'"
eval ARTIFACT_ID=\$\($ARTIFACT_ID_SCRIPT\)
echo $ARTIFACT_ID
echo $ARTIFACT_ID
echo $ARTIFACT_ID