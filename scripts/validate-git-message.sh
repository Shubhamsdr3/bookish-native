#!/bin/bash

# Define the commit message validation regex
REGEX='^[A-Z]{2,8}-[0-9]+.+'

validate_message() {
  local label="$1"
  local msg="$2"

  # Skip merge and revert commits
  if [[ "$msg" == Merge* || "$msg" == Revert* ]]; then
    echo "🔀 Skipping $label commit: $msg"
    return 0
  fi

  # Check if the message matches the regex pattern
  if [[ "$msg" =~ $REGEX ]]; then
    echo "$label is valid: \"$msg\""
    return 0
  else
    echo "Invalid $label:"
    echo "   \"$msg\""
    echo "Expected format: ABC-123: Your message"
    return 1
  fi
}
