// File:	WOKernel_FileType.cxx
// Created:	Wed Jun 28 17:32:21 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>

#include <EDL_API.hxx>
#include <EDL_Template.hxx>
#include <EDL_HSequenceOfVariable.hxx>
#include <EDL_Variable.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Param.hxx>


#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_FileTypeKeyWords.hxx>

#include <WOKernel_FileType.ixx>
#include <Standard_PCharacter.hxx>


//=======================================================================
//function : WOKernel_FileType
//purpose  : instantiates an Empty File Type
//=======================================================================
WOKernel_FileType::WOKernel_FileType() 
  : mystationdep(Standard_False), mydbmsdep(Standard_False), mynestingdep(Standard_False), 
    myentitydep(Standard_False), myfiledep(Standard_False), myisrep(Standard_False)
{
}

//=======================================================================
//function : WOKernel_FileType
//purpose  : intantiates a file types
//=======================================================================
WOKernel_FileType::WOKernel_FileType(const Handle(TCollection_HAsciiString)& aname,
				     const EDL_Template& atemplate)
  : myname(aname), mytemplate(atemplate),
    mystationdep(Standard_False), mydbmsdep(Standard_False), mynestingdep(Standard_False), 
    myentitydep(Standard_False), myfiledep(Standard_False), myisrep(Standard_False)
{
  GetDependency();
}


//=======================================================================
//function : ComputePath
//purpose  : 
//=======================================================================
Handle( TCollection_HAsciiString )
 WOKernel_FileType :: ComputePath (
                       const WOKUtils_Param&                     params,
                       const Handle( TCollection_HAsciiString )& afilename
                      ) {

 static Handle( TCollection_HAsciiString ) javafile =
  new TCollection_HAsciiString ( "javafile" );

 Standard_Boolean                   fJava = Standard_False;
 Standard_PCharacter                s;
 Handle( TCollection_HAsciiString ) result;
 Handle( EDL_HSequenceOfVariable  ) vars = new EDL_HSequenceOfVariable ();

 if (  IsFileDependent () && !afilename.IsNull ()  )

  params.Set (  ( Standard_CString )FILEVAR, afilename -> ToCString ()  );

 Handle( TColStd_HSequenceOfHAsciiString ) needed = mytemplate.GetVariableList ();

 for (  Standard_Integer i = 1; i <= needed -> Length (); ++i  ) {

  const Standard_CString name = needed -> Value ( i ) -> ToCString ();

  if (  params.myapi -> IsDefined ( name )  ) {

   if (  myname -> IsSameString ( javafile ) &&
         !strcmp ( name, ENTITYVAR )
   ) {

    EDL_Variable     v = params.myapi -> GetVariable ( name );
    Standard_PCharacter p = s = (Standard_PCharacter)v.GetValue ();

    while ( *p ) { if ( *p == '.' ) *p = '/'; ++p; }

    vars -> Append ( v );
    fJava = Standard_True;

   } else vars -> Append (  params.myapi -> GetVariable ( name )  );

  } else {

   ErrorMsg() << "WOKernel_FileType::ComputePath"
            << "Needed argument "
            << name
            << " for type "
            << Name ()
            << " is not setted"
            << endm;

   return result;

  }  // end else

 }  // end for

 mytemplate.Eval ( vars );

 Handle( TColStd_HSequenceOfAsciiString ) resseq = mytemplate.GetEval ();

 if (  resseq.IsNull ()  )

  ErrorMsg() << "WOKernel_FileType::ComputePath"
           << "Type "
           << Name ()
           << " could not be evaluated"
           << endm;

 else {

  if (  resseq -> Length () != 1  )

   WarningMsg() << "WOKernel_FileType::ComputePath"
              << "Type "
              << Name ()
              << " evaluates to more than one line : ignoring others"
              << endm;

  result = new TCollection_HAsciiString (  resseq -> Value ( 1 )  );

 }  // end else

 if ( fJava ) while ( *s ) { if ( *s == '/' ) *s = '.'; ++s; }

 return result;

}  // WOKernel_FileType :: ComputePath

//=======================================================================
//function : GetDefinition
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_FileType::GetDefinition() const
{
  return new TCollection_HAsciiString(Template().GetLine(1));
}

//=======================================================================
//function : GetArguments
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_FileType::GetArguments() const
{
  return Template().GetVariableList();
}

//=======================================================================
//function : GetDependency
//purpose  : 
//=======================================================================
void WOKernel_FileType::GetDependency()
{
  Standard_Integer i;
  static Standard_Integer nestlen = strlen(NESTING_PREFIX);
  static Standard_Integer entlen  = strlen(ENTITY_PREFIX);
  Standard_CString astr;

  Handle(TColStd_HSequenceOfHAsciiString) aseq = mytemplate.GetVariableList();
  
  UnSetStationDependent();
  UnSetDBMSDependent();
  UnSetNestingDependent();
  UnSetEntityDependent();
  UnSetFileDependent();

  for(i=1; i<=aseq->Length(); i++)
    {
      astr = aseq->Value(i)->ToCString();
      if(!strncmp(astr, NESTING_PREFIX, nestlen))
	{
	  SetNestingDependent();
	  if(!strcmp(astr, NESTING_STATION))   SetStationDependent();
	  else if(!strcmp(astr, NESTING_DBMS)) SetDBMSDependent();
	  else if(!strcmp(astr, NESTING_DBMS_STATION)) {SetDBMSDependent();SetStationDependent();}
	}
      else if(!strncmp(astr, ENTITY_PREFIX, entlen))
	{
	  SetEntityDependent();
	  if(!strcmp(astr, ENTITY_STATION))   SetStationDependent();
	  else if(!strcmp(astr, ENTITY_DBMS)) SetDBMSDependent();
	  else if(!strcmp(astr, ENTITY_DBMS_STATION)) {SetDBMSDependent();SetStationDependent();}
	}
      else
	{
	  if(!strcmp(astr, STATIONVAR))      SetStationDependent();
	  else if(!strcmp(astr, DBMSVAR))    SetDBMSDependent();
	  else if(!strcmp(astr, ENTITYVAR))  SetEntityDependent();
	  else if(!strcmp(astr, NESTINGVAR)) SetNestingDependent();
	  else if(!strcmp(astr, FILEVAR))    SetFileDependent();
	}
    }
  return;
}

//=======================================================================
//function : Station Dependency
//purpose  : 
//=======================================================================
void WOKernel_FileType::SetStationDependent()   {mystationdep = Standard_True;}
void WOKernel_FileType::UnSetStationDependent() {mystationdep = Standard_False;}

//=======================================================================
//function : DBMS Dependency
//purpose  : 
//=======================================================================
void WOKernel_FileType::SetDBMSDependent()      {mydbmsdep = Standard_True;}
void WOKernel_FileType::UnSetDBMSDependent()    {mydbmsdep = Standard_False;}

//=======================================================================
//function : Nesting Dependency
//purpose  : 
//=======================================================================
void WOKernel_FileType::SetNestingDependent()   {mynestingdep = Standard_True;}
void WOKernel_FileType::UnSetNestingDependent() {mynestingdep = Standard_False;}

//=======================================================================
//function : Unit Dependency
//purpose  : 
//=======================================================================
void WOKernel_FileType::SetEntityDependent()      {myentitydep = Standard_True;}
void WOKernel_FileType::UnSetEntityDependent()    {myentitydep = Standard_False;}

//=======================================================================
//function : File Dependency
//purpose  : 
//=======================================================================
void WOKernel_FileType::SetFileDependent()      {myfiledep = Standard_True;}
void WOKernel_FileType::UnSetFileDependent()    {myfiledep = Standard_False;}



//=======================================================================
//function : Directory
//purpose  : 
//=======================================================================
void WOKernel_FileType::Directory() {myisrep = Standard_True; }
void WOKernel_FileType::File()      {myisrep = Standard_False;}

//=======================================================================
//function : GetDirectory
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_FileType::GetDirectory(const WOKUtils_Param& params) 
{
  Standard_Integer apos;
  Handle(TCollection_HAsciiString) result, nullHandle, apath, dollars = new TCollection_HAsciiString("$$$$$$$$$$$$$$$$$$");

  if(IsDirectory() && !IsFileDependent())
    {
      result = ComputePath(params, nullHandle); 
      return result;
    }

  apath = ComputePath(params, dollars);
  apos = apath->Location(dollars, 1, apath->Length());
  
  if(apos != 0)
    {
      apath = apath->SubString(1, apos);
    }

  apos = apath->SearchFromEnd("/");

  if(apos != -1)
    {
      apath = apath->SubString(1, apos-1);
    }
  else apath.Nullify();

  return apath;
}

//=======================================================================
//function : GetFile
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_FileType::GetFile(const WOKUtils_Param& params) 
{
  Handle(TCollection_HAsciiString) result, nullHandle, apath, dollars = new TCollection_HAsciiString("$$$$$$$$$$$$$$$$$$");

  if(IsFile() && !IsFileDependent())
    {
      result = ComputePath(params, nullHandle);
    }
  return result;
}
