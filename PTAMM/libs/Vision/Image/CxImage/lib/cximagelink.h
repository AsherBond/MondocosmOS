#pragma once

#define _LIBPATH_	"../libs/Vision/Image/CxImage/lib/"

#if _MSC_VER >= 1500 // for vc9
	#ifdef _DEBUG
	#pragma comment( lib, ""_LIBPATH_"cximage599c-mfc-mtd-vc9-d.lib" )
	#else if
	#pragma comment( lib, ""_LIBPATH_"cximage599c-mfc-mtd-vc9-r.lib" )
	#endif
#elif _MSC_VER >= 1400 // for vc8
	#ifdef _DEBUG
	#pragma comment( lib, ""_LIBPATH_"cximage599c-mfc-mtd-vc8-d.lib" )
	#else if
	#pragma comment( lib, ""_LIBPATH_"cximage599c-mfc-mtd-vc8-r.lib" )
	#endif
#elif _MSC_VER >= 1310 // for vc71
	#ifdef _DEBUG
	#pragma comment( lib, ""_LIBPATH_"cximage599c-mfc-mtd-vc71-d.lib" )
	#else if
	#pragma comment( lib, ""_LIBPATH_"cximage599c-mfc-mtd-vc71-r.lib" )
	#endif
#else	// for vc6
	#ifdef _DEBUG
	#pragma comment( lib, ""_LIBPATH_"cximage599c-mfc-mtd-vc6-d.lib" )
	#else if
	#pragma comment( lib, ""_LIBPATH_"cximage599c-mfc-mtd-vc6-r.lib" )
	#endif
#endif

#undef _LIBPATH_