## Message-Id: <199703122050.MAA13073@cs.uoregon.edu>
## To: Jim Graham <jim@n5ial.gnt.com>
## Subject: Re: loading of image in tk 
## Date: Wed, 12 Mar 1997 12:50:06 -0800
## From: Jeffrey Hobbs <jhobbs@cs.uoregon.edu>

# Modified by Jim Graham...still a lot left to do, but.....  Thanks
# to Brent Welch for the help with getting my enhancements to work,
# as well as for flat out showing me how to do some of them.  :-)
#
# Thanks again to Brent Welch for helping me with yet another bug
# (one that I looked straight at, and couldn't see...).


proc unknown {args} {

   global auto_noexec auto_noload env unknown_pending tcl_interactive
   global errorCode errorInfo

# Save the values of errorCode and errorInfo variables, since they
# may get modified if caught errors occur below.  The variables will
# be restored just before re-executing the missing command.

   set savedErrorCode $errorCode
   set savedErrorInfo $errorInfo
   set name [lindex $args 0]
   if ![info exists auto_noload] {
#
# Make sure we're not trying to load the same proc twice.
#
      if [info exists unknown_pending($name)] {
         return -code error "self-referential recursion in \"unknown\" for command \"$name\"";
      }
      set unknown_pending($name) pending;
      set ret [catch {auto_load $name} msg]
      unset unknown_pending($name);
      if {$ret != 0} {
         return -code $ret -errorcode $errorCode  "error while autoloading \"$name\": $msg"
      }
      if ![array size unknown_pending] {
         unset unknown_pending
      }
      if $msg {
         set errorCode $savedErrorCode
         set errorInfo $savedErrorInfo
         set code [catch {uplevel 1 $args} msg]
         if {$code ==  1} {
# Strip the last five lines off the error stack (they're
# from the "uplevel" command).
            set new [split $errorInfo \n]
            set new [join [lrange $new 0 [expr [llength $new] - 6]] \n]
            return -code error -errorcode $errorCode  -errorinfo $new $msg
         } else {
            return -code $code $msg
         }
      }
   }

# *FIND ME*
# This is the original
# {([info level] == 1) && ([info script] == "")  && [info exists tcl_interactive] && $tcl_interactive}

   if {([info level] == 1)} {
      if ![info exists auto_noexec] {
         set new [auto_execok $name]
         if {$new != ""} {
            set errorCode $savedErrorCode
            set errorInfo $savedErrorInfo
            set redir ""
            if {[info commands console] == ""} {
               set redir ">&@stdout <@stdin"
            }
            return [uplevel exec $redir $new [lrange $args 1 end]]
         }
      }
      set errorCode $savedErrorCode
      set errorInfo $savedErrorInfo
      if {$name == "!!"} {
         set newcmd [history event]
      } elseif {[regexp {^!(.+)$} $name dummy event]} {
         set newcmd [history event $event]
      } elseif {[regexp {^\^([^^]*)\^([^^]*)\^?$} $name dummy old new]} {
         set newcmd [history event -1]
         catch {regsub -all -- $old $newcmd $new newcmd}
      }
      if [info exists newcmd] {
         tclLog $newcmd
         history change $newcmd 0
         return [uplevel $newcmd]
      }

      set ret [catch {set cmds [info commands $name*]} msg]
      if {[string compare $name "::"] == 0} { set name "" }
      if {$ret != 0} {
         return -code $ret -errorcode $errorCode  "error in unknown while checking if \"$name\" is a unique command abbreviation: $msg"
      }
      if {[llength $cmds] == 1} {
         return [uplevel [lreplace $args 0 0 $cmds]]
      }
      if {[llength $cmds] != 0} {
         if {$name == ""} {
            return -code error "empty command name \"\""
         } else {
            return -code error  "ambiguous command name \"$name\": [lsort $cmds]"
         }
      }
   }
   return -code error "invalid command name \"$name\""
}


# ------------------------------  CUT HERE  ------------------------------ #
############################################################################

# Back to interp.tcl!

set long_command ""

if ![info exists tcl_prompt1] {
  if {[info exists jstrackrc]} {
      set tcl_prompt1 {puts -nonewline "JStrack ([history nextid]) % "}
   } elseif {[info exists tk_version]} {
      set tcl_prompt1 {puts -nonewline "wish ([history nextid]) % "}
   } else {
      set tcl_prompt1 {puts -nonewline "tclsh ([history nextid]) % "}
   }
}

proc read_stdin {} {
  global eventLoop tcl_prompt1 long_command jdg_hist_file
  set l [gets stdin]
  if {[eof stdin]} {
     set eventLoop "done"     ;# terminate the vwait (eventloop)
  } else {
     if [string compare $l ""] {
	append long_command "$l"
	set l $long_command
	if {[info complete $l]} {
	   if [catch {uplevel \#0 history add [list $l] exec} err] {
	      puts stderr $err
	   } elseif {[string compare $err ""]} {
	      puts $err
	   } else {
              if {[info exists jdg_hist_file]} {
                 set f [open $jdg_hist_file a]
                 puts $f $l
                 close $f
              }
           }
	    set long_command ""
	    catch $tcl_prompt1
	} else {
	    append long_command \n
	    puts -nonewline "> "
	}
     } elseif {[string compare $long_command ""] == 0} {
	catch $tcl_prompt1
     } else {
	puts -nonewline "> "
     }
  flush stdout
  }
}

# set up our keyboard read event handler:
# Vector stdin data to the socket

fileevent stdin readable read_stdin

catch $tcl_prompt1
flush stdout
# wait for and handle or stdin events...
vwait eventLoop



