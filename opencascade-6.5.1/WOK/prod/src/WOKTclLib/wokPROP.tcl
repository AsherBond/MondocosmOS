;#           (((((((((((((((((( P R O P E R T I E S  ))))))))))))))))))))
;#
;#
proc wokProperties {dir location itype } {
    global IWOK_GLOBALS

    set w [wokTPL prop$location]
    if [winfo exists $w ] {
	destroy $w
    } 

    toplevel    $w
    wm title    $w  "Properties of ($location)"
    wm geometry $w  684x439

    wokButton setw [list properties $w]

    set boldfnt [tix option get bold_font]
    set IWOK_GLOBALS($w,PROP,toplevel)      $w
    set IWOK_GLOBALS($w,PROP,location)      $location
   

    menubutton $w.file -menu $w.file.m -text File -underline 0 -takefocus 0
    menu $w.file.m 
    $w.file.m add command -label "Close    " -underline 0 -command [list wokPROP:Kill $w]
    
    set notes [tixNoteBook $w.notes -ipadx 1 -ipady 1] 
    tixForm $w.file 
    tixForm $notes -top $w.file -left 2 -right %99 -bottom %99

    $notes.nbframe configure -backpagecolor  grey51

    if [wokinfo -x $location] {
	set type [wokinfo -t $location]
	set name [wokinfo -n $location]
    } else {
	regsub {trig_} $itype "" type 
    }

    switch $type {

	session {
	    $notes add pag1 -createcmd "wokPROP:NOT wokPROP:vrs $w $notes pag1" \
		    -label "WOK Version"    -raisecmd [list wokPROP:UPD $w]  
	    $notes add pag2 -createcmd "wokPROP:NOT wokPROP:pkg $w $notes pag2" \
		    -label "Packages used" -raisecmd [list wokPROP:UPD $w]  
	    $notes add pag3 -createcmd "wokPROP:NOT wokPROP:env $w $notes pag3" \
		    -label "Environment"   -raisecmd [list wokPROP:UPD $w]
	    $notes add pag4 -createcmd "wokPROP:NOT wokPROP:pth $w $notes pag4" \
		    -label "Pathes"         -raisecmd [list wokPROP:UPD $w]
	    $notes add pag6 -createcmd "wokPROP:NOT wokPROP:EDL $w $notes pag6 $location" \
		    -label "Edl"             -raisecmd [list wokPROP:UPD $w]
	}

	factory {
	    $notes add pag1 -createcmd "wokPROP:NOT wokPROP:factory $w $notes pag1 $location" \
		    -label "General" -raisecmd [list wokPROP:UPD $w] 
	    $notes add pag2 -createcmd "wokPROP:NOT wokPROP:EDL $w $notes pag2 $location" \
		    -label "Edl" -raisecmd [list wokPROP:UPD $w]
	}

	warehouse {
	    $notes add pag1 -createcmd "wokPROP:NOT wokPROP:warehouse $w $notes pag1 $location" \
		    -label "General" -raisecmd [list wokPROP:UPD $w] 
	    $notes add pag2 -createcmd "wokPROP:NOT wokPROP:EDL $w $notes pag2 $location" \
		    -label "Edl" -raisecmd [list wokPROP:UPD $w]
	}

	parcel {
	    $notes add pag1 -createcmd "wokPROP:NOT wokPROP:parcel $w $notes pag1 $location" \
		    -label "General" -raisecmd [list wokPROP:UPD $w]
	    $notes add pag2 -createcmd "wokPROP:NOT wokPROP:parcelExtRef $w $notes pag2 $location" \
		    -label "External References" -raisecmd [list wokPROP:UPD $w]
	}

	workshop {
	    $notes add pag1 -createcmd "wokPROP:NOT wokPROP:workshop $w $notes pag1 $location" \
		    -label "General" -raisecmd [list wokPROP:UPD $w]
	    $notes add pag2 -createcmd "wokPROP:NOT wokPROP:workshopistuff $w $notes pag2 $location" \
		    -label "Integration stuff" -raisecmd [list wokPROP:UPD $w]
	    $notes add pag3 -createcmd "wokPROP:NOT wokPROP:workbenchtree $w $notes pag3 $location" \
		    -label "Workbench Tree" -raisecmd [list wokPROP:UPD $w]
	    $notes add pag4 -createcmd "wokPROP:NOT wokPROP:EDL $w $notes pag4 $location" \
		    -label "Edl" -raisecmd [list wokPROP:UPD $w]
	}

	workbench {
	    $notes add pag1 -createcmd "wokPROP:NOT wokPROP:workbench $w $notes pag1 $location" \
		    -label "General" -raisecmd [list wokPROP:UPD $w]
	    $notes add pag2 -createcmd "wokPROP:NOT wokPROP:workbenchqq $w $notes pag2 $location" \
		    -label "Integration stuff" -raisecmd [list wokPROP:UPD $w]
	    $notes add pag3 -createcmd "wokPROP:NOT wokPROP:EDL $w $notes pag3 $location" \
		    -label "Edl" -raisecmd [list wokPROP:UPD $w]
	}

	devunit {
	    $notes add pag1 -createcmd "wokPROP:devunit $w $notes pag1 $location" \
		    -label "General"   -raisecmd [list wokPROP:UPD $w] 
	    $notes add pag2 -createcmd "wokPROP:arb $w $notes pag2 $name $location" \
		    -label "Suppliers" -raisecmd [list wokPROP:UPD $w] 
	    $notes add pag3 -createcmd "wokPROP:clt $w $notes pag3 $name $location" \
		    -label "Clients"   -raisecmd [list wokPROP:UPD $w]
	    $notes add pag4 -createcmd "wokPROP:NOT wokPROP:BLD $w $notes pag4 $location" \
		    -label "Building steps" -raisecmd [list wokPROP:UPD $w]
	    $notes add pag5 -createcmd "wokPROP:NOT wokPROP:EDL $w $notes pag5 $location" \
		    -label "Edl" -raisecmd [list wokPROP:UPD $w]
	}

	terminal {
	    set data [wokNAV:tlist:TermData $IWOK_GLOBALS(toplevel) $dir]
	    set name [lindex $data end]
	    $notes add pag1 -createcmd "wokPROP:NOT wokPROP:terminal $w $notes pag1 $name" \
		    -label "General" -raisecmd [list wokPROP:UPD $w]
	}

	Repository {
	    set data [wokNAV:tlist:TermData $IWOK_GLOBALS(toplevel) $dir] 
	    ;# data = WOK:k4dev:Repository trig_Repository Repository image37 wokGetworkbenchdate {params}
	    regsub {:Repository} [lindex $data 0] "" fshop
	    
	}

	Queue {
	    set data [wokNAV:tlist:TermData $IWOK_GLOBALS(toplevel) $dir]
	    ;# data = WOK:k4dev:Queue trig_Queue Queue image37 wokGetworkbenchdate {params}
	    regsub {:Queue} [lindex $data 0] "" location
	    $notes add pag1 -createcmd "wokPROP:NOT wokPROP:Queue $w $notes pag1 $location" \
		    -label "General"   -raisecmd [list wokPROP:UPD $w] 
	}

    }
    return
}
;#
;#                            ((((((( F I L E    I N T E G R A T I O N  )))))))
;#
proc wokPROP:Queue { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    if { [wokStore:Report:SetQName $location] != {} } {	
	if { [set qdir [wokStore:Report:GetRootName]] != {} } {
	    set text [text $w.top.jnl -relief flat -font $IWOK_GLOBALS(font)]
	    $text insert end "Integration queue in directory: $qdir\n\n"
	    set journal [wokIntegre:Journal:GetName] 
	    if { $journal != {} } {
		set dir [file dirname $journal]
		$text insert end "Journal in directory: $dir\n\n"
		foreach j [wokIntegre:Journal:List] {
		    $text insert end "[format "%15s %-9d" [file tail $j] [file size $j]]\n"
		}
		set t [clock format [file mtime $journal]]
		set str [format "%15s %-8d(Last modified %s)" [file tail $journal] [file size $journal] $t]
		$text insert end "$str\n\n"
		set scoop [wokIntegre:Scoop:Read]
		if { $scoop != {} } {
		    $text insert end "Last integration: \n\n $scoop "
		}
		$text configure -state disabled
		tixForm $text -top 2 -left 2 -bottom %99 -right %99
	    }
	}
    }
    return
}
;#
;#                            ((((((( A R B R E   D E P E N D A N C E S  )))))))
;#
proc wokPROP:arb { adr nb page name location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief sunken -bd 1 
    button $w.but -text "Click here to run" -command [list DependenceTree $w.top.tree $name $location]
    label $w.lab
    eval "proc wokPROP:LabArb {} { return $w.lab }"
    tixForm $w.but -top 2 
    tixForm $w.top -top $w.but -left 2 -right %99 -bottom $w.lab
    tixForm $w.lab -left 2 -right %99 -bottom %99
    return
}

;#
;#                            ((((((( A R B R E   C L I E N T S  )))))))
;#
proc wokPROP:clt { adr nb page name location} {
    global IWOK_GLOBALS ClientTree_FileName
    set w [$nb subwidget $page]

    frame $w.top -relief sunken -bd 1
    button $w.but -text "Click here to run" -command [list ClientTree $w.top.treeclt $name $location $w.meter]
    label $w.lab
    tixMeter $w.meter -value 0. -relief flat
    $w.meter config -value 0 -text " "
    tixLabelEntry $w.filename -label "Header Name :" -options {entry.width 20 label.width 0 entry.textVariable ClientTree_FileName}
    eval "proc wokPROP:LabClt {} { return $w.lab }"
    tixForm $w.but -top 2
    tixForm $w.meter -top 4  -left $w.but -right %99 
    tixForm $w.filename -top $w.but -left 2 -right %99
    tixForm $w.top -left 2 -right %99 -bottom $w.lab -top $w.filename
    tixForm $w.lab -left 2 -right %99 -bottom %99   

    return
}

proc wokPROP:Meter {meter maxrange progress} {
    set can [$meter subwidget canvas]
    set width [expr [winfo width $can] + 2]
    $meter configure -width $width
    
    set step [expr {100.0 / $maxrange}]
    set progress [expr {$progress + $step }]
    set value    [expr $progress * 0.01]
    set text [expr int($progress)]%

    $meter config -value $value -text $text

    return $progress
}

proc wokPROP:BrowseArb { location item }  { 
    if { "[info procs wokPROP:LabArb]" != "" } {
	set lab [wokPROP:LabArb]
	set ud [lindex [split $item .] end]
	set lud [woklocate -u $ud $location]
	if { $lud != {} } {
	    set type [uinfo -t $lud] 
	    $lab configure -text "Location:    $lud ( $type )"
	} else {
	    $lab configure -text ""
	}
    }
    return
}
;#
;#                            ((((((( P A C K A G E S   U T I L I S E S  )))))))
;#
proc wokPROP:vrs { adr nb page location} {
    global IWOK_GLOBALS
    global env
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    set lm {}
    foreach name [lsort [array names env WOK*]] {
	lappend lm [list $name $env($name)]
    }
    set vrs [file tail $env(WOKHOME)]
    ;#set image [tix getimage wok]
    label   $w.top.ima ;#-image $image
    label   $w.top.vrs -font $IWOK_GLOBALS(boldfont) -text "Used: $vrs"
    set txt [text $w.top.msg -relief flat -font $IWOK_GLOBALS(font)]
    wokPROP:Nice $txt $lm 
    tixForm $w.top.ima -top 12 -left 6
    tixForm $w.top.vrs -top [list $w.top.ima 10]
    tixForm $w.top.msg -top [list $w.top.vrs 20] -left 2
    return
}

proc wokPROP:pkg { adr nb page args} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    set lm {}
    foreach name [lsort [package names]] {
	lappend lm [list $name [package version $name]]
    }
    tixScrolledText  $w.top.msg
    set txt [$w.top.msg subwidget text]
    $txt configure -relief flat -font $IWOK_GLOBALS(font)
    wokPROP:Nice $txt $lm 
    tixForm $w.top.msg -left 1 -top 1 -right %100 -bottom %100
    return
}
;#
;#                            ((((((( P A T H E S )))))))
;#
proc wokPROP:pth { adr nb page args } {

    global env	
    global IWOK_GLOBALS

    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1

    tixPanedWindow $w.top.pane -orient horizontal  -paneborderwidth 0 -separatorbg gray50
    pack $w.top.pane -side top -expand yes -fill both -padx 1 -pady 1

    set p1   [$w.top.pane add tree -min 100 -size  220]
    set p2   [$w.top.pane add text]

    set tree [tixTree  $p1.tree]
    set text [tixScrolledText  $p2.text]

    pack $p1.tree -expand yes -fill both -padx 1 -pady 1
    pack $p2.text -expand yes -fill both -padx 1 -pady 1 -padx 3


    set hlist [$tree subwidget hlist]
    $hlist config  -indicator 1 -selectmode single -separator  "^"  -drawbranch 0 
    set lab   [$text subwidget text]
    $lab configure -font $IWOK_GLOBALS(font) -relief flat
    set pthima [tix getimage path]
    set dirima [tix getimage folder]
    set filima [tix getimage textfile]
    set boldstyle [tixDisplayStyle imagetext -fg #000080 -font $IWOK_GLOBALS(boldfont)]
    $tree config -opencmd [list wokPROP:pth:Open $dirima $filima $tree $hlist ] \
	    -browsecmd [list wokPROP:pth:Browse $lab $dirima $filima $tree $hlist ]
    $hlist add ^
    foreach P [lsort [array names env *PATH*]] {
	$hlist add ^${P} -itemtype imagetext -style $boldstyle -text ${P} -image $pthima -data [list PATH $env($P)]
	$tree  setmode ^${P} open
    }
    return
}

proc  wokPROP:pth:Open { dirima filima tree hlist dir  } {
    if {[set children [$hlist info children $dir]] != {}} {
	foreach kid $children {
	    $hlist show entry $kid
	}
    } else {
	set type [lindex [set data [$hlist info data $dir]] 0]
	switch -- $type {
	    PATH {
		set PT [lindex $data 1]
		if { [string match *:* $PT] != 0 } {
		    set lpp [split $PT :]
		} else {
		    set lpp [split $PT  ]
		}
		set i 1
		foreach cc $lpp {
		    if { $cc != {} } {
			if {[string match *^* $cc] == 0 } {
			    $hlist add ${dir}^${cc} -itemtype imagetext -image $dirima \
				    -text [format "#%-2s %s" $i ${cc}] -data [list PTHDIR $cc]
			    $tree  setmode ${dir}^${cc} open
			    incr i
			}
		    }
		}
	    }
	    
	    PTHDIR {
		set pdir [lindex $data 1]
		if ![catch { set lfdir [wokUtils:EASY:readdir [glob -nocomplain $pdir]] }] {
		    foreach f [lsort $lfdir] {
			if ![file isdirectory $pdir/$f] {
			    if {[string match *^* ${f}] == 0 } {
				$hlist add ${dir}^${f} -itemtype imagetext -image $filima \
					-text $f -data [list TERMINAL $pdir/$f]
			    }
			} else {
			    if {[string match *^* ${f}] == 0 } {
				$hlist add ${dir}^${f} -itemtype imagetext -image $filima \
					-text $f -data [list PTHDIR $pdir/$f]
				$tree  setmode ${dir}^${f} open
			    }
			}
		    }
		}
	    }

	}
    }
    return
}


proc  wokPROP:pth:Browse { lab dirima filima tree hlist dir } {
    set type [lindex [set data [$hlist info data $dir]] 0]
    if { "$type" == "TERMINAL" } {
	set location [lindex $data 1]
	if [file exists $location] {
	    catch { unset tt }
	    file lstat $location tt
	    if [file writable $location]   { 
		set wrt yes 
	    } else {
		set wrt no 	
	    }
	    set exe no; if [file executable $location] { set exe yes }
	    set rea no; if [file readable   $location] { set rea yes }
	    set lm  [list \
		    [list separator   1] \
		    [list Location    $location] \
		    [list separator   1] \
		    [list Size        "$tt(size) (bytes)"]\
		    [list Type        $tt(type)]\
		    [list separator   1]\
		    [list Created     [string range [clock format $tt(ctime)] 4 18]]\
		    [list Modified    [string range [clock format $tt(mtime)] 4 18]]\
		    [list Accessed    [string range [clock format $tt(atime)] 4 18]]\
		    [list separator   1]\
		    [list Readable    $rea]\
		    [list Writable    $wrt]\
		    [list Executable  $exe]\
		    ]
	    wokPROP:Nice $lab $lm
	}
    }
    return
}
;#
;#                            ((((((( T A B L E A U   E N V  )))))))
;#
proc wokPROP:env { adr nb page args} {
    global IWOK_GLOBALS
    global env
    set w [$nb subwidget $page]
    frame $w.top -relief sunken -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    tixScrolledText $w.top.text ; set text [$w.top.text subwidget text]
    $text config -font $IWOK_GLOBALS(font) -relief flat 
    button $w.top.sea -text "Search" -command [list wokSEA $text]
    tixForm $w.top.sea -top 0 -left 1
    tixForm $w.top.text -top $w.top.sea -left 1 -right %99 -bottom %99
    set maxl 0
    foreach name [array names env] {
	lappend lpack $name
	if {[string length $name] > $maxl} {
            set maxl [string length $name]
        }
    }
    set maxl [expr {$maxl + 1}]
    foreach name [lsort [array names env]] {
	$text insert end [format "%-*s = %s" $maxl $name $env($name)]\n 
	update
    }
    $text see 1.0
    return
}

proc wokPROP:EDF { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    return
}

proc wokPROP:EDL { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1

    set p1 $w.top
    set p2 $w.top

    set tree [tixTree  $p1.tree]
    frame  $p1.fram -relief flat -bd 1 
    set btn [button $p1.fram.load -text "Contents" -width 6 -command wokPROP:EDL:see -state disabled] 
    pack $btn -expand yes -fill both -padx 8 -pady 70
    set labatt [text $p2.text]

    tixForm  $p1.tree -left 1 -top 1 -right %80 -bottom %50
    tixForm  $p1.fram -left $p1.tree -top 10 -right %99 -bottom $p2.text
    
    tixForm  $p2.text -left 2 -right %99 -bottom %99 -top $p1.tree

    $labatt configure -relief flat -font $IWOK_GLOBALS(font)
    set filima [tix getimage textfile]
    set pthima [tix getimage path]
    set hlist [$tree subwidget hlist]
    $hlist config  -indicator 1 -selectmode single -separator  "^"  -drawbranch 1
    set boldstyle  [tixDisplayStyle imagetext -fg #000080 -font $IWOK_GLOBALS(boldfont)]
    set boldsimple [tixDisplayStyle imagetext -font $IWOK_GLOBALS(boldfont)]
    $tree config -opencmd [list wokPROP:EDL:Open $btn $boldstyle $filima $labatt $tree $hlist] \
	    -browsecmd [list wokPROP:EDL:Browse  $btn $labatt $tree $hlist]
    set nb 0
    foreach P [wokparam -L $location] {
	if { $nb == 0 } {
	    $hlist add ${P} -itemtype imagetext -image $pthima -style $boldstyle -text ${P} \
		    -data [list PATH $P]
	} else {
	    $hlist add ${P} -itemtype imagetext -image $pthima -style $boldsimple -text ${P} \
		    -data [list PATH $P]
	}
	incr nb
	$tree setmode ${P} open
    }

    return
}

proc wokPROP:EDL:Open { btn boldstyle filima att tree hlist dir } {
    $btn configure -state disabled
    if {[set children [$hlist info children $dir]] != {}} {
	foreach kid $children {
	    $hlist show entry $kid
	}
    } else {
	set data [$hlist info data $dir]
	set pdir [lindex $data 1]
	foreach f [lsort [glob -nocomplain $pdir/*.edl]] {
	    set name [file tail $f]
	    if { [string match *_DEFAULT.edl $name] } {
		$hlist add ${dir}^${f} -itemtype imagetext -image $filima -style $boldstyle \
			-text $name  -data [list TERMINAL $f]
	    } else {
		$hlist add ${dir}^${f} -itemtype imagetext -image $filima \
			-text $name  -data [list TERMINAL $f]
	    }
	}
    }
    return
}

proc wokPROP:EDL:Browse { btn att tree hlist dir  } {
    set type [lindex [set data [$hlist info data $dir]] 0]
    if { "$type" == "TERMINAL" } {
	set location [lindex $data 1]
	if [file exists $location] {
	    catch { unset tt }
	    file lstat $location tt
	    if [file writable $location]   { 
		set wrt yes 
	    } else {
		set wrt no 	
	    }
	    set rea no; if [file readable   $location] { set rea yes }
	    set lm  [list \
		    [list separator   1] \
		    [list Location    $location] \
		    [list separator   1] \
		    [list Size        "$tt(size) (bytes)"]\
		    [list Type        $tt(type)]\
		    [list separator   1]\
		    [list Created     [string range [clock format $tt(ctime)] 4 18]]\
		    [list Modified    [string range [clock format $tt(mtime)] 4 18]]\
		    [list Accessed    [string range [clock format $tt(atime)] 4 18]]\
		    [list separator   1]\
		    [list Readable    $rea]\
		    [list Writable    $wrt]\
		    ]
	    wokPROP:Nice $att $lm
	    $btn configure -state active
	    eval "proc wokPROP:EDL:see {} {wokEDF:EditFile $location}"
	}
    }
    return
}
;#
;#                            ((((((( F A C T O R Y    )))))))
;#
proc wokPROP:factory { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    set lm {}
    foreach tp [wokinfo -T $location] {
	if {  ![string match {*%File} [wokinfo -d $tp $location]] } {
	    lappend lm [list $tp [wokinfo -p $tp $location]]
	}
    }
    label   $w.top.ima 
    set img [image create compound -window $w.top.ima]
    $img add image -image [tix getimage factory] ; $img add text -text "     factory"
    $w.top.ima config -image $img

    tixScrolledText  $w.top.msg -scrollbar y
    set txt [$w.top.msg subwidget text]
    $txt configure -relief flat -font $IWOK_GLOBALS(font) 
    wokPROP:Nice $txt $lm 
    tixForm $w.top.ima -top 12 -left 6
    tixForm $w.top.msg -top [list $w.top.ima 20] -left 2
    return
}
;#
;#                            ((((((( W A R E H O U S E  )))))))
;#
proc wokPROP:warehouse { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    set lm {}
    foreach tp [wokinfo -T $location] {
	lappend lm [list $tp [wokinfo -p ${tp}:. $location]]
    }

    label   $w.top.ima 
    set img [image create compound -window $w.top.ima]
    $img add image -image [tix getimage warehouse] ; $img add text -text "  warehouse"
    $w.top.ima config -image $img

    tixScrolledText $w.top.msg -scrollbar y
    set txt [$w.top.msg subwidget text]
    $txt configure -relief flat -font $IWOK_GLOBALS(font) 
    wokPROP:Nice $txt $lm 
    tixForm $w.top.ima -top 12 -left 6
    tixForm $w.top.msg -top [list $w.top.ima 20] -left 2
    return
}

proc wokPROP:parcel { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    set lm {}
    foreach tp [wokinfo -T $location] {
	lappend lm [list $tp [wokinfo -p ${tp}:. $location]]
    }

    label   $w.top.ima 
    set img [image create compound -window $w.top.ima]
    $img add image -image [tix getimage parcel] ; $img add text -text "  parcel"
    $w.top.ima config -image $img

    tixScrolledText $w.top.msg -scrollbar y
    set txt [$w.top.msg subwidget text]
    $txt configure -relief flat -font $IWOK_GLOBALS(font) 
    wokPROP:Nice $txt $lm 
    tixForm $w.top.ima -top 12 -left 6
    tixForm $w.top.msg -top [list $w.top.ima 20] -left 2
    return
}

proc wokPROP:parcelExtRef { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    
    tixPanedWindow $w.top.pane -orient horizontal -paneborderwidth 0 -separatorbg gray50
    pack $w.top.pane -side top -expand yes -fill both -padx 1 -pady 1
    set p1   [$w.top.pane add tree -min 100 -size  240]
    set p2   [$w.top.pane add text]
    
    set tree  [tixTree $p1.tree]
    set text  [text  $p2.text]

    pack $p1.tree -expand yes -fill both -padx 1 -pady 1
    pack $p2.text -expand yes -fill both -padx 1 -pady 1 -padx 3

    set labatt $text
    $labatt configure -relief flat -font $IWOK_GLOBALS(font)

    set hlist [$tree subwidget hlist]
    $hlist config  -indicator 1 -selectmode single -separator  "^"  -drawbranch 1
    set boldstyle [tixDisplayStyle imagetext -fg #000080 -font $IWOK_GLOBALS(boldfont)]

    $tree config -opencmd [list wokPROP:parcelExtRef:Open $labatt $tree $hlist] \
	    -browsecmd [list wokPROP:parcelExtRef:Browse  $labatt $tree $hlist]

    foreach unit [pinfo -a $location] {
	set type [lindex $unit 0]
	set name [lindex $unit 1]
	set full ${location}:${name}
	set path [lindex [lindex [uinfo -Fpl -TEXTERNLIB $full] 0] end]
	if { "$path" != {} } {
	    $hlist add ${name} -itemtype imagetext -style $boldstyle -text ${name} \
		-image $IWOK_GLOBALS(image,$type) -data [list PATH $path $full]
	$tree setmode ${name} open
	}
    }
    return
}

proc wokPROP:parcelExtRef:Open { att tree hlist dir  } {
     if {[set children [$hlist info children $dir]] != {}} {
	foreach kid $children {
	    $hlist show entry $kid
	}
    } else {
	set data [$hlist info data $dir]
	set ext [wokUtils:FILES:FileToList [lindex $data 1]]
	foreach p $ext {
	    $hlist add ${dir}^${p} -itemtype imagetext -text $p -data [list EDLSTRING $p [lindex $data 2]]
	}
    }
    return
}
proc wokPROP:parcelExtRef:Browse { att tree hlist dir } { 
    global IWOK_GLOBALS
    set type [lindex [set data [$hlist info data $dir]] 0]
    switch -- $type {
	EDLSTRING {
	    set edlstring [lindex $data 1]
	    
	    set adr [lindex $data 2] ; set val {} ; catch {set val [wokparam -e %${edlstring} $adr] } ; 
	    set v1  "Value in $adr : \n $val"
	    set ici [wokcd]          ; set wal {} ; catch {set wal [wokparam -e %${edlstring} $ici] }
	    set v2  "Value in $ici : \n $wal"
	    wokReadList $att [list $v1 {} {} $v2]
	}
    }
    return
}
;#
;#                            ((((((( W O R K S H O P  )))))))
;#
proc wokPROP:workshop { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    set lm {}
    
    foreach tp [wokinfo -T $location] {
	lappend lm [list $tp [wokinfo -p ${tp}:. $location]]
    }
    label   $w.top.ima 
    set img [image create compound -window $w.top.ima]
    $img add image -image [tix getimage workshop]  ; $img add text -text "  workshop"
    $w.top.ima config -image $img

    tixScrolledText $w.top.msg -scrollbar y
    set txt [$w.top.msg subwidget text]
    $txt configure -relief flat -font $IWOK_GLOBALS(font) 
    wokPROP:Nice $txt $lm
    tixForm $w.top.ima -top 12 -left 6
    tixForm $w.top.msg -top [list $w.top.ima 20] -left 2
    return
}
proc wokPROP:workshopistuff { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    update
    return
}
;#
;#                            ((((((( W O R K B E N C H )))))))
;#
proc wokPROP:workbench  { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    set lm {}
    
    foreach tp [wokinfo -T $location] {
	lappend lm [list $tp [wokinfo -p ${tp}:. $location]]
    }
    label   $w.top.ima 
    set img [image create compound -window $w.top.ima]
    $img add image -image [tix getimage workbench]  ; $img add text -text "  workbench"
    $w.top.ima config -image $img

    tixScrolledText  $w.top.msg -scrollbar y
    set txt [$w.top.msg subwidget text]
    $txt configure -relief flat -font $IWOK_GLOBALS(font) 
    wokPROP:Nice $txt $lm
    tixForm $w.top.ima -top 12 -left 6
    tixForm $w.top.msg -top [list $w.top.ima 20] -left 2
    return
}


proc wokPROP:workbenchtree { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    set image [tix getimage workbench]
    frame $w.top -relief sunken -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    set tree [tixTree $w.top.tree -options {hlist.separator "^" hlist.selectMode single }]
    set hli [$tree subwidget hlist]
    set lfath [wokWbtree:LoadSons $location [wokinfo -p WorkbenchListFile $location]]
    if { [llength $lfath] == 1 } {
	set father [lindex $lfath 0]
    } elseif { [llength $lfath] > 1 } {
	puts " more than one root in workbench tree"
	set father [lindex $lfath 0]
    }

    $hli add ^
    update
    button $w.top.but -text "Show tree" \
	    -command [list wokWbtree:Tree $tree $hli "" $father $image]
    tixForm $w.top.but -top 2
    tixForm $tree  -top $w.top.but -left 2 -right %99  -bottom %99
    return
}

proc wokPROP:workbenchqq  { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    if { [wokStore:Report:SetQName $location] != {} } {	
	if { [set qdir [wokStore:Report:GetRootName]] != {} } {
	    set text [text $w.top.jnl -relief flat -font $IWOK_GLOBALS(font)]
	    $text insert end "Integration queue in directory: $qdir\n\n"
	    set journal [wokIntegre:Journal:GetName] 
	    if { $journal != {} } {
		set dir [file dirname $journal]
		$text insert end "Journal in directory: $dir\n\n"
		foreach j [wokIntegre:Journal:List] {
		    $text insert end "[format "%15s %-9d" [file tail $j] [file size $j]]\n"
		}
		set t [clock format [file mtime $journal]]
		set str [format "%15s %-8d(Last modified %s)" [file tail $journal] [file size $journal] $t]
		$text insert end "$str\n\n"
		set scoop [wokIntegre:Scoop:Read]
		if { $scoop != {} } {
		    $text insert end "Last integration: \n\n $scoop "
		}
		$text configure -state disabled
		tixForm $text -top 2 -left 2 -bottom %99 -right %99
	    }
	}
    }

    return
}

;#
;#                            ((((((( D E V U N I T )))))))
;#
proc wokPROP:devunit { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    set lm {}
    
    foreach tp [lsort [wokinfo -T $location]] {
	set itm [wokinfo -p ${tp}:. $location]
	if { [file exists $itm] } {
	    lappend lm [list $tp $itm]
	}
    }
    set type [uinfo -t $location]
    label $w.top.ima 
    set img [image create compound -window $w.top.ima]
    $img add image -image $IWOK_GLOBALS(image,$type)  ; $img add text -text "  $type"
    $w.top.ima config -image $img

    tixScrolledText $w.top.msg  -scrollbar y
    set txt [$w.top.msg subwidget text]
    $txt configure -relief flat -font $IWOK_GLOBALS(font) 
    wokPROP:Nice $txt $lm
    
    tixForm $w.top.ima -top 12 -left 6
    tixForm $w.top.msg -top [list $w.top.ima 20] -left 2 -right %99 -bottom %99
    return
}

proc wokPROP:BLD { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1
    
    text $w.top.txt  -fg #000080 -font $IWOK_GLOBALS(boldfont) -relief flat
    foreach string [umake -S $location] {
	$w.top.txt insert end $string\n
    }
    tixForm $w.top.txt -top 0 -left %24 -right %99 -bottom %99
    return
}
;#
;#                            ((((((( T E R M I N A L   )))))))
;#
proc wokPROP:terminal { adr nb page location} {
    global IWOK_GLOBALS
    set w [$nb subwidget $page]
    frame $w.top -relief flat -bd 1 
    pack $w.top -side top -expand yes -fill both -padx 1 -pady 1

    catch { unset tt }
    file lstat $location tt

    if [file writable $location]   { 
	set wrt yes 
	set image [tix getimage textfile]
    } else {
	set wrt no 	
	set image [tix getimage textfile_rdonly]
    }
    set exe no; if [file executable $location] { set exe yes }
    set rea no; if [file readable   $location] { set rea yes }
    set lm  [list \
	    [list separator   1] \
	    [list Location    [file dirname $location]] \
	    [list Name        [file tail $location]] \
	    [list separator   1] \
	    [list Size        "$tt(size) (bytes)"]\
	    [list Type        $tt(type)]\
	    [list separator   1]\
	    [list Created     [string range [clock format $tt(ctime)] 4 18]]\
	    [list Modified    [string range [clock format $tt(mtime)] 4 18]]\
	    [list Accessed    [string range [clock format $tt(atime)] 4 18]]\
	    [list separator   1]\
	    [list Readable    $rea]\
	    [list Writable    $wrt]\
	    [list Executable  $exe]\
	    ]

    label   $w.top.ima -image $image
    set txt [text $w.top.msg]
    $txt configure -relief flat -font $IWOK_GLOBALS(font) 
    wokPROP:Nice $txt $lm
    tixForm $w.top.ima -top 12 -left 6
    tixForm $w.top.msg -top [list $w.top.ima 20] -left 2
    return
}
;#
;#                            ((((((( N O T E B O O K   A D M   )))))))
;#
proc wokPROP:NOT { command adr w name args} {
    tixBusy $w on
    set id [after 10000 tixBusy $w off]
    $command $adr $w $name $args
    after cancel $id
    after 0 tixBusy $w off
    return
}
;#
;#
;#
proc wokPROP:Kill { w } {

    global IWOK_GLOBALS
    wokButton delw [list properties $IWOK_GLOBALS($w,PROP,toplevel)]
    catch { 
	destroy $IWOK_GLOBALS($w,PROP,toplevel) 
	destroy $IWOK_GLOBALS($w,PROP,help)
    }
    return
}
;#
;#
;#
proc wokPROP:UPD { w } {
    return
}
;#
;# Retourne les Edl dans l'adm de location. Pas de test sur location
;#
proc wokPROP:GetAdmEdl { location } {
    if ![catch { set pth [wokinfo -p AdmDir $location] }] {
	return [lsort [glob -nocomplain $pth/*.edl]]
    } else {
	return  {}
    }
}
;#
;#
;#
proc wokPROP:Nice { text lm {state disabled} } {
    set nice [wokUtils:EASY:NiceList $lm :]
    $text configure -state normal
    $text delete 0.0 end
    foreach string [split $nice \n] {
	$text insert end $string\n
    }
    $text see 1.0
    update
    $text configure -state $state
    return
}



