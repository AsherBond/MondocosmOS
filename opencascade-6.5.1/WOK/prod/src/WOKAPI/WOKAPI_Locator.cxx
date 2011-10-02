// File:	WOKAPI_Locator.cxx
// Created:	Wed Apr 10 20:51:58 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>



#include <WOKernel_Entity.hxx>
#include <WOKernel_File.hxx>

#include <WOKAPI_Entity.hxx>
#include <WOKAPI_File.hxx>

#include <WOKAPI_Locator.ixx>

//=======================================================================
//function : WOKAPI_Locator
//purpose  : 
//=======================================================================
WOKAPI_Locator::WOKAPI_Locator()
{
}

//=======================================================================
//function : WOKAPI_Locator
//purpose  : 
//=======================================================================
WOKAPI_Locator::WOKAPI_Locator(const Handle(WOKernel_Locator)& alocator)
{
  mylocator = alocator;
}



//=======================================================================
//function : Set
//purpose  : 
//=======================================================================
void WOKAPI_Locator::Set(const WOKAPI_Workbench& awb)
{
  if(awb.IsValid())
    mylocator = new WOKernel_Locator(Handle(WOKernel_Workbench)::DownCast(awb.Entity()));
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================
void WOKAPI_Locator::Set(const WOKAPI_Session& asession, 
			       const Handle(TColStd_HSequenceOfHAsciiString)& avisibility)
{
  Handle(TColStd_HSequenceOfHAsciiString)  thevisibility = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i;

  if(!asession.IsValid()) return;
  
  for(i=1; i<=avisibility->Length(); i++)
    {
      WOKAPI_Entity anent(asession,avisibility->Value(i));
      
      if(anent.IsValid())
	{
	  thevisibility->Append(anent.UserPath());
	}
      else return;
    }
  mylocator = new WOKernel_Locator(asession.Session(), thevisibility);
  return;
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================
void WOKAPI_Locator::Set(const Handle(WOKernel_Locator)& alocator)
{
  mylocator = alocator;
}

Handle(WOKernel_Locator) WOKAPI_Locator::Locator() const
{
  return mylocator;
}

//=======================================================================
//function : IsValid
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Locator::IsValid() const
{
  return !mylocator.IsNull();
}

//=======================================================================
//function : Reset
//purpose  : 
//=======================================================================
void WOKAPI_Locator::Reset() const 
{
  if(IsValid()) mylocator->Reset();
}

//=======================================================================
//function : Check
//purpose  : 
//=======================================================================
void WOKAPI_Locator::Check() const 
{
  if(IsValid()) mylocator->Check();
}

//=======================================================================
//function : Locate
//purpose  : 
//=======================================================================
WOKAPI_File WOKAPI_Locator::Locate(const Handle(TCollection_HAsciiString)& alocatorname) const 
{
  WOKAPI_File afile;
  
  if(!IsValid()) return afile ;
  else afile.Set(mylocator->Locate(alocatorname));
  if(afile.IsValid()) afile.Located();
  return afile;
}

//=======================================================================
//function : Locate
//purpose  : 
//=======================================================================
WOKAPI_File WOKAPI_Locator::Locate(const WOKAPI_Entity& anent,
				   const Handle(TCollection_HAsciiString)& atype, 
				   const Handle(TCollection_HAsciiString)& aname) const
{
  WOKAPI_File afile;
  if(IsValid())
    afile.Set(mylocator->Locate(anent.Name(), atype, aname));
  if(afile.IsValid()) afile.Located();
  return afile;
}

//=======================================================================
//function : Locate
//purpose  : 
//=======================================================================
WOKAPI_File WOKAPI_Locator::Locate(const WOKAPI_Entity& anent,
				   const Handle(TCollection_HAsciiString)& atype) const
{
  WOKAPI_File afile;
  if(IsValid())
    afile.Set(mylocator->Locate(anent.Name(), atype, Handle(TCollection_HAsciiString)()));
  if(afile.IsValid()) afile.Located();
  return afile;
}

//=======================================================================
//function : Locate
//purpose  : 
//=======================================================================
void WOKAPI_Locator::Locate(WOKAPI_File& file) const
{
  Handle(WOKernel_File) afile;

  if(!file.IsValid()) return;

  if(IsValid())
    afile = mylocator->Locate(file.NestingEntity().Name(), file.Type(), file.Name());

  if(!afile.IsNull()) 
    {
      file.Set(afile);
      file.Located();
    }
  return;
}

//=======================================================================
//function : LocateUnit
//purpose  : 
//=======================================================================
WOKAPI_Unit WOKAPI_Locator::LocateUnit(const Handle(TCollection_HAsciiString)& aunitname) const 
{
  WOKAPI_Unit aunit;
  
  if(IsValid())
    aunit.Set(mylocator->LocateDevUnit(aunitname));
  return aunit;
}

