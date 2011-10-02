
%{

/*
 * idl.ll - Lexical scanner for IDL 1.1
 */
#define yylval IDLlval
#include <IDL.tab.h>
#include <string.h>
#include <ctype.h>
#ifdef WNT
# include <io.h>
#else
# include <unistd.h>
#endif  /* WNT */

static char	idl_escape_reader(char *);
static double	idl_atof(char *);
static long	idl_atoi(char *, long);

/* static char	*yytext = (char *) yytext; */
#define yyinput() input()

int IDLlineno;
%}

%%

module		return MODULE;
raises		return RAISES;
readonly	return READONLY;
attribute	return ATTRIBUTE;
exception	return EXCEPTION;
context		return CONTEXT;
interface	return INTERFACE;
const		return CONST;
typedef		return TYPEDEF;
struct		return STRUCT;
enum		return ENUM;
string		return STRING;
wstring_t	return WSTRING;
sequence	return SEQUENCE;
union		return UNION;
switch		return SWITCH;
case		return CASE;
default		return DEFAULT;
float		return FLOAT;
double		return DOUBLE;
long		return LONG;
short		return SHORT;
unsigned	return UNSIGNED;
char		return CHAR;
wchar_t		return WCHAR;
boolean		return BOOLEAN;
octet		return OCTET;
void		return VOID;

TRUE		return TRUETOK;
FALSE		return FALSETOK;

inout		return INOUT;
in		return IN;
out		return OUT;
oneway		return ONEWAY;

\<\<		return LEFT_SHIFT;
\>\>		return RIGHT_SHIFT;
\:\:		{
  /*yylval.strval = "::";    */
		  return SCOPE_DELIMITOR;
		}

[a-zA-Z][a-zA-Z0-9_]*	{
  strcpy(yylval.str, yytext);
  return IDENTIFIER;
}

-?[0-9]+"."[0-9]*([eE][+-]?[0-9]+)?[lLfF]?      {
  yylval.dval = idl_atof(yytext);
                  return FLOATING_PT_LITERAL;
                }
-?[0-9]+[eE][+-]?[0-9]+[lLfF]?  {
  yylval.dval = idl_atof(yytext);
                  return FLOATING_PT_LITERAL;
                }

-?[1-9][0-9]*	{
  yylval.ival = idl_atoi(yytext, 10);
		  return INTEGER_LITERAL;
	        }
-?0[xX][a-fA-F0-9]+ {
  yylval.ival = idl_atoi(yytext, 16);
		  return INTEGER_LITERAL;
	        }
-?0[0-7]*	{
  yylval.ival = idl_atoi(yytext, 8);
  return INTEGER_LITERAL;
	      	}

"\""[^\"]*"\""	{
  /*yytext[strlen(yytext)-1] = '\0';
		  yylval.sval = new String(yytext + 1);*/
		  return STRING_LITERAL;
	      	}
"'"."'"		{
  yylval.cval = yytext[1];
		  return CHARACTER_LITERAL;
	      	}
"'"\\([0-7]{1,3})"'"	{
  /* octal character constant */
  yylval.cval = idl_escape_reader(yytext + 1);
  return CHARACTER_LITERAL;
}
"'"\\."'"	{
  yylval.cval = idl_escape_reader(yytext + 1);
		  return CHARACTER_LITERAL;
		}
^#[ \t]*pragma[ \t].*\n	{/* remember pragma */
  IDLlineno++;
  /*idl_store_pragma(yytext);*/
  return POSTPROCESSOR;
}
^#[ \t]*include[ \t].*\n	{/* remember includes */
  IDLlineno++;
  /*idl_store_pragma(yytext);*/
  return POSTPROCESSOR;
}
^#[ \t]*ifndef[ \t].*\n	{
  IDLlineno++;
  /*idl_store_pragma(yytext);*/
  return POSTPROCESSOR;
}
^#[ \t]*define[ \t].*\n	{
  IDLlineno++;
  /*idl_store_pragma(yytext);*/
  return POSTPROCESSOR;
}
^#[ \t]*endif.*\n	{
  IDLlineno++;
  /*idl_store_pragma(yytext);*/
  return POSTPROCESSOR;
}
^#[ \t]*[0-9]*" ""\""[^\"]*"\""" "[0-9]*\n		{
  /*idl_parse_line_and_file(yytext);*/
  return POSTPROCESSOR;
}
^#[ \t]*[0-9]*" ""\""[^\"]*"\""\n			{
  /*idl_parse_line_and_file(yytext);*/
  return POSTPROCESSOR;
}
^#[ \t]*[0-9]*\n	{
  /* idl_parse_line_and_file(yytext);*/
  return POSTPROCESSOR;
}
^#[ \t]*ident.*\n	{
		  /* ignore cpp ident */
  		  IDLlineno++;
		  return POSTPROCESSOR;
		}
\/\/.*\n	{
		  /* ignore comments */
  		  IDLlineno++;
		}
"/*"		{
		  for(;;) {
		    char c = yyinput();
		    if (c == '*') {
		      char next = yyinput();
		      if (next == '/')
			break;
		      else
			unput(c);
	              if (c == '\n') 
		        IDLlineno++;
		    }
	          }
	        }
[ \t]*		;
\n		{
  		  IDLlineno++;
		}
.		return yytext[0];

%%
	/* subroutines */

/*
 * idl_atoi - Convert a string of digits into an integer according to base b
 */
static long
idl_atoi(char *s, long b)
{
	long	r = 0;
	long	negative = 0;

	if (*s == '-') {
	  negative = 1;
	  s++;
	}
	if (b == 8 && *s == '0')
	  s++;
	else if (b == 16 && *s == '0' && (*(s + 1) == 'x' || *(s + 1) == 'X'))
	  s += 2;

	for (; *s; s++)
	  if (*s <= '9' && *s >= '0')
	    r = (r * b) + (*s - '0');
	  else if (b > 10 && *s <= 'f' && *s >= 'a')
	    r = (r * b) + (*s - 'a' + 10);
	  else if (b > 10 && *s <= 'F' && *s >= 'A')
	    r = (r * b) + (*s - 'A' + 10);
	  else
	    break;

	if (negative)
	  r *= -1;

	return r;
}

/*
 * Convert a string to a float; atof doesn't seem to work, always.
 */
static double
idl_atof(char *s)
{
	double	d = 0.0;
	double	e, k;
	long	neg = 0, negexp = 0;

	if (*s == '-') {
	  neg = 1;
	  s++;
	}
	while (*s >= '0' && *s <= '9') {
		d = (d * 10) + *s - '0';
		s++;
	}
	if (*s == '.') {
		s++;
		e = 10;
		while (*s >= '0' && *s <= '9') {
			d += (*s - '0') / (e * 1.0);
			e *= 10;
			s++;
		}
	}
	if (*s == 'e' || *s == 'E') {
		s++;
		if (*s == '-') {
			negexp = 1;
			s++;
		} else if (*s == '+')
			s++;
		e = 0;
		while (*s >= '0' && *s <= '9') {
			e = (e * 10) + *s - '0';
			s++;
		}
		if (e > 0) {
			for (k = 1; e > 0; k *= 10, e--);
			if (negexp)
				d /= k;
			else
				d *= k;
		}
	}

	if (neg) d *= -1.0;

	return d;
}	

/*
 * Convert (some) escaped characters into their ascii values
 */
static char
idl_escape_reader(
    char *str
)
{
  int i;
  char save;
  char out;

    if (str[0] != '\\') {
	return str[0];
    }

    switch (str[1]) {
      case 'n':
	return '\n';
      case 't':
	return '\t';
      case 'v':
	return '\v';
      case 'b':
	return '\b';
      case 'r':
	return '\r';
      case 'f':
	return '\f';
      case 'a':
	return '\a';
      case '\\':
	return '\\';
      case '\?':
	return '?';
      case '\'':
	return '\'';
      case '"':
	return '"';
      case 'x':
	{
	    
	    for (i = 2; str[i] != '\0' && isxdigit((unsigned char)str[i]); i++) {
		continue;
	    }
	    save = str[i];
	    str[i] = '\0';
	    out = (char)idl_atoi(&str[2], 16);
	    str[i] = save;
	    return out;
	}
	break;
      default:
	if (str[1] >= '0' && str[1] <= '7') {
	    for (i = 1; str[i] >= '0' && str[i] <= '7'; i++) {
		continue;
	    }
	    save = str[i];
	    str[i] = '\0';
	    out = (char)idl_atoi(&str[1], 8);
	    str[i] = save;
	    return out;
	} else {
	  return str[1] - 'a';
	}
	break;
    }
}

