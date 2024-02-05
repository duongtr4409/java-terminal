FROM maven:3.9.6-eclipse-temurin-11 as build
WORKDIR /app
COPY . /app
RUN mvn clean compile -Pprod
RUN nohup mvn spring-boot:run -Dmaven.test.skip=true &

#FROM maslick/minimalka:jdk11
#WORKDIR /app
#EXPOSE 8081
#COPY --from=build app/target/*.jar /app
#CMD ls -la
#CMD java -jar /app/java-terminal-0.0.1-SNAPSHOT.jar
