%{
#include <stdio.h>
#include <stdlib.h>
/* all parser must define this variable */

#define yyv EDLv

extern int  EDLlex();
extern void EDLerror ();
extern FILE *FileDesc[];
extern int numFileDesc;
extern FILE *EDLin;
/* extern int edl_must_execute(); */

#include <edl_rule.h>

%}
%token USES
%token TEMPLATE
%token ADDTOTEMPLATE
%token CLEARTEMPLATE
%token END
%token SET
%token UNSET
%token INSTRDEFINED
%token INSTRNOTDEFINED
%token IFDEFINED
%token IFNOTDEFINED
%token IFFILE
%token IFNOTFILE
%token INSTRFILE
%token INSTRNOTFILE
%token IF
%token THEN
%token ELSE
%token ENDIF
%token COUT
%token IS
%token EQ
%token NEQ
%token LOGOR
%token LOGAND
%token SEPARATOR
%token ASTRING
%token AFILE
%token OPENLIB
%token CLOSELIB
%token CALLLIB
%token APPLY
%token WRITE
%token CLOSE
%token VERBOSEON
%token VERBOSEOFF
%token PLUSEQUAL
%token <str> STR IDENT TEMPDEF VAR 
%token <ope> EQ NEQ LOGOR LOGAND
%type  <ope> logoperator expr_operator
%type  <str> tempdefs identifier filename ifdefcond
%union {
 edlstring str;
 int       ope;
}

%%
statements: statement
           | statements statement;

statement:  
          | use_dec   
	  | addtotemplate_dec 
	  | cleartemplate_dec 
	  | template_dec 
	  | ifdefined_dec
	  | ifnotdefined_dec
	  | if_dec       
          | set_dec      
          | unset_dec      
          | cout_dec
	  | string_dec
          | file_dec
	  | iffileexists_dec
  	  | iffilenotexists_dec
	  | openlib_dec
	  | closelib_dec
          | apply_dec
	  | calllib_dec
          | write_dec
          | close_dec
	  | verboseon_dec
          | verboseoff_dec
	;

use_dec:  USES STR SEPARATOR {  edl_uses($2); }
	| USES VAR SEPARATOR {  edl_uses_var($2); }
	;

verboseoff_dec: VERBOSEOFF SEPARATOR
	;

verboseon_dec: VERBOSEON SEPARATOR
	;

template_dec: templatehead identifier { edl_create_template($2); } '(' listvars ')' IS tempdefs END SEPARATOR { edl_end_template(); }
	;

templatehead: TEMPLATE
	;

addtotemplate_dec: ADDTOTEMPLATE identifier { edl_set_template($2); } IS tempdefs END SEPARATOR { edl_end_template(); }
	;

cleartemplate_dec: CLEARTEMPLATE identifier SEPARATOR { edl_clear_template($2); }
	;

identifier: IDENT
	;

tempdefs:  TEMPDEF { edl_add_to_template($1); }
         | tempdefs TEMPDEF { edl_add_to_template($2); }
	;

apply_dec: APPLY VAR '=' IDENT { edl_apply_template($4); } SEPARATOR { edl_end_apply($2); }
	;

listvars:  
	 | VAR { edl_add_to_varlist($1);  }
	 | listvars ',' VAR	{ edl_add_to_varlist($3); }
	;

iffileexists_dec:    IFFILE       '(' VAR ')'  {edl_fileexist_var($3); } if_follow
	|            IFFILE       '(' STR ')'  {edl_fileexist($3); }     if_follow
	;


iffilenotexists_dec: IFNOTFILE    '(' VAR ')'  {edl_filenotexist_var($3); }   if_follow
	|            IFNOTFILE    '(' STR ')'  {edl_filenotexist($3); }       if_follow
	;

ifdefined_dec:       IFDEFINED    '(' ifdefcond ')'  {edl_isvardefined($3); }    THEN statements if_end_dec
	;
ifnotdefined_dec:    IFNOTDEFINED '(' ifdefcond ')'  {edl_isvarnotdefined($3); } THEN statements if_end_dec
	;
if_dec:              IF           '(' conditions ')'                             THEN statements if_end_dec
	;

if_follow: THEN statements if_end_dec
	;

ifdefcond: VAR
	 | IDENT
	;


if_end_dec: ENDIF SEPARATOR { edl_clear_execution_status(); }
	|   ELSE  { edl_else_execution_status(); } statements ENDIF SEPARATOR { edl_clear_execution_status(); }
	;

set_dec: SET VAR '=' STR SEPARATOR { edl_set_var($2,$4); edlstring_free($2); edlstring_free($4);}
       | SET VAR '=' VAR SEPARATOR { edl_set_varvar($2,$4); edlstring_free($2); edlstring_free($4);}
       | SET VAR '=' '[' VAR ']' SEPARATOR { edl_set_varevalvar($2,$5); edlstring_free($2); edlstring_free($5);}
       | SET '[' VAR ']' '=' STR SEPARATOR { edl_set_pvar($3,$6); edlstring_free($3); edlstring_free($6);}
       | SET '[' VAR ']' '=' VAR SEPARATOR { edl_set_pvarvar($3,$6); edlstring_free($3); edlstring_free($6);}
       | SET '[' VAR ']' '=' '[' VAR ']' SEPARATOR { edl_set_pvarevalvar($3,$7); edlstring_free($3); edlstring_free($7);}
       ;

unset_dec: UNSET VAR SEPARATOR { edl_unset_var($2); edlstring_free($2); }
       | UNSET '[' VAR ']' SEPARATOR { edl_unset_pvar($3); edlstring_free($3); }
       ;

cout_dec: COUT { edl_clear_printlist(); } printlists SEPARATOR { edl_cout(); }
	;

string_dec: ASTRING { edl_clear_printlist(); } string_end_dec
	;

string_end_dec: VAR '=' printlists SEPARATOR { edl_create_string_var($1); }
	|       VAR PLUSEQUAL { edl_printlist_addps_var($1); } printlists SEPARATOR { edl_create_string_var($1); }
	;

printlists:  printlist
           | printlists printlist
	;

printlist:
          | VAR { edl_printlist_add_var($1); }
          | STR { edl_printlist_add_str($1); }
	;


file_dec: AFILE IDENT filename SEPARATOR { edl_open_file($2,$3); }
	;

filename:  VAR  { edl_set_varname(); }
         | STR  { edl_set_str();     }
	;

write_dec: WRITE IDENT VAR SEPARATOR { edl_write_file($2,$3); }
	;

close_dec: CLOSE IDENT SEPARATOR { edl_close_file($2); }
	;

openlib_dec: OPENLIB IDENT SEPARATOR { edl_open_library($2); }
	;

calllib_dec: CALLLIB IDENT '.' IDENT '(' arglists ')' SEPARATOR  { edl_call_procedure_library($2,$4); }
	|    CALLLIB VAR '=' IDENT '.' IDENT '(' arglists ')' SEPARATOR  { edl_call_function_library($4,$6,$2); }
	;

arglists:  arglist
         | arglists ',' arglist
	;

arglist:
          | VAR { edl_arglist_add_var($1); }
          | STR { edl_arglist_add_str($1); }
	;

closelib_dec: CLOSELIB IDENT SEPARATOR { edl_close_library($2); }
	;

conditions: condition                                  { edl_eval_condition(); }
          | conditions condition
	;

condition:  VAR logoperator STR                  { edl_test_condition($1,$2,$3); }
          | condition expr_operator condition    { edl_eval_local_condition($2); }
	  | '(' condition ')'
	  | INSTRDEFINED    '(' ifdefcond ')'  {edl_isvardefinedm($3); }
	  | INSTRNOTDEFINED '(' ifdefcond ')'  {edl_isvarnotdefinedm($3); } 
	  | INSTRFILE    '(' VAR ')'           {edl_fileexist_varm($3); } 
	  | INSTRNOTFILE '(' VAR ')'           {edl_filenotexist_varm($3); }
	  | INSTRFILE    '(' STR ')'           {edl_fileexistm($3); }
	  | INSTRNOTFILE '(' STR ')'           {edl_filenotexistm($3); }
	;

logoperator:  EQ  
         |    NEQ
 	;

expr_operator: LOGAND
         |     LOGOR
	;

%%


