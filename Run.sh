#!/bin/bash
if [ ! -x ./Build.sh ]; then
    chmod +x ./Build.sh
fi

if [ ! -x ./Makedoc.sh ]; then
    chmod +x ./Makedoc.sh
fi

./Build.sh
if [ $? -ne 0 ]; then
  echo "Build failed, exiting."
  exit 1
fi

./Makedoc.sh
if [ $? -ne 0 ]; then
  echo "Javadoc generation failed, continuing to run the program."
fi

java -cp ./bin Main "$@"
