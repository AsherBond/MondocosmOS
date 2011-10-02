// File:	WOKernel_UnitTypeBase.cxx
// Created:	Mon Jun  9 10:50:41 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>

#include <TCollection_HAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Param.hxx>

#include <WOKernel_UnitTypeBase.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKernel_UnitTypeBase
//purpose  : 
//=======================================================================
WOKernel_UnitTypeBase::WOKernel_UnitTypeBase()
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Clear
//purpose  : 
//=======================================================================
void WOKernel_UnitTypeBase::Clear() 
{
  mytypes.Clear();
}



//=======================================================================
//Author   : Jean Gautier (jga)
//function : LoadBase
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_UnitTypeBase::LoadBase(const WOKUtils_Param& params) 
{
  
  Handle(TCollection_HAsciiString) types = params.Eval("%WOKEntity_UnitTypes");

  if(types.IsNull())
    {
      ErrorMsg() << "WOKernel_UnitTypeBase::LoadBase" 
	       << "Could not eval parameter %WOKEntity_UnitTypes : unit types cannot be loaded" << endm;
      return Standard_True;
    }

  Handle(TCollection_HAsciiString) atype = types->Token(" \t", 1);
  Standard_Integer i = 1;
  Standard_Boolean status = Standard_False;

  while(!atype->IsEmpty())
    {

      Handle(TCollection_HAsciiString) akeyvar = new TCollection_HAsciiString("%WOKEntity_");
      akeyvar->AssignCat(atype);
      akeyvar->AssignCat("_Key");

      Handle(TCollection_HAsciiString) akey = params.Eval(akeyvar->ToCString());

      if(akey.IsNull())
	{
	  ErrorMsg() << "WOKernel_UnitTypeBase::LoadBase" 
	           << "Type " << atype << " with no key is ignored" << endm;
	  status = Standard_True;
	}
      else
	{
	  Handle(WOKernel_UnitTypeDescr) thetype = new WOKernel_UnitTypeDescr(akey->Value(1), atype);
	  
	  mytypes.Append(thetype);
	}

      i++;
      atype = types->Token(" \t", i);
    }
  return status;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetTypeDescr
//purpose  : 
//=======================================================================
const Handle(WOKernel_UnitTypeDescr)& WOKernel_UnitTypeBase::GetTypeDescr(const Standard_Character akey) const
{
  static Handle(WOKernel_UnitTypeDescr) NULLRESULT;

  for(Standard_Integer i=1; i<=mytypes.Length(); i++)
    {
      const Handle(WOKernel_UnitTypeDescr)& adescr = mytypes.Value(i);
      if ( adescr->Key() == akey ) return adescr;
    }
  return NULLRESULT;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetTypeDescr
//purpose  : 
//=======================================================================
const Handle(WOKernel_UnitTypeDescr)& WOKernel_UnitTypeBase::GetTypeDescr(const Handle(TCollection_HAsciiString)& atype) const
{
  static Handle(WOKernel_UnitTypeDescr) NULLRESULT;

  for(Standard_Integer i=1; i<=mytypes.Length(); i++)
    {
      const Handle(WOKernel_UnitTypeDescr)& adescr = mytypes.Value(i);
      if ( adescr->Type()->IsSameString(atype) ) return adescr;
    }
  return NULLRESULT;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Length
//purpose  : 
//=======================================================================
Standard_Integer WOKernel_UnitTypeBase::Length() const 
{
  return mytypes.Length();
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : Value
//purpose  : 
//=======================================================================
const Handle(WOKernel_UnitTypeDescr)& WOKernel_UnitTypeBase::Value(const Standard_Integer anidx) const
{
  return mytypes.Value(anidx);
}
