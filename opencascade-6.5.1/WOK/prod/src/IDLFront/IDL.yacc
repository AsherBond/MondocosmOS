/*
 * idl.yy - YACC grammar for IDL 1.1
 */

/* Declarations */

%{
#include <stdio.h>
#include <stdlib.h>
#define yyv IDLv
#if (defined(apollo) || defined(hpux)) && defined(__cplusplus)
extern	"C" int IDLwrap();
#endif	


extern void IDL_SetIdentifier            ( char* );
extern void IDL_InterfaceDeclaration     ( void  );
extern void IDL_InterfaceDefinitionBegin ( void  );
extern void IDL_InterfaceDefinitionEnd   ( void  );

extern int  IDLlex   ( void  );
extern void IDLerror ( char* );

%}

/*
 * Declare the type of values in the grammar
 */

/*
 * Token types: These are returned by the lexer
 */

/*
%token <strval>	IDENTIFIER
*/

%token 	<str> IDENTIFIER

%token		CONST
%token		MODULE
%token		INTERFACE
%token		TYPEDEF
%token		LONG
%token		SHORT
%token		UNSIGNED
%token		DOUBLE
%token		FLOAT
%token		CHAR
%token		WCHAR
%token		OCTET
%token		BOOLEAN
%token		ANY
%token		STRUCT
%token		UNION
%token		SWITCH
%token		ENUM
%token		SEQUENCE
%token		STRING
%token		WSTRING
%token		EXCEPTION
%token		CASE
%token		DEFAULT
%token		READONLY
%token		ATTRIBUTE
%token		ONEWAY
%token		IDEMPOTENT
%token		VOID
%token		IN
%token		OUT
%token		INOUT
%token		RAISES
%token		CONTEXT
%token 		POSTPROCESSOR
%token INTEGER_LITERAL
%token STRING_LITERAL
%token CHARACTER_LITERAL
%token FLOATING_PT_LITERAL

/*
%token <ival>	INTEGER_LITERAL
%token <sval>	STRING_LITERAL
%token <cval>	CHARACTER_LITERAL
%token <dval>	FLOATING_PT_LITERAL
*/

%token		TRUETOK
%token		FALSETOK

/*
%token <strval>	SCOPE_DELIMITOR
*/

%token 	SCOPE_DELIMITOR
%token	LEFT_SHIFT
%token	RIGHT_SHIFT

/*
 * These are production names:
 

%type <dcval>	type_spec simple_type_spec constructed_type_spec
%type <dcval>	template_type_spec sequence_type_spec string_type_spec
%type <dcval>	struct_type enum_type switch_type_spec union_type
%type <dcval>	array_declarator op_type_spec seq_head wstring_type_spec

%type <idlist>	scoped_name
%type <slval>	opt_context at_least_one_string_literal
%type <slval>	string_literals

%type <nlval>	at_least_one_scoped_name scoped_names inheritance_spec
%type <nlval>	opt_raises

%type <elval>	at_least_one_array_dim array_dims

%type <llval>	at_least_one_case_label case_labels

%type <dlval>	at_least_one_declarator declarators

%type <ihval>	interface_header

%type <exval>	expression const_expr or_expr xor_expr and_expr shift_expr
%type <exval>	add_expr mult_expr unary_expr primary_expr literal
%type <exval>	positive_int_expr array_dim

%type <ulval>	case_label

%type <ffval>	element_spec

%type <etval>	const_type integer_type char_type boolean_type
%type <etval>	floating_pt_type any_type signed_int
%type <etval>	unsigned_int base_type_spec octet_type

%type <dival>	direction

%type <ofval>	opt_op_attribute

%type <deval>	declarator simple_declarator complex_declarator

%type <bval>	opt_readonly

%type <idval>	interface_decl id
*/
%union {
 char str[256];
 double dval;
 int ival;
 char cval;
}

%%

/*
 * Production starts here
 */
start :	definitions ;

definitions
	: definition definitions
	| /* empty */
	;

definition
	: POSTPROCESSOR
	| type_dcl
	  ';'
	| const_dcl
	  ';'
	| exception
	  ';'
	| interface_def
	  ';'
	| module
	  ';'
	| error
	';'
	{
	  yyerrok;
        }
	;

module	: MODULE IDENTIFIER '{' definitions  '}'  ;

interface_def
	: interface
	| forward
	;

interface :
	interface_header { IDL_InterfaceDefinitionBegin(); } '{' exports '}' { IDL_InterfaceDefinitionEnd(); }
	;

interface_decl:
	 INTERFACE id { IDL_InterfaceDeclaration(); }
	;

interface_header :
	interface_decl inheritance_spec
	;

inheritance_spec
	: ':'
	  at_least_one_scoped_name
	| /* EMPTY */
	;

exports
	: exports export
	| /* EMPTY */
	;

export
	: type_dcl ';'
	| const_dcl ';'
	| exception ';'
	| attribute ';'
	| operation ';'
	| error ';'
	;

at_least_one_scoped_name :
	scoped_name scoped_names
	;

scoped_names
	: scoped_names ','  scoped_name
	| /* EMPTY */
	;

scoped_name
	: id
	| SCOPE_DELIMITOR
	  id
	| scoped_name
	  SCOPE_DELIMITOR
	  id
	;

id: IDENTIFIER { IDL_SetIdentifier($1); }
        ;

forward :
	interface_decl
	;

const_dcl :
	CONST const_type id '=' expression
	;

const_type
	: integer_type
	| char_type
	| octet_type
	| boolean_type
	| floating_pt_type
	| string_type_spec
	| wstring_type_spec
	| scoped_name
	;

expression : const_expr	;

const_expr : or_expr ;

or_expr : xor_expr
	| or_expr '|' xor_expr
	;

xor_expr
	: and_expr
	| xor_expr '^' and_expr
	;

and_expr
	: shift_expr
	| and_expr '&' shift_expr
	;

shift_expr
	: add_expr
	| shift_expr LEFT_SHIFT add_expr
	| shift_expr RIGHT_SHIFT add_expr
	;

add_expr
	: mult_expr
	| add_expr '+' mult_expr
	| add_expr '-' mult_expr
	;

mult_expr
	: unary_expr
	| mult_expr '*' unary_expr
	| mult_expr '/' unary_expr
	| mult_expr '%' unary_expr
	;

unary_expr
	: primary_expr
	| '+' primary_expr
	| '-' primary_expr
	| '~' primary_expr
	;

primary_expr
	: scoped_name
	| literal
	| '(' const_expr ')'
	;

literal
	: INTEGER_LITERAL
	| STRING_LITERAL
	| CHARACTER_LITERAL
	| FLOATING_PT_LITERAL
	| TRUETOK
	| FALSETOK
	;

positive_int_expr :
	const_expr
	;

type_dcl
	: TYPEDEF type_declarator
	| struct_type
	| union_type
	| enum_type
	;

type_declarator :
	type_spec at_least_one_declarator
	;

type_spec
	: simple_type_spec
	| constructed_type_spec
	;

simple_type_spec
	: base_type_spec
	| template_type_spec
	| scoped_name
	;

base_type_spec
	: integer_type
	| floating_pt_type
	| char_type
	| boolean_type
	| octet_type
	| any_type
	;

template_type_spec
	: sequence_type_spec
	| string_type_spec
	| wstring_type_spec
	;

constructed_type_spec
	: struct_type
	| union_type
	| enum_type
	;

at_least_one_declarator :
	declarator declarators
	;
declarators
	: declarators  ',' declarator
	| /* EMPTY */
	;

declarator
	: simple_declarator
	| complex_declarator
	;

simple_declarator :
	id
	;

complex_declarator :
	array_declarator
	;

integer_type
	: signed_int
	| unsigned_int
	;

signed_int
	: LONG
	| LONG LONG
	| SHORT
	;

unsigned_int
	: UNSIGNED LONG
	| UNSIGNED LONG LONG
	| UNSIGNED SHORT
	;

floating_pt_type
	: DOUBLE
	| FLOAT
	| LONG DOUBLE
	;

char_type
	: CHAR
	| WCHAR
	;

octet_type
	: OCTET
	;

boolean_type
	: BOOLEAN
	;

any_type
	: ANY
	;

struct_type :
	STRUCT id '{' at_least_one_member '}'
	;

at_least_one_member : member members ;

members
	: members member
	| /* EMPTY */
	;

member	:
	type_spec at_least_one_declarator
	';'
	| error
	';'
	;

union_type :
	UNION id SWITCH '(' switch_type_spec ')' '{' at_least_one_case_branch '}'
	;

switch_type_spec :
	integer_type
	| char_type
	| octet_type
	| boolean_type
	| enum_type
	| scoped_name
	;

at_least_one_case_branch : case_branch case_branches ;

case_branches
	: case_branches case_branch
	| /* empty */
	;

case_branch :
	at_least_one_case_label element_spec ';'
	| error
	';'
	;

at_least_one_case_label :
	case_label case_labels
	;

case_labels
	: case_labels case_label
	| /* EMPTY */
	;

case_label
	: DEFAULT ':'
	| CASE const_expr ':'
	;

element_spec :
	type_spec declarator
	;

enum_type :
	ENUM id	'{' at_least_one_enumerator '}'
	;

at_least_one_enumerator : enumerator enumerators ;

enumerators
	: enumerators ',' enumerator
	| /* EMPTY */
	;

enumerator :
	IDENTIFIER { IDL_SetIdentifier($1); }
	;

sequence_type_spec
        : seq_head ',' positive_int_expr '>'
	| seq_head '>'
	;

seq_head:
	SEQUENCE '<' simple_type_spec
	;

string_type_spec
	: string_head  '<' positive_int_expr '>'
	| string_head
	;

string_head:
	STRING
	;

wstring_type_spec
	: wstring_head  '<' positive_int_expr '>'
	| wstring_head
	;

wstring_head:
	WSTRING
	;

array_declarator :
	id at_least_one_array_dim
	;

at_least_one_array_dim :
	array_dim array_dims
	;

array_dims
	: array_dims array_dim
	| /* EMPTY */
	;

array_dim :
	'[' positive_int_expr ']'
	;

attribute:
	opt_readonly ATTRIBUTE simple_type_spec at_least_one_declarator
	;

opt_readonly
	: READONLY
	| /* EMPTY */
	;

exception :
	EXCEPTION id '{' members '}'
	;

operation :
	opt_op_attribute op_type_spec IDENTIFIER parameter_list opt_raises opt_context
	;

opt_op_attribute
	: ONEWAY
	| IDEMPOTENT
	| /* EMPTY */
	;

op_type_spec
	: simple_type_spec
	| VOID
	;

parameter_list
	: '('  ')'
	| '(' at_least_one_parameter ')'
	;

at_least_one_parameter : parameter parameters ;

parameters
	: parameters  ','  parameter
	| /* EMPTY */
	;

parameter :
	direction simple_type_spec declarator
	;

direction
	: IN
	| OUT
	| INOUT
	;

opt_raises
	: RAISES  '('  at_least_one_scoped_name  ')'
	| /* EMPTY */
	;

opt_context
	: CONTEXT  '('  at_least_one_string_literal ')'
	| /* EMPTY */
	;

at_least_one_string_literal :
	STRING_LITERAL string_literals
	;

string_literals
	: string_literals ',' STRING_LITERAL
	| /* EMPTY */
	;

%%
/* programs */

int IDLwrap()
{
  return 1;
}

