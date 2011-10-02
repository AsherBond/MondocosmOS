#ifdef WNT
#define STRICT
#include <windows.h>
#include <string.h>

#include <OSD_Path.hxx>
#include <OSD_File.hxx>
#include <WOKNT_Path.hxx>

#define BUFFER_SIZE 4096

#define USAGE() { cerr << "Usage: wokCP [-pr] source destination\n"; return ( 1 ); }

extern "C" int __declspec( dllexport ) wokCP ( int, char** );

int wokCP( int argc, char** argv ) 
{
  int   i, retVal = 1;
  char* src = NULL, *dst = NULL, *ptr;
  BOOL  fPreserve = FALSE, fRecurse = FALSE;
  
  DWORD dwBytesRead, dwBytesWritten;
  BYTE  buffer[ BUFFER_SIZE ];
  
  if ( argc < 3 ) USAGE();
  
  for( i=1; i<argc; ++i) 
    {
      if( argv[i][0] == '-' ) 
	{
	  ptr=argv[i];

	  while( *++ptr != 0 ) 
	    {
	      switch ( *ptr ) 
		{
		case 'p':
		case 'P':
		  fPreserve=TRUE;
		  break;
		case 'r':
		case 'R':
		  fRecurse=TRUE;
		  break;
		default:
		  USAGE();
		}
	    }
	}
      else 
	break;
    }

  if( i==argc ) USAGE();
  
  src=argv[i++];

  if( i==argc) USAGE();

  dst=argv[i];

  if(lstrcmp(src,dst)==0) return 0;

  if ( src && dst ) 
    {
      Handle(WOKNT_Path) woksrc = new WOKNT_Path(new TCollection_HAsciiString(src));
      Handle(WOKNT_Path) wokdst = new WOKNT_Path(new TCollection_HAsciiString(dst));
      Handle(WOKNT_Path) newdst;

      if(woksrc->IsFile() && wokdst->IsDirectory())
	{
	  newdst = new WOKNT_Path(wokdst->Name()->ToCString(), woksrc->FileName()->ToCString());
	  dst = (char *) newdst->Name()->ToCString();
	}
      
      if(fRecurse) 
	{
	  OSD_Path pathSrc(src);
	  OSD_Path pathDst(dst);

	  OSD_File source( pathSrc );

	  source.Copy(pathDst);

	  if(source.Failed())
	    source.Perror ();
	  else
	    retVal = 0;
	}
      else
	{
	  if(fPreserve)
	    {
	      if(!CopyFile(src,dst,FALSE))
		return retVal;
	      retVal = 0;
	    }
	  else 
	    {
	      HANDLE hFileSrc, hFileDst;
	      
	      hFileSrc = CreateFile ( src, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL );
	      hFileDst = CreateFile ( dst, GENERIC_WRITE, 0,              NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL );

	      if ( hFileSrc == INVALID_HANDLE_VALUE || hFileDst == INVALID_HANDLE_VALUE )
		return retVal;

	      while(TRUE) 
		{
		  if( !ReadFile( hFileSrc, buffer, BUFFER_SIZE, &dwBytesRead, NULL)) return retVal;
		  if( dwBytesRead == 0 ) break;
		  
		  if( !WriteFile ( hFileDst, buffer, dwBytesRead, &dwBytesWritten, NULL ) ||
		      dwBytesWritten != dwBytesRead ) return retVal;
   } 
    CloseHandle ( hFileDst );
    CloseHandle ( hFileSrc );

    retVal = 0;
  
   } 
  }  
 }
 return retVal;
} 
#endif
