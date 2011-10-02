proc DependenceTree {w fromud location} {
    global arrayofud arrayoftk lsttk nbud arrayofimpl

    set lsttk [w_info -k $location]
    if {[winfo exist $w]} {
	destroy $w
    }
    
    if {[info exist arrayofud]} {
	unset arrayofud
    }
    if {[info exist arrayoftk]} {
	unset arrayoftk
    }
    
    if {[info exist arrayofimpl]} {
	unset arrayofimpl
    }
    
    set arrayofud(__uu) 0
    set arrayoftk(__uu) 1
    set arrayofimpl(__uu) 0

    tixTree $w

    set hlist             [$w subwidget hlist]
    $hlist config  -indicator 1 -selectmode single -separator "." -width 30 -drawbranch 1 -indent 30
    $hlist config -browsecmd [list DependenceTree_BrowseCommand $location] ;# Yan

    tixForm $w -left 0 -right -0 -top 0 -bottom -0
    DependenceTree_fillarb $w $fromud $fromud 0 $location
    $w  autosetmode
    set popu [tixPopupMenu $w.poph] 
    $popu configure -postcmd [list DependenceTree_PostCommand $popu $w]
    $popu subwidget menu configure -font [tix option get fixed_font]        
    $popu subwidget menubutton configure -font [tix option get fixed_font]  
    $popu bind $hlist 
    
    return $arrayofud(__uu)
}
;# Yan
proc DependenceTree_BrowseCommand { location item } {
    if { "[info procs wokPROP:BrowseArb]" != "" } {
	wokPROP:BrowseArb $location $item 
    }
    return
}


proc DependenceTree_PostCommand {popu w x y} {
    global arrayofimpl arrayofud

    set men [$popu subwidget menu]
    set hlist [$w subwidget hlist]
    $hlist anchor clear
    $hlist selection clear

    set Y  [expr $y - [winfo rooty $w]]    
    set nearest  [$hlist nearest $Y]
    set last [$men index last]

    if {"$last" != "none"} {
	for {set i 0} {$i <= $last} {incr i} {
	    $men delete $i
	}
    }

    set lstnear [split $nearest .]
    set udname [lindex $lstnear end] 

    if {$udname != ""} {
	set lstimpl $arrayofimpl($udname)
	$popu  subwidget menubutton configure  -text "$udname Suppliers"
	set txtmen ""
	foreach allud $lstimpl {
	    if {[info exist arrayofud($allud)]} {
		set txtmen "$allud \[$arrayofud($allud)\] "
	    } else {
		set txtmen "$allud           Suppliers"
	    }
	    $men add comm -lab $txtmen
	}
    } else {
	return 0
    }    
    update

    return 1
}

proc DependenceTree_getunittk { pkname location } {
    global lsttk arrayoftk arrayofimpl
    set returnud ""

    if {![info exist arrayoftk($pkname)]} {
	foreach atk $lsttk {
	    if {![info exist arrayoftk($atk)]} {
		set pkfile [woklocate -p ${atk}:PACKAGES: $location]
		set arrayoftk($atk) 1
		if {[string length $pkfile]} {
		    set lst {}
		    for_file udintk $pkfile {
			set arrayoftk($udintk) $atk
			set lst [append lst " $udintk "]
		    }
		    set arrayoftk($atk) $lst
		    set arrayofimpl($atk) $lst
		}
	    }
	    if {[info exist arrayoftk($pkname)]} {
		return $arrayoftk($pkname)
	    }
	}	
    } else {
	set returnud $arrayoftk($pkname)
    }

    return $returnud
}

proc DependenceTree_fillarb {w fromud path fromtoolkit location} {
    global arrayofud arrayoftk arrayofimpl
    set curud $fromud

    
    set hlist             [$w subwidget hlist]
    set arrayofud($curud) [DependenceTree_getunittk $curud $location]
    set txt               ""
    set ifile             ""
    set bug [woklocate -u $fromud $location]
    set istoolkit         ""
    set isinithere        0

    if {"$bug" != ""} {
	set istoolkit         [uinfo -t $bug]
    }


    if {$arrayofud($curud) != "" && $istoolkit != "toolkit" && $fromtoolkit == 0} {
	set testtname $arrayofud($curud)
	#	puts "TOOLKIT : $testtname - $fromud"
	if {![info exist arrayofud($testtname)]} {
	    #	    puts "$bug == $istoolkit - $arrayofud($curud) - $fromtoolkit"	
	    set ifile $arrayoftk($testtname)
	    set curud $arrayofud($curud)
	
	    set arrayofud($curud) $curud
	    set txt $curud
	
	    # si l'ud racine est dans un toolkit
	    set tofollow ""
	    if {![$hlist info exist $path]} {
		set tofollow $curud
	    } else {
		set tofollow ${path}.$curud
	    }
	    
	    #		puts "TOOL ${tofollow} : $txt"
	    $hlist add ${tofollow} -text "$txt ($fromud)"
	    $hlist see ${tofollow}
	    update
	    incr arrayofud(__uu)

	    if {[llength $ifile] == 0} {
		puts "Warning: no PACKAGES file in toolkit $curud"
		return
	    }	    
	    
	    foreach allud $ifile {
		set udtxt "$allud"
		if {$allud != ""} {
		    if {$allud == $fromud} {
			unset arrayofud($fromud)
			set udtxt "$udtxt *"
		    }
		    DependenceTree_fillarb $w $allud ${tofollow} 1 $location
		    
		    if {![$hlist info exist ${tofollow}.$allud]} {
			$hlist add ${tofollow}.$allud -text "$udtxt"
			$hlist see ${tofollow}.$allud
			update
			incr arrayofud(__uu)
		    }
		}
	    }
	}
    } else {
	set ifile ""
	set lstofimpl {}

	if {![info exist arrayofimpl($curud)]} {
	    set ifile [woklocate -p ${curud}:stadmfile:${curud}.ImplDep $location]
	    if {$ifile != ""} {
		for_file allud $ifile {
		    if {$allud != $curud} {
			lappend lstofimpl $allud
		    }
		}
	    }
	    set arrayofimpl($curud) $lstofimpl
	} else {
	    set lstofimpl $arrayofimpl($curud)
	}
	set txt "$curud"

	if {$curud != $path} {
	    $hlist add ${path}.$curud -text $txt
	    $hlist see ${path}.$curud
	    set tofollow  ${path}.$curud
	    update
	    incr arrayofud(__uu)
	} else {
	    $hlist add ${path} -text $txt
	    $hlist see ${path}
	    set tofollow  ${path}
	    update
	    incr arrayofud(__uu)
	}
	
	if {$lstofimpl == {}} {
	    puts "Warning: no ImplDep file for $curud"
	    return
	}


	foreach allud $lstofimpl {
	    #	    puts "$fromud $allud $tofollow"
	    
	    if {![info exist arrayofud($allud)]} {	
		if {$allud != $curud} {
		    DependenceTree_fillarb $w $allud $tofollow 0 $location
		}
	    }
	}
    }
}
