
proc WOKStep_ccl:AdmFileType {} {
    
    return admfile;
}

proc WOKStep_ccl:OutputDirTypeName {} {
    return tmpdir;
}

proc WOKStep_ccl:HandleInputFile { ID } {

    scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
    
    switch [file extension $name] {
	.ccl {
	    return 1;
	} 
	.us {
	    return 1;
	}
	.fr {
	    return 1;
	}
	.ja {
	    return 1;
	}
	default {
	    return 0;
	}
    }
}

proc WOKStep_ccl:Execute { unit args } {

    msgprint -i -c "WOKStep_ccl:Execute" "Processing unit : $unit"
    msgprint -i -c "WOKStep_ccl:Execute"

    set unitname [wokinfo -n $unit]
    set targetid "$unitname:ccldrv:$unitname.ccl"
    set target [wokinfo -p ccldrv:$unitname.ccl $unit]

    catch {unset tab}
    catch {unset idtab}
    catch {unset tabmess}
    catch {unset idtabmess}

    foreach ID $args {
	scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
	set ext [file extension $name]
	if {$ext == ".ccl"} {
	    lappend idtab($ext) $ID
	    lappend tab($ext)   [woklocate -p $ID $unit]
	} else {
	    if {[string match "*_Msg$ext" $name]} {
		lappend idtabmess($ext) $ID
		lappend tabmess($ext)   [woklocate -p $ID $unit]
	    } else {
		lappend idtab($ext) $ID
		lappend tab($ext)   [woklocate -p $ID $unit]
	    }
	}
    }

    foreach l [array names tab] {

	if {$l == ".ccl"} {
	    set res [wokinfo -p ccldrv:$unitname${l} $unit ]
	    set resid $unitname:ccldrv:$unitname${l}
	} else {
	    set res [wokinfo -p msgfile:$unitname${l} $unit ]
	    set resid $unitname:msgfile:$unitname${l}
	}

	msgprint -i "Creating $res"
	if [file exists $res ] {
	   unlink $res
	}

	foreach file $tab($l) {
	    msgprint -i "Adding $file"
	}

	wokUtils:FILES:concat $res $tab($l)

	stepoutputadd -M -P -L -F $resid

	foreach id $idtab($l) {
	    stepaddexecdepitem -d $id $resid
	}

	foreach id $idtab($l) {
	    stepaddexecdepitem -d $id $resid
	}
	
    }

    foreach l [array names tabmess] {

	set res [wokinfo -p msgfile:${unitname}_Msg${l} $unit ]
	set resid $unitname:msgfile:${unitname}_Msg${l}

	msgprint -i "Creating $res"
	if [file exists $res ] {
	   unlink $res
	}

	foreach file $tabmess($l) {
	    msgprint -i "Adding $file"
	}

	wokUtils:FILES:concat $res $tabmess($l)

	stepoutputadd -M -P -L -F $resid

	foreach id $idtabmess($l) {
	    stepaddexecdepitem -d $id $resid
	}

	foreach id $idtabmess($l) {
	    stepaddexecdepitem -d $id $resid
	}
	
    }


    return 0;
}
