// File:	WOKernel_Session.cxx
// Created:	Thu Jun 29 18:01:08 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_Stream.hxx>

#include <WOKernel_Session.ixx>

#include <WOKernel_Factory.hxx>
#include <WOKernel_Warehouse.hxx>
#include <WOKernel_Workshop.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_DataMapIteratorOfDataMapOfHAsciiStringOfFactory.hxx>
#include <WOKernel_GlobalFileTypeBase.hxx>


#include <WOKernel_FileTypeKeyWords.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_Path.hxx>

#include <OSD_Environment.hxx>
#include <OSD_Host.hxx>
#include <OSD_OEMType.hxx>

#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>

//#ifdef HAVE_IOMANIP
//# include <iomanip>
//#elif defined (HAVE_IOMANIP_H)
//# include <iomanip.h>
//#endif

//=======================================================================
//function : WOKernel_Session
//purpose  : 
//=======================================================================
WOKernel_Session::WOKernel_Session(const Handle(TCollection_HAsciiString)& aname) 
  : WOKernel_Entity(aname, Handle(WOKernel_Entity)()), mystation(WOKernel_UnknownStation)
{
  // nothing to do here
  myfiletypebases= new WOKernel_GlobalFileTypeBase;
}

//=======================================================================
//function : EntityCode
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Session::EntityCode() const 
{
  static Handle(TCollection_HAsciiString) acode = new TCollection_HAsciiString("session");
  return acode;
}

//=======================================================================
//function : GetFileTypeBase
//purpose  : 
//=======================================================================
Handle(WOKernel_FileTypeBase) WOKernel_Session::GetFileTypeBase(const Handle(WOKernel_Entity)& anent) const
{
   Handle(WOKernel_FileTypeBase) base;

   if(!anent.IsNull())
     {
       base =  myfiletypebases->GetFileTypeBase(anent);
   
       if(!base.IsNull())
	 {
	   base->SetNeededArguments(anent, DBMSystem(), Station());
	 }
     }
   return base;
}

//=======================================================================
//function : BuildParameters
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKernel_Session::BuildParameters(const Handle(WOKUtils_HSequenceOfParamItem)& ,
									const Standard_Boolean )
{
  return new WOKUtils_HSequenceOfParamItem;
}

//=======================================================================
//function : Build
//purpose  : 
//=======================================================================
void WOKernel_Session::Build(const Handle(WOKUtils_HSequenceOfParamItem)& )
{
  // nothing to build
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
void WOKernel_Session::Destroy()
{
  // nothing to destroy
}
//=======================================================================
//function : Open
//purpose  : 
//=======================================================================
void WOKernel_Session::Open()
{
  if(IsOpened()) return;
  Standard_ProgramError::Raise("WOKernel_Session::Open : Wrong Open Call for Session");
  return;
}

//=======================================================================
//function : Open
//purpose  : 
//=======================================================================
void WOKernel_Session::Open(const Handle(TCollection_HAsciiString)& aroot, const Handle(TCollection_HAsciiString)& woklib)
{
  //OSD_Environment rootadm("WOK_ROOTADMDIR");
  //OSD_Environment woklib ("WOK_LIBPATH");
  OSD_Host        ahost;
  OSD_OEMType     ahosttype;
  Handle(TCollection_HAsciiString) apath;
  Handle(TCollection_HAsciiString) afile;
  Handle(WOKernel_Factory) afact;

  if(IsOpened()) return;

  Reset();

  // chargement de la table de parametres de la Session
  if(aroot.IsNull())
    {
      ErrorMsg() << "WOKernel_Session::Open" 
	<< "No administration root directory" << endm;
      return;
    }
  else
    {
      Handle(WOKUtils_Path) rootpath = new WOKUtils_Path(aroot);
      if(!rootpath->Exists())
	{
	  ErrorMsg() << "WOKernel_Session::Open" 
	    << "Administration root directory (" << aroot << ") does not exists" << endm;
	  return;
	}
      if(!rootpath->IsDirectory())
	{
	  ErrorMsg() << "WOKernel_Session::Open" 
	    << "Administration root directory (" << aroot << ") is not a directory" << endm;
	  return;
	}
    }

  Params().Set("%WOKSESSION_Adm", aroot->ToCString());

  // chargement de la librarie WOK
  if(woklib.IsNull())
    {
      ErrorMsg() << "WOKernel_Session::Open" << "No File search path" << endm;
      return;
    }

  Params().Set("%WOKSESSION_WokLibPath", woklib->ToCString());

  GetParams();

  ahosttype = ahost.MachineType();
  
  if(ahost.Failed()) ahost.Perror();
  
  WOKernel_StationID thestation = WOKernel_SUN;

  switch(ahosttype)
    {
    case OSD_SUN:
      break;
    case OSD_DEC:
      thestation = WOKernel_DECOSF;
      break;
    case OSD_SGI:
      thestation = WOKernel_SGI;
      break;
    case OSD_HP:
      thestation = WOKernel_HP;
      break;
    case OSD_MAC:
      thestation = WOKernel_MAC;
      break;
    case OSD_PC:
      thestation = WOKernel_WNT;
      break;
    case OSD_LIN:
      thestation = WOKernel_LIN;
      break;
    case OSD_AIX:
      thestation = WOKernel_AIX;
      break;
    default:
      ErrorMsg() << "WOKernel_Session::Open" << "Unrecognized Type of host " << ahost.HostName().ToCString() << endm;
      Standard_Failure::Raise("WOKernel_Session::Open");
    }

  Params().Set((Standard_CString)LOCALARCHVAR, WOKernel_Station::GetName(thestation)->ToCString());

  if(mystation==WOKernel_UnknownStation)
    {
      mystation =  thestation;
    }

  // chargement de l'ATLISTFILE
  afile = EvalParameter("ATListFile");

  if(afile.IsNull() == Standard_True)
    {
      ErrorMsg() << "WOKernel_Session::Open" << "Parameter %WOKSESSION_ATListFile not set" << endm;
      Standard_ProgramError::Raise("WOKernel_Session::Open");
    }
  
  ifstream astream(afile->ToCString(), ios::in);
  char inbuf[1024];

  *inbuf = '\0';
  while(astream >> setw(1024) >> inbuf)
    {
      afact = new WOKernel_Factory(new TCollection_HAsciiString(inbuf), this);
      AddEntity(afact);
      *inbuf = '\0';
    }

  SetOpened();
  return;
}

//=======================================================================
//function : Close
//purpose  : 
//=======================================================================
void WOKernel_Session::Close()
{
  if(!IsOpened()) return;
  ClearEntities();
  Reset();
  SetClosed();
}



//=======================================================================
//function : AddEntity
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_Session::AddEntity(const Handle(WOKernel_Entity)& anentity)
{
  const Handle(TCollection_HAsciiString)& astr = anentity->FullName();

  if(myunits.IsBound(astr)       ||
     myworkbenches.IsBound(astr) || 
     myparcels.IsBound(astr)     ||
     myworkshops.IsBound(astr)   ||
     mywarehouses.IsBound(astr)  ||
     myfactories.IsBound(astr)
     )
    return Standard_False;
  
  if(anentity->IsKind(STANDARD_TYPE(WOKernel_DevUnit)))   {myunits.Bind(astr,*((Handle(WOKernel_DevUnit) *) &anentity));return Standard_True;}
  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Workbench))) {myworkbenches.Bind(astr,*((Handle(WOKernel_Workbench) *) &anentity));return Standard_True;}
  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Parcel)))    {myparcels.Bind(astr,*((Handle(WOKernel_Parcel) *) &anentity));return Standard_True;}
  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Workshop)))  {myworkshops.Bind(astr,*((Handle(WOKernel_Workshop) *) &anentity));return Standard_True;}
  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Warehouse))) {mywarehouses.Bind(astr,*((Handle(WOKernel_Warehouse) *) &anentity));return Standard_True;}
  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Factory)))   {myfactories.Bind(astr,*((Handle(WOKernel_Factory) *) &anentity));return Standard_True;}
  return Standard_False;
}


//=======================================================================
//function : RemoveEntity
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_Session::RemoveEntity(const Handle(WOKernel_Entity)& anentity)
{
  const Handle(TCollection_HAsciiString)&  aname = anentity->FullName();
  if(myunits.IsBound(aname))       myunits.UnBind(aname);
  if(myworkbenches.IsBound(aname)) myworkbenches.UnBind(aname);
  if(myparcels.IsBound(aname))     myparcels.UnBind(aname);
  if(myworkshops.IsBound(aname))   myworkshops.UnBind(aname);
  if(mywarehouses.IsBound(aname))  mywarehouses.UnBind(aname);
  if(myfactories.IsBound(aname))   myfactories.UnBind(aname);
  return Standard_True;
}

//=======================================================================
//function : IsKnownEntity
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_Session::IsKnownEntity(const Handle(TCollection_HAsciiString)& auniquename) const 
{
  if(myunits.IsBound(auniquename))       return Standard_True;
  if(myworkbenches.IsBound(auniquename)) return Standard_True;
  if(myparcels.IsBound(auniquename))     return Standard_True;
  if(myworkshops.IsBound(auniquename))   return Standard_True;
  if(mywarehouses.IsBound(auniquename))  return Standard_True;
  if(myfactories.IsBound(auniquename))   return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : IsKnownEntity
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_Session::IsKnownEntity(const Handle(WOKernel_Entity)& anentity) const 
{
  if(myunits.IsBound(anentity->FullName()))       return Standard_True;
  if(myworkbenches.IsBound(anentity->FullName())) return Standard_True;
  if(myparcels.IsBound(anentity->FullName()))     return Standard_True;
  if(myworkshops.IsBound(anentity->FullName()))   return Standard_True;
  if(mywarehouses.IsBound(anentity->FullName()))  return Standard_True;
  if(myfactories.IsBound(anentity->FullName()))   return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : ClearEntities
//purpose  : 
//=======================================================================
void WOKernel_Session::ClearEntities()
{
  myfactories.Clear();
  mywarehouses.Clear();
  myworkshops.Clear();
  myparcels.Clear();
  myworkbenches.Clear();
  myunits.Clear();
}

//=======================================================================
//function : IsTHETYPE
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_Session::IsFactory(const Handle(TCollection_HAsciiString)& aname) const 
{return myfactories.IsBound(aname);}

Standard_Boolean WOKernel_Session::IsWarehouse(const Handle(TCollection_HAsciiString)& aname) const 
{return mywarehouses.IsBound(aname);}

Standard_Boolean WOKernel_Session::IsWorkshop(const Handle(TCollection_HAsciiString)& aname) const 
{return myworkshops.IsBound(aname);}

Standard_Boolean WOKernel_Session::IsUnitNesting(const Handle(TCollection_HAsciiString)& aname) const 
{
  if( myparcels.IsBound(aname)) return Standard_True;
  return myworkbenches.IsBound(aname);
}

Standard_Boolean WOKernel_Session::IsWorkbench(const Handle(TCollection_HAsciiString)& aname) const 
{return myworkbenches.IsBound(aname);}

Standard_Boolean WOKernel_Session::IsParcel(const Handle(TCollection_HAsciiString)& aname) const 
{return myparcels.IsBound(aname);}

Standard_Boolean WOKernel_Session::IsDevUnit(const Handle(TCollection_HAsciiString)& aname) const 
{return myunits.IsBound(aname);}

//=======================================================================
//function : GetTHETYPE
//purpose  : 
//=======================================================================
const Handle(WOKernel_Entity)& WOKernel_Session::GetEntity(const Handle(TCollection_HAsciiString)& aname) const 
{
  static Handle(WOKernel_Entity) NULLRESULT;
  
  if(aname.IsNull()) return NULLRESULT;
  
  if(myunits.IsBound(aname))       return myunits.Find(aname);
  if(myworkbenches.IsBound(aname)) return myworkbenches.Find(aname);
  if(myparcels.IsBound(aname))     return myparcels.Find(aname);
  if(myworkshops.IsBound(aname))   return myworkshops.Find(aname);
  if(mywarehouses.IsBound(aname))  return mywarehouses.Find(aname);
  if(myfactories.IsBound(aname))   return myfactories.Find(aname);
  if(!strcmp(aname->ToCString(), ":")) 
    {
      static Handle(WOKernel_Entity) ME = this;
      ME = this;
      return ME;
    }
  return NULLRESULT;
}

const Handle(WOKernel_Factory)& WOKernel_Session::GetFactory(const Handle(TCollection_HAsciiString)& aname) const 
{
  static Handle(WOKernel_Factory) NULLRESULT;
  if(myfactories.IsBound(aname)) return myfactories.Find(aname);
  return NULLRESULT;
}

const Handle(WOKernel_Warehouse)& WOKernel_Session::GetWarehouse(const Handle(TCollection_HAsciiString)& aname) const 
{
  static Handle(WOKernel_Warehouse) NULLRESULT;
  if(mywarehouses.IsBound(aname)) return mywarehouses.Find(aname);
  return NULLRESULT;
}

const Handle(WOKernel_Workshop)& WOKernel_Session::GetWorkshop(const Handle(TCollection_HAsciiString)& aname) const 
{
  static Handle(WOKernel_Workshop) NULLRESULT;
  if(myworkshops.IsBound(aname)) return myworkshops.Find(aname);
  return NULLRESULT;
}

const Handle(WOKernel_UnitNesting)& WOKernel_Session::GetUnitNesting(const Handle(TCollection_HAsciiString)& aname) const 
{
  static Handle(WOKernel_UnitNesting) NULLRESULT;
  if(myparcels.IsBound(aname))     return myparcels.Find(aname);
  if(myworkbenches.IsBound(aname)) return myworkbenches.Find(aname);
  return NULLRESULT;
}

const Handle(WOKernel_Workbench)& WOKernel_Session::GetWorkbench(const Handle(TCollection_HAsciiString)& aname) const 
{
  static Handle(WOKernel_Workbench) NULLRESULT;
  if(myworkbenches.IsBound(aname)) return myworkbenches.Find(aname);
  return NULLRESULT;
}

const Handle(WOKernel_Parcel)& WOKernel_Session::GetParcel(const Handle(TCollection_HAsciiString)& aname) const 
{
  static Handle(WOKernel_Parcel) NULLRESULT;
  if(myparcels.IsBound(aname)) return myparcels.Find(aname);
  return NULLRESULT;
}

const Handle(WOKernel_DevUnit)& WOKernel_Session::GetDevUnit(const Handle(TCollection_HAsciiString)& aname) const 
{
  static Handle(WOKernel_DevUnit) NULLRESULT;
  if(myunits.IsBound(aname)) return myunits.Find(aname);
  return NULLRESULT;
}

//=======================================================================
//function : GetMatchingEntities
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Session::GetMatchingEntities(const Handle(TCollection_HAsciiString)& aname,
									      const Standard_Boolean fullpath) const 
{
  WOKernel_EntityIterator anit(this);
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;

  while(anit.More())
    {
      if(fullpath)
	{
	  if(!strcmp(anit.Key()->ToCString(),aname->ToCString()))
	    {
	      aseq->Append(anit.Key());
	    }
	}
      else
	{ 
	  Standard_Integer pos = anit.Key()->Search(aname);
	  if(pos >1)
	    {
	      if(pos == anit.Key()->Length()-aname->Length()+1 && anit.Key()->Value(pos-1)==':')
		{
		  aseq->Append(anit.Key());
		}
	    }
	}
      anit.Next();
    }

  return aseq;
}

//=======================================================================
//function : Factories
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Session::Factories() const 
{
  Handle(TColStd_HSequenceOfHAsciiString) factories = new TColStd_HSequenceOfHAsciiString;

  WOKernel_DataMapIteratorOfDataMapOfHAsciiStringOfFactory anit(myfactories);

  while(anit.More())
    {
      factories->Append(anit.Key());
      anit.Next();
    }

  return factories;
}


//=======================================================================
//function : DumpFactoryList
//purpose  : 
//=======================================================================
void WOKernel_Session::DumpFactoryList() const
{
  Handle(TCollection_HAsciiString) anatlist, aname;
  anatlist = EvalParameter("ATListFile");
  ofstream astream(anatlist->ToCString(), ios::out);
  
  if(!astream)
    {
      ErrorMsg() << "WOKernel_Session::AddFactory" << "Could not open " << anatlist << endm;
      Standard_ProgramError::Raise("WOKernel_Session::AddFactory");
    }
  
  WOKernel_DataMapIteratorOfDataMapOfHAsciiStringOfFactory anit(myfactories);
  while(anit.More())
    {
      astream << anit.Value()->Name()->ToCString() << endl;
      anit.Next();
    }
  return;
}

//=======================================================================
//function : AddFactory
//purpose  : 
//=======================================================================
void WOKernel_Session::AddFactory(const Handle(WOKernel_Factory)& afact)
{
  if(Session()->IsKnownEntity(afact->FullName()))
    {
      ErrorMsg() << "WOKernel_Session::AddFactory" << "There is already an entity named " << afact->FullName() << endm;
      Standard_ProgramError::Raise("WOKernel_Session::AddFactory");
    }

  AddEntity(afact);
  DumpFactoryList();
  return;
}

//=======================================================================
//function : RemoveFactory
//purpose  : 
//=======================================================================
void WOKernel_Session::RemoveFactory(const Handle(WOKernel_Factory)& afact)
{
  if(myfactories.IsBound(afact->FullName())) myfactories.UnBind(afact->FullName());
  DumpFactoryList();
  return;

}

//=======================================================================
//function : SetStation
//purpose  : 
//=======================================================================
void WOKernel_Session::SetStation(const WOKernel_StationID ast)
{
  mystation = ast;
}

//=======================================================================
//function : SetDBMSystem
//purpose  : 
//=======================================================================
void WOKernel_Session::SetDBMSystem(const WOKernel_DBMSID adb)
{
  mydbms = adb;
}

//=======================================================================
//function : DebugMode
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_Session::DebugMode() const
{
  return mydebug;
}

//=======================================================================
//function : SetDebugMode
//purpose  : 
//=======================================================================
void WOKernel_Session::SetDebugMode() 
{
  mydebug = Standard_True;
}

//=======================================================================
//function : UnSetDebugMode
//purpose  : 
//=======================================================================
void WOKernel_Session::UnsetDebugMode() 
{
  mydebug = Standard_False;
}

