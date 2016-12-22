#!/usr/bin/env bash

project_root="$(cd "$(dirname "${BASH_SOURCE:-$0}")/.."; pwd)"

java -cp "${project_root}/build/libs/sa4j-0.0.1.jar" com.github.takuyaa.sa4j.CLI "sais" "$@"
java -cp "${project_root}/build/libs/sa4j-0.0.1.jar" com.github.takuyaa.sa4j.CLI "sa4j" "$@"
