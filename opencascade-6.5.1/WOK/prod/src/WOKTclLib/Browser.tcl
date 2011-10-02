# Init Windows
#
proc Browser_KillWin {w} {
    destroy $w
    wokButton delw [list browser $w]
}

proc BrowserInit { w } {
    # globals variables
    #
    #     Browser_builgraph : true if we must build a OMT like graph
    #     Browser_win       : contains the main window path of the browser
    #     Browser_Menu      : contains the main menu path of the browser
    #
    global Browser_packinfo Browser_win Browser_Menu Browser_builgraph
    
    # init
    #
    set Browser_packinfo(TypeInfo)   1
    set Browser_Method(1)  1

    set Browser_builgraph  0
    set Browser_win $w

    wokButton setw [list browser $w]

    # Widgets
    #
    label $Browser_win.packtitlepack -text "Packages"
    label $Browser_win.packtitleuses -text "Uses"    
    label $Browser_win.packtitleinfo -text "Methods"    
    label $Browser_win.treeinfo -text "Classes Tree"    
        
    tixScrolledListBox $Browser_win.wpackage 
    $Browser_win.wpackage.listbox  configure -exportselection 0
    tixScrolledListBox $Browser_win.wpackmethod -width 4c
    $Browser_win.wpackmethod.listbox   configure -exportselection 0
    tixScrolledListBox $Browser_win.wpackuses -width 2.5c
    $Browser_win.wpackuses.listbox  configure -exportselection 0

    button $Browser_win.bupdatepackagelist   -text "Update packages"
    button $Browser_win.packmethodslist      -text "Methods"
    button $Browser_win.packexceptionslist   -text "Exceptions"
    button $Browser_win.packenumerationslist -text "Enumerations"
    button $Browser_win.packaliaseslist      -text "Aliases"
    button $Browser_win.packpointerslist     -text "Pointers"
    button $Browser_win.packprimitiveslist   -text "Primitives"
    button $Browser_win.packimportedlist     -text "Imported"

    frame $Browser_win.treeclass
    tixTree $Browser_win.treeclass.hlist -browsecmd ClassesTreeBrowse
    
    set hlist [$Browser_win.treeclass.hlist subwidget hlist]
    
    $hlist config -selectmode single -separator "." -width 30 -drawbranch 1 -indent 20 -indicator 1    
    frame $Browser_win.wframeclameth
    
    label $Browser_win.wframeclameth.titleclasses -text "Classes"
    label $Browser_win.wframeclameth.titlemethods -text "Methods"    
    
    tixScrolledListBox $Browser_win.wframeclameth.wclasses -width 8c
    $Browser_win.wframeclameth.wclasses.listbox  configure -exportselection 0
    tixScrolledListBox $Browser_win.wframeclameth.wmethod
    $Browser_win.wframeclameth.wmethod.listbox  configure -exportselection 0
    tixLabelFrame $Browser_win.wframeclameth.frame -label "Class attributes" -relief flat
    
    label $Browser_win.wframeclameth.frame.titlefield -text "Fields"   
    label $Browser_win.wframeclameth.frame.titleancestors -text "Ancestors"   
    tixScrolledListBox $Browser_win.wframeclameth.frame.wfield -width 8c -height 3c
    $Browser_win.wframeclameth.frame.wfield.listbox  configure -exportselection 0
    tixScrolledListBox $Browser_win.wframeclameth.frame.winherits -width 8c -height 3c 
    $Browser_win.wframeclameth.frame.winherits.listbox  configure -exportselection 0
    checkbutton $Browser_win.wframeclameth.frame.private -text "Private"           -state disabled -disabledforeground Black
    checkbutton $Browser_win.wframeclameth.frame.deferred -text "Deferred"         -state disabled -disabledforeground Black
    checkbutton $Browser_win.wframeclameth.frame.generic -text "Generic"           -state disabled -disabledforeground Black
    checkbutton $Browser_win.wframeclameth.frame.instantiates -text "Instantiates" -state disabled -disabledforeground Black
    
    tixLabelFrame $Browser_win.wmethodinfo -label "Method attributes" -relief flat
    label  $Browser_win.wmethodinfo.titleparam   -text "Parameters"   
    tixScrolledListBox $Browser_win.wmethodinfo.param -width 8c -height 3.5c 
    $Browser_win.wmethodinfo.param.listbox configure -exportselection 0
    checkbutton $Browser_win.wmethodinfo.priv    -text "Private" -state disabled -disabledforeground Black
    checkbutton $Browser_win.wmethodinfo.inline  -text "Inline" -state disabled -disabledforeground Black
    checkbutton $Browser_win.wmethodinfo.creturn -text "Return const" -state disabled -disabledforeground Black
    checkbutton $Browser_win.wmethodinfo.rreturn -text "Return ref." -state disabled -disabledforeground Black
    checkbutton $Browser_win.wmethodinfo.dest    -text "Destructor" -state disabled -disabledforeground Black
    checkbutton $Browser_win.wmethodinfo.alias   -text "Alias" -state disabled -disabledforeground Black
    tixLabelEntry $Browser_win.wmethodinfo.aliastext -disabledforeground Black \
	    -label "Alias text :" -state normal -options { entry.width 20 label.width 0 }
    
    
    # Menus
    #
    button $Browser_win.menubar -state disabled -relief raise
    menubutton $Browser_win.menubar.file -menu $Browser_win.menubar.file.options -text "File"
    menubutton $Browser_win.menubar.search -menu $Browser_win.menubar.search.options -text "Search"
    menubutton $Browser_win.menubar.windows -menu $Browser_win.menubar.windows.options -text "Windows"

    menu $Browser_win.menubar.file.options
    $Browser_win.menubar.file.options add  command -label "Close" -command "Browser_KillWin $Browser_win"

    menu $Browser_win.menubar.search.options 
    $Browser_win.menubar.search.options add  command -label "By name" -command "BrowserSearchBuildWindow $Browser_win 1"
    $Browser_win.menubar.search.options add  command -label "By type" -command "BrowserSearchBuildWindow $Browser_win 2"
    $Browser_win.menubar.search.options add  command -label "Instantiates" -command "BrowserSearchInst $Browser_win"

    menu $Browser_win.menubar.windows.options
    $Browser_win.menubar.windows.options add  checkbutton -label "Graph window"   -variable Browser_buildgraph
    
    menubutton $Browser_win.menubar.menu2 -menu $Browser_win.menubar.menu2.options -text "Help"
    menu $Browser_win.menubar.menu2.options 
    $Browser_win.menubar.menu2.options add command -label "About..."   -command {winbuild_MessageBox "About" "CDL Browser Version 1.1"}

    set Browser_Menu $Browser_win.menubar

    # Placement
    #
    tixForm $Browser_win.menubar -left 0 -top 2 -right -0
    tixForm $Browser_win.menubar.file  -top 0 -left 0
    tixForm $Browser_win.menubar.search  -top 0 -left $Browser_win.menubar.file
    tixForm $Browser_win.menubar.windows  -top 0 -left $Browser_win.menubar.search
    tixForm $Browser_win.menubar.menu2  -top 0 -right -2
    tixForm $Browser_win.packtitlepack -left 0 -top  $Browser_win.menubar
    tixForm $Browser_win.packtitleuses -left $Browser_win.wpackage  -top $Browser_win.menubar
    tixForm $Browser_win.packtitleinfo -left $Browser_win.wpackuses -top $Browser_win.menubar
    tixForm $Browser_win.treeinfo      -right -2.7c -top $Browser_win.menubar
    
    tixForm $Browser_win.treeclass -right -2 -top $Browser_win.treeinfo -bottom $Browser_win.packimportedlist
    tixForm $Browser_win.treeclass.hlist -top 0 -bottom -0 -right -0 -left 0
    tixForm $Browser_win.wpackage -bottom $Browser_win.bupdatepackagelist -top $Browser_win.packtitlepack
    tixForm $Browser_win.wpackmethod -bottom $Browser_win.bupdatepackagelist -left $Browser_win.wpackuses -right $Browser_win.treeclass -top $Browser_win.packtitlepack
    tixForm $Browser_win.wpackuses -bottom $Browser_win.bupdatepackagelist -left $Browser_win.wpackage -top $Browser_win.packtitlepack
    
    
    tixForm $Browser_win.bupdatepackagelist  -left 2 -bottom  $Browser_win.wframeclameth 
    tixForm $Browser_win.packmethodslist -left $Browser_win.bupdatepackagelist -bottom  $Browser_win.wframeclameth 
    tixForm $Browser_win.packexceptionslist -left $Browser_win.packmethodslist -bottom  $Browser_win.wframeclameth 
    tixForm $Browser_win.packenumerationslist -left $Browser_win.packexceptionslist -bottom  $Browser_win.wframeclameth 
    tixForm $Browser_win.packaliaseslist  -left $Browser_win.packenumerationslist -bottom  $Browser_win.wframeclameth 
    tixForm $Browser_win.packpointerslist -left $Browser_win.packaliaseslist -bottom  $Browser_win.wframeclameth 
    tixForm $Browser_win.packprimitiveslist  -left $Browser_win.packpointerslist -bottom  $Browser_win.wframeclameth 
    tixForm $Browser_win.packimportedlist -left $Browser_win.packprimitiveslist -bottom  $Browser_win.wframeclameth 
    
    tixForm $Browser_win.wframeclameth  -right -0 -left 0 -bottom $Browser_win.wmethodinfo
    tixForm $Browser_win.wframeclameth.frame.titlefield     -top 20 -left $Browser_win.wframeclameth.frame.winherits
    tixForm $Browser_win.wframeclameth.frame.titleancestors -top 20 -left $Browser_win.wframeclameth.frame.instantiates
    
    tixForm $Browser_win.wframeclameth.frame.wfield -left $Browser_win.wframeclameth.frame.winherits  -top $Browser_win.wframeclameth.frame.titlefield -bottom -10 -right -15
    tixForm $Browser_win.wframeclameth.frame.winherits -left $Browser_win.wframeclameth.frame.instantiates  -top $Browser_win.wframeclameth.frame.titlefield   -bottom -10
    tixForm $Browser_win.wframeclameth.frame -left 4 -right -0 -bottom -0
    
    tixForm $Browser_win.wframeclameth.frame.private -top 40 -left 5
    tixForm $Browser_win.wframeclameth.frame.deferred -top $Browser_win.wframeclameth.frame.private -left 5
    tixForm $Browser_win.wframeclameth.frame.generic -top $Browser_win.wframeclameth.frame.deferred -left 5
    tixForm $Browser_win.wframeclameth.frame.instantiates -top $Browser_win.wframeclameth.frame.generic -left 5
    
    tixForm $Browser_win.wframeclameth.titleclasses -left 0 -top 0
    tixForm $Browser_win.wframeclameth.titlemethods -left $Browser_win.wframeclameth.wclasses -top 0
    tixForm $Browser_win.wframeclameth.wclasses -left 0 -bottom $Browser_win.wframeclameth.frame -top $Browser_win.wframeclameth.titleclasses
    tixForm $Browser_win.wframeclameth.wmethod  -left $Browser_win.wframeclameth.wclasses -right -0 -bottom $Browser_win.wframeclameth.frame -top $Browser_win.wframeclameth.titleclasses

    # PLACEMENT : Method
    #
    tixForm $Browser_win.wmethodinfo  -left 4 -right -0 -bottom -0
    tixForm $Browser_win.wmethodinfo.priv -top 20 -left 5  
    tixForm $Browser_win.wmethodinfo.inline -top $Browser_win.wmethodinfo.priv -left 5
    tixForm $Browser_win.wmethodinfo.creturn -top $Browser_win.wmethodinfo.inline -left 5
    tixForm $Browser_win.wmethodinfo.rreturn -top $Browser_win.wmethodinfo.creturn -left 5
    tixForm $Browser_win.wmethodinfo.dest -top $Browser_win.wmethodinfo.rreturn -left 5
    tixForm $Browser_win.wmethodinfo.alias -top $Browser_win.wmethodinfo.dest -left 5
    tixForm $Browser_win.wmethodinfo.titleparam -left $Browser_win.wmethodinfo.creturn -top 20
    tixForm $Browser_win.wmethodinfo.param -left $Browser_win.wmethodinfo.creturn -top $Browser_win.wmethodinfo.titleparam -right -8
    tixForm $Browser_win.wmethodinfo.aliastext -top $Browser_win.wmethodinfo.alias -right -8 -left 5
    
    # Binding
    #
    
    bind $Browser_win.bupdatepackagelist <ButtonRelease-1> {
	global Browser_win

	UpdatePackageList $Browser_win
    }
    
    bind  $Browser_win.packmethodslist <ButtonRelease-1> {
	global Browser_packinfo Browser_win
	
	set Browser_packinfo(TypeInfo) 1
	UpdatePackInfo $Browser_packinfo(CurrentPackage)
	$Browser_win.packtitleinfo configure -text "Methods"
    }
    
    bind $Browser_win.packexceptionslist <ButtonRelease-1> {
	global Browser_packinfo Browser_win
	
	set Browser_packinfo(TypeInfo) 2
	UpdatePackInfo $Browser_packinfo(CurrentPackage)
	$Browser_win.packtitleinfo configure -text "Exceptions"
    }
    
    bind $Browser_win.packenumerationslist <ButtonRelease-1> {
	global Browser_packinfo Browser_win
	
	set Browser_packinfo(TypeInfo) 3
	UpdatePackInfo $Browser_packinfo(CurrentPackage)
	$Browser_win.packtitleinfo configure -text "Enumerations"
    }
    
    bind $Browser_win.packaliaseslist <ButtonRelease-1> {
	global Browser_packinfo Browser_win
	
	set Browser_packinfo(TypeInfo) 4
	UpdatePackInfo $Browser_packinfo(CurrentPackage)
	$Browser_win.packtitleinfo configure -text "Aliases"
    }
    
    bind  $Browser_win.packpointerslist <ButtonRelease-1> {
	global Browser_packinfo Browser_win
	
	set Browser_packinfo(TypeInfo) 5
	UpdatePackInfo $Browser_packinfo(CurrentPackage)
	$Browser_win.packtitleinfo configure -text "Pointers"
    }
    
    bind  $Browser_win.packprimitiveslist <ButtonRelease-1> {
	global Browser_packinfo Browser_win
	
	set Browser_packinfo(TypeInfo) 6
	UpdatePackInfo $Browser_packinfo(CurrentPackage)
	$Browser_win.packtitleinfo configure -text "Primitives"
    }
    
    bind  $Browser_win.packimportedlist <ButtonRelease-1> {
	global Browser_packinfo Browser_win
	
	set Browser_packinfo(TypeInfo) 7
	UpdatePackInfo $Browser_packinfo(CurrentPackage)
	$Browser_win.packtitleinfo configure -text "Importeds"
    }
    
    bind $Browser_win.wpackage.listbox <ButtonRelease-1> {
	global Browser_packinfo Browser_win
	set ind [$Browser_win.wpackage.listbox curselection]
	
	if {$ind != ""} {
	    set classe [$Browser_win.wpackage.listbox get $ind]
	    set Browser_packinfo(CurrentPackage) $classe
	    
	    UpdatePackUsesList $Browser_packinfo(CurrentPackage)
	    UpdatePackInfo $Browser_packinfo(CurrentPackage)
	    UpdateClassList $Browser_packinfo(CurrentPackage)
	    DisplayMethodInfo ""
	}
    }

    bind $Browser_win.wpackuses.listbox <ButtonRelease-1> {
	global Browser_packinfo Browser_win
	set ind [$Browser_win.wpackuses.listbox curselection]
		
	if {$ind != ""} {
	    set classe [$Browser_win.wpackuses.listbox get $ind]
	    Browser_UpdateAll $classe
	}
    }
        
    bind $Browser_win.wpackmethod.listbox <ButtonRelease-1> {
	global Browser_packinfo
	
	if {$Browser_packinfo(TypeInfo) == 1} {
	    set ind [$Browser_win.wpackmethod.listbox curselection]
	
	    if {$ind != ""} {
		set meth [$Browser_win.wpackmethod.listbox get $ind]
		DisplayMethodInfo $meth
	    }
	}
    }

    bind $Browser_win.wframeclameth.frame.winherits.listbox <ButtonRelease-1> {
	global Browser_packinfo

	set ind [$Browser_win.wframeclameth.frame.winherits.listbox curselection]
	
	if {$ind != ""} {
	    set classe [$Browser_win.wframeclameth.frame.winherits.listbox get $ind]
	    Browser_UpdateAll $classe
	}
    }
    
    bind $Browser_win.wframeclameth.wmethod.listbox <ButtonRelease-1> {
	set ind [$Browser_win.wframeclameth.wmethod.listbox curselection]
	
	if {$ind != ""} {
	    set meth [$Browser_win.wframeclameth.wmethod.listbox get $ind]
	    DisplayMethodInfo $meth
	}
    }
    
    bind $Browser_win.wframeclameth.wclasses.listbox <ButtonRelease-1> {
	set ind [$Browser_win.wframeclameth.wclasses.listbox curselection]
	
	if {$ind != ""} {
	    set classe [$Browser_win.wframeclameth.wclasses.listbox get $ind]
	    Browser_DisplayClassInfo $classe
	}
    }

    Browser_Clear
    UpdatePackageList $Browser_win
}

proc Browser_UpdateAll {classe} {
    global Browser_packinfo
    
    set pos [expr {[string first "_" $classe] - 1}]
    
    if {$pos >= 1} {
	set pack [string range $classe 0 $pos]
	
	if {$pack != $Browser_packinfo(CurrentPackage)} {
	    set Browser_packinfo(CurrentPackage) $pack
	    
	    UpdatePackUsesList $Browser_packinfo(CurrentPackage)
	    UpdatePackInfo $Browser_packinfo(CurrentPackage)
	    UpdateClassList $Browser_packinfo(CurrentPackage)
	    DisplayMethodInfo ""
	}
	Browser_DisplayClassInfo $classe
    }
}

proc browser:Update {} {
    set browserlist [wokButton getw browser]

    foreach browseritem $browserlist {
	if {[winfo exist $browseritem]} {
	    UpdatePackageList $browseritem
	}
    }
}

proc ClassesTreeBrowse {item} {
    global Browser_win Browser_buildgraph Browser_packinfo

    set father $item
    set lst [split $father .]
    set nindex [expr {[llength $lst] - 1}]
    set classe [lindex $lst $nindex]
    
    set pos [expr {[string first "_" $classe] - 1}]
    
    if {$pos >= 1} {
	set pack [string range $classe 0 $pos]

	if {$pack != $Browser_packinfo(CurrentPackage)} {
	    set Browser_packinfo(CurrentPackage) $pack
	    
	    UpdatePackUsesList $Browser_packinfo(CurrentPackage)
	    UpdatePackInfo $Browser_packinfo(CurrentPackage)
	    UpdateClassList $Browser_packinfo(CurrentPackage)
	    DisplayMethodInfo ""
	}
	Browser_DisplayClassInfo $classe
    }
}

proc BuildClassesTree {where classe function} {
    global Browser_packinfo Browser_win Browser_Menu Browser_builgraph

    set thelistpath [$function $classe]
    set thecurrentpath [lindex $thelistpath [expr {[llength $thelistpath] - 1}]]
    set thecurrentitem $thecurrentpath
    set hlist [$where.hlist subwidget hlist]


    while {[llength $thelistpath] > 0} {
	if {[$hlist info exist $thecurrentpath] == 0} {
	    $hlist add $thecurrentpath -text $thecurrentitem
	} 
	
	set parent [$hlist info parent $thecurrentpath]
	if {$parent != ""} {
	    $where.hlist setmode $parent close
	}
	
	set anindex [expr {[llength $thelistpath] - 1}]
	set thelistpath [lreplace $thelistpath $anindex $anindex]
	
	if {[llength $thelistpath] > 0} {
	    set thecurrentitem [lindex $thelistpath [expr {[llength $thelistpath] - 1}]]
	    set thecurrentpath "$thecurrentpath.$thecurrentitem"
	}
    }
}

proc BuildInheritanceList classe {
    global env

    set c {}
    if {[msclinfo -e $classe] == 1} {
	return {}
    }
    
    set m [msclinfo -i $classe]
    lappend c $classe
    
    while {$m != ""} {
	lappend c $m
	if {[msclinfo -e $classe] == 1} {
	    return {}
	}
    
	if {[catch {set m [msclinfo -i $m]}] != 0} {
	    return $c
	}
    }
    
    return $c
}

# clear all the list of the class window
#
proc BrowserClearClassesWindow {win} {
    $win.wclasses.listbox delete 0 end
    $win.wmethod.listbox delete 0 end
    $win.frame.wfield.listbox delete 0 end
    $win.frame.winherits.listbox delete 0 end
}

# Functions
#
proc UpdatePackageList {win} {
    $win.wpackage.listbox delete 0 end
    set pklist [msinfo -p]
    set pklist [lsort $pklist]

    foreach pk $pklist {
	$win.wpackage.listbox insert end $pk
    }
}

proc UpdateClassList package {
    global Browser_win

    $Browser_win.wframeclameth.wclasses.listbox delete 0 end
    $Browser_win.wframeclameth.wmethod.listbox delete 0 end
    $Browser_win.wframeclameth.frame configure -label "Class attributes :"
    $Browser_win.wframeclameth.frame.generic deselect
    $Browser_win.wframeclameth.frame.instantiates deselect
    $Browser_win.wframeclameth.frame.deferred deselect
    $Browser_win.wframeclameth.frame.private deselect
    $Browser_win.wframeclameth.frame.wfield.listbox delete 0 end
    $Browser_win.wframeclameth.frame.winherits.listbox delete 0 end

    set HasMethod 0
    set classelist [mspkinfo -c $package]
    set classelist [lsort $classelist]
    [$Browser_win.treeclass.hlist subwidget hlist] delete all
    foreach cl $classelist {
	set fn "$package\_$cl"
	BuildClassesTree $Browser_win.treeclass $fn BuildInheritanceList

	$Browser_win.wframeclameth.wclasses.listbox insert end $fn
    }
}

proc UpdateMethodList classe {
    global Browser_win

    $Browser_win.wframeclameth.wmethod.listbox delete 0 end

    foreach cl [msclinfo -m $classe] {
	$Browser_win.wframeclameth.wmethod.listbox insert end $cl
    }
    DisplayMethodInfo ""
}

proc UpdatePackInfo package {
    global Browser_packinfo Browser_win

    $Browser_win.wpackmethod.listbox delete 0 end

    if {$Browser_packinfo(TypeInfo) == 1} {
	foreach cl [mspkinfo -m $package] {
	    $Browser_win.wpackmethod.listbox insert end $cl
	}
    } elseif {$Browser_packinfo(TypeInfo) == 2} {
	foreach cl [mspkinfo -x $package] {
	    $Browser_win.wpackmethod.listbox insert end $cl
	}
    } elseif {$Browser_packinfo(TypeInfo) == 3} {
	foreach cl [mspkinfo -e $package] {
	    $Browser_win.wpackmethod.listbox insert end $cl
	}
    } elseif {$Browser_packinfo(TypeInfo) == 4} {
	foreach cl [mspkinfo -a $package] {
	    $Browser_win.wpackmethod.listbox insert end $cl
	}
    } elseif {$Browser_packinfo(TypeInfo) == 5} {
	foreach cl [mspkinfo -p $package] {
	    $Browser_win.wpackmethod.listbox insert end $cl
	}
    } elseif {$Browser_packinfo(TypeInfo) == 6} {
	foreach cl [mspkinfo -P $package] {
	    $Browser_win.wpackmethod.listbox insert end $cl
	}
    } elseif {$Browser_packinfo(TypeInfo) == 7} {
	foreach cl [mspkinfo -i $package] {
	    $Browser_win.wpackmethod.listbox insert end $cl
	}
    }
    
}

proc UpdatePackUsesList package {
    global Browser_win 

    $Browser_win.wpackuses.listbox delete 0 end
    
    set useslst [mspkinfo -u $package]
    set useslst [lsort $useslst]

    foreach cl $useslst {
	if {$cl != $package} {
	    $Browser_win.wpackuses.listbox insert end $cl
	}
    }
}

proc DisplayMethodInfo method {
    global Browser_win

    if {$method == ""} {
	$Browser_win.wmethodinfo.inline deselect
	$Browser_win.wmethodinfo configure -label "Method attributes :"
	$Browser_win.wmethodinfo.priv deselect
	$Browser_win.wmethodinfo.rreturn deselect
	$Browser_win.wmethodinfo.creturn deselect
	$Browser_win.wmethodinfo.dest deselect
	set entry [$Browser_win.wmethodinfo.aliastext subwidget entry]
	$entry delete 0 end
	$Browser_win.wmethodinfo.param.listbox delete 0 end

	return
    }

    catch {
	set fn [msmthinfo -n $method]    
	set ret [msmthinfo -r $method]
	
	if {$ret == ""} {
	    $Browser_win.wmethodinfo configure -label "Method attributes : $fn"
	} else {
	    $Browser_win.wmethodinfo configure -label "Method attributes : $fn returns $ret"
	}
	
	if {[msmthinfo -p $method] == 1} {
	    $Browser_win.wmethodinfo.priv select
	} else {
	    $Browser_win.wmethodinfo.priv deselect
	}
	
	if {[msmthinfo -i $method] == 1} {
	    $Browser_win.wmethodinfo.inline select
	} else {
	    $Browser_win.wmethodinfo.inline deselect
	}
	
	if {[msmthinfo -f $method] == 1} {
	    $Browser_win.wmethodinfo.rreturn select
	} else {
	    $Browser_win.wmethodinfo.rreturn deselect
	}
	
	if {[msmthinfo -c $method] == 1} {
	    $Browser_win.wmethodinfo.creturn select
	} else {
	    $Browser_win.wmethodinfo.creturn deselect
	}
	
	if {[msmthinfo -d $method] == 1} {
	    $Browser_win.wmethodinfo.dest select
	} else {
	    $Browser_win.wmethodinfo.dest deselect
	}
	
	set entry [$Browser_win.wmethodinfo.aliastext subwidget entry]
	
	$entry delete 0 end
	
	set alias [msmthinfo -A $method]
	set alias [lindex $alias 0]
	if {$alias != ""} {
	    $Browser_win.wmethodinfo.alias select
	    $entry insert end $alias
	} else {
	    $Browser_win.wmethodinfo.alias deselect
	}
	
	$Browser_win.wmethodinfo.param.listbox delete 0 end
	
	foreach par [msmthinfo -a $method] {
	    $Browser_win.wmethodinfo.param.listbox insert end $par
	}
    }
}



proc Browser_Clear {} {
    global Browser_win
    
    $Browser_win.wmethodinfo.inline deselect
    $Browser_win.wmethodinfo configure -label "Method attributes :"
    $Browser_win.wmethodinfo.priv deselect
    $Browser_win.wmethodinfo.rreturn deselect
    $Browser_win.wmethodinfo.creturn deselect
    $Browser_win.wmethodinfo.dest deselect
    set entry [$Browser_win.wmethodinfo.aliastext subwidget entry]
    $entry delete 0 end
    $Browser_win.wmethodinfo.param.listbox delete 0 end
    $Browser_win.wframeclameth.wclasses.listbox delete 0 end
    $Browser_win.wframeclameth.wmethod.listbox delete 0 end
    $Browser_win.wframeclameth.frame configure -label "Class attributes :"
    $Browser_win.wframeclameth.frame.generic deselect
    $Browser_win.wframeclameth.frame.instantiates deselect
    $Browser_win.wframeclameth.frame.deferred deselect
    $Browser_win.wframeclameth.frame.private deselect
    $Browser_win.wframeclameth.frame.wfield.listbox delete 0 end
    $Browser_win.wframeclameth.frame.winherits.listbox delete 0 end
}

proc Browser_DisplayClassInfo {classe} {
    global Browser_win Browser_buildgraph

    set err [catch {msclinfo -t $classe}]

    if {$classe != "" && ($err == 0)} {
	set rootwind $Browser_win.wframeclameth
	set Browser_packinfo(CurrentClass) $classe
	
	set type "manipulated by value"
	
	if {[msclinfo -e $classe]} {
	    set type "Incomplete"
	} elseif {[msclinfo -P $classe]} {
	    set type "Persistent"
	} elseif {[msclinfo -T $classe]} {
	    set type "Transient"
	} elseif {[msclinfo -S $classe]} {
	    set type "Storable"
	}
	
	$rootwind.frame configure -label "Class attributes : $classe is $type"
	
	if {[msclinfo -t $classe] == "genclass"} {
	    $rootwind.frame.generic select
	} else {
	    $rootwind.frame.generic deselect
	}
	
	if {[msclinfo -t $classe] == "instclass"} {
	    $rootwind.frame.instantiates select 
	} else {
	    $rootwind.frame.instantiates deselect
	}
	
	if {[msclinfo -d $classe] == 1} {
	    $rootwind.frame.deferred select
	} else {
	    $rootwind.frame.deferred deselect
	}
	
	if {[msclinfo -p $classe] == 1} {
	    $rootwind.frame.private select
	} else {
	    $rootwind.frame.private deselect
	}
	
	$rootwind.frame.wfield.listbox delete 0 end
	$rootwind.frame.winherits.listbox delete 0 end
	
	if {[msclinfo -e $classe] == 0} {
	    foreach p [msclinfo -C $classe] {
		$rootwind.frame.wfield.listbox insert end $p
	    }
	    
	    foreach p [msclinfo -i $classe] {
		$rootwind.frame.winherits.listbox insert end $p
	    }
	    
	    UpdateMethodList $classe
	} else {
	    $rootwind.wmethod.listbox delete 0 end
	}
	
	if {$Browser_buildgraph} {
	    BrowserOMTInitWindow  $Browser_win $classe 0
	}
    }
}
