
/****************************************************************
 *
 * MODULE:     v.net.distance
 *
 * AUTHOR(S):  Daniel Bundala
 *
 * PURPOSE:    Computes shortest distance via the network between
 *             the given sets of features.
 *
 * COPYRIGHT:  (C) 2009-2010 by Daniel Bundala, and the GRASS Development Team
 *
 *             This program is free software under the
 *             GNU General Public License (>=v2).
 *             Read the file COPYING that comes with GRASS
 *             for details.
 *
 ****************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <grass/gis.h>
#include <grass/vector.h>
#include <grass/glocale.h>
#include <grass/dbmi.h>
#include <grass/neta.h>

int main(int argc, char *argv[])
{
    struct Map_info In, Out;
    static struct line_pnts *Points;
    struct line_cats *Cats;
    struct GModule *module;	/* GRASS module for parsing arguments */
    struct Option *map_in, *map_out;
    struct Option *catf_opt, *wheref_opt;
    struct Option *catt_opt, *wheret_opt, *to_type_opt;
    struct Option *afield_opt, *nfield_opt, *abcol, *afcol, *ncol;
    struct Flag *geo_f;
    int with_z, geo;
    int mask_type;
    struct varray *varrayf, *varrayt;
    int afield, nfield;
    dglGraph_s *graph;
    struct ilist *nodest;
    int i, nnodes, nlines;
    int *dst, *nodes_to_features;
    dglInt32_t **prev;
    struct line_cats **on_path;
    char buf[2000];

    /* Attribute table */
    dbString sql;
    dbDriver *driver;
    struct field_info *Fi;

    /* initialize GIS environment */
    G_gisinit(argv[0]);		/* reads grass env, stores program name to G_program_name() */

    /* initialize module */
    module = G_define_module();
    G_add_keyword(_("vector"));
    G_add_keyword(_("network"));
    G_add_keyword(_("shortest path"));
    module->label = _("Computes shortest distance via the network between "
		      "the given sets of features.");
    module->description =
	_("Finds the shortest paths from a feature 'to' to every feature 'from' "
	 "and various information about this relation are uploaded to the attribute table.");

    /* Define the different options as defined in gis.h */
    map_in = G_define_standard_option(G_OPT_V_INPUT);
    map_out = G_define_standard_option(G_OPT_V_OUTPUT);

    afield_opt = G_define_standard_option(G_OPT_V_FIELD);
    afield_opt->key = "alayer";
    afield_opt->answer = "1";
    afield_opt->description = _("Arc layer");
    afield_opt->guisection = _("Cost");

    nfield_opt = G_define_standard_option(G_OPT_V_FIELD);
    nfield_opt->key = "nlayer";
    nfield_opt->answer = "2";
    nfield_opt->description = _("Node layer");
    nfield_opt->guisection = _("Cost");

    catf_opt = G_define_standard_option(G_OPT_V_CATS);
    catf_opt->key = "from_cats";
    catf_opt->label = _("From category values");
    catf_opt->guisection = _("From");

    wheref_opt = G_define_standard_option(G_OPT_DB_WHERE);
    wheref_opt->key = "from_where";
    wheref_opt->label =
	_("From WHERE conditions of SQL statement without 'where' keyword");
    wheref_opt->guisection = _("From");

    catt_opt = G_define_standard_option(G_OPT_V_CATS);
    catt_opt->key = "to_cats";
    catt_opt->label = _("To category values");
    catt_opt->guisection = _("To");

    wheret_opt = G_define_standard_option(G_OPT_DB_WHERE);
    wheret_opt->key = "to_where";
    wheret_opt->label =
	_("To WHERE conditions of SQL statement without 'where' keyword");
    wheret_opt->guisection = _("To");

    to_type_opt = G_define_standard_option(G_OPT_V_TYPE);
    to_type_opt->key = "to_type";
    to_type_opt->options = "point,line,boundary";
    to_type_opt->answer = "point";
    to_type_opt->description = _("To feature type");
    to_type_opt->guisection = _("To");

    afcol = G_define_standard_option(G_OPT_DB_COLUMN);
    afcol->key = "afcolumn";
    afcol->required = NO;
    afcol->description =
	_("Arc forward/both direction(s) cost column (number)");
    afcol->guisection = _("Cost");

    abcol = G_define_standard_option(G_OPT_DB_COLUMN);
    abcol->key = "abcolumn";
    abcol->required = NO;
    abcol->description = _("Arc backward direction cost column (number)");
    abcol->guisection = _("Cost");

    ncol = G_define_standard_option(G_OPT_DB_COLUMN);
    ncol->key = "ncolumn";
    ncol->required = NO;
    ncol->description = _("Node cost column (number)");
    ncol->guisection = _("Cost");

    map_out = G_define_standard_option(G_OPT_V_OUTPUT);

    geo_f = G_define_flag();
    geo_f->key = 'g';
    geo_f->description =
	_("Use geodesic calculation for longitude-latitude locations");


    /* options and flags parser */
    if (G_parser(argc, argv))
	exit(EXIT_FAILURE);
    mask_type = Vect_option_to_types(to_type_opt);

    Points = Vect_new_line_struct();
    Cats = Vect_new_cats_struct();

    Vect_check_input_output_name(map_in->answer, map_out->answer,
				 GV_FATAL_EXIT);

    Vect_set_open_level(2);

    if (1 > Vect_open_old(&In, map_in->answer, ""))
	G_fatal_error(_("Unable to open vector map <%s>"), map_in->answer);

    with_z = Vect_is_3d(&In);

    if (0 > Vect_open_new(&Out, map_out->answer, with_z)) {
	Vect_close(&In);
	G_fatal_error(_("Unable to create vector map <%s>"), map_out->answer);
    }


    if (geo_f->answer) {
	geo = 1;
	if (G_projection() != PROJECTION_LL)
	    G_warning(_("The current projection is not longitude-latitude"));
    }
    else
	geo = 0;


    nnodes = Vect_get_num_nodes(&In);
    nlines = Vect_get_num_lines(&In);

    dst = (int *)G_calloc(nnodes + 1, sizeof(int));
    prev = (dglInt32_t **) G_calloc(nnodes + 1, sizeof(dglInt32_t *));
    nodes_to_features = (int *)G_calloc(nnodes + 1, sizeof(int));
    on_path =
	(struct line_cats **)G_calloc(nlines + 1, sizeof(struct line_cats *));
    if (!dst || !prev || !nodes_to_features || !on_path)
	G_fatal_error(_("Out of memory"));

    for (i = 1; i <= nlines; i++)
	on_path[i] = Vect_new_cats_struct();

    /*initialise varrays and nodes list appropriatelly */
    afield = Vect_get_field_number(&In, afield_opt->answer);
    nfield = Vect_get_field_number(&In, nfield_opt->answer);

    NetA_initialise_varray(&In, nfield, GV_POINT, wheref_opt->answer,
			   catf_opt->answer, &varrayf);
    NetA_initialise_varray(&In, nfield, mask_type, wheret_opt->answer,
			   catt_opt->answer, &varrayt);

    nodest = Vect_new_list();
    NetA_varray_to_nodes(&In, varrayt, nodest, nodes_to_features);

    Vect_net_build_graph(&In, mask_type, 1, 0, afcol->answer, abcol->answer,
			 ncol->answer, geo, 0);
    graph = &(In.graph);
    NetA_distance_from_points(graph, nodest, dst, prev);

    /* Create table */
    Fi = Vect_default_field_info(&Out, 1, NULL, GV_1TABLE);
    Vect_map_add_dblink(&Out, 1, NULL, Fi->table, GV_KEY_COLUMN, Fi->database,
			Fi->driver);
    db_init_string(&sql);
    driver = db_start_driver_open_database(Fi->driver, Fi->database);
    if (driver == NULL)
	G_fatal_error(_("Unable to open database <%s> by driver <%s>"),
		      Fi->database, Fi->driver);

    sprintf(buf,
	    "create table %s ( cat integer, tcat integer, dist double precision)",
	    Fi->table);

    db_set_string(&sql, buf);
    G_debug(2, db_get_string(&sql));

    if (db_execute_immediate(driver, &sql) != DB_OK) {
	db_close_database_shutdown_driver(driver);
	G_fatal_error(_("Unable to create table: '%s'"), db_get_string(&sql));
    }

    if (db_create_index2(driver, Fi->table, GV_KEY_COLUMN) != DB_OK)
	G_warning(_("Cannot create index"));

    if (db_grant_on_table
	(driver, Fi->table, DB_PRIV_SELECT, DB_GROUP | DB_PUBLIC) != DB_OK)
	G_fatal_error(_("Cannot grant privileges on table <%s>"), Fi->table);

    db_begin_transaction(driver);

    Vect_copy_head_data(&In, &Out);
    Vect_hist_copy(&In, &Out);
    Vect_hist_command(&Out);

    for (i = 1; i <= nlines; i++)
	if (varrayf->c[i]) {
	    int type = Vect_read_line(&In, Points, Cats, i);
	    int node, tcat, cat;
	    double cost;
	    dglInt32_t *vertex, vertex_id;

	    if (!Vect_cat_get(Cats, nfield, &cat))
		continue;
		
	    if (type & GV_POINTS) {
		node = Vect_find_node(&In, Points->x[0], Points->y[0], Points->z[0], 0, 0);
	    }
	    else {
		Vect_get_line_nodes(&In, i, &node, NULL);
	    }
	    if (node < 1)
		continue;
	    Vect_write_line(&Out, type, Points, Cats);
	    cost = dst[node] / (double)In.cost_multip;
	    vertex = dglGetNode(graph, node);
	    vertex_id = node;
	    while (prev[vertex_id] != NULL) {
		Vect_cat_set(on_path
			     [abs(dglEdgeGet_Id(graph, prev[vertex_id]))], 1,
			     cat);
		vertex = dglEdgeGet_Head(graph, prev[vertex_id]);
		vertex_id = dglNodeGet_Id(graph, vertex);
	    }
	    Vect_read_line(&In, NULL, Cats, nodes_to_features[vertex_id]);
	    if (!Vect_cat_get(Cats, nfield, &tcat))
		continue;
	    sprintf(buf, "insert into %s values (%d, %d, %f)", Fi->table, cat,
		    tcat, cost);

	    db_set_string(&sql, buf);
	    G_debug(3, db_get_string(&sql));
	    if (db_execute_immediate(driver, &sql) != DB_OK) {
		db_close_database_shutdown_driver(driver);
		G_fatal_error(_("Cannot insert new record: %s"),
			      db_get_string(&sql));
	    };
	}

    for (i = 1; i <= nlines; i++)
	if (on_path[i]->n_cats > 0) {
	    int type = Vect_read_line(&In, Points, NULL, i);

	    Vect_write_line(&Out, type, Points, on_path[i]);
	}

    db_commit_transaction(driver);
    db_close_database_shutdown_driver(driver);

    Vect_build(&Out);

    Vect_close(&In);
    Vect_close(&Out);

    for (i = 1; i <= nlines; i++)
	Vect_destroy_cats_struct(on_path[i]);
    G_free(on_path);
    G_free(nodes_to_features);
    G_free(dst);
    G_free(prev);

    exit(EXIT_SUCCESS);
}
