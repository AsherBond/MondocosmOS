

proc wcd { args } {
    if { [llength $args] !=0 } {
	wokcd -PSrc $args
    } else {
	puts stdout {Usage: wcd  <unit>}
	foreach u [w_info -l] {
	    puts $u
	}
    }
    return

}
proc wsrc {{entity ""}} {
    if { $entity != "" } { 
      if {[wokinfo -t $entity] == "workbench"} {
	   wokcd -Tsrcdir $entity 
      } 
      if {[wokinfo -t $entity] == "devunit"} {
           wokcd -T source $entity
      }	  
    } else {
      if {[wokinfo -t [wokcd]] == "workbench"} { 
	   wokcd -Tsrcdir
      }
      if {[wokinfo -t [wokcd]] == "devunit"} {
           wokcd -T source 
      }	  

    }
}


;#proc wdrv {{entity ""}} {
;#   if { $entity != "" } { wokcd -Tdrvdir $entity } {wokcd -Tdrvdir}
;#}
proc wdrv {{entity ""}} {
    if { $entity != "" } { 
      if {[wokinfo -t $entity] == "workbench"} {
	   wokcd -Tdrvdir $entity 
      } 
      if {[wokinfo -t $entity] == "devunit"} {
           wokcd -T derivated $entity
      }	  
    } else {
      if {[wokinfo -t [wokcd]] == "workbench"} { 
	   wokcd -Tdrvdir
      }
      if {[wokinfo -t [wokcd]] == "devunit"} {
           wokcd -T derivated 
      }	  

    }
}


;#proc wlib {{entity ""}} {
;#    if { $entity != "" } { wokcd -Tlibdir $entity } {wokcd -Tlibdir}
;#}

proc wlib {{entity ""}} {
    if { $entity != "" } { 
      if {[wokinfo -t $entity] == "workbench"} {
	   wokcd -Tlibdir $entity 
      } 
      if {[wokinfo -t $entity] == "devunit"} {
           wokcd -T library $entity
      }	  
    } else {
      if {[wokinfo -t [wokcd]] == "workbench"} { 
	   wokcd -Tlibdir
      }
      if {[wokinfo -t [wokcd]] == "devunit"} {
           wokcd -T library 
      }	  

    }
}

;#proc wbin {{entity ""}} {
;#    if { $entity != "" } { wokcd -Tbindir $entity } {wokcd -Tbindir}
;#}

proc wbin {{entity ""}} {
    if { $entity != "" } { 
      if {[wokinfo -t $entity] == "workbench"} {
	   wokcd -Tbindir $entity 
      } 
      if {[wokinfo -t $entity] == "devunit"} {
           wokcd -T executable $entity
      }	  
    } else {
      if {[wokinfo -t [wokcd]] == "workbench"} { 
	   wokcd -Tbindir
      }
      if {[wokinfo -t [wokcd]] == "devunit"} {
           wokcd -T executable 
      }	  

    }
}


;#proc wobj {{entity ""}} {
;#    if { $entity != "" } { wokcd -Tobjdir $entity } {wokcd -Tobjdir}
;#}

proc wobj {{entity ""}} {
    if { $entity != "" } { 
      if {[wokinfo -t $entity] == "workbench"} {
	   wokcd -Tobjdir $entity 
      } 
      if {[wokinfo -t $entity] == "devunit"} {
           wokcd -T object $entity
      }	  
    } else {
      if {[wokinfo -t [wokcd]] == "workbench"} { 
	   wokcd -Tobjdir
      }
      if {[wokinfo -t [wokcd]] == "devunit"} {
           wokcd -T object 
      }	  

    }
}


;#proc winc {{entity ""}} {
;#    if { $entity != "" } { wokcd -Tpubincdir $entity } {wokcd -Tpubincdir}
;#}

proc winc {{entity ""}} {
    if { $entity != "" } { 
      if {[wokinfo -t $entity] == "workbench"} {
	   wokcd -Tpubincdir $entity 
      } 
      if {[wokinfo -t $entity] == "devunit"} {
           wokcd -T pubinclude $entity
      }	  
    } else {
      if {[wokinfo -t [wokcd]] == "workbench"} { 
	   wokcd -Tpubincdir
      }
      if {[wokinfo -t [wokcd]] == "devunit"} {
           wokcd -T pubinclude 
      }	  

    }
}


;#proc wadm {{entity ""}} {
;#    if { $entity != "" } { wokcd -Tadmfile $entity } {wokcd -Tadmfile}
;#}
proc wadm {{entity ""}} {
    if { $entity != "" } { 
      if {[wokinfo -t $entity] == "workbench"} {
	   wokcd -T AdmDir $entity 
      } 
      if {[wokinfo -t $entity] == "devunit"} {
           wokcd -T admfile $entity
      }	  
    } else {
      if {[wokinfo -t [wokcd]] == "workbench"} { 
	   wokcd -T AdmDir
      }
      if {[wokinfo -t [wokcd]] == "devunit"} {
           wokcd -T admfile 
      }	  

    }
}



proc wls { args } {
    set f [lsearch -regexp $args {-[pnijCtexscfOrd]} ]
    if { $f != -1 } {
	set ft [lindex [split [lindex $args $f] -] 1]
	set lx {}
	set len [string length $ft]
	foreach cc [ucreate -P] {
	    set SLONG([lindex $cc 0]) [lindex $cc 1]
	}

	for {set i 0} {$i < $len} {incr i 1} {
	    set x [string index $ft $i]
	    if [info exists SLONG($x)] {
		lappend lx $SLONG($x)
	    }
	}

	foreach ud [lsort [w_info -a]] {
	    if { [lsearch $lx [lindex $ud 0]] != -1 } {
		puts [lindex $ud 1]
	    }
	}
    } else {
	set l [lsearch -regexp $args {-l}]
	if { $l == -1 } {
	    set retargs $args
	    set act {w_info -l}
	} else {
	    set retargs [lreplace $args $l $l]
	    set act {w_info -a}
	}
	foreach ff [lsort [eval $act $retargs]] {
	    puts $ff
	}
    }
}



