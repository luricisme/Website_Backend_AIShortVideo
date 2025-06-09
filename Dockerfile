FROM openjdk:21
ARG FILE_JAR=target/*.jar
ADD ${FILE_JAR} website-backend-ai-short-video-editor
ENTRYPOINT ["java", "-jar", "website-backend-ai-short-video-editor.jar"]
EXPOSE 8080