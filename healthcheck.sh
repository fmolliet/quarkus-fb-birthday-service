#!/bin/sh

HOST="localhost"
PORT=8081

_healthcheck=$(curl --fail -s -S http://$HOST:$PORT/q/health) || exit 1

echo ${_healthcheck}

exit $?