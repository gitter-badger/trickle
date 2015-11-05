#!/bin/bash

sbt_command="./sbt ++$TRAVIS_SCALA_VERSION ;build;publishLocal"
eval $sbt_command
