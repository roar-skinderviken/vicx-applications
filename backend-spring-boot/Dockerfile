FROM bellsoft/hardened-liberica-runtime-container:jdk-25-glibc
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENV TZ="Europe/Oslo"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
