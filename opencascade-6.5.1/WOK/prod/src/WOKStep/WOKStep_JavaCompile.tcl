proc WOKStep_JavaCompile:AdmFileType {} {
    
 return dbadmfile;

}

proc WOKStep_JavaCompile:OutputDirTypeName {} {

 return dbtmpdir;

}

proc WOKStep_JavaCompile:HandleInputFile { ID } {
    
 scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
    
 if { [file extension $name] == ".java" } {

  return 1;

 } 

 return 0;

}

proc WOKStep_JavaCompile:ComputeIncludeDir { unit } {

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

 # if { [wokparam -t %CSF_JavaHome] } {
 #     set result [wokparam -e %CSF_JavaHome]/jre/lib/rt.jar$ps$result
 # }

 return $result

}

proc WOKStep_JavaCompile:Execute { theunit args } {

 global env
 global tcl_platform
    
 msgprint -i -c "WOKStep_JavaCompile:Execute" "Processing unit : $theunit"
 msgprint -i -c "WOKStep_JavaCompile:Execute"
    
 set fJava [info exists env(WOK_USE_JAVA_DIRECTORY)]

 set unitname [wokinfo -n $theunit]
 set failed 0
 set incdir [WOKStep_JavaCompile:ComputeIncludeDir $theunit]
 wokparam -s%IncludeDir=$incdir

 if { $fJava } {
  set outdir [UNC [wokparam -e WOKEntity_javadir [wokinfo -w]]]
 } else {
  set outdir [UNC [wokparam -e WOKEntity_drvdir [wokinfo -w]]]
 }

 wokparam -s%OutDir=$outdir


 set sources {}
 set sourceslen 0
 set MaxLength 32000
 set tocompile 0

 # For faster compilation all sources are collected and passed to the compiler
 # at once. But for better dependency tracking, .class files are then explicitly
 # "linked" to the .java ones. This will allow to remove orphan .class if there
 # are no their original .java anymore.
 # Building a command line with all file names has a strong limitation - length
 # of the line (e.g. on Windows NT: 32Kb of symbols). So this limitation will
 # affect on large Interface(s) included into Jni. To overcome this, the line
 # is built unless it exceeds its limit, then it is compiled.

 foreach ID $args {
     
     scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
     set infile [UNC [woklocate -p $ID]]
     
     if { $tcl_platform(platform) == "windows" } {
	 regsub -all "/" $infile "\\\\\\" infile
     }
     lappend sources $infile
     set sourceslen [expr $sourceslen + [string length $infile]]
     set tocompile 1

     if { $sourceslen >= $MaxLength } {
	 # Command line is close to its maximum, let's compile it
	 wokparam -s%Source=$sources
	 set thecommand [wokparam -e JAVA_Compiler]
	 msgprint -i -c "WOKStep_JavaCompile:Execute" [lindex $thecommand 0]
	 if { [catch {eval exec [lindex $thecommand 0]} res] } {
	 
	     msgprint -e -c "WOKStep_JavaCompile:Execute" $res
	     set failed 1
	     return $failed
	 }

	 # reset again
	 set sources {}
	 set sourceslen 0
	 set tocompile 0
     }
 } 
 
 if { $tocompile } {
     wokparam -s%Source=$sources
     set thecommand [wokparam -e JAVA_Compiler]
     msgprint -i -c "WOKStep_JavaCompile:Execute" [lindex $thecommand 0]
     if { [catch {eval exec [lindex $thecommand 0]} res] } {
	 
	 msgprint -e -c "WOKStep_JavaCompile:Execute" $res
	 set failed 1
	 return $failed
     }
 }

 # Compilation was successful. Now declare a dependency chain. The same loop as above.
 foreach ID $args {
     scan $ID "%\[^:\]:%\[^:\]:%\[^:\]"  unit type name
     set infile [UNC [woklocate -p $ID]]
     
     if { $tcl_platform(platform) == "windows" } {
	 regsub -all "/" $infile "\\\\\\" infile
     }
     set outfileid [file rootname $name].class
     
     if { $fJava } {
	 stepoutputadd $unitname:javafile:$outfileid
	 stepaddexecdepitem $ID $unitname:javafile:$outfileid
     } else {
	 stepoutputadd $unitname:derivated:$outfileid
	 stepaddexecdepitem $ID $unitname:derivated:$outfileid
     }
     
 }
     

 return $failed

}
