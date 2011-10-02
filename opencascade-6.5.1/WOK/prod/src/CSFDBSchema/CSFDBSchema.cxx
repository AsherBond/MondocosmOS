// CLE : extracteur de schema pour la CSFDB (1996 : v1)
//

#include <stdio.h>

#include <EDL_API.hxx>
#include <WOKTools_Messages.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <MS_MetaSchema.hxx>
#include <Standard_ErrorHandler.hxx>
#include <Standard_NoSuchObject.hxx>
#include <TCollection_HAsciiString.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <MS_StdClass.hxx>
#include <MS_Package.hxx>
#include <MS.hxx>
#include <MS_Schema.hxx>
#include <MS_Error.hxx>
#include <MS_Field.hxx>
#include <MS_HSequenceOfField.hxx>
#include <MS_Field.hxx>
#include <MS_InstClass.hxx>
#include <MS_Enum.hxx>
#include <MS_Alias.hxx>
#include <MS_PrimType.hxx>
#include <TColStd_HSequenceOfInteger.hxx>

extern "C" {

  Handle(TColStd_HSequenceOfHAsciiString) Standard_EXPORT CSFDBSchema_TemplatesUsed();
  
  void Standard_EXPORT CSFDBSchema_Extract(const Handle(MS_MetaSchema)& ams,
			   const Handle(TCollection_HAsciiString)& aname,
			   const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
			   const Handle(TCollection_HAsciiString)& outdir,
			   const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
			   const Standard_CString);
  
}

WOKTools_MapOfHAsciiString AddMap,RemoveMap;

// Standard Extractor API : list the EDL files used by this program
//
Handle(TColStd_HSequenceOfHAsciiString) CSFDBSchema_TemplatesUsed()
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;
  
  result->Append(new TCollection_HAsciiString("CSFDBSchema_Template.edl"));

  return result;
}

// write the content of a variable into a file
//
void CSFDBSchema_WriteFile(const Handle(EDL_API)& api,
			   const Handle(TCollection_HAsciiString)& aFileName,
			   const Standard_CString var)
{
  // ...now we write the result
  //
  api->OpenFile("HTFile",aFileName->ToCString());
  api->WriteFile("HTFile",var);
  api->CloseFile("HTFile");
}

Handle(EDL_API)&  CSFDBSchema_LoadTemplate(const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
					   const Handle(TCollection_HAsciiString)& outdir,
					   const Standard_CString DBMS)
{
  static Handle(EDL_API)  api = new EDL_API;
  static Standard_Boolean alreadyLoaded = Standard_False;

  api->ClearVariables();

  if (!alreadyLoaded) {
    alreadyLoaded = Standard_True;

    for(Standard_Integer i = 1; i <= edlsfullpath->Length(); i++) {
      api->AddIncludeDirectory(edlsfullpath->Value(i)->ToCString());
    }

    if (api->Execute("CSFDBSchema_Template.edl") != EDL_NORMAL) {
      ErrorMsg() << "CSFDBSchema" << "unable to load : CSBDBSchema_Template.edl" << endm;
      Standard_NoSuchObject::Raise();
    } 
  }
  
  // full path of the destination directory
  //
  api->AddVariable("%FullPath",outdir->ToCString());

  // DBMS extraction type
  //
  api->AddVariable("%CPPEXTDBMS",DBMS);

  return api;
}

// Build the schema's header file (<schema>.hxx)
//
void CSFDBSchema_BuildHeader(const Handle(MS_MetaSchema)& aMeta,
			     const Handle(EDL_API)& api,
			     const Handle(TColStd_HSequenceOfHAsciiString)& classList,
			     const Handle(TCollection_HAsciiString)& schName,
			     const Handle(TColStd_HSequenceOfHAsciiString)& outfile)
{
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString) aFileName = new TCollection_HAsciiString(api->GetVariableValue("%FullPath"));
  Handle(TCollection_HAsciiString) proto  = new TCollection_HAsciiString;
  Standard_Integer                 i;
  Handle(MS_StdClass)              c;

  
  aFileName->AssignCat(schName);
  aFileName->AssignCat(".hxx");

  api->OpenFile("HTFile",aFileName->ToCString());

  // common includes for schemas
  //
  api->AddVariable("%Type","Storage_Schema");
  api->Apply("%SHeaderInc","CSFDBInclude");
  api->WriteFile("HTFile","%SHeaderInc");
  api->AddVariable("%Type","Storage_BaseDriver");
  api->Apply("%SHeaderInc","CSFDBInclude");
  api->WriteFile("HTFile","%SHeaderInc");
  api->AddVariable("%Type","Storage_CallBack");
  api->Apply("%SHeaderInc","CSFDBInclude");
  api->WriteFile("HTFile","%SHeaderInc");
  api->AddVariable("%Type","Storage_Macros");
  api->Apply("%SHeaderInc","CSFDBInclude");
  api->WriteFile("HTFile","%SHeaderInc");

  Handle(MS_Type)                  tmpType;

  for (i = 1; i<= classList->Length(); i++) {
    tmpType = aMeta->GetType(classList->Value(i));
    c = *((Handle(MS_StdClass)*)&tmpType);

    if (c->IsPersistent()) {
      api->AddVariable("%Type",classList->Value(i)->ToCString());
      api->Apply("%SHeaderInc","CSFDBHandleDeclaration");
      api->WriteFile("HTFile","%SHeaderInc");
    } 
    else { 
      api->AddVariable("%Type",classList->Value(i)->ToCString());
      api->Apply("%SHeaderInc","CSFDBClassDeclaration");
      api->WriteFile("HTFile","%SHeaderInc");
    }
  }

  api->Apply("%SHeaderInc","CSFDBSchemaClassProto");
  api->WriteFile("HTFile","%SHeaderInc");
  api->CloseFile("HTFile");

  outfile->Append(aFileName);
}


void CSFDBSchema_BuildAddTypeSelection(const Handle(MS_MetaSchema)& aMeta,
				       const Handle(EDL_API)& api,
				       const Handle(TColStd_HSequenceOfHAsciiString)& classList)
{
  Standard_Integer                 i;
  Handle(MS_StdClass)              c;

  api->Apply("%SHeaderInc","CSFDBADDTSBegin");
  api->WriteFile("HTFile","%SHeaderInc");

  for (i = 1; i<= classList->Length(); i++) {
    c = Handle(MS_StdClass)::DownCast(aMeta->GetType(classList->Value(i)));

    if (c->IsPersistent()) {
      api->AddVariable("%Type",classList->Value(i)->ToCString());
      api->Apply("%SHeaderInc","CSFDBADDTSPart");
      api->WriteFile("HTFile","%SHeaderInc");
    }
  }

  api->Apply("%SHeaderInc","CSFDBADDTSEnd");
  api->WriteFile("HTFile","%SHeaderInc");
}

void CSFDBSchema_BuildSchemaTypeList(const Handle(MS_MetaSchema)& aMeta,
				     const Handle(EDL_API)& api,
				     const Handle(TColStd_HSequenceOfHAsciiString)& classList)
{
  Standard_Integer                 i;
  Handle(MS_StdClass)              c;

  api->Apply("%SHeaderInc","CSFDBTypeListBegin");
  api->WriteFile("HTFile","%SHeaderInc");

  for (i = 1; i<= classList->Length(); i++) {
    c = Handle(MS_StdClass)::DownCast(aMeta->GetType(classList->Value(i)));

    if (c->IsPersistent()) {
      api->AddVariable("%Type",classList->Value(i)->ToCString());
      api->Apply("%SHeaderInc","CSFDBTypeListPart");
      api->WriteFile("HTFile","%SHeaderInc");
    }
  }

  api->Apply("%SHeaderInc","CSFDBTypeListEnd");
  api->WriteFile("HTFile","%SHeaderInc");
}

void CSFDBSchema_BuildReadTypeSelection(const Handle(MS_MetaSchema)& aMeta,
					const Handle(EDL_API)& api,
					const Handle(TColStd_HSequenceOfHAsciiString)& classList)
{
  Standard_Integer                 i;
  Handle(MS_StdClass)              c;

  api->Apply("%SHeaderInc","CSFDBREADTSBegin");
  api->WriteFile("HTFile","%SHeaderInc");

  for (i = 1; i<= classList->Length(); i++) {
    c = Handle(MS_StdClass)::DownCast(aMeta->GetType(classList->Value(i)));

    if (c->IsPersistent() && !c->Deferred()) {
      api->AddVariable("%Type",classList->Value(i)->ToCString());
      api->Apply("%SHeaderInc","CSFDBREADTSPart");
      api->WriteFile("HTFile","%SHeaderInc");
    }
  }

  api->Apply("%SHeaderInc","CSFDBREADTSEnd");
  api->WriteFile("HTFile","%SHeaderInc");
}

//Handle(TCollection_HAsciiString) CSFDBSchema_IsVArray(const Handle(MS_MetaSchema)& aMeta,
Handle(TCollection_HAsciiString) CSFDBSchema_IsVArray(const Handle(MS_MetaSchema)& ,
						      const Handle(MS_StdClass)& aClass)
{
  Handle(TCollection_HAsciiString) result;

  if (!aClass->GetMyCreator().IsNull()) {
    Handle(MS_InstClass)             anInst = aClass->GetMyCreator();
    Handle(TCollection_HAsciiString) aGen   = anInst->GenClass();
    
    if (aGen->IsSameString(MS::GetVArrayRootName())) {
      result = anInst->InstTypes()->Value(1);
    }
  }

  return result;
}

//Handle(TCollection_HAsciiString) CSFDBSchema_BuildConvertFunction(const Handle(MS_MetaSchema)& aMeta,
Handle(TCollection_HAsciiString) CSFDBSchema_BuildConvertFunction(const Handle(MS_MetaSchema)& ,
								  const Handle(EDL_API)& api,
								  const Handle(MS_Type)& aType,
								  const Standard_Boolean forDBC)
{
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString;

  if (aType->IsKind(STANDARD_TYPE(MS_Enum))) {
    if (forDBC) api->Apply("%Result","CSFDBDBCRenum");
    else api->Apply("%Result","CSFDBREADenum");
    result = api->GetVariableValue("%Result");
  }
  else if (aType->IsKind(STANDARD_TYPE(MS_PrimType))) {
    Handle(TCollection_HAsciiString) tname = aType->FullName();

    if (strcmp(tname->ToCString(),"Standard_Real") == 0) {
      if (forDBC) api->Apply("%Result","CSFDBDBCRfloat");
      else api->Apply("%Result","CSFDBREADfloat");
      result = api->GetVariableValue("%Result");
    }
    else if (strcmp(tname->ToCString(),"Standard_Character") == 0) {
      if (forDBC) api->Apply("%Result","CSFDBDBCRchar");
      else api->Apply("%Result","CSFDBREADchar");
      result = api->GetVariableValue("%Result");
    }
    else if (strcmp(tname->ToCString(),"Standard_Integer") == 0) {
      if (forDBC) api->Apply("%Result","CSFDBDBCRint");
      else api->Apply("%Result","CSFDBREADint");
      result = api->GetVariableValue("%Result");
    }
    else if (strcmp(tname->ToCString(),"Standard_Byte") == 0) {
      if (forDBC) api->Apply("%Result","CSFDBDBCRchar");
      else api->Apply("%Result","CSFDBREADchar");
      result = api->GetVariableValue("%Result");
    }
    else if (strcmp(tname->ToCString(),"Standard_ShortReal") == 0) {
      if (forDBC) api->Apply("%Result","CSFDBDBCRshortreal");
      else api->Apply("%Result","CSFDBREADshortreal");
      result = api->GetVariableValue("%Result");
    }
    else if (strcmp(tname->ToCString(),"Standard_Boolean") == 0) {
      if (forDBC) api->Apply("%Result","CSFDBDBCRboolean");
      else api->Apply("%Result","CSFDBREADboolean");
      result = api->GetVariableValue("%Result");
    }
    else if (strcmp(tname->ToCString(),"Standard_ExtCharacter") == 0) {
      if (forDBC) api->Apply("%Result","CSFDBDBCRextchar");
      else api->Apply("%Result","CSFDBREADextchar");
      result = api->GetVariableValue("%Result");
    }
    else {
      ErrorMsg() << "CSFDBSchema" << "unknown primitive type :" << tname->ToCString() << endm;
      Standard_NoSuchObject::Raise();
    }
  }

  return result;
}

//Handle(TCollection_HAsciiString) CSFDBSchema_BuildDriverFunction(const Handle(MS_MetaSchema)& aMeta,
Handle(TCollection_HAsciiString) CSFDBSchema_BuildDriverFunction(const Handle(MS_MetaSchema)& ,
//								 const Handle(EDL_API)& api,
								 const Handle(EDL_API)& ,
								 const Handle(MS_Type)& aType,
//								 const Standard_Boolean forDBC)
								 const Standard_Boolean )
{
  Handle(TCollection_HAsciiString) result;

  if (aType->IsKind(STANDARD_TYPE(MS_Enum))) {
    result = new TCollection_HAsciiString("PutInteger");
  }
  else if (aType->IsKind(STANDARD_TYPE(MS_PrimType))) {
    Handle(TCollection_HAsciiString) tname = aType->FullName();

    if (strcmp(tname->ToCString(),"Standard_Real") == 0) {
      result = new TCollection_HAsciiString("PutReal");
    }
    else if (strcmp(tname->ToCString(),"Standard_Character") == 0) {
      result = new TCollection_HAsciiString("PutCharacter");
    }
    else if (strcmp(tname->ToCString(),"Standard_Integer") == 0) {
      result = new TCollection_HAsciiString("PutInteger");
    }
    else if (strcmp(tname->ToCString(),"Standard_Byte") == 0) {
      result = new TCollection_HAsciiString("PutCharacter");
    }
    else if (strcmp(tname->ToCString(),"Standard_ShortReal") == 0) {
      result = new TCollection_HAsciiString("PutShortReal");
    }
    else if (strcmp(tname->ToCString(),"Standard_Boolean") == 0) {
      result = new TCollection_HAsciiString("PutBoolean");
    }
    else if (strcmp(tname->ToCString(),"Standard_ExtCharacter") == 0) {
      result = new TCollection_HAsciiString("PutExtCharacter");
    }
    else {
      ErrorMsg() << "CSFDBSchema" << "unknown primitive type :" << tname->ToCString() << endm;
      Standard_NoSuchObject::Raise();
    }
  }

  return result;
}

void CSFDBSchema_BuildDBCMethod(const Handle(MS_MetaSchema)& aMeta,
				const Handle(EDL_API)& api,
				const Handle(MS_Type)& ftype,
				const Handle(TCollection_HAsciiString)& madd,
				const Handle(TCollection_HAsciiString)& mwrite,
				const Handle(TCollection_HAsciiString)& mread,
				const Handle(TCollection_HAsciiString)& aClass)
{
  Standard_Boolean buildAbbmet = Standard_False;

  if (ftype->IsKind(STANDARD_TYPE(MS_Class))) {
      Handle(MS_StdClass) fclass = *((Handle(MS_StdClass)*)&ftype);
    
    if (fclass->IsPersistent()) {
      buildAbbmet = Standard_True;
      api->Apply("%Result","CSFDBPADDDBC");
      madd->AssignCat(api->GetVariableValue("%Result"));
      api->Apply("%Result","CSFDBDBCWpersistent");
      mwrite->AssignCat(api->GetVariableValue("%Result"));
      api->Apply("%Result","CSFDBDBCPparameter");
      mread->AssignCat(api->GetVariableValue("%Result"));
    }
    else {
      if (AddMap.Contains(ftype->FullName())) {
	api->Apply("%Result","CSFDBSADDDBC");
	madd->AssignCat(api->GetVariableValue("%Result"));
	buildAbbmet = Standard_True;
      }
      api->Apply("%Result","CSFDBDBCWstorable");
      mwrite->AssignCat(api->GetVariableValue("%Result"));
      api->Apply("%Result","CSFDBDBCSparameter");
      mread->AssignCat(api->GetVariableValue("%Result"));
    }
  }
  else {
    api->AddVariable("%DriverFunction",CSFDBSchema_BuildDriverFunction(aMeta,api,ftype,Standard_True)->ToCString());
    api->Apply("%Result","CSFDBDBCWprim");
    mwrite->AssignCat(api->GetVariableValue("%Result"));
    api->AddVariable("%Result",CSFDBSchema_BuildConvertFunction(aMeta,api,ftype,Standard_True)->ToCString());
    mread->AssignCat(api->GetVariableValue("%Result"));
  }

  api->AddVariable("%Type",aClass->ToCString());

  if (buildAbbmet) {
    api->AddVariable("%Body",madd->ToCString());
    api->Apply("%ADDmet","CSFDBSADD");
  }
  else {
    api->AddVariable("%ADDmet","");
  }

  api->AddVariable("%Body",mwrite->ToCString());
  api->Apply("%WRITEmet","CSFDBWRITEDBC");
  
  api->AddVariable("%Body",mread->ToCString());
  api->Apply("%READmet","CSFDBREADDBC");
}

void CSFDBSchema_BuildFieldMethod(const Handle(MS_MetaSchema)& aMeta,
				  const Handle(EDL_API)& api,
				  const Handle(MS_Type)& ftype,
				  const Handle(TColStd_HSequenceOfInteger)& dimension,
				  const Handle(TCollection_HAsciiString)& madd,
				  const Handle(TCollection_HAsciiString)& mwrite,
				  const Handle(TCollection_HAsciiString)& mread)
{
  if (dimension->Length() > 0) {
    Standard_Integer                 i;
    Handle(TCollection_HAsciiString) sdim = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString) stmp = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString) svar = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString) sforr = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString) sforw = new TCollection_HAsciiString;
    char                             num[30],len[30];

    for(i = 1; i <= dimension->Length(); i++) {      
      sprintf(num,"%d",i);
      sprintf(len,"%d",dimension->Value(i));
      api->AddVariable("%Length",len);
      stmp->Clear();
      stmp->AssignCat(api->GetVariableValue("%NameField"));
      stmp->AssignCat(api->GetVariableValue("%Field"));
      stmp->AssignCat(num);

      if (i != 1) {
	svar->AssignCat(",");
	sdim->AssignCat(",");
      }
      svar->AssignCat(stmp);
      svar->AssignCat(" = 0");

      api->AddVariable("%Var",stmp->ToCString());
      api->Apply("%ForResult","CSFDBforWrite");
      sforw->AssignCat(api->GetVariableValue("%ForResult"));
      api->Apply("%ForResult","CSFDBforRead");
      sforr->AssignCat(api->GetVariableValue("%ForResult"));

      sdim->AssignCat(stmp);
    }
    
    api->AddVariable("%VDim",sdim->ToCString());
    api->AddVariable("%ForW",sforw->ToCString());
    api->AddVariable("%ForR",sforr->ToCString());
    api->AddVariable("%LocalVar",svar->ToCString());
  }

  if (ftype->IsKind(STANDARD_TYPE(MS_Class))) {
    Handle(MS_StdClass) fclass = *((Handle(MS_StdClass)*)&ftype);
    
    if (fclass->IsPersistent()) {
      if (dimension->Length() > 0) {
	api->Apply("%Body","CSFDBCallAddTypeSelectionArray");	
	api->Apply("%Result","CSFDBREADARRAYcpp");
	madd->AssignCat(api->GetVariableValue("%Result"));
	api->Apply("%Body","CSFDBCallWritePersistentArray");
	api->Apply("%Result","CSFDBWRITEARRAYcpp");
	mwrite->AssignCat(api->GetVariableValue("%Result"));
	api->Apply("%Body","CSFDBReadPersistentArray");
	api->Apply("%Result","CSFDBREADARRAYcpp");
	mread->AssignCat(api->GetVariableValue("%Result"));
      }
      else {
	api->Apply("%Result","CSFDBCallAddTypeSelection");
	madd->AssignCat(api->GetVariableValue("%Result"));
	api->Apply("%Result","CSFDBCallWritePersistent");
	mwrite->AssignCat(api->GetVariableValue("%Result"));
	api->Apply("%Result","CSFDBPparameter");
	mread->AssignCat(api->GetVariableValue("%Result"));
      }
    }
    else {
      if (dimension->Length() > 0) {
	if (AddMap.Contains(ftype->FullName())) {
	  api->Apply("%Body","CSFDBCallAddArray");	
	  api->Apply("%Result","CSFDBREADARRAYcpp");
	  madd->AssignCat(api->GetVariableValue("%Result"));
	}

	api->Apply("%Body","CSFDBCallWriteStorableArray");
	api->Apply("%Result","CSFDBWRITEARRAYcpp");
	mwrite->AssignCat(api->GetVariableValue("%Result"));
	api->Apply("%Body","CSFDBReadStorableArray");
	api->Apply("%Result","CSFDBREADARRAYcpp");
	mread->AssignCat(api->GetVariableValue("%Result"));
      }
      else {
	if (AddMap.Contains(ftype->FullName())) {
	  api->Apply("%Result","CSFDBCallAdd");
	  madd->AssignCat(api->GetVariableValue("%Result"));
	}
	api->Apply("%Result","CSFDBCallWRITE");
	mwrite->AssignCat(api->GetVariableValue("%Result"));
	api->Apply("%Result","CSFDBSparameter");
	mread->AssignCat(api->GetVariableValue("%Result"));
      }
    }
  }
  else {
    if (dimension->Length() > 0) {
      api->AddVariable("%DriverFunction",CSFDBSchema_BuildDriverFunction(aMeta,api,ftype,Standard_False)->ToCString());
      api->Apply("%Body","CSFDBCallWritePrimitiveArray");
      api->Apply("%Result","CSFDBWRITEARRAYcpp");
      mwrite->AssignCat(api->GetVariableValue("%Result"));
      api->AddVariable("%Convert",CSFDBSchema_BuildConvertFunction(aMeta,api,ftype,Standard_False)->ToCString());

      // warning for enums: we can do f << enum  but not f >> enum
      //                    so here is the trick.
      if (ftype->IsKind(STANDARD_TYPE(MS_Enum))) {
	api->Apply("%Body","CSFDBReadEnumArray");
      }
      else {
	api->Apply("%Body","CSFDBReadPrimitiveArray");
      }
      api->Apply("%Result","CSFDBREADARRAYcpp");
      mread->AssignCat(api->GetVariableValue("%Result"));
    }
    else {
      api->AddVariable("%DriverFunction",CSFDBSchema_BuildDriverFunction(aMeta,api,ftype,Standard_False)->ToCString());
      api->Apply("%Result","CSFDBCallWritePrimitive");
      mwrite->AssignCat(api->GetVariableValue("%Result"));
      api->AddVariable("%Convert",CSFDBSchema_BuildConvertFunction(aMeta,api,ftype,Standard_False)->ToCString());
      // CLE
      if (strcmp(ftype->FullName()->ToCString(),"Standard_Byte") == 0) {
	api->AddVariable("%FType","Standard_Character");
      }
      else {
	api->AddVariable("%FType",ftype->FullName()->ToCString());
      }
      // ENDCLE
      // warning for enums: we can do f << enum  but not f >> enum
      //                    so here is the trick.
      if (ftype->IsKind(STANDARD_TYPE(MS_Enum))) {
	api->Apply("%Result","CSFDBenumeration");
      }
      else {
	api->Apply("%Result","CSFDBprimitive");
      }
      mread->AssignCat(api->GetVariableValue("%Result"));
    }
  }
}

// construction du cxx des classes CallBack
//
void CSFDBSchema_BuildMethodsBody(const Handle(MS_MetaSchema)& aMeta,
				  const Handle(EDL_API)& api,
				  const Handle(TCollection_HAsciiString)& aClass)
{
  Handle(MS_StdClass)                     c,cinh,fclass;
  Handle(TCollection_HAsciiString)        madd = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString)        mwrite = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString)        mread = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString)        dbcitem;
  Handle(TCollection_HAsciiString)        casting = new TCollection_HAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) inh;
  Handle(MS_HSequenceOfField)             fields;
  Standard_Integer                        i,j;
  Standard_Boolean                        isPersistent,needComma = Standard_False;
  Handle(MS_Type)                         ftype;

  ftype        = aMeta->GetType(aClass);
  c            = *((Handle(MS_StdClass)*)&ftype);
  inh          = c->GetFullInheritsNames();
  isPersistent = c->IsPersistent();

  if (isPersistent) {
    api->AddVariable("%TCall","->");
    api->AddVariable("%Cast","");
  }
  else {
    casting->AssignCat("(");
    casting->AssignCat(aClass);
    casting->AssignCat("&)");
    api->AddVariable("%Cast",casting->ToCString());
    api->AddVariable("%TCall",".");
  }

  dbcitem = CSFDBSchema_IsVArray(aMeta,c);
  api->AddVariable("%NameField",c->FullName()->ToCString());

  // DBC VARRAY FIELD
  //
  if (!dbcitem.IsNull()) {
    ftype     = aMeta->GetType(dbcitem);

    if (ftype->IsKind(STANDARD_TYPE(MS_Alias))) {
      Handle(MS_Alias) alias = *((Handle(MS_Alias)*)&ftype);

      ftype = aMeta->GetType(alias->DeepType());
    }
    
    api->AddVariable("%Type",ftype->FullName()->ToCString());	

    CSFDBSchema_BuildDBCMethod(aMeta,api,ftype,madd,mwrite,mread,aClass);
  }
  // NORMAL FIELD
  //
  else {
    Handle(MS_Type) tmpType;

    for (i = inh->Length(); i >= 1; i--) {
      tmpType = aMeta->GetType(inh->Value(i));
      cinh   = *((Handle(MS_StdClass)*)&tmpType);
      fields = cinh->GetFields();
      api->AddVariable("%NameField",inh->Value(i)->ToCString());

      for (j = 1; j <= fields->Length(); j++) {
	if (needComma) {
	  api->Apply("%Result","CSFDBComma");
	  mwrite->AssignCat(api->GetVariableValue("%Result"));
	}      
	
	needComma = Standard_True;
	ftype     = aMeta->GetType(fields->Value(j)->TYpe());

	if (ftype->IsKind(STANDARD_TYPE(MS_Alias))) {
	  Handle(MS_Alias) alias = *((Handle(MS_Alias)*)&ftype);
	  
	  ftype = aMeta->GetType(alias->DeepType());
	}

	api->AddVariable("%Type",fields->Value(j)->TYpe()->ToCString());	
	api->AddVariable("%Field",fields->Value(j)->Name()->ToCString());

	CSFDBSchema_BuildFieldMethod(aMeta,api,ftype,fields->Value(j)->Dimensions(),madd,mwrite,mread);	
      }
    }
        
    fields = c->GetFields();

    api->AddVariable("%NameField",c->FullName()->ToCString());
    
    for (j = 1; j <= fields->Length(); j++) {
      if (needComma) {
	api->Apply("%Result","CSFDBComma");
	mwrite->AssignCat(api->GetVariableValue("%Result"));
      }
      
      needComma = Standard_True;
      ftype     = aMeta->GetType(fields->Value(j)->TYpe());

      if (ftype->IsKind(STANDARD_TYPE(MS_Alias))) {
	Handle(MS_Alias) alias = *((Handle(MS_Alias)*)&ftype);
	
	ftype = aMeta->GetType(alias->DeepType());
      }

      api->AddVariable("%Field",fields->Value(j)->Name()->ToCString());
      api->AddVariable("%Type",fields->Value(j)->TYpe()->ToCString());	

      CSFDBSchema_BuildFieldMethod(aMeta,api,ftype,fields->Value(j)->Dimensions(),madd,mwrite,mread);
    }
 
    api->AddVariable("%Type",aClass->ToCString());

    if (isPersistent) {
      // Methode Add
      //
      api->AddVariable("%Body",madd->ToCString());
      api->Apply("%ADDmet","CSFDBPADD");

      // Methode Write
      //
      api->AddVariable("%Body",mwrite->ToCString());
      api->Apply("%WRITEmet","CSFDBPWRITE");

      // Methode Read
      //
      api->AddVariable("%Body",mread->ToCString());
      api->Apply("%READmet","CSFDBPREAD");
    }
    else {
      if (AddMap.Contains(aClass)) {
	api->AddVariable("%Body",madd->ToCString());
	api->Apply("%ADDmet","CSFDBSADD");
      }
      else {
	api->AddVariable("%ADDmet","");
      }

      api->AddVariable("%Body",mwrite->ToCString());
      api->Apply("%WRITEmet","CSFDBSWRITE");
      api->AddVariable("%Body",mread->ToCString());
      api->Apply("%READmet","CSFDBSREAD");
    }   
  }

  if (isPersistent) {
    if (!c->Deferred()) {
      api->Apply("%SHeaderInc","CSFDBNEWOBJECT");
      api->WriteFile("HTFile","%SHeaderInc");
    }
    else {
      api->Apply("%SHeaderInc","CSFDBNEWOBJECTDeferred");
      api->WriteFile("HTFile","%SHeaderInc");
    }
  }

  api->WriteFile("HTFile","%ADDmet");
  api->WriteFile("HTFile","%WRITEmet");
  api->WriteFile("HTFile","%READmet");
}

void CSFDBSchema_BuildCodeBody(const Handle(MS_MetaSchema)& aMeta,
			       const Handle(EDL_API)& api,
			       const Handle(TColStd_HSequenceOfHAsciiString)& classList,
			       const Handle(TCollection_HAsciiString)& schName,
			       const Handle(TColStd_HSequenceOfHAsciiString)& outfile)
{
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString) aFileName = new TCollection_HAsciiString(api->GetVariableValue("%FullPath"));
  Handle(TCollection_HAsciiString) proto  = new TCollection_HAsciiString;
  Standard_Integer                 i,j;
  Handle(MS_StdClass)              c;
  Handle(MS_Type)                  tmpType;
  
  
  aFileName->AssignCat(schName);
  aFileName->AssignCat(".cxx");
  
  api->OpenFile("HTFile",aFileName->ToCString());

  api->AddVariable("%Type",schName->ToCString());
  api->Apply("%SHeaderInc","CSFDBInclude");
  api->WriteFile("HTFile","%SHeaderInc");
  api->AddVariable("%Type","Storage_StreamUnknownTypeError");
  api->Apply("%SHeaderInc","CSFDBInclude");
  api->WriteFile("HTFile","%SHeaderInc");
  api->AddVariable("%Type","TColStd_SequenceOfAsciiString");
  api->Apply("%SHeaderInc","CSFDBInclude");
  api->WriteFile("HTFile","%SHeaderInc");

  for (i = 1; i <= classList->Length(); i++) {
    tmpType = aMeta->GetType(classList->Value(i));
    c = *((Handle(MS_StdClass)*)&tmpType);
    
    api->AddVariable("%Type",classList->Value(i)->ToCString());

    if (c->IsPersistent()) {
      api->Apply("%SHeaderInc","CSFDBDerivatedInclude");
      api->WriteFile("HTFile","%SHeaderInc");
    }
  }
  
  api->AddVariable("%Schema",schName->ToCString());
  api->Apply("%SHeaderInc","CSFDBTypeMgt");
  api->WriteFile("HTFile","%SHeaderInc");

  CSFDBSchema_BuildAddTypeSelection(aMeta,api,classList);
  CSFDBSchema_BuildReadTypeSelection(aMeta,api,classList);
  CSFDBSchema_BuildSchemaTypeList(aMeta,api,classList);

  api->CloseFile("HTFile");
  outfile->Append(aFileName);
  Handle(MS_HSequenceOfField) theFields;

  for (i = 1; i <= classList->Length(); i++) {
    InfoMsg() << "CSFDBSchema" << "Processing : "<< classList->Value(i)->ToCString() << endm;
    // HXX
    //
    tmpType = aMeta->GetType(classList->Value(i));
    c = *((Handle(MS_StdClass)*)&tmpType);
    theFields = c->GetFields();

    Handle(TCollection_HAsciiString) includeFile = new TCollection_HAsciiString;

    // DBC_VArray  
    //
    if (!c->GetMyCreator().IsNull()) {
      Handle(MS_InstClass) inst    = c->GetMyCreator();

      if (strcmp(inst->GenClass()->ToCString(),"DBC_VArray") == 0) {
	Handle(TCollection_HAsciiString) typeField = inst->InstTypes()->Value(1);

	tmpType = aMeta->GetType(inst->InstTypes()->Value(1));

	if (tmpType->IsKind(STANDARD_TYPE(MS_StdClass)) && !RemoveMap.Contains(typeField)) { 
	  api->AddVariable("%Type",typeField->ToCString());
	  api->Apply("%SHeaderInc","CSFDBDerivatedInclude");
	  includeFile->AssignCat(api->GetVariableValue("%SHeaderInc"));
	}
      }
    }

    
    
    // inheritance
    //
    if (c->GetInheritsNames()->Length() > 0) {
      if (!RemoveMap.Contains(c->GetInheritsNames()->Value(1))) {
	api->AddVariable("%Type",c->GetInheritsNames()->Value(1)->ToCString());
	api->Apply("%SHeaderInc","CSFDBDerivatedInclude");
	includeFile->AssignCat(api->GetVariableValue("%SHeaderInc"));
      }
    }
  
    api->AddVariable("%Include",includeFile->ToCString());
    api->AddVariable("%Type",classList->Value(i)->ToCString());
    aFileName = new TCollection_HAsciiString(api->GetVariableValue("%FullPath"));
    aFileName->AssignCat(schName);
    aFileName->AssignCat("_");
    aFileName->AssignCat(classList->Value(i));
    aFileName->AssignCat(".hxx");
    api->OpenFile("HTFile",aFileName->ToCString());
    api->AddVariable("%Type",classList->Value(i)->ToCString());
    if (c->IsPersistent()) {
      api->Apply("%SHeaderInc","CSFDBPCallBackClass");
    }
    else {
      api->Apply("%SHeaderInc","CSFDBSCallBackClass");
    }
    api->WriteFile("HTFile","%SHeaderInc");
    api->CloseFile("HTFile");
    outfile->Append(aFileName);

    // IXX
    //
    // Includes des champs
    //
    includeFile = new TCollection_HAsciiString;

    // inheritance
    //
    if (c->GetInheritsNames()->Length() > 0) {
      if (!RemoveMap.Contains(c->GetInheritsNames()->Value(1))) {
	api->AddVariable("%Type",c->GetInheritsNames()->Value(1)->ToCString());
	api->Apply("%SHeaderInc","CSFDBIxxInclude");
	includeFile->AssignCat(api->GetVariableValue("%SHeaderInc"));
      }
    }

    for (j = 1; j <= theFields->Length(); j++) {
      Handle(TCollection_HAsciiString) typeField = theFields->Value(j)->TYpe();
      Handle(MS_Type)                  rtypeField = aMeta->GetType(typeField);
      
      if (rtypeField->IsKind(STANDARD_TYPE(MS_StdClass)) && !RemoveMap.Contains(typeField)) {      
	api->AddVariable("%Type",typeField->ToCString());
	api->Apply("%SHeaderInc","CSFDBDerivatedInclude");
	includeFile->AssignCat(api->GetVariableValue("%SHeaderInc"));
      }
    }
    includeFile->AssignCat("\n");

    aFileName = new TCollection_HAsciiString(api->GetVariableValue("%FullPath"));
    aFileName->AssignCat(schName);
    aFileName->AssignCat("_");
    aFileName->AssignCat(classList->Value(i));
    aFileName->AssignCat(".ixx");
    api->OpenFile("HTFile",aFileName->ToCString());
    api->AddVariable("%Type",classList->Value(i)->ToCString());
    api->AddVariable("%SHeaderInc",includeFile->ToCString());
    api->WriteFile("HTFile","%SHeaderInc");
    api->CloseFile("HTFile");
    outfile->Append(aFileName);

    // CXX
    //
    aFileName = new TCollection_HAsciiString(api->GetVariableValue("%FullPath"));
    aFileName->AssignCat(schName);
    aFileName->AssignCat("_");
    aFileName->AssignCat(classList->Value(i));
    aFileName->AssignCat(".cxx");
    api->OpenFile("HTFile",aFileName->ToCString());
    api->Apply("%SHeaderInc","CSFDBDerivatedInclude");
    api->WriteFile("HTFile","%SHeaderInc");

    // here we include Class.hxx
    //
    api->Apply("%SHeaderInc","CSFDBInclude");
    api->WriteFile("HTFile","%SHeaderInc");
    // here we include Class.ixx
    //
    api->Apply("%SHeaderInc","CSFDBIxxInclude");
    api->WriteFile("HTFile","%SHeaderInc");
    api->AddVariable("%Type","Storage_Schema");
    api->Apply("%SHeaderInc","CSFDBInclude");
    api->WriteFile("HTFile","%SHeaderInc");
    api->AddVariable("%Type","Storage_stCONSTclCOM");
    api->Apply("%SHeaderInc","CSFDBInclude");
    api->WriteFile("HTFile","%SHeaderInc");
    api->AddVariable("%Type",classList->Value(i)->ToCString());
    if (c->IsPersistent()) {
      api->Apply("%SHeaderInc","CSFDBCallBackTypeMgt");
      api->WriteFile("HTFile","%SHeaderInc");
    }
    api->AddVariable("%Type",schName->ToCString());
    CSFDBSchema_BuildMethodsBody(aMeta,api,classList->Value(i));
    api->CloseFile("HTFile");
    outfile->Append(aFileName);
  }
}

Standard_Boolean CSFDBSchema_ClassMustHaveAddFunction(const Handle(MS_MetaSchema)& aMeta,
						      const Handle(TCollection_HAsciiString)& aClassName,
						      WOKTools_MapOfHAsciiString& aMap)
{
  Handle(MS_StdClass) stdClass;
  Handle(MS_Type)     aType;
  Standard_Boolean    result = Standard_False;

  if (aMeta->IsDefined(aClassName)) {
    aType    = aMeta->GetType(aClassName);
    stdClass = Handle(MS_StdClass)::DownCast(aType);
  }
  else {
    ErrorMsg() << "CSFDBSchema" << "type " << aClassName->ToCString() << " not defined." << endm;
    return Standard_False;
  }

  if (!stdClass.IsNull()) {
    Handle(MS_HSequenceOfField)      fields = stdClass->GetFields();
    Handle(TCollection_HAsciiString) dbcitem;
    Standard_Integer                 i;
    Standard_Boolean                 locRes;

    
    if (stdClass->IsPersistent()) {
      result = Standard_True;
    }

    if (aMap.Contains(aClassName)) return Standard_True;
    else if (result) aMap.Add(aClassName);

    dbcitem = CSFDBSchema_IsVArray(aMeta,stdClass);

    if (!dbcitem.IsNull()) {
      aType = aMeta->GetType(dbcitem);
      
      if (aType->IsKind(STANDARD_TYPE(MS_StdClass))) {
	stdClass = *((Handle(MS_StdClass)*)&aType);
	
	if (stdClass->IsPersistent()) {
	  result = Standard_True;
	  if (!aMap.Contains(dbcitem)) aMap.Add(dbcitem);
	}
	
	locRes = CSFDBSchema_ClassMustHaveAddFunction(aMeta,dbcitem,aMap);
	
	result = (locRes || result);
      }
    }
    else {
      for (i = 1; i <= fields->Length(); i++) {
	aType = aMeta->GetType(fields->Value(i)->TYpe());
	
	if (aType->IsKind(STANDARD_TYPE(MS_StdClass))) {
	  stdClass = *((Handle(MS_StdClass)*)&aType);
	  
	  if (stdClass->IsPersistent()) {
	    result = Standard_True;
	    if (!aMap.Contains(fields->Value(i)->TYpe())) aMap.Add(fields->Value(i)->TYpe());
	  }
	  
	  locRes = CSFDBSchema_ClassMustHaveAddFunction(aMeta,fields->Value(i)->TYpe(),aMap);
	  
	  result = (locRes || result);
	}
      }
    }
  }

  if (result && !aMap.Contains(aClassName)) aMap.Add(aClassName);

  return result;
}
  
// Standard extractor API : launch the extraction of C++ schema file
//                          from the type <aName>
// 
void CSFDBSchema_Extract(const Handle(MS_MetaSchema)& aMeta,
			 const Handle(TCollection_HAsciiString)& aName,
			 const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
			 const Handle(TCollection_HAsciiString)& outdir,
			 const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
			 const Standard_CString DBMS)
{
  Handle(MS_Schema)                       aSchema;
  Handle(TColStd_HSequenceOfHAsciiString) classList,
                                          classListImp,
                                          classToRemove;
  Standard_Integer                        i,j;
  
  // before begining, we look if the entity has something to extract...
  //
  if (aMeta->IsSchema(aName)) {
    aSchema   = aMeta->GetSchema(aName); 
  }
  else {
    ErrorMsg() << "CSFDBSchema" << aName->ToCString() << " is not a schema..." << endm;
    Standard_NoSuchObject::Raise();
  }
  
  // ... and we load the templates
  //
  Handle(EDL_API)     api;
  
  api = CSFDBSchema_LoadTemplate(edlsfullpath,outdir,DBMS);
  
  api->AddVariable("%Schema",aName->ToCString());
  api->AddVariable("%SchemaComment",aSchema->Comment()->ToCString());
  classList    = aMeta->GetPersistentClassesFromSchema(aName,Standard_True);
  classListImp = aMeta->GetPersistentClassesFromClasses(classList,Standard_True);

  classList->Append(classListImp);

  classToRemove = new TColStd_HSequenceOfHAsciiString;

  for (i = 1; i <= classList->Length(); i++) {
    if (strcmp(classList->Value(i)->ToCString(),"PStandard_ArrayNode") == 0) {
      classToRemove->Append(classList->Value(i));
      if (!RemoveMap.Contains(classList->Value(i))) {
	RemoveMap.Add(classList->Value(i));
      }
    }
    else if (strcmp(classList->Value(i)->ToCString(),"DBC_BaseArray") == 0) {
      classToRemove->Append(classList->Value(i));
      if (!RemoveMap.Contains(classList->Value(i))) {
	RemoveMap.Add(classList->Value(i));
      }
    }
    else {
      if (aMeta->IsDefined(classList->Value(i))) {
	Handle(MS_Type) t = aMeta->GetType(classList->Value(i));

	if (t->IsKind(STANDARD_TYPE(MS_StdClass))) {
	  Handle(MS_StdClass) c = *((Handle(MS_StdClass)*)&t);

	  if (!c->GetMyCreator().IsNull() && c->IsNested()) {
	    Handle(MS_InstClass) inst = c->GetMyCreator(),
	                         instn;
	    Handle(MS_Type) tmpType = aMeta->GetType(c->GetNestingClass());
	    Handle(MS_StdClass) cn = *((Handle(MS_StdClass)*)&tmpType);

	    if (strcmp(inst->GenClass()->ToCString(),"DBC_VArray") == 0) {
	      if (strcmp(cn->GetMyCreator()->GenClass()->ToCString(),inst->GenClass()->ToCString()) == 0) {
		classToRemove->Append(classList->Value(i));
		if (!RemoveMap.Contains(classList->Value(i))) {
		  RemoveMap.Add(classList->Value(i));
		}
	      }
	    }
	  }
	}
      }
    }

    CSFDBSchema_ClassMustHaveAddFunction(aMeta,classList->Value(i),AddMap);
  }

  Standard_Integer LenList = classList->Length();

  for (j = 1; j <= classToRemove->Length(); j++) {
    for (i = 1; i <= LenList; i++) {
      if (strcmp(classList->Value(i)->ToCString(),classToRemove->Value(j)->ToCString()) == 0) {
	classList->Remove(i);
	if (AddMap.Contains(classToRemove->Value(j))) AddMap.Remove(classToRemove->Value(j));
	i = LenList + 1;
      }
    }
    LenList = classList->Length();
  }

  CSFDBSchema_BuildHeader(aMeta,api,classList,aName,outfile);
  CSFDBSchema_BuildCodeBody(aMeta,api,classList,aName,outfile);
  AddMap.Clear();
  RemoveMap.Clear();
}





