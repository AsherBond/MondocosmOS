// File:	WOKStep_EngLinkList.cxx
// Created:	Fri Aug  2 10:07:09 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>

#include <WOKUtils_Extension.hxx>
#include <WOKUtils_Path.hxx>

#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_UnitGraph.hxx>

#include <WOKBuilder_Entity.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_SharedLibrary.hxx>

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKStep_EngLinkList.ixx>


//=======================================================================
//function : WOKStep_EngLinList
//purpose  : 
//=======================================================================
WOKStep_EngLinkList::WOKStep_EngLinkList(const Handle(WOKMake_BuildProcess)& abp,
					 const Handle(WOKernel_DevUnit)& aunit, 
					 const Handle(TCollection_HAsciiString)& acode, 
					 const Standard_Boolean checked, 
					 const Standard_Boolean hidden) 
  : WOKStep_LinkList(abp,aunit, acode, checked, hidden)
{
}


//=======================================================================
//function : ComputeDependency
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKStep_EngLinkList::ComputeDependency(const Handle(TCollection_HAsciiString)& acode, 
									       const Handle(TColStd_HSequenceOfHAsciiString)& directlist) const
{
  return WOKernel_DevUnit::ImplementationDep(UnitGraph(), acode, directlist);
}



//=======================================================================
//Author   : Jean Gautier (jga)
//function : ComputeInterface
//purpose  : 
//=======================================================================
void WOKStep_EngLinkList::ComputeInterface(const Handle(WOKernel_DevUnit)& aunit, const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)    objfile;
  Handle(WOKMake_OutputFile) outfile;
  Handle(TCollection_HAsciiString) astr;
  static Handle(TCollection_HAsciiString) objtype = new TCollection_HAsciiString("object");

  astr = new TCollection_HAsciiString(aunit->Name());
#ifndef WNT
  astr->AssignCat(".o");
#else
  astr->AssignCat(".obj");
#endif

  objfile = Locator()->Locate(aunit->Name(), objtype, astr);
  
  if(objfile.IsNull())
    {
      ErrorMsg() << "WOKStep_EngLinkList::Execute"
	<< "Could not locate object file for interface : " << aunit->Name() << endm;
      SetFailed();
    }
  else
    {
      outfile = new WOKMake_OutputFile(objfile->LocatorName(), objfile, 
				       new WOKBuilder_ObjectFile(objfile->Path()), objfile->Path());
      outfile->SetLocateFlag(Standard_True);
      outfile->SetReference();
      
      AddExecDepItem(infile,outfile, Standard_True);
    } 
  return;
}



//=======================================================================
//Author   : Jean Gautier (jga)
//function : ComputeSchema
//purpose  : 
//=======================================================================
//void WOKStep_EngLinkList::ComputeSchema(const Handle(WOKernel_DevUnit)& aunit, const Handle(WOKMake_InputFile)& infile)
void WOKStep_EngLinkList::ComputeSchema(const Handle(WOKernel_DevUnit)& , const Handle(WOKMake_InputFile)& )
{
  return;
}


//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_EngLinkList::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(TColStd_HSequenceOfHAsciiString) getsupplist = new TColStd_HSequenceOfHAsciiString;
  
  WOKTools_MapOfHAsciiString amap;
  Handle(WOKBuilder_Library) alib;
  Handle(WOKernel_DevUnit)   aunit;
  Handle(WOKMake_InputFile)  inengine;
  Standard_Integer i;
  
  for(i=1; i<=execlist->Length(); i++)
    {
      Handle(WOKMake_OutputFile) outfile;
      Handle(WOKMake_InputFile) infile = execlist->Value(i);
      Handle(WOKBuilder_Entity) anent  = infile->BuilderEntity();
      
      if(anent->IsKind(STANDARD_TYPE(WOKBuilder_Library)) || anent->IsKind(STANDARD_TYPE(WOKBuilder_ObjectFile)))
	{
	  outfile = new WOKMake_OutputFile(infile);
	  outfile->SetReference();
	  AddExecDepItem(infile,outfile, Standard_True);
	}
      else
	{
	  if(anent->IsKind(STANDARD_TYPE(WOKBuilder_MSEntity)))
	    {
	      Handle(WOKBuilder_MSEntity)      amsent = Handle(WOKBuilder_MSEntity)::DownCast(anent);
	      Handle(WOKernel_DevUnit)         aunit = Locator()->LocateDevUnit(amsent->Name());
	      
	      if(!aunit.IsNull())
		{
		  if(WOKernel_IsInterface(aunit))
		    {
		      ComputeInterface(aunit, infile);

		      getsupplist->Append(aunit->Name());
		      // ajouter l'ud
		    }
		  else if(WOKernel_IsEngine(aunit))
		    {
		      inengine = infile;
		    }
		  else if(WOKernel_IsSchema(aunit))
		    {
		      ComputeSchema(aunit,infile);
		      //getsupplist->Append(aunit->Name());
		    }
		  
		}
	      else
		{
		  ErrorMsg() << "WOKStep_EngLinkList::Execute"
			   << "Could not locate interface : " << amsent->Name() << endm;
		  SetFailed();
		}
	    }
	}
    }

  if(inengine.IsNull())
    {
      ErrorMsg() << "WOKStep_EngLinkList::Execute"
	       << "Could determine current engine in InputList" << endm;
      SetFailed();
    }
  else
    {
      for(i=1; i<=getsupplist->Length(); i++)
	{
	  amap.Add(getsupplist->Value(i));
	}

      Handle(TColStd_HSequenceOfHAsciiString) suppliers = ComputeDependency(Unit()->Name(), getsupplist);


      if (suppliers.IsNull())
	{
	  SetFailed();
	  return;
	}

      Handle(WOKMake_OutputFile) outfile;
      
      for(i=suppliers->Length()-1; i>=1; i--)
	{

	  ComputeExternals(suppliers->Value(i));

	  Standard_Boolean use_lib = Standard_True;
	  
	  aunit = Locator()->LocateDevUnit(suppliers->Value(i));
	  
	  if(WOKernel_IsInterface(aunit))
	    {
	      if(amap.Contains(aunit->Name()))
		{
		  // L'interface est utilisee en tant que stub serveur
		  use_lib = Standard_False;
		}
	    }
	  
	  if(use_lib)
	    {
	      //
	      AddUnitContribution(inengine, aunit->Name());
	    }
	  
	}      
    }
  if(!CheckStatus("Execute")) SetSucceeded();
  return;
}






