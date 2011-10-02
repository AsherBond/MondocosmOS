
%{
#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#if STDC_HEADERS
# include <string.h>
#else
# ifndef HAVE_STRCHR
#  define strchr index
#  define strrchr rindex
# endif
  char *strchr (), *strrchr ();
# ifndef HAVE_MEMCPY
#  define memcpy(d, s, n) bcopy ((s), (d), (n))
#  define memmove(d, s, n) bcopy ((s), (d), (n))
# endif
#endif  /* STDC_HEADERS */
#include <stdio.h>
#ifdef WNT
# include <io.h>
#endif  /* WNT */

#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif


#define yylval EDLlval
#include <edl_rule.h>
#include <EDL.tab.h>

#ifndef YY_NO_UNPUT
# define YY_NO_UNPUT
#endif  /* YY_NO_UNPUT */

#define MAX_CHAR     256              /* The limit of a identifier.  */
#define MAX_STRING   (MAX_CHAR * 10)  /* The limit of a string.      */
#define MAX_COMMENT  (MAX_CHAR * 300) /* The limit of comment line   */

char  FileName[11][256];
FILE *FileDesc[10];
int   LineStack[10];
YY_BUFFER_STATE EDL_Buffers[10];

int   numFileDesc = -1;

int templateDef = 0;
int EDLlineno;
int VerboseMode = 0;

#ifdef WNT
#define YY_INPUT(buf,result,max_size) \
	if ( (result = fread( (char *) buf, sizeof(char), max_size, yyin)) == 0 ) \
		if(ferror(yyin)) YY_FATAL_ERROR( "input in flex scanner failed" );
#endif



%}


/* The specials keywords */

COMMENT	        [-][-]

/* The identifiers without underscore at begining and end */

IDENTIFIER	[A-Za-z0-9_-]+

/* [A-Za-z][A-Za-z0-9_]*[A-Za-z0-9] */


/* Integer and real */

INTEGER		[+-]?[0-9]+
/* REAL		[+-]?[0-9]+"."[0-9]+([Ee][+-]?[0-9]+)? */

/* Literal, variable and string */
/* [A-Za-z][A-Za-z0-9_]*[A-Za-z0-9] */

LITERAL 	"'"."'"
STRING	        \"[^"\n]*["\n]
VARNAME         "%"{1}([A-Za-z0-9_-]+)
/* TEMPLATEDEF     ^[ \t]*"$" */
TEMPLATEDEF     "$"
/* The LEX directives. */

/* %p		5000 */
/* %a		9000 */
/* %o		10000 */

%%

{COMMENT}.*\n                     {EDLlineno++;}

@verboseon                        { if (edl_must_execute()) { VerboseMode = 1; printf("%d. @verboseon",EDLlineno);} return(VERBOSEON);}
@verboseoff                       { if (edl_must_execute()) { if (VerboseMode) printf("%d. @verboseoff",EDLlineno); VerboseMode = 0;} return(VERBOSEOFF);}
@uses                             { if (VerboseMode && edl_must_execute()) printf("%d. @uses ",EDLlineno); return(USES);    }
@template        { templateDef = 1; if (VerboseMode && edl_must_execute()) printf("%d. @template ",EDLlineno); return(TEMPLATE);}
is                                { if (VerboseMode && edl_must_execute()) printf(" is\n"); return(IS);}
@set                              { if (VerboseMode && edl_must_execute()) printf("%d. @set ",EDLlineno); return(SET); }
@unset                            { if (VerboseMode && edl_must_execute()) printf("%d. @unset ",EDLlineno); return(UNSET); }
@ifdefined                        { if (VerboseMode && edl_must_execute()) printf("%d. @ifdefined ",EDLlineno); return(IFDEFINED); }
@ifnotdefined                     { if (VerboseMode && edl_must_execute()) printf("%d. @ifnotdefined ",EDLlineno); return(IFNOTDEFINED); }
defined                           { if (VerboseMode && edl_must_execute()) printf("%d. defined ",EDLlineno); return(INSTRDEFINED); }
notdefined                           { if (VerboseMode && edl_must_execute()) printf("%d. notdefined ",EDLlineno); return(INSTRNOTDEFINED); }
@iffile                           { if (VerboseMode && edl_must_execute()) printf("%d. @iffile ",EDLlineno); return(IFFILE); }
@ifnotfile                        { if (VerboseMode && edl_must_execute()) printf("%d. @ifnotfile ",EDLlineno); return(IFNOTFILE); }
file                            { if (VerboseMode && edl_must_execute()) printf("%d file ",EDLlineno); return(INSTRFILE); }
notfile                         { if (VerboseMode && edl_must_execute()) printf("%d notfile ",EDLlineno); return(INSTRNOTFILE); }
@if                               { if (VerboseMode && edl_must_execute()) printf("%d. @if ",EDLlineno); return(IF); }
then                              { if (VerboseMode && edl_must_execute()) printf(" then\n"); return(THEN); }
@else                             { if (VerboseMode && edl_must_execute()) printf("@else\n"); return(ELSE); }
@endif                            { if (VerboseMode && edl_must_execute()) printf("%d. @endif",EDLlineno); return(ENDIF); }
@end            { templateDef = 0;  if (VerboseMode && edl_must_execute()) printf("%d. @end",EDLlineno); return(END); }
@cout                             { if (VerboseMode && edl_must_execute()) printf("%d. @cout ",EDLlineno); return(COUT); }
@file                             { if (VerboseMode && edl_must_execute()) printf("%d. @file ",EDLlineno); return(AFILE); }
@string                           { if (VerboseMode && edl_must_execute()) printf("%d. @string ",EDLlineno); return(ASTRING); }
@openlib                          { if (VerboseMode && edl_must_execute()) printf("%d. @openlib ",EDLlineno); return(OPENLIB); }
@closelib                         { if (VerboseMode && edl_must_execute()) printf("%d. @closelib ",EDLlineno); return(CLOSELIB); } 
@call                             { if (VerboseMode && edl_must_execute()) printf("%d. @call ",EDLlineno); return(CALLLIB); }
@apply                            { if (VerboseMode && edl_must_execute()) printf("%d. @apply ",EDLlineno); return(APPLY); }
@write                            { if (VerboseMode && edl_must_execute()) printf("%d. @write ",EDLlineno); return(WRITE); }
@close                            { if (VerboseMode && edl_must_execute()) printf("%d. @close ",EDLlineno); return(CLOSE); }
@addtotemplate                    { if (VerboseMode && edl_must_execute()) printf("%d. @addtotemplate ",EDLlineno); return(ADDTOTEMPLATE); }
@cleartemplate                    { if (VerboseMode && edl_must_execute()) printf("%d. @cleartemplate ",EDLlineno); return(CLEARTEMPLATE); }
"+="                              { if (VerboseMode && edl_must_execute()) printf(" += "); return(PLUSEQUAL); }
==                                { if (VerboseMode && edl_must_execute()) printf(" == "); EDLlval.ope = EQ; return(EQ); }
!=                                { if (VerboseMode && edl_must_execute()) printf(" != "); EDLlval.ope = NEQ; return(NEQ); }
;                                 { if (VerboseMode && edl_must_execute()) printf(";\n"); return(SEPARATOR); }
"||"                              { if (VerboseMode && edl_must_execute()) printf(" || "); EDLlval.ope = LOGOR; return(LOGOR); }
"&&"                              { if (VerboseMode && edl_must_execute()) printf(" && "); EDLlval.ope = LOGAND; return(LOGAND); }

{STRING}                          { /* we kill the quotes from string */
                                    if (VerboseMode && edl_must_execute()) printf("%s",yytext);
                                    EDLlval.str = edl_string(yytext,yyleng);
				    return(STR);
				  }
{VARNAME}                         { EDLlval.str = edl_strdup(EDLtext, yyleng); if (VerboseMode && edl_must_execute()) printf(" %s ",EDLtext); return(VAR);}
{IDENTIFIER}                      { EDLlval.str = edl_strdup(EDLtext, yyleng); if (VerboseMode && edl_must_execute()) printf(" %s ",EDLtext); return(IDENT);}
[ \t]+	                          {/* We don't take care of line feed, space or tabulation */}
{TEMPLATEDEF}.*\n                 { EDLlval.str = edl_strdup(EDLtext,yyleng); if (VerboseMode && edl_must_execute()) printf("%d. %s",EDLlineno,EDLtext); EDLlineno++; return(TEMPDEF);}


[\n]            { EDLlineno++;}
.               { if (VerboseMode && edl_must_execute()) printf("%c",yytext[0]); return yytext[0]; }
%%

void EDL_SetFile()
{
  YY_BUFFER_STATE buf = yy_create_buffer(EDLin,YY_BUF_SIZE);

  EDL_Buffers[numFileDesc] = YY_CURRENT_BUFFER;

  yy_switch_to_buffer(buf);
}


/* we need this for '@uses' clause */
int EDLwrap()
{
  edlstring _currentFile;

  if (numFileDesc < 0) {
    return 1;
  }
  else {
    fclose(EDLin);

    yy_delete_buffer(YY_CURRENT_BUFFER);

    EDLin           = FileDesc[numFileDesc];
    EDLlineno       = LineStack[numFileDesc] + 1;

    _currentFile.str = &(FileName[numFileDesc][0]);
    _currentFile.length = strlen(&(FileName[numFileDesc][0]));
    EDL_SetCurrentFile(_currentFile);

    yy_switch_to_buffer(EDL_Buffers[numFileDesc]);

    numFileDesc--;

    return 0;
  }
}
