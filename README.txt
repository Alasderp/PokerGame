1. Check the IP of the server computer and use this in the ClientGUI class.
  (If the DealerServer and ClientGUIs are all being ran on the same machine then 127.0.0.1 can be used i.e. localhost)
2. Set the port of the server in the DealerServer.
3. Make sure the port in the ClientGUI matches.
4. Set how many players there are in the DealerServer (max 5).
5. Run DealerServer on the server machine.
6. Client side needs the ClientGUI + MusicPlayer, pictures of the cards, the Sounds Folder and the Music Folder. (The Client folder already has everything needed bundled in, ready to be distributed). Remember to edit the server IP & port in the ClientGUI before sharing it.
7. Any desired music files can simply be placed in the Music folder
8. Run the client and connect to the server. The client's IP should be logged in the console of the server.
   Cards will be dealt once the set number of players have connected.

Notes - 
At least one music file must be in the Music folder as error checking for this has yet to be implemented. 
The Java JDK must be installed + environment variables set up properly on both the server and client machines. 
Server + Client side must be able to compile as well as run java classes.
The game only works over a local network.
  

Useful tutorial - http://cs.lmu.edu/~ray/notes/javanetexamples/
