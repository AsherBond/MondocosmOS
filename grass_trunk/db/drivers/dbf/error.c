#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>
#include <grass/dbmi.h>
#include <grass/gis.h>
#include "globals.h"

/* init error message */
void init_error(void)
{
    if (!errMsg) {
	errMsg = (dbString *) G_malloc(sizeof(dbString));
	db_init_string(errMsg);
    }

    db_set_string(errMsg, "DBMI-DBF driver error:\n");
}

/* append error message */
void append_error(const char *fmt, ...)
{
    FILE *fp = NULL;
    char *work = NULL;
    int count = 0;
    va_list ap;

    va_start(ap, fmt);
    if ((fp = tmpfile())) {
	count = vfprintf(fp, fmt, ap);
	if (count >= 0 && (work = G_calloc(count + 1, 1))) {
	    rewind(fp);
	    fread(work, 1, count, fp);
	    db_append_string(errMsg, work);
	    G_free(work);
	}
	fclose(fp);
    }
    va_end(ap);
}

void report_error(void)
{
    db_append_string(errMsg, "\n");
    db_error(db_get_string(errMsg));
}
