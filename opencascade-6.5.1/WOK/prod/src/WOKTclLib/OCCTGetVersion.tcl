# Get current OCCT version
proc OCCTGetVersion {} {
    set OCCTVersion ""
    set OCCTVersionFile [woklocate -p "Standard:source:Standard_Version.hxx"]
    if {$OCCTVersionFile != "" && [file readable $OCCTVersionFile]} {
	set v_major ""
	set v_minor ""
	set v_build ""
	set vfd [open $OCCTVersionFile {RDONLY}]
	while {[gets $vfd line] >= 0} {
	    if {[regexp {^[ \t]*\#define[ \t]*OCC_VERSION_MAJOR[ \t]*([0-9]+)} $line str num]} {
		set v_major $num
	    } elseif {[regexp {^[ \t]*\#define[ \t]*OCC_VERSION_MINOR[ \t]*([0-9]+)} $line str num]} {
		set v_minor $num
	    } elseif {[regexp {^[ \t]*\#define[ \t]*OCC_VERSION_MAINTENANCE[ \t]*([0-9]+)} $line str num]} {
		set v_build $num
	    }
	}
	close $vfd
	set OCCTVersion $v_major.$v_minor.$v_build
    }
    return $OCCTVersion
}
