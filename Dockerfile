########### Run #########
FROM openjdk17:alpine-slim

ADD target/sushi-api.jar sushi-api.jar
EXPOSE 8098

ENTRYPOINT ["java","-jar", "sushi-api.jar"]