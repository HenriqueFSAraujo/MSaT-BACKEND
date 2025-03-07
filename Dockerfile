FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar os dois arquivos JAR para o container
COPY target/agostinianas-api.jar agostinianas-api.jar
COPY target/module-oauth.jar module-oauth.jar

# Expor as portas das aplicações
EXPOSE 8080 8082 8083

# Comando para rodar as duas aplicações simultaneamente
CMD java -jar garantias-api.jar & java -jar module-oauth.jar

