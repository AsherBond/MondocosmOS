// File:	WOKStep_ProcessStep.cxx
// Created:	Mon Aug 18 15:41:22 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>
#include <Standard_Stream.hxx>
#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#ifdef WNT
#include <io.h>
#endif

#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HArray2OfInteger.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_Shell.hxx>

#ifndef WNT
# include <WOKUtils_RemoteShell.hxx>
#else
# include <WOKUtils_Shell.hxx>
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_HSequenceOfFile.hxx>
#include <WOKernel_Locator.hxx>


#include <WOKBuilder_HSequenceOfToolInShell.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_Entity.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfStepOption.hxx>
#include <WOKMake_AdmFileTypes.hxx>


#include <WOKStep_ProcessStep.ixx>

#include <WOKUtils_AdmFile.hxx>
#include <Standard_PCharacter.hxx>

#define READBUF_SIZE 1024

#ifndef WNT
#define CHECK_REMOTE 1
#endif // WNT

//#ifdef HAVE_IOMANIP_H
//# include <iomanip.h>
//#endif

//=======================================================================
//function : WOKStep_ProcessStep
//purpose  : 
//=======================================================================
WOKStep_ProcessStep::WOKStep_ProcessStep(const Handle(WOKMake_BuildProcess)& abp,
				 const Handle(WOKernel_DevUnit)& aunit, 
				 const Handle(TCollection_HAsciiString)& acode, 
				 const Standard_Boolean checked, 
				 const Standard_Boolean hidden) 
: WOKMake_Step(abp,aunit, acode, checked, hidden)
{
}

//=======================================================================
//function : Init
//purpose  : 
//=======================================================================
void WOKStep_ProcessStep::Init()
{
  BuildProcess()->GetKnownUnits();
}


//=======================================================================
//function : MFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_ProcessStep::ImplDepFileName() const
{
  // le MyUd.MakeState
  Handle(TCollection_HAsciiString) aname  =  new TCollection_HAsciiString(Unit()->Name());
  if(!SubCode().IsNull())
    {
      aname->AssignCat("_");
      aname->AssignCat(SubCode());
    }
  aname->AssignCat(".");
  aname->AssignCat(Unit()->Params().Eval("%FILENAME_MAKESTATE"));
  return aname;
}

//=======================================================================
//function : GetUnitName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_ProcessStep::GetUnitName(const Handle(TCollection_HAsciiString)& aincpath) 
{
  WOKTools_MapOfHAsciiString& knownunits = BuildProcess()->KnownUnits();

  Handle(TCollection_HAsciiString) handleprefix = Unit()->Params().Eval("%FILENAME_HANDLEPREFIX");
  Handle(TCollection_HAsciiString) rejectlist   = Unit()->Params().Eval("%FILENAME_REJECTLIST");
  Handle(TCollection_HAsciiString) unitname;
  Standard_PCharacter ptr;
  static Standard_PCharacter unit = new char[1024];
  Standard_PCharacter unitptr;
  Standard_Integer apos;
  
  unitptr = unit;
  *unitptr = '\0';

  Handle(WOKUtils_Path) apath = new WOKUtils_Path(aincpath);
  Handle(TCollection_HAsciiString) basename = new TCollection_HAsciiString("|");
  basename->AssignCat(apath->FileName());
  basename->AssignCat("|");


  if(rejectlist->Search(basename) != -1) 
    {
      WOK_TRACE {
	VerboseMsg()("WOK_IDEP") << "WOKStep_ProcessStep::GetUnitName" 
			       << "Rejected include : " << aincpath << endm;
      }
      return unitname;
    }

  // Extraire le BaseName
  if((apos = aincpath->SearchFromEnd("/")) != -1)
    {
      ptr = (Standard_PCharacter)aincpath->ToCString() + apos;
    }
  else
    {
      ptr = (Standard_PCharacter)aincpath->ToCString();
    }


  // Supprimer Handle_
  if(!strncmp(ptr, handleprefix->ToCString(), handleprefix->Length()))
    {
      // Handle_*****
      ptr +=  handleprefix->Length();
    }

  // A partir d'ici : Tout est Ud 
  while(*ptr)
    {
      if(IsAlphanumeric(*ptr))
	{
	  *unitptr = *ptr;
	  unitptr++;
	}
      else
	{
	  break;
	}
      ++ptr;
    }

  *unitptr = '\0';

  unitname = new TCollection_HAsciiString(unit);

  if(!knownunits.Contains(unitname))
    {
      unitname.Nullify();
    }
  return unitname;
}


//=======================================================================
//function : GetInputFileFromPath
//purpose  : 
//=======================================================================
const Handle(WOKMake_InputFile)& WOKStep_ProcessStep::GetInputFileFromPath(const Handle(TCollection_HAsciiString)& thepath)
{
  static Handle(WOKMake_InputFile) NULLRESULT;
  WOKMake_IndexedDataMapOfHAsciiStringOfInputFile& makestate = BuildProcess()->MakeState();

  if(makestate.Contains(thepath)) return makestate.FindFromKey(thepath);
  else
    {
      Handle(TCollection_HAsciiString) pubinclude  = new TCollection_HAsciiString("pubinclude");
      Handle(TCollection_HAsciiString) privinclude = new TCollection_HAsciiString("privinclude");
      Handle(TCollection_HAsciiString) source      = new TCollection_HAsciiString("source");
      Handle(TCollection_HAsciiString) aunitname, abasename;
      Handle(WOKernel_File)     incfile;

      Handle(WOKMake_InputFile) result;
      aunitname = GetUnitName(thepath);
      
      if(!aunitname.IsNull())
	{
	  // unitname was found:  Include Cas.Cade
	  Handle(WOKUtils_Path) apath = new WOKUtils_Path(thepath);
	  abasename = apath->FileName();
	  if(aunitname->IsSameString(Unit()->Name()))
	    {
	      // l'include appartient a l'ud en cours : determiner son type
	      
	      if((incfile = Locator()->Locate(Unit()->Name(), pubinclude, abasename)).IsNull())
		{
		  if((incfile = Locator()->Locate(Unit()->Name(), privinclude, abasename)).IsNull())
		    {
		      incfile = Locator()->Locate(Unit()->Name(), source, abasename);
		    }
		}
	    }
	  else
	    {
	      incfile = Locator()->Locate(aunitname, pubinclude, abasename);
	    }
	  
	  if(!incfile.IsNull())
	    {
	      result = new WOKMake_InputFile(incfile->LocatorName(), incfile, Handle(WOKBuilder_Entity)(), incfile->Path());
	      
	      result->SetLocateFlag(Standard_True);
	      result->SetDirectFlag(Standard_False);

	      makestate.Add(thepath, result);
	      
	      return makestate(makestate.Extent());
	    }
	}
      
      if(result.IsNull())
	{
	  result = new WOKMake_InputFile(thepath, 
					 Handle(WOKernel_File)(), Handle(WOKBuilder_Entity)(), 
					 new WOKUtils_Path(thepath));
	  result->SetLocateFlag(Standard_False);
	  result->SetDirectFlag(Standard_False);
	  
	  makestate.Add(thepath, result);
	  
	  return makestate(makestate.Extent());
	  
	}
    }
  return NULLRESULT;
}

//=======================================================================
//function : TreatOutput
//purpose  : 
//=======================================================================
void WOKStep_ProcessStep::TreatOutput(const Handle(WOKMake_InputFile)& infile, const Handle(WOKBuilder_HSequenceOfEntity)& output)
{
  char buffer[READBUF_SIZE];

  Handle(WOKernel_FileType) srctype = Unit()->FileTypeBase()->Type("source");
  Handle(WOKernel_FileType) inctype = Unit()->FileTypeBase()->Type("pubinclude");
  Handle(WOKernel_FileType) drvtype = Unit()->FileTypeBase()->Type("derivated");
  Handle(WOKernel_FileType) objtype = Unit()->FileTypeBase()->Type("object");
  WOKMake_IndexedDataMapOfHAsciiStringOfOutputFile outmap;
  Handle(WOKBuilder_Entity) MFILE;

  for(Standard_Integer i=1; i<=output->Length(); i++)
    {
      Handle(WOKBuilder_Entity) outent = output->Value(i);
      Handle(WOKernel_File) aoutfile;
      Standard_Boolean istemplate = Standard_False;
	      
      WOKUtils_Extension extens = outent->Path()->Extension();

      switch(extens)
	{
	case WOKUtils_TemplateFile:
	  aoutfile = new WOKernel_File(outent->Path()->FileName(), Unit(), srctype);
	  istemplate = Standard_True;
	  break;
	case WOKUtils_CXXFile:
	  aoutfile = new WOKernel_File(outent->Path()->FileName(), Unit(), drvtype);
	  break;
	case WOKUtils_HXXFile:
	  aoutfile = new WOKernel_File(outent->Path()->FileName(), Unit(), inctype);
	  break;
	case WOKUtils_ObjectFile:
	  aoutfile = new WOKernel_File(outent->Path()->FileName(), Unit(), objtype);
	  break;
	case WOKUtils_MFile:
	  // MFiles are no longer output of steps
	  MFILE = outent;
	  break;
	default:
	  break;
	}
	      
      if(aoutfile.IsNull() && extens != WOKUtils_MFile)
	{
	  ErrorMsg() << "WOKStep_ProcessStep::Execute" 
		   << "Unrecognized file : " << outent->Path()->Name() << endm;
	}
      if(!aoutfile.IsNull())
	{
	  if(!outmap.Contains(aoutfile->LocatorName()))
	    {
	      Handle(WOKMake_OutputFile) outfile;
	      // je calcule le path de destination du file
	      aoutfile->GetPath();
//-> EUG4YAN
              TCollection_AsciiString s;
//              OSD_Path p (  outent -> Path () -> Name () -> ToCString ()  );
              TCollection_AsciiString atstr = outent -> Path () -> Name () -> ToCString () ;
              OSD_Path p ( atstr );
//<- EUG4YAN
	      // je l'y deplace
	      outent->Path()->MoveTo(aoutfile->Path());
//-> EUG4YAN
              if ( extens == WOKUtils_ObjectFile ) {

               p.SetExtension ( ".d" );
               p.SystemName   ( s );

               Handle( WOKUtils_Path ) pp = new WOKUtils_Path (
                                                 new TCollection_HAsciiString ( s )
                                                );

               if (  pp -> Exists ()  ) {

//                OSD_Path p (  aoutfile -> Path () -> Name () -> ToCString ()  );
                TCollection_AsciiString atstr =  aoutfile -> Path () -> Name () -> ToCString () ;
                OSD_Path p ( atstr );

                p.SetExtension ( ".d" );
                p.SystemName ( s );

                Handle( WOKUtils_Path ) pd = new WOKUtils_Path (
                                                  new TCollection_HAsciiString ( s )
                                                 );

                pp -> MoveTo ( pd );

               }  // end if

              }  // end if
//<- EUG4YAN
	      if(!istemplate)
		{
		  outfile = new WOKMake_OutputFile(aoutfile->LocatorName(), aoutfile, outent, aoutfile->Path());
		  outfile->SetLocateFlag(Standard_True);
		  outfile->SetProduction();
		  
		  AddExecDepItem(infile, outfile, Standard_True);
		  outmap.Add(aoutfile->LocatorName(), outfile);
		}
	      else
		{
		  outfile = new WOKMake_OutputFile(aoutfile->LocatorName(), aoutfile, outent, aoutfile->Path());
		  outfile->SetLocateFlag(Standard_True);
		  outfile->SetReference();
		  
		  AddExecDepItem(infile, outfile, Standard_True);
		  outmap.Add(aoutfile->LocatorName(), outfile);
		}
	    }
	}
    }

  if(!output.IsNull() && !MFILE.IsNull())
    {
      ifstream mfile(MFILE->Path()->Name()->ToCString());

      while(mfile >> setw(READBUF_SIZE) >> buffer)
	{
	  if(*buffer != '\0')
	    {
	      Handle(TCollection_HAsciiString) thefile = new TCollection_HAsciiString(buffer);
	     
	      const Handle(WOKMake_InputFile)& infile = GetInputFileFromPath(thefile);
	      
	      for(Standard_Integer i=1; i<=outmap.Extent(); i++)
		{
		  AddExecDepItem(infile, outmap(i), Standard_False);
		}
	    }
	}
      mfile.close();
      MFILE->Path()->RemoveFile();
    }
}

//=======================================================================
//function : ComputeIncDirectories
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath) WOKStep_ProcessStep::ComputeIncDirectories() const
{
  Handle(TColStd_HSequenceOfHAsciiString) nestingseq = Unit()->Session()->GetWorkbench(Unit()->Nesting())->Visibility();
  Handle(TCollection_HAsciiString) aname;
  Handle(WOKUtils_HSequenceOfPath) aseq = new WOKUtils_HSequenceOfPath;
  Handle(WOKernel_File)     afile;
  Handle(WOKernel_FileType) atype;
  Handle(WOKernel_DevUnit)  aunit;
  Handle(TCollection_HAsciiString) DOT = new TCollection_HAsciiString(".");
  WOKTools_MapOfHAsciiString amap;
  Standard_Integer i;
  Standard_Boolean usesrcdir = Standard_False;

  if(!Unit()->Params().Eval("%WOKSteps_UseSourceInclude").IsNull())
    {
      usesrcdir = Standard_True;
    }

  for(i=1; i<=nestingseq->Length(); i++)
    {
      Handle(WOKernel_UnitNesting) nesting = Unit()->Session()->GetUnitNesting(nestingseq->Value(i));
      //nesting de la visibilite
      atype = nesting->FileTypeBase()->Type("pubincdir");
      afile = new WOKernel_File(DOT, nesting, atype);
      afile->GetPath();
      if(!amap.Contains(afile->Path()->Name()))
	{
	  aseq->Append(afile->Path());
	  amap.Add(afile->Path()->Name());
	}

      // l'ud est-elle dans ce nesting
      aname = nesting->NestedUniqueName(Unit()->Name());
      if(Unit()->Session()->IsKnownEntity(aname))
	{
	  aunit = Unit()->Session()->GetDevUnit(aname);
	  
	  Handle(WOKernel_UnitNesting) anesting = Unit()->Session()->GetUnitNesting(aunit->Nesting());
	  if(anesting->IsKind(STANDARD_TYPE(WOKernel_Workbench)))
	    {
	      atype = aunit->FileTypeBase()->Type("privinclude");
	      afile = new WOKernel_File(DOT, aunit, atype);
	      afile->GetPath();

	      if(!amap.Contains(afile->Path()->Name()))
		{
		  aseq->Append(afile->Path());
		  amap.Add(afile->Path()->Name());
		}

	      if(usesrcdir)
		{
		  atype = aunit->FileTypeBase()->Type("userinclude");
		  
		  afile = new WOKernel_File(DOT, aunit, atype);
		  afile->GetPath();
		  
		  if(!amap.Contains(afile->Path()->Name()))
		    {
		      aseq->Append(afile->Path());
		      amap.Add(afile->Path()->Name());
		    }
		}
	    }
	}
    }

  DOT   = Unit () -> Params ().Eval ( "%FILENAME_FILES" );
  aname = new TCollection_HAsciiString ( "source" );
  afile = Locator () -> Locate (  Unit () -> Name (), aname, DOT  );

  if (  !afile.IsNull ()  ) {

   WOKUtils_AdmFile                          afiles (  afile -> Path ()  );
   Handle( TCollection_HAsciiString        ) p = new TCollection_HAsciiString ( "privinclude" );
   Handle( TColStd_HSequenceOfHAsciiString ) s;

   s = afiles.Read ();

   if (  !s.IsNull ()  )

    for (  i = 1; i <= s -> Length (); ++i  ) {

     Standard_Integer j;

     DOT = s -> Value ( i );
	  
     DOT -> LeftAdjust  ();
     DOT -> RightAdjust ();

     if (   (  j = DOT -> Search ( ":" )  ) != -1   ) {

      aname = DOT -> SubString ( 1, j - 1 );
      
      afile = Locator () -> Locate (  aname, p, new TCollection_HAsciiString ( "" )  );

      if (   !afile.IsNull () && !amap.Contains (  afile->Path () -> Name ()  )   ) {

       aseq -> Append (  afile -> Path ()  );
       amap.Add(  afile -> Path () -> Name ()  );

      }  // end if

     }  // end if
     
    }  // end for
   
  }  // end if

  return aseq;
}

//=======================================================================
//function : ComputeDatabaseDirectories
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath) WOKStep_ProcessStep::ComputeDatabaseDirectories() const
{
  Handle(WOKUtils_HSequenceOfPath) aseq;

   if(Unit()->Params().Eval("%WOKSteps_UseDatabaseDirectory").IsNull())
    {
      return aseq;
    }

  Handle(TColStd_HSequenceOfHAsciiString) nestingseq = Unit()->Session()->GetWorkbench(Unit()->Nesting())->Visibility();
  Handle(TCollection_HAsciiString) aname;
  Handle(WOKernel_File)     afile;
  Handle(WOKernel_FileType) atype;
  Handle(WOKernel_DevUnit)  aunit;
  Handle(TCollection_HAsciiString) DOT = new TCollection_HAsciiString(".");
  WOKTools_MapOfHAsciiString amap;
  Standard_Integer i;

  aseq = new WOKUtils_HSequenceOfPath;

  for(i=1; i<=nestingseq->Length(); i++)
    {
      Handle(WOKernel_UnitNesting) nesting = Unit()->Session()->GetUnitNesting(nestingseq->Value(i));
      // les wbs uniquement
      if(nesting->IsKind(STANDARD_TYPE(WOKernel_Workbench))) {
	atype = nesting->FileTypeBase()->Type("libdir");
	afile = new WOKernel_File(DOT, nesting, atype);
	afile->GetPath();
	if(!amap.Contains(afile->Path()->Name()))
	  {
	    aseq->Append(afile->Path());
	    amap.Add(afile->Path()->Name());
	  }
      }
    }

  return aseq;
}
