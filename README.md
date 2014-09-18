# CS5223 Assignment 1

Using Java RMI

## API

### Server

- joinGame(Client c): int
    - Client object passed in so that server can call client to inform client
    game can be started.
    - Returns `clientId` (`int`) if client is put in queue. `-1` if game has
    already started
- move(int clientId, MoveDirection dir): GameStatus
    - `clientId` indicates the id for the user
    - `GameStatus` encapsulates the current status of the game

### Client

- startGame(): void
    - Server calls this to indicate start of game. Client can now start calling
    `move()`
