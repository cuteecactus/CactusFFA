# CactusFFA

CactusFFA is a configurable Paper free-for-all plugin built for production Minecraft servers.

This plugin is used on `play.cactusmc.xyz`.

It includes:

- kit categories and direct kit joining
- per-kit arenas
- void arena world support for `ffa_arenas`
- GUI-based kit selection
- combat-lock duels
- per-kit combat options
- kill rewards and configurable drops
- scoreboard override while players are in FFA
- admin commands for arenas, kits, categories, and lobby setup

## Requirements

- Paper 1.20.6+
- Java 21

Optional but recommended:

- Multiverse-Core

You may need `Multiverse-Core` to teleport to the `ffa_arenas` world initially, especially if you want to manage or import that world through Multiverse commands.

## Build

```powershell
mvn -DskipTests package
```

Output jar:

- `target/CactusFFA-1.0.0.jar`

## Installation

1. Build the jar or use the compiled release jar.
2. Put `CactusFFA-1.0.0.jar` into your server `plugins` folder.
3. Start the server once.
4. Stop the server and review the generated config files in `plugins/CactusFFA/`.
5. Start the server again and begin setup.

## First Setup

1. Make sure the arena world exists.
2. If you use Multiverse-Core, you can try:

```text
/mvtp ffa_arenas
```

or import it manually if needed:

```text
/mv import ffa_arenas normal
```

3. Stand inside the `ffa_arenas` world and create arenas:

```text
/cacffa arena create nethop
/cacffa arena create sword-beast
/cacffa arena create sword-speed
```

4. Set the main server lobby location players should return to when leaving FFA:

```text
/cacffa setlobby
```

5. Edit your kits in `plugins/CactusFFA/kits.yml`.
6. Reload the plugin:

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

## Screenshots

Add screenshots here before publishing the plugin page or repository.

Recommended screenshots to upload:

- main `/ffa` menu showing categories
- category menu showing kits inside one category
- admin panel overview
- kit options editor GUI
- an arena in the `ffa_arenas` world
- below-name health / scoreboard while fighting

### Screenshot Placeholder 1

Main `/ffa` menu

```md
![Main FFA Menu](docs/images/main-ffa-menu.png)
```

### Screenshot Placeholder 2

Category kit menu

```md
![Category Menu](docs/images/category-menu.png)
```

### Screenshot Placeholder 3

Admin panel

```md
![Admin Panel](docs/images/admin-panel.png)
```

### Screenshot Placeholder 4

Kit options editor

```md
![Kit Options](docs/images/kit-options.png)
```

### Screenshot Placeholder 5

Arena preview or live combat screenshot

```md
![Arena Combat](docs/images/arena-combat.png)
```

## How To Upload Screenshots To A README

If this README is on GitHub, the cleanest way is:

1. Create a folder such as `docs/images/` in the repository.
2. Put your screenshot files there.
3. Reference them in Markdown using relative paths.

Example:

```md
![Main FFA Menu](docs/images/main-ffa-menu.png)
```

If you are uploading the README somewhere else:

1. Upload the images to an image host or your plugin page assets.
2. Use the direct image URL in Markdown.

Example:

```md
![Main FFA Menu](https://your-site.com/images/main-ffa-menu.png)
```

## Production Notes

- Review `kits.yml` carefully before launch.
- Confirm every kit points to a valid arena.
- Test `/leave`, respawn, kill rewards, combat-locking, and lobby teleporting before public release.
- If `ffa_arenas` was created incorrectly in a previous test, remove the broken world and let the plugin create it again.

## Credits

Developed for the CactusMC network at `play.cactusmc.xyz`.
