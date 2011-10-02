
#############################################################################
#
#                              W C H E C K
#                              ___________
#
#############################################################################
#
# Usage
#
proc wokCheckUsage { } {
    puts stderr {Usage : wcheck  [-t SCCS|RCS] [-report [filename] [file1 file2 ...]}
    puts stderr ""
    puts stderr { wcheck filename  : Check that file1 file2 ..can be placed in repository.}
    puts stderr {                    -report to enter a report file <filename> created by wprepare}
    return
}   

proc wcheck { args } {
    set tblreq(-h)         {}
    set tblreq(-s)         {}
    set tblreq(-report)    value_required:string
    set tblreq(-diff)      {}
    set tblreq(-dir)       value_required:string

    set param {}
    if { [wokUtils:EASY:GETOPT param tabarg tblreq wokCheckUsage $args] == -1 } return
    
    if { [info exists tabarg(-h)] } {
	wokCheckUsage 
	return
    }

    if [info exists tabarg(-diff)] {
	if [info exists tabarg(-dir)] {
	    set dir(-dir)
	    wcheck_diff $param $dir
	} else {
	    wokCheckUsage
	}
	return
    }

    set BTYPE SCCS 
    if [info exists tabarg(-t)] {
	set BTYPE $tabarg(-t)
    }

    set silent [info exists tabarg(-s)]

    set tmpdir /tmp/wcheck[id process]
    if [file exists $tmpdir] {
	wokUtils:FILES:delete [glob -nocomplain $tmpdir/*]
    } else {
	wokUtils:FILES:mkdir $tmpdir
    }

    set LFILE {}
    if { [info exists tabarg(-report)] } {
	set ID $tabarg(-report)
	catch { unset table banner notes }
	wokPrepare:Report:Read $ID table banner notes 
	foreach e [lsort [array names table]] {
	    foreach l $table($e) {
		set str [wokUtils:LIST:Trim $l]
		lappend LFILE [lindex $str 4]/[lindex $str 3]
	    }
	}
    } else {
	eval set LFILE $param
    }
    
    switch -- $BTYPE {

	SCCS {
	    set vrs 1
	    foreach file $LFILE {
		update
		set sfile $tmpdir/s.[file tail $file]
		if { [catch { exec admin -i$file -r$vrs -yCheck $sfile } status ] == 0 } {
		    if { !$silent } {msgprint -c WOKVC -i "$file is OK."}
		} else {
		    if { "$status" == "No id keywords (cm7)" } {
			if { !$silent } { msgprint -c WOKVC -i "$file is OK"}
		    } else {
			msgprint -c WOKVC -e "$file cannot be created ( $status )"
		    }
		}
		catch {wokUtils:FILES:delete $sfile}
	    }
	}

	RCS {
	    msgprint -c WOKVC -e "Not yet implemented"
	}

	default {
	     msgprint -c WOKVC -e  "Unknown base type. Should be SCCS or RCS"
	}

    }

    catch {
	if [file exists $tmpdir] {
	wokUtils:FILES:delete [glob -nocomplain $tmpdir/*]
	}
	wokUtils:FILES:delete $tmpdir
    }

    return
}

proc wcheck_diff { param dir } {
    ;#puts $param 
    ;#puts $dir
    return
}


