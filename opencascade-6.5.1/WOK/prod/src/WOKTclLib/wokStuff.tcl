#
# 
#
proc wokHliAdd { item w } {
    global IWOK_WINDOWS
    set hli1 $IWOK_WINDOWS($w,hlist1)
    set hli2 $IWOK_WINDOWS($w,hlist2)
    if {$item != ""} {
	if {[$hli2 info exist $item] == 0} {
	    $hli2 add $item -itemtype imagetext -text [lindex $item 1] \
		    -image [tix getimage [lindex $item 0]]
	    $hli1 entryconfigure $item -image [tix getimage [lindex $item 0]_open]
	}
    }
    return
}

proc wokHliDel { item w } {
    global IWOK_WINDOWS
    $IWOK_WINDOWS($w,hlist2) delete entry $item
    if [$IWOK_WINDOWS($w,hlist1) info exist $item] {
	$IWOK_WINDOWS($w,hlist1) entryconfigure $item -image [tix getimage [lindex $item 0]]
    }
    return
}

proc wokHliAddall { w } {
    global IWOK_WINDOWS
    set hli1 $IWOK_WINDOWS($w,hlist1)
    set hli2 $IWOK_WINDOWS($w,hlist2)
    foreach item [$hli1 info children] {
	if {[$hli2 info exist $item] == 0} {
	    $hli2 add $item -itemtype imagetext -text [lindex $item 1] \
		    -image [tix getimage [lindex $item 0]]
	    $hli1 entryconfigure $item -image [tix getimage [lindex $item 0]_open]
	}
    }
    return
}

proc wokHliDelall { w } {
    global IWOK_WINDOWS
    foreach item [$IWOK_WINDOWS($w,hlist2) info children] {
	$IWOK_WINDOWS($w,hlist2) delete entry $item
	if [$IWOK_WINDOWS($w,hlist1) info exist $item] {
	    $IWOK_WINDOWS($w,hlist1) entryconfigure $item -image [tix getimage [lindex $item 0]]
	}
    }

    if [info exists  $IWOK_WINDOWS($w,text) ] {
	$IWOK_WINDOWS($w,text) delete 0.0 end
    }
    if [info exists $IWOK_WINDOWS($w,label) ] {
	$IWOK_WINDOWS($w,label) configure -text ""
    }
    return
}
;#
;# cree un toplevel de nom w de tit appelle function en lui 
;# passant w le path du toplevel cree.
;# Si il existe deja, le pop
;#
proc wokTL { w t size {func nop} }  {
    global IWOK_GLOBALS
    if [winfo exists $w] {
	wm deiconify $w
	raise $w
    } else {
	if {[info exist IWOK_GLOBALS(windows)]} {
	    if {[lsearch $IWOK_GLOBALS(windows) $w] == -1} {
		lappend IWOK_GLOBALS(windows) $w
	    }
	}
	toplevel $w 
	wm title $w $t
	if { $size != {} } {
	    wm geometry $w $size
	}
	if {[string compare $func nop] != 0} {
	    $func $w
	}
    }
    return
}
;#
;# retourne la taille du toplevel en fonction de type
;#
proc wokTLAdjust { stuff } {
    global IWOK_GLOBALS
    if { [string compare $stuff source] == 0 } {
	return $IWOK_GLOBALS(windows,barr)
    } else {
	return $IWOK_GLOBALS(windows,rect)
    }
}
#
# Retourne la liste contenant chaque ligne (\) du texte comme element
#
proc wokTextToList {text} {
    return [split [$text get 1.0 end] \n]
}
#
# Ecrit le texte dans path
#
proc wokTextToFile {text file} {
    wokUtils:FILES:ListToFile [wokTextToList $text] $file
    return
}
#
# Retourne la string contenant le texte
#
proc wokTextToString {text} {
    return [$text get 1.0 end]
}
#
# Met le fichier dans un texte
#
proc wokReadFile {text filename {ext 1.0} } {
    $text delete 0.0 end
    catch {
	set fd [open $filename {RDONLY}]
	$text delete 1.0 end

	while {![eof $fd]} {
	    $text insert end [gets $fd]\n
	}
	close $fd
	
    }
    $text see $ext
    update idletasks
    return
}
#
# Met la liste dans un texte
#
proc wokReadList {text liste} {
    $text delete 0.0 end
    foreach string $liste {
	$text insert end $string\n
    }
    $text see 1.0
    update idletasks
    return
}
#
# Met la string dans un texte
#
proc wokReadString {text string} {
    $text delete 0.0 end
    $text insert end $string\n
    $text see 1.0
    update idletasks
    return
}
#
# met ar dans texte
#
proc wokArInText {text file} {
    global tcl_platform
    if { "$tcl_platform(platform)" == "unix" } {
	wokReadString $text [exec ar tv $file]
    } elseif { "$tcl_platform(platform)" == "windows" } {
    }
    return 
}
;#
;# insere le contenu d'un fichier dans texte. Si il y un label, le configure
;#
proc wokDisplayFileinText { item w } {
    global IWOK_WINDOWS
    wokReadFile $IWOK_WINDOWS($w,text) $item
    if [info exists $IWOK_WINDOWS($w,label)] {
	$IWOK_WINDOWS($w,label) configure -text "File $item"
    }
    return
}
;#
;# Utilisee par wokCreations et wokDeletion et wokStore
;#
proc wokMessageInText { code msg text} {
    $text insert end $msg\n
    $text see end
    update
    return
}
;#
;# Met un diff dans un text
;#
proc wokDiffInText { text f1 f2 } {
    global tcl_platform
    set wtmp [wokUtils:FILES:tmpname wokdiff[pid]]
    if { "$tcl_platform(platform)" == "unix" } {
	catch {exec diff $f1 $f2 > $wtmp} 
	wokReadFile $text $wtmp
	wokUtils:FILES:delete $wtmp
    } elseif { "$tcl_platform(platform)" == "windows" } {
	$text delete 0.0 end
	$text insert end {Click on button "More diff" instead.}
	update 
    }
    return
}
;#
;#
;#
proc wokDangerDialBox { w title conftext items bitmap default args } {
    global button
    toplevel $w -class Dialog
    wm title $w $title
    wm iconname $w Dialog
    wm geometry $w 453x314
    frame $w.top -relief raised -bd 1
    pack $w.top -side top -fill both
    frame $w.bot -relief raised -bd 1
    pack $w.bot -side bottom -fill both
    label $w.top.lab 
    set img [image create compound -window $w.top.lab]
    $img add space -width 10
    $img add image -image [tix getimage $bitmap]
    $img add space -width 10
    $img add text -text $conftext
    $w.top.lab config -image $img 
    pack $w.top.lab -expand 1 -fill both
    tixScrolledListBox $w.top.msg  
    foreach e $items {
	[$w.top.msg subwidget listbox] insert end $e 
    }
    $w.top.msg configure -state disabled
    pack $w.top.msg -side right -expand 1 -fill both -padx 3m -pady 3m
    set i 0
    foreach but $args {
	button $w.bot.button$i -text $but -command\
		"set button $i"
	if {$i == $default } {
	    frame $w.bot.default -relief sunken -bd 1
	    raise $w.bot.button$i
	    pack $w.bot.default -side left -expand 1\
		    -padx 3m -pady 2m
	    pack $w.bot.button$i -in $w.bot.default\
		    -side left -padx 2m -pady 2m\
		    -ipadx 2m -ipady 1m
	} else {
	    pack $w.bot.button$i -side left  -expand 1\
		     -padx 3m -pady 3m -ipadx 2m -ipady 1m
	}
	incr i
    }
    if {$default >= 0 } {
	bind $w <Return> "$w.bot.button$default flash;set button $default"
    }
    set oldFocus [focus]
    grab set $w
    focus $w
    tkwait variable button
    destroy $w
    focus $oldFocus
    return $button
}


proc wokDialBox { w title text bitmap default args } {
    global button
    toplevel $w -class Dialog
    wm title $w $title
    wm iconname $w Dialog
    frame $w.top -relief raised -bd 1
    pack $w.top -side top -fill both
    frame $w.bot -relief raised -bd 1
    pack $w.bot -side bottom -fill both
    message $w.top.msg -width 4i -text $text 
    pack $w.top.msg -side right -expand 1 -fill both -padx 3m -pady 3m
    if {$bitmap != "" } {
	label $w.top.bitmap -bitmap $bitmap
	pack $w.top.bitmap -side left -padx 3m -pady 3m
    }
    set i 0
    foreach but $args {
	button $w.bot.button$i -text $but -command\
		"set button $i"
	if {$i == $default } {
	    frame $w.bot.default -relief sunken -bd 1
	    raise $w.bot.button$i
	    pack $w.bot.default -side left -expand 1\
		    -padx 3m -pady 2m
	    pack $w.bot.button$i -in $w.bot.default\
		    -side left -padx 2m -pady 2m\
		    -ipadx 2m -ipady 1m
	} else {
	    pack $w.bot.button$i -side left  -expand 1\
		     -padx 3m -pady 3m -ipadx 2m -ipady 1m
	}
	incr i
    }
    if {$default >= 0 } {
	bind $w <Return> "$w.bot.button$default flash;set button $default"
    }
    set oldFocus [focus]
    grab set $w
    focus $w
    tkwait variable button
    destroy $w
    focus $oldFocus
    return $button
}

;#
;# applique script sur tous les <patterns> du text w
;#
proc wokFAM { w pattern script } {
    scan [$w index end] %d numlines
    for {set i 1} {$i < $numlines} {incr i} {
	$w mark set last $i.0
	while { [regexp -indices $pattern \
		[$w get last "last lineend"] indices]} {
	    $w mark set first \
		    "last + [lindex $indices 0] chars"
	    $w mark set last "last + 1 chars\
		    + [lindex $indices 1] chars"
	    uplevel $script
	}
    }
}

proc wokWait {command w args} {
    tixBusy $w on
    set id [after 10000 tixBusy $w off]
    eval $command $args
    after cancel $id
    after 0 tixBusy $w off
    return
}

;#
;# Toplevel d'un help (Simple texte + label)
;# retourne une liste de 2 elements le texte et le label
;#
proc wokHelp { w title {geometry 950x450} } {

    catch { destroy $w } ; toplevel $w ; wm title $w $title ; wm geometry $w $geometry

    set fnt [tix option get fixed_font]

    menubutton $w.file -menu $w.file.m -text File -underline 0 -takefocus 0
    menu $w.file.m  ; $w.file.m add command -label "Close     " -underline 1 -command "destroy $w"
    
    frame $w.top -relief sunken -bd 1 
    label $w.lab -relief raised 
    
    tixScrolledText    $w.text ; set texte [$w.text subwidget text]   ; $texte config -font $fnt 
    
    tixForm $w.file
    tixForm $w.top   -top $w.file -left 2 -right %99 
    tixForm $w.text  -left 2 -top $w.top -bottom $w.lab -right %99
    tixForm $w.lab   -left 2 -right %99 -bottom %99

    return [list $texte $w.lab]
    
}
