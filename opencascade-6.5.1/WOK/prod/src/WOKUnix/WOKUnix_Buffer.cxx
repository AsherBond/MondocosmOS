#ifndef WNT

#include <WOKUnix_Buffer.ixx>

#include <WOKTools_Messages.hxx>

#include <TCollection_HAsciiString.hxx>
#include <Standard_PCharacter.hxx>

//=======================================================================
//function : WOKUnix_Buffer
//purpose  : 
//=======================================================================
WOKUnix_Buffer::WOKUnix_Buffer(const WOKUnix_FDescr& afd, const WOKUnix_BufferIs astd) : myfd(afd), mystd(astd)
{
}

//=======================================================================
//function : Clear
//purpose  : 
//=======================================================================
void WOKUnix_Buffer::Clear()
{
  Standard_Integer nb = myfd.GetNbToRead();
  while(nb > 0)
    {
      TCollection_AsciiString buffer;
      myfd.Read(buffer, nb);
      nb = myfd.GetNbToRead();
    }
  return;
}

//=======================================================================
//function : GetFDescr
//purpose  : 
//=======================================================================
WOKUnix_FDescr WOKUnix_Buffer::GetFDescr() const 
{
  return myfd;
}

//=======================================================================
//function : SetFDescr
//purpose  : 
//=======================================================================
void WOKUnix_Buffer::SetFDescr(const WOKUnix_FDescr& afd)
{
  myfd = afd;
}

//=======================================================================
//function : BufferIs
//purpose  : 
//=======================================================================
WOKUnix_BufferIs WOKUnix_Buffer::BufferIs() const 
{
  return mystd;
}

//=======================================================================
//function : AssociatedChannel
//purpose  : 
//=======================================================================
WOKUnix_FDescr WOKUnix_Buffer::AssociatedChannel() const
{
  switch(mystd)
    {
    case WOKUnix_STDOUT: 
      return WOKUnix_FDescr::Stdout();
    case WOKUnix_STDERR:
      return WOKUnix_FDescr::Stderr();
    default:
      return WOKUnix_FDescr(-1);
    }
}


//=======================================================================
//function : Echo
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKUnix_Buffer::Echo()  
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer nbtoread = myfd.GetNbToRead();
  Standard_Integer i=1;

  if(!nbtoread) return aseq;

  TCollection_AsciiString buffer;
  TCollection_AsciiString astr;

  myfd.Read(buffer, nbtoread);

  Standard_PCharacter aptr   = (Standard_PCharacter)buffer.ToCString();
  Standard_PCharacter aptr2  = (Standard_PCharacter)buffer.ToCString();
  
  i=0;
  while(i<nbtoread)
    {
      if(*aptr2 == '\n')
	{
	  *aptr2 = '\0';
	  aseq->Append(new TCollection_HAsciiString(aptr));
	  aptr = aptr2;
	  aptr++;
	}
      aptr2++;
      i++;
    }
  if(aptr < aptr2)
    {
      aseq->Append(new TCollection_HAsciiString(aptr));
    }

  return aseq;
}

//=======================================================================
//function : Errors
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKUnix_Buffer::Errors()  
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer nbtoread = myfd.GetNbToRead();
  Standard_Integer i=1;
  TCollection_AsciiString buffer;
  TCollection_AsciiString astr;

  myfd.Read(buffer, nbtoread);

  Standard_PCharacter aptr   = (Standard_PCharacter)buffer.ToCString();
  Standard_PCharacter aptr2  = (Standard_PCharacter)buffer.ToCString();
  
  i=0;
  while(i<nbtoread)
    {
      if(*aptr2 == '\n')
	{
	  *aptr2 = '\0';
	  aseq->Append(new TCollection_HAsciiString(aptr));
	  aptr = aptr2;
	  aptr++;
	}
      aptr2++;
      i++;
    }
  if(aptr < aptr2)
    {
      aseq->Append(new TCollection_HAsciiString(aptr));
    }

  return aseq;
}

//=======================================================================
//function : Write
//purpose  : 
//=======================================================================
void WOKUnix_Buffer::Write( WOKUnix_FDescr& afd)  
{
  Standard_Integer nb = myfd.GetNbToRead();

  if(nb > 0 )
    {
      TCollection_AsciiString buffer;

      myfd.Read(buffer, nb);
      afd.Reset();
      afd.Write(buffer, nb);
    }
}

//=======================================================================
//function : Close
//purpose  : 
//=======================================================================
void WOKUnix_Buffer::Close()
{
  if(myfd.FileNo() >= 0) myfd.Close();
}
#endif
