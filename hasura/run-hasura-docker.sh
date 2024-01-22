#! /bin/bash

version=v2.34.0
image=hasura/graphql-engine
containerInternalPort=8080
containerPublicPort=7008
appName=hasura-sushi

docker pull $image:$version

printf "$image:$version image has been pulled. \n"

read -t 2 -p ""

printf "starting container, docker internal port=$containerInternalPort, public port=$containerPublicPort \n"

docker run -d -p $containerPublicPort:$containerInternalPort \
	   --name=$appName \
	   --restart=unless-stopped \
       --env-file hasura-local.env \
       $image:$version

printf "now container is running, docker internal port=$containerInternalPort, public port=$containerPublicPort \n"
printf "go to http://localhost:$containerPublicPort/console \n"
printf "tail container log, docker logs --tail 50 --follow --timestamps {container-name} or {container-id} \n"
              
# http://localhost:$containerPublicPort/console
# docker logs --tail 50 --follow --timestamps {container-name}