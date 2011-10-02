// File:	ms.cxx
// Created:	Thu Oct  5 20:15:39 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <tcl.h>

#include <OSD.hxx>

#include <WOKTclTools_Interpretor.hxx>
#include <WOKTclTools_Package.hxx>

#include <MSAPI_MetaSchema.hxx>
#include <MSAPI_Package.hxx>
#include <MSAPI_Schema.hxx>
#include <MSAPI_Class.hxx>
#include <MSAPI_StdClass.hxx>
#include <MSAPI_GenClass.hxx>
#include <MSAPI_InstClass.hxx>
#include <MSAPI_Method.hxx>
#include <MSAPI_ExternMet.hxx>
#include <MSAPI_MemberMet.hxx>

#ifdef WNT
# ifdef _DEBUG
extern "C" void _debug_break ( char* );
# endif  // _DEBUG
# define MS_EXPORT __declspec( dllexport )
#else
# define MS_EXPORT
#endif  // WNT

extern "C" MS_EXPORT int Ms_Init(WOKTclTools_PInterp);

int Ms_Init(WOKTclTools_PInterp interp)
{
  
  OSD::SetSignal();                  //==== Armed the signals. =============
 
  Handle(WOKTclTools_Interpretor)& CurrentInterp = WOKTclTools_Interpretor::Current();

  if(WOKTclTools_Interpretor::Current().IsNull())
    {
      CurrentInterp = new WOKTclTools_Interpretor;
      CurrentInterp->Set(interp);
    }
  else
    {
      if(WOKTclTools_Interpretor::Current()->Interp() != interp)
	{
	   CurrentInterp = new WOKTclTools_Interpretor;
	   CurrentInterp->Set(interp);
	}
    }

  WOKTclTools_Package tcl(CurrentInterp, "Tcl", TCL_VERSION );

  tcl.Require();
			 
  // MetaSchema Commands

  CurrentInterp->Add("mstranslate", "CDL Translator",            MSAPI_MetaSchema::Translate,    "MS COMMAND");
  CurrentInterp->Add("mscheck",     "Checks from MS",            MSAPI_MetaSchema::Check,        "MS COMMAND");
  CurrentInterp->Add("msextract",   "Extracts from MS",          MSAPI_MetaSchema::Extract,      "MS COMMAND");
  CurrentInterp->Add("msinfo",      "Information about MS",      MSAPI_MetaSchema::Info,         "MS COMMAND");
  CurrentInterp->Add("msrm",        "Remove type or entity",     MSAPI_MetaSchema::Remove,       "MS COMMAND");
  CurrentInterp->Add("msclear",     "Clear meta schema",         MSAPI_MetaSchema::Clear,        "MS COMMAND");

  CurrentInterp->Add("mspkinfo",    "MSPackage Information",     MSAPI_Package::Info,            "MS COMMAND");

  CurrentInterp->Add("msschinfo",   "MS Schema Information",     MSAPI_Schema::Info,             "MS COMMAND");

  CurrentInterp->Add("msclinfo",    "Class Information",         MSAPI_Class::Info,              "MS COMMAND");
  CurrentInterp->Add("msstdinfo",   "StdClass Information",      MSAPI_StdClass::Info,           "MS COMMAND");
  CurrentInterp->Add("msgeninfo",   "GenClass Information",      MSAPI_GenClass::Info,           "MS COMMAND");
  CurrentInterp->Add("msinstinfo",  "InstClass Information",     MSAPI_InstClass::Info,          "MS COMMAND");

  
  CurrentInterp->Add("msmthinfo",   "Method Information",        MSAPI_Method::Info,             "MS COMMAND");
  CurrentInterp->Add("msxmthinfo",  "Extern Method Information", MSAPI_ExternMet::Info,          "MS COMMAND");
  CurrentInterp->Add("msmmthinfo",  "Member Method Information", MSAPI_MemberMet::Info,          "MS COMMAND");

  WOKTclTools_Package ms(CurrentInterp, "Ms", "2.0");
  
  ms.Provide();

  return TCL_OK;
}
