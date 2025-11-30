# MySMPPlugin

Production-ready Minecraft Paper plugin for multi-world SMP servers.

## Quick Start

- Requirements: Java 17+, Maven, Paper server 1.21.x.
- Build: `mvn package`.
- Output: Jar copies to `${PROJECT_ROOT}/../MinecraftSMP/plugins` via Maven. Adjust `server.plugins.dir` in `pom.xml` if your server path differs.
- Run server from IntelliJ:
  - Add your Paper server as an external run configuration.
  - Ensure the plugins directory points to the folder above.

## Commands

- `/heal` — heal yourself (`mysmp.command.heal`).
- `/lobby` — teleport to lobby world (`mysmp.command.lobby`).
- `/survival` — teleport to survival world (`mysmp.command.survival`).
- `/setlobby` — set lobby spawn to your location (`mysmp.command.setlobby`).
- `/setsurvival` — set survival spawn to your location (`mysmp.command.setsurvival`).
- `/mysmp reload` — reload configs (`mysmp.command.reload`).

## Configs

- `config.yml`: core settings.
- `messages.yml`: prefix and messages.
- `worlds.yml`: declared spawns for lobby and survival.

## Testing

- Unit tests: JUnit 5 for `ConfigUtil`, `LocationSerializer`.
- Integration suggestion: Use MockBukkit to simulate server for command/listener behavior.
- Note: The build currently skips tests by default (`maven.test.skip=true`). Remove or set to `false` to run tests.

## Architecture

- `ServiceLocator` provides simple DI across components.
- Async I/O: DB and config reads use `CompletableFuture`; teleports use `teleportAsync`; world creation is scheduled on main thread and returned as a future.
- DB: SQLite via HikariCP pool, schema auto-migration on start.
- Tasks: Autosave placeholder and cooldown manager.

## Tradeoffs & Expansion

- Permissions: Basic nodes defined; expand per-world/build granularity.
- DB Migrations: Simple `CREATE TABLE IF NOT EXISTS`; consider Flyway for versioned migrations.
- Multiworld Edge Cases: World creation/loading returns a future; consider handling generator settings, world folders, and unloading policies.
- Messaging: Minimal helper using legacy color codes; consider Adventure Components end-to-end.
- Autosave: Placeholder; wire actual in-memory caches as your features grow.

