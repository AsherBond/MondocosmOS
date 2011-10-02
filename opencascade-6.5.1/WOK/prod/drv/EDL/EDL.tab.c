
/*  A Bison parser, made from /adv_20/KAS/C40/ros/src/EDL/EDL.yacc
 by  GNU Bison version 1.25
  */

#define YYBISON 1  /* Identify Bison output.  */

#define yyparse EDLparse
#define yylex EDLlex
#define yyerror EDLerror
#define yylval EDLlval
#define yychar EDLchar
#define yydebug EDLdebug
#define yynerrs EDLnerrs
#define	USES	258
#define	TEMPLATE	259
#define	ADDTOTEMPLATE	260
#define	CLEARTEMPLATE	261
#define	END	262
#define	SET	263
#define	UNSET	264
#define	INSTRDEFINED	265
#define	INSTRNOTDEFINED	266
#define	IFDEFINED	267
#define	IFNOTDEFINED	268
#define	IFFILE	269
#define	IFNOTFILE	270
#define	INSTRFILE	271
#define	INSTRNOTFILE	272
#define	IF	273
#define	THEN	274
#define	ELSE	275
#define	ENDIF	276
#define	COUT	277
#define	IS	278
#define	EQ	279
#define	NEQ	280
#define	LOGOR	281
#define	LOGAND	282
#define	SEPARATOR	283
#define	ASTRING	284
#define	AFILE	285
#define	OPENLIB	286
#define	CLOSELIB	287
#define	CALLLIB	288
#define	APPLY	289
#define	WRITE	290
#define	CLOSE	291
#define	VERBOSEON	292
#define	VERBOSEOFF	293
#define	PLUSEQUAL	294
#define	STR	295
#define	IDENT	296
#define	TEMPDEF	297
#define	VAR	298

#line 1 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"

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


#line 59 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
typedef union {
 edlstring str;
 int       ope;
} YYSTYPE;
#include <stdio.h>

#ifndef __cplusplus
#ifndef __STDC__
#define const
#endif
#endif



#define	YYFINAL		248
#define	YYFLAG		-32768
#define	YYNTBASE	51

#define YYTRANSLATE(x) ((unsigned)(x) <= 298 ? yytranslate[x] : 105)

static const char yytranslate[] = {     0,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,    44,
    45,     2,     2,    47,     2,    50,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
    46,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
    48,     2,    49,     2,     2,     2,     2,     2,     2,     2,
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
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     1,     2,     3,     4,     5,
     6,     7,     8,     9,    10,    11,    12,    13,    14,    15,
    16,    17,    18,    19,    20,    21,    22,    23,    24,    25,
    26,    27,    28,    29,    30,    31,    32,    33,    34,    35,
    36,    37,    38,    39,    40,    41,    42,    43
};

#if YYDEBUG != 0
static const short yyprhs[] = {     0,
     0,     2,     5,     6,     8,    10,    12,    14,    16,    18,
    20,    22,    24,    26,    28,    30,    32,    34,    36,    38,
    40,    42,    44,    46,    48,    50,    54,    58,    61,    64,
    65,    76,    78,    79,    87,    91,    93,    95,    98,    99,
   106,   107,   109,   113,   114,   121,   122,   129,   130,   137,
   138,   145,   146,   155,   156,   165,   173,   177,   179,   181,
   184,   185,   191,   197,   203,   211,   219,   227,   237,   241,
   247,   248,   253,   254,   258,   263,   264,   270,   272,   275,
   276,   278,   280,   285,   287,   289,   294,   298,   302,   311,
   322,   324,   328,   329,   331,   333,   337,   339,   342,   346,
   350,   354,   359,   364,   369,   374,   379,   384,   386,   388,
   390
};

static const short yyrhs[] = {    52,
     0,    51,    52,     0,     0,    53,     0,    59,     0,    61,
     0,    56,     0,    73,     0,    75,     0,    77,     0,    82,
     0,    83,     0,    84,     0,    86,     0,    92,     0,    67,
     0,    70,     0,    96,     0,   100,     0,    64,     0,    97,
     0,    94,     0,    95,     0,    55,     0,    54,     0,     3,
    40,    28,     0,     3,    43,    28,     0,    38,    28,     0,
    37,    28,     0,     0,    58,    62,    57,    44,    66,    45,
    23,    63,     7,    28,     0,     4,     0,     0,     5,    62,
    60,    23,    63,     7,    28,     0,     6,    62,    28,     0,
    41,     0,    42,     0,    63,    42,     0,     0,    34,    43,
    46,    41,    65,    28,     0,     0,    43,     0,    66,    47,
    43,     0,     0,    14,    44,    43,    45,    68,    78,     0,
     0,    14,    44,    40,    45,    69,    78,     0,     0,    15,
    44,    43,    45,    71,    78,     0,     0,    15,    44,    40,
    45,    72,    78,     0,     0,    12,    44,    79,    45,    74,
    19,    51,    80,     0,     0,    13,    44,    79,    45,    76,
    19,    51,    80,     0,    18,    44,   101,    45,    19,    51,
    80,     0,    19,    51,    80,     0,    43,     0,    41,     0,
    21,    28,     0,     0,    20,    81,    51,    21,    28,     0,
     8,    43,    46,    40,    28,     0,     8,    43,    46,    43,
    28,     0,     8,    43,    46,    48,    43,    49,    28,     0,
     8,    48,    43,    49,    46,    40,    28,     0,     8,    48,
    43,    49,    46,    43,    28,     0,     8,    48,    43,    49,
    46,    48,    43,    49,    28,     0,     9,    43,    28,     0,
     9,    48,    43,    49,    28,     0,     0,    22,    85,    90,
    28,     0,     0,    29,    87,    88,     0,    43,    46,    90,
    28,     0,     0,    43,    39,    89,    90,    28,     0,    91,
     0,    90,    91,     0,     0,    43,     0,    40,     0,    30,
    41,    93,    28,     0,    43,     0,    40,     0,    35,    41,
    43,    28,     0,    36,    41,    28,     0,    31,    41,    28,
     0,    33,    41,    50,    41,    44,    98,    45,    28,     0,
    33,    43,    46,    41,    50,    41,    44,    98,    45,    28,
     0,    99,     0,    98,    47,    99,     0,     0,    43,     0,
    40,     0,    32,    41,    28,     0,   102,     0,   101,   102,
     0,    43,   103,    40,     0,   102,   104,   102,     0,    44,
   102,    45,     0,    10,    44,    79,    45,     0,    11,    44,
    79,    45,     0,    16,    44,    43,    45,     0,    17,    44,
    43,    45,     0,    16,    44,    40,    45,     0,    17,    44,
    40,    45,     0,    24,     0,    25,     0,    27,     0,    26,
     0
};

#endif

#if YYDEBUG != 0
static const short yyrline[] = { 0,
    65,    66,    68,    69,    70,    71,    72,    73,    74,    75,
    76,    77,    78,    79,    80,    81,    82,    83,    84,    85,
    86,    87,    88,    89,    90,    93,    94,    97,   100,   103,
   103,   106,   109,   109,   112,   115,   118,   119,   122,   122,
   125,   126,   127,   130,   131,   131,   132,   135,   136,   136,
   137,   139,   139,   141,   141,   143,   146,   149,   150,   154,
   155,   155,   158,   159,   160,   161,   162,   163,   166,   167,
   170,   170,   173,   174,   176,   177,   177,   180,   181,   184,
   185,   186,   190,   193,   194,   197,   200,   203,   206,   207,
   210,   211,   214,   215,   216,   219,   222,   223,   226,   227,
   228,   229,   230,   231,   232,   233,   234,   237,   238,   241,
   242
};
#endif


#if YYDEBUG != 0 || defined (YYERROR_VERBOSE)

static const char * const yytname[] = {   "$","error","$undefined.","USES","TEMPLATE",
"ADDTOTEMPLATE","CLEARTEMPLATE","END","SET","UNSET","INSTRDEFINED","INSTRNOTDEFINED",
"IFDEFINED","IFNOTDEFINED","IFFILE","IFNOTFILE","INSTRFILE","INSTRNOTFILE","IF",
"THEN","ELSE","ENDIF","COUT","IS","EQ","NEQ","LOGOR","LOGAND","SEPARATOR","ASTRING",
"AFILE","OPENLIB","CLOSELIB","CALLLIB","APPLY","WRITE","CLOSE","VERBOSEON","VERBOSEOFF",
"PLUSEQUAL","STR","IDENT","TEMPDEF","VAR","'('","')'","'='","','","'['","']'",
"'.'","statements","statement","use_dec","verboseoff_dec","verboseon_dec","template_dec",
"@1","templatehead","addtotemplate_dec","@2","cleartemplate_dec","identifier",
"tempdefs","apply_dec","@3","listvars","iffileexists_dec","@4","@5","iffilenotexists_dec",
"@6","@7","ifdefined_dec","@8","ifnotdefined_dec","@9","if_dec","if_follow",
"ifdefcond","if_end_dec","@10","set_dec","unset_dec","cout_dec","@11","string_dec",
"@12","string_end_dec","@13","printlists","printlist","file_dec","filename",
"write_dec","close_dec","openlib_dec","calllib_dec","arglists","arglist","closelib_dec",
"conditions","condition","logoperator","expr_operator", NULL
};
#endif

static const short yyr1[] = {     0,
    51,    51,    52,    52,    52,    52,    52,    52,    52,    52,
    52,    52,    52,    52,    52,    52,    52,    52,    52,    52,
    52,    52,    52,    52,    52,    53,    53,    54,    55,    57,
    56,    58,    60,    59,    61,    62,    63,    63,    65,    64,
    66,    66,    66,    68,    67,    69,    67,    71,    70,    72,
    70,    74,    73,    76,    75,    77,    78,    79,    79,    80,
    81,    80,    82,    82,    82,    82,    82,    82,    83,    83,
    85,    84,    87,    86,    88,    89,    88,    90,    90,    91,
    91,    91,    92,    93,    93,    94,    95,    96,    97,    97,
    98,    98,    99,    99,    99,   100,   101,   101,   102,   102,
   102,   102,   102,   102,   102,   102,   102,   103,   103,   104,
   104
};

static const short yyr2[] = {     0,
     1,     2,     0,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     3,     3,     2,     2,     0,
    10,     1,     0,     7,     3,     1,     1,     2,     0,     6,
     0,     1,     3,     0,     6,     0,     6,     0,     6,     0,
     6,     0,     8,     0,     8,     7,     3,     1,     1,     2,
     0,     5,     5,     5,     7,     7,     7,     9,     3,     5,
     0,     4,     0,     3,     4,     0,     5,     1,     2,     0,
     1,     1,     4,     1,     1,     4,     3,     3,     8,    10,
     1,     3,     0,     1,     1,     3,     1,     2,     3,     3,
     3,     4,     4,     4,     4,     4,     4,     1,     1,     1,
     1
};

static const short yydefact[] = {     3,
     0,    32,     0,     0,     0,     0,     0,     0,     0,     0,
     0,    71,    73,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     1,     4,    25,    24,     7,     0,     5,
     6,    20,    16,    17,     8,     9,    10,    11,    12,    13,
    14,    15,    22,    23,    18,    21,    19,     0,     0,    36,
    33,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,    80,     0,     0,     0,     0,     0,     0,     0,     0,
     0,    29,    28,     2,    30,    26,    27,     0,    35,     0,
     0,    69,     0,    59,    58,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,    97,    82,
    81,     0,    78,     0,    74,    85,    84,     0,    88,    96,
     0,     0,     0,     0,    87,     0,     0,     0,     0,     0,
     0,     0,    52,    54,    46,    44,    50,    48,     0,     0,
     0,     0,   108,   109,     0,     0,     0,    98,   111,   110,
     0,    72,    79,    76,    80,    83,     0,     0,    39,    86,
    41,    37,     0,    63,    64,     0,     0,    70,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
    99,   101,     3,   100,    80,     0,    93,     0,     0,    42,
     0,     0,    38,     0,     0,     0,     0,     3,     3,     3,
    47,    45,    51,    49,   102,   103,   106,   104,   107,   105,
     0,     0,    75,    95,    94,     0,    91,     0,    40,     0,
     0,    34,    65,    66,    67,     0,     0,     0,     0,    61,
     0,    56,    77,     0,    93,    93,     0,    43,     0,    53,
    55,    57,     3,    60,    89,    92,     0,     0,    68,     0,
     0,     0,     0,    90,    31,    62,     0,     0
};

static const short yydefgoto[] = {    23,
    24,    25,    26,    27,    28,   116,    29,    30,    78,    31,
    51,   153,    32,   179,   181,    33,   162,   161,    34,   164,
   163,    35,   159,    36,   160,    37,   191,    86,   222,   233,
    38,    39,    40,    62,    41,    63,   105,   175,   102,   103,
    42,   108,    43,    44,    45,    46,   206,   207,    47,    98,
    99,   135,   141
};

static const short yypact[] = {   150,
    26,-32768,    19,    19,   -37,   -20,   -27,    -6,    28,    39,
    46,-32768,-32768,    59,    96,   120,     6,   106,   151,   152,
   169,   177,    21,-32768,-32768,-32768,-32768,-32768,    19,-32768,
-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,
-32768,-32768,-32768,-32768,-32768,-32768,-32768,   178,   179,-32768,
-32768,   180,   163,   167,   183,   170,   134,   134,    49,    76,
    54,   117,   171,   126,   184,   187,   166,   172,   173,   181,
   192,-32768,-32768,-32768,-32768,-32768,-32768,   198,-32768,    85,
   174,-32768,   176,-32768,-32768,   182,   185,   186,   188,   189,
   190,   193,   194,   195,   196,     7,    54,    -3,    41,-32768,
-32768,    56,-32768,   -24,-32768,-32768,-32768,   200,-32768,-32768,
   191,   201,   202,   208,-32768,   197,   203,   216,   218,   204,
   205,   220,-32768,-32768,-32768,-32768,-32768,-32768,   134,   134,
   127,   131,-32768,-32768,   209,    35,   207,    41,-32768,-32768,
    54,-32768,-32768,-32768,   117,-32768,   206,   210,-32768,-32768,
   211,-32768,     2,-32768,-32768,   212,    86,-32768,   233,   234,
   236,   236,   236,   236,   213,   214,   217,   219,   221,   222,
-32768,-32768,   150,    41,   117,    92,   133,   215,   229,-32768,
   105,   235,-32768,   237,   240,   241,   227,   150,   150,   150,
-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,
    73,   108,-32768,-32768,-32768,   156,-32768,   228,-32768,   199,
   230,-32768,-32768,-32768,-32768,   225,    73,    73,    73,-32768,
   243,-32768,-32768,   247,   133,   133,   203,-32768,   248,-32768,
-32768,-32768,   150,-32768,-32768,-32768,   157,     3,-32768,   109,
   249,   250,   251,-32768,-32768,-32768,   280,-32768
};

static const short yypgoto[] = {  -170,
   -23,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,
     8,    55,-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,
-32768,-32768,-32768,-32768,-32768,-32768,    27,   -56,   -19,-32768,
-32768,-32768,-32768,-32768,-32768,-32768,-32768,-32768,  -129,  -101,
-32768,-32768,-32768,-32768,-32768,-32768,    57,     4,-32768,-32768,
   -93,-32768,-32768
};


#define	YYLAST		283


static const short yytable[] = {    74,
   143,    87,   201,   136,   138,    53,    92,    93,   182,   242,
    54,    52,    94,    95,   144,   176,    57,   217,   218,   219,
   247,   145,    55,     1,     2,     3,     4,    56,     5,     6,
   133,   134,     7,     8,     9,    10,    75,    58,    11,    96,
    97,   137,    12,   183,   183,   202,    67,   174,    68,    13,
    14,    15,    16,    17,    18,    19,    20,    21,    22,    50,
   139,   140,   240,    92,    93,    48,   139,   140,    49,    94,
    95,    59,   165,   166,   143,     1,     2,     3,     4,   172,
     5,     6,    60,   142,     7,     8,     9,    10,    88,    61,
    11,    89,   220,   221,    12,   100,    96,    97,   101,    64,
   143,    13,    14,    15,    16,    17,    18,    19,    20,    21,
    22,     1,     2,     3,     4,    90,     5,     6,    91,   203,
     7,     8,     9,    10,   118,   185,    11,   119,   186,   243,
    12,   100,   120,   187,   101,   223,    65,    13,    14,    15,
    16,    17,    18,    19,    20,    21,    22,   100,    69,   210,
   101,   211,     1,     2,     3,     4,   100,     5,     6,   101,
    66,     7,     8,     9,    10,   106,   167,    11,   107,   168,
   169,    12,   204,   170,    84,   205,    85,    74,    13,    14,
    15,    16,    17,    18,    19,    20,    21,    22,   192,   193,
   194,    70,    71,    74,    74,    74,    72,   230,   231,   232,
   224,   241,   225,   225,    73,    76,    77,    79,    80,    81,
    82,   109,    83,   104,   110,   111,    74,   112,   113,   115,
   117,   227,   121,   114,   122,   173,   123,   146,   236,   124,
   125,   147,   126,   127,   128,   150,   129,   130,   131,   132,
   151,   148,   149,   154,   152,   155,   156,   158,   171,   177,
   157,   188,   189,   180,   190,   208,   209,   195,   196,   178,
   184,   197,   212,   198,   213,   199,   200,   214,   215,   216,
   234,   226,   228,   229,   235,   239,   244,   245,   246,   248,
     0,   238,   237
};

static const short yycheck[] = {    23,
   102,    58,   173,    97,    98,    43,    10,    11,     7,     7,
    48,     4,    16,    17,    39,   145,    44,   188,   189,   190,
     0,    46,    43,     3,     4,     5,     6,    48,     8,     9,
    24,    25,    12,    13,    14,    15,    29,    44,    18,    43,
    44,    45,    22,    42,    42,   175,    41,   141,    43,    29,
    30,    31,    32,    33,    34,    35,    36,    37,    38,    41,
    26,    27,   233,    10,    11,    40,    26,    27,    43,    16,
    17,    44,   129,   130,   176,     3,     4,     5,     6,    45,
     8,     9,    44,    28,    12,    13,    14,    15,    40,    44,
    18,    43,    20,    21,    22,    40,    43,    44,    43,    41,
   202,    29,    30,    31,    32,    33,    34,    35,    36,    37,
    38,     3,     4,     5,     6,    40,     8,     9,    43,    28,
    12,    13,    14,    15,    40,    40,    18,    43,    43,    21,
    22,    40,    48,    48,    43,    28,    41,    29,    30,    31,
    32,    33,    34,    35,    36,    37,    38,    40,    43,    45,
    43,    47,     3,     4,     5,     6,    40,     8,     9,    43,
    41,    12,    13,    14,    15,    40,    40,    18,    43,    43,
    40,    22,    40,    43,    41,    43,    43,   201,    29,    30,
    31,    32,    33,    34,    35,    36,    37,    38,   162,   163,
   164,    41,    41,   217,   218,   219,    28,   217,   218,   219,
    45,    45,    47,    47,    28,    28,    28,    28,    46,    43,
    28,    28,    43,    43,    28,    50,   240,    46,    46,    28,
    23,    23,    49,    43,    49,    19,    45,    28,   225,    45,
    45,    41,    45,    45,    45,    28,    44,    44,    44,    44,
    44,    41,    41,    28,    42,    28,    43,    28,    40,    44,
    46,    19,    19,    43,    19,    41,    28,    45,    45,    50,
    49,    45,    28,    45,    28,    45,    45,    28,    28,    43,
    28,    44,    43,    49,    28,    28,    28,    28,    28,     0,
    -1,   227,   226
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

case 26:
#line 93 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{  edl_uses(yyvsp[-1].str); ;
    break;}
case 27:
#line 94 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{  edl_uses_var(yyvsp[-1].str); ;
    break;}
case 30:
#line 103 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_create_template(yyvsp[0].str); ;
    break;}
case 31:
#line 103 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_end_template(); ;
    break;}
case 33:
#line 109 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_set_template(yyvsp[0].str); ;
    break;}
case 34:
#line 109 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_end_template(); ;
    break;}
case 35:
#line 112 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_clear_template(yyvsp[-1].str); ;
    break;}
case 37:
#line 118 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_add_to_template(yyvsp[0].str); ;
    break;}
case 38:
#line 119 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_add_to_template(yyvsp[0].str); ;
    break;}
case 39:
#line 122 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_apply_template(yyvsp[0].str); ;
    break;}
case 40:
#line 122 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_end_apply(yyvsp[-4].str); ;
    break;}
case 42:
#line 126 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_add_to_varlist(yyvsp[0].str);  ;
    break;}
case 43:
#line 127 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_add_to_varlist(yyvsp[0].str); ;
    break;}
case 44:
#line 130 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_fileexist_var(yyvsp[-1].str); ;
    break;}
case 46:
#line 131 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_fileexist(yyvsp[-1].str); ;
    break;}
case 48:
#line 135 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_filenotexist_var(yyvsp[-1].str); ;
    break;}
case 50:
#line 136 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_filenotexist(yyvsp[-1].str); ;
    break;}
case 52:
#line 139 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_isvardefined(yyvsp[-1].str); ;
    break;}
case 54:
#line 141 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_isvarnotdefined(yyvsp[-1].str); ;
    break;}
case 60:
#line 154 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_clear_execution_status(); ;
    break;}
case 61:
#line 155 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_else_execution_status(); ;
    break;}
case 62:
#line 155 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_clear_execution_status(); ;
    break;}
case 63:
#line 158 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_set_var(yyvsp[-3].str,yyvsp[-1].str); edlstring_free(yyvsp[-3].str); edlstring_free(yyvsp[-1].str);;
    break;}
case 64:
#line 159 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_set_varvar(yyvsp[-3].str,yyvsp[-1].str); edlstring_free(yyvsp[-3].str); edlstring_free(yyvsp[-1].str);;
    break;}
case 65:
#line 160 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_set_varevalvar(yyvsp[-5].str,yyvsp[-2].str); edlstring_free(yyvsp[-5].str); edlstring_free(yyvsp[-2].str);;
    break;}
case 66:
#line 161 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_set_pvar(yyvsp[-4].str,yyvsp[-1].str); edlstring_free(yyvsp[-4].str); edlstring_free(yyvsp[-1].str);;
    break;}
case 67:
#line 162 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_set_pvarvar(yyvsp[-4].str,yyvsp[-1].str); edlstring_free(yyvsp[-4].str); edlstring_free(yyvsp[-1].str);;
    break;}
case 68:
#line 163 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_set_pvarevalvar(yyvsp[-6].str,yyvsp[-2].str); edlstring_free(yyvsp[-6].str); edlstring_free(yyvsp[-2].str);;
    break;}
case 69:
#line 166 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_unset_var(yyvsp[-1].str); edlstring_free(yyvsp[-1].str); ;
    break;}
case 70:
#line 167 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_unset_pvar(yyvsp[-2].str); edlstring_free(yyvsp[-2].str); ;
    break;}
case 71:
#line 170 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_clear_printlist(); ;
    break;}
case 72:
#line 170 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_cout(); ;
    break;}
case 73:
#line 173 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_clear_printlist(); ;
    break;}
case 75:
#line 176 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_create_string_var(yyvsp[-3].str); ;
    break;}
case 76:
#line 177 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_printlist_addps_var(yyvsp[-1].str); ;
    break;}
case 77:
#line 177 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_create_string_var(yyvsp[-4].str); ;
    break;}
case 81:
#line 185 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_printlist_add_var(yyvsp[0].str); ;
    break;}
case 82:
#line 186 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_printlist_add_str(yyvsp[0].str); ;
    break;}
case 83:
#line 190 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_open_file(yyvsp[-2].str,yyvsp[-1].str); ;
    break;}
case 84:
#line 193 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_set_varname(); ;
    break;}
case 85:
#line 194 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_set_str();     ;
    break;}
case 86:
#line 197 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_write_file(yyvsp[-2].str,yyvsp[-1].str); ;
    break;}
case 87:
#line 200 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_close_file(yyvsp[-1].str); ;
    break;}
case 88:
#line 203 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_open_library(yyvsp[-1].str); ;
    break;}
case 89:
#line 206 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_call_procedure_library(yyvsp[-6].str,yyvsp[-4].str); ;
    break;}
case 90:
#line 207 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_call_function_library(yyvsp[-6].str,yyvsp[-4].str,yyvsp[-8].str); ;
    break;}
case 94:
#line 215 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_arglist_add_var(yyvsp[0].str); ;
    break;}
case 95:
#line 216 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_arglist_add_str(yyvsp[0].str); ;
    break;}
case 96:
#line 219 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_close_library(yyvsp[-1].str); ;
    break;}
case 97:
#line 222 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_eval_condition(); ;
    break;}
case 99:
#line 226 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_test_condition(yyvsp[-2].str,yyvsp[-1].ope,yyvsp[0].str); ;
    break;}
case 100:
#line 227 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{ edl_eval_local_condition(yyvsp[-1].ope); ;
    break;}
case 102:
#line 229 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_isvardefinedm(yyvsp[-1].str); ;
    break;}
case 103:
#line 230 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_isvarnotdefinedm(yyvsp[-1].str); ;
    break;}
case 104:
#line 231 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_fileexist_varm(yyvsp[-1].str); ;
    break;}
case 105:
#line 232 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_filenotexist_varm(yyvsp[-1].str); ;
    break;}
case 106:
#line 233 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_fileexistm(yyvsp[-1].str); ;
    break;}
case 107:
#line 234 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"
{edl_filenotexistm(yyvsp[-1].str); ;
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
#line 245 "/adv_20/KAS/C40/ros/src/EDL/EDL.yacc"



