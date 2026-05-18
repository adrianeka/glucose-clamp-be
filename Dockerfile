FROM harbor.cloudias79.com/devops-tools/maven:3.8.5-openjdk-17-slim AS builder
WORKDIR /usr/src/app
COPY . .
RUN mvn clean package -DskipTest

FROM harbor.cloudias79.com/devops-tools/openjdk:17-alpine3.14
COPY --from=builder /usr/src/app/target/bookrecipe-0.0.1-SNAPSHOT.jar /usr/local/lib/app.jar
RUN mkdir -p /var/log/ims-log
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]