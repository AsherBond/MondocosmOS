#pragma once

#define _LIBPATH_	"Z:\\Vision\\Image\\simpleimage\\lib\\"

#if _MSC_VER >= 1500 // for vc9
	#ifdef _DEBUG
	#pragma comment(lib, ""_LIBPATH_"simpleimage02-win32-mtd-vc9-d.lib")
	#else if
	#pragma comment(lib, ""_LIBPATH_"simpleimage02-win32-mtd-vc9-r.lib")
	#endif
	
#elif _MSC_VER >= 1400 // for vc8
	#ifdef _DEBUG
	#pragma comment(lib, ""_LIBPATH_"simpleimage02-win32-mtd-vc8-d.lib")
	#else if
	#pragma comment(lib, ""_LIBPATH_"simpleimage02-win32-mtd-vc8-r.lib")
	#endif
// #elif _MSC_VER >= 1310 // for vc71
	// #ifdef _DEBUG
	// #pragma comment(lib, ""_LIBPATH_"simpleimage01-win32-mtd-vc7-d.lib")
	// #else if
	// #pragma comment(lib, ""_LIBPATH_"simpleimage01-win32-mtd-vc7-r.lib")
	// #endif
#else
	#ifdef _DEBUG
	#pragma comment(lib, ""_LIBPATH_"simpleimage01-win32-mtd-vc7-d.lib")
	#else if
	#pragma comment(lib, ""_LIBPATH_"simpleimage01-win32-mtd-vc7-r.lib")
	#endif
#endif

#undef _LIBPATH_