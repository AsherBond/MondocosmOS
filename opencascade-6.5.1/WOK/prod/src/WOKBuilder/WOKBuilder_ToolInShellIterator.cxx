// File:	WOKBuilder_ToolInShellIterator.cxx
// Created:	Thu Jul 11 21:44:48 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Extension.hxx>

#include <WOKBuilder_HSequenceOfExtension.hxx>
#include <WOKBuilder_ToolInShell.hxx>
#include <WOKBuilder_Command.hxx>
#include <WOKBuilder_HSequenceOfToolInShell.hxx>
#include <WOKBuilder_Entity.hxx>

#include <WOKBuilder_ToolInShellIterator.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKBuilder_ToolInShellIterator
//purpose  : 
//=======================================================================
WOKBuilder_ToolInShellIterator::WOKBuilder_ToolInShellIterator(const Handle(TCollection_HAsciiString)& agroup,
							       const WOKUtils_Param& params)
  : mygroup(agroup), myparams(params)
{
  
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKBuilder_ToolInShellIterator
//purpose  : 
//=======================================================================
WOKBuilder_ToolInShellIterator::WOKBuilder_ToolInShellIterator(const Handle(WOKBuilder_HSequenceOfToolInShell)& atoolseq)
  :  mytools(atoolseq)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKBuilder_ToolInShellIterator
//purpose  : 
//=======================================================================
WOKBuilder_ToolInShellIterator::WOKBuilder_ToolInShellIterator(const Handle(TCollection_HAsciiString)& agroup,
							       const Handle(WOKUtils_Shell)& ashell,
							       const Handle(WOKUtils_Path)& apath,
							       const WOKUtils_Param& params)
  : mygroup(agroup), myparams(params), myshell(ashell), myoutdir(apath)
{
  
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInShellIterator::Init(const Handle(WOKUtils_Shell)& ashell,
					  const Handle(WOKUtils_Path)& apath)
  
{
   myshell = ashell; myoutdir = apath;

  Handle(WOKBuilder_HSequenceOfToolInShell) tools = Tools();

  if(!tools.IsNull())
    {
      for(Standard_Integer i=1; i<=tools->Length(); i++)
	{
	  Handle(WOKBuilder_ToolInShell) atool = tools->Value(i);

	  atool->SetShell(ashell);
	  atool->SetOutputDir(apath);
	}
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetTool
//purpose  : 
//=======================================================================
Handle(WOKBuilder_ToolInShell)  WOKBuilder_ToolInShellIterator::GetTool(const Handle(TCollection_HAsciiString)& aname, 
									      const WOKUtils_Param& params) const
{
  return new WOKBuilder_Command(aname,params);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetShell
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInShellIterator::SetShell(const Handle(WOKUtils_Shell)& ashell)
{
  myshell = ashell;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Shell
//purpose  : 
//=======================================================================
Handle(WOKUtils_Shell) WOKBuilder_ToolInShellIterator::Shell() const
{
  return myshell;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetParams
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInShellIterator::SetParam(const WOKUtils_Param& params)
{
  myparams = params;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Params
//purpose  : 
//=======================================================================
WOKUtils_Param WOKBuilder_ToolInShellIterator::Param() const 
{
  return myparams;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetOutputDir
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInShellIterator::SetOutputDir(const Handle(WOKUtils_Path)& apath)
{
  myoutdir = apath;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDir
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) WOKBuilder_ToolInShellIterator::OutputDir() const
{
  return myoutdir;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : LoadGroup
//purpose  : 
//=======================================================================
Standard_Integer WOKBuilder_ToolInShellIterator::LoadGroup()
{
  Handle(TCollection_HAsciiString) varname, toolsnames, toolname;
  Handle(WOKBuilder_ToolInShell)   atool;

  if(mygroup.IsNull())
    {
      ErrorMsg() << "WOKBuilder_ToolInShellIterator::LoadGroup"
	       << "Cannot not load an unamed tool group" << endm;
      return 1;
    }

  varname = new TCollection_HAsciiString("%");
  varname->AssignCat(mygroup);
  varname->AssignCat("_Tools");
  
  toolsnames = myparams.Eval(varname->ToCString(), Standard_True);

  if(toolsnames.IsNull())
    {
      ErrorMsg() << "WOKBuilder_ToolInShellIterator::LoadGroup"
	       << "Cannot not eval tool list for group : " << mygroup << " (parameter : " << varname << ")" << endm;
      return 1;
    }

  mytools = new WOKBuilder_HSequenceOfToolInShell;


  Standard_Integer i=1;
  toolname = toolsnames->Token(" \t\n", i);

  while(!toolname->IsEmpty())
    {
      atool = GetTool(toolname, myparams);

      if(atool.IsNull())
	{
	  ErrorMsg() << "WOKBuilder_ToolInShellIterator::LoadGroup"
		   << "Cannot not get Tool : " << toolname << endm;
	  return 1;
	}

      atool->Load();
      atool->SetShell(myshell);
      atool->SetOutputDir(myoutdir);

      Handle(TColStd_HSequenceOfHAsciiString) extseq = atool->TreatedExtensionNames();

      if(!extseq.IsNull())
	{
	  Standard_Integer j;
	  
	  for(j=1; j<=extseq->Length(); j++)
	    {
	      if(myexts.IsBound(extseq->Value(j)))
		{
		  Handle(WOKBuilder_ToolInShell) prevtool = myexts.Find(extseq->Value(j));

		  WarningMsg() << "WOKBuilder_ToolInShellIterator::LoadGroup"
		             << "Extension " << extseq->Value(j) << " is already recognized by " << prevtool->Name() << endm;
		  
		  WarningMsg() << "WOKBuilder_ToolInShellIterator::LoadGroup"
		             << "It is ignored for " << atool->Name() << endm;
		}
	      else
		{
		  myexts.Bind(extseq->Value(j), atool);
		}
	    }
	}

      mytools->Append(atool);
      
      i++;
      toolname = toolsnames->Token(" \t\n", i);
    }

  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Tools
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfToolInShell) WOKBuilder_ToolInShellIterator::Tools() const
{
  return mytools;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsTreatedExtension
//purpose  : 
//=======================================================================
Standard_Boolean WOKBuilder_ToolInShellIterator::IsTreatedExtension(const Handle(TCollection_HAsciiString)& anext) const
{
  if(myexts.IsBound(anext))
      return Standard_True;
  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AppropriateTool
//purpose  : 
//=======================================================================
Handle(WOKBuilder_ToolInShell) WOKBuilder_ToolInShellIterator::AppropriateTool(const Handle(WOKBuilder_Entity)& anent) const
{
  Handle(WOKBuilder_HSequenceOfExtension) exts;
  Handle(TCollection_HAsciiString) Ext;
  Handle(WOKBuilder_ToolInShell) NULLTOOL;

  if(anent.IsNull())
    {
      ErrorMsg() << "WOKBuilder_ToolInShellIterator::AppropriateTool" 
	       << "Cannot determine Tool for Null Entity" << endm;
      return NULLTOOL;
    }

  if(anent->Path().IsNull())
    {
      ErrorMsg() << "WOKBuilder_ToolInShellIterator::AppropriateTool" 
	       << "Cannot determine Tool for Null path entity" << endm;
      return NULLTOOL;
    }

  Ext = anent->Path()->ExtensionName();

  if(myexts.IsBound(Ext))
    {
      return myexts.Find(Ext);
    }
  return NULLTOOL;
}
 

//=======================================================================
//function : Produces
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfEntity) WOKBuilder_ToolInShellIterator::Produces() const
{
  return myproduction;
}
