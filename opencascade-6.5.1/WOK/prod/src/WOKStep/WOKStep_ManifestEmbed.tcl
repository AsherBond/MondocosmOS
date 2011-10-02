proc WOKStep_ManifestEmbed:AdmFileType {} {
    return dbadmfile;
}

proc WOKStep_ManifestEmbed:OutputDirTypeName {} {
    return dbtmpdir;
}

proc WOKStep_ManifestEmbed:HandleInputFile { ID } {
 scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
 puts "name $name extension [file extension $name]"   
 if { [file extension $name] == ".manifest" } {
  puts "cought!"
  return 1;

 } 

 return 0;

}


proc  WOKStep_ManifestEmbed:Execute { theunit args } {    
    foreach ID $args {
	
	scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name

        set libname [woklocate -p $ID]
	set rootname [file rootname $libname]
        puts "rootname $rootname "
        puts "name $name  libname $libname"
        msgprint -i -c "WOKStep_ManifestEmbeding:Execute" "Embeding $name to $rootname"
	    if [catch {exec mt -nologo -manifest $libname -outputresource:$rootname } status ] {
		msgprint -e -c "WOKStep_ManifestEmbed:Execute" $status	
		return 1
	    }
    }
    return 0;
}
