RFIDReaderApplet
=============
Java applet that reads the unique id from rfid/nfc/mifare tokens via a javascript callback method. Could easily be adapted to read other sectors of the tags.

Example
-------
See example/ for a demo of how to use. 
Beware the jar needs to be signed in order to not choke on a security exception when trying to read the card. 

Why
-------
Based off prototype code for [cantivo] admin panel where it's used by administrators for assigning tokens to users.

[Cantivo]: http://cantivo.org
