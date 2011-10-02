typedef union {
 char str[256];
 double dval;
 int ival;
 char cval;
} YYSTYPE;
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


extern YYSTYPE IDLlval;
