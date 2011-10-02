proc IDLFront_Replace:AdmFileType {} {
    return "dbadmfile";
}

proc IDLFront_Replace:OutputDirTypeName {} {
    return "dbtmpfile";
}


proc IDLFront_Replace:HandleInputFile { ID } { 

    scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name

    switch $name {
         IDL.tab.c  {return 1;} 
	default {
	    return 0;
	}
    }
}

proc IDLFront_Replace:Execute { unit args } {
    
    msgprint -i -c "IDLFront_Replace::Execute" "Copying of IDLFront derivated files  $unit $args "

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
    

	set sourcename IDL.tab.c
	set name       IDL.tab.c

	set source    [woklocate -p IDLFront:source:$sourcename     [wokinfo -N $unit]]
	set vistarget [woklocate -p IDLFront:privinclude [wokinfo -N $unit]]$name
#	set target    [wokinfo   -p IDLFront:privinclude:$name [wokinfo -N $unit]]
msgprint -i -c "$source "
	regsub -all "/" " $source $vistarget" $replstr  TheArgs


	msgprint -i -c "IDLFront_Replace::Execute" "Copy $source to $vistarget"
	if { [file exist $vistarget] && [wokparam -e %Station ] != "wnt" } {
		eval exec "chmod u+w $vistarget"
	}
	eval exec "$copycmd $TheArgs"

    return 0;
}



