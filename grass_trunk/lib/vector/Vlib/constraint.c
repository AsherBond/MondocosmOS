/*!
   \file lib/vector/Vlib/constraint.c

   \brief Vector library - constraints

   Higher level functions for reading/writing/manipulating vectors.

   These routines can affect the read_next_line funtions by
   restricting what they return. They are applied on a per map basis.

   These do not affect the lower level direct read functions.

   Normally, all 'Alive' lines will be returned unless overridden by
   this function. You can specified all the types you are interested
   in (by oring their types together). You can use this to say exclude
   Area type lines.

   By default all DEAD lines are ignored by the read_next_line ()
   functions This too can be overridden by including their types.

   Refer to dig_defines for the line type Defines

   All lines can be forced to be read by setting type = -1

   (C) 2001-2009 by the GRASS Development Team

   This program is free software under the GNU General Public License
   (>=v2).  Read the file COPYING that comes with GRASS for details.

   \author Original author CERL, probably Dave Gerdes or Mike Higgins.
   \author Update to GRASS 5.7 Radim Blazek and David D. Gray.

   \date 2001-2008
 */

#include <grass/vector.h>

/*!
   \brief Set constraint region

   \param Map vector map
   \param n,s,e,w,t,b north, south, east, west, top, bottom coordinates

   \return 0 on success
   \return -1 on error
 */
int
Vect_set_constraint_region(struct Map_info *Map,
			   double n, double s, double e, double w, double t,
			   double b)
{
    if (n <= s)
	return -1;
    if (e <= w)
	return -1;

    Map->Constraint_region_flag = 1;
    Map->Constraint_box.N = n;
    Map->Constraint_box.S = s;
    Map->Constraint_box.E = e;
    Map->Constraint_box.W = w;
    Map->Constraint_box.T = t;
    Map->Constraint_box.B = b;
    Map->head.proj = G_projection();

    return 0;
}

/*!
   \brief Get constraint box

   \param Map vector map
   \param[out] Box bounding box

   \return 0
 */
int Vect_get_constraint_box(const struct Map_info *Map, struct bound_box * Box)
{
    Box->N = Map->Constraint_box.N;
    Box->S = Map->Constraint_box.S;
    Box->E = Map->Constraint_box.E;
    Box->W = Map->Constraint_box.W;
    Box->T = Map->Constraint_box.T;
    Box->B = Map->Constraint_box.B;

    return 0;
}

/*!
   \brief Set constraint type

   \param Map vector map
   \param type constraint type

   \return 0
 */
int Vect_set_constraint_type(struct Map_info *Map, int type)
{
    Map->Constraint_type = type;
    Map->Constraint_type_flag = 1;

    return 0;
}

/*!
   \brief Remove constraints

   \param Map vector map

   \return 0
 */
int Vect_remove_constraints(struct Map_info *Map)
{
    Map->Constraint_region_flag = 0;
    Map->Constraint_type_flag = 0;

    return 0;
}
