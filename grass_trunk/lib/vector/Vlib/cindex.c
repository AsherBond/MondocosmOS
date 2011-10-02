/*!
  \file lib/vector/Vlib/cindex.c
  
  \brief Vector library - category index.
  
  Higher level functions for reading/writing/manipulating vectors.
  
  (C) 2001-2009 by the GRASS Development Team
  
  This program is free software under the GNU General Public License
  (>=v2). Read the file COPYING that comes with GRASS for details.
  
  \author Radim Blazek
*/

#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <grass/vector.h>
#include <grass/glocale.h>

static int cmp_cat(const void *pa, const void *pb);

static void check_status(const struct Map_info *Map)
{
    if (!Map->plus.cidx_up_to_date)
	G_fatal_error(_("Category index is not up to date"));
}

/*!
  \brief Get number of layers in category index
  
  \param Map pointer to Map_info structure
  
  \return number of layers
 */
int Vect_cidx_get_num_fields(const struct Map_info *Map)
{
    check_status(Map);

    return Map->plus.n_cidx;
}

/*!
  \brief Get layer number for given index 

  G_fatal_error() is called when index not found.
  
  \param Map pointer to Map_info structure
  \param index layer index: from 0 to Vect_cidx_get_num_fields() - 1

  \return layer number 
 */
int Vect_cidx_get_field_number(const struct Map_info *Map, int index)
{
    check_status(Map);

    if (index >= Map->plus.n_cidx)
	G_fatal_error(_("Invalid layer index (index >= number of layers)"));

    return Map->plus.cidx[index].field;
}

/*!
  \brief Get layer index for given layer number
  
  \param Map pointer to Map_info structure
  \param field layer number 

  \return layer index
  \return -1 if not found 
 */
int Vect_cidx_get_field_index(const struct Map_info *Map, int field)
{
    int i;
    const struct Plus_head *Plus;

    G_debug(2, "Vect_cidx_get_field_index() field = %d", field);

    check_status(Map);
    Plus = &(Map->plus);

    for (i = 0; i < Plus->n_cidx; i++) {
	if (Plus->cidx[i].field == field)
	    return i;
    }

    return -1;
}

/*!
  \brief Get number of unique categories for given layer index 
  
  G_fatal_error() is called when index not found.
  
  \param Map pointer to Map_info structure
  \param index layer number
  
  \return number of unique categories
  \return -1 on error
 */
int Vect_cidx_get_num_unique_cats_by_index(const struct Map_info *Map, int index)
{
    check_status(Map);

    if (index < 0 || index >= Map->plus.n_cidx)
	G_fatal_error(_("Invalid layer index (index < 0 or index >= number of layers)"));

    return Map->plus.cidx[index].n_ucats;
}

/*!
  \brief Get number of categories for given layer index 
  
  \param Map pointer to Map_info structure
  \param index layer index
  
  \return number of categories
  \return -1 on error
 */
int Vect_cidx_get_num_cats_by_index(const struct Map_info *Map, int index)
{
    check_status(Map);
    if (index >= Map->plus.n_cidx)
	G_fatal_error(_("Invalid layer index (index >= number of layers)"));

    return Map->plus.cidx[index].n_cats;
}

/*!
  \brief Get number of types for given layer index 
  
  G_fatal_error() is called when index not found.
  
  \param Map pointer to Map_info structure
  \param field_index layer index
  
  \return number of types
  \return -1 on error
 */
int Vect_cidx_get_num_types_by_index(const struct Map_info *Map, int field_index)
{
    check_status(Map);
    if (field_index >= Map->plus.n_cidx)
	G_fatal_error(_("Invalid layer index (index >= number of layers)"));

    return Map->plus.cidx[field_index].n_types;
}

/*!
  \brief Get type count field index and type index
  
  \param Map pointer to Map_info structure
  \param field_index layer index
  \param type_index type index
  \param[out] type feature type
  \param[out] count number of items
  
  \return 1 on success
  \return 0 on error
*/
int Vect_cidx_get_type_count_by_index(const struct Map_info *Map, int field_index,
				      int type_index, int *type, int *count)
{
    check_status(Map);
    if (field_index >= Map->plus.n_cidx)
	G_fatal_error(_("Invalid layer index (index >= number of layers)"));

    *type = Map->plus.cidx[field_index].type[type_index][0];
    *count = Map->plus.cidx[field_index].type[type_index][1];

    return 1;
}

/*!
  \brief Get count of features of certain type by layer and type
  
  \param Map pointer to Map_info structure
  \param field layer number
  \param type feature type
  
  \return feature count
  \return 0 if no features, no such field or no such type in cidx
 */
int Vect_cidx_get_type_count(const struct Map_info *Map, int field, int type)
{
    int i, fi, count = 0;

    G_debug(3, "Vect_cidx_get_type_count() field = %d, type = %d", field,
	    type);

    check_status(Map);

    if ((fi = Vect_cidx_get_field_index(Map, field)) < 0)
	return 0;		/* field not found */
    G_debug(3, "field_index = %d", fi);

    G_debug(3, "ntypes = %d", Map->plus.cidx[fi].n_types);
    for (i = 0; i < Map->plus.cidx[fi].n_types; i++) {
	int tp, cnt;

	tp = Map->plus.cidx[fi].type[i][0];
	cnt = Map->plus.cidx[fi].type[i][1];
	if (tp & type)
	    count += cnt;
	G_debug(3, "%d tp = %d, cnt= %d count = %d", i, tp, cnt, count);
    }

    return count;
}

/*!
  \brief Get number of categories for given field and category index 
  
  \param Map pointer to Map_info structure
  \param field_index layer index
  \param cat_index category index
  \param[out] cat category number
  \param[out] type feature type
  \param[out] id feature id
  
  \return 1 on success
  \return 0 on error
*/
int Vect_cidx_get_cat_by_index(const struct Map_info *Map, int field_index,
			       int cat_index, int *cat, int *type, int *id)
{
    check_status(Map);		/* This check is slow ? */

    if (field_index >= Map->plus.n_cidx || field_index < 0 ||
	cat_index >= Map->plus.cidx[field_index].n_cats)
	G_fatal_error(_("Layer or category index out of range"));

    *cat = Map->plus.cidx[field_index].cat[cat_index][0];
    *type = Map->plus.cidx[field_index].cat[cat_index][1];
    *id = Map->plus.cidx[field_index].cat[cat_index][2];

    return 1;
}

/* Compare by cat */
static int cmp_cat(const void *pa, const void *pb)
{
    int *p1 = (int *)pa;
    int *p2 = (int *)pb;

    if (*p1 < p2[0])
	return -1;
    if (*p1 > p2[0])
	return 1;
    return 0;
}

/*!
  \brief Find next line/area id for given category, start_index and type_mask 
  
  \param Map pointer to Map_info structure
  \param field_index layer index
  \param cat category number
  \param type_mask requested type
  \param start_index start search at this index (0 - whole category index)
  \param[out] type returned type
  \param[out] id returned line/area id
  
  \return index to array
  \return -1 not found
*/
int Vect_cidx_find_next(const struct Map_info *Map, int field_index, int cat,
			int type_mask, int start_index, int *type, int *id)
{
    int *catp, cat_index;
    struct Cat_index *ci;

    G_debug(3,
	    "Vect_cidx_find_next() cat = %d, type_mask = %d, start_index = %d",
	    cat, type_mask, start_index);

    check_status(Map);		/* This check is slow ? */
    *type = *id = 0;

    if (field_index >= Map->plus.n_cidx)
	G_fatal_error(_("Layer index out of range"));

    if (start_index < 0)
	start_index = 0;
    if (start_index >= Map->plus.cidx[field_index].n_cats)
	return -1;		/* outside range */

    /* pointer to beginning of searched part of category index */
    ci = &(Map->plus.cidx[field_index]);

    /* calc with pointers is using sizeof(int) !!! */
    catp = bsearch(&cat, (int *)ci->cat + start_index * 3,
		   (size_t) ci->n_cats - start_index,
		   3 * sizeof(int), cmp_cat);

    G_debug(3, "catp = %p", catp);
    if (!catp)
	return -1;

    /* get index from pointer, the difference between pointers is using sizeof(int) !!! */
    cat_index = (catp - (int *)ci->cat) / 3;

    G_debug(4, "cat_index = %d", cat_index);

    /* Go down to the first if multiple */
    while (cat_index > start_index) {
	if (ci->cat[cat_index - 1][0] != cat) {
	    break;
	}
	cat_index--;
    }
    G_debug(4, "cat_index = %d", cat_index);

    do {
	G_debug(3, "  cat_index = %d", cat_index);
	if (ci->cat[cat_index][0] == cat && ci->cat[cat_index][1] & type_mask) {
	    *type = ci->cat[cat_index][1];
	    *id = ci->cat[cat_index][2];
	    G_debug(3, "  type match -> record found");
	    return cat_index;
	}
	cat_index++;
    } while (cat_index < ci->n_cats);

    return -1;
}


/*!
  \brief Gind all line/area id's for given category
  
  \param Map pointer to Map_info structure
  \param layer layer number
  \param type_mask type of objects to search for
  \param cat category number
  \param[out] lines array of ids of found lines/points
*/
void Vect_cidx_find_all(const struct Map_info *Map, int layer, int type_mask,
			int cat, struct ilist *lines)
{
    int type, line;
    struct Cat_index *ci;
    int field_index, idx;

    Vect_reset_list(lines);
    field_index = Vect_cidx_get_field_index(Map, layer);

    if (field_index == -1) {
	/* not found */
	return;
    }
    ci = &(Map->plus.cidx[field_index]);

    idx = Vect_cidx_find_next(Map, field_index, cat,
			      type_mask, 0, &type, &line);

    if (idx == -1) {
	return;
    }

    do {
	if (!(ci->cat[idx][1] & type_mask)
	    || ci->cat[idx][0] != cat) {
	    break;
	}
	Vect_list_append(lines, ci->cat[idx][2]);
	idx++;
    } while (idx < ci->n_cats);
    return;
}


#define SEP "------------------------------------------------------------------------------------------\n"

/*!
  \brief Write category index in text form to file
  
  \param Map pointer to Map_info structure
  \param[out] out output file
  
  \return 1 on success
  \return 0 on error
*/
int Vect_cidx_dump(const struct Map_info *Map, FILE * out)
{
    int i, field, nfields, ntypes;

    G_debug(2, "Vect_cidx_dump()");

    check_status(Map);

    nfields = Vect_cidx_get_num_fields(Map);
    fprintf(out, "---------- CATEGORY INDEX DUMP: Number of layers: %d "
	    "--------------------------------------\n", nfields);

    for (i = 0; i < nfields; i++) {
	int j, nucats, ncats;

	field = Vect_cidx_get_field_number(Map, i);
	nucats = Vect_cidx_get_num_unique_cats_by_index(Map, i);
	ncats = Vect_cidx_get_num_cats_by_index(Map, i);
	ntypes = Vect_cidx_get_num_types_by_index(Map, i);

	fprintf(out,
		"Layer %6d  number of unique cats: %7d  number of "
		"cats: %7d  number of types: %d\n",
		field, nucats, ncats, ntypes);
	fprintf(out, SEP);

	fprintf(out, "            type |     count\n");
	for (j = 0; j < ntypes; j++) {
	    int type, count;

	    Vect_cidx_get_type_count_by_index(Map, i, j, &type, &count);
	    fprintf(out, "           %5d | %9d\n", type, count);
	}

	fprintf(out, " category | type | line/area\n");
	for (j = 0; j < ncats; j++) {
	    int cat, type, id;

	    Vect_cidx_get_cat_by_index(Map, i, j, &cat, &type, &id);
	    fprintf(out, "%9d | %4d | %9d\n", cat, type, id);
	}

	fprintf(out, SEP);
    }

    return 1;
}

/*!
  \brief Save category index to file (cidx)

  \param Map pointer to Map_info structure
  
  \return 0 on success
  \return 1 on error
 */
int Vect_cidx_save(struct Map_info *Map)
{
    struct Plus_head *plus;
    char fname[1024], buf[1024];
    struct gvfile fp;

    G_debug(2, "Vect_cidx_save()");
    check_status(Map);

    plus = &(Map->plus);

    sprintf(buf, "%s/%s", GV_DIRECTORY, Map->name);
    G_file_name(fname, buf, GV_CIDX_ELEMENT, Map->mapset);
    G_debug(2, "Open cidx: %s", fname);
    dig_file_init(&fp);
    fp.file = fopen(fname, "w");
    if (fp.file == NULL) {
	G_warning(_("Unable to open cidx file <%s> for write"), fname);
	return 1;
    }

    /* set portable info */
    dig_init_portable(&(plus->cidx_port), dig__byte_order_out());

    if (0 > dig_write_cidx(&fp, plus)) {
	G_warning(_("Error writing out category index file <%s>"), fname);
	return 1;
    }

    fclose(fp.file);

    return 0;
}

/*!
  \brief Read category index from file if exists
  
  \param Map pointer to Map_info structure
  \param head_only read only header
  
  \return 0 on success 
  \return 1 if file does not exist
  \return -1 error, file exists but cannot be read
 */
int Vect_cidx_open(struct Map_info *Map, int head_only)
{
    int ret;
    char buf[500], file_path[2000];
    struct gvfile fp;
    struct Plus_head *Plus;

    G_debug(2, "Vect_cidx_open(): name = %s mapset= %s", Map->name,
	    Map->mapset);

    Plus = &(Map->plus);

    sprintf(buf, "%s/%s", GV_DIRECTORY, Map->name);
    G_file_name(file_path, buf, GV_CIDX_ELEMENT, Map->mapset);

    if (access(file_path, F_OK) != 0)	/* does not exist */
	return 1;


    dig_file_init(&fp);
    fp.file = G_fopen_old(buf, GV_CIDX_ELEMENT, Map->mapset);

    if (fp.file == NULL) {	/* category index file is not available */
	G_warning(_("Unable to open category index file for vector map <%s>"),
		  Vect_get_full_name(Map));
	return -1;
    }

    /* load category index to memory */
    dig_cidx_init(Plus);
    ret = dig_read_cidx(&fp, Plus, head_only);

    fclose(fp.file);

    if (ret == 1) {
	G_debug(3, "Cannot read cidx");
	return -1;
    }

    return 0;
}
