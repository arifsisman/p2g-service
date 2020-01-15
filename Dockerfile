FROM openjdk:oracle

MAINTAINER Mustafa Arif Sisman <mustafaarifsisman@gmail.com>

ARG DB_IP
ENV DB_IP = $DB_IP

ARG APP_IP
ENV APP_IP = $APP_IP

COPY target/p2g-web-0.1.00-SNAPSHOT.jar app/p2g-web-0.1.00-SNAPSHOT.jar

EXPOSE 8080
CMD ["java", "-jar", "app/p2g-web-0.1.00-SNAPSHOT.jar"]

#mvn clean -DAPP_IP=142.93.239.61 -DDB_IP=142.93.239.61 install
#mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#docker build -t mustafasisman/p2g-web --build-arg DB_IP=142.93.239.61 --build-arg APP_IP=142.93.239.61 --no-cache .
#docker run -d --dns=8.8.8.8 --name p2g-web -p 80:8080 -e “DB_IP=142.93.239.61”  -e “APP_IP=142.93.239.61” mustafasisman/p2g-web