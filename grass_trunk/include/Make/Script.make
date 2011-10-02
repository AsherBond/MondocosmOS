
include $(MODULE_TOPDIR)/include/Make/Vars.make

ifdef MINGW
SCRIPT = $(SCRIPTDIR)/$(PGM).py
else
SCRIPT = $(SCRIPTDIR)/$(PGM)
endif

HTMLSRC = $(SCRIPT)

include $(MODULE_TOPDIR)/include/Make/Rules.make
include $(MODULE_TOPDIR)/include/Make/Html.make
include $(MODULE_TOPDIR)/include/Make/ScriptRules.make

SCRIPT_ACTIONS = $(SCRIPT) html scriptstrings

script: $(SCRIPT_ACTIONS)

scriptstrings: $(STRINGDIR)/$(PGM)_to_translate.c

install:
	$(INSTALL) $(SCRIPT) $(INST_DIR)/scripts/
	$(INSTALL_DATA) $(HTMLDIR)/$(PGM).html $(INST_DIR)/docs/html/
	$(INSTALL_DATA) $(ARCH_DISTDIR)/man/man1/$(PGM).1 $(INST_DIR)/man/man1/

.PHONY: script scriptstrings
