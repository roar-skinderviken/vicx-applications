FROM bellsoft/liberica-openjdk-alpine:25
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENV TZ="Europe/Oslo"

# Installer GCC-runtime-biblioteker
RUN apk add --no-cache libgcc libstdc++ tzdata

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]