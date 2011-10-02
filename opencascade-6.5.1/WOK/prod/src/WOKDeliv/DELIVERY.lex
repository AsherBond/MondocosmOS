
%{
#define YY_NO_UNPUT
#define yylval DELIVERYlval
#include <DELIVERY.tab.h>
#include <WOKDeliv_ParseDelivery.h>
#ifdef WNT
# include <io.h>
#else
# include <unistd.h>
#endif 

int DELIVERYlineno;

void lookup(tok)
int tok;
{
	TheToken = tok;
	if (tok == T_ALPHA) {
	   TheText = strdup(yytext);
	}	
}

#define Token(x) lookup(x);return x
%}

%s NAME ATTRIBUTES
DEUXPOINT "::"
TROISPOINT ":::"
%%

"/*""/"*([^*/]|[^*]"/"[^/])*"*"*"*/"   ;

<INITIAL>"Put"[ \t]+"path"    {BEGIN INITIAL;Token(T_PUTPATH);}
<INITIAL>"Put"[ \t]+"include" {BEGIN INITIAL;Token(T_PUTINCLUDE);}
<INITIAL>"Put"[ \t]+"lib"     {BEGIN INITIAL;Token(T_PUTLIB);}
<INITIAL>"Requires"           {BEGIN NAME;Token(T_REQUIRES);}
<INITIAL>"Get"	              {BEGIN NAME;Token(T_GET);}
<INITIAL>"Name"               {BEGIN NAME;Token(T_NAME);}
<INITIAL>"#ifdef"             {BEGIN NAME;Token(T_IFDEF);}
<INITIAL>"#endif"             {BEGIN INITIAL;Token(T_ENDIF);}


<INITIAL>DevUnit              {BEGIN NAME;Token(T_DEVUNIT);}
<INITIAL>Package	      {BEGIN NAME;Token(T_PACKAGE);}
<INITIAL>Nocdlpack            {BEGIN NAME;Token(T_NOCDLPACK);}
<INITIAL>Executable           {BEGIN NAME;Token(T_EXECUTABLE);}
<INITIAL>Interface            {BEGIN NAME;Token(T_INTERFACE);}
<INITIAL>Engine	              {BEGIN NAME;Token(T_ENGINE);}
<INITIAL>Client	              {BEGIN NAME;Token(T_CLIENT);}
<INITIAL>Schema               {BEGIN NAME;Token(T_SCHEMA);}
<INITIAL>Toolkit              {BEGIN NAME;Token(T_TOOLKIT);}
<INITIAL>Ccl                  {BEGIN NAME;Token(T_CCL);}
<INITIAL>Frontal              {BEGIN NAME;Token(T_FRONTAL);}
<INITIAL>Resource             {BEGIN NAME;Token(T_RESOURCE);}
<INITIAL>Documentation        {BEGIN NAME;Token(T_RESOURCE);}

<ATTRIBUTES>LIBRARY	     {Token(T_LIBRARY);}
<ATTRIBUTES>SHARED	     {Token(T_SHARED);}
<ATTRIBUTES>ARCHIVE	     {Token(T_ARCHIVE);}
<ATTRIBUTES>DATAOBJECT	     {Token(T_DATAOBJECT);}
<ATTRIBUTES>CDL	             {Token(T_CDL);}
<ATTRIBUTES>INCLUDES         {Token(T_INCLUDES);}
<ATTRIBUTES>SOURCES          {Token(T_SOURCES);}
<ATTRIBUTES>STATIC	     {Token(T_STATIC);}
<ATTRIBUTES>DYNAMIC	     {Token(T_DYNAMIC);}
<ATTRIBUTES>STUB_SERVER      {Token(T_STUB_SERVER);}
<ATTRIBUTES>STUB_CLIENT      {Token(T_STUB_CLIENT);}
<ATTRIBUTES>FDDB	     {Token(T_FDDB);}
<ATTRIBUTES>GET              {Token(T_GETRES);}
<ATTRIBUTES>DDL              {Token(T_DDL);}

<ATTRIBUTES>{DEUXPOINT}            {BEGIN NAME;Token(T_SEPARATOR);}
<ATTRIBUTES>{TROISPOINT}           {BEGIN NAME;Token(T_SEPARATOR);}


<NAME>[a-zA-Z\/\-\_\.0-9]+   {BEGIN ATTRIBUTES; Token(T_ALPHA);}

[ \t]*      {Token(T_WSPACE);}
[ \t]*\n    {BEGIN INITIAL; Token(T_NEWLINE);}

.    {Token(T_INVALID);}




%%



