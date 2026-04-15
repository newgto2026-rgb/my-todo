#!/usr/bin/env sh

set -eu

while read -r local_ref local_sha remote_ref remote_sha; do
  [ -z "$local_ref" ] && continue

  case "$remote_ref" in
    refs/heads/main|refs/heads/master)
      echo "[Policy] main/master로 직접 push할 수 없습니다. PR 브랜치를 사용하세요." >&2
      exit 1
      ;;
  esac

done
