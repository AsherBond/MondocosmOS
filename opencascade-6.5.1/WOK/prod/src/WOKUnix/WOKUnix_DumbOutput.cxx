#ifndef WNT

#include <WOKUnix_DumbOutput.ixx>

//=======================================================================
//function : WOKUnix_DumbOutput
//purpose  : 
//=======================================================================
 WOKUnix_DumbOutput::WOKUnix_DumbOutput()
{
}


//=======================================================================
//function : Clear
//purpose  : 
//=======================================================================
void WOKUnix_DumbOutput::Clear() const 
{
}

//=======================================================================
//function : Echo
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKUnix_DumbOutput::Echo() const 
{
  return new TColStd_HSequenceOfHAsciiString;
}

//=======================================================================
//function : Errors
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString)  WOKUnix_DumbOutput::Errors() const 
{
  return new TColStd_HSequenceOfHAsciiString;
}

//=======================================================================
//function : Select
//purpose  : 
//=======================================================================
void WOKUnix_DumbOutput::Select(Standard_Integer& afdmax, WOKUnix_Timeval& atimeout,  WOKUnix_FDSet& aset) const 
{
}

//=======================================================================
//function : Acquit
//purpose  : 
//=======================================================================
void WOKUnix_DumbOutput::Acquit(const Standard_Integer selectstatus, const WOKUnix_FDSet& aset) const 
{
}

//=======================================================================
//function : Close
//purpose  : 
//=======================================================================
void WOKUnix_DumbOutput::Close()
{
}

#endif
