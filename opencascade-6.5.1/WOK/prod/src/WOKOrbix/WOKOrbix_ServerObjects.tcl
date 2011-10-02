
proc WOKOrbix_ServerObjects:AdmFileType {} {
    
    return dbadmfile;
}

proc WOKOrbix_ServerObjects:OutputDirTypeName {} {
    return dbtmpdir;
}

proc WOKOrbix_ServerObjects:HandleInputFile { ID } {

    scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
    
    set list [split $name "_"]

    switch [lindex $list [expr [llength $list] -1 ]] {
	S.cxx { return 1;}
	i.cxx { return 1;}
    }
    return 0;
}

proc WOKOrbix_ServerObjects:Execute { unit args } {

    msgprint -i -c "WOKOrbix_ServerObjects::Execute" "Processing server objects : $unit"
    msgprint -i -c "WOKOrbix_ServerObjects::Execute"

    set unitname [wokinfo -n $unit]
 
    foreach file $args {
	stepoutputadd -R $file
	stepaddexecdepitem $file $file
    }

    return 0;
}
