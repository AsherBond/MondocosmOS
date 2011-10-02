// File:	WOKBuilder_ToolInShell.cxx
// Created:	Wed Aug 23 20:10:30 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ProgramError.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_Extension.hxx>


#include <WOKBuilder_CDLFile.hxx>
#include <WOKBuilder_Include.hxx>
#include <WOKBuilder_CodeGenFile.hxx>
#include <WOKBuilder_Compilable.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_MFile.hxx>
#include <WOKBuilder_SharedLibrary.hxx>
#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_Miscellaneous.hxx>
#include <WOKBuilder_CompressedFile.hxx>
#include <WOKBuilder_TarFile.hxx>

#include <WOKBuilder_ToolInShell.ixx>

//=======================================================================
//function : WOKBuilder_ToolInShell
//purpose  : 
//=======================================================================
WOKBuilder_ToolInShell::WOKBuilder_ToolInShell(const Handle(TCollection_HAsciiString) &aname,
					       const WOKUtils_Param& params) 
  : WOKBuilder_Tool(aname, params)
{
}

//=======================================================================
//function : Shell
//purpose  : 
//=======================================================================
Handle(WOKUtils_Shell) WOKBuilder_ToolInShell::Shell() const
{
  return myshell;
}

//=======================================================================
//function : SetShell
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInShell::SetShell(const Handle(WOKUtils_Shell)& ashell )
{
  myshell = ashell;
}

//=======================================================================
//function : ResetShell
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInShell::ResetShell() const
{
  if(!myshell.IsNull())
    myshell->Kill();
}

//=======================================================================
//function : SetTemplate
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInShell::SetTemplate(const Handle(TCollection_HAsciiString)& atempl)
{
  mytemplate = atempl;
}

//=======================================================================
//function : Template
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_ToolInShell::Template() const
{
  return mytemplate;
}


//=======================================================================
//function : Extensions
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfExtension) WOKBuilder_ToolInShell::Extensions() const
{
  return myexts;
}


//=======================================================================
//function : SetExtensions
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInShell::SetExtensions(const Handle(WOKBuilder_HSequenceOfExtension)& exts) 
{
  myexts = exts;
}

//=======================================================================
//function : Load
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInShell::Load()
{
  Handle(TCollection_HAsciiString) astr; 
  Handle(TCollection_HAsciiString) afile;
  Handle(WOKUtils_Path) apath;
  Standard_Integer i=1;

  // Extensions reconnues par le compilo
  myexts = new WOKBuilder_HSequenceOfExtension;

  astr = EvalToolParameter("Extensions");

  while(!(afile= astr->Token(" \t", i))->IsEmpty())
    {
      apath = new WOKUtils_Path(afile);
      myexts->Append(apath->Extension());
      i++;
    }

  astr = EvalToolParameter("Template");
  SetTemplate(astr);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : TreatedExtensionNames
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_ToolInShell::TreatedExtensionNames() const
{
  Standard_Integer i = 1;
  Handle(TCollection_HAsciiString) astr; 
  Handle(TCollection_HAsciiString) afile;
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;

  astr = EvalToolParameter("Extensions");

  while(!(afile= astr->Token(" \t", i))->IsEmpty())
    {
      Handle(WOKUtils_Path) apath = new WOKUtils_Path(afile);
      result->Append(apath->ExtensionName());
      i++;
    }
  return result;
}

//=======================================================================
//function : OptionLine
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_ToolInShell::OptionLine() const
{
  Handle(TCollection_HAsciiString)      result;
  Handle(TCollection_HAsciiString)      opttpl;
  Handle(TColStd_HSequenceOfHAsciiString) args;
  Standard_Integer i;

  if(opttpl.IsNull())
    {
      opttpl = new TCollection_HAsciiString(Name());
      opttpl->AssignCat("_OptLine");

      if(!Params().IsSet(opttpl->ToCString()))
	{
	  return result;
	}

      args = Params().GetArguments(opttpl->ToCString());
      
      for(i=1; i<=args->Length(); i++)
	{
	  if(!Params().IsSet(args->Value(i)->ToCString()))
	    {
	      ErrorMsg() << "WOKBuilder_ToolInShell::OptionLine" 
		       << "Could not eval ToolInShell option argument : " << args->Value(i)->ToCString() << endm;
	      return result;
	    }
	}
    }

  result = EvalToolTemplate("OptLine");
  return result;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : EvalProduction
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfEntity) WOKBuilder_ToolInShell::EvalProduction() const
{
  Handle(WOKBuilder_HSequenceOfEntity) nullseq, result = new WOKBuilder_HSequenceOfEntity;
  Handle(TCollection_HAsciiString)     prodlist;
  Handle(TCollection_HAsciiString)     aprod;
  Handle(WOKUtils_Path)                apath;
  Handle(WOKBuilder_Entity)            anent;
  Standard_Integer i = 1;

  prodlist = EvalToolTemplate("Production");

  if(prodlist.IsNull())
    {
      ErrorMsg() << "WOKBuilder_ToolInShell::EvalProduction"
	       << "Coul not eval production of " << Name() << endm;
      return nullseq;
    }

  aprod = prodlist->Token(" \t\n", i);

  while(!aprod->IsEmpty())
    {
      apath = new WOKUtils_Path(OutputDir()->Name(), aprod);

      switch(apath->Extension())
	{
	case WOKUtils_CFile:
	case WOKUtils_CXXFile: 
	case WOKUtils_F77File: 
	  anent = new WOKBuilder_Compilable(apath);
	  break;
	case WOKUtils_ObjectFile:
	  anent = new WOKBuilder_ObjectFile(apath);
	  break;
	case WOKUtils_MFile: 
	  anent = new WOKBuilder_MFile(apath);
	  break;
	case WOKUtils_CDLFile: 
	  anent = new WOKBuilder_CDLFile(apath);
	  break;
	case WOKUtils_HFile:
	case WOKUtils_HXXFile: 
	case WOKUtils_IXXFile: 
	case WOKUtils_JXXFile: 
	case WOKUtils_LXXFile: 
	case WOKUtils_GXXFile:
	case WOKUtils_PXXFile:
	case WOKUtils_INCFile:
	  anent = new WOKBuilder_Include(apath);
	  break;
	case WOKUtils_LexFile:
	case WOKUtils_YaccFile:
	case WOKUtils_PSWFile: 
	case WOKUtils_LWSFile: 
	  anent = new WOKBuilder_CodeGenFile(apath);
	  break;
	case WOKUtils_ArchiveFile: 
	  anent = new WOKBuilder_ArchiveLibrary(apath);
	  break;
	case WOKUtils_DSOFile: 
	  anent = new WOKBuilder_SharedLibrary(apath);
	  break;
	case WOKUtils_TarFile: 
	  anent = new WOKBuilder_TarFile(apath);
	  break;
	case WOKUtils_CompressedFile:  
	  anent = new WOKBuilder_CompressedFile(apath);
	  break;
	case WOKUtils_CSHFile: 
	case WOKUtils_DBFile:
	case WOKUtils_FDDBFile: 
	case WOKUtils_DDLFile: 
	case WOKUtils_HO2File:  
	case WOKUtils_LibSchemaFile: 
	case WOKUtils_AppSchemaFile:
	case WOKUtils_TemplateFile:
	case WOKUtils_DATFile:  
	case WOKUtils_ODLFile: 
	case WOKUtils_IDLFile: 
	case WOKUtils_LispFile: 
	case WOKUtils_IconFile:
	case WOKUtils_TextFile: 
	case WOKUtils_UnknownFile: 
	case WOKUtils_NoExtFile:
	default:
	  anent = new WOKBuilder_Miscellaneous(apath);
	  break;
	}
      
      if(!anent.IsNull()) result->Append(anent);

      i++;
      aprod = prodlist->Token(" \t\n", i);      
    }
  
  return result;
}


