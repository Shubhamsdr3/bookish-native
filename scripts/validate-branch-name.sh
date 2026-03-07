#!/bin/bash

# Allowed branch types
ALLOWED_TYPES="feature|bugfix|hotfix|release|chore|refactor|docs|test"

# Branch name must follow: type/TICKET-123-short-description
# OR special branch names: main, develop, master
SPECIAL_BRANCHES="^(main|develop|master)$"
BRANCH_REGEX="^($ALLOWED_TYPES)/[A-Z]{2,8}-[0-9]+-[a-z0-9][a-z0-9-]*[a-z0-9]$"

validate_branch_name() {
  local branch="$1"

  # Allow special branches
  if [[ "$branch" =~ $SPECIAL_BRANCHES ]]; then
    echo "✅ Branch name is valid (protected branch): \"$branch\""
    return 0
  fi

  # Validate against naming convention
  if [[ "$branch" =~ $BRANCH_REGEX ]]; then
    echo "✅ Branch name is valid: \"$branch\""
    return 0
  else
    echo "❌ Invalid branch name: \"$branch\""
    echo "   Expected format: type/TICKET-123-short-description"
    echo "   Allowed types  : feature | bugfix | hotfix | release | chore | refactor | docs | test"
    echo "   Examples       :"
    echo "     feature/ABC-123-add-login-screen"
    echo "     bugfix/PROJ-456-fix-crash-on-startup"
    echo "     hotfix/APP-789-patch-security-issue"
    echo "     release/REL-100-v2-0-0"
    return 1
  fi
}

validate_branch_name "$@"
