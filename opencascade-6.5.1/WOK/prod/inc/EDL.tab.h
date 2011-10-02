typedef union {
 edlstring str;
 int       ope;
} YYSTYPE;
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


extern YYSTYPE EDLlval;
