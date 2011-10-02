proc EDL_Replace:AdmFileType {} {
    return "dbadmfile";
}

proc EDL_Replace:OutputDirTypeName {} {
    return "dbtmpfile";
}


proc EDL_Replace:HandleInputFile { ID } { 

    scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
    
    switch $name {
         EDL.tab.c  {return 1;} 
	default {
	    return 0;
	}
    }
}

proc EDL_Replace:Execute { unit args } {
    
    msgprint -i -c "EDL_Replace::Execute" "Copying of EDL derivated files  $unit $args "

    global tcl_interactive

    set tcl_interactive 1
    package require Wokutils


    if { [wokparam -e %Station $unit] != "wnt" } {
	set copycmd "cp -p "
	set replstr "/"
    } else {
	set copycmd "cmd /c copy"
	set replstr "\\\\\\\\"
    }
    

	set sourcename EDL.tab.c
	set name       EDL.tab.c

	set source    [woklocate -p EDL:source:$sourcename     [wokinfo -N $unit]]
	set vistarget [woklocate -p EDL:privinclude [wokinfo -N $unit]]$name
#	set target    [wokinfo   -p EDL:privinclude:$name [wokinfo -N $unit]]
        msgprint -i -c "$source "
        regsub -all "/" " $source $vistarget" $replstr  TheArgs

        msgprint -i -c "EDL_Replace::Execute" "Copy $source to $vistarget"
        if { [file exist $vistarget] && [wokparam -e %Station ] != "wnt" } {
		eval exec "chmod u+w $vistarget"
	}
	eval exec "$copycmd $TheArgs"

    return 0;
}



