proc wokDelete { dir location } { 
    global IWOK_GLOBALS
    set w [string tolower .wokDelete:${location}]
    if [winfo exists $w ] {
	wm deiconify $w
	raise $w
	return 
    }
    
    toplevel $w ; wm geometry $w 517x411+453+44
    menubutton $w.file -menu $w.file.m -text File -underline 0 -takefocus 0
    menu $w.file.m 
    $w.file.m add command -label "Close     " -underline 1 -command [list wokDelete:Cancel $w]

    ;#  -- label --
    frame $w.top -relief raised -bd 1
    label $w.top.lab 
    set img [image create compound -window $w.top.lab]
    $img add space -width 10
    $img add image -image [tix getimage danger]
    $img add space -width 10
    $img add text -text "Really do that ??"
    pack $w.top.lab -expand 1 -fill both
    ;# -- end label --

    ;# -- paned listbox et text --
    frame $w.mid -relief raised -bd 1
    tixPanedWindow $w.mid.pane -orient vertical -paneborderwidth 0 -separatorbg gray50
    pack $w.mid.pane  -side top -expand yes -fill both -padx 1 -pady 1
    set p1 [$w.mid.pane add list -min 70 -size 200]
    set p2 [$w.mid.pane add text -min 70]
    tixScrolledHList   $p1.list ; set hlist [$p1.list subwidget hlist]
    tixScrolledText    $p2.text ;
    set text [$p2.text subwidget text] 
    $text config -font $IWOK_GLOBALS(font)
    pack $p1.list -expand yes -fill both -padx 1 -pady 1
    pack $p2.text -expand yes -fill both -padx 1 -pady 1
    ;# -- paned listbox et text --

    ;# -- bouton confirm --
    frame $w.bot -relief raised -bd 1
    tixButtonBox $w.bot.but -orientation horizontal -relief flat -padx 0 -pady 0
    pack $w.bot.but -expand yes -fill both -padx 1 -pady 1 
    $w.bot.but add confirm -text "Confirm" 

    $w.bot.but subwidget confirm config -state active -comm \
	    [list wokDelete:Confirm $hlist $text $w $w.bot.but]
    $w.bot.but add cancel -text "Cancel"   ; 
    $w.bot.but subwidget cancel  config -state active -comm [list wokDelete:Cancel $w]
    ;# -- end bouton confirm --

    tixForm $w.file
    tixForm $w.top -top $w.file -left 2 -right %99
    tixForm $w.mid -top $w.top  -left 2 -right %99 -bottom $w.bot
    tixForm $w.bot              -left 2 -right %99 -bottom %99

    tixBusy $w on
    update
    if [wokinfo -x $location] {
	set lrm {}
	foreach x [wokFind $location] {
	    set type [wokinfo -t $x]
	    if {"$type" == "factory"}   {set cmd "frm $x"}
	    if {"$type" == "workshop"}  {set cmd "srm $x"}
	    if {"$type" == "workbench"} {set cmd "wrm $x"}
	    if {"$type" == "devunit"}   {set cmd "urm $x"}
	    $hlist add $cmd -text $cmd -data [list $cmd $type] -state disabled
	}
    }
    $w.top.lab config -image $img 
    tixBusy $w off
    return
}

proc wokDelete:Confirm { hlist text tpl but } {
    tixBusy $tpl on
    update
    $but subwidget cancel configure -state disabled
    msgsetcmd wokMessageInText $text
    set listdel {}
    foreach itm [$hlist info children] {
	set data [$hlist info data $itm]
	set cmd  [lindex $data 0]
	set typ  [lindex $data 1]
	if ![ catch { eval $cmd } helas ] {
	    $hlist delete entry $cmd
	    lappend listdel $data
	} else {
	    msgprint -e "$helas"
	    break
	}
    }
    set listloc {}
    if { $listdel != {} } {
	foreach itm $listdel {
	    lappend listloc [list [lindex [split [lindex $itm 0]] 1] [lindex $itm 1]]
	}
    }
    wokNAV:Tree:Del $listloc
    msgunsetcmd
    tixBusy $tpl off
    destroy $tpl
    return
}


proc wokDelete:Cancel { w } {
    destroy $w
    return
}
