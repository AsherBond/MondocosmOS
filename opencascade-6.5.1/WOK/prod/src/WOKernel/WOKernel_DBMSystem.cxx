// File:	WOKernel_DBMSystem.cxx
// Created:	Fri Jul 28 17:48:03 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKernel_DBMSystem.ixx>

#include <WOKTools_Messages.hxx>

#include <TCollection_AsciiString.hxx>

//=======================================================================
//function : GetID
//purpose  : 
//=======================================================================
WOKernel_DBMSID WOKernel_DBMSystem::GetID(const Handle(TCollection_HAsciiString)& astring)
{
  if( !strcmp( astring->ToCString(), "DFLT"  ) )  return WOKernel_DFLT;
  if( !strcmp( astring->ToCString(), "OBJY"  ) )  return WOKernel_OBJY;
  if( !strcmp( astring->ToCString(), "OBJS"  ) )  return WOKernel_OBJS;

  ErrorMsg() << "WOKernel_DBMSystem::GetID" << "DBMSystem " << astring << " is unknown for WOK" << endm;
  Standard_ProgramError::Raise("WOKernel_DBMSystem::GetID");
  return WOKernel_UnknownDBMS;
}

//=======================================================================
//function : IsNameKnown
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_DBMSystem::IsNameKnown(const Handle(TCollection_HAsciiString)& astring)
{
  if( !strcmp( astring->ToCString(), "DFLT"  ) )  return Standard_True;
  if( !strcmp( astring->ToCString(), "OBJY"  ) )  return Standard_True;
  if( !strcmp( astring->ToCString(), "OBJS"  ) )  return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : GetName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_DBMSystem::GetName(const WOKernel_DBMSID anid)
{
  static Handle(TCollection_HAsciiString) SDFLT    = new TCollection_HAsciiString("DFLT");
  static Handle(TCollection_HAsciiString) SOBJY    = new TCollection_HAsciiString("OBJY");
  static Handle(TCollection_HAsciiString) SOBJS    = new TCollection_HAsciiString("OBJS");
  static Handle(TCollection_HAsciiString) SUNKNOWN = new TCollection_HAsciiString("UnknownDBMS");

  switch(anid)
    {
    case WOKernel_DFLT:
      return SDFLT;
    case WOKernel_OBJY:
      return SOBJY;
    case WOKernel_OBJS:
      return SOBJS;
    default:
      break;
    }
  return SUNKNOWN;
}


//=======================================================================
//function : GetHSeqOfDBMS
//purpose  : 
//=======================================================================
Handle(WOKernel_HSequenceOfDBMSID) WOKernel_DBMSystem::GetHSeqOfDBMS(const Handle(TCollection_HAsciiString)& astr)
{
  Standard_Integer i = 2;
  Handle(TCollection_HAsciiString) thestr;
  Handle(WOKernel_HSequenceOfDBMSID) aseq = new WOKernel_HSequenceOfDBMSID;
  
  if(astr.IsNull())   return aseq;
  if(astr->IsEmpty()) return aseq;

  thestr = astr->Token();

  while(thestr->IsEmpty() == Standard_False)
    {
      if(IsNameKnown(thestr))
	{
	  aseq->Append(WOKernel_DBMSystem::GetID(thestr));
	}
      thestr = astr->Token(" \t", i++);
    }
  return aseq;
}
