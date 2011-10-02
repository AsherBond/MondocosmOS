// File:	WOKernel_Workshop.cxx
// Created:	Wed Jul 26 15:20:50 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>

#include <WOKernel_Workshop.ixx>

#include <WOKernel_Parcel.hxx>
#include <WOKernel_Warehouse.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_ParamItem.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_AdmFile.hxx>

#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>

#include <OSD_File.hxx>
#include <OSD_Protection.hxx>

//=======================================================================
//function : WOKernel_Workshop
//purpose  : instantiates a Workshop (does not open It
//=======================================================================
WOKernel_Workshop::WOKernel_Workshop(const Handle(TCollection_HAsciiString)& aname, 
				     const Handle(WOKernel_Factory)& anesting) 
: WOKernel_Entity(aname, anesting)
{
}

//=======================================================================
//function : BuildParameters
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKernel_Workshop::BuildParameters(const Handle(WOKUtils_HSequenceOfParamItem)& someparams,
									 const Standard_Boolean usedefaults) 
{
  Handle(WOKUtils_HSequenceOfParamItem) resparams = WOKernel_Entity::BuildParameters(someparams, usedefaults);
  TCollection_AsciiString nameparam = "%";
  nameparam += Name()->ToCString();
  nameparam += "_UseConfig";

  Standard_Boolean found = Standard_False;
  for (Standard_Integer i=1; (i<= someparams->Length()) && !found; i++) {
    if (!strcmp(someparams->Value(i).Name()->ToCString(),nameparam.ToCString())) {
      InfoMsg() << "WOKernel_Workshop::BuildParameters"
	<< "Use configuration " << someparams->Value(i).Value() << endm;
      resparams->Append(someparams->Value(i));
      WOKUtils_ParamItem aparam("%ShopName",Name()->ToCString());
      resparams->Append(aparam);
      found = Standard_True;
    }
  }
  return resparams;
}

//=======================================================================
//function : GetParameters
//purpose  : 
//=======================================================================
void WOKernel_Workshop::GetParameters()
{
  Standard_Integer i;
  Handle(TColStd_HSequenceOfAsciiString) aseq;
  Handle(TColStd_HSequenceOfAsciiString) subclasses = new TColStd_HSequenceOfAsciiString;
  Handle(TColStd_HSequenceOfAsciiString) dirs       = new TColStd_HSequenceOfAsciiString;
  Handle(TCollection_HAsciiString) astr;
  Handle(TCollection_HAsciiString) libdir;
  Handle(WOKernel_Entity) entity;

  if(!Nesting().IsNull()) {

    entity = Session()->GetEntity(Nesting());

    aseq = entity->Params().SubClasses();
    if(!aseq.IsNull()) {
      for(i=1; i<=aseq->Length(); i++) {
	subclasses->Append(aseq->Value(i));
      }
    }

    aseq =  entity->Params().SearchDirectories();
    if(!aseq.IsNull()) {
      for(i=1; i<=aseq->Length(); i++) {
	dirs->Append(aseq->Value(i));
      }
    }


    
    // on recupere les ADM et les classes des parcels

    Handle(WOKernel_Factory)   afact = Session()->GetFactory(Nesting());


    if(!afact->Warehouse().IsNull()) {
      Handle(WOKernel_Warehouse) aware = Session()->GetWarehouse(afact->Warehouse());
      aware->Open();

      // on recupere l'ADM du WareHouse      
      astr = aware->EvalParameter("Adm", Standard_False);

      // subclasses->Append(aware->Name()->String());
	 
      if (!astr.IsNull()) {
	dirs->Prepend(astr->ToCString());
	// TCollection_AsciiString& lastsub = subclasses->ChangeValue(subclasses->Length());
	//lastsub.AssignCat("@");
	//lastsub.AssignCat(astr->String());
      }
	 
      //ChangeParams().SetSubClasses(subclasses);
      ChangeParams().SetSearchDirectories(dirs);

      GetParcelsInUse();

      Handle(TColStd_HSequenceOfHAsciiString) parcels = ParcelsInUse();
      
      for (i=1; i<= parcels->Length(); i++) {
	entity = Session()->GetEntity(parcels->Value(i));
	if (!entity.IsNull()) {
	  
	  astr = entity->EvalParameter("Delivery", Standard_False);
	  if (!astr.IsNull()) {
	    subclasses->Append(astr->ToCString());
	  }
	  astr = entity->EvalParameter("Adm", Standard_False);
	  if (!astr.IsNull()) {
	    dirs->Prepend(astr->ToCString());
	    TCollection_AsciiString& lastsub = subclasses->ChangeValue(subclasses->Length());
	    lastsub.AssignCat("@");
	    lastsub.AssignCat(astr->String());
	  }
	}
      }
    }

    subclasses->Append(Name()->ToCString());
    // on evalue le ADM
    astr = EvalParameter("Adm", Standard_False);
      
    if(!astr.IsNull()) {
      dirs->Prepend(astr->ToCString());
      TCollection_AsciiString& lastsub = subclasses->ChangeValue(subclasses->Length());
      lastsub.AssignCat("@");
      lastsub.AssignCat(astr->String());
    }
    
    ChangeParams().SetSubClasses(subclasses);
    ChangeParams().SetSearchDirectories(dirs);
  }
}

//=======================================================================
//function : EntityCode
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Workshop::EntityCode() const 
{
  static Handle(TCollection_HAsciiString) acode = new TCollection_HAsciiString("workshop");
  return acode;
}

//=======================================================================
//function : GetWorkbenches
//purpose  : 
//=======================================================================
void WOKernel_Workshop::GetWorkbenches()
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) astr;
  Handle(TCollection_HAsciiString) afather;
  Handle(TCollection_HAsciiString) awb;
  Handle(WOKernel_Workbench)       afatherwb;
  Handle(WOKernel_Workbench)       awbhandle;
  Handle(WOKernel_File)            wblistfile;
  Handle(TColStd_HSequenceOfHAsciiString)   aseq;

  wblistfile = new WOKernel_File(this, GetFileType("WorkbenchListFile"));
  if (!wblistfile->Name().IsNull()) {
    wblistfile->GetPath();

    WOKUtils_AdmFile  afile(wblistfile->Path());

    aseq = afile.Read();
    
    myworkbenches  = new TColStd_HSequenceOfHAsciiString;
    
    for(i=1; i <= aseq->Length() ; i++)
      {
	awb     = aseq->Value(i)->Token(" \t", 1);
	afather = aseq->Value(i)->Token(" \t", 2);
	
	if(afather->IsEmpty() == Standard_True)
	  {
	    // this is a root Workbench
	    afatherwb = Session()->GetWorkbench(NestedUniqueName(afather));
	    
	    if(afatherwb.IsNull())
	      {
		awbhandle = new WOKernel_Workbench(awb, this, Handle(WOKernel_Workbench)());
		myworkbenches->Append(awbhandle->FullName());
		Session()->AddEntity(awbhandle);
	      }
	  }
	else
	  {
	    // this is a son WB
	    
	    // Recherche du pere
	    afatherwb =  Session()->GetWorkbench(NestedUniqueName(afather));
	    if(afatherwb.IsNull())
	      {
		afatherwb = new WOKernel_Workbench(afather, this, afatherwb);
	      }
	    
	    // recherche du WB
	    awbhandle = Session()->GetWorkbench(NestedUniqueName(awb));
	    
	    if(!awbhandle.IsNull())
	      {
		awbhandle->SetFather(afatherwb);
	      }
	    else
	      {
		awbhandle = new WOKernel_Workbench(awb, this, afatherwb);
		myworkbenches->Append(awbhandle->FullName());
		Session()->AddEntity(awbhandle);
	      }
	  }
      }
  }
}

//=======================================================================
//function : GetParcelsInUse
//purpose  : 
//=======================================================================
void WOKernel_Workshop::GetParcelsInUse() 
{
  Handle(WOKernel_Factory)   afact = Session()->GetFactory(Nesting());
  Handle(WOKernel_Warehouse) aware = Session()->GetWarehouse(afact->Warehouse());
  Handle(WOKernel_Parcel)    aparcel;
  Handle(WOKernel_Entity)    anent;
  Handle(TCollection_HAsciiString) astr, parcellst, pname;
  Standard_Integer i=1;

  myparcelsinuse = new TColStd_HSequenceOfHAsciiString;

  parcellst = EvalParameter("ParcelConfig", Standard_False);

  if( !parcellst.IsNull() )
    {
      if(parcellst->UsefullLength())
	{
	  astr = parcellst->Token(" \t", i);

	  while(!astr->IsEmpty())
	    {
	      aparcel.Nullify();

	      pname = aware->NestedUniqueName(astr);

	      if(Session()->IsKnownEntity(pname))
		{
		  anent = Session()->GetEntity(pname);

		  if(anent->IsKind(STANDARD_TYPE(WOKernel_Parcel)))
		    {
		      aparcel = Handle(WOKernel_Parcel)::DownCast(anent);
		    }
		  else
		    {
		      ErrorMsg() << "WOKernel_Workshop::GetParcelsInUse"
			       << "Name " << astr << " is not a parcel name" << endm;
		    }
		}
	      else
		{
		  ErrorMsg() << "WOKernel_Workshop::GetParcelsInUse"
			   << "Name " << astr << " is unknown" << endm;
		}

	      if(!aparcel.IsNull())
		{
		  aparcel->Open();
		  myparcelsinuse->Append(aparcel->FullName());
		}
	      i++;
	      astr =  parcellst->Token(" \t", i);
	    }
	}
    }
  return;
}

//=======================================================================
//function : Open
//purpose  : opens the workshop to use it
//=======================================================================
void WOKernel_Workshop::Open()
{
  if(IsOpened()) return;
  {
    Reset();

    Handle(WOKernel_FileTypeBase) abase = new WOKernel_FileTypeBase;

    GetParams();

    // chargement de la base des types
    SetFileTypeBase(Session()->GetFileTypeBase(this));

    GetWorkbenches();

    Handle(WOKernel_Factory)   afact = Session()->GetFactory(Nesting());
    if(!afact->Warehouse().IsNull())
      {
	Handle(WOKernel_Warehouse) aware = Session()->GetWarehouse(afact->Warehouse());
	aware->Open();
	
	GetParcelsInUse();
      }
    else
      myparcelsinuse = new TColStd_HSequenceOfHAsciiString;

    SetOpened();
  }
}

//=======================================================================
//function : Close
//purpose  : closes the Workshop and sub-entities
//=======================================================================
void WOKernel_Workshop::Close()
{
  if(!IsOpened()) return;

  Handle(WOKernel_Workbench) abench;
  Standard_Integer i;

  for(i=1; i<=myworkbenches->Length(); i++)
    {
      abench = Session()->GetWorkbench(myworkbenches->Value(i));
      abench->Close();
      Session()->RemoveEntity(abench);
    }
  myworkbenches.Nullify();
  
  Reset();
  SetClosed();
}

//=======================================================================
//function : Worbenches
//purpose  : returns the list of Workbenches of workshop
//=======================================================================
 Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Workshop::Workbenches() const 
{
  return myworkbenches;
}

//=======================================================================
//function : ParcelsInUse
//purpose  : returns the list of parcels the workshop uses
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Workshop::ParcelsInUse() const 
{
  return myparcelsinuse;
}



//=======================================================================
//function : DumpWorkbenchList
//purpose  : Updates Workbench list
//=======================================================================
void WOKernel_Workshop :: DumpWorkbenchList () const {

 Standard_Integer                 i;
 Handle( WOKernel_File ) wblistfile;

 wblistfile = new WOKernel_File (  this, GetFileType ( "WorkbenchListFile" )  );

 wblistfile -> GetPath ();

 Handle( TCollection_HAsciiString ) aNewPath  = new TCollection_HAsciiString (
                                                     wblistfile -> Path () -> Name ()
                                                    );
 Handle( TCollection_HAsciiString ) anOldPath = new TCollection_HAsciiString ( aNewPath );

 aNewPath -> AssignCat ( ".bak" );

 wblistfile -> Path () -> MoveTo (
                           new WOKUtils_Path ( aNewPath )
                          );

 ofstream astream (  anOldPath -> ToCString (), ios :: out  );
  
 if ( !astream ) {

  ErrorMsg() << "WOKernel_Workshop::AddWorkbench"
           << "Could not open "
           << wblistfile -> Path () -> Name ()
           << endm;
  Standard_ProgramError :: Raise ( "WOKernel_Workshop::AddWorkbench" );

 }  // end if
  
 for (  i = 1; i <= myworkbenches -> Length (); ++i ) {

  Handle( WOKernel_Workbench ) abench =
   Session () -> GetWorkbench (  myworkbenches -> Value ( i )  );

  astream << abench -> Name () -> ToCString ();

  if (  !abench -> Father ().IsNull ()  ) {

   Handle( WOKernel_Workbench ) aWb = Session () -> GetWorkbench (  abench -> Father ()  );

   if (  !aWb.IsNull ()  ) astream << " " << aWb -> Name () -> ToCString ();

  }  // end if

  astream << endl;

 }  // end for

 astream.close ();
 OSD_File aFile (   OSD_Path (  anOldPath -> ToCString ()  )   );

 aFile.SetProtection (  OSD_Protection ( OSD_RW, OSD_RW, OSD_RW, OSD_RW )  );


}  // end WOKernel_Workshop :: DumpWorkbenchList
//=======================================================================
//function : AddWorkbench
//purpose  : Adds a wb to the workshop (i.e. updates WBLIST)
//=======================================================================
void WOKernel_Workshop::AddWorkbench(const Handle(WOKernel_Workbench)& aworkbench)
{
  if(Session()->IsKnownEntity(aworkbench->FullName()))
    {
      ErrorMsg() << "WOKernel_Workshop::AddWorkbench" << "There is already a workbench called " <<  aworkbench->Name() << endm;
      Standard_ProgramError::Raise("WOKernel_Workshop::AddWorkbench");
    }
  myworkbenches->Append(aworkbench->FullName());
  Session()->AddEntity(aworkbench);
  DumpWorkbenchList();
  return;
}

//=======================================================================
//function : RemoveWorkbench
//purpose  :
//=======================================================================
void WOKernel_Workshop :: RemoveWorkbench (
                           const Handle( WOKernel_Workbench )& aworkbench
                          ) {

 Standard_Integer                   i, j = 0;
 Handle( TCollection_HAsciiString ) kids = new TCollection_HAsciiString ();
  
 for (  i = 1; i <= myworkbenches -> Length (); ++i  ) {

  Handle( TCollection_HAsciiString ) aFather;
  Handle( WOKernel_Workbench       ) aWb = Session () -> GetWorkbench (
                                                          myworkbenches -> Value ( i )
                                                         );

  if (  !aWb.IsNull ()  ) aFather = aWb -> Father ();

  if (   myworkbenches -> Value ( i ) -> IsSameString (  aworkbench -> FullName ()  )   )

   j = i;

  if (   !aFather.IsNull () && aFather -> IsSameString (  aworkbench -> FullName ()  )   ) {

   kids -> AssignCat (  aWb -> FullName ()  );
   kids -> AssignCat ( " "                  );

  }  // end if

 }  // end for

 if (  !kids -> IsEmpty ()  )

  WarningMsg() << "WOKernel_Workshop :: RemoveWorkbench"
             << "workbench '" << aworkbench -> FullName ()
             << "' has ancestors ( "
             << kids << ")"
             << endm;

 if ( j != 0 ) myworkbenches -> Remove ( j );
  
 Session () -> RemoveEntity ( aworkbench );

 DumpWorkbenchList ();

}  // end WOKernel_Workshop :: RemoveWorkbench
