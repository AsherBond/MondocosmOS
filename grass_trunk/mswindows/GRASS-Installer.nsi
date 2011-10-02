;----------------------------------------------------------------------------------------------------------------------------

;GRASS Installer for Windows
;Written by Marco Pasetti
;Updated for OSGeo4W by Colin Nielsen, Helmut Kudrnovsky, and Martin Landa
;Last Update: $Id: GRASS-Installer.nsi 47953 2011-08-29 18:45:39Z mmetz $
;Mail to: grass-dev@lists.osgeo.org 

;----------------------------------------------------------------------------------------------------------------------------

;Define the source path of the demolocation files

!define DEMOLOCATION_PATH "c:\osgeo4w\usr\src\grass_trunk\demolocation"

;Define the source of the patched msys.bat

!define MSYS_BATCH "C:\OSGeo4W\usr\src\grass_trunk\mswindows\osgeo4w\msys.bat"

;Select if you are building a "Development Version" (Devel) or a "Release Version" (Release) of the GRASS Installer

!define INSTALLER_TYPE "Devel"

;----------------------------------------------------------------------------------------------------------------------------
;set compression configuration

SetCompressor /SOLID lzma
SetCompressorDictSize 64

;----------------------------------------------------------------------------------------------------------------------------

;Version variables

!define RELEASE_VERSION_NUMBER "7.0.0"
!define RELEASE_SVN_REVISION "36599"
!define RELEASE_BINARY_REVISION "1"
!define RELEASE_GRASS_COMMAND "grass70"
!define RELEASE_GRASS_BASE "GRASS 7.0"

!define DEVEL_VERSION_NUMBER "7.0.SVN"
!define DEVEL_SVN_REVISION "36599"
!define DEVEL_BINARY_REVISION "1"
!define DEVEL_GRASS_COMMAND "grass70svn"
!define DEVEL_GRASS_BASE "GRASS 7.0.SVN"

;----------------------------------------------------------------------------------------------------------------------------

;Don't modify the following lines

;----------------------------------------------------------------------------------------------------------------------------

;NSIS Includes

!include "MUI2.nsh"
!include "LogicLib.nsh"

;----------------------------------------------------------------------------------------------------------------------------

;Set the installer variables, depending on the selected version to build

!if ${INSTALLER_TYPE} == "Release"
	!define VERSION_NUMBER "${RELEASE_VERSION_NUMBER}"
	!define SVN_REVISION "${RELEASE_SVN_REVISION}"
	!define BINARY_REVISION "${RELEASE_BINARY_REVISION}"
	!define GRASS_COMMAND "${RELEASE_GRASS_COMMAND}"
	!define GRASS_BASE "${RELEASE_GRASS_BASE}"
	!define INSTALLER_NAME "WinGRASS-${VERSION_NUMBER}-${BINARY_REVISION}-Setup.exe"
	!define DISPLAYED_NAME "GRASS ${VERSION_NUMBER}-${BINARY_REVISION}"
	!define CHECK_INSTALL_NAME "GRASS"
	!define PACKAGE_FOLDER ".\GRASS-70-Release-Package"
!else if ${INSTALLER_TYPE} == "Devel"
	!define VERSION_NUMBER "${DEVEL_VERSION_NUMBER}"
	!define SVN_REVISION "${DEVEL_SVN_REVISION}"
	!define BINARY_REVISION "${DEVEL_BINARY_REVISION}"
	!define GRASS_COMMAND "${DEVEL_GRASS_COMMAND}"
	!define GRASS_BASE "${DEVEL_GRASS_BASE}"
	!define INSTALLER_NAME "WinGRASS-${VERSION_NUMBER}-r${SVN_REVISION}-${BINARY_REVISION}-Setup.exe"
	!define DISPLAYED_NAME "GRASS ${VERSION_NUMBER}-r${SVN_REVISION}-${BINARY_REVISION}"
	!define CHECK_INSTALL_NAME "GRASS 70 SVN"
	!define PACKAGE_FOLDER ".\GRASS-70-Devel-Package"
!endif

;----------------------------------------------------------------------------------------------------------------------------

;Publisher variables

!define PUBLISHER "GRASS Development Team"
!define WEB_SITE "http://grass.osgeo.org"
!define WIKI_PAGE "http://grass.osgeo.org/wiki"

;----------------------------------------------------------------------------------------------------------------------------

;General Definitions

;Name of the application shown during install
Name "${DISPLAYED_NAME}"

;Name of the output file (installer executable)
OutFile "${INSTALLER_NAME}"

;Define installation folder
InstallDir "$PROGRAMFILES\${GRASS_BASE}"

;Request application privileges for Windows Vista
RequestExecutionLevel admin

;Tell the installer to show Install and Uninstall details as default
ShowInstDetails show
ShowUnInstDetails show

;----------------------------------------------------------------------------------------------------------------------------

;StrReplace Function
;Replaces all ocurrences of a given needle within a haystack with another string
;Written by dandaman32
 
Var STR_REPLACE_VAR_0
Var STR_REPLACE_VAR_1
Var STR_REPLACE_VAR_2
Var STR_REPLACE_VAR_3
Var STR_REPLACE_VAR_4
Var STR_REPLACE_VAR_5
Var STR_REPLACE_VAR_6
Var STR_REPLACE_VAR_7
Var STR_REPLACE_VAR_8
 
Function StrReplace
	Exch $STR_REPLACE_VAR_2
	Exch 1
	Exch $STR_REPLACE_VAR_1
	Exch 2
	Exch $STR_REPLACE_VAR_0
		StrCpy $STR_REPLACE_VAR_3 -1
		StrLen $STR_REPLACE_VAR_4 $STR_REPLACE_VAR_1
		StrLen $STR_REPLACE_VAR_6 $STR_REPLACE_VAR_0
		loop:
			IntOp $STR_REPLACE_VAR_3 $STR_REPLACE_VAR_3 + 1
			StrCpy $STR_REPLACE_VAR_5 $STR_REPLACE_VAR_0 $STR_REPLACE_VAR_4 $STR_REPLACE_VAR_3
			StrCmp $STR_REPLACE_VAR_5 $STR_REPLACE_VAR_1 found
			StrCmp $STR_REPLACE_VAR_3 $STR_REPLACE_VAR_6 done
			Goto loop
		found:
			StrCpy $STR_REPLACE_VAR_5 $STR_REPLACE_VAR_0 $STR_REPLACE_VAR_3
			IntOp $STR_REPLACE_VAR_8 $STR_REPLACE_VAR_3 + $STR_REPLACE_VAR_4
			StrCpy $STR_REPLACE_VAR_7 $STR_REPLACE_VAR_0 "" $STR_REPLACE_VAR_8
			StrCpy $STR_REPLACE_VAR_0 $STR_REPLACE_VAR_5$STR_REPLACE_VAR_2$STR_REPLACE_VAR_7
			StrLen $STR_REPLACE_VAR_6 $STR_REPLACE_VAR_0
			Goto loop
		done:
	Pop $STR_REPLACE_VAR_1 ; Prevent "invalid opcode" errors and keep the
	Pop $STR_REPLACE_VAR_1 ; stack as it was before the function was called
	Exch $STR_REPLACE_VAR_0
FunctionEnd
 
!macro _strReplaceConstructor OUT NEEDLE NEEDLE2 HAYSTACK
	Push "${HAYSTACK}"
	Push "${NEEDLE}"
	Push "${NEEDLE2}"
	Call StrReplace
	Pop "${OUT}"
!macroend
 
!define StrReplace '!insertmacro "_strReplaceConstructor"'

;----------------------------------------------------------------------------------------------------------------------------

;.onInit Function (called when the installer is nearly finished initializing)

;Check if GRASS is already installed on the system and, if yes, what version and binary release;
;depending on that, select the install procedure:

;1. first installation = if GRASS is not already installed
;install GRASS asking for the install PATH

;2. upgrade installation = if an older release of GRASS is already installed
;call the uninstaller of the currently installed GRASS release
;if the uninstall procedure succeeded, call the current installer without asking for the install PATH
;GRASS will be installed in the same PATH of the previous installation

;3. downgrade installation = if a newer release of GRASS is already installed
;call the uninstaller of the currently installed GRASS release
;if the uninstall procedure succeeded, call the current installer without asking for the install PATH
;GRASS will be installed in the same PATH of the previous installation

;4. repair installation = if the same release of GRASS is already installed
;call the uninstaller of the currently installed GRASS release
;if the uninstall procedure succeeded, call the current installer asking for the install PATH

;the currently installed release of GRASS is defined by the variable $INSTALLED_VERSION = $INSTALLED_SVN_REVISION + $INSTALLED_BINARY_REVISION 

Function .onInit

	Var /GLOBAL ASK_FOR_PATH
	StrCpy $ASK_FOR_PATH "YES"

	Var /GLOBAL UNINSTALL_STRING
	Var /GLOBAL INSTALL_PATH
	
	Var /GLOBAL INSTALLED_VERSION_NUMBER
	Var /GLOBAL INSTALLED_SVN_REVISION
	Var /GLOBAL INSTALLED_BINARY_REVISION
	
	Var /GLOBAL INSTALLED_VERSION
	
	Var /GLOBAL DISPLAYED_INSTALLED_VERSION
	
	Var /GLOBAL MESSAGE_0_
	Var /GLOBAL MESSAGE_1_
	Var /GLOBAL MESSAGE_2_
	Var /GLOBAL MESSAGE_3_
	
	Var /GLOBAL R_HKLM_INSTALL_PATH 
	Var /GLOBAL R_HKCU_INSTALL_PATH
	
	ReadRegStr $UNINSTALL_STRING HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}" "UninstallString"
	ReadRegStr $INSTALL_PATH HKLM "Software\${GRASS_BASE}" "InstallPath"
	ReadRegStr $INSTALLED_VERSION_NUMBER HKLM "Software\${GRASS_BASE}" "VersionNumber"
	ReadRegStr $INSTALLED_SVN_REVISION HKLM "Software\${GRASS_BASE}" "SvnRevision"
	
	ReadRegStr $R_HKLM_INSTALL_PATH HKLM "Software\R-core\R" "InstallPath" 
	ReadRegStr $R_HKCU_INSTALL_PATH HKCU "Software\R-core\R" "InstallPath"
	
	${If} $INSTALLED_SVN_REVISION == ""
		ReadRegStr $INSTALLED_SVN_REVISION HKLM "Software\${GRASS_BASE}" "Revision"
	${EndIf}	
	
	ReadRegStr $INSTALLED_BINARY_REVISION HKLM "Software\${GRASS_BASE}" "BinaryRevision"
	
	StrCpy $MESSAGE_0_ "${CHECK_INSTALL_NAME} is already installed on your system.$\r$\n"
	StrCpy $MESSAGE_0_ "$MESSAGE_0_$\r$\n"
	
	!if ${INSTALLER_TYPE} == "Release"		
		${If} $INSTALLED_BINARY_REVISION == ""
			StrCpy $DISPLAYED_INSTALLED_VERSION "$INSTALLED_VERSION_NUMBER"
		${Else}
			StrCpy $DISPLAYED_INSTALLED_VERSION "$INSTALLED_VERSION_NUMBER-$INSTALLED_BINARY_REVISION"
		${EndIf}
	!else
		StrCpy $DISPLAYED_INSTALLED_VERSION "$INSTALLED_VERSION_NUMBER-$INSTALLED_SVN_REVISION-$INSTALLED_BINARY_REVISION"
	!endif
	
	StrCpy $MESSAGE_0_ "$MESSAGE_0_The installed release is $DISPLAYED_INSTALLED_VERSION$\r$\n"
	
	StrCpy $MESSAGE_1_ "$MESSAGE_0_$\r$\n"
	StrCpy $MESSAGE_1_ "$MESSAGE_1_You are going to install a newer release of ${CHECK_INSTALL_NAME}$\r$\n"
	StrCpy $MESSAGE_1_ "$MESSAGE_1_$\r$\n"
	StrCpy $MESSAGE_1_ "$MESSAGE_1_Press OK to uninstall GRASS $DISPLAYED_INSTALLED_VERSION"
	StrCpy $MESSAGE_1_ "$MESSAGE_1_ and install ${DISPLAYED_NAME} or Cancel to quit."
	
	StrCpy $MESSAGE_2_ "$MESSAGE_0_$\r$\n"
	StrCpy $MESSAGE_2_ "$MESSAGE_2_You are going to install an older release of ${CHECK_INSTALL_NAME}$\r$\n"
	StrCpy $MESSAGE_2_ "$MESSAGE_2_$\r$\n"
	StrCpy $MESSAGE_2_ "$MESSAGE_2_Press OK to uninstall GRASS $DISPLAYED_INSTALLED_VERSION"
	StrCpy $MESSAGE_2_ "$MESSAGE_2_ and install ${DISPLAYED_NAME} or Cancel to quit."
	
	StrCpy $MESSAGE_3_ "$MESSAGE_0_$\r$\n"
	StrCpy $MESSAGE_3_ "$MESSAGE_3_This is the latest release available.$\r$\n"
	StrCpy $MESSAGE_3_ "$MESSAGE_3_$\r$\n"
	StrCpy $MESSAGE_3_ "$MESSAGE_3_Press OK to reinstall ${DISPLAYED_NAME} or Cancel to quit."
	
	IntOp $INSTALLED_SVN_REVISION $INSTALLED_SVN_REVISION * 1
	IntOp $INSTALLED_BINARY_REVISION $INSTALLED_BINARY_REVISION * 1
	IntOp $INSTALLED_VERSION $INSTALLED_SVN_REVISION + $INSTALLED_BINARY_REVISION
	
	!define /math VERSION ${SVN_REVISION} + ${BINARY_REVISION}
	
	${If} $INSTALLED_VERSION_NUMBER == ""
	${Else}
		${If} $INSTALLED_VERSION < ${VERSION}
			MessageBox MB_OKCANCEL "$MESSAGE_1_" IDOK upgrade IDCANCEL quit_upgrade
			upgrade:
				StrCpy $ASK_FOR_PATH "NO"
				ExecWait '"$UNINSTALL_STRING" _?=$INSTALL_PATH' $0
				Goto continue_upgrade
			quit_upgrade:
				Abort
			continue_upgrade:
		${ElseIf} $INSTALLED_VERSION > ${VERSION}
			MessageBox MB_OKCANCEL "$MESSAGE_2_" IDOK downgrade IDCANCEL quit_downgrade
			downgrade:
				StrCpy $ASK_FOR_PATH "NO"
				ExecWait '"$UNINSTALL_STRING" _?=$INSTALL_PATH' $0
				Goto continue_downgrade
			quit_downgrade:
				Abort
			continue_downgrade:
		${ElseIf} $INSTALLED_VERSION = ${VERSION}
			MessageBox MB_OKCANCEL "$MESSAGE_3_" IDOK reinstall IDCANCEL quit_reinstall
			reinstall:
				ExecWait '"$UNINSTALL_STRING" _?=$INSTALL_PATH' $0
				Goto continue_reinstall
			quit_reinstall:
				Abort
			continue_reinstall:
		${EndIf}	
	${EndIf}
	
	${If} $INSTALLED_VERSION_NUMBER == ""
	${Else}
		${If} $0 = 0
		${Else}
			Abort
		${EndIf}
	${EndIf}

FunctionEnd

;----------------------------------------------------------------------------------------------------------------------------

;CheckUpdate Function
;Check if to show the MUI_PAGE_DIRECTORY during the installation (to ask for the install PATH)

Function CheckUpdate

	${If} $ASK_FOR_PATH == "NO"	
		Abort
	${EndIf}
	
FunctionEnd

;----------------------------------------------------------------------------------------------------------------------------

;CheckInstDir Function
;this is commented out, because the installation path should be in $PROGRAMFILES
;Check if GRASS is going to be installed in a directory containing spaces
;if yes, show a warning message

;Function CheckInstDir
;	Var /GLOBAL INSTDIR_TEST
;	Var /GLOBAL INSTDIR_LENGHT	
;	Var /GLOBAL INSTDIR_TEST_LENGHT
;	Var /GLOBAL MESSAGE_CHKINST_
;	
;	StrCpy $MESSAGE_CHKINST_ "WARNING: you are about to install GRASS into a directory that has spaces$\r$\n"
;	StrCpy $MESSAGE_CHKINST_ "$MESSAGE_CHKINST_in either its name or the path of directories leading up to it.$\r$\n"
;	StrCpy $MESSAGE_CHKINST_ "$MESSAGE_CHKINST_Some functionalities of GRASS might be hampered by this. We would highly$\r$\n"
;	StrCpy $MESSAGE_CHKINST_ "$MESSAGE_CHKINST_appreciate if you tried and reported any problems, so that we can fix them.$\r$\n"
;	StrCpy $MESSAGE_CHKINST_ "$MESSAGE_CHKINST_However, if you want to avoid any such issues, we recommend that you$\r$\n"
;	StrCpy $MESSAGE_CHKINST_ "$MESSAGE_CHKINST_choose a simple installation path without spaces, such as: C:\${GRASS_BASE}.$\r$\n"
;	
;	${StrReplace} "$INSTDIR_TEST" " " "" "$INSTDIR"
;	
;	StrLen $INSTDIR_LENGHT "$INSTDIR"
;	StrLen $INSTDIR_TEST_LENGHT "$INSTDIR_TEST"
;	
;	${If} $INSTDIR_TEST_LENGHT < $INSTDIR_LENGHT	
;		MessageBox MB_OK|MB_ICONEXCLAMATION "$MESSAGE_CHKINST_"
;	${EndIf}
;	
;FunctionEnd

;----------------------------------------------------------------------------------------------------------------------------
;ReplaceLineString Function
;Replace String in an existing file
; code taken from http://nsis.sourceforge.net/Replace_line_that_starts_with_specified_string

Function ReplaceLineStr
 Exch $R0 ; string to replace that whole line with
 Exch
 Exch $R1 ; string that line should start with
 Exch
 Exch 2
 Exch $R2 ; file
 Push $R3 ; file handle
 Push $R4 ; temp file
 Push $R5 ; temp file handle
 Push $R6 ; global
 Push $R7 ; input string length
 Push $R8 ; line string length
 Push $R9 ; global
 
  StrLen $R7 $R1
 
  GetTempFileName $R4
 
  FileOpen $R5 $R4 w
  FileOpen $R3 $R2 r
 
  ReadLoop:
  ClearErrors
   FileRead $R3 $R6
    IfErrors Done
 
   StrLen $R8 $R6
   StrCpy $R9 $R6 $R7 -$R8
   StrCmp $R9 $R1 0 +3
 
    FileWrite $R5 "$R0$\r$\n"
    Goto ReadLoop
 
    FileWrite $R5 $R6
    Goto ReadLoop
 
  Done:
 
  FileClose $R3
  FileClose $R5
 
  SetDetailsPrint none
   Delete $R2
   Rename $R4 $R2
  SetDetailsPrint both
 
 Pop $R9
 Pop $R8
 Pop $R7
 Pop $R6
 Pop $R5
 Pop $R4
 Pop $R3
 Pop $R2
 Pop $R1
 Pop $R0
FunctionEnd

;----------------------------------------------------------------------------------------------------------------------------

;Interface Settings

!define MUI_ABORTWARNING
!define MUI_ICON ".\Installer-Files\Install_GRASS.ico"
!define MUI_UNICON ".\Installer-Files\Uninstall_GRASS.ico"
!define MUI_HEADERIMAGE_BITMAP_NOSTETCH ".\Installer-Files\InstallHeaderImage.bmp"
!define MUI_HEADERIMAGE_UNBITMAP_NOSTRETCH ".\Installer-Files\UnInstallHeaderImage.bmp"
!define MUI_WELCOMEFINISHPAGE_BITMAP ".\Installer-Files\WelcomeFinishPage.bmp"
!define MUI_UNWELCOMEFINISHPAGE_BITMAP ".\Installer-Files\UnWelcomeFinishPage.bmp"

;----------------------------------------------------------------------------------------------------------------------------

;Installer Pages

!insertmacro MUI_PAGE_WELCOME

;These indented statements modify settings for MUI_PAGE_LICENSE
; (with thanks to Ubuntu)
    !define MUI_LICENSEPAGE_TEXT_BOTTOM "GRASS is software libre. You are encouraged and legally \
	entitled to copy, reinstall, modify, and redistribute this program \
	for yourself and your friends under the terms of the GPL. Happy \
	mapping!"
    !define MUI_LICENSEPAGE_BUTTON "Next >"
!insertmacro MUI_PAGE_LICENSE "${PACKAGE_FOLDER}\GPL.TXT"

!define MUI_PAGE_CUSTOMFUNCTION_PRE CheckUpdate
!insertmacro MUI_PAGE_DIRECTORY

;Page custom CheckInstDir

!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_INSTFILES

;These indented statements modify settings for MUI_PAGE_FINISH
    !define MUI_FINISHPAGE_NOAUTOCLOSE
    !define MUI_FINISHPAGE_RUN
    !define MUI_FINISHPAGE_RUN_NOTCHECKED
    !define MUI_FINISHPAGE_RUN_TEXT "Launch GRASS GIS"
    !define MUI_FINISHPAGE_RUN_FUNCTION "LaunchGrass"
    !define MUI_FINISHPAGE_SHOWREADME
    !define MUI_FINISHPAGE_SHOWREADME_NOTCHECKED
    !define MUI_FINISHPAGE_SHOWREADME_TEXT "View the reference manual"
    !define MUI_FINISHPAGE_SHOWREADME_FUNCTION "ViewReadme"
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

;----------------------------------------------------------------------------------------------------------------------------

;Language files

!insertmacro MUI_LANGUAGE "English"

;----------------------------------------------------------------------------------------------------------------------------

;launch Grass Gis by exit the installation wizard

Function LaunchGrass

     ExecShell "" "$DESKTOP\GRASS ${VERSION_NUMBER}.lnk"

FunctionEnd

;----------------------------------------------------------------------------------------------------------------------------

;launch reference manual by exit the installation wizard

Function ViewReadme

      ExecShell "open" "$INSTDIR\docs\html\index.html"

FunctionEnd

;----------------------------------------------------------------------------------------------------------------------------

;Installer Sections

;Declares the variables for optional Sample Data Sections
Var /GLOBAL HTTP_PATH
Var /GLOBAL ARCHIVE_NAME
Var /GLOBAL EXTENDED_ARCHIVE_NAME
Var /GLOBAL ORIGINAL_UNTAR_FOLDER
Var /GLOBAL CUSTOM_UNTAR_FOLDER
Var /GLOBAL ARCHIVE_SIZE_KB
Var /GLOBAL ARCHIVE_SIZE_MB
Var /GLOBAL DOWNLOAD_MESSAGE_

Section "GRASS" SecGRASS

	SectionIn RO
	
	;Set the INSTALL_DIR variable
	Var /GLOBAL INSTALL_DIR
	
	${If} $ASK_FOR_PATH == "NO"	
		StrCpy $INSTALL_DIR "$INSTALL_PATH"
	${Else}
		StrCpy $INSTALL_DIR "$INSTDIR"
	${EndIf}
	
	;Set to try to overwrite existing files
	SetOverwrite try	
	
	;Set the GIS_DATABASE directory
	SetShellVarContext current
	Var /GLOBAL GIS_DATABASE	
	StrCpy $GIS_DATABASE "$DOCUMENTS\GIS DataBase"
	
	;Create the GIS_DATABASE directory
	CreateDirectory "$GIS_DATABASE"

	;add Installer files
	SetOutPath "$INSTALL_DIR\icons"
	File .\Installer-Files\GRASS.ico
	File .\Installer-Files\GRASS_Web.ico
	File .\Installer-Files\GRASS_tcltk.ico
	File .\Installer-Files\GRASS_MSys.ico
	File .\Installer-Files\GRASS_CMD.ico
	File .\Installer-Files\MSYS_Custom_Icon.ico
	File .\Installer-Files\WinGRASS.ico	
	SetOutPath "$INSTALL_DIR"
	File .\Installer-Files\GRASS-WebSite.url
	File .\Installer-Files\WinGRASS-README.url
	
	;add GRASS files
	SetOutPath "$INSTALL_DIR"
	File /r ${PACKAGE_FOLDER}\*.*

	;create run_gmkfontcap.bat
	ClearErrors
	FileOpen $0 $INSTALL_DIR\etc\run_gmkfontcap.bat w
	IfErrors done_create_run_gmkfontcap.bat
	FileWrite $0 '@echo off$\r$\n'
	FileWrite $0 'rem #########################################################################$\r$\n'
	FileWrite $0 'rem #$\r$\n'
	FileWrite $0 'rem # Run g.mkfontcap outside a grass session during installation$\r$\n'
	FileWrite $0 'rem #$\r$\n'
	FileWrite $0 'rem #########################################################################$\r$\n'
	FileWrite $0 'echo Setup of WinGRASS-${VERSION_NUMBER}$\r$\n'
	FileWrite $0 'echo Generating the font configuration file by scanning various directories for fonts.$\r$\n'
	FileWrite $0 'echo Please wait. Console window will close automatically ....$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 'rem set gisbase$\r$\n'
	FileWrite $0 'set GISBASE=$INSTALL_DIR$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 'rem set path to freetype dll$\r$\n'
	FileWrite $0 'set FREETYPEBASE=$INSTALL_DIR\extralib;$INSTALL_DIR\lib$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 'rem set dependecies path$\r$\n'
	FileWrite $0 'set PATH=%FREETYPEBASE%;%PATH%$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 'rem run g.mkfontcap outside a grass session$\r$\n'
	FileWrite $0 '"%GISBASE%\bin\g.mkfontcap.exe" -o$\r$\n'
	FileWrite $0 'exit$\r$\n'
	FileClose $0
	done_create_run_gmkfontcap.bat:

	;create run_gmkfontcap.bat.manifest
	ClearErrors
	FileOpen $0 $INSTALL_DIR\etc\run_gmkfontcap.bat.manifest w
	IfErrors done_create_run_gmkfontcap.bat.manifest
	FileWrite $0 '	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>$\r$\n'
	FileWrite $0 '<assembly xmlns="urn:schemas-microsoft-com:asm.v1" manifestVersion="1.0">$\r$\n' 
	FileWrite $0 '  <assemblyIdentity version="1.0.0.0"$\r$\n'
	FileWrite $0 '     processorArchitecture="X86"$\r$\n'
	FileWrite $0 '     name="run_gmkfontcap"$\r$\n'
	FileWrite $0 '     type="win32"/>$\r$\n' 
	FileWrite $0 '  <description>GRASS help script:run_gmkfontcap<description>$\r$\n' 
	FileWrite $0 '  <!-- Identify the application security requirements. -->$\r$\n'
	FileWrite $0 '  <trustInfo xmlns="urn:schemas-microsoft-com:asm.v2">$\r$\n'
	FileWrite $0 '    <security>$\r$\n'
	FileWrite $0 '      <requestedPrivileges>$\r$\n'
	FileWrite $0 '        <requestedExecutionLevel$\r$\n'
	FileWrite $0 '          level="asInvoker"$\r$\n'
	FileWrite $0 '          uiAccess="false"/>$\r$\n'
	FileWrite $0 '        </requestedPrivileges>$\r$\n'
	FileWrite $0 '       </security>$\r$\n'
	FileWrite $0 '  </trustInfo>$\r$\n'
	FileWrite $0 '</assembly>$\r$\n'
	FileClose $0
	done_create_run_gmkfontcap.bat.manifest:
	
	;Run g.mkfontcap outside a grass session during installation to catch all fonts
	ExecWait '"$INSTALL_DIR\etc\run_gmkfontcap.bat"'
	
	;Install demolocation into the GIS_DATABASE directory
	SetOutPath "$GIS_DATABASE\demolocation"
	File /r ${DEMOLOCATION_PATH}\*.*
	
	;add msys.bat into the INSTALL_DIR\msys directory
	SetOutPath "$INSTALL_DIR\msys"
	File /r ${MSYS_BATCH}	
	
	;Create the Uninstaller
	WriteUninstaller "$INSTALL_DIR\Uninstall-GRASS.exe"
	
	;Registry Key Entries
	
	;HKEY_LOCAL_MACHINE Install entries
	;Set the Name, Version and Revision of GRASS + PublisherInfo + InstallPath	
	WriteRegStr HKLM "Software\${GRASS_BASE}" "Name" "GRASS"
	WriteRegStr HKLM "Software\${GRASS_BASE}" "VersionNumber" "${VERSION_NUMBER}"
	WriteRegStr HKLM "Software\${GRASS_BASE}" "SvnRevision" "${SVN_REVISION}"
	WriteRegStr HKLM "Software\${GRASS_BASE}" "BinaryRevision" "${BINARY_REVISION}"
	WriteRegStr HKLM "Software\${GRASS_BASE}" "Publisher" "${PUBLISHER}"
	WriteRegStr HKLM "Software\${GRASS_BASE}" "WebSite" "${WEB_SITE}"
	WriteRegStr HKLM "Software\${GRASS_BASE}" "InstallPath" "$INSTALL_DIR"
	
	;HKEY_LOCAL_MACHINE Uninstall entries
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}" "DisplayName" "GRASS"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}" "UninstallString" "$INSTALL_DIR\Uninstall-GRASS.exe"
	
	!if ${INSTALLER_TYPE} == "Release"
		WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}"\
		"DisplayVersion" "${VERSION_NUMBER}-${BINARY_REVISION}"
	!else
		WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}"\
		"DisplayVersion" "${VERSION_NUMBER}-r${SVN_REVISION}-${BINARY_REVISION}"
	!endif
	
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}" "DisplayIcon" "$INSTALL_DIR\icons\GRASS.ico"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}" "EstimatedSize" 1
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}" "HelpLink" "${WIKI_PAGE}"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}" "URLInfoAbout" "${WEB_SITE}"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}" "Publisher" "${PUBLISHER}"
  
	;Create the Desktop Shortcut
	SetShellVarContext current
	
	CreateShortCut "$DESKTOP\GRASS ${VERSION_NUMBER}.lnk" "$INSTALL_DIR\${GRASS_COMMAND}.bat" "-wxpython"\
	"$INSTALL_DIR\icons\GRASS.ico" "" SW_SHOWMINIMIZED "" "Launch GRASS ${VERSION_NUMBER} with the new wxPython GUI"

	; not working after changing to start Grass7 by grass70.py
	;CreateShortCut "$DESKTOP\GRASS ${VERSION_NUMBER} with MSYS.lnk" "$INSTALL_DIR\msys\msys.bat" "/grass/bin/${GRASS_COMMAND} -wxpython"\
	;"$INSTALL_DIR\icons\GRASS_MSys.ico" "" SW_SHOWNORMAL "" "Launch GRASS ${VERSION_NUMBER} with the new wxPython GUI and a MSYS UNIX terminal"
					
	; new attempt to get Grass70-wx-gui and a working msys-shell in a windows command line
	CreateShortCut "$DESKTOP\GRASS ${VERSION_NUMBER} with MSYS.lnk" "$INSTALL_DIR\set_shell_start_${GRASS_COMMAND}.bat" ""\
	"$INSTALL_DIR\icons\GRASS_MSys.ico" "" SW_SHOWNORMAL "" "Launch GRASS ${VERSION_NUMBER} with the new wxPython GUI and a MSYS-Windows-commandline"
 	
	;Create the Windows Start Menu Shortcuts
	SetShellVarContext all
	
	CreateDirectory "$SMPROGRAMS\${GRASS_BASE}"
	
	CreateShortCut "$SMPROGRAMS\${GRASS_BASE}\GRASS ${VERSION_NUMBER}.lnk" "$INSTALL_DIR\${GRASS_COMMAND}.bat" "-wxpython"\
	"$INSTALL_DIR\icons\GRASS.ico" "" SW_SHOWMINIMIZED "" "Launch GRASS ${VERSION_NUMBER} with the new wxPython GUI"

	CreateShortCut "$SMPROGRAMS\${GRASS_BASE}\GRASS Command Line.lnk" "$INSTALL_DIR\${GRASS_COMMAND}.bat" "-text"\
	"$INSTALL_DIR\icons\GRASS_CMD.ico" "" SW_SHOWNORMAL "" "Launch GRASS in text mode"
	
	CreateShortCut "$SMPROGRAMS\${GRASS_BASE}\MSYS UNIX Console.lnk" "$INSTALL_DIR\msys\msys.bat" ""\
	"$INSTALL_DIR\icons\MSYS_Custom_Icon.ico" "" SW_SHOWNORMAL "" "Open a MSYS UNIX console"
	
	CreateShortCut "$SMPROGRAMS\${GRASS_BASE}\GRASS Web Site.lnk" "$INSTALL_DIR\GRASS-WebSite.url" ""\
	"$INSTALL_DIR\icons\GRASS_Web.ico" "" SW_SHOWNORMAL "" "Visit the GRASS website"
	
	;CreateShortCut "$SMPROGRAMS\${GRASS_BASE}\GRASS ${VERSION_NUMBER} with MSYS.lnk" "$INSTALL_DIR\msys\msys.bat" "/grass/bin/${GRASS_COMMAND} -wxpython"\
	;"$INSTALL_DIR\icons\GRASS_MSys.ico" "" SW_SHOWNORMAL "" "Launch GRASS ${VERSION_NUMBER} with the new wxPython GUI and a MSYS UNIX terminal"

	CreateShortCut "$SMPROGRAMS\${GRASS_BASE}\GRASS ${VERSION_NUMBER} with MSYS.lnk" "$INSTALL_DIR\set_shell_start_${GRASS_COMMAND}.bat" ""\
	"$INSTALL_DIR\icons\GRASS_MSys.ico" "" SW_SHOWNORMAL "" "Launch GRASS ${VERSION_NUMBER} with the new wxPython GUI and a MSYS-Windows-commandline"
	
; FIXME: ship the WinGrass release notes .html file instead of URL
; http://trac.osgeo.org/grass/browser/grass-web/trunk/grass70/binary/mswindows/native/README.html?format=raw
; probably ship with devel versions too? ie Release Notes, not the Release Announcement press release.
	!if ${INSTALLER_TYPE} == "Release"
		CreateShortCut "$SMPROGRAMS\${GRASS_BASE}\Release Notes.lnk" "$INSTALL_DIR\WinGRASS-README.url" ""\
		"$INSTALL_DIR\icons\WinGRASS.ico" "" SW_SHOWNORMAL "" "Visit the WinGRASS Project Web Page"
	!endif
	
	CreateShortCut "$SMPROGRAMS\${GRASS_BASE}\Uninstall GRASS.lnk" "$INSTALL_DIR\Uninstall-GRASS.exe" ""\
	"$INSTALL_DIR\Uninstall-GRASS.exe" "" SW_SHOWNORMAL "" "Uninstall GRASS ${VERSION_NUMBER}"
	
	;Create the grass_command.bat
	ClearErrors
	FileOpen $0 $INSTALL_DIR\${GRASS_COMMAND}.bat w
	IfErrors done_create_grass_command.bat
	FileWrite $0 '@echo off$\r$\n'
	FileWrite $0 'rem #########################################################################$\r$\n'
	FileWrite $0 'rem #$\r$\n'
	FileWrite $0 'rem # File dynamically created by NSIS installer script;$\r$\n'
	FileWrite $0 'rem #$\r$\n'
	FileWrite $0 'rem #########################################################################$\r$\n'
	FileWrite $0 'rem #$\r$\n'
	FileWrite $0 'rem # GRASS Initialization$\r$\n'
	FileWrite $0 'rem #$\r$\n'
	FileWrite $0 'rem #########################################################################$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 'rem Set GRASS Installation Directory Variable$\r$\n'
	FileWrite $0 'set GISBASE=$INSTALL_DIR$\r$\n'
	FileWrite $0 '$\r$\n'
	${If} $R_HKLM_INSTALL_PATH != ""
	FileWrite $0 'set PATH=$R_HKLM_INSTALL_PATH\bin;%PATH%$\r$\n'
	FileWrite $0 '$\r$\n'
	${EndIf}
	${If} $R_HKCU_INSTALL_PATH != ""
	FileWrite $0 'set PATH=$R_HKCU_INSTALL_PATH\bin;%PATH%$\r$\n'
	FileWrite $0 '$\r$\n'
	${EndIf}
	FileWrite $0 'call "%GISBASE%\etc\env.bat"$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 'cd "%USERPROFILE%"'
	FileWrite $0 '$\r$\n'
	FileWrite $0 '%GRASS_PYTHON% "%WINGISBASE%\grass70.py" %*'
	FileClose $0
	done_create_grass_command.bat:
	
	;Create the set_shell_start_grass.bat to start Grass7-wxgui and a msys-windows-commandline
	ClearErrors
	FileOpen $0 $INSTALL_DIR\set_shell_start_${GRASS_COMMAND}.bat w
	IfErrors done_create_set_shell_start_grass.bat
	FileWrite $0 '@echo off$\r$\n'
	FileWrite $0 'rem #########################################################################$\r$\n'
	FileWrite $0 'rem #$\r$\n'
	FileWrite $0 'rem # File dynamically created by NSIS installer script$\r$\n'
	FileWrite $0 'rem #$\r$\n'
	FileWrite $0 'rem #########################################################################$\r$\n'
	FileWrite $0 'rem #$\r$\n'
	FileWrite $0 'rem # Set SHELL and start GRASS$\r$\n'
	FileWrite $0 'rem #$\r$\n'
	FileWrite $0 'rem #########################################################################$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 'rem *******Set SHELL***********$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 'set SHELL="$INSTALL_DIR\msys\bin\sh.exe"$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 '"$INSTALL_DIR\${GRASS_COMMAND}.bat"$\r$\n'
	FileClose $0
 	done_create_set_shell_start_grass.bat:
			
	;Set the UNIX_LIKE GRASS Path
	Var /GLOBAL UNIX_LIKE_DRIVE
	Var /GLOBAL UNIX_LIKE_GRASS_PATH
  
	StrCpy $UNIX_LIKE_DRIVE "$INSTALL_DIR" 3
	StrCpy $UNIX_LIKE_GRASS_PATH "$INSTALL_DIR" "" 3
  
	;replace "\" with "/" in $UNIX_LIKE_DRIVE
	${StrReplace} "$UNIX_LIKE_DRIVE" "\" "/" "$UNIX_LIKE_DRIVE"
  
	;replace ":" with "" in $UNIX_LIKE_DRIVE
	${StrReplace} "$UNIX_LIKE_DRIVE" ":" "" "$UNIX_LIKE_DRIVE"
	
	;replace "\" with "/" in $UNIX_LIKE_GRASS_PATH
	${StrReplace} "$UNIX_LIKE_GRASS_PATH" "\" "/" "$UNIX_LIKE_GRASS_PATH"

	;Set the USERNAME variable
	Var /GLOBAL USERNAME
	Var /GLOBAL PROFILE_DRIVE
	Var /GLOBAL PROFILE_ROOT

	;It first searches for the Key Regestry value "Logon User Name" in HKCU "Software\Microsoft\Windows\CurrentVersion\Explorer"
	ReadRegStr $USERNAME HKCU "Software\Microsoft\Windows\CurrentVersion\Explorer" "Logon User Name"

	;If the Key Registry value is empty, it uses a work around, retrieving the Username string from the System User Profile variable ($PROFILE)
	;It first read the $PROFILE variable, to scan the OS version:
	;If equal to "drive:\Users\UserName", the OS is Vista, and the $USERNAME variable set to $PROFILE -  "drive:\Users\"
	;If not, the OS is XP or previous, and the $USERNAME variable set to $PROFILE -  "drive:\Documents and Settings\"
	
	${If} $USERNAME == ""
		StrCpy $PROFILE_DRIVE "$PROFILE" 2
		StrCpy $PROFILE_ROOT "$PROFILE" 5 -3
		${If} $USERNAME = "Users"		
			${StrReplace} "$USERNAME" "$PROFILE_DRIVE\Users\" "" "$PROFILE"
		${Else}
			${StrReplace} "$USERNAME" "$PROFILE_DRIVE\Documents and Settings\" "" "$PROFILE"
		${EndIf}
	${EndIf}
		
	;create the $INSTALL_DIR\bin grass_command
	ClearErrors
	FileOpen $0 $INSTALL_DIR\${GRASS_COMMAND}.sh w
	IfErrors done_create_grass_command
	FileWrite $0 '#! /bin/sh$\r$\n'
	FileWrite $0 '#########################################################################$\r$\n'
	FileWrite $0 '#$\r$\n'
	FileWrite $0 '# File dynamically created by NSIS installer script;$\r$\n'
	FileWrite $0 '# Written by Marco Pasetti;$\r$\n'
	FileWrite $0 '#$\r$\n'
	FileWrite $0 '#########################################################################$\r$\n'
	FileWrite $0 '#$\r$\n'
	FileWrite $0 '# MODULE:   	GRASS Initialization$\r$\n'
	FileWrite $0 '# AUTHOR(S):	Justin Hickey - Thailand - jhickey@hpcc.nectec.or.th$\r$\n'
	FileWrite $0 '# PURPOSE:  	The source file for this shell script is in$\r$\n'
	FileWrite $0 '#   	    	lib/init/grass.src and is the grass startup script. It$\r$\n'
	FileWrite $0 '#   	    	requires a source file because the definition of GISBASE$\r$\n'
	FileWrite $0 '#   	    	is not known until compile time and is substituted from the$\r$\n'
	FileWrite $0 '#   	    	Makefile. Any command line options are passed to Init.sh.$\r$\n'
	FileWrite $0 '# COPYRIGHT:  	(C) 2000-2010 by the GRASS Development Team$\r$\n'
	FileWrite $0 '#$\r$\n'
	FileWrite $0 '#             	This program is free software under the GNU General Public$\r$\n'
	FileWrite $0 '#   	    	License (>=v2). Read the file COPYING that comes with GRASS$\r$\n'
	FileWrite $0 '#   	    	for details.$\r$\n'
	FileWrite $0 '#$\r$\n'
	FileWrite $0 '#########################################################################$\r$\n'
	FileWrite $0 '#$\r$\n'
	FileWrite $0 '# Modified by Marco Pasetti$\r$\n'
	FileWrite $0 '# added the export PATH instruction to let GRASS work from$\r$\n'
	FileWrite $0 '# the MSYS environment in the dynamic NSIS installation$\r$\n'
	FileWrite $0 '#$\r$\n'
	FileWrite $0 '#########################################################################$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 'trap "echo '
	FileWrite $0 "'User break!' ; "
	FileWrite $0 'exit" 2 3 9 15$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 '# Set the GISBASE variable$\r$\n'
	FileWrite $0 'GISBASE="/$UNIX_LIKE_DRIVE$UNIX_LIKE_GRASS_PATH"$\r$\n'
	FileWrite $0 'export GISBASE$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 '# Set the PATH variable$\r$\n'
	FileWrite $0 'PATH="$$GISBASE/extrabin:$$GISBASE/extralib:$$PATH"$\r$\n'
	FileWrite $0 'PATH="$$GISBASE/tcl-tk/bin:$$GISBASE/sqlite/bin:$$GISBASE/gpsbabel:$$PATH"$\r$\n'
	FileWrite $0 'export PATH$\r$\n'
	FileWrite $0 '# Set the PYTHONPATH variable$\r$\n'
	FileWrite $0 'PYTHONPATH="$$GISBASE/etc/python:$$GISBASE/Python25:$$PYTHONPATH"$\r$\n'
	FileWrite $0 'export PYTHONPATH$\r$\n'
	FileWrite $0 'PYTHONHOME="$INSTALL_DIR\Python25"$\r$\n'
	FileWrite $0 'export PYTHONHOME$\r$\n'
	FileWrite $0 'if [ -z "$$GRASS_PYTHON" ] ; then$\r$\n'
	FileWrite $0 '   GRASS_PYTHON=python$\r$\n'
	FileWrite $0 '   export GRASS_PYTHON$\r$\n'
	FileWrite $0 'fi$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 '# Set the GRASS_PROJSHARE variable$\r$\n'
	FileWrite $0 'GRASS_PROJSHARE="$INSTALL_DIR\proj"$\r$\n'
	FileWrite $0 'export GRASS_PROJSHARE$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 '# Set the GDAL_DATA variable$\r$\n'
	FileWrite $0 'GDAL_DATA="$INSTALL_DIR\share\gdal"$\r$\n'
	FileWrite $0 'export GDAL_DATA$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 '# Set the PROJ_LIB variable$\r$\n'
	FileWrite $0 'PROJ_LIB="$INSTALL_DIR\proj"$\r$\n'
	FileWrite $0 'export PROJ_LIB $\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 '# Set the GEOTIFF_CSV variable$\r$\n'
	FileWrite $0 'GEOTIFF_CSV="$INSTALL_DIR\share\epsg_csv"$\r$\n'
	FileWrite $0 'export GEOTIFF_CSV$\r$\n'
	FileWrite $0 '$\r$\n'
	FileWrite $0 '"$$GRASS_PYTHON $$GISBASE/grass70.py" "$$@"'
	FileClose $0
	done_create_grass_command:
		
	;Get the short form of the install path (to allow for paths with spaces)
	VAR /GLOBAL INST_DIR_SHORT
	GetFullPathName /SHORT $INST_DIR_SHORT $INSTALL_DIR

	;create the $INSTALL_DIR\msys\etc\fstab with the main grass dir mount info
	ClearErrors
	FileOpen $0 $INSTALL_DIR\msys\etc\fstab w
	IfErrors done_create_fstab
	FileWrite $0 '$INST_DIR_SHORT   /grass$\r$\n'
	FileClose $0
	done_create_fstab:
	
	;Set the Unix-Like GIS_DATABASE Path
	Var /GLOBAL UNIX_LIKE_GIS_DATABASE_PATH
  
	;replace \ with / in $GIS_DATABASE
	${StrReplace} "$UNIX_LIKE_GIS_DATABASE_PATH" "\" "/" "$GIS_DATABASE"
  
	;create $APPDATA\grass7\rc
	SetShellVarContext current
	ClearErrors
	CreateDirectory	$APPDATA\grass7
	FileOpen $0 $APPDATA\grass7\rc w
	IfErrors done_create_grass7\rc
	FileWrite $0 'GISDBASE: $UNIX_LIKE_GIS_DATABASE_PATH$\r$\n'
	FileWrite $0 'LOCATION_NAME: demolocation$\r$\n'
	FileWrite $0 'MAPSET: PERMANENT$\r$\n'
	FileClose $0	
	done_create_grass7\rc:
	
	;replace gisbase = "/c/OSGeo4W/apps/grass/grass-7.0.svn" in grass70.py with $INSTDIR
	Push "$INSTDIR\grass70.py" ; file to modify
	Push 'gisbase = "/c/OSGeo4W/apps/grass/grass-7.0.svn"' ; string that a line must begin with *WS Sensitive*
	Push 'gisbase = "$INSTDIR"' ; string to replace whole line with
	Call ReplaceLineStr
	
	;replace config_projshare = "/c/OSGeo4W/share/proj" i n grass70.py with $INSTDIR\proj
	Push "$INSTDIR\grass70.py" ; file to modify
	Push 'config_projshare = "/c/OSGeo4W/share/proj"' ; string that a line must begin with *WS Sensitive*
	Push 'config_projshare = "$INSTDIR\proj"' ; string to replace whole line with
	Call ReplaceLineStr
                 
SectionEnd

Function DownloadDataSet

	IntOp $ARCHIVE_SIZE_MB $ARCHIVE_SIZE_KB / 1024
	
	StrCpy $DOWNLOAD_MESSAGE_ "The installer will download the $EXTENDED_ARCHIVE_NAME sample data set.$\r$\n"
	StrCpy $DOWNLOAD_MESSAGE_ "$DOWNLOAD_MESSAGE_$\r$\n"
	StrCpy $DOWNLOAD_MESSAGE_ "$DOWNLOAD_MESSAGE_The archive is about $ARCHIVE_SIZE_MB MB and may take"
	StrCpy $DOWNLOAD_MESSAGE_ "$DOWNLOAD_MESSAGE_ several minutes to be downloaded.$\r$\n"
	StrCpy $DOWNLOAD_MESSAGE_ "$DOWNLOAD_MESSAGE_$\r$\n"
	StrCpy $DOWNLOAD_MESSAGE_ "$DOWNLOAD_MESSAGE_The $EXTENDED_ARCHIVE_NAME will be copyed to:$\r$\n"
	StrCpy $DOWNLOAD_MESSAGE_ "$DOWNLOAD_MESSAGE_$GIS_DATABASE\$CUSTOM_UNTAR_FOLDER.$\r$\n"
	StrCpy $DOWNLOAD_MESSAGE_ "$DOWNLOAD_MESSAGE_$\r$\n"
	StrCpy $DOWNLOAD_MESSAGE_ "$DOWNLOAD_MESSAGE_Press OK to continue or Cancel to skip the download and complete the GRASS"
	StrCpy $DOWNLOAD_MESSAGE_ "$DOWNLOAD_MESSAGE_ installation without the $EXTENDED_ARCHIVE_NAME data set.$\r$\n"
	
	MessageBox MB_OKCANCEL "$DOWNLOAD_MESSAGE_" IDOK download IDCANCEL cancel_download
	
	download:	
	SetShellVarContext current	
	InitPluginsDir
	NSISdl::download "$HTTP_PATH/$ARCHIVE_NAME" "$TEMP\$ARCHIVE_NAME"
	Pop $0
	StrCmp $0 "success" download_ok download_failed
		
	download_ok:	
	InitPluginsDir
	untgz::extract "-d" "$GIS_DATABASE" "$TEMP\$ARCHIVE_NAME"
	Pop $0
	StrCmp $0 "success" untar_ok untar_failed
	
	untar_ok:       
	Rename "$GIS_DATABASE\$ORIGINAL_UNTAR_FOLDER" "$GIS_DATABASE\$CUSTOM_UNTAR_FOLDER"
	Delete "$TEMP\$ARCHIVE_NAME"
	Goto end
	
	download_failed:
	DetailPrint "$0" ;print error message to log
	MessageBox MB_OK "Download Failed.$\r$\nGRASS will be installed without the $EXTENDED_ARCHIVE_NAME sample data set."
	Goto end
	
	cancel_download:
	MessageBox MB_OK "Download Cancelled.$\r$\nGRASS will be installed without the $EXTENDED_ARCHIVE_NAME sample data set."
	Goto end
	
	untar_failed:
	DetailPrint "$0" ;print error message to log
	
	end:

FunctionEnd

Section /O "North Carolina (Wake County) Data Set" SecNorthCarolinaSDB

	;Set the size (in KB)  of the archive file
	StrCpy $ARCHIVE_SIZE_KB 138629
	
	;Set the size (in KB) of the unpacked archive file
	AddSize 293314
  
  	StrCpy $HTTP_PATH "http://grass.osgeo.org/sampledata"
	StrCpy $ARCHIVE_NAME "nc_spm_latest.tar.gz"
	StrCpy $EXTENDED_ARCHIVE_NAME "North Carolina (Wake County)"
	StrCpy $ORIGINAL_UNTAR_FOLDER "nc_spm_08"
	StrCpy $CUSTOM_UNTAR_FOLDER "North-Carolina"
	
	Call DownloadDataSet	
	
SectionEnd

Section /O "South Dakota (Spearfish County) Data Set" SecSpearfishSDB

	;Set the size (in KB)  of the archive file
	StrCpy $ARCHIVE_SIZE_KB 20803
	
	;Set the size (in KB) of the unpacked archive file
	AddSize 42171
	
	StrCpy $HTTP_PATH "http://grass.osgeo.org/sampledata"
	StrCpy $ARCHIVE_NAME "spearfish_grass60data-0.3.tar.gz"
	StrCpy $EXTENDED_ARCHIVE_NAME "South Dakota (Spearfish County)"
	StrCpy $ORIGINAL_UNTAR_FOLDER "spearfish60"
	StrCpy $CUSTOM_UNTAR_FOLDER "Spearfish60"
	
	Call DownloadDataSet

SectionEnd

;----------------------------------------------------------------------------------------------------------------------------

;Uninstaller Section

Section "Uninstall"
	;remove files & folders
	RMDir /r "$INSTDIR"
		        	
	;remove the Desktop ShortCut
	SetShellVarContext current
	Delete "$DESKTOP\GRASS ${VERSION_NUMBER}.lnk"
	Delete "$DESKTOP\GRASS ${VERSION_NUMBER} with MSYS.lnk"
	
	;remove the Programs Start ShortCuts
	SetShellVarContext all
	RMDir /r "$SMPROGRAMS\${GRASS_BASE}"
	
	;remove the $APPDATA\grass7 folder
	SetShellVarContext current
	RMDir /r "$APPDATA\grass7"

	;remove the Registry Entries
	DeleteRegKey HKLM "Software\${GRASS_BASE}"
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${GRASS_BASE}"

SectionEnd

;----------------------------------------------------------------------------------------------------------------------------

;Installer Section Descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${SecGRASS} "Install GRASS ${VERSION_NUMBER}"
	!insertmacro MUI_DESCRIPTION_TEXT ${SecNorthCarolinaSDB} "Download and install the North Carolina (Wake County) sample data set"
	!insertmacro MUI_DESCRIPTION_TEXT ${SecSpearfishSDB} "Download and install the South Dakota (Spearfish County) sample data set"
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;----------------------------------------------------------------------------------------------------------------------------
