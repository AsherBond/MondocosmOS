// File:	WOKOrbix_IDLTranslator.cxx
// Created:	Mon Aug 25 10:53:48 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_MSEntity.hxx>


#include <WOKOrbix_IDLTranslator.ixx>


//=======================================================================
//function : WOKOrbix_IDLTranslator
//purpose  : 
//=======================================================================
WOKOrbix_IDLTranslator::WOKOrbix_IDLTranslator(const Handle(TCollection_HAsciiString)& aname,const WOKUtils_Param& params)
  : WOKBuilder_MSTool(aname, params)
{
  mytranslator = NULL;
}

//=======================================================================
//function : Load
//purpose  : 
//=======================================================================
void WOKOrbix_IDLTranslator::Load() 
{
  Handle(TCollection_HAsciiString) astr;

  if(Shared().IsNull())
    {
      astr = EvalToolParameter("SHARED");

      if(astr.IsNull())
	{
	  ErrorMsg() << "WOKOrbix_IDLTranslator::Load"
		   << "Invalid SHARED parameter for tool: " << Name() << endm;
	  return;
	}
      SetShared(astr);
    }

  Handle(WOKUtils_Path) libpath = new WOKUtils_Path(Shared());

  if(!libpath->Exists())
    {
      libpath = Params().SearchFile(Shared());
      
      if(libpath.IsNull())
	{
	  ErrorMsg() << "WOKOrbix_IDLTranslator::Load"
		   << "Could not find file : " << Shared() << endm;
	  return;
	}
    }

  Handle(TCollection_HAsciiString) name  = EvalToolParameter("NAME");
  
  if(name.IsNull())
    {
      ErrorMsg() << "WOKOrbix_IDLTranslator::Load" << "Could not eval NAME for IDLTranslator" << endm;
      return;
    }

  WOKBuilder_ToolInProcess::Load(libpath, name);

  mytranslator = (WOKBuilder_MSTranslatorPtr) (Function());

}

//=======================================================================
//function : Translate
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKOrbix_IDLTranslator::Translate(const Handle(WOKOrbix_IDLFile)& afile,
							 Handle(TColStd_HSequenceOfHAsciiString)& globlist,
							 Handle(TColStd_HSequenceOfHAsciiString)& inctypes,
							 Handle(TColStd_HSequenceOfHAsciiString)& insttypes,
							 Handle(TColStd_HSequenceOfHAsciiString)& gentypes) 
{
  if(mytranslator == NULL)
    {
      ErrorMsg() << "WOKOrbix_IDLTranslator::Translate" << "Null Translator : Cannot Perform" << endm;
      return WOKBuilder_Failed;
    }
  
  globlist  = new TColStd_HSequenceOfHAsciiString;
  inctypes  = new TColStd_HSequenceOfHAsciiString;
  insttypes = new TColStd_HSequenceOfHAsciiString;
  gentypes  = new TColStd_HSequenceOfHAsciiString;

  if((*mytranslator)(MSchema()->MetaSchema(),afile->Path()->Name(),globlist,inctypes,insttypes,gentypes)) 
    {
      ErrorMsg() << "WOKOrbix_IDLTranslator::Translate" << "Errors occured" << endm;
      return WOKBuilder_Failed;
    }
  return WOKBuilder_Success;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKOrbix_IDLTranslator::Execute(const Handle(WOKOrbix_IDLFile)& afile) 
{
  Handle(TColStd_HSequenceOfHAsciiString) globlist, inctypes, insttypes, gentypes;

  WOKBuilder_BuildStatus status = Translate(afile, globlist, inctypes, insttypes, gentypes);

  switch(status)
    {
    case WOKBuilder_Success: 
      {
	Handle(WOKBuilder_HSequenceOfEntity) production = new WOKBuilder_HSequenceOfEntity;

	for(Standard_Integer i=1; i<= globlist->Length(); i++)
	  {
	    Handle(WOKBuilder_MSEntity) anent = new WOKBuilder_MSEntity(globlist->Value(i));
	    production->Append(anent);
	  }
	SetProduction(production);
      }
      break;
    default:
      break;
    }
  return status;
}

