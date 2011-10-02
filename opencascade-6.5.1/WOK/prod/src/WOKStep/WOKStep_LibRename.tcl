proc WOKStep_LibRename:AdmFileType {} {
    return dbadmfile;
}

proc WOKStep_LibRename:OutputDirTypeName {} {
    return dbtmpdir;
}

proc WOKStep_LibRename:HandleInputFile { ID } {
 scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
    
 if { [file extension $name] == ".so" } {

  return 1;

 } 

 return 0;

}


proc  WOKStep_LibRename:Execute { theunit args } {    
    foreach ID $args {
	
	scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name

	set rootname [file rootname $name]
	set ext [file extension $name]

	if {  [regsub -all {\.} $rootname {_} newname] > 0 } {
	    msgprint -i -c "WOKStep_JavaCompile:Execute" "renaming $rootname to $newname$ext"
	    set libname [woklocate -p $ID]
	    #file rename -force $libname [file dirname $libname]/$newname$ext
	    if [catch {exec mv -f $libname [file dirname $libname]/$newname$ext } status ] {
		msgprint -e -c "WOKStep_LibRename:Execute" $status	
		return 1
	    }
	}
    }
    return 0;
}
