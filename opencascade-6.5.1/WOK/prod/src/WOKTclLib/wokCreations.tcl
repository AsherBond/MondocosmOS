proc wokCreate { dir loc {asked_type {}} } { 
    global IWOK_GLOBALS

    if ![wokinfo -x $loc] return

    set mw .wokcreate
    if [winfo exists $mw] {
	destroy $mw
    }

    toplevel $mw
    wm geometry $mw +60+120

    set ent_type {}
    if { $asked_type == {} } {
	set tab([set tab([set tab([set tab(factory) workshop]) workbench]) devunit]) description
	set ent_type $tab([wokinfo -t $loc])
    } else {
	set ent_type $asked_type
    }
    
    
    set IWOK_GLOBALS(scratch) {}

    tixLabelFrame $mw.f -relief raised
    pack $mw.f -expand yes -fill both -padx 1 -pady 1

    set w [$mw.f subwidget frame]

    set img [label $w.img]

    tixLabelEntry $w.e -label "Name: " \
	    -options {
	entry.width 20
	entry.textVariable IWOK_GLOBALS(scratch)
    }

    tixButtonBox $w.box -orientation horizontal
    $w.box add ok -text Ok -underline 0 \
	    -command [list wokCreate:action $mw $dir $loc $ent_type] -width 6
    $w.box add cancel -text Cancel -underline 0 -command "destroy $mw" -width 6

    bind [$w.e subwidget entry]  <Return> {
	focus [[[winfo toplevel %W].f subwidget frame].box subwidget ok]
    }

    switch $ent_type {
	
	factory {
	}

	devunit {
	    $mw.f configure -label "Adding a unit in $loc."
	    tixOptionMenu $w.qt -command wokCreate:SetType -label "Type : " 
	    set mbu [$w.qt subwidget menubutton]
	    foreach I $IWOK_GLOBALS(ucreate-P) {
		$w.qt  add command "$I $mbu" -label [lindex $I 1]
	    }
	    $w.qt subwidget menubutton configure -height 25 -width 136
	    tixForm $w.e -top 20
	    tixForm $w.qt -top $w.e -left 2 -bottom $w.box
	}

	workbench {
	    tixBusy $mw on
	    set image [tix getimage workbench]
	    $img configure -image $image
	    $mw.f configure -label "Adding a workbench in $loc."
	    set tree [tixTree $w.tree -options {hlist.separator "^" hlist.selectMode single }]
	    $tree config -browsecmd [list wokWbtree:UpdLab $tree]
	    set hli [$tree subwidget hlist]
	    set lfath [wokWbtree:LoadSons $loc [wokinfo -p WorkbenchListFile $loc]]
	    if { [llength $lfath] == 1 } {
		set father [lindex $lfath 0]
	    } elseif { [llength $lfath] > 1 } {
		puts " more than one root in workbench tree"
		set father [lindex $lfath 0]
	    }
	    $hli delete all
	    $hli add ^
	    update
	    tixComboBox $w.fh -label "Father:" -variable IWOK_GLOBALS(scratch,father)
	    set IWOK_GLOBALS(scratch,father) $father
	    foreach ww [sinfo -w $loc] {
		$w.fh insert end $ww
	    }
	    $w.box add shotree -text "Show Tree" -underline 0 -width 8 \
		    -command [list wokWbtree:Tree $tree $hli "" $father $image]
	    tixForm $w.tree -left 2 -right %99 -top 20 -bottom $w.e 
	    tixForm $w.e -bottom $w.fh
	    tixForm $w.fh -bottom $w.box
	    tixBusy $mw off
	}

	workshop {
	    $img configure -image [tix getimage workshop]
	    $mw.f configure -label "Adding a workshop in $loc."
	    tixForm $img -left 2 -right %99 -top 8
	    tixForm $w.e -top $img -bottom $w.box 
	}

    }
    tixForm $w.box  -left 2 -right %99 -bottom %99

    return
}
#
# ent est l'adresse dans la hlist
#
proc wokWbtree:UpdLab { tree ent } {
    global IWOK_GLOBALS
    set hli [$tree subwidget hlist]
    set IWOK_GLOBALS(scratch,father) [$hli info data $ent]
    return
}
#
# affiche un arbre dans tree
#
proc wokWbtree:Tree { tree hli ent name ima } {
    if {![$hli info exists ${ent}^${name}] } {
	$hli add ${ent}^${name} -itemtype imagetext -text $name -image $ima -data $name
	update
    }
    set lson [wokWbtree:GetSons $name] 
    foreach son $lson {
	if { "$son" != "$name" } {
	    if {![$hli info exists ${ent}^${name}^${son}] } {
		if { [info procs ${son}.wokhasq] == "${son}.wokhasq" } {
		    $hli add ${ent}^${name}^${son} -itemtype imagetext -text $son -image [${son}.wokhasq] -data $son
		} else {
		    $hli add ${ent}^${name}^${son} -itemtype imagetext -text $son -image $ima -data $son
		}
		wokWbtree:Tree $tree $hli ${ent}^${name} $son $ima
	    }
	}
    }
    if { [info procs ${name}.wokhasq] == "${name}.wokhasq" } {
	$hli entryconfigure ${ent}^${name} -image [${name}.wokhasq]
    }
    return
}

proc wokWbtree:GetSons { wb } {
    if { [info procs ${wb}.woksons] != {} } {
	return [eval ${wb}.woksons]
    } else {
	return {}
    }
}

proc wokWbtree:LoadSons { ent WBLIST } {
    catch {unset TLOC}
    foreach p [info procs *.woksons] {
	rename $p {}
    }

    foreach p [info procs *.wokhasq] {
	rename $p {}
    }
    set imagq [tix getimage workbenchq]
    if [ file exists $WBLIST ] {
	set f [ open $WBLIST r ]
	while {[gets $f line] >= 0} {
	    set ll [split $line]
	    set son [lindex $ll 0]
	    set dad [lindex $ll 1]
	    if { $dad != {} } {
		if { ![info exists TLOC($dad)] } {
		    set TLOC($dad) $son
		} else {
		    set ii $TLOC($dad)
		    lappend ii $son
		    set TLOC($dad) $ii
		}   
	    } else {
		set TLOC($son) {}
		lappend lroot [set root $son]
	    }
	    if [wokStore:Queue:Exists ${ent}:${son}] {
		eval "proc $son.wokhasq {} { return $imagq }"
	    }
	}
	close $f
    } else {
	set root {}
    }
    foreach x [array names TLOC] {
	eval "proc $x.woksons {} { return [list $TLOC($x)] }" 
    }
    return $lroot
}

proc wokCreate:action  { w dir loc cmd } {
    global IWOK_GLOBALS

    tixBusy $w on
    update
    if { $IWOK_GLOBALS(scratch) != {} } {
	
	switch $cmd {

	    factory {
	    }

	    devunit {
		if ![ catch { ucreate -$IWOK_GLOBALS(scratch,wokType) ${loc}:$IWOK_GLOBALS(scratch) } helas ] {
		    wbuild:Update
		    set s $IWOK_GLOBALS(scratch,wokType)
		    set type $IWOK_GLOBALS(S_L,$s)
		    wokNAV:Initdevunit ${loc}
		    wokNAV:Tree:Add $dir ${loc}:$IWOK_GLOBALS(scratch) $IWOK_GLOBALS(scratch) $type
		} else {
		    puts stderr "$helas"
		}
	    }

	    workbench {
		if { [string compare $IWOK_GLOBALS(scratch,father) -] == 0 } {
		    if ![ catch { wcreate -d $IWOK_GLOBALS(scratch)} helas ] {
			wokNAV:Tree:Add $dir ${loc}:$IWOK_GLOBALS(scratch) $IWOK_GLOBALS(scratch) $cmd
		    } else {
			puts stderr "$helas"
		    }
		} else {
		    set father ${loc}:$IWOK_GLOBALS(scratch,father)
		    if ![ catch { wcreate -f $father -d ${loc}:$IWOK_GLOBALS(scratch)} helas ] {
			wokNAV:Tree:Add $dir ${loc}:$IWOK_GLOBALS(scratch) $IWOK_GLOBALS(scratch) $cmd
		    } else {
			puts stderr "$helas"
		    }   
		}
	    }

	    workshop {
		if ![ catch { screate -d ${loc}:$IWOK_GLOBALS(scratch)} helas ] {
		    wokNAV:Tree:Add $dir ${loc}:$IWOK_GLOBALS(scratch) $IWOK_GLOBALS(scratch) $cmd
		} else {
		    puts stderr "$helas"
		}
	    }
	    
	}
	set IWOK_GLOBALS(scratch) {}
    } 
    tixBusy $w off
    destroy $w
    return
}

proc wokCreate:SetType { string } {
    global IWOK_GLOBALS
    regexp {(.*) (.*) (.*)} $string ignore IWOK_GLOBALS(scratch,wokType) longname w
    set img [image create compound -window $w]
    $img add text -text $longname -underline 0
    $img add image -image [tix getimage $longname]
    $w config -image $img
    return
}
