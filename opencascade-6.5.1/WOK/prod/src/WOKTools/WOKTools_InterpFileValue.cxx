

#include <WOKTools_InterpFileValue.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKTools_InterpFileValue
//purpose  : 
//=======================================================================
WOKTools_InterpFileValue::WOKTools_InterpFileValue(const Handle(TCollection_HAsciiString)& afile,
						   const WOKTools_InterpFileType atype)
  : myfile(afile), mytype(atype)
{
  SetType(WOKTools_InterpFile);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : FileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKTools_InterpFileValue::FileName(const WOKTools_InterpFileType atype,
								    const Handle(TCollection_HAsciiString)& abase)
{
  Handle(TCollection_HAsciiString) NULLRESULT, result = new TCollection_HAsciiString(abase);

  switch (atype) 
    {
    case WOKTools_BourneShell:
      result->AssignCat(".sh");
      break;
    case WOKTools_CShell:
      result->AssignCat(".csh");
      break;
    case WOKTools_KornShell:
      result->AssignCat(".ksh");
      break;
    case WOKTools_TclInterp:
      result->AssignCat(".tcl");
      break;
    case WOKTools_EmacsLisp:
      result->AssignCat(".el");
      break;
    case WOKTools_WNTCmd:
      result->AssignCat(".cmd");
      break;
    default:
      return NULLRESULT;
    }
  return result;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : FileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKTools_InterpFileValue::InterpFormat(const WOKTools_InterpFileType atype)
{
  Handle(TCollection_HAsciiString) NULLRESULT;

  switch (atype) 
    {
    case WOKTools_BourneShell:
      {
	static Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString("sh");
	return astr;
      }
    case WOKTools_CShell:
      {
	static Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString("csh");
	return astr;
      }
    case WOKTools_KornShell:
      {
	static Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString("ksh");
	return astr;
      }
    case WOKTools_TclInterp:
      {
	static Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString("tcl");
	return astr;
      }
    case WOKTools_EmacsLisp:
      {
	static Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString("emacs");
	return astr;
      }
    case WOKTools_WNTCmd:
      {
	static Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString("cmd");
	return astr;
      }
    default:
      return NULLRESULT;
    }
  return NULLRESULT;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : InterpType
//purpose  : 
//=======================================================================
WOKTools_InterpFileType WOKTools_InterpFileValue::InterpType(const Handle(TCollection_HAsciiString)& atype)
{
  if(!atype.IsNull())
    {
      Standard_CString astr = atype->ToCString();

      if(!strcmp(astr, "sh"))    return WOKTools_BourneShell;
      if(!strcmp(astr, "csh"))   return WOKTools_CShell;
      if(!strcmp(astr, "ksh"))   return WOKTools_KornShell;
      if(!strcmp(astr, "tcl"))   return WOKTools_TclInterp;
      if(!strcmp(astr, "emacs")) return WOKTools_EmacsLisp;
      if(!strcmp(astr, "cmd"))   return WOKTools_WNTCmd;
    }
  return WOKTools_CShell;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetFile
//purpose  : 
//=======================================================================
void WOKTools_InterpFileValue::SetFile(const Handle(TCollection_HAsciiString)& afile) 
{
  myfile = afile;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : File
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKTools_InterpFileValue::File() const
{
  return myfile;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetInterpType
//purpose  : 
//=======================================================================
void WOKTools_InterpFileValue::SetInterpType(const WOKTools_InterpFileType atype) 
{
  mytype = atype;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : InterpType
//purpose  : 
//=======================================================================
WOKTools_InterpFileType WOKTools_InterpFileValue::InterpType() const
{
  return mytype;
}



