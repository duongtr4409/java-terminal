FROM maslick/minimalka:jdk11
WORKDIR /app
EXPOSE 8081
COPY ./target/java-terminal-0.0.1-SNAPSHOT.jar ./java-terminal-0.0.1-SNAPSHOT.jar
CMD java $JAVA_OPTIONS -jar java-terminal-0.0.1-SNAPSHOT.jar
