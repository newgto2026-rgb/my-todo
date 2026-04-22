#!/usr/bin/env sh

set -eu

zero_sha="0000000000000000000000000000000000000000"
should_run_lint=0

while read -r local_ref local_sha remote_ref remote_sha; do
  [ -z "$local_ref" ] && continue

  case "$remote_ref" in
    refs/heads/main|refs/heads/master)
      echo "[Policy] main/master로 직접 push할 수 없습니다. PR 브랜치를 사용하세요." >&2
      exit 1
      ;;
  esac

  if [ "$local_sha" != "$zero_sha" ]; then
    should_run_lint=1
  fi
done

if [ "$should_run_lint" -eq 1 ]; then
  echo "[Quality Gate] push 전 lint를 실행합니다..."
  ./gradlew --stacktrace lint
fi
