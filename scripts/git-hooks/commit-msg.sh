#!/usr/bin/env sh

set -eu

MSG_FILE="$1"
SCRIPT_DIR="$(CDPATH='' cd -- "$(dirname -- "$0")" && pwd)"
# shellcheck disable=SC1091
. "$SCRIPT_DIR/common.sh"

if ! requires_prd_for_staged_changes >/dev/null; then
  exit 0
fi

branch="$(current_branch)"
if is_exempt_branch "$branch"; then
  exit 0
fi

branch_prd_ids="$(extract_prd_ids "$branch")"
if [ -z "$branch_prd_ids" ]; then
  echo "[PRD] 브랜치명에 PRD ID가 필요합니다. 예: feat/PRD-123-add-calendar" >&2
  exit 1
fi

msg_prd_ids="$(extract_prd_ids "$(cat "$MSG_FILE")")"
if [ -z "$msg_prd_ids" ]; then
  echo "[PRD] 커밋 메시지에 PRD ID를 포함하세요. 예: feat(todo): ... [PRD-123]" >&2
  exit 1
fi

match_found=0
for branch_id in $branch_prd_ids; do
  for msg_id in $msg_prd_ids; do
    if [ "$branch_id" = "$msg_id" ]; then
      match_found=1
      break
    fi
  done
  [ "$match_found" -eq 1 ] && break
done

if [ "$match_found" -ne 1 ]; then
  echo "[PRD] 커밋 메시지의 PRD ID가 브랜치 PRD ID와 일치해야 합니다." >&2
  exit 1
fi
