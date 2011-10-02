// File:	WOKBuilder_CodeGeneratorIterator.cxx
// Created:	Thu Jul 11 19:09:38 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>



#include <WOKTools_Messages.hxx>
#include <WOKUtils_Path.hxx>

#include <WOKBuilder_CodeGenFile.hxx>
#include <WOKBuilder_CodeGenerator.hxx>

#include <WOKBuilder_CodeGeneratorIterator.ixx>
//=======================================================================
//function : WOKBuilder_CodeGeneratorIterator
//purpose  : 
//=======================================================================
WOKBuilder_CodeGeneratorIterator::WOKBuilder_CodeGeneratorIterator(const Handle(TCollection_HAsciiString)& agroup,
								   const WOKUtils_Param& params)
  : WOKBuilder_ToolInShellIterator(agroup,params)
{
}

//=======================================================================
//function : WOKBuilder_CodeGeneratorIterator
//purpose  : 
//=======================================================================
WOKBuilder_CodeGeneratorIterator::WOKBuilder_CodeGeneratorIterator(const Handle(WOKBuilder_HSequenceOfToolInShell)& CodeGenerators)
  : WOKBuilder_ToolInShellIterator(CodeGenerators)
{
}

//=======================================================================
//function : WOKBuilder_CodeGeneratorIterator
//purpose  : 
//=======================================================================
WOKBuilder_CodeGeneratorIterator::WOKBuilder_CodeGeneratorIterator(const Handle(TCollection_HAsciiString)& agroup,
							 const Handle(WOKUtils_Shell)& ashell,
							 const Handle(WOKUtils_Path)& outdir,
							 const WOKUtils_Param& params)
  : WOKBuilder_ToolInShellIterator(agroup,ashell,outdir,params)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKBuilder_CodeGeneratorIterator::Init(const Handle(WOKUtils_Shell)& ashell,
					    const Handle(WOKUtils_Path)& apath)
  
{
  WOKBuilder_ToolInShellIterator::Init(ashell, apath);

  Handle(WOKBuilder_HSequenceOfToolInShell) tools = Tools();
  Handle(TCollection_HAsciiString) optline;

  if(!tools.IsNull())
    {
      for(Standard_Integer i=1; i<=tools->Length(); i++)
	{
	  Handle(WOKBuilder_CodeGenerator) acodegen = Handle(WOKBuilder_CodeGenerator)::DownCast(tools->Value(i));
	  
	  if(!acodegen.IsNull())
	    {
	      optline = acodegen->OptionLine();
	      
	      if(optline.IsNull())
		{
		  ErrorMsg() << "WOKBuilder_CodeGeneratorIterator::Init"
		    << "Could not eval code generator " << acodegen->Name() << " options" << endm;
		  return;
		}
	      
	      InfoMsg() << "WOKBuilder_CodeGeneratorIterator::Init" << optline << endm;
	    }
	}
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetTool
//purpose  : 
//=======================================================================
Handle(WOKBuilder_ToolInShell) WOKBuilder_CodeGeneratorIterator::GetTool(const Handle(TCollection_HAsciiString)& aname, 
									      const WOKUtils_Param& params) const
{
  return new WOKBuilder_CodeGenerator(aname,params);
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_CodeGeneratorIterator::Execute(const Handle(WOKBuilder_CodeGenFile)& aCodeGenFile)  
{
  Handle(WOKBuilder_CodeGenerator) aCodeGenerator;
  WOKBuilder_BuildStatus status;

  myproduction.Nullify();

  aCodeGenerator = Handle(WOKBuilder_CodeGenerator)::DownCast(AppropriateTool(aCodeGenFile));

  if(aCodeGenerator.IsNull())
    {
      ErrorMsg() << "WOKBuilder_CodeGeneratorIterator::Execute" 
	<< "Could not find appropriate CodeGenerator for " << aCodeGenFile->Path()->Name() << endm;
      return WOKBuilder_Failed;
    }

  // setter le .CodeGenFile
  aCodeGenerator->SetCodeGenFile(aCodeGenFile);

  status = aCodeGenerator->Execute();  

  if(status == WOKBuilder_Success)
    {
      myproduction = aCodeGenerator->Produces();
    }
  return status;
}
