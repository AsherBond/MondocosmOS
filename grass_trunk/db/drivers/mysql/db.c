
/**********************************************************
 * MODULE:    mysql
 * AUTHOR(S): Radim Blazek (radim.blazek@gmail.com)
 * PURPOSE:   MySQL database driver
 * COPYRIGHT: (C) 2001 by the GRASS Development Team
 *            This program is free software under the 
 *            GNU General Public License (>=v2). 
 *            Read the file COPYING that comes with GRASS
 *            for details.
 **********************************************************/
#include <stdlib.h>
#include <string.h>

#include <grass/dbmi.h>
#include <grass/gis.h>
#include <grass/glocale.h>

#include "globals.h"
#include "proto.h"

int db__driver_open_database(dbHandle * handle)
{
    const char *name;
    dbConnection default_connection;
    MYSQL *res;

    init_error();
    db_get_connection(&default_connection);
    name = db_get_handle_dbname(handle);

    /* if name is empty use default_connection.databaseName */
    if (strlen(name) == 0)
	name = default_connection.databaseName;

    G_debug(3, "db_driver_open_database() mysql: database definition = '%s'",
	    name);

    {
	/* Client version */
	const char *user, *password;
	CONNPAR connpar;

	if (parse_conn(name, &connpar) == DB_FAILED) {
	    report_error();
	    return DB_FAILED;
	}

	G_debug(3, "host = %s, port = %d, dbname = %s, "
		"user = %s, password = %s",
		connpar.host, connpar.port, connpar.dbname,
		connpar.user, connpar.password);

	db_get_login("mysql", name, &user, &password);

	connection = mysql_init(NULL);
	res = mysql_real_connect(connection, connpar.host, user, password,
				 connpar.dbname, connpar.port, NULL, 0);

	if (res == NULL) {
	    append_error(_("Cannot connect to MySQL: "));
	    append_error(mysql_error(connection));
	    report_error();
	    return DB_FAILED;
	}
    }

    return DB_OK;
}

int db__driver_close_database(void)
{
    init_error();
    mysql_close(connection);	/* this will also release connection */

    return DB_OK;
}

