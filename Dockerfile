########### Run #########
FROM openjdk:17

ADD target/sushi-api.jar sushi-api.jar
EXPOSE 8098

ENTRYPOINT ["java","-jar", "sushi-api.jar"]