# DAI-2023-24-Practical-work-2-TCP

* Lucas Lattion
* Romain Humair

# A Hangman Game

# Hangman Game TCP Protocol Specification

## Section 1 - Overview

This document specifies the application protocol for a Hangman game played over a network using TCP/IP. The game involves a client, which is the player guessing the word, and a server, which manages the game logic and validates the guesses.

## Section 2 - Transport Protocol

- **Protocol**: TCP/IP
- **Port**: The server listens on port **97XX** for incoming connections (The port is link to our birth year 1997 and 19XX. The port is not commonly used by other services).
- **Connection**: The client initiates the TCP connection to the server. The connection must be established with a TCP three-way handshake before any game data is exchanged.
- **Session**: The server maintains session state for each connected client. This includes the current word, guesses made, and number of attempts left.

## Section 3 - Messages

### Connection Setup
- `SYN` (Client -> Server): Begin connection.
- `SYN-ACK` (Server -> Client): Acknowledge connection request.
- `ACK` (Client -> Server): Finalize connection establishment.

### Game Initialization
- `INIT` (Server -> Client): Welcome message including the length of the word and initial display (e.g., "_ _ _ _ _").

### Guessing
- `GUESS <letter or word>` (Client -> Server): The client's guess.
- `RESPONSE <status>` (Server -> Client): The server's response to the guess, where `<status>` includes the updated word display, remaining attempts, and any error/success codes.

### Game End
- `GAME OVER <result>` (Server -> Client): Final game status, indicating win/loss and the correct word if lost.

### Connection Teardown
- `FIN` (Client -> Server or Server -> Client): Initiate connection teardown.
- `ACK` (Server -> Client or Client -> Server): Acknowledge FIN.

### Error Handling
- `ERR <code>` (Server -> Client): Error message with specific code explaining the error.

## Section 4 - Examples

### Successful Guess Sequence

```
Client                               Server
  |                                    |
  |---SYN----------------------------->|
  |<--SYN-ACK--------------------------|
  |---ACK----------------------------->|
  |                                    |
  |<--INIT "5" "_ _ _ _ _"-------------|
  |---GUESS "E"----------------------->|
  |<--RESPONSE "0" "_ _ _ _ E"---------|
  |                                    |
  |---GUESS "ZEBRA"------------------->|
  |<--GAME OVER "WIN" "ZEBRA"----------|
  |                                    |
  |---FIN----------------------------->|
  |<--ACK------------------------------|
  |<--FIN------------------------------|
  |---ACK----------------------------->|
  |                                    |
```

### Error Code Definitions

- `0`: Successful guess.
- `1`: Invalid character (not in the alphabet).
- `2`: Already guessed character.
- `3`: Failed guess (character not in the word).
- `4`: Connection error.
- `5`: Guess limit reached.

## Section 5 - Protocol Diagrams

### Successful Connection and Guess
[Described in the Example section]

### Error Handling

```
Client                               Server
  |                                    |
  |---GUESS "1"----------------------->| (Invalid numeric guess)
  |<--ERR "1"--------------------------|
  |                                    |
  |---GUESS "E"----------------------->| (Repeated guess)
  |<--ERR "2"--------------------------|
  |                                    |
  |---GUESS "XYZ"--------------------->| (Incorrect length)
  |<--ERR "1"--------------------------|
  |                                    |
```

## Edge Cases

Edge cases could include network interruptions, client disconnections, and malformed message formats. Each case should be handled gracefully, with the server providing an appropriate error code and message to the client, or by timing out the connection after a certain period of inactivity.

### Network Interruption

```
Client                               Server
  |                                    |
  |---GUESS "E"----------------------->| 
  |--------(Network Interruption)------|
  |<--(No Response, Timeout)-----------|
  |                                    |
  |---FIN----------------------------->| (Client closes connection after timeout)
  |<--ACK------------------------------|
  |                                    |
```

In this diagram, the client sends a guess but experiences a network interruption that prevents the server's response. After a timeout, the client will close the connection.

### Malformed Message Format

```
Client                               Server
  |                                    |
  |---GUESS "E@#%"-------------------->| (Malformed message with special characters)
  |<--ERR "1"--------------------------|
  |                                    |
```

The server responds with an

error code indicating an invalid character when it receives a malformed guess message. This prevents any further processing of an invalid request.

Implementing these protocols and edge-case management ensures a robust and user-friendly Hangman game experience over a TCP/IP connection.


## Tool used
- Maven
- Java 17
- Intellij IDEA Ultimate
- GitHub
- Markdown
- ChatGpt
