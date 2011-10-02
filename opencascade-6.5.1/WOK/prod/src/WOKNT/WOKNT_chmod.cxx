#ifdef WNT
#define EXPORT
#include <Standard_Stream.hxx>
#include <stdlib.h>

#include <OSD_WNT.hxx>
#include <OSD_WNT_1.hxx>
#include <OSD_File.hxx>
#include <OSD_Path.hxx>
#include <OSD_Protection.hxx>

#define FLAG_SYSTEM  0x01
#define FLAG_OWNER   0x02
#define FLAG_GROUP   0x04
#define FLAG_WORLD   0x08

#define OPER_GRANT   0
#define OPER_DENY    1

#define PROT_READ    0x01
#define PROT_WRITE   0x02
#define PROT_EXECUTE 0x04
#define PROT_DELETE  0x08

#define I_SYSTEM 0
#define I_OWNER  1
#define I_GROUP  2
#define I_WORLD  3

typedef struct _prt_data {

                BYTE fUser;
                BYTE fProt;
                BOOL fGrant;
                BOOL fRecurse;

               } PRT_DATA, *PPRT_DATA;

typedef BOOL ( *CHMOD_FUNC ) ( LPCTSTR, BOOL, void* );

static BOOL _chmod_func ( LPTSTR, BOOL, void* );

#define USAGE() { cerr << "Usage: wokCHMOD [-R] [sugoa]{+|-}[rwxd] file(s)\n"; return 1; }

static int nFiles;

extern "C" __declspec( dllexport ) int wokCHMOD ( int, char** );

int wokCHMOD ( int argc, char** argv ) {

 int      i, retVal = 0;
 char*    ptr;
 PRT_DATA pd = { 0, 0, 0 };
 BOOL     fRecurse;
 char     path[ _MAX_PATH ];
 char     name[ _MAX_FNAME + _MAX_EXT ];
 char     drive[ _MAX_DRIVE ];
 char     dir[ _MAX_DIR ];
 char     fname[ _MAX_FNAME ];
 char     ext[ _MAX_EXT ];

 if ( argc < 3 ) USAGE();

 if (  *argv[ 1 ] == '-' && ( argv[ 1 ][ 1 ] == 'r' || argv[ 1 ][ 1 ] == 'R' )  ) {
 
  fRecurse = pd.fRecurse = TRUE;
  ptr      = argv[ 2 ];
  i        = 3;
 
 } else {
 
  fRecurse = pd.fRecurse = FALSE;
  ptr      = argv[ 1 ];
  i        = 2;
 
 }  // end else
 
 while ( *ptr != '+' && *ptr != '-' ) {
 
  if ( *ptr == 0 ) USAGE();

  switch ( *ptr ) {
  
   case 's':
   case 'S':

    pd.fUser |= FLAG_SYSTEM;

   break;

   case 'o':
   case 'O':
   case 'u':
   case 'U':

    pd.fUser |= FLAG_OWNER;

   break;

   case 'g':
   case 'G':

    pd.fUser |= FLAG_GROUP;

   break;

   case 'a':
   case 'A':

    pd.fUser |= FLAG_WORLD;

   break;

   default:

    USAGE();

  }  // end switch

  ++ptr;
 
 }  // end while

 pd.fGrant = ( *ptr++ == '+' ) ? TRUE : FALSE;

 while ( *ptr != 0 ) {

  switch ( *ptr ) {
  
   case 'r':
   case 'R':

    pd.fProt |= PROT_READ;

   break;

   case 'w':
   case 'W':

    pd.fProt |= PROT_WRITE;

   break;

   case 'x':
   case 'X':

    pd.fProt |= PROT_EXECUTE;

   break;

   case 'd':
   case 'D':

    pd.fProt |= PROT_DELETE;

   break;

   default:

    USAGE();
  
  }  // end switch
 
  ++ptr;
 
 }  // end while

 for ( ; i < argc; ++i ) {

  nFiles = 0;
  _splitpath ( argv[ i ], drive, dir, fname, ext );
  _makepath  ( path, drive, dir, NULL, NULL );
  _makepath  ( name, NULL, NULL, fname, ext );

  if (  !DirWalk ( path, name, ( CHMOD_FUNC )_chmod_func, fRecurse, &pd )  ) retVal = 1;
 
  if ( nFiles == 0 ) {
 
   cerr << "wokCHMOD: could not find " << argv[ i ] << endl << flush;
   retVal = 1;
 
  }   // end if

 }  // end for

 return retVal;

}  // end main

static BOOL _chmod_func ( LPTSTR fName, BOOL fDir, void* data ) {

 int                  i, j;
 OSD_Protection       prot;
 OSD_File             file;
 OSD_SingleProtection pArr[ 4 ];
 OSD_SingleProtection pDef[ 4 ] = { OSD_R, OSD_W, OSD_X, OSD_D };
 unsigned char        flags[ 4 ] = { 0, 0, 0, 0 };
 unsigned char        prt[ 4 ] = { 0, 0, 0, 0 };
 PPRT_DATA            pData = ( PPRT_DATA )data;

 ++nFiles;

 if ( fDir && !pData -> fRecurse ) return TRUE;

 file.SetPath (  OSD_Path ( fName )  );
 prot = file.Protection ();

 if (  file.Failed ()  ) {
 
  cerr << "wokCHMOD: ";
  file.Perror ();
  return FALSE;
 
 }  // end if

 pArr[ I_SYSTEM ] = prot.System ();
 pArr[ I_OWNER  ] = prot.User ();
 pArr[ I_GROUP  ] = prot.Group ();
 pArr[ I_WORLD  ] = prot.World ();

 if ( pData -> fUser & FLAG_SYSTEM ) flags[ I_SYSTEM ] = 1;
 if ( pData -> fUser & FLAG_OWNER  ) flags[ I_OWNER  ] = 1;
 if ( pData -> fUser & FLAG_GROUP  ) flags[ I_GROUP  ] = 1;
 if ( pData -> fUser & FLAG_WORLD  ) flags[ I_WORLD  ] = 1;

 if ( pData -> fProt & PROT_READ    ) prt[ 0 ] = 1;
 if ( pData -> fProt & PROT_WRITE   ) prt[ 1 ] = 1;
 if ( pData -> fProt & PROT_EXECUTE ) prt[ 2 ] = 1;
 if ( pData -> fProt & PROT_DELETE  ) prt[ 3 ] = 1;

 if ( flags[ I_SYSTEM ] == 0 &&
      flags[ I_OWNER  ] == 0 &&
      flags[ I_GROUP  ] == 0 &&
      flags[ I_WORLD  ] == 0
 ) flags[ I_OWNER ] = 1;

 for ( i = 0; i < 4; ++i )

  for ( j = 0; j < 4; ++j )
 
   if ( prt[ j ] )

    if ( flags[ i ] )

     if ( !pData -> fGrant )

      prot.Sub ( pArr[ i ], pDef[ j ] );

     else

      prot.Add ( pArr[ i ], pDef[ j ] );

 prot.SetSystem ( pArr[ I_SYSTEM ] );
 prot.SetUser   ( pArr[ I_OWNER  ] );
 prot.SetGroup  ( pArr[ I_GROUP  ] );
 prot.SetWorld  ( pArr[ I_WORLD  ] );

 file.SetProtection ( prot );

 if (  file.Failed ()  ) {
 
  cerr << "wokCHMOD: ";
  file.Perror ();
  return FALSE;
 
 }  // end if

 return TRUE;

}  // end _chmod_func
#endif
