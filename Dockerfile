#FROM maslick/minimalka:jdk11
#WORKDIR /app
#EXPOSE 8081
#COPY ./target/java-terminal-0.0.1-SNAPSHOT.jar ./java-terminal-0.0.1-SNAPSHOT.jar
#CMD java $JAVA_OPTIONS -jar java-terminal-0.0.1-SNAPSHOT.jar

#
# Build stage
#
FROM eclipse-temurin:17-jdk-jammy AS build
ENV HOME=/usr/app
RUN mkdir -p $HOME
RUN chmod 777 $HOME
WORKDIR $HOME
ADD . $HOME
RUN --mount=type=cache,target=/root/.m2 ./mvnw -f $HOME/pom.xml clean package

#
# Package stage
#
FROM eclipse-temurin:17-jre-jammy
ARG JAR_FILE=/usr/app/target/*.jar
COPY --from=build $JAR_FILE /app/java-terminal-0.0.1-SNAPSHOT.jar.jar
EXPOSE 8080
ENTRYPOINT java -jar /app/java-terminal-0.0.1-SNAPSHOT.jar.jar
