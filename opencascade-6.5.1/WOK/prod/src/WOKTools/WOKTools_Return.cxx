// File:	WOKTools_Return.cxx
// Created:	Wed Aug  2 16:12:17 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKTools_Return.ixx>

#include <WOKTools_StringValue.hxx>
#include <WOKTools_EnvValue.hxx>
#include <WOKTools_ChDirValue.hxx>
#include <WOKTools_InterpFileValue.hxx>

#include <string.h>

//=======================================================================
//function : WOKTools_Return
//purpose  : 
//=======================================================================
WOKTools_Return::WOKTools_Return()
{
}

//=======================================================================
//function : Clear
//purpose  : 
//=======================================================================
void WOKTools_Return::Clear()  
{
  myargs.Nullify();
}

//=======================================================================
//function : AddStringValue
//purpose  : 
//=======================================================================
void WOKTools_Return::AddStringValue(const Standard_CString arg)
{
  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;
  myargs->Append(new WOKTools_StringValue(new TCollection_HAsciiString(arg)));
}

//=======================================================================
//function : AddStringValue
//purpose  : 
//=======================================================================
void WOKTools_Return::AddStringValue(const Handle(TCollection_HAsciiString)& arg)
{
  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;
  myargs->Append(new WOKTools_StringValue(arg));
}

//=======================================================================
//function : AddStringParameter
//purpose  : 
//=======================================================================
void WOKTools_Return::AddStringParameter(const Handle(TCollection_HAsciiString)& aname, 
					 const Handle(TCollection_HAsciiString)& avalue) 
{
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString;

  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;

  astr->AssignCat(aname);
  astr->AssignCat("=");
  if(avalue.IsNull() == Standard_True)
    {
      astr->AssignCat("Undefined!!!");
    }
  else
    {
      astr->AssignCat(avalue);
    }
  
  myargs->Append(new WOKTools_StringValue(astr));
}


//=======================================================================
//function : AddBooleanValue
//purpose  : 
//=======================================================================
void WOKTools_Return::AddBooleanValue(const Standard_Boolean abool)
{
  if(abool)
    {
      AddStringValue("1");
    }
  else
    {
      AddStringValue("0");
    }
}

//=======================================================================
//function : AddIntegerValue
//purpose  : 
//=======================================================================
void WOKTools_Return::AddIntegerValue(const Standard_Integer anint)
{
  AddStringValue(new TCollection_HAsciiString(anint));
}

//=======================================================================
//function : AddEnvironment
//purpose  : 
//=======================================================================
void WOKTools_Return::AddSetEnvironment(const Handle(TCollection_HAsciiString)& name, 
				     const Handle(TCollection_HAsciiString)& value)
{
  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;

  myargs->Append(new WOKTools_EnvValue(name, value));
}


//=======================================================================
//function : AddSetEnvironment
//purpose  : 
//=======================================================================
void WOKTools_Return::AddSetEnvironment(const Standard_CString name, const Standard_CString value)
{
  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;

  myargs->Append(new WOKTools_EnvValue(new TCollection_HAsciiString(name),
				       new TCollection_HAsciiString(value)));
}

//=======================================================================
//function : AddUnSetEnvironment
//purpose  : 
//=======================================================================
void WOKTools_Return::AddUnSetEnvironment(const Handle(TCollection_HAsciiString)& name)
{
  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;
  myargs->Append(new WOKTools_EnvValue(name));
}


//=======================================================================
//function : AddUnSetEnvironment
//purpose  : 
//=======================================================================
void WOKTools_Return::AddUnSetEnvironment(const Standard_CString name)
{
  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;
  myargs->Append(new WOKTools_EnvValue(new TCollection_HAsciiString(name)));
}

//=======================================================================
//function : AddChDir
//purpose  : 
//=======================================================================
void WOKTools_Return::AddChDir(const Handle(TCollection_HAsciiString)& path)
{
  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;
  myargs->Append(new WOKTools_ChDirValue(path));
}

//=======================================================================
//function : AddChDir
//purpose  : 
//=======================================================================
void WOKTools_Return::AddChDir(const Standard_CString path)
{
  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;
  myargs->Append(new WOKTools_ChDirValue(new TCollection_HAsciiString(path)));
}

//=======================================================================
//function : AddInterpFile
//purpose  : 
//=======================================================================
void WOKTools_Return::AddInterpFile(const Handle(TCollection_HAsciiString)& apath, const WOKTools_InterpFileType atype)
{
  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;
  myargs->Append(new WOKTools_InterpFileValue(apath, atype));
}

//=======================================================================
//function : AddInterpFile
//purpose  : 
//=======================================================================
void WOKTools_Return::AddInterpFile(const Standard_CString apath, const WOKTools_InterpFileType atype)
{
  if(myargs.IsNull() == Standard_True) myargs = new WOKTools_HSequenceOfReturnValue;
  myargs->Append(new WOKTools_InterpFileValue(new TCollection_HAsciiString(apath), atype));
}


//=======================================================================
//function : Value
//purpose  : 
//=======================================================================
Handle(WOKTools_ReturnValue) WOKTools_Return::Value(const Standard_Integer idx) const
{
  return myargs->Value(idx);
}


//=======================================================================
//function : Values
//purpose  : 
//=======================================================================
Handle(WOKTools_HSequenceOfReturnValue) WOKTools_Return::Values() const
{
  return myargs;
}

//=======================================================================
//function : Length
//purpose  : 
//=======================================================================
Standard_Integer WOKTools_Return::Length() const
{
  if(myargs.IsNull() == Standard_True) return 0;
  return myargs->Length();
}
