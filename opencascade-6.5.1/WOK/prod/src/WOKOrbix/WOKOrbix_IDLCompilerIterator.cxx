

#include <WOKOrbix_IDLCompilerIterator.ixx>

#include <WOKTools_Messages.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_HSequenceOfPath.hxx>

#include <WOKBuilder_Compilable.hxx>
#include <WOKOrbix_IDLCompiler.hxx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKOrbix_IDLCompilerIterator
//purpose  : 
//=======================================================================
WOKOrbix_IDLCompilerIterator::WOKOrbix_IDLCompilerIterator(const Handle(TCollection_HAsciiString)& agroup,
							 const WOKUtils_Param& params)
: WOKBuilder_ToolInShellIterator(agroup, params)
{
}


//=======================================================================
//function : WOKOrbix_IDLCompilerIterator
//purpose  : 
//=======================================================================
WOKOrbix_IDLCompilerIterator::WOKOrbix_IDLCompilerIterator(const Handle(WOKBuilder_HSequenceOfToolInShell)& compilers)
  : WOKBuilder_ToolInShellIterator(compilers)
{
}

//=======================================================================
//function : WOKOrbix_IDLCompilerIterator
//purpose  : 
//=======================================================================
WOKOrbix_IDLCompilerIterator::WOKOrbix_IDLCompilerIterator(const Handle(TCollection_HAsciiString)& agroup,
							 const Handle(WOKUtils_Shell)& ashell,
							 const Handle(WOKUtils_Path)& outdir,
							 const Handle(WOKUtils_HSequenceOfPath)& incdirs,
							 const WOKUtils_Param& params)
  : WOKBuilder_ToolInShellIterator(agroup,ashell,outdir,params), myincdirs(incdirs)
{
}

//=======================================================================
//function : WOKOrbix_IDLCompilerIterator
//purpose  : 
//=======================================================================
void WOKOrbix_IDLCompilerIterator::Init(const Handle(WOKUtils_Shell)& ashell,
				       const Handle(WOKUtils_Path)& outdir,
				       const Handle(WOKUtils_HSequenceOfPath)& incdirs)
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) optline;

  WOKBuilder_ToolInShellIterator::Init(ashell,outdir);
  
  myincdirs = incdirs;
  
  Handle(WOKBuilder_HSequenceOfToolInShell) tools = Tools();

  if(!tools.IsNull())
    {
      for(i=1; i<=tools->Length(); i++)
	{
	  Handle(WOKOrbix_IDLCompiler) acompiler = Handle(WOKOrbix_IDLCompiler)::DownCast(tools->Value(i));
	  
	  if(!acompiler.IsNull())
	    {
	      acompiler->SetIncludeDirectories(myincdirs);
	    }
	
	  optline = acompiler->OptionLine();
	  
	  if(optline.IsNull())
	    {
	      ErrorMsg() << "WOKOrbix_IDLCompilerIterator::Init"
		<< "Could not eval compiler " << acompiler->Name() << " options" << endm;
	      return;
	    }
	  
	  InfoMsg() << "WOKOrbix_IDLCompilerIterator::Init" << optline << endm;
	}
    }
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetTool
//purpose  : 
//=======================================================================
Handle(WOKBuilder_ToolInShell) WOKOrbix_IDLCompilerIterator::GetTool(const Handle(TCollection_HAsciiString)& aname, 
									      const WOKUtils_Param& params) const
{
  return new WOKOrbix_IDLCompiler(aname,params);
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKOrbix_IDLCompilerIterator::Execute(const Handle(WOKOrbix_IDLFile)& anidlfile)  
{
  Handle(WOKOrbix_IDLCompiler) acompiler;
  WOKBuilder_BuildStatus status;

  myproduction.Nullify();

  acompiler = Handle(WOKOrbix_IDLCompiler)::DownCast(AppropriateTool(anidlfile));

  if(acompiler.IsNull())
    {
      ErrorMsg() << "WOKOrbix_IDLCompilerIterator::Execute" 
	<< "Could not find appropriate Compiler for " << anidlfile->Path()->Name() << endm;
      return WOKBuilder_Failed;
    }

  // setter le .compilable
  acompiler->SetIDLFile(anidlfile);

  status = acompiler->Execute();  

  if(status == WOKBuilder_Success)
    {
      myproduction = acompiler->Produces();
    }
  return status;
}

