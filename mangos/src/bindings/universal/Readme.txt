
** HOW TO SCRIPT IN C++ **

1 - create a file myscript.cpp in scripts folder.
2 - copy the content of script_default.cpp, it as the structure on how the scripting fuctions are organized.
        dont forget to change the name of fuctions, like GossipHello_default to GossipHello_myscript.

3 - in fuction AddSC_default change to AddSC_myscript.
4 - newscript->Name="default"; change the string to "myscript" this name is the one to be called from the db
5 - dont forget to change the name in here to newscript->pGossipHello = &GossipHello_default; this is where the scripted fuctions are stored.
6 - and last thing is in ScriptMgr.cpp

add your AddSC_myscript in here

// -- Scripts to be added --
extern void AddSC_default();
// -------------------

and here

// -- Inicialize the Scripts to be Added --
    AddSC_default();
    // ----------------------------------------

now start using the player fuctions to script ;)
see the sc_defines.h for some fuctions to use.

hope it helps, any question use our forum.

copy libscript.so and libscript.a to your server/lib path

made by: mmcs.
