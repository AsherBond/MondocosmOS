;#
;# This file is used to create from scratch a minimal set of WOK entities
;# A Factory, a warehouse, a workshop and a workbench. It can be used to import
;# sources files from a previously Cascade download.
;# this script has 5 arguments:
;# HOME_ENTITIES is a directory name. This is the name where all further entities
;# will be created.
;# FNAM is a factory name ( a factory is a set of workshop and a warehouse)
;# WSNAM is a workshop name ( a workshop is dedicated to contains a workbenches tree)
;# WBNAM is a workbench name. Typically it will be the name of the root workbench.
;# IMPORT_DIR is a directory name. If IMPORT_DIR is /dev/null no import is done
;# and WBNAM is created as empty workbench. To populate it with development units
;# please refer to the Wok documentation.
;#
source $env(WOKHOME)/site/tclshrc_Wok
proc FileToList { path {sort 0} {trim 0} {purge 0} {emptl 1} } {
    if ![ catch { set id [ open $path r ] } ] {
	set l  {}
	while {[gets $id line] >= 0 } {
	    if { $trim } {
		regsub -all {[ ]+} $line " " line
	    }
	    if { $emptl } {
		if { [string length ${line}] != 0 } {
		    lappend l $line
		}
	    } else {
		lappend l $line
	    }
	}
	close $id
	if { $sort } {
	    return [lsort $l]
	} else {
	    return $l
	}
    } else {
	return {}
    }
}
;#
;#
;#
proc lreplace { fin fout ls1s2 } {
    if { [catch { set in [ open $fin r ] } errin] == 0 } {
	if { [catch { set out [ open $fout w ] } errout] == 0 } {
	    set strin [read $in [file size $fin]]
	    close $in
	    foreach [list s1 s2] $ls1s2 {
		set done 0
		if { [set nbsub [regsub -all -- $s1 $strin $s2 strout]] != 0 } {
		    set done 1
		}
		set strin $strout
	    }
	    puts $out $strout
	    close $out
	    return $done
	} else {
	    puts stderr "Error: $errout"
	    return 0
	}
    } else {
	puts stderr "Error: $errin"
	return 0
    }
}
;#
;#
proc CreateFactory { HOME_ENTITIES FNAM WSNAM WBNAM IMPORT_DIR } {

    package require Wok
    package require Ms
    global env
    
    if [catch { package require Tclx } TclXHere] {
	puts stderr "Warning : You'll need package TclX to correctly use WOK."
	puts stderr "          Check for variable TCLLIBPATH"
    }
    if [catch { package require Expect } ExpectHere] {
	puts stderr "Warning : You'll need package Expect to use WOK integration package.."
	puts stderr "          Check for variable TCLLIBPATH"
    }
    
    ;#
    set savpwd [pwd]
    
    ;#
    ;# Create factory FNAM and warehouse attached to it
    ;#
    if { ![wokinfo -x ${FNAM}] } {
	puts "Creating the factory : ${FNAM}"
	if ![catch {fcreate -DHome=$HOME_ENTITIES/${FNAM} -d ${FNAM} } astatus ] {
	    wokcd ${FNAM}
	    if { ![wokinfo -x ${FNAM}:BAG] } {
		puts "Creating the WareHouse in $HOME_ENTITIES/${FNAM}/BAG"
		if [ catch {Wcreate -DHome=$HOME_ENTITIES/${FNAM}/BAG -d -DAdm=$HOME_ENTITIES/${FNAM}/BAG/adm BAG } astatus ] {
		    puts $astatus
		    cd $savpwd
		}
	    }
	} else {
	    puts $astatus
	    cd $savpwd
	}
    }
    
    ;#
    ;# Create workshop WSNAM
    ;#
    if { ![wokinfo -x ${FNAM}:${WSNAM}] } {
	puts "Creating the workshop : ${FNAM}:${WSNAM}"
	if ![ catch {screate -DHome=$HOME_ENTITIES/${FNAM}/${WSNAM} -d ${WSNAM} } astatus] {
	    wokcd -PAdm ${FNAM}:${WSNAM} 
	} else {
	    puts $astatus
	    cd $savpwd
	}
    }
    ;#
    ;# Create workbench WBNAM 
    ;#
    if { ![wokinfo -x ${FNAM}:${WSNAM}:${WBNAM}] } {
	puts "Creating the workbench : ${FNAM}:${WSNAM}:${WBNAM}"
	wokcd ${FNAM}:${WSNAM}
	set WBROOT $HOME_ENTITIES/${FNAM}/${WSNAM}/${WBNAM}
	if [ catch {wcreate -DHome=$IMPORT_DIR -d ${WBNAM} } astatus] {
	    puts $astatus
	    cd $savpwd
	    exit
	}
    }
        
    if { "$IMPORT_DIR" != {} } {
	wokcd -PAdm ${FNAM}:${WSNAM}:${WBNAM}
	foreach udl [glob -nocomplain *.UDLIST] {
	    puts "Importing units from module $udl"
	    foreach unit [FileToList $udl] {
		if [ catch { ucreate -[lindex $unit 0] [lindex $unit 1] } astatus] {
		    puts $astatus
		}
	    }
	}
    }
    cd $savpwd
}

set CASHOME [file dirname $env(CASROOT)/.]

CreateFactory [file normalize $env(WOK_ROOTADMDIR)] OS OCC51 ros [file normalize $CASHOME]
puts "WOK initialization was done"
exit



