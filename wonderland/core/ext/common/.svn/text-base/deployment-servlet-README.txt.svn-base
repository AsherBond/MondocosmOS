Source: https://deployment.dev.java.net/

JnlpDownloadServlet
-------------------

Brief Description :

JnlpDownloadServlet can be used to package a JNLP file and its associated
resources in a Web Archive (.war) file. The purpose of the servlet is to
provide a simple and convenient packaging format for JNLP applications, so they
can be easily deployed in a Web Container, such as Tomcat or a J2EE-compliant
Application Server.

The download servlet supports the following features:

    * Automatic installation of the codebase URL into JNLP files, thus
      eliminating manual management of hard-coded URLs into JNLP files.
    * Explicit specification of the timestamp for a JNLP file, independent of
      the file-system timestamp.
    * Support for download protocols defined in the JNLP specification v1.0.1.
      These include basic download protocol, version-based download protocol,
      and extension download protocol.
    * Version-based information specified per file or per directory in the
      Web archive. Thus, no centralized file needs to be managed for the entire
      archive.
    * Automatic generation of JARDiff files.
    * pack200-gzip and gzip compression support. You can now host *.jar.pack.gz
      or *.jar.gz files together with you original *.jar files.  If the client
      supports the pack200-gzip or gzip file formats, the servlet will return
      the compressed file if it is available on the server.  Java Web Start 1.5
      supports both compression formats.

The packaging support consists of one servlet: JnlpDownloadServlet. The servlet
is packaged into the deployment-servlet.jar file.


Please refer to the JnlpDownloadServlet guide for more information on the
JnlpDownloadServlet:
http://java.sun.com/j2se/1.5.0/docs/guide/javaws/developersguide/downloadservletguide.html
