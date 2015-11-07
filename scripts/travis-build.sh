#!/bin/bash

sbt_prefix="./sbt ++$TRAVIS_SCALA_VERSION"

check_command="$sbt_prefix build && bash <(curl -s https://codecov.io/bash)"
publish_command="$sbt_prefix publishLocal"

travis_command="$check_command && $publish_command"
eval $travis_command
