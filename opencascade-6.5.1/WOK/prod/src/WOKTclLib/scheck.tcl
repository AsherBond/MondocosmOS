


proc scheck {   } {


    set errorCount 0
    set warnCount  0

    set shop      [wokinfo -s]
    set warehouse "[wokinfo -f]:[finfo -W]"

    msgprint -c "scheck" -i "Checking BAG installation"

    set parcellist   [Winfo -p $warehouse]
    set parcelconfig [sinfo -p $shop]


    msgprint -c "scheck" -i "++ Checking for parcel aggregates"

    foreach parcel $parcellist {
	
	set parcelfile [wokinfo -p admfile:$parcel.edl $warehouse]

	if { [file exists $parcelfile ] } {
	    
	    set phome [wokparam -e %${parcel}_Home]
	    
	    if { [file exists $phome] } {
		
		if { ! [info exists phomes($phome)] } {
		set phomes($phome) $parcel
		} else {
		    lappend phomes($phome) $parcel
		}
		set delivery [pinfo -d ${warehouse}:$parcel]
		set allrequisites($parcel) [wokparam -e %${delivery}_AllRequisites ${warehouse}:$parcel]
	    } else {
		msgprint -c "scheck" -e "Wrong parcel declaration for $parcel ($phome does not exist)"
		incr errorCount
	    }
	} else {
	    msgprint -c "scheck" -e "No definition file for $parcel ($parcelfile does not exists)"
	    incr errorCount
	}
	
    }
    
    set noagg 1
    foreach aggregate [array names phomes] {
	if { [llength $phomes($aggregate) ] > 1 } {
	    msgprint -c "scheck" -i "+++ Found aggregate : $aggregate containing $phomes($aggregate)"
	    set agregates($aggregate) $phomes($aggregate)
	    set noagg 0
	}
    }
    if $noagg {
	msgprint -c "scheck" -i "+++ No aggregate parcels found"
    }
    msgprint -c "scheck" -i "++"
    msgprint -c "scheck" -i "++ Checking $shop configuration"
    msgprint -c "scheck" -i "++"

    set conflicts ""

    msgprint -c "scheck" -i "+++  Checking $shop agregates usage"
    msgprint -c "scheck" -i "+++"

    foreach parcel $parcelconfig { 
	foreach agregate [array names agregates] {
	    if { [lsearch -exact $agregates($agregate) $parcel] >= 0 } {
		if [info exists usedagregates($agregate)] {
		    lappend usedagregates($agregate) $parcel
		} else {
		    set usedagregates($agregate) $parcel
		}
	    }
	}
    }
    
    foreach used [array names usedagregates] {
	msgprint -c "scheck" -i "++++ $used for $usedagregates($used)"
    }

    msgprint -c "scheck" -i "+++"
    msgprint -c "scheck" -i "+++ Checking used parcels requisites"
    msgprint -c "scheck" -i "+++"

    foreach parcel $parcelconfig { 
	
	foreach requisite [lindex $allrequisites($parcel) 0] {
	    if { [lsearch -exact $parcelconfig $requisite] == -1 } {
		msgprint -c "scheck" -e "$requisite used for $parcel is not in $shop configuration"
		incr errorCount
	    }
	}
    }
    
    msgprint -c "scheck" -i "+++"
    msgprint -c "scheck" -i "+++ Checking accurate agregates usage"
    msgprint -c "scheck" -i "+++"

    foreach parcel $parcelconfig { 
	foreach agregate [array names agregates] {
	    if { [lsearch -exact $agregates($agregate) $parcel] >= 0 } {
		foreach aggparcel $agregates($agregate) {
		    if {  [lsearch -exact $parcelconfig $aggparcel] == -1 } {
			if { [lsearch -exact $conflicts $aggparcel] == -1 } {
			    lappend conflicts $aggparcel
			    msgprint -c "scheck" -e "$aggparcel (in $agregate) should be in configuration of workshop $shop"
			    incr errorCount
			}
		    }
		}
	    }
	}
    }


    msgprint -c "scheck" -i "+++"
    msgprint -c "scheck" -i "+++ Checking accurate configuration namings"
    msgprint -c "scheck" -i "+++"

    foreach parcel $parcelconfig { 
	scan $parcel "%\[^-\]-%s" name extension
	lappend configs($extension) $name
    }


    if { [llength [array names configs]] > 1 } {

	msgprint -c "scheck" -w "More than one configuration extensions are currently in use :"
	
	foreach extension [array names configs] {
	    msgprint -c "scheck" -w "          $extension used for parcels : $configs($extension)"
	    incr warnCount
	}    

    } else {
	msgprint -c "scheck" -i "++++ $extension is the current and only configuration used"
    }
	
    if { $errorCount || $warnCount } {
	msgprint -c "scheck" -e "$errorCount errors and $warnCount warnings were found in $shop installation and configuration"
	msgprint -c "scheck" -e "Please remedy"
    } else {
	msgprint -c "scheck" -i "No installation or configuration errors was found"
    }
}
