;#
;# Cree une fenetre pour les parametres
;#
proc wokPRMAff { } {
    set verrue [wokCWD readnocell]
    if [wokinfo -x $verrue] {
	if [wokPRM:Exists] {
	    wokPRM:Kill
	}
	wokPRM:Create $verrue
    } else {
	wokDialBox .wokcd {Unknown location} "Location $verrue is unknown" {} -1 OK
    }
    return
}
;#
;# cree le toplevel si il n'existe pas et init l'arbre , pop le toplevel sinon
;#
proc wokPRM:Create { location } {
    global IWOK_GLOBALS

    if [info exists IWOK_GLOBALS(PRM,toplevel)] {
	set w $IWOK_GLOBALS(PRM,toplevel)
	if [winfo exists $w ] {
	    wm deiconify $w
	    raise $w
	    return 1
	} else {
	    return -1
	}
    }

    set boldfnt [tix option get bold_font]

    set IWOK_GLOBALS(PRM,title)         "Parameters ($location)"
    set IWOK_GLOBALS(PRM,geometry)      1072x660
    set IWOK_GLOBALS(PRM,toplevel)      .woktopl:params
    set IWOK_GLOBALS(PRM,location)      $location

    wokPRMInitDescriptionByClass $location
    set IWOK_GLOBALS(PRM,Description)      [tixDisplayStyle imagetext -font $boldfnt]
    set IWOK_GLOBALS(PRM,DescriptionREAD)  [tixDisplayStyle imagetext -fg orange -font $boldfnt]
    set IWOK_GLOBALS(PRM,DescriptionFREAD) [tixDisplayStyle imagetext -fg orange]

    set w $IWOK_GLOBALS(PRM,toplevel)  

    toplevel    $w
    wm title    $w $IWOK_GLOBALS(PRM,title) 
    wm geometry $w $IWOK_GLOBALS(PRM,geometry)
    
    wokButton setw [list params $w]

    menubutton $w.file -menu $w.file.m -text File -underline 0 -takefocus 0
    menu $w.file.m 
    $w.file.m add command -label "Close     " -underline 0 -command "wokPRM:Kill"

    menubutton $w.help -menu $w.help.m -text Help -underline 0 -takefocus 0
    menu $w.help.m
    $w.help.m add command -label "Help"      -underline 1 -command [list wokPRM:Help $w]

    set notes [tixNoteBook $w.notes -ipadx 5 -ipady 5] 

    tixForm $w.file ; tixForm $w.help -right -2
    tixForm $notes -top $w.file -left 2 -right %99 -bottom %99

    $notes add pag1 -createcmd "wokPRM:NOT wokPRM:ByClass $w $notes pag1" -label "By Class" \
	    -raisecmd [list wokPRM:UPD $w]
    $notes add pag2 -createcmd "wokPRM:NOT wokPRM:ByFile $w $notes pag2" -label "By file" \
	    -raisecmd [list wokPRM:UPD $w]
    $notes add pag3 -createcmd "wokPRM:NOT wokPRM:Modify $w $notes pag3" -label "Modify" \
	    -raisecmd [list wokPRM:UPD $w]

    $notes.nbframe configure -backpagecolor  grey51

    return
}
;#
;#           ((((((((((  M O D I F Y  ))))))))))
;#
proc wokPRM:wrkst { } {
    global IWOK_GLOBALS
    if { $IWOK_GLOBALS(PRM,Modify,wrkst,all) == 1 } {
	foreach  x [array names IWOK_GLOBALS PRM,Modify,wrkst,but,*]  {
	    if { "$x" != "PRM,Modify,wrkst,but,all" } {
		$IWOK_GLOBALS($x) select
	    }
	}
    }
    return
}

proc wokPRM:dbms { } {
    global IWOK_GLOBALS
    if { $IWOK_GLOBALS(PRM,Modify,dbms,all) == 1 } {
	foreach  x [array names IWOK_GLOBALS PRM,Modify,dbms,but,*]  {
	    if { "$x" != "PRM,Modify,dbms,but,all" } {
		$IWOK_GLOBALS($x) select
	    }
	}
    }
    return
}

proc wokPRM:Modify { adr nb page } {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    set fram [frame $w.top -relief sunken -bd 1]
    pack $fram -expand yes -fill both -padx 1 -pady 1

    tixLabelEntry $fram.nam  -label "Parameter name :"  -labelside left -options {
	label.anchor w
	entry.width 35
    }

    tixLabelEntry $fram.val  -label "Current value  :"  -labelside left -options {
	label.anchor w
	entry.width 35
    }

    tixLabelEntry $fram.app  -label "Append         :"  -labelside left -options {
	label.anchor w
	entry.width 35
    }

    tixLabelEntry $fram.fil  -label "                    "  -labelside left -options {
	label.anchor w
	entry.width 35
	entry.relief flat
    }

    tixLabelFrame $fram.scope -label "Scope for modification:"

    set scp [$fram.scope subwidget frame]

    set ici [wokinfo -t [set x :$IWOK_GLOBALS(PRM,location)]]
    if { "$ici" == "devunit" } {
	radiobutton $scp.f -text [set loc [wokinfo -f $x]] \
		-var IWOK_GLOBALS(PRM,Modify,scope) -val [list [wokinfo -n $loc] [wokinfo -p AdmDir $loc]]
	if { "$x" == "$loc" } { $scp.f select }
	radiobutton $scp.s -text [set loc [wokinfo -s $x]] \
		-var IWOK_GLOBALS(PRM,Modify,scope) -val [list [wokinfo -n $loc] [wokinfo -p AdmDir $loc]]
	if { "$x" == "$loc" } { $scp.s select }
	radiobutton $scp.w -text [set loc [wokinfo -w $x]] \
		-var IWOK_GLOBALS(PRM,Modify,scope) -val [list [wokinfo -n $loc] [wokinfo -p AdmDir $loc]]
	if { "$x" == "$loc" } { $scp.w select }
	radiobutton $scp.u -text [set loc [wokinfo -u $x]] \
		-var IWOK_GLOBALS(PRM,Modify,scope) -val [list [wokinfo -n $loc] [wokinfo -p source:. $loc]]
	if { "$x" == "$loc" } { $scp.u select }
	pack $scp.f $scp.s $scp.w $scp.u -anchor w  -padx 1m -pady 1m
    } elseif { "$ici" == "workbench" } {
	radiobutton $scp.f -text [set loc [wokinfo -f $x]] \
		-var IWOK_GLOBALS(PRM,Modify,scope) -val [list [wokinfo -n $loc] [wokinfo -p AdmDir $loc]]
	if { "$x" == "$loc" } { $scp.f select }
	radiobutton $scp.s -text [set loc [wokinfo -s $x]] \
		-var IWOK_GLOBALS(PRM,Modify,scope) -val [list [wokinfo -n $loc] [wokinfo -p AdmDir $loc]]
	if { "$x" == "$loc" } { $scp.s select }
	radiobutton $scp.w -text [set loc [wokinfo -w $x]] \
		-var IWOK_GLOBALS(PRM,Modify,scope) -val [list [wokinfo -n $loc] [wokinfo -p AdmDir $loc]]
	if { "$x" == "$loc" } { $scp.w select }
	label $scp.bid1
	pack $scp.f $scp.s $scp.w $scp.bid1 -anchor w  -padx 1m -pady 1m
	
    } elseif { "$ici" == "workshop" } {
	radiobutton $scp.f -text [set loc [wokinfo -f $x]] \
		-var IWOK_GLOBALS(PRM,Modify,scope) -val [list [wokinfo -n $loc] [wokinfo -p AdmDir $loc]]
	if { "$x" == "$loc" } { $scp.f select }
	radiobutton $scp.s -text [set loc [wokinfo -s $x]] \
		-var IWOK_GLOBALS(PRM,Modify,scope) -val [list [wokinfo -n $loc] [wokinfo -p AdmDir $loc]]
	if { "$x" == "$loc" } { $scp.s select }
	label $scp.bid1 ; label $scp.bid2
	pack $scp.f $scp.s $scp.bid1 $scp.bid2 -anchor w  -padx 1m -pady 1m
    } elseif { "$ici" == "factory" } {

	radiobutton $scp.f -text [set loc [wokinfo -f $x]] \
		-var IWOK_GLOBALS(PRM,Modify,scope) -val [list [wokinfo -n $loc] [wokinfo -p AdmDir $loc]]
	if { "$x" == "$loc" } { $scp.f select }
	label $scp.bid1 ; label $scp.bid2 ; label $scp.bid3
	pack $scp.f $scp.bid1 $scp.bid2 $scp.bid3 -anchor w  -padx 1m -pady 2m
    } elseif { "$ici" == "session" } {
	set IWOK_GLOBALS(PRM,Modify,scope) [list : [pwd]]
    }

    tixLabelFrame $fram.wrkst -label "Modify for stations:"
    set wrkst [$fram.wrkst subwidget frame]

    set IWOK_GLOBALS(PRM,Modify,wrkst,but,all) \
	    [checkbutton ${wrkst}.all -text all -var IWOK_GLOBALS(PRM,Modify,wrkst,all) -comm wokPRM:wrkst]
    tixForm $IWOK_GLOBALS(PRM,Modify,wrkst,but,all) -top 2
    
    set prev {} ; set curr {} ;set curwrkst [wokprofile -s]
    foreach x [list sun sil ao1 hp wnt lin bsd mac] {
	set curr [string tolower ${wrkst}.${x}]
	
	checkbutton $curr -text $x -variable IWOK_GLOBALS(PRM,Modify,wrkst,$x) -command wokPRM:wrkst
	set IWOK_GLOBALS(PRM,Modify,wrkst,but,$x) $curr
	if { "$x" == "$curwrkst" } {
	    $curr select
	}
	
	if { $prev == {} } {
	    tixForm $curr -top $IWOK_GLOBALS(PRM,Modify,wrkst,but,all)
	} else {
	    tixForm $curr -top $IWOK_GLOBALS(PRM,Modify,wrkst,but,all) -left $prev
	}
	set prev $curr
    }

    tixLabelFrame $fram.dbms -label "DBMs: "
    set dbms [$fram.dbms subwidget frame]

    set IWOK_GLOBALS(PRM,Modify,dbms,but,all) \
	    [checkbutton ${dbms}.all -text all -var IWOK_GLOBALS(PRM,Modify,dbms,all) -comm wokPRM:dbms]
    tixForm $IWOK_GLOBALS(PRM,Modify,dbms,but,all) -top 2

    set prev {} ; set curr {} ; set curdbms [wokprofile -b]
    foreach x [list DFLT OBJS] {
	set curr [string tolower ${dbms}.${x}]
	checkbutton $curr -text $x -variable IWOK_GLOBALS(PRM,Modify,dbms,$x) -command wokPRM:dbms
	set IWOK_GLOBALS(PRM,Modify,dbms,but,$x) $curr
	if { "$x" == "$curdbms" } {
	    $curr select
	}
	if { $prev == {} } {
	    tixForm $curr -top $IWOK_GLOBALS(PRM,Modify,dbms,but,all)
	} else {
	    tixForm $curr -top $IWOK_GLOBALS(PRM,Modify,dbms,but,all) -left $prev
	}
	set prev $curr
    }

    checkbutton $fram.shot -text "One shot" -variable IWOK_GLOBALS(PRM,Modify,oneshot)
    checkbutton $fram.verb -text "Verbose " -variable IWOK_GLOBALS(PRM,Modify,verbose)

    tixScrolledListBox $fram.cmp -command wokPRM:Selected 

    tixButtonBox $fram.but -orientation horizontal -padx 0 -pady 0

    tixScrolledText $fram.txt ; $fram.txt subwidget text  config -font $IWOK_GLOBALS(font)

    tixForm $fram.nam -top 10m       -left 1m -right %75
    tixForm $fram.val -top 25m -left 1m -right %75
    tixForm $fram.app -top 40m -left 1m -right %75

    tixForm $fram.cmp   -top 10m -left $fram.nam -right %99 -bottom %30
    tixForm $fram.fil   -left 2                  -right %99 -bottom %99
    tixForm $fram.txt   -top $fram.cmp           -right %99 -bottom $fram.fil

    tixForm $fram.scope -top $fram.cmp   -left 1m -right $fram.txt
    tixForm $fram.wrkst -top $fram.scope -left 1m -right $fram.txt
    tixForm $fram.dbms  -top $fram.wrkst -left 1m 
    tixForm $fram.shot -left $fram.dbms -top $fram.wrkst -right $fram.txt
    tixForm $fram.verb -left $fram.dbms -top $fram.shot  -right $fram.txt
    tixForm $fram.but   -top $fram.dbms  -left 1m -right $fram.txt -bottom $fram.fil

    [set IWOK_GLOBALS(PRM,Modify,labnam) [$fram.nam subwidget label]] config -font $IWOK_GLOBALS(font)
    set IWOK_GLOBALS(PRM,Modify,entnam)  [$fram.nam subwidget entry] 
    $IWOK_GLOBALS(PRM,Modify,entnam)     config -text IWOK_GLOBALS(PRM,Modify,name)
    
    [set IWOK_GLOBALS(PRM,Modify,labval) [$fram.val subwidget label]] config -font $IWOK_GLOBALS(font)
    set IWOK_GLOBALS(PRM,Modify,entval)  [$fram.val subwidget entry]
    $IWOK_GLOBALS(PRM,Modify,entval)     config -text IWOK_GLOBALS(PRM,Modify,value)
    
    [set IWOK_GLOBALS(PRM,Modify,labapp) [$fram.app subwidget label]] config -font $IWOK_GLOBALS(font)
    set IWOK_GLOBALS(PRM,Modify,entapp)  [$fram.app subwidget entry]
    $IWOK_GLOBALS(PRM,Modify,entapp)     config -text IWOK_GLOBALS(PRM,Modify,append)

    [set IWOK_GLOBALS(PRM,Modify,labfil) [$fram.fil subwidget label]] config -font $IWOK_GLOBALS(font)
    set IWOK_GLOBALS(PRM,Modify,entfil)  [$fram.fil subwidget entry]
    $IWOK_GLOBALS(PRM,Modify,entfil)     config -text IWOK_GLOBALS(PRM,Modify,file)

    set IWOK_GLOBALS(PRM,Modify,compl) [$fram.cmp subwidget listbox]
    [set IWOK_GLOBALS(PRM,Modify,text) [$fram.txt subwidget text]]  config -font $IWOK_GLOBALS(font)

    set IWOK_GLOBALS(PRM,Modify,bshow)   [$fram.but add show   -text Show   -comm wokPRM:ShowModif]
    set IWOK_GLOBALS(PRM,Modify,bcancel) [$fram.but add cancel -text Cancel -comm wokPRM:CancelModif]
    set IWOK_GLOBALS(PRM,Modify,bwrite)  [$fram.but add apply  -text Write  -comm [list wokPRM:ApplyModif W]]
    set IWOK_GLOBALS(PRM,Modify,bappend) [$fram.but add append -text Append -comm [list wokPRM:ApplyModif A]]

    $IWOK_GLOBALS(PRM,Modify,bshow)   configure -state active
    $IWOK_GLOBALS(PRM,Modify,bcancel) configure -state active
    $IWOK_GLOBALS(PRM,Modify,bwrite)  configure -state active
    $IWOK_GLOBALS(PRM,Modify,bappend) configure -state active

    ;# completions des noms de parametres
    ;#
    bind $IWOK_GLOBALS(PRM,Modify,entnam) <space> {
	wokPRM:InitCompletionModify %W
	wokPRM:AffLocModify [wokPRM:CompleteModify [wokUtils:EASY:sb [%W get]]]
    }

    bind $IWOK_GLOBALS(PRM,Modify,entnam) <Return> {
	wokPRM:ModifyParam [wokUtils:EASY:sb [%W get]]
    }

    bind $IWOK_GLOBALS(PRM,Modify,entval) <Return> {
	wokPRM:ShowModif
    }

    return
}

proc wokPRM:ShowModif { } {
    global IWOK_GLOBALS

    set pnam  [wokUtils:EASY:sb [$IWOK_GLOBALS(PRM,Modify,entnam) get]]
    if { "$pnam" == "" } {
	return
    }
    set clas  [wokPRM:ClassName $pnam]
    if { "[string index $pnam 0]" != "%" } {
	set pnam "%${pnam}"
    }
    if { "[string index $clas 0]" == "%" } {
	set clas [string range $clas 1 end]
    }

    set IWOK_GLOBALS(PRM,Modify,pnam) $pnam

    set pathdef [lindex $IWOK_GLOBALS(PRM,Modify,scope) 1]
    set redef   [lindex $IWOK_GLOBALS(PRM,Modify,scope) 0]_$clas
    set IWOK_GLOBALS(PRM,Modify,file) $pathdef/${redef}.edl

    if { [set appval [$IWOK_GLOBALS(PRM,Modify,entapp) get]] != "" } {
	set action "@string "
	set assign "+="
	set value  $appval
    } else {
	set action "@set "
	set assign "="
	set value [$IWOK_GLOBALS(PRM,Modify,entval) get]
    }

    catch { unset IWOK_GLOBALS(PRM,Modify,edlbuf) }

    append IWOK_GLOBALS(PRM,Modify,edlbuf) "-- File:	${clas}.edl" \n
    append IWOK_GLOBALS(PRM,Modify,edlbuf) "-- Author: 	" \n
    append IWOK_GLOBALS(PRM,Modify,edlbuf) "-- History:	" \n \n

    if { $IWOK_GLOBALS(PRM,Modify,verbose) == 1 } {
	append IWOK_GLOBALS(PRM,Modify,edlbuf) "@verboseon;" \n
    }


    if { $IWOK_GLOBALS(PRM,Modify,oneshot) == 1 } {
	append IWOK_GLOBALS(PRM,Modify,edlbuf) "-- standard protection against multiple execution" \n
	append IWOK_GLOBALS(PRM,Modify,edlbuf) "@ifnotdefined ( ${pnam}_EDL ) then" \n
	append IWOK_GLOBALS(PRM,Modify,edlbuf) "@set ${pnam}_EDL = \"\"\;" \n \n
    }

    if { $IWOK_GLOBALS(PRM,Modify,wrkst,all) == 0 } {
	if { $IWOK_GLOBALS(PRM,Modify,dbms,all) == 0 } {
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "@if ( [wokPRM:test_wrkst] ) then" \n
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "  @if ( [wokPRM:test_dbms]  ) then" \n
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "    $action $pnam $assign \"$value\"\;" \n
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "  @endif\;" \n
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "@endif\;"
	} else {
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "@if ( [wokPRM:test_wrkst] ) then" \n
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "  $action $pnam $assign \"$value\"\;" \n
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "@endif\;"
	}
    } else {
	if { $IWOK_GLOBALS(PRM,Modify,dbms,all) == 0 } {
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "@if ( [wokPRM:test_dbms] ) then" \n
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "  $action $pnam $assign \"$value\"\;" \n
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "@endif\;"
	} else {
	    append IWOK_GLOBALS(PRM,Modify,edlbuf) "$action $pnam $assign \"$value\"\;" 
	}
    }

    if { $IWOK_GLOBALS(PRM,Modify,oneshot) == 1 } {
	append IWOK_GLOBALS(PRM,Modify,edlbuf) \n "@endif\;"
    }

    if { $IWOK_GLOBALS(PRM,Modify,verbose) == 1 } {
	append IWOK_GLOBALS(PRM,Modify,edlbuf) \n "@verboseoff;"
    }

    wokReadString $IWOK_GLOBALS(PRM,Modify,text) $IWOK_GLOBALS(PRM,Modify,edlbuf)
    $IWOK_GLOBALS(PRM,Modify,labfil)  configure -text "Will write in file           : "
    $IWOK_GLOBALS(PRM,Modify,entfil)  configure -relief sunken
    return
}

proc wokPRM:test_wrkst { } {
    global IWOK_GLOBALS
    set l {}
    foreach x [array names IWOK_GLOBALS PRM,Modify,wrkst,*] {
	if { "$IWOK_GLOBALS($x)" == "1" } {
	    lappend l "%Station == \"[lindex [split $x ,] end]\"" ||
	}
    }
    if { $l != {}} {
	return [join [lrange $l 0 [expr { [llength $l] - 2 }]] ]
    } else {
	return 1
    }
}

proc wokPRM:test_dbms { } {
    global IWOK_GLOBALS
    set l {}
    foreach x [array names IWOK_GLOBALS PRM,Modify,dbms,*] {
	if { "$IWOK_GLOBALS($x)" == "1" } {
	    lappend l "%DBMS == \"[lindex [split $x ,] end]\"" ||
	}
    }
    if { $l != {}} {
	return [join [lrange $l 0 [expr { [llength $l] - 2 }]] ]
    } else {
	return 1
    }
}

proc wokPRM:ApplyModif { option } {
    global IWOK_GLOBALS
    switch -- $option {
	W {
	    if [file exists $IWOK_GLOBALS(PRM,Modify,file)] {
		set retval [wokDialBox .wokprm {File already exists} "File already exists." \
			warning 1 {Overwrite} {Abort}]
		if { $retval } {
		    $IWOK_GLOBALS(PRM,Modify,labfil)  configure -text "Abort...                       "
		    return
		} 
	    }
	    wokTextToFile $IWOK_GLOBALS(PRM,Modify,text) $IWOK_GLOBALS(PRM,Modify,file)
	    $IWOK_GLOBALS(PRM,Modify,labfil)  configure -text "Created file                 : "
	    $IWOK_GLOBALS(PRM,Modify,labfil)  configure -relief sunken
	}

	A {
	    set l {}
	    if [file exists $IWOK_GLOBALS(PRM,Modify,file)] {
		foreach ln [wokUtils:FILES:FileToList  $IWOK_GLOBALS(PRM,Modify,file)] {
		    lappend l $ln
		}
	    } 
	    foreach x [wokTextToList $IWOK_GLOBALS(PRM,Modify,text)] {
		lappend l $x
	    }
	    wokUtils:FILES:ListToFile $l $IWOK_GLOBALS(PRM,Modify,file)
	    $IWOK_GLOBALS(PRM,Modify,labfil)  configure -text "Appended to file             : "
	    $IWOK_GLOBALS(PRM,Modify,entfil)  configure -relief sunken
	}
    }
    
    if { [info exists IWOK_GLOBALS(PRM,ByClass,AllParams)] } {
	if { [lsearch $IWOK_GLOBALS(PRM,ByClass,AllParams) $IWOK_GLOBALS(PRM,Modify,pnam)] == -1 } {
	    lappend IWOK_GLOBALS(PRM,ByClass,AllParams) $IWOK_GLOBALS(PRM,Modify,pnam)
	}
    }
    wokclose

    return
}

proc wokPRM:CancelModif { } {
    global IWOK_GLOBALS
    catch { unset  IWOK_GLOBALS(PRM,Modify,edlbuf) }
    set    IWOK_GLOBALS(PRM,Modify,name)   "" 
    set    IWOK_GLOBALS(PRM,Modify,value)  ""
    set    IWOK_GLOBALS(PRM,Modify,append) ""
    set    IWOK_GLOBALS(PRM,Modify,file)   ""
    $IWOK_GLOBALS(PRM,Modify,text) delete 1.0 end
    return
}

proc wokPRM:ModifyParam { param } {
    global IWOK_GLOBALS
    $IWOK_GLOBALS(PRM,Modify,compl) delete 0 end
    if [info exists IWOK_GLOBALS(PRM,ByClass,AllParams,$param)] {
	set IWOK_GLOBALS(PRM,Modify,value) $IWOK_GLOBALS(PRM,ByClass,AllParams,$param)
    } else {
	set IWOK_GLOBALS(PRM,Modify,value) ""
    }
    return
}

proc wokPRM:InitCompletionModify { w } {
    global IWOK_GLOBALS
    if ![info exists IWOK_GLOBALS(PRM,ByClass,InitCompletionByParams)] {
	tixBusy $w on
	update
	wokPRM:InitCompletionByClass $w
	set IWOK_GLOBALS(PRM,ByClass,AllParams) [wokPRM:LoadByParam $IWOK_GLOBALS(PRM,ByClass,AllClasses)]
	set IWOK_GLOBALS(PRM,ByClass,InitCompletionByParams) done
	tixBusy $w off
    }
    set IWOK_GLOBALS(PRM,CompletionType) Modify
    return
}

proc wokPRM:AffLocModify { ret } {
    global IWOK_GLOBALS
    if { $ret != {} } {
	$IWOK_GLOBALS(PRM,Modify,entnam) delete 0 end
	$IWOK_GLOBALS(PRM,Modify,entnam) insert 0 $ret
    }
    return
}

proc wokPRM:CompleteModify { str } {
    set lent [wokPRM:GetELByParam]
    set retcomp [wokUtils:LIST:POF $str $lent]
    set comp [lindex $retcomp 0]
    set newaff [lindex $retcomp 1]
    set lcomp [llength $newaff]

    if { $lcomp == 1 } {
	return ${comp}
    }
    
    if { $lcomp != 0  } {
	wokPRM:AffEntModify $newaff
	return ${comp}
    }
    
    if { $lcomp == 0 } {
	wokPRM:AffEntModify ___NoMatch___
	return {}
    }
}

proc wokPRM:AffEntModify  { l } {
    global IWOK_GLOBALS
    $IWOK_GLOBALS(PRM,Modify,compl) delete 0 end
    if { "$l" == "___NoMatch___" } {
	$IWOK_GLOBALS(PRM,Modify,compl) insert end "No match."
    } else {
	foreach s $l {
	    $IWOK_GLOBALS(PRM,Modify,compl) insert end $s
	}
    }
    return
}



;#
;#           (((((((((( B Y C L A S S ))))))))))
;#
proc wokPRM:ByClass { adr nb page } {
    global IWOK_GLOBALS
    
    set w [$nb subwidget $page]

    frame $w.top -relief sunken -bd 1 

    tixPanedWindow $w.top.pane -orient horizontal  -paneborderwidth 0 -separatorbg gray50
    pack $w.top.pane -side top -expand yes -fill both -padx 1 -pady 1

    set p1 [$w.top.pane add tree -min 250 -size  300]
    set tree  [tixTree  $p1.tree -options {separator "^" hlist.selectMode single }]
    pack $p1.tree -expand yes -fill both -padx 1 -pady 1
    $tree config -browsecmd "wokPRM:BrowseByClass $w $tree" -opencmd "wokPRM:OpenByClass $tree"

    set p2 [$w.top.pane add fram]
    set fram [frame $p2.fram]
    pack $p2.fram  -expand yes -fill both -padx 1 -pady 1

    tixLabelEntry $fram.cla  -label "Class name        :"  -labelside left -options {
	label.anchor w
	entry.width 35
    }

    tixLabelEntry $fram.nam  -label "Parameter name    :"  -labelside left -options {
	label.anchor w
	entry.width 35
    }

    tixLabelEntry $fram.val  -label "Parameter value   :"  -labelside left -options {
	label.anchor w
	entry.width 35
    }
    
    tixComboBox  $fram.fil  \
	    -variable IWOK_GLOBALS(PRM,ByClass,file) \
	    -command wokPRM:EdlTexte -label "Defined in file(s):" \
	    -editable false -labelside left \
	    -history 1 -prunehistory 1 

    $fram.fil subwidget label configure -anchor w -font $IWOK_GLOBALS(font)
    $fram.fil subwidget entry configure -width 35

    tixScrolledListBox $fram.cmp -command wokPRM:Selected  

    tixScrolledText $fram.txt  ; $fram.txt subwidget text  config -font $IWOK_GLOBALS(font)

    tixForm $fram.cla -top 1c -left 1m -right %75
    tixForm $fram.nam -top 2c -left 1m -right %75
    tixForm $fram.val -top 3c -left 1m -right %75 
    tixForm $fram.fil -top 4c -left 1m -right %75

    tixForm $fram.cmp -top 1c -left $fram.fil -top 1c -bottom $fram.txt -right %99
 
    tixForm $fram.txt -top 5c -left 2 -right %99 -bottom %99
    
    tixForm $w.top -top 2  -left 2 -right %99 -bottom %99

    set IWOK_GLOBALS(PRM,ByClass,tree)   $tree
    set IWOK_GLOBALS(PRM,ByClass,hlist)  [$tree subwidget hlist]

    [set IWOK_GLOBALS(PRM,ByClass,labcla) [$fram.cla subwidget label]] config -font $IWOK_GLOBALS(font)
    set IWOK_GLOBALS(PRM,ByClass,entcla)  [$fram.cla subwidget entry]
    
    [set IWOK_GLOBALS(PRM,ByClass,labnam) [$fram.nam subwidget label]] config -font $IWOK_GLOBALS(font)
    set IWOK_GLOBALS(PRM,ByClass,entnam)  [$fram.nam subwidget entry]
    
    [set IWOK_GLOBALS(PRM,ByClass,labval) [$fram.val subwidget label]] config -font $IWOK_GLOBALS(font)
    set IWOK_GLOBALS(PRM,ByClass,entval)  [$fram.val subwidget entry]

    set IWOK_GLOBALS(PRM,ByClass,boxfil)   $fram.fil 
    [set IWOK_GLOBALS(PRM,ByClass,labfil) [$fram.fil subwidget label]] config -font $IWOK_GLOBALS(font)
    [set IWOK_GLOBALS(PRM,ByClass,entfil) [$fram.fil subwidget entry]
    [set IWOK_GLOBALS(PRM,ByClass,lstfil) [$fram.fil subwidget listbox]

    $IWOK_GLOBALS(PRM,ByClass,entcla) config -text IWOK_GLOBALS(PRM,ByClass,class)
    $IWOK_GLOBALS(PRM,ByClass,entnam) config -text IWOK_GLOBALS(PRM,ByClass,name)
    $IWOK_GLOBALS(PRM,ByClass,entval) config -text IWOK_GLOBALS(PRM,ByClass,value)

    set IWOK_GLOBALS(PRM,ByClass,compl) [$fram.cmp subwidget listbox]

    set IWOK_GLOBALS(PRM,ByClass,text) [$fram.txt subwidget text]

    wokPRM:InitCompletionByClass $w
    wokPRM:FillByClass

    ;# completions des noms de classe
    ;#
    bind  $IWOK_GLOBALS(PRM,ByClass,entcla) <space> {
	wokPRM:InitCompletionByClass %W
	wokPRM:AffLocByClass [wokPRM:CompleteByClass [wokUtils:EASY:sb [%W get]]]
    }
    bind  $IWOK_GLOBALS(PRM,ByClass,entcla) <Return> {
	wokPRM:AddByClass [wokUtils:EASY:sb [%W get]]
    }

    ;# completions des noms de parametres
    ;#
    bind $IWOK_GLOBALS(PRM,ByClass,entnam) <space> {
	wokPRM:InitCompletionByParams %W
	wokPRM:AffLocByParam [wokPRM:CompleteByParam [wokUtils:EASY:sb [%W get]]]
    }

    bind $IWOK_GLOBALS(PRM,ByClass,entnam) <Return> {
	wokPRM:AddByParam [wokUtils:EASY:sb [%W get]]
    }

    return

}

proc wokPRM:InitCompletionByClass { w } {
    global IWOK_GLOBALS
    set IWOK_GLOBALS(PRM,CompletionType) ByClass
    if ![info exists IWOK_GLOBALS(PRM,ByClass,InitCompletionByClass)] {
	tixBusy $w on
	set IWOK_GLOBALS(PRM,ByClass,AllClasses) [wokPRM:LoadByClass]
	set IWOK_GLOBALS(PRM,ByClass,InitCompletionByClass) done
	tixBusy $w off
    }
    return
}

proc wokPRM:InitCompletionByParams { w } {
    global IWOK_GLOBALS
    if ![info exists IWOK_GLOBALS(PRM,ByClass,InitCompletionByParams)] {
	tixBusy $w on
	update
	wokPRM:InitCompletionByClass $w
	set IWOK_GLOBALS(PRM,ByClass,AllParams) [wokPRM:LoadByParam $IWOK_GLOBALS(PRM,ByClass,AllClasses)]
	set IWOK_GLOBALS(PRM,ByClass,InitCompletionByParams) done
	tixBusy $w off
    }
    set IWOK_GLOBALS(PRM,CompletionType) ByParam
    return
}
#
# insert dans la ComBoBox.listbox la liste des fichiers definissant class. 
#                ComBoBox.entry  le premier d'entre eux. ( declenche l'insertion du texte )
#
proc wokPRM:InsertDEFByClass { class } {
    global IWOK_GLOBALS
    if [info exists IWOK_GLOBALS(PRM,ByClass,AllClasses,$class)] {
	set lf [wokUtils:LIST:union $IWOK_GLOBALS(PRM,ByClass,AllClasses,$class) [wokparam -F $class]]
	set IWOK_GLOBALS(PRM,ByClass,file) [lindex $lf 0]
	$IWOK_GLOBALS(PRM,ByClass,lstfil) delete 0 end
	foreach f [lrange $lf 1 end] {
	   $IWOK_GLOBALS(PRM,ByClass,lstfil) insert end $f 
	}
    } 
    return 
}
#
#
#
proc wokPRM:CleanByClass { widgets } {
    global IWOK_GLOBALS
    if { "$widgets" == "ALL" } {
	set widgets [list text entcla entnam entval entfil compl]
    }
    foreach w $widgets {
	switch -- $w {
	    text {
		$IWOK_GLOBALS(PRM,ByClass,text) delete 1.0 end
	    }
	    entcla {
		set IWOK_GLOBALS(PRM,ByClass,class) ""
	    }
	    entnam {
		set IWOK_GLOBALS(PRM,ByClass,name)  ""
	    }
	    entval {
		set IWOK_GLOBALS(PRM,ByClass,value) ""
	    }
	    entfil {
		$IWOK_GLOBALS(PRM,ByClass,boxfil) configure -disablecallback true
		set IWOK_GLOBALS(PRM,ByClass,file) ""
		$IWOK_GLOBALS(PRM,ByClass,lstfil) delete 0 end
		$IWOK_GLOBALS(PRM,ByClass,boxfil) configure -disablecallback false 
	    }

	    compl {
		$IWOK_GLOBALS(PRM,ByClass,compl) delete 0 end
	    }
	}
    }
    update
    return
}
;#
;# appele dans la listbox des completions
;#
proc wokPRM:Selected {  } {
   
    global IWOK_GLOBALS
    
    switch -- $IWOK_GLOBALS(PRM,CompletionType) {
	
	ByClass {
	    set sl [$IWOK_GLOBALS(PRM,ByClass,compl) curselection]
	    if { "$sl" != "" } {
		set class [$IWOK_GLOBALS(PRM,ByClass,compl) get $sl]
		if { "$class" != "" } {
		    set IWOK_GLOBALS(PRM,ByClass,class) $class
		    wokPRM:AddByClass $class
		}
	    }
	}
	
	ByParam {
	    set sl [$IWOK_GLOBALS(PRM,ByClass,compl) curselection]
	    if { "$sl" != "" } {
		set param [$IWOK_GLOBALS(PRM,ByClass,compl) get $sl]
		if { "$param" != "" } {
		    set IWOK_GLOBALS(PRM,ByClass,name) $param
		    wokPRM:AddByParam $param
		}
	    }
	}
	
	Modify {
	    set sl [$IWOK_GLOBALS(PRM,Modify,compl) curselection]
	    if { "$sl" != "" } {
		set param [$IWOK_GLOBALS(PRM,Modify,compl) get $sl]
		if { "$param" != "" } {
		    set IWOK_GLOBALS(PRM,Modify,name) $param 
		    wokPRM:ModifyParam $param
		}
	    }
	}
	
    }
    return
}

;#
;# Completions sur les noms de classe
;#
proc wokPRM:CompleteByClass { str } {
    set lent [wokPRM:GetELByClass]
    set retcomp [wokUtils:LIST:POF $str $lent]
    set comp [lindex $retcomp 0]
    set newaff [lindex $retcomp 1]
    set lcomp [llength $newaff]

    if { $lcomp == 1 } {
	return ${comp}
    }
    
    if { $lcomp != 0  } {
	wokPRM:AffEntByClass $newaff
	return ${comp}
    }
    
    if { $lcomp == 0 } {
	wokPRM:AffEntByClass ___NoMatch___
	return {}
    }
}

;#
;# Completions sur les noms de classe
;#
proc wokPRM:CompleteByParam { str } {
    set lent [wokPRM:GetELByParam]
    set retcomp [wokUtils:LIST:POF $str $lent]
    set comp [lindex $retcomp 0]
    set newaff [lindex $retcomp 1]
    set lcomp [llength $newaff]

    if { $lcomp == 1 } {
	return ${comp}
    }
    
    if { $lcomp != 0  } {
	wokPRM:AffEntByClass $newaff
	return ${comp}
    }
    
    if { $lcomp == 0 } {
	wokPRM:AffEntByClass ___NoMatch___
	return {}
    }
}

;#
;# Completions sur les noms de classe
;#
proc wokPRM:AffEntByClass { l } {
    global IWOK_GLOBALS
    $IWOK_GLOBALS(PRM,ByClass,compl) delete 0 end
    if { "$l" == "___NoMatch___" } {
	$IWOK_GLOBALS(PRM,ByClass,compl) insert end "No match."
    } else {
	foreach s $l {
	    $IWOK_GLOBALS(PRM,ByClass,compl) insert end $s
	}
    }
    return
}
;#
;# Completions sur les noms de parametres
;#
proc wokPRM:AffEntByParam { l } {
    global IWOK_GLOBALS
    $IWOK_GLOBALS(PRM,ByClass,compl) delete 0 end
    if { "$l" == "___NoMatch___" } {
	$IWOK_GLOBALS(PRM,ByClass,compl) insert end "No match."
    } else {
	foreach s $l {
	    $IWOK_GLOBALS(PRM,ByClass,compl) insert end $s
	}
    }
    return
}

;#
;# Completions sur les noms de classe
;#
proc wokPRM:AffLocByClass { ret } {
    global IWOK_GLOBALS
    if { $ret != {} } {
	$IWOK_GLOBALS(PRM,ByClass,entcla) delete 0 end
	$IWOK_GLOBALS(PRM,ByClass,entcla) insert 0 $ret
    }
    return
}
;#
;# Completions sur les noms de classe
;#
proc wokPRM:AffLocByParam { ret } {
    global IWOK_GLOBALS
    if { $ret != {} } {
	$IWOK_GLOBALS(PRM,ByClass,entnam) delete 0 end
	$IWOK_GLOBALS(PRM,ByClass,entnam) insert 0 $ret
    }
    return
}
;#
;# Completions sur les noms de classe
;#
proc wokPRM:GetELByClass { } {
    global IWOK_GLOBALS
    return [lsort $IWOK_GLOBALS(PRM,ByClass,AllClasses)]
}
;#
;# Completions sur les noms de classe
;#
proc wokPRM:GetELByParam { } {
    global IWOK_GLOBALS
    return [lsort $IWOK_GLOBALS(PRM,ByClass,AllParams)]
}
;#
;#  Appel declenche par le changement de valeur de la ComboBox
;#
proc wokPRM:EdlTexte { edlfile } {
    global IWOK_GLOBALS
    if { "$edlfile" != "" } {
	$IWOK_GLOBALS(PRM,ByClass,boxfil) addhistory $edlfile
	wokReadFile $IWOK_GLOBALS(PRM,ByClass,text) $edlfile
	wokPRM:HiLiByClass
    }
    return
}
;#
;# 
;#
proc wokPRM:AddByClass  { class } {
    global IWOK_GLOBALS
    set hlist $IWOK_GLOBALS(PRM,ByClass,hlist)

    foreach e [$hlist info children] {
	set ldata [lindex [$hlist info data $e] 1]
	if { [lsearch $ldata $class] != -1 } {
	    wokPRM:OpenByClass $IWOK_GLOBALS(PRM,ByClass,tree) $e
	    $IWOK_GLOBALS(PRM,ByClass,tree) setmode $e close
	    wokPRM:OpenByClass $IWOK_GLOBALS(PRM,ByClass,tree) ${e}^${class}
	    $IWOK_GLOBALS(PRM,ByClass,tree) setmode ${e}^${class} close
	    wokPRM:CleanByClass [list  entnam entval text] 
	    set IWOK_GLOBALS(PRM,ByClass,class) $class
	    wokPRM:InsertDEFByClass $class
	    wokPRM:SeeMe ${e}^${class}
	    return
	}
    }
    return
}
;#
;#
;#
proc wokPRM:AddByParam { param } {
    global IWOK_GLOBALS
    set hlist $IWOK_GLOBALS(PRM,ByClass,hlist)
    set class [string range [wokPRM:ClassName $param] 1 end] ;# (retirer le %)

    foreach e [$hlist info children] {
	set ldata [lindex [$hlist info data $e] 1]
	if { [lsearch $ldata $class] != -1 } {
	    wokPRM:OpenByClass $IWOK_GLOBALS(PRM,ByClass,tree) $e
	    $IWOK_GLOBALS(PRM,ByClass,tree) setmode $e close
	    wokPRM:OpenByClass $IWOK_GLOBALS(PRM,ByClass,tree) ${e}^${class}
	    $IWOK_GLOBALS(PRM,ByClass,tree) setmode ${e}^${class} close
	    set IWOK_GLOBALS(PRM,ByClass,class) $class
	    wokPRM:InsertDEFByClass $class
	    set IWOK_GLOBALS(PRM,ByClass,name) $param
	    set IWOK_GLOBALS(PRM,ByClass,value) [lindex [$hlist info data ${e}^${class}^${param}] 2]
	    wokPRM:SeeMe ${e}^${class}^${param}
	    wokPRM:HiLiByClass
	    return
	}
    }
    return
}
;#
;#
;#
proc wokPRM:FillByClass { } {
    global IWOK_GLOBALS
    set hlist $IWOK_GLOBALS(PRM,ByClass,hlist)
    $hlist delete all
    foreach name [lsort [array names IWOK_GLOBALS PRMDSC,*]] {
	if { "$name" != "PRMDSC,DefaultFamilies" } {
	    $hlist add ${name} -itemtype imagetext -style $IWOK_GLOBALS(PRM,Description) \
		    -text [lindex [split $name ,] 1] \
		    -data [list F $IWOK_GLOBALS($name)]
	    $IWOK_GLOBALS(PRM,ByClass,tree) setmode ${name} open
	}
    }

    set all $IWOK_GLOBALS(PRM,ByClass,AllClasses)
    set def $IWOK_GLOBALS(PRMDSC,DefaultFamilies)
    set lothers [lsort [wokUtils:LIST:moins $all $def]]
    $hlist add  __Others__  -itemtype imagetext -style $IWOK_GLOBALS(PRM,Description) \
	    -text "Others..." -data [list F $lothers]
    $IWOK_GLOBALS(PRM,ByClass,tree) setmode __Others__ open
    return
}
;#
;# retourne le nom dec la classe a partir de string 
;#
proc wokPRM:ClassName { string } {
    if [regexp {(^[^_]*)_(.*)} $string all class rest] {
	return $class
    } else {
	;#msgprint -w "Unknown syntax for a class name : $string"
	return $string
    }
}
#
# retourne une table avec toutes les classses et l'adresse des edls correspondants.
#
proc wokPRM:GetAllClasses { table } {
    upvar $table TLOC
    catch {unset TLOC}
    foreach d [wokparam -L] {
	foreach e [glob -nocomplain $d/*.edl] {
	    set cname [wokPRM:ClassName [file root [file tail $e]]]
	    if [info exists TLOC($cname)] {
		set i $TLOC($cname)
		lappend i $e
		set TLOC($cname) $i
	    } else {
		set TLOC($cname) $e
	    }
	}
    }
    return
}
#
# retourne une table avec tous les parametres des lclasses  et leur valeur
#
proc wokPRM:GetAllParams { lclasses table } {
    upvar $table TLOC
    catch {unset TLOC}
    foreach class $lclasses {
	foreach p [wokparam -l $class] {
	    if [regexp {^%.*} $p] { 
		set x [split $p =]
		set TLOC([lindex $x 0]) [lindex $x 1]
	    }
	}
    }
    return
}
;#
;#
;#
proc wokPRM:LoadByParam { l } {
    global IWOK_GLOBALS
    catch { unset table }
    wokPRM:GetAllParams $l table
    wokUtils:EASY:MAD IWOK_GLOBALS PRM,ByClass,AllParams table
    return [lsort [array names table]]
}
;#
;#
;#
proc wokPRM:LoadByClass { } {
    global IWOK_GLOBALS
    catch { unset table }
    wokPRM:GetAllClasses table
    wokUtils:EASY:MAD IWOK_GLOBALS PRM,ByClass,AllClasses table
    return [lsort [array names table]]
}
;#
;# appelee a l'ouverture d'un item: Le remplit avec les fichiers de l'UD.
;#
proc wokPRM:OpenByClass { tree ent } {
    global IWOK_GLOBALS
    set hlist [$tree subwidget hlist]
    tixBusy $IWOK_GLOBALS(PRM,toplevel) on
    update
    if {[$hlist info children $ent] == {}} {
	set dat [$hlist info data $ent] 
	set data [lindex $dat  1]
	switch -- [lindex $dat 0] {
	    F  {
		foreach c $data {
		   $hlist add $ent^$c -itemtype imagetext -style $IWOK_GLOBALS(PRM,Description) \
			   -text $c -data [list C $c]
		    $tree setmode $ent^$c open
		}
		wokPRM:CleanByClass ALL
	    }

	    C {
		catch {
		    foreach p [lsort [wokparam -l $data]] {
			if [regexp {^%.*} $p] { 
			    set x [split $p =]
			    set name [lindex $x 0]
			    set value [lindex $x 1]
			    $hlist add $ent^$name  -itemtype imagetext -text $name -data [list P $name $value]
			}
		    }
		}
		wokPRM:CleanByClass [list entnam entval compl]
	    }

	}
	
    }
    tixBusy $IWOK_GLOBALS(PRM,toplevel) off
    foreach kid [$hlist info children $ent] {
	$hlist show entry $kid
    }

    return
}

#
# appelee quand on brouze la liste.
#
proc wokPRM:BrowseByClass {  w slb args } {
    global IWOK_GLOBALS
    set hlist [$slb subwidget hlist]

    set ent   [$hlist info anchor]
    if {$ent == ""} {
	return
    }

    set kid [$hlist info children $ent]
    if {$kid == {} } {
	set fullitem [$hlist info data $ent]
	tixBusy $IWOK_GLOBALS(PRM,toplevel) on
	switch -- [lindex $fullitem 0] {
	    
	    C {
		set class [lindex $fullitem 1]
		set IWOK_GLOBALS(PRM,ByClass,class) $class
		wokPRM:InsertDEFByClass $class
	    }

	    P {
		set class [lindex [$hlist info data [$hlist info parent $ent]] 1]
		if { "$IWOK_GLOBALS(PRM,ByClass,class)" != "$class" } {
		    set IWOK_GLOBALS(PRM,ByClass,class) $class
		    wokPRM:InsertDEFByClass $class
		}

		set IWOK_GLOBALS(PRM,ByClass,name)  [lindex $fullitem 1]
		set IWOK_GLOBALS(PRM,ByClass,value) [lindex $fullitem 2]

		wokPRM:HiLiByClass
	    }
	}
	tixBusy $IWOK_GLOBALS(PRM,toplevel) off
    }
    return
}
;#
;# surligne dans le texte le parametre en cours si il y en a un
;#
proc wokPRM:HiLiByClass { } {
    global IWOK_GLOBALS
    if { "$IWOK_GLOBALS(PRM,ByClass,name)" != "" } {
	catch {
	    $IWOK_GLOBALS(PRM,ByClass,text) tag delete big
	    wokFAM $IWOK_GLOBALS(PRM,ByClass,text) $IWOK_GLOBALS(PRM,ByClass,name) \
		    { $IWOK_GLOBALS(PRM,ByClass,text) tag add big first last }
	    $IWOK_GLOBALS(PRM,ByClass,text) tag configure big -background orange \
		    -foreground black -borderwidth 2 \
		    -font -Adobe-Helvetica-Medium-R-Normal--*-120-* -relief raised
	    set index [$IWOK_GLOBALS(PRM,ByClass,text) search \
		    -exact $IWOK_GLOBALS(PRM,ByClass,name) 1.0]
	    $IWOK_GLOBALS(PRM,ByClass,text) see $index 
	}
    }
    return
}
;#
;#
;#
proc wokPRM:SeeMe { entry } {
    global IWOK_GLOBALS
    $IWOK_GLOBALS(PRM,ByClass,hlist) selection clear
    $IWOK_GLOBALS(PRM,ByClass,hlist) anchor clear
    $IWOK_GLOBALS(PRM,ByClass,hlist) anchor set $entry
    $IWOK_GLOBALS(PRM,ByClass,hlist) selection set $entry
    $IWOK_GLOBALS(PRM,ByClass,hlist) see $entry
    return
}

;#
;# Init des familles de classes
;#
proc wokPRMInitDescriptionByClass { location } {
    global IWOK_GLOBALS
    set IWOK_GLOBALS(PRMDSC,DefaultFamilies) [concat \
	    [set IWOK_GLOBALS(PRMDSC,Compilations)     [list CMPLRS OBJSCMPLRS]]\
	    [set IWOK_GLOBALS(PRMDSC,Links/Shareables/Archives) [list LD LDSHR LDEXE ARX LDAR ]]\
	    [set IWOK_GLOBALS(PRMDSC,Unit-description)          [list FILENAME]]\
	    [set IWOK_GLOBALS(PRMDSC,BD/Extractors)             [list TCPP CODEGEN CPPENG]]\
	    [set IWOK_GLOBALS(PRMDSC,Building-steps)            [list WOKSteps WOKStepsDFLT ]]\
	    [set IWOK_GLOBALS(PRMDSC,Environnement)             [list COMMAND ENV]]\
	    [set IWOK_GLOBALS(PRMDSC,Current-location)          [wokparam -C $location]]\
	    ]
    return
} 

proc wokPRM:DBGByClass { {root {}} } {
    global IWOK_GLOBALS
    set hli $IWOK_GLOBALS(PRM,ByClass,hlist)
    foreach c [$hli info children $root] {
	puts "$c : data <[$hli info data $c]>"
	wokPRM:DBGByClass $c
    }
    return
}
;#
;#           ((((((((((( B Y F I L E )))))))))))
;#
;#
proc wokPRM:ByFile { adr nb page } {
    global IWOK_GLOBALS
    
    set w [$nb subwidget $page]

    frame $w.top -relief sunken -bd 1 

    tixPanedWindow $w.top.pane -orient horizontal  -paneborderwidth 0 -separatorbg gray50
    pack $w.top.pane -side top -expand yes -fill both -padx 1 -pady 1

    set p1 [$w.top.pane add tree -min 250 -size  380]
    set tree  [tixTree  $p1.tree -options {separator "^" hlist.selectMode single }]
    pack $p1.tree -expand yes -fill both -padx 1 -pady 1
    $tree config -browsecmd "wokPRM:BrowseByFile $w $tree" -opencmd "wokPRM:OpenByFile $tree"

    set p2 [$w.top.pane add fram ]
    set fram [frame $p2.fram]
    pack $p2.fram  -expand yes -fill both -padx 1 -pady 1

    tixLabelEntry $fram.cla  -label "Class name        :"  -labelside left -options {
	label.anchor w
	entry.width 35
    }

    tixLabelEntry $fram.nam  -label "Parameter name    :"  -labelside left -options {
	label.anchor w
	entry.width 35
    }

    tixLabelEntry $fram.val  -label "Parameter value   :"  -labelside left -options {
	label.anchor w
	entry.width 35
    }

    ;# mettre ici la date du fichier etc...
    
    tixScrolledText $fram.txt  

    tixForm $fram.cla -top 1c -left 1m -right %75
    tixForm $fram.nam -top 2c -left 1m -right %75
    tixForm $fram.val -top 3c -left 1m -right %75 
    tixForm $fram.txt -top 5c -left 2  -right %99 -bottom %99

    tixForm $w.top -top 2  -left 2 -right %99 -bottom %99

    set IWOK_GLOBALS(PRM,ByFile,tree)  $tree
    set IWOK_GLOBALS(PRM,ByFile,hlist) [$tree subwidget hlist]

    [set IWOK_GLOBALS(PRM,ByFile,labcla) [$fram.cla subwidget label]] config -font $IWOK_GLOBALS(font)
    [set IWOK_GLOBALS(PRM,ByFile,entcla) [$fram.cla subwidget entry]] config -stat disabled

    [set IWOK_GLOBALS(PRM,ByFile,labnam) [$fram.nam subwidget label]] config -font $IWOK_GLOBALS(font)
    [set IWOK_GLOBALS(PRM,ByFile,entnam) [$fram.nam subwidget entry]] config -stat disabled

    [set IWOK_GLOBALS(PRM,ByFile,labval) [$fram.val subwidget label]] config -font $IWOK_GLOBALS(font)
    [set IWOK_GLOBALS(PRM,ByFile,entval) [$fram.val subwidget entry]] config -stat disabled

    $IWOK_GLOBALS(PRM,ByFile,entcla) config -text IWOK_GLOBALS(PRM,ByFile,class)
    $IWOK_GLOBALS(PRM,ByFile,entnam) config -text IWOK_GLOBALS(PRM,ByFile,name)
    $IWOK_GLOBALS(PRM,ByFile,entval) config -text IWOK_GLOBALS(PRM,ByFile,value)

    [set IWOK_GLOBALS(PRM,ByFile,text) [$fram.txt subwidget text]] config -font $IWOK_GLOBALS(font)

    wokPRM:FillByFile
    return
}
;#
;# remplit la hlist avec les directories du search-path
;#
proc wokPRM:FillByFile { } {
    global IWOK_GLOBALS
    set hlist $IWOK_GLOBALS(PRM,ByFile,hlist)
    $hlist delete all
    set inx [llength [set lf [wokUtils:LIST:reverse [wokparam -L]]]]
    foreach name $lf {
	set directory $name
	set entry $name
	set text [format "(%-2s) %s" ${inx} ${directory}]
	if { [llength [set ledl [glob -nocomplain $directory/*.edl]]] == 0  } {
	    append text (empty)
	}
	$hlist add $entry -itemtype imagetext -style $IWOK_GLOBALS(PRM,Description) \
		-text $text \
		-data [list D [lsort $ledl]]
	$IWOK_GLOBALS(PRM,ByFile,tree) setmode $entry open
	incr inx -1
    }
    return
}
proc wokPRM:OpenByFile { tree ent } {
    global IWOK_GLOBALS
    set hlist [$tree subwidget hlist]
    tixBusy $IWOK_GLOBALS(PRM,toplevel) on
    update
    if {[$hlist info children $ent] == {}} {
	set dat [$hlist info data $ent] 
	set data [lindex $dat  1]
	switch -- [lindex $dat 0] {
	    D  {
		
		set IWOK_GLOBALS(PRM,ByFile,value) ""
		set IWOK_GLOBALS(PRM,ByFile,name)  ""
		set IWOK_GLOBALS(PRM,ByFile,class) ""
		foreach c $data {
		    set t [file tail $c] 
		    $hlist add $ent^$t -itemtype imagetext \
			    -style $IWOK_GLOBALS(PRM,Description) \
			    -text $t -data [list F $c]
		    $tree setmode $ent^$t open
		}
		
	    }

            F {
		set IWOK_GLOBALS(PRM,ByFile,value) ""
		set IWOK_GLOBALS(PRM,ByFile,name)  ""
		set IWOK_GLOBALS(PRM,ByFile,class) [wokPRM:ClassName [file root [file tail $data]]]
		wokReadFile $IWOK_GLOBALS(PRM,ByFile,text) $data
		foreach p [wokPRM:ParamsInfile $data] {
		    if ![$hlist info exists $ent^$p] {
			$hlist add $ent^$p -itemtype imagetext \
				-text $p -data [list P $p]
		    }
		}
	    }
	    

            P  {
             
            }


	}
	
    }
    tixBusy $IWOK_GLOBALS(PRM,toplevel) off
    foreach kid [$hlist info children $ent] {
	$hlist show entry $kid
    }

    return
}
;#
;# retourne la liste des parametres definis/ecrits dans file
;# syntaxes reconnues : @set %KERNEL_SCCS = ...
;#                      @string %SESSION_Adm  = ...
;# retournera aussi le contenu entier du fichier ca ira plus vite
;# le meme parametre peut etre plusieurs fois dans la liste.
proc wokPRM:ParamsInfile { file } {
    set synt_set {^[ ]*@set[ ]+(%[^ ]*)[ ]*=}
    set synt_string {^[ ]*@string[ ]+(%[^ ]*)[ ]*=}
    set lp {}
    foreach str [wokUtils:FILES:FileToList $file] {
	if { [regexp $synt_set $str all pname] || [regexp $synt_string $str all pname] } {
	    lappend lp $pname
	}
    }
    return [lsort $lp]
}
proc wokPRM:BrowseByFile {  w slb args } {
    global IWOK_GLOBALS
    set hlist [$slb subwidget hlist]
    
    set ent   [$hlist info anchor]
    if {$ent == ""} {
	return
    }

    set kid [$hlist info children $ent]
    if {$kid == {} } {
	set fullitem [$hlist info data $ent]
	set action [lindex $fullitem 0]
	tixBusy $IWOK_GLOBALS(PRM,toplevel) on
	switch -- $action {
	    
	    P {
		set parent [lindex [$hlist info data [$hlist info parent $ent]] 1]
		set IWOK_GLOBALS(PRM,ByFile,class) [wokPRM:ClassName [file root [file tail $parent]]]
		set prm [lindex $fullitem 1]
		set IWOK_GLOBALS(PRM,ByFile,name)  $prm
		set IWOK_GLOBALS(PRM,ByFile,value) ""
		catch {set IWOK_GLOBALS(PRM,ByFile,value) [wokparam -e $prm $IWOK_GLOBALS(PRM,location)]}
		wokReadFile $IWOK_GLOBALS(PRM,ByFile,text) $parent
		wokPRM:HiLiByFile
	    }

	}
	tixBusy $IWOK_GLOBALS(PRM,toplevel) off
    }
    return
}
;#
;# surligne dans le texte le parametre en cours si il y en a un
;#
proc wokPRM:HiLiByFile { } {
    global IWOK_GLOBALS
    if { "$IWOK_GLOBALS(PRM,ByFile,name)" != "" } {
	catch {
	    $IWOK_GLOBALS(PRM,ByFile,text) tag delete big
	    wokFAM $IWOK_GLOBALS(PRM,ByFile,text) $IWOK_GLOBALS(PRM,ByFile,name) \
		    { $IWOK_GLOBALS(PRM,ByFile,text) tag add big first last }
	    $IWOK_GLOBALS(PRM,ByFile,text) tag configure big -background orange \
		    -foreground black -borderwidth 2 \
		    -font -Adobe-Helvetica-Medium-R-Normal--*-120-* -relief raised
	    set index [$IWOK_GLOBALS(PRM,ByFile,text) search \
		    -exact $IWOK_GLOBALS(PRM,ByFile,name) 1.0]
	    $IWOK_GLOBALS(PRM,ByFile,text) see $index 
	}
    }
    return
}
;#
;#
;#
proc wokPRM:DBGByFile { {root {}} } {
    global IWOK_GLOBALS
    set hli $IWOK_GLOBALS(PRM,ByFile,hlist)
    foreach c [$hli info children $root] {
	puts "$c : data <[$hli info data $c]>"
        wokPRM:DBGByFile $c
    }
    return
}


;#
;#         (((((((  N O T E B O O K / H E L P / T O P L E V E L )))))))
;#
;# 
;# 
;#
proc wokPRM:Scope { path location } {
    return
}

;#
;#  test existence
;#
proc wokPRM:Exists { } {
    global IWOK_GLOBALS
    return [info exists IWOK_GLOBALS(PRM,toplevel)] 
}
;#
;# detruit le toplevel et unset les variables associees
;#
proc wokPRM:Kill { } {
    global IWOK_GLOBALS
    wokButton delw [list params $IWOK_GLOBALS(PRM,toplevel)]
    catch { 
	destroy $IWOK_GLOBALS(PRM,toplevel) 
	destroy $IWOK_GLOBALS(PRM,help)
    }
    foreach var [array names IWOK_GLOBALS PRM,*] {
	unset IWOK_GLOBALS($var) 
    }
    return
}


proc wokPRM:UPD { w } {
    return
}
proc wokPRM:NOT { command adr w name } {
    tixBusy $w on
    set id [after 10000 tixBusy $w off]
    $command $adr $w $name
    after cancel $id
    after 0 tixBusy $w off
    return
}
;#
;# Help
;#
proc wokPRM:Help { w } {
    global IWOK_GLOBALS
    global env
    set whelp [wokHelp [set IWOK_GLOBALS(PRM,help) .wokPRMHelp] "About parameters "]
    set texte [lindex $whelp 0] ; set label [lindex $whelp 1]
    if {[info exist IWOK_GLOBALS(windows)]} {
	if {[lsearch $IWOK_GLOBALS(windows) .wokPRMHelp ] == -1} {
	    lappend IWOK_GLOBALS(windows) .wokPRMHelp 
	}
    }
    wokReadFile $texte  $env(WOK_LIBRARY)/wokPRM.hlp
    wokFAM $texte <.*> { $texte tag add big first last }

    $texte tag configure big -background Bisque3 -foreground black -borderwidth 2 \
	     -relief raised

    $texte configure -state disabled
    update
    return   
}


