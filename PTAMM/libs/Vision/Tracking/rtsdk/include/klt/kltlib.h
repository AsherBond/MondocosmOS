#pragma once

#if _MSC_VER >= 1400 // for vc8
	#ifdef _DEBUG
	#pragma comment( lib, "klt134-win32-vc8-mtd-d.lib" )
	#else
	#pragma comment( lib, "klt134-win32-vc8-mtd-r.lib" )
	#endif
#elif _MSC_VER >= 1310 // for vc71
	#ifdef _DEBUG
	#pragma comment( lib, "klt134-win32-vc71-mtd-d.lib" )
	#else
	#pragma comment( lib, "klt134-win32-vc71-mtd-r.lib" )
	#endif
#else	// for vc6
	#ifdef _DEBUG
	#pragma comment( lib, "klt134-win32-vc6-mtd-d.lib" )
	#else
	#pragma comment( lib, "klt134-win32-vc6-mtd-r.lib" )
	#endif
#endif


