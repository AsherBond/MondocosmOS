srcdir = @srcdir@

SUBDIRS = adm/make

VPATH = @srcdir@ \
@top_srcdir@/make: \
@top_srcdir@/adm/make:

WOK_PKGS = WOKBuilderDef WOKEntityDef WOKStepsDef WOKTclLib WOKsite 
WOK_DIRS = $(WOK_PKGS)


WOK:
	cd $(top_builddir)/adm/make && $(MAKE) $(AM_MAKEFLAGS) WOK

CP_Linux=L
CP_SunOS=

dir_Linux=lin
dir_SunOS=sun

install-exec-local:
	$(INSTALL) -d $(prefix)/$(platform)
	if [ -e $(prefix)/bin -a ! -e $(prefix)/$(platform)/bin ]; then \
		cd $(prefix)/$(platform) && ln -s ../bin bin; \
	fi
	if [ -e $(prefix)/lib -a ! -e $(prefix)/$(platform)/lib ]; then \
		cd $(prefix)/$(platform) && ln -s ../lib lib; \
	fi
	$(INSTALL) -d $(prefix)/$(dir_$(platform))
	if [ -e $(prefix)/bin -a ! -e $(prefix)/$(dir_$(platform))/bin ]; then \
		cd $(prefix)/$(dir_$(platform)) && ln -s ../bin bin; \
	fi
	if [ -e $(prefix)/lib -a ! -e $(prefix)/$(dir_$(platform))/lib ]; then \
		cd $(prefix)/$(dir_$(platform)) && ln -s ../lib lib; \
	fi
	bd=`pwd`; cd $(top_srcdir); sd=`pwd`; cd $(prefix); pd=`pwd`; cd $$bd; \
	if [ "$$pd" != "$$sd" ]; then \
		$(INSTALL) -d $(prefix)/inc; \
		cp -fr$(CP_$(platform)) $(top_srcdir)/inc $(prefix); \
	fi; \
	if [ "$$pd" != "$$bd" ]; then \
		cp -fr$(CP_$(platform)) $$bd/config.h $(prefix); \
	fi
	if [ -e $(prefix)/inc/config.h ]; then \
		unlink $(prefix)/inc/config.h; \
	fi
	cd $(prefix)/inc && ln -s ../config.h config.h 
	bd=`pwd`; cd $(top_srcdir); sd=`pwd`; cd $(prefix); pd=`pwd`; cd $$bd; \
	bd=`pwd`; cd $(top_srcdir); sd=`pwd`; cd $(prefix); pd=`pwd`; cd $$bd; \
	if [ "$$pd" != "$$sd" ]; then \
		inst_dirs="$(WOK_DIRS)"; \
		if test "x$(WOK_DIRS)" != "x"; then \
			for d in $$inst_dirs; do \
				$(INSTALL) -d $(prefix)/src/$$d; \
				cp -fr$(CP_$(platform)) $(top_srcdir)/src/$$d $(prefix)/src; \
			done; \
		fi; \
	fi

.PHONY: WOK
