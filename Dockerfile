FROM openjdk:8-jdk-alpine
WORKDIR /opt
RUN apk add --no-cache curl tar bash procps
COPY target/cs643-*.jar /demo app.jar

FROM bde2020/spark-java-template:1.5.1-hadoop2.6

ENV SPARK_APPLICATION_MAIN_CLASS edu.njit.cs643.SparkML_EC2
ENV SPARK_APPLICATION_JAR_NAME cs643-p2-artifact-0.0.1-SNAPSHOT-jar-with-dependencies.jar

ENV HDFS_URL=hdfs://hdfs:9000

ENV APP_ARGS_OWNER=localhost
ENV APP_ARGS_MAX_DETAIL=128
ENV APP_ARGS_INPUT=/input
ENV APP_ARGS_OUTPUT=/output

ADD demo.sh /

RUN chmod +x /demo.sh

CMD ["/bin/bash", "/demo.sh"]

CMD ["java", "jar", "/demo.jar"]