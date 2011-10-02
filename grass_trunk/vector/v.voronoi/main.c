
/****************************************************************************
 *
 * MODULE:       v.voronoi
 * AUTHOR(S):    James McCauley <mccauley ecn.purdue.edu>, s.voronoi, based
 *                     on netlib code (see README) (original contributor)
 *               Andrea Aime <aaime libero.it>
 *               Radim Blazek <radim.blazek gmail.com> (GRASS 5.1 v.voronoi) 
 *               Glynn Clements <glynn gclements.plus.com>,  
 *               Markus Neteler <neteler itc.it>
 * PURPOSE:      produce a Voronoi diagram using vector points
 * COPYRIGHT:    (C) 1993-2006 by the GRASS Development Team
 *
 *               This program is free software under the GNU General Public
 *               License (>=v2). Read the file COPYING that comes with GRASS
 *               for details.
 *
 *****************************************************************************/

/*-s.voronoi
**
** Author: James Darrell McCauley (mccauley@ecn.purdue.edu)
**         USDA Fellow
**         Department of Agricultural Engineering
**         Purdue University
**         West Lafayette, Indiana 47907-1146 USA
**
** Permission to use, copy, modify, and distribute this software and its
** documentation for any purpose and without fee is hereby granted. This
** software is provided "as is" without express or implied warranty.
**
** Modification History:
** 06 Feb 93 - James Darrell McCauley <mccauley@ecn.purdue.edu> pieced
**             this together from stuff he found on netlib (see the manpage).
**/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <grass/gis.h>
#include <grass/dbmi.h>
#include <grass/vector.h>
#include <grass/glocale.h>
#include "sw_defs.h"
#include "defs.h"

typedef struct
{
    double x, y;
} COOR;

int cmp(void *a, void *b)
{
    COOR *ca = (COOR *) a;
    COOR *cb = (COOR *) b;
    double ma, mb;

    /* calculate measure */
    ma = mb = 0.0;

    if (fabs(ca->y - Box.S) < GRASS_EPSILON) {	/* bottom */
	ma = ca->x - Box.W;
    }
    else if (fabs(ca->x - Box.E) < GRASS_EPSILON) {	/* right */
	ma = (Box.E - Box.W) + (ca->y - Box.S);
    }
    else if (fabs(ca->y - Box.N) < GRASS_EPSILON) {	/* top */
	ma = (Box.E - Box.W) + (Box.N - Box.S) + (Box.E - ca->x);
    }
    else {			/* left */
	ma = 2 * (Box.E - Box.W) + (Box.N - Box.S) + (Box.N - ca->y);
    }


    if (fabs(cb->y - Box.S) < GRASS_EPSILON) {	/* bottom */
	mb = cb->x - Box.W;
    }
    else if (fabs(cb->x - Box.E) < GRASS_EPSILON) {	/* right */
	mb = (Box.E - Box.W) + (cb->y - Box.S);
    }
    else if (fabs(cb->y - Box.N) < GRASS_EPSILON) {	/* top */
	mb = (Box.E - Box.W) + (Box.N - Box.S) + (Box.E - cb->x);
    }
    else {			/* left */
	mb = 2 * (Box.E - Box.W) + (Box.N - Box.S) + (Box.N - cb->y);
    }

    if (ma < mb)
	return -1;
    if (ma > mb)
	return 1;
    return 0;
}

int main(int argc, char **argv)
{
    int i;
    int **cats, *ncats, nfields, *fields;
    struct Flag *line_flag;

    /* struct Flag *all_flag; */
    struct Option *in_opt, *out_opt;
    struct Flag *table_flag;
    struct GModule *module;
    struct line_pnts *Points;
    struct line_cats *Cats;
    int node, nnodes;
    COOR *coor;
    int ncoor, acoor;
    int line, nlines, type, ctype, area, nareas;
    int err_boundaries, err_centr_out, err_centr_dupl, err_nocentr;

    G_gisinit(argv[0]);

    module = G_define_module();
    G_add_keyword(_("vector"));
    G_add_keyword(_("geometry"));
    G_add_keyword(_("triangulation"));
    module->description = _("Creates a Voronoi diagram from an input vector "
			    "map containing points or centroids.");

    in_opt = G_define_standard_option(G_OPT_V_INPUT);
    out_opt = G_define_standard_option(G_OPT_V_OUTPUT);

    /*
       all_flag = G_define_flag ();
       all_flag->key = 'a';
       all_flag->description = _("Use all points (do not limit to current region)");
     */

    line_flag = G_define_flag();
    line_flag->key = 'l';
    line_flag->description =
	_("Output tessellation as a graph (lines), not areas");

    table_flag = G_define_flag();
    table_flag->key = 't';
    table_flag->description = _("Do not create attribute table");

    if (G_parser(argc, argv))
	exit(EXIT_FAILURE);


    if (line_flag->answer)
	Type = GV_LINE;
    else
	Type = GV_BOUNDARY;

    All = 0;

    Points = Vect_new_line_struct();
    Cats = Vect_new_cats_struct();

    /* open files */
    Vect_set_open_level(2);
    Vect_open_old(&In, in_opt->answer, "");

    if (Vect_open_new(&Out, out_opt->answer, 0) < 0)
	G_fatal_error(_("Unable to create vector map <%s>"), out_opt->answer);

    Vect_hist_copy(&In, &Out);
    Vect_hist_command(&Out);

    /* initialize working region */
    G_get_window(&Window);
    Vect_region_box(&Window, &Box);
    Box.T = 0.5;
    Box.B = -0.5;

    freeinit(&sfl, sizeof(struct Site));

    G_message(_("Reading sites..."));
    readsites();

    siteidx = 0;
    geominit();

    triangulate = 0;
    plot = 0;
    debug = 0;

    G_message(_("Voronoi triangulation..."));
    voronoi(triangulate, nextone);

    /* Close free ends by current region */
    Vect_build_partial(&Out, GV_BUILD_BASE);

    ncoor = 0;
    acoor = 100;
    coor = (COOR *) G_malloc(sizeof(COOR) * acoor);

    nnodes = Vect_get_num_nodes(&Out);
    for (node = 1; node <= nnodes; node++) {
	double x, y;

	if (Vect_get_node_n_lines(&Out, node) < 2) {	/* add coordinates */
	    Vect_get_node_coor(&Out, node, &x, &y, NULL);

	    if (ncoor == acoor - 5) {	/* always space for 5 region corners */
		acoor += 100;
		coor = (COOR *) G_realloc(coor, sizeof(COOR) * acoor);
	    }

	    coor[ncoor].x = x;
	    coor[ncoor].y = y;
	    ncoor++;
	}
    }

    /* Add region corners */
    coor[ncoor].x = Box.W;
    coor[ncoor].y = Box.S;
    ncoor++;
    coor[ncoor].x = Box.E;
    coor[ncoor].y = Box.S;
    ncoor++;
    coor[ncoor].x = Box.E;
    coor[ncoor].y = Box.N;
    ncoor++;
    coor[ncoor].x = Box.W;
    coor[ncoor].y = Box.N;
    ncoor++;

    /* Sort */
    qsort(coor, ncoor, sizeof(COOR), (void *)cmp);

    /* add last (first corner) */
    coor[ncoor].x = Box.W;
    coor[ncoor].y = Box.S;
    ncoor++;

    for (i = 1; i < ncoor; i++) {
	if (coor[i].x == coor[i - 1].x && coor[i].y == coor[i - 1].y)
	    continue;		/* duplicate */

	Vect_reset_line(Points);
	Vect_append_point(Points, coor[i].x, coor[i].y, 0.0);
	Vect_append_point(Points, coor[i - 1].x, coor[i - 1].y, 0.0);
	Vect_write_line(&Out, Type, Points, Cats);
    }

    G_free(coor);

    /* Copy input points as centroids */
    nfields = Vect_cidx_get_num_fields(&In);
    cats = (int **)G_malloc(nfields * sizeof(int *));
    ncats = (int *)G_malloc(nfields * sizeof(int));
    fields = (int *)G_malloc(nfields * sizeof(int));
    for (i = 0; i < nfields; i++) {
	ncats[i] = 0;
	cats[i] =
	    (int *)G_malloc(Vect_cidx_get_num_cats_by_index(&In, i) *
			    sizeof(int));
	fields[i] = Vect_cidx_get_field_number(&In, i);
    }

    if (line_flag->answer)
	ctype = GV_POINT;
    else
	ctype = GV_CENTROID;

    nlines = Vect_get_num_lines(&In);

    G_message(_("Writing sites to output..."));

    for (line = 1; line <= nlines; line++) {

	G_percent(line, nlines, 2);

	type = Vect_read_line(&In, Points, Cats, line);
	if (!(type & GV_POINTS))
	    continue;

	if (!Vect_point_in_box(Points->x[0], Points->y[0], 0.0, &Box))
	    continue;

	Vect_write_line(&Out, ctype, Points, Cats);


	for (i = 0; i < Cats->n_cats; i++) {
	    int f, j;

	    f = -1;
	    for (j = 0; j < nfields; j++) {	/* find field */
		if (fields[j] == Cats->field[i]) {
		    f = j;
		    break;
		}
	    }
	    if (f > -1) {
		cats[f][ncats[f]] = Cats->cat[i];
		ncats[f]++;
	    }
	}
    }

    /* Copy tables */
    if (!(table_flag->answer)) {
	int ttype, ntabs = 0;
	struct field_info *IFi, *OFi;

	/* Number of output tabs */
	for (i = 0; i < Vect_get_num_dblinks(&In); i++) {
	    int f, j;

	    IFi = Vect_get_dblink(&In, i);

	    f = -1;
	    for (j = 0; j < nfields; j++) {	/* find field */
		if (fields[j] == IFi->number) {
		    f = j;
		    break;
		}
	    }
	    if (f > -1) {
		if (ncats[f] > 0)
		    ntabs++;
	    }
	}

	if (ntabs > 1)
	    ttype = GV_MTABLE;
	else
	    ttype = GV_1TABLE;

	for (i = 0; i < nfields; i++) {
	    int ret;

	    if (fields[i] == 0)
		continue;

	    G_message(_("Layer %d"), fields[i]);

	    /* Make a list of categories */
	    IFi = Vect_get_field(&In, fields[i]);
	    if (!IFi) {		/* no table */
		G_message(_("No table"));
		continue;
	    }

	    OFi =
		Vect_default_field_info(&Out, IFi->number, IFi->name, ttype);

	    ret =
		db_copy_table_by_ints(IFi->driver, IFi->database, IFi->table,
				      OFi->driver,
				      Vect_subst_var(OFi->database, &Out),
				      OFi->table, IFi->key, cats[i],
				      ncats[i]);

	    if (ret == DB_FAILED) {
		G_warning(_("Cannot copy table"));
	    }
	    else {
		Vect_map_add_dblink(&Out, OFi->number, OFi->name, OFi->table,
				    IFi->key, OFi->database, OFi->driver);
	    }
	}
    }


    Vect_close(&In);

    /* cleaning part 1: count errors */
    Vect_build_partial(&Out, GV_BUILD_CENTROIDS);
    err_boundaries = err_centr_out = err_centr_dupl = err_nocentr = 0;
    nlines = Vect_get_num_lines(&Out);
    for (line = 1; line <= nlines; line++) {

	if (!Vect_line_alive(&Out, line))
	    continue;

	type = Vect_get_line_type(&Out, line);
	if (type == GV_BOUNDARY) {
	    int left, right;

	    Vect_get_line_areas(&Out, line, &left, &right);

	    if (left == 0 || right == 0) {
		G_debug(3, "line = %d left = %d right = %d", line, 
			left, right);
		err_boundaries++;
	    }
	}
	if (type == GV_CENTROID) {
	    area = Vect_get_centroid_area(&Out, line);
	    if (area == 0)
		err_centr_out++;
	    else if (area < 0)
		err_centr_dupl++;
	}
    }

    err_nocentr = 0;
    nareas = Vect_get_num_areas(&Out);
    for (area = 1; area <= nareas; area++) {
	if (!Vect_area_alive(&Out, area))
	    continue;
	line = Vect_get_area_centroid(&Out, area);
	if (line == 0)
	    err_nocentr++;
    }

    /* cleaning part 2: snap */
    if (err_nocentr || err_centr_dupl || err_centr_out) {
	int nmod;

	G_important_message(_("Output needs topological cleaning"));
	Vect_snap_lines(&Out, GV_BOUNDARY, 1e-7, NULL);
	do {
	    Vect_break_lines(&Out, GV_BOUNDARY, NULL);
	    Vect_remove_duplicates(&Out, GV_BOUNDARY, NULL);
	    nmod =
		Vect_clean_small_angles_at_nodes(&Out, GV_BOUNDARY, NULL);
	} while (nmod > 0);

	err_boundaries = 0;
	nlines = Vect_get_num_lines(&Out);
	for (line = 1; line <= nlines; line++) {

	    if (!Vect_line_alive(&Out, line))
		continue;

	    type = Vect_get_line_type(&Out, line);
	    if (type == GV_BOUNDARY) {
		int left, right;

		Vect_get_line_areas(&Out, line, &left, &right);

		if (left == 0 || right == 0) {
		    G_debug(3, "line = %d left = %d right = %d", line, 
			    left, right);
		    err_boundaries++;
		}
	    }
	}
    }
    /* cleaning part 3: remove remaining incorrect boundaries */
    if (err_boundaries) {
	G_important_message(_("Removing incorrect boundaries from output"));
	nlines = Vect_get_num_lines(&Out);
	for (line = 1; line <= nlines; line++) {

	    if (!Vect_line_alive(&Out, line))
		continue;

	    type = Vect_get_line_type(&Out, line);
	    if (type == GV_BOUNDARY) {
		int left, right;

		Vect_get_line_areas(&Out, line, &left, &right);

		/* &&, not ||, no typo */
		if (left == 0 && right == 0) {
		    G_debug(3, "line = %d left = %d right = %d", line, 
			    left, right);
		    Vect_delete_line(&Out, line);
		}
	    }
	}
    }

    /* build clean topology */
    Vect_build_partial(&Out, GV_BUILD_NONE);
    Vect_build(&Out);
    Vect_close(&Out);

    G_done_msg(" ");
    exit(EXIT_SUCCESS);
}
