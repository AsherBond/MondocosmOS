#include <stdlib.h>
#include <grass/gis.h>
#include <grass/glocale.h>
#include <grass/imagery.h>
#include "parms.h"


int parse(int argc, char *argv[], struct parms *parms)
{
    struct Option *group, *subgroup, *sigfile, *trainingmap;

    trainingmap = G_define_standard_option(G_OPT_R_MAP);
    trainingmap->key = "trainingmap";
    trainingmap->description = _("Ground truth training map");

    group = G_define_standard_option(G_OPT_I_GROUP);

    subgroup = G_define_standard_option(G_OPT_I_SUBGROUP);

    sigfile = G_define_option();
    sigfile->key = "signaturefile";
    sigfile->description = _("Name for output file containing result signatures");
    sigfile->required = YES;
    sigfile->type = TYPE_STRING;

    if (G_parser(argc, argv))
	exit(EXIT_FAILURE);

    parms->training_map = trainingmap->answer;
    parms->group = group->answer;
    parms->subgroup = subgroup->answer;
    parms->sigfile = sigfile->answer;

    /* check all the inputs */
    if (G_find_raster(parms->training_map, "") == NULL)
	G_fatal_error(_("Raster map <%s> not found"), parms->training_map);

    if (!I_find_group(parms->group))
	G_fatal_error(_("Group <%s> not found"), parms->group);

    if (!I_find_subgroup(parms->group, parms->subgroup))
	G_fatal_error(_("Subgroup <%s> not found"), parms->subgroup);

    return 0;
}
