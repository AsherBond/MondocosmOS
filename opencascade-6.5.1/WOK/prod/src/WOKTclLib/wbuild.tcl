# CLE
# Workbench Builder
# Cas.Cade (c) 96
#
#
proc Interrupt:IsInterrupted {} {
    global Interrupt_IsInterrupted

    return $Interrupt_IsInterrupted
}

proc Interrupt:SIGNAL {sigtype {sigfunc ""}} {
    global Interrupt_IsInterrupted

    set Interrupt_IsInterrupted 1
    if {$sigfunc == ""} {
	puts $sigtype
    } else {
	eval $sigfunc
    }

    return 1
}

proc Interrupt:ResetInterruptFlag {} {
    global Interrupt_IsInterrupted

    set Interrupt_IsInterrupted 0
}

proc Interrupt:SetSignal {{sigfunc ""}} {
    global Interrupt_IsInterrupted

    set Interrupt_IsInterrupted 0
    signal trap * "Interrupt:SIGNAL $sigfunc %S"
    
    signal block SIGINT
}

proc wokbrowser { } {
    wokTL .woktopl:browser "Browser" {} BrowserInit
    return
}

proc winbuild_DoHelp { w } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS
    global env

    set IWOK_WINDOWS($w,help) [set wh .winbuildHelp]
    if {[info exist IWOK_GLOBALS(windows)]} {
	if {[lsearch $IWOK_GLOBALS(windows) $wh ] == -1} {
	    lappend IWOK_GLOBALS(windows) $wh 
	}
    }

    set whelp [wokHelp $wh "About building a workbench"]
    set texte [lindex $whelp 0] ; set label [lindex $whelp 1]
    wokReadFile $texte  $env(WOK_LIBRARY)/wbuild.hlp
    wokFAM $texte <.*> { $texte tag add big first last }
    $texte tag configure big -background Bisque3 -foreground black -borderwidth 2 \
	    -font -Adobe-Helvetica-Medium-R-Normal--*-120-* -relief raised
    update
    $texte configure -state disabled

    return
}

proc winbuild_MessageBox {title msg} {
    if {![winfo exist .messagebox]} {
	toplevel .messagebox
	
	button .messagebox.message -text $msg -relief flat
	button .messagebox.ok -text "Ok" -command "destroy .messagebox" 
	wm title .messagebox $title
	
	tixForm .messagebox.message -top 0 -left 0 -right -0 -bottom .messagebox.ok
	tixForm .messagebox.ok -bottom -0 -left 0 -right -0
	
	tkwait window .messagebox
    }
}

proc winbuild_SaveOrCancel {wroot filename} {
    global winbuild_tmpvar IWOK_GLOBALS

    set winbuild_tmpvar 2

    tixBusy $wroot on

    if {[file exist $filename]} {
	toplevel $wroot.ouinon
	button $wroot.ouinon.message -text "commands file $filename exist.\n\n do you want to use it, cancel to change the filename \n or rewrite it ?"
	tixButtonBox $wroot.ouinon.options 
	$wroot.ouinon.options add use -text "Use" -command "set winbuild_tmpvar 0"
	$wroot.ouinon.options add cancel -text "Cancel" -command "set winbuild_tmpvar 1"
	$wroot.ouinon.options add rewrite -text "Rewrite" -command "set winbuild_tmpvar 2"
	
	tixForm $wroot.ouinon.message -top 2 -left 0 -right -0
	tixForm $wroot.ouinon.options -top $wroot.ouinon.message -left 2 -right -2
	tkwait variable winbuild_tmpvar
	destroy $wroot.ouinon
    }

    tixBusy $wroot off

    return $winbuild_tmpvar
}

proc winbuild_BuildAddUdProc {wroot} {
    global IWOK_GLOBALS

    set winbuild_AddUdbody1 {global IWOK_GLOBALS;}
    set winbuild_AddUdbody2 "set hlist $IWOK_GLOBALS($wroot,list1);"
    set winbuild_AddUdbody3 {if {[$hlist info exist $item] == 0} {$hlist add $item -itemtype imagetext -text [lindex $item 1] -image [tix getimage [lindex $item 0]];}}
    set winbuild_AddUd "proc winbuild_${wroot}AddUd {item} {$winbuild_AddUdbody1 $winbuild_AddUdbody2 $winbuild_AddUdbody3}"
    eval $winbuild_AddUd
}

proc winbuild_BuildRemoveUdProc {wroot} {
    global IWOK_GLOBALS

    set winbuild_RemoveUdbody1 {global IWOK_GLOBALS;}

    set winbuild_RemoveUdbody2 "set hlist $IWOK_GLOBALS($wroot,list1);"

    set winbuild_RemoveUdbody3 {$hlist delete entry $item;}

    set winbuild_RemoveUd "proc winbuild_${wroot}RemoveUd { item } {$winbuild_RemoveUdbody1 $winbuild_RemoveUdbody2 $winbuild_RemoveUdbody3}"
    eval $winbuild_RemoveUd
}

proc winbuild_Profile {wroot} {
    global  IWOK_GLOBALS
    
    toplevel $IWOK_GLOBALS($wroot,window).savelog
    wm title $IWOK_GLOBALS($wroot,window).savelog "Profile"
    tixBusy $IWOK_GLOBALS($wroot,window) on
    wokConfigWB $IWOK_GLOBALS($wroot,window).savelog $wroot

    tkwait window $IWOK_GLOBALS($wroot,window).savelog

    set txtlab "$IWOK_GLOBALS($wroot,compilemode) $IWOK_GLOBALS($wroot,dbms)"
    $IWOK_GLOBALS($wroot,window).profile configure -text $txtlab
    tixBusy $IWOK_GLOBALS($wroot,window) off
}

proc winbuild_SaveLog {wroot} {
    global  IWOK_GLOBALS
    
    toplevel $IWOK_GLOBALS($wroot,window).savelog
    wm title $IWOK_GLOBALS($wroot,window).savelog "Save Log File"
    
    tixExFileSelectBox $IWOK_GLOBALS($wroot,window).savelog.file -filetypes {{{*.wlg} {Log Files}}} -directory $IWOK_GLOBALS($wroot,logdir) -pattern {*.wlg}
    tixForm $IWOK_GLOBALS($wroot,window).savelog.file -left 1 -right -2 -top 1 -bottom -1
    
    set fok [$IWOK_GLOBALS($wroot,window).savelog.file subwidget ok]
    set fcancel [$IWOK_GLOBALS($wroot,window).savelog.file subwidget cancel]
    set fdirlist [$IWOK_GLOBALS($wroot,window).savelog.file subwidget dirlist]
    set ffilelist [$IWOK_GLOBALS($wroot,window).savelog.file subwidget filelist]
    
    $ffilelist configure -command "winbuild_LogFileOk $wroot $IWOK_GLOBALS($wroot,window).savelog"
    $fdirlist chdir $IWOK_GLOBALS($wroot,logdir)
    $fok configure -command "winbuild_LogFileOk $wroot $IWOK_GLOBALS($wroot,window).savelog"
    $fcancel configure -command "winbuild_LogFileCancel $wroot $IWOK_GLOBALS($wroot,window).savelog"
    tixBusy $IWOK_GLOBALS($wroot,window) on
}


proc winbuild_SaveCfg {wroot} {
    global  IWOK_GLOBALS
    
    toplevel $IWOK_GLOBALS($wroot,window).savelog
    wm title $IWOK_GLOBALS($wroot,window).savelog "Save Configuration File"
    
    tixExFileSelectBox $IWOK_GLOBALS($wroot,window).savelog.file -filetypes {{{*.cfg} {Configuration Files}}} -directory $IWOK_GLOBALS($wroot,cfgdir) -pattern {*.cfg}
    tixForm $IWOK_GLOBALS($wroot,window).savelog.file -left 1 -right -2 -top 1 -bottom -1
    
    set fok [$IWOK_GLOBALS($wroot,window).savelog.file subwidget ok]
    set fcancel [$IWOK_GLOBALS($wroot,window).savelog.file subwidget cancel]
    set fdirlist [$IWOK_GLOBALS($wroot,window).savelog.file subwidget dirlist]
    set ffilelist [$IWOK_GLOBALS($wroot,window).savelog.file subwidget filelist]

    $ffilelist configure -command "winbuild_CfgFileOk $wroot 0 $IWOK_GLOBALS($wroot,window).savelog"    
    $fdirlist chdir $IWOK_GLOBALS($wroot,cfgdir)
    $fok configure -command "winbuild_CfgFileOk $wroot 0 $IWOK_GLOBALS($wroot,window).savelog"
    $fcancel configure -command "winbuild_CfgFileCancel $wroot $IWOK_GLOBALS($wroot,window).savelog"
    tixBusy  $IWOK_GLOBALS($wroot,window) on
}

proc winbuild_LoadCfg {wroot} {
    global  IWOK_GLOBALS
    
    toplevel $IWOK_GLOBALS($wroot,window).savelog
    wm title $IWOK_GLOBALS($wroot,window).savelog "Load Configuration File"
    
    tixExFileSelectBox $IWOK_GLOBALS($wroot,window).savelog.file -filetypes {{{*.cfg} {Configuration Files}}} -directory $IWOK_GLOBALS($wroot,cfgdir) -pattern {*.cfg}
    tixForm $IWOK_GLOBALS($wroot,window).savelog.file -left 1 -right -2 -top 1 -bottom -1
    
    set fok [$IWOK_GLOBALS($wroot,window).savelog.file subwidget ok]
    set fcancel [$IWOK_GLOBALS($wroot,window).savelog.file subwidget cancel]
    set fdirlist [$IWOK_GLOBALS($wroot,window).savelog.file subwidget dirlist]
    set ffilelist  [$IWOK_GLOBALS($wroot,window).savelog.file subwidget filelist]

    $fdirlist chdir $IWOK_GLOBALS($wroot,cfgdir)
    $fok configure -command "winbuild_CfgFileOk $wroot 1 $IWOK_GLOBALS($wroot,window).savelog"
    $fcancel configure -command "winbuild_CfgFileCancel $wroot $IWOK_GLOBALS($wroot,window).savelog"
    $ffilelist configure -command "winbuild_CfgFileOk $wroot 1 $IWOK_GLOBALS($wroot,window).savelog"
    tixBusy $IWOK_GLOBALS($wroot,window) on
}

proc winbuild_Break {wroot} {
    global IWOK_GLOBALS

    set IWOK_GLOBALS($wroot,stop) 1
    set IWOK_GLOBALS($wroot,endofjob) 1
    set IWOK_GLOBALS(winbuild,building) 0
    set IWOK_GLOBALS($wroot,building) 0
}

proc winbuild_Disable {wroot} {
    global IWOK_GLOBALS

    $IWOK_GLOBALS($wroot,window).build configure -state disabled
    $IWOK_GLOBALS($wroot,window).expr configure -state disabled
    $IWOK_GLOBALS($wroot,window).scom configure -state disabled
    $IWOK_GLOBALS($wroot,window).force configure -state disabled
    $IWOK_GLOBALS($wroot,window).all configure -state disabled
    $IWOK_GLOBALS($wroot,window).keep configure -state disabled
    $IWOK_GLOBALS($wroot,window).rall configure -state disabled
    $IWOK_GLOBALS($wroot,window).profile configure -state disabled
    $IWOK_GLOBALS($wroot,window).menubar.menu1 configure -state disabled
    $IWOK_GLOBALS($wroot,window).menubar.menu2 configure -state disabled
}

proc winbuild_Enable {wroot} {
    global IWOK_GLOBALS

    $IWOK_GLOBALS($wroot,window).build configure -state normal
    $IWOK_GLOBALS($wroot,window).expr configure -state normal
    $IWOK_GLOBALS($wroot,window).scom configure -state normal
    $IWOK_GLOBALS($wroot,window).force configure -state normal
    $IWOK_GLOBALS($wroot,window).all configure -state normal
    $IWOK_GLOBALS($wroot,window).keep configure -state normal
    $IWOK_GLOBALS($wroot,window).rall configure -state normal
    $IWOK_GLOBALS($wroot,window).menubar.menu1 configure -state normal
    $IWOK_GLOBALS($wroot,window).menubar.menu2 configure -state normal
    $IWOK_GLOBALS($wroot,window).profile configure -state normal
}

proc winbuild_Search {wroot} {
    global IWOK_GLOBALS

    set lst {}

    set itmlist [w_info -a $IWOK_GLOBALS($wroot,wcd)]    
    set itmlist [lsort $itmlist]
    set exprtomatch "$IWOK_GLOBALS($wroot,expression)*"

    foreach i $itmlist {
	set c [lindex $i 1]
	set udtype [lindex $i 0]
	if {[string match $exprtomatch $c]} {
	    if {($IWOK_GLOBALS($wroot,devunitfilter) == "All") || ($udtype == $IWOK_GLOBALS($wroot,devunitfilter))} {
		lappend lst $i
	    }
	}
    }

    return $lst
}

proc winbuild_DevUnitSearch {wroot item} {
    global IWOK_GLOBALS  winbuild_tabim

    set IWOK_GLOBALS($wroot,devunitfilter) $item
    set tmplst [winbuild_Search $wroot]
    
    if {$item != "All"} {
	set lst {}
	
	foreach p $tmplst {
	    set c [lindex $p 0]
	    
	    if {$c == $item} {
		lappend lst $p
	    }
	}
    } else {
	set lst $tmplst
    }

    set hlist $IWOK_GLOBALS($wroot,list)
    $hlist delete all
    foreach i $lst {
	if {[$hlist info exist $i] == 0} {
	    $hlist add $i -itemtype imagetext -text [lindex $i 1] -image $winbuild_tabim([lindex $i 0])
	    if {[$IWOK_GLOBALS($wroot,list1) info exist $i]} {
		winbuild_UpdateUdList $wroot $i 0
	    }
	}
    }
}

proc winbuild_LogFileOk {wroot w} {
    global IWOK_GLOBALS

    set txt [$IWOK_GLOBALS($wroot,console) get 1.0 end]

    set ffile [$IWOK_GLOBALS($wroot,window).savelog.file subwidget file]
    set fdir [$IWOK_GLOBALS($wroot,window).savelog.file subwidget dir]

    set dir [$fdir cget -selection]
    set file [$ffile cget -selection]

    set IWOK_GLOBALS($wroot,logfile) $file
    set IWOK_GLOBALS($wroot,logdir) $dir

    if {[catch {set f [open "$dir/$file" w]}] == 0} {
	
	puts $f $txt
	
	close $f
    } else {
	tixBusy $IWOK_GLOBALS($wroot,window).savelog on
	winbuild_MessageBox "Save Error" "Error while opening file:\n $dir/$file."
	tixBusy $IWOK_GLOBALS($wroot,window).savelog off
    }
	
    destroy $w
    
    tixBusy $IWOK_GLOBALS($wroot,window) off
}

proc winbuild_LogFileCancel {wroot w} {
    global IWOK_GLOBALS

    destroy $w
    tixBusy $IWOK_GLOBALS($wroot,window) off
}

proc winbuild_CfgFileOk {wroot sol w} {
    global IWOK_GLOBALS winbuild_tabim env

    if {$sol == 0} {
	set ffile [$IWOK_GLOBALS($wroot,window).savelog.file subwidget file]
	set fdir [$IWOK_GLOBALS($wroot,window).savelog.file subwidget dir]
	
	set dir [$fdir cget -selection]
	set file [$ffile cget -selection]
	
	set IWOK_GLOBALS($wroot,cfgfile) $file
	set IWOK_GLOBALS($wroot,cfgdir) $dir
	
	if {[catch {set f [open "$dir/$file" w]}] == 0} {
	
	    puts $f $IWOK_GLOBALS($wroot,wcd)
	    puts $f $IWOK_GLOBALS($wroot,dbms)
	    puts $f $IWOK_GLOBALS($wroot,compilemode)
	    
	    set step [winbuild_GetStep $wroot]
	    
	    puts $f $step
	    
	    set l [$IWOK_GLOBALS($wroot,list1) info children]
	    
	    foreach i $l {
		puts $f $i
	    }
	    
	    puts $f "END CFG"
	    
	    close $f
	} else {
	    tixBusy $IWOK_GLOBALS($wroot,window).savelog on
	    winbuild_MessageBox "Save Error" "Error while opening file:\n $dir/$file."
	    tixBusy $IWOK_GLOBALS($wroot,window).savelog off
	}
	destroy $w
	tixBusy $IWOK_GLOBALS($wroot,window) off
    } else {
	set IWOK_GLOBALS($wroot,devunitfilter) All
	set ffile [$IWOK_GLOBALS($wroot,window).savelog.file subwidget file]
	set fdir [$IWOK_GLOBALS($wroot,window).savelog.file subwidget dir]
	
	set dir [$fdir cget -selection]
	set file [$ffile cget -selection]
	
	set IWOK_GLOBALS($wroot,cfgfile) $file
	set IWOK_GLOBALS($wroot,cfgdir) $dir
	
	if {[catch {set f [open "$dir/$file" r]}] == 0} {
	    $IWOK_GLOBALS($wroot,list)  delete all
	    $IWOK_GLOBALS($wroot,list1) delete all
	    $IWOK_GLOBALS($wroot,console) delete 1.0 end
	    set ud ""
	    
	    gets $f IWOK_GLOBALS($wroot,wcd)
	    gets $f IWOK_GLOBALS(curprf,extractor)
	    gets $f IWOK_GLOBALS(curprf,compile)
	    
	    set IWOK_GLOBALS($wroot,compilemode) $IWOK_GLOBALS(curprf,compile)
	    set IWOK_GLOBALS($wroot,dbms) $IWOK_GLOBALS(curprf,extractor)
	    set txtlab "$IWOK_GLOBALS($wroot,compilemode) $IWOK_GLOBALS($wroot,dbms)"
	    $IWOK_GLOBALS($wroot,window).profile configure -text $txtlab	
	    
	    set step {}
	    gets $f step
	    
	    wbuild_SetOffStep $wroot
	    wbuild_SetStepListOn $wroot $step
	    
	    gets $f ud
	    
	    while {$ud != "END CFG"} {
		set i $ud
		set type [lindex $i 0]
		
		if {[catch {uinfo -t $IWOK_GLOBALS($wroot,wcd):[lindex $i 1]}] == 0} {
		    if {$type != [uinfo -t $IWOK_GLOBALS($wroot,wcd):[lindex $i 1]]} {
			$IWOK_GLOBALS($wroot,list1) add [list [uinfo -t $IWOK_GLOBALS($wroot,wcd):[lindex $i 1]] [lindex $i 1]]  -itemtype imagetext -text [lindex $i 1] -image $winbuild_tabim([uinfo -t [lindex $i 1]])
			tixBusy $IWOK_GLOBALS($wroot,window).savelog on
			winbuild_MessageBox "Load Warning" "Warning  : type of unit $IWOK_GLOBALS($wroot,wcd):[lindex $i 1] has changed.\nSave your configuration before exiting from 'Workbench Builder'.\n"
			tixBusy $IWOK_GLOBALS($wroot,window).savelog off
		    } else { 
			$IWOK_GLOBALS($wroot,list1) add $i -itemtype imagetext -text [lindex $i 1] -image $winbuild_tabim([lindex $i 0])
		    }
		} else {
		    winbuild_Msg W "Warning  : unit $IWOK_GLOBALS($wroot,wcd):[lindex $i 1] does not exist.\n" $wroot
		}
		gets $f ud
	    }
	    
	    foreach i [winbuild_Search $wroot] {
		$IWOK_GLOBALS($wroot,list) add $i -itemtype imagetext -text [lindex $i 1] -image $winbuild_tabim([lindex $i 0])
		if {[$IWOK_GLOBALS($wroot,list1) info exist $i]} {
		    winbuild_UpdateUdList $wroot $i 0
		}
	    }
	    
	    set IWOK_GLOBALS($wroot,currenterror) 0
	    set winbuild_station $env(WOKSTATION)
	    set iduser [id user]
	    set statname [id host]
	    wm title $IWOK_GLOBALS($wroot,window) "WorkBench Builder on $statname ($winbuild_station) as $iduser in $IWOK_GLOBALS($wroot,wcd)"
	    close $f
	} else {
	    tixBusy $IWOK_GLOBALS($wroot,window).savelog on
	    winbuild_MessageBox "Load Error" "Error while opening file:\n $dir/$file."
	    tixBusy $IWOK_GLOBALS($wroot,window).savelog off
	}
	
	destroy $w
	tixBusy $IWOK_GLOBALS($wroot,window) off
    }
}
    
proc winbuild_CfgFileCancel {wroot w} {
    global IWOK_GLOBALS

    destroy $w
    tixBusy $IWOK_GLOBALS($wroot,window) off
}

proc winbuild_WokMsg {code msg} {
    global IWOK_GLOBALS

    set line [$IWOK_GLOBALS($IWOK_GLOBALS(winbuild,builder),console) index insert]
    $IWOK_GLOBALS($IWOK_GLOBALS(winbuild,builder),console) insert end $msg\n

    if {$code == "I"} {
	set bingo [scan  $msg "Info    : Failed   %\[^ :\] " toto]
	if {$bingo} {
	    lappend IWOK_GLOBALS($IWOK_GLOBALS(winbuild,builder),errorud) $toto
	}
    } elseif {$code == "E"} {
	lappend IWOK_GLOBALS($IWOK_GLOBALS(winbuild,builder),errorlines) $line
	set endline [$IWOK_GLOBALS($IWOK_GLOBALS(winbuild,builder),console) index insert]
	$IWOK_GLOBALS($IWOK_GLOBALS(winbuild,builder),window).prev configure -state normal
	$IWOK_GLOBALS($IWOK_GLOBALS(winbuild,builder),window).next configure -state normal
	$IWOK_GLOBALS($IWOK_GLOBALS(winbuild,builder),console) tag add $line $line $endline
	$IWOK_GLOBALS($IWOK_GLOBALS(winbuild,builder),console) tag configure $line -foreground white
    }
    $IWOK_GLOBALS($IWOK_GLOBALS(winbuild,builder),console) see end

    update
} 

proc winbuild_Msg {code msg wroot} {
    global IWOK_GLOBALS

    $IWOK_GLOBALS($wroot,console) insert end $msg\n
    $IWOK_GLOBALS($wroot,console) see end
    update
} 

proc winbuild_Kill {wroot} {
    global  IWOK_GLOBALS

    if {[info exist IWOK_GLOBALS($wroot,window)]} {
	if {[winfo exist $IWOK_GLOBALS($wroot,window)]} {
	    if {$IWOK_GLOBALS($wroot,building) == 0} {
		destroy $IWOK_GLOBALS($wroot,window)
		wokButton delw [list wbuild $IWOK_GLOBALS($wroot,window)]
		rename "winbuild_${wroot}AddUd" ""
		rename "winbuild_${wroot}RemoveUd" ""
	    }
	}
    }
}

proc winbuild_ShowCommands {wroot} {
    global IWOK_GLOBALS

    $IWOK_GLOBALS($wroot,console) configure -state normal
    $IWOK_GLOBALS($wroot,console) delete 1.0 end

    set IWOK_GLOBALS($wroot,execstate) 0
    set IWOK_GLOBALS($wroot,mustshow) 1
    winbuild_Build $wroot
    set IWOK_GLOBALS($wroot,execstate) 1
    set IWOK_GLOBALS($wroot,mustshow) 0
}

proc winbuild_Build {wroot} {
    global IWOK_GLOBALS winbuild_tabim

    set l [$IWOK_GLOBALS($wroot,list1) info children]
    set ud ""
    
    if {[llength $l] == 0} {
	return
    }
    set step [winbuild_GetStep $wroot]
    if {[llength $step] == 0} {
	return
    }

    winbuild_Disable $wroot
    tixBusy $wroot on
    update
    $IWOK_GLOBALS($wroot,window).prev configure -state disabled
    $IWOK_GLOBALS($wroot,window).next configure -state disabled

    foreach p $l {
	set unit  [lindex $p 1]
	set ud "$ud $unit"
    }

    set IWOK_GLOBALS($wroot,errorlines) {}
    set IWOK_GLOBALS($wroot,errorud) {}
    $IWOK_GLOBALS($wroot,console) configure -state normal
    set force ""
    if {$IWOK_GLOBALS($wroot,force)} {
	set force "-f"
    }

    set cmpmode "-d"
    if {$IWOK_GLOBALS(curprf,compile) == "Optimized"} {
	set cmpmode "-o"
    }
    set command "wprocess  $IWOK_GLOBALS($wroot,wcd) $cmpmode -B $IWOK_GLOBALS(curprf,extractor) $force -DGroups=[join $step ,] -DUnits=[join $ud ,]"

    if {$IWOK_GLOBALS($wroot,mustshow) == 0} {
	msgsetcmd winbuild_WokMsg
	set IWOK_GLOBALS(winbuild,console) $IWOK_GLOBALS($wroot,console)
	set IWOK_GLOBALS(winbuild,builder) $wroot
    }

    if {$IWOK_GLOBALS($wroot,mustshow) == 0} {
	$IWOK_GLOBALS($wroot,console) configure -state normal
	$IWOK_GLOBALS($wroot,console) delete 1.0 end
	catch {set errors [eval $command]}
	set hlist $IWOK_GLOBALS($wroot,list1)
	#
	# update browser informations
	#
	browser:Update
	set udobject [$hlist info children]
	foreach ud $udobject {
	    set name [lindex $ud 1]
	    set type [lindex $ud 0]
	    if {[lsearch $IWOK_GLOBALS($wroot,errorud) $name] >= 0} {
		$hlist entryconfigure $ud -image $winbuild_tabim(caution)
	    } else {
		$hlist entryconfigure $ud -image $winbuild_tabim($type)
	    }
	}
	if {[llength $IWOK_GLOBALS($wroot,errorlines)] > 0} {
	    set line [lindex $IWOK_GLOBALS($wroot,errorlines) 0]
	    $IWOK_GLOBALS($wroot,console) tag configure $line -foreground $IWOK_GLOBALS($wroot,errorcolor)
	    $IWOK_GLOBALS($wroot,console) see $line
	}
    } else {
	winbuild_Msg "Info" $command $wroot 
    }

    if {$IWOK_GLOBALS($wroot,mustshow) == 0} {
	msgunsetcmd
    }

    $IWOK_GLOBALS($wroot,console) insert end "\n\n"

    set IWOK_GLOBALS($wroot,endofjob) 1
    winbuild_Enable $wroot
    tixBusy $wroot off
    update
}

proc winbuild_UpdateUdList {wroot sel fromremove} {
    global IWOK_GLOBALS 

    if {$sel != ""} {
	if {[$IWOK_GLOBALS($wroot,list) info exist $sel]} {
	    if {$fromremove} {
		$IWOK_GLOBALS($wroot,list) entryconfigure $sel -image [tix getimage [lindex $sel 0]]
	    } else {
		$IWOK_GLOBALS($wroot,list) entryconfigure $sel -image [tix getimage [lindex $sel 0]_open]
	    }
	}
    }
}

proc wbuild:Update {} {
    global IWOK_GLOBALS winbuild_tabim 

    set buildlist [wokButton getw wbuild]

    foreach wbuilditem $buildlist {
	if {[winfo exist $wbuilditem]} {
	    set hlist [${wbuilditem}.list subwidget hlist]
	    set hlist1 [${wbuilditem}.list1 subwidget hlist]

	    set itmlist [$hlist info children]
	    foreach i $itmlist {
		if {![wokinfo -x $IWOK_GLOBALS($wbuilditem,wcd):[lindex $i 1]]} {
		    $hlist delete entry $i
		}
	    }
	    set itmlist [winbuild_Search $wbuilditem]
	    foreach i $itmlist {
		if {[$hlist info exist $i] == 0} {
		    $hlist add $i -itemtype imagetext -text [lindex $i 1] -image $winbuild_tabim([lindex $i 0])
		    $hlist see $i
		    
		}
	    }
	    set itmlist [$hlist1 info children]
	    foreach i $itmlist {
		if {![wokinfo -x $IWOK_GLOBALS($wbuilditem,wcd):[lindex $i 1]]} {
		    $hlist1 delete entry $i
		}
	    }
	} else {
	    wokButton delw {wbuild $wbuilditem}
	}
    }
}

proc winbuild_ConsoleSearch { wroot } {
    global IWOK_GLOBALS

    wokSEA $IWOK_GLOBALS($wroot,console)
}

proc winbuild_KeepFailed {wroot} {
    global IWOK_GLOBALS winbuild_tabim

    set hlist1 $IWOK_GLOBALS($wroot,list1)
    set hlist  $IWOK_GLOBALS($wroot,list)   
    
    set itemlist [$hlist1 info children]

    foreach item $itemlist {
	if {$item != ""} {
	    if {[$hlist info exist $item]} {
		if {$winbuild_tabim(caution) != [$hlist1 entrycget $item -image]} {
		    $hlist entryconfigure $item -image [tix getimage [lindex $item 0]]
		    $hlist1 delete entry $item
		}
	    }
	}
    }
}

# PRINCIPALE
#
proc winbuild { {loc {}} {les_uds {}} } {
    global IWOK_GLOBALS wbuild_action winbuild_tabim env winbuild_treeaction

    set winbuild_treeaction [lindex [wokparam -e %WOKSteps_Groups] 0]

    if { $loc == {} } {
	set verrue [wokCWD readnocell]
    } else {
	set verrue $loc
    }

    if ![wokinfo -x $verrue] {
	wokDialBox .wokcd {Unknown location} "Location $verrue is unknown" {} -1 OK
	return
    }

    set fact  [wokinfo -n [wokinfo -f $verrue]] 
    set work  [wokinfo -n [wokinfo -s $verrue]] 
    set workb [wokinfo -n [wokinfo -w $verrue]] 

    set wroot ".w$fact:$work:$workb"
    winbuild_Kill $wroot

    if {[info exist IWOK_GLOBALS($wroot,building)] == 1} {
	if {$IWOK_GLOBALS($wroot,building)} {
	    return
	}
    }
    #
    # comme dans CAS.CADE
    #
    # Interrupt:SetSignal 

    set IWOK_GLOBALS($wroot,wcd)    "$fact:$work:$workb"
    set IWOK_GLOBALS($wroot,window) ".w$fact:$work:$workb"
    set IWOK_GLOBALS($wroot,logfile) "$workb.wlg"
    set IWOK_GLOBALS($wroot,logdir) [wokinfo -p AdmDir "$fact:$work:$workb"]
    set IWOK_GLOBALS($wroot,cfgdir) [wokinfo -p AdmDir "$fact:$work:$workb"]
    set IWOK_GLOBALS($wroot,cfgfile) "$workb.cfg"
    set IWOK_GLOBALS($wroot,force) 0
    set IWOK_GLOBALS($wroot,execstate) 1
    set IWOK_GLOBALS($wroot,stop) 0
    set IWOK_GLOBALS($wroot,endofjob) 1
    set IWOK_GLOBALS($wroot,mustshow) 0
    set IWOK_GLOBALS($wroot,building) 0
    set IWOK_GLOBALS($wroot,errorcolor) green
    set IWOK_GLOBALS($wroot,errorud) {}

    if {[info exist IWOK_GLOBALS($wroot,expression)] == 0} {
	set IWOK_GLOBALS($wroot,expression) ""
    }

    set IWOK_GLOBALS($wroot,devunitfilter) All
    set IWOK_GLOBALS($wroot,compilemode) [wokprofile -m]
    set IWOK_GLOBALS($wroot,dbms) [wokprofile -b]

    wokConfigDisplay $wroot

    if {[info exist IWOK_GLOBALS(winbuild,building)] == 0} {
	set IWOK_GLOBALS(winbuild,building) 0
    }

    toplevel $IWOK_GLOBALS($wroot,window)

    lappend IWOK_GLOBALS(windows) $IWOK_GLOBALS($wroot,window)
    set winbuild_station $env(WOKSTATION)
    set iduser [id user]
    set statname [id host]
    wm title $IWOK_GLOBALS($wroot,window) "WorkBench Builder on $statname ($winbuild_station) as $iduser in $fact:$work:$workb"

    wm geometry $IWOK_GLOBALS($wroot,window) 825x700+100+100
    wm minsize $IWOK_GLOBALS($wroot,window) 825 700
    wokButton setw [list wbuild $IWOK_GLOBALS($wroot,window)]


    if { $les_uds == {} } {
	set itmlist [winbuild_Search $wroot]
    } else {
	set itmlist [list $les_uds]

    }

    foreach type $IWOK_GLOBALS(ucreate-P) {
	set tn [lindex $type 1]
	set winbuild_tabim($tn) [tix getimage $tn]
    }

    set winbuild_tabim(caution) [tix getimage caution]

    button $IWOK_GLOBALS($wroot,window).menubar -state disabled -relief raise
    menubutton $IWOK_GLOBALS($wroot,window).menubar.menu1 -menu $IWOK_GLOBALS($wroot,window).menubar.menu1.options -text "File"
    menu $IWOK_GLOBALS($wroot,window).menubar.menu1.options

    $IWOK_GLOBALS($wroot,window).menubar.menu1.options add command -label "Profile ..."   -command "winbuild_Profile $wroot"
    $IWOK_GLOBALS($wroot,window).menubar.menu1.options add command -label "Load Cfg ..."   -command "winbuild_LoadCfg $wroot"
    $IWOK_GLOBALS($wroot,window).menubar.menu1.options add command -label "Save Cfg ..."   -command "winbuild_SaveCfg $wroot"
    $IWOK_GLOBALS($wroot,window).menubar.menu1.options add command -label "Save Log ..."   -command "winbuild_SaveLog $wroot"
    $IWOK_GLOBALS($wroot,window).menubar.menu1.options add command -label "Close"   -command "winbuild_Kill $wroot"

    menubutton $IWOK_GLOBALS($wroot,window).menubar.menu2 -menu $IWOK_GLOBALS($wroot,window).menubar.menu2.options -text "Help"
    menu $IWOK_GLOBALS($wroot,window).menubar.menu2.options 
    $IWOK_GLOBALS($wroot,window).menubar.menu2.options add command -label "Help ..."   -command "winbuild_DoHelp $wroot"
    $IWOK_GLOBALS($wroot,window).menubar.menu2.options add command -label "About ..."   -command {winbuild_MessageBox "About" "Workbench Builder Version 1.2"}

    button $IWOK_GLOBALS($wroot,window).profile  -relief raise -command "winbuild_Profile $wroot"

    tixScrolledText  $IWOK_GLOBALS($wroot,window).console 

    tixScrolledHList $IWOK_GLOBALS($wroot,window).list -width 8c
    set hlist [$IWOK_GLOBALS($wroot,window).list subwidget hlist]

    tixScrolledHList $IWOK_GLOBALS($wroot,window).list1 -width 8c
    set hlist1 [$IWOK_GLOBALS($wroot,window).list1 subwidget hlist]

    set IWOK_GLOBALS($wroot,list1) $hlist1
    set IWOK_GLOBALS($wroot,list)  $hlist

    winbuild_BuildAddUdProc $wroot
    winbuild_BuildRemoveUdProc $wroot

    $hlist config -drawbranch 0 -selectmode single
    $hlist1 config -drawbranch 0 -selectmode single
   
    tixLabelEntry $IWOK_GLOBALS($wroot,window).expr -label "Name :" -options {entry.width 20 label.width 0 entry.textVariable IWOK_GLOBALS($wroot,Expression)} 
    button $IWOK_GLOBALS($wroot,window).build -text "Build" -command "winbuild_Build $wroot"
    button $IWOK_GLOBALS($wroot,window).scom -text "Show Commands" -command "winbuild_ShowCommands $wroot"
    checkbutton  $IWOK_GLOBALS($wroot,window).force -text "Force" -variable  IWOK_GLOBALS($wroot,force)
    button $IWOK_GLOBALS($wroot,window).all -text "Add All" -command "winbuild_AddAll $wroot"
    button $IWOK_GLOBALS($wroot,window).rall -text "Del All" -command "winbuild_RemoveAll $wroot"
    button $IWOK_GLOBALS($wroot,window).prev -text "Previous Error"
    button $IWOK_GLOBALS($wroot,window).next -text "Next Error"
    button $IWOK_GLOBALS($wroot,window).search -text "Search" -command "winbuild_ConsoleSearch $wroot"
    button $IWOK_GLOBALS($wroot,window).keep -text "Keep Failed" -command "winbuild_KeepFailed $wroot"

    set IWOK_GLOBALS($wroot,step) [tixCheckList $IWOK_GLOBALS($wroot,window).step -scrollbar auto -options {hlist.indicator 1 hlist.indent 20 }]
    set IWOK_GLOBALS($wroot,currenterror) 0

    # popup menu
    set IWOK_GLOBALS($wroot,popup) [tixPopupMenu $wroot.popmenu -title "Select" ]
    set IWOK_GLOBALS($wroot,popup,menu) [$IWOK_GLOBALS($wroot,popup) subwidget menu]

    foreach t [linsert $IWOK_GLOBALS(ucreate-P) 0 [list All All] ] {
	$IWOK_GLOBALS($wroot,popup,menu) add command -label [lindex $t 1] -command "winbuild_DevUnitSearch $wroot [lindex $t 1]"
    }

    $IWOK_GLOBALS($wroot,popup) bind $hlist
    tixForm $IWOK_GLOBALS($wroot,window).menubar -top 2 -left 0 -right $IWOK_GLOBALS($wroot,window).profile
    tixForm $IWOK_GLOBALS($wroot,window).menubar.menu1 -left 0 -top 0 
    tixForm $IWOK_GLOBALS($wroot,window).menubar.menu2 -right -2 -top 0 
    tixForm $IWOK_GLOBALS($wroot,window).profile -right -2 -top 2 -bottom  $IWOK_GLOBALS($wroot,window).list1
    set txtlab "$IWOK_GLOBALS($wroot,compilemode) $IWOK_GLOBALS($wroot,dbms)"
    $IWOK_GLOBALS($wroot,window).profile configure -text $txtlab

    tixForm $IWOK_GLOBALS($wroot,window).list    -top $IWOK_GLOBALS($wroot,window).menubar  -left 2   -bottom $IWOK_GLOBALS($wroot,window).expr
    tixForm $IWOK_GLOBALS($wroot,window).list1   -top $IWOK_GLOBALS($wroot,window).menubar  -right -2 -bottom $IWOK_GLOBALS($wroot,window).expr
    tixForm $IWOK_GLOBALS($wroot,window).expr -left 2 -right $IWOK_GLOBALS($wroot,window).list1 -bottom $IWOK_GLOBALS($wroot,window).console

    tixForm $IWOK_GLOBALS($wroot,window).step -top $IWOK_GLOBALS($wroot,window).menubar  -bottom $IWOK_GLOBALS($wroot,window).force -left $IWOK_GLOBALS($wroot,window).list -right $IWOK_GLOBALS($wroot,window).list1
    tixForm $IWOK_GLOBALS($wroot,window).console -bottom $IWOK_GLOBALS($wroot,window).build -right -2 -left 2

    tixForm $IWOK_GLOBALS($wroot,window).force  -bottom $IWOK_GLOBALS($wroot,window).console -right $IWOK_GLOBALS($wroot,window).all
    tixForm $IWOK_GLOBALS($wroot,window).all    -bottom $IWOK_GLOBALS($wroot,window).console -top $IWOK_GLOBALS($wroot,window).list1 -left  $IWOK_GLOBALS($wroot,window).expr 
    tixForm $IWOK_GLOBALS($wroot,window).keep    -bottom $IWOK_GLOBALS($wroot,window).console -top $IWOK_GLOBALS($wroot,window).list1 -left $IWOK_GLOBALS($wroot,window).all -right $IWOK_GLOBALS($wroot,window).rall -bottom $IWOK_GLOBALS($wroot,window).console -top $IWOK_GLOBALS($wroot,window).list1
# -right $IWOK_GLOBALS($wroot,window).rall
    tixForm $IWOK_GLOBALS($wroot,window).rall    -bottom $IWOK_GLOBALS($wroot,window).console -right -2  -top $IWOK_GLOBALS($wroot,window).list1

    tixForm $IWOK_GLOBALS($wroot,window).build   -bottom -2 
    tixForm $IWOK_GLOBALS($wroot,window).scom    -left $IWOK_GLOBALS($wroot,window).build   -bottom -2 
    tixForm $IWOK_GLOBALS($wroot,window).prev    -left $IWOK_GLOBALS($wroot,window).scom     -bottom -2 
    tixForm $IWOK_GLOBALS($wroot,window).next    -left $IWOK_GLOBALS($wroot,window).prev    -bottom -2 
    tixForm $IWOK_GLOBALS($wroot,window).search   -left $IWOK_GLOBALS($wroot,window).next    -bottom -2 -right -2

    wbuild_StepList $wroot

    $IWOK_GLOBALS($wroot,window).prev configure -state disabled
    $IWOK_GLOBALS($wroot,window).next configure -state disabled

    set IWOK_GLOBALS($wroot,console) [$IWOK_GLOBALS($wroot,window).console subwidget text]

    set entry [$IWOK_GLOBALS($wroot,window).expr subwidget entry]

    bind $IWOK_GLOBALS($wroot,list) <ButtonRelease-1> {
	global IWOK_GLOBALS
	set wroot [winfo toplevel %W]

	set sel [$IWOK_GLOBALS($wroot,list) nearest %y]

	if {$sel != ""} {
	    winbuild_${wroot}AddUd $sel
	    winbuild_UpdateUdList $wroot $sel 0
	    $IWOK_GLOBALS($wroot,list1) see $sel
	}
    }

    bind $IWOK_GLOBALS($wroot,list1) <ButtonRelease-1> {
	global IWOK_GLOBALS
	set wroot [winfo toplevel %W]
	
	set sel [$IWOK_GLOBALS($wroot,list1) nearest %y]

	if {$sel != ""} {
	    winbuild_${wroot}RemoveUd $sel
	    if {[$IWOK_GLOBALS($wroot,list) info exist $sel]} {
		winbuild_UpdateUdList $wroot $sel 1
		$IWOK_GLOBALS($wroot,list) see $sel
	    }
	}
    }

    bind $entry <space> {
	global IWOK_GLOBALS winbuild_tabim

	set wroot [winfo toplevel %W]
	set IWOK_GLOBALS($wroot,expression) [winbuild_Trim [[$IWOK_GLOBALS($wroot,window).expr subwidget entry] get]]
	[$IWOK_GLOBALS($wroot,window).expr subwidget entry] delete 0 end
	[$IWOK_GLOBALS($wroot,window).expr subwidget entry] insert end $IWOK_GLOBALS($wroot,expression)
	$IWOK_GLOBALS($wroot,list) delete all
	set itmlist [winbuild_Search $wroot]

	if {[llength $itmlist] == 1} {
	    set IWOK_GLOBALS($wroot,expression) [lindex [lindex $itmlist 0] 1]
	    [$IWOK_GLOBALS($wroot,window).expr subwidget entry] delete 0 end
	    [$IWOK_GLOBALS($wroot,window).expr subwidget entry] insert end $IWOK_GLOBALS($wroot,expression)
	}
	foreach i $itmlist {
	    $IWOK_GLOBALS($wroot,list) add $i -itemtype imagetext -text [lindex $i 1] -image $winbuild_tabim([lindex $i 0])
	    if {[$IWOK_GLOBALS($wroot,list1) info exist $i]} {
		winbuild_UpdateUdList $wroot $i 0
	    }
	}
    }

    bind $IWOK_GLOBALS($wroot,window).prev <ButtonRelease-1> {
	global  IWOK_GLOBALS

	set wroot [winfo toplevel %W]
	
	if {[lsearch [$IWOK_GLOBALS($wroot,window).prev configure -state] "disabled"] == -1} {
	    set lenl [llength $IWOK_GLOBALS($wroot,errorlines)]
	    
	    if {$lenl > 0} {
		set numerror $IWOK_GLOBALS($wroot,currenterror)
		set line [lindex $IWOK_GLOBALS($wroot,errorlines) $numerror]
		$IWOK_GLOBALS($wroot,console) tag configure $line -foreground white

		set numerror [expr {$numerror - 1}]
		
		if {$numerror < 0} {
		    set numerror [expr {$lenl - 1}]
		}
		
		set IWOK_GLOBALS($wroot,currenterror) $numerror
		
		set line [lindex $IWOK_GLOBALS($wroot,errorlines) $numerror]
		$IWOK_GLOBALS($wroot,console) tag configure $line -foreground $IWOK_GLOBALS($wroot,errorcolor)
		
		set t $IWOK_GLOBALS($wroot,console)
		$t see $line
	    }
	}
    }

    bind $IWOK_GLOBALS($wroot,window).next <ButtonRelease-1> {
	global  IWOK_GLOBALS

	set wroot [winfo toplevel %W]

	if {[lsearch [$IWOK_GLOBALS($wroot,window).next configure -state] "disabled"] == -1} {
	    set lenl [llength $IWOK_GLOBALS($wroot,errorlines)]

	    if {$lenl > 0} {
		set numerror $IWOK_GLOBALS($wroot,currenterror)
		set line [lindex $IWOK_GLOBALS($wroot,errorlines) $numerror]
		$IWOK_GLOBALS($wroot,console) tag configure $line -foreground white

		incr numerror 1
		if {$numerror == $lenl} {
		    set numerror 0
		}
		
		set IWOK_GLOBALS($wroot,currenterror) $numerror
		
		set line [lindex $IWOK_GLOBALS($wroot,errorlines) $numerror]
		$IWOK_GLOBALS($wroot,console) tag configure $line -foreground $IWOK_GLOBALS($wroot,errorcolor)	
		set t $IWOK_GLOBALS($wroot,console)
		$t see $line
	    }
	}
    }

    foreach i $itmlist {
	$hlist add $i -itemtype imagetext -text [lindex $i 1] -image $winbuild_tabim([lindex $i 0])
    }

    if { $les_uds != {} } {
	winbuild_${wroot}AddUd $les_uds
	winbuild_UpdateUdList $wroot $les_uds 0
	$IWOK_GLOBALS($wroot,list1) see $les_uds
    }
}

proc winbuild_AddAll {wroot} {
    global IWOK_GLOBALS
    
    set hlist1 $IWOK_GLOBALS($wroot,list1)
    set hlist  $IWOK_GLOBALS($wroot,list)   
    set itemlist [$IWOK_GLOBALS($wroot,list) info children]

    foreach item $itemlist {
	if {[$hlist1 info exist $item] == 0} {
	    $hlist1 add $item -itemtype imagetext -text [lindex $item 1] -image [tix getimage [lindex $item 0]]
	    winbuild_UpdateUdList $wroot $item 0
	}
    }
}

proc winbuild_RemoveAll {wroot} {
    global IWOK_GLOBALS

    set hlist1 $IWOK_GLOBALS($wroot,list1)
    set hlist  $IWOK_GLOBALS($wroot,list)   
    
    set itemlist [$IWOK_GLOBALS($wroot,list) info children]

    foreach item $itemlist {
	winbuild_UpdateUdList $wroot $item 1
    }

    $hlist1 delete all
}

## build all units of a workbench
##
## syntax : wbuild <begin step> <end step>
##
## each step is executed for all units before
## processing the following
##
## ex.: wbuild src obj 
##   step 1: src  for ud1 ud2 ud3
##   step 2: xcpp for ud1 ud2
##   step 3: obj  for ud1 ud2 ud3
##
global winbuild_treeaction

proc wbuild_StepList {wroot} {
    global winbuild_treeaction IWOK_GLOBALS

    set c3 $IWOK_GLOBALS($wroot,window).step
 
    set h3 [$c3 subwidget hlist]
    
    for {set i 0} {$i < [llength $winbuild_treeaction]} {incr i} {
	set step [lindex $winbuild_treeaction $i]
	$h3 add $step -itemtype imagetext -text $step	
	$c3 setstatus $step  on
    }
    $c3 autosetmode  

    for {set i 0} {$i < [llength $winbuild_treeaction]} {incr i} {
	set step [lindex $winbuild_treeaction $i]
	$c3 close $step
    }
}

proc wbuild_SetOffStep {wroot} {
    global winbuild_treeaction IWOK_GLOBALS

    set c3 $IWOK_GLOBALS($wroot,window).step

    for {set i 0} {$i < [llength $winbuild_treeaction]} {incr i} {
	set step [lindex $winbuild_treeaction $i]
	$c3 setstatus $step  off
    }
}

proc wbuild_SetStepListOn {wroot l} {
    global winbuild_treeaction IWOK_GLOBALS

    set c3 $IWOK_GLOBALS($wroot,window).step

    foreach p $l {
	if {[lsearch $winbuild_treeaction $p] >= 0} {
	    $c3 setstatus $p  on
	} else {
	    winbuild_Msg "I" "Warning : step $p does not exist, it is ignored." $wroot 
	}
    }
}

proc winbuild_GetStep {wroot} {
    global winbuild_treeaction IWOK_GLOBALS

    set lstep {}
    
    set c3 $IWOK_GLOBALS($wroot,window).step

    for {set i 0} {$i < [llength $winbuild_treeaction]} {incr i} {
	set step [lindex $winbuild_treeaction $i]

	set sstat [$c3 getstatus $step]

	if {$sstat} {
	    lappend lstep $step
	}
    }

    return $lstep
}

proc wokConfigWB { w wroot} {
    global IWOK_GLOBALS
 
    tixLabelFrame $w.cm -label "Compilation mode"
    tixLabelFrame $w.em -label "Extraction mode"

    tixButtonBox $w.but -orientation horizontal -relief raised

    $w.but add apply  -text Apply  -command [list wokConfigApply  $w $wroot]
    $w.but add cancel -text Cancel -command [list wokConfigCancel $w $wroot]

    tixForm $w.em -left 2 -top 2 -right %50 -bottom %80
    tixForm $w.cm -left $w.em -top 2 -right %99 -bottom %80

    tixForm $w.but -left 2 -right %99 -top $w.cm -bottom %99

    set t [$w.cm subwidget frame]
    radiobutton $t.b1 -text Debug     -variable IWOK_GLOBALS(curprf,compile)  -value Debug 
    radiobutton $t.b2 -text Optimized -variable IWOK_GLOBALS(curprf,compile)  -value Optimized 
    pack $t.b1 $t.b2 -anchor w  -padx 1m -pady 1m
    set t [$w.em subwidget frame]

    set work  [wokinfo -n [wokinfo -w $IWOK_GLOBALS($wroot,wcd)]] 
    set dblist [wokparam -e %${work}_DBMSystems]
    if {[regexp {\{(.*)\}} $dblist t1 dblist1]} {
	set dblist1 [string trim $dblist1]
	set dblist [split $dblist1]
	#    puts $dblist
	#    puts [lsearch $dblist "OBJY"]
	#    puts [lsearch $dblist "OBJS"]
	#    puts [lsearch $dblist "DFLT"]
	
	if {[lsearch $dblist "OBJY"] >= 0} {
	    radiobutton $t.b2 -text Objectivity  -variable IWOK_GLOBALS(curprf,extractor) -value OBJY -state normal
	} else {
	    radiobutton $t.b2 -text Objectivity  -variable IWOK_GLOBALS(curprf,extractor) -value OBJY -state disabled
	}
	if {[lsearch $dblist "OBJS"] >= 0} {
	    radiobutton $t.b3 -text ObjectStore  -variable IWOK_GLOBALS(curprf,extractor) -value OBJS -state normal
	} else {
	    radiobutton $t.b3 -text ObjectStore  -variable IWOK_GLOBALS(curprf,extractor) -value OBJS -state disabled
	}
	if {[lsearch $dblist "DFLT"] >= 0} {
	    radiobutton $t.b4 -text Default -variable IWOK_GLOBALS(curprf,extractor) -value DFLT -state normal
	} else {
	    radiobutton $t.b4 -text Default -variable IWOK_GLOBALS(curprf,extractor) -value DFLT -state disabled
	}
	
	pack $t.b2 $t.b3 $t.b4 -anchor w  -padx 1m -pady 1m
	
	wokConfigDisplay $wroot   
    }
    return
}

proc wokConfigApply { w wroot} {
    global IWOK_GLOBALS

    set IWOK_GLOBALS($wroot,compilemode) $IWOK_GLOBALS(curprf,compile)
    set IWOK_GLOBALS($wroot,dbms) $IWOK_GLOBALS(curprf,extractor) 

    destroy $w

    return
}

proc wokConfigCancel { w wroot} {
    wokConfigDisplay $wroot
    destroy $w
    return
}

proc wokConfigDisplay {wroot  } {
    global IWOK_GLOBALS

    set IWOK_GLOBALS(curprf,extractor) $IWOK_GLOBALS($wroot,dbms)
    set IWOK_GLOBALS(curprf,compile) $IWOK_GLOBALS($wroot,compilemode)

    return
}


proc winbuild_Trim {str} {
    set a ""
    set len [string length $str]

    for {set i 0} {$i < $len} {incr i 1} {
	set x [string index $str $i]
	if {$x != " "} {
	    append a $x
	}
    }

    return $a
}
