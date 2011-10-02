#ifndef WNT
#include <WOKUnix_NoBuffer.ixx>

 WOKUnix_NoBuffer::WOKUnix_NoBuffer(const WOKUnix_FDescr& afd, const WOKUnix_BufferIs astd) : WOKUnix_Buffer(afd, astd)
{
}

void WOKUnix_NoBuffer::Select(Standard_Integer& afd, WOKUnix_Timeval& atimeout, WOKUnix_FDSet& aset) const 
{
  afd = ( afd > GetFDescr().FileNo() ) ? afd : GetFDescr().FileNo();
  if(atimeout.tv_sec < 0)
    {
      atimeout.tv_usec = 0;
    }
  else
    {
      atimeout.tv_sec = 0;
      atimeout.tv_usec = 0;
    }

  FD_SET(GetFDescr().FileNo(), &aset);
  GetFDescr().Flush();
  return;
}

void WOKUnix_NoBuffer::Acquit(const Standard_Integer astatus, const WOKUnix_FDSet& aset)  
{
  WOKUnix_FDescr afd = AssociatedChannel();
  if(astatus == 0)
    {
      WOKUnix_ProcessTimeOut::Raise("NoBuffer::Acquit : time out\n");
      return;
    }
  else
    {
      Write(afd);
    }
  return;
}


void WOKUnix_NoBuffer::Close()
{
  
}
#endif
