
set wokemacs_priv(SERVERPORT) 1563
set wokemacs_priv(lispfile)   $env(HOME)/wokemacs/wok-comm.el
set wokemacs_priv(debug) 0
set wokemacs_priv(timeout) 10000

if { [array names wokemacs_priv initialized] == "" } {
    set wokemacs_priv(initialized) 0
}

set client ""

proc wokemacs_handle {} {
}

proc wokemacs_read { } {

  wokemacs_read_data NAME;
}

proc wokemacs_read_data {pathName } {

    global wokemacs_priv;

    set readData "";
    set wokemacs_priv($pathName,result) "";
    
    set fileId $wokemacs_priv($pathName);

    if { [eof $fileId] } {
	puts "wokemacs : lost controller of $pathName"
	
	close  $fileId

	set clidx [lsearch $wokemacs_priv(clients) $pathName ]
	
	if { $clidx != -1 } {
	    if { $clidx == 0 } {
		set wokemacs_priv(clients) {};
	    } {
		set wokemacs_priv(clients) [lreplace $wokemacs_priv(clients) $clidx $clidx]
	    }
	} 

	if { [info exists wokemacs_priv($pathName)] }        {unset wokemacs_priv($pathName)}
	if { [info exists wokemacs_priv($pathName,wait)] }   {unset wokemacs_priv($pathName,wait)}
	if { [info exists wokemacs_priv($pathName,result)] } {unset wokemacs_priv($pathName,result)}
	if { [info exists wokemacs_priv($pathName,status)] } {unset wokemacs_priv($pathName,status)}
	
	return;
    }


    set context [scancontext create]

    scanmatch $context "CMD: (.*)$" {
	set theCmd $matchInfo(submatch0);

	if {$wokemacs_priv(debug)} {
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

	set wokemacs_priv($pathName,status) 1;
	set wokemacs_priv($pathName,result) $theError
	set wokemacs_priv($pathName,wait)   0;

	if {$wokemacs_priv(debug)} {
	    puts stdout "Received Error : $theError";
	}
    }

    scanmatch $context "RET: (.*)$" {
	set theReturn $matchInfo(submatch0);

	if {$wokemacs_priv(debug)} {
	    puts stdout "Received Return : $theReturn";
	}
	lappend wokemacs_priv($pathName,result) $theReturn;
    }

    scanmatch $context "END:" {
	if {$wokemacs_priv(debug)} {
	    puts stdout "Sending END:"
	}
	puts $fileId "END:"
	flush $fileId 
	set wokemacs_priv($pathName,wait) 0;
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

proc wokemacs_wait {pathName} {

  global wokemacs_priv;

  if {$wokemacs_priv($pathName,wait)} {
    after $wokemacs_priv(timeout) "set wokemacs_priv($pathName,wait) 0; set wokemacs_priv($pathName,result) {}"
    vwait wokemacs_priv($pathName,wait);
  }
}

proc wokemacs_send_command {pathName cmd} {

    global wokemacs_priv

    
    set fileId $wokemacs_priv($pathName)
    
    if { [eof $fileId] } {
	error "wokemacs : tried to access a closed socket"
    } 

    puts  $fileId "CMD: $cmd"
    puts  $fileId "END:"
    flush $fileId 
    
    set wokemacs_priv($pathName,wait)   1;
    set wokemacs_priv($pathName,result) "";
    set wokemacs_priv($pathName,status) 0;

    wokemacs_wait $pathName

    if { $wokemacs_priv($pathName,status) } {
	error  $wokemacs_priv($pathName,result)
    } {
	return $wokemacs_priv($pathName,result)
    }
}

    

proc wokemacs_accept {name address clientport} {
    
    global wokemacs_priv

    set newBody [info body wokemacs_read]
    
    regsub -all NAME    $newBody $clientport newBody

    eval "proc wokemacs_read_$name \{\} \{$newBody\}"

    if {$wokemacs_priv(debug)} {
	puts stdout "Name : $name"
	puts stdout "address : $address"
	puts stdout "clientport : $clientport"
    }

    set wokemacs_priv($clientport) $name

    fconfigure $name -blocking 0
    fconfigure $name -translation {auto lf}
    fileevent $name readable  wokemacs_read_$name
    lappend wokemacs_priv(clients) $clientport
    close $wokemacs_priv(SERVERSOCK)
    return;
}


proc wokemacs_create_server_sock { port } {
    
    global wokemacs_priv

    puts $port
    set wokemacs_priv(SERVERPORT) $port
    set wokemacs_priv(SERVERSOCK) [socket -server wokemacs_accept $port];
}    

proc wokemacs_init { port } {
    
    global wokemacs_priv

    puts $port

    set wokemacs_priv(SERVERPORT) $port
    set wokemacs_priv(SERVERSOCK) [socket -server wokemacs_accept $port];
    set wokemacs_priv(clients) "";
    set wokemacs_priv(initialized) 1;
#JMB
    vwait wokemacs_priv(clients);
}    

proc wokemacs_create {} {
    
    global wokemacs_priv

    if { ! $wokemacs_priv(initialized) } {
	error "wokemacs is not initialized"
    }
    
    set nbclients [llength $wokemacs_priv(clients)]

    set newCommand "emacs"
    append newCommand { -rn WOKEMACS -l $wokemacs_priv(lispfile) -wokwidget WOKEMACS localhost $wokemacs_priv(SERVERPORT)} ;
    
    eval "exec $newCommand &"

    after $wokemacs_priv(timeout) "set wokemacs_priv(clients) $wokemacs_priv(clients);"
    vwait wokemacs_priv(clients)

    if {  $nbclients == [llength $wokemacs_priv(clients)] } {
	error "wokemacs : new emacs could not be created"
    } {
	return [lindex $wokemacs_priv(clients) [expr [llength $wokemacs_priv(clients)] -1]]
    }
}


proc wokclient_connect { {host "localhost"} {port "1563"} } {

    global wokemacs_priv
    
    set wokemacs_priv(CLIENTSOCK) [socket $host $port];
    set name $wokemacs_priv(CLIENTSOCK)

    set newBody [info body wokemacs_read_server_name]
    regsub -all NAME    $newBody $name newBody

    eval "proc wokemacs_read_$name \{\} \{$newBody\}"

    fileevent $name readable  wokemacs_read_$name
}

proc wokclient_send { command } {

    global wokemacs_priv

    if { ! [info exist wokemacs_priv(CLIENTSOCK)] } {
	error "tcl is not connected to a WOK server"
	return;
    } 

    puts $wokemacs_priv(CLIENTSOCK) "CMD: $command"
    puts $wokemacs_priv(CLIENTSOCK) "END:"
    flush $wokemacs_priv(CLIENTSOCK)

    wokemacs_wait $wokemacs_priv(CLIENTSOCK)

    return $wokemacs_priv($wokemacs_priv(CLIENTSOCK),result)
}

proc wokemacs {args} {
    
    global wokemacs_priv;

    if { [llength $args] == 0} {
	puts "usage : wokemacs \[init|create|clients...\]"
	error;
    }
    set minorCommand [lindex $args 0]

    case $minorCommand {
	
	{init} {
	    if { $wokemacs_priv(initialized) } {
		error "wokemacs is already initialized"
	    }
	    if { [llength $args] == 2 } {
		puts "[lindex $args 2]";
		wokemacs_init [lindex $args 2];
	    } {
		wokemacs_init $wokemacs_priv(SERVERPORT);
	    }
	}
	{newserver} {
	    wokemacs_create_server_sock 1563
	}
	{create} {
	    if { ! $wokemacs_priv(initialized) } {
		error "wokemacs is not initialized"
	    }
	    return [wokemacs_create];
	}
	{clients} {
	    if { ! $wokemacs_priv(initialized) } {
		error "wokemacs is not initialized"
		return {}
	    }
	    return  $wokemacs_priv(clients)
	}
	{sendcmd} {
	    if { ! $wokemacs_priv(initialized) } {
		error "wokemacs is not initialized"
	    }
	    if { [llength $args] != 3} {
		error "missing args"
	    }
	    return [wokemacs_send_command [lindex $args 1] [lindex $args 2]]
	}
	{findfile} {
	    if { ! $wokemacs_priv(initialized) } {
		error "wokemacs is not initialized"
	    }
	    if { [llength $args] != 3} {
		error "missing args"
	    }
	    set thecmd "(find-file \"[lindex $args 2]\")"
	    return [wokemacs_send_command [lindex $args 1] $thecmd]
	}
    }
}
