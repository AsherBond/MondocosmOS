

proc wok_interp_command { format } {

 switch $format {
	csh {
	    return [list "/usr/bin/csh -f" ]
	}
	tcl {
	    return [list "/usr/tcltk/bin/tclsh" ]
	}
	ksh {
	    return [list "/usr/bin/ksh" ]
	}
	sh {
	    return [list "/usr/bin/sh" ]
	}
	cmd {
	    return [list "cmd.exe" ]
	}
	default {
	    error "Invalid format $format"
	}
    }
    
}


proc wok_interp_setprompt_cmd { format string } {

 switch $format {
	csh {
	    return [list "set prompt = \"$string\"\n" ]
	}
	tcl {
	    return [list "set tcl_prompt1 {puts -nonewline stdout \"$string\"}\n"]
	}
	ksh {
	    return [list "PS1=$string\n" ]
	}
	sh {
	    return [list "PS1=$string\n" ]
	}
	cmd {
	    ## Don't know how to do this
	    return [list "PROMPT $string\n"]
	}
	default {
	    error "Invalid format $format"
	}
    }
}



proc wokinterp_create_shell {format} {
    global WOK_GLOBALS spawn_id ;

    eval spawn  [lindex [wok_interp_command $format] 0]

    set WOK_GLOBALS(wokinterp,$format,id) $spawn_id

    set WOK_GLOBALS(wokinterp,$format,prompt) [format "%s: " $format]

    exp_send -i $WOK_GLOBALS(wokinterp,$format,id) -- [lindex [wok_interp_setprompt_cmd $format $WOK_GLOBALS(wokinterp,$format,prompt)] 0];

    expect {
	-i $WOK_GLOBALS(wokinterp,$format,id) 
	-re  "$WOK_GLOBALS(wokinterp,$format,prompt)$" {} 
	-re  "." {puts -nonewline stdout $expect_out(0,string);exp_continue}
    }

    wokinterp_follow_wokcd $format;
}

proc wok_end_shell {format} {
    global WOK_GLOBALS
    catch {close $WOK_GLOBALS(wokinterp,$format,id)}
    unset WOK_GLOBALS(wokinterp,$format,id);
    unset WOK_GLOBALS(wokinterp,$format,prompt);
}

proc wokinterp_follow_wokcd {format} {
    global WOK_GLOBALS
    
    set WOK_GLOBALS(wokinterp,$format,prompt) [format "%s %s\> "  [wokcd] $format]

    exp_send -i $WOK_GLOBALS(wokinterp,$format,id) -- [lindex [wok_interp_setprompt_cmd $format $WOK_GLOBALS(wokinterp,$format,prompt)] 0];
    expect   -i $WOK_GLOBALS(wokinterp,$format,id) -re "$WOK_GLOBALS(wokinterp,$format,prompt)$" {}  -re "." {exp_continue};
    exp_send -i $WOK_GLOBALS(wokinterp,$format,id) -- "\n"
}

proc woksh_usage {} {
    
    puts stderr "woksh [-format <csh|...>] -setenv"
}

proc @@ {args} {
    
    puts stderr " This service is no longer supported."
}
proc woksh {args} {
    
    global env;

#    if [info exists env(EMACS)] {
#	woksh_emacs [list $args]
#    } {
	woksh_csh [list $args]
#    }
}

proc woksh_csh {args} {
    
    global WOK_GLOBALS spawn_id user_spawn_id interact_out auto_index
    if { [llength $args] == 0 } {
	set format "csh"
    } {
	set format [lindex $args 0]
    }

    log_user 0

    set tblreq(-h)      {}
    set tblreq(-format) value_required:list
    set tblreq(-setenv) default
    set tblreq(-view)   {}
 
    ;# Parameters
    ;#
    set param {}

    if { [wokUtils:EASY:GETOPT param table tblreq woksh_usage $args] == -1 } return

    if { [info exists table(-h)] } {
	woksh_usage 
	return
    }

    if { [info exists table(-format)] } {
	set format $table(-format)
    }  {
	set format "csh"
    }

    set launched "/tmp/wokenv_[id process]_ToLaunch"
    set LAUNCH [lindex [wok_interp_command $format] 0]

    if {  [info exists table(-setenv)] } {
	
	set thefile "/tmp/wokenv_[id process]_tcl"
	wokenv -f $thefile -t tcl
	source $thefile

	set thefile "/tmp/wokenv_[id process]_$format"
	wokenv -f $thefile -t $format

	if { [catch { set Template [ open $thefile r ] } errout] == 0 } {
	    if { [catch { set Launched [ open $launched w ] } errin] == 0 } {
		puts $Launched "\#\!/usr/bin/csh -v"
		copyfile $Template $Launched
		puts $Launched "[lindex [wok_interp_setprompt_cmd $format [concat [wokcd] $format >]] 0]"
		puts $Launched "[lindex [wok_interp_command $format] 0]"
		close $Launched
		close $Template
		wokUtils:FILES:chmod 0777 $launched
		set LAUNCH $launched
	    }
	}
	wokUtils:FILES:delete $thefile
    } 

    msgprint -w -c "woksh_csh" "Emacs mode disabled : use exit to return to tcl"
    log_user 1

    spawn  $LAUNCH

    set WOK_GLOBALS(wokinterp,$format,id) $spawn_id 

    exp_send -i $spawn_id  -- "\r\n"
    wokinterp_follow_wokcd $format

    interact $spawn_id {
	eof {
	    exp_send_user -- "\n"
	}
    }
    wok_end_shell $format
    wokUtils:FILES:delete $launched
    return; 
}


proc woksh_emacs {args} {

    global WOK_GLOBALS spawn_id user_spawn_id interact_out auto_index
    
    if { [llength $args] == 0 } {
	set format "csh"
    } {
	set format [lindex $args 0]
    }
    log_user 0
    set tblreq(-h)      {}
    set tblreq(-format) value_required:list
    set tblreq(-setenv) default
    set tblreq(-view)   {}
    
    ;# Parameters
    ;#
    set param {}
    
    if { [wokUtils:EASY:GETOPT param table tblreq woksh_usage $args] == -1 } return
    
    if { [info exists table(-h)] } {
	woksh_usage 
	return
    }
    
    if { [info exists table(-format)] } {
	set format $table(-format)
    }  {
	set format "csh"
    }
    
    if { ! [info exists WOK_GLOBALS(wokinterp,$format,id)] } then  {
	msgprint -i -c "woksh_emacs" "Emacs mode enabled : use @@ to return to tcl"
	wokinterp_create_shell $format
    } {
	msgprint -i -c "woksh_emacs" "Returning to csh\n"
	wokinterp_follow_wokcd $format;	
    }
    
    log_user 1;
    
    if {  [info exists table(-setenv)] } {
	
	set thefile "/tmp/wokenv_[id process]_tcl"
	
	wokenv -f $thefile -t tcl

	source $thefile
	
	wokUtils:FILES:delete $thefile
	
	set thefile "/tmp/wokenv_[id process]_$format"
	
	wokenv -f $thefile -t $format
	
	if { [catch { set Template [ open $thefile r ] } errout] == 0 } {
	    
	    while {  ! [eof $Template] } {
		gets $Template theline
		
		set tooolong 0
		
		if { [string length $theline] > 100} {
		    set tooolong 1

		    set thelongcmdfile "/tmp/wokenv_[id process]_${format}_long"
		    
		    if { [catch { set thefd [ open $thelongcmdfile w ] } errout] == 0 } {
			puts $thefd $theline
			close $thefd
			log_user 0;
			exp_send -i $WOK_GLOBALS(wokinterp,$format,id) "[lindex [wok_source_cmd $format $thelongcmdfile] 0]\n"
			wokUtils:FILES:delete $thelongcmdfile
		    }
		    
		    while { [string length $theline] > 100} {
			exp_send_user  -- "[string range $theline 0 100]\\\n"
			set theline [string range $theline 101 [string length $theline]]
		    }
		    
		    exp_send_user -- "$theline\n" 
		    expect   -i $WOK_GLOBALS(wokinterp,$format,id) -re "$WOK_GLOBALS(wokinterp,$format,prompt)$" {} \
			    -re "." {exp_continue};
		    exp_send_user -- "$WOK_GLOBALS(wokinterp,$format,prompt)"
		    log_user 1;
		} else {
		    log_user 1;
		    exp_send -i $WOK_GLOBALS(wokinterp,$format,id) "$theline\n"
		    expect   -i $WOK_GLOBALS(wokinterp,$format,id) -re "$WOK_GLOBALS(wokinterp,$format,prompt)$" {} \
			    -re "." {exp_continue};
		}

	    }
	}
	wokUtils:FILES:delete $thefile
    } 
    log_user 0;
    
    if { ! [info exists WOK_GLOBALS(wokinterp,$format,intertacting) ] } {

	set WOK_GLOBALS(wokinterp,$format,intertacting) 1
	
	set OLD_EMACS_CDSTAT $WOK_GLOBALS(cd_proc,emacs)
	
	interact {
	    -output $WOK_GLOBALS(wokinterp,$format,id)
	    -exact "@@" {
		msgprint -i -c "woksh_emacs" "Returning to tcl\n"
		unset WOK_GLOBALS(wokinterp,$format,intertacting)
		inter_return;
	    }
	    -exact "exit" {
		exp_send_user "\n"
		wok_end_shell $format;
		unset WOK_GLOBALS(wokinterp,$format,intertacting)
		return;
	    }
	    -re $WOK_GLOBALS(wokinterp,tclcommands) {
		set cmd "$interact_out(0,string)"
		set theargs ""
		expect_user   -re ".*\n" { 
		    set theargs "$expect_out(0,string)"
		}
		if { [info exists auto_index(:$cmd)] || \
			[info exists auto_index($cmd)] || \
			[info commands $cmd] != "" } {
		    if { $cmd == "cd" } {
			set WOK_GLOBALS(cd_proc,emacs) 0
			set cmd "wok_cd_proc"
		    } 
		    catch "$cmd $theargs" result
		    set WOK_GLOBALS(cd_proc,emacs) $OLD_EMACS_CDSTAT
		    if { $result != "" } {
			puts $result;
		    }
		    wokinterp_follow_wokcd $format;
		} {
		    exp_send -i  $WOK_GLOBALS(wokinterp,$format,id)  -- "$cmd $theargs";
		}
	    }
	    "\n" {
		exp_send -i  $WOK_GLOBALS(wokinterp,$format,id) -- "\n";
	    }
	    -input $WOK_GLOBALS(wokinterp,$format,id)
	    eof {
		exp_send_user -- "\n"
		wok_end_shell $format;
		unset WOK_GLOBALS(wokinterp,$format,intertacting)
		return;
	    }
	}
	catch "unset WOK_GLOBALS(wokinterp,$format,intertacting)" 
    }
    return; 
}
