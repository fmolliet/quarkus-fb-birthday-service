#!/bin/bash
#export JAVA_HOME=C:/java/graalvm-jdk-21.0.4_8.1
# Extrai a versão do pom.xml
VERSION=1.1.2

# Verifica se a versão foi extraída corretamente
if [ -z "$VERSION" ]; then
  echo "Erro ao extrair a versão do pom.xml"
  exit 1
fi

echo "Versão do projeto: $VERSION"

# Build native binary
#./mvnw package -Pnative -DskipTests

# Verifica se o build foi bem sucedido
#if [ $? -ne 0 ]; then
#  echo "Erro no build do projeto"
#  exit 1
#fi

# Build new image
docker build . -t birthday-service:latest --no-cache

# Verifica se a imagem foi criada com sucesso
if [ $? -ne 0 ]; then
  echo "Erro ao construir a imagem Docker"
  exit 1
fi

# Tag image
docker tag birthday-service:latest winty.io:5000/winty/birthday-service:$VERSION

# Verifica se a imagem foi marcada com sucesso
if [ $? -ne 0 ]; then
  echo "Erro ao marcar a imagem Docker"
  exit 1
fi

# Push image to remote server
docker push winty.io:5000/winty/birthday-service:$VERSION

# Verifica se o push foi bem sucedido
if [ $? -ne 0 ]; then
  echo "Erro ao enviar a imagem Docker para o repositório"
  exit 1
fi

echo "Build e deploy concluídos com sucesso!"
