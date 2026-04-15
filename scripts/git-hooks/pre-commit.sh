#!/usr/bin/env sh

set -eu

SCRIPT_DIR="$(CDPATH='' cd -- "$(dirname -- "$0")" && pwd)"
# shellcheck disable=SC1091
. "$SCRIPT_DIR/common.sh"

branch="$(current_branch)"
if [ "$branch" = "main" ] || [ "$branch" = "master" ]; then
  echo "[Policy] main/master에서 직접 commit할 수 없습니다." >&2
  exit 1
fi

if requires_prd_for_staged_changes >/dev/null; then
  assert_prd_exists_for_branch
fi
