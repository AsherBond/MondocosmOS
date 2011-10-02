;# This procedure is called when using the command:
;#
;# wnews -x -from label1 -to label2 -command wnews:cpwb -usedata w1,w2,[,ulist,notes]
;#
;# It has been designed to update the workbench w2 from workbench w1 with units and files
;# modified in w1 between the integrations named label1 and label2.
;# If ulist is specified only units listed in this file are processed.
;# If notes is specified all comments are written in this file.
;#
;#          (((((((((((((((  W A R N I N G )))))))))))))))

proc wnews:cpwb { comments table args } {
    upvar $table MYTABLE

    set userargs [split $args ,]
    set from_wb [lindex $userargs 0]   ;# The origine workbench
    set dest_wb [lindex $userargs 1]   ;# The target  workbench
    set file_ud [lindex $userargs 2]   ;# The List of units to be processed in the origine workbench
    set rnotes  [lindex $userargs 3]   ;# The file to receive the release notes.

    if { $from_wb == {} ||  $dest_wb == {} } {
	puts stderr "news:cpwb: Error : Need at least 2 workbenches name"
	puts stderr "Append this to your command :"
	puts stderr { -userdata fac:shop:wbfrom,fac:shop:wbto,myfile.dat }
	return 0
    }
   
    if ![file exists $file_ud] {
	set list_ud [w_info -l $from_wb]
	msgprint -i "proc wnews:cpwb: Info : will process all units in $from_wb"
    } else {
	set list_ud [wokUtils:FILES:FileToList $file_ud]
	if { $list_ud == {} } {
	    msgprint -e "wnews:cpwb File $file_ud is empty. Nothing done"
	    return 
	} else {
	    msgprint -i "wnews:cpwb Copy from $from_wb to $dest_wb units in file $file_ud "
	}
    }
    
    if { $rnotes != {} } {
	wokUtils:FILES:ListToFile [split $comments \n] $rnotes
	msgprint -i "wnews:cpwb: Info : File $rnotes created will all comments."
    }

    if ![wokinfo -x $dest_wb] {
	msgprint -e "wnews:cpwb: The workbench $dest_wb does not exists."
	return
    } 

    set l_fab [w_info -l $dest_wb]
    set l_ud {}

    foreach UD [lsort [array names MYTABLE]] {
	set x      [split $UD .]
	set name   [lindex $x 0]
	if { [lsearch $list_ud $name] != -1 } {
	    set type   [lindex $x 1]
	    lappend l_ud $name
	    if { [lsearch $l_fab $name] == -1 } {
		msgprint -i "ucreate -${type} ${dest_wb}:${name}"
		ucreate -${type} ${dest_wb}:${name}
	    }
	    set from_src  [wokinfo -p source:. ${from_wb}:${name}]
	    set dest_src  [wokinfo -p source:. ${dest_wb}:${name}]
	    set l_file {}
	    foreach item $MYTABLE($UD) {
		set mark [lindex $item 0]  ;# == Modified | Added | Deleted
		switch -- $mark {
		    
		    Modified {
			set elem [lindex $item 1]  ;# == {name.ext x.y}
			set file [lindex $elem 0]  ;#
			set vers [lindex $item 1]  ;#
			if { [lsearch $l_file $file] == -1 } {
			    if [file exists $from_src/$file] {
				msgprint -i  "Copying $from_src/$file to $dest_src/$file"
				catch {exec cp $from_src/$file $dest_src/$file}
				catch {exec chmod 0644 $dest_src/$file}
				lappend l_file $file
			    } else {
				msgprint -w "File $from_src/$file not copied. File not found"
			    }
			}
		    }
		    
		    Added  {
			set elem [lindex $item 1]  ;# == {name.ext x.y}
			set file [lindex $elem 0]  ;#
			set vers [lindex $item 1]  ;#
			if { [lsearch $l_file $file] == -1 } {
			    if [file exists $from_src/$file] {
				msgprint -i "Copying $from_src/$file to $dest_src/$file"
				catch {exec cp -f $from_src/$file $dest_src/$file}
				catch {exec chmod 0644 $dest_src/$file}
				lappend l_file $file
			    } else {
				msgprint -w "File $from_src/$file not copied. File not found"
			    }
			}
		    }
		    
		    Deleted {
		    }
		}
	    }
	}
    }
    return 1
}
