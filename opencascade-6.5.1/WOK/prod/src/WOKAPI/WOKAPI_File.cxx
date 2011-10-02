// File:	WOKAPI_File.cxx
// Created:	Wed Apr  3 23:01:03 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKUtils_Path.hxx>

#include <WOKernel_FileType.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Session.hxx>

#include <WOKAPI_Entity.hxx>

#include <WOKAPI_File.ixx>

//=======================================================================
//function : WOKAPI_File
//purpose  : 
//=======================================================================
 WOKAPI_File::WOKAPI_File()
   : mylocated(Standard_False)
{
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================
void WOKAPI_File::Set(const Handle(WOKernel_File)& afile)
{
  myfile = afile;
  mylocated = Standard_False;
}

//=======================================================================
//function : NestingEntity
//purpose  : 
//=======================================================================
WOKAPI_Entity WOKAPI_File::NestingEntity() const 
{
  WOKAPI_Entity result;

  if(myfile.IsNull()) return result;

  Handle(WOKernel_Entity) thenesting = myfile->Session()->GetEntity(myfile->Nesting());

  if(!thenesting.IsNull())
    {
      result.Set(thenesting);
    }
  return result;
}

//=======================================================================
//function : Type
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_File::Type() const 
{
  Handle(TCollection_HAsciiString) result;

  if(myfile.IsNull()) return result;

  result = myfile->TypeName();
  return result;
}

//=======================================================================
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_File::Name() const 
{
  Handle(TCollection_HAsciiString) result;

  if(myfile.IsNull()) return result;

  result = myfile->Name();
  return result;
}

//=======================================================================
//function : LocatorName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_File::LocatorName() const 
{
  Handle(TCollection_HAsciiString) result;

  if(myfile.IsNull()) return result;

  result = myfile->LocatorName();
  return result;
}

//=======================================================================
//function : UserPath
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_File::UserPath() const 
{
  Handle(TCollection_HAsciiString) result;

  if(myfile.IsNull()) return result;

  result = myfile->UserPathName(); 
  return result;
}

//=======================================================================
//function : Exists
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_File::Exists() const
{
  if(myfile.IsNull()) return Standard_False;
  myfile->GetPath();
  return myfile->Path()->Exists();
}

//=======================================================================
//function : IsLocalTo
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_File::IsLocalTo(const WOKAPI_Entity& aent) const 
{
  Handle(TCollection_HAsciiString) result;
  
  if(myfile.IsNull()) return Standard_False;
  
  if(NestingEntity().UserPath()->IsSameString(aent.UserPath())) return Standard_True;
  else return Standard_False;
}

//=======================================================================
//function : IsFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_File::IsFile() const 
{
  if(myfile.IsNull()) return Standard_False;
  
  return myfile->Type()->IsFile();
}

//=======================================================================
//function : IsDirectory
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_File::IsDirectory() const 
{
  if(myfile.IsNull()) return Standard_False;
  
  return myfile->Type()->IsDirectory();
}


//=======================================================================
//function : IsValid
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_File::IsValid() const
{
  if(myfile.IsNull()) return Standard_False;
  return Standard_True;
}

//=======================================================================
//function : IsDirectory
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_File::IsLocated() const 
{
  if(myfile.IsNull()) return Standard_False;
  
  return mylocated;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsDBMSDependent
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_File::IsDBMSDependent() const
{
  if(myfile.IsNull()) return Standard_False;

  const Handle(WOKernel_FileType)& atype = myfile->Type();

  if(atype.IsNull())  return Standard_False;

  return atype->IsDBMSDependent();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsStationDependent
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_File::IsStationDependent() const
{
  if(myfile.IsNull()) return Standard_False;

  const Handle(WOKernel_FileType)& atype = myfile->Type();

  if(atype.IsNull())  return Standard_False;

  return atype->IsStationDependent();
}

//=======================================================================
//function : Path
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_File::Path() const 
{
  Handle(TCollection_HAsciiString) path;
  if(myfile.IsNull())         return path;
  if(myfile->Path().IsNull()) return path;
  return myfile->Path()->Name();
}

//=======================================================================
//function : Locate
//purpose  : 
//=======================================================================
void WOKAPI_File::Locate(const WOKAPI_Locator& alocator) 
{
  if(!alocator.IsValid()) {mylocated=Standard_False; return;}

  if(!IsValid()) {mylocated=Standard_False; return;}
  if(IsLocated()) return;

  alocator.Locate(*this);
}

//=======================================================================
//function : Located
//purpose  : 
//=======================================================================
void WOKAPI_File::Located()
{
  mylocated=Standard_True;
}

//=======================================================================
//function : UnLocated
//purpose  : 
//=======================================================================
void WOKAPI_File::UnLocated()
{
  mylocated=Standard_False;
}
