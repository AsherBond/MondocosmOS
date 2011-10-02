// File:	WOKBuilder_MSTool.cxx
// Created:	Mon Sep 11 13:29:29 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKBuilder_MSTool.ixx>


//=======================================================================
//function : WOKBuilder_MSTool
//purpose  : 
//=======================================================================
WOKBuilder_MSTool::WOKBuilder_MSTool(const Handle(TCollection_HAsciiString)& aname,
				     const WOKUtils_Param& params)
  : WOKBuilder_ToolInProcess(aname,params)
{
}

//=======================================================================
//function : MetaSchema
//purpose  : 
//=======================================================================
Handle(WOKBuilder_MSchema) WOKBuilder_MSTool::GetMSchema() 
{
  static Handle(WOKBuilder_MSchema) thems = new WOKBuilder_MSchema;

  return thems;
}

//=======================================================================
//function : SetMSchema
//purpose  : 
//=======================================================================
void  WOKBuilder_MSTool::SetMSchema(const Handle(WOKBuilder_MSchema)& ams) 
{
  myschema = ams;
}



//=======================================================================
//function : MetaSchema
//purpose  : 
//=======================================================================
Handle(WOKBuilder_MSchema) WOKBuilder_MSTool::MSchema() const
{
  return myschema;
}


//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTool::Execute() 
{
  return WOKBuilder_Success;
}
