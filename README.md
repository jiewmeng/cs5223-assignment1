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


### Plan for part 2

Plan for part 2: 
- Clients also implements server
- Only primary server will call startGame
- At end of wait time, server chooses a backup server 
- Clients still call move() on primary server. 
    - Primary server will then relay new game state to backup server. 
    - If client, no response after 1s, primary server is assumed down
- Primary server down: 
    - Client that found primary is down will call backup to make it e primary server
    - Multiple clients may do that ... 
    - While doing so, clients should also send their moveDirections to ckup who will process it
    - Backup server will promote itself to primary, selecting a new ckup server from list of clients
    - Once selected, new primary (previously backup) will ping new ckup server, to verify its working
    - If no response, it will choose a new backup
    - Once a new backup is selected, broadcast to all clients, tifying them of new backup server
