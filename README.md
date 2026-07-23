# CactusFFA

A free-for-all (FFA) plugin for Paper 1.21. Built for practice servers

## Features

- **Kit System** -- Create kits from your inventory, set icons, rename, assign breakable block whitelists
- **Per-Kit Rules** -- Toggle saturation regen, hunger drain, item drops on death, block placement/breaking, and lobby-on-death per kit
- **Arena System** -- Define arena boundaries and spawn points, enable/disable per arena, bind arenas to kits
- **Auto Cleanup** -- Track every block placed or broken in arenas and restore them on a configurable timer (default: 30 min)
- **FFA GUI** -- Players browse and join kits through a customizable GUI
- **Fully Configurable** -- All messages, GUI layout, cleanup interval, and leave commands are configurable in YAML

## Commands

### Player Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/ffa` | `cactusffa.use` | Open the FFA GUI to join a kit-based arena |
| `/leaveffa` | `cactusffa.use` | Leave the current FFA and return to lobby |
| `/lobby` | `cactusffa.use` | Teleport to the lobby |

### Admin Commands (`cactusffa.admin`)

All admin commands use `/cactusffa` or `/cffa`.

#### Kit Management

| Command | Description |
|---------|-------------|
| `/cffa kit create <name>` | Create a kit from your current inventory |
| `/cffa kit setinv <id>` | Update a kit's inventory |
| `/cffa kit load <id>` | Preview a kit's inventory |
| `/cffa kit icon <id> <material>` | Set the kit's GUI icon |
| `/cffa kit rename <id> <name>` | Rename a kit's display name |
| `/cffa kit editor <id>` | Open the kit editor GUI (toggle rules, icon, enable) |
| `/cffa kit breakableblocks <id> add\|remove\|list [material]` | Manage whitelisted breakable blocks |
| `/cffa kit arena <id> <arena\|none>` | Assign an arena to a kit (or `none` to clear) |

#### Arena Management

| Command | Description |
|---------|-------------|
| `/cffa arena create <name>` | Create a new arena |
| `/cffa arena enable <id> <true\|false>` | Enable or disable an arena |
| `/cffa arena corner1 <id>` | Set corner 1 of the arena boundary |
| `/cffa arena corner2 <id>` | Set corner 2 of the arena boundary |
| `/cffa arena spawn <id>` | Set the arena spawn point |
| `/cffa arena rename <id> <name>` | Rename an arena |
| `/cffa arena tp <id>` | Teleport to the arena's spawn |

#### Other

| Command | Description |
|---------|-------------|
| `/cffa setlobby` | Set the lobby to your current position |

## Kit Rules

Each kit has configurable rules toggled via the kit editor GUI or `kits.yml`:

| Rule | Effect When Enabled |
|------|-------------------|
| `saturation` | Disables natural saturation-based health regen |
| `hunger` | Disables hunger drain |
| `drop-items` | Allows items to drop on death |
| `death-lobby` | Sends player to lobby on death instead of respawning |
| `break-blocks` | Allows players to break blocks |
| `place-blocks` | Allows players to place blocks |

## Permissions

| Permission | Default | Description |
|-----------|---------|-------------|
| `cactusffa.admin` | `op` | Full admin access to all `/cffa` commands |
| `cactusffa.use` | `true` | Access to `/ffa`, `/leaveffa`, `/lobby` |

## Configuration

### `config.yml`

```yaml
lobby: null                          # Lobby location (set via /cffa setlobby)
lobby-command: true                  # Enable /lobby command
ffa-leave-commands: []               # Commands to run when players leave FFA ({player} placeholder)
cleanup-duration: 30                 # Arena block cleanup interval in minutes (0 = disable)
```

### `messages.yml`

All user-facing messages are customizable with color codes (`&`) and placeholders (`{kit}`, `{arena}`, `{block}`, `{name}`, `{player}`).

### `guis/ffa.yml`

Configure the FFA selection GUI: title, rows, filler item, kit display name/lore, and kit slot mapping.

## Building

```bash
mvn clean package
```

The compiled JAR will be in `target/`.

## Dependencies

- **Paper 1.21** (API)
- **Triumph GUI 3.1.13** (shaded into the plugin)
