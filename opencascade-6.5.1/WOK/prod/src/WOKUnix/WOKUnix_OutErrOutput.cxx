#ifndef WNT

#include <WOKUnix_OutErrOutput.ixx>
#include <WOKUnix_FileBuffer.hxx>
#include <WOKUnix_NoBuffer.hxx>
#include <WOKUnix_CantBlockBuffer.hxx>


//=======================================================================
//function : WOKUnix_OutErrOutput
//purpose  : 
//=======================================================================
WOKUnix_OutErrOutput::WOKUnix_OutErrOutput()
{
}


//=======================================================================
//function : WOKUnix_OutErrOutput
//purpose  : 
//=======================================================================
WOKUnix_OutErrOutput::WOKUnix_OutErrOutput(const WOKUnix_FDescr& aoutfd, 
					     const WOKUnix_FDescr& aerrfd, 
					     const WOKUnix_PopenBufferMode amode)
{
  switch(amode)
    {
    case WOKUnix_POPEN_BUFFERED:
      myout = new WOKUnix_FileBuffer(aoutfd, WOKUnix_STDOUT);
      myerr = new WOKUnix_FileBuffer(aerrfd, WOKUnix_STDERR);
      break;
    case WOKUnix_POPEN_IMMEDIATE:
      myout = new WOKUnix_NoBuffer(aoutfd, WOKUnix_STDOUT);
      myerr = new WOKUnix_NoBuffer(aerrfd, WOKUnix_STDERR);
      break;
    case WOKUnix_POPEN_ECHOIFBLOCKED:
      myout = new WOKUnix_CantBlockBuffer(aoutfd, WOKUnix_STDOUT);
      myerr = new WOKUnix_CantBlockBuffer(aerrfd, WOKUnix_STDERR);
      break;
    }      
}

//=======================================================================
//function : WOKUnix_OutErrOutput
//purpose  : 
//=======================================================================
WOKUnix_OutErrOutput::WOKUnix_OutErrOutput(const Handle(WOKUnix_OutErrOutput)& anoutput, 
					      const WOKUnix_PopenBufferMode amode)
{
}

//=======================================================================
//function : Echo
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKUnix_OutErrOutput::Echo() const 
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = myout->Echo();
  aseq->Append(myerr->Echo());
  return aseq;
}

//=======================================================================
//function : Clear
//purpose  : 
//=======================================================================
void WOKUnix_OutErrOutput::Clear() const 
{
  myout->Clear();
  myerr->Clear();
}

//=======================================================================
//function : Errors
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKUnix_OutErrOutput::Errors() const 
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = myout->Errors();
  aseq->Append(myerr->Errors());
  return aseq;
}

//=======================================================================
//function : Select
//purpose  : 
//=======================================================================
void WOKUnix_OutErrOutput::Select(Standard_Integer& afdmax, WOKUnix_Timeval& atimeout,  WOKUnix_FDSet& aset) const 
{
  myout->Select(afdmax, atimeout, aset);
  myerr->Select(afdmax, atimeout, aset);
}

//=======================================================================
//function : Acquit
//purpose  : 
//=======================================================================
void WOKUnix_OutErrOutput::Acquit(const Standard_Integer aselectstatus, const WOKUnix_FDSet& aset) const 
{
  myout->Acquit(aselectstatus, aset);
  myerr->Acquit(aselectstatus, aset);
}

//=======================================================================
//function : Close
//purpose  : 
//=======================================================================
void WOKUnix_OutErrOutput::Close()
{
  myout->Close();
  myerr->Close();
}

#endif
