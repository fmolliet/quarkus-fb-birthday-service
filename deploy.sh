# Building Quarkus native
#quarkus build --native
# docker-compose build 
# Build new image
docker build . -t birthday-service:latest --no-cache
# Tag image
docker tag birthday-service:latest winty.io:5000/winty/birthday-service:latest
# Push image to remote server
docker push winty.io:5000/winty/birthday-service:latest