// File:	WOKStep_TKReplace.cxx
// Created:	Thu Aug  8 14:33:43 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HArray2OfBoolean.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_IndexedMapOfHAsciiString.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_UnitGraph.hxx>

#ifndef WNT
# include <WOKBuilder_ArchiveLibrary.hxx>
# include <WOKBuilder_SharedLibrary.hxx>
#else
# include <WOKNT_WNT_BREAK.hxx>
# include <WOKBuilder_StaticLibrary.hxx>
# include <WOKBuilder_ImportLibrary.hxx>
#endif // WNT

#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_Command.hxx>

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>
#include <WOKMake_HSequenceOfStepOption.hxx>


#include <WOKStep_TKReplace.ixx>

//=======================================================================
//function : WOKStep_TKReplace
//purpose  : 
//=======================================================================
WOKStep_TKReplace::WOKStep_TKReplace(const Handle(WOKMake_BuildProcess)& abp,
				     const Handle(WOKernel_DevUnit)& aunit, 
				     const Handle(TCollection_HAsciiString)& acode, 
				     const Standard_Boolean checked, 
				     const Standard_Boolean hidden) 
  : WOKStep_LinkList(abp,aunit, acode, checked, hidden), myauthlist(Standard_False)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_TKReplace::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_TKReplace::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;   
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_TKReplace::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
      switch(apath->Extension())
	{
#ifndef WNT
	case WOKUtils_ArchiveFile:  
	  result = new WOKBuilder_ArchiveLibrary(apath); break;
	case WOKUtils_DSOFile:  
	  result = new WOKBuilder_SharedLibrary(apath); break;
#else
	case WOKUtils_LIBFile:
	  result = new WOKBuilder_StaticLibrary(apath); break;
	case WOKUtils_IMPFile:
	  result = new WOKBuilder_ImportLibrary(apath); break;
	case WOKUtils_RESFile:
#endif // WNT
	case WOKUtils_ObjectFile:  
	  result = new WOKBuilder_ObjectFile(apath); break;
	default:  
	  return Standard_False;
	}
      
      infile->SetBuilderEntity(result);
      infile->SetDirectFlag(Standard_True);
      return Standard_True;
    }  
  else
    {
      if(!infile->IsPhysic()) return Standard_True;
    }
  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : LoadTKDefs
//purpose  : 
//=======================================================================
void WOKStep_TKReplace::LoadTKDefs()
{
  // Liste des Uds connues 

  Handle(WOKernel_Workbench)              abench = Unit()->Session()->GetWorkbench(Unit()->Nesting());
  Handle(TColStd_HSequenceOfHAsciiString) aseq = abench->Visibility();
  Handle(TColStd_HSequenceOfHAsciiString) units;
  Handle(TCollection_HAsciiString)        aunitname;
  Handle(TCollection_HAsciiString)        afullname, abasename;
  Standard_Integer                        i,j;
#if 0  
  Handle(WOKernel_File) toolkits = Locator()->Locate(Unit()->Name(),
						     new TCollection_HAsciiString("source"), 
						     new TCollection_HAsciiString("TOOLKITS"));
#else
  Handle(WOKernel_File) toolkits;
#endif

  if(toolkits.IsNull())
    {

      for(i=1; i<=aseq->Length() && toolkits.IsNull(); i++)
	{
	  abench = Unit()->Session()->GetWorkbench(aseq->Value(i));
	  
	  if(!abench.IsNull())
	    {
	      toolkits = new WOKernel_File(new TCollection_HAsciiString("TOOLKITS"),
					  abench,
					  abench->GetFileType("admfile"));
	      toolkits->GetPath();
	      if(!toolkits->Path()->Exists())
		{
		  toolkits.Nullify();
		}
	    }
	}
    }

  if(!toolkits.IsNull())
    {

      WOKUtils_AdmFile afile(toolkits->Path());
      Handle(TColStd_HSequenceOfHAsciiString) aseq = afile.Read();
      
      if(!aseq.IsNull())
	{
	  for(i=1; i<=aseq->Length(); i++)
	    {
	      aseq->Value(i)->LeftAdjust();
	      aseq->Value(i)->RightAdjust();
	      myautorized.Add(aseq->Value(i));
	    }
	  myauthlist=Standard_True;
	}
      else
	{
	  WarningMsg() << "WOKStep_TKReplace::Execute" 
		     << "Unreadable TOOLKITS file not taken into account" << endm;
	}
    }

  for(i=1; i<=aseq->Length(); i++)
    {
      Handle(WOKernel_UnitNesting) nesting = Unit()->Session()->GetUnitNesting(aseq->Value(i));
      if(!nesting.IsNull())
	{
	  nesting->Open();
	  
	  units = nesting->Units();
      
	  for(j=1; j<=units->Length(); j++)
	    {
#ifdef WNT
	      _TEST_BREAK();
#endif  // WNT
	      Handle(WOKernel_DevUnit) aunit;

	      aunit = Unit()->Session()->GetDevUnit(units->Value(j));
	      
	      if(!aunit.IsNull())
		{
		  aunitname = aunit->Name();
	      
		  if(WOKernel_IsToolkit(aunit))
		    {
		      if(!mytks.Contains(aunitname))
			{
			  mytks.Add(aunitname);
			}
		}
		  else
		    {
		      if(!myuds.Contains(aunitname))
			{
			  myuds.Add(aunitname);
			}
		    }
		}
	    }
	}
    }


  // Construction de la matrice TK uds.

  if(mytks.Extent() )
    {
      Handle(TCollection_HAsciiString) pkgstype = new TCollection_HAsciiString("PACKAGES");
      Handle(TCollection_HAsciiString) PACKAGESname = Unit()->Params().Eval("%FILENAME_PACKAGES");

      mymatrix = new TColStd_HArray2OfBoolean(1,mytks.Extent(), 1,myuds.Extent(), Standard_False);

      for(i=1; i<=mytks.Extent(); i++)
	{
	  const Handle(TCollection_HAsciiString)& atk = mytks(i);
	  Handle(WOKernel_File) PACKAGES = Locator()->Locate(atk, pkgstype, PACKAGESname);

	  if(PACKAGES.IsNull()) 
	    {
	      if (IsAuthorized(atk)) {
		WarningMsg() << "WOKStep_TKReplace::Execute" 
		  << "Could not find PACKAGES file for toolkit : " << atk << endm;
		WarningMsg() << "WOKStep_TKReplace::Execute" 
		  << "Toolkit " << atk << "is ignored" << endm;
	      }
	    }
	  else
	    {
	      WOKUtils_AdmFile afile(PACKAGES->Path());
	      Handle(TColStd_HSequenceOfHAsciiString) udsoftk;
	  
	      udsoftk = afile.Read();

	      if(udsoftk.IsNull())
		{
		  ErrorMsg() << "WOKStep_TKReplace::Execute" 
			   << "Could not read file " << PACKAGES->Path()->Name() << endm;
		  SetFailed();
		  return;
		}
	  
	      for(j=1; j<=udsoftk->Length(); j++)
		{
		  Standard_Integer udidx = myuds.FindIndex(udsoftk->Value(j));

		  if(udidx == 0)
		    {
		      ErrorMsg() << "WOKStep_TKReplace::Execute" 
			       << "Unknown unit (" << udsoftk->Value(j) << ") listed in packages of : " << atk << endm;
		      SetFailed();
		      return;
		    }
		  else
		    mymatrix->SetValue(i,udidx,Standard_True);
		}
	    }
	} 
    }

#ifdef WNT
 if (  !mymatrix.IsNull ()  ) {

  // if faut enlever le tk qui contient eventuellement l'ud en cours de construction
  if(!WOKernel_IsToolkit(Unit()))
    {
      Standard_Integer udidx = myuds.FindIndex(Unit()->Name());
      
      myauthlist=Standard_True;
      
      for(Standard_Integer tkidx=mymatrix->LowerRow(); tkidx<=mymatrix->UpperRow(); tkidx++)
	{
	  const Handle(TCollection_HAsciiString)& atk = mytks(tkidx);
	  if(!mymatrix->Value(tkidx, udidx))
	    myautorized.Add(atk);
	  else if(myautorized.Contains(mytks(tkidx)))
	    myautorized.Remove(atk);
	}
    }
  else
    {
      // Il faut enlever tous les tks qui contiennent les 
      // Uds de ce TK
      Standard_Integer curtk = mytks.FindIndex(Unit()->Name());
      myauthlist=Standard_True;
      
      // A priori Authoriser tous les toolkits
      for(Standard_Integer i=1; i<=mytks.Extent(); i++)
	{
	  if(i!=curtk)
	    {
	      myautorized.Add(mytks(i));
	    }
	}

      for(Standard_Integer udidx=mymatrix->LowerCol(); udidx<=mymatrix->UpperCol(); udidx++)
	{
	  if(mymatrix->Value(curtk, udidx))
	    {
	      // Cette ud est dans le TK en cours 
	      for(Standard_Integer tkidx=mymatrix->LowerRow(); tkidx<=mymatrix->UpperRow(); tkidx++)
		{
		  const Handle(TCollection_HAsciiString)& atk = mytks(tkidx);
		  if(mymatrix->Value(tkidx, udidx))
		    myautorized.Remove(atk);
		}
	    }
	}
    }
 }  // end if
#endif
}

//=======================================================================
//function : IsAuthorized
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_TKReplace::IsAuthorized(const Handle(TCollection_HAsciiString)& atk) const
{
  if(!myauthlist) return Standard_True;
  else
    return myautorized.Contains(atk); 
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetTKForUnit
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_TKReplace::GetTKForUnit(const Handle(TCollection_HAsciiString)& aunit)
{
  Handle(TCollection_HAsciiString) result;

  if(mytks.Extent())
    {
      Standard_Integer i, uidx = myuds.FindIndex(aunit);
      if ( uidx == 0 ) { 
	// aunit  is not in the list -> certainly a toolkit ; so return aunit
	result = aunit ;
      } else {

      for(i=mymatrix->LowerRow(); i<=mymatrix->UpperRow(); i++)
	{
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
	  if(mymatrix->Value(i, uidx))
	    {
	      if(!result.IsNull())
		{
		  WarningMsg() << "WOKStep_TKReplace::GetTKForUnit"
			     << "More than one toolkit contains " << aunit << " using " << result << " ignoring " << mytks(i) << endm;
#if 0
		  WarningMsg() << "WOKStep_TKReplace::GetTKForUnit"
			     << "You can specify the toolkit used using a TOOLKITS file" << endm;
#endif
		}
	      else
		{
		  const Handle(TCollection_HAsciiString)& astr = mytks(i);
		  if(IsAuthorized(astr))
		    result = astr;
		}
	    }
	}
      }

    }

  return result;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : SubstituteInput
//purpose  : 
//=======================================================================
Handle(WOKMake_OutputFile) WOKStep_TKReplace::SubstituteInput(const Handle(WOKMake_InputFile)& afile)
{
#ifndef WNT
  Standard_Integer k,l;
  Handle(WOKMake_OutputFile) result,NULLRESULT;

  if(afile->BuilderEntity()->IsKind(STANDARD_TYPE(WOKBuilder_SharedLibrary)))
    {
      Handle(WOKernel_DevUnit) TheUnit = Unit()->Session()->GetDevUnit(afile->File()->Nesting());
      
      Handle(TCollection_HAsciiString) current = TheUnit->Name();

      mytreated.Add(current);
      Handle(TCollection_HAsciiString) curtk = GetTKForUnit(current);
      if(!curtk.IsNull())
	{
	  // gerer l'apparition de ce TK
	  Standard_Integer tkidx = mytks.FindIndex(curtk);

	  for(k=mymatrix->LowerCol(); k<=mymatrix->UpperCol(); k++)
	    {
	      if(mymatrix->Value(tkidx, k))
		{
		  Handle(TCollection_HAsciiString) implied = myuds(k);

		  if(!mytreated.Contains(implied) && !myorig.Contains(implied))
		    {
		      Handle(WOKernel_DevUnit) aunit = Locator()->LocateDevUnit(implied);

		      

		      Handle(TColStd_HSequenceOfHAsciiString) directlist = aunit->ImplementationDepList(UnitGraph());

		      if (directlist.IsNull()) return NULLRESULT;

		      Handle(TColStd_HSequenceOfHAsciiString) suppliers = ComputeDependency(aunit->Name(), directlist);

		      if (suppliers.IsNull()) return NULLRESULT;
		      
		      for(l=1; l<=suppliers->Length(); l++)
			{
			  implied = suppliers->Value(l);
			  
			  // J'ajoute les EXTERNLIB des packages induits 
			  ComputeExternals(implied);

			  if(!mytreated.Contains(implied) && !myorig.Contains(implied))
			    {
			      mytreated.Add(implied);
			      Handle(WOKernel_DevUnit)   impunit = Locator()->LocateDevUnit(implied);
			      Handle(WOKMake_OutputFile) unitlib = GetUnitLibrary(impunit);
			      
			      if (Status()==WOKMake_Failed) return NULLRESULT;

			      if (!unitlib.IsNull()) 
				{
				  Handle(WOKMake_InputFile)  unitinlib = new WOKMake_InputFile(unitlib);
				  unitinlib->SetDirectFlag(Standard_False);
				  
				  Handle(WOKMake_OutputFile) outfile = SubstituteInput(unitinlib);
				  if (outfile.IsNull()) return NULLRESULT;
				  outfile->SetExtern();
				  
				  Handle(WOKernel_DevUnit) implsubs = Unit()->Session()->GetDevUnit(outfile->File()->Nesting());
				  
				  if(!mydirecttks.Contains(implsubs->Name()) && !myorig.Contains(implsubs->Name()))
				    {
				      if(!myadded.Contains(implsubs->Name()))
					{
					  if(myorig.Contains(current))
					    {
					      WarningMsg() << "WOKStep_TKReplace::SubstituteInput"
						<< implied << " (implied by " << curtk << " defining " << current << ")"
						  << " implies addition of " << outfile->ID() << endm;
					    }
					  myadded.Add(implsubs->Name());
					  AddExecDepItem(afile, outfile, Standard_True);
					}
				      else
					{
					  AddExecDepItem(afile, outfile, Standard_False);
					}
				    }
				} 
			    }
			}
		    }
		}
	    }

	  Handle(WOKernel_DevUnit) TKUnit = Locator()->LocateDevUnit(curtk);

	  // Check de visibilite : unit doit etre "plus loin" que tk

	  Handle(TCollection_HAsciiString) tknest = TKUnit->Nesting();
	  Handle(TCollection_HAsciiString) unitnest = TheUnit->Nesting();
	  if (Unit()->Session()->IsWorkbench(unitnest)) {
	    Handle(WOKernel_UnitNesting) thetknest = Unit()->Session()->GetUnitNesting(tknest);
	    Handle(WOKernel_Workbench) theunitwb = Unit()->Session()->GetWorkbench(unitnest);
	    
	    if (thetknest != theunitwb) {
	      Handle(TColStd_HSequenceOfHAsciiString) thevisib = theunitwb->Visibility();
	      for (Standard_Integer i=2; i<= thevisib->Length(); i++) {
		if (!strcmp(thevisib->Value(i)->ToCString(), thetknest->FullName()->ToCString())) {
		  WarningMsg() << "WOKStep_TKReplace::SubstituteInput"
		    << "Toolkit " << TKUnit->Name() << " in " << thetknest->Name()
		      << " hides unit " << TheUnit->Name() << " in " << theunitwb->Name() << endm;
		}
	      }
	    }
	  }
	      
	  result = GetUnitLibrary(TKUnit);

	  if (Status()==WOKMake_Failed) return NULLRESULT;
	  
	  if (!result.IsNull())
	    result->SetExtern();
	  else 
	    {
	      result = new WOKMake_OutputFile(afile);
	      result->SetReference();
	    }
	  return result;
	}
      else
	{
	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(afile);
	  outfile->SetReference();
	  return outfile;
	}
    }
  else
    {
      result = new WOKMake_OutputFile(afile);
      result->SetReference();
      result->SetMember();
    }
  return result;
#else
  Handle( WOKMake_OutputFile ) result;

  if (   afile -> BuilderEntity () -> IsKind (  STANDARD_TYPE( WOKBuilder_StaticLibrary )  )   ) {
  
   Handle( WOKernel_DevUnit ) TheUnit = Unit () -> Session () -> GetDevUnit (
                                                                  afile -> File () -> Nesting ()
                                                                 );
   Handle( TCollection_HAsciiString ) current = TheUnit -> Name ();
   Handle( TCollection_HAsciiString ) curtk = GetTKForUnit ( current );
   Handle( TCollection_HAsciiString ) libname = curtk.IsNull () ? current : curtk;

   if (  !mytreated.Contains ( libname )  ) {
   
    Handle( WOKernel_DevUnit   ) impunit = Locator () -> LocateDevUnit ( libname );

    result = GetUnitLibrary ( impunit );

    if (  !result.IsNull ()  ) 
      {
	ComputeExternals(impunit->Name());
	AddExecDepItem ( afile, result, Standard_True );
      }
   
   }  // end if
     
  } else {
  
   result = new WOKMake_OutputFile ( afile );
   result -> SetReference ();
  
  }  // end else

  return result;
#endif  // WNT
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_TKReplace::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Standard_Integer i;

  LoadTKDefs();

  for(i=1; i<=execlist->Length(); i++)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
      Handle(WOKMake_InputFile) afile = execlist->Value(i);

      if(afile->IsPhysic())
	{
#ifndef WNT
	  if(afile->BuilderEntity()->IsKind(STANDARD_TYPE(WOKBuilder_SharedLibrary)))
#else
	  if(afile->BuilderEntity()->IsKind(STANDARD_TYPE(WOKBuilder_StaticLibrary)))
#endif  // WNT
	    {
	      Handle(WOKernel_DevUnit) TheUnit = Unit()->Session()->GetDevUnit(afile->File()->Nesting());
	      Handle(TCollection_HAsciiString) atk = TheUnit->Name() ;
	      if(!atk.IsNull())
		{
		  mydirecttks.Add(atk);
		}
		  
	    }
	}
    }

  for(i=1; i<=execlist->Length(); i++)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
      Handle(WOKMake_InputFile) afile = execlist->Value(i);
      if(afile->IsPhysic())
	{
#ifndef WNT
	  if(afile->BuilderEntity()->IsKind(STANDARD_TYPE(WOKBuilder_SharedLibrary)))
#else
	  if(afile->BuilderEntity()->IsKind(STANDARD_TYPE(WOKBuilder_StaticLibrary)))
#endif  // WNT
	    {
	      Handle(WOKernel_DevUnit) TheUnit = Unit()->Session()->GetDevUnit(afile->File()->Nesting());
	      if(!myorig.Contains(TheUnit->Name()))
		{
		  myorig.Add(TheUnit->Name());
		}
	    }
	}
    }
  
  if(!CheckStatus("LoadTkDefs"))
    {
      
      for(i=1; i<=execlist->Length(); i++)
	{
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
	  Handle(WOKMake_InputFile) afile = execlist->Value(i);

	  if(afile->IsPhysic())
	    {
	      Handle(WOKMake_OutputFile) outfile = SubstituteInput(execlist->Value(i));
	      
	      if(!outfile.IsNull())
		{
		  AddExecDepItem(afile, outfile, Standard_True);
		}
	      else 
		{
		  SetFailed();
		  return;
		}
	    }
	  else
	    {
	      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(afile);
	      
	      outfile->SetReference();
	      AddExecDepItem(afile, outfile, Standard_True);
	    }
	}
    }
    
  if(!CheckStatus("LoadTkDefs"))
    {
      SetSucceeded();
    }

  myuds.Clear();
  mytks.Clear();

  myautorized.Clear();
  mydirecttks.Clear();
  mytreated.Clear();
  myorig.Clear();
  myadded.Clear();
  mymatrix.Nullify();
  return;
}


