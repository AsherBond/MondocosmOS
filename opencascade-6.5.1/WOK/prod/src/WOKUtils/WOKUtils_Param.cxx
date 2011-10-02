// File:	WOKUtils_Param.cxx
// Created:	Tue May 30 09:17:22 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <EDL_Template.hxx>
#include <EDL_Variable.hxx>
#include <EDL_API.hxx>
#include <EDL_DataMapIteratorOfMapOfVariable.hxx>
#include <EDL_DataMapIteratorOfMapOfTemplate.hxx>


#include <WOKTools_Messages.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_HSequenceOfPath.hxx>
#include <WOKUtils_ParamItem.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_SearchList.hxx>
#include <WOKUtils_WOKVersion.hxx>
#include <WOKUtils_Param.ixx>
#include <Standard_PCharacter.hxx>

#ifdef WNT
# include <windows.h>
#endif

#if defined(HAVE_UNISTD_H) 
# include <unistd.h>
#endif

//=======================================================================
//function : WOKUtils_Param
//purpose  : Simply creates the param
//=======================================================================
WOKUtils_Param :: WOKUtils_Param () {

 myapi = new EDL_API ();
 SetBasicVariables ();

}  // end WOKUtils_Param :: WOKUtils_Param

void WOKUtils_Param :: SetBasicVariables ( void ) {

 myapi -> AddVariable ( "%WOK_VERSION", WOK_VERSION );
#ifdef WNT
 myapi -> AddVariable ( "%Station",     "wnt"       );
#elif defined(__sun) || defined(SOLARIS)
 myapi -> AddVariable ( "%Station",     "sun"       );
#elif defined(linux) || defined(LIN)
 myapi -> AddVariable ( "%Station",     "lin"       );
#elif defined(__osf__) || defined(DECOSF1)
 myapi -> AddVariable ( "%Station",     "ao1"       );
#elif defined(__hpux) || defined(HPUX)
 myapi -> AddVariable ( "%Station",     "hp"       );
#elif defined(_AIX)
 myapi -> AddVariable ( "%Station",     "aix"       );
#elif defined(__FreeBSD__)
 myapi -> AddVariable ( "%Station",     "bsd"       );
#else
 myapi -> AddVariable ( "%Station",     "def"       );
#endif  // WNT

}  // end WOKUtils_Param :: SetBasicVariables

//=======================================================================
//function : Clear
//purpose  : 
//=======================================================================
void WOKUtils_Param::Clear()
{
  myapi = new EDL_API ();
  SetBasicVariables ();
  mysubs.Nullify();
}

//=======================================================================
//function : SetSearchDirectories
//purpose  : 
//=======================================================================
void WOKUtils_Param::SetSearchDirectories(const Handle(TColStd_HSequenceOfAsciiString)& aseq) 
{
  Standard_Integer i;

  myapi->ClearIncludes();

  for(i=1; i<=aseq->Length(); i++)
    {
      myapi->AddIncludeDirectory(aseq->Value(i).ToCString());
    }
  
  return;
}

void WOKUtils_Param :: SetSearchDirectories (  const Handle( WOKUtils_SearchList )& aList  ) {

 Standard_Integer                   i;
 Handle( WOKUtils_HSequenceOfPath ) pathList = aList -> List ();

 myapi -> ClearIncludes ();

 for (  i = 1; i <= pathList -> Length (); ++i  )

  myapi -> AddIncludeDirectory (
            pathList -> Value ( i ) -> Name () -> ToCString ()
           );

}  // end WOKUtils_Param :: SetSearchDirectories

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SearchFile
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) WOKUtils_Param::SearchFile(const Handle(TCollection_HAsciiString)& aname) const
{
  Handle(WOKUtils_Path) apath;
  Handle(TColStd_HSequenceOfAsciiString) dirs = SearchDirectories();
  Standard_Integer i;

  for(i=1; i<=dirs->Length(); i++)
    {
      Handle(WOKUtils_Path) apath = new WOKUtils_Path(dirs->Value(i).ToCString(), aname->ToCString());

      if (apath->Exists()) return apath;
    }
  return apath;
}

//=======================================================================
//function : SearchDirectories
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfAsciiString) WOKUtils_Param::SearchDirectories() const
{
  return myapi->GetIncludeDirectory();
}

//=======================================================================
//function : SetSubClasses
//purpose  : 
//=======================================================================
void WOKUtils_Param::SetSubClasses(const Handle(TColStd_HSequenceOfAsciiString)& aseq) 
{
  mysubs = aseq;
}

//=======================================================================
//function : SubClasses
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfAsciiString) WOKUtils_Param::SubClasses() const
{
  return mysubs;
}

//=======================================================================
//function : IsSet
//purpose  : 
//=======================================================================
Standard_Boolean WOKUtils_Param::IsSet(const Standard_CString aname) const
{
  return myapi->IsDefined(aname);
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================
void WOKUtils_Param::Set(const Standard_CString aname, const Standard_CString avalue) const
{
  myapi->AddVariable(aname, avalue);
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================
void WOKUtils_Param::Set(const Handle(WOKUtils_HSequenceOfParamItem)& aseq) const
{
  Standard_Integer i;

  for(i=1; i<=aseq->Length(); i++)
    {
      myapi->AddVariable(aseq->Value(i).Name()->ToCString(), aseq->Value(i).Value()->ToCString());
    }
}



//=======================================================================
//function : UnSet
//purpose  : 
//=======================================================================
void WOKUtils_Param::UnSet(const Standard_CString aname) const
{
  if(myapi->IsDefined(aname))
    myapi->RemoveVariable(aname);
}

//=======================================================================
//function : Value
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUtils_Param::Value(const Standard_CString  aname, const Standard_Boolean usesubs) const 
{
  Handle(TCollection_HAsciiString) result;
  
  if(!myapi->IsDefined(aname)) 
    {
      Handle(TCollection_HAsciiString) aclass;
      
      aclass = ParamClass(aname);
      
      if(!aclass.IsNull()) 
	{
	  if(usesubs) 
	    {
	      if(!LoadParamClass(aclass->ToCString(), mysubs)) return result;
	    } 
	  else 
	    {
	      if(!LoadParamClass(aclass->ToCString())) return result;
	    }
	}
    }

  if(aname[0] == '%')
    {
      if(myapi->IsDefined(aname))
	{
	  result =  myapi->GetVariableValue(aname);
	}
    }
  else
    {
      if(myapi->IsDefined(aname))
	{
	  EDL_Template atempl = myapi->GetTemplate(aname);
	  Standard_Integer i=1;
	  Standard_CString astr;

	  result = new TCollection_HAsciiString;

	  astr = atempl.GetLine(i);

	  while(astr != NULL)
	    {
	      result->AssignCat(astr);
	      i++;
	      astr = atempl.GetLine(i);
	    }
	}
    }
  return result;
}

//=======================================================================
//function : Eval
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUtils_Param::Eval(const Standard_CString  aname, const Standard_Boolean usesubs) const 
{

  Handle(TCollection_HAsciiString) result;
  
  if(!myapi->IsDefined(aname)) 
    {
      Handle(TCollection_HAsciiString) aclass;
      
      aclass = ParamClass(aname);
      
      if(!aclass.IsNull()) 
	{
	  if(usesubs) 
	    {
	      if(!LoadParamClass(aclass->ToCString(), mysubs)) return result;
	    } 
	  else 
	    {
	      if(!LoadParamClass(aclass->ToCString())) return result;
	    }
	}
    }

  if(aname[0] == '%')
    {
      if(myapi->IsDefined(aname))
	{
	  result =  myapi->GetVariableValue(aname);
	}
    }
  else
    {
      if(myapi->IsDefined(aname))
	{
	  myapi->Apply("%PARAM_RESULT", aname);
	  result =  myapi->GetVariableValue("%PARAM_RESULT");
	  //myapi->RemoveVariable("%PARAM_RESULT");
	}
    }
  return result;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsClassVisible
//purpose  : 
//=======================================================================
Standard_Boolean WOKUtils_Param::IsClassVisible(const Standard_CString aclass) const
{
  Handle(TCollection_HAsciiString) afile = ClassFile(aclass);

  return IsFileVisible(afile);
}


//=======================================================================
//function : IsFileVisible
//purpose  : 
//=======================================================================
Standard_Boolean WOKUtils_Param::IsFileVisible(const Handle(TCollection_HAsciiString)& afile) const
{
  Standard_Integer i;
  Handle(WOKUtils_Path) apath;
  Handle(TColStd_HSequenceOfAsciiString) dirs = SearchDirectories();

  for(i=1; i<=dirs->Length(); i++)
    {
      apath = new WOKUtils_Path(dirs->Value(i).ToCString(), afile->ToCString());

      if(apath->Exists()) return Standard_True;
    }
  return Standard_False;
}

//=======================================================================
//function : VisiblePath
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) WOKUtils_Param::VisiblePath(const Handle(TCollection_HAsciiString)& afile) const
{
  Standard_Integer i;
  Handle(WOKUtils_Path) apath, nullpath;
  Handle(TColStd_HSequenceOfAsciiString) dirs = SearchDirectories();

  for(i=1; i<=dirs->Length(); i++)
    {
      apath = new WOKUtils_Path(dirs->Value(i).ToCString(), afile->ToCString());

      if(apath->Exists()) return apath;
    }
  return nullpath;
}

//=======================================================================
//function : ParamClass
//purWOKMakepose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUtils_Param::ParamClass(const Standard_CString aparam) const
{
  TCollection_AsciiString name(aparam);
  Handle(TCollection_HAsciiString) result;
  Standard_Integer deb, fin;

  if(name.Value(1) == '%')  deb = 2;
  else                      deb = 1;

  if((fin = name.Search("_")) != -1)
    {
      result = new TCollection_HAsciiString(name.SubString(deb, fin -1));
    }

  return result;
}

//=======================================================================
//function : ClassLoadFlag
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUtils_Param::ClassLoadFlag(const Standard_CString aclass) const
{
  TCollection_AsciiString name;
  Handle(TCollection_HAsciiString) result;
  
  name.AssignCat("%");
  name.AssignCat(aclass);
  name.AssignCat("_EDL");

  result = new TCollection_HAsciiString(name);
  return result;
}

//=======================================================================
//function : ClassLoadFlag
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUtils_Param::ClassSubLoadFlag(const Standard_CString aclass, const Standard_CString asub) const
{
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString("%");
  
  result->AssignCat(asub);
  result->AssignCat("_");
  result->AssignCat(aclass);
  result->AssignCat("_EDL");
  return result;
}

//=======================================================================
//function : ClassFile
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUtils_Param::ClassFile(const Standard_CString aclass) const
{
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString(aclass);
  result->AssignCat(".edl");
  return result;
}

//=======================================================================
//function : ClassSubFile
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUtils_Param::ClassSubFile(const Standard_CString aclass, const Standard_CString asub) const
{
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString(asub);
  Standard_CString thedir = strchr(asub, '@');

  if(thedir)
  {
    Handle(TCollection_HAsciiString) res = new TCollection_HAsciiString(&(thedir[1]));
    result->Trunc(thedir-asub);
    res->AssignCat("/");
    res->AssignCat(result);
    result = res;
  }

  result->AssignCat("_");
  result->AssignCat(aclass);
  result->AssignCat(".edl");
  return result;
}

//=======================================================================
//function : LoadFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKUtils_Param::LoadFile(const Handle(TCollection_HAsciiString)& afile, const Standard_Boolean filemaynotexist) const
{
  switch(myapi->Execute(afile->ToCString()))
    {
    case EDL_NORMAL:
      return Standard_True;
    case EDL_SYNTAXERROR:
      ErrorMsg() << "WOKUtils_Param::LoadParamClass"
	       << "EDL Syntax errors occured while loading file : " << afile << endm;
      break;
    case EDL_VARNOTFOUND:
      ErrorMsg() << "WOKUtils_Param::LoadParamClass"
	       << "Variable not found while loading file : " << afile << endm;
      break;
    case EDL_TEMPMULTIPLEDEFINED:
    case EDL_TEMPLATENOTDEFINED:
      ErrorMsg() << "WOKUtils_Param::LoadParamClass"
	       << "Template not found while loading file : " << afile << endm;
      break;
    case EDL_LIBRARYNOTFOUND:
    case EDL_LIBNOTOPEN:
      ErrorMsg() << "WOKUtils_Param::LoadParamClass"
	       << "Library error while loading file : " << afile << endm;
      break;
    case EDL_FUNCTIONNOTFOUND:
      ErrorMsg() << "WOKUtils_Param::LoadParamClass"
	       << "Function not found while loading file : " << afile << endm;
      break;
    case EDL_FILEOPENED:
    case EDL_FILENOTOPENED:
      if(filemaynotexist) return Standard_True;
      ErrorMsg() << "WOKUtils_Param::LoadParamClass"
	       << "File " << afile << " could not be opened" << endm;
      break;
    case EDL_TOOMANYINCLUDELEVEL:
      ErrorMsg() << "WOKUtils_Param::LoadParamClass"
	       << "Too many include levels while loading file : " << afile << endm;
      break;
    case EDL_FILENOTFOUND:
      ErrorMsg() << "WOKUtils_Param::LoadParamClass"
	       << "File not found : " << afile << endm;
      break;
    }
  return Standard_False;
}

//=======================================================================
//function : LoadParamClass
//purpose  : 
//=======================================================================
Standard_Boolean WOKUtils_Param::LoadParamClass(const Standard_CString aclass) const
{
  if(!myapi->IsDefined(ClassLoadFlag(aclass)->ToCString()))
    {
      WOK_TRACE {
	VerboseMsg()("WOK_PARAM") << "WOKUtils_Param::LoadParamClass"
				<< "Loading class : " << aclass << endm;
      }
      return LoadFile(ClassFile(aclass));
    }
  return Standard_True;
}

//=======================================================================
//function : LoadParamClass
//purpose  : 
//=======================================================================
Standard_Boolean WOKUtils_Param::LoadParamClass(const Standard_CString aclass, const Standard_CString asub) const
{
  TCollection_AsciiString astr;
  Handle(TCollection_HAsciiString) thesubclass, thefile, theflag;

  thefile = ClassSubFile(aclass, asub);
  theflag = ClassSubLoadFlag(aclass, asub);

  if(!myapi->IsDefined(theflag->ToCString()))
    {
      WOK_TRACE {
	VerboseMsg()("WOK_PARAM") << "WOKUtils_Param::LoadParamClass"
				<< "Loading subclass : " << asub << "_" << aclass << endm;
      }
      Standard_CString tok = strchr(asub,'@');
      if(tok)
	{
#ifndef WNT
	  if( !access(thefile->ToCString(), F_OK) ) 
#else
	    if ( GetFileAttributes(thefile->ToCString() ) != 0xFFFFFFFF ) 
#endif	      
	      return LoadFile(thefile, Standard_True);
	}
      else
	return LoadFile(thefile, Standard_True);
    }
  return Standard_True;
}

//=======================================================================
//function : LoadParamClass
//purpose  : 
//=======================================================================
Standard_Boolean WOKUtils_Param::LoadParamClass(const Standard_CString aclass, const Handle(TColStd_HSequenceOfAsciiString)& asubseq) const 
{
  Standard_Integer i;

  if(!LoadParamClass(aclass)) return Standard_False;

  if(!asubseq.IsNull())
    {
      for(i=1; i<=asubseq->Length(); i++)
	{
	  if(!LoadParamClass(aclass, asubseq->Value(i).ToCString())) return Standard_False;
	}
    }
  return Standard_True;
}

//=======================================================================
//function : GetClassValues
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKUtils_Param::GetClassValues(const Standard_CString aclass) const
{
  Handle(WOKUtils_HSequenceOfParamItem) result = new WOKUtils_HSequenceOfParamItem;
  TCollection_AsciiString varprefix;
  TCollection_AsciiString tempprefix;
  Standard_Integer varlen, templen;

  varprefix.AssignCat("%");
  varprefix.AssignCat(aclass);
  varprefix.AssignCat("_");
  varlen = varprefix.Length();

  tempprefix.AssignCat(aclass);
  tempprefix.AssignCat("_");
  templen = tempprefix.Length();

  EDL_DataMapIteratorOfMapOfVariable varit(myapi->GetVariableIterator());

  while(varit.More())
    {
      const EDL_Variable& avar = varit.Value();
      
      if(!strncmp(avar.GetName(), varprefix.ToCString(), varlen))
	{
	  result->Append(WOKUtils_ParamItem(new TCollection_HAsciiString(avar.GetName()), 
					    new TCollection_HAsciiString(avar.GetValue())));
	}

      varit.Next();
    }

  EDL_DataMapIteratorOfMapOfTemplate tempit(myapi->GetTemplateIterator());
  
  while(tempit.More())
    {
      const EDL_Template& atemp = tempit.Value();

      if(!strncmp(atemp.GetName(), tempprefix.ToCString(), templen))
	{
	  result->Append(WOKUtils_ParamItem(new TCollection_HAsciiString(atemp.GetName()), 
					    new TCollection_HAsciiString));
	}
      tempit.Next();
    }

  return result;
}

//=======================================================================
//function : GetArguments
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKUtils_Param::GetArguments(const Standard_CString aname) const
{
  if(aname[0] == '%') 
    {
      Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;
      return result;
    }

  return myapi->GetTemplate(aname).GetVariableList();
}

//=======================================================================
//function : GetArguments
//purpose  : 
//=======================================================================
void WOKUtils_Param::GetArguments(const Standard_CString aname,
				  const Handle(TColStd_HSequenceOfHAsciiString)& result,
				  WOKTools_MapOfHAsciiString& amap ) const
{
  Standard_Integer i;
  Handle(TColStd_HSequenceOfHAsciiString) argseq = GetArguments(aname);
  Handle(TCollection_HAsciiString) anarg;

  for(i=1; i<=argseq->Length(); i++)
    {
      anarg = argseq->Value(i);
      if(!amap.Contains(anarg))
	{
	  result->Append(anarg);
	  amap.Add(anarg);
	}
    }
  return;
}


//=======================================================================
//function : Write
//purpose  : 
//=======================================================================
Standard_Boolean WOKUtils_Param::Write(const Handle(WOKUtils_Path)& apath, 
				       const Handle(TColStd_HSequenceOfHAsciiString)& somevars) const
{
  Handle(TCollection_HAsciiString) astr;
  Standard_Integer i;

  LoadParamClass("EDL");

  if(myapi->OpenFile("EDLFILE", apath->Name()->ToCString()) != EDL_NORMAL) return Standard_False;

  myapi->AddVariable("%FileName"   , apath->FileName()->ToCString());
  myapi->AddVariable("%ParamClass" , apath->BaseName()->ToCString());

  myapi->Apply("%EDL_RESULT", "EDL_FileHeader");
  
  myapi->WriteFile("EDLFILE", "%EDL_RESULT");

  for(i=1; i<=somevars->Length(); i++)
    {
      myapi->AddVariable("%Name",  somevars->Value(i)->ToCString());
      myapi->AddVariable("%Value", myapi->GetVariableValue(somevars->Value(i)->ToCString())->ToCString());
      myapi->Apply("%EDL_RESULT", "EDL_SetLine");
      myapi->WriteFile("EDLFILE", "%EDL_RESULT");
    }
  
  myapi->Apply("%EDL_RESULT", "EDL_FileFooter");
  myapi->WriteFile("EDLFILE", "%EDL_RESULT");

  myapi->CloseFile("EDLFILE");

  myapi->RemoveVariable("%FileName");
  myapi->RemoveVariable("%ParamClass");
  myapi->RemoveVariable("%Name");
  myapi->RemoveVariable("%Value");
  myapi->RemoveVariable("%EDL_RESULT");

  return Standard_True;
}


//=======================================================================
//function : Write
//purpose  : 
//=======================================================================
Standard_Boolean WOKUtils_Param::Write(const Handle(WOKUtils_Path)& apath, 
				       const Handle(WOKUtils_HSequenceOfParamItem)& somevars) const
{
  Handle(TCollection_HAsciiString) astr;
  Standard_Integer i;
  
  LoadParamClass("EDL");
  
  if(myapi->OpenFile("EDLFILE", apath->Name()->ToCString())) return Standard_False;

  myapi->AddVariable("%FileName"   , apath->FileName()->ToCString());
  myapi->AddVariable("%ParamClass" , apath->BaseName()->ToCString());

  myapi->Apply("%EDL_RESULT", "EDL_FileHeader");
  
  myapi->WriteFile("EDLFILE", "%EDL_RESULT");

  for(i=1; i<=somevars->Length(); i++)
    {
      myapi->AddVariable("%Name",  somevars->Value(i).Name()->ToCString());
      myapi->AddVariable("%Value", somevars->Value(i).Value()->ToCString());
      myapi->Apply("%EDL_RESULT", "EDL_SetLine");
      myapi->WriteFile("EDLFILE", "%EDL_RESULT");
    }
  
  myapi->Apply("%EDL_RESULT", "EDL_FileFooter");
  myapi->WriteFile("EDLFILE", "%EDL_RESULT");

  myapi->CloseFile("EDLFILE");

  myapi->RemoveVariable("%FileName");
  myapi->RemoveVariable("%ParamClass");
  myapi->RemoveVariable("%Name");
  myapi->RemoveVariable("%Value");
  myapi->RemoveVariable("%EDL_RESULT");

  return Standard_True;
}
