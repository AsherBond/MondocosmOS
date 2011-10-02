#ifdef WNT
#define STRICT
#include <windows.h>

#include <stdlib.h>
#include <memory.h>

__declspec( dllexport ) int wokCMP ( int argc, char** argv ) 
{
  
  HANDLE hFile1,  hFile2;
  HANDLE hMap1,   hMap2;
  DWORD  dwSize1, dwSize2;
  PVOID  pvAddr1, pvAddr2;
  
  int retVal = 2;
  
  hFile1 = hFile2 = INVALID_HANDLE_VALUE;
  
  pvAddr1 = pvAddr2 = hMap1 = hMap2 = NULL;
 
  if( argc != 3 ) return 2;

  if((hFile1 = CreateFile(argv[ 1 ],             /* file name   */
			  GENERIC_READ,          /* access      */
			  FILE_SHARE_READ,       /* sharing     */
			  NULL,                  /* security    */
			  OPEN_EXISTING,         /* disposition */
			  FILE_ATTRIBUTE_NORMAL, /* attributes  */
			  NULL                   /* template    */
			  )) == INVALID_HANDLE_VALUE )
    retVal = 2;
  else if((hFile2 = CreateFile(argv[ 2 ],             /* file name   */
			  GENERIC_READ,          /* access      */
			  FILE_SHARE_READ,       /* sharing     */
			  NULL,                  /* security    */
			  OPEN_EXISTING,         /* disposition */
			  FILE_ATTRIBUTE_NORMAL, /* attributes  */
			  NULL                   /* template    */
			  )) == INVALID_HANDLE_VALUE)
    retVal = 2;
  else
    {
      dwSize1 = GetFileSize ( hFile1, NULL );
      dwSize2 = GetFileSize ( hFile2, NULL );
      
      if ( dwSize1 != dwSize2 ) retVal = 1;
      else if(( hMap1 = CreateFileMapping(hFile1, NULL, PAGE_READONLY, 0, dwSize1, NULL)) == NULL) retVal = 2;
      else if((hMap2 = CreateFileMapping(hFile2, NULL, PAGE_READONLY, 0, dwSize2, NULL)) == NULL)  retVal = 2;
      else if((pvAddr1 = MapViewOfFile(hMap1, FILE_MAP_READ, 0, 0, dwSize1)) == NULL)  retVal = 2;
      else if((pvAddr2 = MapViewOfFile(hMap2, FILE_MAP_READ, 0, 0, dwSize2)) == NULL)  retVal = 2;
      else 
	retVal = memcmp ( pvAddr1, pvAddr2, dwSize1 );
      
      if ( retVal ) retVal = 1;
    }    
  
  if ( pvAddr2 != NULL ) UnmapViewOfFile ( pvAddr2 );
  if ( pvAddr1 != NULL ) UnmapViewOfFile ( pvAddr1 );
  
  if ( hMap2 != NULL ) CloseHandle ( hMap2 );
  if ( hMap1 != NULL ) CloseHandle ( hMap1 );
  
  if ( hFile2 != INVALID_HANDLE_VALUE ) CloseHandle ( hFile2 );
  if ( hFile1 != INVALID_HANDLE_VALUE ) CloseHandle ( hFile1 );
  
  return retVal;
}
#endif
