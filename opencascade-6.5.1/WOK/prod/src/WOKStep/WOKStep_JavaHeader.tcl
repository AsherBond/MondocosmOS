proc WOKStep_JavaHeader:AdmFileType {} {
    
 return dbadmfile;

}

proc WOKStep_JavaHeader:OutputDirTypeName {} {

 return dbtmpdir;

}

proc WOKStep_JavaHeader:HandleInputFile { ID } {

 scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
    
 if { [file extension $name] == ".java" } {

  return 1;

 } 

 return 0;

}

proc WOKStep_JavaHeader:ComputeIncludeDir { unit } {
    
 global env
 global tcl_platform

 if { $tcl_platform(platform) == "windows" } {
  set ps "\\;"
 } else {
  set ps ":"
 }

 set fJava [info exists env(WOK_USE_JAVA_DIRECTORY)]
    
 set allwb [w_info -A $unit]
 set unitname [wokinfo -n $unit]
 set result ""
    
 set themax [llength $allwb]
    
 for { set i $themax } { [expr $i != 0] } { incr i -1 } {

  set awb [lindex $allwb [expr $i - 1]]

  if { $fJava } {
   set addinc [UNC [wokparam -e WOKEntity_javadir ${awb}]]
  } else {
   set addinc [UNC [wokparam -e WOKEntity_drvdir ${awb}]]
  }

  set result ${addinc}$ps$result

 }

 set result $env(WOKHOME)$ps$result

 return $result

}

proc WOKStep_JavaHeader:Execute { theunit args } {

 global tcl_platform

 msgprint -i -c "WOKStep_JavaHeader:Execute" "Processing unit : $theunit"
 msgprint -i -c "WOKStep_JavaHeader:Execute"

 set unitname [wokinfo -n $theunit]
 set failed 0
 set incdir [WOKStep_JavaHeader:ComputeIncludeDir $theunit]
 wokparam -s%IncludeDir=$incdir

 foreach ID $args {

  scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
  set infile [UNC [woklocate -p $ID]]
	
  if { $tcl_platform(platform) == "windows" } {
   regsub -all "/" $infile "\\\\\\" infile
  }

  set outfileid [file rootname $name]
  wokparam -s%Class=${unitname}.$outfileid

  set nameid ${unitname}
  regsub -all "\\." $nameid "_" nameid
  set outfileid ${nameid}_${outfileid}.h
  set outfile [UNC [wokinfo -p privinclude:$outfileid $theunit]]
  wokparam -s%OutFile=$outfile

  set thecommand [wokparam -e JAVA_Header]
	
  msgprint -i -c "WOKStep_JavaCompile:Execute" "Building header $outfileid"

  if { [catch {eval exec [lindex $thecommand 0]} res] } {

   msgprint -e -c "WOKStep_JavaCompile:Execute" $res
   set failed 1

  } else {

   stepoutputadd $unitname:privinclude:$outfileid
   stepaddexecdepitem $ID $unitname:privinclude:$outfileid

  }

 }
    
 return $failed

}
