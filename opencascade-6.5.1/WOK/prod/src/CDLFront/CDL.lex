/*======================================================*/
/*== CDL lexical analyzer.                              */
/*======================================================*/

%{

#include <string.h>
#include <stdio.h>
#ifdef WNT
# include <io.h>
#else
# include <unistd.h>
#endif  /* WNT */

#include <cdl_defines.hxx>
void add_cpp_comment(int, char*);
void add_documentation(char*);
void add_documentation1(char*);

#define yylval CDLlval
#include <CDL.tab.h>
int CDLlineno;

#ifndef YY_NO_UNPUT
# define YY_NO_UNPUT
#endif  /* YY_NO_UNPUT */

extern void CDLerror ( char* );

%}


/* The specials key words */

COMMENTER	[-][-]
DOCUMENTATION   [-][-][-][ \t]*[P][u][r][p][o][s][e].*[\n]
DOCUMENTATION1  [-]{2,}[ \t]{1,}.*[\n]
FCPLUSPLUS      [-][-][-][C][+][+][ \t]*
CPLUSPLUS       [-][-][-][C][+][+][ \t]*[:][ \t]*
FCPLUSPLUSD     [-][-][-][C][+][+][ \t]*[2][.][0][ \t]*
CPLUSPLUSD      [-][-][-][C][+][+][ \t]*[2][.][0][ \t]*[:][ \t]*
FCPLUSPLUSD1    [-][-][-][C][+][+][ \t]*[2][.][1][ \t]*
CPLUSPLUSD1     [-][-][-][C][+][+][ \t]*[2][.][1][ \t]*[:][ \t]*
OPERATOR        [-][-][-][C][+][+][ \t]*[:][ \t]*[a][l][i][a][s][ \t]*[o][p][e][r][a][t][o][r]
INLINE          [-][-][-][C][+][+][ \t]*[:][ \t]*[i][n][l][i][n][e][ \t]*
DESTRUCTOR      [-][-][-][C][+][+][ \t]*[:][ \t]*[a][l][i][a][s][ \t]*['~'][ \t]*
CONSTREF        [-][-][-][C][+][+][ \t]*[:][ \t]*[r][e][t][u][r][n][ \t]*[c][o][n][s][t][ \t]*['&'][ \t]*
CONSTRET        [-][-][-][C][+][+][ \t]*[:][ \t]*[r][e][t][u][r][n][ \t]*[c][o][n][s][t][ \t]*
REF             [-][-][-][C][+][+][ \t]*[:][ \t]*[r][e][t][u][r][n][ \t]*['&'][ \t]*
HARDALIAS       [-][-][-][C][+][+][ \t]*[:][ \t]*[a][l][i][a][s][ \t]*\"(\\\"|[^"])*\"[ \t]*
FUNCTIONCALL    [-][-][-][C][+][+][ \t]*[:][ \t]*[f][u][n][c][t][i][o][n][ \t]*[c][a][l][l][ \t]*
PTR             [-][-][-][C][+][+][ \t]*[:][ \t]*[r][e][t][u][r][n][ \t]*['*'][ \t]* 
CONSTPTR        [-][-][-][C][+][+][ \t]*[:][ \t]*[r][e][t][u][r][n][ \t]*[c][o][n][s][t][ \t]*['*'][ \t]* 

/* The identifiers without underscore at begining and end */

IDENTIFIER	[A-Za-z][A-Za-z0-9_]*[A-Za-z0-9]
JAVAIDENTIFIER	[A-Za-z][A-Za-z0-9_.]*[A-Za-z0-9]


/* Integer and real */

INTEGER		[+-]?[0-9]+
REAL		[+-]?[0-9]+"."[0-9]+([Ee][+-]?[0-9]+)?

/* Literal and string */

LITERAL 	"'"."'"
STRING		\"(\\\"|[^"])*\"

/* The LEX directives. */

/* %p		5000  */
/* %a		9000  */
/* %o		10000 */

/* The rules section execfile	{ return(execfile); }*/
%s DOC_BLOCK 

%%

{DOCUMENTATION}     { add_documentation(CDLtext); BEGIN(DOC_BLOCK);  CDLlineno++; }
<DOC_BLOCK>{DOCUMENTATION1}    { add_documentation1(CDLtext);  CDLlineno++; }
{REF}\n             { add_cpp_comment(CDL_REF,CDLtext); CDLlineno++; BEGIN(0);}
{CONSTREF}\n        { add_cpp_comment(CDL_CONSTREF,CDLtext); CDLlineno++; BEGIN(0); }
{PTR}\n             { add_cpp_comment(CDL_PTR,CDLtext); CDLlineno++; BEGIN(0); }
{CONSTPTR}\n        { add_cpp_comment(CDL_CONSTPTR,CDLtext); CDLlineno++; BEGIN(0); }
{CONSTRET}\n        { add_cpp_comment(CDL_CONSTRET,CDLtext); CDLlineno++; BEGIN(0); }
{DESTRUCTOR}\n      { add_cpp_comment(CDL_DESTRUCTOR,CDLtext); CDLlineno++;  BEGIN(0);}
{INLINE}\n          { add_cpp_comment(CDL_INLINE,CDLtext); CDLlineno++;  BEGIN(0);}
{OPERATOR}.*\n      { add_cpp_comment(CDL_OPERATOR,CDLtext); CDLlineno++; BEGIN(0); }
{HARDALIAS}\n       { add_cpp_comment(CDL_HARDALIAS,CDLtext); CDLlineno++; BEGIN(0); }
{FUNCTIONCALL}\n    { add_cpp_comment(CDL_FUNCTIONCALL,CDLtext); CDLlineno++; BEGIN(0); }
{CPLUSPLUSD}.*\n    { CDLlineno++; CDLerror("C++2.0 directive no more supported."); BEGIN(0); } 
{FCPLUSPLUSD}.*\n   { CDLlineno++; CDLerror("C++2.0 directive no more supported (':' missing).");  BEGIN(0);} 
{CPLUSPLUSD1}.*\n   { CDLlineno++; CDLerror("C++2.1 directive no more supported."); BEGIN(0);} 
{FCPLUSPLUSD1}.*\n  { CDLlineno++; CDLerror("C++2.1 directive no more supported (':' missing).");  BEGIN(0);} 
{CPLUSPLUS}\n       { CDLlineno++; CDLerror("Empty C++ directive."); BEGIN(0); } 
{FCPLUSPLUS}.*\n    { CDLlineno++; CDLerror("C++ directive without ':'.");  BEGIN(0);} 
{COMMENTER}.*\n	    { CDLlineno++; BEGIN(0); }


alias		{ BEGIN(0); return(alias); }
any		{ BEGIN(0); return(any); }
asynchronous    { BEGIN(0); return(asynchronous); }
as		{ BEGIN(0); return(as); }
class		{ BEGIN(0); return(class); }
client          { BEGIN(0); return(client); }
component       { BEGIN(0); return(component); }
deferred  	{ BEGIN(0); return(deferred); }
schema  	{ BEGIN(0); return(schema); }
end		{ BEGIN(0); return(end); }
engine		{ BEGIN(0); return(engine); }
enumeration	{ BEGIN(0); return(enumeration); }
exception	{ BEGIN(0); return(exception); }
executable	{ BEGIN(0); return(executable); }
fields		{ BEGIN(0); return(fields); }
friends		{ BEGIN(0); return(friends); }
from		{ BEGIN(0); return(CDL_from); }
generic		{ BEGIN(0); return(generic); }
immutable	{ BEGIN(0); return(immutable); }
imported	{ BEGIN(0); return(imported); }
in		{ BEGIN(0); return(in); }
inherits	{ BEGIN(0); return(inherits); }
instantiates	{ BEGIN(0); return(instantiates); }
interface	{ BEGIN(0); return(interface); }
is		{ BEGIN(0); return(is); }
like		{ BEGIN(0); return(like); }
me		{ BEGIN(0); return(me); }
mutable		{ BEGIN(0); return(mutable); }
myclass		{ return(myclass); }
out		{ return(out); }
package		{ return(package); }
pointer		{ return(pointer); }
private		{ return(private); }
primitive	{ return(primitive); }
protected	{ return(protected); }
raises 		{ return(raises); }
redefined	{ return(redefined); }
returns		{ return(returns); }
static		{ return(statiC); }
to 		{ return(CDL_to); }
uses		{ return(uses); }
virtual         { return(virtual); }
library	        { return(library); }
external	{ return(external); }
as[ \t]*[c][+][+]       { BEGIN(0); return(cpp); }
as[ \t]*c	{ return(krc); }
as[ \t]*fortran         { return(fortran); }
as[ \t]*object          { return(object); }

{IDENTIFIER} |
[A-Za-z]	{ strncpy(CDLlval.str,CDLtext,MAX_CHAR);
		  return(IDENTIFIER); }

{JAVAIDENTIFIER} { strncpy(CDLlval.str,CDLtext,MAX_CHAR);
		   return(JAVAIDENTIFIER); }

{INTEGER}	{ strncpy (CDLlval.str,CDLtext,MAX_CHAR);
		  return(INTEGER); }

{REAL}		{ strncpy(CDLlval.str,CDLtext,MAX_CHAR);
		  return(REAL); }


{LITERAL}	{ strncpy(CDLlval.str,CDLtext,MAX_CHAR);
		  return(LITERAL); }

{STRING}	{ strncpy(CDLlval.str,CDLtext,MAX_STRING);
		  return(STRING) ;}

;		{ return(';'); }
:		{ return(':'); }
"("		{ return('('); }
")"		{ return(')'); }
","		{ return(','); }
"["		{ return('['); }
"]"		{ return(']'); }
"="		{ return('='); }

[ \t]		{ /* We don't take care of line feed, space or tabulation */ }
[\n]            { CDLlineno++;  BEGIN(0);}
.		{ return(INVALID); }

%%
/*
static char comment[MAX_COMMENT + 1];
static int  comment_nb = 0;
static int  new_comment = 0;

 Returns the last identifier 

static Comment()
{
  int size;
  size = strlen(CDLtext);
  if(comment_nb <= MAX_COMMENT - (size+1)) {



     strcpy(&comment[comment_nb],CDLtext);
     comment_nb += size;
     new_comment = 1;
     cout << comment << endl;
  }
}

 Returns the last identifier 
static EndComment()
{
  int size;
  size = strlen(ENDOFCOMMENT);
  if(new_comment && (comment_nb <= MAX_COMMENT - (size+1))) {



     strcpy(&comment[comment_nb], ENDOFCOMMENT);
     comment_nb += size;
     new_comment = 0;
  }
}
*/

/* Returns the last identifier */

char* YYident(){
 return NULL;
}

/* Returns the last integer */

char* YYinteger(){
 return NULL;
}

/* Returns the last real */

char* YYreal(){
 return NULL;
}

/* Returns the last literal */

char* YYliteral(){
 return NULL;
}

/* Returns the last String */

char* YYstring(){
 return NULL;
}

/* Returns the last comment 

char* YYcomment(){

	comment[comment_nb] = 0;
	comment_nb = 0;     	
	return(comment);
}
*/

int CDLwrap()
{
 return 1;
}

