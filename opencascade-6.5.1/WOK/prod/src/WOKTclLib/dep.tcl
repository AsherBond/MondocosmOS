# tri topologik. retourne une liste
# Exemple wokUtils:EASY:tsort {  {a h} {b g} {c f} {c h} {d i}  }
#                           => { d a b c i g f h }
#wokUtils:EASY:tsort { listofpairs }

proc ClientTree {w fromud location meter} {
    global ClientTree_arrayofud ClientTree_arrayofimpl ClientTree_Stop ClientTree_FileName
    set ClientTree_Stop 0

    if {[winfo exist $w]} {
	destroy $w
    }
    
    if {[info exist ClientTree_arrayofud]} {
	unset ClientTree_arrayofud
    }

    if {[info exist ClientTree_arrayofimpl]} {
	unset ClientTree_arrayofimpl
    }
    
    set ClientTree_arrayofud(__uu) 0
    set ClientTree_arrayofimpl(__uu) 0

    tixTree $w
    set hlist             [$w subwidget hlist]

    $hlist config  -indicator 1 -selectmode single -separator "-" -width 30 -drawbranch 1 -indent 30

    tixForm $w -left 0 -right -0 -top 0 -bottom -0
    tixBusy $w on
    wokPROP:Meter $meter 1000 0
    update
    [wokPROP:LabClt] configure -text "Hit Escape to stop..."
    bind $hlist <Escape> {
	global ClientTree_Stop
	set ClientTree_Stop 1
	[wokPROP:LabClt] configure -text "Interrupted..."
	update
    }
    focus $hlist

    ClientTree_GetDependence $w $location $fromud $ClientTree_FileName $meter
    $w  autosetmode
    
    tixBusy $w off
    [wokPROP:LabClt] configure -text "Ready..."
    update

    return $ClientTree_arrayofud(__uu)
}

proc ClientTree_GetDependence {wtree location fromud targetinclude meter} {
    global ClientTree_arrayofud ClientTree_Stop
    
    set usetinclude 1
    set w [$wtree subwidget hlist]
    set lstud [w_info -l $location]
    set progress 0
    set maxrange [llength $lstud]

    if {$targetinclude == ""} {
	set usetinclude 0
    }

    foreach ud $lstud {
	update
	set ifile ""
	set lstofimpl {}
	set ifile [woklocate -p ${ud}:stadmfile:${ud}_obj_comp.Dep $location]

	if {$ClientTree_Stop} return

	if {$ifile != ""} {
	    set lstinc {}
	    set vcxx ""
	    set vhxx ""
	    set vsource ""
	    for_file allud $ifile {
		if {$ClientTree_Stop} return
		## we search for string like this:
		##  + Storage:object:Storage_BaseDriver.o Storage:source:Storage_BaseDriver.cxx
		##
		if {[string index $allud 0] == "+"} {
		    ## we shoot these kinds of strings:
		    ## + Storage:stadmfile:Storage_obj_comp.In Storage:admfile:Storage_src.Out
		    ## + * Storage:dbadmfile:Storage_xcpp_header.Out
		    ##
		    if {[string first "admfile:" $allud] < 0} {
			## we look for a source file including our package			
			##
			if {$vsource != "" && $lstinc != {}} {
			    lappend lstofimpl $vsource $lstinc
			    if {[$w info exist $ud] == 0} {
				$w add $ud -text $ud
				$w see $ud
			    }
			    set i 0
			    set nomsrc ""
			    regexp {([^:]*):([^:]*):([^:]*)} $vsource all av avv nomsrc
			    $w add $ud-$vsource -text $nomsrc
			    $w hide entry $ud-$vsource
			    foreach u $lstinc {
				incr i
				$w add $ud-$vsource-$i -text $u
				$w hide entry $ud-$vsource-$i
				if {$ClientTree_Stop} return
			    }
			    update
			    if {$ClientTree_Stop} return
			}		
			scan $allud "%s %s %s" vs vo vsource
			set lstinc {}
		    }
		} else {
		    update
		    if {$ClientTree_Stop} return
		    scan $allud "- * %s" vcxx
		    if {[string first :${fromud}_${ud}_ $vcxx] >= 0} {
			scan $vcxx "$ud:pubinclude:%s" vhxx
			if {$usetinclude} {
			    if {$vhxx == $targetinclude} {
				lappend lstinc $vhxx
			    }
			} else {
			    lappend lstinc $vhxx
			}
		    } elseif {[string first :${fromud}_${ud}. $vcxx] >= 0} {
			scan $vcxx "$fromud:pubinclude:%s" vhxx
			if {$usetinclude} {
			    if {$vhxx == $targetinclude} {
				lappend lstinc $vhxx
			    }
			} else {
			    lappend lstinc $vhxx
			}
		    } elseif {[string first :${fromud}. $vcxx] >= 0} {
			scan $vcxx "$fromud:pubinclude:%s" vhxx
			if {$usetinclude} {
			    if {$vhxx == $targetinclude} {
				lappend lstinc $vhxx
			    }
			} else {
			    lappend lstinc $vhxx
			}
		    } elseif {[string first :${fromud}_ $vcxx] >= 0} {
			scan $vcxx "$fromud:pubinclude:%s" vhxx
			if {$usetinclude} {			    
			    if {$vhxx == $targetinclude} {
				lappend lstinc $vhxx
			    }
			} else {
			    lappend lstinc $vhxx
			}
		    } elseif {[string first Handle_${fromud}_ $vcxx] >= 0} {
			scan $vcxx "$fromud:pubinclude:%s" vhxx
			if {$usetinclude} {
			    if {$vhxx == $targetinclude} {
				lappend lstinc $vhxx
			    }
			} else {
			    lappend lstinc $vhxx
			}
		    }
		}
	    }
	    ## we look for a source file including our package			
	    ##
	    if {$vsource != "" && $lstinc != {}} {
		lappend lstofimpl $vsource $lstinc
		if {[$w info exist $ud] == 0} {
		    $w add $ud -text $ud
		    $w see $ud
		}
		set i 0
		set nomsrc ""
		regexp {([^:]*):([^:]*):([^:]*)} $vsource all av avv nomsrc
		$w add $ud-$vsource -text $nomsrc
		$w hide entry $ud-$vsource
		foreach u $lstinc {
		    incr i
		    $w add $ud-$vsource-$i -text $u
		    $w hide entry $ud-$vsource-$i
		    if {$ClientTree_Stop} return
		}
		update
		if {$ClientTree_Stop} return
	    }
	}
	set ClientTree_arrayofud($ud) $lstofimpl
	set progress [wokPROP:Meter $meter $maxrange $progress]
    }
}
