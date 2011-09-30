#ifndef __WMLLIB_LOAD__H
#define __WMLLIB_LOAD__H

#define _LIBPATH_	"Z:/Math/Basic/WmlMath/lib/"

#ifdef _DEBUG

#ifdef _MSVC_60
#pragma comment(lib, ""_LIBPATH_"WmlMath100-win32-mtd-vc6-d.lib")
#endif

#ifdef _MSVC_70
#pragma comment(lib, ""_LIBPATH_"WmlMath100-win32-mtd-vc7-d.lib")
#endif

#ifdef _MSVC_80
#pragma comment(lib, ""_LIBPATH_"WmlMath100-win32-mtd-vc8-d.lib")
#endif

#else

#ifdef _MSVC_60
#pragma comment(lib, ""_LIBPATH_"WmlMath100-win32-mtd-vc6-r.lib")
#endif

#ifdef _MSVC_70
#pragma comment(lib, ""_LIBPATH_"WmlMath100-win32-mtd-vc7-r.lib")
#endif

#ifdef _MSVC_80
#pragma comment(lib, ""_LIBPATH_"WmlMath100-win32-mtd-vc8-r.lib")
#endif

#endif 


#undef _LIBPATH_

#endif