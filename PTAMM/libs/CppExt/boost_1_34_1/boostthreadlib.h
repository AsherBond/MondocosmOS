#pragma once

#define _LIBPATH_	"../libs/cppext/boost_1_34_1/lib/"

#if _MSC_VER >= 1400 // for vc8
	#ifdef _DEBUG
	#pragma comment( lib, ""_LIBPATH_"libboost_thread-vc80-mt-gd-1_34_1.lib" )
	#else
	#pragma comment( lib, ""_LIBPATH_"libboost_thread-vc80-mt-1_34_1.lib" )
	#endif
#elif _MSC_VER >= 1310 // for vc71
	#ifdef _DEBUG
	#pragma comment( lib, ""_LIBPATH_"libboost_thread-vc71-mt-gd-1_34_1.lib" )
	#else
	#pragma comment( lib, ""_LIBPATH_"libboost_thread-vc71-mt-1_34_1.lib" )
	#endif
#else
	#ifdef _DEBUG	// for vc6
	#pragma comment( lib, ""_LIBPATH_"libboost_thread-vc6-mt-gd-1_34.lib" )
	#else
	#pragma comment( lib, ""_LIBPATH_"libboost_thread-vc6-mt-1_34.lib" )
	#endif
#endif

#undef _LIBPATH_