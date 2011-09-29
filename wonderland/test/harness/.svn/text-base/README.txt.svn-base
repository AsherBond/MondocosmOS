
Setup
-----

Edit props/wonderland-server.properties to point to the wonderland server you are testing. By default the
system will test http://localhost:8080/

Edit build.properties and set the name of the master test server (this is the machine administering the test, not the
Darkstar server).

Test Execution
--------------

Current the harness is hard coded to run the SimpleTestDirector and the test details are hard coded in the
SimpleTestDirector.java file.

To run the test;

ant run-master   the master test manager
ant run-slave    this can be run multiple times 
ant run-manager  optional ui for managing tests, limited functionality at this time

Note that if you want to run multiple slaves on the same machine, you will
need to set different Wonderland user directories for each instance.  So
in every instance past the first, run with:

ant -Dwonderland.user.dir=/path/to/unique/directory run-slave


JTRunner
--------

TODO
