#############################################################################
#
#                              W P R E P A R E
#                              _______________
#
#############################################################################
#
# Usage
#
proc wokPrepareUsage { } {
    puts stderr { Usage: wprepare  [-ref] [-ud <ud_1,ud_2, ..,ud_N>] -o [filename]}
    puts stderr {        Note: If your specify more than one unit, separate names with a comma.}
    puts stderr {                                                                              }
    return
}
#
# Point d'entree de la commande
#
proc wprepare { args } {

    global wokfileid 
    global WOKVC_STYPE WOKVC_LTYPE

    set tblreq(-h)     {}
    set tblreq(-ud)    value_required:list
    set tblreq(-ref)   {}
    set tblreq(-o)     value_required:file

    set tblreq(-son)   value_required:string
    set tblreq(-dad)   value_required:string

    set tblreq(-since) value_required:string
    
    set param {}
    if { [wokUtils:EASY:GETOPT param tabarg tblreq wokPrepareUsage $args] == -1 } return

    if [info exists tabarg(-h)] {
	wokPrepareUsage
	return
    }

    if [info exists tabarg(-son)] {
	set WBFils $tabarg(-son)
    } else {
	set WBFils [wokinfo -w]
    }
    
    if [info exists tabarg(-dad)] {
	set WBPere $tabarg(-dad)
    } else {
	set WBPere [lindex [w_info -A $WBFils] 1] 
    }


    set SHPere [wokinfo -s $WBPere]
    set SHFils [wokinfo -s $WBFils]

    if [info exists tabarg(-o)] {
	set wokfileid [open $tabarg(-o) w]
	eval "proc wprepare_return { } { close $wokfileid ; return }"
    } else {
	set wokfileid stdout
	eval "proc wprepare_return { } { return }"
    }

    if [info exists tabarg(-ud)] {
	set LUnits $tabarg(-ud)
    } else {
	set LUnits [w_info -l $WBFils]
    }


    if [info exists tabarg(-since)] {
	set date $tabarg(-since)
	if { $date != {} } {
	    wokclose -a
	    wokPrepare:Report:InitTypes
	    wokPrepare:Report:Output banner [wokinfo -n $SHFils] [wokinfo -n $WBPere] [wokinfo -n $WBFils] 
	    wokPrepare:Unit:Since wokPrepare:Report:Output ${WBFils} $LUnits $date
	    puts $wokfileid "is"
	    puts $wokfileid "  Author        : "
	    puts $wokfileid "  Study/CSR     : "
	    puts $wokfileid "  Debug         : "
	    puts $wokfileid "  Improvements  : "
	    puts $wokfileid "  News          : "
	    puts $wokfileid "  Deletions     : "
	    puts $wokfileid "  Impact        : "
	    puts $wokfileid "  Comments      : "
	    puts $wokfileid "end;"
	    catch {unset wokfileid}
	    wprepare_return
	    return
	} else {
	    msgprint -c WOKVC -e "Bad format for date."
	    return
	}
    }

    wokclose -a

    wokPrepare:Report:InitTypes

    ;# wokinfo -n "" => WOKSESSION..
    if { [info exists tabarg(-ref)] || [wokUtils:WB:IsRoot $WBFils] } {
	wokPrepare:Report:Output banner [wokinfo -n $SHFils] [wokinfo -n $WBFils] [wokinfo -n $WBFils] 	
	wokPrepare:Unit:Ref wokPrepare:Report:Output ${WBFils} $LUnits
    } else {
	wokPrepare:Report:Output banner [wokinfo -n $SHFils] [wokinfo -n $WBPere] [wokinfo -n $WBFils] 
	wokPrepare:Unit:Loop wokPrepare:Report:Output ${WBPere} ${WBFils} $LUnits
    }

    wokPrepare:Report:Output notes

    catch {unset wokfileid}
    wprepare_return
}
#;>
# Boucle sur une liste  {type name}, ecrit dans table(name.type) = " # name etc.."
# pour tous les fichiers dont la mtime est superieur strictement a date.
# wokPrepare:Unit:Ref Mytable DEMO:Demo:Kernel {NTD AccesServer}
# Wb: un prefixe quelconque a une Ud
#;<
proc wokPrepare:Unit:Since { Fout Wb Uliste date } {
    foreach e $Uliste {
	set t [uinfo -t ${Wb}:${e}]
	$Fout uheader "$e.$t"
	foreach f [lsort [uinfo -pl -Tsource ${Wb}:${e}]]  {
	    set mti [file mtime $f]
	    if { $mti > $date } {
		$Fout files # [clock  format [file mtime $f] -format "%d/%m/%y %R"] [file tail $f] [file dirname $f]
	    }
	}
    }
    return
}
#;>
# Boucle sur une liste  {type name}, ecrit dans table(name.type) = " + name etc.."
# wokPrepare:Unit:Ref Mytable DEMO:Demo:Kernel {NTD AccesServer}
# Wb: un prefixe quelconque a une Ud
#;<
proc wokPrepare:Unit:Ref { Fout Wb Uliste } {
    foreach e $Uliste {
	set t [uinfo -t ${Wb}:${e}]
	$Fout uheader "$e.$t"
	foreach f [lsort [uinfo -pl -Tsource ${Wb}:${e}]]  {
	    $Fout files + [clock  format [file mtime $f] -format "%d/%m/%y %R"] [file tail $f] [file dirname $f]
	}
    }
    return
}

#;>
# Boucle sur une liste  {type name}, ecrit dans table le resultat de la comparaison
# wokPrepare:Unit:Loop Mytable DEMO:Demo:Kernel DEMO:Demo:FK  {NTD AccesServer}
# Pere = FACT:SHOP:WBPERE , Fils: FACT:SHOP:WBFILS
# Pour chacune des Uds il faut chercher dans quel workbench elle se trouve au dessus.
# si elle n'existe pas =. on met {}. 
#
#;<
proc wokPrepare:Unit:Loop { Fout Pere Fils Uliste } {
    wokPrepare:Unit:ouestu ${Pere} $Uliste map
    foreach e $Uliste {
	set t [uinfo -t ${Fils}:${e}]
	$Fout uheader "$e.$t"
	set loc [uinfo -fl -Tsource ${Fils}:$e]
	
	if { $map($e) != {} } {
	    set ances $map($e)
	    wokPrepare:Unit:Diff $Fout [uinfo -fp -Tsource $ances:$e] [uinfo -fp -Tsource ${Fils}:$e] $loc
	} else {
	    wokPrepare:Unit:Diff $Fout {} [uinfo -fp -Tsource ${Fils}:$e] $loc
	}
    }
}

#;>
# retourne le nom du workbench ou se trouve u
# dans l'ascendance de wb, (wb inclus)
# wb est un full path
#;<
proc wokPrepare:Unit:ouestu { wb lu map} {
    upvar $map TLOC
    set pfx [wokinfo -s $wb]
    set ancestors [w_info -A $wb]
    foreach u $lu {
	foreach w $ancestors {
	    set TLOC($u) {}
	    if { [wokinfo -x ${pfx}:${w}:$u] } {
		set TLOC($u) ${pfx}:$w
		break
	    }
	}
    }
}

#;>
#
#  l1 liste des sources vue du pere {basename dirname}
#  l2    "      "       vue du fils "    "
#  local basename des sources effectivement dans dfils
#
#  retourne une liste des comparaisons
#;<
proc wokPrepare:Unit:Diff { Fout l1 l2 local } {
    ;#
    ;# 1. Comparaison de l1 et l2 dans wokM
    ;#
    catch {unset wokM}
    foreach e $l1 {
	set wokM([lindex $e 0]) [list - [lindex $e 1]]
    }

    foreach e $l2 {
	set k [lindex $e 0]
	set p [lindex $e 1]
	if { [info exists wokM($k)] } {
	    set l $wokM($k)
	    set wokM($k) [list # [lindex $l 1] $p]
	} else {
	    set wokM($k) [list + $p]
	}
    }
    ;#
    ;# 2. Parcours de wokM : impression des nouveaux et des disparus
    ;#
    ;#parray wokM
    foreach e [array names wokM] {
	switch -- [lindex $wokM($e) 0] {
	    - {
		set file  [lindex $wokM($e) 1]
		if [file exists $file] {
		    $Fout files - [clock  format [file mtime $file] -format "%d/%m/%y %R"] $e [file dirname $file]
		} else {
		    ;#msgprint -w "Unit files list should be recomputed. (umake -o src)"
		}
	    }

	    + {
		set file  [lindex $wokM($e) 1]
		if [file exists $file] {
		    $Fout files + [clock  format [file mtime $file] -format "%d/%m/%y %R"] $e [file dirname $file]
		} else {
		    ;#msgprint -w "Unit files list should be recomputed. (umake -o src)"
		}
	    }

	    # {
		if { [lsearch $local $e] != -1 } {
		    set fpere [lindex $wokM($e) 1] ; set ffils [lindex $wokM($e) 2]
		    set date [clock  format [file mtime $ffils] -format "%d/%m/%y %R"]
		    if { [file isfile $fpere] && [file isfile $ffils] } {
			if { [wokUtils:FILES:AreSame $fpere $ffils] } {
			    $Fout files = $date $e [file dirname $ffils] [file dirname $fpere]
			} else {
			    $Fout files # $date $e [file dirname $ffils] [file dirname $fpere]
			}
		    }
		}
	    }
	}
    }

    return
}
#;>
# Lit un report et charge :
#                           1. La banniere dans banner (liste de 3 elements)
#                           2. les UDs dans table ( index = name(type) )
#                           3. Les ReleasesNotes dans notes (liste de n elements)
#;<
proc wokPrepare:Report:Read { name table banner notes } {
    upvar $table TLOC $banner BLOC $notes NLOC
    set l [wokUtils:FILES:FileToList $name]
    set BLOC [lrange $l 0 2]
    set is [lsearch  -regexp $l (^is$) ]
    set NLOC [lrange $l [expr $is+1] [expr [llength $l]-2] ]
    foreach x [lrange $l 5 [expr $is -1]] {
	set uheader [wokPrepare:Report:UnitHeader decode $x]
	if { $uheader != {} } {
	    set key $uheader
	    set TLOC($key) {}
	} else {
	    set l $TLOC($key)
	    set TLOC($key) [lappend l $x]
	}
    }
    return
}
#;>
# ecrit station workshop workbench sur fileid
#;<
proc wokPrepare:Report:WriteInfo { workshop wbpere wbfils {fileid stdout}} {
    puts $fileid [format "Workshop   :  %s" $workshop];
    puts $fileid [format "Master workbench  :  %s" $wbpere];
    puts $fileid [format "Revision workbench  :  %s\n" $wbfils];
    return
}
#;>
# retourne station workshop workbench 
#;<
proc wokPrepare:Report:ListInfo { workshop wbpere wbfils {fileid stdout}} {
    return [list \
	    [format "Workshop   :  %s" $workshop]\
	    [format "Master workbench :  %s" $wbpere]\
	    [format "Revision workbench  :  %s" $wbfils]\
    ]
}
#;>
# decode info (liste de 3 elements ) dans les variables qui suivent
#;<
proc wokPrepare:Report:ReadInfo { info workshop wbpere wbfils } {
    upvar $workshop wsloc $wbpere wbpereloc $wbfils wbfilsloc
    regexp {Workshop   :  (.*)} [lindex $info 0] ignore wsloc
    regexp {Master workbench  :  (.*)} [lindex $info 1] ignore wbpereloc
    regexp {Revision workbench  :  (.*)} [lindex $info 2] ignore wbfilsloc
    return
}
#;>
# Init d'une global pour utiliser simplement les types de Wok.
# (Voir wokPrepare:Report:UnitHeader)
#
#;<
proc wokPrepare:Report:InitTypes {} {
    global WOKVC_STYPE WOKVC_LTYPE
    set ucreateP \
	    [list {p package} {s schema} {i interface} {C client} {e engine} {x executable}\
	    {n nocdlpack} {t toolkit} {r resource} {O documentation} {c ccl} {f frontal}\
	    {d delivery} {I idl} {S server} {j jini} {m module}]
    foreach itm $ucreateP {
	set shrt [lindex $itm 0]
	set long [lindex $itm 1]
	set WOKVC_STYPE($shrt) $long 
	set WOKVC_LTYPE($long) $shrt
    }
    return
}
#;>
# Encode/decode un nom d'UD dans un report
# code   "Technos.nocdlpack"        -> "  * Technos (nocdlpack):" 
# decode "  * Technos (nocdlpack):" -> "Technos.nocdlpack" {} si le regexp n'est pas trouvee
# tolong "Doc.r"                    -> {resource Doc}
# longto {resource Doc}             -> "Doc.r"
# stol et ltos self explan.             
# default "Technos nocdlpack" -> Technos.nocdlpack (Utilise comme index des tables)
#;<
proc wokPrepare:Report:UnitHeader {option string} { 
    global WOKVC_LTYPE  WOKVC_STYPE 
    switch $option {
	code {
	    set uheader  [regexp {(.*)\.(.*)} $string all udname type]
	    return [format "  * %s (%s):" $udname $type]
	}
	
	decode {
	    set uheader  [regexp { \* (.*) \((.*)\):} $string all udname type]
	    if { $uheader } {
		return ${udname}.$WOKVC_LTYPE($type)
	    } else {
		return {}
	    }
	}

	tolong {
	    set l [split $string .]
	    return [list  $WOKVC_STYPE([lindex $l 1]) [lindex $l 0]]
	}

	stol {
	    if [info exists WOKVC_STYPE($string)] {
		return $WOKVC_STYPE($string)
	    }
	}

	ltos {
	    if [info exists WOKVC_LTYPE($string)] {
		return $WOKVC_LTYPE($string)
	    }
	}


	default {
	    return ${option}.${string}
	}
    }
}
#;>
#
# Appele pour sortir un report sur fileid
#
proc wokPrepare:Report:replicate { s tim} { 
    for {set i 0} {$i < $tim} {incr i 1} {
	append ret $s
    }
    return $ret
}
#;<
proc wokPrepare:Report:Output { opt args } {

    global wokfileid
    set fileid $wokfileid

    switch $opt {

	banner {
	    set shop [lindex $args 0]
	    set wbpere [lindex $args 1]
	    set wbfils [lindex $args 2]
	    set buf [wokPrepare:Report:replicate _ 30]
	    set buf_path [wokPrepare:Report:replicate _ 61]
	    wokPrepare:Report:WriteInfo $shop $wbpere $wbfils $fileid
	    puts $fileid [format "    S   Date   Time  Name"];
	    puts $fileid [format "    _ ________ _____ %s %s" $buf $buf_path];
	}

	uheader {
	    puts $fileid ""
	    puts $fileid [wokPrepare:Report:UnitHeader code $args]
	    puts $fileid ""
	}

	files {
	    set flag [lindex $args 0]
	    set date [lindex $args 1]
	    set e [lindex $args 2]
	    switch -- $flag {
		+ {
		    set dfils [lindex $args 3]
		    puts $fileid [format "    + %s %-30s %s" $date $e $dfils]
		}
		- {
		    set pnts "........................................"
		    set dpere [lindex $args 3]
		    puts $fileid [format "    - %s %-30s %s %s" $date $e $pnts $dpere]
		}
		= {
		    set dfils [lindex $args 3]
		    set dpere [lindex $args 4]
		    puts $fileid [format "    = %s %-30s %-40s %s" $date $e $dfils $dpere]
		}
		# {
		    set dfils [lindex $args 3]
		    set dpere [lindex $args 4]
		    puts $fileid [format "    # %s %-30s %-40s %s" $date $e $dfils $dpere]
		}
	    }

	}
	
	notes {
	    wokIntegre:Journal:ReleaseNotes $fileid
	}
	
    }
}
