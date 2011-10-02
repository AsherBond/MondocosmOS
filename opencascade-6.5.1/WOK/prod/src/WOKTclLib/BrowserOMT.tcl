proc BrowserOMTDestroyWin {win} {
    global Browser_Menu Browser_packinfo BrowserOMT_clarray BrowserOMT_maxy

    set swin [$win.swin subwidget window]
    $swin.can delete all
    destroy $win
    $Browser_Menu.windows.options delete $Browser_packinfo(womt)
}

# display a graph from class <classe> in a 
# toplevel window named $win.womt
#
# win      : a window
# classe   : a class full name
#
proc BrowserOMTInitWindow {win classe disp} {
    global Browser_Menu Browser_packinfo BrowserOMT_clarray BrowserOMT_maxy 

    if {$classe != "this"} {
	set BrowserOMT_clarray(root) $classe
    } else {
	set classe $BrowserOMT_clarray(root)
    }
    
    if {[winfo exist $win.womt] == 0} {
	toplevel $win.womt
	$Browser_Menu.windows.options add command -label "Graphic" -command "raise $win.womt"
	set Browser_packinfo(womt) [$Browser_Menu.windows.options index last]
	wm title $win.womt "Graph"
	wm geometry $win.womt 600x600+100+100

	tixScrolledWindow  $win.womt.swin
	
	button $win.womt.menubar -state disabled -relief raise
	menubutton $win.womt.menubar.menu1 -menu $win.womt.menubar.menu1.options -text "File"	
	menu $win.womt.menubar.menu1.options
	$win.womt.menubar.menu1.options add command -label "PostScript"   -command "BrowserOMTPostScript $win.womt"
	$win.womt.menubar.menu1.options add command -label "Close"   -command "BrowserOMTDestroyWin $win.womt"

	tixForm $win.womt.menubar -top 2 -left 0 -right -0
	tixForm $win.womt.menubar.menu1 -left 0 -top 0
	tixForm $win.womt.swin -left 0 -top $win.womt.menubar -right -0 -bottom -0

	set swin [$win.womt.swin subwidget window]
	canvas $swin.can
	pack $swin.can
    }

    set swin [$win.womt.swin subwidget window]
    $swin.can delete all

    wm title $win.womt "Graph : $classe"

    set BrowserOMT_maxy {}
    lappend BrowserOMT_maxy 5.0
    lappend BrowserOMT_maxy 5.0
    $swin.can configure -width 100.0 -height 100.0
    BrowserOMTDrawBox $swin.can $classe [lindex $BrowserOMT_maxy 0] [lindex $BrowserOMT_maxy 1] $classe
    BrowserOMTSetupCanva $swin.can

    BrowserOMTInitScrollBar $win.womt.swin

    unset BrowserOMT_clarray
    unset BrowserOMT_maxy
    set BrowserOMT_clarray(root) $classe
}

proc BrowserOMTSetupCanva {c} {
    global BrowserOMT_maxy

    set mx [expr {[lindex $BrowserOMT_maxy 0] + 10.0}]
    set my [lindex $BrowserOMT_maxy 1]

    if {$mx > $my} {
	set r [expr {$my / $mx}]

	if {$r < 0.65} {
	    set my [expr {$mx * 0.65}]
	}
    } elseif {$my > $mx} {
	set r [expr {$mx / $my}]

	if {$r < 0.65} {
	    set mx [expr {$my * 0.65}]
	}
    }
    $c configure -height $my -width $mx
#    puts "$mx $my"
}

proc BrowserOMTPostScript {win} {
    global BrowserOMT_printer BrowserOMT_printerok BrowserOMT_printerrotate

    set BrowserOMT_printerrotate 0

    toplevel $win.printer
    label $win.printer.label -text "Printer name :"
    entry $win.printer.entry -width 20 -relief sunken -bd 2 -textvariable BrowserOMT_printer
    button $win.printer.ok -text "Ok"
    button $win.printer.cancel -text "Cancel"
    checkbutton $win.printer.rotate -text "Rotate" -variable  BrowserOMT_printerrotate
    tixForm $win.printer.label -top 4 -left 4
    tixForm $win.printer.entry -top 4 -left $win.printer.label -right -4
    tixForm $win.printer.rotate -top $win.printer.entry  -left $win.printer.ok -right $win.printer.cancel
    tixForm $win.printer.ok -top $win.printer.entry -bottom -0 -left 0
    tixForm $win.printer.cancel -top $win.printer.entry -bottom -0 -right -0

    set BrowserOMT_printerok -1
    
    bind $win.printer.entry <Return> {
	focus -force [winfo toplevel %W].ok
    }
    bind $win.printer.ok <Return> {
	set BrowserOMT_printerok 1
    }
    bind $win.printer.ok <ButtonRelease-1> {
	set BrowserOMT_printerok 1
    }
    bind $win.printer.cancel <ButtonRelease-1> {
	set BrowserOMT_printerok 0
    }
    tixBusy $win on
    tkwait variable BrowserOMT_printerok
    tixBusy $win off

    if {$BrowserOMT_printerok == 1} {
	set swin [$win.swin subwidget window]
	$swin.can postscript -file "/tmp/can.ps" -rotate $BrowserOMT_printerrotate -pageheight 28.5c -pagewidth 18.5c
	catch {exec lpr -P$BrowserOMT_printer /tmp/can.ps}
    }

    destroy $win.printer
}

proc BrowserOMTToggleArrow {win type} {
    set swin [$win.swin subwidget window]

    if {$type == "I"} {
	set color [$swin.can itemcget I -fill]

	if {$color == ""} {
	    $swin.can itemconfigure I -fill grey40
	} else {
	    $swin.can itemconfigure I -fill "" 
	}
    } else {
	set color [$swin.can itemcget C -fill]

	if {$color == ""} {
	    $swin.can itemconfigure C -fill black
	} else {
	    $swin.can itemconfigure C -fill "" 
	}
    }
}

proc BrowserOMTGetMax {lmax l} {
    set res {}

    if {[lindex $lmax 0] < [lindex $l 0]} {
	lappend res [lindex $l 0]
    } else {
	lappend res [lindex $lmax 0]
    }
    
    if {[lindex $lmax 1] < [lindex $l 1]} {
	lappend res [lindex $l 1]
    } else {
	lappend res [lindex $lmax 1]
    }

    return $res
}

proc BrowserOMTDrawBox {where classe x y tag} {
    global BrowserOMT_clarray BrowserOMT_maxy

    set error [catch {msclinfo -t $classe}]

    if {$error != 0} return

    set incomplete [msclinfo -e $classe]

    if {[info exist BrowserOMT_clarray($tag)]} 	return

    set fnt "-adobe-helvetica-medium-r-normal--12-120-75-75-p-67-iso8859-1"
    set x1 $x
    set y1 $y
    set x2 $x
    set y2 $y
    set inherits {}
    set hasinh   0
    set backcolor "yellow"
    set thisbackcolor "yellow"

    if {$incomplete == 0} {
	if {$BrowserOMT_clarray(root) == $classe} {
	    set inherits [msclinfo -i $classe]
	    set hasinh [llength $inherits]
	}
	set backcolor "black"
	set thisbackcolor "black"
    }
    
    if {$hasinh} {
	set l {}
	lappend l $x
	lappend l $y
	set BrowserOMT_maxy [BrowserOMTGetMax $BrowserOMT_maxy $l]
	BrowserOMTSetupCanva $where
	
	set p [lindex $inherits 0]
	set rwidth [BrowserOMTDrawClass $where  $p $x $y $p 0]
	set x1 [lindex $rwidth 0]
	set y1 [lindex $rwidth 1]
	set x2 [lindex $rwidth 2]
	set y2 [lindex $rwidth 3]
	set xinh [expr {([lindex $BrowserOMT_clarray($p) 2] + [lindex $BrowserOMT_clarray($p) 0]) / 2.0}]
	set yinh [lindex $BrowserOMT_clarray($p) 3]
    }

    if {![info exist BrowserOMT_clarray($tag)]} {
	set thisclass 0

	if {$BrowserOMT_clarray(root) == $classe} {
	    set thisclass 1
	}
	set rwidth [BrowserOMTDrawClass $where $classe $x1 [expr {$y2 + 40}] $tag $thisclass]
	set x1 [lindex $rwidth 0]
	set y1 [lindex $rwidth 1]
	set x2 [lindex $rwidth 2]
	set y2 [lindex $rwidth 3]
	set l {}
	lappend l [expr {$x2 - 10.0}]
	lappend l [expr {$y2 + 40.0}]
	set BrowserOMT_maxy [BrowserOMTGetMax $BrowserOMT_maxy $l]
	BrowserOMTSetupCanva $where
	
	if {$hasinh} {
	    set xinh [expr {$xinh - 10.0}]
	    set xout [expr {($x1 + $x2) / 2.0 - 10.0}]
	    set yout $y1
	    set xmid $xout
	    set ymid [expr {$yinh + 20.0}]
	    $where create line  $xout $yout $xmid $ymid $xinh $ymid $xinh $yinh -arrow last -fill black -tags I -joinstyle round -width 0.1c
	}

	#
	# RETURN if the class is incomplete
	#
	if {$incomplete != 0} {
	    return
	}

	if {$BrowserOMT_clarray(root) == $classe} {
	    set typeClass [msclinfo -t $classe]

	    foreach p [msclinfo -C $classe] {
		set name [lindex $p 0]
		set error [catch {msclinfo -t $name}]

		if {$error == 0} {
		    if {$name != $classe} {
			set rwidth [BrowserOMTDrawBox $where $name $x2 [lindex $BrowserOMT_maxy 1] $name]
			lappend l [lindex $rwidth 2]
			lappend l [lindex $rwidth 3]
			set BrowserOMT_maxy [BrowserOMTGetMax $BrowserOMT_maxy $l]
			BrowserOMTSetupCanva $where
			
			set error [catch {msclinfo -t $name}]
			
			if {$error == 0} {
			    set xout [expr {($x1 + $x2) / 2.0}]
			    set yin  [expr {([lindex $BrowserOMT_clarray($name) 3] + [lindex $BrowserOMT_clarray($name) 1]) / 2.0}]
			    if {$yin < $y2} {
				set yout $y1
			    } else { 
				set yout $y2
			    }
			    if {$xout > [lindex $BrowserOMT_clarray($name) 0]} {
				set xin  [lindex $BrowserOMT_clarray($name) 2]
			    } else {
				set xin  [lindex $BrowserOMT_clarray($name) 0]
			    }
			    set xmid $xout
			    set ymid $yin
			    $where create line  $xout $yout $xmid $ymid $xin $yin -tags C -width 0.1c
			    $where create oval [expr {$xout - 5}] $yout [expr {$xout + 5}] [expr {$yout + 10}]  -outline $thisbackcolor -fill black
			    if {[msclinfo -e $name] == 0} {
				if {[msclinfo -P $name] || [msclinfo -T $name]} {
				    $where create rectangle [expr {$xin - 10}] [expr {$yin -5}] [expr {$xin}] [expr {$yin + 5}]  -outline $thisbackcolor -fill white
				} else {
				    $where create rectangle [expr {$xin - 10}] [expr {$yin -5}] [expr {$xin}] [expr {$yin + 5}]  -outline $thisbackcolor -fill black
				}
			    }
			}
		    } else {
			$where create line  $x1 $y1 $x1 [expr {$y1 - 15.0}] [expr {$x1 + 15.0}] [expr {$y1 - 15.0}] [expr {$x1 + 15.0}] $y1 -tags C -width 0.1c
			$where create oval $x1 $y1 [expr {$x1 + 10}] [expr {$y1 + 10}] -outline $thisbackcolor -fill black
		    }
		}
	    }
	    set usex [expr {[lindex $BrowserOMT_maxy 0] + 70}]
	    set usey $y
	    set genclass ""
	    if {$typeClass == "instclass"} {
		set genclass [msinstinfo -g $classe]
	    }
	    foreach  p [msclinfo -u $classe] {
		set error [catch {msclinfo -t $p}]

		if {$error == 0 && ($genclass != $p)} {
		    
		    if {$p != $classe} {
			if {[info exist BrowserOMT_clarray($p)] == 0} {
			    set usey [expr {$usey + 50}]
			    set rwidth [BrowserOMTDrawClass $where $p $usex $usey $p 0]
			    set usey [lindex $rwidth 3]
			    set l {}
			    lappend l [lindex $rwidth 2]
			    lappend l [lindex $rwidth 3]
			    set BrowserOMT_maxy [BrowserOMTGetMax $BrowserOMT_maxy $l]
			    BrowserOMTSetupCanva $where
			    
			    set error [catch {msclinfo -t $p}]

			    if {$error == 0} {
				set xout $x2
				set yout [expr {($y1 + $y2) / 2.0}]
				set yin  [expr {([lindex $BrowserOMT_clarray($p) 3] + [lindex $BrowserOMT_clarray($p) 1]) / 2.0}]
				set xin  [lindex $BrowserOMT_clarray($p) 0]
				set xmid [expr {$usex - 30}]
				
				set ymid $yout
				set xmid1 $xmid
				set ymid1 $yin
				$where create line  $xout $yout $xmid $ymid $xmid1 $ymid1 $xin $yin -tags C -width 0.1c -fill black
				$where create oval $xout [expr {$yout - 5}] [expr {$xout + 10}] [expr {$yout + 5}]  -outline $thisbackcolor -fill white
			    }
			}
		    }
		}
	    }
	}
    }
}


proc BrowserOMTInitScrollBar {w} {
    set hsb  [$w subwidget hsb]
    set vsb  [$w subwidget vsb]
    set hcmd [lindex [$hsb configure -command] 4]
    set vcmd [lindex [$vsb configure -command] 4]
    eval $hcmd moveto 0
    eval $vcmd moveto 0
    return
}

proc BrowserOMTDrawStandardClass {where classe x y tag istheclass} {
    global BrowserOMT_clarray BrowserOMT_maxy

    set incomplete [msclinfo -e $classe]
    set txt ""
    set fnt "-adobe-helvetica-medium-r-normal--12-120-75-75-p-67-iso8859-1"
    set inherits {}
    set hasinh   0
    set backcolor "yellow"
    set thisbackcolor "yellow"
    set nestedclass ""

    if {$incomplete == 0} {
	set backcolor "black"
	set thisbackcolor "black"
	if {[msclinfo -n $classe]} {
	    set nestedclass [msclinfo -N $classe]
	    set nestedclass "($nestedclass) "
	}
    }

    if {$BrowserOMT_clarray(root) == $classe} {
	set txt "< $classe $nestedclass>\n\n"
    } else {
	set txt "$classe $nestedclass\n\n"
    }
    
    if {$incomplete == 0} {
	if {$istheclass} {
	    set len [expr {[string length $classe] + 2}]
	    foreach p [msclinfo -m $classe] {
		set mth [string range $p $len [string length $p]]
		set txt "$txt $mth\n"
	    }
	    set txt "$txt\n"
	}	
    }
  
    $where create text $x $y -text $txt -anchor nw -tags grotas -font $fnt -justify left
    set rwidth [$where bbox grotas]
    set BrowserOMT_clarray($tag) $rwidth
    $where delete grotas

    set linex2 [lindex $rwidth 2]
    set liney2 [lindex $rwidth 3]
    set x1 [lindex $rwidth 0]
    set y1 [lindex $rwidth 1]
    set x2 [lindex $rwidth 2]
    set y2 [lindex $rwidth 3]

    if {$incomplete == 0} {
	if {$istheclass} {
	    set len [expr {[string length $classe] + 2}]
	    set txt "$txt\n"
	    foreach p [msclinfo -C $classe] {
		set txt "$txt $p\n"
	    }
	    set txt "$txt\n"
	}	
	$where create text $x $y -text $txt -anchor nw -tags grotas -font $fnt -justify left
	set rwidth [$where bbox grotas]
	set BrowserOMT_clarray($tag) $rwidth
	$where delete grotas
	set x1 [lindex $rwidth 0]
	set y1 [lindex $rwidth 1]
	set x2 [lindex $rwidth 2]
	set y2 [lindex $rwidth 3]
    }
    
    $where delete $tag
    
    if {$BrowserOMT_clarray(root) == $classe} {
	$where create rectangle $x1 $y1 $x2 $y2 -outline $thisbackcolor -fill white
	$where create rectangle $x1 $y1 $x2 [expr {$y1+20.0}] -outline $thisbackcolor -fill grey
	$where create line $x1 $liney2 $linex2 $liney2 -fill black
    } else {
	$where create rectangle $x1 $y1 $x2 $y2 -outline $backcolor -fill white	
	$where create rectangle $x1 $y1 $x2 [expr {$y1+20.0}] -outline $backcolor -fill grey
    }
    
    set tagtext [$where create text $x1 [expr {$y1 + 5.0}] -text $txt -anchor nw -tags $tag -font $fnt -justify left]

    $where bind $tagtext <Button-1> {
	global Browser_win
	set t [%W find withtag current]
	
	if {$t != ""} {
	    set name [lindex [%W gettags $t] 0]
	    BrowserOMTInitWindow  $Browser_win $name 0
	}
    }

    $where bind $tagtext <Any-Enter> {
	%W itemconfigure current -fill red
    }
    
    $where bind $tagtext <Any-Leave> {
	%W itemconfigure current -fill black
    }
    
    set posx [expr {$x1 + 10}]
    set posy [expr {$y2 - 10}]
    BrowserOMTAddStuff $where $classe $posx $posy $tag

    return $rwidth
}

proc BrowserOMTDrawGenericClass {where classe x y tag istheclass} {
    global BrowserOMT_clarray BrowserOMT_maxy

    set incomplete [msclinfo -e $classe]
    set txt ""
    set fnt "-adobe-helvetica-medium-r-normal--12-120-75-75-p-67-iso8859-1"
    set inherits {}
    set hasinh   0
    set backcolor "yellow"
    set thisbackcolor "yellow"
    set nestedclass ""

    if {$incomplete == 0} {
	set backcolor "black"
	set thisbackcolor "black"
	if {[msclinfo -n $classe]} {
	    set nestedclass [msclinfo -N $classe]
	    set nestedclass "($nestedclass) "
	}
    }

    if {$BrowserOMT_clarray(root) == $classe} {
	set txt "< $classe $nestedclass> : Generic\n\n"
    } else {
	set txt "$classe $nestedclass: Generic\n\n"
    }
    
    if {$incomplete == 0} {
	set genType [msgeninfo -g $classe]
	set len [llength $genType]
	set txt "$txt <"
	for {set i 0} {$i < $len} {incr i} {
	    if {$i != 0} {
		set txt "$txt,[lindex $genType $i]"
	    } else {
		set txt "$txt [lindex $genType $i]"
	    }
	}
	set txt "$txt >\n\n"
	
	if {$istheclass} {
	    set len [expr {[string length $classe] + 2}]
	    foreach p [msclinfo -m $classe] {
		set mth [string range $p $len [string length $p]]
		set txt "$txt $mth\n"
	    }
	    set txt "$txt\n"
	}	
    }
  
    $where create text $x $y -text $txt -anchor nw -tags grotas -font $fnt -justify left
    set rwidth [$where bbox grotas]
    set BrowserOMT_clarray($tag) $rwidth
    $where delete grotas

    set linex2 [lindex $rwidth 2]
    set liney2 [lindex $rwidth 3]
    set x1 [lindex $rwidth 0]
    set y1 [lindex $rwidth 1]
    set x2 [lindex $rwidth 2]
    set y2 [lindex $rwidth 3]

    if {$incomplete == 0} {
	if {$istheclass} {
	    set len [expr {[string length $classe] + 2}]
	    set txt "$txt\n"
	    foreach p [msclinfo -C $classe] {
		set txt "$txt $p\n"
	    }
	    set txt "$txt\n"
	}	
	$where create text $x $y -text $txt -anchor nw -tags grotas -font $fnt -justify left
	set rwidth [$where bbox grotas]
	set BrowserOMT_clarray($tag) $rwidth
	$where delete grotas
	set x1 [lindex $rwidth 0]
	set y1 [lindex $rwidth 1]
	set x2 [lindex $rwidth 2]
	set y2 [lindex $rwidth 3]
    }
    
    $where delete $tag
    
    if {$BrowserOMT_clarray(root) == $classe} {
	$where create rectangle $x1 $y1 $x2 $y2 -outline $thisbackcolor -fill white
	$where create rectangle $x1 $y1 $x2 [expr {$y1+20.0}] -outline $thisbackcolor -fill grey
	$where create line $x1 $liney2 $linex2 $liney2 -fill black
    } else {
	$where create rectangle $x1 $y1 $x2 $y2 -outline $backcolor -fill white	
	$where create rectangle $x1 $y1 $x2 [expr {$y1+20.0}] -outline $backcolor -fill grey
    }
    
    set tagtext [$where create text $x1 [expr {$y1 + 5.0}] -text $txt -anchor nw -tags $tag -font $fnt -justify left]

    $where bind $tagtext <Button-1> {
	global Browser_win
	set t [%W find withtag current]
	
	if {$t != ""} {
	    set name [lindex [%W gettags $t] 0]
	    BrowserOMTInitWindow  $Browser_win $name 0
	}
    }

    $where bind $tagtext <Any-Enter> {
	%W itemconfigure current -fill red
    }
    
    $where bind $tagtext <Any-Leave> {
	%W itemconfigure current -fill black
    }
     
    set posx [expr {$x1 + 10}]
    set posy [expr {$y2 - 10}]
    BrowserOMTAddStuff $where $classe $posx $posy $tag

    return $rwidth
}

proc BrowserOMTDrawInstClass {where classe x y tag istheclass} {
    global BrowserOMT_clarray BrowserOMT_maxy

    set incomplete [msclinfo -e $classe]
    set txt ""
    set fnt "-adobe-helvetica-medium-r-normal--12-120-75-75-p-67-iso8859-1"
    set inherits {}
    set hasinh   0
    set backcolor "yellow"
    set thisbackcolor "yellow"
    set nestedclass ""

    if {$incomplete == 0} {
	set backcolor "black"
	set thisbackcolor "black"
        if {[msclinfo -n $classe]} {
	    set nestedclass [msclinfo -N $classe]
	    set nestedclass "($nestedclass) "
	}
    }

    set genclass [msinstinfo -g $classe]
    if {$BrowserOMT_clarray(root) == $classe} {
	set txt "< $classe $nestedclass> : Instantiates\n\n"
    } else {
	set txt "$classe $nestedclass: Instantiates\n\n"
    }

    if {$incomplete == 0} {
	set genType [msgeninfo -g $genclass]
	set instType [msinstinfo -s $classe]
	set len [llength $genType]

	set txt "$txt $genclass <"
	for {set i 0} {$i < $len} {incr i} {
	    if {$i != 0} {
		set txt "$txt,[lindex $instType $i]"
	    } else {
		set txt "$txt [lindex $instType $i]"
	    }
	}
	set txt "$txt >\n\n"
	
	if {$istheclass} {
	    set len [expr {[string length $classe] + 2}]
	    foreach p [msclinfo -m $classe] {
		set mth [string range $p $len [string length $p]]
		set txt "$txt $mth\n"
	    }
	    set txt "$txt\n"
	}	
    }
  
    $where create text $x $y -text $txt -anchor nw -tags grotas -font $fnt -justify left
    set rwidth [$where bbox grotas]
    set BrowserOMT_clarray($tag) $rwidth
    $where delete grotas

    set linex2 [lindex $rwidth 2]
    set liney2 [lindex $rwidth 3]
    set x1 [lindex $rwidth 0]
    set y1 [lindex $rwidth 1]
    set x2 [lindex $rwidth 2]
    set y2 [lindex $rwidth 3]

    if {$incomplete == 0} {
	if {$istheclass} {
	    set len [expr {[string length $classe] + 2}]
	    set txt "$txt\n"
	    foreach p [msclinfo -C $classe] {
		set txt "$txt $p\n"
	    }
	    set txt "$txt\n"
	}	
	$where create text $x $y -text $txt -anchor nw -tags grotas -font $fnt -justify left
	set rwidth [$where bbox grotas]
	set BrowserOMT_clarray($tag) $rwidth
	$where delete grotas
	set x1 [lindex $rwidth 0]
	set y1 [lindex $rwidth 1]
	set x2 [lindex $rwidth 2]
	set y2 [lindex $rwidth 3]
    }
    
    $where delete $tag
    
    if {$BrowserOMT_clarray(root) == $classe} {
	$where create rectangle $x1 $y1 $x2 $y2 -outline $thisbackcolor -fill white
	$where create rectangle $x1 $y1 $x2 [expr {$y1+20.0}] -outline $thisbackcolor -fill grey
	$where create line $x1 $liney2 $linex2 $liney2 -fill black
    } else {
	$where create rectangle $x1 $y1 $x2 $y2 -outline $backcolor -fill white	
	$where create rectangle $x1 $y1 $x2 [expr {$y1+20.0}] -outline $backcolor -fill grey
    }
    
    set tagtext [$where create text $x1 [expr {$y1 + 5.0}] -text $txt -anchor nw -tags $tag -font $fnt -justify left]

    $where bind $tagtext <Button-1> {
	global Browser_win
	set t [%W find withtag current]
	
	if {$t != ""} {
	    set name [lindex [%W gettags $t] 0]
	    BrowserOMTInitWindow  $Browser_win $name 0
	}
    }

    $where bind $tagtext <Any-Enter> {
	%W itemconfigure current -fill red
    }
    
    $where bind $tagtext <Any-Leave> {
	%W itemconfigure current -fill black
    } 
    set posx [expr {$x1 + 10}]
    set posy [expr {$y2 - 10}]
    BrowserOMTAddStuff $where $classe $posx $posy $tag

    return $rwidth
}

proc BrowserOMTAddStuff {where classe x y tag} {
    set posx $x
    set posy $y

    if {[msclinfo -p $classe]} {
	set btm [tix getimage private]
	$where create image $posx $posy -image $btm -tags $tag
	set posx [expr {$posx + 16}]
    }

    if {[msclinfo -d $classe]} {
	set btm [tix getimage abstract]
	$where create image $posx $posy -image $btm -tags $tag
	set posx [expr {$posx + 16}]
    }

    if {[msclinfo -P $classe]} {
	set btm [tix getimage persistent]
	$where create image $posx $posy -image $btm -tags $tag
	set posx [expr {$posx + 16}]
    } elseif {[msclinfo -S $classe]} {
	set btm [tix getimage storable]
	$where create image $posx $posy -image $btm -tags $tag
	set posx [expr {$posx + 16}]
    } elseif {[msclinfo -T $classe]} {
	set btm [tix getimage transient]
	$where create image $posx $posy -image $btm -tags $tag
	set posx [expr {$posx + 16}]
    }
}

proc BrowserOMTDrawClass {where classe x y tag istheclass} {
    set Classtype [msclinfo -t $classe]
    set rwidth {}

    if {$Classtype == "stdclass"} {
	set rwidth [BrowserOMTDrawStandardClass $where $classe $x $y $tag $istheclass]
    } elseif {$Classtype == "genclass"} {
	set rwidth [BrowserOMTDrawGenericClass $where $classe $x $y $tag $istheclass]
    } elseif {$Classtype == "instclass"} {
	set rwidth [BrowserOMTDrawInstClass $where $classe $x $y $tag $istheclass]
    } else {
	puts "Unknown type $Classtype for $classe"
    }

    return $rwidth
}
