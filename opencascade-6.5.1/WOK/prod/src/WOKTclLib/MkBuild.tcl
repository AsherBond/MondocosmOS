proc wokBuild { {fast 0} } {

    global IWOK_WINDOWS
    global IWOK_GLOBALS
    global env
    set w $IWOK_GLOBALS(toplevel)
    set top [frame $w.thu -bd 1 -relief raised]
    
    # Paned Window 
    #
    set p [tixPanedWindow $top.p -orient horizontal]
    pack $p -expand yes -fill both -padx 4 -pady 4
    
    set p1 [$p add pane1 -expand 1] ; $p1 config -relief flat ; set IWOK_GLOBALS(tree,name)   $p1
    set p2 [$p add pane2 -expand 4] ; $p2 config -relief flat ; set IWOK_GLOBALS(canvas,name) $p2
    
    # Tree
    #
    set tree  [tixTree $p1.tree -options {hlist.separator "^" hlist.selectMode single }]
    
    $tree config -opencmd  [list wokNAV:Tree:Open $w] -browsecmd [list wokNAV:Tree:Browse $w]
    
    # ScrolledWindow
    #
    set scr  [tixScrolledWindow $p2.st]
    
    pack $p1.tree -expand yes -fill both -padx 4 -pady 4
    pack $p2.st   -expand yes -fill both -padx 4 -pady 4	

    set IWOK_WINDOWS($w,NAV,tree)     $tree
    set IWOK_WINDOWS($w,NAV,hlist)    [$tree subwidget hlist]
    set IWOK_WINDOWS($w,NAV,scrolled) $scr                       
    set IWOK_WINDOWS($w,NAV,window)   [$p2.st subwidget window]

    set IWOK_GLOBALS(canvas)  [canvas $IWOK_WINDOWS($w,NAV,window).c]
    $IWOK_GLOBALS(canvas) configure -width $IWOK_GLOBALS(canvas,width) -height $IWOK_GLOBALS(canvas,height)

    wokButton initialize
    
    button $w.mnu -state disabled -relief raised
    menubutton $w.mnu.fil -menu $w.mnu.fil.menu0 -text "File"
    menu $w.mnu.fil.menu0
    $w.mnu.fil.menu0 add command -label "Exit" -command wokKillAll

    menubutton $w.mnu.but -menu $w.mnu.but.menu1 -text "Windows"
    $w.mnu.but configure -state disabled
    menu $w.mnu.but.menu1
    $w.mnu.but.menu1 add command -label "Hide all" -command wokHideAll
    $w.mnu.but.menu1 add command -label "Show all" -command wokShowAll
    $w.mnu.but.menu1 add separator

    menubutton $w.mnu.hlp -menu $w.mnu.hlp.menu -text "Help"
    menu $w.mnu.hlp.menu
    $w.mnu.hlp.menu add command -label "Help" -command [list wokMainHelp $w]
    
    tixForm $w.mnu -left 0 -right -0 -top 0
    tixForm $w.mnu.fil -left 0 -top 0 
    tixForm $w.mnu.but -left $w.mnu.fil
    tixForm $w.mnu.hlp -right -0 -top 0

    set lastbut [wokButton create $w]   
    wokButton balloon
    
    set dis [wokDSP:Init $w]
    set mov [wokMOV:Init $w]   

    tixComboBox $w.l \
	    -variable IWOK_GLOBALS(CWD) \
	    -command wokSetLoc -label "Contents of:" \
	    -editable true -labelside left \
	    -history 1 -prunehistory 1 -histlimit 20
    set IWOK_GLOBALS(label) $w.l
    [set IWOK_GLOBALS(label,entry) [$IWOK_GLOBALS(label) subwidget entry]] configure -relief sunken

    set arr [$w.l subwidget arrow] ; tixBalloon $arr.bal ; $arr.bal bind $arr -msg "Last spots"

    if [file exists $env(WOK_LIBRARY)/images/opencascade.gif] {
	set ogif $env(WOK_LIBRARY)/images/opencascade.gif
    } else {
	set ogif $env(WOK_LIBRARY)/opencascade.gif
    }
    button $w.mdtv -image [image create photo -file $ogif] 


    tixForm $dis -left $lastbut  -bottom $top -top $w.mnu

    tixForm $mov -left $dis  -bottom $top -top $w.mnu

    tixForm $IWOK_GLOBALS(label) -left $mov -bottom $top -top  $w.mnu -right $w.mdtv 
    tixForm $w.mdtv -right -0 -bottom $top -top  $w.mnu
    tixForm $top -top  $lastbut -left 0 -right -0 -bottom -0
    
    set poph [wokPOP:hlist create  $w] ; wokPOP:hlist  initialize ; $poph bind $IWOK_WINDOWS($w,NAV,hlist)
    set popc [wokPOP:canvas create $w] ; wokPOP:canvas initialize ; $popc bind $IWOK_GLOBALS(canvas)
    ;#
    ;# Go from current location. 
    
    wokNAV:Tree:UpdateSession $IWOK_GLOBALS(toplevel) you
    if { $fast == 0 } {
	tixBusy $IWOK_GLOBALS(toplevel) on
	update
	wokMOV:Alonzi $IWOK_GLOBALS(toplevel) [wokcd]
	wokMOV:wokcd
	tixBusy $IWOK_GLOBALS(toplevel) off
    } else {
	wokButton session
    }
   
    wokCWD disable
    wokSeeLayout

    $IWOK_GLOBALS(canvas) bind current <Button-1> {
	wokNAV:Tree:Focus [winfo toplevel %W] [lindex [%W gettags current] 0]
    }

    $IWOK_GLOBALS(canvas) bind  current <Button-3> {
	eval "proc wokPOP:canvas:GetInfo { } { return \"[%W gettags current]\" }"
    }

}


proc wokSetTypeDisplayed { str } {
    global IWOK_GLOBALS
    set IWOK_GLOBALS(canvas,TypeDisplayed) $str
    return
}

proc wokGetTypeDisplayed { } {
    global IWOK_GLOBALS
    if [info exists IWOK_GLOBALS(canvas,TypeDisplayed)] {
	return $IWOK_GLOBALS(canvas,TypeDisplayed)
    } else {
	return {}
    }
}

proc wokSeeLayout { } {
    global IWOK_GLOBALS
    if { $IWOK_GLOBALS(layout) == 0 } {
	set IWOK_GLOBALS(layout) 1
	if { $IWOK_GLOBALS(layout,update) == 1 } {
	    wokUpdateLayout $IWOK_GLOBALS(CWD)
	    set IWOK_GLOBALS(layout,update) 0 
	}
	wm geometry $IWOK_GLOBALS(toplevel) $IWOK_GLOBALS(toplevel,opened)
	raise $IWOK_GLOBALS(toplevel)
    } else {
	set IWOK_GLOBALS(layout) 0
	wm geometry $IWOK_GLOBALS(toplevel) $IWOK_GLOBALS(toplevel,closed)
    }
    return
}

proc wokSetLoc { loc } {
    global IWOK_GLOBALS
    tixBusy $IWOK_GLOBALS(toplevel) on
    wokCWD updatehistory $loc
    if { $IWOK_GLOBALS(layout) == 1 } {
	set IWOK_GLOBALS(layout,update) 0
	wokUpdateLayout $loc
    } else {
	set IWOK_GLOBALS(layout,update) 1
	wokButton [wokNAV:tlist:Type $IWOK_GLOBALS(toplevel) $loc]
    }
    tixBusy $IWOK_GLOBALS(toplevel) off
    return
}

proc wokUpdateLayout { loc } {
    global IWOK_GLOBALS
    set w $IWOK_GLOBALS(toplevel)
    wokNAV:Tree:Show $w [wokNAV:tlist:Get $IWOK_GLOBALS(toplevel) $loc] 
    set type  [wokNAV:tlist:Type $w $loc]
    wokUpdateCanvas $w $loc
    wokButton $type
    raise $IWOK_GLOBALS(toplevel)
    return
}
#
# Retourne la liste des elements affiches dans le canvas.
#
proc wokListLayout { {option location} } {
    global IWOK_GLOBALS
    set canv $IWOK_GLOBALS(canvas)
    set ll {}
    foreach i [$canv find all] {
	set x [$canv gettags $i]
	if { "$option" == "location" } {
	    if { [lsearch $ll [lindex $x 1]] == -1 } {
		lappend ll [lindex $x 1]
	    }
	} elseif { "$option" == "anchor" } {
	    if { [lsearch $ll [lindex $x 0]] == -1 } {
		lappend ll [lindex $x 0]
	    }
	} elseif { "$option" == "type" } {
	    if { [lsearch $ll [lindex $x 2]] == -1 } {
		lappend ll [lindex $x 2]
	    }
	} elseif { "$option" == "names" } {
	    if { [lsearch $ll [lindex $x 3]] == -1 } {
		lappend ll [lindex $x 3]
	    }
	}
    }
    return $ll
}
;#
;# Configure/cree les boutons 
;#
;# Pour savoir si la fenetre browser est la:
;# set val [wokButton getw browser] 
;# if $val != {} { val = list des toplevels allumes par le bouton }
;# Pour avoir la liste des toplevels allumes par les boutons:
;# set lst [wokButton listw]
;# Pour remettre a zero l'etat du bouton browser:
;# wokButton resetw browser
;#
proc wokButton { option {w nil} } {
    global IWOK_GLOBALS

    switch -glob -- $option  {

	initialize {
	    wokUtils:key:lset IWOK_GLOBALS(blist) prepare   [list z wokPrepare {wprepare}]
	    wokUtils:key:lset IWOK_GLOBALS(blist) wbuild    [list w winbuild {umake}]
	    wokUtils:key:lset IWOK_GLOBALS(blist) browser   [list b wokbrowser {CDL Browser}]
	    wokUtils:key:lset IWOK_GLOBALS(blist) params    [list p wokPRMAff  {Parameters}]
	}

	create { 
	    set blist $IWOK_GLOBALS(blist)

	    foreach i [wokUtils:key:lkeys blist] {
		set v [wokUtils:key:lget blist $i]
		set m [lindex $v 0]
		set f [lindex $v 1]
		lappend v [button $w.$m -height 32 -width 32 -image [tix getimage $i] -command $f]
		wokUtils:key:lset blist $i $v
		set IWOK_GLOBALS(buttons,state,$i) {}
	    }

	    set prev {}
	    set curr {}
	    foreach i [wokUtils:key:lkeys blist] {
		set v [wokUtils:key:lget blist $i]
		set curr [lindex $v 0]
		if { $prev == {} } {
		    tixForm $w.$curr -top $w.mnu
		} else {
		    tixForm $w.$curr -left $w.$prev -top $w.mnu
		}
		set prev $curr
	    }

	    set IWOK_GLOBALS(buttons) $blist
	    return $w.$curr
	}

	balloon {
	    foreach b $IWOK_GLOBALS(buttons) {
		set x [lindex $b 1] 
		tixBalloon [lindex $x end].bal 
		[lindex $x end].bal bind [lindex $x end] -msg "[lindex $x 2]"
	    }
	}

	disable {
	    foreach bt $w {
		[lindex [wokUtils:key:lget IWOK_GLOBALS(buttons) $bt] end] configure -state disabled 
	    }
	    
	}

	activate {
	    foreach bt $w {
		[lindex [wokUtils:key:lget IWOK_GLOBALS(buttons) $bt] end] configure -state normal
	    }
	 }


	setw {
	    lappend IWOK_GLOBALS(buttons,state,[lindex $w 0]) [lindex $w 1]
	    wokUpdateWindowMenu [lindex $w 1]
	}

	delw {
	    set ltpl $IWOK_GLOBALS(buttons,state,[lindex $w 0])
	    set tpl [lindex $w 1] 
	    set i [lsearch $ltpl $tpl]
	    if { $i != -1 } {
		set IWOK_GLOBALS(buttons,state,[lindex $w 0]) [lreplace $ltpl $i $i]
		wokRemoveWindowMenu $tpl
	    }
	}

	resetw {
	    set IWOK_GLOBALS(buttons,state,[lindex $w 0]) {}
	}


	getw {
	    return $IWOK_GLOBALS(buttons,state,[lindex $w 0])
	}

	listw {
	    set ll {}
	    foreach bb [array names IWOK_GLOBALS buttons,state,*] {
		lappend ll [list [lindex [split $bb ,] 2] $IWOK_GLOBALS($bb)]
	    }
	    return $ll
	}

	session {
	    wokButton disable  {params prepare wbuild browser}
	}

	factory {
	    wokButton disable  {prepare wbuild browser}
	    wokButton activate params
	}

	workshop {
	    wokButton disable {prepare wbuild browser}
	    wokButton activate params
	}

	workbench {
	    wokButton activate {prepare wbuild browser params}
	}

	devunit_* {
	    wokButton activate {prepare wbuild browser params}
	}

	devunitstuff {
	    wokButton activate {prepare browser wbuild params}
	}

    }
    return
}

proc wokReaff { } {
    global IWOK_GLOBALS
    if { "[set cwd [wokCWD read]]" != ":" } {
	if { [set dad [wokNAV:Tlist:Dad $IWOK_GLOBALS(toplevel) $cwd]] != {} } {
	    wokCWD write $dad
	}
    } 
    return
}

proc wokReaffCanvas { } {
    global IWOK_GLOBALS
    wokUpdateCanvas $IWOK_GLOBALS(toplevel) [wokCWD read]
    return
}


proc wokUpdateWindowMenu {wl}  {
    global IWOK_GLOBALS
    if {![info exist IWOK_GLOBALS(menuwin,$wl)]} {
	set wroot $IWOK_GLOBALS(toplevel)
	set t "[wm title $wl]"
	$wroot.mnu.but.menu1 add command -label $t -command "wokRaise $wl"
	set ind [$wroot.mnu.but.menu1 index last]
	set IWOK_GLOBALS(menuwin,$wl) $ind
	$wroot.mnu.but configure -state active
    }
    return
}

proc wokRemoveWindowMenu {wl}  {
    global IWOK_GLOBALS
    set wroot $IWOK_GLOBALS(toplevel)
    if {[info exist IWOK_GLOBALS(menuwin,$wl)]} {
	$wroot.mnu.but.menu1 delete $IWOK_GLOBALS(menuwin,$wl)
	unset IWOK_GLOBALS(menuwin,$wl)
	if { [$wroot.mnu.but.menu1 index last] == 0 } {
	    $wroot.mnu.but configure -state disabled
	}
    }
    return
}

proc wokRaise { w } { 
    catch {
	wm deiconify $w
	raise $w
    }
    return
}

proc wokHideAll { } {
    foreach ws [wokButton listw] {
	foreach tpl [lindex $ws 1] {
	    catch { 
		lower $tpl
	    }
	}
    }
}
proc wokShowAll { } {
    foreach ws [wokButton listw] {
	wokRaise [lindex $ws 1]
    }
}
proc wokMainHelp { w } { 
    global IWOK_GLOBALS
    global IWOK_WINDOWS
    global env

    set IWOK_WINDOWS($w,help) [set wh .wokMainHelp]
    if {[info exist IWOK_GLOBALS(windows)]} {
	if {[lsearch $IWOK_GLOBALS(windows) $wh ] == -1} {
	    lappend IWOK_GLOBALS(windows) $wh 
	}
    }

    set whelp [wokHelp $wh "About iwok"]
    set texte [lindex $whelp 0] ; set label [lindex $whelp 1]
    wokReadFile $texte  $env(WOK_LIBRARY)/wokMainHelp.hlp
    wokFAM $texte <.*> { $texte tag add big first last }
    $texte tag configure big -background Bisque3 -foreground black -borderwidth 2 \
	    -font -Adobe-Helvetica-Medium-R-Normal--*-120-* -relief raised
    update
    $texte configure -state disabled
    return
}
;#
;# Lit/ecrit/decode le contenu de Location. lire: [list fact shop wb unit]
;#
proc wokCWD { option args } {
    global IWOK_GLOBALS
    switch -- $option {

	read {
	    return $IWOK_GLOBALS(CWD) 
	}


	split {
	    set l {}
	    set str [wokCWD read]
	    if [wokinfo -x $str] {
		set fact [wokinfo -f $str]
		set shop [ lindex [split [wokinfo -s $str] :] end]
		set wb   [ lindex [split [wokinfo -w $str] :] end]
		set unit [ lindex [split [wokinfo -u $str] :] end]
		set l    [list $fact $shop $wb $unit]
	    }
	    return $l
	}

	readnocell {
	    set ll [llength [set lc [split $IWOK_GLOBALS(CWD) :]]]
	    if { $ll != 5 } {
		return $IWOK_GLOBALS(CWD)
	    } else {
		return [join [lrange $lc 0 [expr $ll - 2]] :]
	    }
	}

	write {
	    set IWOK_GLOBALS(CWD) $args
	    return
	}

	writenocallback {
	    $IWOK_GLOBALS(label) configure -disablecallback true
	    set IWOK_GLOBALS(CWD) $args
	    $IWOK_GLOBALS(label) configure -disablecallback false
	}

	updatehistory {
	    $IWOK_GLOBALS(label,entry) configure -state normal
	    $IWOK_GLOBALS(label) appendhistory $args
	    $IWOK_GLOBALS(label,entry) configure -state disabled
	}

	deletefromhistory {
	    set entry $args
	    set lstb [$IWOK_GLOBALS(label) subwidget listbox]
	    set inx [lsearch [$lstb get 0 end] $entry]
	    if { $inx != -1 } {
		$lstb delete $inx
	    }
	}

	disable {
	    $IWOK_GLOBALS(label,entry) configure -state disabled
	}

    }

}
;#           (((((((((((((((((( P O P U P  C A N V A S ))))))))))))))))))))
;#
;#
;#^WOK^k3dev^iwok^WOKTclLib^admfile WOK:k3dev:iwok:WOKTclLib:admfile stuff_admfile admfile image35 
;# wokGetdevunitstuffdate {18 18 600 18 10 1.2} current
;#
proc wokPOP:canvas:cmd { action } {
    set data [wokPOP:canvas:Selection] 
    set dir  [lindex $data 0] 
    set loc  [lindex $data 1] 
    set typ  [lindex $data 2]
    switch -- $action {
	wokcd {
	    wokMOV:wokcd $loc
	}

	Add {
	   wokCreate $dir $loc 
	}

	Delete {
	    wokDelete $dir $loc
	}

	Build {
	    set lud {}
	    set ltyp [split $typ _]
	    if { "[lindex $ltyp 0]" == "devunit" } { set lud [list [lindex $ltyp 1] [wokinfo -n $loc]] }
	     winbuild $loc $lud 
	}

	Prepare {
	    set lud {}
	    set ltyp [split $typ _]
	    if { "[lindex $ltyp 0]" == "devunit" } { set lud [list [lindex $ltyp 1] [wokinfo -n $loc]] }
	    wokPrepare $loc $lud
	}

	Properties {
	     wokProperties $dir $loc $typ
	}

    }
    return
}
;#
;# retourne 1 si il faut filtrer des UDS ou activer le pop
;#
proc wokPOP:DoSelect { } {
    if { [wokinfo -x [wokCWD read]] } {
	if { "[wokinfo -t [wokCWD read]]" == "workbench" } {
	    return 1
	} else {
	    return 0
	}
    } else {
	return 0
    }
}
proc wokPOP:canvas { option {w nil} } {
    global IWOK_GLOBALS

    switch -glob -- $option  {

	initialize {
	    $IWOK_GLOBALS(popc,mnu) add comm -lab [wokUtils:EASY:OneHead wokcd 30] \
		    -command [list wokPOP:canvas:cmd wokcd]
	    $IWOK_GLOBALS(popc,mnu) add separator
	    $IWOK_GLOBALS(popc,mnu) add casc -lab Select -menu $IWOK_GLOBALS(popc,mnu).selud
	    wokPOP:canvas initselud  [menu $IWOK_GLOBALS(popc,mnu).selud -font $IWOK_GLOBALS(font)]
	    wokPOP:canvas initselext [menu $IWOK_GLOBALS(popc,mnu).selext -font $IWOK_GLOBALS(font)]
	    set IWOK_GLOBALS(popc,Selected) All
	    $IWOK_GLOBALS(popc,mnu) add separator
	    $IWOK_GLOBALS(popc,mnu) add comm -lab [wokUtils:EASY:OneHead Add 30] \
		    -command [list wokPOP:canvas:cmd Add]
	    $IWOK_GLOBALS(popc,mnu) add comm -lab [wokUtils:EASY:OneHead Delete 30] \
		    -command [list wokPOP:canvas:cmd Delete]
	    $IWOK_GLOBALS(popc,mnu) add separator
	    $IWOK_GLOBALS(popc,mnu) add comm -lab [wokUtils:EASY:OneHead Build 30] \
		    -command [list wokPOP:canvas:cmd Build]
	    $IWOK_GLOBALS(popc,mnu) add comm -lab [wokUtils:EASY:OneHead Prepare 30] \
		    -comm    [list wokPOP:canvas:cmd Prepare]
	    $IWOK_GLOBALS(popc,mnu) add separator
	    $IWOK_GLOBALS(popc,mnu) add comm -lab [wokUtils:EASY:OneHead Properties 30] \
		    -comm    [list wokPOP:canvas:cmd Properties]
	}

	create {
            set IWOK_GLOBALS(popc) [tixPopupMenu $w.popc -postcmd [list wokPOP:canvas:PostCommand $w]]
            set IWOK_GLOBALS(popc,mnu) [$IWOK_GLOBALS(popc) subwidget menu]
	    $IWOK_GLOBALS(popc,mnu) configure -font $IWOK_GLOBALS(font)
	    $IWOK_GLOBALS(popc) subwidget menubutton configure -font $IWOK_GLOBALS(font)
	    return $w.popc
        }

	initselud {
	    set llitm [linsert $IWOK_GLOBALS(ucreate-P) 0 [list All All]]
	    foreach t $llitm {
		set xt [lindex $t 1]
		$w add radio -lab $xt -vari IWOK_GLOBALS(popc,Selected) -comm wokReaffCanvas
	    }
	}

	initselext {
	    set llitm [linsert $IWOK_GLOBALS(ucreate-P) 0 [list All All]]
	    foreach t $llitm {
		set xt [lindex $t 1]
		if [info exists IWOK_GLOBALS(EXT,$xt,ext)] {
		    foreach e $IWOK_GLOBALS(EXT,$xt,ext) {
			;#puts "$w add radio -lab $e -comm "
		    }
		}
	    }
	}

	disable {
	    foreach e $w {
		$IWOK_GLOBALS(popc,mnu) entryconfigure ${e}* -state disabled
	    }
	}

	activate {
	    foreach e $w {
		$IWOK_GLOBALS(popc,mnu) entryconfigure ${e}* -state active
	    }
	}
	
	activeselect {
	    set last [$IWOK_GLOBALS(popc,mnu) index last]
	    for {set i 0} {$i <= $last} {incr i} {
		if { "[$IWOK_GLOBALS(popc,mnu) type $i]" != "separator" } {
		    $IWOK_GLOBALS(popc,mnu) entryconfigure $i -state disabled
		}
	    }

	    if { [wokPOP:DoSelect] } {wokPOP:canvas activate Select}
	}
	
	factory    {
	    wokPOP:canvas activate {wokcd Add Delete Properties}
	    wokPOP:canvas disable  {Select Build Prepare}  
	}
		
	workshop   {
	    wokPOP:canvas activate {wokcd Add Delete Properties}
	    wokPOP:canvas disable  {Select Build Prepare}  
	}
	
	warehouse {
	    wokPOP:canvas activate {wokcd Properties}
	    wokPOP:canvas disable  {Select Add Delete Build Prepare}  
	}
	
	parcel {
	    wokPOP:canvas activate {wokcd Properties}
	    wokPOP:canvas disable  {Select Add Delete Build Prepare }  
	}
	
	workbench {
	    wokPOP:canvas activate {wokcd Add Delete Build Prepare Properties}
	    wokPOP:canvas disable  {Select}  
	}
	
	session    {
	    wokPOP:canvas activate {}
	    wokPOP:canvas disable  {Select wokcd Build Prepare Add Delete Properties}  
	}
	
	stuff_* {
	    wokPOP:canvas activate {wokcd}
	    wokPOP:canvas disable  {Select Add Delete Build Prepare Properties}  
	}
	
	parcel_* {
	    wokPOP:canvas activate {Delete}
	    wokPOP:canvas disable  {Add Delete wokcd Select Build Prepare Properties}  
	}
	
	parcelstuff_* {
	    wokPOP:canvas activate {}
	    wokPOP:canvas disable  {wokcd Add Delete Select Build Prepare Properties}  
	}
	
	devunit_* { 
	    wokPOP:canvas activate {wokcd Delete Properties Select Build Prepare}
	    wokPOP:canvas disable  {Add }  
	}

	trig_Repository {
	    wokPOP:canvas activate {}
	    wokPOP:canvas disable  {wokcd Select Build Prepare Add Delete Properties} 
	}

	trig_terminal {
	    wokPOP:canvas activate {Properties}
	    wokPOP:canvas disable  {wokcd Add Delete Select Build Prepare} 
	}

	trig_Queue {
	    wokPOP:canvas activate {Properties}
	    wokPOP:canvas disable  {wokcd Select Build Prepare Add Delete } 
	}

    }
}
;#
;# Appelee avant l affichage du Popup sur la canvas . Recupere la selection 
;# front = coord x du separateur de la paned window
;# taily = coord y du coin hg du canvas (aussi donc de la canvas)
;#  $x < $front vrai on est a gauche donc dans la canvas etc..
;# En  fonction du type  met les menus specifiques.
;#
;#   DANS le CANVAS:
;# Mb3 sur un element:   ouvrir (fait le double click / sauf si c est un terminal)
;#                       apercu rapide
;#                       supprimer/renommer/reconstruire/preparer
;#                       proprietes
;#
proc wokPOP:canvas:PostCommand { w x y } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS

    if { "[info procs wokPOP:canvas:GetInfo]" != "" } {
	set seltag [wokPOP:canvas:GetInfo]
	set mtx    [lindex $seltag 1]
	set option [lindex $seltag 2]
	rename wokPOP:canvas:GetInfo {}
    } else {
	set seltag {}
	set mtx    {Display}
	set option activeselect
    }
    eval "proc wokPOP:canvas:Selection {} { return \"$seltag\" }"
    set len [string length [lindex $seltag 1]]
    if { $len <= 30 } {
	$IWOK_GLOBALS(popc) subwidget menubutton configure  -text [lindex $seltag 1]
    } else {
	$IWOK_GLOBALS(popc) subwidget menubutton configure  -text [string range [lindex $seltag 1] 0 28]..
    }
    update
    wokPOP:canvas $option
    return 1
}

;#           (((((((((((((((((( P O P U P  H L I S T ))))))))))))))))))))
;#
;# 
;#
proc wokPOP:hlist:cmd { action } {
    set data [wokPOP:hlist:Selection] ;#WOK:k3dev:cle workbench cle image33 wokGetworkbenchdate {18 18 ...}
    set dir  [lindex $data 0] 
    set loc  [lindex $data 1] 
    set typ  [lindex $data 2] 
    switch -- $action {
	wokcd {
	    wokMOV:wokcd $loc
	}

	Add {
	   wokCreate $dir $loc 
	}

	Delete {
	    wokDelete $dir $loc
	}

	Build {
	    set lud {}
	    set ltyp [split $typ _]
	    if { "[lindex $ltyp 0]" == "devunit" } { set lud [list [lindex $ltyp 1] [wokinfo -n $loc]] }
	    winbuild $loc $lud 
	} 

	Prepare {
	    set lud {}
	    set ltyp [split $typ _]
	    if { "[lindex $ltyp 0]" == "devunit" } { set lud [list [lindex $ltyp 1] [wokinfo -n $loc]] }
	    wokPrepare $loc $lud
	}

	Properties {
	     wokProperties $dir $loc $typ
	}

    }
    return
}

proc wokPOP:hlist { option {w nil} } {
    global IWOK_GLOBALS

    switch -glob -- $option  {

	initialize {

	    $IWOK_GLOBALS(poph,mnu) add comm -lab [wokUtils:EASY:OneHead wokcd 30] \
		    -comm [list wokPOP:hlist:cmd wokcd]
	    $IWOK_GLOBALS(poph,mnu) add separator
	    $IWOK_GLOBALS(poph,mnu) add comm -lab [wokUtils:EASY:OneHead Add 30] \
		    -comm [list wokPOP:hlist:cmd Add]
	    $IWOK_GLOBALS(poph,mnu) add comm -lab [wokUtils:EASY:OneHead Delete 30] \
		    -comm [list wokPOP:hlist:cmd Delete]
	    $IWOK_GLOBALS(poph,mnu) add separator
	    $IWOK_GLOBALS(poph,mnu) add comm -lab [wokUtils:EASY:OneHead Build 30] \
		    -comm [list wokPOP:hlist:cmd Build]
	    $IWOK_GLOBALS(poph,mnu) add comm -lab [wokUtils:EASY:OneHead Prepare 30] \
		    -comm [list wokPOP:hlist:cmd Prepare]
	    $IWOK_GLOBALS(poph,mnu) add separator
	    $IWOK_GLOBALS(poph,mnu) add comm -lab [wokUtils:EASY:OneHead Properties 30] \
		    -comm [list wokPOP:hlist:cmd Properties]
	}

	adjust {
	}

	create {
            set IWOK_GLOBALS(poph) [tixPopupMenu $w.poph -postcmd [list wokPOP:hlist:PostCommand $w]]
            set IWOK_GLOBALS(poph,mnu) [$IWOK_GLOBALS(poph) subwidget menu]
	    $IWOK_GLOBALS(poph,mnu) configure -font $IWOK_GLOBALS(font)
	    $IWOK_GLOBALS(poph) subwidget menubutton configure -font $IWOK_GLOBALS(font)
	    return $w.poph
        }

	disable {
	    foreach e $w {
		$IWOK_GLOBALS(poph,mnu) entryconfigure ${e}*  -state disabled
	    }
	}
	
	activate {
	    foreach e $w {
		$IWOK_GLOBALS(poph,mnu) entryconfigure ${e}*  -state active
	    }
	}
	
	factory    {
	    wokPOP:hlist activate {wokcd Add Delete Properties}
	    wokPOP:hlist disable  {Build Prepare}  
	}
		
	workshop   {
	    wokPOP:hlist activate {wokcd Add Delete Properties}
	    wokPOP:hlist disable  {Build Prepare}  
	}
	
	warehouse {
	    wokPOP:hlist activate {wokcd  Properties }
	    wokPOP:hlist disable  {Build Add Delete Prepare }  
	}
	
	parcel {
	    wokPOP:hlist activate {wokcd Properties }
	    wokPOP:hlist disable  {Add Delete Build Prepare } 
	}
	
	workbench {
	    wokPOP:hlist activate {wokcd Add Delete Build Prepare Properties}
	    wokPOP:hlist disable  {}  
	}
	
	session    {
	    wokPOP:hlist activate {Properties}
	    wokPOP:hlist disable  {wokcd Build Prepare Add Delete}  
	}
	
	stuff_* {
	    wokPOP:hlist activate {wokcd}
	    wokPOP:hlist disable  {Build Add Delete Prepare Properties}  
	}
	
	parcel_* {
	    wokPOP:hlist activate {}
	    wokPOP:hlist disable  {wokcd Add Delete Build Prepare Properties}  
	}
	
	parcelstuff_* {
	    wokPOP:hlist activate {}
	    wokPOP:hlist disable  {wokcd Add Delete Build Prepare Properties}  
	}
	
	devunit_* { 
	    wokPOP:hlist activate {wokcd  Delete Properties Build Prepare}
	    wokPOP:hlist disable  { Add }  
	}

	trig_Repository {
	    wokPOP:hlist activate {}
	    wokPOP:hlist disable  {wokcd Add Delete Build Prepare Properties}  
	}

	trig_terminal {
	    wokPOP:hlist activate {Properties}
	    wokPOP:hlist disable  {wokcd Add Delete Build Prepare} 
	}

	trig_Queue {
	    wokPOP:hlist activate {Properties}
	    wokPOP:hlist disable  {wokcd Add Delete Build Prepare}  
	}
    }
}
;#
;#                    appelee avant le Post
;#
proc wokPOP:hlist:PostCommand { w x y } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS
    $IWOK_WINDOWS($w,NAV,hlist) anchor clear
    $IWOK_WINDOWS($w,NAV,hlist) selection clear
    set Y     [expr $y - [winfo rooty $IWOK_GLOBALS(tree,name)]]
    set hlist $IWOK_WINDOWS($w,NAV,hlist) 
    set nearest  [$hlist nearest $Y]
    set seltag [$hlist info data [$hlist nearest $Y]]
    eval "proc wokPOP:hlist:Selection {} { return \"$nearest $seltag\" }"

    set len [string length [lindex $seltag 0]]
    if { $len <= 30 } {
	$IWOK_GLOBALS(poph) subwidget menubutton configure -text [lindex $seltag 0]
    } else {
	$IWOK_GLOBALS(popc) subwidget menubutton configure  -text [string range [lindex $seltag 0] 0 28]..
    }
    update
    wokPOP:hlist [lindex $seltag 1]
    return 1
}


;#           (((((((((((((((((( D I S P L A Y ))))))))))))))))))))
;#
;# Selectionne dans l les uds du type selectionne. (IWOK_GLOBALS(popc,Selected) = package..)
;#
;# faudrait voir a speeder qunad il ne s'agit pas d'UD.
;# il faut initialiser IWOK_GLOBALS(popc,Selected) a All 
proc wokSelType { l } {
    global IWOK_GLOBALS
    if { ![wokPOP:DoSelect] } { return $l }
    if { "$IWOK_GLOBALS(popc,Selected)" == "All" } { return $l }
    set ll {}
    foreach x $l {
	set rtyp [lindex [split [lindex $x 2] _] 1]
	if { "$IWOK_GLOBALS(popc,Selected)" == "$rtyp" } {
	    lappend ll $x
	}
    }
    return $ll
}
;# 
;# affiche les items dans le canvas.  
;# 
proc wokUpdateCanvas { w loc } {
    set l     [wokSelType [wokNAV:tlist:GetData $w $loc]]
    set disp  [wokNAV:tlist:Display $w $loc]
    set func  [wokDSP:Func]
    set fdate [wokNAV:tlist:date $w $loc]
    if [wokDSP:IsLong] {
	if [wokDSP:IsLast] {
	    set ll [$fdate $l 1]
	} else {
	    set ll [$fdate $l 0]
	}
    } else {
	set ll $l
    }
    $func $disp $ll

    return
}
;#
proc wokDSP:Display { button toggle } {
    global IWOK_GLOBALS
    if { $toggle == 1 } {
	set IWOK_GLOBALS(canvas,func) $IWOK_GLOBALS(canvas,func,$button)
	wokUpdateCanvas $IWOK_GLOBALS(toplevel) [wokCWD read]
    }
    return
}
proc wokDSP:Init { w } {
    global IWOK_GLOBALS
    
    set ww [frame $w.myf]
    tixSelect $ww.dis -allowzero false -radio true -command wokDSP:Display \
	    -label "" \
	    -variable IWOK_GLOBALS(canvas,format) \
	    -options {
	label.width 0
	label.padx 0
	label.anchor n
    }
    
    set msg(byrow)  "Rows"                ;set IWOK_GLOBALS(canvas,func,byrow)  wokUpdatePage_xy;# X- X-
    set msg(bycol)  "Columns"             ;set IWOK_GLOBALS(canvas,func,bycol)  wokUpdatePage_tt;# X-
    set msg(bylong) "Date/Size"           ;set IWOK_GLOBALS(canvas,func,bylong) wokUpdatePage_cy;# X- -
    set msg(bylast) "Last modified first" ;set IWOK_GLOBALS(canvas,func,bylast) wokUpdatePage_cy;# Y- -


    foreach bix [array names msg] {
	$ww.dis add $bix  -image [tix getimage $bix]
	set bux [$ww.dis subwidget $bix]
	tixBalloon ${bux}.bal
	${bux}.bal bind ${bux} -msg $msg($bix)
    }

    pack $ww.dis -expand yes -fill both -padx 8 -pady 8

    $ww.dis configure -disablecallback true             ;# sinon boum CWD pas encore allouee
    set IWOK_GLOBALS(canvas,format) byrow  
    set IWOK_GLOBALS(canvas,func)   wokUpdatePage_xy
    $ww.dis configure -disablecallback false

    return $ww
}
#
#
#
proc wokDSP:Func { } {
    global IWOK_GLOBALS
    return $IWOK_GLOBALS(canvas,func)
}
#
# retourne 1 si on doit calculer la date des items a afficher.  
#
proc wokDSP:IsLong { } {
    global IWOK_GLOBALS
    if { "$IWOK_GLOBALS(canvas,format)" == "bylong" || "$IWOK_GLOBALS(canvas,format)" == "bylast" } {
	return 1 
    } else {
	return 0
    }
}
#
# retourne 1 si on doit ordonner par rapport a mtime
#
proc wokDSP:IsLast { } {
    global IWOK_GLOBALS
    if { "$IWOK_GLOBALS(canvas,format)" == "bylast" } {
	return 1 
    } else {
	return 0
    }
}
#
# colle les scrollbars de w au debut de la w 
#
proc wokUSB { w } {
    set hsb  [$w subwidget hsb]
    set vsb  [$w subwidget vsb]
    set hcmd [lindex [$hsb configure -command] 4]
    set vcmd [lindex [$vsb configure -command] 4]
    eval $hcmd moveto 0
    eval $vcmd moveto 0
    return
}
#
# ajuste la taille du canvas x et y en "screen units" i.e. celle retournee par coord ou bbox
#
proc wokSetCanvasSize { x y } {
    global IWOK_GLOBALS
    set Mx $IWOK_GLOBALS(canvas,width)
    set My $IWOK_GLOBALS(canvas,height)
    $IWOK_GLOBALS(canvas) configure \
	    -width  [expr { ($x <= $Mx) ? $Mx : $x }] \
	    -height [expr { ($y <= $My) ? $My : $y }]
    return
}
;#
;# items en ligne 
;#
proc wokUpdatePage_xy { param itemlist } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS
    set w $IWOK_GLOBALS(toplevel)

    set fscr $IWOK_WINDOWS($w,NAV,scrolled)

    set can $IWOK_GLOBALS(canvas)
    wokUSB $fscr

    $can delete all

    set X    [lindex $param 0] 
    set Y    [lindex $param 1] 
    set WDTH [lindex $param 2] 
    set DY   [lindex $param 3]
    set DT   [lindex $param 4]
    set COEF [lindex $param 5]

    set mdx 0
    set lele {}
    ;#^WOK^k3dev^iwok WOK:k3dev:iwok workbench iwok image17

    foreach E $itemlist {
	set name [lindex $E 3]
	set btm  [lindex $E 4]
	set ima [$can create image 0 0 -image $btm -tag $E]
	set itx [$can create text  0 0 -anchor w -text $name \
		-fill $IWOK_GLOBALS(toplevel,fg) -font $IWOK_GLOBALS(font) -tag $E]
	$can bind $ima <Any-Enter> {catch { %W configure -cursor {hand2 red white}}}
	$can bind $ima <Any-Leave> {catch { %W configure -cursor {}}}
	$can bind $itx <Any-Enter> {catch { %W configure -cursor {hand2 red white}}}
	$can bind $itx <Any-Leave> {catch { %W configure -cursor {}}}
	lappend lele [list $ima $itx]
	set retl  [$can bbox $itx]
	set d [expr [lindex $retl 2] - [lindex $retl 0]]
	set mdx [expr { ($d > $mdx) ? $d : $mdx }]
    }

    set supx 0 ; set supy 0 ; set mdx [expr { int ( $COEF * $mdx ) } ]

    set INIX $X
    foreach e $lele {
	set ima [lindex $e 0]
	set itx [lindex $e 1]
	$can coords $ima $X $Y
	$can coords $itx [expr $X+$DT] $Y
	set NX [incr X $mdx]
	if { $NX > $WDTH } {
	    set X $INIX
	    set Y [incr Y $DY]
	} else {
	    set X $NX
	}
	set retl [wokMaxbbox [$can bbox $ima] [$can bbox $itx]]
	set x2 [lindex $retl 0]
	set y2 [lindex $retl 1]
	
	set supx [expr { ($x2 > $supx) ? $x2 : $supx }]
	set supy [expr { ($y2 > $supy) ? $y2 : $supy }]
    }
    
    wokSetCanvasSize $supx $supy
    pack $can
    return

}
;#
;# item sur une seule colonne: x constant
;#
proc wokUpdatePage_cy { param itemlist } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS
    set w $IWOK_GLOBALS(toplevel)
    set fscr $IWOK_WINDOWS($w,NAV,scrolled)
    set can $IWOK_GLOBALS(canvas)
    wokUSB $fscr

    $can delete all
    
    set X    [lindex $param 0] 
    set Y    [lindex $param 1] 
    set WDTH [lindex $param 2] 
    set DY   [lindex $param 3]
    set DT   [lindex $param 4]
    set COEF [lindex $param 5]


    set supx 0 ; set supy 0 ;
    
    foreach E $itemlist {
	set name [lindex $E 3]
	set btm  [lindex $E 4]
	set ima [$can create image $X $Y -image $btm -tag $E]
	set itx [$can create text [expr $X+$DT] $Y -anchor w -fill $IWOK_GLOBALS(toplevel,fg) \
		-text $name -font $IWOK_GLOBALS(font) -tag $E ]
	$can bind $ima <Any-Enter> {catch { %W configure -cursor {hand2 red white}}}
	$can bind $ima <Any-Leave> {catch { %W configure -cursor {}}}
	$can bind $itx <Any-Enter> {catch { %W configure -cursor {hand2 red white}}}
	$can bind $itx <Any-Leave> {catch { %W configure -cursor {}}}
	set Y [incr Y $DY]	
        set retl [wokMaxbbox [$can bbox $ima] [$can bbox $itx]]
	set x2 [lindex $retl 0]
	set y2 [lindex $retl 1]
	set supx [expr { ($x2 > $supx) ? $x2 : $supx }]
	set supy [expr { ($y2 > $supy) ? $y2 : $supy }]
    }
    
    wokSetCanvasSize $supx $supy
    pack $can
    return

}

;#
;# item ordonne sur une seule colonne
;#
proc wokUpdatePage_tt { param itemlist } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS
    set w $IWOK_GLOBALS(toplevel)
    set fscr $IWOK_WINDOWS($w,NAV,scrolled)
    set can $IWOK_GLOBALS(canvas)
    wokUSB $fscr

    $can delete all
    
    set X    [lindex $param 0]
    set Y    [lindex $param 1] 
    set WDTH [lindex $param 2]
    set DY   [lindex $param 3]
    set DT   [lindex $param 4]
    set COEF [lindex $param 5]

    set supx 0 ; set supy 0 ; set mdx 0

    set nblm 0

    set TA [lindex [lindex $itemlist end] 2]

    set nlig 28 
    switch -glob -- $TA {
	trig_terminal { set nlig 36 }
	devunit_*     { set nlig 21 }
    }
    set nb 38 ; set nbm2 36
    foreach E  $itemlist {
	set ina [lindex $E 3]
	set len [string length $ina]
	if { $len <= $nb } {
	    set name $ina
	} else {
	    set name [string range $ina 0 $nbm2]..
	}
	set btm  [lindex $E 4]
	set ima [$can create image $X $Y -image $btm -tag $E]
	set itx [$can create text [expr $X+$DT] $Y -anchor w -fill $IWOK_GLOBALS(toplevel,fg) \
		-text $name -font $IWOK_GLOBALS(font) -tag $E ]
	$can bind $ima <Any-Enter> {catch { %W configure -cursor {hand2 red white}}}
	$can bind $ima <Any-Leave> {catch { %W configure -cursor {}}}
	$can bind $itx <Any-Enter> {catch { %W configure -cursor {hand2 red white}}}
	$can bind $itx <Any-Leave> {catch { %W configure -cursor {}}}
	incr nblm
	if { $nblm > $nlig } {
	    set nblm 0
	    set X [expr $X + int ( $COEF * $mdx )]
	    set Y [lindex $param 1] 
	    set mdx 0
	} else {
	    set Y [incr Y $DY]	
	    set bx1 [$can bbox $ima] 
	    set lx1 [expr [lindex $bx1 2] - [lindex $bx1 0]]
	    set bx2 [$can bbox $itx] 
	    set lx2 [expr [lindex $bx2 2] - [lindex $bx2 0]]	
	    set d   [expr $lx1 + $lx2]	    
	    set mdx [expr { ($d > $mdx) ? $d : $mdx }]
	}
	set x2 [lindex $bx2 2]
	set y2 [lindex $bx1 2]
	set supx [expr { ($x2 > $supx) ? $x2 : $supx }]
	set supy [expr { ($y2 > $supy) ? $y2 : $supy }]
    }
	
    wokSetCanvasSize $supx $supy
    pack $can
    return

}



#
# retourne le max de 2 bbox
# 
proc wokMaxbbox { l1 l2 } { 
    return [list [max [lindex $l1 2] [lindex $l2 2]] [max [lindex $l1 3] [lindex $l2 3]]]
}

#
#         ((((((((((( D A T E ))))))))))) ll = ;#^WOK^k3dev^iwok WOK:k3dev:iwok workbench iwok imag17 ..
#
proc wokGetsessiondate { ll last } { 
    return $ll
}

proc wokGetfactorydate { ll last } { 
    return $ll
}

proc wokGetworkshopdate { ll last } {  
    return $ll
}

proc wokGetworkbenchdate { ll last } { 
    return $ll
    set lt [woktutu4 $ll]
    if { $last == 1 } {
	set lr [lsort -decreasing -command wok5Sort $lt]
    } else {
	set lr $lt
    }
    set l {}
    set nb 28
    set fm 32
    foreach e $lr {
	set len [string length [lindex $e 3]]
	if { $len <= [expr $nb + 2 ]} {
	    set str [lindex $e 3]
	} else {
	    set str [string range [lindex $e 3] 0 $nb]..
	}
	set x [split [lindex $e 5] ,]
	set dat [string range [clock format [lindex $x 0]] 4 18]
	set siz [lindex $x 1]
	lappend l [lreplace $e 3 3 [format "%-${fm}s %9s %14s" $str $siz $dat]]
    }
    return $l
}
;#
;# retourne pour chaque Ud la date du fichier le plus recent et la somme des sizes de sources
;#
proc woktutu4 { ll } {
    set l {}
    foreach e $ll {
	set st [wokUtils:FILES:StatDir [wokinfo -p source:. [lindex $e 1]]]
	lappend l [lreplace $e 5 5 [lindex $st 0],[lindex $st 1]]
    }
    return $l
}

proc wokGetdevunitdate { ll last } { 
    return $ll
    set lt [woktutu6 $ll]
    if { $last == 1 } {
	set lr [lsort -decreasing -command wok5Sort $lt]
    } else {
	set lr $lt
    }
    set l {}
    set nb 28
    set fm 32
    foreach e $lr {
	set len [string length [lindex $e 3]]
	if { $len <= [expr $nb + 2 ]} {
	    set str [lindex $e 3]
	} else {
	    set str [string range [lindex $e 3] 0 $nb]..
	}
	set x [split [lindex $e 5] ,]
	set dat [string range [clock format [lindex $x 0]] 4 18]
	set siz [lindex $x 1]
	lappend l [lreplace $e 3 3 [format "%-${fm}s %9s %14s" $str $siz $dat]]
    }
    return $l
}

proc woktutu6 { ll } {
   set l {}
    foreach e $ll {
	set L [llength [set lc [split [lindex $e 1] :]]]
	set actloc [join [lrange $lc 0 [expr $L - 2]] :]
	set st [wokUtils:FILES:StatDir [wokinfo -p [lindex $lc end]:. $actloc]]
	lappend l [lreplace $e 5 5 [lindex $st 0],[lindex $st 1]]
    }
    return $l
}


proc wokGetparceldate { ll last } { 
    return $ll
}
proc wokGetparcelunitdate { ll last } { 
    return $ll
}
proc wokGetparcelunitstuffdate { ll last } { 
    return [wokGetdevunitstuffdate $ll $last]
}
;#
;# ll est triee par ordre alphab; 
;#
proc wokGetdevunitstuffdate { ll last } { 
    set lt [woktutu5 $ll]
    if { $last == 1 } {
	set lr [lsort -decreasing -command wok5Sort $lt]
    } else {
	set lr $lt
    }
    set l {}
    set nb 28
    set fm 32
    foreach e $lr {
	set len [string length [lindex $e 3]]
	if { $len <= [expr $nb + 2 ]} {
	    set str [lindex $e 3]
	} else {
	    set str [string range [lindex $e 3] 0 $nb]..
	}
	set x [split [lindex $e 5] ,]
	set dat [string range [clock format [lindex $x 0]] 4 18]
	set siz [lindex $x 1]
	lappend l [lreplace $e 3 3 [format "%-${fm}s %9s %14s" $str $siz $dat]]
    }
    return $l
}

proc wok5Sort { a b } {
    return [expr [lindex [split [lindex $a 5] ,] 0] - [lindex [split [lindex $b 5] ,] 0] ]
}
#
# remplace le path par la date sous forme comparable
#
proc woktutu5 { ll } {
    set l {}
    foreach e $ll {
	catch {unset m}
	file lstat [lindex $e 5] m
	lappend l [lreplace $e 5 5 $m(mtime),$m(size)]
    }
    return $l
}
#
# Boutons up et Layout
#
proc wokMOV:Init { w } {
    set ww [frame $w.mov]
    tixButtonBox $ww.mov -orientation horizontal -relief flat -padx 0 -pady 0

    $ww.mov add back -image [tix getimage back]  -command wokReaff
    $ww.mov add wcd  -image [tix getimage wokcd] -command wokMOV:wokcd

    set bck [$ww.mov subwidget back] ; tixBalloon $bck.bal ; $bck.bal bind $bck -msg "Go up"
    set wcd [$ww.mov subwidget wcd]  ; tixBalloon $wcd.bal ; $wcd.bal bind $wcd -msg "wokcd"

    pack $ww.mov -expand yes -fill both -padx 6 -pady 6
    return $ww 
}
#
# WOK:k3dev:iwok:WOKTclLib     => fait wokcd 
# WOK:k3dev:iwok:WOKTclLib:xxx => fait wokcd WOK:k3dev:iwok:WOKTclLib et cd /...
# Pour l'instant c'est ici que sont configures les boutons.
#
proc wokMOV:wokcd { {here {}} } {
    if { $here == {} } {
	set location [wokCWD read]
    } else {
	set location $here
    }
    set ll [llength [set lc [split $location :]]]
    if { $ll != 5 } {
	catch { wokcd [set actloc [join $lc :]] }
    } else {
	catch {
	    wokcd   [set actloc [join [lrange $lc 0 [expr $ll - 2]] :]]
	    cd [wokinfo -p [lindex $lc end]:. $actloc]
	}
    }

    return
}
#
# Maintenant il y : en tete de l'adresse....
#
proc wokMOV:Range { adr } {
     if { "[string index $adr 0]" == ":" } {
	return [string range $adr 1 end]
    } else {
	return $adr
    }
}
#
# 
#
proc wokMOV:Alonzi { tpl wokcd } {
   
    if { [set f [wokMOV:Range [wokinfo -f $wokcd]]  ] != {} } {
	wokNAV:Tree:Updateworkshop $tpl ${f} ^${f}
	set loc  ${f} 
	set dir ^${f}
    } else {
	return
    }

    if { [set s [wokMOV:Range [wokinfo -s $wokcd]]  ] != {} } {
	set S [wokinfo -n $s]
	wokNAV:Tree:Updateworkbench $tpl ${f}:$S ^${f}^$S
	set loc  ${f}:$S 
	set dir ^${f}^$S
    } else {
	wokNAV:Tree:SeeMe $tpl $loc $dir
	return
    }
    
    if { [set w [wokMOV:Range [wokinfo -w $wokcd]]  ] != {} } {
	set W [wokinfo -n $w]
	wokNAV:Tree:Updatedevunit $tpl ${f}:$S:$W ^${f}^$S^$W
	set loc  ${f}:$S:$W 
	set dir ^${f}^$S^$W
    } else {
	wokNAV:Tree:SeeMe $tpl $loc $dir
	return
    }

    if { [set u [wokMOV:Range [wokinfo -u $wokcd]]  ] != {} } {
	set U [wokinfo -n $u]
	wokNAV:Tree:Updatedevunitstuff $tpl ${f}:$S:$W:$U ^${f}^$S^$W^$U
	set loc  ${f}:$S:$W:$U
	set dir ^${f}^$S^$W^$U
    } else {
	wokNAV:Tree:SeeMe $tpl $loc $dir
	return
    }

    wokNAV:Tree:SeeMe $tpl $loc $dir
    return
}
;#
;# fait find parce que ca non plus ca existe pas
;#
proc wokFind { location } {
    if ![wokinfo -x $location] return
    if {"[wokinfo -t $location]" == "devunit" } {
	return $location
    } elseif {"[wokinfo -t $location]" == "workbench" } {
	set l {}
	foreach e [w_info -l $location] {
	    set l [concat $l ${location}:$e]
	}
	return [concat $l $location]
    } elseif {"[wokinfo -t $location]" == "workshop" } {
	set l {}
	foreach e [sinfo -w $location] {
	    set l [concat $l [wokFind ${location}:$e]]
	}
	return [concat $l $location]
    } elseif {"[wokinfo -t $location]" == "factory" } {
	set l {}
	foreach e [finfo -s $location] {
	    set l [concat $l [wokFind ${location}:$e]]
	}
	return [concat $l $location]
    }
}
