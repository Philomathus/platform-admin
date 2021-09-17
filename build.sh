#!/usr/bin/env bash
mvn clean install -Dmaven.test.skip=true
docker build . -t 749308904276.dkr.ecr.ap-northeast-1.amazonaws.com/platform-admin-2.0:latest
docker push 749308904276.dkr.ecr.ap-northeast-1.amazonaws.com/platform-admin-2.0:latest