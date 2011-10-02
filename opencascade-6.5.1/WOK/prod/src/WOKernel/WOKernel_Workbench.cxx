// File:	WOKernel_Workbench.cxx
// Created:	Wed Jul 26 18:27:19 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>

#include <WOKernel_Workbench.ixx>

#include <WOKernel_Session.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>

#include <WOKUtils_Param.hxx>
#include <WOKUtils_ParamItem.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKTools_Messages.hxx>

#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>

//#ifdef HAVE_IOMANIP
//# include <iomanip>
//#elif defined (HAVE_IOMANIP_H)
//# include <iomanip.h>
//#endif

//=======================================================================
//function : WOKernel_Workbench
//purpose  : instantiates a Workbench
//=======================================================================
WOKernel_Workbench::WOKernel_Workbench(const Handle(TCollection_HAsciiString)& aname, 
				       const Handle(WOKernel_Workshop)& anesting,
				       const Handle(WOKernel_Workbench)& afather) 
: WOKernel_UnitNesting(aname, anesting)
{
  if(afather.IsNull() == Standard_False)
    myfather = afather->FullName();
}

//=======================================================================
//function : GetParameters
//purpose  :  
//=======================================================================
void WOKernel_Workbench::GetParameters()
{
  Standard_Integer i;
  Handle(TColStd_HSequenceOfAsciiString) aseq;
  Handle(TColStd_HSequenceOfAsciiString) subclasses = new TColStd_HSequenceOfAsciiString;
  Handle(TColStd_HSequenceOfAsciiString) dirs       = new TColStd_HSequenceOfAsciiString;
  Handle(TCollection_HAsciiString) astr;
  Handle(TCollection_HAsciiString) libdir;
  Handle(WOKernel_Entity) entity;
  
  if(!Nesting().IsNull())
    {
      // Entites quelconques
      
      entity = Session()->GetEntity(Nesting());
      
      aseq = entity->Params().SubClasses();
      if(!aseq.IsNull())
	{
	  for(i=1; i<=aseq->Length(); i++)
	    {
	      subclasses->Append(aseq->Value(i));
	    }
	}
      
      aseq =  entity->Params().SearchDirectories();
      if(!aseq.IsNull())
	{
	  for(i=1; i<=aseq->Length(); i++)
	    {
	      dirs->Append(aseq->Value(i));
	    }
	}
      ChangeParams().SetSubClasses(subclasses);
      ChangeParams().SetSearchDirectories(dirs);

      // on evalue le ADM des ancetres
      Handle(TColStd_HSequenceOfHAsciiString) ances = Ancestors();
      Handle(WOKernel_Workbench) wb;
      for (Standard_Integer i= ances->Length(); i > 0 ; i--) {
	wb = Session()->GetWorkbench(ances->Value(i));
	if (!wb.IsNull()) {
	  Params().SubClasses()->Append(wb->Name()->ToCString());
	  Handle(TCollection_HAsciiString) astr = wb->EvalParameter("Adm", Standard_False);
	  
	  if(!astr.IsNull()) {
	    dirs->Prepend(astr->ToCString());
	    TCollection_AsciiString& lastsub = subclasses->ChangeValue(subclasses->Length());
	    lastsub.AssignCat("@");
	    lastsub.AssignCat(astr->String());
	  }
	}
      }
      ChangeParams().SetSubClasses(subclasses);
      ChangeParams().SetSearchDirectories(dirs);
    }

}  


//=======================================================================
//function : EntityCode
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Workbench::EntityCode() const 
{
  static Handle(TCollection_HAsciiString) acode = new TCollection_HAsciiString("workbench");
  return acode;
}

//=======================================================================
//function : GetUnitList
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Workbench::GetUnitList() 
{
  Standard_Character                        typecode;
  Handle(WOKernel_DevUnit)                  unit;
  Handle(WOKernel_File)                     afile;
  Handle(TCollection_HAsciiString)          astr;
  Handle(TColStd_HSequenceOfHAsciiString)   units;

  units = new TColStd_HSequenceOfHAsciiString;

  afile = GetUnitListFile();
  afile->GetPath();

  ifstream astream(afile->Path()->Name()->ToCString(), ios::in);
  char namebuf[1024];

  typecode = 0;
  *namebuf = '\0';

  while(astream >> typecode >> setw(1024) >> namebuf)
    {
      astr = new TCollection_HAsciiString(namebuf);

      unit = GetDevUnit(typecode, astr);

      if(unit.IsNull() == Standard_True)
	{
	  ErrorMsg() << "WOKernel_UnitNesting::Open" << "Unknown type code (" << typecode << ") in UNITLIST of " << Name() << endm;
	  Standard_ProgramError::Raise("WOKernel_UnitNesting::Open");
	}

      units->Append(unit->FullName());
      Session()->AddEntity(unit);
      typecode = 0;
      *namebuf = '\0';
    }
  astream.close();
  return units;
}


//=======================================================================
//function : Open
//purpose  : opens a Wb
//=======================================================================
void WOKernel_Workbench :: Open () {

 if (  IsOpened ()  ) return;

 Handle( WOKernel_Workbench ) afather;

 if (  !Father ().IsNull ()  ) {

  afather = Session () -> GetWorkbench (  Father ()  );

  if (  !afather.IsNull ()  ) afather -> Open ();

 }  //end if
    
 GetParams ();

 WOKernel_UnitNesting :: Open ();

 SetOpened ();

}  // end WOKernel_Workbench :: Open

//=======================================================================
//function : Close
//purpose  : 
//=======================================================================
void WOKernel_Workbench::Close()
{
  if(!IsOpened()) return;
  WOKernel_UnitNesting::Close();
  SetClosed();
  return;
}

//=======================================================================
//function : Father
//purpose  : returns WB's father
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Workbench::Father() const 
{
  return myfather;
}

//=======================================================================
//function : SetFather
//purpose  : Sets the father of Wb
//=======================================================================
void WOKernel_Workbench::SetFather(const Handle(WOKernel_Workbench)& afather) 
{
  myfather = afather->FullName();

  
}

//=======================================================================
//function : Ancestors
//purpose  : returns WB ancestors
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Workbench::Ancestors() const 
{
  Handle(WOKernel_Workbench) wb = Handle(WOKernel_Workbench)(this);
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;

  
  while(wb.IsNull() == Standard_False)
    {
      aseq->Append(wb->FullName());
      wb = Session()->GetWorkbench(wb->Father());
    }

  return aseq;
}


//=======================================================================
//function : Visibility
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Workbench::Visibility() const
{
  Standard_Integer i;
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Handle(WOKernel_Workbench) abench = this;
  
  // l'heritage des workbenchs
  while(!abench.IsNull())
    {
      aseq->Append(abench->FullName());
      abench = Session()->GetWorkbench(abench->Father());
    }

  // les parcels
  Handle(WOKernel_Workshop) ashop = Session()->GetWorkshop(Nesting());
  Handle(TColStd_HSequenceOfHAsciiString) parcelseq = ashop->ParcelsInUse();
  for( i=1; i<=parcelseq->Length(); i++)
    {
      aseq->Append(parcelseq->Value(i));
    }
 
  return aseq;
}

//=======================================================================
//function : GetUnitListFile
//purpose  : 
//=======================================================================
Handle(WOKernel_File) WOKernel_Workbench::GetUnitListFile() const
{
  return new WOKernel_File(this, GetFileType("UnitListFile"));
}
