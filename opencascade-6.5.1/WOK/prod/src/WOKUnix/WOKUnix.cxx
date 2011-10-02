#ifndef WNT

#include <WOKUnix.ixx>

#include <string.h>
#include <errno.h>


Standard_Integer WOKUnix::SystemLastError()
{
  return errno;
}


Standard_CString WOKUnix::SystemMessage(const Standard_Integer errCode)
{
  return strerror(errCode);
}



Standard_CString WOKUnix::LastSystemMessage()
{
  return strerror(SystemLastError());
}

#endif
