

auto_load wok_cd_proc
auto_load wok_exit_proc
auto_load wokemacs

if { [info commands tcl_exit_proc] == "" } {
    rename exit tcl_exit_proc
    rename wok_exit_proc exit
}


set tcl_prompt1 {if {[info commands wokcd] != ""}  then {puts -nonewline stdout "[wokcd]> "} else {puts -nonewline stdout "tclsh> "}}

