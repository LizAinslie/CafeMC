# CafeMC
> A custom plugin for my Minecraft SMP server.

## Want to join?
If you want to join the server, you can join the discord server [here](https://discord.gg/hKd9eQTQmZ). Just be friendly & active,
and follow the rules!

## Features

- [ ] Teleportation commands
  - [x] `/tpa`, `/tpaccept`, `/tpdeny`
    - [x] Teleports expire after 2 minutes & sender is notified
  - [x] `/home`, `/home set`, `/home clear` (one home per player)
  - [x] `/back` - Teleport to last death location
  - [ ] `/warp`, `/warp list`, `/warp create`, `/warp delete` (admin only aside from `/warp` and `/warp list`)
  - [ ] `/playerwarp <player>`, `/playerwarp set`, `/playerwarp remove` - Player warps, allowing players to set warps for other players to use
- [x] AFK system
  - [x] `/afk`
  - [x] AFK Players are shown in the tab list and do not count towards the sleep counter
  - [x] Players are automatically set as AFK after 5 minutes of inactivity
  - [x] AFK players will be updated to not AFK if they move
- [ ] Nicknames
  - [x] `/nick [nickname]` to set nickname or clear nickname if no argument is given
  - [x] Nicknames are shown in chat
  - [ ] Nicknames are shown in the tab list
  - [ ] Nicknames are shown above the player's head
- [ ] Economy system
  - [x] `/balance`, `/pay`
  - [x] Economy system will be used for player shops and trading
  - [ ] Admin shop system
- [ ] Custom items
  - [ ] Custom item system for adding new items to the game
  - [ ] Custom crafting recipes for custom items
  - [ ] Item shop
  - [ ] Items:
    - [ ] Mob Off! - Like bug spray, used to repel mobs in a small radius around the player for a short duration
    - [ ] Mob Off! Candles -  Placed item that prevents mob spawning in the chunk it is placed in
    - [ ] Spawner Range Extender - Increases the activation range of a mob spawner when used on it
    - [ ] Luck potion - Increases the player's luck attribute for a short duration, increasing the chances of better loot from various sources and other luck based mechanics

## Commemorations

### Dragon Fight

The first dragon fight on the server was a success! We had a total of 9 players participate in the fight. The dragon was
defeated in a few minutes :3

Thanks to `SirOwlie` (Minecraft IGN) for the screenshots!

![](https://imagedelivery.net/W9K_l6ndK9x4x8m3rurakg/6e80a8cd-56af-45a9-0cb7-838a851e5e00/original)
![](https://imagedelivery.net/W9K_l6ndK9x4x8m3rurakg/2ce6c31c-8cf9-446e-9912-c258b4cf1f00/original)
![](https://imagedelivery.net/W9K_l6ndK9x4x8m3rurakg/5d367dc8-c118-4c21-668e-84cacd5d5a00/original)
  
## Contributing
If you would like to contribute to the plugin, feel free to fork the repository and submit a pull request. I will review
the changes and merge them if they are good. If you have any questions, feel free to ask in the discord server.

- `./gradlew shadowJar` - Builds the plugin jar file

TODO: Add some utilities for contributing
- Sqlite Schema for migrating in dev environment
- Gradle tasks for running the server in dev environment

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more information.