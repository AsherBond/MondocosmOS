proc WOKDeliv_Replace:AdmFileType {} {
    return "dbadmfile";
}

proc WOKDeliv_Replace:OutputDirTypeName {} {
    return "dbtmpfile";
}


proc WOKDeliv_Replace:HandleInputFile { ID } { 

    scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
    
    switch $name {
         DELIVERY.tab.c  {return 1;} 
	default {
	    return 0;
	}
    }
}

proc WOKDeliv_Replace:Execute { unit args } {
    
    msgprint -i -c "WOKDeliv_Replace::Execute" "Copying of WOKDeliv derivated files  $unit $args "

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
    
    ## traiting DELIVERY.tab.c

    set sourcename DELIVERY.tab.c
    set name       DELIVERY.tab.c

    set source    [woklocate -p WOKDeliv:source:$sourcename     [wokinfo -N $unit]]
    set vistarget [woklocate -p WOKDeliv:privinclude [wokinfo -N $unit]]$name
#	set target    [wokinfo   -p WOKDeliv:privinclude:$name [wokinfo -N $unit]]
    msgprint -i -c "$source "
    regsub -all "/" " $source $vistarget" $replstr  TheArgs

    msgprint -i -c "WOKDeliv_Replace::Execute" "Copy $source to $vistarget"
    if { [file exist $vistarget] && [wokparam -e %Station ] != "wnt" } {
	eval exec "chmod u+w $vistarget"
    }
    eval exec "$copycmd $TheArgs"


    return 0;
}



