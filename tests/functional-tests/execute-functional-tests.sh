#!/bin/bash

echo "Starting Functional Tests exection..."

echo "CIRENE_HOST on SHELL: $CIRENE_URL_HOST"

mvn -DCIRENE_URL_HOST=$CIRENE_URL_HOST clean test

echo "Functional Tests finished..."
