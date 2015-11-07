#!/bin/bash

sbt_prefix="./sbt ++$TRAVIS_SCALA_VERSION"

clean_command="$sbt_prefix clean"
check_command="$sbt_prefix build && bash <(curl -s https://codecov.io/bash)"
publish_command="$sbt_prefix publishLocal"

travis_command="$check_command && $clean_command && $publish_command"
eval $travis_command
