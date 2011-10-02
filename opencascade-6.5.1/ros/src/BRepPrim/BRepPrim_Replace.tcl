

proc BRepPrim_Replace:AdmFileType {} {
    return "dbadmfile";
}

proc BRepPrim_Replace:OutputDirTypeName {} {
    return "dbtmpfile";
}


proc BRepPrim_Replace:HandleInputFile { ID } { 

    scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name

    switch $name {
	BRepPrim_OneAxis.hxx {return 1;}
	default {
	    return 0;
	}
    }
}

proc BRepPrim_Replace:Execute { unit args } {
    
    global tcl_interactive

    set tcl_interactive 1
    package require Wokutils

    msgprint -i -c "BRepPrim_Replace:Execute" "Copying of BRepPrim includes"

    if { [wokparam -e %Station $unit] != "wnt" } {
	set copycmd "cp -p "
	set replstr "/"
    } {
	set copycmd "cmd /c copy"
	set replstr "\\\\\\\\"
    }
    
    foreach file  $args {
	scan $file "%\[^:\]:%\[^:\]:%\[^:\]"  Unit type name
	
	regsub ".hxx" $name ".hxx" sourcename

	set source    [woklocate -p BRepPrim:source:$sourcename     [wokinfo -N $unit]]
	set vistarget [woklocate -p BRepPrim:pubinclude:$name [wokinfo -N $unit]]
	set target    [wokinfo   -p pubinclude:$name          $unit]

	regsub -all "/" " $source $target" $replstr  TheArgs

	msgprint -i -c "BRepPrim_Replace:Execute" "Copy $source to $target"
	if { [file exist $target] && [wokparam -e %Station] != "wnt" } {
		eval exec "chmod u+w $target"
	}
	eval exec "$copycmd $TheArgs"

    }
    return 0;
}
