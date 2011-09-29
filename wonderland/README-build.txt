
To resolve OutOfMemoryError: PermGen space set the following ant options on OSX and Linux

export ANT_OPTS="-XX:MaxPermSize=900m -Xmx900m"
