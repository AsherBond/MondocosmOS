proc upack_usage { } { 
    puts stderr \
	    {
	Usage: upack -[hcrl] <Archname> [-t <Type1>,<Type2>... ]

	upack -h
	
	   Displays this text
	
	upack -c [Unit] -o Archnamecompress
	
	   Creates archive <Archname> from Unit. If Unit is not
	   specified, then uses the current one. This is the default
           option.
	
	upack -r Archname [-d TclScript]
	
	   Creates a unit in the current environment using <Archname>.
	   Basename of <Archname> is used as the unit name, its extension
	   as the unit type (package nocdlpack schema executable etc... )
	
	   If -d option is specified, uses  TclScript for backuping
	   the files from the archive.
	
	upack -l Archname
	
	   Displays the contents of <Archname>
	
	   Options -t can be used to select one or more specifics types
	
	Examples: 
	   To create  an archive with only "source" and "object" types:
	   > upack -c [Unit] -o Archname -t source,object
    
    }
    return
}

proc upack { args } {

    ;# Options
    ;#
    set tblreq(-c) default
    set tblreq(-h) {}
    set tblreq(-l) {}
    set tblreq(-r) {}
    set tblreq(-v) {}
    set tblreq(-d) value_required:file
    set tblreq(-o) value_required:file
    set tblreq(-t) value_required:list

    ;# Si on n'est sur de n'avoir que de l'ascii
    ;# 
    set tblreq(-f) {}
    set tblreq(-F) value_required:string

    ;# Parameters
    ;#
    set param {}

    if { [wokUtils:EASY:GETOPT param table tblreq upack_usage $args] == -1 } return

    if { [info exists table(-h)] } {
	upack_usage 
	return
    }

    set verbose [info exists table(-v)] 

    set typsel {}
    if { [info exists table(-t)] } {
	set typsel $table(-t)
    }

    if { [info exists table(-c)] } {
	set Uadr [lindex $param 0]
	if { $Uadr == {} } {
	    set Uadr [wokcd]
	}
	if { [info exists table(-o)] } {
	    set Zadr $table(-o)
	    if ![catch {set idar [open $Zadr w]} status] {
		if { [info exists table(-f)] } {
		    if { [info exists table(-F)] } {
			auto_load $table(-F)
			upack:Fold [$table(-F) $Uadr $verbose] $idar {} $verbose
		    } else {
			upack:Fold [uinfo -Fp -Tsource $Uadr] $idar {} $verbose
		    }
		} else {
		    if { $typsel == {} } {
			set typsel [upack:Upackable]
		    }
		    upack:Fold [wokUtils:LIST:Filter [uinfo -Fp $Uadr] upack:UOK 2] $idar $typsel $verbose
		}
		close $idar
		wokUtils:FILES:compress $Zadr
	    } else {
		puts stderr "Error: $status"
	    }
	} else {
	    upack_usage
	}
	return
    }

    if { [info exists table(-l)] } {
	set Zadr [lindex $param 0]
	set adr [wokUtils:FILES:SansZ $Zadr]
	if { $adr != -1} {
	    if ![catch {set idar [open $adr r]} status] {
		upack:LsFold $idar $typsel
		close $idar
	    } else {
		puts stderr "Error: $status"
	    }
	    if [file exists $adr] {
		catch {wokUtils:FILES:delete $adr}
	    }
	} 
	return
    }

    if { [info exists table(-r)] } {
	set Zadr [lindex $param 0]
	set adr [wokUtils:FILES:SansZ $Zadr]
	if { $adr != -1} {
	    if ![catch {set idar [open $adr r]} status] {
		set dirtmp [wokUtils:FILES:tmpname {}]
		upack:UnFold $idar stderr $dirtmp $typsel $verbose
		close $idar
	    } else {
		puts stderr "Error: $status"
	    }
	    if [file exists $adr] {
		catch {wokUtils:FILES:delete $adr}
	    }
	}
	return
    }
    
    upack_usage
    return
}
#
# Retourne le full path du fichier ou il faut restaurer le fichier 
# de nom <name> et de type <type>. Retourne -1 sinon.
# Cette fonction peut etre redefinie en fonction de ce que l'on souhaite faire
# Ce qui suit permet de recreer les fichiers dans le cadre d'une UD Wok++. deja existante.
#
proc upack:GetBackupName { type name } {
    ;#puts stdout "type = $type name = $name longeur de name : [string length $name]"
    if { [string length $name] != 0 } {
	catch {unset filename}
	if { ![catch {set filename [wokinfo -p ${type}:${name}] }] } {
	    set dna [file dirname $filename]
	    if {[file exists $dna]} {
		return $filename
	    } else {
		msgprint -w "Directory $dna not found. File $name not restaured."
		return -1
	    }
	} else {
	    msgprint -w "Unable to get type of ${type}:${name}"
	    return -1
	}
    } else {
	msgprint -w "Obsolete type $type. File $name not restaured."
	return -1
    }
}
#
# Retourne la liste des fichiers candidats a aller dans une archive de source. Si cette liste 
# est {} tout le monde y va exemple: return [list source object stadmfile]
#
proc upack:Upackable { } {
    return [list source]
}
#
# Permet de filtrer le retour de uinfo 
#
proc upack:UOK { x } {
    return [expr { [file exists $x] && ![file isdirectory $x] }]
}
#
# Depliage d'une archive de sources
# 
# FileId (entree) descripteur de l'archive de source
# errlog : descripteur du fichier ou l'on ecrit ce que l'on n'a pas pu faire
# (restaurer des types inconnus ou dont le profil ne correspond pas)
# errdir : Nom du directory ou l'on restaurera tout ce qui n'a pu l'etre dans l'UD.
# typsel : Liste des types a restaurer si {} on (tente) de tout restaurer
#
proc upack:UnFold { fileid errlog errdir {typsel {}} verbose } {
    set lu {}
    set lst [llength $typsel]
    while {[gets $fileid line] >= 0 } {
	if { [regexp {^=\+=\+=\+=\+=\+=\+=\+=\+=\+=\+ ([^ ]*) ([^ ]*)} $line ignore type name] } {
	    if [info exist fileout] {catch {close $fileout; unset fileout } }
	    if { ($lst == 0) || ( ($lst != 0) && ([lsearch $typsel $type] != -1)) } {
		set retval [upack:GetBackupName $type $name]
		if { $retval != -1 } {
		    set filename $retval
		} else {
		    puts $errlog "Error: Item $line not processed"
		    set filename $errdir/notdone
		}
		if {[string compare [file extension $retval] .U] == 0 } {
		    lappend lu $retval
		}
		if ![catch { set fileout [open $filename w] } errout] {
		    if { $verbose } { msgprint -i "Creating $filename" }
		} else {
		    msgprint -e "$errout"
		    return -1
		}
	    } else {
	    }
	} else {
	    if [info exist fileout] {
		puts $fileout $line
	    }
	}
    }
    if [info exist fileout] {catch {close $fileout; unset fileout } }
    foreach u $lu {
	puts -nonewline stderr "Decoding $u ..."
	wokUtils:FILES:uudecode $u
	wokUtils:FILES:delete $u
	puts stderr "Done"
    }
    return
}
#
# Pliage d'un liste de fichiers dans fileid (deja ouvert et checke)
# TypesAndFullPathesList : retour de uinfo -Fp
# Si typsel = {} tout le monde y va
#
proc upack:Fold { List3 fileid {typsel {}} verbose } {
    set lst [llength $typsel]
    set dirtmp [wokUtils:FILES:tmpname {}]
    foreach e $List3 {
	set type [lindex $e 0]
	if {[lsearch $typsel $type] != -1 || $lst == 0} {
	    set tnam [lindex $e 2]
	    set name $tnam
	    set code [wokUtils:FILES:Encodable $tnam]
	    if { $code != -1 } {
		set name $dirtmp/[file tail $tnam].U
		wokUtils:FILES:uuencode $tnam $name
	    }
	    if { [catch { set in [ open $name r ] } errin] == 0 } {
		if { $verbose } { msgprint -i "Processing file $name"}
		puts $fileid [format "=+=+=+=+=+=+=+=+=+=+ %s %s" $type [file tail $name]]
		puts -nonewline $fileid [read $in]
		close $in
	    } else {
		puts stderr "Error: $errin"
	    }
	    if { $code != -1 } {
		wokUtils:FILES:delete $name
	    }
	}
    }
    return
}
#
# Listing d'une archive de sources
# 
# FileId (entree) descripteur de l'archive de source
#
proc upack:LsFold { fileid {typsel {}} } {
    set lst [llength $typsel]
    while {[gets $fileid line] >= 0 } {
	if { [regexp {^=\+=\+=\+=\+=\+=\+=\+=\+=\+=\+ ([^ ]*) ([^ ]*)} $line ignore type name] } {
	    if { ($lst == 0) || ( ($lst != 0) && ([lsearch $typsel $type] != -1)) } {
		msgprint -i "$type $name"
	    } 
	}
    }
    return 
}

#
# Packer un workbench
#
proc wpack_usage { } { 
    puts stderr \
	    {
  Usage:

  To create an archive file from a workbench: 

    wpack -c [workbench] [-d Dirname | -f filename ] [-t <Type1>,<Type2>.. ] [-u Ud1,Ud2,..]
	
  Backup contents of <workbench> in ,<Dirname> or <filename>.

  If you specify -d Dirname, one archive file will be created for each unit in in directory 
  Dirname. They will be named Unit.type.Z. They can be further downloaded separatly using upack in 
  a existing workbench, or globally using -r option of wpack.
 
  If you specify -f filename, all units will be archived in one file named filename.Z. This should be 
  more convenient for mailing.

  Options -t and -u selects respectively the type and the name of the units to process.
  Wildcard as * can be used for specifying type and unit names.

  To restore an archive file:

    wpack -r [workbench] [-d Dirname | -f filename ] [-t <Type1>,<Type2>.. ] [-u Ud1,Ud2,..]

  Restores contents of <Dirname> or <filename> in <workbench>. Options -t and -u selects 
  respectively the type and the name of the units to restore. If applicable, units will be 
  automatically created and filled in with files. No test is done to check if the restored 
  files are already existing. It is recommended to create and empty workbench and restore
  archives in that workbench. Further comparison with existing files, and housekeeping should 
  be carried out using the command wprepare. 

  To create an archive file from a report created by the command wprepare:
  
    wpack -rep <ReportName> -f filename 

  This option should be used in conjonction with the command wprepare -since to package deltas sources.
  See examples (wpack -examples)

  Other options: 

  -> If applicable, source files in a parcel can be downloaded using:

    wpack -r [workbench] -p ParcelName [-t <Type1>,<Type2>.. ] [-u Ud1,Ud2,..]

  -> To list the contents of a archive file:

    wpack -l <archname>

  -> To turn on verbose mode use -v option  

  -> To get some examples: 
 
    wpack -examples

    }
    return
}

proc wpack:examples { } {
    puts stderr \
	    {
	Examples: 
	
	To pack the full workbench MDL:k4dev:ref in file /tmp/update.bck:
	> wpack -c  MDL:k4dev:ref -f /tmp/send.bck

	To pack all interface and engine of current workbench in directory /tmp/transfert:
	> wpack -c  -d /tmp/transfert -t interface,engine

	To restore the file /tmp/update.bck.Z  in workbench FAC:WS:WB
        > wcreate FAC:WS:WB ... ( if applicable )
	> wpack -r FAC:WS:WB -f /tmp/update.bck.Z
	
	To download in the current workbench, the units of delivery KERNEL-B4-1:
	> wpack -r -p MYFACT:MYBAG:KERNEL-B4-1

	To pack all sources and units modified since the mark REL1.1 (See wnews for marks)
        > wprepare -since REL1.1 -o /tmp/update-report
	> ... Comments report file /tmp/update-report ... (See also wnews -comments)
        > wpack -rep /tmp/update-report -f /tmp/update.bck
        > ... Send update.bck with mail or Internet facilities..

	To restore the previous update file in a workbench named WBUPD.
	> wcreate WBUPD -f ...
	> wokcd FACT:SHOP:WBUPD
        > wpack -r -f /tmp/update.bck 
        All units will be created and automatically filled in with the source files.

        To restore the previous update in a integration queue ( See also command wstore)
        > wstore -ar /tmp/update.bck

	To pack a workbench FAC1:SHOP1:WB1, then restore it in an other workshop queue 
        named FAC2:SHOP2.
        > wokcd FAC1:SHOP1
        > wpack -c WB1 -f /tmp/arch.bck
        > wokcd FAC2:SHOP2
        > wstore -ar /tmp/arch.bck


    }
}
proc wpack { args } {

    ;# Options
    ;#
    set tblreq(-h) {}
    set tblreq(-examples) {}
    set tblreq(-c) default
    set tblreq(-r) {}
    set tblreq(-v) {}
    set tblreq(-d) value_required:string
    set tblreq(-f) value_required:string
    set tblreq(-p) value_required:string
    set tblreq(-t) value_required:list
    set tblreq(-u) value_required:list
    set tblreq(-l) {}

    set tblreq(-rep) value_required:string

    set disallow(-d) {-f}
    set disallow(-d) {-p}
    set disallow(-c) {-r}
    ;# Parameters
    ;#
    set param {}

    if { [wokUtils:EASY:GETOPT param table tblreq wpack_usage $args] == -1 } return
    if { [wokUtils:EASY:DISOPT table disallow wpack_usage  ] == -1 } return

    if { [info exists table(-h)] } {
	wpack_usage 
	return
    }
    if { [info exists table(-examples)] } {
	wpack:examples 
	return
    }

    set verbose [info exists table(-v)] 

    if { [info exists table(-rep)] } {
	if { [info exists table(-f)] } {
	    set Zadr $table(-f)
	    if ![catch {set idar [open $Zadr w]} status] {
		wokStore:Report:Pack $idar $table(-rep) $verbose
		close $idar
		wokUtils:FILES:compress $Zadr
		msgprint -i "File ${Zadr}.Z has been created."
	    } else {
		puts stderr "$status"
	    }
	} else {
	    wpack_usage  
	}
	return
    }


    if { [info exists table(-l)] } {
	set Zadr [lindex $param 0]
	set adr [wokUtils:FILES:SansZ $Zadr]
	if { $adr != -1} {
	    if ![catch {set idar [open $adr r]} status] {
		wpack:LsFold $idar {}
		close $idar
	    } else {
		puts stderr "Error: $status"
	    }
	    if [file exists $adr] {
		catch {wokUtils:FILES:delete $adr}
	    }
	} 
	return
    }


    set typsel *
    if { [info exists table(-t)] } {
	set typsel $table(-t)
    }

    set namsel *
    if { [info exists table(-u)] } {
	set namsel $table(-u)
    }
    
    set Wadr [lindex $param 0]
    if { $Wadr == {} } {
	set Wadr [wokcd]
    }
    set Wadr [wokinfo -w $Wadr]

    if { [info exists table(-c)] } {
	set ulist [wpack:UF [w_info -a $Wadr] $namsel $typsel]
	if { $ulist == {} } {
	    msgprint -w "No units selected."
	    return
	}
	if { [info exists table(-f)] } {
	    set Zadr $table(-f)
	    if ![catch {set idar [open $Zadr w]} status] {
		foreach Uadr $ulist {
		    set typ [lindex $Uadr 0]
		    set nam [lindex $Uadr 1]
		    if { $verbose } { puts -nonewline stderr "Packing $typ $nam..." }
		    puts $idar [format "=!=!=!=!=!=!=!=!=!=! %s %s" $typ $nam]
		    upack:Fold [uinfo -Fp ${Wadr}:${nam}] $idar [upack:Upackable] 0
		    if { $verbose } { puts stderr "Done" }
		}
		close $idar
		wokUtils:FILES:compress $Zadr
		msgprint -i "File ${Zadr}.Z has been created"
	    } else {
		puts stderr "$status"
	    }
	} elseif { [info exists table(-d)] } { 
	    if { ![file exists $table(-d)] } { wokUtils:FILES:mkdir  $table(-d) } 
	    wpack:Fold $Wadr $ulist $table(-d) [info exists table(-v)]
	    msgprint -i "Archive files have been created in $table(-d)"
	} else {
	    wpack_usage
	}
	return
    }
    
    if { [info exists table(-r)] } {
	if { [info exists table(-f)] } {
	    set Zadr $table(-f)
	    set adr [wokUtils:FILES:SansZ $Zadr]
	    if { $adr != -1} {
		if ![catch {set idar [open $adr r]} status] {
		    set dirtmp [wokUtils:FILES:tmpname {}]
		    set savwd [wokcd]
		    wpack:UnFold $idar $Wadr stderr $dirtmp {} $verbose
		    close $idar
		    wokcd $savwd
		} else {
		    puts stderr "Error: $status"
		}
		if [file exists $adr] {
		    catch {wokUtils:FILES:delete $adr}
		}
	    }
	} else {
	    if { [info exists table(-d)] } {
		set Dadr $table(-d)
	    } else {
		set Dadr [pwd]
	    }
	    if { [info exists table(-p)] } {
		set ULadr $table(-p)
		set Dadr [wokinfo -p sourcedir:. $table(-p)]
	    }
	    if { ![file exists $Dadr] } {
		msgprint -e "Directory $Dadr not found."
		return
	    } 
	    if { [set LZ [wpack:LZ [glob $Dadr/*.*.Z] $namsel $typsel]] != {} } {
		set savwokcd [wokcd]
		foreach e2 [ucreate -P $Wadr] {
		    set LtoS([lindex $e2 1]) [lindex $e2 0]
		}
		set l_ud [w_info -l $Wadr]
		foreach z $LZ {
		    set x [split [file tail $z] .]
		    set nam [lindex $x 0]
		    set typ [lindex $x 1]
		    if { [lsearch $l_ud $nam] == -1 } {
			ucreate -$LtoS($typ) ${Wadr}:${nam}
		    }
		    if { $verbose } { msgprint -i "Unpacking file $z" }
		    wokcd ${Wadr}:${nam}
		    upack -r $z
		}
		wokcd $savwokcd
	    } else {
		msgprint -e "No match in directory $Dadr."
	    }
	}
	return
    }

    wpack_usage 

}
;#
;# Filtre les UDs demandees avec -u et -t
;# l2 liste des UDs a filtrer. (w_info)
;# lu liste de ce qu'il y a derriere -u
;# lt liste de ce qu'il y a derriere -t
;#
proc wpack:UF { l2 lu lt } {
    set l {}
    foreach u $lu {
	foreach t $lt {
	    foreach e2 $l2 {
		set typ [lindex $e2 0]
		set nam [lindex $e2 1]
		if { [string match $u $nam] && [string match $t $typ] } {
		    lappend l $e2
		}
	    }
	}
    }
    return $l
}
;#
;# Filtre les UDs demandees avec -u et -i
;# l1 liste des UDs a filtrer. (glob)
;# lu liste de ce qu'il y a derriere -u
;# lt liste de ce qu'il y a derriere -t
;#
proc wpack:LZ { l1 lu lt } {
    set l {}
    foreach u $lu {
	foreach t $lt {
	    foreach e1 $l1 {
		if { [string match ${u}.${t}.Z [file tail $e1] ] } {
		    lappend l $e1
		}
	    }
	}
    }
    return $l
}
;#
;# Crees les archives a partir de ulist et les met dans Dadr.
;#
proc wpack:Fold { Wadr ulist Dadr {verbose 0 } } {
    foreach e2 [lsort $ulist] {
	set typ [lindex $e2 0]
	set nam [lindex $e2 1]
	if { $verbose } { msgprint -i "Creating file $Dadr/$nam.${typ}.Z" }
	upack -f -c ${Wadr}:${nam} -o $Dadr/$nam.${typ}
    }
    
    return
}
#
# Listing d'un backup de workbench
# 
# FileId (entree) descripteur de l'archive 
#
proc wpack:LsFold { fileid {typsel {}} } {
    set lst [llength $typsel]
    while {[gets $fileid line] >= 0 } {
	if { [regexp {^=\+=\+=\+=\+=\+=\+=\+=\+=\+=\+ ([^ ]*) ([^ ]*)} $line ignore type name] } {
	    if { ($lst == 0) || ( ($lst != 0) && ([lsearch $typsel $type] != -1)) } {
		msgprint -i "$type $name"
	    } 
	} elseif {[regexp {^=!=!=!=!=!=!=!=!=!=! ([^ ]*) ([^ ]*)} $line ignore type name]} {
	    msgprint -i ">> $type $name"
	}
    }
    return 
}
;#
;# Cree dans le workbench Wadr les Uds packes dans le fichier pointe par fileid
;#
proc wpack:UnFold { fileid Wadr errlog errdir {typsel {}} verbose } {
    set lu {}
    set lst [llength $typsel]
    foreach e2 [ucreate -P $Wadr] {
	set LtoS([lindex $e2 1]) [lindex $e2 0]
    }
    set l_ud [w_info -l $Wadr]
    while {[gets $fileid line] >= 0 } {
	if { [regexp {^=\+=\+=\+=\+=\+=\+=\+=\+=\+=\+ ([^ ]*) ([^ ]*)} $line ignore type name] } {
	    if [info exist fileout] {catch {close $fileout; unset fileout } }
	    if ![string match report-* $type] {
		if { ($lst == 0) || ( ($lst != 0) && ([lsearch $typsel $type] != -1)) } {
		    set retval [upack:GetBackupName $type $name]
		    if { $retval != -1 } {
			set filename $retval
		    } else {
			puts $errlog "Error: Item $line not processed"
			set filename $errdir/notdone
		    }
		    if {[string compare [file extension $retval] .U] == 0 } {
			lappend lu $retval
		    }
		    if ![catch { set fileout [open $filename w] } errout] {
			if { $verbose } { msgprint -i "Creating $filename" }
		    } else {
			msgprint -e "$errout"
			return -1
		    }
		}
	    } else {
		if ![catch { set fileout [open [pwd]/$name w] } errout] {
		    msgprint -i "Creating [pwd]/$name"
		} else {
		    msgprint -e "$errout"
		    return -1
		}
	    }
	} elseif {[regexp {^=!=!=!=!=!=!=!=!=!=! ([^ ]*) ([^ ]*)} $line ignore typ nam]}  {
	    if ![string match report-* $typ] {
		if { [lsearch $l_ud $nam] == -1 } {
		    ucreate -$LtoS($typ) ${Wadr}:${nam}
		    if { $verbose } { msgprint -i "Creating $typ $nam " }
		}
		wokcd $nam
	    }
	} else {
	    if [info exist fileout] {
		puts $fileout $line
	    }
	}

    }
    if [info exist fileout] {catch {close $fileout; unset fileout } }
    foreach u $lu {
	puts -nonewline stderr "Decoding $u ..."
	wokUtils:FILES:uudecode $u
	wokUtils:FILES:delete $u
	puts stderr "Done"
    }
    return
}
