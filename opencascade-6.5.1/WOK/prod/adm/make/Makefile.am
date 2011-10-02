srcdir = @srcdir@
WOK_PKGS = TKWOK TKWOKTcl TKTCPPExt TKCDLFront TKCPPExt TKCPPClient TKIDLFront TKCPPJini TKCPPIntExt TKCSFDBSchema WOKSH WOKLibs 

WOK_DIRS = $(WOK_PKGS)

SUBDIRS = $(WOK_DIRS) 


VPATH = @srcdir@ @top_srcdir@/adm/make: \
@top_srcdir@/adm/make/TKWOK: @top_srcdir@/adm/make/TKWOKTcl: @top_srcdir@/adm/make/TKTCPPExt: @top_srcdir@/adm/make/TKCDLFront: @top_srcdir@/adm/make/TKCPPExt: @top_srcdir@/adm/make/TKCPPClient: @top_srcdir@/adm/make/TKIDLFront: @top_srcdir@/adm/make/TKCPPJini: @top_srcdir@/adm/make/TKCPPIntExt: @top_srcdir@/adm/make/TKCSFDBSchema: @top_srcdir@/adm/make/WOKSH: @top_srcdir@/adm/make/WOKLibs: 

WOK:
	for d in $(WOK_PKGS); do \
		cd $$d; $(MAKE) $(AM_MAKEFLAGS) lib$$d.la; cd ..; \
	done


.PHONY: WOK
