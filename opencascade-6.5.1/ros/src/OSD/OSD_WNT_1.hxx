#ifndef __OSD_WNT_H
# define __OSD_WNT_H

# ifndef STRICT
#  define STRICT
# endif  /* STRICT */

# ifndef _INC_WINDOWS
#  include <windows.h>
# endif  /* _INC_WINDOWS */

# ifndef _INC_TCHAR
#  include <tchar.h>
# endif  /* _INC_TCHAR */

# ifndef OSDAPI
#  if !defined(HAVE_NO_DLL)
#   ifdef __OSD_DLL
#    define OSDAPI __declspec( dllexport )
#   else
#    define OSDAPI __declspec( dllimport )
#   endif  /* OSD_DLL */
#  else
#   define OSDAPI
#  endif
# endif  /* OSDAPI */

#if defined(__CYGWIN32__) || defined(__MINGW32__)
#define _TINT int
#endif

# ifdef __cplusplus
extern "C" {
# endif  /* __cplusplus */

enum DIR_RESPONSE { DIR_ABORT, DIR_RETRY, DIR_IGNORE };

enum MB_ITEMTYPE { MBT_BUTTON, MBT_ICON };

#define FLAG_READ_PIPE  0x00000001
#define FLAG_EOF        0x00000002
#define FLAG_FILE       0x00000004
#define FLAG_DIRECTORY  0x00000008
#define FLAG_PIPE       0x00000010
#define FLAG_SOCKET     0x00000020
#define FLAG_NAMED_PIPE 0x00000040
#define FLAG_DEVICE     0x00000080
#define FLAG_TYPE       0x0000007C

// 2 macros modified for VisualAge
//#define LODWORD( a ) ( DWORD )(   ( ( DWORDLONG )( a ) ) & 0x00000000FFFFFFFF   )
//#define HIDWORD( a ) ( DWORD )(   ( ( DWORDLONG )( a ) ) >> 32                  )

#define LODWORD( a ) ( DWORD )(   ( ( _int64 )( a ) ) & 0x00000000FFFFFFFF   )
#define HIDWORD( a ) ( DWORD )(   ( ( _int64 )( a ) ) >> 32                  )

typedef struct _group_sid {

                DWORD  nCount;
                PSID*  pSID;

               } GROUP_SID, *PGROUP_SID;

typedef struct _MB_DESC {

                MB_ITEMTYPE   itemType;
                _TINT         itemId;
                _TCHAR*       buttonLabel;

               } MB_DESC, *LPMB_DESC;


typedef struct _file_ace {

                ACE_HEADER header;
                DWORD      dwMask;
                PSID       pSID;

               } FILE_ACE, *PFILE_ACE;

typedef void ( *MOVE_DIR_PROC   ) ( LPCTSTR, LPCTSTR );
typedef void ( *COPY_DIR_PROC   ) ( LPCTSTR, LPCTSTR );
typedef void ( *DELETE_DIR_PROC ) ( LPCTSTR          );

typedef DIR_RESPONSE ( *RESPONSE_DIR_PROC ) ( LPCTSTR );

#define GET_SID( pACE ) (  ( PSID )(   (  ( PBYTE )pACE    ) + \
                                       sizeof ( ACE_HEADER ) + \
                                       sizeof ( DWORD      )   \
                                   )                           \
                        )

#define GET_MSK( pACE ) (  ( PDWORD )(   (  ( PBYTE )pACE    ) + \
                                         sizeof ( ACE_HEADER )   \
                                     )                           \
                        )

PSECURITY_DESCRIPTOR OSDAPI AllocSD ( void                 );
void                 OSDAPI FreeSD  ( PSECURITY_DESCRIPTOR );

LPVOID OSDAPI GetTokenInformationEx ( HANDLE, TOKEN_INFORMATION_CLASS );
void   OSDAPI FreeTokenInformation  ( LPVOID                          );

PSECURITY_DESCRIPTOR OSDAPI GetFileSecurityEx ( LPCTSTR, SECURITY_INFORMATION );
void                 OSDAPI FreeFileSecurity  ( PSECURITY_DESCRIPTOR          );

BOOL OSDAPI LookupAccountSidEx ( PSID, LPTSTR*, LPTSTR* );
void OSDAPI FreeAccountNames   ( LPTSTR, LPTSTR         );

PSID OSDAPI GetSecurityDescriptorOwnerEx ( PSECURITY_DESCRIPTOR );
PSID OSDAPI GetSecurityDescriptorGroupEx ( PSECURITY_DESCRIPTOR );
PACL OSDAPI GetSecurityDescriptorDaclEx  ( PSECURITY_DESCRIPTOR );

PACL OSDAPI CreateAcl ( DWORD );
void OSDAPI FreeAcl   ( PACL  );

BOOL OSDAPI PredefinedSid   ( PSID );
BOOL OSDAPI NtPredefinedSid ( PSID );
PSID OSDAPI AdminSid        ( void );
PSID OSDAPI WorldSid        ( void );
PSID OSDAPI InteractiveSid  ( void );
PSID OSDAPI NetworkSid      ( void );
PSID OSDAPI LocalSid        ( void );
PSID OSDAPI DialupSid       ( void );
PSID OSDAPI BatchSid        ( void );
PSID OSDAPI CreatorOwnerSid ( void );
PSID OSDAPI NullSid         ( void );
PSID OSDAPI NtSid           ( void );

PSID OSDAPI CopySidEx ( PSID );
void OSDAPI FreeSidEx ( PSID );

PGROUP_SID OSDAPI AllocGroupSid ( PSECURITY_DESCRIPTOR );
void       OSDAPI FreeGroupSid  ( PGROUP_SID           );

PVOID OSDAPI AllocAccessAllowedAce ( DWORD, BYTE, PSID );
void  OSDAPI FreeAce ( PVOID );

BOOL OSDAPI DeleteDirectory ( LPCTSTR          );
BOOL OSDAPI MoveDirectory   ( LPCTSTR, LPCTSTR );
BOOL OSDAPI CopyDirectory   ( LPCTSTR, LPCTSTR );

void OSDAPI SetDeleteDirectoryProc   ( DELETE_DIR_PROC   );
void OSDAPI SetMoveDirectoryProc     ( MOVE_DIR_PROC     );
void OSDAPI SetCopyDirectoryProc     ( COPY_DIR_PROC     );
void OSDAPI SetResponseDirectoryProc ( RESPONSE_DIR_PROC );

BOOL OSDAPI DirWalk (  LPCTSTR, LPCTSTR, BOOL ( * ) ( LPCTSTR, BOOL, void* ), BOOL, void*  );

_TINT OSDAPI MsgBox ( HWND, LPTSTR, LPCTSTR, _TINT, LPMB_DESC );

void    OSDAPI WNT_InitTimer ( void );
LPCTSTR OSDAPI WNT_StatTimer ( void );

void OSDAPI _debug_break ( LPTSTR );

# ifdef __cplusplus
}
# endif  /* __cplusplus */

#endif  /* __OSD_WNT_H */
