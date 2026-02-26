#!/bin/bash

REGEX='^[A-Z]{2,8}-[0-9]+.+'

validate_message() {
  local label="$1"
  local msg="$2"

  # Skip merge and revert commits
  if [[ "$msg" == Merge* ]] || [[ "$msg" == Revert* ]]; then
    echo "🔀 Skipping $label: $msg"
    return 0
  fi

 # Remove backticks from message for validation
  local sanitized_msg="${msg//\`/}"

  # Validate message
  if [[ "$sanitized_msg" =~ $REGEX ]]; then
    echo "✅ $label is valid: \"$msg\""
    return 0
  else
    echo "❌ Invalid $label: \"$msg\""
    echo "   Expected format: ABC-123: Your message"
    return 1
  fi
}

validate_message "$@"