// Copyright: 	Matra-Datavision 1999
// File:	WOKBuilder_MSJiniExtractor.cxx
// Created:	Mon Mar 22 17:14:48 1999
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKTools_Messages.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_Client.hxx>
#include <MS_Interface.hxx>

#include <WOKBuilder_MSActionID.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSchema.hxx>

#include <WOKBuilder_MSJiniExtractor.ixx>

extern "C" {

typedef void (*WOKBuilder_MSJiniExtractorInitPtr)(const Handle(MS_MetaSchema)& ,
						  const Handle(TCollection_HAsciiString)& ,
						  const Handle(MS_HSequenceOfExternMet)& ,
						  const Handle(MS_HSequenceOfMemberMet)&,
                                                  const Handle(TColStd_HSequenceOfHAsciiString)&
                                                 );
}


//=======================================================================
//function : WOKBuilder_MSJiniExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSJiniExtractor::WOKBuilder_MSJiniExtractor(const Handle(TCollection_HAsciiString)& ashared, 
							   const Handle(TColStd_HSequenceOfHAsciiString)& searchlist)
  : WOKBuilder_MSHeaderExtractor(ashared, searchlist)
{
}

//=======================================================================
//function : WOKBuilder_MSJiniExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSJiniExtractor::WOKBuilder_MSJiniExtractor(const WOKUtils_Param& params)
   : WOKBuilder_MSHeaderExtractor(new TCollection_HAsciiString("CPPJINI"),params)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKBuilder_MSJiniExtractor::Init(const Handle(TCollection_HAsciiString)& aname)
{
    
  myxmeth = new MS_HSequenceOfExternMet;
  mymmeth = new MS_HSequenceOfMemberMet;
  mycompl.Clear();
  myicompl.Clear();
  myscompl.Clear();

  Handle(MS_Client) aclt;
  Handle(WOKBuilder_MSchema) ams = WOKBuilder_MSTool::GetMSchema();
  
  if(ams->MetaSchema()->IsClient(aname))
    {
      aclt = ams->MetaSchema()->GetClient(aname);
      
      aclt->ComputeTypes(myxmeth, mymmeth, mycompl, myicompl, myscompl);

      if(myinitfunc != NULL) 
	{
	  ((WOKBuilder_MSJiniExtractorInitPtr) myinitfunc) (ams->MetaSchema(),aname, myxmeth,mymmeth,aclt->Uses());
	}
    }

}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : MemberMethods
//purpose  : 
//=======================================================================
Handle(MS_HSequenceOfMemberMet) WOKBuilder_MSJiniExtractor::MemberMethods() const
{
  return mymmeth;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExternMethods
//purpose  : 
//=======================================================================
Handle(MS_HSequenceOfExternMet) WOKBuilder_MSJiniExtractor::ExternMethods() const
{
  return myxmeth;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : CompleteTypes
//purpose  : 
//=======================================================================
const WOKTools_MapOfHAsciiString&  WOKBuilder_MSJiniExtractor::CompleteTypes() const
{
  return mycompl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CompleteTypes
//purpose  : 
//=======================================================================
const WOKTools_MapOfHAsciiString&  WOKBuilder_MSJiniExtractor::IncompleteTypes() const
{
  return myicompl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CompleteTypes
//purpose  : 
//=======================================================================
const WOKTools_MapOfHAsciiString&  WOKBuilder_MSJiniExtractor::SemiCompleteTypes() const
{
  return myscompl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExtractorID
//purpose  : 
//=======================================================================
WOKBuilder_MSActionType WOKBuilder_MSJiniExtractor::ExtractorID() const
{
  return WOKBuilder_ClientExtract;
}

//=======================================================================
//function : ExtractionStatus
//purpose  : 
//=======================================================================
WOKBuilder_MSActionStatus WOKBuilder_MSJiniExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& anaction)
{
  Handle(TCollection_HAsciiString) astr;
  Handle(TColStd_HSequenceOfHAsciiString) aList;
  Handle(MS_MetaSchema) ameta = MSchema()->MetaSchema();
  Standard_Integer i;

  WOKBuilder_MSActionID anid(anaction->Entity()->Name(), anaction->Type());

  if(!MSchema()->IsActionDefined(anid)) return WOKBuilder_OutOfDate;

  astr = anaction->Entity()->Name();

  aList = GetTypeDepList(anaction->Entity()->Name());

  for(i=1; i<=aList->Length(); i++)
    {
      astr = aList->Value(i);
#ifdef WOK_VERBOSE
      WOKUtils_TimeStat depdate = GetTypeMDate(astr);

      VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSJiniExtractor::ExtractionStatus" 
                                << "Comparing extraction   date : " 
			        << (Standard_Integer) anaction->Date() << " of " << anaction->Entity()->Name() << endm;
  
      VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSJiniExtractor::ExtractionStatus" 
	                        << "with      modification date : " 
			        << (Standard_Integer) depdate << " of " << astr << endm;
#endif
  
      if( GetTypeMDate(astr) > anaction->Date())
	{
#ifdef WOK_VERBOSE
	  VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSJiniExtractor::ExtractionStatus" 
	                            << anaction->Entity()->Name() << " is out of date compared to " << astr << endm;
#endif
	  return WOKBuilder_OutOfDate;
	}
      else
	{
#ifdef WOK_VERBOSE
	  VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSJiniExtractor::ExtractionStatus" 
	                            << anaction->Entity()->Name() << " is up to date compared to : " << astr << endm;
#endif
	}
    }
  
#ifdef WOK_VERBOSE
  VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSJiniExtractor::ExtractionStatus" 
                            << anaction->Entity()->Name() << " is up to date" << endm;
#endif  

  return WOKBuilder_UpToDate;
}

