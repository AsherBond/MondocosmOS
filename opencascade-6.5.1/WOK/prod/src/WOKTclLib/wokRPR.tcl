proc wokUpdateRepository { {loc {}} } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS

    if { $loc == {} } {
	set verrue [wokCWD readnocell]
    } else {
	regexp {(.*):Repository} $loc all verrue 
    }
    
    if ![wokinfo -x $verrue] {
	wokDialBox .wokcd {Unknown location} "Location $verrue is unknown" {} -1 OK
	return
    }
    set fact [wokinfo -f $verrue]
    set shop [wokinfo -s $verrue]
    set curwb [wokinfo -w $verrue]

   

    set w [wokTPL rpr${verrue}]
    if [winfo exists $w ] {
	wm deiconify $w
	raise $w
	return 
    }

    if { [wokStore:Report:SetQName $curwb] == {} } {
	return
    }
    
    set type [wokIntegre:BASE:InitFunc]

    toplevel    $w
    wm title    $w "Repository of $curwb ."
    wm geometry $w 1124x658+135+92
    wokButton setw [list Rpr_close $w]

    tixBusy $w on
    update

    menubutton $w.file -menu $w.file.m -text File -underline 0 -takefocus 0
    menu $w.file.m  ; $w.file.m add command -label "Close     " -underline 1 -command [list wokRPRExit $w]

    menubutton $w.help -menu $w.help.m -text Help -underline 0 -takefocus 0
    menu $w.help.m
    $w.help.m add command -label "Help"      -underline 1 -command [list wokRPRHelp $w]
    
    menubutton $w.tools -menu $w.tools.m -text Tools  -underline 0 -takefocus 0
    menu $w.tools.m 
    $w.tools.m add command -label "Check out" -state disabled -command [list wokRPRCheckout $w]
    $w.tools.m add command -label "To editor" -state disabled -command [list wokRPREditor $w]
    $w.tools.m add command -label "More Diff" -state disabled -command [list wokRPRxdiff $w]
    $w.tools.m add command -label "Search"    -state disabled -command [list wokRPRSearch $w]

    menubutton $w.marks -menu $w.marks.m -text Marks  -underline 0 -takefocus 0
    menu $w.marks.m 
    $w.marks.m add checkbutton -label "Display" -variable IWOK_WINDOWS($w,markdisplay) 

    menubutton $w.admin -menu $w.admin.m -text Admin  -underline 0 -takefocus 0
    menu $w.admin.m 
    $w.admin.m add command -label "Show params"    -underline 0 -command [list wokRPRShowVersions $w]
    $w.admin.m add command -label "Check contents" -underline 0 -command [list wokRPRCheckItem $w]
    $w.admin.m add command -label "Delete element" -underline 0 -command [list wokRPRDeleteItem $w]

    frame $w.top -relief sunken -bd 1 
    label $w.lab -relief raised  

    tixPanedWindow $w.top.pane -orient horizontal  -paneborderwidth 0 -separatorbg gray50
    pack $w.top.pane -side top -expand yes -fill both -padx 1 -pady 1

    set p0 [$w.top.pane add tree -min 70 -size 240]
    set p1 [$w.top.pane add scrw -min 60 -size 180]
    set p2 [$w.top.pane add text -min 70]
    
    set tree  [tixTree $p0.tree -options {separator "^" hlist.selectMode single }]
    $tree config \
	    -command   "wokRPRBrowse $w $tree run" \
	    -browsecmd "wokRPRBrowse $w $tree browse" \
	    -opencmd   "wokFillUnit $w $tree"

    tixScrolledWindow  $p1.scrw 
    set windo [$p1.scrw subwidget window]
    canvas $windo.c 
    set canva $windo.c

    tixScrolledText  $p2.text 
    set texte [$p2.text subwidget text] 
    $texte config -font  $IWOK_GLOBALS(font)

    pack $p0.tree -expand yes -fill both -padx 1 -pady 1
    pack $p1.scrw -expand yes -fill both -padx 1 -pady 1
    pack $p2.text -expand yes -fill both -padx 1 -pady 1 

    tixForm $w.file ; tixForm $w.help -right -2
    tixForm $w.tools -left $w.file
    tixForm $w.marks -left $w.tools
    tixForm $w.admin -left $w.marks
    tixForm $w.top   -top $w.file -left 1 -right %99 -bottom $w.lab
    tixForm $w.lab   -left 1 -right %99 -bottom %99

    set IWOK_WINDOWS($w,menu)    $w.file.m
    set IWOK_WINDOWS($w,tools)   $w.tools.m
    set IWOK_WINDOWS($w,admin)   $w.admin.m
    set IWOK_WINDOWS($w,label)   $w.lab
    set IWOK_WINDOWS($w,tree)    $tree
    set IWOK_WINDOWS($w,hlist)   [set hlist  [$tree subwidget hlist]]
    set IWOK_WINDOWS($w,text)    $texte
    set IWOK_WINDOWS($w,canvas)  $canva
    set IWOK_WINDOWS($w,fact)    $fact
    set IWOK_WINDOWS($w,shop)    $shop
    set IWOK_WINDOWS($w,journal) [wokIntegre:Journal:GetName]
    set IWOK_WINDOWS($w,qroot)   [wokIntegre:BASE:GetRootName]
    set IWOK_WINDOWS($w,data)    {}


    catch {
	wokIntegre:Mark:NiceDump $IWOK_WINDOWS($w,journal) tt
	wokUtils:EASY:MAD IWOK_WINDOWS $w,mark tt
	set IWOK_WINDOWS($w,lmark) [array exists tt]
    }

    set IWOK_WINDOWS($w,markdisplay) 0

    set IWOK_GLOBALS(repository,popup)      [tixPopupMenu $w.p -title "Select" ]
    $w.p  subwidget menubutton configure    -font $IWOK_GLOBALS(font) 
    set IWOK_GLOBALS(repository,popup,menu) [$IWOK_GLOBALS(repository,popup) subwidget menu]
    $IWOK_GLOBALS(repository,popup,menu)    configure -font $IWOK_GLOBALS(font) 

    set LB [wokIntegre:BASE:LS]
    set V  [wokIntegre:Version:Get]
    set R  $IWOK_WINDOWS($w,qroot)

    foreach d  $LB {
	set B [lindex $d 0]
	set T [lindex $d 1]
	$hlist add ${B}${T} -itemtype imagetext -text $B \
		-image $IWOK_GLOBALS(image,[string index $T 1]) \
		-data [list $R $B $T $V]
	$tree setmode ${B}${T} open
    }


    set lewb {}
    set llitm [linsert $IWOK_GLOBALS(ucreate-P)  0 [list All All..]]
    if { "[wokinfo -t $verrue]" == "workbench" } {
	set llitm  [linsert $llitm 0 [list You Yours..]]
	set lewb [w_info -l $verrue]
    }

    foreach t $llitm {
	$IWOK_GLOBALS(repository,popup,menu) add command -label [lindex $t 1]\
		-command [list wokRprFilterdevunit $tree $hlist [lindex $t 0] $LB $V $R $lewb]
    }
    $IWOK_GLOBALS(repository,popup) bind $hlist
    tixBusy $w off
    return
}
#
# met list dans hlist en filtrant avec t, V version et R root
#
proc wokRprFilterdevunit { tree hlist t list V R lewb} {
    global IWOK_GLOBALS
    $hlist delete all
    if { "$t" != "You" } {
	foreach d $list {
	    set B [lindex $d 0]
	    set T [lindex $d 1]
	    if { "$t" != "All" } {
		set ext [lindex $d 1]
		if { "$ext" == ".$t" } {
		    $hlist add ${B}${T} -itemtype imagetext -text $B \
			    -image $IWOK_GLOBALS(image,[string index $T 1]) -data [list $R $B $T $V]
		    $tree setmode ${B}${T} open
		}
	    } else {
		$hlist add ${B}${T} -itemtype imagetext -text $B \
			-image  $IWOK_GLOBALS(image,[string index $T 1]) -data [list $R $B $T $V]
		$tree setmode ${B}${T} open
	    }
	}
    } else {
	foreach d $list {
	    set B [lindex $d 0]
	    if { [lsearch $lewb $B] != -1 } {
		set T [lindex $d 1]
		$hlist add ${B}${T} -itemtype imagetext -text $B \
			-image $IWOK_GLOBALS(image,[string index $T 1]) -data [list $R $B $T $V]
		$tree setmode ${B}${T} open
	    }
	}
    }
    return
}
#
# appelee a l'ouverture d'un item: Le remplit s'il est vide et montre les fils
#
proc wokFillUnit { w tree ent } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS
    set hlist [$tree subwidget hlist]
    if {[$hlist info children $ent] == {}} {
	set data [$hlist info data $ent] ;# R B T V
	set R $IWOK_WINDOWS($w,qroot)
	;#set R [lindex $data 0]
	set B [lindex $data 1]
	set T [lindex $data 2]
	set V [lindex $data 3]
	set dir $R/${B}${T}
	set LSF [wokIntegre:BASE:List $B $V]
	set txtima [tix getimage textfile]
	foreach s $LSF {
	    set sfile $dir/[wokIntegre:BASE:ftos $s $V]
	    $hlist add ${B}${T}^${s} -itemtype imagetext -text $s -image $txtima -data $sfile
	}
    }
    foreach kid [$hlist info children $ent] {
	$hlist show entry $kid
    }
    return
}

#
# appelee quand on brouze la liste.
#
proc wokRPRBrowse {  w slb action args } {
    global IWOK_WINDOWS

    set hlist [$slb subwidget hlist]
    set ent   [$hlist info anchor]

    if {$ent == ""} {
	return
    }

    set kid [$hlist info children $ent]
    if {$kid != {} } {
	;#puts "HEADER"
	;# un Unit header pour l'instant rien a faire
	return
    } else {
	;# un fils donc un sfile peut aussi etre /home/wb/kl/KERNEL/SCCS/SCCS/s.BASES DBT .p {}
	set sfile [$hlist info data $ent]
	if { $sfile == "" || [llength $sfile] != 1 } {
	    return
	}
    }

    case $action {
	"run" {
	    ;#double clique 
	}

	"browse" {
	    wokSetCanv $w
	}
    }
    return
}

proc wokRPRShowVersions { w } {
    global IWOK_WINDOWS

    puts " not yet implemented"
    return
}

;#
;# Affiche un historique en X Y 
;#
proc wokUpdateHist1 { w infile XIN YIN } {
    global IWOK_WINDOWS
    set Canv $IWOK_WINDOWS($w,canvas)

    catch { unset FILS }
    set root [wokIntegre:BASE:tree $infile FILS]
    $Canv delete all

    set X $XIN
    set Y $YIN
    set mx 0
    set my 0
    set t 28

    set lastitl {}
    set IWOK_WINDOWS($w,vlabels) {} 
    lappend IWOK_WINDOWS($w,vlabels) [list 1 [expr $XIN +20 ] [expr $YIN + 10 ]]
    while 1 {
	set dat [lindex $root 0]
	set lab [lindex $dat 0]
	set nxt [lindex $root 1]
	set cmt [lindex $dat 1]
	
	wokArtVrs 
	set lastitl $curitl
	if ![info exists FILS($nxt)] { break }
	set root [lindex $FILS($nxt) 0]

    }
    lappend IWOK_WINDOWS($w,vlabels) [list 9999 9999 9999]
    set IWOK_WINDOWS($w,vlablen) [expr [llength $IWOK_WINDOWS($w,vlabels)] - 1 ]

    if {$IWOK_WINDOWS($w,markdisplay) == 1} {
	if { [wokCheckLabels $IWOK_WINDOWS($w,vlabels)] == 0 } {
	    wokDialBox .badnews {Bad news} \
		    "Incoherent archive file format. Unable to place marks for this file" {} -1 OK
	} else {
	    if { $IWOK_WINDOWS($w,lmark) == 1 } {
		foreach xn [array names IWOK_WINDOWS $w,mark,*] {
		    set xrk [split $IWOK_WINDOWS($xn) ,]
		    set maxt [wokArtMark $w [lindex [split $xn ,] 2] [lindex $xrk 0] [lindex $xrk 1] ]
		    if { $maxt > $mx } {
			set mx $maxt
		    }
		}
	    }
	}   
    }
    $Canv configure -width $mx -height $my
    pack $Canv 
    update 
    return
}
;#
;# verifie que list est croissante/ 1 er element. list { {a b c} {a b c }.. } Contient au moins 2 elements.
;# 
proc wokCheckLabels { list } {
    set ll [llength $list]
    for {set i 0} {$i < $ll } {incr i 1} { 
	set n1 [lindex $list $i]
	set n2 [lindex $list [expr $i + 1 ]]
	if { $n1 != {} && $n2 != {} } {
	    if { [lindex $n1 0] > [lindex $n2 0] } {
		return 0
	    }
	}
    }
    return 1
}
;#
;# dessine mrk. remonte mx my pour config du canvas
;#
proc wokArtMark { w txt mrk dat} {
    global IWOK_WINDOWS
    set c $IWOK_WINDOWS($w,canvas)
    for {set i 0} {$i < $IWOK_WINDOWS($w,vlablen)} {incr i 1} { 
	;#puts "i = $i ip1 = [expr $i+1]"
	set binf [lindex $IWOK_WINDOWS($w,vlabels) $i] 
	set bsup [lindex [lindex $IWOK_WINDOWS($w,vlabels) [expr $i+1]] 0]
	;#puts "binf = $binf bsup = $bsup"
	if { [lindex $binf 0] <= $mrk && $mrk < $bsup } {
	    set b1 [lindex $binf 1]
	    set b2 [lindex $binf 2]
	    set ttx  [$c create text $b1 $b2 -text $txt -anchor w -tag [list MRK $txt $mrk $dat]]
	    set bbl  [$IWOK_WINDOWS($w,canvas) bbox $ttx]
	    set x2 [lindex $bbl 2]
	    $c create rectangle [lindex $bbl 0] [lindex $bbl 1] $x2 [lindex $bbl 3] -fill yellow
	    $c raise $ttx
	    set hhx  [expr  $x2 + 10]
	    set IWOK_WINDOWS($w,vlabels) \
		    [lreplace $IWOK_WINDOWS($w,vlabels) $i $i [list [lindex $binf 0] $hhx [lindex $binf 2]]]
	    return $hhx 
	}
    }
}
;#
;# evaluee dans wokUpdateHist1 
;#
proc wokArtVrs { } {
    uplevel {
	regexp {([0-9]*)\..*} $lab all bn
	set col black
	set itx [$Canv create text $X $Y -text $lab -fill $col -tag [list LAB $lab] -anchor n] 
	$Canv bind $itx <Any-Enter> {catch {%W configure -cursor {hand2 red white}}}
	set lxy [$Canv bbox $itx]
	set x1  [lindex $lxy 0]; set y1 [lindex $lxy 1] ; set x2 [lindex $lxy 2]; set y2 [lindex $lxy 3]
	set itr [$Canv create rectangle $x1 $y1 $x2 $y2 -fill grey -tag [list RECT R${lab}]]
	$Canv raise $itx
	set midx [expr $x1 + ($x2-$x1)/2]
	set curitl [$Canv create line $midx $y2 $midx [expr $y2 +$t]  -arrow last]
	if { $lastitl != {} } {
	    $Canv itemconfigure $lastitl -tag [list CMT $cmt]
	    set nbr [lindex [wokIntegre:Journal:UnMark $cmt] 1]
	    set nbr [lindex [split $nbr _] 0]    ;# pour les vieux comments de DPE
	    lappend IWOK_WINDOWS($w,vlabels) [list $nbr [expr $midx + 20 ] [expr $y1 + ($y2-$y1)/2]]
	}
	set lastitl $curitl
	set Y [expr $y2 + $t]
	set mx $x2
	set my $y2
    }
}
;#
;# Configure le label, canvas et le texte pour l'item selectionne
;#
proc wokSetCanv { w } { 
    global IWOK_WINDOWS
    global IWOK_GLOBALS

    set sfile [wokRPRGetSfile $w] ;# variable dans les bind
    if { $sfile == {} } {
	return
    }
    
    set canv $IWOK_WINDOWS($w,canvas) ;#
    set text $IWOK_WINDOWS($w,text)   ;# constant par rapport a w 
    set lab  $IWOK_WINDOWS($w,label)  ;#

    $IWOK_WINDOWS($w,tools) entryconfigure 1 -state active
    $IWOK_WINDOWS($w,tools) entryconfigure 2 -state active
    $IWOK_WINDOWS($w,tools) entryconfigure 4 -state active

    $canv delete all
    catch {unset v1 v2}
    wokUpdateHist1 $w $sfile 14 20 
    $text delete 0.0 end
    wokReadString $text [wokIntegre:BASE:cat $sfile last]
    set vrs [wokIntegre:BASE:vrs $sfile]
    set dta [clock format [file mtime $sfile] -format "%d %h %y %R" ]
    set item [wokIntegre:BASE:stof [file tail $sfile] {}]
    set fmt [format "FILE: %--30s Version: %--10s Last registered: %--15s" $item $vrs $dta]
    $lab configure -text $fmt -font $IWOK_GLOBALS(font)
    set IWOK_WINDOWS($w,data) $vrs
    bind $canv <Button-1> {
	set w [winfo toplevel %W]
	set info [%W gettags current]
	set nat [lindex $info 0]
	if {[string compare $nat LAB] == 0} {
	    set vrs [lindex $info 1]
	    foreach e [%W find all] {
		set lt [%W gettags $e]
		if {[lsearch $lt RECT] != -1} {
		    %W itemconfigure $e -outline black -width 1
		    if {[lsearch $lt R${vrs}]!= -1} {
			%W itemconfigure $e -outline red -width 2
		    }
		}
	    }
	    set ts [wokRPRGetSfile $w]
	    $IWOK_WINDOWS($w,label) configure -text "File [wokIntegre:BASE:stof $ts {} ] ($vrs)"
	    wokReadString $IWOK_WINDOWS($w,text) [wokIntegre:BASE:cat $ts $vrs]
	    set IWOK_WINDOWS($w,data) $vrs
	}

	if {[string compare $nat CMT] == 0} {
	    tixBusy $w on
	    update
	    set _x [wokIntegre:Journal:UnMark [lindex $info 1]]
	    set _j [wokIntegre:Journal:GetSlice [set _n [lindex $_x 1]]]
	    
	    wokReadList $IWOK_WINDOWS($w,text) \
		    [wokIntegre:Journal:PickMultReport $_j ${_n} ${_n}]
	    wokFAM $IWOK_WINDOWS($w,text) {^-- } { $IWOK_WINDOWS($w,text) tag add big first last }
	    $IWOK_WINDOWS($w,text) tag configure big -background orange -foreground black -borderwidth 2 \
	    -font -Adobe-Helvetica-Medium-R-Normal--*-120-* -relief raised
	    $IWOK_WINDOWS($w,text) see end
	    $IWOK_WINDOWS($w,label) configure -text [$IWOK_WINDOWS($w,text) get 1.0 1.end]
	    catch { unset _x _n _j}
	    tixBusy $w off
	}

	if {[string compare $nat MRK] == 0} {
	    wokReadList $IWOK_WINDOWS($w,text) \
		    [wokIntegre:Mark:GetComment $IWOK_WINDOWS($w,journal) [lindex $info 1]]
	    $IWOK_WINDOWS($w,label) configure \
		    -text "Mark to integration [lindex $info 2]. Placed on [clock format [lindex $info 3]]"
	}

    }
    
    bind $canv <Control-Button-1> {
	set w [winfo toplevel %W]
	set info [%W gettags current]
	if ![info exists v1] {
	    set v1 [lindex $info 1]
	    foreach e [%W find all] {
		set lt [%W gettags $e]
		if {[lsearch $lt RECT] != -1} {
		    %W itemconfigure $e -outline black -width 1
		    if {[lsearch $lt R${v1}]!= -1} {
			%W itemconfigure $e -outline red -width 2
		    }
		}
	    }
	    
	} else {
	    if ![info exists v2] {
		set v2 [lindex $info 1]
		foreach e [%W find all] {
		    if {[lsearch [%W gettags $e] R${v2}]!= -1} {
			%W itemconfigure $e -outline red -width 2
		    }
		}
		set ts [wokRPRGetSfile $w]
		wokReadString $IWOK_WINDOWS($w,text) [wokIntegre:BASE:diff $ts $v1 $v2]
		$IWOK_WINDOWS($w,label) configure -text "Differences  ($v1) <=> ($v2)"
		if [wokUtils:EASY:INPATH xdiff] {
		    $IWOK_WINDOWS($w,tools) entryconfigure 3 -state active
		    set IWOK_WINDOWS($w,data) [list $ts $v1 $v2]
		}
	    }
	    unset v1 v2
	}
    }
    return
}
;#
;# recupere le sfile en cours
;#
proc wokRPRGetSfile { w } {
    global IWOK_WINDOWS
    set hlist $IWOK_WINDOWS($w,hlist)
    catch { set anchor [$hlist info anchor] }
    if { $anchor != {} } {
	set sfile [$hlist info data $anchor]
	if [file exists $sfile] {
	    return $sfile
	}
    }
    return {}
}
;#
;# Sort un fichier de la base dans le repertoire courant
;#
proc wokRPRCheckout  { w } {
    global IWOK_WINDOWS
    set sfile [wokRPRGetSfile $w]
    set vrs $IWOK_WINDOWS($w,data)
    if ![ file exists $sfile ] {
	return
    }
    set wf [pwd]/$vrs,[wokIntegre:BASE:stof [file tail $sfile] {}]
    if ![catch { wokUtils:FILES:ListToFile [split [wokIntegre:BASE:cat $sfile $vrs] \n] $wf } status] { 
	$IWOK_WINDOWS($w,label) configure -text  "File $wf has been created"
    } else {
	wokDialBox .nowrite {Cannot write file} $status {} -1 OK
    }
    return
}
;#
;#
;#
proc wokRPRSearch { w } {
    global IWOK_WINDOWS
    wokSEA $IWOK_WINDOWS($w,text)
    return
}
;#
;#
;#
proc wokRPREditor { w } {
    global IWOK_WINDOWS
    set sfile [wokRPRGetSfile $w]
    if [file exists $sfile] {
	set f [wokIntegre:BASE:stof [file tail $sfile] {}]
	set vrs $IWOK_WINDOWS($w,data)
	set file "/tmp/$vrs,${f}"
	wokUtils:FILES:ListToFile [split [wokIntegre:BASE:cat $sfile $vrs] \n] $file
	wokEDF:EditFile $file 
    }
    return
}

proc wokRPRxdiff { w } {
    global IWOK_WINDOWS
    set ts [lindex $IWOK_WINDOWS($w,data) 0]
    set f [wokIntegre:BASE:stof [file tail [lindex $IWOK_WINDOWS($w,data) 0]] {}]
    set v1 [lindex $IWOK_WINDOWS($w,data) 1]
    set v2 [lindex $IWOK_WINDOWS($w,data) 2]
    set f1 "/tmp/$v1,$f"
    set f2 "/tmp/$v2,$f"
    wokUtils:FILES:ListToFile [split [wokIntegre:BASE:cat $ts $v1] \n] $f1
    wokUtils:FILES:ListToFile [split [wokIntegre:BASE:cat $ts $v2] \n] $f2
    catch {exec xdiff $f1 $f2 &}
    return
}



proc wokRPRDeleteItem { w } {
    global IWOK_WINDOWS
    set hlist $IWOK_WINDOWS($w,hlist)

    set len [llength [set lstent [split [set entry [$hlist info anchor]] ^]]]
    
    if { $len == 0 } {
	return
    } elseif { $len ==  1 } {
	set unit [lindex $lstent 0]
	tixBusy $w on
	wokIntegre:BASE:Delete  $unit
	tixBusy $w off
    } elseif { $len  >  1 } {
	set unit [lindex $lstent 0]
	set item [lindex $lstent 1]
	set data [$hlist info data [$hlist info parent $entry]]
	set vrs [lindex $data 2]
	catch { wokUtils:FILES:delete $IWOK_WINDOWS($w,qroot)/$unit/[wokIntegre:BASE:ftos $item $vrs] }
    }
    $hlist delete entry $entry
    $IWOK_WINDOWS($w,canvas) delete all
    $IWOK_WINDOWS($w,text)   delete 1.0 end
    return
}
proc wokRPRCheckItem { w } {
    global IWOK_WINDOWS
    set hlist $IWOK_WINDOWS($w,hlist)

    set len [llength [set lstent [split [set entry [$hlist info anchor]] ^]]]
    
    if { $len == 0 } {
	return
    } elseif { $len ==  1 } {
	set unit [lindex $lstent 0]
	tixBusy $w on
	update
	$IWOK_WINDOWS($w,text) delete 0.0 end
	set dir $IWOK_WINDOWS($w,qroot)/$unit
	set lst {}
	catch { set lst [wokUtils:EASY:readdir $dir] }
	foreach sfile [lsort $lst] {
	    if [wokIntegre:BASE:IsElm $sfile] {
		set stat [wokIntegre:BASE:check $dir/$sfile]
		if { $stat != {} } {
		    $IWOK_WINDOWS($w,text) insert end "$stat \n"
		} else {
		    $IWOK_WINDOWS($w,text) insert end "File OK: $dir/$sfile \n"
		}
	    }
	    $IWOK_WINDOWS($w,text) see end
	    update
	}
	tixBusy $w off
    } elseif { $len  >  1 } {
	set unit [lindex $lstent 0]
	set item [lindex $lstent 1]
	set data [$hlist info data [$hlist info parent $entry]]
	set vrs [lindex $data 2]
	$IWOK_WINDOWS($w,canvas) delete all
	set sfile $IWOK_WINDOWS($w,qroot)/$unit/[wokIntegre:BASE:ftos $item $vrs]
	set stat [wokIntegre:BASE:check $sfile]
	if { $stat != {} } {
	    wokReadString $IWOK_WINDOWS($w,text) "$stat"
	} else {
	    wokReadString $IWOK_WINDOWS($w,text) "File OK: $sfile"
	}
    }
    return
}

proc wokRPRExit { w } {
    global IWOK_WINDOWS
    destroy $w
    wokUtils:FILES:delete [glob -nocomplain /tmp/jnltmp[pid].*]
    if [info exists IWOK_WINDOWS($w,help)] {
	catch {destroy $IWOK_WINDOWS($w,help)}
    }
    wokButton delw [list Rpr_close $w]
    return
}


proc wokPrepareCheckWithRPR { w } {
    global IWOK_WINDOWS
    $IWOK_WINDOWS($w,hlist) delete all
    $IWOK_WINDOWS($w,text) delete 1.0 end
    msgsetcmd wokMessageInText $IWOK_WINDOWS($w,text)
    tixBusy $w on
    update
    foreach item [$IWOK_WINDOWS($w,hlist2) info children] {
	set ud $IWOK_WINDOWS($w,shop):$IWOK_WINDOWS($w,WBFils):[lindex $item 1]
	set root [wokIntegre:BASE:GetRootName]/[lindex $item 1].[uinfo -c ${ud}]
	wcheck -diff  [uinfo -plTsource $ud] -dir $root
    }
    tixBusy $w off
    msgunsetcmd
    return
}



;#
;# Help du repository
;#
proc wokRPRHelp { w } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS
    global env

    set IWOK_WINDOWS($w,help) [set wh .wokRPRHelp]
    if {[info exist IWOK_GLOBALS(windows)]} {
	if {[lsearch $IWOK_GLOBALS(windows) $wh ] == -1} {
	    lappend IWOK_GLOBALS(windows) $wh 
	}
    }

    set whelp [wokHelp $wh "About sources repository"]
    set texte [lindex $whelp 0] ; set label [lindex $whelp 1]
    wokReadFile $texte  $env(WOK_LIBRARY)/wokRPRHelp.hlp
    wokFAM $texte <.*> { $texte tag add big first last }
    $texte tag configure big -background Bisque3 -foreground black -borderwidth 2 \
	    -font -Adobe-Helvetica-Medium-R-Normal--*-120-* -relief raised
    update
    $texte configure -state disabled
    return
}
