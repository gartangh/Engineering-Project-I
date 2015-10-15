#!/bin/bash -ue

if [[ $# -ne 1 ]]; then
	echo "Usage: $0 <DESTINATION>" >&2
	exit 1
fi
DEST=$1

TEMP=$(mktemp)
if ! adb pull "/sdcard/accelDataLog.csv" "$TEMP"; then
  echo "Oops, could not pull, bailing." >&2
  exit 1
fi

mv "$TEMP" "$DEST"
echo "Done!"
