#!/usr/bin/env sh

set -eu

PRD_ID_REGEX='PRD-[0-9]+'

current_branch() {
  git symbolic-ref --quiet --short HEAD 2>/dev/null || true
}

extract_prd_ids() {
  printf '%s' "$1" | grep -Eo "$PRD_ID_REGEX" | sort -u || true
}

is_exempt_branch() {
  branch="$1"
  case "$branch" in
    main|master|release/*|hotfix/*|dependabot/*)
      return 0
      ;;
    *)
      return 1
      ;;
  esac
}

is_exempt_path() {
  path="$1"
  case "$path" in
    docs/*|*.md|.github/*|*/src/test/*|*/src/androidTest/*)
      return 0
      ;;
    *)
      return 1
      ;;
  esac
}

staged_files() {
  git diff --cached --name-only --diff-filter=ACMR
}

requires_prd_for_staged_changes() {
  staged_files | while IFS= read -r path; do
    [ -z "$path" ] && continue
    if ! is_exempt_path "$path"; then
      echo "$path"
      return 0
    fi
  done
  return 1
}

assert_prd_exists_for_branch() {
  branch="$(current_branch)"

  if [ -z "$branch" ]; then
    echo "[PRD] Detached HEAD에서는 PRD 검증을 통과할 수 없습니다." >&2
    exit 1
  fi

  if is_exempt_branch "$branch"; then
    return 0
  fi

  branch_prd_ids="$(extract_prd_ids "$branch")"
  if [ -z "$branch_prd_ids" ]; then
    echo "[PRD] 브랜치명에 PRD ID가 필요합니다. 예: feat/PRD-123-add-calendar" >&2
    exit 1
  fi

  missing=""
  for prd_id in $branch_prd_ids; do
    prd_file="docs/prd/${prd_id}.md"
    if [ ! -f "$prd_file" ]; then
      missing="$missing $prd_file"
      continue
    fi

    if ! grep -Eq "^prd_id:\s*${prd_id}\s*$" "$prd_file"; then
      echo "[PRD] ${prd_file}에 'prd_id: ${prd_id}'가 필요합니다." >&2
      exit 1
    fi

    if ! grep -Eq "^status:\s*approved\s*$" "$prd_file"; then
      echo "[PRD] ${prd_file}는 status: approved 여야 합니다." >&2
      exit 1
    fi
  done

  if [ -n "$missing" ]; then
    echo "[PRD] PRD 파일이 없습니다:$missing" >&2
    exit 1
  fi
}
