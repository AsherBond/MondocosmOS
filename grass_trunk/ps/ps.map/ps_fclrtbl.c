/* Function: ps_fcolortable
 **
 ** Author: Radim Blazek, leto 02
 */

#include <string.h>
#include <grass/raster.h>
#include <grass/glocale.h>

#include "colortable.h"
#include "local_proto.h"

#define LEFT 0
#define RIGHT 1
#define LOWER 0
#define UPPER 1
#define CENTER 2

#define NSTEPS 3
#define NNSTEP 4		/* number of nice steps */

int PS_fcolortable(void)
{
    char buf[512], *ch, *units;
    int i, k;
    int R, G, B;
    DCELL dmin, dmax, val;
    double t, l, r;		/* legend top, left, right */
    double x1, x2, y, dy, fontsize, xu, yu;
    double col_width;
    double width;		/* width of legend in map units */
    double height;		/* width of legend in map units */
    double cwidth;		/* width of one color line */
    double lwidth;		/* line width - frame, ... */
    double step;		/* step between two values */
    int ncols, cur_step, ddig;
    double nice_steps[NNSTEP] = { 1.0, 2.0, 2.5, 5.0 };	/* nice steps */
    struct Colors colors;
    struct FPRange range;
    double ex, cur_d, cur_ex;
    int do_color;
    double grey_color_val, margin;
    int max_label_length = 0, label_posn, label_xref, label_yref;

    /* let user know what's happenning */
    G_message(_("Creating color table for <%s in %s>..."),
	      ct.name, ct.mapset);

    /* Get color range */
    if (Rast_read_fp_range(ct.name, ct.mapset, &range) == -1) {
	G_warning(_("Range information not available (run r.support)"));
	return 1;
    }

    Rast_get_fp_range_min_max(&range, &dmin, &dmax);

    /* override if range command is set */
    if (ct.range_override) {
	dmin = ct.min;
	dmax = ct.max;
    }

    if (dmin == dmax) {		/* if step==0 all sorts of infinite loops and DIV by 0 errors follow */
	G_warning(_("A floating point colortable must contain a range of values"));
	return 1;
    }

    if (Rast_read_colors(ct.name, ct.mapset, &colors) == -1)
	G_warning(_("Unable to read colors for colorbar"));

    do_color = (PS.grey == 0 && PS.level == 2);

    /* set font */
    fontsize = (double)ct.fontsize;
    fprintf(PS.fp, "(%s) FN %.1f SF\n", ct.font, fontsize);

    /* set colortable location,  */
    /* if height and width are not given, calculate defaults */
    if (ct.width <= 0)
	ct.width = 2 * ct.fontsize / 72.0;
    if (ct.height <= 0)
	ct.height = 10 * ct.fontsize / 72.0;

    dy = 1.5 * fontsize;

    G_debug(3, "pwidth = %f pheight = %f", PS.page_width, PS.page_height);
    G_debug(3, "ct.width = %f ct.height = %f", ct.width, ct.height);
    G_debug(3, "ct.x = %f ct.y = %f", ct.x, ct.y);

    /* reset position to get at least something in BBox */
    if (ct.y < PS.top_marg) {	/* higher than top margin */
	G_warning(_("Colorbar y location beyond page margins. Adjusting."));
	ct.y = PS.top_marg + 0.1;
    }
    else if (ct.y > PS.page_height - PS.bot_marg) {
	/* lower than bottom margin - simply move one inch up from bottom margin */
	G_warning(_("Colorbar y location beyond page margins. Adjusting."));
	ct.y = PS.page_height - PS.bot_marg - 1;
    }
    t = 72.0 * (PS.page_height - ct.y);

    if (ct.x < PS.left_marg) {
	G_warning(_("Colorbar x location beyond page margins. Adjusting."));
	ct.x = PS.left_marg + 0.1;
    }
    else if (ct.x > PS.page_width - PS.right_marg) {
	/* move 1 inch to the left from right marg */
	G_warning(_("Colorbar x location beyond page margins. Adjusting."));
	ct.x = PS.page_width - PS.right_marg - 1;
    }
    l = 72.0 * ct.x;

    G_debug(3, "corrected ct.x = %f ct.y = %f", ct.x, ct.y);

    r = l + 72.0 * ct.width;
    col_width = ct.width / (double)ct.cols;

    /* Calc number of colors to print */
    width = 72.0 * ct.width;
    height = 72.0 * ct.height;
    cwidth = 0.1;
    ncols = (int)height / cwidth;
    step = (dmax - dmin) / (ncols - 1);
    lwidth = width > 72 ? 0.02 * 72 : 0.02 * width;

    /* Print color band */
    y = t;
    x1 = l;
    x2 = x1 + width;
    fprintf(PS.fp, "%.8f W\n", cwidth);

    for (i = 0; i < ncols; i++) {
	/*      val = dmin + i * step;   flip */
	val = dmax - i * step;
	Rast_get_d_color(&val, &R, &G, &B, &colors);

	if (do_color)
	    fprintf(PS.fp, "%.3f %.3f %.3f C\n", (double)R / 255.,
		    (double)G / 255., (double)B / 255.);
	else {
	    grey_color_val =
		(.3 * (double)R + .59 * (double)G + .11 * (double)B) / 255.;
	    fprintf(PS.fp, "%.3f setgray\n", grey_color_val);
	}

	fprintf(PS.fp, "NP\n");
	fprintf(PS.fp, "%f %f M\n", x1, y);
	fprintf(PS.fp, "%f %f LN\n", x2, y);
	fprintf(PS.fp, "D\n");
	y -= cwidth;
    }

    /* Frame around */
    fprintf(PS.fp, "NP\n");
    set_ps_color(&ct.color);
    fprintf(PS.fp, "%.8f W\n", lwidth);
    fprintf(PS.fp, "%f %f %f %f B\n", x1,
	    t - (ncols - 1) * cwidth - (cwidth + lwidth) / 2, x2,
	    t + (cwidth + lwidth) / 2);
    fprintf(PS.fp, "D\n");

    /* Print labels */
    /* maximum number of parts we can divide into */
    k = (ncols - 1) * cwidth / dy;
    /* step in values for labels */
    step = (dmax - dmin) / k;

    /* raw step - usually decimal number with many places, not nice */
    /* find nice step and first nice value for label: nice steps are 
     * 1, 2, 2.5 or 5 * 10^n,
     * we need nice step which is first >= raw step, we take each nice 
     * step and find 'n' and then compare differences */

    for (i = 0; i < NNSTEP; i++) {
	/* smalest n for which nice step >= raw step */
	if (nice_steps[i] <= step) {
	    ex = 1;
	    while (nice_steps[i] * ex < step)
		ex *= 10;
	}
	else {
	    ex = 0.1;
	    while (nice_steps[i] * ex > step)
		ex *= 0.1;
	    ex *= 10;
	}
	if (i == 0 || (nice_steps[i] * ex - step) < cur_d) {
	    cur_step = i;
	    cur_d = nice_steps[i] * ex - step;
	    cur_ex = ex;
	}
    }
    step = nice_steps[cur_step] * cur_ex;

    /* Nice first value: first multiple of step >= dmin */
    k = dmin / step;
    val = k * step;
    if (val < dmin)
	val += step;

    x1 = l + width + 0.1;
    if (ct.tickbar)		/* switch to draw tic all the way through bar */
	x2 = x1 - width;
    else {
	x2 = x1 + 0.37 * width;
	if (width > 36)
	    x2 = x1 + 0.37 * 36;
    }    

    /* do nice label: we need so many decimal places to hold all step decimal digits */
    if (step > 100) {		/* nice steps do not have > 2 digits, important separate, otherwise */
	ddig = 0;		/* we can get something like 1000000.00000000765239 */
    }
    else {
	sprintf(buf, "%.10f", step);
	k = strlen(buf) - 1;
	while (buf[k] == '0')
	    k--;
	k = k - (int)(strchr(buf, '.') - buf);
	ddig = k;
    }

    fprintf(PS.fp, "%.8f W\n", lwidth);
    while (val <= dmax) {
	/*      y = t - (val - dmin) * height / (dmax - dmin) ;  flip */
	y = t - (dmax - val) * height / (dmax - dmin);
	fprintf(PS.fp, "NP\n");
	fprintf(PS.fp, "%f %f M\n", x1, y);
	fprintf(PS.fp, "%f %f LN\n", x2, y);
	fprintf(PS.fp, "D\n");

	sprintf(buf, "%f", val);
	ch = (char *)strchr(buf, '.');
	ch += ddig;
	if (ddig > 0)
	    ch++;
	*ch = '\0';

	if (ct.tickbar)		/* switch to draw tic all the way through bar */
	    fprintf(PS.fp, "(%s) %f %f MS\n", buf, x1 + 0.2 * fontsize,
		    y - 0.35 * fontsize);
	else
	    fprintf(PS.fp, "(%s) %f %f MS\n", buf, x2 + 0.2 * fontsize,
		    y - 0.35 * fontsize);

	if(strlen(buf) > max_label_length)
	    max_label_length = strlen(buf);

	val += step;
    }


    /* print units label, if present */
    units = Rast_read_units(ct.name, ct.mapset);
    if (!units)
        units = "";

    if(strlen(units)) {
	margin = 0.2 * fontsize;
	if (margin < 2)
	    margin = 2;
	fprintf(PS.fp, "/mg %.1f def\n", margin);

	/* Hint from Glynn:
	   You can use the `stringwidth` operator to obtain the width of a string
	   (the distance that the current point will move if you `show` the string).
	   If the string is short, you can obtain the bounding box with a combination
	   of the `charpath` and `pathbbox` operators (if the string is long,
	   `charpath` may overflow the maximum path length). */

	/* select label position */
	label_posn = 3;
	/*  1 2
	      3
	    5 4 */
	switch (label_posn) {
	case 1:
	    /* above the tick numbers */
	    xu = x1;
	    yu = t + 0.05*72;
	    label_xref = LEFT;
	    label_yref = LOWER;
	    break;
	case 2:
	    /* directly above the tick numbers */
	    if (ct.tickbar)
		xu = x1 + 0.2 * fontsize;
	    else
		xu = x2 + 0.2 * fontsize;
	    yu = t + 0.05*72;
	    label_xref = LEFT;
	    label_yref = LOWER;
	    break;
	case 3:
	    /* to the right of the tick numbers */
	    xu = 0.15*72 + max_label_length*fontsize*0.5;
	    if (ct.tickbar)
		xu += x1;
	    else
		xu += x2;
	    yu = t - height/2;
	    label_xref = LEFT;
	    label_yref = CENTER;
	    break;
	case 4:
	    /* directly below the tick numbers */
	    if (ct.tickbar)
		xu = x1 + 0.2 * fontsize;
	    else
		xu = x2 + 0.2 * fontsize;
	    yu = t - height - 0.05*72;
	    label_xref = LEFT;
	    label_yref = UPPER;
	    break;
	case 5:
	    /* below the tick numbers */
	    xu = x1;
	    yu = t - height - 0.05*72;
	    label_xref = LEFT;
	    label_yref = UPPER;
	    break;
	}

	text_box_path( xu, yu, label_xref, label_yref, units, fontsize, 0);
	set_rgb_color(BLACK); 
	fprintf(PS.fp, "TIB\n");
    }

    Rast_free_colors(&colors);

    return 0;
}
