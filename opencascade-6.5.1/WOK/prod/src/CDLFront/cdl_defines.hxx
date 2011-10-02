#define MAX_CHAR     256              /* The limit of a identifier.  */
#define MAX_STRING   (MAX_CHAR * 10)  /* The limit of a string.      */
#define MAX_COMMENT  (MAX_CHAR * 300) /* The limit of comment line   */
#define ENDOFCOMMENT "\n%\n"          /* The marque of end of coment */

#define CDL_OPERATOR     1
#define CDL_INLINE       2
#define CDL_DESTRUCTOR   3
#define CDL_CONSTREF     4
#define CDL_CONSTRET     5
#define CDL_REF          6
#define CDL_HARDALIAS    7
#define CDL_FUNCTIONCALL 8
#define CDL_COMMENT      9
#define CDL_PTR         10
#define CDL_CONSTPTR    11




