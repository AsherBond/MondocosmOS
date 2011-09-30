#pragma  once
// For dll
// #if defined(_AFXDLL)||defined(_USRDLL)
//  #define DLL_EXPORT __declspec(dllexport)
// 	#define DLL_EXP_OBJ __declspec(dllexport) extern
// #elif defined(_MSC_VER)&&(_MSC_VER<1200)
//  #define DLL_EXPORT __declspec(dllimport)
// #else
//  #define DLL_EXPORT
// #endif

#if (defined(_USRDLL))
#define DLL_EXPORT __declspec(dllexport)
#define DLL_EXP_OBJ __declspec(dllexport) extern
#elif (defined(_LIB))
#define DLL_EXPORT __declspec(dllimport)
#define DLL_EXP_OBJ __declspec(dllimport) extern
#else 
#define DLL_EXPORT __declspec(dllimport)
#define DLL_EXP_OBJ __declspec(dllimport) extern
#endif

// For use dll in dll.
#if (defined(_AFXDLL)||defined(_USRDLL))&&defined(_DLLDLLEXPORT)
#define DLL_DLL_EXPORT __declspec(dllexport)
#define DLL_DLL_EXPORT_OBJ __declspec(dllexport) extern
#elif (defined(_LIB))&&defined(_DLLDLLEXPORT)
#define DLL_DLL_EXPORT
#define DLL_DLL_EXPORT_OBJ extern
#else
#define DLL_DLL_EXPORT __declspec(dllimport)
#define DLL_DLL_EXPORT_OBJ __declspec(dllimport) extern
#endif