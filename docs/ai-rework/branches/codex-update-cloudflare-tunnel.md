# Rework Metrics: codex/update-cloudflare-tunnel

## Branch Summary

| Field | Value |
|---|---|
| Branch | `codex/update-cloudflare-tunnel` |
| PR | `https://github.com/newgto2026-rgb/your-todo/pull/112` |
| Primary AI Model | `GPT-5 Codex` |
| Task Type | `bugfix` |
| Rework Count | `1` |
| P0 Issues | `0` |
| P1 Issues | `0` |
| P2 Issues | `1` |
| Automation Possible Issues | `0` |
| Automation Added Issues | `0` |
| Open Events | `0` |

## Rework Events

### R1 - Release config should not reference debug tunnel

| Field | Value |
|---|---|
| Source | `gemini-code-assist` review thread `PRRT_kwDOSAf4v86J04aE` / comment `PRRC_kwDOSAf4v87Lzjby` |
| Severity | `P2` |
| Status | `closed` |
| Feedback Signal | Release `YOURTODO_AI_SERVER_BASE_URL` fallback still referenced `activeDebugServerBaseUrl`, even though release builds require `yourtodo.serverBaseUrl`. |
| Product/Engineering Impact | Release configuration would keep a dead/unintended debug tunnel fallback in build logic, increasing configuration ambiguity. |
| Root Cause Hypothesis | The debug URL constant was reused mechanically across debug and release fallback chains. |
| System Gap | Local review focused on the debug build user impact and did not separately inspect release fallback reachability. |
| Automation Hypothesis | Existing release property check makes this detectable by review/static reasoning; no new automation is needed for this one-off simplification. |
| Decision | Fix immediately by removing the release fallback to `activeDebugServerBaseUrl`. |
| Fix Scope | Removed the debug Cloudflare tunnel from the release AI server URL fallback while preserving the release server property fallback and configuration-time empty fallback used by debug tasks. |
| Fix Size | 1-line build configuration change plus metrics update. |
| Verification | `./gradlew :core:network:lintDebug` PASS; `./gradlew :core:network:testDebugUnitTest` PASS; `scripts/quality/product-harness-check.sh` PASS; `scripts/quality/rework-metrics-check.sh --local` PASS. |
| Lesson | Inspect debug and release provider fallback chains separately when rotating debug endpoints; release-only properties still need configuration-time fallbacks for debug Gradle tasks. |
| Rework Commit | `HEAD` |

## External Event Coverage

### Review Threads

- `PRRT_kwDOSAf4v86J04aE` / `PRRC_kwDOSAf4v87Lzjby` - release AI server URL fallback should not reference the debug tunnel; fixed and ready to resolve.

### Actionable PR Comments

No actionable PR comments recorded yet.

### Check Failures

No check failures recorded yet.

## Non-Rework Follow-up Commits

- `c02fb7d` - Record PR URL in branch metrics after PR creation.
