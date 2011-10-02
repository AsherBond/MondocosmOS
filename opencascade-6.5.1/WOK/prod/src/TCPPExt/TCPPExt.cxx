// CLE : this extractor is a test for the general extraction architecture
//       for the new DTV tools 
// SYC : 08/1996
//

#include <TCPPExt.hxx>


#include <MS_HArray1OfParam.hxx>

#include <WOKTools_Messages.hxx>

// Standard Extractor API : list the EDL files used by this program
//
Handle(TColStd_HSequenceOfHAsciiString) TCPP_TemplatesUsed()
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;

  result->Append(new TCollection_HAsciiString("TCPPExt_MethodTemplate.edl"));

  return result;

}

Handle(EDL_API)&  TCPP_LoadTemplate(const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
//				    const Handle(TCollection_HAsciiString)& outdir)
				    const Handle(TCollection_HAsciiString)& )
{
  static Handle(EDL_API)  api = new EDL_API;
  static Standard_Boolean alreadyLoaded = Standard_False;

  api->ClearVariables();

  if (!alreadyLoaded) {
    alreadyLoaded = Standard_True;

    for(Standard_Integer i = 1; i <= edlsfullpath->Length(); i++) {
      api->AddIncludeDirectory(edlsfullpath->Value(i)->ToCString());
    }

    if (api->Execute("TCPPExt_MethodTemplate.edl") != EDL_NORMAL) {
      ErrorMsg() << "TCPPExt" << "unable to load : TCPPExt_MethodTemplate.edl" << endm;
      Standard_NoSuchObject::Raise();
    }
  }

  // templates for methods extraction
  //
  api->AddVariable(VMethodHeader,"MethodTemplate");
  api->AddVariable(VConstructorHeader,"ConstructorTemplate");

  return api;
}

//  build a return, parameter or field type in c++
//  return a <type name> or a Handle_<type name>
//
Handle(TCollection_HAsciiString) TCPP_BuildType(const Handle(MS_MetaSchema)& aMeta,
						const Handle(TCollection_HAsciiString)& aTypeName,
						const Handle(MS_Class)& aClass)
{
  Handle(TCollection_HAsciiString)   result = new TCollection_HAsciiString;
  Handle(MS_Type)                    aType;

  if (aMeta->IsDefined(aTypeName)) {
    aType = aMeta->GetType(aTypeName);

    if (aType->IsKind(STANDARD_TYPE(MS_Alias))) {
      Handle(MS_Alias) analias = Handle(MS_Alias)::DownCast(aType);

      aType = aMeta->GetType(analias->DeepType());
    }
    
    if (aType->IsKind(STANDARD_TYPE(MS_Class))) {
      Handle(MS_Class) aClass;
      
      aClass = Handle(MS_Class)::DownCast(aType);
      
      if (aClass->IsPersistent() || aClass->IsTransient()) {
	result->AssignCat("Handle(");
	result->AssignCat(aTypeName);
	result->AssignCat(")");
      }
      else {
	result->AssignCat(aTypeName);
      } 
    }
    else {
      result->AssignCat(aTypeName);
    }
  }
  else if (( aClass->IsKind(STANDARD_TYPE(MS_GenClass)) ) ||                           // if it's a generic class 
	   ( (aClass->IsKind(STANDARD_TYPE(MS_StdClass))) && (aClass->IsNested()) )) { // or a class nested in a generic class
    Handle(MS_GenClass) myGenClass;

    // we get the MS_GenClass associated to the class
    if ( (aClass->IsKind(STANDARD_TYPE(MS_StdClass))) && (aClass->IsNested()) ) {
      myGenClass = Handle(MS_GenClass)::DownCast(aMeta->GetType(aClass->GetNestingClass()));
    }
    if ( aClass->IsKind(STANDARD_TYPE(MS_GenClass)) ) {
      myGenClass = Handle(MS_GenClass)::DownCast(aClass);
    }

    // we find the type in the generic types defined in the MS_GenClass
    Handle(MS_HSequenceOfGenType) myGenTypes = myGenClass->GenTypes();
    Handle(MS_GenType) myGenType ;
      
    for (int i = 1; i <= myGenTypes->Length(); i++) 
      if ( (strcmp(myGenTypes->Value(i)->Name()->ToCString(),aTypeName->ToCString()) == 0) )
	myGenType = myGenTypes->Value(i);

    // we process the type
    if (!myGenType->Any()) {
      if ( (strcmp(myGenType->TYpeName()->ToCString(),"Transient") == 0)
	  || (strcmp(myGenType->TYpeName()->ToCString(),"Persistent") == 0) ) {
	result->AssignCat("Handle(");
	result->AssignCat(aTypeName);
	result->AssignCat(")");
      }
      else {
	result->AssignCat(aTypeName);
      } 
    }
    else {
      result->AssignCat(aTypeName);
    }
  }
  else {
    ErrorMsg() << "TCPPExt" << "type " << aType->FullName()->ToCString() << " not defined..." << endm;
    Standard_NoSuchObject::Raise();
  }
  return result;
}

// Build a parameter list for methods
//    the output is in C++
//
Handle(TCollection_HAsciiString) TCPP_BuildParameterList(const Handle(MS_MetaSchema)& aMeta, 
							 const Handle(MS_HArray1OfParam)& aSeq)
{
  Standard_Integer                 i;
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString;
  Handle(MS_Type)                  aType;
  Handle(MS_Class)                 aClass;

  if(!aSeq.IsNull()) {
    for (i = 1; i <= aSeq->Length(); i++) {
      if (i > 1) {
	result->AssignCat(",");
      }

      if (!aSeq->Value(i)->IsOut()) {
	result->AssignCat("const ");
      }
    
      if (aMeta->IsDefined(aSeq->Value(i)->TypeName())) {
	aType = aMeta->GetType(aSeq->Value(i)->TypeName());
      
	if (aType->IsKind(STANDARD_TYPE(MS_Class))) {
	  aClass = Handle(MS_Class)::DownCast(aType);
//---> EUG BUC60592
     if (   aClass -> IsKind (  STANDARD_TYPE( MS_InstClass )  ) &&
           !aClass -> IsKind (  STANDARD_TYPE( MS_StdClass  )  )
     ) {

      Handle( MS_InstClass ) :: DownCast ( aClass ) -> InstToStd ();

      aType = aMeta -> GetType (  aSeq -> Value ( i ) -> TypeName ()  );

	  aClass = Handle( MS_Class ) :: DownCast ( aType );

     }  // end if
//<--- EUG BUC60592
	  if (aClass->IsPersistent() || aClass->IsTransient()) {
	    result->AssignCat("Handle(");
	    result->AssignCat(aSeq->Value(i)->TypeName());
	    result->AssignCat(")& ");
	    result->AssignCat(aSeq->Value(i)->Name());
	  }
	  else {
	    result->AssignCat(aSeq->Value(i)->TypeName());
	    result->AssignCat("& ");
	    result->AssignCat(aSeq->Value(i)->Name());
	  }
	} 
	else if (aType->IsKind(STANDARD_TYPE(MS_Imported)) || aType->IsKind(STANDARD_TYPE(MS_Pointer)) || aSeq->Value(i)->IsItem() || aSeq->Value(i)->IsOut()) {
	  result->AssignCat(aSeq->Value(i)->TypeName());
	  result->AssignCat("& ");
	  result->AssignCat(aSeq->Value(i)->Name());
	}
	// WARNING : ALIASES
	//
	else if (aType->IsKind(STANDARD_TYPE(MS_Alias))) {
	  Handle(MS_Alias)                 analias  = Handle(MS_Alias)::DownCast(aType);
	  Handle(TCollection_HAsciiString) deeptype = analias->DeepType();

	  if (aMeta->IsDefined(deeptype)) {
	    Handle(MS_Type) dt = aMeta->GetType(deeptype);

	    if (dt->IsKind(STANDARD_TYPE(MS_Imported)) || dt->IsKind(STANDARD_TYPE(MS_Pointer)) || aSeq->Value(i)->IsItem() || aSeq->Value(i)->IsOut()) {
	      result->AssignCat(aSeq->Value(i)->TypeName());
	      result->AssignCat("& ");
	      result->AssignCat(aSeq->Value(i)->Name());
	    } 
	    else if (dt->IsKind(STANDARD_TYPE(MS_Class))) {
	      aClass = Handle(MS_Class)::DownCast(dt);
	    
	      if (aClass->IsPersistent() || aClass->IsTransient()) {
		result->AssignCat("Handle(");
		result->AssignCat(aSeq->Value(i)->TypeName());
		result->AssignCat(")& ");
		result->AssignCat(aSeq->Value(i)->Name());
	      }
	      else {
		result->AssignCat(aSeq->Value(i)->TypeName());
		result->AssignCat("& ");
		result->AssignCat(aSeq->Value(i)->Name());
	      }
	    } 
	    else {
	      result->AssignCat(aSeq->Value(i)->TypeName());
	      result->AssignCat(" ");
	      result->AssignCat(aSeq->Value(i)->Name());
	    }
	  }
	  else {
	    ErrorMsg() << "TCPPExt" << "incomplete alias deep type in method's parameter..." << endm;
	    Standard_NoSuchObject::Raise();
	  }
	}
	else {
	  result->AssignCat(aSeq->Value(i)->TypeName());
	  result->AssignCat(" ");
	  result->AssignCat(aSeq->Value(i)->Name());
	}
      }
      else {
	result->AssignCat(aSeq->Value(i)->TypeName());
	result->AssignCat("& ");
	result->AssignCat(aSeq->Value(i)->Name());
      }
    }
  }
  return result;
}

// write the content of a variable into a file
//
void TCPP_WriteFile(const Handle(EDL_API)& api,
		    const Handle(TCollection_HAsciiString)& aFileName,
		    const Standard_CString var)
{
  // ...now we write the result
  //
  api->OpenFile("HTFile",aFileName->ToCString());
  api->WriteFile("HTFile",var);
  api->CloseFile("HTFile");
}

// Main phase :
//    extract template for .gxx, .lxx and .cxx for classes
//    and packages
//
		 
void TCPP_Extract(const Handle(MS_MetaSchema)& aMeta, 
		  const Handle(TCollection_HAsciiString)& aName,
		  const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
		  const Handle(TCollection_HAsciiString)& outdir,
		  const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
		  const Standard_CString)
{
  Handle(MS_Type)     srcType;
  Handle(MS_Class)    srcClass;
  Handle(MS_Package)  srcPackage;


  // before begining, we look if the entity has something to extract...
  //
  if (aMeta->IsDefined(aName)) {
    srcType    = aMeta->GetType(aName); 
    srcClass   = Handle(MS_Class)::DownCast(srcType); 
    
    if (( srcType->IsKind(STANDARD_TYPE(MS_InstClass)) ) || 
	( (!srcType->IsKind(STANDARD_TYPE(MS_StdClass))) && (!srcType->IsKind(STANDARD_TYPE(MS_GenClass))) )) {
      // InfoMsg() << "TCPPExt" << " c'est une InstClass ou une Enum " << endm;
      return;
    }

    Handle(MS_StdClass) stdClass = Handle(MS_StdClass)::DownCast(srcType);
    if (!stdClass.IsNull()) {
      if (!stdClass->GetMyCreator().IsNull()) {
	// InfoMsg() << "TCPPExt" << " c'est une classe instantiee par InstToStd " << endm;
	return;
      }
    }
  }
  else if (aMeta->IsPackage(aName)) {
    srcPackage = aMeta->GetPackage(aName);
  }
  else {
    ErrorMsg() << "TCPPExt" << "class " << aName->ToCString() << " not defined..." << endm;
    Standard_NoSuchObject::Raise();
  }
  
  Handle(MS_HSequenceOfMemberMet) aSeqMemMet;
  Handle(MS_HSequenceOfExternMet) aSeqExtMet;
  Standard_Integer                i,
  seqLen = 0;
  
  if (!srcClass.IsNull()) {
    aSeqMemMet = srcClass->GetMethods();
    seqLen     = aSeqMemMet->Length();
  }
  else if (!srcPackage.IsNull()) {
    aSeqExtMet = srcPackage->Methods();
    seqLen     = aSeqExtMet->Length();
  }
  
  if (seqLen == 0) {
    return;
  }
  
  // ... and we load the templates
  //
  Handle(EDL_API)     api = TCPP_LoadTemplate(edlsfullpath,outdir);
  Handle(MS_Method)   m;
  Handle(MS_InstMet)  im;
  Handle(MS_ClassMet) cm;
  Handle(MS_Construc) ct;
  Handle(MS_Param)    retType;

  Standard_Boolean HasInlineMethod = Standard_False,
                   InlineMethod,
                   noInclude = Standard_False;
  
  Handle(TCollection_HAsciiString) CxxFileName = new TCollection_HAsciiString(outdir);
  Handle(TCollection_HAsciiString) LxxFileName = new TCollection_HAsciiString(outdir);
  Handle(TCollection_HAsciiString) IncludeName = new TCollection_HAsciiString("#include <");
  
  CxxFileName->AssignCat(aName);

  if (!srcClass.IsNull()) {
    if (( srcClass->IsKind(STANDARD_TYPE(MS_GenClass)) )  ||
	( (srcClass->IsKind(STANDARD_TYPE(MS_StdClass))) && (srcClass->IsNested()) )) {
      CxxFileName->AssignCat(".gxx.template");
      noInclude = Standard_True;
    }
    else {
      CxxFileName->AssignCat(".cxx.template");
    }
  }
  else {
    CxxFileName->AssignCat(".cxx.template");
  }

  LxxFileName->AssignCat(aName);
  LxxFileName->AssignCat(".lxx.template");
  
  IncludeName->AssignCat(aName);

  if (!srcPackage.IsNull()) {
    IncludeName->AssignCat(".hxx>\n\n");
  }
  else {
    IncludeName->AssignCat(".ixx>\n\n");
  }

  api->AddVariable(VClass,aName->ToCString());

  api->OpenFile(VCxxFile,CxxFileName->ToCString());
  outfile->Append(CxxFileName);

  if (!noInclude) {
    api->AddVariable("%Include",IncludeName->ToCString());
    api->WriteFile(VCxxFile,"%Include");
  }

  for (i = 1; i <= seqLen; i++) {
    
    if (!srcClass.IsNull()) {
      m = aSeqMemMet->Value(i);
    }
    else if (!srcPackage.IsNull()) {
      m = aSeqExtMet->Value(i);
    }
    else {
      ErrorMsg() << "TCPPExt" << "while extracting " << aName->ToCString()  << endm;
      Standard_NoSuchObject::Raise();
    }
    
    // here we process all the common attributes of methods
    //

    // build a c++ declaration method
    // the result is in the EDL variable VMethod
    //
    //   the EDL variables : 
    //        VMethodHeader : must contains the name of the template used for 
    //                        methods construction
    //        VConstructorHeader :  must contains the name of the template used for 
    //                              constructors construction
    //
    Handle(TCollection_HAsciiString) MetTemplate,
                                     ConTemplate;

    MetTemplate = api->GetVariableValue(VMethodHeader);
    ConTemplate = api->GetVariableValue(VConstructorHeader);
    // here we process all the common attributes of methods
    //
    api->AddVariable(VMethodName,m->Name()->ToCString());
    api->AddVariable(VVirtual,"");
    
    // it s inline method ?
    //
    if (m->IsInline()) {
      api->AddVariable(VIsInline,"yes");
      InlineMethod = Standard_True;
      if (!HasInlineMethod) {
	HasInlineMethod = Standard_True;
	api->OpenFile(VLxxFile,LxxFileName->ToCString());
	outfile->Append(LxxFileName);
      }
    }
    else {
      InlineMethod = Standard_False;
      api->AddVariable(VIsInline,"no");
    }
    
    // it s returning const ?
    //
    if (m->IsConstReturn()) {
      api->AddVariable(VRetSpec,"const");
    }
    else {
      api->AddVariable(VRetSpec,"");
    }
    
    // it s returning & ?
    //
    //
    Standard_CString pC[3]={"&","*",""};
    Standard_Integer iX;
    //
    iX=2;
    if (m->IsRefReturn()) {
      iX=0;
    }
    else if (m->IsPtrReturn()) {
      iX=1;
    }
    api->AddVariable(VAnd, pC[iX]);
    //
    api->AddVariable(VArgument,TCPP_BuildParameterList(aMeta,m->Params())->ToCString());
    
    // it s returning a type or void
    //
    retType = m->Returns();
    
    if (!retType.IsNull()) {
      Handle(TCollection_HAsciiString) aTypeName = retType->TypeName();
      api->AddVariable(VReturn,TCPP_BuildType(aMeta,aTypeName,srcClass)->ToCString());
    }
    else {
      api->AddVariable(VReturn,"void");
    }
  
    // now the specials attributes
    //
    // instance methods
    //
    if (m->IsKind(STANDARD_TYPE(MS_InstMet))) {
      im = Handle(MS_InstMet)::DownCast(m);
      
      api->AddVariable(VIsCreateMethod,"no");

      if (!im->IsDeferred()) {
	if (im->IsConst()) {
	  api->AddVariable(VMetSpec,"const");
	}
	else {
	  api->AddVariable(VMetSpec,"");
	}
	api->Apply(VMethod,MetTemplate->ToCString());
	
	if (InlineMethod) {
	  api->Apply(VMethod,"InlineMethodTemplate");
	  api->WriteFile(VLxxFile,"%Method");
	}
	else {
	  api->WriteFile(VCxxFile,"%Method");
	}
      }
    }
    //
    // class methods
    //
    else if (m->IsKind(STANDARD_TYPE(MS_ClassMet))) {
      api->AddVariable(VIsCreateMethod,"no");
      api->AddVariable(VMetSpec,"");
      api->Apply(VMethod,MetTemplate->ToCString());
      
      if (InlineMethod) {
	api->Apply(VMethod,"InlineMethodTemplate");
	api->WriteFile(VLxxFile,"%Method");
      }
      else {
	api->WriteFile(VCxxFile,"%Method");
      }
    }
    //
    // constructors
    //
    else if (m->IsKind(STANDARD_TYPE(MS_Construc))) {
      api->AddVariable(VIsCreateMethod,"yes");
      api->Apply(VMethod,ConTemplate->ToCString());
      
      if (InlineMethod) {
	api->Apply(VMethod,"InlineMethodTemplate");
	api->WriteFile(VLxxFile,"%Method");
      }
      else {
	api->WriteFile(VCxxFile,"%Method");
      }
    }
    //
    // package methods
    //
    else if (m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
      api->AddVariable(VIsCreateMethod,"no");
      api->AddVariable(VMetSpec,"");
      api->Apply(VMethod,MetTemplate->ToCString());
      if (InlineMethod) {
	api->Apply(VMethod,"InlineMethodTemplate");
	api->WriteFile(VLxxFile,"%Method");
      }
      else {
	api->WriteFile(VCxxFile,"%Method");
      }
    }
  }
  // then, we close the files
  //   note that inline methods file may be empty
  //
  api->CloseFile(VCxxFile);
  
  if (HasInlineMethod) {
    api->CloseFile(VLxxFile);
    }
}
