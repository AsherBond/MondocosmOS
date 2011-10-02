

proc WOKUtils_Replace:AdmFileType {} {
    return "dbadmfile";
}

proc WOKUtils_Replace:OutputDirTypeName {} {
    return "dbtmpfile";
}


proc WOKUtils_Replace:HandleInputFile { ID } { 

    scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name

    switch $name {
	Handle_WOKUtils_RegExp.hxx      {return 1;} 
	Handle_WOKUtils_Path.hxx        {return 1;} 
	Handle_WOKUtils_Shell.hxx       {return 1;} 
	Handle_WOKUtils_RemoteShell.hxx {return 1;} 
	WOKUtils_RegExp.hxx             {return 1;} 
	WOKUtils_Path.hxx               {return 1;} 
	WOKUtils_ShellManager.hxx       {return 1;} 
	WOKUtils_PathIterator.hxx       {return 1;} 
	WOKUtils_Shell.hxx              {return 1;} 
	WOKUtils_RemoteShell.hxx        {return 1;} 
	WOKUtils_Extension.hxx          {return 1;} 
	WOKUtils_RESyntax.hxx           {return 1;} 
	WOKUtils_Trigger.hxx            {return 1;} 
	WOKUtils_Param.hxx              {return 1;} 
	default {
	    return 0;
	}
    }
}

proc WOKUtils_Replace:Execute { unit args } {
    
    global tcl_interactive

    set tcl_interactive 1
    package require Wokutils

    msgprint -i -c "WOKUtils_Replace::Execute" "Copying of WOKUtils includes"

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

	set source    [woklocate -p WOKUtils:source:$sourcename     [wokinfo -N $unit]]
	set vistarget [woklocate -p WOKUtils:pubinclude:$name [wokinfo -N $unit]]
	set target    [wokinfo   -p pubinclude:$name          $unit]

	regsub -all "/" " $source $target" $replstr  TheArgs

	msgprint -i -c "WOKUtils_Replace::Execute" "Copy $source to $target"
	if { [file exist $target] && ( [wokparam -e %Station ] != "wnt" ) } {
	    eval exec "chmod u+w $target"
	}
	eval exec "$copycmd $TheArgs"
    }
    return 0;
}
