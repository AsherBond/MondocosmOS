// File:	WOKAPI_MakeStep.cxx
// Created:	Wed Apr  3 23:10:10 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKMake_Step.hxx>

#include <WOKAPI_MakeStep.ixx>

//=======================================================================
//function : WOKAPI_MakeStep
//purpose  : 
//=======================================================================
WOKAPI_MakeStep::WOKAPI_MakeStep()
{
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================
void WOKAPI_MakeStep::Set(const Handle(WOKMake_Step)& atep)
{
  mystep = atep;
}

//=======================================================================
//function : IsToExecute
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_MakeStep::IsToExecute() const 
{
  if(mystep.IsNull()) return Standard_False;
  return mystep->IsToExecute();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Code
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_MakeStep::Code() const
{
  Handle(TCollection_HAsciiString) result;
  if(!mystep.IsNull())
    {
      result = mystep->Code();
    }
  return result;
}


//=======================================================================
//function : UniqueName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_MakeStep::UniqueName() const
{
  Handle(TCollection_HAsciiString) result;
  if(!mystep.IsNull())
    {
      result = mystep->UniqueName();
    }
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Input
//purpose  : 
//=======================================================================
//Standard_Integer WOKAPI_MakeStep::Input(WOKAPI_SequenceOfFile& aseq) const
Standard_Integer WOKAPI_MakeStep::Input(WOKAPI_SequenceOfFile& ) const
{
  if(mystep.IsNull()) return 1;

  Handle(WOKMake_HSequenceOfInputFile) tmp;

  return 0;
}
