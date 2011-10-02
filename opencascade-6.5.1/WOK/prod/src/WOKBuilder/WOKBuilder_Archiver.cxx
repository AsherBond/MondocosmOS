// File:	WOKBuilder_Archiver.cxx
// Created:	Tue Oct 24 13:31:42 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKUtils_Shell.hxx>
#include <WOKUtils_Path.hxx>

#include <EDL_API.hxx>


#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_Miscellaneous.hxx>
#include <WOKBuilder_HSequenceOfObjectFile.hxx>

#include <WOKBuilder_Archiver.ixx>

//=======================================================================
//function : WOKBuilder_Archiver
//purpose  : 
//=======================================================================
WOKBuilder_Archiver::WOKBuilder_Archiver(const WOKUtils_Param& params) 
  : WOKBuilder_ToolInShell(new TCollection_HAsciiString("LDAR"), params)
{
}

//=======================================================================
//function : Load
//purpose  : 
//=======================================================================
void WOKBuilder_Archiver::Load()
{
}

//=======================================================================
//function : SetObjectList
//purpose  : 
//=======================================================================
void WOKBuilder_Archiver::SetObjectList(const Handle(WOKBuilder_HSequenceOfObjectFile)& objects)
{
  myobjects = objects;
}

//=======================================================================
//function : ObjectList
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfObjectFile) WOKBuilder_Archiver::ObjectList() const 
{
  return myobjects;
}

//=======================================================================
//function : SetTargetName
//purpose  : 
//=======================================================================
void WOKBuilder_Archiver::SetTargetName(const Handle(TCollection_HAsciiString)& atarget)
{
  mytarget = atarget;
}

//=======================================================================
//function : TargetName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_Archiver::TargetName() const 
{
  return mytarget;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Archiver::Execute()
{
  Handle(TCollection_HAsciiString)       objlist = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString)       objtempl, astr, templ;
  Handle(WOKBuilder_HSequenceOfObjectFile)  aseq = new WOKBuilder_HSequenceOfObjectFile;
  Handle(WOKBuilder_HSequenceOfEntity)    result = new WOKBuilder_HSequenceOfEntity;
  Handle(WOKBuilder_ArchiveLibrary)        anent;
  Standard_Integer i=0;

  if(Shell()->IsLaunched() == Standard_False) Shell()->Launch();

  if(!IsLoaded()) Load();

  templ = EvalToolParameter("Template");
  
  if(templ.IsNull()) return WOKBuilder_Failed;

  SetTemplate(templ);

  objtempl = EvalToolParameter("ObjectRef");

  if(objtempl.IsNull())
    {
      ErrorMsg() << "WOKBuilder_Archiver::Execute" 
	       << "Could not eval Tool Parameter " << Name() << "_ObjectRef" << endm;
      return WOKBuilder_Failed;
    }

  Handle(TCollection_HAsciiString) strlimit = EvalToolParameter("LibLimit");
  Standard_Integer  limit = 0;

  if(!strlimit.IsNull())
    {
      if(strlimit->IsIntegerValue())
	{
	  limit = strlimit->IntegerValue();
	}
    }

  if(!limit) 
    {
      limit = myobjects->Length()+1;
    }

    
  // 
  //// Calcul de la liste des objets
  //
  Handle(TCollection_HAsciiString) filename = new TCollection_HAsciiString(TargetName());
  filename->AssignCat(".ObjList");
  
  Handle(WOKUtils_Path) objlistpath = new WOKUtils_Path(OutputDir()->Name(), filename);
  
  ofstream objstream(objlistpath->Name()->ToCString());
  
  if(!objstream.good())
    {
      ErrorMsg() << "WOKBuilder_Archiver::Execute" 
	       << "Could not open " << objlistpath->Name() << " for writing" << endm;
      return WOKBuilder_Failed;
    }

  
  for( i=1; i<=myobjects->Length(); i++)
    {
      objstream << myobjects->Value(i)->Path()->Name()->ToCString() << endl;
    }

  objstream.close();

  Params().Set("%LD_ObjList", objlistpath->Name()->ToCString()); 


  // calcul du path de la librairie
  anent = new WOKBuilder_ArchiveLibrary(TargetName(), OutputDir(), WOKBuilder_FullPath);
  anent->GetPath(Params());

  Params().Set("%LibName", anent->Path()->Name()->ToCString());

  Handle(TCollection_HAsciiString) begcmd = EvalToolTemplate("Begin");


  Shell()->Execute(begcmd);

  if(Shell()->Status())
    {
      ErrorMsg() << "WOKBuilder_Archiver::Execute" << "Errors occured in Shell during begin" << endm;
      Handle(TColStd_HSequenceOfHAsciiString) aseq = Shell()->Errors();
      
      for(Standard_Integer i=1; i<= aseq->Length(); i++)
	{
	  ErrorMsg() << "WOKBuilder_Archiver::Execute" << aseq->Value(i) << endm;
	}

      return WOKBuilder_Failed;
    }

  Shell()->ClearOutput();  

  i=1;
  while ( i <= myobjects->Length() )
    {
      Standard_Integer nbiniter = 1;
      objlist = new TCollection_HAsciiString;

      while ( (nbiniter <= limit) && (i<=myobjects->Length()) )
	{
	  Params().Set("%ObjectPath", myobjects->Value(i)->Path()->Name()->ToCString());
	  astr = Params().Eval(objtempl->ToCString());
	  objlist->AssignCat(astr);
	  nbiniter++;
	  i++;
	}
      Params().Set("%ObjectList", objlist->ToCString());

      astr = Params().Eval("LDAR_Iter");
      
      WOK_TRACE {
	VerboseMsg()("WOK_LDAR") << "WOKBuilder_Archiver::Execute" 
			       << "Archive line : " << astr << endm;
      }
      
      Shell()->Execute(astr);
      
      if(Shell()->Status())
	{
	  ErrorMsg() << "WOKBuilder_Archiver::Execute" << "Errors occured in Shell during iteration" << endm;
	  Handle(TColStd_HSequenceOfHAsciiString) aseq = Shell()->Errors();
	  
	  for(Standard_Integer i=1; i<= aseq->Length(); i++)
	    {
	      ErrorMsg() << "WOKBuilder_Archiver::Execute" << aseq->Value(i) << endm;
	    }
	  
	  return WOKBuilder_Failed;
	}
      Shell()->ClearOutput();
    }

  astr = Params().Eval("LDAR_End");
  
  WOK_TRACE {
    VerboseMsg()("WOK_LDAR") << "WOKBuilder_Archiver::Execute" 
			   << "Archive line : " << astr << endm;
  }
  
  Shell()->Execute(astr);
  
  if(Shell()->Status())
    {
      ErrorMsg() << "WOKBuilder_Archiver::Execute" << "Errors occured in Shell during end" << endm;
      Handle(TColStd_HSequenceOfHAsciiString) aseq = Shell()->Errors();
      
      for(Standard_Integer i=1; i<= aseq->Length(); i++)
	{
	  ErrorMsg() << "WOKBuilder_Archiver::Execute" << aseq->Value(i) << endm;
	}
      
      return WOKBuilder_Failed;
    }
  Shell()->ClearOutput();
  result->Append(anent);
  result->Append(new WOKBuilder_Miscellaneous(objlistpath));
  SetProduction(result);

  return WOKBuilder_Success;
}

