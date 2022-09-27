#!/usr/bin/env bash

set -euo pipefail

echo "$(date): Deploying ${REPOSITORY_TAG} to ECS (region ${AWS_REGION})..."

aws cloudformation deploy \
    --stack-name quarkus-ecs \
    --template-file ecs.yaml \
    --parameter-overrides \
      RepositoryTag="${REPOSITORY_TAG}" \
      ClusterName="quarkus" \
      Subnet1="${SUBNET1}" \
#      Subnet2="${SUBNET2}" \
      ContainerSecurityGroup="${CONTAINER_SECURITY_GROUP}" \
      TaskRoleArn="${TASK_ROLE_ARN}" \
      ConnectionStringSecretArn="${CONNECTION_STRING_SECRET_ARN}"

echo "$(date): Done."
