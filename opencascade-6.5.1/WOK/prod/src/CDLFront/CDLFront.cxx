// CDLFront.cxx      Version 1.1
//
//	Date: 06/04/1995 
//
//
//
#include <string.h>
#include <stdio.h>
// Standard includes
//
#include <Standard_ErrorHandler.hxx>

// MS includes
//
#include <MS_AccessMode.hxx>
#include <MS.hxx>
#include <MS_TraductionError.hxx>

#include <MS_Common.hxx>

#include <MS_Schema.hxx>
#include <MS_Engine.hxx>
#include <MS_Component.hxx>
#include <MS_Interface.hxx>
#include <MS_Package.hxx>
#include <MS_Executable.hxx>
#include <MS_ExecPart.hxx>
#include <MS_Alias.hxx>
#include <MS_Pointer.hxx>
#include <MS_Imported.hxx>
#include <MS_PrimType.hxx>
#include <MS_Enum.hxx>
#include <MS_Class.hxx>
#include <MS_StdClass.hxx>
#include <MS_Error.hxx>
#include <MS_GenClass.hxx>
#include <MS_InstClass.hxx>
#include <MS_Method.hxx>
#include <MS_ExternMet.hxx>
#include <MS_Construc.hxx>
#include <MS_InstMet.hxx>
#include <MS_ClassMet.hxx>
#include <MS_Field.hxx>
#include <MS_Param.hxx>
#include <MS_ParamWithValue.hxx>
#include <MS_GenType.hxx>
#include <MS_ExecFile.hxx>
#include <MS_Client.hxx>

#include <MS_MetaSchema.hxx>

#include <MS_Language.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

// instantiations
//
#include <MS_HSequenceOfPackage.hxx>
#include <MS_HSequenceOfInterface.hxx>
#include <MS_HSequenceOfType.hxx>
#include <MS_HSequenceOfGenClass.hxx>
#include <MS_HSequenceOfMethod.hxx>
#include <MS_HSequenceOfExecPart.hxx>
#include <MS_HSequenceOfGenType.hxx>
#include <MS_HSequenceOfParam.hxx>

// Collections includes
//
#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfInteger.hxx>

// CDLFront include
#include <CDLFront.hxx>

// lex and yacc glue includes
//
extern "C" {
#include <cdl_rules.h>
}
#include <cdl_defines.hxx>
#include <CDL.tab.h>

void CDL_InitVariable();

static int   YY_nb_error;
static int   YY_nb_warning;
static Handle(TCollection_HAsciiString)& CDLFileName() {
  static Handle(TCollection_HAsciiString) CDLFileName;
  return CDLFileName;
}

//=======================================================================
//function : CDLerror
//purpose  : 
//=======================================================================
extern "C" {
  void CDLerror(char* text)    {
    extern int CDLlineno;
    //
    // The unix like error declaration 
    //
    if (text == NULL) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" \
	<<  ", line " << CDLlineno << ": syntax error..." << endm;
      CDL_InitVariable();
      MS_TraductionError::Raise("Syntax error");
    }
    else {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() \
	<< "\"" <<  ", line " << CDLlineno << ": " << text << endm;
      YY_nb_error++;
    }
  }
// int yyparse();
#ifdef YYDEBUG
 extern int yydebug = 1;	
#endif
}

// line number of method definition (for C++ directive error)
//
static Standard_Integer methodlineno = 0;

#define MAX_CHAR                 256

// type of the current entity
//
#define CDL_NULL     0
#define CDL_PACKAGE  1
#define CDL_STDCLASS 2
#define CDL_GENCLASS 3
#define CDL_NESCLASS 4
#define CDL_INCDECL  5
#define CDL_GENTYPE  6 
#define CDL_INTERFACE 7
#define CDL_EXECUTABLE 8
#define CDL_CLIENT     9

#define CDL_CPP      1
#define CDL_FOR      2
#define CDL_C        3
#define CDL_OBJ      4
#define CDL_LIBRARY  5
#define CDL_EXTERNAL 6

#define CDL_MUSTNOTCHECKUSES 0
#define CDL_MUSTCHECKUSES    1

// because we don't check uses for friends
//
static Standard_Integer CheckUsesForClasses = CDL_MUSTCHECKUSES;

static Standard_Integer Current_Entity      = CDL_NULL;
static Standard_Integer SaveState           = CDL_NULL;

// lex variable
//      line number
//   
#ifndef WNT
extern int CDLlineno;
#else
extern "C" int CDLlineno;
#endif  // WNT

#ifndef WNT
extern FILE             *CDLin;
#else
extern "C" FILE             *CDLin;
#endif  // WNT
  
// The Flags
//
static Standard_Boolean Private        = Standard_False,      
                        Protected      = Standard_False,
                        Static         = Standard_True,   
                        Deferred       = Standard_False,
	                Redefined      = Standard_False,
	                Like           = Standard_False,
			Any            = Standard_False,
			CPPReturnRef   = Standard_False,
			CPPReturnConst = Standard_False,
			CPPOperator    = Standard_False,
			CPPAlias       = Standard_False,
			CPPInline      = Standard_False;

static Standard_Boolean DynaType  = Standard_False;


// The Identifiers
//
static 	 char   thetypename     [MAX_CHAR + 1],   // The name of the current type
                Pack_Name    [MAX_CHAR + 1];   // The Name of package

// The Classes
//
static Standard_Integer Mutable = 0,
                        InOrOut = MS_IN;

// Container : an entity where type are declared or defined
//   ex.: a package, an interface,...
//

static Handle(TCollection_HAsciiString)& Container() {
  static Handle(TCollection_HAsciiString) Container = new TCollection_HAsciiString;
  return Container;
}
// The variables representing the analyze of current object
// The Conventions:                           
//   Begining of analyse: a new object is creating. 
//        End of analyse:      the variable  is nullified  
//

// The Schema variables 
//
static Handle(MS_Schema)&	Schema() {
  static Handle(MS_Schema)	Schema;     // The current Schema
  return Schema;
}

// The Engine variables
//
static Handle(MS_Engine)&	Engine() {
  static Handle(MS_Engine)	Engine;     // The current Engine
  return Engine;
}

// The Component variables
//
static Handle(MS_Component)&	Component() {
  static Handle(MS_Component)	Component;     // The current Component
  return Component;
}

// The Executable variables
//
static Handle(MS_Executable)&	      Executable() {
  static Handle(MS_Executable)	      Executable; // The current Executable
  return Executable;
}

static Handle(MS_ExecPart)&	      ExecPart() {
  static Handle(MS_ExecPart)	      ExecPart;   // The current ExePart
  return ExecPart;
}

static Handle(MS_HSequenceOfExecPart)& ExecTable() {
  static Handle(MS_HSequenceOfExecPart) ExecTable;
  return ExecTable;
}

static int ExecutableLanguage;
static int ExecutableUseType;

// The Client variables
//
static Handle(MS_Client)&	Client() {
  static Handle(MS_Client)	Client;  // The current Client
  return Client;
}

// The Interface variables
//
static Handle(MS_Interface)&	Interface() {
  static Handle(MS_Interface)	Interface;  // The current Interface
  return Interface;
}

// The Package variables
//
static Handle(MS_Package)&	Package() {
  static Handle(MS_Package)	Package;    // The current package
  return Package;
}

// The Alias variables
//
static Handle(MS_Alias)&	Alias() {
  static Handle(MS_Alias)	Alias;	    // The current Alias
  return Alias;
}

// The Pointer variables
//
static Handle(MS_Pointer)&	Pointer() {
  static Handle(MS_Pointer)	Pointer;    // The current Pointer
  return Pointer;
}

// The Imported variables
//
static Handle(MS_Imported)&	Imported() {
  static Handle(MS_Imported)	Imported;   // The current Imported
  return Imported;
}

// The primitive variables
//
static Handle(MS_PrimType)&	Primitive() {
  static Handle(MS_PrimType)	Primitive;  // The current Primitive
  return Primitive;
}

// The Enum variables
//
static Handle(MS_Enum)&		Enum() {
  static Handle(MS_Enum)		Enum;	    // The current enum
  return Enum;
}

// The Error (exception) class
//
static Handle(MS_Error)&         Exception() {
  static Handle(MS_Error)         Exception;
  return Exception;
}

// For dynamic generic instantiation like
//   generic class toto (item1, this one --> item2 as list from TCollection(item1))
//
static Handle(MS_GenType)&       DynType() {
  static Handle(MS_GenType)       DynType;
  return DynType;
}

// The Class variables
//
static Handle(MS_Class)&        SimpleClass() {
  static Handle(MS_Class)	        SimpleClass;
  return SimpleClass;
}

static Handle(MS_StdClass)&	Class() {
  static Handle(MS_StdClass)	Class;      // The current class
  return Class;
}

static Handle(MS_StdClass)&	StdClass() {
  static Handle(MS_StdClass)	StdClass;   // The current class is
  return                        StdClass;   // a Standard Class
}

static Handle(MS_StdClass)&	GenStdClass() {
  static Handle(MS_StdClass)	GenStdClass;// The current class
  return 	GenStdClass;		    // descipt a Generic Class
}

static Handle(MS_Error)&        Error() {
  static Handle(MS_Error)	        Error;      // The current class is
  return 		Error;		    // a Exception
}

static Handle(MS_GenClass)&	GenClass() {         
  static Handle(MS_GenClass)	GenClass;   // The current class is
  return 		GenClass;	    // a Generic class 
    
}

static Handle(MS_InstClass)&	InstClass() {
  static Handle(MS_InstClass)	InstClass;  // The current class is
  return 		InstClass;	    // a instanciated class
}

static Handle(MS_GenClass)&	Embeded() {
static Handle(MS_GenClass)	Embeded;    // The current class is
 return 	Embeded;		    // embeded class       
}

// The Method variables
//
static Handle(TCollection_HAsciiString)& MethodName() {
  static Handle(TCollection_HAsciiString) MethodName = new TCollection_HAsciiString;
  return MethodName;
}

static Handle(MS_Method)&	Method() {
  static Handle(MS_Method)	Method;     // The current method
  return Method;
}

static Handle(MS_MemberMet)&	MemberMet() {
  static Handle(MS_MemberMet)	MemberMet;  // The Membermethod
  return MemberMet;
}

static Handle(MS_ExternMet)&	ExternMet() {
  static Handle(MS_ExternMet)	ExternMet;  // The current metod is
  return 	ExternMet;		    // a method of package
}

static Handle(MS_Construc)&	Construc() {
  static Handle(MS_Construc)	Construc;   // The current metod is
  return 		Construc;	    // a constructeur
}

static Handle(MS_InstMet)&	InstMet() {
  static Handle(MS_InstMet)	InstMet;    // The current metod is
  return 	InstMet;		    // a method of instance
}

static Handle(MS_ClassMet)&	ClassMet() {
  static Handle(MS_ClassMet)	ClassMet;   // The current metod is
  return 		ClassMet;	    // a method of class
}

static Handle(MS_HSequenceOfParam)& MethodParams() {
  static Handle(MS_HSequenceOfParam) MethodParams; // The current method parameters
  return MethodParams;
}

// The most important : the meta-schema
//
static Handle(MS_MetaSchema)&           theMetaSchema() {
  static Handle(MS_MetaSchema)           theMetaSchema;
  return theMetaSchema;
}

// The Field variables
//
static Handle(MS_Field)&	Field() {
  static Handle(MS_Field)	Field;
  return Field;
}

static Handle(TCollection_HAsciiString)& DefCons() {
  static Handle(TCollection_HAsciiString) DefCons = new TCollection_HAsciiString("Initialize");
  return DefCons;
}

static Handle(TCollection_HAsciiString)& NorCons() {
  static Handle(TCollection_HAsciiString) NorCons = new TCollection_HAsciiString("Create");
  return NorCons;
}

// The Parameter variables
//
static Handle(MS_Param)&                Param() {
  static Handle(MS_Param)	                Param;
  return Param;
}

static Standard_Integer                 ParamType = 0;

static Handle(TCollection_HAsciiString)& ParamValue() {
  static Handle(TCollection_HAsciiString) ParamValue;
  return ParamValue;
}

// for clause like : type1,type2,type3, ... ,typen
//
static Handle(TColStd_HSequenceOfHAsciiString)& ListOfTypes() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfTypes     = new TColStd_HSequenceOfHAsciiString;
  return ListOfTypes;
}

static Handle(TColStd_HSequenceOfHAsciiString)& ListOfPackages() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfPackages  = new TColStd_HSequenceOfHAsciiString;
  return ListOfPackages;
}

// for generic classes (generic item1, ... ,generic itemn)
//
static Handle(TColStd_HSequenceOfHAsciiString)& ListOfItem() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfItem      = new TColStd_HSequenceOfHAsciiString;
  return ListOfItem;
}

static Handle(TColStd_HSequenceOfHAsciiString)& ListOfName() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfName      = new TColStd_HSequenceOfHAsciiString;
  return ListOfName;
}

static Handle(TColStd_HSequenceOfHAsciiString)& ListOfCplusplus() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfCplusplus = new TColStd_HSequenceOfHAsciiString;
  return ListOfCplusplus;
}

static Handle(TColStd_HSequenceOfHAsciiString)& ListOfComments() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfComments  = new TColStd_HSequenceOfHAsciiString;
  return ListOfComments;
}

static Handle(TColStd_HSequenceOfInteger)&      ListOfCPPType() {
  static Handle(TColStd_HSequenceOfInteger)      ListOfCPPType   = new TColStd_HSequenceOfInteger;
  return ListOfCPPType;
}

static Handle(TColStd_HSequenceOfHAsciiString)& ListOfInteger() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfInteger   = new TColStd_HSequenceOfHAsciiString;
  return ListOfInteger;
}

// this is a dummy package name for generic type (item, etc...)
//
const char *aDummyPackageName = "___D";
const char *theRootPack = "Standard";
char        thePackNameFound[128];

static Handle(TColStd_HSequenceOfHAsciiString)& ListOfGlobalUsed() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfGlobalUsed;
  return ListOfGlobalUsed;
}

static Handle(TColStd_HSequenceOfHAsciiString)& ListOfTypeUsed() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfTypeUsed;
  return ListOfTypeUsed;
}

static Handle(TColStd_HSequenceOfHAsciiString)& ListOfInst() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfInst;
  return ListOfInst;
}

static Handle(TColStd_HSequenceOfHAsciiString)& ListOfGen() {
  static Handle(TColStd_HSequenceOfHAsciiString) ListOfGen;
  return ListOfGen;
}

void CDL_MustNotCheckUses() 
{
  CheckUsesForClasses = CDL_MUSTNOTCHECKUSES;
}

void CDL_MustCheckUses() 
{
  CheckUsesForClasses = CDL_MUSTCHECKUSES;
}

void CDL_InitVariable()
{
  CheckUsesForClasses = CDL_MUSTCHECKUSES;
  Current_Entity = CDL_NULL;
  SaveState      = CDL_NULL;
  Private        = Standard_False;
  Protected      = Standard_False;
  Static         = Standard_True;
  Deferred       = Standard_False;
  Redefined      = Standard_False;
  Like           = Standard_False;
  Any            = Standard_False;
  CPPReturnRef   = Standard_False;
  CPPReturnConst = Standard_False;
  CPPOperator    = Standard_False;
  CPPAlias       = Standard_False;
  CPPInline      = Standard_False;
  YY_nb_error    = 0;
  YY_nb_warning  = 0;

  DynaType  = Standard_False;
  Mutable = 0;
  InOrOut = MS_IN;
  Container() = new TCollection_HAsciiString;
  Schema().Nullify();
  Engine().Nullify();
  Component().Nullify();
  Executable().Nullify();
  ExecPart().Nullify();
  ExecTable().Nullify();
  ExecutableLanguage = CDL_CPP;
  ExecutableUseType = CDL_LIBRARY;
  Interface().Nullify();
  Package().Nullify();    
  Alias().Nullify();
  Pointer().Nullify();
  Imported().Nullify();
  Primitive().Nullify();
  Enum().Nullify();
  Exception().Nullify();
  DynType().Nullify();
  SimpleClass().Nullify();
  Class().Nullify();
  StdClass().Nullify();
  GenStdClass().Nullify();
  Error().Nullify();
  GenClass().Nullify();
  InstClass().Nullify();
  Embeded().Nullify();
  MethodName() = new TCollection_HAsciiString;
  Method().Nullify();
  MemberMet().Nullify();
  ExternMet().Nullify();
  Construc().Nullify();
  MethodParams().Nullify();
  InstMet().Nullify();
  ClassMet().Nullify();
  theMetaSchema().Nullify();
  Field().Nullify();
  Param().Nullify();
  ParamType = 0;
  ParamValue().Nullify();
  Client().Nullify();
  ListOfTypes()     = new TColStd_HSequenceOfHAsciiString;
  ListOfPackages()  = new TColStd_HSequenceOfHAsciiString;
  ListOfItem()      = new TColStd_HSequenceOfHAsciiString;
  ListOfName()      = new TColStd_HSequenceOfHAsciiString;
  ListOfCplusplus() = new TColStd_HSequenceOfHAsciiString;
  ListOfComments()  = new TColStd_HSequenceOfHAsciiString;
  ListOfCPPType()   = new TColStd_HSequenceOfInteger;
  ListOfInteger()   = new TColStd_HSequenceOfHAsciiString;
  ListOfGlobalUsed().Nullify();
  ListOfTypeUsed().Nullify();
  ListOfInst().Nullify();
  ListOfGen().Nullify();
}

// ////////////////////////////////////////
// Implementation                        //
// ////////////////////////////////////////
void Clear_ListOfItem() 
{
  ListOfItem()->Clear();
}

void set_inc_state() 
{
  SaveState      = Current_Entity;
  Current_Entity = CDL_INCDECL;
}

void restore_state() 
{
  Current_Entity = SaveState;
}

void Type_Name(char *aName)	 
{
  strncpy(thetypename,aName,MAX_CHAR);
}

// WARNING : dirty code : look at "Standard_" (but faster than build a string from MS::RootPackageName() + "_")
//
//=======================================================================
//function : VerifyClassUses
//purpose  : 
//=======================================================================
Standard_Boolean VerifyClassUses(const Handle(TCollection_HAsciiString)& theTypeName) 
{
  if ((Current_Entity == CDL_STDCLASS || 
       Current_Entity == CDL_GENCLASS) && 
      CheckUsesForClasses == CDL_MUSTCHECKUSES) {
    // WARNING : dirty code -> here is !!! (sorry for future hacker, guilty : CLE)
    //
    if (strncmp("Standard_",theTypeName->ToCString(),9) == 0) {
      if (theMetaSchema()->IsDefined(theTypeName)) {
	ListOfTypeUsed()->Append(theTypeName);

	return Standard_True;
      }
      else {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() \
	  << "\"" <<  ", line " << CDLlineno << ": " \
	    << "The package Standard has no declaration of " \
	      << "'" << theTypeName << "'" << endm;
	YY_nb_error++;
	return Standard_True;
      }
    }

    if (theTypeName->IsSameString(SimpleClass()->FullName())) return  Standard_True;

    if (Current_Entity == CDL_GENCLASS) {
      if (theTypeName->IsSameString(GenClass()->FullName())) return  Standard_True;

      Standard_Integer                        i;
      Handle(TColStd_HSequenceOfHAsciiString) seqascii = GenClass()->GetNestedName();
      Handle(TCollection_HAsciiString)        nestname,
                                              nestnestname = new TCollection_HAsciiString;

      if (theMetaSchema()->IsDefined(theTypeName)) {
	Handle(MS_Type) theType = theMetaSchema()->GetType(theTypeName);
	
	if (theType->IsKind(STANDARD_TYPE(MS_Class))) {
	  Handle(MS_Class) inst = *((Handle(MS_Class)*)&theType);
	  
	  if (!inst->GetNestingClass().IsNull()) {
	    if (GenClass()->FullName()->IsSameString(inst->GetNestingClass())) return Standard_True;
	    nestnestname = inst->GetNestingClass();
	  }
	}
      }

      for (i = 1; i <= seqascii->Length(); i++) {
	nestname = MS::BuildFullName(Container(),seqascii->Value(i));

	if (theTypeName->IsSameString(nestname) || nestnestname->IsSameString(nestname)) {
	  return Standard_True;
	}
      }

      Handle(MS_HSequenceOfGenType) genericitems = GenClass()->GenTypes();

      for (i = 1; i <= genericitems->Length(); i++) {
	if (genericitems->Value(i)->Name()->IsSameString(theTypeName)) {
	  return Standard_True;
	}
      }
    }

    Handle(TColStd_HSequenceOfHAsciiString) seqOfType = SimpleClass()->GetUsesNames();

     for (Standard_Integer i = 1; i <= seqOfType->Length(); i++) {
       if (seqOfType->Value(i)->IsSameString(theTypeName)) {
	 return Standard_True;
       }
     }  
    
      //for (i = 1; i <= ListOfComments()->Length(); i++ ) {
      //   SimpleClass()->SetComment(ListOfComments()->Value(i));
      //}  

    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString()\
      << "\"" <<  ", line " << CDLlineno << ": " \
	<< "The 'uses' statement of your class has no declaration of : " \
	  << theTypeName << endm;
    YY_nb_error++;
  }
  else return Standard_True;

  return Standard_False;
}

//=======================================================================
//function : VerifyUses
//purpose  : 
//=======================================================================
Standard_Boolean VerifyUses(char* used)
{
  if (Current_Entity == CDL_PACKAGE || 
      Current_Entity == CDL_INTERFACE || 
      Current_Entity == CDL_EXECUTABLE) {
    Handle(TColStd_HSequenceOfHAsciiString)  aSeqOfPackage;
    Handle(MS_Package)                       aPackage;
    Handle(MS_Interface)                     anInterface;
    Handle(MS_Engine)                        anEngine;
    Handle(MS_Component)                     aComponent;
    Standard_Boolean                         status = Standard_False;
    Standard_Integer                         i;

    if (theMetaSchema()->IsPackage(Container())) {
      aPackage = theMetaSchema()->GetPackage(Container());
      aSeqOfPackage = aPackage->Uses();
      //for (i = 1; i <= ListOfComments()->Length(); i++ ) {
         //aPackage->SetComment(ListOfComments()->Value(i));
      //}  

    } 
    else if (theMetaSchema()->IsInterface(Container())) {
      anInterface = theMetaSchema()->GetInterface(Container());
      aSeqOfPackage = anInterface->Uses();
    }
    else if (theMetaSchema()->IsEngine(Container())) {
      anEngine = theMetaSchema()->GetEngine(Container());
      aSeqOfPackage = anEngine->Uses();
    }
    else if (theMetaSchema()->IsComponent(Container())) {
      aComponent = theMetaSchema()->GetComponent(Container());
      aSeqOfPackage = aComponent->Uses();
    }
    
    for (i = 1; i <= aSeqOfPackage->Length() && (status == 0); i++) {
      if (strcmp(aSeqOfPackage->Value(i)->ToCString(),used) == 0) {
	status = Standard_True;
      }
    }
    ListOfComments()->Clear();

    return status;
  }
  else return Standard_True;
}

void Type_Pack(char *aName)	 
{
  if (!VerifyUses(aName)) {
    Handle(TCollection_HAsciiString) msg = new TCollection_HAsciiString("the entity : ");
    msg->AssignCat(aName);
    msg->AssignCat(" is not in the 'uses' clause of ");
    msg->AssignCat(Container());
    CDLerror((char*)msg->ToCString());
  }

  strncpy(Pack_Name,aName,MAX_CHAR);
}

char *TypeCompletion(char *aName)
{
  Handle(TColStd_HSequenceOfHAsciiString)  aSeqOfPackage;
  Handle(TCollection_HAsciiString)         aFullName     = new TCollection_HAsciiString;
  Standard_Integer                         i;
  
  if (Current_Entity == CDL_GENCLASS || Current_Entity == CDL_STDCLASS) {
    Handle(TCollection_HAsciiString) aPackageName, thethetypename = new TCollection_HAsciiString(aName);
    

    if (SimpleClass()->Name()->IsSameString(thethetypename)) {
       return (char *)Container()->ToCString();
    }

    aSeqOfPackage = SimpleClass()->GetUsesNames();
    for (i = 1; i <= aSeqOfPackage->Length(); i++) {
      aPackageName = aSeqOfPackage->Value(i)->Token("_");
      if (aSeqOfPackage->Value(i)->IsSameString(MS::BuildFullName(aPackageName,thethetypename))) {
	strcpy(thePackNameFound,aPackageName->ToCString());
	return thePackNameFound;
      }
    }

    if (theMetaSchema()->IsDefined(MS::BuildFullName(MS::GetPackageRootName(),thethetypename))) return (char *)theRootPack;

    if (Current_Entity == CDL_GENCLASS) { 
      for (i = 1; i <= ListOfItem()->Length(); i++) {
	if (strcmp(ListOfItem()->Value(i)->ToCString(),aName) == 0) {
	  return (char *)aDummyPackageName;
	}
      }
    } 
  }

  Handle(MS_Package)                       aPackage;
  Handle(MS_Interface)                     anInterface;
  Handle(MS_Engine)                        anEngine;
  Handle(MS_Component)                     aComponent;
  
  if (theMetaSchema()->IsPackage(Container())) {
    aPackage = theMetaSchema()->GetPackage(Container());
    aSeqOfPackage = aPackage->Uses();
  } 
  else if (theMetaSchema()->IsInterface(Container())) {
    anInterface = theMetaSchema()->GetInterface(Container());
    aSeqOfPackage = anInterface->Uses();
  }
  else if (theMetaSchema()->IsEngine(Container())) {
    anEngine = theMetaSchema()->GetEngine(Container());
    aSeqOfPackage = anEngine->Uses();
  }
  else if (theMetaSchema()->IsComponent(Container())) {
    aComponent = theMetaSchema()->GetComponent(Container());
    aSeqOfPackage = aComponent->Uses();
  }
  else {
    aSeqOfPackage = new TColStd_HSequenceOfHAsciiString;
    aSeqOfPackage->Append(MS::GetPackageRootName());
  }
  
  for (i = 1; i <= aSeqOfPackage->Length(); i++) {
    aFullName->AssignCat(aSeqOfPackage->Value(i));
    aFullName->AssignCat("_");
    aFullName->AssignCat(aName);
    
    if (theMetaSchema()->IsDefined(aFullName)) {
      return (char*)(aSeqOfPackage->Value(i)->ToCString());
    }
    
    aFullName->Clear();
  }

 
  return NULL;
}

void Type_Pack_Blanc()   
{
  char *thePackName;

  // we check if we are able to use incomplete declaration
  //
  if (Current_Entity == CDL_PACKAGE || 
      Current_Entity == CDL_INTERFACE ||
      Current_Entity == CDL_EXECUTABLE || 
      Current_Entity == CDL_CLIENT) {
    Handle(TCollection_HAsciiString)         aFullName     = new TCollection_HAsciiString;
    aFullName->AssignCat(Container());
    aFullName->AssignCat("_");
    aFullName->AssignCat(thetypename);
    
    if (!theMetaSchema()->IsDefined(aFullName)) {
      aFullName->Clear();
      aFullName->AssignCat(MS::GetPackageRootName());
      aFullName->AssignCat("_");
      aFullName->AssignCat(thetypename);

      if (!theMetaSchema()->IsDefined(aFullName)) {
	Handle(TCollection_HAsciiString) msg = new TCollection_HAsciiString("the type '");
	msg->AssignCat(thetypename);	
	msg->AssignCat("' must be followed by a package name.");
	CDLerror((char *)(msg->ToCString()));
      }
    }
  }

  if (Current_Entity != CDL_INCDECL && Current_Entity != CDL_GENTYPE) { 
    thePackName = TypeCompletion(thetypename);
    
    if (thePackName == NULL) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "the type '" << thetypename << "' is not defined." << endm;
      YY_nb_error++;
    }
    else {
      Type_Pack(thePackName);
    }
  }
  else {
    Type_Pack((char *)(Container()->ToCString()));
  }

}

void Add_Type() 
{
  Handle(TCollection_HAsciiString) aPackName = new TCollection_HAsciiString(Pack_Name);
  Handle(TCollection_HAsciiString) athetypename = new TCollection_HAsciiString(thetypename);

  ListOfTypes()->Append(athetypename);
  ListOfPackages()->Append(aPackName);
}

void add_documentation(char *comment)
{ 
  Handle(TCollection_HAsciiString) aComment;
  Handle(TCollection_HAsciiString) aRealComment;
  Standard_Integer pos;
  aComment = new TCollection_HAsciiString(comment);
  pos = aComment->Location(1,':',1,aComment->Length());
  aRealComment = aComment->SubString(pos + 1, aComment->Length());
  aRealComment->RightAdjust();
  if (!aRealComment->IsEmpty()) {
    aRealComment->AssignCat (" <br>");
    aRealComment->Insert(1,"//!");
    ListOfComments()->Append(aRealComment);
  }
}

void add_documentation1(char *comment)
{ 
  while ( *comment && IsSpace(*comment)) comment++;
  while ( *comment == '-' ) comment++;
  if ( ! *comment ) return;

  Handle(TCollection_HAsciiString) aRealComment;
  aRealComment = new TCollection_HAsciiString(comment);
  aRealComment->RightAdjust();
  aRealComment->AssignCat (" <br>");
  aRealComment->Insert(1,"\n//!");
  ListOfComments()->Append(aRealComment);
}

//=======================================================================
//function : add_cpp_comment
//purpose  : 
//=======================================================================
void add_cpp_comment(int cpptype, char *comment)
{
  Handle(TCollection_HAsciiString) aComment;
  Handle(TCollection_HAsciiString) aRealComment;

  if (Method().IsNull()) {
    WarningMsg() << "CDL" << "line " << CDLlineno \
      << " : " << "C++ directive outside method definition : "\
	<< comment << endm;
    YY_nb_warning++;
  }
  else {
    if (cpptype == CDL_HARDALIAS || cpptype == CDL_OPERATOR) {
      Standard_Integer pos;
      aComment = new TCollection_HAsciiString(comment);
      
      pos = aComment->Location(1,':',1,aComment->Length());
      aRealComment = aComment->SubString(pos + 1,aComment->Length());
      aRealComment->LeftAdjust();
    }
    
    ListOfCplusplus()->Append(aRealComment);
    ListOfCPPType()->Append(cpptype);
  }
}

//=======================================================================
//function : add_name_to_list
//purpose  : 
//=======================================================================
void add_name_to_list(char *name)
{
  Handle(TCollection_HAsciiString) aName = 
    new TCollection_HAsciiString(name);

  ListOfName()->Append(aName);
}

//=======================================================================
//function : Begin_List_Int
//purpose  : 
//=======================================================================
void Begin_List_Int(char *anInt) 
{
  Handle(TCollection_HAsciiString) Int = new TCollection_HAsciiString(anInt);

  ListOfInteger()->Clear();
  ListOfInteger()->Append(Int);
}

void Make_List_Int(char *anInt)
{
  Handle(TCollection_HAsciiString) Int = new TCollection_HAsciiString(anInt);

  ListOfInteger()->Append(Int);
}

// The actions for the Schema
//
void Schema_Begin(char *name)
{
  Handle(TCollection_HAsciiString) aSchemaName = new TCollection_HAsciiString(name);

  Schema() = new MS_Schema(aSchemaName);
  Schema()->MetaSchema(theMetaSchema());
  Container() = aSchemaName;

  if (!theMetaSchema()->AddSchema(Schema())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Schema : " << aSchemaName << " is already defined." << endm;
    YY_nb_error++;
  }
 ListOfComments()->Clear();
}

void Schema_Package(char *name)
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) aName = new TCollection_HAsciiString(name);
  Schema()->Package(aName);
  for(i = 1; i <= ListOfComments()->Length(); i++) {
     Schema()->SetComment(ListOfComments()->Value(i));
  }
  ListOfComments()->Clear();
}

void Schema_Class()
{
  Handle(TCollection_HAsciiString) aClassName = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackageName = new TCollection_HAsciiString(Pack_Name);

  Schema()->Class(MS::BuildFullName(aPackageName,aClassName));
}

void Schema_End()
{
  Schema().Nullify();
  Container().Nullify();
}

// The actions for the Engine
//
void Engine_Begin(char *engineName)
{
  Handle(TCollection_HAsciiString) anEngineName = new TCollection_HAsciiString(engineName);
  
  Engine() = new MS_Engine(anEngineName);
  Engine()->MetaSchema(theMetaSchema());
  Container() = anEngineName;

  if (!theMetaSchema()->AddEngine(Engine())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Engine : " << anEngineName << " is already defined." << endm;
    YY_nb_error++;
  }

  Engine()->Use(MS::GetPackageRootName());
}

void Engine_Schema(char *name)
{
  Handle(TCollection_HAsciiString) sname = new TCollection_HAsciiString(name);

  Engine()->Schema(sname);
  ListOfGlobalUsed()->Append(sname);
}

void Engine_Interface(char *inter)
{
  Handle(TCollection_HAsciiString) sname = new TCollection_HAsciiString(inter);

  Engine()->Interface(sname);
  ListOfGlobalUsed()->Append(sname);
}

void Engine_End()
{
  Engine().Nullify();
  Container().Nullify();
}

// The actions for the Component
//
void Component_Begin(char *ComponentName)
{
  Handle(TCollection_HAsciiString) anComponentName = new TCollection_HAsciiString(ComponentName);
  
  Component() = new MS_Component(anComponentName);
  Component()->MetaSchema(theMetaSchema());
  Container() = anComponentName;

  if (!theMetaSchema()->AddComponent(Component())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Component : " << anComponentName << " is already defined." << endm;
    YY_nb_error++;
  }

  Component()->Use(MS::GetPackageRootName());
}

void Component_Interface(char *inter, char *udname)
{
  Handle(TCollection_HAsciiString) uname = new TCollection_HAsciiString(udname);
  Handle(TCollection_HAsciiString) sname = new TCollection_HAsciiString(inter);

  sname = MS::BuildFullName(uname,sname);
  Component()->Interface(sname);
  ListOfGlobalUsed()->Append(sname);
}

void Component_End()
{
  Component().Nullify();
  Container().Nullify();
}

// UD : stub client
//
void Client_Begin(char *clientName)
{
  Handle(TCollection_HAsciiString) aClientName = new TCollection_HAsciiString(clientName);
  
  Client() = new MS_Client(aClientName);
  Client()->MetaSchema(theMetaSchema());
  Container() = aClientName;
  
  if (!theMetaSchema()->AddClient(Client())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Client : " << clientName << " is already defined." << endm;
    YY_nb_error++;
  }
  
  Current_Entity = CDL_CLIENT;
}

void Client_Interface(char *inter)
{
  Handle(TCollection_HAsciiString) aIName = new TCollection_HAsciiString(inter);

  Client()->Interface(aIName);
}

void Client_Method(char *entity, int execmode)
{
  if (execmode == 1) {
    if (entity != NULL && !ExternMet().IsNull()) {
      ExternMet()->Package(new TCollection_HAsciiString(entity));
    }
    Method()->Params(MethodParams());
    MethodParams().Nullify();
    Method()->CreateFullName();
    
    Client()->Method(Method()->FullName());
  }
  else if (execmode < 0) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "constructor cannot have the asynchronous execution mode." << endm;
    YY_nb_error++;
  }

  Method().Nullify();
  MemberMet().Nullify();
  ExternMet().Nullify();
  Construc().Nullify();
  InstMet().Nullify();
  ClassMet().Nullify();
}


void Client_End() 
{
  Method().Nullify();
  MemberMet().Nullify();
  ExternMet().Nullify();
  Construc().Nullify();
  InstMet().Nullify();
  ClassMet().Nullify();
  Interface().Nullify();
  Container().Nullify();
  Client().Nullify();

  Current_Entity = CDL_NULL;
  Private   = Standard_False;
  Protected = Standard_False;
  Static    = Standard_True;
  Deferred  = Standard_False;
  Redefined = Standard_False;
  Like      = Standard_False;
}

// The actions for the Executable

void Executable_Begin(char *name)
{
  ExecutableLanguage = CDL_CPP;
  ExecutableUseType  = 0;

  Handle(TCollection_HAsciiString) anExecName = new TCollection_HAsciiString(name);

  Executable() = new MS_Executable(anExecName);
  Executable()->MetaSchema(theMetaSchema());

  if (!theMetaSchema()->AddExecutable(Executable())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Executable : " << anExecName << " is already defined." << endm;
    YY_nb_error++;
  }

  ExecTable() = new MS_HSequenceOfExecPart;

  Current_Entity = CDL_EXECUTABLE;
}

void ExecFile_Begin(char *name)
{
  ExecutableLanguage = CDL_CPP;
  ExecutableUseType  = 0;

  Handle(TCollection_HAsciiString) anExecName = new TCollection_HAsciiString(name);

  ExecPart() = new MS_ExecPart(anExecName);
  ExecPart()->MetaSchema(theMetaSchema());
  ExecTable()->Append(ExecPart());
}


void ExecFile_Schema(char *name)
{
  Handle(TCollection_HAsciiString) a = new TCollection_HAsciiString(name);

  ExecPart()->Schema(a);
}

void ExecFile_AddUse(char *name)
{
  Handle(TCollection_HAsciiString) a = new TCollection_HAsciiString(name);

  if (ExecutableUseType == CDL_LIBRARY) {
    ExecPart()->AddLibrary(a);
  }
  else {
    ExecPart()->AddExternal(a);
  }
}

void ExecFile_SetUseType(int t)
{
  ExecutableUseType = t;
}

void ExecFile_AddComponent(char *name)
{
  Handle(TCollection_HAsciiString) a = new TCollection_HAsciiString(name);
  Handle(MS_ExecFile)              aFile;

  aFile = new MS_ExecFile(a);

  switch (ExecutableLanguage) {
  case CDL_CPP : aFile->SetLanguage(MS_CPP);
                 break;
  case CDL_FOR : aFile->SetLanguage(MS_FORTRAN);
                 break;
  case CDL_C   : aFile->SetLanguage(MS_C);
                 break;
  case CDL_OBJ : aFile->SetLanguage(MS_OBJECT);
                 break;
    default :  aFile->SetLanguage(MS_CPP);
                 break;
  }

  ExecPart()->AddFile(aFile);
}

void ExecFile_SetLang(int l)
{
  ExecutableLanguage = l;
}

void ExecFile_End()
{
  ExecPart().Nullify();
}

void Executable_End()
{
  Executable()->AddParts(ExecTable());

  ExecTable().Nullify();
  Method().Nullify();
  MemberMet().Nullify();
  ExternMet().Nullify();
  Construc().Nullify();
  InstMet().Nullify();
  ClassMet().Nullify();
  Interface().Nullify();
  Executable().Nullify();
  Client().Nullify();

  Current_Entity = CDL_NULL;
  Private   = Standard_False;
  Protected = Standard_False;
  Static    = Standard_True;
  Deferred  = Standard_False;
  Redefined = Standard_False;
  Like      = Standard_False;
}

// The actions for the Interface

void Interface_Begin(char *anInterName)
{
  Handle(TCollection_HAsciiString) anInterfaceName = new TCollection_HAsciiString(anInterName);

  Interface() = new MS_Interface(anInterfaceName);
  Interface()->MetaSchema(theMetaSchema());
  Container() = anInterfaceName;

  if (!theMetaSchema()->AddInterface(Interface())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Interface : " << anInterName << " is already defined." << endm;
    YY_nb_error++;
  }

  Interface()->Use(MS::GetPackageRootName());
  Current_Entity = CDL_INTERFACE;
}

void Interface_Use(char*aPackageName)
{
  Handle(TCollection_HAsciiString) aPackName = new TCollection_HAsciiString(aPackageName);

  ListOfGlobalUsed()->Append(aPackName);
  Interface()->Use(aPackName);
}

void Client_Use ( char* aClientName ) {

 Handle( TCollection_HAsciiString ) aCltName =
  new TCollection_HAsciiString ( aClientName );

 Client() -> Use ( aCltName );

}  // end Client_Use

void Interface_Package(char *aPackageName)
{
  Handle(TCollection_HAsciiString) aPackName = new TCollection_HAsciiString(aPackageName);

  Interface()->Package(aPackName);
}

void Interface_Class()
{
  Handle(TCollection_HAsciiString) aClassName = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackageName = new TCollection_HAsciiString(Pack_Name);

  Interface()->Class(MS::BuildFullName(aPackageName,aClassName));
  ListOfTypeUsed()->Append(MS::BuildFullName(aPackageName,aClassName));
}

void Method_TypeName()
{
  Handle(TCollection_HAsciiString) aClassName = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackageName = new TCollection_HAsciiString(Pack_Name);

  ListOfTypeUsed()->Append(MS::BuildFullName(aPackageName,aClassName));
}

void Interface_Method(char *entityName)
{
  if (entityName != NULL && !ExternMet().IsNull()) {
    ExternMet()->Package(new TCollection_HAsciiString(entityName));
  }

  Method()->Params(MethodParams());
  MethodParams().Nullify();
  Method()->CreateFullName();
  Interface()->Method(Method()->FullName());

  Method().Nullify();
  MemberMet().Nullify();
  ExternMet().Nullify();
  Construc().Nullify();
  InstMet().Nullify();
  ClassMet().Nullify();
}

void Interface_End()
{
  Method().Nullify();
  MemberMet().Nullify();
  ExternMet().Nullify();
  Construc().Nullify();
  InstMet().Nullify();
  ClassMet().Nullify();
  Interface().Nullify();
  Container().Nullify();
  Client().Nullify();

  Current_Entity = CDL_NULL;
  Private   = Standard_False;
  Protected = Standard_False;
  Static    = Standard_True;
  Deferred  = Standard_False;
  Redefined = Standard_False;
  Like      = Standard_False;
}

// The actions for the Package
//
void Pack_Begin(char *aPackageName)
{
  
  Standard_Integer i;

  Handle(TCollection_HAsciiString) aPackName = new TCollection_HAsciiString(aPackageName);

  Container() = aPackName;

  Package() = new MS_Package(aPackName);
 
  Package()->MetaSchema(theMetaSchema());

  for (i = 1; i <= ListOfComments()->Length(); i++) {
    Package()->SetComment(ListOfComments()->Value(i));
  }  

  if (!theMetaSchema()->AddPackage(Package())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Package : " << aPackageName << " is already defined." << endm;
    YY_nb_error++;
  }
  Package()->Use(MS::GetPackageRootName());


  Current_Entity = CDL_PACKAGE;
  ListOfComments()->Clear();

}

void Pack_Use(char *aPackageName)
{
  Handle(TCollection_HAsciiString) aPackName = new TCollection_HAsciiString(aPackageName);
  for (Standard_Integer i = 1; i <= ListOfComments()->Length(); i++) {
    Package()->SetComment(ListOfComments()->Value(i));
  }  

  ListOfGlobalUsed()->Append(aPackName);
  Package()->Use(aPackName);
  ListOfComments()->Clear();

}

void Pack_End()
{
  add_cpp_comment_to_method();
  Package().Nullify();
  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
  ListOfComments()->Clear();

}

// The actions for the classes

void Alias_Begin()
{
  Handle(TCollection_HAsciiString) anAliasName = new TCollection_HAsciiString(thetypename);

  Alias() = new MS_Alias(anAliasName,Container(),Container(),Private);

  Alias()->MetaSchema(theMetaSchema());
  
  if (!theMetaSchema()->AddType(Alias())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Alias : " << Alias()->FullName() << " is already defined." << endm;
    YY_nb_error++;
  }

  Private = Standard_False;
}

void Alias_Type()
{
  Handle(TCollection_HAsciiString) anAliasName = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackageName = new TCollection_HAsciiString(Pack_Name);


  Alias()->Type(anAliasName,aPackageName);
  ListOfTypeUsed()->Append(Alias()->Type());
}

void Alias_End()
{
  Package()->Alias(Alias()->Name());
  Alias().Nullify();
}

// Pointer type
//
void Pointer_Begin()
{
  Handle(TCollection_HAsciiString) aPointerName = new TCollection_HAsciiString(thetypename);

  Pointer() = new MS_Pointer(aPointerName,Container(),Container(),Private);

  Pointer()->MetaSchema(theMetaSchema());
  
  if (!theMetaSchema()->AddType(Pointer())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Pointer : " << Pointer()->FullName() << " is already defined." << endm;
    YY_nb_error++;
  }

  Private = Standard_False;
}

void Pointer_Type()
{
  Handle(TCollection_HAsciiString) athetypename = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackageName = new TCollection_HAsciiString(Pack_Name);

  Pointer()->Type(athetypename,aPackageName);
  ListOfTypeUsed()->Append(Pointer()->Type());
}

void Pointer_End()
{
  Package()->Pointer(Pointer()->Name());
  Pointer().Nullify();
}


void Imported_Begin()
{
  Handle(TCollection_HAsciiString) anImportedName = new TCollection_HAsciiString(thetypename);

  Imported() = new MS_Imported(anImportedName,Container(),Container(),Private);

  Imported()->MetaSchema(theMetaSchema());

  if (!theMetaSchema()->AddType(Imported())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Imported : " << Imported()->FullName() << " is already defined." << endm;
    YY_nb_error++;
  }
  //for (i =1; i <= ListOfComments()->Length(); i++) {
  //     Imported()->SetComment(ListOfComments()->Value(i));
  //}

  Private = Standard_False;
}

void Imported_End()
{
  Package()->Imported(Imported()->Name());
  Imported().Nullify();
}


void Prim_Begin()
{  
  Handle(TCollection_HAsciiString) aPrimName = new TCollection_HAsciiString(thetypename);

  Primitive() = new MS_PrimType(aPrimName,Container(),Container(),Private);

  Primitive()->MetaSchema(theMetaSchema());

  if (!theMetaSchema()->AddType(Primitive())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Primitive : " << Primitive()->FullName() << " is already defined." << endm;
    YY_nb_error++;
  }

  Private = Standard_False;
}

void Prim_End()
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) iName;

  for (i = 1; i <= ListOfTypes()->Length(); i++) {
    iName = MS::BuildFullName(ListOfPackages()->Value(i),ListOfTypes()->Value(i));

    if (i > 1) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Type " << Primitive()->FullName() << " uses multiple inheritance." << endm;
      YY_nb_error++;
    }
    else if (!iName->IsSameString(Primitive()->FullName())) {
      Primitive()->Inherit(ListOfTypes()->Value(i),ListOfPackages()->Value(i));
    }
    else {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Primitive : " << Primitive()->FullName() << " can not inherits from itself." << endm;
    YY_nb_error++;
    }
  }

  ListOfTypes()->Clear();
  ListOfPackages()->Clear();

  Package()->Primitive(Primitive()->Name());
  Primitive().Nullify();
}


void Except_Begin()
{
  Handle(TCollection_HAsciiString) anExceptName = new TCollection_HAsciiString(thetypename);
  
  Exception() = new MS_Error(anExceptName,Container());

  Exception()->MetaSchema(theMetaSchema());

  if (!theMetaSchema()->AddType(Exception())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Exception : " << Exception()->FullName() << " is already defined." << endm;
    YY_nb_error++;
  }
}

void Except_End()
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) iName;

  for (i = 1; i <= ListOfTypes()->Length(); i++) {
    iName = MS::BuildFullName(ListOfPackages()->Value(i),ListOfTypes()->Value(i));
    
    if (i > 1) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Exception " << Exception()->FullName() << " uses multiple inheritance." << endm;
      YY_nb_error++;
    }
    else if (!iName->IsSameString(Exception()->FullName())) {
      Exception()->Inherit(ListOfTypes()->Value(i),ListOfPackages()->Value(i));
    }
    else {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Exception : " << Exception()->FullName() << " can not inherits from itself." << endm;
      YY_nb_error++;
    }
  }

  ListOfTypes()->Clear();
  ListOfPackages()->Clear();

  Package()->Except(Exception()->Name());
  Exception().Nullify();
}

void Inc_Class_Dec()
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) aClassName = new TCollection_HAsciiString(thetypename);

  StdClass() = new MS_StdClass(aClassName,Container());

  StdClass()->MetaSchema(theMetaSchema());
  for (i =1; i <= ListOfComments()->Length(); i++) {
     //StdClass()->SetComment(ListOfComments()->Value(i));
  }

  if (!theMetaSchema()->AddType(StdClass())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class : " << StdClass()->FullName() << " is already defined." << endm;
    YY_nb_error++;
  }

  StdClass()->Private(Private);
  StdClass()->Deferred(Deferred);
  StdClass()->Incomplete(Standard_True);
  Package()->Class(StdClass()->Name());
  StdClass()->Package(Package()->FullName());
  
  ListOfTypeUsed()->Append(StdClass()->FullName());

  StdClass().Nullify();

  Private   = Standard_False;
  Protected = Standard_False;
  Static    = Standard_True;
  Deferred  = Standard_False;
  Redefined = Standard_False;
  Like      = Standard_False;
  ListOfComments()->Clear();
}

void Inc_GenClass_Dec()
{
  Standard_Integer    i;
  Handle(MS_GenClass) theClass;

  for (i = 1; i <= ListOfTypes()->Length(); i++) {
    GenClass() = new MS_GenClass(ListOfTypes()->Value(i),ListOfPackages()->Value(i));
    GenClass()->MetaSchema(theMetaSchema());

    if (i == 1) {
      theClass = GenClass();
    }
    else {
      theClass->AddNested(GenClass()->Name());
      GenClass()->Mother(theClass->FullName());
      GenClass()->NestingClass(theClass->FullName());
    }

    if (!theMetaSchema()->AddType(GenClass())) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Generic class : " << GenClass()->FullName() << " is already defined." << endm;
      YY_nb_error++;
    }
    
    GenClass()->Private(Private);
    GenClass()->Deferred(Deferred);
    GenClass()->Incomplete(Standard_True);

    Package()->Class(GenClass()->Name());
    GenClass()->Package(Package()->FullName());
  }

  ListOfGen()->Append(theClass->FullName());
  Private   = Standard_False;
  Protected = Standard_False;
  Static    = Standard_True;
  Deferred  = Standard_False;
  Redefined = Standard_False;
  Like      = Standard_False;

  ListOfTypes()->Clear();
  ListOfPackages()->Clear();

  GenClass().Nullify();
}

// Generic type processing
//
void GenClass_Begin()
{
  Handle(MS_Package) aPackage;
  Handle(TCollection_HAsciiString) aClassName = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackName  = new TCollection_HAsciiString(Pack_Name);
  Handle(MS_Type)                  aType;

  if (Current_Entity == CDL_GENCLASS) {
    aPackName = GenClass()->Package()->Name();
  }
  else {
    Container() = aPackName;
  }

  if (!theMetaSchema()->IsPackage(Container())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Unknown package " << Container() << endm;
    YY_nb_error++;
    CDL_InitVariable();
    MS_TraductionError::Raise("Unknown package.");
  }
  
  GenClass() = new MS_GenClass(aClassName,aPackName);
  
  if (!theMetaSchema()->IsDefined(GenClass()->FullName())) {    
    GenClass()->MetaSchema(theMetaSchema());
    
    GenClass()->Private(Private);
    GenClass()->Deferred(Deferred);
    GenClass()->Incomplete(Standard_False);    

    theMetaSchema()->AddType(GenClass());
  }
  else {
    GenClass() = Handle(MS_GenClass)::DownCast(theMetaSchema()->GetType(GenClass()->FullName()));

    if (GenClass().IsNull()) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class : " << GenClass()->FullName() << " already declared but not as a generic class." << endm;
      CDL_InitVariable();
      MS_TraductionError::Raise("Class already defined but as generic.");
    }

    if (Private != GenClass()->Private()) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class " << GenClass()->FullName() << " has not the same visibility keyword in package declaration and in class definition." << endm;
      YY_nb_error++;
    }

    if (Deferred != GenClass()->Deferred()) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class " << GenClass()->FullName() << " is ";

      if (Deferred) {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class " << GenClass()->FullName() << " is declared 'deferred' in class definition but not in package declaration." << endm;
      }
      else {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class " << GenClass()->FullName() << " is declared 'deferred' in package declaration but not in class definition." << endm;
      }
      YY_nb_error++;
    }
    GenClass()->GetNestedName()->Clear();
  }

  GenClass()->Package(aPackName);

  Current_Entity = CDL_GENCLASS;
  
  SimpleClass() = GenClass();

  Private   = Standard_False;
  Protected = Standard_False;
  Static    = Standard_True;
  Deferred  = Standard_False;
  Redefined = Standard_False;
  Like      = Standard_False;
  ListOfComments()->Clear();
}

void Add_GenType()
{
  if (Any) {
    GenClass()->GenType(ListOfItem()->Value(ListOfItem()->Length()));
    Any = Standard_False;
  }
  else {
    Handle(TCollection_HAsciiString) aTName = new TCollection_HAsciiString(thetypename);
    Handle(TCollection_HAsciiString) aPName = new TCollection_HAsciiString(Pack_Name);

    GenClass()->GenType(ListOfItem()->Value(ListOfItem()->Length()),MS::BuildFullName(aPName,aTName));
  }
}

void Add_DynaGenType()
{
  GenClass()->GenType(DynType());

  DynType().Nullify();
  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
}

void Add_Embeded()
{
  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
}

void GenClass_End()
{
  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
  Clear_ListOfItem();

  GenClass()->Incomplete(Standard_False);

  if (!StdClass().IsNull()) {
    StdClass()->Incomplete(Standard_False);
  }

  GenClass().Nullify();
  StdClass().Nullify();

  Current_Entity = CDL_NULL;
}


void InstClass_Begin()
{
  Handle(TCollection_HAsciiString) aPackName  = Container();
  Handle(TCollection_HAsciiString) aClassName = new TCollection_HAsciiString(thetypename);
  //Standard_Integer i;

  if (Current_Entity == CDL_GENCLASS) {
    aPackName  = GenClass()->Package()->Name();
  }

  if (Current_Entity != CDL_PACKAGE) {
    if (!theMetaSchema()->IsPackage(aPackName)) {
       ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Package : " << Pack_Name << " is not defined." << endm;
       CDL_InitVariable();
       MS_TraductionError::Raise("Package not defined.");
    }
  }


  InstClass() = new MS_InstClass(aClassName,aPackName);

  if (theMetaSchema()->IsDefined(InstClass()->FullName()) && Current_Entity == CDL_PACKAGE) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Instantiation class " << InstClass()->Name() << " is already declared in package " << aPackName << endm;
    YY_nb_error++;
  }
  
  InstClass()->MetaSchema(theMetaSchema());  

  if (!theMetaSchema()->IsDefined(InstClass()->FullName()) || Current_Entity == CDL_GENCLASS) {
    if (Current_Entity == CDL_GENCLASS && theMetaSchema()->IsDefined(InstClass()->FullName())) {
      theMetaSchema()->RemoveType(InstClass()->FullName(),Standard_False);
      GenClass()->NestedInsClass(InstClass()->Name());
      InstClass()->Mother(GenClass()->FullName());
    }
    else if (Current_Entity == CDL_GENCLASS) {
      Handle(MS_Package) aPackage = theMetaSchema()->GetPackage(aPackName);

      if (!aPackage->HasClass(aClassName)) {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Nested instantiation class : " << InstClass()->Name() << " is not declared in package " << aPackName << endm;
	YY_nb_error++;
	CDL_InitVariable();
	MS_TraductionError::Raise("Instantiation not defined.");
      }
    }
    
    if (Current_Entity == CDL_GENCLASS) {
      InstClass()->NestingClass(GenClass()->FullName());
      GenClass()->AddNested(InstClass()->Name());
    }

    InstClass()->MetaSchema(theMetaSchema());
    InstClass()->Package(aPackName);
    InstClass()->Private(Private);

    theMetaSchema()->AddType(InstClass());
    Private   = Standard_False;
  }
  else {
    Handle(MS_Type) aType = theMetaSchema()->GetType(InstClass()->FullName());
    
    InstClass() = Handle(MS_InstClass)::DownCast(aType);

    if (InstClass().IsNull()) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "The instantiation " << aClassName << " was not declared..." << endm;
      YY_nb_error++;
      CDL_InitVariable();
      MS_TraductionError::Raise("Instantiation not defined.");
    }
  }
  
  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
  ListOfComments()->Clear();
}

void Add_Gen_Class()
{
  Handle(TCollection_HAsciiString) aPackName  = new TCollection_HAsciiString(Pack_Name);
  Handle(TCollection_HAsciiString) aClassName = new TCollection_HAsciiString(thetypename);

  InstClass()->GenClass(aClassName,aPackName);
}

void Add_InstType()
{
  Standard_Integer                 i;
  Handle(MS_Type)                  aType;
  Handle(TCollection_HAsciiString) aFullName;
  Standard_Boolean                 ComeFromDynaType = Standard_False; 

  if (Current_Entity == CDL_GENTYPE) {
    ComeFromDynaType = Standard_True;
    restore_state();
  }

  for (i = 1; i <= ListOfTypes()->Length(); i++) {
    aFullName = MS::BuildFullName(ListOfPackages()->Value(i),ListOfTypes()->Value(i));

    if (theMetaSchema()->IsDefined(aFullName)) {
      aType = theMetaSchema()->GetType(aFullName);
    }
    else {
      char *athetypename = TypeCompletion((char *)(ListOfTypes()->Value(i)->ToCString()));

      if (athetypename == aDummyPackageName) {
	ListOfPackages()->Value(i)->Clear();
      }
    }

    if (!ComeFromDynaType) {
      InstClass()->InstType(ListOfTypes()->Value(i),ListOfPackages()->Value(i));
    }
    else {
      if (ListOfPackages()->Value(i)->IsEmpty()) {
	DynType()->InstType(ListOfTypes()->Value(i));
      }
      else {
	DynType()->InstType(MS::BuildFullName(ListOfPackages()->Value(i),ListOfTypes()->Value(i)));
      }
    }
  }

  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
}

void InstClass_End()
{
  if (Current_Entity == CDL_GENCLASS) {
    InstClass()->Instantiates();
  }
  else  if (Current_Entity == CDL_PACKAGE) {
    Package()->Class(InstClass()->Name());
  }

  if (Current_Entity != CDL_GENCLASS) {
    ListOfInst()->Append(InstClass()->FullName());
  }


  InstClass()->Incomplete(Standard_False);
  ListOfGen()->Append(InstClass()->GenClass());
  InstClass().Nullify();
  ListOfTypes()->Clear();
  ListOfPackages()->Clear();


  Private   = Standard_False;
  Protected = Standard_False;
  Static    = Standard_True;
  Deferred  = Standard_False;
  Redefined = Standard_False;
  Like      = Standard_False;
}

void DynaType_Begin()
{
  Handle(MS_Package)                       aPackage;
  Handle(TColStd_HSequenceOfHAsciiString)  aSeqOfPackage;

  SaveState      = Current_Entity;
  Current_Entity = CDL_GENTYPE;
  
  if (! ListOfItem()->IsEmpty()) {
    Standard_Integer i;
    Handle(TCollection_HAsciiString) aPackName;
    Handle(TCollection_HAsciiString) aGenName  = new TCollection_HAsciiString(thetypename);

    if (strcmp(aDummyPackageName,Pack_Name)) {
      aPackName = new TCollection_HAsciiString(Pack_Name);
    }
    else {
      aPackage = theMetaSchema()->GetPackage(Container());
      aSeqOfPackage = aPackage->Uses();
      
      for (i = 1; i <= aSeqOfPackage->Length(); i++) {
	if (theMetaSchema()->IsDefined(MS::BuildFullName(aSeqOfPackage->Value(i),aGenName))) {
	  aPackName = aSeqOfPackage->Value(i);
	}
      }

      if (aPackName.IsNull()) {
	aPackName = new TCollection_HAsciiString;
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "constraint type " << thetypename << " comes from a package not declared in 'uses' clause of the package " << Container() << endm;
	YY_nb_error++;
      }
    }

    DynType() = new MS_GenType(GenClass(),ListOfItem()->Value(ListOfItem()->Length()),MS::BuildFullName(aPackName,aGenName));
  }

  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
}


void StdClass_Begin()
{
  Handle(TCollection_HAsciiString) aClassName = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackName  = new TCollection_HAsciiString(Pack_Name);
  Standard_Integer i;

  if (Current_Entity == CDL_GENCLASS) {
    aPackName  = GenClass()->Package()->Name();
  }

  Container()  = aPackName;

  if (!theMetaSchema()->IsPackage(Container())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Unknown package " << Container() << endm;
    YY_nb_error++;
    CDL_InitVariable();
    MS_TraductionError::Raise("Unknown package.");
  }
  // si la classe n a pas ete cree par une dec incomplete
  //
  StdClass() = new MS_StdClass(aClassName,aPackName);
  StdClass()->MetaSchema(theMetaSchema());

  if (!theMetaSchema()->IsDefined(StdClass()->FullName()) || Current_Entity == CDL_GENCLASS) {    
    if (Current_Entity == CDL_GENCLASS && theMetaSchema()->IsDefined(StdClass()->FullName())) {
      theMetaSchema()->RemoveType(StdClass()->FullName(),Standard_False);
      GenClass()->NestedStdClass(StdClass()->Name());
      StdClass()->Mother(GenClass()->FullName());
    }
    else if (Current_Entity == CDL_GENCLASS) {
      Handle(MS_Package) aPackage = theMetaSchema()->GetPackage(aPackName);

      if (!aPackage->HasClass(aClassName)) {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class : " << StdClass()->Name() << " is not declared in package " << aPackName << endm;
	YY_nb_error++;
	CDL_InitVariable();
	MS_TraductionError::Raise("Class not defined.");
      }
      GenClass()->NestedStdClass(StdClass()->Name());
      StdClass()->Mother(GenClass()->FullName());      
    }

    if (Current_Entity == CDL_GENCLASS) {
      StdClass()->SetGenericState(Standard_True);
      StdClass()->NestingClass(GenClass()->FullName());
      GenClass()->AddNested(StdClass()->Name());
    }
    
    StdClass()->MetaSchema(theMetaSchema());
    
    StdClass()->Private(Private);
    StdClass()->Deferred(Deferred);
    StdClass()->Incomplete(Standard_False);

    theMetaSchema()->AddType(StdClass());
    
    StdClass()->Package(aPackName);
  }
  else {
    Handle(MS_Type) aType = theMetaSchema()->GetType(StdClass()->FullName());
    
    StdClass() = Handle(MS_StdClass)::DownCast(aType);
    
    if (StdClass().IsNull()) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "The class " << aClassName << " was not declared..." << endm;
      CDL_InitVariable();
      MS_TraductionError::Raise("Class not defined.");
    }

    if (Private != StdClass()->Private()) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class " << StdClass()->FullName() << " has not the same visibility keyword in package declaration and in class definition." << endm;
      YY_nb_error++;
    }
    
    if (Deferred != StdClass()->Deferred()) {
      if (Deferred) {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class " << StdClass()->FullName() << " is declared 'deferred' in class definition but not in package declaration." << endm;
      }
      else {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class " << StdClass()->FullName() << " is declared 'deferred' in package declaration but not in class definition." << endm;
      }
      YY_nb_error++;
    }
  }
  
  if (Current_Entity != CDL_GENCLASS) {
    Current_Entity = CDL_STDCLASS;
  }
  for (i =1; i <= ListOfComments()->Length(); i++) {
     StdClass()->SetComment(ListOfComments()->Value(i));
  }

  SimpleClass() = StdClass();

  Private   = Standard_False;
  Protected = Standard_False;
  Static    = Standard_True;
  Deferred  = Standard_False;
  Redefined = Standard_False;
  Like      = Standard_False;

  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
  ListOfComments()->Clear();

}

void Add_Std_Ancestors()
{  
  Standard_Integer                 i;
  Handle(TCollection_HAsciiString) aFullName;

  for (i = 1; i <= ListOfTypes()->Length(); i++) {
    aFullName = MS::BuildFullName(ListOfPackages()->Value(i),ListOfTypes()->Value(i));

    if (theMetaSchema()->IsDefined(aFullName)) {
      Handle(MS_Class) aClass = Handle(MS_Class)::DownCast(theMetaSchema()->GetType(aFullName));
      if (aClass.IsNull()) {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class : " << aFullName << " must not be a normal class." << endm;
	YY_nb_error++;
      } 
      
      if (i > 1) {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class " << SimpleClass()->FullName() << " uses multiple inheritance." << endm;
	YY_nb_error++;
      }
      else if (!SimpleClass()->FullName()->IsSameString(aClass->FullName())) {
	SimpleClass()->Inherit(aClass);
      }
      else {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class " << SimpleClass()->FullName() << " can not inherits from itself." << endm;
	YY_nb_error++;
      }

      SimpleClass()->Use(ListOfTypes()->Value(i),ListOfPackages()->Value(i));
      
      ListOfTypeUsed()->Append(aFullName);
    }
    else {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Class : " << aFullName << " not defined, can't be in inherits clause." << endm;
      YY_nb_error++;
    }
  }
  //SimpleClass()->MetaSchema(theMetaSchema());
  for (i =1; i <= ListOfComments()->Length(); i++) {
     SimpleClass()->SetComment(ListOfComments()->Value(i));
  }

  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
  ListOfComments()->Clear();
}

void Add_Std_Uses()
{
  Standard_Integer  i;


  //SimpleClass()->MetaSchema(theMetaSchema());
  for (i =1; i <= ListOfComments()->Length(); i++) {
     SimpleClass()->SetComment(ListOfComments()->Value(i));
  }

  for (i = 1; i <= ListOfTypes()->Length(); i++) {
    Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(ListOfPackages()->Value(i),ListOfTypes()->Value(i));

    if (Current_Entity != CDL_GENCLASS && !theMetaSchema()->IsDefined(aFullName)) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "The 'uses' statement of your class has a type : " << aFullName << " from a package not declared in the 'uses' statement of the package " << Container() << endm;
      YY_nb_error++;
    }
    else if (Current_Entity != CDL_GENCLASS) {
      if (!SimpleClass()->Package()->IsUsed(ListOfPackages()->Value(i))) {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "The 'uses' statement of your class has a type : " << aFullName << " from a package not declared in the 'uses' statement of the package " << Container() << endm;
	YY_nb_error++;
      }
    }
    
    SimpleClass()->Use(ListOfTypes()->Value(i),ListOfPackages()->Value(i));

    ListOfTypeUsed()->Append(aFullName);
  }

  ListOfComments()->Clear();
  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
}

void StdClass_End()
{
  if (Current_Entity == CDL_GENCLASS) {
    SimpleClass() = GenClass();
  }

  StdClass()->Incomplete(Standard_False);

  StdClass().Nullify();
  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
  ListOfComments()->Clear();
}


void Add_Raises()
{
  Standard_Integer i;

  for (i = 1; i <= ListOfTypes()->Length(); i++) {
    Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(ListOfPackages()->Value(i),ListOfTypes()->Value(i));
    if (theMetaSchema()->IsDefined(aFullName)) {
      SimpleClass()->Raises(ListOfTypes()->Value(i),ListOfPackages()->Value(i));
    }
    else {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "the exception "  << "'" << aFullName << "'" << " is not defined."  << endm;
      YY_nb_error++;
    }
  }

  ListOfTypes()->Clear();
  ListOfPackages()->Clear();
}

void Add_Field()
{
  Standard_Integer i,j;
  Handle(TCollection_HAsciiString) aClassName = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackName  = new TCollection_HAsciiString(Pack_Name);

  for (i = 1; i <= ListOfName()->Length(); i++) {
    Field() = new MS_Field(SimpleClass(),ListOfName()->Value(i));

    Field()->MetaSchema(theMetaSchema());

    for (j = 1; j <= ListOfInteger()->Length(); j++) {
      Field()->Dimension(ListOfInteger()->Value(j)->IntegerValue());
    }

    if (strcmp(Pack_Name,aDummyPackageName) == 0) {
      aPackName->Clear();
    }
    else {
      VerifyClassUses(MS::BuildFullName(aPackName,aClassName));
    }

    Field()->TYpe(aClassName,aPackName);


    Field()->Protected(Protected);
    
    SimpleClass()->Field(Field());
  }

  Private = Standard_False;
  Protected = Standard_False;
  ListOfInteger()->Clear();
  ListOfName()->Clear();
}

void Add_RedefField()
{
  ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Fields redefinition no more supported..." << endm;
  YY_nb_error++;
}

void Add_FriendMet()
{
  Method()->Params(MethodParams());
  MethodParams().Nullify();
  Method()->CreateFullName();
  SimpleClass()->FriendMet(Method()->FullName());
}

void Add_FriendExtMet(char *aPackName)
{
  Handle(TCollection_HAsciiString) apack = new TCollection_HAsciiString(aPackName);

  ExternMet()->Package(apack);
  Method()->Params(MethodParams());
  MethodParams().Nullify();
  Method()->CreateFullName();
  SimpleClass()->FriendMet(Method()->FullName());
}

//=======================================================================
//function : Add_Friend_Class
//purpose  : 
//=======================================================================
void Add_Friend_Class()
{
  Handle(TCollection_HAsciiString) aClassName = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackName  = new TCollection_HAsciiString(Pack_Name);
  Handle(TCollection_HAsciiString) theTypeName = MS::BuildFullName(aPackName,aClassName);

  if (theMetaSchema()->IsDefined(theTypeName)) {
    SimpleClass()->Friend(aClassName,aPackName);
    ListOfTypeUsed()->Append(theTypeName);
  }
  else {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "friend class " << theTypeName->ToCString() << " is not defined." << endm;
    YY_nb_error++;
  }
}

WOKTools_MapOfHAsciiString anEnumMap;

// The actions for the Enumeration
//
//=======================================================================
//function : Enum_Begin
//purpose  : 
//=======================================================================
void Enum_Begin()
{
  Handle(TCollection_HAsciiString) anEnumName = new TCollection_HAsciiString(thetypename);
  Standard_Integer i;

  anEnumMap.Clear();

  Enum() = new MS_Enum(anEnumName,Container(),Container(),Private);

  Enum()->MetaSchema(theMetaSchema());
  Enum()->Package(Package()->FullName());
  for(i = 1; i <= ListOfComments()->Length(); i++) {
     Enum()->SetComment(ListOfComments()->Value(i));
  }  
  ListOfComments()->Clear();
  if (!theMetaSchema()->AddType(Enum())) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Enumeration : " << Enum()->FullName() << " is already defined." << endm;
    YY_nb_error++;
  }
}


void Add_Enum(char *aValue)
{
  Handle(TCollection_HAsciiString) anEnumValue = new TCollection_HAsciiString(aValue);
  for(Standard_Integer i = 1; i <= ListOfComments()->Length(); i++) {
     Enum()->SetComment(ListOfComments()->Value(i));
  }  
  ListOfComments()->Clear();

  if (!anEnumMap.Contains(anEnumValue)) {
    anEnumMap.Add(anEnumValue);
    Enum()->Enum(anEnumValue);
  }
  else {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Enumeration value " << aValue << " in " << Enum()->FullName() << " is already defined." << endm;
    YY_nb_error++;
  }
}

void Enum_End()
{
  //Enum->Check();
  Package()->Enum(Enum()->Name());
  for(Standard_Integer i = 1; i <= ListOfComments()->Length(); i++) {
     Enum()->SetComment(ListOfComments()->Value(i));
  }  
  ListOfComments()->Clear();
  Enum().Nullify();
  anEnumMap.Clear();

  Private   = Standard_False;
}

// The actions for the Methods
//
void get_cpp_commentalias(const Handle(TCollection_HAsciiString)& aComment)
{
  Handle(TCollection_HAsciiString) aToken1,
                                   aToken2;

  aToken1 = aComment->Token();

  aToken1->LeftAdjust();
  aToken1->RightAdjust();

  aComment->Remove(1,aToken1->Length());
  aComment->LeftAdjust();
  aComment->RightAdjust();
}

//=======================================================================
//function : add_cpp_comment_to_method
//purpose  : 
//=======================================================================
void add_cpp_comment_to_method()
{
  if (Method().IsNull()) {
    if (ListOfCplusplus()->Length() > 0) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() \
	<< "\"" <<  ", line " << CDLlineno << ": " \
	  << "C++ directive outside method definition." << endm;
      YY_nb_error++;
    }
  }
  else {
    int                               aCommentType;
    Standard_Integer                  i, aNbCPP;
    Handle(TCollection_HAsciiString)  aCP;
    
    for(i = 1; i <= ListOfComments()->Length(); ++i) {
        Method()->SetComment(ListOfComments()->Value(i));
    }
    //
    aNbCPP=ListOfCplusplus()->Length();
    for(i = 1; i <= aNbCPP; ++i) {
      aCommentType = ListOfCPPType()->Value(i);
      //
      switch (aCommentType) {
        case CDL_HARDALIAS: 
	  get_cpp_commentalias(ListOfCplusplus()->Value(i));
	  Method()->Alias(ListOfCplusplus()->Value(i));
	  Method()->SetAliasType(Standard_False);
	  break;
	case CDL_OPERATOR:
	  get_cpp_commentalias(ListOfCplusplus()->Value(i));
	  Method()->Alias(ListOfCplusplus()->Value(i));
	  Method()->SetAliasType(Standard_True);
	  break;
	case CDL_INLINE:
	  Method()->Inline(Standard_True);
	  break;
	case CDL_DESTRUCTOR:
	  if (Method()->IsFunctionCall()) {
	    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() \
	      << "\"" <<  ", line " << methodlineno << ": "\
		<< "C++ directive 'alias ~' cannot be used with 'function call'." \
		  << endm;
	    YY_nb_error++;
	  }
	  Method()->Destructor(Standard_True);
	  break;
	  
	case CDL_CONSTREF:
	  if (!Method()->Returns().IsNull()) {
	    Method()->ConstReturn(Standard_True);
	    Method()->RefReturn(Standard_True);
	  }
	  else {
	    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() \
	      << "\"" <<  ", line " << methodlineno << ": " \
	      << "C++ directive 'return const &' cannot be used without 'returns' clause."\
	      << endm;
	    YY_nb_error++;
	  }
	  break;
	case CDL_REF:
	  if (!Method()->Returns().IsNull()) {
	    Method()->RefReturn(Standard_True);
	  }
	  else {
	    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString()\
	      << "\"" <<  ", line " << methodlineno << ": "\
	      << "C++ directive 'return &' cannot be used without 'returns' clause."\
              << endm;
	    YY_nb_error++;
	  }
	  break;
	  //===f
	case CDL_CONSTPTR:
	  if (!Method()->Returns().IsNull()) {
	    Method()->ConstReturn(Standard_True);
	    Method()->PtrReturn(Standard_True);
	  }
	  else {
	    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() \
	      << "\"" <<  ", line " << methodlineno << ": " \
	      << "C++ directive 'return const &' cannot be used without 'returns' clause."\
	      << endm;
	    YY_nb_error++;
	  }
	  break;
	case CDL_PTR:
	  if (!Method()->Returns().IsNull()) {
	    Method()->PtrReturn(Standard_True);
	  }
	  else {
	    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString()\
	      << "\"" <<  ", line " << methodlineno << ": "\
	      << "C++ directive 'return &' cannot be used without 'returns' clause." << endm;
	    YY_nb_error++;
	  }
	  break;
	  //===t
	case CDL_CONSTRET:
	  if (!Method()->Returns().IsNull()) {
	    Method()->ConstReturn(Standard_True);
	  }
	  else {
	    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() \
	      << "\"" <<  ", line " << methodlineno << ": "\
		<< "C++ directive 'return const' cannot be used without 'returns' clause." << endm;
	    YY_nb_error++;
	  }
	  break;
	case CDL_FUNCTIONCALL:
	  if (Method()->IsDestructor()) {
	    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString()\
	      << "\"" <<  ", line " << methodlineno << ": "\
		<< "C++ directive 'function call' cannot be used with 'alias ~'." << endm;
	    YY_nb_error++;
	  }
	  Method()->FunctionCall(Standard_True);
	  break;
	default:
	  break;
       }//switch (aCommentType) {
    }//for(i = 1; i <= aNbCPP; ++i) {
    //
    ListOfComments()->Clear();
    ListOfCplusplus()->Clear();
    ListOfCPPType()->Clear();
    theMetaSchema()->AddMethod(Method());
  }
}

//=======================================================================
//function : Construct_Begin
//purpose  : 
//=======================================================================
void Construct_Begin()
{
  if (SimpleClass()->Deferred()) {
    if (!MethodName()->IsSameString(DefCons())) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "A deferred class must not have a constructor with name 'Create' or no 'me' or 'myclass' present for the method." << endm;
      YY_nb_error++;
    }
  }
  else {
    if (!MethodName()->IsSameString(NorCons())) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "A class must have a constructor with name 'Create' or no 'me' or 'myclass' present for the method." << endm;
      YY_nb_error++;
    }
  }

  if (!Method().IsNull()) {
    add_cpp_comment_to_method();
  }
  methodlineno = CDLlineno;
  Construc()  = new MS_Construc(MethodName(),SimpleClass()->FullName());
  Method()    = Construc();
  MemberMet() = Construc();
  Method()->MetaSchema(theMetaSchema());
}

void Friend_Construct_Begin()
{
  Construc()  = new MS_Construc(MethodName(),ListOfTypeUsed()->Value(ListOfTypeUsed()->Length()));
  Method()    = Construc();
  MemberMet() = Construc();
  Method()->MetaSchema(theMetaSchema());
}

void InstMet_Begin()
{
  if (!Method().IsNull()) {
    add_cpp_comment_to_method();
  }
  methodlineno = CDLlineno;
  InstMet()   = new MS_InstMet(MethodName(),SimpleClass()->FullName());
  Method()    = InstMet();  
  Method()->MetaSchema(theMetaSchema());
  MemberMet() = InstMet();
}

void Friend_InstMet_Begin()
{
  InstMet()   = new MS_InstMet(MethodName(),ListOfTypeUsed()->Value(ListOfTypeUsed()->Length()));
  Method()    = InstMet();
  MemberMet() = InstMet();
  Method()->MetaSchema(theMetaSchema());
}

void ClassMet_Begin()
{
  if (!Method().IsNull()) {
    add_cpp_comment_to_method();
  }
  methodlineno = CDLlineno;
  ClassMet()  = new MS_ClassMet(MethodName(),SimpleClass()->FullName());
  Method()    = ClassMet();
  Method()->MetaSchema(theMetaSchema());
  MemberMet() = ClassMet();
}

void Friend_ClassMet_Begin()
{
  ClassMet()  = new MS_ClassMet(MethodName(),ListOfTypeUsed()->Value(ListOfTypeUsed()->Length()));
  Method()    = ClassMet();
  MemberMet() = ClassMet();
  Method()->MetaSchema(theMetaSchema());
}

void ExtMet_Begin()
{
  if (!Method().IsNull()) {
    add_cpp_comment_to_method();
  }
  methodlineno = CDLlineno;
  ExternMet() = new MS_ExternMet(MethodName(),Package()->Name());
  Method()    = ExternMet();
  Method()->MetaSchema(theMetaSchema());
}

void Friend_ExtMet_Begin()
{
  ExternMet() = new MS_ExternMet(MethodName());
  Method()    = ExternMet();
  Method()->MetaSchema(theMetaSchema());
}

void Add_Me()
{
  if (Mutable == MS_MUTABLE) {
    InstMet()->ConstMode(MSINSTMET_MUTABLE);
  }
  else if (InOrOut == MS_INOUT || InOrOut == MS_OUT) {
    InstMet()->ConstMode(MSINSTMET_OUT);
  }
  else {
    InstMet()->Const(Standard_True);
  }

  Mutable = 0;
  InOrOut = MS_IN;
}

void Add_MetRaises()
{
  Standard_Integer                 i,j;
  Handle(TCollection_HAsciiString) aName;

  for (i = 1; i <= ListOfTypes()->Length(); i++) {
    aName = MS::BuildFullName(ListOfPackages()->Value(i),ListOfTypes()->Value(i));
    if (theMetaSchema()->IsDefined(aName)) {
      if (Current_Entity == CDL_STDCLASS || Current_Entity == CDL_GENCLASS) {
	Handle(TColStd_HSequenceOfHAsciiString) seq = SimpleClass()->GetRaises();
	Standard_Boolean isFound = Standard_False;
	
	for (j = 1; j <= seq->Length() && !isFound; j++) {
	  if (seq->Value(j)->IsSameString(aName)) {
	    isFound = Standard_True;
	  }
	}

	if (!isFound) {
	  ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "the exception "  << "'" << aName << "'" << " is not declared in 'raises' clause of the class: " << SimpleClass()->FullName() << endm;
	  YY_nb_error++;
	}
	else {
	  Method()->Raises(aName);
	}
      }
      else {
	Method()->Raises(aName);
      }
    }
    else {
      // si on est dans les methodes de package on ne verifie pas les raises
      //
      if (ExternMet().IsNull()) {
	ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "the exception "  << "'" << aName << "'" << " is not defined."  << endm;
	YY_nb_error++;
      }
      else {
	Method()->Raises(aName);
      }
    }
  }

  ListOfPackages()->Clear();
  ListOfTypes()->Clear();
}

void Add_Returns()
{
  Handle(TCollection_HAsciiString) athetypename = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackName = new TCollection_HAsciiString(Pack_Name);
  Handle(MS_Param)                 aParam;
  Standard_Boolean                 isGenType = Standard_False;

  aParam = new MS_Param(Method(),athetypename);
  
  aParam->Like(Like);
  aParam->AccessMode(Mutable);
  aParam->AccessMode(InOrOut);
  aParam->MetaSchema(theMetaSchema());

  if (strcmp(Pack_Name,aDummyPackageName) == 0) {
    aPackName->Clear();
    isGenType = Standard_True;
  }
  else {
    VerifyClassUses(MS::BuildFullName(aPackName,athetypename));
  }


  aParam->Type(athetypename,aPackName);

  if (!Construc().IsNull()) {
    if (!aParam->TypeName()->IsSameString(Construc()->Class())) {
      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "The constructor must return " << Construc()->Class() << " not " << aParam->TypeName() << endm;
      YY_nb_error++;
    }
  }

  Method()->Returns(aParam);
 
  Mutable = 0;
  InOrOut = MS_IN;
  Like      = Standard_False;

  ListOfName()->Clear();  
}

void MemberMet_End() 
{
  SimpleClass()->Method(MemberMet());
  Method()->Params(MethodParams());
  MethodParams().Nullify();
  MemberMet()->CreateFullName();
  MemberMet()->Private(Private);
  MemberMet()->Protected(Protected);

  if (!InstMet().IsNull()) {
    InstMet()->Deferred(Deferred);
    InstMet()->Redefined(Redefined);
    InstMet()->Static(Static);
  }

  MemberMet().Nullify();
  InstMet().Nullify();
  Construc().Nullify();
  ClassMet().Nullify();

  Private   = Standard_False;
  Protected = Standard_False;
  Static    = Standard_True;
  Deferred  = Standard_False;
  Redefined = Standard_False;
  Like      = Standard_False;
}

void ExternMet_End() 
{
  Package()->Method(ExternMet());
  Method()->Params(MethodParams());
  MethodParams().Nullify();
  ExternMet()->CreateFullName();
  ExternMet()->Private(Private);
  ExternMet().Nullify();
  Private   = Standard_False;
  Protected = Standard_False;
  Static    = Standard_True;
  Deferred  = Standard_False;
  Redefined = Standard_False;
  Like      = Standard_False;
}

// The actions for Parameters
//
void Param_Begin()
{
  Standard_Integer                 i;
  Handle(MS_Param)                 aParam;
  Handle(TCollection_HAsciiString) athetypename = new TCollection_HAsciiString(thetypename);
  Handle(TCollection_HAsciiString) aPackName = new TCollection_HAsciiString(Pack_Name);
  Standard_Boolean                 isGenType = Standard_False;
  
  for (i = 1; i <= ListOfName()->Length(); i++) {
    if (ParamValue().IsNull()) {
      aParam = new MS_Param(Method(),ListOfName()->Value(i));
    }
    else {
      aParam = new MS_ParamWithValue(Method(),ListOfName()->Value(i));
    }

    aParam->AccessMode(Mutable);
    aParam->AccessMode(InOrOut);
    aParam->MetaSchema(theMetaSchema());

    if (strcmp(Pack_Name,aDummyPackageName) == 0) {
      aPackName->Clear();
      isGenType = Standard_True;
    }
    else {
      VerifyClassUses(MS::BuildFullName(aPackName,athetypename));
    }

    aParam->Like(Like);
    aParam->Type(athetypename,aPackName);

    if (!ParamValue().IsNull()) {
      MS_TypeOfValue pt = MS_INTEGER;
      MS_ParamWithValue *pwv;

      switch (ParamType) {
      case INTEGER:
		    break;
      case REAL:    pt = MS_REAL;
		    break;
      case STRING:  pt = MS_STRING;
		    break;
      case LITERAL: pt = MS_CHAR;
		    break;
      case IDENTIFIER: pt = MS_ENUM;
		    break;
      default:      ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "Type of default value unknown." << endm;
	            YY_nb_error++;
		    break;
      }
      
      pwv = (MS_ParamWithValue *)aParam.operator->();
      pwv->Value(ParamValue(),pt);
    }

    //Method()->Param(aParam);
    if(MethodParams().IsNull()) MethodParams() = new MS_HSequenceOfParam;
    MethodParams()->Append(aParam);
  }

  ParamValue().Nullify();
  Mutable = 0;
  InOrOut = MS_IN;
  Like    = Standard_False;

  ListOfName()->Clear();
}

void Add_Value(char *str,int type)
{
  ParamValue() = new TCollection_HAsciiString(str);
  ParamType  = type;
}


// The general actions
//
void End()
{

}

void Set_In()
{
  InOrOut = MS_IN;
}

void Set_Out()
{
  InOrOut = MS_OUT;
}

void Set_InOut()
{
  InOrOut = MS_INOUT;
}

void Set_Mutable()
{
  Mutable = MS_MUTABLE;
}

void Set_Mutable_Any()
{
  Mutable = MS_ANY;
}

void Set_Immutable()
{
  Mutable = MS_IMMUTABLE;
}

void Set_Priv()
{
  Private = Standard_True;
}

void Set_Defe()
{
  Deferred = Standard_True;
}

void Set_Redefined()
{
  if (!Construc().IsNull()) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "A constructor cannot be redefined." << endm;
    YY_nb_error++;
  }
  if (!ClassMet().IsNull()) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "A class method cannot be redefined." << endm;
    YY_nb_error++;
  }
  Redefined = Standard_True;
  Static    = Standard_False;
}

void Set_Prot()
{
  Protected = Standard_True;
  Private   = Standard_False; 
}

void Set_Static()
{
  Static = Standard_True;
}

void Set_Virtual()
{
  if (!ClassMet().IsNull()) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "A class method cannot be redefined, so the 'virtual' keyword cannot be applied to " << ClassMet()->Name() << endm;
    YY_nb_error++;
  }
  if (!Construc().IsNull()) {
    ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": " << "A constructor cannot be redefined, so the 'virtual' keyword cannot be applied to it." << endm;
    YY_nb_error++;
  }
  Static = Standard_False;
}

void Set_Method(char *name)
{
  MethodName() = new TCollection_HAsciiString(name);
}

void Set_Like_Me()
{
  Like = Standard_True;

  strncpy(thetypename,SimpleClass()->Name()->ToCString(),MAX_CHAR);
  strncpy(Pack_Name,Container()->ToCString(),MAX_CHAR);
}

void Set_Like_Type()
{
  ErrorMsg() << "CDL" << "\"" << CDLFileName()->ToCString() << "\"" <<  ", line " << CDLlineno << ": Obsolete syntaxe : like <thetypename>" << endm;
  YY_nb_error++;
}

void Set_Item(char *Item)
{
  Handle(TCollection_HAsciiString) anItem = new TCollection_HAsciiString(Item);

  ListOfItem()->Append(anItem);
}

void Set_Any()
{
  Any = Standard_True;
}


extern "C" int CDLparse();


void CDL_Main()
{
  YY_nb_error = 0;
  CDLparse();
} 

extern "C" {
void CDLrestart(FILE*);
}

int TraductionMain(char *FileName)
{

  CDLin = fopen(FileName,"r");



  if (CDLin == NULL) {
    CDL_InitVariable();
    ErrorMsg() << "CDL" << " File not found : " << FileName << endm;
    MS_TraductionError::Raise("File not found.");
  }

  CDLrestart(CDLin);
  // Boot file
  //
  CDL_Main();

  fclose(CDLin);

  if (YY_nb_error > 0) {
    ErrorMsg() << "CDL" << YY_nb_error << " errors." << endm;
  } 
  
  if (YY_nb_warning > 0) {
    WarningMsg() << "CDL" << YY_nb_warning << " warnings." << endm;
  }

  return YY_nb_error;
}



int CDLTranslate(const Handle(MS_MetaSchema)&             aMetaSchema, 
		 const Handle(TCollection_HAsciiString)&  aFileName,
		 const Handle(TColStd_HSequenceOfHAsciiString)& aGlobalList,
		 const Handle(TColStd_HSequenceOfHAsciiString)& aTypeList,
		 const Handle(TColStd_HSequenceOfHAsciiString)& anInstList,
		 const Handle(TColStd_HSequenceOfHAsciiString)& anGenList) 
{
  volatile Standard_Integer  ErrorLevel = 0;

  CDL_InitVariable();

  theMetaSchema()    = aMetaSchema;
  ListOfGlobalUsed() = aGlobalList;
  ListOfTypeUsed()   = aTypeList;
  ListOfInst()       = anInstList;
  ListOfGen()        = anGenList;

  if (!aFileName.IsNull()) {
    CDLlineno = 1;
    CDLFileName() = aFileName;

    try {
      OCC_CATCH_SIGNALS
      ErrorLevel = TraductionMain((char *)(aFileName->ToCString()));
    }
    catch(Standard_Failure) {
      fclose(CDLin);
      ErrorLevel = 1;
    }
  }
  else {
    ErrorLevel = 1;
  }

  theMetaSchema().Nullify();
  ListOfGlobalUsed().Nullify();
  ListOfTypeUsed().Nullify();
  ListOfInst().Nullify();
  ListOfGen().Nullify();
  ListOfComments().Nullify();
  return ErrorLevel;
}


