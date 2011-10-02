#ifndef WNT


#include <WOKUnix_CantBlockBuffer.ixx>

#include <WOKUnix_MaxPipeSize.hxx>

//=======================================================================
//function : WOKUnix_CantBlockBuffer
//purpose  : Constructs a CantBlockBuffer
//=======================================================================
WOKUnix_CantBlockBuffer::WOKUnix_CantBlockBuffer(const WOKUnix_FDescr& afd, const WOKUnix_BufferIs astd) : WOKUnix_Buffer(afd, astd)
{
}

//=======================================================================
//function : Select
//purpose  : Performs settings for the select operation 
//=======================================================================
void WOKUnix_CantBlockBuffer::Select(Standard_Integer& afd, WOKUnix_Timeval& atimeout, WOKUnix_FDSet& aset) const 
{
  afd = ( (afd) > (GetFDescr().FileNo()) ) ? afd : GetFDescr().FileNo();
  atimeout.tv_sec = 0;
  atimeout.tv_usec = 500;
  return;
}

//=======================================================================
//function : Acquit
//purpose  : Performs Acquitement of the select operation
//=======================================================================
void WOKUnix_CantBlockBuffer::Acquit(const Standard_Integer astatus, const WOKUnix_FDSet& aset)  
{
  WOKUnix_FDescr  afd = AssociatedChannel();
  Standard_Integer nbtoread = GetFDescr().GetNbToRead();
  if(nbtoread >= MAX_PIPE_SIZE)
    {
      cerr << "Error : CantBlockBuffer::Acquit : Could be blocked : Echo of " << AssociatedChannel().FileNo() << endl;
      Write(afd);
    }
}

void WOKUnix_CantBlockBuffer::Close()
{
  if(mybuffer.FileNo() >= 0) mybuffer.Close();
  WOKUnix_Buffer::Close();
}
#endif
