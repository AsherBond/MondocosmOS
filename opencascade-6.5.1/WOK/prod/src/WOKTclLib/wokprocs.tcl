

proc wok_cd_cmd { format dir } {
    
    switch $format {
	csh {
	    return [list "cd $dir\n" ]
	}
	tcl {
	    return [list "cd $dir\n" ]
	}
	ksh {
	    return [list "cd $dir\n" ]
	}
	emacs {
	    return [list "(progn (shell-cd \"$dir\") (shell-dirstack-message))";]
	}
	cmd {
	    return [list "cd $dir\n" ]
	}
	default {
	    error "Invalid format $format"
	}
    }
 
}


proc wok_exit_cmd { format } {

    switch $format {
	csh {
	    return [list "exit\n"]
	}
	tcl {
	    return [list "exit\n"]
	}
	ksh {
	    return [list "exit\n"]
	}
	emacs {
	    return [list "(save-buffers-kill-emacs)"]
	}
	cmd {
	    return [list "exit\n"]
	}
	default {
	    error "Invalid format $format"
	}
    }
}

proc wok_setenv_cmd { format var value } {

    switch $format {
	csh {
	    return [list "setenv $var \"$value\"\n"]
	}
	tcl {
	    return [list "global env \n set env($var) \"$value\"\n"]
	}
	ksh {
	    return [list "$var=\"$value\"\nexport $var\n"]
	}
	sh  {
	    return [list "$var=\"$value\"\nexport $var\n"]
	}
	emacs {
	    return [list "(woksh-setenv \"$var\" \"$value\")"]
	}
	cmd {
	    return [list "set $var=\"$value\"\n"]
	}
	default {
	    error "Invalid format $format"
	}
    }
}


proc wok_source_cmd { format file } {

    switch $format {
	sh {
	    return [list ". $file\n"]
	}
	csh {
	    return [list "source $file\n"]
	}
	tcl {
	    return [list "source $file\n"]
	}
	ksh {
	    return [list ". $file\n"]
	}
	emacs {
	    return [list "(load-file \"$file\")"]
	}
	cmd {
	    return [list "call $file\n"]
	}
	default {
	    error "Invalid format $format"
	}
    }
}


proc wok_cd_proc {args} {
    
    global IWOK_GLOBALS
    global WOK_GLOBALS
    global wokemacs_priv;

    set CLIENTS "";

    set dir "$args"

    if { $WOK_GLOBALS(cd_proc,emacs) } {
	if { ! [catch {wokemacs clients} CLIENTS] } {
	    foreach client [wokemacs clients] {
		wokemacs sendcmd $client [lindex [wok_cd_cmd emacs $dir] 0]
	    }
	}
    }
    
    if { $WOK_GLOBALS(cd_proc,term) } {
	if { [info exists IWOK_GLOBALS(term,started)] } {
	    if { $IWOK_GLOBALS(term,started) } {
		exp_send -i $IWOK_GLOBALS(term,term_spawn_id) [lindex [wok_cd_cmd csh $dir] 0];
	    }
	}
	if { [info exists WOK_GLOBALS(wokinterp,csh,id)] } {
	    exp_send -i $WOK_GLOBALS(wokinterp,csh,id) [lindex [wok_cd_cmd csh $dir] 0];
	    expect   -i $WOK_GLOBALS(wokinterp,csh,id)  -exact $WOK_GLOBALS(wokinterp,csh,prompt);
	}
    }

    
    
    if {  $WOK_GLOBALS(cd_proc,tcl) } {
	catch {eval [lindex [wok_cd_cmd tcl $dir] 0]}
    }
    return;
}

proc wok_exit_proc {args} {
    
    global wokemacs_priv;
    global IWOK_GLOBALS
    global WOK_GLOBALS


    #
    ## Close term connection
    #
    if { [info exists IWOK_GLOBALS(term,started)] } {
	if {  $IWOK_GLOBALS(term,started) } {
	    close -i $IWOK_GLOBALS(term,term_spawn_id)
	}
    }
    
    #
    ## Close Emacs connection
    #
    set CLIENTS "";
    if { ! [catch {wokemacs clients} CLIENTS] } {

	foreach client $CLIENTS {
	    puts "Closing client $client"
	    close $wokemacs_priv($client)
	}
    }
    if { $args == "" } {
	tcl_exit_proc 0;
    } {
	tcl_exit_proc $args
    }
}


proc wok_setenv_proc {var value} {

    global IWOK_GLOBALS;
    global WOK_GLOBALS;
    global wokemacs_priv;
    global env;
    
    set limit 200;

    if { $WOK_GLOBALS(setenv_proc,emacs) } {
	if { ! [catch {wokemacs clients} CLIENTS] } {
	    foreach client [wokemacs clients] {
		wokemacs sendcmd $client [lindex [wok_setenv_cmd emacs $var $value] 0]
	    }
	}
    }
    
    if { $WOK_GLOBALS(setenv_proc,term)  } {
	if { [info exists IWOK_GLOBALS(term,started)] } {
	    if { $IWOK_GLOBALS(term,started) } {

		# First Initialize Variable
		set len [string length $value]
		set debut 0

		set i $limit
		set sub [string range $value $debut $i]
		set command  [lindex [wok_setenv_cmd csh $var $value] 0]
		exp_send -i $IWOK_GLOBALS(term,term_spawn_id) $command;
		sleep .1
		incr i
		update

		while { $i < $len } {

		    # Then Append trailing
		    set debut $i
		    set i [expr $i + $limit]
		    
		    set sub [string range $value $debut $i]
		    exp_send -i $IWOK_GLOBALS(term,term_spawn_id) "setenv $var \"\${$var}$sub\"\n"
		    sleep .1
		    incr i
		    update
		}
	    }
	}
    }
    
    if {  $WOK_GLOBALS(setenv_proc,tcl) } {
	eval [lindex [wok_setenv_cmd tcl $var $value] 0]
    }
    update
}    

proc wok_source_proc {type file} {

    global IWOK_GLOBALS;
    global WOK_GLOBALS;
    global env;

    switch $type {
	csh {
    
	    if { [info exists WOK_GLOBALS(source_proc,term) ] } {
		if { $WOK_GLOBALS(source_proc,term) } {
		    if { [info exists IWOK_GLOBALS(term,started)] } {
			if { $IWOK_GLOBALS(term,started) } {
			    exp_send -i $IWOK_GLOBALS(term,term_spawn_id) [lindex [wok_source_cmd csh $file] 0];
			}
		    }
		}
	    }
	}
	tcl {
	    eval [lindex [wok_source_cmd tcl $file] 0]
	}
	emacs {
	    if { $WOK_GLOBALS(setenv_proc,emacs) } {
		if { ! [catch {wokemacs clients} CLIENTS] } {
		    foreach client [wokemacs clients] {
			wokemacs sendcmd $client [lindex [wok_source_cmd emacs $file] 0]
		    }
		}
	    }
	}
	default {
	}
    }
    update
}    
