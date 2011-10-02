;#
;# Cree une boite de recherche pour le text text. 
;#
proc wokSEA { text } {
    global IWOK_WINDOWS
    set wt $text.wokstringsearch ;# automatikli destroyed with parent !!
    catch { destroy $wt }
    toplevel $wt ; wm title $wt "Search" ; wm iconname $wt "Search"
    
    set wask [frame $wt.ask]
    label $wask.noma ; label $wask.lab -text "Search    :    "
    entry $wask.ent -textvar IWOK_WINDOWS($wt,string) -relief sunken 
    tixForm $wask.noma -top 4 -left 4
    tixForm $wask.lab -top [list $wask.noma 2] -left 4
    tixForm $wask.ent -left $wask.lab -top $wask.noma

    set wsens [frame $wt.sens]
    radiobutton $wsens.fwd -text "Forward" -var IWOK_WINDOWS($wt,sens) \
	    -relief flat -val 1 -command [list wokSEA:InitCur $wt]
    radiobutton $wsens.bwd -text "Backward" -var IWOK_WINDOWS($wt,sens) \
	    -relief flat -val 0 -command [list wokSEA:InitCur $wt]
    checkbutton $wsens.reg -text "Regexp" -var IWOK_WINDOWS($wt,regexp) -relief flat 
    checkbutton $wsens.cas -text "Case    " -var IWOK_WINDOWS($wt,case) -relief flat

    tixForm $wsens.cas -top 0 -left 0
    tixForm $wsens.bwd  -left $wsens.cas

    tixForm $wsens.reg  -top $wsens.cas -left 0
    tixForm $wsens.fwd -left $wsens.reg -top $wsens.bwd

    set wbut [frame $wt.but]
    button $wbut.next -text Next   -width 6 -command [list wokSEA:GO $wt]
    button $wbut.canc -text Cancel -width 6 -command [list wokSEA:Exit $wt $text]
    pack  $wbut.next $wbut.canc -side top -pady 0 -anchor w

    tixForm $wask -top 0 -left 0 -right %80
    tixForm $wsens -top $wask -left 0 -right %80
    tixForm $wbut -left $wask -top 4 -right %99

    set IWOK_WINDOWS($wt,noma) $wask.noma
    set IWOK_WINDOWS($wt,text) $text
    set IWOK_WINDOWS($wt,sens) 1
    wokSEA:InitCur $wt

    bind $wask.ent <Return>      { wokSEA:GO  [winfo toplevel %W] }
    bind $wask.ent <KeyPress>    { wokSEA:CLR [winfo toplevel %W] }

    return 
}
proc wokSEA:CLR  { wt } {
    global IWOK_WINDOWS
    $IWOK_WINDOWS($wt,noma) configure -text ""
}

proc wokSEA:Exit { w text } {
    global IWOK_WINDOWS
    $text tag remove search 0.0 end	
    foreach v [array names IWOK_WINDOWS $w,*] {
	unset IWOK_WINDOWS($v)
    }
    if { [winfo exists $w] } {
	destroy $w
    }
    return
}

proc wokSEA:InitCur { wt } {
    global IWOK_WINDOWS
    if { [ info exists IWOK_WINDOWS($wt,lastmatch)] } {
	set IWOK_WINDOWS($wt,cur) $IWOK_WINDOWS($wt,lastmatch)
    } else {
	if { $IWOK_WINDOWS($wt,sens) == 1 } {
	    set IWOK_WINDOWS($wt,cur) 1.0 
	} else {
	    set IWOK_WINDOWS($wt,cur) end
	}
    }
    return
}

proc wokSEA:GO { wt } {
    global IWOK_WINDOWS
    set string  $IWOK_WINDOWS($wt,string) 
    if { $string == "" } { return }
    set text    $IWOK_WINDOWS($wt,text) 
    set mode -exact
    if { $IWOK_WINDOWS($wt,regexp) == 1 } { set mode -regexp }
    set case -nocase
    if { $IWOK_WINDOWS($wt,case) == 1 } { set case "" }
    set sens -backward
    if { $IWOK_WINDOWS($wt,sens) == 1 } { set sens -forward }
    set ncur [eval $text search $mode $case $sens -count len -- $string $IWOK_WINDOWS($wt,cur)]
    if {$ncur != "" } {
	$IWOK_WINDOWS($wt,noma) configure -text "" 
	$text tag remove search 0.0 end	    	    
	$text tag add search $ncur "$ncur + $len char"
	$text see $ncur
	$text tag configure search -relief raised -background white -foreground black -borderwidth 2 
	if { $IWOK_WINDOWS($wt,sens) == 1 } {
	    set IWOK_WINDOWS($wt,cur) [$text index "$ncur + $len char"]
	} else {
	    set IWOK_WINDOWS($wt,cur) [$text index "$ncur - $len char"]
	}
	set IWOK_WINDOWS($wt,lastmatch) $ncur
    } else {
	$IWOK_WINDOWS($wt,noma) configure -text "nomatch" 
	wokSEA:InitCur $wt
    }
    return
}
;#------------------------------------------
proc wokSEA:testme { w } {
    toplevel $w
    frame $w.buttons
    pack $w.buttons -side bottom -fill x -pady 2m
    button $w.buttons.dismiss -text Dismiss -command "destroy $w"
    button $w.buttons.code -text "Search" 
    pack $w.buttons.dismiss $w.buttons.code -side left -expand 1
    text $w.text -relief sunken -bd 2 -yscrollcommand "$w.scroll set" -setgrid 1 \
	    -height 10
    scrollbar $w.scroll -command "$w.text yview"
    pack $w.scroll -side right -fill y
    pack $w.text -expand yes -fill both
    $w.text insert 0.0 \
	    {This window is a text widget.  It displays one or more lines of text
    and allows you to edit the text.  Here is a summary of the things you
    can do to a text widget:
  
    Resize the window.  This widget has been configured with the "setGrid"
    option on, so that if you resize the window it will always resize to an
    even number of characters high and wide.  Also, if you make the window
    narrow you can see that long lines automatically wrap around onto
    additional lines so that all the information is always visible.}
    $w.text mark set insert 0.0
    $w.buttons.code configure -command [list wokSEA $w.text]
    return 
}
