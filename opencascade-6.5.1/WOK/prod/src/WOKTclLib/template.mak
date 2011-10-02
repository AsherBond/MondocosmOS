!IF "$(CFG)" == ""
CFG=__TKNAM__ - Win32 Debug
!MESSAGE No configuration specified. Defaulting to __TKNAM__ - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "__TKNAM__ - Win32 Release" && "$(CFG)" != "__TKNAM__ - Win32 Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "__TKNAM__.mak" CFG="__TKNAM__ - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "__TKNAM__ - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "__TKNAM__ - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 
__TCLUSED__
__JAVAUSED__

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 

!IF  "$(CFG)" == "__TKNAM__ - Win32 Release"

OUTDIR=.\..\..\win32\bin
INTDIR=.\..\..\win32\obj\__TKNAM__
LIBDIR=.\..\..\win32\lib
# Begin Custom Macros
OutDir=.\..\..\win32\bin
# End Custom Macros

__FIELD1__	-@erase "$(INTDIR)\vc*.idb"
        -@erase "$(INTDIR)\vc*.pdb"
	-@erase "$(OUTDIR)\__TKNAM__.dll"
	-@erase "..\..\win32\lib\__TKNAM__.exp"
	-@erase "..\..\win32\lib\__TKNAM__.lib"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"
    if not exist "$(LIBDIR)/$(NULL)" mkdir "$(LIBDIR)"

"$(INTDIR)" :
    if not exist "$(INTDIR)/$(NULL)" mkdir "$(INTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MD /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "WNT" /D "No_Exception" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /D "CSFDB" /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "NDEBUG" /mktyplib203 /o "NUL" /win32 
RSC=rc.exe
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\__TKNAM__.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
__FIELD2__

"$(OUTDIR)\__TKNAM__.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ELSEIF  "$(CFG)" == "__TKNAM__ - Win32 Debug"

OUTDIR=.\..\..\win32\bind
INTDIR=.\..\..\win32\objd\__TKNAM__
LIBDIR=.\..\..\win32\libd
# Begin Custom Macros
OutDir=.\..\..\win32\bind
# End Custom Macros


__FIELD3__	-@erase "$(INTDIR)\vc*.idb"
        -@erase "$(INTDIR)\vc*.pdb"
	-@erase "$(OUTDIR)\__TKNAM__.dll"
	-@erase "..\..\win32\libd\__TKNAM__.exp"
	-@erase "..\..\win32\libd\__TKNAM__.lib"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"
    if not exist "$(LIBDIR)/$(NULL)" mkdir "$(LIBDIR)"

"$(INTDIR)" :
    if not exist "$(INTDIR)/$(NULL)" mkdir "$(INTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MDd /W3 /GX /Zi /Od /D "WIN32" /D "DEB" /D "_DEBUG" /D "_WINDOWS" /D "WNT" /D "CSFDB" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "_DEBUG" /mktyplib203 /o "NUL" /win32 
RSC=rc.exe
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\__TKNAM__.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
__FIELD4__

"$(OUTDIR)\__TKNAM__.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ENDIF 

!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("__TKNAM__.dep")
!INCLUDE "__TKNAM__.dep"
!ELSE 
!MESSAGE Warning: cannot find "__TKNAM__.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "__TKNAM__ - Win32 Release" || "$(CFG)" == "__TKNAM__ - Win32 Debug"

__FIELD5__

__FIELD6__

!ENDIF 

