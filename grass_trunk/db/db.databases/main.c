
/****************************************************************************
 *
 * MODULE:       db.databases
 * AUTHOR(S):    Radim Blazek <radim.blazek gmail.com> (original contributor)
 *               Glynn Clements <glynn gclements.plus.com>, Markus Neteler <neteler itc.it>
 * PURPOSE:      lists all databases for a given driver
 * COPYRIGHT:    (C) 2002-2006 by the GRASS Development Team
 *
 *               This program is free software under the GNU General Public
 *               License (>=v2). Read the file COPYING that comes with GRASS
 *               for details.
 *
 *****************************************************************************/

#include <stdlib.h>
#include <grass/dbmi.h>
#include <grass/gis.h>
#include <grass/codes.h>
#include <grass/glocale.h>


struct
{
    char *driver;
    char *location;
} parms;


/* function prototypes */
static void parse_command_line(int, char **);


int main(int argc, char **argv)
{
    dbDriver *driver;
    dbHandle *handles;
    dbString locations;
    int nlocs = 0;
    int count, i;

    db_init_string(&locations);
    parse_command_line(argc, argv);

    if (parms.location) {
	db_set_string(&locations, parms.location);
	nlocs = 1;
    }

    driver = db_start_driver(parms.driver);
    if (driver == NULL)
	G_fatal_error(_("Unable to start driver <%s>"), parms.driver);

    if (db_list_databases(driver, &locations, nlocs, &handles, &count) !=
	DB_OK)
	G_fatal_error(_("Unable to list databases"));

    db_shutdown_driver(driver);

    for (i = 0; i < count; i++) {
	fprintf(stdout, "%s", db_get_handle_dbname(&handles[i]));
	fprintf(stdout, "\n");
    }

    exit(EXIT_SUCCESS);
}


static void parse_command_line(int argc, char **argv)
{
    struct Option *driver, *location;
    struct GModule *module;

    /* Initialize the GIS calls */
    G_gisinit(argv[0]);

    driver = G_define_standard_option(G_OPT_DB_DRIVER);
    driver->options = db_list_drivers();

    location = G_define_option();
    location->key = "location";
    location->type = TYPE_STRING;
    location->required = NO;
    location->multiple = YES;
    location->description = _("Location name");


    /* Set description */
    module = G_define_module();
    G_add_keyword(_("database"));
    G_add_keyword(_("SQL"));
    module->description =
	_("List all databases for a given driver and location.");

    if (G_parser(argc, argv))
	exit(EXIT_FAILURE);

    parms.driver = driver->answer;
    parms.location = location->answer;
}
