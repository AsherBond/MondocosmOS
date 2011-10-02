

proc WOKTools_Replace:AdmFileType {} {
    return "dbadmfile";
}

proc WOKTools_Replace:OutputDirTypeName {} {
    return "dbtmpfile";
}


proc WOKTools_Replace:HandleInputFile { ID } { 

    scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name

    switch $name {
	WOKTools_Message.hxx           {return 1;} 
	WOKTools_Options.hxx           {return 1;} 
	default {
	    return 0;
	}
    }
}

proc WOKTools_Replace:Execute { unit args } {
    
    global tcl_interactive

    set tcl_interactive 1
    package require Wokutils

    msgprint -i -c "WOKTools_Replace::Execute" "Copying of WOKTools includes"

    if { [wokparam -e %Station $unit] != "wnt" } {
	set copycmd "cp -p "
	set replstr "/"
    } {
	set copycmd "cmd /c copy"
	set replstr "\\\\\\\\"
    }
    
    foreach file  $args {
	scan $file "%\[^:\]:%\[^:\]:%\[^:\]"  Unit type name
	
	regsub ".hxx" $name "_proto.hxx" sourcename

	set source    [woklocate -p WOKTools:source:$sourcename     [wokinfo -N $unit]]
	set vistarget [woklocate -p WOKTools:pubinclude:$name [wokinfo -N $unit]]
	set target    [wokinfo   -p pubinclude:$name          $unit]

	regsub -all "/" " $source $target" $replstr  TheArgs

	msgprint -i -c "WOKTools_Replace::Execute" "Copy $source to $target"
	if { [file exist $target] && [wokparam -e %Station] != "wnt" } {
		eval exec "chmod u+w $target"
	}
	 eval exec "$copycmd $TheArgs"

    }
    return 0;
}
