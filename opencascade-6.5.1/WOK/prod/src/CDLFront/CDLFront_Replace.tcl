proc CDLFront_Replace:AdmFileType {} {
    return "dbadmfile";
}

proc CDLFront_Replace:OutputDirTypeName {} {
    return "dbtmpfile";
}


proc CDLFront_Replace:HandleInputFile { ID } { 

    scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
    
    switch $name {
         CDL.tab.c  {return 1;} 
         CDL.tab.h  {return 1;} 
	default {
	    return 0;
	}
    }
}

proc CDLFront_Replace:Execute { unit args } {
    
    msgprint -i -c "CDLFront_Replace::Execute" "Copying of CDLFront derivated files  $unit $args "

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
    
    ## traiting CDL.tab.c

    set sourcename CDL.tab.c
    set name       CDL.tab.c

    set source    [woklocate -p CDLFront:source:$sourcename     [wokinfo -N $unit]]
    set vistarget [woklocate -p CDLFront:privinclude [wokinfo -N $unit]]$name
#	set target    [wokinfo   -p CDLFront:privinclude:$name [wokinfo -N $unit]]
    msgprint -i -c "$source "
    regsub -all "/" " $source $vistarget" $replstr  TheArgs

    msgprint -i -c "CDLFront_Replace::Execute" "Copy $source to $vistarget"
    if { [file exist $vistarget] && [wokparam -e %Station ] != "wnt" } {
	eval exec "chmod u+w $vistarget"
    }
    eval exec "$copycmd $TheArgs"

    ## traiting CDL.tab.h

    set name       CDL.tab.h
    set source    [woklocate -p CDLFront:source:$name     [wokinfo -N $unit]]
    set vistarget [woklocate -p CDLFront:pubinclude [wokinfo -N $unit]]$name
    msgprint -i -c "$source "
    regsub -all "/" " $source $vistarget" $replstr  TheArgs

    msgprint -i -c "CDLFront_Replace::Execute" "Copy $source to $vistarget"
    if { [file exist $vistarget] && [wokparam -e %Station ] != "wnt" } {
	eval exec "chmod u+w $vistarget"
    }
    eval exec "$copycmd $TheArgs"



    return 0;
}



