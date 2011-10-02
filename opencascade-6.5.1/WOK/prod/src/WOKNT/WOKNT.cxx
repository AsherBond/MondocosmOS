


#ifdef WNT

#include <windows.h>

#ifdef CreateFile
# undef CreateFile
#endif  // CreateFile

#ifdef CreateDirectory
# undef CreateDirectory
#endif  // CreateFile

#ifdef RemoveDirectory
# undef RemoveDirectory
#endif  // CreateFile

#include <WOKNT.hxx>


Standard_Integer WOKNT::SystemLastError() 
{
  return GetLastError();
}

Standard_CString WOKNT::SystemMessage(const Standard_Integer errCode) 
{
  static Standard_Character buffer[ 1024 ];
  
  if(!FormatMessage ( FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ARGUMENT_ARRAY,
		     0, errCode, MAKELANGID( LANG_NEUTRAL, SUBLANG_NEUTRAL ),
		     buffer, 2048, NULL)) 
    { 
      wsprintf ( buffer, "error code %d", errCode );
      SetLastError ( errCode );
    }
  return buffer;
}
Standard_CString WOKNT::LastSystemMessage() 
{
  return SystemMessage(SystemLastError());
}




#endif
