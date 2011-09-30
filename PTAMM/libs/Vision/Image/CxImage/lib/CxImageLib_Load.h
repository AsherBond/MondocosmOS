#ifndef __CXIMAGE_LIBLOAD__H
#define __CXIMAGE_LIBLOAD__H


#define _LIBPATH_	"Z:/Vision/Image/CxImage/lib/"

#ifdef _DEBUG

#ifdef _MSVC_60
#pragma comment(lib, ""_LIBPATH_"cximagecrt599c-mtd-vc6-d.lib")
#endif

#ifdef _MSVC_70
#pragma comment(lib, ""_LIBPATH_"cximagecrt599c-mtd-vc6-d.lib")
#endif

#ifdef _MSVC_80
#pragma comment(lib, ""_LIBPATH_"cximagecrt599c-mtd-vc8-d.lib")
#endif

#else

#ifdef _MSVC_60
#pragma comment(lib, ""_LIBPATH_"cximagecrt599c-mtd-vc6-r.lib")
#endif

#ifdef _MSVC_70
#pragma comment(lib, ""_LIBPATH_"cximagecrt599c-mtd-vc6-r.lib")
#endif

#ifdef _MSVC_80
#pragma comment(lib, ""_LIBPATH_"cximagecrt599c-mtd-vc8-r.lib")
#endif

#endif 

#undef _LIBPATH_


#endif