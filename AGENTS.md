# AI Agents Guidelines - Silent Vault

Context for AI agents working on this project.

## Project
Silent Vault — captures all notifications, silences them, stores them in a local database, and shows a simple UI to review them later. MVP scope. Prefer simple, readable Kotlin — immutable data, pure functions, small focused classes.

## Tech Stack
- **Language**: Kotlin 2.2.10
- **UI**: Jetpack Compose (BOM 2026.02.01), Material 3
- **Database**: Room + KSP
- **DI**: none (manual for MVP scope)
- **State**: ViewModel + StateFlow
- **Notifications**: NotificationListenerService
- **Min SDK**: 24, **Target SDK**: 36
- **Architecture**: MVVM (intended)

## Package Structure
```
io.github.pablosalgado.silent.vault
├── data/
│   ├── local/
│   │   ├── NotificationEntity.kt
│   │   ├── NotificationDao.kt
│   │   └── NotificationDatabase.kt
│   └── NotificationRepository.kt
├── service/
│   └── NotificationListener.kt
├── ui/
│   ├── MainScreen.kt
│   ├── MainViewModel.kt
│   └── theme/   (existing)
└── MainActivity.kt
```

## Agent Instructions
- **GitHub CLI**: all GitHub operations (issues, PRs, merges) use `gh`.
- **Issues**: create a GitHub issue from discussion before implementing.
- **No `build.gradle.kts` changes** without permission or clear dependency need.
- **Commit authorship**: use `GIT_AUTHOR_NAME` and `GIT_AUTHOR_EMAIL` to identify yourself (e.g., `OpenCode <opencode@silent-vault.dev>`). Include a `Co-authored-by:` trailer when the human makes the commit.
- **Be concise**: direct answers, no preamble.

## Workflow (Issue → Branch → PR → Review)
- Every change starts with a **GitHub issue** — create it with a conventional commit prefix: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`.
- Infer the type from the discussion context (bug → `fix`, new feature → `feat`, code cleanup → `refactor`, etc.).
- Create a branch: `{type}/#{issue}-short-description` (e.g. `feat/#3-add-encryption`).
- Use **conventional commits** referencing the issue.
- Open a **draft PR**, link to the issue, write a summary of what/why/how.
- **Self-review**: re-read the diff, check edge cases, run `./gradlew build` locally before opening the PR.
- Mark PR as ready. I **review and leave comments**.
- **Iterate**: address all PR comments, push fixes, resolve resolved conversations, re-request review.
- All CI checks (build + tests) must pass before merge.
- Squash-merge to `main` when approved.
