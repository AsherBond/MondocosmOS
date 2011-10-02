#
# Ouvre une entry dans le tree. (Double Click)
#
proc wokNAV:Tree:Open { w dir } {
    global IWOK_WINDOWS
     if {[$IWOK_WINDOWS($w,NAV,hlist) info children $dir] != {}} {
	foreach kid [$IWOK_WINDOWS($w,NAV,hlist) info children $dir] {
	    $IWOK_WINDOWS($w,NAV,hlist) show entry $kid
	}
	set data [$IWOK_WINDOWS($w,NAV,hlist) info data $dir]
	set loc  [lindex $data 0]
	wokUpdateLayout $loc
	wokCWD writenocallback $loc
    } else {
	tixBusy $w on
	update
	wokNAV:Tree:Fill $w $dir
	tixBusy $w off
    }
 
    return
}

#
# Appele par Open.
#
proc wokNAV:Tree:Fill { w dir } {
    global IWOK_WINDOWS
    set data [$IWOK_WINDOWS($w,NAV,hlist) info data $dir]
    set loc  [lindex $data 0]
    set type [lindex $data 1]

    switch -glob $type {

	factory    { 
	    wokNAV:Tree:Updateworkshop $w $loc $dir
	}
	
	workshop   {
	    wokNAV:Tree:Updateworkbench $w $loc $dir
	}
	
	warehouse {
	    wokNAV:Tree:Updatewarehouse $w $loc $dir
	}

	parcel {
	    wokNAV:Tree:Updateparcel $w $loc $dir
	}
	
	workbench {
	    wokNAV:Tree:Updatedevunit $w $loc $dir
	}
	
	session    {
	    wokNAV:Tree:Updatefactory $w $loc $dir
	}
	
	stuff_* {
	    wokNAV:Tree:Updatestufflist  $w $loc $dir $type
	}

	parcel_* {
	    wokNAV:Tree:Updateparcelstufflist  $w $loc $dir $type
	}

	parcelstuff_* {
	    wokNAV:Tree:Updatestufflist $w $loc $dir $type
	}

	devunit_* { 
	    wokNAV:Tree:Updatedevunitstuff $w $loc $dir
	}

	default {
	    puts "type = $type = for $loc is unknown"
	    return
	}
    }
    wokCWD write $loc
    return
}
#
# 
#    
proc wokNAV:Tree:Show { w dir } {
    global IWOK_WINDOWS
    $IWOK_WINDOWS($w,NAV,hlist) anchor clear
    $IWOK_WINDOWS($w,NAV,hlist) anchor set $dir
    $IWOK_WINDOWS($w,NAV,hlist) selection clear
    $IWOK_WINDOWS($w,NAV,hlist) selection set $dir
    $IWOK_WINDOWS($w,NAV,hlist) see $dir
    return
}
#
# Charge un terminal/trigger en fonction de son type. (Simple Click)
#
proc wokNAV:Tree:Browse { w dir } {
    global IWOK_WINDOWS
    set data [$IWOK_WINDOWS($w,NAV,hlist) info data $dir]
    set type [lindex $data 1]
    if { [regexp {trig_(.*)} $type all trig] } {
	switch -- $trig {
	    terminal {
		wokNAV:Tree:terminal $w [lindex $data 0] $dir
	    }

	    Repository {
		wokUpdateRepository [lindex $data 0]
	    }

	    Queue {
		wokWaffQueue [lindex $data 0]
	    }
	}
    }
    return
}

#
# Hilight l'entry correspondante et l'ouvre sauf si c'est un "trigger" (appelee par bind canvas)
#
proc wokNAV:Tree:Focus { w dir } {
    global IWOK_WINDOWS
    set data [$IWOK_WINDOWS($w,NAV,hlist) info data $dir]
    set type [lindex $data 1]
    if { [regexp {trig_(.*)} $type all trig] } {
	switch -- $trig {
	    terminal {
		wokNAV:Tree:terminal $w [lindex $data 0] $dir
	    }

	    Repository {
		wokUpdateRepository [lindex $data 0]
	    }

	    Queue {
		wokWaffQueue [lindex $data 0]
	    }

	}

    } else {
	wokNAV:Tree:Open $w $dir
	$IWOK_WINDOWS($w,NAV,tree) setmode $dir close
    }
    wokNAV:Tree:Show $w $dir
    return
}
#
#
#
proc wokNAV:Tree:SeeMe { w loc dir } {
    wokNAV:tlist:Set $w $loc $dir
    wokCWD write $loc
    wokNAV:Tree:Focus $w $dir
    return
}
#
#
#
proc wokNAV:Tree:UpdateSession  { w user } {
    global IWOK_WINDOWS
    set disp [list 18 18 600 30 16 1.4]
    set fdate wokGetsessiondate
    $IWOK_WINDOWS($w,NAV,hlist) add ^ -text $user -data [list : session $user {} $fdate $disp]
    wokNAV:Tree:Fill $w ^
    wokNAV:tlist:Set $w : ^
    return
}
#
# ici dir = ^
#
proc wokNAV:Tree:Updatefactory  { w loc dir } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS    
    wokNAV:Initfactory
    set disp  $IWOK_GLOBALS(factory,disp)
    set fdate $IWOK_GLOBALS(factory,fdate)
    set image $IWOK_GLOBALS(factory,image)

    foreach name [lsort [Sinfo -F]] {
	if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}$name] {
	    $IWOK_WINDOWS($w,NAV,hlist) add ${dir}$name \
		    -itemtype imagetext -text $name \
		    -image $image \
		    -data  [list $name factory $name $image $fdate $disp]
	}
	$IWOK_WINDOWS($w,NAV,tree) setmode ${dir}$name open
    }
    wokNAV:tlist:Set $w $loc $dir 
    return
}
#
# loc est une adresse de factory
#
proc wokNAV:Tree:Updateworkshop { w loc dir } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS
    wokNAV:Initworkshop
    
    set disp  $IWOK_GLOBALS(workshop,disp)
    set fdate $IWOK_GLOBALS(workshop,fdate)
    set image $IWOK_GLOBALS(workshop,image)

    set name [finfo -W $loc]
    if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^$name] {
	$IWOK_WINDOWS($w,NAV,hlist) add ${dir}^$name \
		-itemtype imagetext -text $name \
		-image [tix getimage warehouse] \
		-data  [list ${loc}:${name} warehouse $name [tix getimage warehouse] $fdate $disp]
    }
    $IWOK_WINDOWS($w,NAV,tree) setmode ${dir}^$name open

    foreach name [lsort [finfo -s $loc]] {
	if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^$name] {
	    $IWOK_WINDOWS($w,NAV,hlist) add ${dir}^${name} \
		    -itemtype imagetext -text $name \
		    -image $image \
		    -data  [list ${loc}:${name} workshop $name $image $fdate $disp]
	}
	$IWOK_WINDOWS($w,NAV,tree) setmode ${dir}^${name} open
    }
    wokNAV:tlist:Set $w $loc $dir 
    return
}
;#
;# loc est une adresse de workshop
;#
proc wokNAV:Tree:Updateworkbench { w loc dir } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS
    wokNAV:Initworkbench
    set disp  $IWOK_GLOBALS(workbench,disp) 
    set fdate $IWOK_GLOBALS(workbench,fdate)

    foreach name [sinfo -w $loc] {

	if [wokStore:Queue:Exists ${loc}:${name}] {
	    set image $IWOK_GLOBALS(workbenchq,image)	
	} else {
	    set image $IWOK_GLOBALS(workbench,image)	    
	}
	if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^${name}] {
	    $IWOK_WINDOWS($w,NAV,hlist) add ${dir}^${name} -text $name -itemtype imagetext  \
		    -image $image \
		    -data  [list ${loc}:${name} workbench $name $image $fdate $disp]
	}
	$IWOK_WINDOWS($w,NAV,tree) setmode ${dir}^${name} open
    }
    wokNAV:tlist:Set $w $loc $dir 
    return
}
;#
;# loc est une adresse de workbench.
;#
proc wokNAV:Tree:Updatedevunit { w loc dir } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS

    set disp  $IWOK_GLOBALS(workbench,disp) 
    set fdate $IWOK_GLOBALS(workbench,fdate)
    set image $IWOK_GLOBALS(workbench,image)

    if { [wokStore:Queue:Exists $loc] } {
	if { [wokStore:Report:SetQName $loc] != {} } {
	    if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^_Queue] {
		$IWOK_WINDOWS($w,NAV,hlist) add ${dir}^_Queue \
			-itemtype imagetext -text Queue \
			-image [tix getimage queue] \
			-data  [list ${loc}:Queue trig_Queue Queue [tix getimage queue] $fdate $disp]
	    }
	    if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^_Reposit] {
		$IWOK_WINDOWS($w,NAV,hlist) add ${dir}^_Reposit \
			-itemtype imagetext -text Repository \
			-image [tix getimage reposit] \
			-data  [list ${loc}:Repository trig_Repository Repository [tix getimage reposit] $fdate $disp]
	    }
	}
    }

    wokNAV:Initdevunit $loc
    foreach d [lsort -command wokSortUnit [w_info -a $loc]] {
	set name [lindex $d 1]
	set type [lindex $d 0]
	set disp  $IWOK_GLOBALS($type,disp)
	set fdate $IWOK_GLOBALS($type,fdate)
	set image $IWOK_GLOBALS($type,image)
	if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^${name}] {
	    $IWOK_WINDOWS($w,NAV,hlist) add ${dir}^${name} -itemtype imagetext \
		    -text $name -image $image \
		    -data [list ${loc}:${name} devunit_$type $name $image $fdate $disp]
	}
	$IWOK_WINDOWS($w,NAV,tree) setmode ${dir}^${name} open
    }
    wokNAV:tlist:Set $w $loc $dir
    return
}
;#
;#
;#
proc wokSortUnit { a b } {
    return [string compare [lindex $a 1] [lindex $b 1]]
}
proc wokSortPath { a b } {
     return [string compare [file tail $a] [file tail $b]]
}
;#
;#
;#
proc wokNAV:Tree:Updatedevunitstuff { w loc dir } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS
    wokNAV:Initdevunitstuff
    set fdate $IWOK_GLOBALS(devunitstuff,fdate)
    set disp  $IWOK_GLOBALS(devunitstuff,disp)

    catch { unset TLOC }
    foreach f [uinfo -Fpl $loc] { 
	set t [lindex $f 0]
	set p [lindex $f 2]
	if [info exists TLOC($t)] {
	    set l $TLOC($t)
	    lappend l $p
	    set TLOC($t) $l
	} else {
	    set TLOC($t) $p
	}
    }

    
    if [info exists TLOC(source)] {
	set name source
	if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^${name}] {
	    $IWOK_WINDOWS($w,NAV,hlist) add ${dir}^${name} -itemtype imagetext \
		    -text $name -image $IWOK_GLOBALS(devunitstuff,source) \
		    -data [list ${loc}:${name} stuff_$name $name  $IWOK_GLOBALS(devunitstuff,source) $fdate $disp]
	}
	$IWOK_WINDOWS($w,NAV,tree) setmode ${dir}^${name} open
	set IWOK_WINDOWS($w,NAV,tree,uinfo,${loc}:${name},$name) $TLOC($name)
    }
    
    foreach name [array names TLOC]  {
	if { "$name" != "source" } { 
	    if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^${name}] {
		$IWOK_WINDOWS($w,NAV,hlist) add ${dir}^${name} -itemtype imagetext \
			-text $name -image $IWOK_GLOBALS(devunitstuff,cell) \
			-data [list ${loc}:${name} stuff_$name $name $IWOK_GLOBALS(devunitstuff,cell) $fdate $disp]
	    }
	    $IWOK_WINDOWS($w,NAV,tree) setmode ${dir}^${name} open
	    set IWOK_WINDOWS($w,NAV,tree,uinfo,${loc}:${name},$name) $TLOC($name)
	}
    }
    wokNAV:tlist:Set $w ${loc} $dir
    
    return
}
;#
;#
;#
proc  wokNAV:Tree:Updatestufflist { w loc dir stuff_type } {
    global IWOK_WINDOWS

    set image [tix getimage textfile]
    set type [lindex [split $stuff_type _] 1]
   
    foreach name [lsort -command wokSortPath $IWOK_WINDOWS($w,NAV,tree,uinfo,$loc,$type)] { 
	set text [file tail $name]
	if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^${text}] {
	    $IWOK_WINDOWS($w,NAV,hlist) add ${dir}^${text} -itemtype imagetext -text $text \
		    -image $image -data [list $loc trig_terminal $text $image $name] 
	}
    }
    wokNAV:tlist:Set $w ${loc} $dir
    return
}
#
# data /adv_23/WOK/k2dev/ref/src/NWOK/COMPONENTS terminal
#
proc wokNAV:Tree:terminal { w loc dir } {
    global IWOK_WINDOWS
    set data [$IWOK_WINDOWS($w,NAV,hlist) info data $dir]
    wokEDF:AdequateCommand [lindex $data 4] $loc
    return
}
#
#  loc est une adresse de factory
#
proc wokNAV:Tree:Updatewarehouse { w loc dir } {
    global IWOK_WINDOWS
    set disp [list 18 18 600 30 12 1.4]
    set fdate wokGetparceldate
    set image [tix getimage parcel]
    foreach itm [Winfo -p $loc] {
	if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^${itm}] {
	    $IWOK_WINDOWS($w,NAV,hlist) add ${dir}^${itm} -itemtype imagetext \
		    -text $itm -image $image \
		    -data [list ${loc}:${itm} parcel ${itm} $image $fdate $disp]
	    $IWOK_WINDOWS($w,NAV,tree) setmode  ${dir}^${itm} open
	}
    }
    wokNAV:tlist:Set $w $loc $dir
    return
}
#
# loc est une adresse de parcel (WOK:BAG:NWOK-K2-1)
#
proc wokNAV:Tree:Updateparcel { w loc dir } {
    global IWOK_WINDOWS
    set disp [list 18 18 600 30 12 1.4]
    set fdate wokGetparcelunitdate
    foreach unit [pinfo -a ${loc}] {
	set type [lindex $unit 0]
	set name [lindex $unit 1]
	if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^${name}] {
	    $IWOK_WINDOWS($w,NAV,hlist) add ${dir}^${name} -itemtype imagetext \
		    -text $name -image [tix getimage $type] \
		    -data [list ${loc}:${name} parcel_$type ${name} [tix getimage $type] $fdate $disp]
	}
	$IWOK_WINDOWS($w,NAV,tree) setmode ${dir}^${name} open
    }
    wokNAV:tlist:Set $w $loc $dir
    return
}
#
#
#
proc wokNAV:Tree:Updateparcelstufflist { w loc dir stuff_type } {
    global IWOK_WINDOWS
    ;#puts "Updateparcelstufflist :   $loc $dir $stuff_type"
        
    set isource      [tix getimage source]
    set icell        [tix getimage cell]

    set disp [list 18 18 600 18 10 1.2]
    set fdate wokGetparcelunitstuffdate

    catch { unset TLOC }
    foreach f [uinfo -Fpl $loc] { 
	set t [lindex $f 0]
	set p [lindex $f 2]
	if [info exists TLOC($t)] {
	    set l $TLOC($t)
	    lappend l $p
	    set TLOC($t) $l
	} else {
	    set TLOC($t) $p
	}
    }
    
    foreach name [array names TLOC]  {
	set image $icell
	if { "$name" == "source" } { set image $isource }
	if ![$IWOK_WINDOWS($w,NAV,hlist) info exists ${dir}^${name}]  {
	    $IWOK_WINDOWS($w,NAV,hlist) add ${dir}^${name} -itemtype imagetext \
		    -text $name -image $image \
		    -data [list ${loc}:${name} parcelstuff_$name $name $image $fdate $disp]
	}
	$IWOK_WINDOWS($w,NAV,tree) setmode ${dir}^${name} open
	set IWOK_WINDOWS($w,NAV,tree,uinfo,${loc}:${name},$name) $TLOC($name)
    }
    wokNAV:tlist:Set $w ${loc} $dir
    return
}
#
# Sauve l'adresse( dans la hlist ) de ce qui a ete affiche
#
proc wokNAV:tlist:Set { w loc dir } {
    global IWOK_WINDOWS
    set IWOK_WINDOWS($w,NAV,tlist,$loc) $dir
    ;#puts "set IWOK_WINDOWS($w,NAV,tlist,$loc) = $dir"
    return
}
#
#
#
proc wokNAV:tlist:Get { w loc } {
    global IWOK_WINDOWS
    if [info exists IWOK_WINDOWS($w,NAV,tlist,$loc)] {
	return $IWOK_WINDOWS($w,NAV,tlist,$loc)
    } else {
	;#puts "wokNAV:tlist:Get pas d'info pour $loc"
	return {}
    }
}
#
# horreur 1: tout ca pour ce petit cheri de wokinfo
# on pourrait scanner tous les noeuds et chercher la data qui correspond...
proc wokNAV:tlist:locTodir { loc } {
    set l [split $loc :]
    if {"[lindex $l 0]" == {} } {
	return [join $l ^]
    } else {
	return ^[join $l ^]
    }
}
#
#
#
proc wokNAV:tlist:Type { w loc } {
    global IWOK_WINDOWS
    if [info exists IWOK_WINDOWS($w,NAV,tlist,$loc)] {
	return  [lindex [$IWOK_WINDOWS($w,NAV,hlist) info data $IWOK_WINDOWS($w,NAV,tlist,$loc)] 1]
    } else {
	return {}
    }
}
#
# Recupere les info associees a loc
#
proc wokNAV:tlist:GetData { w loc } {
    global IWOK_WINDOWS
    set hlist $IWOK_WINDOWS($w,NAV,hlist)
    set ll {}
    set dir [wokNAV:tlist:Get $w $loc]
    if [$hlist info exists $dir] {
	if { [$hlist info children $dir] != {} } {
	    foreach kid [$IWOK_WINDOWS($w,NAV,hlist) info children $dir] {
		lappend ll [linsert [$hlist info data $kid] 0 $kid]
	    }
	}
    }
    return $ll
}
#
# Idem qu'au dessus : utilise pour un terminal.
#
proc wokNAV:tlist:TermData { w dir } {
    global IWOK_WINDOWS
    set hlist $IWOK_WINDOWS($w,NAV,hlist)
    if [$hlist info exists $dir] {
	return [$hlist info data $dir]
    }
}

proc wokNAV:tlist:Display { w loc } {
    global IWOK_WINDOWS
    if [info exists IWOK_WINDOWS($w,NAV,tlist,$loc) ] {
	return [lindex [$IWOK_WINDOWS($w,NAV,hlist) info data $IWOK_WINDOWS($w,NAV,tlist,$loc)] end]
    }
}

proc wokNAV:tlist:date { w loc } {
    global IWOK_WINDOWS
    if [info exists IWOK_WINDOWS($w,NAV,tlist,$loc) ] {
	set i [expr [llength [$IWOK_WINDOWS($w,NAV,hlist) info data $IWOK_WINDOWS($w,NAV,tlist,$loc)]] -2]
	return [lindex [$IWOK_WINDOWS($w,NAV,hlist) info data $IWOK_WINDOWS($w,NAV,tlist,$loc)] $i]
    }
}
#
# Recupere l'adresse (loc) du pere 
#
proc wokNAV:Tlist:Dad { w loc } {
    global IWOK_WINDOWS
    if [info exists IWOK_WINDOWS($w,NAV,tlist,$loc) ] {
	set dir $IWOK_WINDOWS($w,NAV,tlist,$loc)
	set dad [$IWOK_WINDOWS($w,NAV,hlist) info parent $dir]
	return  [lindex [$IWOK_WINDOWS($w,NAV,hlist) info data $dad] 0]
    } else {
	return {}
    }
}
#
# imprime tout ce qu'il y a dans hli ( Hlist )
#
proc wokNAV:DBG { {root {}} } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS
    set w $IWOK_GLOBALS(toplevel)
    set hli $IWOK_WINDOWS($w,NAV,hlist)
    foreach c [$hli info children $root] {
	puts "$c : data <[$hli info data $c]>"
	wokNAV:DBG $c
    }
    return
}
proc wokDUMP { } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS
    parray IWOK_WINDOWS $IWOK_GLOBALS(toplevel),NAV,tlist,*
    return
}
# 
# Ajoute l'entry de type $type correspondant a loc a l'adresse fdir du tree .
# 
#
proc wokNAV:Tree:Add { fdir loc name type } {
    global IWOK_GLOBALS
    global IWOK_WINDOWS
    set w $IWOK_GLOBALS(toplevel)
    if { [$IWOK_WINDOWS($w,NAV,hlist) info children $fdir] != {} } {
	wokNAV:Init${type} $loc
	if {  [info exists IWOK_GLOBALS($type,disp)] } {
	    set disp  $IWOK_GLOBALS($type,disp) 
	    set fdate $IWOK_GLOBALS($type,fdate)
	    set image $IWOK_GLOBALS($type,image)
	    set dir ${fdir}^${name}
	    if { ![$IWOK_WINDOWS($w,NAV,hlist) info exists $dir] } {
		$IWOK_WINDOWS($w,NAV,hlist) add ${dir} \
			-itemtype imagetext -text $name \
			-image $image \
			-data  [list $name $type $name $image $fdate $disp]
		$IWOK_WINDOWS($w,NAV,tree) setmode ${dir} open
		wokNAV:tlist:Set $w ${loc} $dir
		;# si l'entry pere a ete depliee puis repliee ne pas afficher, sera fait a l'ouverture
		if { "[$IWOK_WINDOWS($w,NAV,tree) getmode $fdir]" == "open" } {
		    ;#puts "on cache car $fdir n est pas open"
		    $IWOK_WINDOWS($w,NAV,hlist) hide entry ${dir}
		}
		;# si l'adresse est celle affiche dans le canvas : mettre a jour
		if { "[wokCWD read]:${name}" == "$loc" } { 
		    wokCWD write [wokCWD read]
		}
	    } else {
		puts stderr "Entry $dir already exists. not done"
	    }
	} else {
	    puts stderr "Unable to read IWOK_GLOBALS($type,disp)"
	}
    } else {
	;# n'a pas ete encore affichee => sera updatee par WOK a la prochaine ouverture.
    }
    return
}
#
# Vire les entry de lstdir du tree. listloc = { ... {$loc $type} ... }
# Comme on ne connait pas l'etat du tree on fait info exists...
# meme chose pour l'historique dans la ComboBox.
#
proc wokNAV:Tree:Del { listloc } {
    global IWOK_WINDOWS
    global IWOK_GLOBALS
    set w $IWOK_GLOBALS(toplevel)
    set update 0
    set layout [wokListLayout]
    foreach itm $listloc {
	set loc [lindex $itm 0]
	set typ [lindex $itm 1]
	set dir [wokNAV:tlist:locTodir $loc]
	if { [$IWOK_WINDOWS($w,NAV,hlist) info exists $dir] } {
	    set actloc [lindex [$IWOK_WINDOWS($w,NAV,hlist) info data $dir] 0]
	    $IWOK_WINDOWS($w,NAV,hlist) delete entry ${dir} 
	    if [info exists IWOK_WINDOWS($w,NAV,tlist,$actloc)] {
		unset IWOK_WINDOWS($w,NAV,tlist,$actloc)
	    }
	    ;# faudra t-il mettre a jour le canvas ?
	    if { [lsearch $layout $actloc] != -1 } {
		set update 1
	    }
	    wokCWD deletefromhistory $actloc
	}
    }
    ;# Un des elements detruits etait dans le canvas. On met a jour.
    ;# On monte d'un cran si en pointant dans la hlist on detruit l'element affiche (CWD read)
    ;# tout ca est un peu complique.
    if { $update == 1 } {
	set displayed [wokCWD read]
	set dir [wokNAV:tlist:locTodir $displayed]
	if [$IWOK_WINDOWS($w,NAV,hlist) info exists $dir] {
	    wokCWD write $displayed
	} else {
	    wokCWD write [wokinfo -N $displayed]
	}
    }
    return
}

proc wokNAV:Initfactory { args } {
    global IWOK_GLOBALS
    if ![info exists IWOK_GLOBALS(factory,initdone)] {
	set IWOK_GLOBALS(factory,initdone) 1
	set IWOK_GLOBALS(factory,disp)  [list 18 18 600 30 16 1.8] 
	set IWOK_GLOBALS(factory,fdate) wokGetfactorydate
	set IWOK_GLOBALS(factory,image) [tix getimage factory]
    }
    return
}

proc wokNAV:Initworkshop { args  } {
    global IWOK_GLOBALS
    if ![info exists IWOK_GLOBALS(workshop,initdone)] {
	set IWOK_GLOBALS(workshop,initdone) 1
	set IWOK_GLOBALS(workshop,disp)  [list 18 18 600 30 14 1.5]
	set IWOK_GLOBALS(workshop,fdate) wokGetworkshopdate
	set IWOK_GLOBALS(workshop,image) [tix getimage workshop]
    }
    return 
}

proc wokNAV:Initworkbench { args } {
    global IWOK_GLOBALS
    global env
    if ![info exists IWOK_GLOBALS(workbench,initdone)] {
	set IWOK_GLOBALS(workbench,initdone) 1
	set IWOK_GLOBALS(workbench,disp)  [list 18 18 600 30 12 1.4]
	set IWOK_GLOBALS(workbench,fdate) wokGetworkbenchdate
	set IWOK_GLOBALS(workbench,image) [tix getimage workbench]
	set IWOK_GLOBALS(workbenchq,image) [tix getimage workbenchq]
    }
    return 
}

proc wokNAV:Initdevunit { args } {
    global IWOK_GLOBALS
    ;# [ucreate -P $args]
    if ![info exists IWOK_GLOBALS(devunit,initdone)] {
	set IWOK_GLOBALS(devunit,initdone) 1
	foreach t $IWOK_GLOBALS(ucreate-P)  {
	    set x [lindex $t 1]
	    set IWOK_GLOBALS($x,disp)  [list 18 18 600 18 10 1.2]
	    set IWOK_GLOBALS($x,fdate) wokGetdevunitdate
	    set IWOK_GLOBALS($x,image) [tix getimage $x]
	    eval "proc wokNAV:Init${x} { args } {global IWOK_GLOBALS ; set IWOK_GLOBALS($x,disp) \"$IWOK_GLOBALS($x,disp)\"  ;set IWOK_GLOBALS($x,fdate) $IWOK_GLOBALS($x,fdate) ;set IWOK_GLOBALS($x,image) $IWOK_GLOBALS($x,image);return }"
	}
    }
    return
}

proc wokNAV:Initdevunitstuff { args } {
    global IWOK_GLOBALS
    if ![info exists IWOK_GLOBALS(devunitstuff,initdone)] {
	set IWOK_GLOBALS(devunitstuff,initdone) 1
	set IWOK_GLOBALS(devunitstuff,disp)   [list 18 18 600 18 10 1.2]
	set IWOK_GLOBALS(devunitstuff,fdate)  wokGetdevunitstuffdate
	set IWOK_GLOBALS(devunitstuff,source) [tix getimage source]
	set IWOK_GLOBALS(devunitstuff,cell)   [tix getimage cell]
    }
    return
}
