#include <Standard_Stream.hxx>
#include <Standard_SStream.hxx>
#include <Standard_ProgramError.hxx>
#include <Standard_ErrorHandler.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>

#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_IndexedDataMapOfHAsciiString.hxx>


#include <WOKUtils_ParamItem.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_AdmFile.hxx>


#include <WOKernel_Session.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_DataMapIteratorOfDataMapOfFileType.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_HSequenceOfFile.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_UnitGraph.hxx>
#include <WOKernel_SortedImpldepFromIterator.hxx>
#include <WOKernel_SortedClientsFromIterator.hxx>
#include <WOKernel_DevUnit.ixx>

#define READBUF_SIZE 1024

#ifndef WOK_DEPCOMPAT
#define WOK_DEPCOMPAT 1
#endif

#ifdef HAVE_IOMANIP
#include <iomanip>
#elif defined (HAVE_IOMANIP_H)
# include <iomanip.h>
#endif

//=======================================================================
//function : WOKernel_DevUnit
//purpose  : 
//=======================================================================
WOKernel_DevUnit::WOKernel_DevUnit(const Handle(WOKernel_UnitTypeDescr)& atype,
				   const Handle(TCollection_HAsciiString)& aname, 
				   const Handle(WOKernel_UnitNesting)& anesting) 
  : WOKernel_Entity(aname, anesting), mytype(atype)
{
}

//=======================================================================
//function : GetParameters
//purpose  :  
//=======================================================================
void WOKernel_DevUnit::GetParameters()
{
  Standard_Integer i;
  Handle(TColStd_HSequenceOfAsciiString) aseq;
  Handle(TColStd_HSequenceOfAsciiString) subclasses = new TColStd_HSequenceOfAsciiString;
  Handle(TColStd_HSequenceOfAsciiString) dirs       = new TColStd_HSequenceOfAsciiString;
  Handle(TCollection_HAsciiString) astr;
  Handle(TCollection_HAsciiString) libdir;
  Handle(WOKernel_Entity) wbnesting,isletnesting,udfromdad;
  
  if(!Nesting().IsNull())
    {
      // Entites quelconques
      
      wbnesting = Session()->GetEntity(Nesting());
      if (!wbnesting->IsKind(STANDARD_TYPE(WOKernel_Workbench))) 
	{ // unit in parcel
	  WOKernel_Entity::GetParameters();
	}
      else 
	{ // unit in workbench : identical subclasses
	  aseq = wbnesting->Params().SubClasses();
	  if(!aseq.IsNull())
	    {
	      for(i=1; i<=aseq->Length(); i++)
		{
		  subclasses->Append(aseq->Value(i));
		}
	    }
	  
	  subclasses->Append(Name()->ToCString());
	  
	  //ChangeParams().SetSubClasses(subclasses);
	  // unit in workbench : Search Directories for src are inserted
	  isletnesting = Session()->GetEntity(wbnesting->Nesting());
	  if (!isletnesting.IsNull()) 
	    {
	      aseq =  isletnesting->Params().SearchDirectories();
	      if(!aseq.IsNull())
		{
		  for(i=1; i<=aseq->Length(); i++)
		    {
		      dirs->Append(aseq->Value(i));
		    }
		}
	      
	      ChangeParams().SetSubClasses(subclasses);
	      ChangeParams().SetSearchDirectories(dirs);
	      
	      // Evaluation adm for wb ancestors and insert sources dirs for DevUnit
	      Handle(TColStd_HSequenceOfHAsciiString) ances = Handle(WOKernel_Workbench)::DownCast(wbnesting)->Ancestors();

	      for (Standard_Integer i= ances->Length(); i > 1 ; i--) 
		{
		  Handle(WOKernel_Workbench) wb = Session()->GetWorkbench(ances->Value(i));
		  
		  
		  if (!wb.IsNull()) 
		    {
		      astr = wb->EvalParameter("Adm", Standard_False);
		      
		      if(!astr.IsNull()) 
			{
			  dirs->Prepend(astr->ToCString());
			  //TCollection_AsciiString& lastsub = subclasses->ChangeValue(subclasses->Length());
			  //lastsub.AssignCat("@");
			  //lastsub.AssignCat(astr->String());
			}
		      
		      Handle(WOKernel_DevUnit) udfromdad = Session()->GetDevUnit(wb->NestedUniqueName(Name()));
		      if (!udfromdad.IsNull()) 
			{
			  udfromdad->Open();
			  Handle(WOKernel_FileType)        atype = udfromdad->FileTypeBase()->Type("source");
			  Handle(TCollection_HAsciiString) apath = atype->ComputePath(udfromdad->Params(), new TCollection_HAsciiString("."));
			  dirs->Prepend(apath->ToCString());
			  TCollection_AsciiString& lastsub = subclasses->ChangeValue(subclasses->Length());
			  if(lastsub.Search("@") > 0 )
			    {
			      // grandfathers
			      subclasses->Append(Name()->String());
			      TCollection_AsciiString& lastsub = subclasses->ChangeValue(subclasses->Length());
			      lastsub.AssignCat("@");
			      lastsub.AssignCat(apath->String());
			    }
			  else
			    {
			      lastsub.AssignCat("@");
			      lastsub.AssignCat(apath->String());
			    }
			}
		    }
		}
	      
	      // dans le WB courant
	      astr = wbnesting->EvalParameter("Adm", Standard_False);
	      if(!astr.IsNull()) 
		{
		  dirs->Prepend(astr->ToCString());
		}
	      //Handle(WOKernel_FileType)        atype = FileTypeBase()->Type("source");
	      //Handle(TCollection_HAsciiString) apath = atype->ComputePath(Params(), new TCollection_HAsciiString("."));
	      //dirs->Prepend(apath->ToCString());

	      ChangeParams().SetSearchDirectories(dirs);
	    }
	}
    }
  
  Params().Set("%UnitType", Type()->ToCString());
  
}  

//=======================================================================
//Author   : Jean Gautier (jga)
//function : EntityCode
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_DevUnit::EntityCode() const
{
  static Handle(TCollection_HAsciiString) acode = new TCollection_HAsciiString("devunit");
  return acode;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : TypeCode
//purpose  : 
//=======================================================================
Standard_Character WOKernel_DevUnit::TypeCode() const
{
  return mytype->Key();
}
//=======================================================================
//Author   : Jean Gautier (jga)
//function : TypeCode
//purpose  : 
//=======================================================================
const Handle(TCollection_HAsciiString)& WOKernel_DevUnit::Type() const
{
  return mytype->Type();
}

//=======================================================================
//function : BuildParameters
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKernel_DevUnit::BuildParameters(const Handle(WOKUtils_HSequenceOfParamItem)& someparams,
									const Standard_Boolean usedefaults) 
{
  Handle(WOKernel_UnitNesting) anesting;
  
  anesting = Session()->GetUnitNesting(Nesting());

  if(anesting.IsNull())
    {
      ErrorMsg() << "WOKernel_DevUnit::Build" 
	       << "Nesting : " << Nesting() << " is an invalid Nesting for a DevUnit" << endm;
      return someparams;
    }

  someparams->Append(WOKUtils_ParamItem(ParameterName("DBMSystems"), anesting->EvalParameter("DBMSystems")));
  someparams->Append(WOKUtils_ParamItem(ParameterName("Stations"),   anesting->EvalParameter("Stations")));

  WOKernel_Entity::BuildParameters(someparams, usedefaults);
  return someparams;
}

//=======================================================================
//function : Build
//purpose  :
//=======================================================================
void WOKernel_DevUnit::Build(const Handle(WOKUtils_HSequenceOfParamItem)& someparams)
{
  WOKernel_Entity::Build(someparams);
  return;
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
void WOKernel_DevUnit::Destroy()
{
  if(myfiles.IsNull())
    {
      ReadFileList(Handle(WOKernel_Locator)());
    }

  Standard_Integer i;
  Handle(TCollection_HAsciiString) aname, atype;
  Handle(WOKernel_File) afile;

  for(i=1; i<=myfiles->Length(); i++)
    {
      aname = myfiles->Value(i)->Token(":", 3);
      atype = myfiles->Value(i)->Token(":", 2);
      
      afile = new WOKernel_File(aname, this, GetFileType(atype));

      afile->GetPath();

      if(afile->Path()->Exists())
	{
	  WOK_TRACE {
	    VerboseMsg()("WOK_DESTROY") << "WOKernel_DevUnit::Destroy" 
				      << "Removing file : " << afile->Path()->Name() << endm;
	  }
	  afile->Path()->RemoveFile();
	}
    }

  WOKernel_Entity::Destroy();
}

//=======================================================================
//function : Open
//purpose  : 
//=======================================================================
void WOKernel_DevUnit::Open()
{
  if(IsOpened()) return;
  {
    Handle(WOKernel_Workbench)            abench;
    Handle(WOKernel_DevUnit)              aunit;
    Handle(WOKernel_BaseEntity)           anentity;
    Handle(WOKernel_UnitNesting)     anesting = Session()->GetUnitNesting(Nesting());
    Handle(TCollection_HAsciiString) astr;
    
    GetParams();

    SetFileTypeBase(Session()->GetFileTypeBase(this));

    abench = Session()->GetWorkbench(Nesting());

    if(!abench.IsNull())
      {
	Handle(WOKernel_Workbench) afather = Session()->GetWorkbench(abench->Father());

	// le nesting est un WB donc tenter d'ouvrir l'ud ds les wbs peres d'abord
	if(afather.IsNull() == Standard_False)
	  {
	    // le WB n'est pas une racine
	    astr = afather->NestedUniqueName(Name());

	    anentity = Session()->GetEntity(afather->NestedUniqueName(Name()));

	    if(anentity.IsNull() == Standard_False)
	      {
		// l'ud est presente dans le pere

		aunit = Handle(WOKernel_DevUnit)::DownCast(anentity);
		
		if(aunit.IsNull() == Standard_True)
		  {
		    ErrorMsg() << "WOKernel_DevUnit::Open" 
		      << "Wrong Type for Entity " 
			<< Name() << "found in workbench " 
			  << afather->Name() << endm;
		    Standard_ProgramError::Raise("WOKernel_DevUnit::Open");
		  }
		
		aunit->Open();
	      }
	  }
      }

    

    // Clacul de quelques parametres de confort : ADM, SRC, DRV, HOME
    
    Handle(WOKernel_FileType)        atype;
    Handle(TCollection_HAsciiString) apath;

    atype = FileTypeBase()->Type("source");
    
    apath = atype->ComputePath(Params(), new TCollection_HAsciiString("."));

    if(!apath.IsNull())
      {
	Params().Set(ParameterName("Src")->ToCString(), apath->ToCString());
	Params().Set(ParameterName("Home")->ToCString(), apath->ToCString());
	Params().SearchDirectories()->Prepend(apath->ToCString());
	Params().SubClasses()->Append(Name()->String());
	TCollection_AsciiString& lastsub =  Params().SubClasses()->ChangeValue(Params().SubClasses()->Length());
	lastsub.AssignCat("@");
	lastsub.AssignCat(apath->String());
    
      }
    
    atype = FileTypeBase()->Type("library");

    apath = atype->ComputePath(Params(), new TCollection_HAsciiString("."));
    
    if(!apath.IsNull())
      {
	Params().Set(ParameterName("Lib")->ToCString(), apath->ToCString());
      }
    
    
    atype = FileTypeBase()->Type("admfile");

    apath = atype->ComputePath(Params(), new TCollection_HAsciiString("."));
    
    if(!apath.IsNull())
      {
	Params().Set(ParameterName("Adm")->ToCString(), apath->ToCString());
      }

    SetOpened();
  }
}

//=======================================================================
//function : Close
//purpose  : 
//=======================================================================
void WOKernel_DevUnit::Close()
{
  if(!IsOpened()) return;

  myfiles.Nullify();
  Reset();
  SetClosed();
}

//=======================================================================
//function : AddFile
//purpose  : 
//=======================================================================
void WOKernel_DevUnit::AddFile(const Handle(WOKernel_File)& afile)
{
  FileList()->Append(afile->LocatorName());
}

//=======================================================================
//function : RemoveFile
//purpose  : 
//=======================================================================
void WOKernel_DevUnit::RemoveFile(const Handle(WOKernel_File)& afile)
{
  Standard_Integer i;
  
  for(i=1; i<=myfiles->Length(); i++)
    {
      if(afile->FullName()->IsSameString(myfiles->Value(i)))
	{
	  myfiles->Remove(i);
	  break;
	}
    }
  return;
}

//=======================================================================
//function : ReadSingleFileList
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_DevUnit::ReadSingleFileList(const Handle(WOKernel_File)& afile) const
{
  Handle(TColStd_HSequenceOfHAsciiString) afileseq = new TColStd_HSequenceOfHAsciiString;
  Handle(WOKernel_File) thefile;
  Handle(TCollection_HAsciiString) astr, atype;
  Handle(WOKernel_File) infile;
  Handle(WOKUtils_Path) apath;

  if(afile.IsNull())
    {
      return afileseq;
    }

  afile->GetPath();
    
  apath = afile->Path();
  
  if(!apath->Exists())
    {  
      return afileseq;
    }


    {
      char typebuf[1024];
      char namebuf[1024];
      ifstream astream(apath->Name()->ToCString(), ios::in);

      *namebuf = *typebuf = '\0';

      if(!astream)
	{
	  ErrorMsg() << "WOKernel_DevUnit::ReadSingleFileList" << "Could not open " << afile->Path()->Name() << endm;
	  Standard_ProgramError::Raise("WOKernel_DevUnit::ReadSingleFileList");
	}
      
      while(astream >> setw(1024) >> typebuf >> setw(1024) >> namebuf)
	{
	  astr  = new TCollection_HAsciiString(namebuf);
	  atype = new TCollection_HAsciiString(typebuf);

	  afileseq->Append(WOKernel_File::FileLocatorName(Name(), atype, astr));

	  *namebuf = *typebuf = '\0';
	}
      astream.close();
      return afileseq;
    }
}


//=======================================================================
//function : WriteSingleFileList
//purpose  : 
//=======================================================================
void WOKernel_DevUnit::WriteSingleFileList(const Handle(WOKernel_File)& afile, const Handle(WOKernel_HSequenceOfFile)& alist) const
{
  afile->GetPath();
  ofstream astream(afile->Path()->Name()->ToCString(), ios::out);
  
  if(!astream)
    {
      ErrorMsg() << "WOKernel_DevUnit::WriteSingleFileList" << "Could not open " << afile->Path()->Name() << endm;
      Standard_ProgramError::Raise("WOKernel_DevUnit::WriteSingleFileList");
    }
  
  for(Standard_Integer i=1; i<=alist->Length(); i++)
    {
      astream << FileTypeBase()->TypeName(alist->Value(i)->Type())->ToCString() 
	<< " "
	  << alist->Value(i)->Name()->ToCString() << endl;
    }
  astream.close();
  return;
}

//=======================================================================
//function : ReadFileList
//purpose  : 
//=======================================================================
void WOKernel_DevUnit::ReadFileList(const Handle(WOKernel_Locator)& alocator) 
{
  Handle(TCollection_HAsciiString) afilelistfile, locatorname;
  Handle(WOKernel_File)                  admfile, dbadmfile, stadmfile;
  Handle(WOKernel_File)                    afile;
  Handle(TColStd_HSequenceOfHAsciiString) afileseq;
  WOKTools_MapOfHAsciiString                amap;
  Standard_Integer i;

  if(myfiles.IsNull()==Standard_False)
    {
      myfiles->Clear();
    }
  else
    {
      myfiles = new TColStd_HSequenceOfHAsciiString;
    }
  
  afilelistfile = new TCollection_HAsciiString(Name());
  afilelistfile->AssignCat(Params().Eval("%FILENAME_FILELIST_EXT"));

  if(alocator.IsNull())
    admfile = new WOKernel_File(afilelistfile, this, GetFileType("admfile"));
  else
    admfile = alocator->Locate(Name(), GetFileType("admfile")->Name(), afilelistfile);

  if(!admfile.IsNull())
    {
      if (!amap.Contains(admfile->LocatorName())) {
	amap.Add(admfile->LocatorName());
	myfiles->Append(admfile->LocatorName());
      }
      afileseq = ReadSingleFileList(admfile);
      for(i=1; i<=afileseq->Length(); i++) 
	{
	  locatorname = afileseq->Value(i);
	  if(!amap.Contains(locatorname))
	    {
	      amap.Add(locatorname);
	      myfiles->Append(locatorname);
	    }
	}
    }

  if(alocator.IsNull())
    dbadmfile = new WOKernel_File(afilelistfile, this, GetFileType("dbadmfile"));
  else
    dbadmfile = alocator->Locate(Name(), GetFileType("dbadmfile")->Name(), afilelistfile);

  if(!dbadmfile.IsNull())
    {
      if (!amap.Contains(dbadmfile->LocatorName())) {
	amap.Add(dbadmfile->LocatorName());
	myfiles->Append(dbadmfile->LocatorName());
      }
      afileseq = ReadSingleFileList(dbadmfile);
      for(i=1; i<=afileseq->Length(); i++) 
	{
	  locatorname = afileseq->Value(i);
	  if(!amap.Contains(locatorname))
	    {
	      amap.Add(locatorname);
	      myfiles->Append(locatorname);
	    }
	}
    }

  if(alocator.IsNull())
    stadmfile = new WOKernel_File(afilelistfile, this, GetFileType("stadmfile"));
  else
    stadmfile = alocator->Locate(Name(), GetFileType("stadmfile")->Name(), afilelistfile);

  if(!stadmfile.IsNull())
    {
      if (!amap.Contains(stadmfile->LocatorName())) {
	amap.Add(stadmfile->LocatorName());
	myfiles->Append(stadmfile->LocatorName());
      }
      afileseq = ReadSingleFileList(stadmfile);
      for(i=1; i<=afileseq->Length(); i++) 
	{
	  locatorname = afileseq->Value(i);
	  if(!amap.Contains(locatorname))
	    {
	      amap.Add(locatorname);
	      myfiles->Append(locatorname);
	    }
	}
    }
  return;
}

//=======================================================================
//function : DumpFileList
//purpose  : 
//=======================================================================
void WOKernel_DevUnit::DumpFileList(const Handle(WOKernel_Locator)& alocator) const
{
  Handle(TCollection_HAsciiString) afilelistfile;
  Handle(WOKernel_File) admfile, dbadmfile, stadmfile;
  Handle(WOKernel_File) afile, thefile;

  afilelistfile = new TCollection_HAsciiString(Name());
  afilelistfile->AssignCat(Params().Eval("%FILENAME_FILELIST_EXT"));

  admfile = new WOKernel_File(afilelistfile, this, GetFileType("admfile"));
  admfile->GetPath();
  ofstream admstream(admfile->Path()->Name()->ToCString());

  dbadmfile = new WOKernel_File(afilelistfile, this, GetFileType("dbadmfile"));
  dbadmfile->GetPath();
  ofstream dbadmstream(dbadmfile->Path()->Name()->ToCString());

  stadmfile = new WOKernel_File(afilelistfile, this, GetFileType("stadmfile"));
  stadmfile->GetPath();
  ofstream stadmstream(stadmfile->Path()->Name()->ToCString());

  for(Standard_Integer i=1; i<=myfiles->Length(); i++)
    {
      afile = alocator->Locate(myfiles->Value(i));

      if(afile.IsNull() == Standard_False)
	{
	  if(afile->Type()->IsStationDependent())
	    {
	      stadmstream << afile->TypeName()->ToCString() << " " << afile->Name()->ToCString() << endl;
	    }
	  else if(afile->Type()->IsDBMSDependent())
	    {
	      dbadmstream << afile->TypeName()->ToCString() << " " << afile->Name()->ToCString() << endl;
	    }
	  else
	    {
	      admstream << afile->TypeName()->ToCString() << " " << afile->Name()->ToCString() << endl;
	    }
	}
    }
  stadmstream.close();
  dbadmstream.close();
  admstream.close();
  return;
}
//=======================================================================
//function : SearchInFileList
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_DevUnit::SearchInFileList(const Handle(WOKernel_Locator)& alocator,
						    const Handle(TCollection_HAsciiString)& afilename)
{
  Handle(TColStd_HSequenceOfHAsciiString) fileseq;
  Standard_Boolean found = Standard_False;
  Standard_Integer i;
  
  if (FileList().IsNull()) ReadFileList(alocator);

  fileseq = FileList();
  
  for(i=1; i<=fileseq->Length() && !found; i++)
    {
      Handle(TCollection_HAsciiString) file     = fileseq->Value(i);
      Handle(TCollection_HAsciiString) filename = file->Token(":",3);

      if (!strcmp(filename->ToCString(),afilename->ToCString()))
	found =Standard_True;
    }

  return found;  
}

//=======================================================================
//function : NestedFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_DevUnit::NestedFileName(const Handle(TCollection_HAsciiString)& atype,
								  const Handle(TCollection_HAsciiString)& aname)  
{
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString(aname);
  astr->AssignCat(":");
  astr->AssignCat(atype);
  astr->AssignCat(":");
  astr->AssignCat(FullName());
  return astr;
}

//=======================================================================
//function : SetFileList
//purpose  : 
//=======================================================================
void WOKernel_DevUnit::SetFileList(const Handle(TColStd_HSequenceOfHAsciiString)& aseq)
{
  myfiles = aseq;
}

//=======================================================================
//function : FileList
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_DevUnit::FileList() const
{
  return myfiles;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : ImplDepFile
//purpose  : 
//=======================================================================
Handle(WOKernel_File) WOKernel_DevUnit::ImplDepFile(const Handle(WOKernel_Locator)& alocator,
						    const Handle(TCollection_HAsciiString)& aname) const
{
  Handle(WOKernel_File) result;
  Handle(TCollection_HAsciiString) impldepfilename;

  static Handle(TCollection_HAsciiString)     stadmtype = new TCollection_HAsciiString("stadmfile");
  
  impldepfilename = new TCollection_HAsciiString(aname);
  impldepfilename->AssignCat(".");
  impldepfilename->AssignCat(Params().Eval("%FILENAME_IMPLDEP"));
  
  result = alocator->Locate(Name(), stadmtype, impldepfilename);

  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ReadImplDepFile
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_DevUnit::ReadImplDepFile(const Handle(WOKUtils_Path)& afile,
									  const Handle(WOKernel_Locator)& alocator,
//									  const Standard_Boolean oldWokFile) const
									  const Standard_Boolean ) const
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;
  Handle(TCollection_HAsciiString)        astr;
  Handle(WOKernel_DevUnit)                aunit;
  ifstream astream(afile->Name()->ToCString());
  static char                                 anamebuf[READBUF_SIZE];

  *anamebuf = '\0';

  astream >> setw(READBUF_SIZE) >> anamebuf;
  
  while (!astream.eof()) {
    if(strcmp(anamebuf, Name()->ToCString())) {
      astr = new TCollection_HAsciiString(anamebuf);
      
      aunit = alocator->LocateDevUnit(astr);
      
      if(aunit.IsNull()) 
	{
	  WarningMsg() << "WOKernel_DevUnit::ReadImplDepFile" 
		     << "Wrong or not visible entry " << astr << " in implementation dep of " << Name() << endm;
	}
      else
	{
	  result->Append(astr);
	}
    }
    *anamebuf = '\0';
    
    astream >> setw(READBUF_SIZE) >> anamebuf;  
  }
  astream.close();
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ImplementationDepList
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_DevUnit::ImplementationDepList(const Handle(WOKernel_UnitGraph)& agraph) 
{
  Handle(TColStd_HSequenceOfHAsciiString)     unitDep, result,NULLRESULT;
  Handle(TCollection_HAsciiString)            impldepfilename, aname;
  Handle(WOKernel_DevUnit)                    aunit;
  Handle(WOKernel_File)                       impldepfile;
  Standard_Boolean                            oldWokFile = Standard_False;
	      
  unitDep = new TColStd_HSequenceOfHAsciiString;

  if(!IsOpened()) Open();

  static Handle(TCollection_HAsciiString)     stadmtype = new TCollection_HAsciiString("stadmfile");

  if (agraph->Contains(Name())) 
    {
      return agraph->Suppliers(Name());
    }
  else 
    {
      impldepfile = ImplDepFile(agraph->Locator(), Name());
      
      oldWokFile = Standard_False;
      
      if (impldepfile.IsNull()) 
	{  
	  Handle(TCollection_HAsciiString) impldepfilename = new TCollection_HAsciiString;
	  Handle(TCollection_HAsciiString) stadmtype = new TCollection_HAsciiString("stadmfile");
	  
	  impldepfilename->AssignCat(Name());
	  impldepfilename->AssignCat(".");
	  impldepfilename->AssignCat(Params().Eval("%FILENAME_IMPLDEP"));
  
	  Standard_Boolean mustExist = SearchInFileList(agraph->Locator(),impldepfilename);
	  if (mustExist)
	    {
	      ErrorMsg() << "WOKernel_DevUnit::ImplementationDepList" 
		       << "Implementation Dependences not found for " << Name() << endm;
	      ErrorMsg() << "WOKernel_DevUnit::ImplementationDepList" 
		       << "Perhaps " << Name() << " is not compiled on this platform" << endm;
	      return NULLRESULT;
	    }
	  else
	    {
	      WarningMsg() << "WOKernel_DevUnit::ImplementationDepList" 
		         << "Could not determine Implementation Dependences for " << Name() << endm;
	      agraph->Add(Name(), new TColStd_HSequenceOfHAsciiString);
	    }
	}
      else 
	{
	  unitDep = ReadImplDepFile(impldepfile->Path(), agraph->Locator(), oldWokFile);
	  agraph->Add(Name(),unitDep);
	}
    }      
  
  static Handle(TCollection_HAsciiString) sourcetype = new TCollection_HAsciiString("source");
  Handle(TCollection_HAsciiString) filename = new TCollection_HAsciiString(Name());
  
  filename->AssignCat(".");
  filename->AssignCat(Params().Eval("%FILENAME_DYNAMICPK"));

  Handle(WOKernel_File) intern = agraph->Locator()->Locate(Name(), sourcetype, filename);
  
  if(!intern.IsNull())
    {
      WOKUtils_AdmFile adm(intern->Path());
      Handle(TColStd_HSequenceOfHAsciiString) aseq = adm.Read();

      if(!aseq.IsNull())
	{
	  Standard_Integer i;

	  for(i=1; i<=aseq->Length(); i++)
	    {
	      Handle(TCollection_HAsciiString) paramname = new TCollection_HAsciiString("%");
	      Handle(TCollection_HAsciiString) paramval;
	      
	      paramname->AssignCat(aseq->Value(i));

	      paramval = Params().Eval(paramname->ToCString());

	      if(paramval.IsNull())
		{
		  WarningMsg() << "WOKernel_DevUnit::ImplementationDepList" 
			   << "Could not find DYNAMICPK definition for " << paramname 
			   << " referenced in " << intern->Path()->Name() << endm;
		  
		}
	      else
		{
		  Handle(WOKernel_DevUnit) unit = agraph->Locator()->LocateDevUnit(paramval);
		  
		  if(unit.IsNull())
		    {
		      WarningMsg() << "WOKernel_DevUnit::ImplementationDepList" 
				 << "Could not find unit " << paramval 
				 << " referenced in " << intern->Path()->Name() << endm;
		    }
		  else
		    {
		      unitDep->Append(unit->Name()); 
		    }
		}
	    }
	}
    }

  result = unitDep;
  
  return result;
}

//=======================================================================
//function : ImplementationDep
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_DevUnit::ImplementationDepList(const Handle(TCollection_HAsciiString)& aname,
										const Handle(WOKernel_UnitGraph)& agraph) 
{
  Handle(TCollection_HAsciiString)            impldepfilename, filename;
  Handle(TCollection_HAsciiString)            stadmtype = new TCollection_HAsciiString("stadmfile");
  Handle(WOKernel_File)                       impldepfile;
  Handle(WOKernel_DevUnit)                    aunit;
  Handle(TColStd_HSequenceOfHAsciiString)     result,tmp = new TColStd_HSequenceOfHAsciiString;
  Standard_Boolean                            depError = Standard_False;
  
  if(!agraph->Contains(aname))
    {
      static char                                 anamebuf[READBUF_SIZE];
      
      impldepfilename = Params().Eval("%FILENAME_IMPLDEP");
      
      if(impldepfilename.IsNull()) {
	ErrorMsg() << "WOKernel_DevUnit::ImplementationDep" << "Could not eval parameter : FILENAME_IMPLDEP" << endm;
	return result;
      }

      anamebuf[0] = '\0';
      
      
      filename = new TCollection_HAsciiString(aname);
      filename->AssignCat(".");
      filename->AssignCat(impldepfilename);
      
      impldepfile = agraph->Locator()->Locate(Name(), stadmtype, filename);
      
      if (impldepfile.IsNull()) 
	{
	  Standard_Boolean mustExist = SearchInFileList(agraph->Locator(),filename);
	  if (mustExist)
	    {
	      ErrorMsg() << "WOKernel_DevUnit::ImplementationDepList" 
		       << "Implementation Dependences not found for " << aname << endm;
	      ErrorMsg() << "WOKernel_DevUnit::ImplementationDepList" 
		       << "Perhaps " << aname << " is not compiled on this platform" << endm;
	      return result;
	    }
	  else
	    {
	      WarningMsg() << "WOKernel_DevUnit::ImplementationDepList" 
		         << "Could not determine Implementation Dependences for " << aname << endm;
	      result = new TColStd_HSequenceOfHAsciiString;
	    }
	  
	}

      else {
	Handle(TCollection_HAsciiString) astr;
	ifstream astream(impldepfile->Path()->Name()->ToCString());
	
	*anamebuf = '\0';
	while(astream >> setw(READBUF_SIZE) >> anamebuf)
	  {
	    if(strcmp(anamebuf, Name()->ToCString()))
	      {
		astr = new TCollection_HAsciiString(anamebuf);
		
		aunit = agraph->Locator()->LocateDevUnit(astr);
		
		if(aunit.IsNull())
		  {
		    WarningMsg() << "WOKernel_Executable::ImplementationDep" 
			       << "Wrong or not visible entry " << astr << " in implementation dep of " << Name() << endm;
		    depError = Standard_True;
		  }
		else
		  {
		    tmp->Append(astr);
		  }
	      }
	    
	    *anamebuf = '\0';
	  }
	if(!depError)
	  {
	    agraph->Add(aname, tmp);
	    result = tmp;
	  }
      }
    }
  else
    {
      result = agraph->Suppliers(aname);
    }
  return result;
}


//=======================================================================
//function : ImplementationDep
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_DevUnit::ImplementationDep(const Handle(WOKernel_UnitGraph)& agraph,
									    const Handle(TCollection_HAsciiString)& name,
									    const Handle(TColStd_HSequenceOfHAsciiString)& alist)
{
  Handle(TColStd_HSequenceOfHAsciiString)  result = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString)  NULLRESULT;
  Standard_Boolean                         IsCyclic;
  WOKernel_SortedImpldepFromIterator       algo;
  Standard_Integer                         i;

  try {
    OCC_CATCH_SIGNALS

    IsCyclic = Standard_False;

    agraph->Add(name,alist); 
    
    algo.FromVertex(name);
    algo.Perform(agraph);
    
    while(algo.More())
      {      
	if(algo.NbVertices() > 1)
	  {
	    ErrorMsg() << "WOKernel_DevUnit::ImplementationDep"
	             << "Cyclic dependency detected between: ";
	    
	    for(i=1; i<= algo.NbVertices(); i++)
	      {
		ErrorMsg() << algo.Value(i) << " ";
	      }
	    
	    ErrorMsg() << endm;
	    
	    IsCyclic = Standard_True;
	  }
	
	else
	  result->Prepend(algo.Value(1));
	
	algo.Next();
      }
    
    if(IsCyclic )
      return NULLRESULT;
    else
      return result;
  }

  catch (Standard_Failure )  
    { 
      Handle(Standard_Failure) E = Standard_Failure::Caught();	
      ErrorMsg() << "WOKernel_DevUnit::ImplementationDep" << "Exception was raised : " << E->GetMessageString() << endm;
      return NULLRESULT ;
    }
  return NULLRESULT;

}

//=======================================================================
//function : ImplementationDep
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_DevUnit::ImplementationDep(const Handle(WOKernel_UnitGraph)& agraph) 
{
  Handle(TColStd_HSequenceOfHAsciiString) tmp;

  tmp = ImplementationDepList(agraph);

  if (tmp.IsNull()) 
    return tmp;
  else
    return WOKernel_DevUnit::ImplementationDep(agraph, Name(), tmp);
}

//=======================================================================
//function : ImplementationDep
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_DevUnit::ImplementationDep(const Handle(TCollection_HAsciiString)& apart,
									       const Handle(WOKernel_UnitGraph)& agraph)
{
  Handle(TCollection_HAsciiString)        aname;
  Handle(TColStd_HSequenceOfHAsciiString) tmp;

  aname = new TCollection_HAsciiString(Name());
  aname->AssignCat("_");
  aname->AssignCat(apart);

  tmp = ImplementationDepList(aname, agraph);

  if(!tmp.IsNull())
    {
      return WOKernel_DevUnit::ImplementationDep(agraph, aname, tmp);
    }
  else return tmp;
}


//=======================================================================
//function : ImplClients
//purpose  : perform an algorithm for clients search
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_DevUnit::ImplClients(const Handle(WOKernel_UnitGraph)& aclientgraph)
{
  Handle(TColStd_HSequenceOfHAsciiString)  result = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString)  NULLRESULT;
  Standard_Boolean                         IsCyclic = Standard_False;
  WOKernel_SortedClientsFromIterator       algo;
  Standard_Integer                         i;

  try {
    OCC_CATCH_SIGNALS

    IsCyclic = Standard_False;

    algo.FromVertex(Name());
    algo.Perform(aclientgraph);
    
    while(algo.More())
      {      
	if(algo.NbVertices() > 1)
	  {
	    ErrorMsg() << "WOKernel_DevUnit::ImplClients"
	             << "Cyclic dependency detected between: ";
	    
	    for(i=1; i<= algo.NbVertices(); i++)
	      {
		ErrorMsg() << algo.Value(i) << " ";
	      }
	    
	    ErrorMsg() << endm;
	    
	    IsCyclic = Standard_True;
	  }
	
	else
	  result->Prepend(algo.Value(1));
	
	algo.Next();
      }
    
    if(IsCyclic)
      return NULLRESULT;
    else
      return result;
  }

  catch (Standard_Failure )  
    { 
      Handle(Standard_Failure) E = Standard_Failure::Caught();	
      ErrorMsg() << "WOKernel_DevUnit::ImplClients" << "Exception was raised : " << E->GetMessageString() << endm;
      return NULLRESULT ;
    }
   return NULLRESULT ;
}



