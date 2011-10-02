
/*  A Bison parser, made from /adv_20/KAS/C40/ros/src/IDLFront/IDL.yacc
 by  GNU Bison version 1.25
  */

#define YYBISON 1  /* Identify Bison output.  */

#define yyparse IDLparse
#define yylex IDLlex
#define yyerror IDLerror
#define yylval IDLlval
#define yychar IDLchar
#define yydebug IDLdebug
#define yynerrs IDLnerrs
#define	IDENTIFIER	258
#define	CONST	259
#define	MODULE	260
#define	INTERFACE	261
#define	TYPEDEF	262
#define	LONG	263
#define	SHORT	264
#define	UNSIGNED	265
#define	DOUBLE	266
#define	FLOAT	267
#define	CHAR	268
#define	WCHAR	269
#define	OCTET	270
#define	BOOLEAN	271
#define	ANY	272
#define	STRUCT	273
#define	UNION	274
#define	SWITCH	275
#define	ENUM	276
#define	SEQUENCE	277
#define	STRING	278
#define	WSTRING	279
#define	EXCEPTION	280
#define	CASE	281
#define	DEFAULT	282
#define	READONLY	283
#define	ATTRIBUTE	284
#define	ONEWAY	285
#define	IDEMPOTENT	286
#define	VOID	287
#define	IN	288
#define	OUT	289
#define	INOUT	290
#define	RAISES	291
#define	CONTEXT	292
#define	POSTPROCESSOR	293
#define	INTEGER_LITERAL	294
#define	STRING_LITERAL	295
#define	CHARACTER_LITERAL	296
#define	FLOATING_PT_LITERAL	297
#define	TRUETOK	298
#define	FALSETOK	299
#define	SCOPE_DELIMITOR	300
#define	LEFT_SHIFT	301
#define	RIGHT_SHIFT	302

#line 7 "/adv_20/KAS/C40/ros/src/IDLFront/IDL.yacc"

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


#line 144 "/adv_20/KAS/C40/ros/src/IDLFront/IDL.yacc"
typedef union {
 char str[256];
 double dval;
 int ival;
 char cval;
} YYSTYPE;
#include <stdio.h>

#ifndef __cplusplus
#ifndef __STDC__
#define const
#endif
#endif



#define	YYFINAL		297
#define	YYFLAG		-32768
#define	YYNTBASE	69

#define YYTRANSLATE(x) ((unsigned)(x) <= 302 ? yytranslate[x] : 162)

static const char yytranslate[] = {     0,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,    61,    56,     2,    63,
    64,    59,    57,    52,    58,     2,    60,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,    51,    48,    66,
    53,    65,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
    67,     2,    68,    55,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,    49,    54,    50,    62,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     1,     2,     3,     4,     5,
     6,     7,     8,     9,    10,    11,    12,    13,    14,    15,
    16,    17,    18,    19,    20,    21,    22,    23,    24,    25,
    26,    27,    28,    29,    30,    31,    32,    33,    34,    35,
    36,    37,    38,    39,    40,    41,    42,    43,    44,    45,
    46,    47
};

#if YYDEBUG != 0
static const short yyprhs[] = {     0,
     0,     2,     5,     6,     8,    11,    14,    17,    20,    23,
    26,    32,    34,    36,    37,    43,    46,    49,    52,    53,
    56,    57,    60,    63,    66,    69,    72,    75,    78,    82,
    83,    85,    88,    92,    94,    96,   102,   104,   106,   108,
   110,   112,   114,   116,   118,   120,   122,   124,   128,   130,
   134,   136,   140,   142,   146,   150,   152,   156,   160,   162,
   166,   170,   174,   176,   179,   182,   185,   187,   189,   193,
   195,   197,   199,   201,   203,   205,   207,   210,   212,   214,
   216,   219,   221,   223,   225,   227,   229,   231,   233,   235,
   237,   239,   241,   243,   245,   247,   249,   251,   253,   256,
   260,   261,   263,   265,   267,   269,   271,   273,   275,   278,
   280,   283,   287,   290,   292,   294,   297,   299,   301,   303,
   305,   307,   313,   316,   319,   320,   324,   327,   337,   339,
   341,   343,   345,   347,   349,   352,   355,   356,   360,   363,
   366,   369,   370,   373,   377,   380,   386,   389,   393,   394,
   396,   401,   404,   408,   413,   415,   417,   422,   424,   426,
   429,   432,   435,   436,   440,   445,   447,   448,   454,   461,
   463,   465,   466,   468,   470,   473,   477,   480,   484,   485,
   489,   491,   493,   495,   500,   501,   506,   507,   510,   514
};

static const short yyrhs[] = {    70,
     0,    71,    70,     0,     0,    38,     0,   100,    48,     0,
    86,    48,     0,   149,    48,     0,    73,    48,     0,    72,
    48,     0,     1,    48,     0,     5,     3,    49,    70,    50,
     0,    74,     0,    85,     0,     0,    77,    75,    49,    79,
    50,     0,     6,    84,     0,    76,    78,     0,    51,    81,
     0,     0,    79,    80,     0,     0,   100,    48,     0,    86,
    48,     0,   149,    48,     0,   147,    48,     0,   150,    48,
     0,     1,    48,     0,    83,    82,     0,    82,    52,    83,
     0,     0,    84,     0,    45,    84,     0,    83,    45,    84,
     0,     3,     0,    76,     0,     4,    87,    84,    53,    88,
     0,   112,     0,   116,     0,   117,     0,   118,     0,   115,
     0,   139,     0,   141,     0,    83,     0,    89,     0,    90,
     0,    91,     0,    90,    54,    91,     0,    92,     0,    91,
    55,    92,     0,    93,     0,    92,    56,    93,     0,    94,
     0,    93,    46,    94,     0,    93,    47,    94,     0,    95,
     0,    94,    57,    95,     0,    94,    58,    95,     0,    96,
     0,    95,    59,    96,     0,    95,    60,    96,     0,    95,
    61,    96,     0,    97,     0,    57,    97,     0,    58,    97,
     0,    62,    97,     0,    83,     0,    98,     0,    63,    89,
    64,     0,    39,     0,    40,     0,    41,     0,    42,     0,
    43,     0,    44,     0,    89,     0,     7,   101,     0,   120,
     0,   124,     0,   133,     0,   102,   107,     0,   103,     0,
   106,     0,   104,     0,   105,     0,    83,     0,   112,     0,
   115,     0,   116,     0,   118,     0,   117,     0,   119,     0,
   137,     0,   139,     0,   141,     0,   120,     0,   124,     0,
   133,     0,   109,   108,     0,   108,    52,   109,     0,     0,
   110,     0,   111,     0,    84,     0,   143,     0,   113,     0,
   114,     0,     8,     0,     8,     8,     0,     9,     0,    10,
     8,     0,    10,     8,     8,     0,    10,     9,     0,    11,
     0,    12,     0,     8,    11,     0,    13,     0,    14,     0,
    15,     0,    16,     0,    17,     0,    18,    84,    49,   121,
    50,     0,   123,   122,     0,   122,   123,     0,     0,   102,
   107,    48,     0,     1,    48,     0,    19,    84,    20,    63,
   125,    64,    49,   126,    50,     0,   112,     0,   116,     0,
   117,     0,   118,     0,   133,     0,    83,     0,   128,   127,
     0,   127,   128,     0,     0,   129,   132,    48,     0,     1,
    48,     0,   131,   130,     0,   130,   131,     0,     0,    27,
    51,     0,    26,    89,    51,     0,   102,   109,     0,    21,
    84,    49,   134,    50,     0,   136,   135,     0,   135,    52,
   136,     0,     0,     3,     0,   138,    52,    99,    65,     0,
   138,    65,     0,    22,    66,   103,     0,   140,    66,    99,
    65,     0,   140,     0,    23,     0,   142,    66,    99,    65,
     0,   142,     0,    24,     0,    84,   144,     0,   146,   145,
     0,   145,   146,     0,     0,    67,    99,    68,     0,   148,
    29,   103,   107,     0,    28,     0,     0,    25,    84,    49,
   122,    50,     0,   151,   152,     3,   153,   158,   159,     0,
    30,     0,    31,     0,     0,   103,     0,    32,     0,    63,
    64,     0,    63,   154,    64,     0,   156,   155,     0,   155,
    52,   156,     0,     0,   157,   103,   109,     0,    33,     0,
    34,     0,    35,     0,    36,    63,    81,    64,     0,     0,
    37,    63,   160,    64,     0,     0,    40,   161,     0,   161,
    52,    40,     0,     0
};

#endif

#if YYDEBUG != 0
static const short yyrline[] = { 0,
   156,   159,   160,   164,   165,   167,   169,   171,   173,   175,
   182,   185,   186,   189,   190,   193,   197,   202,   204,   208,
   209,   213,   214,   215,   216,   217,   218,   221,   226,   227,
   231,   232,   234,   239,   242,   246,   251,   252,   253,   254,
   255,   256,   257,   258,   261,   263,   265,   266,   270,   271,
   275,   276,   280,   281,   282,   286,   287,   288,   292,   293,
   294,   295,   299,   300,   301,   302,   306,   307,   308,   312,
   313,   314,   315,   316,   317,   320,   325,   326,   327,   328,
   331,   336,   337,   341,   342,   343,   347,   348,   349,   350,
   351,   352,   356,   357,   358,   362,   363,   364,   367,   371,
   372,   376,   377,   380,   384,   389,   390,   394,   395,   396,
   400,   401,   402,   406,   407,   408,   412,   413,   417,   421,
   425,   428,   432,   435,   436,   439,   442,   446,   450,   452,
   453,   454,   455,   456,   459,   462,   463,   466,   468,   472,
   477,   478,   482,   483,   486,   490,   494,   497,   498,   501,
   506,   507,   510,   515,   516,   519,   524,   525,   528,   532,
   536,   541,   542,   545,   549,   554,   555,   558,   562,   567,
   568,   569,   573,   574,   578,   579,   582,   585,   586,   589,
   594,   595,   596,   600,   601,   605,   606,   609,   614,   615
};
#endif


#if YYDEBUG != 0 || defined (YYERROR_VERBOSE)

static const char * const yytname[] = {   "$","error","$undefined.","IDENTIFIER",
"CONST","MODULE","INTERFACE","TYPEDEF","LONG","SHORT","UNSIGNED","DOUBLE","FLOAT",
"CHAR","WCHAR","OCTET","BOOLEAN","ANY","STRUCT","UNION","SWITCH","ENUM","SEQUENCE",
"STRING","WSTRING","EXCEPTION","CASE","DEFAULT","READONLY","ATTRIBUTE","ONEWAY",
"IDEMPOTENT","VOID","IN","OUT","INOUT","RAISES","CONTEXT","POSTPROCESSOR","INTEGER_LITERAL",
"STRING_LITERAL","CHARACTER_LITERAL","FLOATING_PT_LITERAL","TRUETOK","FALSETOK",
"SCOPE_DELIMITOR","LEFT_SHIFT","RIGHT_SHIFT","';'","'{'","'}'","':'","','","'='",
"'|'","'^'","'&'","'+'","'-'","'*'","'/'","'%'","'~'","'('","')'","'>'","'<'",
"'['","']'","start","definitions","definition","module","interface_def","interface",
"@1","interface_decl","interface_header","inheritance_spec","exports","export",
"at_least_one_scoped_name","scoped_names","scoped_name","id","forward","const_dcl",
"const_type","expression","const_expr","or_expr","xor_expr","and_expr","shift_expr",
"add_expr","mult_expr","unary_expr","primary_expr","literal","positive_int_expr",
"type_dcl","type_declarator","type_spec","simple_type_spec","base_type_spec",
"template_type_spec","constructed_type_spec","at_least_one_declarator","declarators",
"declarator","simple_declarator","complex_declarator","integer_type","signed_int",
"unsigned_int","floating_pt_type","char_type","octet_type","boolean_type","any_type",
"struct_type","at_least_one_member","members","member","union_type","switch_type_spec",
"at_least_one_case_branch","case_branches","case_branch","at_least_one_case_label",
"case_labels","case_label","element_spec","enum_type","at_least_one_enumerator",
"enumerators","enumerator","sequence_type_spec","seq_head","string_type_spec",
"string_head","wstring_type_spec","wstring_head","array_declarator","at_least_one_array_dim",
"array_dims","array_dim","attribute","opt_readonly","exception","operation",
"opt_op_attribute","op_type_spec","parameter_list","at_least_one_parameter",
"parameters","parameter","direction","opt_raises","opt_context","at_least_one_string_literal",
"string_literals", NULL
};
#endif

static const short yyr1[] = {     0,
    69,    70,    70,    71,    71,    71,    71,    71,    71,    71,
    72,    73,    73,    75,    74,    76,    77,    78,    78,    79,
    79,    80,    80,    80,    80,    80,    80,    81,    82,    82,
    83,    83,    83,    84,    85,    86,    87,    87,    87,    87,
    87,    87,    87,    87,    88,    89,    90,    90,    91,    91,
    92,    92,    93,    93,    93,    94,    94,    94,    95,    95,
    95,    95,    96,    96,    96,    96,    97,    97,    97,    98,
    98,    98,    98,    98,    98,    99,   100,   100,   100,   100,
   101,   102,   102,   103,   103,   103,   104,   104,   104,   104,
   104,   104,   105,   105,   105,   106,   106,   106,   107,   108,
   108,   109,   109,   110,   111,   112,   112,   113,   113,   113,
   114,   114,   114,   115,   115,   115,   116,   116,   117,   118,
   119,   120,   121,   122,   122,   123,   123,   124,   125,   125,
   125,   125,   125,   125,   126,   127,   127,   128,   128,   129,
   130,   130,   131,   131,   132,   133,   134,   135,   135,   136,
   137,   137,   138,   139,   139,   140,   141,   141,   142,   143,
   144,   145,   145,   146,   147,   148,   148,   149,   150,   151,
   151,   151,   152,   152,   153,   153,   154,   155,   155,   156,
   157,   157,   157,   158,   158,   159,   159,   160,   161,   161
};

static const short yyr2[] = {     0,
     1,     2,     0,     1,     2,     2,     2,     2,     2,     2,
     5,     1,     1,     0,     5,     2,     2,     2,     0,     2,
     0,     2,     2,     2,     2,     2,     2,     2,     3,     0,
     1,     2,     3,     1,     1,     5,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     3,     1,     3,
     1,     3,     1,     3,     3,     1,     3,     3,     1,     3,
     3,     3,     1,     2,     2,     2,     1,     1,     3,     1,
     1,     1,     1,     1,     1,     1,     2,     1,     1,     1,
     2,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     2,     3,
     0,     1,     1,     1,     1,     1,     1,     1,     2,     1,
     2,     3,     2,     1,     1,     2,     1,     1,     1,     1,
     1,     5,     2,     2,     0,     3,     2,     9,     1,     1,
     1,     1,     1,     1,     2,     2,     0,     3,     2,     2,
     2,     0,     2,     3,     2,     5,     2,     3,     0,     1,
     4,     2,     3,     4,     1,     1,     4,     1,     1,     2,
     2,     2,     0,     3,     4,     1,     0,     5,     6,     1,
     1,     0,     1,     1,     2,     3,     2,     3,     0,     3,
     1,     1,     1,     4,     0,     4,     0,     2,     3,     0
};

static const short yydefact[] = {     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     4,
     1,     0,     0,     0,    12,    19,    14,    13,     0,     0,
    78,    79,    80,     0,    10,    34,   108,   110,     0,   114,
   115,   117,   118,   119,   120,   156,   159,     0,    44,    31,
     0,    37,   106,   107,    41,    38,    39,    40,    42,   155,
    43,   158,     0,    16,   121,     0,    86,    77,     0,    82,
    84,    85,    83,    87,    88,    89,    91,    90,    92,    96,
    97,    98,    93,     0,    94,    95,     0,     0,     0,     0,
     2,     9,     8,     0,    17,     0,     6,     5,     7,   109,
   116,   111,   113,    32,     0,     0,     0,     0,     0,     0,
   104,    81,   101,   102,   103,   105,     0,   152,     0,     0,
     0,   125,    18,    30,    21,   112,    33,     0,    70,    71,
    72,    73,    74,    75,     0,     0,     0,     0,    67,    76,
    46,    47,    49,    51,    53,    56,    59,    63,    68,     0,
     0,     0,   153,     0,   160,   163,    99,     0,     0,     0,
     0,   125,     0,   150,     0,   149,     0,    28,     0,    36,
    45,    64,    65,    66,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,   154,   157,    11,     0,   161,
     0,   151,   127,     0,   122,     0,   108,   134,   129,   130,
   131,   132,     0,   133,   146,   147,   168,   124,     0,     0,
   166,   170,   171,    15,    20,     0,     0,     0,     0,     0,
     0,     0,    69,    48,    50,    52,    54,    55,    57,    58,
    60,    61,    62,   164,   162,   100,   126,     0,     0,    29,
    27,    23,    22,    25,     0,    24,    26,   174,   173,     0,
     0,   148,     0,     0,     0,     0,     0,     0,   137,     0,
   142,   165,     0,   185,   139,     0,   143,   128,     0,     0,
     0,   140,   181,   182,   183,   175,     0,   179,     0,     0,
   187,   144,   136,   145,   138,   141,   176,   177,     0,     0,
     0,   169,     0,   180,     0,     0,   178,   184,   190,     0,
   188,   186,     0,   189,     0,     0,     0
};

static const short yydefgoto[] = {   295,
    11,    12,    13,    14,    15,    86,    16,    17,    85,   159,
   205,   113,   158,   129,    40,    18,    19,    41,   160,   130,
   131,   132,   133,   134,   135,   136,   137,   138,   139,   140,
    20,    58,   150,    60,    61,    62,    63,   102,   147,   103,
   104,   105,    64,    43,    44,    65,    66,    67,    68,    69,
    70,   151,   157,   198,    71,   193,   248,   259,   249,   250,
   262,   251,   261,    72,   155,   196,   156,    73,    74,    75,
    50,    76,    52,   106,   145,   180,   146,   208,   209,    24,
   211,   212,   240,   254,   267,   278,   268,   269,   271,   282,
   290,   291
};

static const short yypact[] = {   115,
   -28,   226,    23,    31,   366,    31,    31,    31,    31,-32768,
-32768,   123,   -12,    -5,-32768,   -16,-32768,-32768,     6,    37,
-32768,-32768,-32768,    42,-32768,-32768,    29,-32768,    72,-32768,
-32768,-32768,-32768,-32768,-32768,-32768,-32768,    31,    32,-32768,
    31,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,    -8,
-32768,    59,    77,-32768,-32768,    65,    32,-32768,    31,-32768,
-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,
-32768,-32768,-32768,   -23,-32768,-32768,    83,    88,    86,    89,
-32768,-32768,-32768,    30,-32768,    90,-32768,-32768,-32768,-32768,
-32768,   135,-32768,-32768,    31,    92,    21,    21,   207,   415,
    80,-32768,-32768,-32768,-32768,-32768,    21,-32768,   341,    91,
   146,-32768,-32768,    32,-32768,-32768,-32768,    21,-32768,-32768,
-32768,-32768,-32768,-32768,    28,    28,    28,    21,    32,-32768,
    98,   101,   103,    47,    39,   -15,-32768,-32768,-32768,    95,
    97,   107,-32768,    21,-32768,-32768,   111,   109,   120,    31,
   125,-32768,   433,-32768,   126,-32768,   177,   127,   273,-32768,
-32768,-32768,-32768,-32768,   113,    21,    21,    21,    21,    21,
    21,    21,    21,    21,    21,-32768,-32768,-32768,   114,    80,
    31,-32768,-32768,   154,-32768,   316,   175,    32,-32768,-32768,
-32768,-32768,   139,-32768,-32768,   152,-32768,-32768,    30,   157,
-32768,-32768,-32768,-32768,-32768,   158,   159,   161,   186,   168,
   169,   390,-32768,   101,   103,    47,    39,    39,   -15,   -15,
-32768,-32768,-32768,-32768,-32768,-32768,-32768,   170,   146,    32,
-32768,-32768,-32768,-32768,   415,-32768,-32768,-32768,-32768,   215,
    26,-32768,    31,   160,   172,    21,   173,   171,-32768,   366,
-32768,-32768,    22,   194,-32768,   180,-32768,-32768,    24,    31,
   196,    76,-32768,-32768,-32768,-32768,   183,-32768,   415,   188,
   216,-32768,-32768,-32768,-32768,-32768,-32768,   200,    31,    30,
   191,-32768,    54,-32768,   192,   218,-32768,-32768,-32768,   195,
   203,-32768,   221,-32768,   262,   263,-32768
};

static const short yypgoto[] = {-32768,
     7,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,
-32768,   -14,-32768,    -2,     0,-32768,   105,-32768,-32768,  -100,
-32768,    99,   102,   100,   -57,   -54,  -126,   -26,-32768,   -68,
   116,-32768,    -4,   -98,-32768,-32768,-32768,  -138,-32768,  -168,
-32768,-32768,    12,-32768,-32768,   268,    13,    14,    19,-32768,
    10,-32768,   121,   163,    11,-32768,-32768,-32768,    34,-32768,
-32768,    38,-32768,     5,-32768,-32768,    70,-32768,-32768,   304,
-32768,   305,-32768,-32768,-32768,-32768,   128,-32768,-32768,   150,
-32768,-32768,-32768,-32768,-32768,-32768,    27,-32768,-32768,-32768,
-32768,-32768
};


#define	YYLAST		478


static const short yytable[] = {    39,
    59,   143,    57,    54,    23,    77,    78,    79,    80,    21,
    22,   184,   226,    42,    46,    47,    23,   161,    81,    25,
    48,    21,    22,    26,   245,    53,   245,   165,   107,   141,
    26,   -35,    26,    26,    84,    82,    90,    94,   148,    91,
    96,   108,    83,   173,   174,   175,   221,   222,   223,   246,
   247,   246,   247,    87,   263,   264,   265,    97,   101,   119,
   120,   121,   122,   123,   124,    38,   119,   120,   121,   122,
   123,   124,    38,  -135,    38,   179,    95,   125,   126,    92,
    93,   114,   127,   128,    88,   266,   263,   264,   265,    89,
   128,   274,   169,   170,   117,   171,   172,    57,   162,   163,
   164,   246,   247,    23,   252,   142,    57,   110,    21,    22,
   284,   217,   218,   239,    -3,     1,   219,   220,     2,     3,
     4,     5,    -3,     1,    98,    99,     2,     3,     4,     5,
   100,   109,     6,     7,   111,     8,   243,   112,   115,     9,
     6,     7,   116,     8,   118,   256,   144,     9,   154,   101,
   188,   166,    10,   153,    57,   167,   178,   194,   168,   176,
    10,   177,   181,    23,   189,   190,   191,   183,    21,    22,
   279,   192,    -3,   182,   185,   195,   213,   149,   199,    26,
   101,   224,    90,    57,    27,    28,    29,    30,    31,    32,
    33,    34,    35,    55,     6,     7,   230,     8,    56,    36,
    37,   227,   228,   229,   231,   232,   233,     1,   234,    57,
     2,     3,     4,     5,   235,   236,   237,   244,   241,   255,
   258,    38,   253,   257,     6,     7,   197,     8,    26,   270,
   272,     9,    57,    27,    28,    29,    30,    31,    32,    33,
    34,    35,   101,   275,    10,   260,   277,    57,    36,    37,
   280,   283,   281,   286,   293,   288,    -3,   289,   292,   101,
   294,   296,   297,   206,   214,   285,    57,   216,   215,    45,
    38,   152,   186,   200,   207,  -172,     2,   114,   101,     5,
  -172,  -172,  -172,  -172,  -172,  -172,  -172,  -172,  -172,  -172,
     6,     7,   273,     8,  -172,  -172,  -172,     9,   242,   276,
   201,  -167,   202,   203,  -172,    49,    51,   225,   210,   287,
     0,     0,     0,     0,     0,     0,   149,  -172,    26,     0,
     0,     0,   204,    27,    28,    29,    30,    31,    32,    33,
    34,    35,    55,     6,     7,     0,     8,    56,    36,    37,
     0,   149,     0,    26,     0,     0,     0,     0,    27,    28,
    29,    30,    31,    32,    33,    34,    35,    55,     6,     7,
    38,     8,    56,    36,    37,  -123,     0,     0,    26,     0,
     0,     0,     0,    27,    28,    29,    30,    31,    32,    33,
    34,    35,    55,     6,     7,    38,     8,    56,    36,    37,
     0,     0,    26,     0,     0,     0,     0,    27,    28,    29,
    30,    31,    32,    33,    34,    35,    55,     0,     0,     0,
    38,    56,    36,    37,     0,     0,     0,    26,     0,     0,
     0,   238,    27,    28,    29,    30,    31,    32,    33,    34,
    35,    55,     0,     0,    38,    26,    56,    36,    37,     0,
   187,    28,    29,     0,     0,    32,    33,    34,    35,     0,
     0,     0,     0,     8,     0,     0,     0,     0,     0,    38,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,    38
};

static const short yycheck[] = {     2,
     5,   100,     5,     4,     0,     6,     7,     8,     9,     0,
     0,   150,   181,     2,     2,     2,    12,   118,    12,    48,
     2,    12,    12,     3,     1,     3,     1,   128,    52,    98,
     3,    48,     3,     3,    51,    48,     8,    38,   107,    11,
    41,    65,    48,    59,    60,    61,   173,   174,   175,    26,
    27,    26,    27,    48,    33,    34,    35,    66,    59,    39,
    40,    41,    42,    43,    44,    45,    39,    40,    41,    42,
    43,    44,    45,    50,    45,   144,    45,    57,    58,     8,
     9,    84,    62,    63,    48,    64,    33,    34,    35,    48,
    63,   260,    46,    47,    95,    57,    58,   100,   125,   126,
   127,    26,    27,    99,   243,    99,   109,    20,    99,    99,
   279,   169,   170,   212,     0,     1,   171,   172,     4,     5,
     6,     7,     0,     1,    66,    49,     4,     5,     6,     7,
    66,    49,    18,    19,    49,    21,   235,    49,    49,    25,
    18,    19,     8,    21,    53,   246,    67,    25,     3,   150,
   153,    54,    38,    63,   157,    55,    50,   153,    56,    65,
    38,    65,    52,   159,   153,   153,   153,    48,   159,   159,
   269,   153,    50,    65,    50,    50,    64,     1,    52,     3,
   181,    68,     8,   186,     8,     9,    10,    11,    12,    13,
    14,    15,    16,    17,    18,    19,   199,    21,    22,    23,
    24,    48,    64,    52,    48,    48,    48,     1,    48,   212,
     4,     5,     6,     7,    29,    48,    48,     3,    49,    48,
    50,    45,    63,    51,    18,    19,    50,    21,     3,    36,
    51,    25,   235,     8,     9,    10,    11,    12,    13,    14,
    15,    16,   243,    48,    38,   250,    64,   250,    23,    24,
    63,    52,    37,    63,    52,    64,    50,    40,    64,   260,
    40,     0,     0,   159,   166,   280,   269,   168,   167,     2,
    45,   109,   152,     1,   159,     3,     4,   280,   279,     7,
     8,     9,    10,    11,    12,    13,    14,    15,    16,    17,
    18,    19,   259,    21,    22,    23,    24,    25,   229,   262,
    28,    29,    30,    31,    32,     2,     2,   180,   159,   283,
    -1,    -1,    -1,    -1,    -1,    -1,     1,    45,     3,    -1,
    -1,    -1,    50,     8,     9,    10,    11,    12,    13,    14,
    15,    16,    17,    18,    19,    -1,    21,    22,    23,    24,
    -1,     1,    -1,     3,    -1,    -1,    -1,    -1,     8,     9,
    10,    11,    12,    13,    14,    15,    16,    17,    18,    19,
    45,    21,    22,    23,    24,    50,    -1,    -1,     3,    -1,
    -1,    -1,    -1,     8,     9,    10,    11,    12,    13,    14,
    15,    16,    17,    18,    19,    45,    21,    22,    23,    24,
    -1,    -1,     3,    -1,    -1,    -1,    -1,     8,     9,    10,
    11,    12,    13,    14,    15,    16,    17,    -1,    -1,    -1,
    45,    22,    23,    24,    -1,    -1,    -1,     3,    -1,    -1,
    -1,    32,     8,     9,    10,    11,    12,    13,    14,    15,
    16,    17,    -1,    -1,    45,     3,    22,    23,    24,    -1,
     8,     9,    10,    -1,    -1,    13,    14,    15,    16,    -1,
    -1,    -1,    -1,    21,    -1,    -1,    -1,    -1,    -1,    45,
    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
    -1,    -1,    -1,    -1,    -1,    -1,    -1,    45
};
/* -*-C-*-  Note some compilers choke on comments on `#line' lines.  */
#line 3 "/PRODUCTS/flexbis-253-125/share/bison.simple"

/* Skeleton output parser for bison,
   Copyright (C) 1984, 1989, 1990 Free Software Foundation, Inc.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */

/* As a special exception, when this file is copied by Bison into a
   Bison output file, you may use that output file without restriction.
   This special exception was added by the Free Software Foundation
   in version 1.24 of Bison.  */

#ifndef alloca
#ifdef __GNUC__
#define alloca __builtin_alloca
#else /* not GNU C.  */
#if (!defined (__STDC__) && defined (sparc)) || defined (__sparc__) || defined (__sparc) || defined (__sgi)
#include <alloca.h>
#else /* not sparc */
#if (  defined( MSDOS ) || defined( WNT )  ) && !defined (__TURBOC__)
#include <malloc.h>
#else /* not MSDOS, or __TURBOC__ */
#if defined(_AIX)
#include <malloc.h>
 #pragma alloca
#else /* not MSDOS, __TURBOC__, or _AIX */
#ifdef __hpux
#ifdef __cplusplus
extern "C" {
void *alloca (unsigned int);
};
#else /* not __cplusplus */
void *alloca ();
#endif /* not __cplusplus */
#endif /* __hpux */
#endif /* not _AIX */
#endif /* not MSDOS, or __TURBOC__ */
#endif /* not sparc.  */
#endif /* not GNU C.  */
#endif /* alloca not defined.  */

/* This is the parser code that is written into each bison parser
  when the %semantic_parser declaration is not specified in the grammar.
  It was written by Richard Stallman by simplifying the hairy parser
  used when %semantic_parser is specified.  */

/* Note: there must be only one dollar sign in this file.
   It is replaced by the list of actions, each action
   as one case of the switch.  */

#define yyerrok		(yyerrstatus = 0)
#define yyclearin	(yychar = YYEMPTY)
#define YYEMPTY		-2
#define YYEOF		0
#define YYACCEPT	return(0)
#define YYABORT 	return(1)
#define YYERROR		goto yyerrlab1
/* Like YYERROR except do call yyerror.
   This remains here temporarily to ease the
   transition to the new meaning of YYERROR, for GCC.
   Once GCC version 2 has supplanted version 1, this can go.  */
#define YYFAIL		goto yyerrlab
#define YYRECOVERING()  (!!yyerrstatus)
#define YYBACKUP(token, value) \
do								\
  if (yychar == YYEMPTY && yylen == 1)				\
    { yychar = (token), yylval = (value);			\
      yychar1 = YYTRANSLATE (yychar);				\
      YYPOPSTACK;						\
      goto yybackup;						\
    }								\
  else								\
    { yyerror ("syntax error: cannot back up"); YYERROR; }	\
while (0)

#define YYTERROR	1
#define YYERRCODE	256

#ifndef YYPURE
#define YYLEX		yylex()
#endif

#ifdef YYPURE
#ifdef YYLSP_NEEDED
#ifdef YYLEX_PARAM
#define YYLEX		yylex(&yylval, &yylloc, YYLEX_PARAM)
#else
#define YYLEX		yylex(&yylval, &yylloc)
#endif
#else /* not YYLSP_NEEDED */
#ifdef YYLEX_PARAM
#define YYLEX		yylex(&yylval, YYLEX_PARAM)
#else
#define YYLEX		yylex(&yylval)
#endif
#endif /* not YYLSP_NEEDED */
#endif

/* If nonreentrant, generate the variables here */

#ifndef YYPURE

int	yychar;			/*  the lookahead symbol		*/
YYSTYPE	yylval;			/*  the semantic value of the		*/
				/*  lookahead symbol			*/

#ifdef YYLSP_NEEDED
YYLTYPE yylloc;			/*  location data for the lookahead	*/
				/*  symbol				*/
#endif

int yynerrs;			/*  number of parse errors so far       */
#endif  /* not YYPURE */

#if YYDEBUG != 0
int yydebug;			/*  nonzero means print parse trace	*/
/* Since this is uninitialized, it does not stop multiple parsers
   from coexisting.  */
#endif

/*  YYINITDEPTH indicates the initial size of the parser's stacks	*/

#ifndef	YYINITDEPTH
#define YYINITDEPTH 200
#endif

/*  YYMAXDEPTH is the maximum size the stacks can grow to
    (effective only if the built-in stack extension method is used).  */

#if YYMAXDEPTH == 0
#undef YYMAXDEPTH
#endif

#ifndef YYMAXDEPTH
#define YYMAXDEPTH 10000
#endif

/* Prevent warning if -Wstrict-prototypes.  */
#ifdef __GNUC__
int yyparse (void);
#endif

#if __GNUC__ > 1		/* GNU C and GNU C++ define this.  */
#ifdef WNT
#define __yy_bcopy(FROM,TO,COUNT)	__builtin_memcpy(TO,FROM,COUNT)
#else
#define __yy_memcpy(TO,FROM,COUNT)	__builtin_memcpy(TO,FROM,COUNT)
#endif
#else				/* not GNU C or C++ */
#ifndef __cplusplus

/* This is the most reliable way to avoid incompatibilities
   in available built-in functions on various systems.  */
static void
#ifdef WNT
__yy_bcopy (__from, __to, __count)
     char *__from;
     char *__to;
     int __count;
#else
__yy_memcpy (to, from, count)
     char *to;
     char *from;
     int count;
#endif
{
#ifdef WNT
  register char *f = __from;
  register char *t = __to;
  register int i = __count;
#else
  register char *f = from;
  register char *t = to;
  register int i = count;
#endif
  while (i-- > 0)
    *t++ = *f++;
}

#else /* __cplusplus */

/* This is the most reliable way to avoid incompatibilities
   in available built-in functions on various systems.  */
static void
#ifdef WNT
__yy_bcopy (char *__from, char *__to, int __count)
#else
__yy_memcpy (char *to, char *from, int count)
#endif
{
#ifdef WNT
  register char *f = __from;
  register char *t = __to;
  register int i = __count;
#else
  register char *f = from;
  register char *t = to;
  register int i = count;
#endif

  while (i-- > 0)
    *t++ = *f++;
}

#endif
#endif

#line 196 "/PRODUCTS/flexbis-253-125/share/bison.simple"

/* The user can define YYPARSE_PARAM as the name of an argument to be passed
   into yyparse.  The argument should have type void *.
   It should actually point to an object.
   Grammar actions can access the variable by casting it
   to the proper pointer type.  */

#ifdef YYPARSE_PARAM
#ifdef __cplusplus
#define YYPARSE_PARAM_ARG void *YYPARSE_PARAM
#define YYPARSE_PARAM_DECL
#else /* not __cplusplus */
#define YYPARSE_PARAM_ARG YYPARSE_PARAM
#define YYPARSE_PARAM_DECL void *YYPARSE_PARAM;
#endif /* not __cplusplus */
#else /* not YYPARSE_PARAM */
#define YYPARSE_PARAM_ARG
#define YYPARSE_PARAM_DECL
#endif /* not YYPARSE_PARAM */

int
#ifdef WNT
yyparse()
#else
yyparse(YYPARSE_PARAM_ARG)
     YYPARSE_PARAM_DECL
#endif
{
  register int yystate;
  register int yyn;
  register short *yyssp;
  register YYSTYPE *yyvsp;
  int yyerrstatus;	/*  number of tokens to shift before error messages enabled */
  int yychar1 = 0;		/*  lookahead token as an internal (translated) token number */

  short	yyssa[YYINITDEPTH];	/*  the state stack			*/
  YYSTYPE yyvsa[YYINITDEPTH];	/*  the semantic value stack		*/

  short *yyss = yyssa;		/*  refer to the stacks thru separate pointers */
  YYSTYPE *yyvs = yyvsa;	/*  to allow yyoverflow to reallocate them elsewhere */

#ifdef YYLSP_NEEDED
  YYLTYPE yylsa[YYINITDEPTH];	/*  the location stack			*/
  YYLTYPE *yyls = yylsa;
  YYLTYPE *yylsp;

#define YYPOPSTACK   (yyvsp--, yyssp--, yylsp--)
#else
#define YYPOPSTACK   (yyvsp--, yyssp--)
#endif

  int yystacksize = YYINITDEPTH;

#ifdef YYPURE
  int yychar;
  YYSTYPE yylval;
  int yynerrs;
#ifdef YYLSP_NEEDED
  YYLTYPE yylloc;
#endif
#endif

  YYSTYPE yyval;		/*  the variable used to return		*/
				/*  semantic values from the action	*/
				/*  routines				*/

  int yylen;

#if YYDEBUG != 0
  if (yydebug)
    fprintf(stderr, "Starting parse\n");
#endif

  yystate = 0;
  yyerrstatus = 0;
  yynerrs = 0;
  yychar = YYEMPTY;		/* Cause a token to be read.  */

  /* Initialize stack pointers.
     Waste one element of value and location stack
     so that they stay on the same level as the state stack.
     The wasted elements are never initialized.  */

  yyssp = yyss - 1;
  yyvsp = yyvs;
#ifdef YYLSP_NEEDED
  yylsp = yyls;
#endif

/* Push a new state, which is found in  yystate  .  */
/* In all cases, when you get here, the value and location stacks
   have just been pushed. so pushing a state here evens the stacks.  */
yynewstate:

  *++yyssp = yystate;

  if (yyssp >= yyss + yystacksize - 1)
    {
      /* Give user a chance to reallocate the stack */
      /* Use copies of these so that the &'s don't force the real ones into memory. */
      YYSTYPE *yyvs1 = yyvs;
      short *yyss1 = yyss;
#ifdef YYLSP_NEEDED
      YYLTYPE *yyls1 = yyls;
#endif

      /* Get the current used size of the three stacks, in elements.  */
      int size = yyssp - yyss + 1;

#ifdef yyoverflow
      /* Each stack pointer address is followed by the size of
	 the data in use in that stack, in bytes.  */
#ifdef YYLSP_NEEDED
      /* This used to be a conditional around just the two extra args,
	 but that might be undefined if yyoverflow is a macro.  */
      yyoverflow("parser stack overflow",
		 &yyss1, size * sizeof (*yyssp),
		 &yyvs1, size * sizeof (*yyvsp),
		 &yyls1, size * sizeof (*yylsp),
		 &yystacksize);
#else
      yyoverflow("parser stack overflow",
		 &yyss1, size * sizeof (*yyssp),
		 &yyvs1, size * sizeof (*yyvsp),
		 &yystacksize);
#endif

      yyss = yyss1; yyvs = yyvs1;
#ifdef YYLSP_NEEDED
      yyls = yyls1;
#endif
#else /* no yyoverflow */
      /* Extend the stack our own way.  */
      if (yystacksize >= YYMAXDEPTH)
	{
	  yyerror("parser stack overflow");
	  return 2;
	}
      yystacksize *= 2;
      if (yystacksize > YYMAXDEPTH)
	yystacksize = YYMAXDEPTH;
      yyss = (short *) alloca (yystacksize * sizeof (*yyssp));
#ifdef WNT
      __yy_bcopy ((char *)yyss1, (char *)yyss, size * sizeof (*yyssp));
#else
      __yy_memcpy ((char *)yyss, (char *)yyss1, size * sizeof (*yyssp));
#endif
      yyvs = (YYSTYPE *) alloca (yystacksize * sizeof (*yyvsp));
#ifdef WNT
      __yy_bcopy ((char *)yyvs1, (char *)yyvs, size * sizeof (*yyvsp));
#else
      __yy_memcpy ((char *)yyvs, (char *)yyvs1, size * sizeof (*yyvsp));
#endif
#ifdef YYLSP_NEEDED
      yyls = (YYLTYPE *) alloca (yystacksize * sizeof (*yylsp));
#ifdef WNT
      __yy_bcopy ((char *)yyls1, (char *)yyls, size * sizeof (*yylsp));
#else
      __yy_memcpy ((char *)yyls, (char *)yyls1, size * sizeof (*yylsp));
#endif
#endif
#endif /* no yyoverflow */

      yyssp = yyss + size - 1;
      yyvsp = yyvs + size - 1;
#ifdef YYLSP_NEEDED
      yylsp = yyls + size - 1;
#endif

#if YYDEBUG != 0
      if (yydebug)
	fprintf(stderr, "Stack size increased to %d\n", yystacksize);
#endif

      if (yyssp >= yyss + yystacksize - 1)
	YYABORT;
    }

#if YYDEBUG != 0
  if (yydebug)
    fprintf(stderr, "Entering state %d\n", yystate);
#endif

  goto yybackup;
 yybackup:

/* Do appropriate processing given the current state.  */
/* Read a lookahead token if we need one and don't already have one.  */
/* yyresume: */

  /* First try to decide what to do without reference to lookahead token.  */

  yyn = yypact[yystate];
  if (yyn == YYFLAG)
    goto yydefault;

  /* Not known => get a lookahead token if don't already have one.  */

  /* yychar is either YYEMPTY or YYEOF
     or a valid token in external form.  */

  if (yychar == YYEMPTY)
    {
#if YYDEBUG != 0
      if (yydebug)
	fprintf(stderr, "Reading a token: ");
#endif
      yychar = YYLEX;
    }

  /* Convert token to internal form (in yychar1) for indexing tables with */

  if (yychar <= 0)		/* This means end of input. */
    {
      yychar1 = 0;
      yychar = YYEOF;		/* Don't call YYLEX any more */

#if YYDEBUG != 0
      if (yydebug)
	fprintf(stderr, "Now at end of input.\n");
#endif
    }
  else
    {
      yychar1 = YYTRANSLATE(yychar);

#if YYDEBUG != 0
      if (yydebug)
	{
	  fprintf (stderr, "Next token is %d (%s", yychar, yytname[yychar1]);
	  /* Give the individual parser a way to print the precise meaning
	     of a token, for further debugging info.  */
#ifdef YYPRINT
	  YYPRINT (stderr, yychar, yylval);
#endif
	  fprintf (stderr, ")\n");
	}
#endif
    }

  yyn += yychar1;
  if (yyn < 0 || yyn > YYLAST || yycheck[yyn] != yychar1)
    goto yydefault;

  yyn = yytable[yyn];

  /* yyn is what to do for this token type in this state.
     Negative => reduce, -yyn is rule number.
     Positive => shift, yyn is new state.
       New state is final state => don't bother to shift,
       just return success.
     0, or most negative number => error.  */

  if (yyn < 0)
    {
      if (yyn == YYFLAG)
	goto yyerrlab;
      yyn = -yyn;
      goto yyreduce;
    }
  else if (yyn == 0)
    goto yyerrlab;

  if (yyn == YYFINAL)
    YYACCEPT;

  /* Shift the lookahead token.  */

#if YYDEBUG != 0
  if (yydebug)
    fprintf(stderr, "Shifting token %d (%s), ", yychar, yytname[yychar1]);
#endif

  /* Discard the token being shifted unless it is eof.  */
  if (yychar != YYEOF)
    yychar = YYEMPTY;

  *++yyvsp = yylval;
#ifdef YYLSP_NEEDED
  *++yylsp = yylloc;
#endif

  /* count tokens shifted since error; after three, turn off error status.  */
  if (yyerrstatus) yyerrstatus--;

  yystate = yyn;
  goto yynewstate;

/* Do the default action for the current state.  */
yydefault:

  yyn = yydefact[yystate];
  if (yyn == 0)
    goto yyerrlab;

/* Do a reduction.  yyn is the number of a rule to reduce with.  */
yyreduce:
  yylen = yyr2[yyn];
  if (yylen > 0)
    yyval = yyvsp[1-yylen]; /* implement default value of the action */

#if YYDEBUG != 0
  if (yydebug)
    {
      int i;

      fprintf (stderr, "Reducing via rule %d (line %d), ",
	       yyn, yyrline[yyn]);

      /* Print the symbols being reduced, and their result.  */
      for (i = yyprhs[yyn]; yyrhs[i] > 0; i++)
	fprintf (stderr, "%s ", yytname[yyrhs[i]]);
      fprintf (stderr, " -> %s\n", yytname[yyr1[yyn]]);
    }
#endif


  switch (yyn) {

case 10:
#line 177 "/adv_20/KAS/C40/ros/src/IDLFront/IDL.yacc"
{
	  yyerrok;
        ;
    break;}
case 14:
#line 190 "/adv_20/KAS/C40/ros/src/IDLFront/IDL.yacc"
{ IDL_InterfaceDefinitionBegin(); ;
    break;}
case 15:
#line 190 "/adv_20/KAS/C40/ros/src/IDLFront/IDL.yacc"
{ IDL_InterfaceDefinitionEnd(); ;
    break;}
case 16:
#line 194 "/adv_20/KAS/C40/ros/src/IDLFront/IDL.yacc"
{ IDL_InterfaceDeclaration(); ;
    break;}
case 34:
#line 239 "/adv_20/KAS/C40/ros/src/IDLFront/IDL.yacc"
{ IDL_SetIdentifier(yyvsp[0].str); ;
    break;}
case 150:
#line 502 "/adv_20/KAS/C40/ros/src/IDLFront/IDL.yacc"
{ IDL_SetIdentifier(yyvsp[0].str); ;
    break;}
}
   /* the action file gets copied in in place of this dollarsign */
#line 498 "/PRODUCTS/flexbis-253-125/share/bison.simple"

  yyvsp -= yylen;
  yyssp -= yylen;
#ifdef YYLSP_NEEDED
  yylsp -= yylen;
#endif

#if YYDEBUG != 0
  if (yydebug)
    {
      short *ssp1 = yyss - 1;
      fprintf (stderr, "state stack now");
      while (ssp1 != yyssp)
	fprintf (stderr, " %d", *++ssp1);
      fprintf (stderr, "\n");
    }
#endif

  *++yyvsp = yyval;

#ifdef YYLSP_NEEDED
  yylsp++;
  if (yylen == 0)
    {
      yylsp->first_line = yylloc.first_line;
      yylsp->first_column = yylloc.first_column;
      yylsp->last_line = (yylsp-1)->last_line;
      yylsp->last_column = (yylsp-1)->last_column;
      yylsp->text = 0;
    }
  else
    {
      yylsp->last_line = (yylsp+yylen-1)->last_line;
      yylsp->last_column = (yylsp+yylen-1)->last_column;
    }
#endif

  /* Now "shift" the result of the reduction.
     Determine what state that goes to,
     based on the state we popped back to
     and the rule number reduced by.  */

  yyn = yyr1[yyn];

  yystate = yypgoto[yyn - YYNTBASE] + *yyssp;
  if (yystate >= 0 && yystate <= YYLAST && yycheck[yystate] == *yyssp)
    yystate = yytable[yystate];
  else
    yystate = yydefgoto[yyn - YYNTBASE];

  goto yynewstate;

yyerrlab:   /* here on detecting error */

  if (! yyerrstatus)
    /* If not already recovering from an error, report this error.  */
    {
      ++yynerrs;

#ifdef YYERROR_VERBOSE
      yyn = yypact[yystate];

      if (yyn > YYFLAG && yyn < YYLAST)
	{
	  int size = 0;
	  char *msg;
	  int x, count;

	  count = 0;
	  /* Start X at -yyn if nec to avoid negative indexes in yycheck.  */
	  for (x = (yyn < 0 ? -yyn : 0);
	       x < (sizeof(yytname) / sizeof(char *)); x++)
	    if (yycheck[x + yyn] == x)
	      size += strlen(yytname[x]) + 15, count++;
	  msg = (char *) malloc(size + 15);
	  if (msg != 0)
	    {
	      strcpy(msg, "parse error");

	      if (count < 5)
		{
		  count = 0;
		  for (x = (yyn < 0 ? -yyn : 0);
		       x < (sizeof(yytname) / sizeof(char *)); x++)
		    if (yycheck[x + yyn] == x)
		      {
			strcat(msg, count == 0 ? ", expecting `" : " or `");
			strcat(msg, yytname[x]);
			strcat(msg, "'");
			count++;
		      }
		}
	      yyerror(msg);
	      free(msg);
	    }
	  else
	    yyerror ("parse error; also virtual memory exceeded");
	}
      else
#endif /* YYERROR_VERBOSE */
	yyerror("parse error");
    }

  goto yyerrlab1;
yyerrlab1:   /* here on error raised explicitly by an action */

  if (yyerrstatus == 3)
    {
      /* if just tried and failed to reuse lookahead token after an error, discard it.  */

      /* return failure if at end of input */
      if (yychar == YYEOF)
	YYABORT;

#if YYDEBUG != 0
      if (yydebug)
	fprintf(stderr, "Discarding token %d (%s).\n", yychar, yytname[yychar1]);
#endif

      yychar = YYEMPTY;
    }

  /* Else will try to reuse lookahead token
     after shifting the error token.  */

  yyerrstatus = 3;		/* Each real token shifted decrements this */

  goto yyerrhandle;

yyerrdefault:  /* current state does not do anything special for the error token. */

#if 0
  /* This is wrong; only states that explicitly want error tokens
     should shift them.  */
  yyn = yydefact[yystate];  /* If its default is to accept any token, ok.  Otherwise pop it.*/
  if (yyn) goto yydefault;
#endif

yyerrpop:   /* pop the current state because it cannot handle the error token */

  if (yyssp == yyss) YYABORT;
  yyvsp--;
  yystate = *--yyssp;
#ifdef YYLSP_NEEDED
  yylsp--;
#endif

#if YYDEBUG != 0
  if (yydebug)
    {
      short *ssp1 = yyss - 1;
      fprintf (stderr, "Error: state stack now");
      while (ssp1 != yyssp)
	fprintf (stderr, " %d", *++ssp1);
      fprintf (stderr, "\n");
    }
#endif

yyerrhandle:

  yyn = yypact[yystate];
  if (yyn == YYFLAG)
    goto yyerrdefault;

  yyn += YYTERROR;
  if (yyn < 0 || yyn > YYLAST || yycheck[yyn] != YYTERROR)
    goto yyerrdefault;

  yyn = yytable[yyn];
  if (yyn < 0)
    {
      if (yyn == YYFLAG)
	goto yyerrpop;
      yyn = -yyn;
      goto yyreduce;
    }
  else if (yyn == 0)
    goto yyerrpop;

  if (yyn == YYFINAL)
    YYACCEPT;

#if YYDEBUG != 0
  if (yydebug)
    fprintf(stderr, "Shifting error token, ");
#endif

  *++yyvsp = yylval;
#ifdef YYLSP_NEEDED
  *++yylsp = yylloc;
#endif

  yystate = yyn;
  goto yynewstate;
}
#line 618 "/adv_20/KAS/C40/ros/src/IDLFront/IDL.yacc"

/* programs */

int IDLwrap()
{
  return 1;
}

