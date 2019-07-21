#!/bin/bash
export NAME=igorperikov
export IMAGE=mighty-watcher
docker build -t $NAME/$IMAGE:latest .
if [ "$TRAVIS_BRANCH" == "master" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] ; then
    docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"
    docker tag $NAME/$IMAGE:latest $NAME/$IMAGE:$TRAVIS_BUILD_NUMBER
    docker push $NAME/$IMAGE:latest
    docker push $NAME/$IMAGE:$TRAVIS_BUILD_NUMBER
fi
