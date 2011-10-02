#include <stdio.h>

#include <grass/gis.h>
#include <grass/glocale.h>

#include "parser_local_proto.h"

static void print_escaped_for_html(FILE * f, const char *str);


/*!
  \brief Print module usage description in HTML format.
*/
void G__usage_html(void)
{
    struct Option *opt;
    struct Flag *flag;
    const char *type;
    int new_prompt = 0;

    new_prompt = G__uses_new_gisprompt();

    if (!st->pgm_name)		/* v.dave && r.michael */
	st->pgm_name = G_program_name();
    if (!st->pgm_name)
	st->pgm_name = "??";

    fprintf(stdout,
	    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
    fprintf(stdout, "<html>\n<head>\n");
    fprintf(stdout, "<title>GRASS GIS manual: %s</title>\n", st->pgm_name);
    fprintf(stdout,
	    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n");
    fprintf(stdout,
	    "<link rel=\"stylesheet\" href=\"grassdocs.css\" type=\"text/css\">\n");
    fprintf(stdout, "</head>\n");
    fprintf(stdout, "<body bgcolor=\"white\">\n\n");
    fprintf(stdout,
	    "<img src=\"grass_logo.png\" alt=\"GRASS logo\"><hr align=center size=6 noshade>\n\n");
    fprintf(stdout, "<h2>%s</h2>\n", _("NAME"));
    fprintf(stdout, "<em><b>%s</b></em> ", st->pgm_name);

    if (st->module_info.label || st->module_info.description)
	fprintf(stdout, " - ");

    if (st->module_info.label)
	fprintf(stdout, "%s<BR>\n", st->module_info.label);

    if (st->module_info.description)
	fprintf(stdout, "%s\n", st->module_info.description);


    fprintf(stdout, "<h2>%s</h2>\n", _("KEYWORDS"));
    if (st->module_info.keywords) {
	G__print_keywords(stdout, NULL);
	fprintf(stdout, "\n");
    }
    fprintf(stdout, "<h2>%s</h2>\n", _("SYNOPSIS"));
    fprintf(stdout, "<b>%s</b><br>\n", st->pgm_name);
    fprintf(stdout, "<b>%s help</b><br>\n", st->pgm_name);

    fprintf(stdout, "<b>%s</b>", st->pgm_name);



    /* print short version first */
    if (st->n_flags) {
	flag = &st->first_flag;
	fprintf(stdout, " [-<b>");
	while (flag != NULL) {
	    fprintf(stdout, "%c", flag->key);
	    flag = flag->next_flag;
	}
	fprintf(stdout, "</b>] ");
    }
    else
	fprintf(stdout, " ");

    if (st->n_opts) {
	opt = &st->first_option;

	while (opt != NULL) {
	    if (opt->key_desc != NULL)
		type = opt->key_desc;
	    else
		switch (opt->type) {
		case TYPE_INTEGER:
		    type = "integer";
		    break;
		case TYPE_DOUBLE:
		    type = "float";
		    break;
		case TYPE_STRING:
		    type = "string";
		    break;
		default:
		    type = "string";
		    break;
		}
	    if (!opt->required)
		fprintf(stdout, " [");
	    fprintf(stdout, "<b>%s</b>=<em>%s</em>", opt->key, type);
	    if (opt->multiple) {
		fprintf(stdout, "[,<i>%s</i>,...]", type);
	    }
	    if (!opt->required)
		fprintf(stdout, "] ");

	    opt = opt->next_opt;
	    fprintf(stdout, " ");
	}
    }
    if (new_prompt)
	fprintf(stdout, " [--<b>overwrite</b>] ");

    fprintf(stdout, " [--<b>verbose</b>] ");
    fprintf(stdout, " [--<b>quiet</b>] ");

    fprintf(stdout, "\n");


    /* now long version */
    fprintf(stdout, "\n");
    if (st->n_flags || new_prompt) {
	flag = &st->first_flag;
	fprintf(stdout, "<h3>%s:</h3>\n", _("Flags"));
	fprintf(stdout, "<DL>\n");
	while (st->n_flags && flag != NULL) {
	    fprintf(stdout, "<DT><b>-%c</b></DT>\n", flag->key);

	    if (flag->label) {
		fprintf(stdout, "<DD>");
		fprintf(stdout, "%s", flag->label);
		fprintf(stdout, "</DD>\n");
	    }

	    if (flag->description) {
		fprintf(stdout, "<DD>");
		fprintf(stdout, "%s", flag->description);
		fprintf(stdout, "</DD>\n");
	    }

	    flag = flag->next_flag;
	    fprintf(stdout, "\n");
	}
	if (new_prompt) {
	    fprintf(stdout, "<DT><b>--overwrite</b></DT>\n");
	    fprintf(stdout, "<DD>%s</DD>\n",
		    _("Allow output files to overwrite existing files"));
	}

	fprintf(stdout, "<DT><b>--verbose</b></DT>\n");
	fprintf(stdout, "<DD>%s</DD>\n", _("Verbose module output"));

	fprintf(stdout, "<DT><b>--quiet</b></DT>\n");
	fprintf(stdout, "<DD>%s</DD>\n", _("Quiet module output"));

	fprintf(stdout, "</DL>\n");
    }

    fprintf(stdout, "\n");
    if (st->n_opts) {
	opt = &st->first_option;
	fprintf(stdout, "<h3>%s:</h3>\n", _("Parameters"));
	fprintf(stdout, "<DL>\n");

	while (opt != NULL) {
	    /* TODO: make this a enumeration type? */
	    if (opt->key_desc != NULL)
		type = opt->key_desc;
	    else
		switch (opt->type) {
		case TYPE_INTEGER:
		    type = "integer";
		    break;
		case TYPE_DOUBLE:
		    type = "float";
		    break;
		case TYPE_STRING:
		    type = "string";
		    break;
		default:
		    type = "string";
		    break;
		}
	    fprintf(stdout, "<DT><b>%s</b>=<em>%s", opt->key, type);
	    if (opt->multiple) {
		fprintf(stdout, "[,<i>%s</i>,...]", type);
	    }
	    fprintf(stdout, "</em></DT>\n");

	    if (opt->label) {
		fprintf(stdout, "<DD>");
		print_escaped_for_html(stdout, opt->label);
		fprintf(stdout, "</DD>\n");
	    }
	    if (opt->description) {
		fprintf(stdout, "<DD>");
		print_escaped_for_html(stdout, opt->description);
		fprintf(stdout, "</DD>\n");
	    }

	    if (opt->options) {
		fprintf(stdout, "<DD>%s: <em>", _("Options"));
		print_escaped_for_html(stdout, opt->options);
		fprintf(stdout, "</em></DD>\n");
	    }

	    if (opt->def) {
		fprintf(stdout, "<DD>%s: <em>", _("Default"));
		print_escaped_for_html(stdout, opt->def);
		fprintf(stdout, "</em></DD>\n");
	    }

	    if (opt->descs) {
		int i = 0;

		while (opt->opts[i]) {
		    if (opt->descs[i]) {
			fprintf(stdout, "<DD><b>");
			print_escaped_for_html(stdout, opt->opts[i]);
			fprintf(stdout, "</b>: ");
			print_escaped_for_html(stdout, opt->descs[i]);
			fprintf(stdout, "</DD>\n");
		    }
		    i++;
		}
	    }

	    opt = opt->next_opt;
	    fprintf(stdout, "\n");
	}
	fprintf(stdout, "</DL>\n");
    }

    fprintf(stdout, "</body>\n</html>\n");
}


/*!
 * \brief Format text for HTML output
 */
#define do_escape(c,escaped) case c: fputs(escaped,f);break
static void print_escaped_for_html(FILE * f, const char *str)
{
    const char *s;

    for (s = str; *s; s++) {
	switch (*s) {
	    do_escape('&', "&amp;");
	    do_escape('<', "&lt;");
	    do_escape('>', "&gt;");
	    do_escape('\n', "<br>");
	default:
	    fputc(*s, f);
	}
    }
}
#undef do_escape
