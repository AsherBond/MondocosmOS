

set wokclient_priv(debug) 0
set wokclient_priv(timeout) 10000


proc wokclient_read { } {

  wokclient_read_server NAME;
}


proc wokclient_read_server {pathName } {

    global wokclient_priv;

    set readData "";
    set wokclient_priv($pathName,result) "";
    
    set fileId $pathName;

    if { [eof $fileId] } {
	puts "wokclient : lost controller of $pathName"
	
	close  $fileId

	set clidx [lsearch $wokclient_priv(clients) $pathName ]
	
	if { $clidx != -1 } {
	    if { $clidx == 0 } {
		set wokclient_priv(clients) {};
	    } {
		set wokclient_priv(clients) [lreplace $wokclient_priv(clients) $clidx $clidx]
	    }
	} 

	if { [info exists wokclient_priv($pathName)] }        {unset wokclient_priv($pathName)}
	if { [info exists wokclient_priv($pathName,wait)] }   {unset wokclient_priv($pathName,wait)}
	if { [info exists wokclient_priv($pathName,result)] } {unset wokclient_priv($pathName,result)}
	if { [info exists wokclient_priv($pathName,status)] } {unset wokclient_priv($pathName,status)}
	
	return;
    }


    set context [scancontext create]

    scanmatch $context "CMD: (.*)$" {
	set theCmd $matchInfo(submatch0);

	if {$wokclient_priv(debug)} {
	    puts stdout "Received Command : $theCmd";
	}

	if {[catch "$theCmd" res]} {
	    puts $fileId "ERR: $res"
	} {
	    puts $fileId "RET: $res"
	}    
    }

    scanmatch $context "ERR: (.*)$" {
	set theError $matchInfo(submatch0);

	set wokclient_priv($pathName,status) 1;
	set wokclient_priv($pathName,result) $theError
	set wokclient_priv($pathName,wait)   0;

	if {$wokclient_priv(debug)} {
	    puts stdout "Received Error : $theError";
	}
    }

    scanmatch $context "RET: (.*)$" {
	set theReturn $matchInfo(submatch0);

	if {$wokclient_priv(debug)} {
	    puts stdout "Received Return : $theReturn";
	}
	lappend wokclient_priv($pathName,result) $theReturn;
    }


    scanmatch $context "END:" {
	if {$wokclient_priv(debug)} {
	    puts stdout "Received END:"
	}
	set wokclient_priv($pathName,wait) 0;
	return;
    }

    if { ! [eof $fileId] } {
	scanfile  $context $fileId 
	return;
    } {
	close $fileId
	return;
    }
}

proc wokclient_wait {pathName} {

  global wokclient_priv;

  if {$wokclient_priv($pathName,wait)} {
    after $wokclient_priv(timeout) "set wokclient_priv($pathName,wait) 0; set wokclient_priv($pathName,result) {}"
    vwait wokclient_priv($pathName,wait);
  }
}

proc wokclient_connect { {host "localhost"} {port "1563"} } {

    global wokclient_priv
    
    set wokclient_priv(CLIENTSOCK) [socket $host $port];
    set name $wokclient_priv(CLIENTSOCK)

    set newBody [info body wokclient_read]
    regsub -all NAME    $newBody $name newBody

    eval "proc wokclient_read_$name \{\} \{$newBody\}"

    fconfigure $name -blocking 0
    fconfigure $name -translation {auto lf}
    fileevent $name readable  wokclient_read_$name
}

proc wokclient_send { command } {

    global wokclient_priv

    if { ! [info exist wokclient_priv(CLIENTSOCK)] } {
	error "tcl is not connected to a WOK server"
	return;
    } 

    set wokclient_priv($wokclient_priv(CLIENTSOCK),wait)   1;
    set wokclient_priv($wokclient_priv(CLIENTSOCK),result) "";
    set wokclient_priv($wokclient_priv(CLIENTSOCK),status) 0;

    puts $wokclient_priv(CLIENTSOCK) "CMD: $command"
    puts $wokclient_priv(CLIENTSOCK) "END:"
    flush $wokclient_priv(CLIENTSOCK)

    wokclient_wait $wokclient_priv(CLIENTSOCK)

    if { $wokclient_priv($wokclient_priv(CLIENTSOCK),status) } {
	error  $wokclient_priv($wokclient_priv(CLIENTSOCK),result)
    } {
	return $wokclient_priv($wokclient_priv(CLIENTSOCK),result)
    }

}
