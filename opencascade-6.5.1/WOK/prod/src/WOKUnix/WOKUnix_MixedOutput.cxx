#ifndef WNT

#include <WOKUnix_MixedOutput.ixx>


#include <WOKUnix_FileBuffer.hxx>
#include <WOKUnix_NoBuffer.hxx>
#include <WOKUnix_CantBlockBuffer.hxx>

//=======================================================================
//function : WOKUnix_MixedOutput
//purpose  : 
//=======================================================================
WOKUnix_MixedOutput::WOKUnix_MixedOutput()
{
}

//=======================================================================
//function : WOKUnix_MixedOutput
//purpose  : 
//=======================================================================
WOKUnix_MixedOutput::WOKUnix_MixedOutput(const WOKUnix_FDescr& afd, const WOKUnix_PopenBufferMode amode)
{
  switch(amode)
    {
    case WOKUnix_POPEN_BUFFERED:
      myout = new WOKUnix_FileBuffer(afd, WOKUnix_STDOUT);
      break;
    case WOKUnix_POPEN_IMMEDIATE:
      myout = new WOKUnix_NoBuffer(afd, WOKUnix_STDOUT);
      break;
    case WOKUnix_POPEN_ECHOIFBLOCKED:
      myout = new WOKUnix_CantBlockBuffer(afd, WOKUnix_STDOUT);
      break;
    }      
}

//=======================================================================
//function : Echo
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKUnix_MixedOutput::Echo() const 
{
  return myout->Echo();
}

void WOKUnix_MixedOutput::Clear() const 
{
  myout->Clear();
}

//=======================================================================
//function : Errors
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString)  WOKUnix_MixedOutput::Errors() const 
{
  return myout->Errors();
}

//=======================================================================
//function : Select
//purpose  : 
//=======================================================================
void WOKUnix_MixedOutput::Select(Standard_Integer& afdmax, WOKUnix_Timeval& atimeout,  WOKUnix_FDSet& aset) const 
{
  myout->Select(afdmax, atimeout, aset);
}

//=======================================================================
//function : Acquit
//purpose  : 
//=======================================================================
void WOKUnix_MixedOutput::Acquit(const Standard_Integer afd, const WOKUnix_FDSet& aset) const 
{
  myout->Acquit(afd, aset);
}

//=======================================================================
//function : Close
//purpose  : 
//=======================================================================
void WOKUnix_MixedOutput::Close()
{
  myout->Close();
}

#endif
