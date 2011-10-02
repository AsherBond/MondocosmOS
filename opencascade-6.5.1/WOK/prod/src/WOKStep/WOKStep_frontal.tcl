
proc WOKStep_frontal:AdmFileType {} {
    return stadmfile;
}

proc WOKStep_frontal:OutputDirTypeName {} {
    return sttmpdir;
}

proc WOKStep_frontal:HandleInputFile { ID } {
    return 1;
}


proc WOKStep_frontal:ExecuteOldFrontal {unit args} {
    global WOK_GLOBALS

    msgprint -i "WOKStep_frontal:ExecuteOldFrontal"

    set pk [wokinfo -n $unit]
    set nameFROMFRONT [woklocate -p ${pk}:source:FROM_FRONTAL]
    if {[string length $nameFROMFRONT] == 0} {
	msgprint -e "Unable to locate file FROM_FRONTAL"
	return 1
    }
    set nameCOMP [woklocate -p ${pk}:source:COMPONENTS]
    if {[string length $nameCOMP] == 0} {
	msgprint -e "Unable to locate file COMPONENTS"
	return 1
    }

    set wb [wokinfo -N $unit]

    set dir [file dirname [wokinfo -p sttmpfile:missing $unit]]

    set savpwd [pwd]
    cd $dir
    

    set bin [wokUtils:FILES:FileToList $nameFROMFRONT]

    set from [woklocate -p ${bin}:executable:lelisp.bin $wb]
    set fromid ${bin}:executable:lelisp.bin
    if { $from == {} } {
	set from [woklocate -p CCL:executable:lelisp.bin $wb]
	set fromid CCL:executable:lelisp.bin
	if { $from == {}} {
	    msgprint -e "Unable to locate the file lelisp.bin."
	    cd $savpwd
	    return 1
	}
    }

    set exec [woklocate -p ${bin}:executable:${bin} $wb]
    if { $exec == {} } {
	msgprint -w "Unable to locate the frontal file $bin"
	cd $savpwd
	return 1
    }

    msgprint -i "Copying binary file Lelisp from ${bin}"
    if [catch {eval "exec cp $from [pwd]"} res] {
	msgprint -e -c "WOKStep_frontal:Execute" $res
	return 1
    }

    if [file exists tmp.ccl] {
	wokUtils:FILES:delete  tmp.ccl
    }

    set failed 0
    set l {}
    foreach ccl [wokUtils:FILES:FileToList $nameCOMP] {
	set f [woklocate -p ${ccl}:ccldrv:${ccl}.ccl $wb]
	set m [woklocate -p ${ccl}:msgfile:${ccl}.us $wb]
	if { $f != {} } { 
	    lappend l [format "(load \"%s\")" $f] 
	} {
	    msgprint -e "Could not locate file : ${ccl}:ccldrv:${ccl}.ccl"
	}
	if { $m != {} } {
	    lappend l [format "(load-message \"%s\")" $m] 
	}
    }

    if $failed {return 1}

    if { $l != {} } {
	lappend l [format "(make-exec \"%s\" \"%s\" )" $dir $pk]
	lappend l [format "(end)"]
	wokUtils:FILES:ListToFile $l tmp.ccl
    } else {
	msgprint -e "Empty file COMPONENTS"
        return 1
    }

    msgprint -i "Setting Environnement"
    set WOK_GLOBALS(setenv_proc,tcl) 1
    wokenv -s 
    set WOK_GLOBALS(setenv_proc,tcl) 0

    set licensefile [woklocate -p CCLFrontal:datafile:[wokparam -e %Ilog_File] $wb]

    if { $licensefile == "" } {
	msgprint -c "WOKStep_frontal:Execute" -e "Unable to locate the Ilog license file"
	return 1;
    }

    msgprint -c "WOKStep_frontal:Execute" -i "Building $pk"
    
    if [catch {eval "exec /bin/env ILOG_LICENSE_FILE=$licensefile $exec -f tmp.ccl << (end)"} result] {
	msgprint -e "$result"
    }

    set resexe [wokinfo -p executable:$pk $unit]
    set rescore [wokinfo -p corelisp:${pk}.core $unit]

    msgprint -i -c "WOKStep_frontal:Execute" "Updating $resexe"
    wokparam -s "%LeLispFile=$from"
    wokparam -s "%CoreFile=$rescore"
    
    set thecommand [wokparam -e FRONTAL_FrontalScript]

    if {[catch {set fidexe [open $resexe "w"]} res] == 0} {
	puts $fidexe [lindex $thecommand 0]
	close $fidexe
	wokUtils:FILES:chmod 0755 $resexe
    } else {
	msgprint -e -c "WOKStep_frontal:Execute" "Enable to generate $rescore"
	msgprint -e -c "WOKStep_frontal:Execute" $res

	return 1
    }

    msgprint -i -c "WOKStep_frontal:Execute" "Updating $rescore"
    if [catch {eval "exec cp $pk.core $rescore"} result] {
	msgprint -e -c "WOKStep_frontal:Execute" $result
    }

    
    # insert dependencies

    stepinputadd ${bin}:executable:${bin}

    stepinputadd $fromid

    stepoutputadd -M -P -L -F $pk:executable:$pk

    stepaddexecdepitem -d ${bin}:executable:${bin} $pk:executable:$pk
    
    stepaddexecdepitem -d $fromid $pk:executable:$pk

    stepaddexecdepitem -d ${pk}:source:FROM_FRONTAL $pk:executable:$pk

    stepoutputadd -M -P -L -F $pk:corelisp:${pk}.core

    stepaddexecdepitem -d $fromid $pk:corelisp:${pk}.core

    stepaddexecdepitem -d ${pk}:source:FROM_FRONTAL $pk:corelisp:${pk}.core
    
    foreach ccl [wokUtils:FILES:FileToList $nameCOMP] {
	set f [woklocate -p ${ccl}:ccldrv:${ccl}.ccl $wb]
	if { $f != {} } {
	    stepinputadd  ${ccl}:ccldrv:${ccl}.ccl
	    stepaddexecdepitem -d ${ccl}:ccldrv:${ccl}.ccl $pk:corelisp:${pk}.core
	}
    }

    cd $savpwd
    return 0;
}




proc WOKStep_frontal:ExecuteNewFrontal { unit args } {
    global WOK_GLOBALS

    msgprint -i "WOKStep_frontal:ExecuteNewFrontal"

    set pk [wokinfo -n $unit]
    set nameFROMFRONT [woklocate -p ${pk}:source:FROM_FRONTAL]
    if {[string length $nameFROMFRONT] == 0} {
	msgprint -e "Unable to locate file FROM_FRONTAL"
	return 1
    }
    set nameCOMP [woklocate -p ${pk}:source:COMPONENTS]
    if {[string length $nameCOMP] == 0} {
	msgprint -e "Unable to locate file COMPONENTS"
	return 1
    }

    set wb [wokinfo -N $unit]

    set fileout [wokinfo -p ccldrv:${pk}.ccl $unit]

    msgprint -i "Creating $fileout"
    if [file exists $fileout ] {
	wokUtils:FILES:delete $fileout
    }


    set idout [open $fileout "w"]
    puts $idout ";; CCL Frontal -> $pk "
    close $idout

    stepoutputadd -M -P -L -F ${pk}:ccldrv:${pk}.ccl

    set fromunit [wokUtils:FILES:FileToList $nameFROMFRONT]

    if {$fromunit == "CCLKernel"} {

	msgprint -i "Switch CCLKernel to KernelFrontal"

	set fromunit "KernelFrontal"
    }
    

    if {$fromunit != {}} {
	
	set f [woklocate -p ${fromunit}:ccldrv:${fromunit}.ccl $wb]
	
	if { $f != {} } { 
	    wokUtils:FILES:concat $fileout $f
	    stepinputadd ${fromunit}:ccldrv:${fromunit}.ccl
	    stepaddexecdepitem -d ${fromunit}:ccldrv:${fromunit}.ccl ${pk}:ccldrv:${pk}.ccl
	} else {
	    msgprint -w "Could not locate file : ${fromunit}:ccldrv:${fromunit}.ccl"
	}
    }


    foreach ccl [wokUtils:FILES:FileToList $nameCOMP] {
	set f [woklocate -p ${ccl}:ccldrv:${ccl}.ccl $wb]
	if { $f != {} } { 
	    wokUtils:FILES:concat $fileout $f
	    stepinputadd ${ccl}:ccldrv:${ccl}.ccl
	    stepaddexecdepitem -d ${ccl}:ccldrv:${ccl}.ccl ${pk}:ccldrv:${pk}.ccl
	} else {
            set f [woklocate -p ${ccl}:ccldrv:${ccl}.ll $wb]
            if { $f != {} } { 
                wokUtils:FILES:concat $fileout $f
                stepinputadd ${ccl}:ccldrv:${ccl}.ll
                stepaddexecdepitem -d ${ccl}:ccldrv:${ccl}.ll ${pk}:ccldrv:${pk}.ccl
            } else {
                msgprint -e "Could not locate file : ${ccl}:ccldrv:${ccl}.ccl"
            }
	}
    }

    if {[wokparam -e %Station] == "wnt"} {
	set execid CCL${pk}.cmd
    } else {
	set execid CCL${pk}
    }


    set resexe [wokinfo -p executable:$execid $unit]

    set fileoutmsg [wokinfo -p cmpmsgfile:${pk}_Cmp.us $unit]
    set fileoutoldmsg [wokinfo -p msgfile:${pk}.us $unit]

    msgprint -i -c "WOKStep_frontal:Execute" "Updating $resexe"
    wokparam -s "%CCLFile=$fileout"
    wokparam -s "%MsgCmpFile=$fileoutmsg"
    wokparam -s "%MsgFile=$fileoutoldmsg"
    
    set thecommand [wokparam -e FRONTAL_CCLScript]

    if {[catch {set fidexe [open $resexe "w"]} res] == 0} {
	puts $fidexe [lindex $thecommand 0]
	close $fidexe
	if {[wokparam -e %Station] != "wnt"} {
	    wokUtils:FILES:chmod 0755 $resexe
	}
    } else {
	msgprint -e -c "WOKStep_frontal:Execute" "Enable to generate $resexe"
	msgprint -e -c "WOKStep_frontal:Execute" $res

	return 1
    }

    if {[wokparam -e %Station] == "wnt"} {
	set execbinid CCL${pk}bin.cmd
    } else {
	set execbinid CCL${pk}bin
    }


    # insert dependencies

    stepinputadd ${pk}:source:FROM_FRONTAL
    
    stepinputadd ${pk}:source:COMPONENTS

    stepoutputadd ${pk}:executable:$execid

    stepaddexecdepitem -d ${pk}:source:FROM_FRONTAL ${pk}:executable:$execid

    stepaddexecdepitem -d ${pk}:source:COMPONENTS ${pk}:executable:$execid

    stepaddexecdepitem -d ${pk}:source:FROM_FRONTAL ${pk}:ccldrv:${pk}.ccl

    stepaddexecdepitem -d ${pk}:source:COMPONENTS ${pk}:ccldrv:${pk}.ccl





    set bin [wokUtils:FILES:FileToList $nameFROMFRONT]
    if { $bin != {} } {
        set dir [file dirname [wokinfo -p sttmpfile:missing $unit]]

        set savpwd [pwd]
        cd $dir

        set from [woklocate -p ${bin}:executable:CCLinterpretor $wb]
        set fromid ${bin}:executable:CCLinterpretor
        if { $from == {} } {
	    set from [woklocate -p CCL:executable:CCLinterpretor $wb]
	    set fromid CCL:executable:CCLinterpretor
	    if { $from == {}} {
	        msgprint -e "Unable to locate the file CCLinterpretor."
	        cd $savpwd
	        return 1
	    }
        }

        set exec [woklocate -p ${bin}:executable:CCL${bin} $wb]
        if { $exec == {} } {
	    msgprint -w "Unable to locate the frontal file ${bin}:executable:CCL${bin} from $nameFROMFRONT"
	    cd $savpwd
	    return 1
        }

        msgprint -c "WOKStep_frontal:Execute" -i "Building $pk"
    
        set resexe [wokinfo -p executable:CCL$pk $unit]
        set rescore [wokinfo -p executable:CCL${pk}.bin $unit]

        msgprint -i -c "WOKStep_frontal:Execute" "Updating $pk.bin"
        wokparam -s "%InterpretorFile=$from"
        wokparam -s "%CCLFile=$fileout"
        wokparam -s "%CoreFile=$pk.bin"
    
        set thecommand [wokparam -e FRONTAL_NewFrontalScript]

	wokUtils:FILES:ListToFile ${thecommand} tmp.ccl
	wokUtils:FILES:chmod 0755 tmp.ccl
        msgprint -i "Setting Environnement"
        set WOK_GLOBALS(setenv_proc,tcl) 1
        wokenv -s 
        set WOK_GLOBALS(setenv_proc,tcl) 0

        if [file exists $pk.bin ] {
	    wokUtils:FILES:chmod 0755 $pk.bin
            wokUtils:FILES:delete  $pk.bin
	}
        if {[catch {eval "exec tmp.ccl " } res]} {
	    msgprint -e -c "WOKStep_frontal:Execute" "Enable to generate $rescore"
	    msgprint -e -c "WOKStep_frontal:Execute" $res

	    return 1
        }

        msgprint -i -c "WOKStep_frontal:Execute" "Updating $rescore"
        if [file exists $rescore ] {
	    wokUtils:FILES:chmod 0755 $rescore
            wokUtils:FILES:delete $rescore
	}
        if [catch {eval "exec cp $pk.bin $rescore"} result] {
	    msgprint -e -c "WOKStep_frontal:Execute" $result
        }

    
        set resbinexe [wokinfo -p executable:$execbinid $unit]

        set fileoutmsg [wokinfo -p cmpmsgfile:${pk}_Cmp.us $unit]
        set fileoutoldmsg [wokinfo -p msgfile:${pk}.us $unit]

        msgprint -i -c "WOKStep_frontal:Execute" "Updating $resbinexe"
        wokparam -s "%BINFile=$rescore"
        wokparam -s "%MsgCmpFile=$fileoutmsg"
        wokparam -s "%MsgFile=$fileoutoldmsg"
    
        set thebincommand [wokparam -e FRONTAL_BINScript]

        if {[catch {set fidexe [open $resbinexe "w"]} res] == 0} {
	    puts $fidexe [lindex $thebincommand 0]
	    close $fidexe
	    if {[wokparam -e %Station] != "wnt"} {
	        wokUtils:FILES:chmod 0755 $resbinexe
	    }
        } else {
	    msgprint -e -c "WOKStep_frontal:Execute" "Enable to generate $resbinexe"
	    msgprint -e -c "WOKStep_frontal:Execute" $res

	    return 1
        }

        # insert dependencies
        stepinputadd ${bin}:executable:CCL${bin}

        stepinputadd $fromid

        stepoutputadd -M -P -L -F $pk:executable:CCL$pk

        stepaddexecdepitem -d ${bin}:executable:CCL${bin} $pk:executable:CCL$pk
    
        stepaddexecdepitem -d $fromid $pk:executable:CCL$pk

        stepaddexecdepitem -d ${pk}:source:FROM_FRONTAL $pk:executable:CCL$pk

        stepoutputadd -M -P -L -F $pk:executable:CCL${pk}.bin

        stepaddexecdepitem -d $fromid $pk:executable:CCL${pk}.bin

        stepaddexecdepitem -d ${pk}:source:FROM_FRONTAL $pk:executable:CCL${pk}.bin
    
        stepoutputadd -M -P -L -F $pk:executable:CCL${pk}bin

        stepaddexecdepitem -d $fromid $pk:executable:CCL${pk}bin

        stepaddexecdepitem -d ${pk}:source:FROM_FRONTAL $pk:executable:CCL${pk}bin
    



	cd $savpwd
    }

    return 0;
}
    
proc WOKStep_frontal:ExecuteMessages { unit args } {
    
    set pk [wokinfo -n $unit]

    set wb [wokinfo -N $unit]

    set nameFROMFRONT [woklocate -p ${pk}:source:FROM_FRONTAL]

    set nameMESS [woklocate -p ${pk}:source:MESSAGES]

    set nameCOMP [woklocate -p ${pk}:source:COMPONENTS]

    set hasmess 1

    if {[string length $nameMESS] == 0} {
	msgprint -i "Unable to locate file MESSAGES"
	msgprint -i "No messages compilation done"
	msgprint -i "Catenate message files"
	set hasmess 0

    }

    set fileoutmsg [wokinfo -p tmpfile:${pk}.us $unit]

    if {$hasmess == 0} {
	set fileoutmess [wokinfo -p msgfile:${pk}.us $unit]
    } else {
	set fileoutmess [wokinfo -p msgfile:${pk}_Msg.us $unit]
    }

    if {![file exists $fileoutmess]} {
	set idoutmsg [open $fileoutmess "w"]
	puts $idoutmsg " "
	close $idoutmsg
    }
    
    stepinputadd ${pk}:source:COMPONENTS
    if {$hasmess == 0} {
	stepoutputadd -M -P -L -F ${pk}:msgfile:${pk}.us
	stepaddexecdepitem -d ${pk}:source:COMPONENTS ${pk}:msgfile:${pk}.us
    } else {
	stepoutputadd -M -P -L -F ${pk}:msgfile:${pk}_Msg.us
	stepaddexecdepitem -d ${pk}:source:COMPONENTS ${pk}:msgfile:${pk}_Msg.us
    }

    
    if [file exists $fileoutmsg ] {
	wokUtils:FILES:delete $fileoutmsg
    }

    set idoutmsg [open $fileoutmsg "w"]
    if {$hasmess} {
	puts $idoutmsg "! Messages -> $pk "
    } else {
	puts $idoutmsg ";; CCL Messages -> $pk "
    }

    close $idoutmsg

    set fromunit [wokUtils:FILES:FileToList $nameFROMFRONT]

    if {$fromunit != {}} {
	
	if {$hasmess == 0} {
	    set f [woklocate -p ${fromunit}:msgfile:${fromunit}.us $wb]
	} else {
	    set f [woklocate -p ${fromunit}:msgfile:${fromunit}_Msg.us $wb]
	}
	
	if { $f != {} } {
	    wokUtils:FILES:concat $fileoutmsg $f
	    if {$hasmess == 0} {
		stepinputadd ${fromunit}:msgfile:${fromunit}.us
		stepaddexecdepitem -d ${fromunit}:msgfile:${fromunit}.us ${pk}:msgfile:${pk}.us
	    } else {
		stepinputadd ${fromunit}:msgfile:${fromunit}_Msg.us
		stepaddexecdepitem -d ${fromunit}:msgfile:${fromunit}_Msg.us ${pk}:msgfile:${pk}_Msg.us
	    }
	} else {
	    if {$hasmess} {
		msgprint -w "Could not locate file : ${fromunit}:msgfile:${fromunit}_Msg.us"
	    } else {
		msgprint -w "Could not locate file : ${fromunit}:msgfile:${fromunit}.us"
	    }
	}
    }

    foreach ccl [wokUtils:FILES:FileToList $nameCOMP] {
	    if {$hasmess == 0} {
		set f [woklocate -p ${ccl}:msgfile:${ccl}.us $wb]
	    } else {
		set f [woklocate -p ${ccl}:msgfile:${ccl}_Msg.us $wb]
	    }
	if { $f != {} } { 
	    wokUtils:FILES:concat $fileoutmsg $f
	    if {$hasmess == 0} {
		stepinputadd ${ccl}:msgfile:${ccl}.us
		stepaddexecdepitem -d ${ccl}:msgfile:${ccl}.us ${pk}:msgfile:${pk}.us
	    } else {
		stepinputadd ${ccl}:msgfile:${ccl}_Msg.us
		stepaddexecdepitem -d ${ccl}:msgfile:${ccl}_Msg.us ${pk}:msgfile:${pk}_Msg.us
	    }
	} else {
	    if {$hasmess} {
		msgprint -w "Could not locate file : ${ccl}:msgfile:${ccl}_Msg.us"
	    } else {
		msgprint -w "Could not locate file : ${ccl}:msgfile:${ccl}.us"
	    }
	}
    }

    if {$hasmess} {
	foreach mesunit [wokUtils:FILES:FileToList $nameMESS] {
	    set f [woklocate -p ${mesunit}:msgfile:${mesunit}_Msg.us $wb]
	    if { $f != {} } { 
		wokUtils:FILES:concat $fileoutmsg $f
		stepinputadd ${mesunit}:msgfile:${mesunit}_Msg.us
		stepaddexecdepitem -d ${mesunit}:msgfile:${mesunit}_Msg.us ${pk}:msgfile:${pk}_Msg.us
	    } else {
		msgprint -w "Could not locate file : ${mesunit}:msgfile:${mesunit}_Msg.us"
	    }
	}
    }

    set torecompute 0
    if {![wokUtils:FILES:AreSame $fileoutmsg $fileoutmess]} {
	msgprint -i "Modifying $fileoutmess"
	wokUtils:FILES:copy $fileoutmsg $fileoutmess
	set torecompute 1
    }
    if {[string length [woklocate -p ${pk}:cmpmsgfile:${pk}_Cmp.us]] == 0} {
	set torecompute 1
    }
    if {$hasmess} {
	if {$torecompute} {
	    msgprint -i "Compiling messages"
	    set msgtoolname "MsgBuild"
	    if {[wokparam -e %Station] == "wnt"} {
		set msgtoolname "MsgBuild.exe"
	    }
	    set msgtool [woklocate -p MsgUtil:executable:${msgtoolname}]
	    set resmsg [wokinfo -p cmpmsgfile:${pk}_Cmp.us $unit]
	    if {![catch {eval "exec $msgtool $resmsg $fileoutmess"} errorinmsg ]} {
		msgprint -e $errorinmsg
		wokUtils:FILES:delete $fileoutmess
		return 1
	    } 
	}
	stepoutputadd -M -P -L -F ${pk}:cmpmsgfile:${pk}_Cmp.us
	stepaddexecdepitem -d ${pk}:source:COMPONENTS ${pk}:cmpmsgfile:${pk}_Cmp.us
    }
    return 0
}

proc WOKStep_frontal:Execute { unit args } {

    set pk [wokinfo -n $unit]
    set resold 0
    catch {
	if {$pk != "KernelFrontal"} {
	    if {[wokparam -e %Station] != "wnt"} {
		set resold [WOKStep_frontal:ExecuteOldFrontal $unit $args]
	    } 
	}
    }
    set resnew [WOKStep_frontal:ExecuteNewFrontal $unit $args]

    set resmes [WOKStep_frontal:ExecuteMessages $unit $args]

    return [expr [expr $resold && $resnew] || $resmes]
}
