#include <grass/gis.h>
#include <grass/raster.h>
#include <grass/segment.h>
#include "cseg.h"

static char *me = "cseg_read_cell";

int cseg_read_cell(CSEG * cseg, char *map_name, char *mapset)
{
    int row, nrows;
    int map_fd;
    CELL *buffer;

    cseg->name = NULL;
    cseg->mapset = NULL;

    map_fd = Rast_open_old(map_name, mapset);
    nrows = Rast_window_rows();
    buffer = Rast_allocate_c_buf();
    for (row = 0; row < nrows; row++) {
	Rast_get_c_row(map_fd, buffer, row);
	if (segment_put_row(&(cseg->seg), buffer, row) < 0) {
	    G_free(buffer);
	    Rast_close(map_fd);
	    G_warning("%s(): unable to segment put row for [%s] in [%s]",
		    me, map_name, mapset);
	    return (-1);
	}
    }

    Rast_close(map_fd);
    G_free(buffer);

    cseg->name = G_store(map_name);
    cseg->mapset = G_store(mapset);

    return 0;
}
