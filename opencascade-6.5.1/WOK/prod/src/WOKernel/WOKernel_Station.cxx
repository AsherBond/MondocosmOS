// File:	WOKernel_Station.cxx
// Created:	Fri Jul 28 17:28:32 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKernel_Station.ixx>

#include <WOKTools_Messages.hxx>

//=======================================================================
//function : GetID
//purpose  : Gives the ID of a station name
//=======================================================================
WOKernel_StationID WOKernel_Station::GetID(const Handle(TCollection_HAsciiString)& astring)
{
  if( !strcmp( astring->ToCString(), "sun" ) )  return WOKernel_SUN;
  if( !strcmp( astring->ToCString(), "ao1" ) )  return WOKernel_DECOSF;
  if( !strcmp( astring->ToCString(), "sil" ) )  return WOKernel_SGI;
  if( !strcmp( astring->ToCString(), "hp"  ) )  return WOKernel_HP;
  if( !strcmp( astring->ToCString(), "wnt" ) )  return WOKernel_WNT;
  if( !strcmp( astring->ToCString(), "mac" ) )  return WOKernel_MAC;
  if( !strcmp( astring->ToCString(), "lin" ) )  return WOKernel_LIN;
  if( !strcmp( astring->ToCString(), "aix" ) )  return WOKernel_AIX;
  if( !strcmp( astring->ToCString(), "bsd" ) )  return WOKernel_BSD;

  ErrorMsg() << "WOKernel_Station::GetID" << "Station " << astring << " is unknown to WOK" << endm;
  Standard_ProgramError::Raise("WOKernel_Station::GetID");
  return WOKernel_UnknownStation;
}

//=======================================================================
//function : IsNameKnown
//purpose  : Gives the ID of a station name
//=======================================================================
Standard_Boolean WOKernel_Station::IsNameKnown(const Handle(TCollection_HAsciiString)& astring)
{
  if( !strcmp( astring->ToCString(), "sun" ) )  return Standard_True;
  if( !strcmp( astring->ToCString(), "ao1" ) )  return Standard_True;
  if( !strcmp( astring->ToCString(), "sil" ) )  return Standard_True;
  if( !strcmp( astring->ToCString(), "hp"  ) )  return Standard_True;
  if( !strcmp( astring->ToCString(), "wnt" ) )  return Standard_True;
  if( !strcmp( astring->ToCString(), "mac" ) )  return Standard_True;
  if( !strcmp( astring->ToCString(), "lin" ) )  return Standard_True;
  if( !strcmp( astring->ToCString(), "aix" ) )  return Standard_True;
  if( !strcmp( astring->ToCString(), "bsd" ) )  return Standard_True;

  return Standard_False;
}

//=======================================================================
//function : GetName
//purpose  : Gives the name of a station id
//=======================================================================
 Handle(TCollection_HAsciiString) WOKernel_Station::GetName(const WOKernel_StationID anid)
{
  static Handle(TCollection_HAsciiString) SSUN     = new TCollection_HAsciiString("sun");
  static Handle(TCollection_HAsciiString) SDECOSF  = new TCollection_HAsciiString("ao1");
  static Handle(TCollection_HAsciiString) SSGI     = new TCollection_HAsciiString("sil");
  static Handle(TCollection_HAsciiString) SHP      = new TCollection_HAsciiString("hp");
  static Handle(TCollection_HAsciiString) SWNT     = new TCollection_HAsciiString("wnt");
  static Handle(TCollection_HAsciiString) SMAC     = new TCollection_HAsciiString("mac");
  static Handle(TCollection_HAsciiString) SLIN     = new TCollection_HAsciiString("lin");
  static Handle(TCollection_HAsciiString) SAIX     = new TCollection_HAsciiString("aix");
  static Handle(TCollection_HAsciiString) SBSD     = new TCollection_HAsciiString("bsd");
  static Handle(TCollection_HAsciiString) SUNKNOWN = new TCollection_HAsciiString("UnknownStation");

  switch(anid)
    {
    case WOKernel_SUN: 
      return SSUN;
    case WOKernel_DECOSF: 
      return SDECOSF;
    case WOKernel_SGI: 
      return SSGI;
    case WOKernel_HP: 
      return SHP;
    case WOKernel_WNT: 
      return SWNT;
    case WOKernel_MAC: 
      return SMAC;
    case WOKernel_LIN:
      return SLIN;
    case WOKernel_AIX: 
      return SAIX;
    case WOKernel_BSD:
      return SBSD;
      break;
    default:
      break;
    }
  return SUNKNOWN;
}

//=======================================================================
//function : GetHSeqOfStation
//purpose  : 
//=======================================================================
Handle(WOKernel_HSequenceOfStationID) WOKernel_Station::GetHSeqOfStation(const Handle(TCollection_HAsciiString)& astr)
{
  Standard_Integer i = 2;
  Handle(TCollection_HAsciiString) thestr;
  Handle(WOKernel_HSequenceOfStationID) aseq = new WOKernel_HSequenceOfStationID;

  if(astr.IsNull())   return aseq;
  if(astr->IsEmpty()) return aseq;
  
  thestr = astr->Token();

  while(thestr->IsEmpty() == Standard_False)
    {
      if(IsNameKnown(thestr))
	{
	  aseq->Append(WOKernel_Station::GetID(thestr));
	}
      thestr = astr->Token(" \t", i++);
    }
  return aseq;
}
