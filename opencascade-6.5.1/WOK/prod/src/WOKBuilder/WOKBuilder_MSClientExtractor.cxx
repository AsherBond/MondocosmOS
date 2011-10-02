// File:	WOKBuilder_MSClientExtractor.cxx
// Created:	Tue Mar 19 20:45:22 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKTools_Messages.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_Client.hxx>
#include <MS_Interface.hxx>

#include <WOKBuilder_MSActionID.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSchema.hxx>

#include <WOKBuilder_MSClientExtractor.ixx>



extern "C" {

typedef void (*WOKBuilder_MSClientExtractorInitPtr)(const Handle(MS_MetaSchema)& ,
						    const Handle(TCollection_HAsciiString)& ,
						    const Handle(MS_HSequenceOfExternMet)& ,
						    const Handle(MS_HSequenceOfMemberMet)& );
}


//=======================================================================
//function : WOKBuilder_MSClientExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSClientExtractor::WOKBuilder_MSClientExtractor(const Handle(TCollection_HAsciiString)& ashared, 
							   const Handle(TColStd_HSequenceOfHAsciiString)& searchlist)
  : WOKBuilder_MSHeaderExtractor(ashared, searchlist)
{
}

//=======================================================================
//function : WOKBuilder_MSClientExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSClientExtractor::WOKBuilder_MSClientExtractor(const WOKUtils_Param& params)
   : WOKBuilder_MSHeaderExtractor(new TCollection_HAsciiString("CPPCLIENT"),params)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKBuilder_MSClientExtractor::Init(const Handle(TCollection_HAsciiString)& aname)
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
	  ((WOKBuilder_MSClientExtractorInitPtr) myinitfunc) (ams->MetaSchema(),aname, myxmeth,mymmeth);
	}
    }

}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : MemberMethods
//purpose  : 
//=======================================================================
Handle(MS_HSequenceOfMemberMet) WOKBuilder_MSClientExtractor::MemberMethods() const
{
  return mymmeth;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExternMethods
//purpose  : 
//=======================================================================
Handle(MS_HSequenceOfExternMet) WOKBuilder_MSClientExtractor::ExternMethods() const
{
  return myxmeth;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : CompleteTypes
//purpose  : 
//=======================================================================
const WOKTools_MapOfHAsciiString&  WOKBuilder_MSClientExtractor::CompleteTypes() const
{
  return mycompl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CompleteTypes
//purpose  : 
//=======================================================================
const WOKTools_MapOfHAsciiString&  WOKBuilder_MSClientExtractor::IncompleteTypes() const
{
  return myicompl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CompleteTypes
//purpose  : 
//=======================================================================
const WOKTools_MapOfHAsciiString&  WOKBuilder_MSClientExtractor::SemiCompleteTypes() const
{
  return myscompl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExtractorID
//purpose  : 
//=======================================================================
WOKBuilder_MSActionType WOKBuilder_MSClientExtractor::ExtractorID() const
{
  return WOKBuilder_ClientExtract;
}

//=======================================================================
//function : ExtractionStatus
//purpose  : 
//=======================================================================
WOKBuilder_MSActionStatus WOKBuilder_MSClientExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& anaction)
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

      WOKUtils_TimeStat depdate = GetTypeMDate(astr);

      WOK_TRACE {
	VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSClientExtractor::ExtractionStatus" 
				  << "Comparing extraction   date : " 
				  << (Standard_Integer) anaction->Date() << " of " << anaction->Entity()->Name() << endm;
	
	VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSClientExtractor::ExtractionStatus" 
				  << "with      modification date : " 
				  << (Standard_Integer) depdate << " of " << astr << endm;
      }
      
      if( GetTypeMDate(astr) > anaction->Date())
	{
	  WOK_TRACE {
	    VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSClientExtractor::ExtractionStatus" 
				      << anaction->Entity()->Name() << " is out of date compared to " << astr << endm;
	  }
	  return WOKBuilder_OutOfDate;
	}
      else
	{
	  WOK_TRACE {
	    VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSClientExtractor::ExtractionStatus" 
				      << anaction->Entity()->Name() << " is up to date compared to : " << astr << endm;
	  }
	}
    }
  
  WOK_TRACE {
    VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSClientExtractor::ExtractionStatus" 
			      << anaction->Entity()->Name() << " is up to date" << endm;
  }

  return WOKBuilder_UpToDate;
}
