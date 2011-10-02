// File:	WOKBuilder_MSTranslatorIterator.cxx
// Created:	Mon Sep 18 18:46:08 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <Standard_ProgramError.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <MS.hxx>
#include <MS_MetaSchema.hxx>
#include <MS_StdClass.hxx>
#include <MS_InstClass.hxx>
#include <MS_GenClass.hxx>
#include <MS_Package.hxx>
#include <MS_Interface.hxx>
#include <MS_MemberMet.hxx>
#include <MS_Param.hxx>
#include <MS_HSequenceOfParam.hxx>

#include <WOKBuilder_Specification.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSTool.hxx>

#include <WOKBuilder_MSTranslatorIterator.ixx>


//=======================================================================
//function : WOKBuilder_MSTranslatorIterator
//purpose  : 
//=======================================================================
WOKBuilder_MSTranslatorIterator::WOKBuilder_MSTranslatorIterator(const Handle(WOKBuilder_MSchema)& ams,
								 const WOKBuilder_MSActionID& anaction)  
: myms(ams)
{  
  AddInStack(new TCollection_HAsciiString("Standard"), WOKBuilder_DirectUses);
  AddInStack(anaction.Name(), anaction.Type());
  myms->RemoveAutoTypes();
}


//=======================================================================
//function : WOKBuilder_MSTranslatorIterator
//purpose  : 
//=======================================================================
WOKBuilder_MSTranslatorIterator::WOKBuilder_MSTranslatorIterator(const Handle(WOKBuilder_MSchema)& ams)  
: myms(ams)
{  
  AddInStack(new TCollection_HAsciiString("Standard"), WOKBuilder_DirectUses);
  myms->RemoveAutoTypes();
}


//=======================================================================
//function : Reset
//purpose  : 
//=======================================================================
void WOKBuilder_MSTranslatorIterator::Reset()
{

  myglobal.Clear();
  myinsttypes.Clear();
  mygentypes.Clear();
  mygetypes.Clear();
  mytypes.Clear();

  mystack.Clear();

  mytarget.Nullify();
  AddInStack(new TCollection_HAsciiString("Standard"), WOKBuilder_DirectUses);
  myms->RemoveAutoTypes();
}

//=======================================================================
//function : Value
//purpose  : 
//=======================================================================
const Handle(WOKBuilder_MSAction)& WOKBuilder_MSTranslatorIterator::Value()
{
  if(!myglobal.IsEmpty())    {
    mycurrent = myglobal.Front();    
    return mycurrent;
  }
  if(!mygetypes.IsEmpty())   {
    mycurrent = mygetypes.Front();   
    return mycurrent;
  }
  if(!mygentypes.IsEmpty())  {
    mycurrent = mygentypes.Front();  
    return mycurrent;
  }
  if(!myinsttypes.IsEmpty()) {
    mycurrent = myinsttypes.Front(); 
    return mycurrent;
  }
  mycurrent = mytypes.Front(); 
  return mycurrent;
}

//=======================================================================
//function : GetMSAction
//purpose  : 
//=======================================================================
const Handle(WOKBuilder_MSAction)& WOKBuilder_MSTranslatorIterator::GetMSAction(const Handle(TCollection_HAsciiString)& aname, 
										const WOKBuilder_MSActionType action)
{
  WOKBuilder_MSActionID anid(aname, action);

  static Handle(WOKBuilder_MSAction) theaction;

  if(!mystack.IsBound(anid))
    {
      theaction = myms->GetAction(anid);
    }
  else
    {
      return mystack.Find(anid);
    }
  return theaction;
}

//=======================================================================
//function : EquivActionStacked
//purpose  : 
//=======================================================================
void WOKBuilder_MSTranslatorIterator::EquivActionStacked(const Handle(TCollection_HAsciiString)& aname, 
							 const WOKBuilder_MSActionType action)
{
  WOKBuilder_MSActionID anid(aname, action);

  if(!mystack.IsBound(anid))
    {
      Handle(WOKBuilder_MSAction) theaction;

      theaction = GetMSAction(aname, action);
      mystack.Bind(anid, theaction);
    }
  return;
}

//=======================================================================
//function : AddInStack
//purpose  : 
//=======================================================================
void WOKBuilder_MSTranslatorIterator::AddInStack(const Handle(TCollection_HAsciiString)& aname, 
						 const WOKBuilder_MSActionType action)
{
  WOKBuilder_MSActionID anid(aname, action);
  Handle(WOKBuilder_MSAction) anact;
  Standard_Boolean added = Standard_False;

  if(!mystack.IsBound(anid))
    {
      anact = myms->GetAction(anid);

      switch(action)
	{
	case WOKBuilder_Package:
	case WOKBuilder_Interface:
	case WOKBuilder_Client:
	case WOKBuilder_Schema:
	case WOKBuilder_Engine:
	case WOKBuilder_Executable:
	case WOKBuilder_Component:
	case WOKBuilder_DirectUses:
	case WOKBuilder_SchUses:
	case WOKBuilder_Uses:
	case WOKBuilder_GlobEnt:
	  myglobal.Push(anact);
	  break;
	case WOKBuilder_Instantiate:
	case WOKBuilder_InstToStd:
	  myinsttypes.Push(anact);
	  break;
	case WOKBuilder_InterfaceTypes:
	case WOKBuilder_SchemaTypes:
	case WOKBuilder_PackageMethods:
	  mygetypes.Push(anact);
	  break;
	case WOKBuilder_GenType:
	  mygentypes.Push(anact);
	  break;
	case WOKBuilder_CompleteType:
	case WOKBuilder_SchemaType:
	case WOKBuilder_Inherits:
	case WOKBuilder_TypeUses:
	  mytypes.Push(anact);
	  break;
	default:
	  Standard_ProgramError::Raise("WOKBuilder_MSTranslatorIterator::AddInStack : Unknown action type");
	  break;
	}
      

      mystack.Bind(anid, anact);

      Handle(TCollection_HAsciiString) astr = anact->Entity()->Name();

      switch(action)
	{
	case WOKBuilder_Package:
	case WOKBuilder_Schema:
	case WOKBuilder_Interface:
	case WOKBuilder_Client:
	case WOKBuilder_Executable:
	case WOKBuilder_Component:
	case WOKBuilder_Engine:
	  EquivActionStacked(astr, WOKBuilder_DirectUses);
	  EquivActionStacked(astr, WOKBuilder_Uses);
	  EquivActionStacked(astr, WOKBuilder_GlobEnt);
	  break;
	case WOKBuilder_DirectUses:
	  EquivActionStacked(astr, WOKBuilder_Uses);
	  EquivActionStacked(astr, WOKBuilder_GlobEnt);
	  break;
	case WOKBuilder_SchUses:
	  EquivActionStacked(astr, WOKBuilder_Uses);
	  EquivActionStacked(astr, WOKBuilder_GlobEnt);
	  break;
	case WOKBuilder_Uses:
	  EquivActionStacked(astr, WOKBuilder_GlobEnt);
	  break;
	case WOKBuilder_GlobEnt:
	case WOKBuilder_Instantiate:
	case WOKBuilder_SchemaTypes:
	case WOKBuilder_InterfaceTypes:
	case WOKBuilder_PackageMethods:
	case WOKBuilder_InstToStd:
	case WOKBuilder_GenType:
	  break;
	case WOKBuilder_SchemaType:
	  EquivActionStacked(astr, WOKBuilder_CompleteType);
	  EquivActionStacked(astr, WOKBuilder_Inherits);
	  EquivActionStacked(astr, WOKBuilder_TypeUses);
	  break;	  
	case WOKBuilder_CompleteType:
	  EquivActionStacked(astr, WOKBuilder_Inherits);
	  EquivActionStacked(astr, WOKBuilder_TypeUses);
	  break;
	case WOKBuilder_Inherits:
	  break;
	case WOKBuilder_TypeUses:
	  EquivActionStacked(astr, WOKBuilder_Inherits);
	  break;
	default:
	  Standard_ProgramError::Raise("WOKBuilder_MSTranslatorIterator::AddInStack : Unknown action type");
	  break;
	}
      added = Standard_True;
    }

  WOK_TRACE {
    if(VerboseMsg()("WOK_TRANSIT").IsSet())
      {
	Standard_CString actionstr;
	switch(action)
	  {
	  case WOKBuilder_Package:
	    actionstr = "Package";
	    break;
	  case WOKBuilder_Schema:
	    actionstr = "Schema";
	    break;
	  case WOKBuilder_Interface:
	    actionstr = "Interface";
	    break;
	  case WOKBuilder_Client:
	    actionstr = "Client";
	    break;
	  case WOKBuilder_Engine:
	    actionstr = "Engine";
	    break;
	  case WOKBuilder_Executable:
	    actionstr = "Executable";
	    break;
	  case WOKBuilder_Component:
	    actionstr = "Component";
	    break;
	  case WOKBuilder_DirectUses:
	    actionstr = "DirectUses";
	    break;
	  case WOKBuilder_SchUses:
	    actionstr = "SchUses";
	    break;
	  case WOKBuilder_Uses:
	    actionstr = "Uses";
	    break;
	  case WOKBuilder_GlobEnt:
	    actionstr = "GlobalEntity";
	    break;
	  case WOKBuilder_Instantiate:
	    actionstr = "Instantiate";
	    break;
	  case WOKBuilder_InterfaceTypes:
	    actionstr = "Inter Types";
	    break;
	  case WOKBuilder_SchemaTypes:
	    actionstr = "Schema Types";
	    break;
	  case WOKBuilder_PackageMethods:
	    actionstr = "Pk Methods";
	    break;
	  case WOKBuilder_GenType:
	    actionstr = "GenType";
	    break;
	  case WOKBuilder_InstToStd:
	    actionstr = "InstToStd";
	    break;
	  case WOKBuilder_SchemaType:
	    actionstr = "SchemaType";
	    break;
	  case WOKBuilder_CompleteType:
	    actionstr = "CompleteType";
	    break;
	  case WOKBuilder_Inherits:
	    actionstr = "Inherits";
	    break;
	  case WOKBuilder_TypeUses:
	    actionstr = "TypeUses";
	    break;
	  default:
	    actionstr = "unknown";
	    Standard_ProgramError::Raise("WOKBuilder_MSTranslatorIterator::AddInStack : Unknown action type");
	    break;
	    
	  }
	if(!added)
	  {
	    VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::AddInStack" 
				      << "Adding : " << aname << " as " << actionstr << " not added : Already in stack" << endm;
	  }
	else
	  {
	    VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::AddInStack" 
				      << "Adding : " << aname << " as " << actionstr  << endm;
	  }
      }
  }
}

//=======================================================================
//function : IsInStack
//purpose  : 
//=======================================================================
Standard_Boolean WOKBuilder_MSTranslatorIterator::IsInStack(const Handle(TCollection_HAsciiString)& aname, 
							    const WOKBuilder_MSActionType action) const 
{
  WOKBuilder_MSActionID anid(aname, action);

  return mystack.IsBound(anid);
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslatorIterator::Execute(const Handle(WOKBuilder_MSTranslator)& atranslator, 
								const Handle(WOKBuilder_MSAction)& anaction,
								const Handle(WOKBuilder_Specification)& afile)
{
  return atranslator->Execute(anaction, afile, *this);
}

//=======================================================================
//function : Next
//purpose  : 
//=======================================================================
void WOKBuilder_MSTranslatorIterator::Next() 
{
  switch(mycurrent->Type())
    {
    case WOKBuilder_Package:
    case WOKBuilder_Schema:
    case WOKBuilder_Interface:
    case WOKBuilder_Client:
    case WOKBuilder_Executable:
    case WOKBuilder_Component:
    case WOKBuilder_Engine:
    case WOKBuilder_DirectUses:
    case WOKBuilder_SchUses:
    case WOKBuilder_Uses:
    case WOKBuilder_GlobEnt:
      myglobal.Pop();
      break;
    case WOKBuilder_InterfaceTypes:
    case WOKBuilder_SchemaTypes:
    case WOKBuilder_PackageMethods:
      mygetypes.Pop();
      break;
    case WOKBuilder_Instantiate:
    case WOKBuilder_InstToStd:
      myinsttypes.Pop();
      break;
    case WOKBuilder_GenType:
      mygentypes.Pop();
      break;
    case WOKBuilder_SchemaType:
    case WOKBuilder_CompleteType:
    case WOKBuilder_Inherits:
    case WOKBuilder_TypeUses:
      mytypes.Pop();
      break;
    default:
      Standard_ProgramError::Raise("WOKBuilder_MSTranslatorIterator::Next : Unknown action type");
      break;
    }

  return;
}


//=======================================================================
//function : More
//purpose  : 
//=======================================================================
Standard_Boolean WOKBuilder_MSTranslatorIterator::More() const
{
  if(!myglobal.IsEmpty())    return Standard_True;
  if(!mygetypes.IsEmpty())   return Standard_True;
  if(!mygentypes.IsEmpty())  return Standard_True;
  if(!myinsttypes.IsEmpty()) return Standard_True;
  if(!mytypes.IsEmpty())     return Standard_True;
  return Standard_False;
}




