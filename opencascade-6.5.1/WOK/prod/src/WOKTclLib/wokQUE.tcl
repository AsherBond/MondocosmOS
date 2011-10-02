proc wokWaffQueue { {loc {}} } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS
    

    if { $loc == {} } {
	set verrue [wokCWD readnocell]
    } else {
	regexp {(.*):Queue} $loc all verrue 
    }

    if ![wokinfo -x $verrue] {
	wokDialBox .wokcd {Unknown location} "Location $verrue is unknown" {} -1 OK
	return
    }
    set curwb [wokinfo -w $verrue]
    set w  [wokTPL queue${verrue}]
    if [winfo exists $w ] {
	wm deiconify $w
	raise $w
	return 
    }
    
    if { [wokStore:Report:SetQName $curwb] == {} } {
	return
    }

    toplevel $w
    wm title $w "Integration Queue of $curwb"
    wm geometry $w 742x970+515+2

    wokButton setw [list reports $w]
    
    menubutton $w.file -menu $w.file.m -text File -underline 0 -takefocus 0
    menu $w.file.m 
    $w.file.m add command -label "Close     " -underline 1 -command [list wokWaffQueueExit $w]

    menubutton $w.help -menu $w.help.m -text Help -underline 0 -takefocus 0
    menu $w.help.m
    $w.help.m add command -label "Help"      -underline 1 -command [list wokWaffQueueHelp $w]

    frame $w.top -relief sunken -bd 1 
    label $w.lab -relief raised
    
    tixPanedWindow $w.top.pane -orient vertical -paneborderwidth 0 -separatorbg gray50
    pack $w.top.pane -side top -expand yes -fill both -padx 10 -pady 10
    
    set p1 [$w.top.pane add list -min 70 -size 200]
    set p2 [$w.top.pane add text -min 70]
    
    tixScrolledHList   $p1.list ; set hlist [$p1.list subwidget hlist]
    tixScrolledText    $p2.text ; $p2.text subwidget text    config -font $IWOK_GLOBALS(font)
    
    $hlist config -font $IWOK_GLOBALS(font) -separator ^ -drawbranch 0 \
	    -browsecmd [list wokDisplayReport $w] ;#-selectmode single

    pack $p1.list -expand yes -fill both -padx 1 -pady 1
    pack $p2.text -expand yes -fill both -padx 1 -pady 1
    
    tixLabelFrame $w.reports -label "Reports queue"
    set fw [$w.reports subwidget frame]

    tixButtonBox $fw.but -orientation horizontal -relief flat -padx 0 -pady 0
    pack $fw.but -fill both

    set buttons1 [list \
	    {integrate     "Integrate" disabled  wokIntegrateReport} \
	    {remove        "Remove"    disabled  wokRemoveReport} \
	    {search        "Search"    disabled  wokSearchReport} \
	    {updatequeue   "Update"    active    wokUpdateQueue} ]

    foreach b $buttons1 {
	$fw.but add [lindex $b 0] -text [lindex $b 1] 
	[$fw.but subwidget [lindex $b 0]] configure -state [lindex $b 2] -command [list [lindex $b 3] $w] 
    }

    tixLabelFrame $w.journal -label "Integration jounal" 
    set gw [$w.journal subwidget frame]

    tixButtonBox $gw.but -orientation horizontal -relief flat -padx 0 -pady 0
    pack $gw.but -fill both

    set buttons1 [list \
	    {journal       "Display"    active wokReadStuffJournalOfcurwb} \
	    {upday         "Prev"       active wokUpday} \
	    {downday       "Next"       active wokDownday} \
	    {toEditor      "To Editor"  active wokEditJnl} \
	    {search        "Search"     active wokSearchJnl} \
	    {purge         "Purge"      active wokPurgeJnl} ]

    foreach b $buttons1 {
	$gw.but add [lindex $b 0] -text [lindex $b 1] 
	[$gw.but subwidget [lindex $b 0]] configure -state [lindex $b 2] -command [list [lindex $b 3] $w] 
    }
    
    tixForm $w.file ; tixForm $w.help -right -2
    tixForm $w.reports -top $w.file -left 2 -right  %40
    tixForm $w.journal -top $w.file -left $w.reports -right -2
    tixForm $w.top -top $w.reports  -left 2 -right  %99 -bottom $w.lab 
    tixForm $w.lab -left 2 -right %99  -bottom %99

    
    set IWOK_WINDOWS($w,menu)        $w.file.m
    set IWOK_WINDOWS($w,label)       $w.lab
    set IWOK_WINDOWS($w,hlist)       $hlist
    set IWOK_WINDOWS($w,text)        [$p2.text subwidget text]
    set IWOK_WINDOWS($w,reports)     $fw.but
    set IWOK_WINDOWS($w,journal)     $gw.but
    set IWOK_WINDOWS($w,journal,day) [clock scan yesterday]
    set IWOK_WINDOWS($w,curwb)        $curwb
    set IWOK_WINDOWS($w,frigo)       [wokStore:Report:GetRootName]
    set IWOK_WINDOWS($w,basewrite)   [wokIntegre:BASE:Writable]

    wokUpdateQueue $w
    set jnl [wokIntegre:Journal:GetName]
    if [file exist $jnl] {
	$w.lab configure -text "Last integration: [clock format [file mtime $jnl]]"
    }
    return
}

proc wokSearchJnl { w } {
    global IWOK_WINDOWS
    wokSEA $IWOK_WINDOWS($w,text) 
    return
}

proc wokReadStuffJournalOfcurwb { w } {
    global IWOK_WINDOWS
    tixBusy $w on
    update 
    set jnltmp [wokUtils:FILES:tmpname jnltmp[pid].[wokinfo -n $IWOK_WINDOWS($w,curwb)]]
    if [file exists $jnltmp] {
	wokUtils:FILES:delete $jnltmp
    }
    wokIntegre:Journal:Assemble $jnltmp 
    if [file exists $jnltmp] {
	wokReadFile $IWOK_WINDOWS($w,text) $jnltmp  end
    }
    tixBusy $w off
    $w.lab configure -text "Contents of integration journal"
    return
}
;#
;# Lecture du journal dans un editeur
;#
proc wokEditJnl { w } {
    global IWOK_WINDOWS
    tixBusy $w on
    update 
    set jnltmp [wokUtils:FILES:tmpname jnltmp[pid].[wokinfo -n $IWOK_WINDOWS($w,curwb)]]
    if [file exists $jnltmp] {
	wokUtils:FILES:delete $jnltmp
    }
    wokIntegre:Journal:Assemble $jnltmp 
    if [file exists $jnltmp] {
	wokEDF:EditFile $jnltmp
    }
    tixBusy $w off
    return
}
;#
;#  Aujourd'hui
;#
proc wokToday { w } {
    global IWOK_WINDOWS
    set IWOK_WINDOWS($w,journal,day) [clock scan yesterday]
    wokThisday $w
}
;#
;#  Remonte d'un jour
;#
proc wokUpday { w } {
    global IWOK_WINDOWS
    incr IWOK_WINDOWS($w,journal,day) -[expr 24*3600]
    wokThisday $w
}
;#
;#  Descend d'un jour
;#
proc wokDownday { w } {
    global IWOK_WINDOWS
    incr IWOK_WINDOWS($w,journal,day) [expr 24*3600]
    wokThisday $w
}
;#
;# affiche uniquement les integrations de la journee
;#
proc wokThisday { w } {
    global IWOK_WINDOWS
    tixBusy $w on
    update
    set jnltmp [wokUtils:FILES:tmpname jnltmp[pid].[wokinfo -n $IWOK_WINDOWS($w,curwb)]]
    $IWOK_WINDOWS($w,text) delete 1.0 end
    if ![file exists $jnltmp] {
	wokIntegre:Journal:Assemble $jnltmp 
    }
    set upto [expr $IWOK_WINDOWS($w,journal,day) + 24*3600]
    set str [wokIntegre:Journal:Since $jnltmp $IWOK_WINDOWS($w,journal,day) $upto]
    if { $str != {} } {
	wokReadString $IWOK_WINDOWS($w,text) $str
	$w.lab configure -text "Done that day"
    } else {
	$w.lab configure -text "Nothing done that day"
    }
    tixBusy $w off
    return
}
;#
;# Procs appeles par quand on browse la liste des reports dans la queue
;#
proc wokDisplayReport { w jtem } {
    global IWOK_WINDOWS
    set hli $IWOK_WINDOWS($w,hlist)
    if { $jtem != {} } {
	tixBusy $w on	
	if { [string index $jtem 0] == "^" } {
	    set item [string range $jtem 1 end]
	} else {
	    set item $jtem
	}
	
	set data [$hli info data $jtem]
	switch -- $data {

	    Report {
		catch { unset IWOK_WINDOWS($w,dupl,f1) }
		catch { unset IWOK_WINDOWS($w,dupl,f2) }
		catch { unset IWOK_WINDOWS($w,dupl,m1) }
		catch { unset IWOK_WINDOWS($w,dupl,m2) }
		set dir [wokStore:Report:GetTrueName $item $IWOK_WINDOWS($w,queue)]
		wokReadFile $IWOK_WINDOWS($w,text) $IWOK_WINDOWS($w,frigo)/$dir/report-orig
		$IWOK_WINDOWS($w,label) configure -text "Contents of report  $item" -fg yellow
		[$IWOK_WINDOWS($w,reports) subwidget remove]  configure -state active 
		[$IWOK_WINDOWS($w,reports) subwidget search]  configure -state active 
		if { $IWOK_WINDOWS($w,basewrite) } {
		    [$IWOK_WINDOWS($w,reports) subwidget integrate]  configure -state active 
		} else {
		    [$IWOK_WINDOWS($w,reports) subwidget integrate]  configure -state disabled
		}
	    }
	    
	    Doublon {
		set indx [lindex [split $item ^] 0]
		set path [lindex [split $item ^] 1]
		if ![info exists IWOK_WINDOWS($w,dupl,f1)] {
		    set IWOK_WINDOWS($w,dupl,f1) $path
		    set IWOK_WINDOWS($w,dupl,m1) "Diff Report $indx : [file tail $path] < "
		    $IWOK_WINDOWS($w,label) configure -text $IWOK_WINDOWS($w,dupl,m1) -fg orange
		} else {
		    if ![info exists IWOK_WINDOWS($w,dupl,f2)] {
			set IWOK_WINDOWS($w,dupl,f2) $path
			set IWOK_WINDOWS($w,dupl,m2) " > Report $indx : [file tail $path]"
			wokDiffInText $IWOK_WINDOWS($w,text) \
				$IWOK_WINDOWS($w,dupl,f1) $IWOK_WINDOWS($w,dupl,f2) 
			$IWOK_WINDOWS($w,label) configure -text \
				"$IWOK_WINDOWS($w,dupl,m1) $IWOK_WINDOWS($w,dupl,m2)" -fg orange
			catch { unset IWOK_WINDOWS($w,dupl,f1) }
			catch { unset IWOK_WINDOWS($w,dupl,f2) }
			catch { unset IWOK_WINDOWS($w,dupl,m1) }
			catch { unset IWOK_WINDOWS($w,dupl,m2) }
		    }
		}
		[$IWOK_WINDOWS($w,reports) subwidget remove]  configure -state  disabled
		[$IWOK_WINDOWS($w,reports) subwidget integrate]  configure -state disabled
	    }
	}
	tixBusy $w off
	update
    }
    return
}

proc wokSearchReport { w } {
    global IWOK_WINDOWS
    wokSEA $IWOK_WINDOWS($w,text) 
    return
}


proc wokIntegrateReport { w } {
    global IWOK_WINDOWS
    set hli $IWOK_WINDOWS($w,hlist)
    set anchor [$hli info anchor]
    if { $anchor != {} } {
	if { [string index $anchor 0] == "^" } {
	    set entry [string range $anchor 1 end]
	} else {
	    set entry $anchor
	}
	set type [$hli info data $anchor]
	if { "$type" == "Report" } {
	    $IWOK_WINDOWS($w,text) delete 1.0 end
	    msgsetcmd wokIntegre:Msg $w
	    tixBusy $w on
	    wintegre -wb $IWOK_WINDOWS($w,curwb) $entry
	    msgunsetcmd
	    $IWOK_WINDOWS($w,text) see end
	    wokUpdateQueue $w
	    tixBusy $w off
	}
    }
    return
}

proc wokRemoveReport { w } {
    global IWOK_WINDOWS
    set hli $IWOK_WINDOWS($w,hlist)
    set anchor [$hli info anchor]
    if { $anchor != {} } {
	if { [string index $anchor 0] == "^" } {
	    set entry [string range $anchor 1 end]
	} else {
	    set entry $anchor
	}
	set type [$hli info data $anchor]
	if { "$type" == "Report" } {
	    $IWOK_WINDOWS($w,text) delete 1.0 end
	    msgsetcmd wokIntegre:Msg $w
	    tixBusy $w on
	    update
	    wstore -wb $IWOK_WINDOWS($w,curwb) -rm $entry
	    msgunsetcmd
	    $IWOK_WINDOWS($w,text) see end
	    wokUpdateQueue $w
	    tixBusy $w off
	}
    }
    return
}

proc wokIntegre:Msg   { code msg args} {
    global IWOK_WINDOWS
    set w [lindex $args 0]
    $IWOK_WINDOWS($w,text) insert end $msg\n
    $IWOK_WINDOWS($w,text) see end
    update
    return
}
#
# Met a jour la liste des reports dans la hlist d'adresse w
#
proc wokUpdateQueue { w } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS
    set boldstyle [tixDisplayStyle text -font $IWOK_GLOBALS(boldfont)]
    set dupstyle  [tixDisplayStyle text -fg orange -font $IWOK_GLOBALS(boldfont)]
    set hli $IWOK_WINDOWS($w,hlist)
    set IWOK_WINDOWS($w,queue) [wokStore:Report:GetReportList $IWOK_WINDOWS($w,frigo)]
    catch { unset IWOK_WINDOWS($w,dupl,f1) }
    catch { unset IWOK_WINDOWS($w,dupl,f2) }
    $hli delete all
    $hli add ^
    set i 0
    catch { unset tabdup }
    wokStore:Report:InitState $IWOK_WINDOWS($w,frigo) tabdup
    if { [llength $IWOK_WINDOWS($w,queue)] != 0 } {
	foreach e  $IWOK_WINDOWS($w,queue) { 
	    set user [wokUtils:FILES:Userid $IWOK_WINDOWS($w,frigo)/$e]
	    set str  [wokStore:Report:GetPrettyName $e]
	    if { $str != {} } {
		set rep  [string range [lindex $str 0] 0 19]
		set dte  [lindex $str 1]
		set affrep [format "%3d - %-10s %-20s (stored at %s)" [incr i] $user $rep $dte]
		$hli add ^$i -text $affrep -itemtype text -style $boldstyle -data Report
		if [info exists tabdup($e)] {
		    catch {unset dupfmt }
		    wokStore:Report:Fmtdup $IWOK_WINDOWS($w,frigo)/$e $tabdup($e) dupfmt
		    foreach u [lsort [array names dupfmt]] {
			set udn [lindex [split $u .] 0]
			foreach f $dupfmt($u) {
			    set text "     ${udn}:${f}"
			    $hli add ^${i}^$IWOK_WINDOWS($w,frigo)/$e/${u}/${f} \
				    -text $text -data Doublon -itemtype text -style $dupstyle
			}
		    }
		}
	    }
	}
	if { $IWOK_WINDOWS($w,basewrite) } {
	    [$IWOK_WINDOWS($w,reports) subwidget integrate]  configure -state active
	} else {
	    [$IWOK_WINDOWS($w,reports) subwidget integrate]  configure -state disabled
	}
	$IWOK_WINDOWS($w,reports) subwidget remove configure -state active
	$IWOK_WINDOWS($w,reports) subwidget search configure -state active
    } else {
	if { $IWOK_WINDOWS($w,basewrite) } {
	    [$IWOK_WINDOWS($w,reports) subwidget integrate]  configure -state disabled
	} else {
	    [$IWOK_WINDOWS($w,reports) subwidget integrate]  configure -state disabled
	}
	$IWOK_WINDOWS($w,reports) subwidget remove configure -state disabled
	$IWOK_WINDOWS($w,reports) subwidget search configure -state disabled
    }

    update 
    return
}

proc wokWaffQueueExit { w } {
    global IWOK_WINDOWS
    destroy $w
    wokUtils:FILES:delete [glob -nocomplain /tmp/jnltmp[pid].*]
    if [info exists IWOK_WINDOWS($w,help)] {
	catch {destroy $IWOK_WINDOWS($w,help)}
    }
    wokButton delw [list reports $w]
    return  
}

proc wokPurgeJnl { w } {
    global IWOK_WINDOWS
    msgsetcmd wokIntegre:Msg $w
    tixBusy $w on
    wokIntegre:Journal:Purge 
    tixBusy $w off
    msgunsetcmd
    return
}

proc wokWaffQueueHelp { w } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS
    global env

    set IWOK_WINDOWS($w,help) [set wh .wokWaffQueueHelp]
    if {[info exist IWOK_GLOBALS(windows)]} {
	if {[lsearch $IWOK_GLOBALS(windows) $wh ] == -1} {
	    lappend IWOK_GLOBALS(windows) $wh 
	}
    }

    set whelp [wokHelp $wh "About integration queue"]
    set texte [lindex $whelp 0] ; set label [lindex $whelp 1]
    wokReadFile $texte  $env(WOK_LIBRARY)/wokWaffQueueHelp.hlp
    wokFAM $texte <.*> { $texte tag add big first last }
    $texte tag configure big -background Bisque3 -foreground black -borderwidth 2 \
	    -font -Adobe-Helvetica-Medium-R-Normal--*-120-* -relief raised
    update
    $texte configure -state disabled
    return
}
