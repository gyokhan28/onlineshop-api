#!/bin/bash

FLAG_FILE="/App/data/data_loaded.flag"

if [ "${LOAD_TEST_DATA}" = "true" ] && [ ! -f "$FLAG_FILE" ]; then
  export DATA_INIT_MODE=always
  # Create a flag file to indicate that the data has been loaded
  touch "$FLAG_FILE"
else
  # Don't load any data or don't load it twice /app will crash/
  export DATA_INIT_MODE=never
fi

java -jar /App/app.jar
