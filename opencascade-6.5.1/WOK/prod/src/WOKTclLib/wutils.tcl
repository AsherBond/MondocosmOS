#
# Convert a date 
# 07/03/96 11:55 => "07 Mar 96 11:55"
#
proc wokUtils:TIME:dpe { dpedateheure } {
    set dt(01) Jan;set dt(02) Feb;set dt(03) Mar;set dt(04) Apr;set dt(05) May;set dt(06) Jun 
    set dt(07) Jul;set dt(08) Aug;set dt(09) Sep;set dt(10) Oct;set dt(11) Nov;set dt(12) Dec
    regexp {(.*)/(.*)/(.*) (.*)} $dpedateheure ignore day mth yea hour
    return [convertclock "$day $dt($mth) $yea $hour"]
}
#
# Convert a date 08-Jan-94.12:05:43 to seconds 
# clock scan "Sun Nov 24 12:30 1996"
#
proc wokUtils:TIME:clr { e } {
    if {[regsub {(..)\-(...)\-(..)\.(........)} $e {\1 \2 \3 \4} f] != 0 } {
	return [clock scan $f]
    }
}
#
# Sort 2 dates in ClearCase format.
#
proc wokUtils:TIME:clrsort { e1 e2 } {
    if {[regsub {(..)\-(...)\-(..)\.(........)} $e1 {\1 \2 \3 \4} f1] != 0 } {
	if {[regsub {(..)\-(...)\-(..)\.(........)} $e2 {\1 \2 \3 \4} f2] != 0 } {
	    if { [clock scan $f1] <= [clock scan $f2] } {
		return -1
	    } else {
		return 1
	    }
	}
    }
}
;#
;# Returs for a full path the liste of n last directory part
;# n = 1 => tail
;# n = 2 => dir/file.c
;# n = 3 => sdir/dir/file.c
;# etc..
proc wokUtils:FILES:wtail { f n } {
    set ll [expr [llength [set lif [file split $f]]] -$n]
    return [join [lrange $lif $ll end] /]
}

#
# Returs the list of files in dirlist using gblist as pattern newer than lim
# 
proc wokUtils:FILES:Since { dirlist gblist lim } {
    set result {}
    set recurse {}
    foreach dir $dirlist {
        foreach ptn $gblist {
	    set ll {}
	    foreach fle [glob -nocomplain -- $dir/$ptn] {
		if { [file mtime $fle] > $lim } {
		    lappend ll $fle
		}
	    }
	    set result [concat $result $ll]
        }
        foreach file [wokUtils:EASY:readdir $dir] {
            set file $dir/$file
            if [file isdirectory $file] {
                set fileTail [file tail $file]
                if {!(($fileTail == ".") || ($fileTail == ".."))} {
                    lappend recurse $file
                }
            }
        }
    }
    if ![lempty $recurse] {
        set result [concat $result [wokUtils:FILES:Since $recurse $gblist $lim]]
    }
    return $result
}
#
# returns a list:
# First is the date and time of more recent file in dir
# Second is the accumulate size of all files
#
proc wokUtils:FILES:StatDir { dir } {
    set s 0
    set m [file mtime $dir]
    foreach f [glob -nocomplain $dir/*] {
	incr s [file size $f]
	if { [set mf [file mtime $f]] > $m } {
	    set m $mf
	}
    }
    return [list $m $s]
}
#
# Returns results > 0 if f1 newer than f2 
#
proc wokUtils:FILES:IsNewer { f1 f2 } {
    return [ expr [file mtime $f1] - [file mtime $f2] ]
}
#
# Write in table(file) the list of directories of ldir that contains file
#
proc wokUtils:FILES:Intersect { ldir table } {
    upvar $table TLOC
    foreach r $ldir {
	foreach f [wokUtils:EASY:readdir $r] {
	    if [info exists TLOC($f)] {
		set l $TLOC($f)
	    } else {
		set l {}
	    }
	    lappend l $r
	    set TLOC($f) $l
	}
    }
    return
}
;#
;# Put a list of strings in a map indexed by the n first field.
;# if sep = "/" then strings may represent pathes.
;# Hence if n = 1 => 2 first fields of the path are used for indexing.
;# The value is (a list) of the remainder path.
;# All the pathes in lpathes must contains at least n fields.
;# Example: n=1 a/b/c => map(a/b) = c
;#              a/b/d => map(a/b) = {c d } and so on.
;# if n = -1 then automatically search the longuest string index
;#
proc wokUtils:LIST:ListOfPathesToMap { lpath nf map {sep /} } {
    upvar $map TLOC

    if { $nf < 0 } {
	puts "???
    } else {
	set n $nf
    }


    set np [expr $n + 1]
    
    foreach p $lpath {
	set ll [split $p $sep]
	set k [join [lrange $ll 0 $n] $sep] 	
	if [info exists TLOC($k)] {
	    set l $TLOC($k)
	    lappend l [join [lrange $ll $np end] $sep]
	    set TLOC($k) $l
	} else {
	    set TLOC($k) [join [lrange $ll $np end] $sep]
	}
    }
}
#
# Returns 1 if name does not begin with -
#
proc wokUtils:FILES:ValidName { name } {
    return [expr ([regexp {^-.*} $name]) ? 0 : 1]
}
#
# Read a file in a string as is.
#
proc wokUtils:FILES:FileToString { fin } {
    if { [catch { set in [ open $fin r ] } errin] == 0 } {
	set strin [read $in [file size $fin]]
	close $in
	return $strin
    } else {
	return {}
    }
}
#
# Write a string in a file
#
proc wokUtils:FILES:StringToFile { str path } {
    if { [catch { set out [ open $path w ] } errout] == 0 } {
	puts -nonewline $out $str
	close $out
	return 1
    } else {
	return {}
    }
}
#
# Read file pointed to by path
# 1. sort = 1 tri 
# 2. trim = 1 plusieurs blancs => 1 seul blanc
# 3. purge= not yet implemented.
# 4. emptl= dont process blank lines
#
proc wokUtils:FILES:FileToList { path {sort 0} {trim 0} {purge 0} {emptl 1} } {
    if ![ catch { set id [ open $path r ] } ] {
	set l  {}
	while {[gets $id line] >= 0 } {
	    if { $trim } {
		regsub -all {[ ]+} $line " " line
	    }
	    if { $emptl } {
		if { [string length ${line}] != 0 } {
		    lappend l $line
		}
	    } else {
		lappend l $line
	    }
	}
	close $id
	if { $sort } {
	    return [lsort $l]
	} else {
	    return $l
	}
    } else {
	return {}
    }
}
;#
;# Unix like find, return a list of names.
;#
proc wokUtils:FILES:find { dirlist gblist } {
    set result {}
    set recurse {}
    foreach dir $dirlist {
        foreach ptn $gblist {
            set result [concat $result [glob -nocomplain -- $dir/$ptn]]
        }
        foreach file [wokUtils:EASY:readdir $dir] {
            set file $dir/$file
            if [file isdirectory $file] {
                set fileTail [file tail $file]
                if {!(($fileTail == ".") || ($fileTail == ".."))} {
                    lappend recurse $file
                }
            }
        }
    }
    if ![lempty $recurse] {
        set result [concat $result [wokUtils:FILES:find $recurse $gblist]]
    }
    return $result
}
;#
;# Returns a list representation for a directory tree 
;# l = { r {sub1 .. subn} } where sub1 .. subn as l
;#
proc wokUtils:FILES:DirToTree { d } { 
    set flst ""
    set pat [file join $d *]
    foreach f [ lsort [ glob -nocomplain $pat]] {
	if [file isdirectory $f] { 
	    set cts [wokUtils:FILES:DirToTree $f]
	} else {
	    set cts ""
	}
	lappend flst [list [file tail $f] $cts]
    } 
    return $flst
}
;#
;# Write in map all directories under d. Each index is a directory name ( trimmed by d).
;# Contents of index is the list of files in that directory
;# "Error regsub  --" peut arriver si d se termine par un slache
;#
proc wokUtils:FILES:DirToMap { d map {tail 0} } { 
    upvar $map TLOC
    catch { unset TLOC }
    set l [wokUtils:FILES:find $d *]
    set TLOC(.) {}
    foreach e $l {
	if { [file isdirectory $e] } {
	    if [regsub -- $d $e "" k] {
		set TLOC($k) {}
	    } else {
		puts "Error regsub  -- $d $e"
	    }
	} else {
	    set dir [file dirname $e]
	    if [regsub -- $d $dir "" k] {
		if { $k == {} } {
		    set k .
		}
		if [info exists TLOC($k)] {
		    set l $TLOC($k)
		    lappend l $e
		    set TLOC($k) $l
		} else {
		    set TLOC($k) $e
		}
	    } else {
		puts "Error regsub  -- $d $dir"
	    }
	}
    }

    if { $tail == 0 } { return }

    foreach x [array names TLOC] {
	set l {}
	foreach e $TLOC($x) {
	    lappend l [file tail $e]
	}
	set TLOC($x) $l
    }

    return
}
;#
;# Returns a list of Tcl statements that should be 
;# used for checking existence of files in lpath.
;# Invoked later on, procname will return elements in lpath
;# that no longer exists.
;# 
proc wokUtils:FILES:WasThere { lpath procname } {
    lappend l [format "proc $procname { } {"]
    lappend l [format "set l {}"]
    foreach f $lpath {
	set fmt [format "if { !\[file exists %s\] } {lappend l %s }" $f $f]
	lappend l $fmt
    }
    lappend l [format "return \$l\n}"]
}
;#
;# Returns a list of Tcl statements that should be 
;# used for checking date of files in lpath.
;# Invoked later on, procname will return elements in lpath
;# that have been modified and their original date.
;# 
proc wokUtils:FILES:Remember { lpath procname } {
    lappend l [format "proc $procname { } {"]
    lappend l [format "set l {}"]
    foreach f $lpath {
	set d [file mtime $f]
	set fmt \
[format "if { \[file mtime %s\] != %s } {lappend l [list %s %s]}" $f $d $f $d]
	lappend l $fmt
    }
    lappend l [format "return \$l\n}"]
}
;#
;# used to sort MAP created by wokUtils:FILES:DirToMap
;# so that tree directory is traversed "en largeur daborrhe' 
;#
proc wokUtils:FILES:Depth { d1 d2 } { 
    set n1 [regsub -all / $d1 {} nil]
    set n2 [regsub -all / $d2 {} nil]
    if { $n1 > $n2 } {
	return 1
    } else {
	return -1
    }
}
;#
;# Same as above but write a Tcl proc to perform it. Proc has 1 argument. the name of the map.
;# 
proc wokUtils:FILES:DirMapToProc { d TclFile ProcName } { 
    catch { unset TLOC }
    wokUtils:FILES:DirToMap $d TLOC 1
    if ![ catch { set id [ open $TclFile w ] } errout ] {
	puts $id "proc $ProcName { map } {"
	puts $id "upvar \$map TLOC"
	foreach x [array names TLOC] {
	    puts $id "set TLOC($x) {$TLOC($x)}"
	}
	puts $id "return"
	puts $id "}"
	close $id
	return 1
    } else {
	puts stderr "$errout"
	return -1
    }
}
;#
;# 1.Scan each element of liste
;# 2.For each element e:
;#    substr from  e of of the string of pathliste
;# Create a map indexed by the common roots
;# Each entry contains a list of file name that begins under this entry.
;# 3.Returns the list of element where substr failed.
;# CAUTION : add a slash at the end of pathes in pathliste so entries will be directly usable
;# tt [list /adv_11/KAS/C30/ref/  /adv_11/KAS/C30/UpdateC31/] FOC.lst
;# Easy for preparing Gnu tar command using option --from-file.
;#
proc wokUtils:FILES:RelativePathes { pathliste liste map } {
    upvar $map TLOC
    set ret {}
    foreach e [wokUtils:FILES:FileToList $liste] {
	set yy [wokUtils:EASY:trytrim $pathliste $e]
	if { $yy != {} } {
	    set yy0 [lindex $yy 0]
	    if [info exists TLOC($yy0)] {
		set li $TLOC($yy0)
		lappend li [lindex $yy 1]
		set TLOC($yy0) $li
	    } else {
		set TLOC($yy0) [lindex $yy 1]
	    }
	} else {
	    lappend ret $e
	}
    }
    return $ret
}
#
# Compare contents of directory d with a previous state.
# (previous state in file $d/__PreviousState.tcl)
# If new = 1 then once directory d parsed (re-)writes 
# the file $d/__PreviousState.tcl
#
proc wokUtils:FILES:MakeDirHistory { d tclfile procname } {
    wokUtils:FILES:DirMapToProc $d $d/$tclfile $procname
    
}
#
# Same as above but also returns a ordonned list of directories names
# Use it as follow
# set treelist [wokUtils:FILES:DirToTree $dir]
# wokUtils:FILES:DirToH MAP root "" $treelist
#
proc wokUtils:FILES:DirToH { var node label info } { 
    upvar #0 $var data
    set data($node-label) $label
    set data($node-children) ""
    set num 0
    foreach rec $info {
	set subnode "$node-[incr num]"
	lappend data($node-children) $subnode
	set sublabel [lindex $rec 0]
	set subinfo [lindex $rec 1]
	wokUtils:FILES:DirToH  $var $subnode $sublabel $subinfo
    }
}
#
# Concat all files in lsfiles. Writes the result in result
#
proc wokUtils:FILES:concat { result lstfile } {
    if ![ catch { set id1 [ open $result a ] } errout ] {
	foreach file2 $lstfile {
	    if ![ catch { set id2 [ open $file2 r ] } ] {
		puts $id1 [read -nonewline $id2]
	    }
	    close $id2
	}
	close $id1
	return 1
    } else {
	puts stderr "$errout"
	return -1
    }
}
#
# returns the concatenation of lines in file <path> i.e. with the following rules:
# If a line has format:<mark> <string> then calls func with args to get a full path
# In all the case, append the string as is.
#
# Ex: wokUtils:FILES:rconcat [pwd]/file @ myfunc MS source
# 
# with 
#
#proc myfunc { basename_file args } {
#    set  ud  [lindex $args 0]
#    set type [lindex $args 1]
#    return   [woklocate -f ${ud}:${type}:${basename_file}]
#}
#
proc wokUtils:FILES:rconcat { path mark func args } {
    if ![ catch { set id [ open $path r ] } errin ] {
	while {[gets $id line] >= 0 } {
	    set sl [split $line]
	    if { "[lindex $sl 0]" == "$mark" } {
		set file [eval $func [lindex $sl 1] $args]
		append str [eval wokUtils:FILES:rconcat $file $mark $func $args] 
	    } else {
		append str $line \n
	    }
	}
	close $id
	return $str
    } else {
	puts stderr "Error : $errin"
	return ""
    }
}
#
# Creates a file. If string is not {} , writes it.
# 
proc wokUtils:FILES:touch { path { string {} } { nonewline {} } } {
    if [ catch { set id [ open $path w ] } status ] {
	puts stderr "$status"
	return 0
    } else {
	if { $string != {} } {
	    if { $nonewline != {} } {
		puts -nonewline $id $string
	    } else {
		puts $id $string
	    }
	}
	close $id
	return 1
    }
}
#
# Writes a list in path.
# 
proc wokUtils:FILES:ListToFile { liste path } {
    if [ catch { set id [ open $path w ] } ] {
	return 0
    } else {
	foreach e $liste {
	    puts $id $e
	}
	close $id
	return 1
    }
}
#
#
#
proc wokUtils:FILES:AppendListToFile { liste path } {
    if [ catch { set id [ open $path a ] } ] {
	return 0
    } else {
	foreach e $liste {
	    puts $id $e
	}
	close $id
	return 1
    } 
}
;#
;# substr "" in elements of lp (path part) in fname
;# returns a liste of 2 elements : e1 e2
;# e1 1 st element that match
;# e2 fname after substr example:
;# tt:parseloc {/adv_11/KAS/C30/ref/  /adv_11/KAS/C30/UpdateC31/} /adv_11/KAS/C30/UpdateC31/README
;# => /adv_11/KAS/C30/UpdateC31/ README
;# tt:parseloc {/adv_11/KAS/C30/ref/  /adv_11/KAS/C30/UpdateC31/} /adv_11/KAS/C30/ref/src/gp/gp_Pnt.cxx
;# => /adv_11/KAS/C30/ref/ src/gp/gp_Pnt.cxx
;#
proc wokUtils:EASY:trytrim { lp pname } {
    set res {}
    foreach p $lp {
	if [regsub $p $pname "" dst] {
	    set res [list  $p $dst]
	    break
	}
    }
    return $res
}
#
# Replace s1 by s2 in fin and write result in fout
# return 1 if substitution was performed.
#
proc wokUtils:EASY:replace { fin fout s1 s2  } {
    if { [catch { set in [ open $fin r ] } errin] == 0 } {
	if { [catch { set out [ open $fout w ] } errout] == 0 } {
	    set strin [read $in [file size $fin]]
	    close $in
	    set done 0
	    if { [set nbsub [regsub -all -- $s1 $strin $s2 strout]] != 0 } {
		set done 1
	    }
	    puts $out $strout
	    close $out
	    return $done
	} else {
	    puts stderr "Error: $errout"
	    return 0
	}
    } else {
	    puts stderr "Error: $errin"
	return 0
    }
}
#
# Same as Replace but l is a list of couple {s1 s2}
# return 1 if at least one substitution has been done.
# This proc differs of the above one for lazyness purpose.
#
proc wokUtils:EASY:lreplace { fin fout ls1s2 } {
    if { [catch { set in [ open $fin r ] } errin] == 0 } {
	if { [catch { set out [ open $fout w ] } errout] == 0 } {
	    set strin [read $in [file size $fin]]
	    close $in
	    foreach [list s1 s2] $ls1s2 {
		set done 0
		if { [set nbsub [regsub -all -- $s1 $strin $s2 strout]] != 0 } {
		    set done 1
		}
		set strin $strout
	    }
	    puts $out $strout
	    close $out
	    return $done
	} else {
	    puts stderr "Error: $errout"
	    return 0
	}
    } else {
	    puts stderr "Error: $errin"
	return 0
    }
}
#
# Compares 2 full pathes for TEXT ASCII files. Returs 1 if identicals 0 ifnot
#
proc wokUtils:FILES:AreSame { f1 f2 } {
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
}
#
# Copy file
#
proc wokUtils:FILES:copy { fin fout } {
    global tcl_version
    regsub -all {\.[^.]*} $tcl_version "" major
    if { $major == 8 } {
	file copy -force $fin $fout
    } else {
	if { "[info command copyfile]" == "copyfile" } {
	    if { [catch { set in [ open $fin r ] } errin] == 0 } {
		if { [catch { set out [ open $fout w ] } errout] == 0 } {
		    set nb [copyfile $in $out]
		    close $in 
		    close $out
		    return $nb
		} else {
		    puts stderr "Error: $errout"
		    return -1
		}
	    } else {
		puts stderr "Error: $errin"
		return -1
	    }
	} else {
	    puts stderr "wokUtils:FILES:copy : Error unable to find a copy command."
	}
    }
}
#
# Rename a file
#
proc wokUtils:FILES:rename { old new } {
    global tcl_version
    regsub -all {\.[^.]*} $tcl_version "" major
    if { $major == 8 } {
	file rename -force -- $old $new
    } else {
	if { "[info command frename]" == "frename" } {
	    frename $old $new
	} else {
	    puts stderr "wokUtils:FILES:rename : Error unable to find a rename command."
	}
    }
}
#
# chmod a lfile ( chmod 0777 file : the best way to process..)
#
proc wokUtils:FILES:chmod { m  lf } {
    global tcl_version
    regsub -all {\.[^.]*} $tcl_version "" major
    if { $major == 8 } {
	foreach f $lf {
	    file attributes $f -permissions $m
	}
    } else {
	if { "[info command chmod]" == "chmod" } {
	    chmod $m $lf 
	} else {
	    puts stderr "wokUtils:FILES:chmod : Error unable to find a chmod command."
	}
    }
}

proc wokUtils:FILES:mkdir { d } {
    global tcl_version
    regsub -all {\.[^.]*} $tcl_version "" major
    if { $major == 8 } {
	file mkdir $d
    } else {
	if ![file exists $d] {
	    if { "[info command mkdir]" == "mkdir" } {
		mkdir -path $d
	    } else {
		puts stderr "wokUtils:FILES:mkdir : Error unable to find a mkdir command."
	    }
	}
    }
    if [file exists $d] {
	return $d
    } else {
	return {}
    }
}

#
# Delete a list of files: Either Tcl 8.x or Tcl 7.x or later and Tclx.
#
proc wokUtils:FILES:delete { lf } {
    global tcl_version
    regsub -all {\.[^.]*} $tcl_version "" major
    if { $major == 8 } {
	foreach f $lf {
	    file delete -- $f
	}
    } else {
	if { "[info command unlink]" == "unlink" } {
	    foreach f $lf {
		unlink -nocomplain $f
	    }
	} else {
	    puts stderr "wokUtils:FILES:delete : Error unable to find a delete command."
	}
    }
}
#
# Returns a list of selected files
#
proc wokUtils:FILES:ls  { dir {select all} } {
    set l {}
    if { [file exists $dir] } {
	foreach f [wokUtils:EASY:readdir $dir] {
	    set e [file extension $f]
	    switch -- $select {
		all {
		    if {![regexp {[^~]~$} $f] && ![string match *.*-sav* $e]} {
			lappend l $f 
		    }
		}
		
		cdl {
		    if { [string compare $e .cdl] == 0 } {
			lappend l $f 
		    }
		}
		
		cxx {
		    if { [string compare $e .cxx] == 0 } {
			lappend l $f
		    }
		}
		
		others {
		    if { [string compare $e .cdl] !=0 && [string compare $e .cxx] != 0 } {
			lappend l $f
		    }
		}
		
	    }
	}
    }
    return  [lsort $l]
}
;#
;#
;#
proc  wokUtils:FILES:FindFile { startDir namePat } {
    set pwd [pwd]
    if [catch {cd $startDir} err] {
	puts stderr $err
	return
    }
    foreach match [glob -nocomplain -- $namePat] {
	puts stdout [file join $startDir $match]
    }
    foreach file [glob -nocomplain *] {
	if [file isdirectory $file] {
	     wokUtils:FILES:FindFile [file join $startDir $file] $namePat
	}
    }
    cd $pwd
}
;#
;# Copy src in dest and translate to native format
;# src qnd dest can be directory.
;# at least Tcl 7.6 (file mkdir ..) if not uses Tclx
;# Basic use wokUtils:FILES:NatCopy  pth1 pth2
;#
proc wokUtils:FILES:NatCopy { src dest {verbose 0} {YesOrNo wokUtils:EASY:NatCopy} } {
    global tcl_version
    if [file isdirectory $src] {
	wokUtils:FILES:mkdir $dest
	foreach f [glob -nocomplain [file join $src *]] {
	    wokUtils:FILES:NatCopy $f [file join $dest [file tail $f]] $verbose $YesOrNo
	}
	return
    }

    if [file isdirectory $dest] {
	set dest [file join $dest [file tail $src]]
    }

    if [$YesOrNo $src] {
	if { $verbose } { puts stderr "Converting $src" }
	set in [open $src]
	set ws [read $in]
	close $in
	set out [open $dest w]
	puts -nonewline $out $ws
	close $out 
    }
}
#
# Compress /decompress fullpath
#
proc wokUtils:FILES:compress { fullpath } {
    if [catch {exec compress -f $fullpath} status] {
	puts stderr "Error while compressing ${fullpath}: $status"
	return -1
    } else {
	return 1
    }
}
proc wokUtils:FILES:uncompress { fullpath } {
    if [catch {exec uncompress -f $fullpath} status] {
	puts stderr "Error while uncompressing ${fullpath}: $status"
	return -1
    } else {
	return 1
    }
}

#
# Uncompresse if applicable Zin in dirout, returns the full path of uncompressed file
# ( if Zin is not compresses returns Zin)
# returns -1 if an error occured
#
proc wokUtils:FILES:SansZ { Zin } {
    if { [file exists $Zin] } {
	if {[string compare [file extension $Zin] .Z] == 0 } {
	    set dirout [wokUtils:FILES:tmpname {}]
	    set bnaz [file tail $Zin]
	    if { [string compare $Zin $dirout/$bnaz] != 0 } {
		wokUtils:FILES:copy $Zin $dirout/$bnaz
	    }
	    if { [wokUtils:FILES:uncompress $dirout/$bnaz] != -1 } {
		return $dirout/[file root $bnaz]
	    } else {
		return -1
	    }
	} else {
	    return $Zin
	}
    } else {
	puts stderr "Error: $Zin does not exists."
	return -1
    }
}
#
# uuencode
#
proc wokUtils:FILES:uuencode { fullpathin fullpathout {codename noname}} {
    if {[string compare $codename noname] == 0} {
	set codename [file tail $fullpathin]
    }
    if [catch {exec uuencode $fullpathin $codename > $fullpathout } status] {
	puts stderr "Error while encoding ${fullpathin}: $status"
	return -1
    } else {
	return 1
    }
}
#
# uudecode
#
proc wokUtils:FILES:uudecode { fullpathin {dirout noname}} {
    if {[string compare $dirout noname] == 0} {
	set dirout [file dirname $fullpathin]
    }
    set savpwd [pwd]
    cd $dirout
    if [catch {exec uudecode $fullpathin} status] {
	set ret -1
    } else {
	set ret 1
    }
    cd $savpwd
    return $ret
}
#
# Returns something != -1 if file must be uuencoded
#
proc wokUtils:FILES:Encodable { file } {
    return [lsearch {.xwd .rgb .o .exe .a .so .out .Z .tar} [file extension $file]]
}
# 
# remove a directory. One level. Very ugly procedure. Do not use.
# Bricolage pour que ca marche sur NT.
# 
proc wokUtils:FILES:rmdir { d } {
    global env
    global tcl_platform tcl_version  
    regsub -all {\.[^.]*} $tcl_version "" major
    if { $major == 8 } {
	file delete -force $d
    } else {
	if { "$tcl_platform(platform)" == "unix" } {
	    catch { exec rm -rf $d}
	} else {
	    
	}
    }
    return 
}
#
# returns a string used for temporary directory name
#
proc wokUtils:FILES:tmpname { name } {
    global env
    global tcl_platform tcl_version
    if { "$tcl_platform(platform)" == "unix" } {
	return [file join /tmp $name]
    } elseif { "$tcl_platform(platform)" == "windows" } {
	return [file join $env(TMP) $name]
    }
    return {}
}
#
# userid. 
#
proc wokUtils:FILES:Userid { file } {
    global env
    global tcl_platform tcl_version
    regsub -all {\.[^.]*} $tcl_version "" major
    if { "$tcl_platform(platform)" == "unix" } {
	if { $major == 8 } {
	    return [file attributes $file -owner]
	} else {
	    if { "[info command id]" == "id" } {
		file stat $file myT
		if ![ catch { id convert userid $myT(uid) } result ] {
		    return $result
		} else {
		    return unknown
		}
	    } else {
		return unknown
	    }
	}
    } else {
	return unknown
    }
}
#
# Try to supply a nice diff utility name
#
proc wokUtils:FILES:MoreDiff { } {
    global tcl_platform
    if { "$tcl_platform(platform)" == "unix" } {
	if [wokUtils:EASY:INPATH xdiff] {
	    return xdiff
	} else {
	    return {}
	}
    } elseif { "$tcl_platform(platform)" == "windows" } {
	return windiff
    } else {
	return {}
    }
}
#
# dirtmp one level
#
proc wokUtils:FILES:dirtmp { tmpnam } {
    if [file exist $tmpnam] {
	wokUtils:FILES:rmdir $tmpnam
    }
    wokUtils:FILES:mkdir $tmpnam
    return 
}
;#
;# Recursive copy of dir in dest . 
;# Date of files in dest are modified since they are newly created.
;# FunCopy is the function called to perform the copy.
;# It receives 2 arguments :
;# 1. Full path of the source file.
;# 2. Full path of the destination file.
;#
proc wokUtils:FILES:recopy { dir dest  {verbose 0} {FunCopy wokUtils:FILES:copy} } { 
    wokUtils:FILES:DirToMap $dir MAP
    foreach odir [lsort -command wokUtils:FILES:Depth [array names MAP]] {
	regsub  {^/} $odir {} sd
	set did [file join $dest $sd]
	if { ![file exists $did] } {
	    if { $verbose } { puts stderr "Creating directory $did" }	    
	    wokUtils:FILES:mkdir -path $did
	} else {
	    if { $verbose } { puts stderr "Directory $did already exists. " }	    
	}
	foreach f $MAP($odir) {
	    if { $verbose } { puts stderr "Creating file [file join $did [file tail $f]]" }
	    $FunCopy $f [file join $did [file tail $f]]
	}
    }
    return
}
;#
;#
;#
proc  wokUtils:FILES:html { file } {
    global tcl_platform
    if { "$tcl_platform(platform)" == "unix" } {
	set cmd "exec netscape -remote \"openFile($file)\""
	if { [catch $cmd] != 0 } {
	    exec netscape &
	    while { [catch $cmd] != 0 } { 
		after 500
	    }
	}
    } elseif { "$tcl_platform(platform)" == "windows" } {
	set cmd [list exec netscape $file &]
	if { [catch $cmd] != 0 } {
	    set prog [tk_getOpenFile -title "Where is Netscape ?"]
	    if { $prog != "" } {
		puts $prog
		exec $prog $file &
	    }
	}
    }
    return    
}
#
# l1 U l2 
#
proc wokUtils:LIST:union { l1 l2 } {
    set l {}
    foreach e [concat $l1 $l2] {
	if { [lsearch $l $e] == -1 } {
	    lappend l $e
	} 
    }
    return $l
}
#
# l1 - l2
#
proc wokUtils:LIST:moins { l1 l2 } {
    set l {}
    foreach e $l1 {
	if { [lsearch $l2 $e] == -1 } {
	    lappend l $e
	}
    }
    return $l
}
#
# Do something i cannot remenber, 
# 
proc wokUtils:LIST:subls { list } {
    set l {}
    set len [llength $list]
    for {set i 0} {$i < $len} {incr i 1} {
	lappend l [lrange $list 0 $i]
    }
    return $l
}
#
# { 1 2 3 } =>  { 3 2 1 }
#
proc wokUtils:LIST:reverse { list } { 
    set ll [llength $list]
    if { $ll == 0 } {
	return
    } elseif { $ll == 1 } {
	return $list
    } else {
	return [concat [wokUtils:LIST:reverse [lrange $list 1 end]] [list [lindex $list 0]]]
    }
}
#
# flat a list: { a {b c} {{{{d}}}e } etc.. 
#            =>   { a b c d e }
#
proc wokUtils:LIST:flat { list } {
    if { [llength $list] == 0 } {
	return {}
    } elseif { [llength [lindex $list 0]] == 1 } {
	return [concat [lindex $list 0] [wokUtils:LIST:flat [lrange $list 1 end]]]
    } elseif { [llength [lindex $list 0]] > 1 } {
	return [concat [wokUtils:LIST:flat [lindex $list 0]] [wokUtils:LIST:flat [lrange $list 1 end]]]
    }
}
#
# returns 3 lists l1-l2 l1-inter-l2 l2-l1
#
proc wokUtils:LIST:i3 { l1 l2 } {
    set a1(0) {} ; unset a1(0)
    set a2(0) {} ; unset a2(0)
    set a3(0) {} ; unset a3(0)
    foreach v $l1 {
        set a1($v) {}
    }
    foreach v $l2 {
        if [info exists a1($v)] {
            set a2($v) {} ; unset a1($v)
        } {
            set a3($v) {}
        }
    }
    list [lsort [array names a1]] [lsort [array names a2]]  [lsort [array names a3]]
}
#
# returns all elements of list matching of the expr in lexpr
# Ex: GM [glob *] [list *.tcl *.cxx A*.c]
#
proc wokUtils:LIST:GM { list lexpr } {
    set l {}
    foreach expr $lexpr {
	foreach e $list {
	    if [string match $expr $e] {
		if { [lsearch $l $e] == -1 } {
		    lappend l $e
		}
	    }
	}
    }
    return $l
}
#
# returns the longer prefix that begin with str in inlist ( Completion purpose.)
#
proc wokUtils:LIST:POF { str inlist } {
    set list {}
    foreach e $inlist {
	if {[string match $str* $e]} {
	    lappend list $e
	}
    }
    if { $list == {} } {
	return [list {} {}]
    }
    set l [expr [string length $str] -1]
    set miss 0
    set e1 [lindex $list 0]
    while {!$miss} {
	incr l
	if {$l == [string length $e1]} {
	    break
	}
	set new [string range $e1 0 $l]
	foreach f $list {
	    if ![string match $new* $f] {
		set miss 1
		incr l -1
		break
	    }
	}
    }
    set match [string range $e1 0 $l]
    set newlist {}
    foreach e $list {
	if {[string match $match* $e]} {
	    lappend newlist $e
	}
    }
    return [list $match $newlist]
}
#
# Split l in to p list of max n elements.
# then llength(l) = p*n + r
#
proc wokUtils:LIST:split { l n } {
    set i 0
    foreach e $l {
	incr i
	if { $i <= $n } {
	    lappend bf $e
	} else {
	    set i 1
	    if [info exists bf] {
		lappend res $bf
		set bf $e
	    }
	}
    }
    lappend res $bf
    return $res
}
#
# pos = 1 {{a b c } x} => { {x a} {x b} {x c} } default
# pos = 2 {{a b c } x} => { {a x} {a x} {a x} }
#
proc wokUtils:LIST:pair { l e {pos 1}} {
    set r {}
    if { $pos == 1 } {
	foreach x $l {
	    lappend r [list $e $x ]
	}
    } else {
	foreach x $l {
	    lappend r [list $x $e ]
	}
    }

    return $r
}
#
# { {x a} {x b} {x c} } => {a b c}
#
proc wokUtils:LIST:unpair { ll } {
    set r {}
    foreach x $ll {
	lappend r [lindex $x 1]
    }
    return $r
}
#
# keep in list of form ll = { {x a} {x b} {x c} } all elements which "cdr lisp" is in l
#
proc wokUtils:LIST:selectpair { ll l } {
    set rr {}
    foreach x $ll {

	if { [lsearch $l [lindex $x 1]] != -1 } {
	    lappend rr $x
	}
    }
    return $rr
}
#
# { a.x b.c c.v } => { a b c}
#
proc wokUtils:LIST:sanspoint { l } {
    set rr {}
    foreach x $l {
	lappend rr [file root $x]
    }
    return $rr
}
#
# sort elements of l, according to key 
# key is the field number of an element considered as a string  
# command is invoked and receive the 2 fields.
#
proc wokUtils:LIST:sort { l key {sep " "} {mode -ascii} {order -increasing}} {
    foreach e $l {
	puts $e
	set x [split $e $sep]
	puts "x = $x"
	set map([lindex $x $key]) $e
    }
    parray map
    set le_tour_est_joue {}
    foreach e [lsort $mode $order [array names map]] {
	lappend le_tour_est_joue $map($e)
    }
    return $le_tour_est_joue
}
#
# sort a list of pairs
#
proc wokUtils:LIST:Sort2 { ll } {
    catch { unset tw }
    foreach x $ll {
	set e [lindex $x 0]
	if [info exists tw($e)] {
	    set lw $tw($e)
	    lappend lw [lindex $x 1]
	    set tw($e) $lw
	} else {
	    set tw($e) [lindex $x 1]
	}
    }
    set l {}
    foreach x  [lsort [array names tw]] {
	foreach y [lsort $tw($x)] {
	    lappend l [list $x $y]
	}
    }
    return $l
}
#
# Purge a list. Dont modify order
#
proc wokUtils:LIST:Purge { l } {
    set r {}
     foreach e $l {
	 if ![info exist tab($e)] {
	     lappend r $e
	     set tab($e) {}
	 } 
     }
     return $r
}
#
# Purge and sort a list.
#
proc wokUtils:LIST:SortPurge { l } {
     foreach e $l {
	 set tab($e) {}
     }
     return [lsort [array names tab]]
}
#
#
# trim a list
#
proc wokUtils:LIST:Trim { l } {
    set r {}
    foreach e $l {
	if { $e != {} } {
	    set r [ concat $r $e]
	}
    }
    return $r
}
#
# truncates all strings in liststr which length exceed nb char
# 
proc wokUtils:LIST:cut { liststr {nb 10} } {
    set l {}
    foreach str $liststr {
	set len [string length $str]
	if { $len <= [expr $nb + 2 ]} {
	    lappend l $str
	} else {
	    lappend l [string range $str 0 $nb]..
	}
    }
    return $l
}
#
# given a/b/c/d/e => returns l = {a a/b a/b/c a/b/c/d a/b/c/d/e}
#
proc wokUtils:LIST:descend { strdir {sep /} } {
    set list [split $strdir $sep]
    set l {}
    set len [llength $list]
    for {set i 0} {$i < $len} {incr i 1} {
	lappend l [join [lrange $list 0 $i] $sep]
    }
    return $l
}
#
# compares 2 lists of fulls pathes (master and revision) and fill table with the following format
# table(simple.nam) {flag path1 path2}
# flag = + => simple.nam in master but not in revision 
# flag = ? => simple.nam in master and in revision (files should be further compared)
# flag = - => simple.nam in revision but not in master 
#
proc wokUtils:LIST:SimpleDiff { table master revision {gblist {}} } {
    upvar $table TLOC
    catch {unset TLOC}
    foreach e $master {
	set key [file tail $e]
	if { $gblist == {} } {
	    set TLOC($key) [list - [file dirname $e]]
	} elseif { [lsearch $gblist [file extension $key]]  != -1 } { 
	    set TLOC($key) [list - [file dirname $e]]
	}
    }
    foreach e $revision {
	set key [file tail $e]
	set dir [file dirname $e]
	if { $gblist == {} } {
	    if { [expr { ( [lsearch -exact [array names TLOC] $key] == -1 ) ? 0 : 1 }] } {
		set TLOC($key) [list ? [lindex $TLOC($key) 1] $dir]
	    } else {
		set TLOC($key) [list + $dir]
	    }
	} elseif { [lsearch $gblist [file extension $key]]  != -1 } { 
	    if { [expr { ( [lsearch -exact [array names TLOC] $key] == -1 ) ? 0 : 1 }] } {
		set TLOC($key) [list ? [lindex $TLOC($key) 1] $dir]
	    } else {
		set TLOC($key) [list + $dir]
	    }
	}
    }
    return
}
#
# modify table ( created by wokUtils:LIST:SimpleDiff) as follows:
# substitues flag ? by = if function(path1,path2) returns 1 , by # if not
# all indexes in tbale are processed.
#
proc wokUtils:LIST:CompareAllKey { table function } {
    upvar $table TLOC
    foreach e [array names TLOC] {
	set flag [lindex $TLOC($e) 0]
	set f1 [lindex $TLOC($e) 1]/$e
	set f2 [lindex $TLOC($e) 2]/$e
	if { [string compare $flag ?] == 0 } {
	    if { [$function $f1 $f2] == 1 } {
		set TLOC($e) [list = $f1 $f2]
	    } else {
		set TLOC($e) [list # $f1 $f2]
	    }
	}
    }
}
#
# Same as above but only indexex in keylist are processed.
# This proc to avoid testing each key in the above procedure
#  
proc wokUtils:LIST:CompareTheseKey { table function keylist } {
    upvar $table TLOC
    foreach e [array names TLOC] {
	if  { [expr { ([lsearch -exact $keylist $e] != -1) ? 1 : 0}] } {
	    set flag [lindex $TLOC($e) 0]
	    set f1 [lindex $TLOC($e) 1]/$e
	    set f2 [lindex $TLOC($e) 2]/$e
	    if { [string compare $flag ?] == 0 } {
		if { [$function $f1 $f2] == 1 } {
		    set TLOC($e) [list = $f1 $f2]
		} else {
		    set TLOC($e) [list # $f1 $f2]
		}
	    }
	} else {
	    unset TLOC($e)
	}
    }
    return
}
#
# same as array set, i guess
#
proc wokUtils:LIST:ListToMap { name list2 } {
    upvar $name TLOC 
    foreach f $list2 {
	set TLOC([lindex $f 0]) [lindex $f 1]
    }
    return
}
#
# reverse 
#
proc wokUtils:LIST:MapToList { name {reg *}} {
    upvar $name TLOC 
    set l {}
    foreach f [array names TLOC $reg] {
	lappend l [list $f $TLOC($f)]
    }
    return $l
}
#
# Same as wokUtils:LIST:ListToMap. For spurious reason
#
proc wokUtils:LIST:MapList { name list2 } {
    upvar $name TLOC 
    foreach f $list2 {
	set TLOC([lindex $f 0]) [lindex $f 1]
    }
    return
}

# 
# Applique le test Func sur l'element index de list 
#
proc wokUtils:LIST:Filter { list Func {index 0} } {
    set l {}
    foreach e $list {
	if { [$Func [lindex $e $index]] } {
	    lappend l $e
	}
    }
    return $l
}
    
#
# not Very,very,very,very,very useful
#
proc wokUtils:EASY:GETOPT { prm table tablereq usage listarg } {

    upvar $table TLOC $tablereq TRQ $prm PARAM
    catch {unset TLOC}

    set fill 0

    foreach e $listarg {
	if [regexp {^-.*} $e opt] {
	    if [info exists TRQ($opt)] {
		set TLOC($opt) {}
		set fill 1
	    } else {
		puts stderr "Error: Unknown option $e"
		eval $usage
		return -1
	    }
	} else {
	    if [info exist opt] {
		set fill [regexp {value_required:(.*)} $TRQ($opt) all typ]
		if { $fill } {
		    if { $TLOC($opt) == {} } {
			set TLOC($opt) $e
			set fill 0
		    } else {
			lappend PARAM $e
		    }
		} else {
		    lappend PARAM $e
		}
	    } else {
		lappend PARAM $e
	    }
	}
    }

    if [array exists TLOC] {
	foreach e [array names TLOC] {
	    if { [regexp {value_required:(.*)} $TRQ($e) all typ ] == 1 } {
		if { $TLOC($e) == {} } {
		    puts "Error: Option $e requires a value"
		    eval $usage
		    return -1
		}
		switch -- $typ {
		    
		    file {
		    }
		    
		    string {
		    }
		    
		    date {
		    }
		    
		    list {
			set TLOC($e) [split $TLOC($e) ,]
		    }
		    
		    number {
			if ![ regexp {^[0-9]+$} $TLOC($e) n ] {
			    puts "Error: Option $e requires a number."
			    eval $usage
			    return -1
			}
		    }
		    
		}
		
	    }
	}
    } else {
	foreach d [array names TRQ] {
	    if { "$TRQ($d)" == "default" } {
		set TLOC($d) {}
	    }
	}
    }
    
    return
}
;#
;# Disallow 2 qualifiers
;#
proc wokUtils:EASY:DISOPT  { tabarg tbldis usage } {
    upvar $tabarg TARG $tbldis TDIS
    set largs [array names TARG]
    foreach o $largs {
	if [info exists TDIS($o)] {
	    set lo $TDIS($o)
	    foreach y $largs {
		if { [set inx [lsearch $lo $y]] != -1 } {
		    puts "Option $o and [lindex $lo $inx] are mutually exclusive."
		    eval $usage
		    return -1
		}
	    }
	}
    }
    return
}
;#
;# Answer Yes or No to convert file ( full path /ordinary file  )
;# Here is a quite bestial/brutal version to convert automatically about 70 percent of text files
;#
proc wokUtils:EASY:NatCopy { file } {
    set glob_style_patterns {*.cxx *.C *.h *.c *.f *.ll *.cdl *.edl *.tcl *.ld *.idl *.ccl}
    set bf [file tail $file]
    foreach ptn $glob_style_patterns {
	if [string match $ptn $bf] {
	    return 1
	}
    }
    return 0
}
# fait la substitution de / par \\ sur NT
# sur Unix ne fait rien
#
proc wokUtils:EASY:stobs2 { l } {
    global tcl_platform
    switch -- $tcl_platform(platform) {
	unix {
	    return $l
	}
	windows {
	    return [wokUtils:EASY:lbs2 $l]
	}
    }
}
#
proc wokUtils:EASY:bs1 { s } {
    regsub -all {/} $s {\\} r
    return $r
}
#
proc wokUtils:EASY:lbs1 { ls } {
    set lr {}
    foreach s $ls {
	regsub -all {/} $s {\\} r
	lappend lr $r
    }
    return $lr
}
#
proc wokUtils:EASY:lbs2 { ls } {
    set lr {}
    foreach s $ls {
	regsub -all {/} $s {\\\\} r
	lappend lr $r
    }
    return $lr
}
#
# string trim does not work. Do it 
# 
proc wokUtils:EASY:sb { str } {
    set a ""
    set len [string length $str]
    for {set i 0} {$i < $len} {incr i 1} {
	set x [string index $str $i]
	if { $x != " " } {
	    append a $x
	}
    }
    return $a
}
#
# returns 1 if exec is in the path
#
proc wokUtils:EASY:INPATH { exec } {
    if { [set x [auto_execok $exec]] != {} } {
	if { $x != 0 } {
	    return 1
	}
    }
    return 0
}
#
# Insert a MAP in an other MAP to the index here
#
proc wokUtils:EASY:MAD { table here t } {
    upvar $table TLOC $t tin
    foreach hr [array names TLOC ${here}*] {
	catch { unset TLOC($hr)}
    }
    foreach v [array names tin] {
	set TLOC($here,$v) $tin($v)
    }
}
#
# tar
# Examples:
#
#  tarfromroot: 
#
#               wokUtils:EASY:tar tarfromroot  /tmp/yan.tar .
#               wokUtils:EASY:tar tarfromroot  [glob ./*.tcl]
#
#  tarfromlist: 
#
#               wokUtils:EASY:tar tarfromliste /tmp/yan.tar /tmp/LISTE
#               (si LISTE = basenames => tous les fichiers dans le repertoire courant)
#               (si LISTE = fullpathes => ya des fulls path dans le tar)
#
#  untar      :
#
#               wokUtils:EASY:tar untar /tmp/yan.tar 
#               
#  untarZ     : 
#
#               wokUtils:EASY:tar untarZ /tmp/yan.tarZ
# 
proc wokUtils:EASY:tar { option args } {
    
    catch { unset command return_output }
    
    switch -- $option {
	
	tarfromroot {
	    set name [lindex $args 0]
	    set root [lindex $args 1]
	    append command {tar cf } $name " " $root
	}
	
	tarfromliste {
	    set name [lindex $args 0]
	    set list [lindex $args 1]
	    if [file exists $list] {
		set liste [wokUtils:FILES:FileToList [lindex $args 1]]
		append command  {tar cf } $name
		foreach f $liste {
		    append command " " $f
		}
	    } else {
		error "File $list not found"
		return -1
	    }
	}
	
	untar {
	    set name [lindex $args 0]
	    append command {tar xof } $name
	}
	
	untarZ {
	    set name [lindex $args 0]
	    append command uncompress { -c } $name { | tar xof - >& /dev/null }
	}


	ls {
	    set return_output 1
	    set name [lindex $args 0]
	    append command {tar tvf } $name
	}

	lsZ {
	    set return_output 1
	    set name [lindex $args 0]
	    append command uncompress { -c } $name { | tar tvf - }
	}

    }
    
    ;#puts "command = $command"
    
    if [catch {eval exec $command} status] {
	puts stderr "Tar Error in command: $command"
	puts stderr "Status          : $status"
	set statutar -1
    } else {
	if [info exist return_output] {
	    set statutar $status
	} else {
	    set statutar 1
	}
    }

    return $statutar
}
#
# Send a mail on unix platform.
#
proc wokUtils:EASY:mail { to from cc subject text {option send} } {
    global tcl_platform
    if { "$tcl_platform(platform)" == "unix" } {
	switch -- $option {
	    send {
		set cmd {wokUtils:EASY:mail $to $from $cc $subject $text command}
		if {[catch $cmd result] != 0} {
		    puts $result
		    return {}
		} else {
		    return 1
		}
	    }
	    
	    command {
		set fid [open "| /usr/lib/sendmail -oi -t" "w"]
		puts $fid "To: $to"
		if {[string length $from] > 0} {
		    puts $fid "From: $from"
		}
		if {[string length $cc] > 0} {
		    puts $fid "Cc: $cc"
		}
		puts $fid "Subject: $subject"
		puts $fid "Date: [clock format [clock seconds]]"
		puts $fid ""  
		puts $fid $text
		close $fid
		return 1
	    }
	}
    }
}
;#
;# topological sort. returns a list.
;#wokUtils:EASY:tsort {  {a h} {b g} {c f} {c h} {d i}  }
;#               => { d a b c i g f h }
;#
proc wokUtils:EASY:tsort { listofpairs } {
    foreach x $listofpairs {
	set e1 [lindex $x 0]
	set e2 [lindex $x 1]
	if ![info exists pcnt($e1)] {
	    set pcnt($e1) 0
	}
	if ![ info exists pcnt($e2)] {
	    set pcnt($e2) 1
	} else {
	    incr pcnt($e2)
	}
	if ![info exists scnt($e1)] {
	    set scnt($e1) 1
	} else {
	    incr scnt($e1)
	}
	set l {}
	if [info exists slist($e1)] {
	    set l $slist($e1)
	}
	lappend l $e2
	set slist($e1) $l
    }
    set nodecnt 0
    set back 0
    foreach node [array names pcnt] {
	incr nodecnt
	if { $pcnt($node) == 0 } {
	    incr back
	    set q($back) $node
	}
	if ![info exists scnt($node)] {
	    set scnt($node) 0
	}
    }
    set res {}
    for {set front 1} { $front <= $back } { incr front } {
	lappend res [set node $q($front)]
	for {set i 1} {$i <= $scnt($node) } { incr i } {
	    set ll $slist($node)
	    set j [expr {$i - 1}]
	    set u [expr { $pcnt([lindex $ll $j]) - 1 }]
	    if { [set pcnt([lindex $ll $j]) $u] == 0 } {
		incr back
		set q($back) [lindex $ll $j]
	    }
	}
    }
    if { $back != $nodecnt } {
	puts stderr "input contains a cycle"
	return {}
    } else {
	return $res
    }
}
#
#
#
proc wokUtils:EASY:OneHead { str len } {
    return  $str[replicate " " [expr { $len - [string length $str] }]]
}
#
# Execute lcmd : a list of commands
# return the list of commqnd to execute in case of error.
# that is returns {}if everything's OK.
# verbose 1 exec 0 => just print. dont execute.
# verbose 1 exec 1 => print command then execute.
# verbose 0 exec 1 => just execute 
# verbose 0 exec 0 => do nothing. 
# continue 0 => return if error.
# continue 1 => try do end execution of list.
# 
proc wokUtils:EASY:Command { lcmd {verbose 0} {exec 1} {continue 1} } {
    foreach command $lcmd {
	if { $verbose } { puts stdout "Ex: $command" }
	if { $exec } {
	    if [catch { eval exec $command } status ] {
		puts "$status"
		if { $continue == 0 } {
		    return -1
		}
	    }
	}
    }
    return 1
}
;#
;# Same as above without exec
;#
proc wokUtils:EASY:TclCommand { lcmd {verbose 0} {exec 1} {continue 1} } {
    foreach command $lcmd {
	if { $verbose } { puts stdout "Ex: $command" }
	if { $exec } {
	    eval $command
	}
    }
    return 1
}
#
# Execute command_file as a whole. Default send exec.
# Can use package Expect on Unix platform. => shell is expect:ShellName
# Send a exec command on WNT platform
#
proc wokUtils:EASY:Execute { command_file {shell sh} {fileid stdout} {timeout -1} {V 0} } {
    global tcl_platform
    if { "$shell" == "noexec" } {
	foreach l [wokUtils:FILES:FileToList $command_file] {
	    puts "$l"
	}
	return
    } elseif { "$shell" == "expect:sh" } {
	set shell sh
	spawn -noecho $shell $command_file
	set LOCID $spawn_id
	log_user 0
	exp_internal $V
	set timeout $timeout
	expect {
	    -i $LOCID -indices -re "(\[^\r]*)\r\n" {
		;#puts stdout $expect_out(1,string)
		puts $fileid $expect_out(1,string)
		exp_continue
	    }
	    -i $LOCID eof {
		puts $fileid "Received eof. Bye"
		return
	    }
	    -i $LOCID timeout {
		puts $fileid "Timeout excedeed ($timeout) from spawned process."
	    }
	}
	return
    } else {
	foreach command [wokUtils:FILES:FileToList $command_file] {
	    puts "Ex: $command"
	    if ![catch { eval exec $command } status ] {
		puts $fileid $status
	    } else {
		puts "Ex ERROR: $status"
	    }
	}
	return
    }
}
;#
;# search for each element in dfile if it belongs to a directory of dlist
;#
proc wokUtils:EASY:yfind { dfile dlist } {
    set ret {}
    foreach file $dfile {
	set f {}
	foreach dir $dlist {
	    if [file exists $dir/$file] {
		set f $dir
		break
	    }
	}
	lappend ret [list $file $f]
    }
    return $ret
}
;#
;# returns the list of all directories under dir
;#
proc wokUtils:EASY:seadir { dir } {
    set l $dir
    foreach f [wokUtils:EASY:readdir $dir] {
	if [file isdirectory $dir/$f] { 
	    set l [concat $l [wokUtils:EASY:seadir $dir/$f]]
	}
    }
    return $l
}

proc wokUtils:EASY:NiceList { a sep } {
    set maxl 0
    foreach x $a {
	if { [set lc [string length [lindex $x 0]]] > $maxl } {
	    set maxl $lc
	}
    }
    incr maxl ; set ret ""
    foreach x $a {
	set value [lindex $x 1]
	if { [set name  [lindex $x 0]] == "separator" } {
	    append ret \n
	} else {
	    append ret [format "%-*s %s" $maxl $name$sep $value]\n
	}
    }
    return $ret
}
;#
;# Write a Tcl proc to return the contents of map. Proc will have 1 argument: the name of the map.
;# 
proc wokUtils:EASY:MapToProc { map TclFile ProcName } { 
    upvar $map TLOC
    if ![ catch { set id [ open $TclFile w ] } errout ] {
	puts $id "proc $ProcName { map } {"
	puts $id "upvar \$map TLOC"
	foreach x [array names TLOC] {
	    puts $id "set TLOC($x) {$TLOC($x)}"
	}
	puts $id "return"
	puts $id "}"
	close $id
	return 1
    } else {
	puts stderr "$errout"
	return -1
    }
}
;#
;# Write a Tcl proc to return the contents of list. Proc will has no argument.
;# 
proc wokUtils:EASY:ListToProc { list TclFile ProcName } { 
    if ![ catch { set id [ open $TclFile w ] } errout ] {
	puts $id "proc $ProcName { } {"
	puts $id "set l {$list}" 
	puts $id {return $l}
	puts $id "}"
	close $id
	return 1
    } else {
	puts stderr "$errout"
	return -1
    }
}
;#
;# Returns the list of all "revision" files in map that is:
;#
proc wokUtils:EASY:RevFiles { map } {
    upvar $map TLOC
    set l {}
    foreach x [array names TLOC] {
	foreach e $TLOC($x) {
	    if [regexp {[ ]*#[ ]*([^ ]*)[ ]*([^ ]*)[ ]*([^ ]*)} $e all basn elem from] {
		lappend l [file join $from $basn]
	    }  elseif [regexp {[ ]*\+[ ]*([^ ]*)[ ]*([^ ]*)} $e all basn from] {
		lappend l [file join $from $basn]
	    }
	}
    }
    return $l
}
;#
;# Write a map. map(.ext) = { list of files in lpath with this extension)
;# 
proc wokUtils:EASY:ext { lpath map } {
    upvar $map TLOC
    catch { unset TLOC }
    foreach f $lpath {
	lappend TLOC([file extension $f]) $f
    }
    return
}
;#
;# Compares 2 maps created by DirToMap.
;# Writes in res the result of comparison in res
;# res is a map indexed by :
;#
;# ##,d where d was found both in the 2 maps imas and irev.
;# Element res(##,d) contains the comparaison of the 2 directories.
;# --,d where d was found in imas and not in irev
;#
;#
proc wokUtils:EASY:Compare { imas irev res {CompareFunc wokUtils:FILES:AreSame} {hidee 0} {gblist *} } {
    upvar $imas mas $irev rev $res TLOC 
    if { [array exists mas] } {
	set lmas [array names mas]
    } else {
	set lmas {}
    }
    set lcom [wokUtils:LIST:i3 $lmas [array names rev]]
    set pnts "                                        "
    foreach dir [lsort [lindex $lcom 1]] {
	wokUtils:LIST:SimpleDiff COMP $mas($dir) $rev($dir) $gblist
	if { [array exists COMP] } {
	    set lapp {}
	    foreach e [lsort [array names COMP]] {
		set flag [lindex $COMP($e) 0]
		set f1 [set d1 [lindex $COMP($e) 1]]/$e
		set f2 [set d2 [lindex $COMP($e) 2]]/$e
		if { [string compare $flag ?] == 0 } {
		    if { [$CompareFunc $f1 $f2] == 1 } {
			if { $hidee == 0 } {
			    lappend lapp [format "    = %-30s %-40s %s" $e $d1 $d2]
			}
		    } else {
			lappend lapp [format "    # %-30s %-40s %s" $e $d1 $d2]
		    }
		} elseif { "$flag" == "+" } {
		    lappend lapp [format "    + %-30s %s %s" $e $pnts $d1]
		} elseif { "$flag" == "-" } {
		    lappend lapp [format "    - %-30s %s %s" $e $d1 $pnts]
		}
	    }
	    set TLOC(##,$dir) $lapp
	}
    }
    
    foreach dir [lindex $lcom 0] {
	set lapp {}
	foreach f $mas($dir) {
	    lappend lapp [format "    - %-30s %s %s" [file tail $f] $f $pnts]  
	}
	set TLOC(--,$dir) $lapp
    }
    foreach dir [lindex $lcom 2] {
	set lapp {}
	foreach f $rev($dir) {
	    lappend lapp  [format "    + %-30s %s %s" [file tail $f] $pnts [file dirname $f]]
	}
	set TLOC(++,$dir) $lapp
    }
    return
}
;#
;# lit une map cree par wokUtils:EASY:Compare et imprime dans l'ordre.
;#
proc wokUtils:EASY:WriteCompare { dir1 dir2 map {fileid stdout} } {
    upvar $map TLOC

    foreach dir [lsort [array names TLOC ##,*]] {
	puts $fileid "\n Directory $dir\n"
	foreach l $TLOC($dir) {
	    puts $fileid $l
	}
    }

    foreach dir [array names TLOC --,*] {
	puts $fileid "\n Directory $dir\n"
	foreach l $TLOC($dir) {
	    puts $fileid $l
	}
    }

    foreach dir [lsort -command wokUtils:FILES:Depth [array names TLOC ++,*]] {
	puts $fileid "\n Directory $dir\n"
	foreach l $TLOC($dir) {
	    puts $fileid $l
	}
    }
}
;#
;# lit sur fileid un report cree par wokUtils:EASY:WriteCompare et genere une map avec comme index:
;# ##,x Contient la liste des operations a faire sur le sous dir x qui n'est que modifie
;# ++,x Contient la liste des operations (ajout) a faire sur le sous dir x qui est nouveau
;# --,x Contient la liste des operations (rm) a faire sur le sous dir x qui a disparu.
;# Pour l'appelant Ca s'est bien passe si [array exists map]
;# Arrete la lecture a la premiere ligne contenant un caractere en 1 er colonne.
;#
proc wokUtils:EASY:ReadCompare { map fileid } {
    upvar $map TLOC
    catch { unset TLOC }
    while {[gets $fileid x] >= 0} {
	if { $x != {} } {
	    if { [string range $x 0 0] == " " } {
		if { [regexp { Directory (.*)} $x all comdir] } {
		    set TLOC($comdir) {}
		} else {
		    if [info exists comdir] {
			if [info exists TLOC($comdir)] {
			    set l $TLOC($comdir)
			    lappend l $x
			    set TLOC($comdir)  $l
			}
		    } else {
			puts stderr "Format error: $x"
			return
		    }
		}
	    } else {
		return
	    }
	}
    }
}
;#
;# sert a trier les index ++ d'une map cree ci dessus de facon a obtenir une liste de directories
;# triee "de la racine vers le bas". Pas bokou plus que wokUtils:FILES:Depth
;# donc inx1 et inx2 de la forme ++,dirname
;#
proc wokUtils:EASY:SortCompare++ { inx1 inx2 } {
    regsub -all {\+\+,} $inx1 {} d1
    regsub -all {\+\+,} $inx2 {} d2
    return [wokUtils:FILES:Depth $d1 $d2]
}
;#
;# returns 1 if map has no entry concerning regx
;#
proc wokUtils:EASY:MapEmpty { map {regx *} } {
    upvar $map TLOC
    if [array exists TLOC] {
	set ll 0
	foreach x [array names TLOC $regx] {
	    set ll [expr { $ll + [llength $TLOC($x)]} ]
	}
	if { $ll == 0 } {
	    return 1
	} else {
	    return 0
	}
    } else {
	return 1
    }
}
;#
;# a stack with an array ; push
;#
proc wokUtils:STACK:push { stack value } {
    upvar $stack TLOC
    if ![info exists TLOC(top)] {
	set TLOC(top) 0
    }
    set TLOC($TLOC(top)) $value
    incr TLOC(top)
}
;#
;# a stack with an array ; pop returns {} if empty
;#
proc wokUtils:STACK:pop { stack } {
    upvar $stack TLOC
    if ![info exists TLOC(top)] {
	return {}
    }
    if { $TLOC(top) == 0 } {
	return {}
    } else {
	incr TLOC(top) -1
	set x $TLOC($TLOC(top))
	unset  TLOC($TLOC(top))
	return $x
    }
}
#
# Renvoie 1 si wb est une racine 0 sinon
#
proc wokUtils:WB:IsRoot { wb } {
    return [expr { ( [llength [w_info -A $wb]] > 1 ) ? 0 : 1 }]
}
;#
;# lnames is a list of names, returns a map indexed  with the lowered name and as value the original name
;# used to copy file from Windows to Unix system.
;#
proc wokUtils:EASY:u2l { lnames map } {
    upvar $map TLOC
    foreach name $lnames {
	set TLOC([string tolower $name]) $name
    }
    return
}
;#
;#
;#
proc wokUtils:EASY:readdir { dir } {
    set l {}
    foreach f [glob -nocomplain [file join $dir *]] {
	lappend l [file tail $f]
    }
    return $l
}

;#
;# "Nice letter: %s" { a b c } => {Nice letter: %a}  {Nice letter: %b} ..
;# as a string without backslash
;#
proc wokUtils:EASY:FmtSimple1 { fmt l {backslh 1} } {
    foreach e $l {
	if { $backslh } {
	    append str [format $fmt $e] "\n"
	} else {
	    append str [format $fmt $e]
	}
    }
    return $str
}
;#
;# 
;# 
proc wokUtils:EASY:FmtString1 { fmt l {yes_for_last 0} {edit_last {}} } {
    set ldeb [lrange $l 0 [expr [llength $l] -2]]
    set last [lrange $l end end]
    foreach e $ldeb {
	append str [format $fmt $e] " \\" "\n"
    }

    if {$edit_last != {} } {
	set slast [$edit_last [format $fmt $last]]
    } else {
	set slast [format $fmt $last]
    }

    if { $yes_for_last } {
	append str $slast " \\" "\n" 
    } else {
	append str $slast "\n"
    }
    return $str
}
;#
;# 
;# 
;# edit_last is performed ONCE fmt has been applied.
;#
proc wokUtils:EASY:FmtString2 { fmt l {yes_for_last 0} {edit_last {}} } {
    set ldeb [lrange $l 0 [expr [llength $l] -2]]
    set last [lrange $l end end]
    foreach e $ldeb {
	append str [format $fmt $e $e] " \\" "\n"
    }

    if {$edit_last != {} } {
	set slast [$edit_last [format $fmt $last $last]]
    } else {
	set slast [format $fmt $last $last]
    }

    if { $yes_for_last } {
	append str $slast " \\" "\n" 
    } else {
	append str $slast "\n"
    }

    return $str
}
;#
;# Apply fmt1 to the car of l, fmt2 to the cdr
;#
proc wokUtils:EASY:FmtFmtString1 { fmt1 fmt2 l {yes_for_last 0} {edit_last {}} } {
    set car [lindex $l 0]
    set ldeb [lrange $l 1 [expr [llength $l] -2]]
    set last [lrange $l end end]
    append str [format $fmt1 $car] " \\" "\n"
    foreach e $ldeb {
	append str [format $fmt2 $e] " \\" "\n"
    }

    if {$edit_last != {} } {
	set slast [$edit_last [format $fmt2 $last]]
    } else {
	set slast [format $fmt2 $last]
    }

    if { $yes_for_last } {
	append str $slast " \\" "\n" 
    } else {
	append str $slast  "\n"
    }
    return $str
}
;#
;# Same as above. The first argument is treated with a specific format.
;#
proc wokUtils:EASY:FmtFmtString2 { fmt1 fmt2 l {yes_for_last 0} {edit_last {}} } {
    set car [lindex $l 0]
    set ldeb [lrange $l 1 [expr [llength $l] -2]]
    set last [lrange $l end end]

    append str [format $fmt1 $car $car] " \\" "\n"
    foreach e $ldeb {
	append str [format $fmt2 $e $e] " \\" "\n"
    }

    if {$edit_last != {} } {
	set slast [$edit_last [format $fmt2 $last $last]]
    } else {
	set slast [format $fmt2 $last $last]
    }


    if { $yes_for_last } {
	append str $slast " \\" "\n" 
    } else {
	append str $slast "\n"
    }
    return $str
}
;#
;# keys.. provided by Tclx
;#
proc wokUtils:key:lset { listvar key value } {
    upvar $listvar VLOC
    set lret {}
    if [info exists VLOC] {
	set l2 {}
	foreach x $VLOC {
	    lappend l2 [lindex $x 0]
	    lappend l2 [lindex $x 1]
	}
	array set MM $l2
	set MM($key) $value
	foreach f [array names MM] {
	    lappend lret [list $f $MM($f)]
	}
    } else {
	lappend lret [list $key $value]
    }
    set VLOC $lret
    return
}
proc wokUtils:key:lkeys { listvar } {
    upvar   $listvar VLOC
    set lret {}
    foreach x $VLOC {
	lappend lret [lindex $x 0]
    }
    return $lret
}
proc wokUtils:key:lget { listvar indx } {
    upvar $listvar VLOC
    foreach x $VLOC {
	if { [string compare [lindex $x 0] $indx] == 0 } {
	    return [lindex $x 1]
	}
    }
    return {}
}

proc wokUtils:key:ldel { listvar indx } {
    upvar $listvar VLOC
    set lret {}
    foreach x $VLOC {
	if { [string compare [lindex $x 0] $indx] != 0 } {
	    lappend lret $x
	} 
    }
    set VLOC $lret
    return
}
