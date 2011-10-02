// File:	WOKernel_File.cxx
// Created:	Wed Jul 26 19:02:37 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <Standard_NullObject.hxx>
#include <Standard_ConstructionError.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_FileTypeBase.hxx>

#include <WOKernel_File.ixx>

//=======================================================================
//function : WOKernel_File
//purpose  : 
//=======================================================================
WOKernel_File::WOKernel_File(const Handle(TCollection_HAsciiString)& aname, 
			     const Handle(WOKernel_Entity)& anesting, 
			     const Handle(WOKernel_FileType)& atype) 
: WOKernel_BaseEntity(aname, anesting), mytype(atype)
{
  myfullname = GetUniqueName();
}


//=======================================================================
//function : WOKernel_File
//purpose  : 
//=======================================================================
WOKernel_File::WOKernel_File(const Handle(WOKernel_Entity)& anesting, 
			     const Handle(WOKernel_FileType)& atype) 
: WOKernel_BaseEntity(Handle(TCollection_HAsciiString)(), anesting), mytype(atype)
{
  if(mytype->IsFileDependent())
    {
      ErrorMsg() << "WOKernel_File::WOKernel_File"
	       << "Tried to build a File with noname with a FileDependant Type (" << mytype->Name() << ")" << endm;

      Standard_ConstructionError::Raise("WOKernel_File::WOKernel_File");
    }

  Handle(TCollection_HAsciiString) aname = mytype->ComputePath(anesting->Params(),Handle(TCollection_HAsciiString)());
  if (!aname.IsNull()) {
    Handle(WOKUtils_Path) apath = new WOKUtils_Path(aname);
    SetName(apath->FileName());
  }
}



//=======================================================================
//function : SetPath
//purpose  : 
//=======================================================================
void WOKernel_File::SetPath(const Handle(WOKUtils_Path)& apath)
{
  if(apath.IsNull())  Standard_NullObject::Raise("WOKernel_File::SetPath");
  mypath = apath;
}

//=======================================================================
//function : SetType
//purpose  : 
//=======================================================================
void WOKernel_File::SetType(const Handle(WOKernel_FileType)& atype)
{
  mypath.Nullify();
  mytype = atype;
}

//=======================================================================
//function : GetUniqueName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_File::GetUniqueName() const
{
  Handle(TCollection_HAsciiString) apath = new TCollection_HAsciiString;

  Handle(WOKernel_Entity) nesting = Session()->GetEntity(Nesting());

  if(!nesting.IsNull())
    {
      apath->AssignCat(nesting->UserPathName());
      apath->AssignCat(":");
      apath->AssignCat(TypeName());
      if(!Name().IsNull()) 
	{
	  apath->AssignCat(":");
	  apath->AssignCat(Name());
	}
      return apath;
    }
  else
    {
      apath->AssignCat(TypeName());
      if(!Name().IsNull()) 
	{
	  apath->AssignCat(":");
	  apath->AssignCat(Name());
	}
      return apath;
    }
}

//=======================================================================
//function : LocatorName
//purpose  : 
//=======================================================================
const Handle(TCollection_HAsciiString)& WOKernel_File::LocatorName() 
{
  
  if(mylocatorname.IsNull())
    {
      Handle(TCollection_HAsciiString) apath = new TCollection_HAsciiString;
      
      Handle(WOKernel_Entity) nesting = Handle(WOKernel_DevUnit)::DownCast(Session()->GetEntity(Nesting()));
      
      if(!nesting.IsNull())
	{
	  apath->AssignCat(nesting->Name());
	  apath->AssignCat(":");
	  apath->AssignCat(TypeName());
	  apath->AssignCat(":");
	  apath->AssignCat(Name());
	}
      else
	{
	  apath->AssignCat(TypeName());
	  apath->AssignCat(":");
	  apath->AssignCat(Name());
	}
      mylocatorname = apath;
    }
  return mylocatorname;
}

//=======================================================================
//function : FileLocatorName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_File::FileLocatorName(const Handle(TCollection_HAsciiString)& unitname,
								const Handle(TCollection_HAsciiString)& type,
								const Handle(TCollection_HAsciiString)& name) 
{
  Handle(TCollection_HAsciiString) apath = new TCollection_HAsciiString;
  
  if(!unitname.IsNull())
    {
      apath->AssignCat(unitname);
      apath->AssignCat(":");
      apath->AssignCat(type);
      apath->AssignCat(":");
      apath->AssignCat(name);
      return apath;
    }
  else
    {
      apath->AssignCat(type);
      apath->AssignCat(":");
      apath->AssignCat(name);
      return apath;
    }
}

//=======================================================================
//function : GetFilePath
//purpose  : 
//=======================================================================
void WOKernel_File::GetPath() 
{
  if(mypath.IsNull())
    {
      Handle(WOKernel_FileType)        atype;
      
      // D'abord le type de Nesting (DevUnit, UnitNesting)
      
      Handle(WOKernel_Entity) anesting = Session()->GetEntity(Nesting());
      
      atype = Type();
      
      Handle(TCollection_HAsciiString) astr = atype->ComputePath(anesting->Params(), Name());
      mypath = new WOKUtils_Path(astr);
    }
 return;
}

