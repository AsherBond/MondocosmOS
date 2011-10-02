
#############################################################################
#
#                              W I N T E G R E
#                              _______________
#
#############################################################################
#
# Usage
#
proc wokIntegreUsage { } {
    puts stderr { }    
    puts stderr { usage : wintegre [ <reportID> ]}
    puts stderr { }        
    puts stderr {          <reportID>  is a number. The range of the report in the queue.}
    puts stderr {          You get this number by using the command : wstore -wb <name> -ls }
    puts stderr {          where <name> is the workbench used a the storage reference.}
    puts stderr { }        
    puts stderr {  -all      : Process all reports in the queue. }
    puts stderr { }
    puts stderr {  -wb Wbnam : Use Wbnam as working workbench. }
    puts stderr {              Default is the current workbench. }
    puts stderr { -trig file : Use trigger defined in <file>. Trigger must be a Tcl proc }
    puts stderr {              whose name and definition is wintegre_trigger { table notes num } }
    puts stderr {              This proc is invoked for each integration and receive 3 arguments: }
    puts stderr {              table reflects the contents of the integration. (units and files)  }
    puts stderr {              notes contains comments of integration }
    puts stderr {              num the integration number.            } 
    puts stderr { }
    puts stderr {  -param    : Show the current value of parameters. }
    return
}
#
# Point d'entree de la commande
#
proc wintegre { args } {

    set tblreq(-h)         {}
    set tblreq(-all)       {}
    set tblreq(-wb)        value_required:string 
    set tblreq(-v)         {}
    set tblreq(-trig)      value_required:file 
    set tblreq(-param)     {}

    set param {}
    if { [wokUtils:EASY:GETOPT param tabarg tblreq wokIntegreUsage $args] == -1 } return

    set VERBOSE [info exists tabarg(-v)]

    if { [set trig [info exists tabarg(-trig)]] } {
        if { [file exists [set trigfile $tabarg(-trig)]] } {
            uplevel #0 source $trigfile
            if { "[info procs wintegre_trigger]" == "wintegre_trigger" } {
                set trig 1
            } else {
                msgprint -c WOKVC -e "Sourcing $trigfile does not create proc named wintegre_trigger. Ignored"
                set trig 0
            }
        } else {
            msgprint -c WOKVC -e "File $trigfile not found. Ignored"
            set trig 0
        }
    } else {
        set trig 0
    }

    if { [info exists tabarg(-h)] } {
        wokIntegreUsage 
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

    if { [info exists tabarg(-param)] } {
        msgprint -c WOKVC -i "Workbench $curwb :"
        msgprint -c WOKVC -i "Welcome new units ?: [wokIntegre:RefCopy:Welcome]"
        msgprint -c WOKVC -i "Reports queue  under [wokStore:Report:GetRootName]" 
        msgprint -c WOKVC -i "Repository ([wokIntegre:BASE:GetType]) under [wokIntegre:BASE:GetRootName]" 
        msgprint -c WOKVC -i "Integration counter in : [wokIntegre:Number:GetName]"
        msgprint -c WOKVC -i "Integration journal in : [wokIntegre:Journal:GetName]"

        return
    }

    if { [info exists tabarg(-all)] } {
        set LISTREPORT [wokStore:Report:Get all]
    } else {
        if { [llength $param] == 1 } {
            set ID [lindex $param 0]
            set LISTREPORT [wokStore:Report:Get $ID]
        } else {
           wokIntegreUsage 
            return -1 
        }
    }
        
    if { [set BTYPE [wokIntegre:BASE:InitFunc]] == {} } {
        return -1
    }

    if { ![file exists [set broot [wokIntegre:BASE:GetRootName]]] } {
        msgprint -c WOKVC -e "The repository does not exists."
        return -1
    }

    if ![file writable $broot] {
        msgprint -c WOKVC -e "You cannot write in $broot."
        return -1
    }


    wokIntegrebase

    return
}
#;>
# Miscellaneous: Assemblage traitement avec base
#;<
proc wokIntegrebase  { } {
    uplevel {
        foreach REPORT $LISTREPORT {
            if { $VERBOSE } { msgprint -c WOKVC -i "Processing report in $REPORT" }
            set num [wokIntegre:Number:Get]
            if { [wokUtils:FILES:dirtmp [set dirtmp /tmp/wintegre[pid]]] == -1 } {
                msgprint -c WOKVC -e "Unable to create working directory"
                return -1
            }
            set jnltmp $dirtmp/wintegre.jnl
            set jnlid [open $jnltmp w]
            set comment [wokIntegre:Journal:Mark $curwb $num rep]
            ;#
            ;# Lecture du report
            ;#  
            set stat [wokStore:Report:Process normal $REPORT table info notes]
            if { $stat == -1 } {
                wokIntegreCleanup $broot table [list $jnlid] $dirtmp 
                return -1
            }
            wokPrepare:Report:ReadInfo $info workshop wmaster workbench
            if ![info exists workbench] {
                msgprint -c WOKVC -e "Old format report. Use wprepare to create a new one."
                return -1
            }

            set version [wokIntegre:Version:Get]

            ;# 1. Bases temporaires : Ecriture de la commande
            ;# 
            set cmdtmp $dirtmp/wintegre.cmd
            set cmdid [open $cmdtmp w]
            
            wokIntegre:BASE:UpdateRef $broot table $version $comment $cmdid
            wokIntegre:BASE:EOF $cmdid  
	    close $cmdid

            ;#
            ;# 1 bis. Tester [id user] peut ecrire dans le workbench qui sert de REFCOPY
            ;#
            set write_ok [wokIntegre:RefCopy:Writable table [wokIntegre:RefCopy:GetWB]]
            if { $write_ok == -1 } {
                msgprint -c WOKVC -e "You cannot write or create units in the workbench $curwb"
                wokIntegreCleanup $broot table [list $cmdid $jnlid] $dirtmp 
                return -1
            }
            
            ;#
            ;# 2. Bases temporaires : Execution et ecriture journal temporaire
            ;#    
            wokIntegre:Journal:WriteHeader rep $num $workbench xxxxx $jnlid
            
            set statx [wokIntegre:BASE:Execute $VERBOSE $cmdtmp $jnlid] 
            if { $statx != 1 } {
                set cmd [file tail $cmdtmp]
                wokUtils:FILES:copy $cmdtmp $cmd
                wokIntegreCleanup $broot table [list $cmdid $jnlid] $dirtmp 
                msgprint -c WOKVC -e "occuring while creating temporary bases. Repository not modified."
                msgprint -c WOKVC -e "Dump script in file [pwd]/$cmd"
                return -1
            }
            
            ;#
            ;# 3. Ecriture Bases definitives
            ;#
            foreach UD [lsort [array names table]] {
                msgprint -c WOKVC -i [format "Updating unit %s in repository" $UD]
                wokIntegre:BASE:Fill $broot/$UD [wokIntegre:BASE:BTMPCreate $broot $UD 0]
            }
            ;#
            ;# 4. Fermer le journal temporaire
            ;#
            wokIntegre:Journal:WriteNotes $notes $jnlid  
	    close $jnlid
            set saved_notes $notes
            ;#
            ;# 5. Mettre a jour le journal , le scoop et le compteur 
            ;#
            wokUtils:FILES:concat [wokIntegre:Journal:GetName] $jnltmp
            wokIntegre:Scoop:Create $jnltmp
            
            if { [wokIntegre:Number:Put  [wokIntegre:Number:Incr ]] == {} } {
                msgprint -c WOKVC -e "during update of counter."
                wokIntegreCleanup $broot table [list $cmdid $jnlid] $dirtmp 
                return -1
            }
            
            ;#
            ;# 6. Mise a jour de CURWB et appel trigger.
            ;#
            catch {unset table}
            wokIntegre:Journal:PickReport $jnltmp table notes $num
            wokIntegre:RefCopy:GetPathes  table $curwb
            set dirtmpu /tmp/wintegrecreateunits[pid]
            catch {
                rmdir -nocomplain $dirtmpu 
                mkdir -path $dirtmpu
            }
            set chkout $dirtmpu/checkout.cmd
            set chkid  [open $chkout w]
            wokIntegre:RefCopy:FillRef  table $chkid
            wokIntegre:BASE:EOF $chkid 
            close $chkid
            msgprint -c WOKVC -i "Updating units in workbench $curwb"
            set statx [wokIntegre:BASE:Execute $VERBOSE $chkout] 
            if { $statx != 1 } {
                msgprint -c WOKVC -e "during checkout(Get). The report has not been removed."
                wokIntegreCleanup $broot table [list $chkid] [list $dirtmpu]
                return -1
            }

            ;#
            ;# 8. Activate trigger if any.
            ;#
            if { $trig } {
                msgprint -c WOKVC -i "Invoking trigger file $trigfile"
                catch { 
                    wokIntegre:Journal:Slice $jnltmp $num $num wintegre_trigger {}
                    rename wintegre_trigger {}
                } status
                if { $status != {} } {
                    msgprint -c WOKVC -w "Trigger status = $status"
                }
            }

            wokIntegreCleanup $broot table [list $chkid] [list $dirtmpu]
            
            ;#
            ;# 9. Detruire le report et menage
            ;#
            wokStore:Report:Del $REPORT 
            wokIntegreCleanup $broot table [list $cmdid $jnlid] [list $dirtmp]
            
        }
    }
}
#;>
#
# Miscellaneous: Fait le menage apres wintegre
#
# listid : liste de file descripteur a fermer
# dirtmp : liste de repertoire  a demolir
# table  : liste des UDs contenant une base temporaire
#;<
proc wokIntegreCleanup { broot table listid dirtmp } {
    upvar table TLOC

    foreach UD [array names TLOC] {
        wokIntegre:BASE:BTMPDelete $broot $UD
    }
    if [info exists listid] {
        foreach id $listid {
            catch { close $id }
        }
    }
    if [info exists dirtmp] {
        foreach d $dirtmp {
            catch { wokUtils:FILES:removedir $d }
        }
    }
    return
}
#;>
# Charge l'interface necessaire pour acceder aux bases de la factory.
# Se fait en fonction du type de repository code dans le parametre VC_TYPE
#
#;<
proc wokIntegre:BASE:InitFunc { } {
    global env
    set wdir $env(WOK_LIBRARY)
    set type [wokIntegre:BASE:GetType]
    if { $type != {} } {
        set interface $wdir/WOKVC.$type
        if [file exist $interface] {
            uplevel #0 source $interface
            return $type
        } else {
            msgprint -c WOKVC -e "File $interface not found."
            return {}
        }
    } else {
        msgprint -c WOKVC -w "Unknown type for source repository."
        return {}
    }
}
#;>
#######################################################################
# Updater la reference : Ecriture du fichier de commande base temporaire
#
# table  : table des UDs a traiter ( il ya des flags + - # )
# vrs    : version  a utiliser
# comment: Commentaire a coller dans l'historique PAS DE BLANC
# fileid : file descriptor
#######################################################################
#;<
proc wokIntegre:BASE:UpdateRef { broot table vrs comment fileid } {
    upvar table TLOC
    foreach UD [lsort [array names TLOC]] {
        set tmpud [wokIntegre:BASE:BTMPCreate $broot $UD 1]
        puts $fileid [format "echo Processing unit : %s" $UD]
        puts $fileid [format "cd %s" $tmpud]
        set root $broot/$UD
        foreach ELM $TLOC($UD) {
            set mark [lindex $ELM 0]
            set F [lindex $ELM 1]
            set bna [file tail $F]
            set sfl $root/[wokIntegre:BASE:ftos $bna $vrs]
            switch -- $mark {
                
                + {
                    if [file exists $sfl] {
                        ;#puts "Coucou: reapparition de $sfl"
                        wokIntegre:BASE:UpdateFile $sfl $vrs $comment $F $fileid
                    } else {
                        wokIntegre:BASE:InitFile $F $vrs $comment \
                                $tmpud/[wokIntegre:BASE:ftos $bna $vrs] $fileid
                    }
                }
                
                # {
                    if [file exists $sfl] {
                        wokIntegre:BASE:UpdateFile $sfl $vrs $comment $F $fileid
                    } else {
                        wokIntegre:BASE:InitFile $F $vrs $comment \
                                $tmpud/[wokIntegre:BASE:ftos $bna $vrs] $fileid
                    }
                }
                
                - {
                  wokIntegre:BASE:DeleteFile $bna $fileid
                }
            }
        }
    }
    return
}
#;>
#######################################################################
# Init d'une reference: Ecriture du fichier de commande base temporaire
#
# table  : table des UDs a traiter
# vrs    : version de base a creer
# comment: Commentaire a coller dans l'historique
# fileid : file descriptor
########################################################################
#;<
proc wokIntegre:BASE:InitRef { broot table vrs comment fileid } {
    upvar table TLOC
    foreach UD [lsort [array names TLOC]] {
        set tmpud [wokIntegre:BASE:BTMPCreate $broot $UD 1]
        puts $fileid [format "echo Processing unit : %s" $UD]
        puts $fileid [format "cd %s" $tmpud]
        set root $broot/$UD
        foreach F $TLOC($UD) {
            set bna [file tail $F]
            set sfl $root/[wokIntegre:BASE:ftos $bna $vrs]
            if [file exists $sfl] {
                wokIntegre:BASE:ReInitFile $sfl $vrs $comment $F $fileid
            } else {
                wokIntegre:BASE:InitFile $F $vrs $comment $tmpud/[wokIntegre:BASE:ftos $bna $vrs] $fileid
            }
        }
    }
    return
}
#;>
# Remplit une base Bname avec les elements de elmin (full pathes)
# Si la base n'existe pas la cree.   
# Par defaut le remplissage se fait avec frename (mv)
# Pour faire une copie (cp) action = copy (pas traite)
# Seuls les fichiers commencant par s. sont traites
# (sfiles ou des directories de sfiles)
#;<
proc wokIntegre:BASE:Fill { broot elmin {action move} } {
    set bdir $broot
    if ![file exists $bdir] {
        mkdir -path $bdir
        chmod 0777 $bdir
    }
 
    foreach e $elmin {
        if { [file isfile $e] } {
            set bna [file tail $e]
            catch { frename $e $bdir/$bna }
        } elseif { [file isdirectory $e] } {
            set dl {}
            foreach f [wokUtils:EASY:readdir $e] {
                lappend dl $e/$f
            }
            wokIntegre:BASE:Fill $broot $dl $action
        }
    }
    return $bdir
}
#;>
# Detruit une base Bname. 
#;<
proc wokIntegre:BASE:Delete {  Bname } {
    if [catch { exec rm -rf [wokIntegre:BASE:GetRootName]/$Bname } status ] {
        msgprint -c WOKVC -e "BASE:Delete $status"
        return -1
    } 
    return 1
}
#;>
# retourne 1 si le user courant peut ecrire dans les base de l'atelier courant
#;<
proc wokIntegre:BASE:Writable { } {
    return [file writable [wokIntegre:BASE:GetRootName]]
}
#;>
# retourne la liste des bases sous la forme { {name ext} ... {name ext} } 
#;<
proc wokIntegre:BASE:LS { } {
    set l {}
    set r [wokIntegre:BASE:GetRootName ]
    if [file exists $r] {
        foreach e [lsort [wokUtils:EASY:readdir $r]] {
            if { [string compare [file type $r/$e] file] != 0 } {
                lappend l [list [file root $e] [file extension $e]]
            }
        }
    }
    return $l
}
#;>
# retourne le nom de la base temporaire associee a une Unit. Si create la cree
#;<
proc wokIntegre:BASE:BTMPCreate { broot Unit {create 0} } {
    if { $create } {
        wokIntegre:BASE:BTMPDelete $broot $Unit
        mkdir -path $broot/$Unit/tmp
    }
    return $broot/$Unit/tmp
}
#;>
# detruit la base temporaire associee a une Unit.
# Le directory est vide puis detruit. Il y a un seul niveau
# Le directory courant ne doit pas etre unit/tmp
#;<
proc wokIntegre:BASE:BTMPDelete { broot Unit } {
    set R $broot/$Unit/tmp
    if [file exists $R] {
        foreach f [wokUtils:EASY:readdir $R] {
            unlink $R/$f
        }
        rmdir -nocomplain $R
    }
    return 1
}
#
#  ((((((((((((((((REFCOPY))))))))))))))))
#
#;>
#   Check owner et fait ucreate si necessaire des UDs de table
#   1. ucreate -p workbench:NTD si owner OK    
#;<
proc wokIntegre:RefCopy:Writable { table workbench } {
    upvar $table TLOC
    foreach UD [array names TLOC] {
        regexp {(.*)\.(.*)} $UD ignore name type
        if { [lsearch [w_info -l ${workbench}] $name ] == -1 } {
            ucreate -$type ${workbench}:${name}
        }
        set dirsrc [wokinfo -p source:. ${workbench}:${name}]
        if ![file writable $dirsrc] {
            msgprint -c WOKVC -e "You cannot write in directory $dirsrc"
            return -1
        }
    }
    return 1
}

#;>
#   1. Met en tete des elements de table (liste) le liste le full path du repertoire a alimenter
#      SOUS RESERVE QUE LES UDS aient deja ete crees.
#   Input:   table(NTD.p) = { {toto.c 2.1} {titi.c 4.3} } 
#   Output:  table(NTD.p) = { /home/wb/qqchose/NTD/src {toto.c 2.1} {titi.c 4.3} }
#;<
proc wokIntegre:RefCopy:GetPathes { table workbench } {
    upvar $table TLOC
    foreach UD [array names TLOC] {
        regexp {(.*)\.(.*)} $UD ignore name type
        if { [lsearch [w_info -l $workbench] $name ] != -1 } {
            set lsf $TLOC($UD)
            set TLOC($UD) [linsert $lsf 0 [wokinfo -p source:. ${workbench}:${name}]] 
        } else {
            msgprint -c WOKVC -e "(GetPathes) Unit $name not found in $workbench"
            return -1
        }
    }
    return 1
}
#;>
#   Modifie si c'est possible les protections  des elements de table (liste) 
#   si ils appartiennent a <user>
#   Utilise par wget en reference
#   Input:  table(NTD.p) = { /home/wb/qqchose/NTD/src {toto.c 2.1} {titi.c 4.3} }
#;<
proc wokIntegre:RefCopy:SetWritable { table user } {
    upvar $table TLOC
    foreach UD [array names TLOC] {
        set dirsrc [lindex $TLOC($UD) 0]
        foreach e [lrange $TLOC($UD) 1 end] {
            set file $dirsrc/[lindex $e 0]
            if [file owned $file] {
                chmod u+w $file
            } else {
                msgprint -c WOKVC -e "Protection of $file cannot be modified (File not found or not owner)."
                return -1
            }
        }
    }
    return 1
}
#;>
# Ecriture du fichier de commande pour remplir ce qui se trouve decrit dans table
# (format :Journal:PickReport modifie par wokIntegre:RefCopy:Getpathes )
# Si un fichier a creer existe deja et est writable, il est renomme en -sav
# Comportement correspondant au remplissage du workbench de reference
#;<
proc wokIntegre:RefCopy:FillRef {  table {fileid stdout} } {
    upvar $table TLOC
    foreach UD [array names TLOC] {
        set lsf $TLOC($UD)
        set dirsrc [lindex $lsf 0]
        puts $fileid "cd $dirsrc"
        set root [wokIntegre:BASE:GetRootName ]/$UD
        set i [llength $lsf]
        while { $i > 1 } {
            set i [expr $i-1]
            set elm  [lindex $lsf $i]
            set vrs  [lindex $elm 1]
            set file [lindex $elm 0]
            if { [string compare $vrs x.x] != 0 } {
                if [file writable $dirsrc/$file] {
                    frename $dirsrc/$file $dirsrc/${file}-sav
                    msgprint -c WOKVC -i "File $dirsrc/$file renamed ${file}-sav"
                }
                set Sfile $root/[wokIntegre:BASE:ftos $file $vrs]
                wokIntegre:BASE:GetFile $Sfile $vrs $fileid
            }
        }
    }
    return
}
#;>
# Ecriture du fichier de commande pour remplir ce qui se trouve decrit dans table
# (format :Journal:PickReport modifie par wokIntegre:RefCopy:Getpathes )
# Si un fichier a creer existe deja, il n'est pas ecrase
# Comportement correspondant au remplissage d'une UD avec wget.
# On change aussi la protection du fichier cree (writable pour le user)
#;<
proc wokIntegre:RefCopy:FillUser {  table {force 0} {fileid stdout} {mask 644} } {
    upvar $table TLOC
    foreach UD [array names TLOC] {
        set lsf $TLOC($UD)
        set dirsrc [lindex $lsf 0]
        puts $fileid "cd $dirsrc"
        set root [wokIntegre:BASE:GetRootName ]/$UD
        set i [llength $lsf]
        while { $i > 1 } {
            set i [expr $i-1]
            set elm  [lindex $lsf $i]
            set vrs  [lindex $elm 1]
            set file [lindex $elm 0]
            if { [string compare $vrs x.x] != 0 } {
                if [file exists $dirsrc/$file] {
                    if { $force } {
                        if { [file writable $dirsrc/$file] } {
                            frename $dirsrc/$file $dirsrc/${file}-sav
                            msgprint -c WOKVC -i "File $dirsrc/$file renamed ${file}-sav"
                            set Sfile $root/[wokIntegre:BASE:ftos $file $vrs]
                            wokIntegre:BASE:GetFile $Sfile $vrs $fileid
                            puts $fileid [format "chmod %s %s" $mask $dirsrc/$file]
                        } else {
                            msgprint -c WOKVC -e "File $dirsrc/$file is not writable. Cannot be overwritten."
                            return -1
                        }
                    } else {
                        msgprint -c WOKVC -e "File $dirsrc/$file already exists. Not overwritten."
                    }
                } else {
                    set Sfile $root/[wokIntegre:BASE:ftos $file $vrs]
                    wokIntegre:BASE:GetFile $Sfile $vrs $fileid
                    puts $fileid [format "chmod %s %s" $mask $dirsrc/$file]
                }
            }
        }
    }
    return
}
#
#  ((((((((((((((((COMPTEUR-INTEGRATIONS))))))))))))))))
#
#;>
# Retourne le numero de l'integration suivante (celle a faire dans shop )
# Si Setup = 1 , met le compteur a 1
#;<
proc wokIntegre:Number:Get { } {
    set diradm [wokIntegre:Number:GetName]
    if [file exists $diradm] {
        return [wokUtils:FILES:FileToList $diradm]
    } else {
        return {} 
    }
}
#;>
# Ecrit number comme numero de l'integration suivante
#;<
proc wokIntegre:Number:Put {  number } {
    set diradm [wokIntegre:Number:GetName]
    if [file exists $diradm] {
        wokUtils:FILES:ListToFile $number $diradm
        return $number
    } else {
        return {}
    }
}
#;>
# Incremente le numero de l'integration 
#;<
proc wokIntegre:Number:Incr { } {
    set diradm [wokIntegre:Number:GetName]
    if [file exists $diradm] {
        set n [wokUtils:FILES:FileToList $diradm]
        return [incr n]
    } else {
        return {}
    }
}

#############################################################################
#
#                              W G E T
#                              _______
#
#############################################################################
#
# Usage
#
proc wokGetUsage { } {
    puts stderr \
            {
        Usage:
        
        wget  -wb wbnam [-f] [-ud <udname>] <filename> [-v <version>]
        wget  -wb wbnam [-f] [-ud <udname>] <filename_1> ... <filename_N>

        -wb     : Specify the workbench to copy from. This option is mandatory.
                  If the specified workbench has a repository attached to, the file(s)
                  are getted from there. In this case and if only one file is selected
                  then option -v can specify the version you want to get.
                  If the specified workbench has no repository attahec to, the file(s)
                  are directly copied form the specified workbench.

        -ud     : Specify a unit name. If it does not exist in the current workbench it is
                  created with the same type than in the origin workbench.

        -f      : Force files to be overwritten if they already exist in the current
                  unit..

        wget -l : List "gettable" files for the current unit 

    }
    return
}
#
# Point d'entree de la commande
#
proc wget { args } {

    ;# Options
    ;#
    set tblreq(-h)      {}
    set tblreq(-l)      {}
    set tblreq(-f)      {}
    set tblreq(-V)      {}
    set tblreq(-v)      value_required:string
    set tblreq(-ud)     value_required:string
    set tblreq(-wb)     value_required:string
    
    set param {}
    if { [wokUtils:EASY:GETOPT param tabarg tblreq wokGetUsage $args] == -1 } return

    set VERBOSE [info exists tabarg(-V)]

    if [info exists tabarg(-h)] {
        wokGetUsage
        return
    }

    if [info exists tabarg(-wb)] {
        set fromwb $tabarg(-wb)
    } else {
        msgprint -c WOKVC -e "Option -wb is required."
        return
    }
    

    set workbench [wokinfo -n [wokinfo -w [wokcd]]]
    if { "[wokinfo -n [wokinfo -w $fromwb]]" == "$workbench" } {
        msgprint -c WOKVC -e "Cannot get from current workbench"
        return
    }

    set fshop [wokinfo -s [wokcd]]

    if [info exists tabarg(-ud)] {
        set ud $tabarg(-ud)
    } else {
        set ud [Sinfo -u]
    }

    set forced [info exists tabarg(-f)]

    if [info exists tabarg(-v)] {
        set version $tabarg(-v)
    } else {
        catch {unset version}
    }

    if [info exists tabarg(-l)] {
        set listbase 1
    } else {
        catch {unset listbase}
    }


    if { "[wokinfo -t [wokinfo -w $fromwb]]" != "workbench" } {
        msgprint -c WOKVC -e "$fromwb is not a workbench. Nothing done."
        return
    }
    
    if { [wokStore:Report:SetQName $fromwb] != {} } {   
        if { [set BTYPE [wokIntegre:BASE:InitFunc]] == {} } {
            return -1
        }
        if { ![file exists [set broot [wokIntegre:BASE:GetRootName]]] } {
            msgprint -c WOKVC -e "The repository does not exists."
            return -1
        }
        wokGetbase
        return
    } else {
        if [info exists version] {
            msgprint -c WOKVC -w "Option -v ignored in this context"
        }
        wokGetcopy
        return
    }
}
;#
;#
;#
proc wokGetcopy { } {
    uplevel {
        if [wokinfo -x ${fromwb}:$ud] {
            set listfileinbase [uinfo -f -Tsource ${fromwb}:$ud]
        } else {
            set listfileinbase {}
        }
        
        if [info exists listbase] {
            if { $param == {} } { 
                foreach f [wokUtils:LIST:GM $listfileinbase *] {
                    puts $f
                }
            } else {
                foreach f [wokUtils:LIST:GM $listfileinbase $param] {
                    puts $f
                }
            }
            return
        }
        
        if { [set RES [wokUtils:LIST:GM $listfileinbase $param]]  == {} } {
            msgprint -c WOKVC -e "No match for $param in unit ${fromwb}:$ud. "
            return
        }
        if { ![wokinfo -x ${workbench}:$ud] } {
            ucreate ${workbench}:${ud}
        }
        
        set from [wokinfo -p source:. ${fromwb}:${ud}]
        set to [wokinfo -p source:. ${workbench}:${ud}]

        foreach e $RES {
            if { [file exists [file join $to $e]] } {
                msgprint -c WOKVC -w "Renamed [file join $to $e] [file join $to $e]-sav"
                frename [file join $to $e] [file join $to $e]-sav
            }
            if { $VERBOSE } { msgprint -c WOKVC -i "Copying [file join $from $e] to [file join $to $e]" } 
            wokUtils:FILES:copy [file join $from $e] [file join $to $e]
        }
    }
}
;#
;# 
;#
proc wokGetbase { } {
    uplevel {
        set actv [wokIntegre:Version:Get]
        set listfileinbase [wokIntegre:BASE:List $ud $actv]
        if [info exists listbase] {
            if { $param == {} } { 
                foreach f [wokUtils:LIST:GM $listfileinbase *] {
                    puts $f
                }
            } else {
                foreach f [wokUtils:LIST:GM $listfileinbase $param] {
                    puts $f
                }
            }
            return
        }
        
        if { [set RES [wokUtils:LIST:GM $listfileinbase $param]]  == {} } {
            msgprint -c WOKVC -e "No match for $param in unit $ud."
            return
        }

        if [info exists version] {
            set vrs $version
        } else {
            set vrs last:${actv} 
        }

        if { $VERBOSE } { msgprint -c WOKVC -i "Checking out version : $vrs" }  
        if { [info exists version] && [llength $RES] > 1 } {
            msgprint -c WOKVC -e "Option -v should be used with only one file to check out. Not done"
            return
        }

        set locud [woklocate -u $ud ${fshop}:${workbench}]
        if { $locud != {} } {
            set table(${ud}.[uinfo -c $locud]) [wokUtils:LIST:pair $RES $vrs 2]
        } else {
            msgprint -c WOKVC -e "Unit $ud not found. Cannot create a new one (Unknown type)."
            return -1
        }

        if { [wokIntegre:RefCopy:Writable table $workbench] == -1 } {
            return -1
        }
        wokIntegre:RefCopy:GetPathes table $workbench
        
        if { [wokUtils:FILES:dirtmp [set dirtmp /tmp/wintegrecreateunits[pid]]] == -1 } {
            msgprint -c WOKVC -e "Unable to create working directory"
            return -1
        }
        
        set chkout $dirtmp/checkout.cmd
        set chkid  [open $chkout w]
        wokIntegre:RefCopy:FillUser table $forced $chkid
        wokIntegre:BASE:EOF $chkid
        close $chkid
        
        if { $VERBOSE } {
            msgprint -c WOKVC -i "Send the following script:"
            puts [exec cat $dirtmp/checkout.cmd]
        }
        
        set statx [wokIntegre:BASE:Execute $VERBOSE $chkout] 
        if { $statx != 1 } {
            msgprint -c WOKVC -e "Error during checkout(Get)."
            msgprint -c WOKVC -e "The following script was sent to perform check-out"
            puts [exec cat $dirtmp/checkout.cmd]
        }
        
        unlink $chkout
        rmdir -nocomplain $dirtmp
        return $statx
    }
}

