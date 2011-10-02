#ifdef WNT
/******************************************************************************/
/* Extended regular expression matching and search.                           */
/* Copyright (C) 1985 Free Software Foundation, Inc.                          */
/*                                                                            */
/* This program is free software; you can redistribute it and/or              */
/* modify it under the terms of the GNU Lesser General Public                 */
/* License as published by the Free Software Foundation; either               */
/* version 2.1 of the License, or (at your option) any later version.         */
/*                                                                            */
/*  This program is distributed in the hope that it will be useful,           */
/*  but WITHOUT ANY WARRANTY; without even the implied warranty of            */
/*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             */
/*  GNU Lesser General Public License for more details.                       */
/*                                                                            */
/*  You should have received a copy of the GNU Lesser General Public          */
/*  License along with the GNU C Library; if not, write to the Free           */
/*  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA         */
/*  02111-1307 USA.                                                           */
/******************************************************************************/
/******************************************************************************/
/* Modified by EUG ( MATRA Datavision ) for Windows NT and UNICODE ( 1996 )   */
/******************************************************************************/
/***/


#include <WOKNT_regexp.h>

/***/
/******************************************************************************/
/*                                                                            */
/* Define the syntax stuff, so we can do the \<...\> things.                  */
/*                                                                            */
/******************************************************************************/
/***/
#ifndef Sword   /* must be non-zero in some of the tests below...             */
# define Sword 1
#endif  /* Sword */

#define SYNTAX( c ) re_syntax_table[ ( c ) ]

TCHAR* re_syntax_table;
/***/
/******************************************************************************/
/*                                                                            */
/* Number of failure points to allocate space for initially,                  */
/* when matching.  If this number is exceeded, more space is allocated,       */
/* so it is not a hard limit.                                                 */
/*                                                                            */
/******************************************************************************/
/***/
#ifndef NFAILURES
# define NFAILURES 80
#endif  /* NFAILURES */
/***/
#ifndef SIGN_EXTEND_CHAR
# define SIGN_EXTEND_CHAR( x ) ( x )
#endif  /* SIGN_EXTEND_CHAR */
/***/
static int obscure_syntax;
/***/
/******************************************************************************/
/*                                                                            */
/* Specify the precise syntax of regexp for compilation.                      */
/* This provides for compatibility for various utilities                      */
/* which historically have different, incompatible syntaxes.                  */
/*                                                                            */
/* The argument SYNTAX is a bit-mask containing the two bits                  */
/* RE_NO_BK_PARENS and RE_NO_BK_VBAR.                                         */
/*                                                                            */
/******************************************************************************/
/***/
int re_set_syntax ( int syntax ) {

 int ret;

 ret = obscure_syntax;
 obscure_syntax = syntax;

 return ret;

}  /* end re_set_syntax */
/***/
/******************************************************************************/
/*                                                                            */
/* re_compile_pattern takes a regular-expression string                       */
/*  and converts it into a buffer full of byte commands for matching.         */
/*                                                                            */
/* PATTERN   is the address of the pattern string                             */
/* SIZE      is the length of it.                                             */
/* BUFP	     is a  struct re_pattern_buffer *  which points to the info       */
/*           on where to store the byte commands.                             */
/*           This structure contains a  char *  which points to the           */
/*           actual space, which should have been obtained with malloc.       */
/*           re_compile_pattern may use  realloc  to grow the buffer space.   */
/*                                                                            */
/*  The number of bytes of commands can be found out by looking in            */
/*  the  struct re_pattern_buffer  that bufp pointed to,                      */
/*  after re_compile_pattern returns.                                         */
/*                                                                            */
/******************************************************************************/
/***/
#define PATPUSH( ch ) (  *b++ = ( _TCHAR )( ch )  )

#define PATFETCH( c ) { if ( p == pend ) goto end_of_pattern;        \
                        ( c ) = *( _TUCHAR* )p++;                    \
                        if ( translate ) ( c ) = translate[ ( c ) ]; \
                      }

#define PATFETCH_RAW( c ) { if ( p == pend ) goto end_of_pattern; \
                            ( c ) = *( _TUCHAR* )p++;             \
                          }

#define PATUNFETCH p--

#define EXTEND_BUFFER { _TCHAR* old_buffer = bufp -> buffer;      \
                        if (  bufp -> allocated == ( 1 << 16 )  ) \
                         goto too_big;                            \
                        bufp -> allocated *= 2;                   \
                        if (  bufp -> allocated > ( 1 << 16 )  )  \
                         bufp -> allocated = ( 1 << 16 );         \
                        if (   !(  bufp -> buffer =               \
                                    ( _TCHAR* )realloc (          \
                                                bufp -> buffer,   \
                                                bufp -> allocated \
                                               )                  \
                                )                                 \
                        ) goto memory_exhausted;                  \
                        c = bufp -> buffer - old_buffer;          \
                        b += c;                                   \
                        if ( fixup_jump ) fixup_jump += c;        \
                        if ( laststart ) laststart += c;          \
                        begalt += c;                              \
                        if ( pending_exact )                      \
                         pending_exact += ( long )c;              \
                      }

static void store_jump  ( _TCHAR*, _TCHAR, _TCHAR* );
static void insert_jump ( _TCHAR, _TCHAR*, _TCHAR*, _TCHAR* );

_TCHAR* re_compile_pattern (
         _TCHAR* pattern, int size, PRE_PATTERN_BUFFER bufp
        ) {

 _TCHAR*  b = bufp -> buffer;
 _TCHAR*  p = pattern;
 _TCHAR*  pend = pattern + size;
 _TCHAR*  p1;
 _TUCHAR* translate = ( _TUCHAR* )bufp -> translate;

 unsigned long c, c1;

/* address of the count-byte of the most recently inserted "exactn" command.  */
/*  This makes it possible to tell whether a new exact-match character        */
/*  can be added to that command or requires a new "exactn" command.          */
     
 _TCHAR* pending_exact = NULL;

/* address of the place where a forward-jump should go                        */
/*  to the end of the containing expression.                                  */
/*  Each alternative of an "or", except the last, ends with a forward-jump    */
/*  of this sort.                                                             */

 _TCHAR* fixup_jump = NULL;

/* address of start of the most recently finished expression.                 */
/*  This tells postfix * where to find the start of its operand.              */

 _TCHAR* laststart = NULL;

/* In processing a repeat, 1 means zero matches is allowed                    */

 _TCHAR zero_times_ok;

/* In processing a repeat, 1 means many matches is allowed                    */

 _TCHAR many_times_ok;

/* address of beginning of regexp, or inside of last \(                       */

 _TCHAR* begalt = b;

/* Stack of information saved by \( and restored by \).                       */
/*   Four stack elements are pushed by each \(:                               */
/*     First, the value of b.                                                 */
/*     Second, the value of fixup_jump.                                       */
/*     Third, the value of regnum.                                            */
/*     Fourth, the value of begalt.                                           */

 long  stackb[ 40 ];
 long* stackp = stackb;
 long* stacke = stackb + 40;
 long* stackt;

/* Counts \('s as they are encountered.  Remembered for the matching \),      */
/*   where it becomes the "register number" to put in the stop_memory command */

 int regnum = 1;

 bufp -> fastmap_accurate = 0;

 if ( bufp -> allocated == 0 ) {

  bufp -> allocated = 28;
      
  if ( bufp -> buffer )
	/* EXTEND_BUFFER loses when bufp->allocated is 0 */
   bufp -> buffer = ( _TCHAR* )realloc (  bufp -> buffer, 28 * sizeof ( _TCHAR )  );
  
  else
	/* Caller did not allocate a buffer.  Do it for him */
   bufp -> buffer = ( _TCHAR* ) malloc (  28 * sizeof ( _TCHAR )  );

  if ( !bufp -> buffer ) goto memory_exhausted;

  begalt = b = bufp -> buffer;
    
 }  /* end if */

 while ( p != pend ) {

  if (  ( int )( b - bufp -> buffer ) >
        ( int )(  bufp -> allocated - 10 * sizeof ( _TCHAR )  )
  )
	
   EXTEND_BUFFER; /* Note that EXTEND_BUFFER clobbers c */

  PATFETCH( c );

  switch ( c ) {

   case _TEXT( '$' ):

    if ( obscure_syntax & RE_TIGHT_VBAR ) {

	 if (  !( obscure_syntax & RE_CONTEXT_INDEP_OPS ) && p != pend  ) goto normal_char;
	      /* Make operand of last vbar end before this `$'.  */
	 if ( fixup_jump ) store_jump ( fixup_jump, jump, b );
	      
      fixup_jump = NULL;
     PATPUSH( endline );
	      
     break;
    
    }  /* end if */

	/* $ means succeed if at end of line, but only in special contexts.    */
	/*   If randomly in the middle of a pattern, it is a normal character. */
	if (  p == pend                                ||
         *p == _TEXT( '\n' )                       ||
         ( obscure_syntax & RE_CONTEXT_INDEP_OPS ) ||
         ( obscure_syntax & RE_NO_BK_PARENS ?
            *p == _TEXT( ')'  )             :
            *p == _TEXT( '\\' ) &&
             p[ 1 ] == _TEXT( ')' )
         )                                         ||
	     ( obscure_syntax & RE_NO_BK_VBAR ?
          *p == _TEXT( '|'  )             :
          *p == _TEXT( '\\' ) &&
           p[ 1 ] == _TEXT( '|' )
         )
    ) {

     PATPUSH( endline );

     break;

	}  /* end if */

	goto normal_char;

   case _TEXT( '^' ):
	  /* ^ means succeed if at beg of line, but only if no preceding pattern. */

    if (  laststart && p[ -2 ] != _TEXT( '\n' )      &&
          !( obscure_syntax & RE_CONTEXT_INDEP_OPS )
    ) goto normal_char;

    if ( obscure_syntax & RE_TIGHT_VBAR ) {

     if ( p != pattern + 1                           &&
          !( obscure_syntax & RE_CONTEXT_INDEP_OPS )
     ) goto normal_char;

	 PATPUSH( begline );
	 begalt = b;

	    
    } else PATPUSH( begline );

   break;

   case _TEXT( '+' ):
   case _TEXT( '?' ):

    if ( obscure_syntax & RE_BK_PLUS_QM ) goto normal_char;

handle_plus:

   case _TEXT( '*' ):
	  /* If there is no previous pattern, char not special. */

    if (  !laststart && !( obscure_syntax & RE_CONTEXT_INDEP_OPS )  ) goto normal_char;

	  /* If there is a sequence of repetition chars,  */
	  /*  collapse it down to equivalent to just one. */
	zero_times_ok = 0;
	many_times_ok = 0;

	while ( 1 ) {

	 zero_times_ok |= c != _TEXT( '+' );
	 many_times_ok |= c != _TEXT( '?' );

	 if ( p == pend ) break;

	 PATFETCH( c );

	 if (  c == _TEXT( '*' )  )
	  ;
	 else if (  !( obscure_syntax & RE_BK_PLUS_QM )           &&
                 (  c == _TEXT( '+' ) || c == _TEXT( '?' )  )
          );
     else if (  ( obscure_syntax & RE_BK_PLUS_QM ) && c == _TEXT( '\\' )  ) {

      long c1;

      PATFETCH( c1 );

      if (   !(  c1 == _TEXT( '+' ) || c1 == _TEXT( '?' )  )   ) {

       PATUNFETCH;
       PATUNFETCH;

       break;
		    
      }  /* end if */

      c = c1;

     } else {

      PATUNFETCH;

      break;

     }  /* end else */

	}  /* end while */

	/* Star, etc. applied to an empty pattern is equivalent */
	/*   to an empty pattern.                               */
	  
    if ( !laststart ) break;

	/* Now we know whether 0 matches is allowed,   */
	/*   and whether 2 or more matches is allowed. */

	if ( many_times_ok ) {
     /* If more than one repetition is allowed, */
	 /*  put in a backward jump at the end.     */

	 store_jump ( b, maybe_finalize_jump, laststart - 3L );
     b += 3;

	}  /* end if */

	insert_jump ( on_failure_jump, laststart, b + 3, b );
    pending_exact = NULL;
    b += 3;

    if ( !zero_times_ok ) {
	 /* At least one repetition required: insert before the loop */
	 /*  a skip over the initial on-failure-jump instruction     */

     insert_jump ( dummy_failure_jump, laststart, laststart + 6L, b );
	 b += 3;
	    
    }  /* end if */

   break;

   case _TEXT( '.' ):

    laststart = b;
    PATPUSH( anychar );

   break;

   case _TEXT( '[' ):

    while (  b - bufp -> buffer  >
		     ( int )( bufp->allocated - 3 - ( 1 << BYTEWIDTH ) / BYTEWIDTH )
    ) /* Note that EXTEND_BUFFER clobbers c */ EXTEND_BUFFER;

    laststart = b;
	  
    if (  *p == _TEXT( '^' )  )

     PATPUSH( charset_not ), p++;
	  
    else

	 PATPUSH( charset );

    p1 = p;
    PATPUSH(  ( 1 << BYTEWIDTH ) / BYTEWIDTH  );
	/* Clear the whole map */
	memset (  b, 0, ( 1 << BYTEWIDTH ) / BYTEWIDTH * sizeof ( TCHAR )  );

	/* Read in characters and ranges, setting map bits */
    while ( 1 ) {

	 PATFETCH( c );

	 if (  c == _TEXT( ']' ) && p != p1 + 1  ) break;

	 if (  *p == _TEXT( '-' ) && p[ 1 ] != _TEXT( ']' )  ) {

      PATFETCH( c1 );
      PATFETCH( c1 );

      if ( translate )

       while ( c <= c1 ) {

        _TUCHAR mapped_c = translate[ c ];
        b[ mapped_c / BYTEWIDTH ] |= 1 << ( mapped_c % BYTEWIDTH );
        ++c;

       }  /* end while */

	  else

       while ( c <= c1 ) b[ c / BYTEWIDTH ] |= 1 << ( c % BYTEWIDTH ), c++;
		
     } else {

      if ( translate ) c = translate[ c ];

      b[ c / BYTEWIDTH ] |= 1 << ( c % BYTEWIDTH );
		
     }  /* end else */

    }  /* end while */

	/* Discard any bitmap bytes that are all 0 at the end of the map. */
	/*  Decrement the map-length byte too.                            */
	while (  ( int )b[ -1 ] > 0 && b[  b[ -1 ] - 1  ] == 0  ) b[ -1 ]--;

    b += b[ -1 ];

   break;

   case _TEXT( '(' ):

    if (  !( obscure_syntax & RE_NO_BK_PARENS )  )
	 goto normal_char;
	else
	 goto handle_open;

   case _TEXT( ')' ):
    if (  !( obscure_syntax & RE_NO_BK_PARENS )  )
     goto normal_char;
    else
     goto handle_close;

   case '\n':
    if (  !( obscure_syntax & RE_NEWLINE_OR )  )
     goto normal_char;
    else
     goto handle_bar;

   case _TEXT( '|' ):
    if (  !( obscure_syntax & RE_NO_BK_VBAR )  )
     goto normal_char;
    else
     goto handle_bar;

   case _TEXT( '\\' ):

    if ( p == pend ) goto invalid_pattern;

    PATFETCH_RAW( c );

    switch ( c ) {

     case _TEXT( '(' ):

	  if ( obscure_syntax & RE_NO_BK_PARENS ) goto normal_backsl;

handle_open:

      if ( stackp == stacke ) goto nesting_too_deep;

	  if ( regnum < RE_NREGS ) {

       PATPUSH( start_memory );
       PATPUSH( regnum );
	        
      }  /* end if */

      *stackp++  = b - bufp -> buffer;
      *stackp++  = fixup_jump ? fixup_jump - bufp -> buffer + 1L : 0L;
      *stackp++  = regnum++;
      *stackp++  = begalt - bufp -> buffer;
	  fixup_jump = NULL;
	  laststart  = NULL;
	  begalt     = b;

     break;

     case _TEXT( ')' ):

      if ( obscure_syntax & RE_NO_BK_PARENS ) goto normal_backsl;

handle_close:

      if ( stackp == stackb ) goto unmatched_close;

      begalt = *--stackp + bufp -> buffer;

      if ( fixup_jump ) store_jump ( fixup_jump, jump, b );
	      
      if ( stackp[ -1 ] < RE_NREGS ) {

       PATPUSH( stop_memory );
       PATPUSH( stackp[ -1 ] );
		
      }  /* end if */

      stackp -= 2;
      fixup_jump = NULL;

      if ( *stackp ) fixup_jump = *stackp + bufp -> buffer - 1L;

      laststart = *--stackp + bufp -> buffer;

	 break;

     case _TEXT( '|' ):

      if ( obscure_syntax & RE_NO_BK_VBAR ) goto normal_backsl;

handle_bar:

      insert_jump ( on_failure_jump, begalt, b + 6, b );
      pending_exact = NULL;
      b += 3;

      if ( fixup_jump ) store_jump ( fixup_jump, jump, b );
	      
      fixup_jump = b;
      b += 3;
      laststart = 0;
      begalt = b;

     break;

	 case _TEXT( 'w' ):

	  laststart = b;
	  PATPUSH( wordchar );

	 break;

     case _TEXT( 'W' ):

      laststart = b;
      PATPUSH( notwordchar );

     break;

     case _TEXT( '<' ):

      PATPUSH( wordbeg );

     break;

     case _TEXT( '>' ):

      PATPUSH( wordend );

     break;

     case _TEXT( 'b' ):

      PATPUSH( wordbound );

     break;

     case _TEXT( 'B' ):

      PATPUSH( notwordbound );
     
     break;

     case _TEXT( '`' ):

      PATPUSH( begbuf );

     break;

     case _TEXT( '\'' ):

      PATPUSH( endbuf );

     break;

     case _TEXT( '1' ):
     case _TEXT( '2' ):
     case _TEXT( '3' ):
     case _TEXT( '4' ):
     case _TEXT( '5' ):
     case _TEXT( '6' ):
     case _TEXT( '7' ):
     case _TEXT( '8' ):
     case _TEXT( '9' ):

      c1 = c - _TEXT( '0' );

      if (  ( int )c1 >= regnum  ) goto normal_char;

      for ( stackt = stackp - 2;  stackt > stackb;  stackt -= 4 )
 		
       if (  *stackt == ( long )c1  ) goto normal_char;

	  laststart = b;
	  PATPUSH( duplicate );
	  PATPUSH( c1 );

	 break;

	 case _TEXT( '+' ):
     case _TEXT( '?' ):

	  if ( obscure_syntax & RE_BK_PLUS_QM ) goto handle_plus;

	 default:

normal_backsl:

	 /* You might think it would be useful for \ to mean */
     /* not to translate; but if we don't translate it   */
     /* it will never match anything.                    */
	  if ( translate ) c = translate[ c ];
      
      goto normal_char;

	}  /* end switch */

   break;

   default:

normal_char:

    if (  !pending_exact                            ||
           pending_exact + *pending_exact + 1L != b ||
	      *pending_exact == 0177                    ||
          *p == _TEXT( '*' )                        ||
          *p == _TEXT( '^' )                        ||
	      (  ( obscure_syntax & RE_BK_PLUS_QM ) ?
              *p == _TEXT( '\\' ) &&
              (  p[ 1 ] == _TEXT( '+' ) ||
                 p[ 1]  == _TEXT( '?' )
              )                                 :
              ( *p == _TEXT( '+' ) ||
                *p == _TEXT( '?' )
              )
          )
    ) {

	 laststart = b;
	 PATPUSH( exactn );
	 pending_exact = b;
	 PATPUSH( 0 );

	}  /* end if */

    PATPUSH( c );
    ( *pending_exact )++;

  }  /* end switch */

 }  /* end while */

 if ( fixup_jump ) store_jump ( fixup_jump, jump, b );

 if ( stackp != stackb ) goto unmatched_open;

 bufp -> used = b - bufp -> buffer;

 return NULL;

invalid_pattern:  return _TEXT( "invalid regular expression" );

unmatched_open:   return _TEXT( "unmatched \\(" );

unmatched_close:  return _TEXT( "unmatched \\)" );

end_of_pattern:   return _TEXT( "premature end of regular expression" );

nesting_too_deep: return _TEXT( "nesting too deep" );

too_big:          return _TEXT( "regular expression too big" );

memory_exhausted: return _TEXT( "Memory exhausted" );

}  /* end re_compile_pattern */
/***/
/******************************************************************************/
/*                                                                            */
/* Store where `from' points a jump operation to jump to where `to' points.   */
/* `opcode' is the opcode to store.                                           */
/*                                                                            */
/******************************************************************************/
/***/
static void store_jump ( _TCHAR* from, _TCHAR opcode, _TCHAR* to ) {

 from[ 0 ] = opcode;
 from[ 1 ] = (  to - ( from + 3 )  ) &  CHAR_MASK;
 from[ 2 ] = (  to - ( from + 3 )  ) >> BYTEWIDTH;

}  /* end store_jump */
/***/
/******************************************************************************/
/*                                                                            */
/* Open up space at char FROM, and insert there a jump to TO.                 */
/* CURRENT_END gives te end of the storage no in use,                         */
/* so we know how much data to copy up.                                       */
/* OP is the opcode of the jump to insert.                                    */
/*                                                                            */
/* If you call this function, you must zero out pending_exact.                */
/*                                                                            */
/******************************************************************************/
/***/
static void insert_jump (
             _TCHAR op, _TCHAR* from, _TCHAR* to, _TCHAR* current_end
            ) {

  _TCHAR* pto = current_end + 3;
  _TCHAR* pfrom = current_end;

  while ( pfrom != from ) *--pto = *--pfrom;

  store_jump ( from, op, to );

}  /* end insert_jump */
/***/
/******************************************************************************/
/*                                                                            */
/* Given a pattern, compute a fastmap from it.                                */
/* The fastmap records which of the (1 << BYTEWIDTH) possible characters      */
/* can start a string that matches the pattern.                               */
/* This fastmap is used by re_search to skip quickly over totally implausible */
/* text.                                                                      */
/* The caller must supply the address of a (1 << BYTEWIDTH)-byte data area    */
/* as bufp->fastmap.                                                          */
/* The other components of bufp describe the pattern to be used.              */
/*                                                                            */
/******************************************************************************/
/***/
void re_compile_fastmap ( PRE_PATTERN_BUFFER bufp ) {

 int j, size = bufp -> used;

 _TUCHAR*  pattern   = (_TUCHAR* )bufp -> buffer;
 _TUCHAR*  p         = pattern;
 _TUCHAR*  pend      = pattern + size;
 _TUCHAR*  translate = (_TUCHAR* )bufp -> translate;
 _TCHAR*   fastmap   = bufp -> fastmap;
 _TUCHAR*  stackb[ NFAILURES ];
 _TUCHAR** stackp = stackb;

 memset (  fastmap, 0, ( 1 << BYTEWIDTH ) * sizeof ( TCHAR )  );
 bufp -> fastmap_accurate = 1;
 bufp -> can_be_null = 0;
      
 while ( p ) {

  if ( p == pend ) {

   bufp -> can_be_null = 1;

   break;

  }  /* end if */

  switch (  ( enum regexpcode )*p++  ) {

   case exactn:

    if ( translate )
     fastmap[   translate[  p[ 1 ]  ]   ] = 1;
    else
     fastmap[  p[ 1 ]  ] = 1;
	  
   break;

   case begline:
   case before_dot:
   case at_dot:
   case after_dot:
   case begbuf:
   case endbuf:
   case wordbound:
   case notwordbound:
   case wordbeg:
   case wordend:

    continue;

   case endline:

    if ( translate )
     fastmap[  translate[ _TEXT( '\n' ) ]  ] = 1;
    else
     fastmap[ _TEXT( '\n' ) ] = 1;

    if ( bufp -> can_be_null != 1 )
     bufp -> can_be_null = 2;
	  
   break;

   case finalize_jump:
   case maybe_finalize_jump:
   case jump:
   case dummy_failure_jump:

    bufp -> can_be_null = 1;
    j = *p++ & CHAR_MASK;
    j += SIGN_EXTEND_CHAR(  *( char* )p  ) << BYTEWIDTH;
    p += j + 1;  /* The 1 compensates for missing ++ above */
	  
    if ( j > 0 ) continue;

    /* Jump backward reached implies we just went through      */
    /*  the body of a loop and matched nothing.                */
    /*  Opcode jumped to should be an on_failure_jump.         */
    /*  Just treat it like an ordinary jump.                   */
    /*  For a * loop, it has pushed its failure point already; */
    /*    if so, discard that as redundant.                    */
    if (  ( enum regexpcode )*p != on_failure_jump  ) continue;

    ++p;
    j = *p++ & CHAR_MASK;
    j += SIGN_EXTEND_CHAR(  *( char* )p  ) << BYTEWIDTH;
    p += j + 1;  /* The 1 compensates for missing ++ above */

    if ( stackp != stackb && *stackp == p ) stackp--;

    continue;
	  
   case on_failure_jump:

    j = *p++ & CHAR_MASK;
    j += SIGN_EXTEND_CHAR(  *( char* )p  ) << BYTEWIDTH;
    p++;
    *++stackp = p + j;

    continue;

   case start_memory:
   case stop_memory:

    ++p;

    continue;

   case duplicate:

    bufp -> can_be_null = 1;
    fastmap[ _TEXT( '\n' )] = 1;

   case anychar:

    for (  j = 0; j < ( 1 << BYTEWIDTH ); j++  )
	    
     if (  j != _TEXT( '\n' )  ) fastmap[ j ] = 1;

    if ( bufp -> can_be_null ) return;

    /* Don't return; check the alternative paths  */
    /*  so we can set can_be_null if appropriate. */
   break;

   case wordchar:

    for (  j = 0; j < ( 1 << BYTEWIDTH ); j++  )

     if (  SYNTAX( j ) == Sword  ) fastmap[ j ] = 1;
	  
   break;

   case notwordchar:

    for (  j = 0; j < ( 1 << BYTEWIDTH ); j++  )

     if (  SYNTAX( j ) != Sword  ) fastmap[ j ] = 1;

   break;

   case charset:

    for ( j = *p++ * BYTEWIDTH - 1; j >= 0; j-- )

     if (   p[ j / BYTEWIDTH ] & (  1 << ( j % BYTEWIDTH )  )   ) {

      if ( translate )
       fastmap[  translate[ j ]  ] = 1;
      else
       fastmap[ j ] = 1;

     }  /* end if */

   break;

   case charset_not:
    /* Chars beyond end of map must be allowed */
    for (  j = *p * BYTEWIDTH; j < ( 1 << BYTEWIDTH ); j++  )
	    
     if ( translate )
      fastmap[  translate[ j ]  ] = 1;
     else
      fastmap[ j ] = 1;

    for ( j = *p++ * BYTEWIDTH - 1; j >= 0; j-- )

     if (    !(  p[ j / BYTEWIDTH ] & (  1 << ( j % BYTEWIDTH )  )   )    ) {

      if ( translate )
       fastmap[  translate[ j ]  ] = 1;
      else
       fastmap[ j ] = 1;

     }  /* end if */
	  
   break;
	
  }  /* end switch */

   /* Get here means we have successfully found the possible starting    */
   /* characters of one path of the pattern.  We need not follow this    */
   /* path any farther. Instead, look at the next alternative remembered */
   /* in the stack.                                                      */
  if ( stackp != stackb )
   p = *stackp--;
  else
   break;
    
 }  /* end while */

}  /* end re_compile_fastmap */
/***/
/******************************************************************************/
/*                                                                            */
/* Like re_search_2, below, but only one string is specified.                 */
/*                                                                            */
/******************************************************************************/
/***/
int re_search (
     PRE_PATTERN_BUFFER pbufp, _TCHAR* string, int size, int startpos,
     int range, PRE_REGISTERS regs
    ) {

 return re_search_2 (
         pbufp, NULL, 0, string, size, startpos, range, regs, size
        );

}  /* end re_search */
/***/
/******************************************************************************/
/*                                                                            */
/* Like re_match_2 but tries first a match starting at index STARTPOS,        */
/* then at STARTPOS + 1, and so on.                                           */
/* RANGE is the number of places to try before giving up.                     */
/* If RANGE is negative, the starting positions tried are                     */
/*  STARTPOS, STARTPOS - 1, etc.                                              */
/* It is up to the caller to make sure that range is not so large             */
/*  as to take the starting position outside of the input strings.            */
/*                                                                            */
/* The value returned is the position at which the match was found,           */
/* or -1 if no match was found,                                               */
/* or -2 if error (such as failure stack overflow).                           */
/*                                                                            */
/******************************************************************************/
/***/
int re_search_2 (
     PRE_PATTERN_BUFFER pbufp,
     _TCHAR* string1, int size1, _TCHAR* string2, int size2,
     int startpos, int range, PRE_REGISTERS regs, int mstop
    ) {

 _TCHAR*  fastmap   = pbufp -> fastmap;
 _TUCHAR* translate = (_TUCHAR* )pbufp -> translate;

 int total = size1 + size2;
 int val;

 /* Update the fastmap now if not correct already */
 if ( fastmap && !pbufp -> fastmap_accurate ) re_compile_fastmap ( pbufp );
  
 /* Don't waste time in a long search for a pattern */
 /*  that says it is anchored.                      */
 if ( pbufp -> used > 0                                 &&
      ( enum regexpcode )pbufp -> buffer[ 0 ] == begbuf &&
      range > 0
 ) {

  if ( startpos > 0 )
   return -1;
  else
   range = 1;
    
 }  /* end if */

 while ( 1 ) {

  /* If a fastmap is supplied, skip quickly over characters  */
  /* that cannot possibly be the start of a match.           */
  /* Note, however, that if the pattern can possibly match   */
  /* the null string, we must test it at each starting point */
  /* so that we take the first null string we get.           */
  if ( fastmap && startpos < total && pbufp -> can_be_null != 1 ) {

   if ( range > 0 ) {

	int lim    = 0;
    int irange = range;

	_TUCHAR* p;

	if ( startpos < size1 && startpos + range >= size1 )
		
     lim = range - ( size1 - startpos );

	p = (  ( _TUCHAR * )
		    &( startpos >= size1 ? string2 - size1 : string1 )[ startpos ]
        );

	if ( translate )

     while ( range > lim && !fastmap[  translate[ *p++ ]  ] ) range--;

    else
		  
     while ( range > lim && !fastmap[ *p++ ] ) range--;

    startpos += irange - range;

   } else {

	_TUCHAR c;

	if ( startpos >= size1 )
     c = string2[ startpos - size1 ];
	else
     c = string1[ startpos ];

	c &= CHAR_MASK;

	if ( translate ? !fastmap[  translate[ c ]  ] : !fastmap[ c ] ) goto advance;

   }  /* end else */

  }  /* end if */

  if ( range >= 0           &&
       startpos == total    &&
	   fastmap              &&
       pbufp -> can_be_null == 0
  ) return -1;

  val = re_match_2 ( pbufp, string1, size1, string2, size2, startpos, regs, mstop );

  /* Propagate error indication if worse than mere failure.  */
  if ( val == -2 ) return -2;

  /* Return position on success.  */
  if ( 0 <= val ) return startpos;

advance:

  if ( !range ) break;

  if ( range > 0 )
   range--, startpos++;
  else
   range++, startpos--;
    
 }  /* end while */

  return -1;

}  /* end int re_search_2 */
/***/
/******************************************************************************/
/*                                                                            */
/*                                                                            */
/******************************************************************************/
/***/
int re_match (
     PRE_PATTERN_BUFFER pbufp, _TCHAR* string, int size, int pos,
     PRE_REGISTERS regs
    ) {

 return re_match_2 ( pbufp, NULL, 0, string, size, pos, regs, size );

}  /* end re_match */
/***/
/******************************************************************************/
/*                                                                            */
/* Maximum size of failure stack.  Beyond this, overflow is an error.         */
/*                                                                            */
/******************************************************************************/
/***/
#define RE_MAX_FAILURES 65536
/***/
static int bcmp_translate();
/***/
/******************************************************************************/
/*                                                                            */
/* Match the pattern described by PBUFP                                       */
/*   against data which is the virtual concatenation of STRING1 and STRING2.  */
/*   SIZE1 and SIZE2 are the sizes of the two data strings.                   */
/*   Start the match at position POS.                                         */
/*   Do not consider matching past the position MSTOP.                        */
/*                                                                            */
/*   If pbufp->fastmap is nonzero, then it had better be up to date.          */
/*                                                                            */
/*   The reason that the data to match are specified as two components        */
/*   which are to be regarded as concatenated                                 */
/*   is so this function can be used directly on the contents of an Emacs     */
/*   buffer.                                                                  */
/*                                                                            */
/*   -1 is returned if there is no match.  -2 is returned if there is         */
/*   an error (such as match stack overflow).  Otherwise the value is the     */
/*   length of the substring which was matched.                               */
/*                                                                            */
/******************************************************************************/
/***/
int re_match_2 (
     PRE_PATTERN_BUFFER pbufp,
     _TUCHAR* string1, int size1, _TUCHAR* string2, int size2,
     int pos, PRE_REGISTERS regs, int mstop
    ) {

 _TUCHAR* p    = ( _TUCHAR* )pbufp -> buffer;
 _TUCHAR* pend = p + pbufp -> used;
 /* End of first string */
 _TUCHAR* end1;
 /* End of second string */
 _TUCHAR* end2;
 /* Pointer just past last char to consider matching */
 _TUCHAR* end_match_1, *end_match_2;
 _TUCHAR* d, *dend;
 _TUCHAR* translate = ( _TUCHAR* ) pbufp -> translate;
 
 int mcnt;
 
 /* Failure point stack.  Each place that can handle a failure       */
 /* further down the line pushes a failure point on this stack.      */
 /* It consists of two char *'s.                                     */
 /* The first one pushed is where to resume scanning the pattern;    */
 /* the second pushed is where to resume scanning the strings.       */
 /* If the latter is zero, the failure point is a "dummy".           */
 /* If a failure happens and the innermost failure point is dormant, */
 /* it discards that failure point and tries the next one.           */

 _TUCHAR*  initial_stack[ 2 * NFAILURES ];
 _TUCHAR** stackb = initial_stack;
 _TUCHAR** stackp = stackb, **stacke = &stackb[ 2 * NFAILURES ];

 /* Information on the "contents" of registers.                              */
 /*  These are pointers into the input strings; they record                  */
 /*  just what was matched (on this attempt) by some part of the pattern.    */
 /*  The start_memory command stores the start of a register's contents      */
 /*  and the stop_memory command stores the end.                             */
 /*                                                                          */
 /*  At that point, regstart[regnum] points to the first character in        */
 /*  the register,                                                           */
 /*  regend[regnum] points to the first character beyond the end of          */
 /*  the register,                                                           */
 /*  regstart_seg1[regnum] is true iff regstart[regnum] points into string1, */
 /*  and regend_seg1[regnum] is true iff regend[regnum] points into string1. */

  _TUCHAR* regstart[ RE_NREGS ];
  _TUCHAR* regend[ RE_NREGS ];
  _TUCHAR  regstart_seg1[ RE_NREGS ], regend_seg1[ RE_NREGS ];

 /* Set up pointers to ends of strings.                               */
 /*  Don't allow the second string to be empty unless both are empty. */
 if ( !size2 ) {

  string2 = string1;
  size2 = size1;
  string1 = NULL;
  size1 = 0;
    
 }  /* end if */

 end1 = string1 + size1;
 end2 = string2 + size2;

 /* Compute where to stop matching, within the two strings */
 if ( mstop <= size1 ) {

  end_match_1 = string1 + mstop;
  end_match_2 = string2;

 } else {

  end_match_1 = end1;
  end_match_2 = string2 + mstop - size1;

 }  /* end else */

 /* Initialize \) text positions to -1                */
 /*  to mark ones that no \( or \) has been seen for. */

 for (  mcnt = 0; mcnt < sizeof ( regend ) / sizeof( *regend ); mcnt++ )

  regend[ mcnt ] = ( _TUCHAR* )-1;

 /* `p' scans through the pattern as `d' scans through the data.         */
 /*  `dend' is the end of the input string that `d' points within.       */
 /*  `d' is advanced into the following input string whenever necessary, */
 /*  but this happens before fetching;                                   */
 /*  therefore, at the beginning of the loop,                            */
 /*  `d' can be pointing at the end of a string,                         */
 /*  but it cannot equal string2.                                        */
 if ( pos <= size1 )
  d = string1 + pos, dend = end_match_1;
 else
  d = string2 + pos - size1, dend = end_match_2;

/* Write PREFETCH; just before fetching a character with *d. */
#define PREFETCH while ( d == dend ) {                                                  \
                  if ( dend == end_match_2 ) goto fail; /* end of string2 => failure */ \
                  d = string2; /* end of string1 => advance to string2. */              \
                  dend = end_match_2;                                                   \
                 }

 /* This loop loops over pattern commands.                                        */
 /*  It exits by returning from the function if match is complete,                */
 /*  or it drops through if match fails at this starting point in the input data. */
 while ( 1 ) {

  if (p == pend) {  /* End of pattern means we have succeeded! */
	  
   if ( regs ) { /* If caller wants register contents data back, convert it to indices */

 	regs -> start[ 0 ] = pos;
 	      
    if ( dend == end_match_1 )
     regs -> end[ 0 ] = d - string1;
    else
     regs->end[ 0 ] = d - string2 + size1;

 	for ( mcnt = 1; mcnt < RE_NREGS; mcnt++ ) {

     if (  regend[ mcnt ] == ( _TUCHAR* )-1  ) {

      regs -> start[ mcnt ] = -1;
      regs -> end[ mcnt ]   = -1;
		      
      continue;
		    
     }  /* end if */

     if ( regstart_seg1[ mcnt ] )
      regs -> start[ mcnt ] = regstart[ mcnt ] - string1;
     else
      regs -> start[ mcnt ] = regstart[ mcnt ] - string2 + size1;
 		  
     if ( regend_seg1[ mcnt ] )
      regs -> end[ mcnt ] = regend[ mcnt ] - string1;
     else
      regs -> end[ mcnt ] = regend[ mcnt ] - string2 + size1;
		
    }  /* end for */
	    
   }  /* end if */

   if ( dend == end_match_1 )
    return ( d - string1 - pos );
   else
	return d - string2 + size1 - pos;

  }  /* end if */

  /* Otherwise match next pattern command */
  switch (  ( enum regexpcode )*p++  ) {

   /* \( is represented by a start_memory, \) by a stop_memory.              */
   /*   Both of those commands contain a "register number" argument.         */
   /*   The text matched within the \( and \) is recorded under that number. */
   /*   Then, \<digit> turns into a `duplicate' command which                */
   /*   is followed by the numeric value of <digit> as the register number.  */
   case start_memory:

    regstart[ *p ] = d;
    regstart_seg1[ *p++ ] = ( dend == end_match_1 );
	 
   break;

   case stop_memory:

    regend[ *p ] = d;
    regend_seg1[ *p++ ] = ( dend == end_match_1 );
	 
   break;

   case duplicate: {

    int     regno = *p++;  /* Get which register to match against */
    _TUCHAR *d2, *dend2;

    /* Don't allow matching a register that hasn't been used. */
    /*     This isn't fully reliable in the current version,  */
    /*     but it is better than crashing.                    */
    if (  ( int )regend[ regno ] == -1  ) goto fail;

    d2 = regstart[ regno ];
    dend2 = (  ( regstart_seg1[ regno ] == regend_seg1[ regno]  ) ?
             regend[regno]                                        :
             end_match_1
            );

    while ( 1 ) {

     /* Advance to next segment in register contents, if necessary */
     while ( d2 == dend2 ) {

      if ( dend2 == end_match_2     ) break;
      if ( dend2 == regend[ regno ] ) break;

      d2 = string2, dend2 = regend[ regno ];  /* end of string1 => advance to string2. */
		  
     }  /* end while */
	
     /* At end of register contents => success */
     if ( d2 == dend2 ) break;

     /* Advance to next segment in data being matched, if necessary */
     PREFETCH;

     /* mcnt gets # consecutive chars to compare */
     mcnt = dend - d;

     if ( mcnt > dend2 - d2 ) mcnt = dend2 - d2;

     /* Compare that many; failure if mismatch, else skip them. */
     if ( translate                                 ?
          bcmp_translate ( d, d2, mcnt, translate ) :
          memcmp ( d, d2, mcnt )
     ) goto fail;

     d += mcnt, d2 += mcnt;
	      
    }  /* end while */

   }  /* duplicate */

   break;

   case anychar:

    /* fetch a data character */
    PREFETCH;
    /* Match anything but a newline.  */
    if (  ( translate ? translate[ *d++ ] : *d++ ) == _TEXT( '\n' )  ) goto fail;

   break;

   case charset:
   case charset_not: {

    /* Nonzero for charset_not */
    int not = 0;
    int c;

    if (  *( p - 1 ) == ( _TUCHAR ) charset_not  ) not = 1;

    /* fetch a data character */
    PREFETCH;

    if ( translate )
     c = translate [ *d ];
    else
     c = *d;

    if (  c < ( int )( *p * BYTEWIDTH ) &&
         p[ 1 + c / BYTEWIDTH ] & (  1 << ( c % BYTEWIDTH ) )
    ) not = !not;

    p += 1 + *p;

    if ( !not ) goto fail;

    ++d;

   }  /* charset & charset_not */

   break;

   case begline:

    if (  d == string1 || d[ -1 ] == _TEXT( '\n' )  ) break;

    goto fail;

   case endline:

    if ( d == end2 ||
         ( d == end1 ? ( size2 == 0 ||
                         *string2 == _TEXT( '\n' )
                       )
                     : *d == _TEXT( '\n' )
         )
    ) break;

    goto fail;

   /* "or" constructs ("|") are handled by starting each alternative             */
   /*  with an on_failure_jump that points to the start of the next alternative. */
   /*  Each alternative except the last ends with a jump to the joining point.   */
   /*  (Actually, each jump except for the last one really jumps                 */
   /*   to the following jump, because tensioning the jumps is a hassle.)        */
   /*                                                                            */
   /* The start of a stupid repeat has an on_failure_jump that points            */
   /* past the end of the repeat text.                                           */
   /* This makes a failure point so that, on failure to match a repetition,      */
   /* matching restarts past as many repetitions have been found                 */
   /* with no way to fail and look for another one.                              */
   /*                                                                            */
   /* A smart repeat is similar but loops back to the on_failure_jump            */
   /* so that each repetition makes another failure point.                       */

   case on_failure_jump:

    if ( stackp == stacke ) {

     _TUCHAR** stackx;

     if ( stacke - stackb > RE_MAX_FAILURES ) return -2;

     stackx = ( _TUCHAR** ) alloca(  2 * ( stacke - stackb ) * sizeof ( _TCHAR* )  );
     memcpy (  stackx, stackb, ( stacke - stackb ) * sizeof ( _TCHAR* )  );
     stackp = stackx + ( stackp - stackb );
     stacke = stackx + 2 * ( stacke - stackb );
     stackb = stackx;
	    
    }  /* end if */

    mcnt = *p++ & CHAR_MASK;
    mcnt += SIGN_EXTEND_CHAR(  *( _TCHAR* )p  ) << BYTEWIDTH;
    ++p;
    *stackp++ = mcnt + p;
    *stackp++ = d;
	  
   break;

   /* The end of a smart repeat has an maybe_finalize_jump back. */
   /* Change it either to a finalize_jump or an ordinary jump.   */
   case maybe_finalize_jump:

    mcnt = *p++ & CHAR_MASK;
    mcnt += SIGN_EXTEND_CHAR(  *( _TCHAR* )p  ) << BYTEWIDTH;
    ++p;

    /* Compare what follows with the begining of the repeat.      */
    /*  If we can establish that there is nothing that they would */
    /*  both match, we can change to finalize_jump                */
    if ( p == pend )
     p[ -3 ] = ( _TUCHAR )finalize_jump;
    else if ( *p == ( _TUCHAR )exactn  || 
              *p == ( _TUCHAR )endline
    ) {
	      
     int      c  = *p == ( _TUCHAR ) endline ? _TEXT( '\n' ) : p[ 2 ];
     _TUCHAR* p1 = p + mcnt;
	      
     /* p1[0] ... p1[2] are an on_failure_jump. */
     /* Examine what follows that               */
     if (  p1[ 3 ] == ( _TUCHAR )exactn && p1[ 5 ] != c  )
      p[ -3 ] = ( _TUCHAR )finalize_jump;
     else if (  p1[ 3 ] == ( _TUCHAR )charset     ||
                p1[ 3 ] == ( _TUCHAR )charset_not
     ) {

      int not = p1[ 3 ] == ( _TCHAR )charset_not;
		  
      if ( c < ( int )( p1[ 4 ] * BYTEWIDTH  ) &&
           p1[ 5 + c / BYTEWIDTH ] & (  1 << ( c % BYTEWIDTH )  )
      ) not = !not;

      /* not is 1 if c would match             */
      /* That means it is not safe to finalize */
      if ( !not ) p[ -3 ] = ( _TUCHAR )finalize_jump;

     }  /* end if */

    }  /* end if */

    p -= 2;

    if ( p[ -1 ] != ( _TUCHAR )finalize_jump ) {

     p[ -1 ] = ( _TUCHAR ) jump;
     goto nofinalize;

    }  /* end if */

   /* The end of a stupid repeat has a finalize-jump              */
   /* back to the start, where another failure point will be made */
   /* which will point after all the repetitions found so far.    */

   case finalize_jump:

    stackp -= 2;

   case jump:

nofinalize:

    mcnt = *p++ & CHAR_MASK;
    mcnt += SIGN_EXTEND_CHAR(  *( _TCHAR* )p  ) << BYTEWIDTH;
    p += mcnt + 1; /* The 1 compensates for missing ++ above */

   break;

   case dummy_failure_jump:

    if ( stackp == stacke ) {

     _TUCHAR** stackx =
      ( _TUCHAR** ) alloca(  2 * ( stacke - stackb ) * sizeof ( _TUCHAR* )  );
	      
     memcpy (  stackx, stackb, (stacke - stackb) * sizeof ( _TCHAR* )  );
     stackp = stackx + ( stackp - stackb );
     stacke = stackx + 2 * ( stacke - stackb );
     stackb = stackx;
	    
    }  /* end if */

    *stackp++ = 0;
    *stackp++ = 0;
	 
    goto nofinalize;

   case wordbound:

    if ( d == string1  ||             /* Points to first char */
         d == end2     ||             /* Points to end        */
         ( d == end1 && size2 == 0 )  /* Points to end        */
    ) break;

    if (   (  SYNTAX( d[ -1 ]                   ) == Sword  ) !=
           (  SYNTAX( d == end1 ? *string2 : *d ) == Sword  )
    ) break;

    goto fail;

   case notwordbound:

    if ( d == string1  ||             /* Points to first char */
         d == end2     ||             /* Points to end        */
         ( d == end1 && size2 == 0 )  /* Points to end        */
    ) goto fail;

    if (   (  SYNTAX( d[ -1 ]                   ) == Sword  ) !=
           (  SYNTAX( d == end1 ? *string2 : *d ) == Sword  )
    ) goto fail;

    break;

   case wordbeg:

    if ( d == end2                  ||                      /* Points to end          */
         ( d == end1 && size2 == 0) ||                      /* Points to end          */
         SYNTAX(  *( d == end1 ? string2 : d )  ) != Sword  /* Next char not a letter */
    ) goto fail;

    if ( d == string1 ||             /* Points to first char */
         SYNTAX( d[ -1 ] ) != Sword  /* prev char not letter */
    ) break;

    goto fail;

   case wordend:

    if ( d == string1 ||           /* Points to first char */
         SYNTAX( d[-1] ) != Sword  /* prev char not letter */
    ) goto fail;

    if ( d == end2                 ||                   /* Points to end          */
         (d == end1 && size2 == 0) ||                   /* Points to end          */
         SYNTAX( d == end1 ? *string2 : *d ) != Sword   /* Next char not a letter */
    ) break;

    goto fail;

   case wordchar:

    PREFETCH;

    if (  SYNTAX ( *d++ ) == 0  ) goto fail;

   break;
	  
   case notwordchar:

    PREFETCH;

    if (  SYNTAX ( *d++ ) != 0  ) goto fail;
	  
   break;

   case begbuf:

    if ( d == string1 ) /* Note, d cannot equal string2 */
     break;    	     /* unless string1 == string2.  */

    goto fail;

   case endbuf:

    if (  d == end2 || ( d == end1 && size2 == 0 )  ) break;

    goto fail;

   case exactn:
    /* Match the next few pattern characters exactly. */
    /*  mcnt is how many characters to match.         */
    mcnt = *p++;

    if ( translate ) {

     do {

      PREFETCH;

      if ( translate[ *d++ ] != *p++ ) goto fail;
		
     } while ( --mcnt );
	    
    } else {

     do {

      PREFETCH;

      if ( *d++ != *p++ ) goto fail;
		
     } while ( --mcnt );
	    
    }  /* end else */

   break;

  }  /* end switch */

  continue; /* Successfully matched one pattern command; keep matching */

  /* Jump here if any matching operation fails. */
fail:

  if ( stackp != stackb ) { /* A restart point is known.  Restart there and pop it. */

   if ( !stackp[ -2 ] ) {   /* If innermost failure point is dormant, */
	                        /* flush it and keep looking              */
    stackp -= 2;      

	goto fail;
	    
   }  /* end if */

   d = *--stackp;
   p = *--stackp;
	  
   if ( d >= string1 && d <= end1 ) dend = end_match_1;

  } else break;   /* Matching at this starting point really fails! */

 }  /* end while */

 return -1; /* Failure to match */

}  /* end re_search_2 */
/***/
/******************************************************************************/
/*                                                                            */
/*                                                                            */
/******************************************************************************/
/***/
static int bcmp_translate (
            _TUCHAR* s1, _TUCHAR* s2, int len, _TUCHAR* translate
           ) {

  _TUCHAR *p1 = s1, *p2 = s2;

  while ( len ) {

   if ( translate [ *p1++ ] != translate [ *p2++] ) return 1;

   --len;
    
  }  /* end while */

  return 0;

}  /* end bcmp_translate */
/***/
/******************************************************************************/
#endif
