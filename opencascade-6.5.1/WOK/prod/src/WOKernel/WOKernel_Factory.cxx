// File:	WOKernel_Factory.cxx
// Created:	Thu Jun 29 18:50:43 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>
#include <WOKernel_Factory.ixx>

#include <WOKernel_Workshop.hxx>
#include <WOKernel_Warehouse.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

//=======================================================================
//function : WOKernel_Factory
//purpose  : Factory constructor
//=======================================================================
WOKernel_Factory::WOKernel_Factory(const Handle(TCollection_HAsciiString)& aname, const Handle(WOKernel_Session)& anesting) : WOKernel_Entity(aname, anesting)
{
  SetSession(anesting);
}

//=======================================================================
//function : EntityCode
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Factory::EntityCode() const 
{
  static Handle(TCollection_HAsciiString) acode = new TCollection_HAsciiString("factory");
  return acode;
}

//=======================================================================
//function : Open
//purpose  : Opens an existing factory
//=======================================================================
void WOKernel_Factory::Open()
{
  if(IsOpened()) return;
  {
    Handle(TCollection_HAsciiString) astr;
    Handle(TColStd_HSequenceOfHAsciiString) aseq;
    Handle(WOKernel_Workshop)     ashop;
    Handle(WOKUtils_Path)         apath;
    Handle(WOKernel_Warehouse)    thewarehouse;
    Handle(WOKernel_File)         afile;
    Handle(WOKernel_FileType)     atype;
    Reset();

    GetParams();

    SetFileTypeBase(Session()->GetFileTypeBase(this));

    afile = new WOKernel_File(this, FileTypeBase()->Type("WorkshopListFile"));
    afile->GetPath();

    ifstream astream(afile->Path()->Name()->ToCString());
    char inbuf[1024];

    myworkshops = new TColStd_HSequenceOfHAsciiString();

    while(astream >> setw(1024) >> inbuf)
      {
	ashop = new WOKernel_Workshop(new TCollection_HAsciiString(inbuf), Handle(WOKernel_Factory)(this));
	myworkshops->Append(ashop->FullName());
	Session()->AddEntity(ashop);
      }
    astream.close();

    // charger le warehouse et l'ouvrir
    thewarehouse = new WOKernel_Warehouse(EvalParameter("Warehouse"), Handle(WOKernel_Factory)(this));
    mywarehouse = thewarehouse->FullName();
    
    if(Params().IsClassVisible(thewarehouse->Name()->ToCString()))
      {
	mywarehouse = thewarehouse->FullName();
	Session()->AddEntity(thewarehouse);
      }
    

    // charger la base SCCS
    afile = new WOKernel_File(astr, this, FileTypeBase()->Type("SCCSDir"));
    afile->GetPath();

    mysccsbase = afile->Path();

    SetOpened();
  }
  return;
}

//=======================================================================
//function : Close
//purpose  : Closes an opened factory
//=======================================================================
void WOKernel_Factory::Close()
{
  if(!IsOpened()) return;

  Handle(WOKernel_Workshop) ashop;
  Handle(WOKernel_Warehouse) abag;

  Standard_Integer i;
  
  for(i=1; i <= myworkshops->Length(); i++)
    {
      ashop = Session()->GetWorkshop(myworkshops->Value(i));
      ashop->Close();
      Session()->RemoveEntity(ashop);
    }
  abag = Session()->GetWarehouse(mywarehouse);
  if(!abag.IsNull())
    {
      abag->Close();
      Session()->RemoveEntity(abag);
      mywarehouse.Nullify();
    }

  myworkshops.Nullify();
  Reset();
  SetClosed();
  return;
}

//=======================================================================
//function : Workshops
//purpose  : Gives the list of the kown Workshop in factory
//=======================================================================
 Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Factory::Workshops() const 
{
  return myworkshops;
}

//=======================================================================
//function : DumpWorkshopList
//purpose  : updates Workshop list
//=======================================================================
void WOKernel_Factory::DumpWorkshopList() const
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) name;
  Handle(WOKernel_File) afile;

  afile = new WOKernel_File(this, GetFileType("WorkshopListFile"));
  afile->GetPath();

  ofstream astream(afile->Path()->Name()->ToCString(), ios::out);

  if(!astream)
    {
      ErrorMsg() << "WOKernel_Factory::AddWorkshop" << "Could not open " << afile->Path()->Name() << endm;
      Standard_ProgramError::Raise("WOKernel_Factory::AddWorkshop");
    }

  for(i = 1 ; i <= myworkshops->Length() ; i++)
    {
      name = Session()->GetWorkshop(myworkshops->Value(i))->Name();
      astream << name->ToCString() << endl;
    }
  return;
  
}

//=======================================================================
//function : AddWorkshop
//purpose  : Adds a workshop in the list of workshops (i.e. updates WSLIST)
//=======================================================================
void WOKernel_Factory::AddWorkshop(const Handle(WOKernel_Workshop)& aworkshop)
{
  if(Session()->IsKnownEntity(aworkshop->FullName()))
    {
      ErrorMsg() << "WOKernel_Factory::AddWorkshop" << "There is already an entity named " <<  aworkshop->Name() << endm;
      Standard_ProgramError::Raise("WOKernel_Factory::AddWorkshop");
    }

  myworkshops->Append(aworkshop->FullName());
  Session()->AddEntity(aworkshop);

  DumpWorkshopList();
  return;
}

//=======================================================================
//function : RemoveWorkshop
//purpose  : removes a workshop in the list of workshops (i.e. updates WSLIST)
//=======================================================================
void WOKernel_Factory::RemoveWorkshop(const Handle(WOKernel_Workshop)& aworkshop)
{
  Standard_Integer i;

  for(i = 1 ; i <= myworkshops->Length() ; i++)
    {
      if(myworkshops->Value(i)->IsSameString(aworkshop->FullName()))
	{myworkshops->Remove(i);break;}
    }
  Session()->RemoveEntity(aworkshop);
  
  DumpWorkshopList();
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetWarehouse
//purpose  : 
//=======================================================================
void WOKernel_Factory::SetWarehouse(const Handle(WOKernel_Warehouse)& awarehouse) 
{
  if(!awarehouse.IsNull())
    {
      mywarehouse = awarehouse->FullName();
    }
}

//=======================================================================
//function : Warehouse
//purpose  : Returns Handle of Factory Warehouse
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Factory::Warehouse() const 
{
  return mywarehouse;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetSourceStorage
//purpose  : 
//=======================================================================
void WOKernel_Factory::SetSourceStorage(const Handle(WOKUtils_Path)& apath)
{
  mysccsbase = apath;
}

//=======================================================================
//function : SourceStorage
//purpose  : Returns Path of Factory Source repository
//=======================================================================
Handle(WOKUtils_Path) WOKernel_Factory::SourceStorage() const 
{
  return mysccsbase;
}

