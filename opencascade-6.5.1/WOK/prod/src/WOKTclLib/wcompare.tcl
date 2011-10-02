;# mettre une selection sur les extenstions dans wprepare.
;# 
proc wcompareUsage { {GiveMore 0} } {
    puts stderr {                                                                      }
    puts stderr { Usage: wcompare dir1 dir2   [-options..]                             }
    puts stderr {                                                                      }
    puts stderr {   Compare the contents of directories under dir1(master)             }
    puts stderr {   and dir2(revision). Each file displayed is marked with a flag:     }
    puts stderr {    # indicates 2 differents files                                    }
    puts stderr {    = indicates that files in dir1 et dir2 are identicals.            }
    puts stderr {    + indicates that the file is in dir2 but not in dir1.("appeared") }
    puts stderr {    - indicates that file is in dir1 but not in dir2 .("removed")     }
    puts stderr {                                                                      }
    puts stderr { Options for output:                                                  }
    puts stderr { -hide=       : Don't display identical files (marked =)              }
    puts stderr { -o file      : Output results in file                                }
    puts stderr {                                                                      }
    puts stderr { More information with wcompare -H  , examples with wcompare -exam    }
    puts stderr {                                                                      }
    if { $GiveMore == 0 } { return                                                     }
    puts stderr { Options for filtering:                                               }
    puts stderr {                                                                      }
    puts stderr { -depth depth : Subdirectories whose level is greater than depth are  }
    puts stderr {                not compared. (Directory itself is depth =0 )         }
    puts stderr { -ext e1,e2,. : Select extension file to be compared. Extenstion must }
    puts stderr {                separated by comma, and begin with a dot (.)          }
    puts stderr {                Ex: wcompare d1 d2 -ext .cxx,.hxx,.jxx                }
    puts stderr {               See also -compare option for more sophisticated filter.}
    puts stderr { -dir d1,d2,. : Select directory names to be compared. Names can be   }
    puts stderr {                glob-style match.                                     }
    puts stderr { -Xdir d1,d2, : Same as above but excludes directory from comparison  }
    puts stderr {                                                                      }
    puts stderr { Option for modifying comparison:                                     }
    puts stderr {                                                                      }
    puts stderr {      -compare TclComm : Specify your own comparison function         }
    puts stderr {                                                                      }
    puts stderr {     TclComm is called with 2 arguments, the full pathes of the files }
    puts stderr {     to compare.                                                      }
    puts stderr {     If the script returns 1 the file will be marked # in the report  }
    puts stderr {     If the script returns 0 the file will be marked = in the report  }
    puts stderr {     By default, Comparison is done using contents of the files.      }   
    puts stderr {                                                                      }
    puts stderr { Option for acting on files according to the result of comparison:    }
    puts stderr {                                                                      }
    puts stderr {      -do TclComm : Specify a Tcl command to act on files.            }
    puts stderr {                                                                      }
    puts stderr {     TclComm is called with 5 arguments a1 a2 a3 a4 a5:               }
    puts stderr {     a1 is the string "f" or "d" to indicate the type of a3 and a4    }
    puts stderr {     "d" stands for "directory" and "f" for simple file.              }
    puts stderr {     a2 contains the result of the comparison (= - + #)               }
    puts stderr {     a3 the directory (or {} ) of the first file being compared.      }
    puts stderr {     a4 the directory (or {} ) of the second file being compared.     }
    puts stderr {     a5 the basename of the file for a plain file.                    }
    puts stderr {     In that case above options for formatting output are ignored.    }
    puts stderr {     For example such a routine could be used to update the contents  }
    puts stderr {     of dir2 (considered as the revision file ) according to dir1     } 
    puts stderr {     (considered as the master file).                                 } 
    puts stderr {                                                                      }
    puts stderr {     Examples with wcompare -exam                                     }
    return
}

proc wcompareExamples { } {
    puts stderr {                                                                                    }
    puts  { Compare 2 directories and send output in file /tmp/diff:                                 }
    puts  { >  wcompare /adv_23/WOK/k4/ref  /adv_23/WOK/k5/ref -o /tmp/diff                          }
    puts  {  }
    puts  { Same as above, exclude directories *drv*, select .cxx and .hxx files:                    }
    puts  { > wcompare /adv_23/WOK/k4/ref  /adv_23/WOK/k5/ref   -xdir *drv* -ext .cxx,.hxx           }
    puts  {  }
    puts  { Uses routine "wcompare:Quick"(*) instead of default comparison,don't display same files. }
    puts  { > wcompare -compare wcompare:Quick /dp_87/IMA/DMGR-K4B /adv_32/IGD/DMGR-A4-1 -hide=      }
    puts  {  }
    puts  { Same as above, keep *ao1* directories but exclude adm directories and hxx files:         }
    puts  { > wcompare -compare wcompare:Quick /dp_87/IMA/DMGR-K4B /adv_32/IGD/DMGR-A4-1 -dir *ao1* -xdir *.adm* }
    puts  { Compare but  do  not  examine  any directories or files below level 3 if any:            }
    puts  { > wcompare /adv_23/WOK/k4dev /adv_23/WOK/k5dev -depth 3                                  }
    puts  {  }
    puts  { Compare 2 directories, ignore sub-directories, uses proc wcompare:ExampleDo(**) to act on files.  }
    puts  { > wcompare /usr/home/guest /usr/home/me -depth 0 -do  wcompare:ExampleDo                          }
    puts  {  }
    puts  { (*)  See args and code of wcompare:Quick:   }
    puts  {     > info args wcompare:Quick              }
    puts  {     > info body wcompare:Quick              }
    puts  {  }
    puts  { (**) See args and code of wcompare:ExampleDo. (Reproduce default output of wcompare) }
    puts  {     > info args wcompare:ExampleDo           }
    puts  {     > info body wcompare:ExampleDo           }
    puts  {  }
    return
}


proc wcompare { args } {
    
    set tblreq(-h)       {}
    set tblreq(-H)       {}
    set tblreq(-o)       value_required:file
    set tblreq(-hide=)   {}
    set tblreq(-compare) value_required:string
    set tblreq(-depth)   value_required:string
    set tblreq(-do)      value_required:string
    set tblreq(-ext)     value_required:list
    set tblreq(-dir)     value_required:list
    set tblreq(-xdir)    value_required:list
    set tblreq(-exam)    {}

    set param {}
    if { [wokUtils:EASY:GETOPT param tabarg tblreq wcompareUsage $args] == -1 } return

    if [info exists tabarg(-h)] {
	wcompareUsage
	return
    }

    if [info exists tabarg(-H)] {
	wcompareUsage 1
	return
    }

    if [info exists tabarg(-exam)] {
	wcompareExamples
	return
    }

    set hidee [info exists tabarg(-hide=)]

    if { [llength $param] != 2 } {
	wcompareUsage
	return
    }

    if {  [file exists  [set d1 [lindex $param 0]]] } {
	if { ![file isdirectory $d1] } {
	    puts stderr "$d1 is not a directory"
	    return
	}
    } else {
	puts  stderr "Directory $d1 does not exists."
	return
    }
    
    if {  [file exists [set d2 [lindex $param 1]]] } {
	if { ![file isdirectory $d2] } {
	    puts  stderr "$d2 is not a directory"
	    return
	}
    } else {
	puts  stderr "Directory $d2 does not exists."
	return
    }

    if [info exists tabarg(-o)] {
	if [ catch { set fileid [ open $tabarg(-o) w ] } status ] {
	    puts stderr "$status"
	    return
	}
    } else {
	set fileid stdout
    }

   
    if [info exists tabarg(-do)] {
	set DoFunc $tabarg(-do)
    } else {
	set DoFunc {}
    }

    set CompareFunc wokUtils:FILES:AreSame
    if [info exists tabarg(-compare)] {
	set CompareFunc $tabarg(-compare)
    }

    set gblist {}
    if [info exists tabarg(-ext)] {
	foreach e $tabarg(-ext) {
	    lappend gblist $e
	}
    }

    wokUtils:FILES:DirToMap $d1 mas 
    wokUtils:FILES:DirToMap $d2 rev 
    
    if [info exists tabarg(-depth)] {
	set depth [expr $tabarg(-depth) + 1]
	foreach ky [array names mas] {
	    if { [expr [llength [split $ky /]] -1] >= $depth } {
		unset mas($ky)  
	    }
	}
	foreach ky [array names rev] {
	    if { [expr [llength [split $ky /]] -1] >= $depth } {
		unset rev($ky)  
	    }
	}
    }
    
    if [info exists tabarg(-dir)] {
	foreach ptn $tabarg(-dir) {
	    foreach ky [array names mas] {
		if ![string match $ptn $ky] {
		    unset mas($ky)
		}
	    }
	    foreach ky [array names rev] {
		if ![string match $ptn $ky] {
		    unset rev($ky)
		}
	    }
	}
    }

    if [info exists tabarg(-xdir)] {
	foreach ptn $tabarg(-xdir) {
	    foreach ky [array names mas] {
		if [string match $ptn $ky] {
		    unset mas($ky)
		}
	    }
	    foreach ky [array names rev] {
		if [string match $ptn $ky] {
		    unset rev($ky)
		}
	    }
	}
    }

    ;#
    ;# Bay gio chung minh phai lam viec.
    ;#
    set lcom [wokUtils:LIST:i3 [array names mas] [array names rev]]

    if { $DoFunc !={} } {
	foreach dir [lsort [lindex $lcom 1]] {
	    $DoFunc d # $d1$dir $d2$dir {}
	    wokUtils:LIST:SimpleDiff COMP $mas($dir) $rev($dir) $gblist
	    if [array exists COMP] {
		wokUtils:LIST:CompareAllKey COMP $CompareFunc
		foreach f [lsort [array names COMP]] {
		    switch -- [lindex $COMP($f) 0] {
			= {
			    $DoFunc f = [file dirname [lindex $COMP($f) 1]] \
				    [file dirname [lindex $COMP($f) 2]] $f
			} 
			# {
			    $DoFunc f # [file dirname [lindex $COMP($f) 1]] \
				    [file dirname [lindex $COMP($f) 2]] $f
			}
			- {
			    $DoFunc f - [lindex $COMP($f) 1] {} $f
			}
			
			+ {
			    $DoFunc f + {} [lindex $COMP($f) 1] $f
			} 
		    }
		}
	    }
	    foreach dir [lsort [lindex $lcom 0]] { $DoFunc d - $d1$dir {} {} }
	    foreach dir [lsort [lindex $lcom 2]] { $DoFunc d + {} $d2$dir {} }   
	}
    } else {
	set pnts "                                        "
	foreach dir [lsort [lindex $lcom 1]] {
	    puts $fileid "\n## Directory $d1$dir and $d2$dir\n "
	    wokUtils:LIST:SimpleDiff COMP $mas($dir) $rev($dir) $gblist
	    if [array exists COMP] {
		foreach e [lsort [array names COMP]] {
		    set flag [lindex $COMP($e) 0]
		    set f1 [lindex $COMP($e) 1]/$e
		    set f2 [lindex $COMP($e) 2]/$e
		    if { [string compare $flag ?] == 0 } {
			if { [$CompareFunc $f1 $f2] == 1 } {
			    if { $hidee == 0 } {
				puts $fileid [format "    = %-30s %-40s %s" $e [lindex $COMP($e) 1] [lindex $COMP($e) 2]]
			    }
			} else {
			    puts $fileid [format "    # %-30s %-40s %s" $e [lindex $COMP($e) 1] [lindex $COMP($e) 2]]
			}
		    } elseif { "$flag" == "+" } {
			puts $fileid [format "    + %-30s %s %s" $e $pnts [lindex $COMP($e) 1]]
		    } elseif { "$flag" == "-" } {
			puts $fileid [format "    - %-30s %s %s" $e [lindex $COMP($e) 1] $pnts]
		    }
		}
	    }
	}

	foreach dir [lsort [lindex $lcom 0]] {
	    puts $fileid "\n-- Directory  $d1$dir\n"
	    foreach f [wokUtisl:EASY:readdir $d1$dir] {
		puts $fileid [format "    - %-30s %s %s" $f $d1$dir $pnts]  
	    }
	}
	
	foreach dir [lsort [lindex $lcom 2]] {
	    puts $fileid "\n++ Directory  $d2$dir\n"
	    foreach f [wokUtisl:EASY:readdir $d2$dir] {
		puts $fileid [format "    + %-30s %s %s" $f $pnts $d2$dir]
	    }   
	}
	
	if { [string match file* $fileid] } {
	    close $fileid
	}
    }
    return
}

proc wcompare:ExampleDo { type flag f1 f2 f} {
    if { "$type" == "f" } {
	switch -- $flag {
	    = {
		puts  [format "    = %-30s %-40s %s" $f $f1 $f2]
	    }
	    # {
		puts [format "    # %-30s %-40s %s" $f $f1 $f2]
	    }			
	    - {
		set pnts "                                        "
		puts [format "    - %-30s %s %s" $f $f1 $pnts]
	    }
	    + {
		set pnts "                                        "
		puts [format "    - %-30s %s %s" $f $pnts $f2]
	    }
	}
    } else {
	switch -- $flag {
	    # {
		puts  "\n## Directory $f1 and $f2\n "
	    }
	    - {
		puts  "\n-- Directory  $f1\n"
		set pnts "                                        "
		foreach f [wokUtisl:EASY:readdir $f1] {
		    puts [format "    + %-30s %s %s" $f $f1 $pnts]
		}
	    }
	    + {
		puts "\n++ Directory  $f2\n"
		set pnts "                                        "
		foreach f [wokUtisl:EASY:readdir $f2] {
		    puts [format "    + %-30s %s %s" $f $pnts $f2]
		}
	    }
	}
    }
    return
}

proc wcompare:Quick { f1 f2 } {
    if { [file mtime $f1] != [file mtime $f2] } {
	set ls1 [file size $f1]
	set ls2 [file size $f2]
	if { $ls1 == $ls2 } {
	    set id1 [open $f1 r] 
	    set id2 [open $f2 r]
	    set s1 [read $id1 $ls1]
	    set s2 [read $id2 $ls2]
	    close $id1
	    close $id2
	    if { $s1 == $s2 } {
		return 1
	    } else {
		return 0
	    }
	} else {
	    return 0 
	}
    } else {
	return 1
    }
}
