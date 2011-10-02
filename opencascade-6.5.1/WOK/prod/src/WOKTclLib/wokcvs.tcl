
proc wokcvsUsage { } {
    global env
    global tcl_platform
    puts stderr \
	    "
	This command provide a basic support of CVS for workbenches managed by Wok. Typically it can be
        used to place a workbench under CVS control, provided that units in the workbench have a entry
        in the CVS repository. If your workbench contains OpenCascade units you can place them under the 
        OpenCascade CVS repository that you can find at :pserver:anonymous@cvs.matra-dtv.fr:/www/cvs.
        This is the default. However your can address an other CVS repository. See command below.
        Important note: You must login to the repository before using these commands.
        A basic test is done by searching in your home directory the file .cvspass.

        Further operations such update,commit etc.. can be performed using cvs native commands or 
        multiple and powerful front end that come with CVS. 

	Usage :
 
	wokcvs \[-configure -param -log -checkout\] \[u1,u2,.. \]

        (Note that unit names must be separated by a comma). You can use * wildcard for unit names.

        Do nothing if :
          1. src/<Unit>/CVS  already exists in your workbench
          2. src/<Unit> does not exists in the repository (so cannot be checkout-ed)
	  3. you cannot create units in the workbench (write access to the src directory)
        Complain if:
          1. You have src/<Unit> in your workbench and src/<Unit> is empty. Checkout all files.
          2. src/<Unit> only contains new files (Not already in CVS). Tell you what to do.
                        
	wokcvs -param   : Lists parameters value of the current configuration. A configuration comprises :
                           1. A full CVS repository address in the form
                          :<access-mode>:<username>:<domaine>:/directory. See CVS manual for more 
                          information. Default is :pserver:anonymous@cvs.matra-dtv.fr:/www/cvs 
                           2. A executable wrapper used to perform placement of a directory unit
                           under CVS. The default on this platform is :
                           $env(WOK_LIBRARY)/ud2cvs_$tcl_platform(platform)
                           (Example 4)

        wokcvs -configure : Set parameters values for CVS repository address and wok CVS wrapper.
                            Use -repository to specify the full repository name
                            Use -cvswrapper to specify the full path of a wrapper. Actually the command
                            supplied in Wok is sufficient.(Examples 4 and 5)
                            Use -checkout_options to specify additional option for CVS checkout such as
                             -r -A D etc... Note that only checkout specific option (in CVS sense) can be 
                            given. Option -d of CVS is already used to create adequate directory.

        wokcvs -log  file : Output for cvs transactions are logged out in the file wokcvs.log in
                            your home directory. You can specify an other location for this.(Example 2)

	About cvs checkout options:
	By default the CVS command checkout is invoked with the repository name and the -d option
        to create the adequate directory under the root of the workbench. If you want to add other options,
        create your own copy of the file $env(WOK_LIBRARY)/ud2cvs_$tcl_platform(platform) and use
        the -configure -wrapper option to specify your own checkout wrapper. (Example 5)

        Examples:

	1.Checkout the units TKernel and gp (if they already exists in the current workbench.):
	tclsh> wokcd MyFac:MyShop:MyWb
        tclsh> wokcvs -checkout TKernel,gp

        2.Checkout all units of the current workbench that have an entry in the OpenCascade CVS repository:
        Print cvs output in file /tmp/cvs_output.log
	tclsh> wokcd MyFac:MyShop:MyWb
	tclsh> wokcvs -checkout * -log /tmp/cvs_output.log

        3.Checkout all packages of current workbench:
	tclsh> wokcd MyFac:MyShop:MyWb
	tclsh> wokcvs -checkout [join [w_info -l -T package],]
	(w_info returns a list of units and the Tcl command join inserts a comma between the names)

        4.Configure so that the repository is the OpenCascade one.:
	tclsh> wokcvs -configure -repository :pserver:anonymous@cvs.matra-dtv.fr:/www/cvs
	(Actually this is done by default.)
        tclsh> wokcvs -param

        5.Configure so that checkouts are done with option specified in the file /home/onlyme/mycheckout:
        tclsh> cp $env(WOK_LIBRARY)/ud2cvs_$tcl_platform(platform) /home/onlyme/mycheckout
        tclsh> edit.. /home/onlyme/mycheckout (say add \"-r C30\" to force tag C30)
	tclsh> wokcvs -configure -cvswrapper /home/onlyme/mycheckout
	tclsh> wokcd MyFac:MyShop:MyWb
	tclsh> wokcvs -checkout * 
    "
    return
} 
;#
;# Command entry point.
;#
proc wokcvs { args } {

    global env
    global tcl_platform

    set tblreq(-h)          {}
    set tblreq(-param)      {}
    set tblreq(-wb)         value_required:string
    set tblreq(-configure)  {}
    set tblreq(-log)        value_required:file
    set tblreq(-repository) value_required:string
    set tblreq(-cvswrapper) value_required:string
    set tblreq(-checkout)   value_required:list

    if { [wokUtils:EASY:GETOPT param tabarg tblreq wokcvsUsage $args] == -1 } return
    
    set verbose [info exists tabarg(-v)]
    
    if { [info exists tabarg(-h)] } {
	wokcvsUsage
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

    if { [info exists tabarg(-configure)] } {
	set repository :pserver:anonymous@cvs.matra-dtv.fr:/www/cvs
	if { [info exists tabarg(-repository)] } {
	    set repository $tabarg(-repository)
	}
	set cvswrapper $env(WOK_LIBRARY)/ud2cvs_$tcl_platform(platform)
	if { [info exists tarbarg(-cvswrapper)] } {
	    set cvswrapper $tarbarg(-cvswrapper)
	}
	wokcvs:Configure [wokcvs:FileAdm $curwb] $repository $cvswrapper    
	msgprint -c WOKVC -i "CVS Repository : $repository " 
	msgprint -c WOKVC -i "Wrapper        : $cvswrapper " 
	return
    }

    if { [file exists [set cvsdef [wokcvs:FileAdm $curwb]]] } {
	uplevel #0 source $cvsdef
    } else {
	msgprint -c WOKVC -e "File CVSDEF.tcl not found in the Adm directory of $curwb." 
	return
    }

    if { ![file exists [file join $env(HOME) .cvspass]] } {
	msgprint -c WOKVC -e "Log in to the CVS repository before."
	return
    }


    if { [info exists tabarg(-checkout)] } {
	;# Yes, it's ugly. I change current directory
	set lud [wokUtils:LIST:GM [w_info -l $curwb] $tabarg(-checkout)]
	set savwd [wokcd]
	wokcd $curwb
	set flog [file join $env(HOME) wokcvs.log]
	if [info exists tabarg(-log)] {
	    set flog $tabarg(-log)
	}

	if ![ catch { set logid [ open $flog w ] } status ] {
	    wokcvs:ud2cvs [wokinfo -p srcdir:. $curwb] $lud $logid
	    close $logid
	    msgprint -c WOKVC -i "CVS output logged to $flog.."
	} else {
	    puts stderr $status
	    wokcvs:ud2cvs [wokinfo -p srcdir:. $curwb] $lud stderr
	}

	;# But i restore it. :-)
	wokcd $savwd
	return
    }


}

proc wokcvs:FileAdm { wb } {
    return [file join [wokinfo -p AdmDir:. $wb] CVSDEF.tcl]
}

proc wokcvs:Configure { fileadm repname wrpname } {
    global env
    set proc_defined_in_VC [list \
	    wokcvs:BASE:GetWrap \
	    wokcvs:BASE:GetName]
    eval "proc  wokcvs:BASE:GetWrap { } { return $wrpname }"
    eval "proc  wokcvs:BASE:GetName { } { return $repname }"
    set id [open $fileadm w]
    foreach p ${proc_defined_in_VC} {
	puts $id "proc $p { } {"
	puts $id "[info body $p]"
	puts $id "}"	
    }
    close $id
}
;#
;# Place a list of units under CVS control.
;# root is the directory name under which the chekout will be done
;#
proc wokcvs:ud2cvs { root lud logid } {

    set wrap [wokcvs:BASE:GetWrap]
    set repo [wokcvs:BASE:GetName]

    if [file writable $root] {
	foreach ud $lud {
	    msgprint -c WOKVC -i "Processing unit $ud"
	    catch {exec $wrap $root $ud $repo } status
	    puts $logid $status
	}
    } else {
	msgprint -c WOKVC -e "You need write access to the directory $root"
    }
    return
}
