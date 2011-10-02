
/**
 * \file locale.c
 *
 * \brief GIS Library - Functions to handle locale.
 *
 * (C) 2001-2008 by the GRASS Development Team
 *
 * This program is free software under the GNU General Public License
 * (>=v2). Read the file COPYING that comes with GRASS for details.
 *
 * \author GRASS GIS Development Team
 *
 * \date 2004-2008
 */

#include <grass/config.h>
#include <stdlib.h>
#include <string.h>
#include <locale.h>
#include <grass/glocale.h>
#include <grass/gis.h>

void G_init_locale(void)
{
    static int initialized;
    const char *gisbase;

    if (G_is_initialized(&initialized))
	return;

    setlocale(LC_CTYPE, "");

#if defined(HAVE_LIBINTL_H) && defined(USE_NLS)
#ifdef LC_MESSAGES
    setlocale(LC_MESSAGES, "");
#endif

    gisbase = getenv("GISBASE");
    if (gisbase && *gisbase) {
	char localedir[GPATH_MAX];

	strcpy(localedir, gisbase);
	strcat(localedir, "/locale");

	bindtextdomain("grasslibs", localedir);
	bindtextdomain("grassmods", localedir);
    }
#endif

    G_initialize_done(&initialized);
}


/**
 * \brief Gets localized text.
 *
 * \param[in] package
 * \param[in] msgid
 * \retval char * Pointer to string
 */

char *G_gettext(const char *package, const char *msgid)
{
#if defined(HAVE_LIBINTL_H) && defined(USE_NLS)
    G_init_locale();

    return dgettext(package, msgid);
#else
    return (char *)msgid;
#endif
}
