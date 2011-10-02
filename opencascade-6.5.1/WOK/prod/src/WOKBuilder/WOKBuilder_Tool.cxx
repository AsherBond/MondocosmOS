// File:	WOKBuilder_Tool.cxx
// Created:	Wed Aug 23 20:10:26 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_Tool.ixx>

//=======================================================================
//function : WOKBuilder_Tool
//purpose  : 
//=======================================================================
 WOKBuilder_Tool::WOKBuilder_Tool(const Handle(TCollection_HAsciiString)& aname,
				  const WOKUtils_Param& params) 
   : myname(aname), myparams(params), myisloaded(Standard_False)
{
}

//=======================================================================
//function : SetOutputDir
//purpose  : 
//=======================================================================
void WOKBuilder_Tool::SetOutputDir(const Handle(WOKUtils_Path)& apath)
{
  outputdir = apath;
}

//=======================================================================
//function : OutputDir
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) WOKBuilder_Tool::OutputDir() const 
{
  return outputdir;
}


//=======================================================================
//function : SetName
//purpose  : 
//=======================================================================
void WOKBuilder_Tool::SetName(const Handle(TCollection_HAsciiString)& aname)
{
  myname = aname;
}

//=======================================================================
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_Tool::Name() const 
{
  return myname;
}

//=======================================================================
//function : Produces
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfEntity) WOKBuilder_Tool::Produces() 
{
  return myproduction;
}

//=======================================================================
//function : SetProduction
//purpose  : 
//=======================================================================
void WOKBuilder_Tool::SetProduction(const Handle(WOKBuilder_HSequenceOfEntity)& alist)
{
  myproduction = alist;
}

//=======================================================================
//function : SetParams
//purpose  : 
//=======================================================================
void WOKBuilder_Tool::SetParams(const WOKUtils_Param& params)
{
  myparams = params;
}

//=======================================================================
//function : EvalToolParameter
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_Tool::EvalToolParameter(const Handle(TCollection_HAsciiString)& aparam) const
{
  Handle(TCollection_HAsciiString) astr;

  if(aparam.IsNull()) return astr;

  astr =  EvalToolParameter(aparam->ToCString());
  return astr;
}


//=======================================================================
//function : EvalToolParameter
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_Tool::EvalToolParameter(const Standard_CString aparam) const
{
  Handle(TCollection_HAsciiString) astr;
  TCollection_AsciiString name;

  name.AssignCat("%");
  name.AssignCat(Name()->ToCString());
  name.AssignCat("_");
  name.AssignCat(aparam);
  
  astr = myparams.Eval(name.ToCString(), Standard_True);

  return astr;
}

//=======================================================================
//function : EvalToolParameter
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_Tool::EvalToolTemplate(const Handle(TCollection_HAsciiString)& aparam) const
{
  Handle(TCollection_HAsciiString) astr;

  if(aparam.IsNull()) return astr;

  astr =  EvalToolTemplate(aparam->ToCString());
  return astr;
}


//=======================================================================
//function : EvalToolParameter
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_Tool::EvalToolTemplate(const Standard_CString aparam) const
{
  Handle(TCollection_HAsciiString) astr;
  TCollection_AsciiString name;

  name.AssignCat(Name()->ToCString());
  name.AssignCat("_");
  name.AssignCat(aparam);
  
  astr = myparams.Eval(name.ToCString(), Standard_True);

  return astr;
}

//=======================================================================
//function : IsLoaded
//purpose  : 
//=======================================================================
Standard_Boolean WOKBuilder_Tool::IsLoaded() const {return myisloaded;}

//=======================================================================
//function : SetLoaded
//purpose  : 
//=======================================================================
void WOKBuilder_Tool::SetLoaded()  { myisloaded = Standard_True;}

//=======================================================================
//function : UnsetLoaded
//purpose  : 
//=======================================================================
void WOKBuilder_Tool::UnsetLoaded()  { myisloaded = Standard_False;}







