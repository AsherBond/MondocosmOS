;#
;# charge file dans widget texte.
;#
proc wokEDF:iwok_editor { file } {
    set w [wokTPL [id user][id host]$file]
    if [winfo exists $w] {
	wm deiconify $w
	raise $w
	return
    }
    set fnt [tix option get fixed_font]
    toplevel $w 
    wm title $w "$file"

    tixScrolledText    $w.text ; set texte [$w.text subwidget text] 
    $texte config -font $fnt 

    menubutton $w.file -menu $w.file.m -text File -underline 0 -takefocus 0
    menu $w.file.m  
    $w.file.m add command -label "Close     " -underline 1 -command "destroy $w"

    menubutton $w.hlp -menu $w.hlp.menu -text "Help"
    menu $w.hlp.menu
    $w.hlp.menu add command -label "Help" -command [list wokEDF:Help $w]

    menubutton $w.edit -menu $w.edit.m -text Edit -underline 0 -takefocus 0
    menu $w.edit.m
    $w.edit.m add command -label "Search" -command [list wokSEA $texte] 

    frame $w.top -relief sunken -bd 1 
    label $w.lab -relief raised 

    if [ file writable $file] {
	$w.file.m add command -label "Save"  -underline 1 -command [list wokEDF:Save $texte $file]
    }
    

    tixForm $w.file  -top 0
    tixForm $w.edit -left $w.file   -top 0
    tixForm $w.hlp -right -0 -top 0
    tixForm $w.top   -top $w.file -left 2 -right %99 
    tixForm $w.text  -left 2 -top $w.top -bottom $w.lab -right %99
    tixForm $w.lab   -left 2 -right %99 -bottom %99

    wokReadFile $texte $file 1.0
    update
    return
}

proc wokEDF:Save { text file } {
    if [file writable $file] {
	wokTextToFile $text $file 
    } 
    return
}
;#
;# envoie l'editeur charge par wokEDF:EDITOR 
;#
proc wokEDF:EditFile { file } {
    global IWOK_GLOBALS

    if ![ info exists IWOK_GLOBALS(EDF,EDITOR)] {
	set IWOK_GLOBALS(EDF,EDITOR) [wokEDF:EDITOR]
    }

    switch -- $IWOK_GLOBALS(EDF,EDITOR) {
	
	connected_emacs {
	}
	
	iwok_editor {
	    wokEDF:iwok_editor $file

	}

	default {
	    if { "file tail $IWOK_GLOBALS(EDF,EDITOR)" == "vi" } {
		catch { exec xterm -T $file -e $IWOK_GLOBALS(EDF,EDITOR) $file & }
	    } else {
		catch { eval  exec $IWOK_GLOBALS(EDF,EDITOR) $file & }
	    }
	}
	
    }
    return
}
;#
;# Retourne de quoi editer un fichier
;# 1. connected_emacs si la session courante provient d'un emacs connecte dans ce cas 
;#    la variable IWOK_GLOBALS(EDF,clients) contient le numero de la connexion
;# 2. la valeur de la V.E. EDITOR si elle existe
;# 3. iwok_editor dans tous les autres cas (avec iwok_editor = widget text !!!)
;#
proc wokEDF:EDITOR { } {
    global env
    global IWOK_GLOBALS
    global tcl_platform
    if { "$tcl_platform(platform)" == "unix" } {
	if {[info exists env(EDITOR)]} {
	    return $env(EDITOR)
	} else {
	    return iwok_editor
	}
    } else {
	return iwok_editor
    }
}
;#
;# Help
;#
proc wokEDF:Help { w } {
    global IWOK_GLOBALS
    global env
    set whelp [wokHelp [set IWOK_GLOBALS(EDF,help) .wokEDFHelp] "About iwok editor"]
    set texte [lindex $whelp 0] ; set label [lindex $whelp 1]
    if {[info exist IWOK_GLOBALS(windows)]} {
	if {[lsearch $IWOK_GLOBALS(windows) .wokEDFHelp ] == -1} {
	    lappend IWOK_GLOBALS(windows) .wokEDFHelp 
	}
    }
    wokReadFile $texte  $env(WOK_LIBRARY)/wokEDF.hlp
    wokFAM $texte <.*> { $texte tag add big first last }

    $texte tag configure big -background Bisque3 -foreground black -borderwidth 2 \
	    -font -Adobe-Helvetica-Medium-R-Normal--*-120-* -relief raised

    $texte configure -state disabled
    update
    return   
}

;#
;# 
;#
proc wokEDF:Archive { file args } {
    set w [wokTPL [id user][id host]$file]
    if [winfo exists $w] {
	wm deiconify $w
	raise $w
	return
    }
    set fnt [tix option get fixed_font]
    toplevel $w 
    wm title $w "Contents of archive $file"

    wokButton setw [list archive $w]

    menubutton $w.file -menu $w.file.m -text File -underline 0 -takefocus 0
    menu $w.file.m  
    $w.file.m add command -label "Close     " -underline 1 -command "destroy $w"

    frame $w.top -relief sunken -bd 1 
    label $w.lab -relief raised 
    
    tixScrolledText $w.text ; set texte [$w.text subwidget text] 
    $texte config -font $fnt  -cursor {hand2 red white}

    tixForm $w.file 
    tixForm $w.top  -top $w.file -left 2 -right %99 
    tixForm $w.text -left 2 -top $w.top -bottom $w.lab -right %99
    tixForm $w.lab  -left 2 -right %99 -bottom %99

    wokReadString $texte [exec ar tv $file]

    update
    return
}
proc wokEDF:Archive:Exit { w } {
    wokButton delw [list archive $w]
    destroy $w
    return
}
;#
;# donne la liste des .o d'une shareable et proposera nm et Cie args Adr File
;#
proc wokEDF:Shareable { file args} {
    global IWOK_WINDOWS
    set location $args  ;# loc WOK:k3dev:ref:TKWOK:stadmfile
    set ll [llength [set lc [split $location :]]]
    set actloc   [join [lrange $lc 0 [expr $ll - 2]] :]
    set typloc   [lindex $lc end]
    set ud       [wokinfo -n $actloc]
    set  objlist [wokinfo -p stadmfile:${ud}.ObjList $actloc]
    if [file exists $objlist] {
	set tfi [split [file tail $file] .]
	set w [string tolower .[id user]:[id host]:[join $tfi :]]
	if [winfo exists $w] {
	    wm deiconify $w
	    raise $w
	    return
	}
	set fnt [tix option get fixed_font]
	toplevel $w 
	wm title $w "Objects in file $file"
	
	wokButton setw [list shareable $w]

	tixScrolledText $w.text ; set IWOK_WINDOWS($w,text) [$w.text subwidget text] 
	$IWOK_WINDOWS($w,text) config -font $fnt  -cursor {hand2 red white}
	
	menubutton $w.file -menu $w.file.m -text File -underline 0 -takefocus 0
	menu $w.file.m  
	$w.file.m add command -label "Close     " -underline 1 -command [list wokEDF:Shareable:Exit $w]
	
	menubutton $w.disp -menu $w.disp.m -text Arrange -underline 0 -takefocus 0
	menu $w.disp.m
	$w.disp.m add radio -label "By name"   -under 1 -var IWOK_WINDOWS($w,bywhat) \
		-value byname -comm [list wokEDF:Shareable:Disp $w]
	$w.disp.m add radio -label "By date"   -under 1 -var IWOK_WINDOWS($w,bywhat) \
		-value bydate -comm [list wokEDF:Shareable:Disp $w]
	set IWOK_WINDOWS($w,bywhat) bydate
	$w.disp.m add separator
	$w.disp.m add radio -label "Full path" -under 1 -var IWOK_WINDOWS($w,bywhat) \
		-value fullpa -comm [list wokEDF:Shareable:Disp $w]
	set IWOK_WINDOWS($w,bywhat) fullpa
	
	frame $w.top -relief sunken -bd 1 
	label $w.lab -relief raised 
	
	menubutton $w.edit -menu $w.edit.m -text Edit -underline 0 -takefocus 0
	menu $w.edit.m
	$w.edit.m add command -label "Search" -command [list wokSEA $IWOK_WINDOWS($w,text)]
	
	tixForm $w.file ; tixForm $w.disp -left $w.file ; tixForm $w.edit -left $w.disp 
	tixForm $w.top  -top $w.file -left 2 -right %99
	tixForm $w.text -left 2 -top $w.top -bottom $w.lab -right %99
	tixForm $w.lab  -left 2 -right %99 -bottom %99
	
	set IWOK_WINDOWS($w,listo) [wokUtils:FILES:FileToList $objlist]
	wokEDF:Shareable:Disp $w
	set solen [llength $IWOK_WINDOWS($w,listo)]
	set sodat [string range [clock format [file mtime $file]] 4 18]
	$w.lab configure -text "$solen objects."
	update
    }
    return
}

proc wokEDF:Shareable:Exit { w } {
    wokButton delw [list shareable $w]
    destroy $w
    return
}

proc wokEDF:Shareable:Disp { w } {
    global IWOK_WINDOWS
    set ll $IWOK_WINDOWS($w,listo)
    set l1 {}
    if { "$IWOK_WINDOWS($w,bywhat)" == "fullpa" } {
	wokReadList $IWOK_WINDOWS($w,text) $IWOK_WINDOWS($w,listo)
	return
    }
    tixBusy $w on
    update
    foreach e $ll {
	catch {unset m}
	file lstat  $e m
	lappend l1 [list [file tail $e] $m(mtime) $m(size)]
    }
    
    switch -- $IWOK_WINDOWS($w,bywhat) {
	byname {
	    set l2 [lsort -command wokEDF:Shareable:byname $l1]
	}
	
	bydate {
	    set l2 [lsort -decreasing -command wokEDF:Shareable:bydate $l1]
	}
    }
    set l3 {}
    foreach e $l2 {
	set str [lindex $e 0]
	set dat [string range [clock format [lindex $e 1]] 4 18]
	set siz [lindex $e 2]
	lappend l3 [format "%15s %-10s %s" $dat $siz $str]
    }
    wokReadList $IWOK_WINDOWS($w,text) $l3
    tixBusy $w off
    return
}
;#
;# Ordre alphab sur le path ou le full path
;#
proc wokEDF:Shareable:byname { a b } {
    return [string compare [lindex $a 0] [lindex $b 0] ]
}

;#
;# dernier modifie d'abord
;#
proc wokEDF:Shareable:bydate { a b } {
    return [expr [lindex $a 1] - [lindex $b 1] ]
}
;#
;#
;#
proc wokEDF:Zfile { file args } {
    return
}

proc wokEDF:ofile { file args } {
    return
}

;#
;# comment recuperer les extensions de shareable ??
;#
proc wokEDF:InitAdequateCommand { } {
    global IWOK_GLOBALS
    set IWOK_GLOBALS(EDF,EDITOR) [wokEDF:EDITOR]
    set IWOK_GLOBALS(EDF,.a)      wokEDF:Archive
    set IWOK_GLOBALS(EDF,.so)     wokEDF:Shareable
    set IWOK_GLOBALS(EDF,.sl)     wokEDF:Shareable
    set IWOK_GLOBALS(EDF,.Z)      wokEDF:Zfile
    set IWOK_GLOBALS(EDF,.o)      wokEDF:ofile
    return
}
;#
;# 
;#
proc wokEDF:AdequateCommand {  path args } {
    global IWOK_GLOBALS
    set ext [file extension $path]
    if [info exists IWOK_GLOBALS(EDF,$ext)] {
	$IWOK_GLOBALS(EDF,$ext) $path $args
    } else {
	wokEDF:EditFile $path
    }
    return
}
;#
;#
;#
proc wokEDF:InitExtension { } {
    global IWOK_GLOBALS
    set IWOK_GLOBALS(EXT,package,ext)       {.cdl .cxx .hxx .c .h}
    set IWOK_GLOBALS(EXT,nocdlpack,ext)     {.cxx .hxx .c .h}
    set IWOK_GLOBALS(EXT,interface,ext)     {.cdl .cxx .hxx}
    set IWOK_GLOBALS(EXT,client,ext)        {.cdl .cxx .hxx}
    set IWOK_GLOBALS(EXT,toolkit,ext)       {}
    set IWOK_GLOBALS(EXT,engine,ext)        {.cdl .cxx .hxx}
    set IWOK_GLOBALS(EXT,executable,ext)    {.cdl .cxx .hxx}
    set IWOK_GLOBALS(EXT,schema,ext)        {.cdl .cxx .hxx}
    set IWOK_GLOBALS(EXT,ccl,ext)           {.ccl .us  .fr}
    set IWOK_GLOBALS(EXT,frontal,ext)       {}
    set IWOK_GLOBALS(EXT,documentation,ext) {}
    set IWOK_GLOBALS(EXT,resource,ext)      {.o .xwd .dat .tcl .el .csh}
    set IWOK_GLOBALS(EXT,delivery,ext)      {}
    set IWOK_GLOBALS(EXT,all)               {.cdl .cxx .hxx .gxx .lxx .pxx .c .h .edl .tcl}
    return
}
