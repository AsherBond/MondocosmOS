
proc iwokNotYetImplemented { } {
puts stderr \
	    {
	This proc is not available on this version of Tcl/Tk.
	The commands used to prepare, store and integrate as available as
        respectively : wprepare , wstore , and wintegre.
        See online help for these commands.
    }
}




proc iwokUsage { } {
    puts stderr {Usage : iwok [-fh] }
    puts stderr ""
    puts stderr { iwok -f       : Fast start. Set current location to the session }
    puts stderr ""
    return
}

proc iwok { args } {

    global IWOK_GLOBALS
    global env
    global tcl_platform    
    global tcl_version

    if { [lsearch $args -h] != -1 } {
	iwokUsage
	return
    }



    regsub -all {\.[^.]*} $tcl_version "" major
    if { $major == 8 } {
	iwokNotYetImplemented 
	return
    }

    if [catch {package require Tix} statix ] {
	puts stderr "$statix"
	return
    }
    
    set fast 0
    if { [lsearch $args -f] != -1 } { set fast 1 }

    catch {wokKillAll}


    set IWOK_GLOBALS(windows) {}
    set IWOK_GLOBALS(toplevel) .wok[join [split [id user][id host] .] _]
    set IWOK_GLOBALS(toplevel,geometry) 1200x80+10+30
    set IWOK_GLOBALS(toplevel,closed)   1200x80
    set IWOK_GLOBALS(toplevel,opened)   1200x800
    set IWOK_GLOBALS(user_pwd) [pwd]
    set IWOK_GLOBALS(windows,rect) 950x450+4000+40 
    set IWOK_GLOBALS(windows,barr) 198x971+1063+38
    set IWOK_GLOBALS(canvas,width) 1500
    set IWOK_GLOBALS(canvas,height) 1200
    set IWOK_GLOBALS(order) 1
    set IWOK_GLOBALS(term,started) 0
    if [file exists [file join $env(WOK_LIBRARY) images]] {
	tix addbitmapdir [set IWOK_GLOBALS(maps) [file join $env(WOK_LIBRARY) images]]
    } else {
	tix addbitmapdir [set IWOK_GLOBALS(maps) $env(WOK_LIBRARY)]
    }
    
    set IWOK_GLOBALS(layout) 0
    set IWOK_GLOBALS(layout,update) 1
    set IWOK_GLOBALS(font)     [tix option get fixed_font]
    set IWOK_GLOBALS(boldfont) [tix option get bold_font]

    ;# ucreate -P dans factory/workshop/ => erreur ?!!!
    set IWOK_GLOBALS(ucreate-P) [list {j jini} {p package} {s schema} {i interface} {C client} {e engine} {x executable} {n nocdlpack} {t toolkit} {r resource} {O documentation} {c ccl} {f frontal} {d delivery} {I idl} {S server}  ]

    foreach type $IWOK_GLOBALS(ucreate-P) {
	set st [lindex $type 0]
	set lt [lindex $type 1]
	set IWOK_GLOBALS(image,$st) [set IWOK_GLOBALS(image,$lt) [tix getimage $lt]]
	set IWOK_GLOBALS(S_L,$st) $lt
	set IWOK_GLOBALS(L_S,$lt) $st
    }

    wm withdraw .
    toplevel $IWOK_GLOBALS(toplevel)
    wm title $IWOK_GLOBALS(toplevel) "WOK ( [id user] ) on host [id host]"
    ;#wm geometry $IWOK_GLOBALS(toplevel) $IWOK_GLOBALS(toplevel,geometry)
    wm geometry $IWOK_GLOBALS(toplevel) 1200x80+10+30
    ;#wokInitPalette black white orange blue
    wokInitPalette
    
    set IWOK_GLOBALS(toplevel,fg) [option get $IWOK_GLOBALS(toplevel) foreground {}]

    wokEDF:InitAdequateCommand
    wokEDF:InitExtension 

    eval "proc WOK_DoWhenIdle {} {update}"

    auto_load wokMessageInText
    wokBuild $fast
    return
}

proc wokInitPalette { {bgcolor grey51} {fgcolor white} {ycolor yellow} {bkcol black} } {
    tk_setPalette background $bgcolor foreground  $fgcolor
    option add *background  $bgcolor
    option add *activeBackground  $bgcolor
    option add *highlightBackground  $bgcolor
    option add *foreground  $ycolor
    option add *activeForeground $fgcolor
    option add *highlightColor $fgcolor
    option add *troughColor  $bgcolor
    option add *selectBackground $ycolor
    option add *selectForeground $bgcolor
    option add *insertBackground $bkcol
}


proc wokTPL { string } {
    regsub -all {[:.]} $string "" w
    return .[string tolower $w]
}


proc wokKillAll { } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS

    destroy $IWOK_GLOBALS(toplevel)
    
    msgunsetcmd
    if [winfo exists $IWOK_GLOBALS(toplevel)] {
	foreach child [ tixDescendants $IWOK_GLOBALS(toplevel) ] {
	    if [winfo exists $child] {
		destroy $child
	    }
	}
    }
    
    foreach ws [wokButton listw] {
	foreach tpl [lindex $ws 1] {
	    catch { destroy $tpl }
	}
    }

    cd $IWOK_GLOBALS(user_pwd)
    catch { unset IWOK_WINDOWS }
    catch { unset IWOK_GLOBALS }
    return
}
