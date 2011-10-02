#ifndef __WOKNT_WNT_BREAK__HXX
# define __WOKNT_WNT_BREAK__HXX

# ifdef WNT
#  include <WOKNT_ShellManager.hxx>
#  define _TEST_BREAK() WOKNT_ShellManager :: Break ()
# endif  // WNT

#endif  // __WOKNT_WNT_BREAK__HXX
