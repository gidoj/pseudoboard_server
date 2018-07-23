# Pseudoboard (Server)

Pseudoboard (name not necessarily final) is a virtual and collaboarative whiteboard. Users may create, edit, connect to, and share canvases. Included drawing tools make up a very basic paint program (brush, line, rectangle, oval, text, etc.). The program is still very much in early stages.

## Getting Started

Follow instructions below to get program up and running.  
(Main server file: BoardServer.java)

### Installing

Make sure to download pseudoboard_client, psuedoboard_server, and pseudoboard_common.

### Code Modifications

Client Files:  
-MessageHandler.java -- modify the server string to the address you wish to use.  
-Login.java -- modify the port int to whichever port you'd like to use (default is 21898).

Server Files:  
-BoardServer.java -- modify the DEFAULT_PORT int to whichever port you'd like to use (default is 21898).

Common Files:  
-NONE
