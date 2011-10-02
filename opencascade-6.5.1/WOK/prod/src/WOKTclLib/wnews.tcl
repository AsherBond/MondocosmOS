#############################################################################
#
#                                 W N E W S
#                               _____________
#
#############################################################################
#
# Usage
#
proc wokNewsUsage { } {
    puts stderr { Usage :}
    puts stderr { }
    puts stderr { wnews [-x] [-from p1 -to p2] [-headers|-units|-comments|-all] [-command TclCmd] }
    puts stderr { }
    puts stderr {  Extract a slice of the journal file between index p1 and p2}
    puts stderr {  p1 et p2 are integration number or marks (See format below)}
    puts stderr {  If p1 is not specified, reports are extracted from the beginning of the journal file.}
    puts stderr {  If p2 is not specified, reports are extracted up to the end of the journal file.}
    puts stderr { }
    puts stderr { wnews -set markname [ -at p ] [-c "stringcomment"] [-cf filecomment]                    }
    puts stderr { }
    puts stderr {  Place a mark at the index p. p is a integration number. }
    puts stderr {  If is not given,the mark is placed at the end of the journal.}
    puts stderr {  stringcomment is a comment for the mark. You can give a file name, by using -cf option. }
    puts stderr { }
    puts stderr { wnews -ls [-bydate]}
    puts stderr { }
    puts stderr {  List the marks. If -bydate is specified thay are listed in the order they were created.}
    puts stderr {  Otherwise they are listed in order according to their place in the journal file.}
    puts stderr { }
    puts stderr { wnews -rm markname}
    puts stderr { }
    puts stderr {  Remove the mark markname}
    puts stderr { }
    puts stderr { wnews -admin }
    puts stderr { }
    puts stderr {  Display journal location, date and other informations.}
    puts stderr { }
    puts stderr { wnews -purge }
    puts stderr { }
    puts stderr {     Save the journal file and creates a new empty one.}
    puts stderr { }
    puts stderr { Additionals options : }
    puts stderr { }
    puts stderr { -o file <name> redirect output in file. This option is ignored if -command is specified.}
    puts stderr { -ws <shop>     uses journal of <shop> instead of the current one. <shop> must belongs to }
    puts stderr {                the current factory.}
    return
}
#
# Point d'entree de la commande
#
proc wnews { args } {

    set tblreq(-h)         {}
    set tblreq(-v)         {}
    set tblreq(-x)         {}
    set tblreq(-from)      value_required:string
    set tblreq(-to)        value_required:string
    set tblreq(-headers)   {}
    set tblreq(-units)     {}
    set tblreq(-comments)  {}        
    set tblreq(-all)       {}        
    set tblreq(-command)   value_required:string
    set tblreq(-userdata)  value_required:string
   
    set tblreq(-rm)        value_required:string
    set tblreq(-set)       value_required:string
    set tblreq(-at)        value_required:number

    set tblreq(-c)         value_required:string
    set tblreq(-cf)        value_required:string


    set tblreq(-ls)        {} ;#default
    set tblreq(-bydate)    {}

    set tblreq(-admin)     {}
    set tblreq(-purge)     {}

    set tblreq(-o)         value_required:string

    set tblreq(-wb)        value_required:string

    set disallow(-x)      {-set -ls -rm -admin -purge }
    set disallow(-admin)  {-set -ls -rm -purge }
    set disallow(-bydate) {-set -rm -admin -purge }

    set param {}

    if { [wokUtils:EASY:GETOPT param tabarg tblreq wokNewsUsage $args] == -1 } return
    if { [wokUtils:EASY:DISOPT tabarg disallow wokNewsUsage ] == -1 } return


    set VERBOSE [info exists tabarg(-v)]

    if { $param != {} } {
	wokNewsUsage 
	return
    }

    if { [info exists tabarg(-h)] } {
	wokNewsUsage 
	return
    }

    if [info exists tabarg(-wb)] {
	set curwb $tabarg(-wb)
    } else {
	if { [set curwb [wokinfo -w [wokcd]]] == {} } {
	    msgprint -c WOKVC -e "Current location [wokcd] is not a workbench."
	    return
	}
    }

    if { [wokStore:Report:SetQName $curwb] == {} } {
	return
    }
    
    if { ![file exists [set journal [wokIntegre:Journal:GetName]]] } { 
	msgprint -c WOKVC -e "Journal file [wokIntegre:Journal:GetName] not found."
	return
    }

    if [info exists tabarg(-x)] {
	if [info exists tabarg(-o)] {
	    if ![ catch { set newsfileid [ open $tabarg(-o) w ] } ] {
		set newsoutput [list puts $newsfileid]
	    } else {
		msgprint -c WOKVC -e "Fail to open $tabarg(-o) for writing."
		return
	    }
	} else {
	    set newsoutput [list puts stdout]
	}

	set command wokNewsSlicer
	set userdata [list [info exists tabarg(-all)] [info exists tabarg(-headers)] \
		[info exists tabarg(-units)] [info exists tabarg(-comments)] ]
	lappend userdata $newsoutput

	if [info exists tabarg(-command)] {
	    set command $tabarg(-command)
	    set userdata {}
	    if [info exists tabarg(-userdata)] {
		set userdata $tabarg(-userdata)
	    }
	} 

	set end [expr { [wokIntegre:Number:Get] - 1 } ]
	set mark_from 1
	if [info exists tabarg(-from)] { 
	    if { [string toupper $tabarg(-from)] == "END" } {
		set mark_from $end
	    } else {
		set mark_from $tabarg(-from)
	    }
	}

	set mark_to $end
	if [info exists tabarg(-to)]   { 
	    if { [string toupper $tabarg(-to)] != "END" } {
		set mark_to $tabarg(-to) 
	    }
	}

	wokNewsExtract
	if [info exists newsfileid] {
	    catch { close $newsfileid }
	}
	return
    }

    if [info exists tabarg(-set)] {
	set mark_name $tabarg(-set)
	if [info exists tabarg(-at)] {
	    set mark_value $tabarg(-at)
	} else {
	    set mark_value [wokIntegre:Number:Get]
	}
 	if { $journal != {} } {
	    if { [wokIntegre:Mark:GetTableName $journal 1] != {} } {
		wokIntegre:Mark:Set $journal $mark_name $mark_value
		if [info exists tabarg(-c)] {
		    wokIntegre:Mark:SetComment $journal string $mark_name $tabarg(-c)
		}
		if [info exists tabarg(-cf)] {
		    if [file exists $tabarg(-cf)] {
			wokIntegre:Mark:SetComment $journal file   $mark_name $tabarg(-cf)
		    } else {
			msgprint -e "File $tabarg(-cf) not found. Mark not commented"
		    }
		}
	    }
	}
	return
    }

    if [info exists tabarg(-ls)] {
	foreach x [wokIntegre:Mark:Dump $journal [info exists tabarg(-bydate)]] {
	    if [regexp {([-A-Za-z][-A-Za-z0-9]*) ([0-9]+),([0-9]+)} $x all mark index date] {
		puts stdout [format "%10s = %-3d  (placed at %s)"  $mark $index [clock format $date] ]
	    }
	}
	return
    }

    if [info exists tabarg(-rm)] {
	set mark $tabarg(-rm)
	if { $journal != {} } {
	    wokIntegre:Mark:Del $journal $mark 
	}
	return
    }

    if [info exists tabarg(-admin)] {
	puts stdout "\n Journal file in directory [file dirname $journal]  \n"
	foreach j [wokIntegre:Journal:List] {
	    puts stdout [format "%15s %-9d" [file tail $j] [file size $j]]
	}
	set t [clock format [file mtime $journal]]
	puts stdout \
		[format "%15s %-8d(Last modified %s)" [file tail $journal] [file size $journal] $t]

	set scoop [wokIntegre:Scoop:Read ]
	if { $scoop != {} } {
	    puts stdout "\n Last integration: \n\n $scoop "
	}
	puts stdout "\n Marks: \n"
	wnews -ls -wb $curwb
	return
    }

    if [info exists tabarg(-purge)] {
	wokIntegre:Journal:Purge 
	return
    }

}

proc wokNewsExtract { } {
    uplevel {
	set n1 [wokIntegre:Mark:Trn $journal $mark_from]
	set n2 [wokIntegre:Mark:Trn $journal $mark_to]
	if { $n1 != {} && $n2 != {} } {
	    set jnltmp [wokUtils:FILES:tmpname wgetslice[pid]]
	    wokIntegre:Journal:Assemble  $jnltmp  [wokIntegre:Journal:GetBigSlice $n1 $n2]
	    if { [file size $jnltmp] != 0 } {
		wokIntegre:Journal:Slice $jnltmp $n1 $n2 $command $userdata
	    }
	}
	catch { wokUtils:FILES:delete $jnltmp }
    }
    return
}
;#
;#
;#
proc wokNewsSlicer { comment table args } {
    upvar $table TLOC
    set userdata  [lindex [lindex $args 0] 0] 
    set lall      [lindex $userdata 0]
    set lheaders  [lindex $userdata 1]
    set lunits    [lindex $userdata 2]
    set lcomments [lindex $userdata 3]
    set loutput   [lindex $userdata 4]
    if { $lall   } {
	if [array exists TLOC] {
	    parray TLOC
	}
    } elseif { $lunits } {
	foreach ud [array names TLOC] {
	    eval $loutput $ud 
	}
    } elseif { $lcomments } {
	set locmt $comment
	while { [set line [ctoken locmt \n]] != "" } {
	    if { "[string range $line 0 1]" == "--" } {
		eval $loutput [list $line]
	    }
	}
    } elseif { $lheaders } {
	set locmt $comment
	while { [set line [ctoken locmt \n]] != "" } {
	    if { "[string range $line 0 5]" == "Report" } {
		eval $loutput [list $line]
	    }
	}
    }
    
    return 1 

}
#
#  ((((((((((((((((JOURNAL))))))))))))))))
#
#;>
# Ecrit un template de ReleaseNotes, si fileid = -1 retourne une liste
#;<
proc wokIntegre:Journal:ReleaseNotes { {fileid stdout} } {
    if { $fileid != -1 } {
	puts $fileid "is"
	puts $fileid "  Author        : "
	puts $fileid "  Study/CSR     : "
	puts $fileid "  Debug         : "
	puts $fileid "  Improvements  : "
	puts $fileid "  News          : "
	puts $fileid "  Deletions     : "
	puts $fileid "  Impact        : "
	puts $fileid "  Comments      : "
	puts $fileid "end;"
    } else {
	return [append dummyvar \
		"Author        : " \n \
		"Study/CSR     : " \n \
		"Debug         : " \n \
		"Improvements  : " \n \
		"News          : " \n \
		"Deletions     : " \n \
		"Impact        : " \n \
		"Comments      : " \n ]
    }
}
#;>
# Retourne vraiment n'importe quoi.
#;<
proc wokIntegre:Journal:EditReleaseNotes { {A {}} {S {}} {D {}} {I {}} {N {}} {D {}} {I {}} {C {}} } {
    return [list \
	    "Author        : $A" \
		"Study/CSR     : $S"  \
		"Debug         : $D"  \
		"Improvements  : $I"  \
		"News          : $N"  \
		"Deletions     : $D"  \
		"Impact        : $I"  \
		"Comments      : $C"  
    ]
}
# Retourne une marque unique pour ecrire dans le journal comme header de report.
# et pour mettre un commentaire des BASES
#;<
proc wokIntegre:Journal:Mark { string number rep } {
    return [format "%s:%s_%s" $string $number $rep]
}
#;>
#; inverse 
#;<
proc wokIntegre:Journal:UnMark { string } {
    if [regexp {(.*):(.*)_(.*)}  $string all strout number rep] {
	return [list $strout $number $rep]
    }
}
#;>
# Ecrit sur jnlid le header d'un report
#;<
proc wokIntegre:Journal:WriteHeader { rep num wb station {jnlid stdout}} {
    set report_out [format "%s_%s" $num $rep]
    set today   [clock format [clock seconds] -format "%d/%m/%y %R"]
    puts $jnlid [format "\n\nReport %s - %s from workbench %s (%s)" $report_out $today $wb $station]
    puts $jnlid [format "------------"]
    return
}
#;>
# Ecrit sur jnlid les strings se trouvant dans Notes precedees du separareur "--"
#;<
proc wokIntegre:Journal:WriteNotes { Notes {jnlid stdout }} {
    foreach s $Notes {
	puts $jnlid [format "-- %s" $s]
    }
    return
}
#;>
# retourne le bout du journal contenant les reports dont la date est superieure a celle donnee.
#;<
proc wokIntegre:Journal:Since { file date1 date2 } {
    return {}
}
#;>
# Ecrit dans table, le contenu d'un report file (full path). 
# Retourne le header complet du report trouve.
# Entree: Numero de report ou le mot cle end pour lire le dernier report
# Format: table (UD) = {{f1 v1} {f2 v2} ... {fn vn}}
# fi = nom (basename) du fichier. vi sa version.
#;<
proc wokIntegre:Journal:PickReport { file table notes ReportNum {action fill } } {
    upvar $table TLOC $notes NLOC


    if [ catch { set fileid [open $file r] } ] {
	return {}
    }

    set num $ReportNum

    set R_begrgx [format {^Report %s_.* -} $num]
    set U_begrgx {^  ([^ ]*) ([^ ]*) :}
    set F_begrgx {^    ([^ ]*)[ ]*:  ([^ ]*) ([^ ]*)}

    set REPORT {}
    set TEXTE {}
    
    while {[gets $fileid strin]>= 0} {
	if [regexp $R_begrgx $strin MATCHREPORT] {
	    set REPORT $strin
	    while {[gets $fileid line] >= 0} {
		if { [string compare $action fill] == 0 } {
		    if { [regexp $U_begrgx $line ignore UD status ] } {
			set  TLOC($UD) {}
		    } elseif {[regexp $F_begrgx $line ignore status name version ] } {
			set l $TLOC($UD)
			set TLOC($UD) [lappend l [list $name $version]]
		    } elseif {[regexp {^Report [0-9]*_.*} $line] } {
			break
		    }
		} else {
		    if {[regexp {^Report [0-9]*_.*} $line] } {
			break 
		    } else {
			lappend TEXTE $line
		    }
		}
	    }
	}
    }

    close $fileid
    if {[string compare $action fill] == 0} {
	return $REPORT
    } else {
	return $TEXTE
    }
}
#;>
#; Retourne la string de file entre n1 et n2
#;<
proc wokIntegre:Journal:PickMultReport { file n1 n2 } {
    if { $n1 > $n2 } {
	return {}
    }
    if [ catch { set fileid [open $file r] } err ] {
	msgprint -c WOKVC -e "$err"
	return {}
    }
    set lines [split [read $fileid] \n]
    close $fileid
    set ret {}
    set fillnow 0
    foreach line $lines {
	if {[regexp {^Report ([0-9]*)_rep } $line all num] } {
	    if { $num >= $n1 && $num <= $n2 } {
		lappend ret $line 
		set fillnow 1
	    } elseif { $num > $n2 } {
		break
	    }
	} else {
	    if { $fillnow } {
		lappend ret $line
	    }
	}
    }
    return $ret
}
#;>
# Appel function en lui passant une tranche de journal.
# function a 2 arguments: function { string table }
# <string> est la concatenation de tous les commentaires 
# Table est une map indexe par le nom.type de l'UD. Chaque entry contient la liste 
# (non purgee) des fichiers modifies avec leur numero de version. (upvar)
#;<
proc wokIntegre:Journal:Slice { journal n1 n2 function args } {
    catch {unset TLOC}
    set comments {}
    set llstr [wokIntegre:Journal:PickMultReport $journal $n1 $n2]
    foreach line $llstr {
	set lili [split $line]
	set head [lindex $lili 0]
	set updt [lindex $lili 3]
	set flag [lindex $lili 4]
	if { "$head" == "Report" || "$head" == "--" } {
	    append comments $line \n
	} elseif { "$updt" == "(Updated)" } {
	    set unit [lindex $lili 2]
	} elseif { "$flag" == "Modified" } {
	    set l {}
	    if [info exists TLOC($unit)] { set l $TLOC($unit) }
	    lappend l [list $flag [lindex [split $line :] 1]]
	    set TLOC($unit) $l
	} elseif { "$flag" == "Added" } {
	    set l {}
	    if [info exists TLOC($unit)] { set l $TLOC($unit) }
	    lappend l [list $flag [lindex [split $line :] 1]]
	    set TLOC($unit) $l
	} elseif { "$flag" == "Deleted" } {
	    set l {}
	    if [info exists TLOC($unit)] { set l $TLOC($unit) }
	    lappend l [list $flag [lindex [split $line :] 1]]
	    set TLOC($unit) $l
	}
    }
    return [$function $comments TLOC $args]
}
#;>
# Liste le nom de tous les reports enregistre dans le journal
#;<
proc wokIntegre:Journal:ListReport { file } {
    ;#Report 6_rep - 13/03/96 14:25 from workbench Yan (yfokon)
    set str [wokUtils:FILES:FileToString $file]
    set lret {}
    set R_begrgx {^Report ([0-9]+)_.* - ([0-9]+/[0-9]+/[0-9]+ [0-9]+:[0-9]+) from workbench ([^ ]*) ([^ ]*)}     
    foreach w [split $str "\n"] {
	if [regexp -- $R_begrgx $w all num dte wb mach] {
	    lappend lret [list $num $dte $wb $mach]
	}
    }
    return $lret
}
;#
;# retourne la date du report num sous forme comparable.
;#
proc wokIntegre:Journal:ReportDate { file num} {
    return {}
}

#;>
# Cree un journal tout neuf et renomme le vieux en num1-num2.jnl
#;<
proc wokIntegre:Journal:Purge { } {
    set jnl [wokIntegre:Journal:GetName ]
    if [file exists $jnl] {
	set lrep [wokIntegre:Journal:ListReport $jnl]
	set num1  [lindex [lindex $lrep 0] 0]
	set num2  [lindex [lindex $lrep end] 0]
	set savjnl [file dirname $jnl]/${num1}-${num2}.jnl
	wokUtils:FILES:rename $jnl $savjnl
	msgprint -c WOKVC -i "Creating file $jnl"
	wokUtils:FILES:ListToFile {} $jnl
	wokUtils:FILES:chmod 0777 $jnl
	return $savjnl
    } else {
	return {}
    }
}
#;>
# Retourne la liste des a-b.jnl dans l'ordre correct pour etre concatene.
#;<
proc wokIntegre:Journal:List {  } {
    set dir [file dirname [wokIntegre:Journal:GetName ]]
    set l {}
    set deb 1
    while { 1 } {
	set fxt [glob -nocomplain $dir/${deb}-*.jnl]
	if { $fxt != {} } {
	    set deb [expr { [lindex [split [file root [file tail $fxt]] -] 1] +1 }]
	    lappend l $fxt
	} else {
	    break
	}
    }
    return $l
}
#;>
# Reconstruit le  journal complet dans path.
#;<
proc wokIntegre:Journal:Assemble { path {liste {}} } {
    if [file exists $path] {
	if [catch { wokUtils:FILES:delete $path } err] {
	    msgprint -c WOKVC -e "Assemble error: $err"
	    return
	}
    }
    if { $liste == {} } {
	wokUtils:FILES:concat $path \
		[concat \
		[wokIntegre:Journal:List ] \
		[wokIntegre:Journal:GetName ] ]
    } else {
	wokUtils:FILES:concat $path $liste
    }
    return
}
#;>
# Retourne le path du bout de journal contenant le report <num>
#;<
proc wokIntegre:Journal:GetSlice { num } {
    set ljnl [wokIntegre:Journal:List ]
    foreach fxt $ljnl {
	set lll [split [file root [file tail $fxt]] -]
	if { $num >= [lindex $lll 0] && $num <= [lindex $lll 1] } {
	    return $fxt
	}
    }
    return [wokIntegre:Journal:GetName ]
}
#;>
# Retourne la liste des pathes des bouts de journal contenant les reports <num1> a <num2>
#;<
proc wokIntegre:Journal:GetBigSlice { num1 num2  } {
    set ljnl [wokIntegre:Journal:List ]
    foreach fxt $ljnl {
	set lll [split [file root [file tail $fxt]] -]
	if { $num1 >= [lindex $lll 0] && $num1 <= [lindex $lll 1] } {
	    set i1 [lsearch  $ljnl $fxt]
	    break
	}
    }

    foreach fxt $ljnl {
	set lll [split [file root [file tail $fxt]] -]
	if { $num2 >= [lindex $lll 0] && $num2 <= [lindex $lll 1] } {
	    set i2 [lsearch  $ljnl $fxt]
	    break
	}
    }

    if { [info exists i1] && [info exists i2] } {
	return [lrange $ljnl $i1 $i2]
    } elseif { [info exists i1] } {
	return [concat [lrange $ljnl $i1 end] [wokIntegre:Journal:GetName ]]
    } elseif { [info exists i2] } {
	return {}
    } else {
	return {}
    }
    
}
#  ((((((((((((((((MARK))))))))))))))))
#
#;>
# Retourne le path du fichier mark, associe a journal si create = 1 le cree s'il n'existe pas.
#;<
proc wokIntegre:Mark:GetTableName { journal {create 0} } {
    set diradm [file dirname $journal]/mark
    if [file exists $diradm] {
	return $diradm
    } else {
	if { $create } {
	    msgprint -c WOKVC -i "Creating marks file in [file dirname $diradm]"
	    catch { wokUtils:FILES:mkdir [file dirname $diradm] }
	    wokUtils:FILES:ListToFile {} $diradm
	    wokUtils:FILES:chmod 0777 $diradm
	    return $diradm
	} else {
	    return {}
	}
    }
}
#;>
# Associe un commentaire a une mark
#;<
proc wokIntegre:Mark:SetComment { journal option mark_name comment } {
    switch -- $option {
	string {
	    set l $comment
	}
	file {
	    set l [wokUtils:FILES:FileToList $comment]
	}
    }
    wokUtils:FILES:ListToFile $l [file dirname $journal]/${mark_name}.cmtmrk
    return
}
#;>
# Retourne le commentaire associe a une mark
#;<
proc wokIntegre:Mark:GetComment { journal mark_name } {
    return [wokUtils:FILES:FileToList [file dirname $journal]/${mark_name}.cmtmrk]
}
#;>
# Retourne la liste des marks associees a journal
#;<
proc wokIntegre:Mark:Dump { journal {bydate 0} } {
    set l [wokUtils:FILES:FileToList [wokIntegre:Mark:GetTableName $journal]]
    if { $bydate == 0 } {
	return [lsort -command wokIntegre:Mark:sbyindex $l]
    } else {
	return [lsort -command wokIntegre:Mark:sbydate $l]
    }
}
#;>
# Retourne la liste des marks associees a journal
#;<
proc wokIntegre:Mark:NiceDump { journal table } {
    upvar $table TLOC
    catch {unset TLOC}
    foreach x [wokUtils:FILES:FileToList [wokIntegre:Mark:GetTableName $journal]] {
	set TLOC([lindex $x 0]) [lindex $x 1]
    }
    return
}
#;>
#
#;<
proc wokIntegre:Mark:sbydate { a b } {
    set n1 [lindex [split [lindex $a 1] ,] 1]
    set n2 [lindex [split [lindex $b 1] ,] 1]
    return [expr $n1 -$n2]
}
proc wokIntegre:Mark:sbyindex { a b } {
    set n1 [lindex [split [lindex $a 1] ,] 0]
    set n2 [lindex [split [lindex $b 1] ,] 0]
    return [expr $n1 -$n2]
}
#;>
# Retourne l'index associe a la marque mark {} si elle n'existe pas.
#;<
proc wokIntegre:Mark:Get { journal mark } {
    set f [wokIntegre:Mark:GetTableName $journal]
    if { $f != {} } {
	foreach e [wokUtils:FILES:FileToList $f] {
	    if { $mark == [lindex $e 0] } {
		return [lindex [split [lindex $e 1] ,] 0]
	    }
	}
    }
    return {}
}
#;>
# Retourne la mark posee le plus recemment dans le journal.
#;<
proc wokIntegre:Mark:Last { journal } {
    set mx 0
    set str [list {} {} ]
    foreach x [wokUtils:FILES:FileToList [wokIntegre:Mark:GetTableName $journal]] {
	if { [regexp {([-A-Za-z][-A-Za-z0-9]*) ([0-9]+),([0-9]+)} $x all mark index date] } {
	    if { $date >= $mx } { 
		set mx $date
		set str [list $mark $index]
	    }
	}
    }
    return $str
}
;#
;# Retourne la date pointee par la marque 
;#
proc wokIntegre:Mark:Date { journal mark } {
    set nbr [wokIntegre:Mark:Get $journal $mark]
    return $nbr
}
#;>
# Ajoute une marque a la liste de celle associee a journal. Ecrase la precedente
# 2 marks differentes peuvent pointer sur le meme index. Pas l'inverse
# Pour mettre une mark a la fin du journal index = [expr [wokIntegre:Number:Get] -1]
#;<
proc wokIntegre:Mark:Set { journal mark index } {
    catch {unset tmark}
    set f [wokIntegre:Mark:GetTableName $journal]
    wokUtils:LIST:ListToMap tmark [wokUtils:FILES:FileToList $f]
    set tmark($mark) ${index},[clock seconds]
    wokUtils:FILES:copy $f ${f}-previous
    wokUtils:FILES:ListToFile [wokUtils:LIST:MapToList tmark] $f
    return
}
#;>
# Detruit une marque
#;<
proc wokIntegre:Mark:Del { journal mark } {
    catch {unset tmark}
    set f [wokIntegre:Mark:GetTableName $journal]
    wokUtils:LIST:ListToMap tmark [wokUtils:FILES:FileToList $f]
    catch { unset tmark($mark) }
    wokUtils:FILES:copy $f ${f}-previous
    wokUtils:FILES:ListToFile [wokUtils:LIST:MapToList tmark] $f
    return
}
#;>
#  traite un index dans le journal string = s1:s2 avec:
#  s1 == <entier>|<mark>|LAST   ( la marque posee le plus recemment )              
#  s2 == <entier>|<mark>|END    ( la fin du journal ) 
#  mark doit commencer par une lettre et ne pas contenir ":"          
#;<
proc wokIntegre:Mark:Scan { jnl string } {
    set l [split $string :]
    if { [llength $l] == 2 } {
	return [list [wokIntegre:Mark:Trn $jnl [lindex $l 0]] [wokIntegre:Mark:Trn $jnl [lindex $l 1]]]
    } else {
	return [list {} {}]
    }
}
#;>
#
#;<
proc wokIntegre:Mark:Trn { journal m } {
    set digit {^[0-9]+$}
    set regmark {^[-A-Za-z][-A-Za-z0-9]*$}
    set r {}
    if { [regexp -- $digit $m] } {
	set r $m
    } else {
	if { [wokIntegre:Mark:Check $m] } {
	    if { "$m" == "END" } {
		set r [wokIntegre:Number:Get]
	    } elseif { "$m" == "LAST" } {
		set r [lindex [wokIntegre:Mark:Last $journal] 1]
	    } elseif { [regexp -- $regmark $m] } {
		set r [wokIntegre:Mark:Get $journal $m]
	    }   
	}
    }
    return $r
}
#;> 
# 
#;<
proc wokIntegre:Mark:Check { s } {
    set regmark {^[-A-Za-z][-A-Za-z0-9]*$}
    set e1 [expr { "$s" == "END"} ]
    set e2 [expr { "$s" == "LAST"} ]
    set e3 [expr { [regexp -- $regmark $s]} ]
    return [expr $e1 || $e2 || $e3 ]
}

#;>
# Place texte dans le fichier scoop ( derniere integration faite)
# si texte = {} retourne le nom du scoop.
#;<
proc wokIntegre:Scoop:Create { {texte {}} } {
    set diradm [file join [file dirname [wokIntegre:Journal:GetName]] scoop.jnl]
    if { $texte != {} } {
	wokUtils:FILES:copy $texte $diradm
	wokUtils:FILES:chmod 0777 $diradm
    }
    return $diradm
}
#;>
# Place texte dans le fichier scoop ( derniere integration faite)
# si texte = {} retourne le nom du scoop.
#;<
proc wokIntegre:Scoop:Read { {option header} } {
    switch -- $option {

	header {
	    set scoop [wokIntegre:Scoop:Create]
	    if [file exists $scoop] {
		return [lindex [wokUtils:FILES:FileToList $scoop] 0]
	    } else {
		return {}
	    }
	}
	
    }
}
;#############################################################
