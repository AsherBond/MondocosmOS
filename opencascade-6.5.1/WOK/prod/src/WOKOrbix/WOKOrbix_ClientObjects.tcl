
proc WOKOrbix_ClientObjects:AdmFileType {} {
    
    return dbadmfile;
}

proc WOKOrbix_ClientObjects:OutputDirTypeName {} {
    return dbtmpdir;
}

proc WOKOrbix_ClientObjects:HandleInputFile { ID } {

    scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
    
    set list [split $name "_"]

    switch [lindex $list [expr [llength $list] -1 ]] {
	C.cxx { return 1;}
    }
    return 0;
}

proc WOKOrbix_ClientObjects:Execute { unit args } {

    msgprint -i -c "WOKOrbix_ClientObjects::Execute" "Processing client objects : $unit"
    msgprint -i -c "WOKOrbix_ClientObjects::Execute"

    set unitname [wokinfo -n $unit]
 
    foreach file $args {
	stepoutputadd -R $file
	stepaddexecdepitem $file $file
    }

    return 0;
}
