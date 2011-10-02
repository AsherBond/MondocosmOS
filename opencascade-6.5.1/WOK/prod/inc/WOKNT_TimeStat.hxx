#ifndef __WOKNT_FILETIME_HXX
# define __WOKNT_FILETIME_HXX

#ifdef WNT

# ifndef STRICT
#  define STRICT
# endif  // STRICT

# ifndef _INC_TIME
#  include <time.h>
# endif  // _INC_TIME

typedef time_t WOKNT_TimeStat;

#endif  // __WOKNT_FILETIME_HXX

#endif
