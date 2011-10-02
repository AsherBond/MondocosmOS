proc BrowserSearch {where} {
    global Browser_Expression Browser_KindOfSearch
    
    if {$Browser_KindOfSearch == 1} {
	# search classes
	#
	BrowserSearchClasses $where $Browser_Expression
    } elseif {$Browser_KindOfSearch == 2} {
	# search methods
	#
	BrowserSearchMethods $where $Browser_Expression
    }
}

proc BrowserSearchDestroyWin {win} {
    global Browser_Menu Browser_packinfo

    destroy $win
    $Browser_Menu.windows.options delete $Browser_packinfo(wsearch)
}

proc BrowserSearchSetClasses {entry} {
    global Browser_KindOfSearch

    set Browser_KindOfSearch 1
    $entry configure -label "Classes :"
}

proc BrowserSearchSetMethods {entry} {
    global Browser_KindOfSearch

    set Browser_KindOfSearch 2
    $entry configure -label "Methods :"
}

proc BrowserSetKindOfSearch {n} {
    global Browser_SearchListbox

    $Browser_SearchListbox delete 0 end

    set lstpk [msinfo -p]
    set lstpk [lsort $lstpk]

    set doublefor 0
    
    if {$n < 4} {
	set doublefor 1
	set opt "-c"
	if {$n == 1} {
	    set test "stdclass"
	} elseif {$n == 2} {
	    set test "instclass"
	} elseif {$n == 3} {
	    set test "genclass"
	}
    } else {
	if {$n == 4} {
	    set opt "-P"
	} elseif {$n == 5} {
	    set opt "-a"
	} elseif {$n == 6} {
	    set opt "-e"
	} elseif {$n == 7} {
	    set opt "-p"
	} elseif {$n == 8} {
	    set opt "-x"
	} elseif {$n == 9} {
	    set opt "-i"
	}
    }

    foreach p $lstpk {
	set lsttype [mspkinfo $opt $p]
	set lsttype [lsort $lsttype]

	foreach t $lsttype {
	    if {$doublefor} {
		if {[msclinfo -t "${p}_$t"] == $test} {
		    $Browser_SearchListbox insert end "${p}_$t"
		}
	    } else {
		$Browser_SearchListbox insert end "${p}_$t"
	    }
	}
    }
}

# return a listbox
#
proc BrowserSearchBuildWindow {win searchtype} {
    if {[winfo exist $win.wsearch] == 1} {
	BrowserSearchDestroyWin $win.wsearch
    }

    global Browser_Menu Browser_packinfo Browser_Expression Browser_KindOfSearch Browser_SearchListbox
    
    toplevel $win.wsearch
    $Browser_Menu.windows.options add command -label "Search" -command "raise $win.wsearch"
    set Browser_packinfo(wsearch) [$Browser_Menu.windows.options index last]
    wm title $win.wsearch "Search"
    wm geometry $win.wsearch 400x400+100+100
    set Browser_KindOfSearch 1
    
    tixScrolledListBox $win.wsearch.result
    set Browser_SearchListbox [$win.wsearch.result subwidget listbox]
    $Browser_SearchListbox configure -exportselection 0

    if {$searchtype == 1} {
	tixLabelEntry $win.wsearch.expr -label "Classes :" -options {entry.width 20 label.width 0 entry.textVariable Browser_Expression}
    } elseif {$searchtype == 2} {
	tixOptionMenu $win.wsearch.expr -command BrowserSetKindOfSearch -label "Type : " -options {menubutton.width 8}
	$win.wsearch.expr add command 1 -label "Standard Class"
	$win.wsearch.expr add command 2 -label "Instantiation"
	$win.wsearch.expr add command 3 -label "Generic Class"
	$win.wsearch.expr add command 4 -label "Primitive"
	$win.wsearch.expr add command 5 -label "Alias"
	$win.wsearch.expr add command 6 -label "Enumeration"
	$win.wsearch.expr add command 7 -label "Pointer"
	$win.wsearch.expr add command 8 -label "Exception"
	$win.wsearch.expr add command 9 -label "Imported"
    }
    
    button $win.wsearch.menubar -state disabled -relief raise
    menubutton $win.wsearch.menubar.menu1 -menu $win.wsearch.menubar.menu1.options -text "File"	
    menu $win.wsearch.menubar.menu1.options	
    
    if {$searchtype == 1} {
	$win.wsearch.menubar.menu1.options add command -label "Classes" -command "BrowserSearchSetClasses $win.wsearch.expr"
	$win.wsearch.menubar.menu1.options add command -label "Methods" -command "BrowserSearchSetMethods $win.wsearch.expr"
    }
    
    $win.wsearch.menubar.menu1.options add command -label "Close"   -command "BrowserSearchDestroyWin $win.wsearch"
    tixForm $win.wsearch.menubar -top 2 -left 0 -right -0
    tixForm $win.wsearch.menubar.menu1 -left 0 -top 0
    tixForm $win.wsearch.expr -top $win.wsearch.menubar -left 0 -right -0
    tixForm $win.wsearch.result -top $win.wsearch.expr -left 0 -right -0 -bottom -0
    
    
    if {$searchtype == 1} {
	bind [$win.wsearch.expr subwidget entry] <Return> {
	    global Browser_KindOfSearch
	    set tt [winfo toplevel %W]
	    [$tt.result subwidget listbox] delete 0 end
	    BrowserSearch [$tt.result subwidget listbox]
	}
    }
    
    bind [$win.wsearch.result subwidget listbox] <ButtonRelease-1> {
	global Browser_win Browser_KindOfSearch
	
	if {$Browser_KindOfSearch == 1} {
	    set win [winfo toplevel %W]
	    set hlist [$win.result subwidget listbox]
	    set ind [$hlist curselection]
	    
	    if {$ind != ""} {
		set class [$hlist get $ind]
		catch {
		    if {[msclinfo -t $class] != ""} {
			Browser_UpdateAll $class
		    }
		}
	    }
	} elseif {$Browser_KindOfSearch == 2} {
	    set win [winfo toplevel %W]
	    set hlist [$win.result subwidget listbox]
	    set ind [$hlist curselection]
	    
	    if {$ind != ""} {
		set meth [$hlist get $ind]
		set pos [expr {[string first ":" $meth] - 1}]
		set class [string range $meth 0 $pos]
		Browser_UpdateAll $class
		DisplayMethodInfo $meth
	    }
	}
    }
    
    
    return $win.wsearch.result.listbox
}

proc BrowserSearchClasses {where lookingfor} {
    set lstpk [msinfo -p]
    set lstpk [lsort $lstpk]

    foreach p $lstpk {
	set lstcl [mspkinfo -c $p]
	set lstcl [lsort $lstcl]

	foreach c $lstcl {
	    if {[string match $lookingfor $c]} {
		 $where insert end "${p}_$c"
	    }
	}
    }
}

proc BrowserSearchMethods {where lookingfor} {
    set lstpk [msinfo -p]
    set lstpk [lsort $lstpk]
    set lookingformet "*:${lookingfor}(*"

    foreach p $lstpk {
	set lstcl [mspkinfo -c $p]
	set lstcl [lsort $lstcl]

	foreach c $lstcl {
	    set cl "${p}_$c"

	    if {[msclinfo -e $cl] == 0} {
		set lstmet [msclinfo -m $cl]
		foreach m $lstmet {
		    if {[string match $lookingformet $m]} {
			$where insert end $m
		    }
		}
	    }
	}
    }
}

###########################################################################
# Search instantiations
#
proc BrowserSearchInstDestroyWin {win} {
    global Browser_Menu Browser_packinfo

    destroy $win
    $Browser_Menu.windows.options delete $Browser_packinfo(winst)
}

proc BrowserSearchInst {win} {
    global Browser_Menu Browser_packinfo Browser_SearchInstInst

    if {[winfo exist $win.winst] == 0} {
	toplevel $win.winst
	$Browser_Menu.windows.options add command -label "Instantiations" -command "raise $win.winst"
	set Browser_packinfo(winst) [$Browser_Menu.windows.options index last]
	wm title $win.winst "Instantiations"
	wm geometry $win.winst 400x600+100+100

	button $win.winst.menubar -state disabled -relief raise
	menubutton $win.winst.menubar.menu1 -menu $win.winst.menubar.menu1.options -text "File"	
	menu $win.winst.menubar.menu1.options
	$win.winst.menubar.menu1.options add command -label "Close"   -command "BrowserSearchInstDestroyWin $win.winst"

	label $win.winst.lab1 -text "Generic classes :"
	label $win.winst.lab2 -text "Instantiates :"
	tixScrolledListBox $win.winst.gen
	set hlist [$win.winst.gen subwidget listbox]
	$hlist configure -exportselection 0
	tixScrolledListBox $win.winst.inst
	set hlist [$win.winst.inst subwidget listbox]
	$hlist configure -exportselection 0

	set Browser_SearchInstInst [$win.winst.inst subwidget listbox]

	tixForm $win.winst.menubar -top 0 -left 0 -right -0
	tixForm $win.winst.menubar.menu1 -top 0 -left 0
	tixForm $win.winst.lab1 -top $win.winst.menubar -left 2
	tixForm $win.winst.gen  -top $win.winst.lab1 -left 2 -right -2
	tixForm $win.winst.lab2 -left 2 -top $win.winst.gen
	tixForm $win.winst.inst  -top $win.winst.lab2 -bottom -0 -left 2 -right -2

	set lstpk [msinfo -p]
	set lstpk [lsort $lstpk]
	set insertlist [$win.winst.gen subwidget listbox]
	foreach p $lstpk {
	    set lsttype [mspkinfo -c $p]
	    set lsttype [lsort $lsttype]
	    
	    foreach t $lsttype {
		if {[msclinfo -t "${p}_$t"] == "genclass"} {
		    $insertlist insert end "${p}_$t"
		}
	    }
	}
	
	bind [$win.winst.gen subwidget listbox] <ButtonRelease-1> {
	    set win [winfo toplevel %W]
	    set hlist [$win.gen subwidget listbox]
	    set ind [$hlist curselection]
	    
	    if {$ind != ""} {
		set class [$hlist get $ind]
		BrowserSearchInstSearch $class
	    }
	}
	bind [$win.winst.inst subwidget listbox] <ButtonRelease-1> {
	    set win [winfo toplevel %W]
	    set hlist [$win.inst subwidget listbox]
	    set ind [$hlist curselection]
	    
	    if {$ind != ""} {
		set class [$hlist get $ind]
		Browser_UpdateAll $class
	    }
	}
    }
}

proc BrowserSearchInstSearch {classe} {
    global Browser_SearchInstInst

    $Browser_SearchInstInst delete 0 end
    set test "instclass"
    set lstpk [msinfo -p]
    set lstpk [lsort $lstpk]

    foreach p $lstpk {
	set lsttype [mspkinfo -c $p]
	set lsttype [lsort $lsttype]
	
	foreach t $lsttype {
	    if {[msclinfo -t "${p}_$t"] == $test} {
		if {[msinstinfo -g "${p}_$t"] == $classe} {
		    $Browser_SearchInstInst insert end "${p}_$t"
		}
	    }
	}
    }
}



