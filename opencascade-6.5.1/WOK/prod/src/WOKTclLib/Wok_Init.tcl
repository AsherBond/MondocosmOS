set __initenv__ [array get env]

proc __restore_env__ {} {
  global env __initenv__
  set m [array size env]
  for {set i 0} {$i < $m} {incr i} {
    set k [array startsearch env]
    unset env([array nextelement env $k])
  }
  array set env $__initenv__
}
proc UNC { path } {

 if { [cindex $path 0] == "{" && [cindex $path [clength $path]-1] == "}" } {
  set path [crange $path 1 [clength $path]-2]
 }

 if { [cindex $path 0] == "\\" && [cindex $path 1] == "\\"} {
  set path "//[crange $path 2 [clength $path]]"
 }

 return $path

}


auto_load wok_cd_proc
auto_load wok_exit_proc
auto_load wok_source_proc
auto_load wok_setenv_proc
auto_load wokemacs

if { [info commands tcl_exit_proc] == "" } {
    rename exit tcl_exit_proc
    rename wok_exit_proc exit
}

set tcl_prompt1 {if {[info commands wokcd] != ""}  then {puts -nonewline stdout "[wokcd]> "} else {puts -nonewline stdout "tclsh> "}}

global WOK_GLOBALS;

set WOK_GLOBALS(setenv_proc,term)  1
set WOK_GLOBALS(setenv_proc,emacs) 1
set WOK_GLOBALS(setenv_proc,tcl)   1

set WOK_GLOBALS(cd_proc,term)      1
set WOK_GLOBALS(cd_proc,emacs)     1
set WOK_GLOBALS(cd_proc,tcl)       1

set WOK_GLOBALS(source_proc,term)  1
set WOK_GLOBALS(source_proc,emacs) 1
set WOK_GLOBALS(source_proc,tcl)   1
update
set WOK_GLOBALS(wokinterp,tclcommands) "Winfo|finfo|pinfo|screate|sinfo|srm|ucreate|uinfo|umake|urm|w_info|wcreate|wokcd|wokclose|wokinfo|wokparam|wokprofile|wokenv|wrm|wmove|msclear|wprepare|wstore|wintegre|upack|iwok|wsrc|wdrv|wls|wcd|cd"
update


