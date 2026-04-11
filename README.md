# CactusFFA

CactusFFA is a configurable Paper free-for-all plugin built for production Minecraft servers.

This plugin is used on `play.cactusmc.xyz`.

It includes:

- kit categories and direct kit joining
- per-kit arenas
- arena creation in any world
- GUI-based kit selection
- combat-lock duels
- per-kit combat options
- kill rewards and configurable drops
- scoreboard override while players are in FFA
- admin commands for arenas, kits, categories, and lobby setup

## Requirements

- Paper 1.21.11+
- Java 21

## Build

```powershell
mvn clean package
```

Output jar:

- `target/CactusFFA-*.jar`

## Installation

1. Build the jar or use the compiled release jar.
2. Put `CactusFFA-*.jar` into your server `plugins` folder.
3. Start the server once.
4. Stop the server and review the generated config files in `plugins/CactusFFA/`.
5. Start the server again and begin setup.

## First Setup

1. Go to the world and location where you want an arena.
2. Create arenas directly at your current position:

```text
/cacffa arena create nethop
/cacffa arena create sword-beast
/cacffa arena create sword-speed
```

3. Set the main server lobby location players should return to when leaving FFA:

```text
/cacffa setlobby
```

4. Edit your kits in `plugins/CactusFFA/kits.yml`.
5. Reload the plugin:

```text
/cacffa reload
```

## Player Commands

```text
/ffa
/ffa <category>
/ffa <kit>
/ffa <category> <kit>
/leave
```

## Admin Commands

```text
/cacffa
/cacffa reload
/cacffa setlobby
/cacffa arena create <id>
/cacffa arena setspawn <id>
/cacffa arena tp <id>
/cacffa arena delete <id>
/cacffa kitcategory create <id>
/cacffa kitcategory delete <id>
/cacffa kit create <id> <arena> [category|none]
/cacffa kit setinventory <id>
/cacffa kit delete <id>
```

## Config Notes

Important files:

- `plugins/CactusFFA/config.yml`
- `plugins/CactusFFA/messages.yml`
- `plugins/CactusFFA/menus.yml`
- `plugins/CactusFFA/kits.yml`
- `plugins/CactusFFA/arenas.yml`

The plugin now merges missing config keys on startup and reload without overwriting your existing values.

## Production Notes

- Review `kits.yml` carefully before launch.
- Confirm every kit points to a valid arena.
- Create arenas in whichever worlds fit your server design. A dedicated FFA world is not required.
- Test `/leave`, respawn, kill rewards, combat-locking, and lobby teleporting before public release.

## Credits

Developed for the CactusMC network at `play.cactusmc.xyz`.
