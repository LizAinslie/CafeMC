# CafeMC
> A custom plugin for my Minecraft SMP server.

## Want to join?
If you want to join the server, you can join the discord server [here](https://discord.gg/). Just be friendly & active,
and follow the rules!

## Features

- [x] Teleportation commands
  - [x] `/tpa`, `/tpaccept`, `/tpdeny`
    - [x] Teleports expire after 2 minutes & sender is notified
  - [x] `/home`, `/home set`, `/home clear` (one home per player)
- [x] AFK system
  - [x] `/afk`
  - [x] AFK Players are shown in the tab list and do not count towards the sleep counter
  - [ ] Players are automatically set as AFK after 5 minutes of inactivity
  - [ ] AFK players will be updated to not AFK if they move
- [ ] Economy system (TBD whether to use a plugin or custom implementation)
  - [ ] `/balance`, `/pay`, `/shop` 
  - [ ] Economy system will be used for player shops and trading
  - [ ] Maybe a custom currency? Considering using gold ingots as currency with a bank system to deposit/withdraw
    - this would be a good way to give gold more use in the game

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