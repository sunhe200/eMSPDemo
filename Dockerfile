# 使用 OpenJDK 17 作为基础镜像
FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制 target 目录下所有 jar 文件到容器中
COPY target/*.jar /app/

# 利用 RUN 命令查找 jar 文件（取第一个匹配的）并重命名为 app.jar
RUN set -eux; \
    jarFile=$(ls /app/*.jar | head -n 1); \
    mv "$jarFile" app.jar

# 暴露应用端口（根据实际情况修改）
EXPOSE 8080

# 启动 jar 文件
ENTRYPOINT ["java", "-jar", "/app/app.jar"]