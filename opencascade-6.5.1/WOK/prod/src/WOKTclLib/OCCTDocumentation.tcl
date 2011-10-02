# general procedure for generation Doxygen documentation
# it launches both generation process and post process
proc OCCDoc_GenerateDoc {outDir {modules {}} {doxygenPath {}} {graphvizPath {}} {useSearch YES} {tagFiles {}}} {
    catch {exec $doxygenPath/doxygen [OCCDoc_MakeDoxyfile $outDir $modules $graphvizPath]}
    OCCDoc_PostProcessor $outDir
}

# generate Doxygen configuration file for specified OCCT module of toolkit
proc OCCDoc_MakeDoxyfile {outDir {modules {}} {graphvizPath {}} {useSearch YES} {tagFiles {}}} {

    # by default take all modules
    if { [llength $modules] <= 0 } {
	set modules [OS -lm]
    }

    # create target directory
    if { ! [file exists $outDir] } {
	mkdir $outDir
    }
 
    # set context
    set one_module [expr [llength $modules] == 1]
    if { $one_module } {
	set title "OCCT [$modules:name]"
	set name $modules
    } else {
	set title "Open CASCADE Technology"
	set name OCCT
    }

    # get list of header files in the specified modules
    set filelist {}
    foreach module $modules {
	if {[lsearch [OS -lm] $module] == -1 } {
	    puts "Error: no module $module is known in current workbench"
	    continue
	}
	foreach tk [$module:toolkits] {
	    foreach pk [osutils:tk:units [woklocate -u $tk]] {
		lappend filelist [uinfo -p -T pubinclude $pk]
	    }
	}
    } 

    # filter out files Handle_*.hxx and *.lxx
    set hdrlist {}
    foreach fileset $filelist {
        set hdrset {}
        foreach hdr $fileset {
	    if { ! [regexp {Handle_.*[.]hxx} $hdr] && ! [regexp {.*[.]lxx} $hdr] } {
		lappend hdrset $hdr
	    }
	}
	lappend hdrlist $hdrset
    }
    set filelist $hdrlist

    # get OCCT version number
    set occt_version [OCCTGetVersion]

    set filename "$outDir/$name.Doxyfile"
    msgprint -i -c "WOKStep_DocGenerate:Execute" "Generating Doxygen file for $title in $filename"
    set fileid [open $filename "w"]

    set path_prefix "$outDir/"

    puts $fileid "PROJECT_NAME = \"$title\""
    puts $fileid "PROJECT_NUMBER = $occt_version "
    puts $fileid "OUTPUT_DIRECTORY = ${path_prefix}."
    puts $fileid "CREATE_SUBDIRS   = NO"
    puts $fileid "OUTPUT_LANGUAGE  = English"
    puts $fileid "MULTILINE_CPP_IS_BRIEF = YES"
    puts $fileid "INHERIT_DOCS           = YES"
    puts $fileid "REPEAT_BRIEF           = NO"
    puts $fileid "ALWAYS_DETAILED_SEC    = NO"
    puts $fileid "INLINE_INHERITED_MEMB  = NO"
    puts $fileid "FULL_PATH_NAMES        = NO"
    puts $fileid "OPTIMIZE_OUTPUT_FOR_C  = YES"
    puts $fileid "SUBGROUPING      = YES"
    puts $fileid "DISTRIBUTE_GROUP_DOC   = YES"
    puts $fileid "EXTRACT_ALL	= YES"
    puts $fileid "EXTRACT_PRIVATE	= NO"
    puts $fileid "EXTRACT_LOCAL_CLASSES = NO"
    puts $fileid "EXTRACT_LOCAL_METHODS = NO"
    puts $fileid "HIDE_FRIEND_COMPOUNDS = YES"
    puts $fileid "HIDE_UNDOC_MEMBERS = NO"
    puts $fileid "INLINE_INFO = YES"
    puts $fileid "SHOW_DIRECTORIES	= NO"
    puts $fileid "VERBATIM_HEADERS = NO"
    puts $fileid "QUIET		= YES"
    puts $fileid "WARNINGS		= NO"
    puts $fileid "ENABLE_PREPROCESSING = YES"
    puts $fileid "MACRO_EXPANSION = YES"
    puts $fileid "EXPAND_ONLY_PREDEF = YES"
    puts $fileid "PREDEFINED = Standard_EXPORT __Standard_API __Draw_API Handle(a):=Handle<a>"
    puts $fileid "GENERATE_HTML	= YES"
    puts $fileid "GENERATE_LATEX   = NO"
    puts $fileid "SEARCH_INCLUDES  = YES"
    puts $fileid "GENERATE_TAGFILE = ${path_prefix}${name}.tag"
    puts $fileid "ALLEXTERNALS = NO"
    puts $fileid "EXTERNAL_GROUPS = NO"
    
    # add tag files for OCCT modules (except current one and depending);
    # this is based on file Modules.tcl in unit "OS" which defines list of modules
    # in the order of their dependency
    if { [llength $tagFiles] > 0 } {
	set tagdef {}
	foreach tagfile $tagFiles {
	    if [file exists ${path_prefix}$tagname.tag] {
		set tagdef "$tagdef \\\n           ${path_prefix}${tagname}.tag=../../${tagname}/html"
	    }
	}
	puts $fileid "TAGFILES = $tagdef"
    }

    if { $useSearch } {
	puts $fileid "SEARCHENGINE     = $useSearch"
#	puts $fileid "SERVER_BASED_SEARCH = NO"
    }
    if { "$graphvizPath" == "" && [info exists env(GRAPHVIZ_HOME)] } {
	set graphvizPath $env(GRAPHVIZ_HOME)
    }
    if { "$graphvizPath" != "" } {
	puts $fileid "HAVE_DOT		= YES"
	puts $fileid "DOT_PATH		= $graphvizPath"
    } else {
	puts "Warning: DOT is not found; use environment variable GRAPHVIZ_HOME or command argument to specify its location"
	puts $fileid "HAVE_DOT		= NO"
	puts $fileid "DOT_PATH		= "
    }

    puts $fileid "COLLABORATION_GRAPH = NO"
    puts $fileid "ENABLE_PREPROCESSING = YES"
    puts $fileid "INCLUDE_FILE_PATTERNS = *.hxx *.pxx"
    puts $fileid "EXCLUDE_PATTERNS = */Handle_*.hxx"
    puts $fileid "SKIP_FUNCTION_MACROS = YES"
    puts $fileid "INCLUDE_GRAPH = NO"
    puts $fileid "INCLUDED_BY_GRAPH = NO"
    puts $fileid "DOT_MULTI_TARGETS = YES"
    puts $fileid "DOT_IMAGE_FORMAT = png"
    puts $fileid "INLINE_SOURCES   = NO"

    # include dirs
    set incdirs ""
    foreach wb [w_info -A] {
	set incdirs "$incdirs [wokparam -v %${wb}_Home]/inc"
    }
    puts $fileid "INCLUDE_PATH = $incdirs"

    # list of files to generate
    set mainpage [OCCDoc_MakeMainPage $outDir/$name.dox $modules]
    puts $fileid "INPUT		= $mainpage \\"
    foreach header $filelist {
	puts $fileid "               $header \\"
    } 
    puts $fileid ""

    close $fileid

    return $filename
}

# generate main page file describing module structure
proc OCCDoc_MakeMainPage {outFile modules} {
    set one_module [expr [llength $modules] == 1]

    set fd [open $outFile "w"]

    # main page: list of modules
    if { ! $one_module } {
	puts $fd "/**"
	puts $fd "\\mainpage Open CASCADE Technology"
	foreach mod $modules {
	    puts $fd "\\li \\subpage [string tolower module_$mod]"
	}
	puts $fd "**/\n"
    }

    # one page per module: list of toolkits
    set toolkits {}
    foreach mod $modules {
        puts $fd "/**"
	if { $one_module } {
	    puts $fd "\\mainpage OCCT Module [$mod:name]"
	} else {
	    puts $fd "\\page [string tolower module_$mod] Module [$mod:name]"
	}
	foreach tk [lsort [$mod:toolkits]] {
	    lappend toolkits $tk
	    puts $fd "\\li \\subpage [string tolower toolkit_$tk]"
	}
        puts $fd "**/\n"
    }

    # one page per toolkit: list of packages
    set packages {}
    foreach tk $toolkits {
        puts $fd "/**"
	puts $fd "\\page [string tolower toolkit_$tk] Toolkit $tk"
	foreach pk [lsort [osutils:tk:units [woklocate -u $tk]]] {
	    lappend packages $pk
	    set u [wokinfo -n $pk]
	    puts $fd "\\li \\subpage [string tolower package_$u]"
	}
	puts $fd "**/\n"
    }

    # one page per package: list of classes
    foreach pk $packages {
	set u [wokinfo -n $pk]
        puts $fd "/**"
	puts $fd "\\page [string tolower package_$u] Package $u"
	foreach hdr [lsort [uinfo -f -T pubinclude $pk]] {
	    if { ! [regexp {^Handle_} $hdr] && [regexp {(.*)[.]hxx} $hdr str obj] } {
		puts $fd "\\li \\subpage $obj"
	    }
	}
	puts $fd "**/\n"
    }

    # one page per class: set reference to package
#    foreach pk $packages {
#	set u [wokinfo -n $pk]
#	foreach hdr [uinfo -f -T pubinclude $pk] {
#	    if { ! [regexp {^Handle_} $hdr] && [regexp {(.*)[.]hxx} $hdr str obj] } {
#		puts $fd "/**"
#		puts $fd "\\class $obj"
#		puts $fd "Contained in \\ref [string tolower package_$u]"
##		puts $fd "\\addtogroup package_$u"
#		puts $fd "**/\n"
#	    }
#	}
#    }

    close $fd
    return $outFile
}

# parse generated files to add a navigation path 
proc OCCDoc_PostProcessor {outDir} {
    puts "Post-process is started..."
    append outDir "/html"
    set files [glob -nocomplain -type f $outDir/package__*]
    if { $files != {} } {
        foreach f [lsort $files] {
	    set packageFilePnt [open $f r]
            set packageFile [read $packageFilePnt]
            set navPath [OCCDoc_GetNodeContents "div" " class=\"navpath\"" $packageFile]
            set packageName [OCCDoc_GetNodeContents "h1" "" $packageFile]
	    regsub -all {<[^<>]*>} $packageName "" packageName 
	    
	    # add package link to nav path
	    set first [expr 1 + [string last "/" $f]]
	    set last [expr [string length $f] - 1]
	    set packageFileName [string range $f $first $last]
	    append navPath "&nbsp;&raquo;&nbsp; <a class=\"el\" href=\"$packageFileName\">$packageName</a>" 
	    
	    # get list of files to update
	    set listContents [OCCDoc_GetNodeContents "div" " class=\"contents\"" $packageFile]
	    set listContents [OCCDoc_GetNodeContents "ul" "" $listContents]
	    set lines [split $listContents "\n"]
	    foreach line $lines {
		#puts "mLine:  $line"
		if {[regexp {href=\"([^\"]*)\"} $line tmpLine classFileName]} {
		    # check if anchor is there
		    set anchorPos [string first "#" $classFileName]
		    if {$anchorPos != -1} {
			set classFileName [string range $classFileName 0 [expr $anchorPos - 1]]
		    }
		    # read class file
		    set classFilePnt [open $outDir/$classFileName r+]
		    set classFile [read $classFilePnt]
		    # find position of content block 
		    set contentPos [string first "<div class=\"contents\">" $classFile]
		    set navPart [string range $classFile 0 [expr $contentPos - 1]]
		    # position where to insert nav path
		    set posToInsert [string last "</div>" $navPart]
		    set prePart [string range $classFile 0 [expr $posToInsert - 1]]
		    set postPart [string range $classFile $posToInsert [string length $classFile]]
		    set newClassFile ""
		    append newClassFile $prePart "<div class=\"navpath\">" $navPath "</div>" $postPart
		    # write updated content
		    seek $classFilePnt 0
		    puts $classFilePnt $newClassFile
		    close $classFilePnt
		} 
	
	    }
	    
	    
	   close $packageFilePnt
        }
    } else {
        puts "no files found"
    }
}

# get contents of the given html node
proc OCCDoc_GetNodeContents {node props html} {
    set openTag "<$node$props>"
    set closingTag "</$node>"
    set start [string first $openTag $html]
 
    if {$start == -1} {
	return ""
    }
    set start [expr $start + [string length $openTag]]
    set end [string length $html]
    set html [string range $html $start $end]
    
    set start [string first $closingTag $html]
    set end [string length $html]
    if {$start == -1} {
	return ""
    }
    set start [expr $start - 1]
    return [string range $html 0 $start]
}
