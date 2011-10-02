
/****************************************************************
 * 
 *  MODULE:       v.net.salesman
 *  
 *  AUTHOR(S):    Radim Blazek, Markus Metz
 *                
 *  PURPOSE:      Create a cycle connecting given nodes.
 *                
 *  COPYRIGHT:    (C) 2001-2011 by the GRASS Development Team
 * 
 *                This program is free software under the 
 *                GNU General Public License (>=v2). 
 *                Read the file COPYING that comes with GRASS
 *                for details.
 * 
 **************************************************************/
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <grass/gis.h>
#include <grass/vector.h>
#include <grass/dbmi.h>
#include <grass/glocale.h>

/* TODO: Use some better algorithm */

typedef struct
{
    int city;
    double cost;
} COST;

int ncities;			/* number of cities */
int nnodes;			/* number of nodes */
int *cities;			/* array of cities */
int *cused;			/* city is in cycle */
COST **costs;			/* pointer to array of pointers to arrays of sorted forward costs */
COST **bcosts;			/* pointer to array of pointers to arrays of sorted backward costs */
int *cycle;			/* path */
int ncyc = 0;			/* number of cities in cycle */
int debug_level;

int cmp(const void *, const void *);

int cnode(int city)
{
    return (cities[city]);
}

void add_city(int city, int after)
{				/* index !!! to cycle, after which to put it */
    int i, j;

    if (after == -1) {
	cycle[0] = city;
    }
    else {
	/* for a large number of cities this will become slow */
	for (j = ncyc - 1; j > after; j--)
	    cycle[j + 1] = cycle[j];

	cycle[after + 1] = city;
    }
    cused[city] = 1;
    ncyc++;

    if (debug_level >= 2) {
	G_debug(2, "Cycle:");
	for (i = 0; i < ncyc; i++) {
	    G_debug(2, "%d: %d: %d", i, cycle[i], cities[cycle[i]]);
	}
    }

}


int main(int argc, char **argv)
{
    int i, j, k, ret, city, city1;
    int nlines, type, ltype, afield, tfield, geo, cat;
    int node, node1, node2, line;
    double **cost_cache;			/* pointer to array of pointers to arrays of cached costs */
    struct Option *map, *output, *afield_opt, *tfield_opt, *afcol, *abcol,
	*type_opt, *term_opt;
    struct Flag *geo_f;
    struct GModule *module;
    struct Map_info Map, Out;
    struct ilist *TList;	/* list of terminal nodes */
    struct ilist *List;
    struct ilist *StArcs;	/* list of arcs on Steiner tree */
    struct ilist *StNodes;	/* list of nodes on Steiner tree */
    double cost, tmpcost, tcost;
    struct cat_list *Clist;
    struct line_cats *Cats;
    struct line_pnts *Points;
    const char *dstr;
    char buf[2000], buf2[2000];

    /* Initialize the GIS calls */
    G_gisinit(argv[0]);

    module = G_define_module();
    G_add_keyword(_("vector"));
    G_add_keyword(_("network"));
    G_add_keyword(_("salesman"));
    module->label =
	_("Creates a cycle connecting given nodes (Traveling salesman problem).");
    module->description =
	_("Note that TSP is NP-hard, heuristic algorithm is used by "
	  "this module and created cycle may be sub optimal");

    map = G_define_standard_option(G_OPT_V_INPUT);
    output = G_define_standard_option(G_OPT_V_OUTPUT);

    type_opt = G_define_standard_option(G_OPT_V_TYPE);
    type_opt->options = "line,boundary";
    type_opt->answer = "line,boundary";
    type_opt->description = _("Arc type");

    afield_opt = G_define_standard_option(G_OPT_V_FIELD);
    afield_opt->key = "alayer";
    afield_opt->description = _("Arc layer");

    tfield_opt = G_define_standard_option(G_OPT_V_FIELD);
    tfield_opt->key = "nlayer";
    tfield_opt->answer = "2";
    tfield_opt->description = _("Node layer (used for cities)");

    afcol = G_define_option();
    afcol->key = "afcolumn";
    afcol->type = TYPE_STRING;
    afcol->required = NO;
    afcol->description =
	_("Arc forward/both direction(s) cost column (number)");

    abcol = G_define_option();
    abcol->key = "abcolumn";
    abcol->type = TYPE_STRING;
    abcol->required = NO;
    abcol->description = _("EXPERIMENTAL: Arc backward direction cost column (number)");

    term_opt = G_define_standard_option(G_OPT_V_CATS);
    term_opt->key = "ccats";
    term_opt->required = YES;
    term_opt->description = _("Categories of points ('cities') on nodes "
			      "(layer is specified by nlayer)");

    geo_f = G_define_flag();
    geo_f->key = 'g';
    geo_f->description =
	_("Use geodesic calculation for longitude-latitude locations");

    if (G_parser(argc, argv))
	exit(EXIT_FAILURE);

    Cats = Vect_new_cats_struct();
    Points = Vect_new_line_struct();

    type = Vect_option_to_types(type_opt);
    afield = atoi(afield_opt->answer);

    TList = Vect_new_list();
    List = Vect_new_list();
    StArcs = Vect_new_list();
    StNodes = Vect_new_list();

    Clist = Vect_new_cat_list();
    tfield = atoi(tfield_opt->answer);
    Vect_str_to_cat_list(term_opt->answer, Clist);
    
    dstr = G__getenv("DEBUG");

    if (dstr != NULL)
	debug_level = atoi(dstr);
    else
	debug_level = 0;

    if (debug_level >= 1) {
	G_debug(1, "Input categories:");
	for (i = 0; i < Clist->n_ranges; i++) {
	    G_debug(1, "%d - %d", Clist->min[i], Clist->max[i]);
	}
    }

    if (geo_f->answer)
	geo = 1;
    else
	geo = 0;

    Vect_check_input_output_name(map->answer, output->answer, GV_FATAL_EXIT);

    Vect_set_open_level(2);
    Vect_open_old(&Map, map->answer, "");
    nnodes = Vect_get_num_nodes(&Map);
    nlines = Vect_get_num_lines(&Map);

    /* Create list of terminals based on list of categories */
    for (i = 1; i <= nlines; i++) {
	
	ltype = Vect_get_line_type(&Map, i);
	if (!(ltype & GV_POINT))
	    continue;

	Vect_read_line(&Map, Points, Cats, i);
	node = Vect_find_node(&Map, Points->x[0], Points->y[0], Points->z[0], 0, 0);
	if (!node) {
	    G_warning(_("Point is not connected to the network"));
	    continue;
	}
	if (!(Vect_cat_get(Cats, tfield, &cat)))
	    continue;
	if (Vect_cat_in_cat_list(cat, Clist)) {
	    Vect_list_append(TList, node);
	}
	
    }

    ncities = TList->n_values;
    G_message(_("Number of cities: [%d]"), ncities);
    if (ncities < 2)
	G_fatal_error(_("Not enough cities (< 2)"));

    /* Alloc memory */
    cities = (int *)G_malloc(ncities * sizeof(int));
    cused = (int *)G_malloc(ncities * sizeof(int));
    for (i = 0; i < ncities; i++) {
	G_debug(1, "%d", TList->value[i]);
	cities[i] = TList->value[i];
	cused[i] = 0;		/* not in cycle */
    }

    costs = (COST **) G_malloc(ncities * sizeof(COST *));
    for (i = 0; i < ncities; i++) {
	costs[i] = (COST *) G_malloc(ncities * sizeof(COST));
    }
    cost_cache = (double **) G_malloc(ncities * sizeof(double *));
    for (i = 0; i < ncities; i++) {
	cost_cache[i] = (double *) G_malloc(ncities * sizeof(double));
    }
    if (abcol->answer) {
	bcosts = (COST **) G_malloc(ncities * sizeof(COST *));
	for (i = 0; i < ncities; i++) {
	    bcosts[i] = (COST *) G_malloc(ncities * sizeof(COST));
	}
    }
    else
	bcosts = NULL;

    cycle = (int *)G_malloc((ncities + 1) * sizeof(int));	/* + 1 is for output cycle */

    /* Build graph */
    Vect_net_build_graph(&Map, type, afield, 0, afcol->answer, abcol->answer, NULL,
			 geo, 0);

    /* Create sorted lists of costs */
    /* for a large number of cities this will become very slow, can not be fixed */
    for (i = 0; i < ncities; i++) {
	k = 0;
	for (j = 0; j < ncities; j++) {
	    if (i == j)
		continue;
	    ret =
		Vect_net_shortest_path(&Map, cities[i], cities[j], NULL,
				       &cost);

	    if (ret == -1)
		G_fatal_error(_("Destination node [%d] is unreachable "
				"from node [%d]"), cities[i], cities[j]);

	    /* TODO: add to directional cost cache: from, to, cost */
	    costs[i][k].city = j;
	    costs[i][k].cost = cost;
	    cost_cache[i][j] = cost;

	    k++;
	}
	qsort((void *)costs[i], k, sizeof(COST), cmp);
    }
    
    if (bcosts) {
	for (i = 0; i < ncities; i++) {
	    k = 0;
	    for (j = 0; j < ncities; j++) {
		if (i == j)
		    continue;
		    
		bcosts[i][k].city = j;
		bcosts[i][k].cost = cost_cache[j][i];

		k++;
	    }
	    qsort((void *)bcosts[i], k, sizeof(COST), cmp);
	}
    }
    
    if (debug_level >= 2) {
	/* debug: print sorted */
	for (i = 0; i < ncities; i++) {
	    for (j = 0; j < ncities - 1; j++) {
		city = costs[i][j].city;
		G_debug(2, "%d -> %d = %f", cities[i], cities[city],
			costs[i][j].cost);
	    }
	}
    }

    /* find 2 cities with largest distance */
    cost = city = -1;
    for (i = 0; i < ncities; i++) {
	tmpcost = costs[i][ncities - 2].cost;
	if (tmpcost > cost) {
	    cost = tmpcost;
	    city = i;
	}
    }
    G_debug(2, "biggest costs %d - %d", city,
	    costs[city][ncities - 2].city);

    /* add these 2 cities to array */
    add_city(city, -1);
    add_city(costs[city][ncities - 2].city, 0);

    /* In each step, find not used city, with biggest cost to any used city, and insert 
     *  into cycle between 2 nearest nodes */
    /* for a large number of cities this will become very slow, can be fixed */
    for (i = 0; i < ncities - 2; i++) {
	G_percent(i, ncities - 3, 1);
	cost = -1;
	G_debug(2, "---- city %d ----", i);
	for (j = 0; j < ncities; j++) {
	    if (cused[j] == 1)
		continue;
	    tmpcost = 0;
	    for (k = 0; k < ncities - 1; k++) {
		G_debug(2, "forward? %d (%d) - %d (%d)", j, cnode(j),
			costs[j][k].city, cnode(costs[j][k].city));
		if (!cused[costs[j][k].city])
		    continue;	/* only used */
		/* directional costs j -> k */
		tmpcost += costs[j][k].cost;
		break;		/* first nearest */
	    }
	    /* forward/backward: tmpcost = min(fcost) + min(bcost) */
	    if (bcosts) {
		for (k = 0; k < ncities - 1; k++) {
		    G_debug(2, "backward? %d (%d) - %d (%d)", j, cnode(j),
			    bcosts[j][k].city, cnode(bcosts[j][k].city));
		    if (!cused[bcosts[j][k].city])
			continue;	/* only used */
		    /* directional costs k -> j */
		    tmpcost += bcosts[j][k].cost;
		    break;		/* first nearest */
		}
	    }

	    G_debug(2, "    cost = %f x %f", tmpcost, cost);
	    if (tmpcost > cost) {
		cost = tmpcost;
		city = j;
	    }
	}
	G_debug(2, "add city %d", city);

	/* add to cycle on lowest costs */
	cycle[ncyc] = cycle[0];	/* temporarily close the cycle */
	cost = PORT_DOUBLE_MAX;
	city1 = 0;
	for (j = 0; j < ncyc; j++) {
	    /* cost from j to j + 1 (directional) */
	    /* get cost from directional cost cache */
	    tcost = cost_cache[cycle[j]][cycle[j + 1]];
	    tmpcost = -tcost;

	    /* check insertion of city between j and j + 1 */

	    /* cost from j to city (directional) */
	    /* get cost from directional cost cache */
	    tcost = cost_cache[cycle[j]][city];
	    tmpcost += tcost;
	    /* cost from city to j + 1 (directional) */
	    /* get cost from directional cost cache */
	    tcost = cost_cache[city][cycle[j + 1]];
	    tmpcost += tcost;
	    
	    /* tmpcost must always be > 0 */

	    G_debug(2, "? %d - %d cost = %f x %f", node1, node2, tmpcost,
		    cost);
	    /* always true for j = 0 */
	    if (tmpcost < cost) {
		city1 = j;
		cost = tmpcost;
	    }
	}

	add_city(city, city1);

    }

    if (debug_level >= 2) {
	/* debug print */
	G_debug(2, "Cycle:");
	for (i = 0; i < ncities; i++) {
	    G_debug(2, "%d: %d: %d", i, cycle[i], cities[cycle[i]]);
	}
    }

    /* Create list of arcs */
    cycle[ncities] = cycle[0];  /* close the cycle */
    for (i = 0; i < ncities; i++) {
	node1 = cities[cycle[i]];
	node2 = cities[cycle[i + 1]];
	G_debug(2, " %d -> %d", node1, node2);
	ret = Vect_net_shortest_path(&Map, node1, node2, List, NULL);
	for (j = 0; j < List->n_values; j++) {
	    line = abs(List->value[j]);
	    Vect_list_append(StArcs, line);
	    Vect_get_line_nodes(&Map, line, &node1, &node2);
	    Vect_list_append(StNodes, node1);
	    Vect_list_append(StNodes, node2);
	}
    }



    /* Write arcs to new map */
    Vect_open_new(&Out, output->answer, Vect_is_3d(&Map));
    Vect_hist_command(&Out);

    G_verbose_message(_("Cycle:"));
    G_verbose_message(_("Arcs' categories (layer %d, %d arcs):"), afield,
	    StArcs->n_values);
    for (i = 0; i < StArcs->n_values; i++) {
	line = StArcs->value[i];
	ltype = Vect_read_line(&Map, Points, Cats, line);
	Vect_write_line(&Out, ltype, Points, Cats);
	Vect_cat_get(Cats, afield, &cat);
	if (i > 0) {
	    sprintf(buf2, ", %d", cat);
	    strcat(buf, buf2);
	}
	else
	    sprintf(buf, "%d", cat);
    }
    G_verbose_message("%s\n\n", buf);

    G_verbose_message(_("Nodes' categories (layer %d, %d nodes):"), tfield,
	    StNodes->n_values);
    k = 0;
    for (i = 0; i < TList->n_values; i++) {
	double coor_x, coor_y, coor_z;
	
	node = TList->value[i];
	Vect_get_node_coor(&Map, node, &coor_x, &coor_y, &coor_z);
	line = Vect_find_line(&Map, coor_x, coor_y, coor_z, GV_POINT, 0, 0, 0);
	
	if (!line)
	    continue;

	ltype = Vect_read_line(&Map, Points, Cats, line);
	if (!(ltype & GV_POINT))
	    continue;
	if (!(Vect_cat_get(Cats, tfield, &cat)))
	    continue;
	Vect_write_line(&Out, ltype, Points, Cats);
	if (k > 0) {
	    sprintf(buf2, ", %d", cat);
	    strcat(buf, buf2);
	}
	else
	    sprintf(buf, "%d", cat);
	k++;
    }
    G_verbose_message("%s\n\n", buf);

    Vect_build(&Out);

    /* Free, ... */
    Vect_destroy_list(StArcs);
    Vect_destroy_list(StNodes);
    Vect_close(&Map);
    Vect_close(&Out);

    exit(EXIT_SUCCESS);
}

int cmp(const void *pa, const void *pb)
{
    COST *p1 = (COST *) pa;
    COST *p2 = (COST *) pb;

    if (p1->cost < p2->cost)
	return -1;

    if (p1->cost > p2->cost)
	return 1;

    return 0;
}
