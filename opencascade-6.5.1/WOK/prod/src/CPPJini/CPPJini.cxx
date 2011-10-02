#include <CPPJini.hxx>
#include <CPPJini_ClientInfo.hxx>
#include <CPPJini_SequenceOfClientInfo.hxx>

#include <MS.hxx>
#include <MS_Client.hxx>
#include <MS_ParamWithValue.hxx>
#include <MS_HArray1OfParam.hxx>
#include <MS_HSequenceOfClass.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <WOKTools_DataMapOfHAsciiStringOfHAsciiString.hxx>

#include <TColStd_SequenceOfInteger.hxx>
#include <TColStd_Array1OfInteger.hxx>
#include <TColStd_DataMapOfAsciiStringInteger.hxx>

#include <Standard_ProgramError.hxx>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#ifdef HAVE_LIMITS
# include <limits>
#elif defined (HAVE_LIMITS_H)
# include <limits.h>
#endif

#ifdef WNT
# include <limits>
#endif

#define CPPJINI_BOOLEAN         1
#define CPPJINI_CHARACTER       2
#define CPPJINI_ENUMERATION     3
#define CPPJINI_EXTCHARACTER    4
#define CPPJINI_INTEGER         5
#define CPPJINI_REAL            6
#define CPPJINI_BYTE            7
#define CPPJINI_SHORTREAL       8

static Handle( MS_MetaSchema ) s_MS;
static CPPJini_ExtractionType  s_Type;

Handle( MS_HSequenceOfMemberMet ) SeqOfMemberMet = new MS_HSequenceOfMemberMet ();
Handle( MS_HSequenceOfExternMet ) SeqOfExternMet = new MS_HSequenceOfExternMet ();

Handle( TCollection_HAsciiString ) CPPJini_InterfaceName;
Handle( TCollection_HAsciiString ) CPPJini_ErrorArgument =
                                    new TCollection_HAsciiString ( "%error%" );

Handle( TCollection_HAsciiString ) CPPJini_DotReplace ( char*, char = '_' );

static CPPJini_SequenceOfClientInfo s_CltInfo;
static Standard_Integer             s_UseLevel;
static WOKTools_MapOfHAsciiString   s_CltMap;

WOKTools_MapOfHAsciiString                   g_ImportMap;
WOKTools_DataMapOfHAsciiStringOfHAsciiString g_SkipMap;
Handle( CPPJini_ClientInfo )                 g_Client;

static Standard_Boolean CPPJini_CheckClients (
                         const Handle( TCollection_HAsciiString )& className,
                         Standard_Integer                          idx,
                         Handle( TCollection_HAsciiString )&       cltName,
                         Standard_Boolean&                         fDuplicate
                        ) {

 Standard_Boolean retVal = Standard_False;
 Standard_Boolean fCond;
 Standard_Integer i;

 fDuplicate = Standard_False;

 for ( i = 1; i <= s_CltInfo.Length (); ++i ) {

  switch ( idx ) {

   case 0:

    fCond = s_CltInfo.Value ( i ) -> HasComplete ( className );

   break;

   case 1:

    fCond = s_CltInfo.Value ( i ) -> HasIncomplete ( className );

   break;

   default:

    fCond = s_CltInfo.Value ( i ) -> HasSemicomplete ( className );

  }  // end switch

  if ( fCond ) {

   if ( !retVal ) {

    cltName = new TCollection_HAsciiString (  s_CltInfo.Value ( i ) -> Name ()  );
    retVal  = Standard_True;

   } else {

    fDuplicate = Standard_True;
    break;

   }  // end else

  }  // end if

 }  // end for

 return retVal;

}  // end CPPJini_CheckClients

Standard_Boolean CPPJini_HasComplete (
                  const Handle( TCollection_HAsciiString )& className,
                  Handle( TCollection_HAsciiString )&       cltName,
                  Standard_Boolean&                         fDuplicate
                 ) {

 return CPPJini_CheckClients ( className, 0, cltName, fDuplicate );

}  // end CPPJini_HasIncomplete

Standard_Boolean CPPJini_HasIncomplete (
                  const Handle( TCollection_HAsciiString )& className,
                  Handle( TCollection_HAsciiString )&       cltName,
                  Standard_Boolean&                         fDuplicate
                 ) {

 return CPPJini_CheckClients ( className, 1, cltName, fDuplicate );

}  // end CPPJini_HasIncomplete

Standard_Boolean CPPJini_HasSemicomplete (
                  const Handle( TCollection_HAsciiString )& className,
                  Handle( TCollection_HAsciiString )&       cltName,
                  Standard_Boolean&                         fDuplicate
                 ) {

 return CPPJini_CheckClients ( className, 2, cltName, fDuplicate );

}  // end CPPJini_HasIncomplete

Standard_Boolean CPPJini_Defined (
                  const Handle( TCollection_HAsciiString )& aTypeName,
                  Handle( TCollection_HAsciiString )&       cltName,
                  Standard_Boolean&                         fDuplicate,
                  CPPJini_ExtractionType&                   aType
                 ) {

 Standard_Boolean                    retVal = Standard_False;
 CPPJini_ExtractionType              type;
 Standard_Integer                    i;
 Handle( CPPJini_ClientInfo )        cInfo;
 TColStd_SequenceOfInteger           sLevels;
 TColStd_SequenceOfInteger           sModes;
 TColStd_SequenceOfInteger           sIndices;

 fDuplicate = Standard_False;

 for ( i = 1; i <= s_CltInfo.Length (); ++i )

  if (  s_CltInfo.Value ( i ) -> Defined ( aTypeName, type )  ) {

   sLevels.Append  (  s_CltInfo.Value ( i ) -> Level ()  );
   sIndices.Append (  i                                  );
   sModes.Append   (  ( Standard_Integer )type           );

  }  // end if

 switch (  sLevels.Length ()  ) {

  case 0:

  break;

  case 1:

   cltName = new TCollection_HAsciiString (
                  s_CltInfo.Value (  sIndices.Value ( 1 )  ) -> Name ()
                 );
   aType   = ( CPPJini_ExtractionType ) (int) sModes.Value ( 1 );
   retVal  = Standard_True;

  break;

  default: {

   Handle( CPPJini_ClientInfo ) ci;
   Standard_Boolean             fDefined = Standard_False;

   if ( s_Type != CPPJini_SEMICOMPLETE ) {

    for (  i = 1; i <= sIndices.Length (); ++i  )

     if (   s_CltInfo.Value (  sIndices.Value ( i )  ) -> Root ()    )

      if ( !fDefined ) {

       ci = s_CltInfo.Value (  sIndices.Value ( i )  );
       s_CltInfo.Remove (  sIndices.Value ( i )  );
       s_CltInfo.Prepend ( ci );
       fDefined = Standard_True;
       cltName = new TCollection_HAsciiString (  ci -> Name ()  );
       aType = ( CPPJini_ExtractionType ) (int) sModes.Value ( i );

      } else {

       fDuplicate = Standard_True;
       break;

      }  // end else

    if ( !fDefined ) {

     Standard_Integer maxVal = INT_MIN;
     Standard_Integer index=0, j=0;

     for (  i = 1; i <= sLevels.Length (); ++i  )

      if (  sLevels.Value ( i ) > maxVal  ) {

       maxVal     = sLevels.Value  ( i );
       index      = sIndices.Value ( i );
       j          = i;
       fDuplicate = Standard_False;
       fDefined   = Standard_True;

      } else if (  sLevels.Value ( i ) == maxVal  ) {

       fDuplicate = Standard_True;
       continue;

      }  // end if

     ci      = s_CltInfo.Value ( index );
     cltName = new TCollection_HAsciiString (  ci -> Name ()  );
     aType   = ( CPPJini_ExtractionType ) (int) sModes.Value ( j );
     s_CltInfo.Remove  ( index );
     s_CltInfo.Prepend ( ci    );

    }  // end if

   }  // end if

   if ( !fDefined ) {

    Standard_Integer minVal = INT_MAX;
    Standard_Integer index=0, j=0;

    for (  i = 1; i <= sLevels.Length (); ++i  )

     if (  sLevels.Value ( i ) < minVal  ) {

      minVal     = sLevels.Value  ( i );
      index      = sIndices.Value ( i );
      j          = i;
      fDuplicate = Standard_False;

     } else if (  sLevels.Value ( i ) == minVal  ) {

      fDuplicate = Standard_True;
      continue;

     }  // end if

    ci      = s_CltInfo.Value ( index );
    cltName = new TCollection_HAsciiString (  ci -> Name ()  );
    aType   = ( CPPJini_ExtractionType ) (int) sModes.Value ( j );
    s_CltInfo.Remove  ( index );
    s_CltInfo.Prepend ( ci    );

   }  // end if

   retVal = Standard_True;

  }  // default

 }  // end switch;

 return retVal;

}  // end CPPJini_Defined

Standard_Boolean CPPJini_Defined ( 
                  const Handle( TCollection_HAsciiString )& aTypeName,
                  Handle( TCollection_HAsciiString )&       cltName
                 ) {

 Standard_Boolean       fDup;
 CPPJini_ExtractionType xType;

 return CPPJini_Defined ( aTypeName, cltName, fDup, xType );

}  // end CPPJini_Defined

Handle( TColStd_HSequenceOfHAsciiString ) CPPJini_TemplatesUsed () {

 Handle( TColStd_HSequenceOfHAsciiString ) result =
  new TColStd_HSequenceOfHAsciiString ();

 result -> Append (  new TCollection_HAsciiString ( "CPPJini_Template.edl" )  );
 result -> Append (  new TCollection_HAsciiString ( "CPPJini_General.edl"  )  );

 return result;

}  // end CPPJini_TemplatesUsed

static void _CPPJini_FillUses (
             const Handle( MS_MetaSchema                   )& ms,
             const Handle( TColStd_HSequenceOfHAsciiString )& use
            ) {

 ++s_UseLevel;

  Standard_Integer i, j;

  for ( i = 1; i <= use -> Length (); ++i ) {

   Handle( MS_Client ) clt;

   if (   s_CltMap.Contains (  use -> Value ( i )  )   ) {

    ErrorMsg() << "_CPPJini_FillUses"
             << "Cyclic dependency between clients ( check your 'uses' lists )"
             << endm;
    Standard_ProgramError :: Raise ();

   }  // end if

   for (  j = 1; j <= s_CltInfo.Length (); ++j  )

    if (   s_CltInfo.Value ( j ) -> Name () -> IsSameString (  use -> Value ( i )  )   ) {

     s_CltInfo.Value ( j ) -> SetLevel (
                               s_CltInfo.Value ( j ) -> Level () + 1
                              );
     goto next;

    }  // end if

   clt = ms -> GetClient (  use -> Value ( i )  );

   s_CltInfo.Prepend (
              new CPPJini_ClientInfo (
                   ms, use -> Value ( i ), s_UseLevel
                  )
             );

   s_CltMap.Add (  use -> Value ( i )  );
    _CPPJini_FillUses (  ms, clt -> Uses ()  );
   s_CltMap.Remove (  use -> Value ( i )  );
next: continue;
  }  // end for

 if (  i == 1 && s_CltInfo.Length ()  )

  s_CltInfo.Value ( 1 ) -> SetRoot ( Standard_True );

 --s_UseLevel;

}  // end _CPPJini_FillUses

void CPPJini_Init (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( TCollection_HAsciiString        )& aName, 
      const Handle( MS_HSequenceOfExternMet         )& SeqOfEM,
      const Handle( MS_HSequenceOfMemberMet         )& SeqOfMM,
      const Handle( TColStd_HSequenceOfHAsciiString )& use
     ) {

 SeqOfMemberMet = SeqOfMM;
 SeqOfExternMet = SeqOfEM;

 WOKTools_MapOfHAsciiString map;

 for ( Standard_Integer i = 1; i <= SeqOfEM -> Length (); ++i )

  if (   map.Contains (  SeqOfEM -> Value ( i ) -> FullName ()  )   )

   SeqOfEM -> Remove ( i );

  else

   map.Add (  SeqOfEM -> Value ( i ) -> FullName ()  );

 if (  aMeta -> IsClient ( aName )  ) {

  CPPJini_InterfaceName = aName;

 } else {

  ErrorMsg() << "CPPJini" << "Init : Client " << aName << " not found..." << endm;
  Standard_NoSuchObject :: Raise ();
  
 }  // end else

 s_CltInfo.Clear ();
 s_CltMap.Clear  ();

 s_UseLevel = 0;
 s_CltMap.Add ( aName );
  _CPPJini_FillUses ( aMeta, use );
 s_CltMap.Remove ( aName );

 g_SkipMap.Clear ();

 g_Client = new CPPJini_ClientInfo ( aMeta, aName, 0 );

}  // end CPPJini_Init

Handle( TCollection_HAsciiString )& CPPJini_TransientRootName () {

 static Handle( TCollection_HAsciiString ) name =
  new TCollection_HAsciiString ( "Standard_Transient" );

 return name;

}  // end CPPJini_TransientRootName

Handle( TCollection_HAsciiString )& CPPJini_MemoryRootName () {

 static Handle( TCollection_HAsciiString ) name =
  new TCollection_HAsciiString ("MMgt_TShared");

 return name;

}  // end CPPJini_MemoryRootName

Handle( TCollection_HAsciiString )& CPPJini_MPVRootName () {

 static Handle( TCollection_HAsciiString ) name =
  new TCollection_HAsciiString ( "jcas.Object" );

 return name;

}  // end CPPJini_MPVRootName

Handle( EDL_API )& CPPJini_LoadTemplate (
                    const Handle( TColStd_HSequenceOfHAsciiString )& edlsfullpath,
				    const Handle( TCollection_HAsciiString        )& outdir
                   ) {

 static Handle( EDL_API )  api           = new EDL_API ();
 static Standard_Boolean   alreadyLoaded = Standard_False;

 api -> ClearVariables ();

 if ( !alreadyLoaded ) {

  alreadyLoaded = Standard_True;

  for ( Standard_Integer i = 1; i <= edlsfullpath -> Length(); ++i )

   api -> AddIncludeDirectory (
           edlsfullpath -> Value ( i ) -> ToCString ()
          );


  if (  api -> Execute ( "CPPJini_Template.edl" ) != EDL_NORMAL  ) {

   ErrorMsg() << "CPPJini" << "unable to load : CPPJini_Template.edl" << endm;
   Standard_NoSuchObject :: Raise ();

  }  // end if

  if (  api -> Execute ( "CPPJini_General.edl" ) != EDL_NORMAL  ) {

   ErrorMsg() << "CPPJini" << "unable to load : CPPJini_General.edl" << endm;
   Standard_NoSuchObject :: Raise ();

  }  // end if
  
 }  // end if

 api -> AddVariable (  VJFullPath,          outdir -> ToCString ()  );
 api -> AddVariable (  VJMethodHeader,     "MethodHeader"           );
 api -> AddVariable (  VJConstructorHeader,"ConstructorHeader"      );
 api -> AddVariable (
         VJInterface, CPPJini_InterfaceName -> ToCString ()
        );

 Handle( TCollection_HAsciiString ) libName = new TCollection_HAsciiString (
                                                   CPPJini_InterfaceName -> ToCString ()
                                                  );

 libName -> ChangeAll ( '.', '_' );

 api -> AddVariable (  "%LibName", libName -> ToCString ()  );

 return api;

}  // end CPPJini_LoadTemplate

void CPPJini_WriteFile (
      const Handle( EDL_API                  )& api,
      const Handle( TCollection_HAsciiString )& aFileName,
      const Standard_CString                    var
     ) {

 api -> OpenFile  ("HTFile", aFileName -> ToCString ()  );
 api -> WriteFile ("HTFile", var                        );
 api -> CloseFile ("HTFile"                             );

}  // end CPPJini_WriteFile

void CPPJini_DispatchUsedType (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( MS_Type                         )& thetype,
      const Handle( TColStd_HSequenceOfHAsciiString )& List,
      const Handle( TColStd_HSequenceOfHAsciiString )& Incp,
      const Standard_Boolean                           notusedwithref
     ) {

 MS :: AddOnce (  List, thetype -> FullName ()  );
 MS :: AddOnce (  Incp, thetype -> FullName ()  );

}  // end CPPJini_DispatchUsedType

Handle( TCollection_HAsciiString ) CPPJini_UnderScoreReplace (
                                    const Handle( TCollection_HAsciiString )& name
                                   ) {

 char  str[ 5000 ];
 char* from = (char *)name -> ToCString ();
 int   cur = 0;
 
 for (  int i = 0; i < name -> Length (); ++i )

  if ( from[ i ] == '_' ) {

   str[ cur++ ] = '_';
   str[ cur++ ] = '1';

  } else str[ cur++ ] = from[ i ];

 str[ cur ] = '\0';
 
 return new TCollection_HAsciiString ( str );
	
}  // end CPPJini_UnderScoreReplace

Standard_Boolean CPPJini_HaveEmptyConstructor (
                  const Handle( MS_MetaSchema            )& aMeta,
                  const Handle( TCollection_HAsciiString )& aClass,
                  const Handle( MS_HSequenceOfMemberMet  )& methods
                 ) {

 if (  methods.IsNull ()  ) return Standard_False;
  
 for (  int i = 1; i <= methods -> Length (); ++i  )

  if (   methods -> Value ( i ) -> IsKind (  STANDARD_TYPE( MS_Construc )  )   )
#if 1
   if (  methods -> Value ( i ) -> Params ().IsNull () &&
        !methods -> Value ( i ) -> Private ()          &&
        !methods -> Value ( i ) -> IsProtected ()
   ) return Standard_True;
#else
   if (  methods -> Value ( i ) -> Params ().IsNull ()  ) return Standard_True;
#endif
 return Standard_False;

}  // end CPPJini_HaveEmptyConstructor

// sort the method used types :
//
//    FullList : all the used types 
//    List     : the types that must have a full definition
//    Incp     : the types that only have to be declared
//
void CPPJini_MethodUsedTypes (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( MS_Method                       )& aMethod,
      const Handle( TColStd_HSequenceOfHAsciiString )& List,
      const Handle( TColStd_HSequenceOfHAsciiString )& Incp
     ) {

 Standard_Integer                   i;
 Handle( MS_Param                 ) aParam;
 Handle( MS_Type                  ) thetype;
 Handle( TCollection_HAsciiString ) aName, aNameType, parname;

 if (   aMethod -> IsKind (  STANDARD_TYPE( MS_MemberMet )  )   ) {

  Handle( MS_MemberMet ) aMM = *(   (  Handle( MS_MemberMet )*  )&aMethod   );

  aName = aMM -> Class ();

 } else if (   aMethod -> IsKind (  STANDARD_TYPE( MS_ExternMet )  )   ) {

  Handle( MS_ExternMet ) aMM = *(   (  Handle( MS_ExternMet )*  )&aMethod   );

  aName = aMM -> Package ();
  
 }  // end if

 aParam = aMethod -> Returns ();

 if (  !aParam.IsNull ()  ) {

  thetype = aParam -> Type     ();
  parname = aParam -> TypeName ();

  if (   thetype -> IsKind (  STANDARD_TYPE( MS_Alias )  )   ) {

   Handle( MS_Alias ) analias = *(   (  Handle( MS_Alias )*  )&thetype   );
      
   parname = analias -> DeepType ();
      
   if (  aMeta -> IsDefined ( parname )  )

	thetype = aMeta -> GetType ( parname );

   else {

	ErrorMsg() << "CPPJini" << "Type " << parname << " not defined..." << endm;
	Standard_NoSuchObject :: Raise ();

   }  // end else

  }  // end if
  
  if (  !parname -> IsSameString ( aName )  )

   CPPJini_DispatchUsedType (
    aMeta, thetype, List, Incp, !aMethod -> IsRefReturn ()
   );

 }  // end if

 Standard_Boolean          fEnum    = Standard_False;     
 Handle(MS_HArray1OfParam) seqparam = aMethod->Params();

 if (  !seqparam.IsNull ()  ) {

  for ( i = 1; i <= seqparam -> Length (); ++i ) {

   thetype = seqparam -> Value ( i ) -> Type ();
     
   if (  !fEnum && thetype -> IsKind (  STANDARD_TYPE( MS_Enum )  )   )

    fEnum = Standard_True;

   parname = seqparam -> Value ( i ) -> TypeName ();

   if (   thetype -> IsKind (  STANDARD_TYPE( MS_Alias )  )   ) {

    Handle( MS_Alias ) analias = *(   (  Handle( MS_Alias )*  )&thetype   );
      
    parname = analias -> DeepType ();
      
    if (  aMeta -> IsDefined ( parname )  )

     thetype = aMeta -> GetType ( parname );

    else {

     ErrorMsg() << "CPPJini" << "Type " << parname << " not defined..." << endm;
     Standard_NoSuchObject :: Raise ();
	
    }  // end else

   }  // end if

   if (  !parname -> IsSameString ( aName )  )

    CPPJini_DispatchUsedType (
     aMeta, thetype, List, Incp, Standard_False
    );

  }  // end for

  if ( fEnum ) List -> Append (  new TCollection_HAsciiString ( "Standard_Short" )  );

 }  // end if

}  // end CPPJini_MethodUsedTypes

// sort the class used types :
//
//    FullList : all the used types 
//    List     : the types that must have a full definition
//    Incp     : the types that only have to be declared
//
void CPPJini_ClassUsedTypes (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( MS_Class                        )& aClass,
      const Handle( TColStd_HSequenceOfHAsciiString )& List,
      const Handle( TColStd_HSequenceOfHAsciiString )& Incp
     ) {

 Standard_Integer                          i;
 Handle( TColStd_HSequenceOfHAsciiString ) asciiseq;
 Handle( TCollection_HAsciiString        ) str, aNameType;

 asciiseq = aClass -> GetInheritsNames ();

 for ( i = 1; i <= asciiseq -> Length (); ++i ) {

  aNameType = new TCollection_HAsciiString ();
  aNameType -> AssignCat (  CPPJini_InterfaceName    );
  aNameType -> AssignCat (  "_"                      );
  aNameType -> AssignCat (  asciiseq -> Value ( i )  );

  MS::AddOnce ( List, aNameType );

 }  // end for

 Handle( MS_HSequenceOfMemberMet ) metseq = aClass -> GetMethods ();

 for ( i = 1; i <= metseq -> Length (); ++i )

  CPPJini_MethodUsedTypes (  aMeta, metseq -> Value ( i ), List, Incp  );

}  // end CPPJini_ClassUsedTypes

// sort the used types :
//
//    FullList : all the used types 
//    List     : the types that must have a full definition
//    Incp     : the types that only have to be declared
//
void CPPJini_UsedTypes (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( MS_Common                       )& aCommon,
      const Handle( TColStd_HSequenceOfHAsciiString )& List,
      const Handle( TColStd_HSequenceOfHAsciiString )& Incp
     ) {

 if (   aCommon -> IsKind (  STANDARD_TYPE( MS_Type  )  ) &&
        aCommon -> IsKind (  STANDARD_TYPE( MS_Class )  )
 ) {

  Handle( MS_Class ) aClass = *(   (  Handle( MS_Class )*  )&aCommon   );
      
  CPPJini_ClassUsedTypes ( aMeta, aClass, List, Incp );

 }  // end if

}  // end CPPJini_UsedTypes

// build a return, parameter or field type in c++
//  return a <type name> or a Handle_<type name>
//
Handle( TCollection_HAsciiString ) CPPJini_BuildType (
                                    const Handle( MS_MetaSchema            )& aMeta,
                                    const Handle( TCollection_HAsciiString )& aTypeName
                                   ) {

 Handle( TCollection_HAsciiString ) result = new TCollection_HAsciiString ();
 Handle( TCollection_HAsciiString ) rTypeName;
 Handle( TCollection_HAsciiString ) parname;
 Handle( MS_Type                  ) aType;
  
 if (  aMeta -> IsDefined ( aTypeName )  ) {

  aType   = aMeta -> GetType ( aTypeName );
  parname = aTypeName;

  if (   aType -> IsKind (  STANDARD_TYPE( MS_Alias )  )   ) {

   Handle( MS_Alias ) analias = *(   (  Handle( MS_Alias )*  )&aType   );
      
   parname = analias -> DeepType ();
      
   if (  aMeta -> IsDefined ( parname )  )

	aType = aMeta -> GetType ( parname );

   else {

    ErrorMsg() << "CPPJini" << "Type " << parname << " not defined..." << endm;
    Standard_NoSuchObject :: Raise ();

   }  // end else

  }  // end if
    
  if (   aType -> IsKind (  STANDARD_TYPE( MS_Enum )  )   )

   parname = new TCollection_HAsciiString ( "short" );

  result -> AssignCat ( parname );

  if (   aType -> IsKind (  STANDARD_TYPE( MS_NatType )  )   )

   if (  aType -> IsKind (  STANDARD_TYPE( MS_Imported )  ) ||
         aType -> IsKind (  STANDARD_TYPE( MS_Pointer  )  )
   ) result = CPPJini_ErrorArgument;

  if (   !strcmp (  aType -> FullName () -> ToCString (), "Standard_Address"  )   )

   result = CPPJini_ErrorArgument;

 } else {

  ErrorMsg() << "CPPJini" << "Type " << aTypeName << " not defined..." << endm;
  Standard_NoSuchObject :: Raise ();

 }  // end else

 return result;

}  // end CPPJini_BuildType

Standard_Boolean CPPJini_IsCasType (  const Handle( TCollection_HAsciiString )& typname  ) {

 char* typeName = (char *)typname -> ToCString ();

 if (  !strcmp ( typeName, "Object" )  ) return Standard_True;

 if ( typeName[ 0 ] != 'S' ) return Standard_False;

 if (  !strcmp ( typeName, "Standard_CString"      )  ) return Standard_True;
 if (  !strcmp ( typeName, "Standard_ExtString"    )  ) return Standard_True;
 if (  !strcmp ( typeName, "Standard_Character"    )  ) return Standard_True;
 if (  !strcmp ( typeName, "Standard_Integer"      )  ) return Standard_True;
 if (  !strcmp ( typeName, "Standard_Real"         )  ) return Standard_True;
 if (  !strcmp ( typeName, "Standard_Boolean"      )  ) return Standard_True;
 if (  !strcmp ( typeName, "Standard_ExtCharacter" )  ) return Standard_True;
 if (  !strcmp ( typeName, "Standard_Byte"         )  ) return Standard_True;
 if (  !strcmp ( typeName, "Standard_ShortReal"    )  ) return Standard_True;
 if (  !strcmp ( typeName, "Standard_Address"      )  ) return Standard_True;
 if (  !strcmp ( typeName, "Standard_Short"        )  ) return Standard_True;

 return Standard_False;

}  // end CPPJini_IsCasType

Handle( TCollection_HAsciiString ) CPPJini_CheckPrimParam (
                                    const Handle( TCollection_HAsciiString )& parname,
                                    const Standard_Boolean                    isout
                                   ) {

 char* typeName = (char *)parname -> ToCString ();

 if (  !strcmp ( typeName, "Standard_Address" )  ) return CPPJini_ErrorArgument;

 if (  !strcmp ( typeName, "Standard_ExtString" )  )

  return !isout ? new TCollection_HAsciiString ( "String"       )
                : new TCollection_HAsciiString ( "StringBuffer" );

 if ( isout ) return parname;

 if (  !strcmp ( typeName, "Standard_Integer"      )  )
  return new TCollection_HAsciiString ( "int"     );
 if (  !strcmp ( typeName, "Standard_Real"         )  )
  return new TCollection_HAsciiString ( "double"  );
 if (  !strcmp ( typeName, "Standard_Boolean"      )  )
  return new TCollection_HAsciiString ( "boolean" );
 if (  !strcmp ( typeName, "Standard_ExtCharacter" )  )
  return new TCollection_HAsciiString ( "char"    );
 if (  !strcmp ( typeName, "Standard_Byte"         )  )
  return new TCollection_HAsciiString ( "byte"    );
 if (  !strcmp ( typeName, "Standard_ShortReal"    )  )
  return new TCollection_HAsciiString ( "float"   );

 return parname;

}  // end CPPJini_CheckPrimParam

Handle( TCollection_HAsciiString ) CPPJini_GetFullJavaType (
                                    const Handle( TCollection_HAsciiString )& className
                                   ) {

 Handle( TCollection_HAsciiString ) theret;

 if (  CPPJini_IsCasType ( className )  )

  theret = new TCollection_HAsciiString ( "jcas." );
 
 else {

  theret = new TCollection_HAsciiString (  CPPJini_InterfaceName -> ToCString ()  );
  theret -> AssignCat ( "." );

 }  // end else

 theret -> AssignCat ( className );

 return theret;

}  // end CPPJini_GetFullJavaType

// Build a parameter list for methods
//    the output is in C++
//
Handle( TCollection_HAsciiString ) CPPJini_BuildParameterList (
                                    const Handle( MS_MetaSchema     )& aMeta, 
                                    const Handle( MS_HArray1OfParam )& aSeq,
                                    const Standard_Boolean             withtype
                                   ) {

 Standard_Integer                   i;
 Handle( TCollection_HAsciiString ) result = new TCollection_HAsciiString ();
 Handle( MS_Type                  ) aType;
 Handle( MS_Class                 ) aClass;
 Handle( TCollection_HAsciiString ) parname;

 if (  !aSeq.IsNull ()  ) {

  for (  i = 1; i <= aSeq -> Length (); ++i  ) {

   if ( i > 1 ) result -> AssignCat ( "," );

   if (   aMeta -> IsDefined (  aSeq -> Value ( i ) -> TypeName ()  )   ) {

    if ( !withtype )

     result -> AssignCat (  aSeq -> Value ( i ) -> Name ()  );

    else {

     parname = aSeq -> Value ( i ) -> TypeName ();
     aType   = aMeta -> GetType ( parname );
      
     if (   aType -> IsKind (  STANDARD_TYPE( MS_Alias )  )   ) {

      Handle( MS_Alias ) analias = *(   (  Handle( MS_Alias )*  )&aType   );
	    
      parname = analias -> DeepType ();
	    
      if (  aMeta -> IsDefined ( parname )  )

       aType = aMeta -> GetType ( parname );

      else {

       ErrorMsg() << "CPPJini" << "Type " << parname << " not defined..." << endm;
       Standard_NoSuchObject :: Raise ();

      }  // end else

     }  // end if

     if (  aType -> IsKind (  STANDARD_TYPE( MS_Imported )  ) ||
           aType -> IsKind (  STANDARD_TYPE( MS_Pointer  )  )
     ) {

      result = CPPJini_ErrorArgument;
      return result;

     }  // end if

     if (   aType -> IsKind(  STANDARD_TYPE( MS_Enum )  )   )

      if (  aSeq -> Value ( i ) -> IsOut ()  )

       parname = new TCollection_HAsciiString ( "Standard_Short" );

      else

       parname = new TCollection_HAsciiString ( "short" );

     if (   aType -> IsKind (  STANDARD_TYPE( MS_PrimType )  )   ) {

      parname = CPPJini_CheckPrimParam (  parname, aSeq -> Value ( i ) -> IsOut ()  );

      if ( parname == CPPJini_ErrorArgument ) return parname;

     }  // end if
	  
     result -> AssignCat ( parname );
     result -> AssignCat ( " " );
     result -> AssignCat (  aSeq -> Value ( i ) -> Name ()  );

    }  // end else

   }  // end if

  }  // end for

 }  // end if

 return result;

}  // end if

void CPPJini_CheckMethod (
      const Standard_Integer                    index,
      const Handle( TCollection_HAsciiString )& thename,
      TColStd_DataMapOfAsciiStringInteger&      themap,
      TColStd_Array1OfInteger&                  theindexes
     ) {

 TCollection_AsciiString name(  thename -> ToCString ()  );

 if (  themap.IsBound ( name )  ) {

  Standard_Integer curnb = 0;
  Standard_Integer theindex = themap.Find ( name );

  if (  theindexes ( theindex ) == 0  ) theindexes ( theindex ) = 1;

  curnb = theindexes ( theindex ) + 1;
  theindexes ( index ) = curnb;
  themap.UnBind ( name );

 }  // end if

 themap.Bind ( name, index );

}  // end CPPJini_CheckMethod
			 
// build a c++ declaration method
// the result is in the EDL variable VJMethod
//
//   template used :
//
//         MethodTemplateDef
//         ConstructorTemplateDef
//         ConstructorTemplateDec
//
//   the EDL variables : 
//        VJMethodHeader : must contains the name of the template used for 
//                        methods construction
//        VJConstructorHeader :  must contains the name of the template used for 
//                              constructors construction
//
//  WARNING : if an error was found the result in the variable "%Method" will be "%error%"
//
void CPPJini_BuildMethod(const Handle(MS_MetaSchema)& aMeta, 
			 const Handle(EDL_API)& api,
			 const Handle(TCollection_HAsciiString)& className,
			 const Handle(MS_Method)& m,
			 const Handle(TCollection_HAsciiString)& methodName,
			 const Standard_Integer MethodNumber)
{
  Handle(MS_InstMet)               im;
  Handle(MS_ClassMet)              cm;
  Handle(MS_Construc)              ct;
  Handle(MS_Param)                 retType;
  Handle(TCollection_HAsciiString) MetTemplate,OverMetTemplate,theArgList,theArgVals,
  ConTemplate;


  MetTemplate = api->GetVariableValue(VJMethodHeader);
  ConTemplate = api->GetVariableValue(VJConstructorHeader);

  if (MethodNumber != 0) {
    OverMetTemplate = new TCollection_HAsciiString("Overload");
    OverMetTemplate->AssignCat(MetTemplate);
  }

  // here we process all the common attributes of methods
  //
  api->AddVariable(VJMethodName,methodName->ToCString());
  api->AddVariable(VJVirtual,"");
  
  api->AddVariable("%NbMet",MethodNumber);
  api->AddVariable("%RetMode","");
  api->AddVariable("%Class",className->ToCString());
  
  theArgList = CPPJini_BuildParameterList(aMeta,m->Params(),Standard_True);
  
  if (theArgList == CPPJini_ErrorArgument) {
    WarningMsg() << "CPPJini" << "Bad argument type in method (pointer or imported type) " << m->FullName() << endm;
    WarningMsg() << "CPPJini" << "Method : " << m->FullName() << " not exported." << endm;
    api->AddVariable(VJMethod,CPPJini_ErrorArgument->ToCString());
    return;
  }
  
  api->AddVariable("%Arguments",theArgList->ToCString());

  theArgVals = CPPJini_BuildParameterList(aMeta,m->Params(),Standard_False);
  api->AddVariable("%ArgsInCall",theArgVals->ToCString());

  // it s returning a type or void ?
  //
  retType = m->Returns();
  
  if (!retType.IsNull()) {
    Handle(TCollection_HAsciiString) returnT = CPPJini_BuildType(aMeta,retType->TypeName());
    
    if (returnT == CPPJini_ErrorArgument) {
      WarningMsg() << "CPPJini" << "Return type (pointer or imported type) of " << m->FullName() << " not exportable." << endm;
      WarningMsg() << "CPPJini" << "Method : " << m->FullName() << " not exported." << endm;
      api->AddVariable(VJMethod,CPPJini_ErrorArgument->ToCString());
      return;
    }
    else {
      returnT = CPPJini_CheckPrimParam(returnT,Standard_False);
      api->AddVariable(VJReturn,returnT->ToCString());
      api->AddVariable("%RetMode","return");
    }
  }
  else {
    api->AddVariable(VJReturn,"void");
  }
  
  // now the specials attributes
  //
  // instance methods
  //
  api->AddVariable(VJVirtual,"");
  
  if (m->IsKind(STANDARD_TYPE(MS_InstMet))) {
    im = *((Handle(MS_InstMet)*)&m);
    
    if (MethodNumber != 0) {
      MetTemplate = OverMetTemplate;
    }
    if (im->IsStatic()) {
      api->AddVariable(VJVirtual,"final");
    }
    api->Apply(VJMethod,MetTemplate->ToCString());
  }
  //
  // class methods
  //
  else if (m->IsKind(STANDARD_TYPE(MS_ClassMet))) {
    api->AddVariable(VJVirtual,"static");
    if (MethodNumber != 0) {
      MetTemplate = OverMetTemplate;
    }

    api->Apply(VJMethod,MetTemplate->ToCString());
  }
  //
  // constructors
  //
  else if (m->IsKind(STANDARD_TYPE(MS_Construc))) {
    api->Apply(VJMethod,ConTemplate->ToCString());
  }
  //
  // package methods
  //
  else if (m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
    api->AddVariable(VJVirtual,"static");
    if (MethodNumber != 0) {
      MetTemplate = OverMetTemplate;
    }
    
    api->Apply(VJMethod,MetTemplate->ToCString());
  }  
}


Handle(TCollection_HAsciiString) CPPJini_ConvertToJavaType(const Handle(MS_MetaSchema)& aMeta,
							   const Handle(TCollection_HAsciiString)& aTypeName,Standard_Boolean isout,Standard_Integer& typeprim)
{
  Handle(TCollection_HAsciiString)   result = new TCollection_HAsciiString();
  Handle(TCollection_HAsciiString)   rTypeName;
  Handle(TCollection_HAsciiString)   parname;
  Handle(MS_Type)                    aType;

  typeprim = 0;

  if (aMeta->IsDefined(aTypeName)) {
    aType     = aMeta->GetType(aTypeName);
    parname   = aTypeName;

    if (aType->IsKind(STANDARD_TYPE(MS_Alias))) {
      Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&aType);
      
      parname = analias->DeepType();
      
      if (aMeta->IsDefined(parname)) {
	aType = aMeta->GetType(parname);
      }
      else {
	ErrorMsg() << "CPPJini" << "Type " << parname << " not defined..." << endm;
	Standard_NoSuchObject::Raise();
      }
    }
  

    if (aType->IsKind(STANDARD_TYPE(MS_Enum))) {
      typeprim = CPPJINI_ENUMERATION;
      if (!isout) {
	result->AssignCat("jshort");
      }
      else {
	result->AssignCat("jobject");
      }
      return result;
    }


    char* typeName = (char *)parname->ToCString();

    if (!strcmp(typeName,"Standard_Address")) {
      return CPPJini_ErrorArgument;
    }

    if (!strcmp(typeName,"Standard_ExtString")) {
      if (isout) {
	return new TCollection_HAsciiString("jstringbuffer");
      }
      return new TCollection_HAsciiString("jstring");
    }

    if (!strcmp(typeName,"Standard_CString")) {
      return new TCollection_HAsciiString("cstring");
    }

    if (!strcmp(typeName,"Standard_Integer")) {
      typeprim = CPPJINI_INTEGER;
      if (isout) {
	return new TCollection_HAsciiString("jobject");
      }
      result->AssignCat("jint");
    }
    else if (!strcmp(typeName,"Standard_Real")) {
      typeprim = CPPJINI_REAL;
      if (isout) {
	return new TCollection_HAsciiString("jobject");
      }
      result->AssignCat("jdouble");
    }
    else if (!strcmp(typeName,"Standard_Boolean")) {
      typeprim = CPPJINI_BOOLEAN;
      if (isout) {
	return new TCollection_HAsciiString("jobject");
      }
      result->AssignCat("jboolean");
    }
    else if (!strcmp(typeName,"Standard_ExtCharacter")) {
      typeprim = CPPJINI_EXTCHARACTER;
      if (isout) {
	return new TCollection_HAsciiString("jobject");
      }
      result->AssignCat("jchar");
    } 
    else if (!strcmp(typeName,"Standard_Byte")) {
      typeprim = CPPJINI_BYTE;
      if (isout) {
	return new TCollection_HAsciiString("jobject");
      }
      result->AssignCat("jbyte");
    } 
    else if (!strcmp(typeName,"Standard_ShortReal")) {
      typeprim = CPPJINI_SHORTREAL;
      if (isout) {
	return new TCollection_HAsciiString("jobject");
      }
      result->AssignCat("jfloat");
    }
    else if (!strcmp(typeName,"Standard_Character")) {
      typeprim = CPPJINI_CHARACTER;
      result->AssignCat("jobject");
    }
    else {
      result->AssignCat("jobject");
    }      
  }
  
  return result;

}

void   CPPJini_ArgumentBuilder(const Handle(MS_MetaSchema)& aMeta, 
			       const Handle(EDL_API)& api, 
			       const Handle(TCollection_HAsciiString)& className,
			       const Handle(MS_Method)& m,
			       Handle(TCollection_HAsciiString)& ArgsInDecl,
			       Handle(TCollection_HAsciiString)& ArgsRetrieve,
			       Handle(TCollection_HAsciiString)& ArgsInCall,
			       Handle(TCollection_HAsciiString)& ArgsOut)
{
  Handle(MS_HArray1OfParam) aSeqP = m->Params();
  Standard_Integer            i;
  
  if(!aSeqP.IsNull()) {
    for (i = 1; i <= aSeqP->Length(); i++) {
      int curprimtype = 0;
      Handle(TCollection_HAsciiString) thety = CPPJini_ConvertToJavaType(aMeta,aSeqP->Value(i)->TypeName(),aSeqP->Value(i)->IsOut(),curprimtype);

      if (!strcmp(thety->ToCString(),"jstringbuffer")) {
	api->AddVariable("%TypeName","jobject");
      }
      else if (!strcmp(thety->ToCString(),"cstring")) {
	api->AddVariable("%TypeName","jobject");
      }
      else {
	api->AddVariable("%TypeName",thety->ToCString());
      }
      api->AddVariable("%ArgName",aSeqP->Value(i)->Name()->ToCString());

      api->Apply("%Method","MethodArg");
      ArgsInDecl->AssignCat(api->GetVariableValue("%Method"));

      if (!strcmp("jobject",thety->ToCString())) {
	Handle(MS_Type) curtype  = aMeta->GetType(aSeqP->Value(i)->TypeName());

	if (curtype->IsKind(STANDARD_TYPE(MS_Alias))) {
	  Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&curtype);
      
	  curtype = aMeta->GetType(analias->DeepType());
	}
	
	if (curtype->IsKind(STANDARD_TYPE(MS_StdClass))) {
	  Handle(MS_StdClass) aClass = *((Handle(MS_StdClass)*) &curtype);
	  api->AddVariable("%ClassName",aClass->FullName()->ToCString());
	  if (aClass->IsTransient()) {
	    api->Apply("%Method","TransientGetValue");
	    ArgsInCall->AssignCat("the_");		      
	    ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	    if (aSeqP->Value(i)->IsOut()) {

              Handle( TCollection_HAsciiString ) cltName;
              Handle( TCollection_HAsciiString ) iName = CPPJini_Defined (
                                                          aClass -> FullName (), cltName
                                                         ) ? cltName : CPPJini_InterfaceName;

              api -> AddVariable (
                      "%FromInterface",
                      CPPJini_DotReplace (
                       (char *)iName -> ToCString (), '/'
                      ) -> ToCString ()
                     );
	      api->Apply("%MetOut","TransientSetValue");
	      ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
	    }
	  }
	  else {
	    api->Apply("%Method","ValueGetValue");
	    ArgsInCall->AssignCat("*the_");		      
	    ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	  }
	  ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));

	}
	else {
	  // type primitif non converti en type java
	  switch (curprimtype) {
	  case CPPJINI_BOOLEAN :
	    {
	      ArgsInCall->AssignCat("the_");		      
	      ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	      api->Apply("%Method","BooleanGetValue");
	      ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	      api->Apply("%MetOut","BooleanSetValue");
	      ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
	      break;
	    }

	  case CPPJINI_CHARACTER :
	    {
	      ArgsInCall->AssignCat("the_");		      
	      ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	      api->Apply("%Method","CharacterGetValue");
	      ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	      if (aSeqP->Value(i)->IsOut()) {
		api->Apply("%MetOut","CharacterSetValue");
		ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
	      }
	      break;
	    }

	  case CPPJINI_ENUMERATION :
	    {
	      ArgsInCall->AssignCat("the_");		      
	      ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
              api -> AddVariable (  "%EnumName", curtype -> FullName () -> ToCString ()  );
	      api->Apply("%Method","ShortGetEnumValue");
	      ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	      api->Apply("%MetOut","ShortSetValue");
	      ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
	      break;
	    }
	  case CPPJINI_INTEGER :
	    {
	      ArgsInCall->AssignCat("the_");		      
	      ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	      api->Apply("%Method","IntegerGetValue");
	      ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	      api->Apply("%MetOut","IntegerSetValue");
	      ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
	      break;
	    }

	  case CPPJINI_EXTCHARACTER :
	    {
	      ArgsInCall->AssignCat("the_");		      
	      ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	      api->Apply("%Method","ExtCharacterGetValue");
	      ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	      api->Apply("%MetOut","ExtCharacterSetValue");
	      ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
	      break;
	    }

	  case CPPJINI_REAL :
	    {
	      ArgsInCall->AssignCat("the_");		      
	      ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	      api->Apply("%Method","RealGetValue");
	      ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	      api->Apply("%MetOut","RealSetValue");
	      ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
	      break;
	    }

	  case CPPJINI_BYTE :
	    {
	      ArgsInCall->AssignCat("the_");		      
	      ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	      api->Apply("%Method","ByteGetValue");
	      ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	      api->Apply("%MetOut","ByteSetValue");
	      ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
	      break;
	    }

	  case CPPJINI_SHORTREAL :
	    {
	      ArgsInCall->AssignCat("the_");		      
	      ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	      api->Apply("%Method","ShortRealGetValue");
	      ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	      api->Apply("%MetOut","ShortRealSetValue");
	      ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
	      break;
	    }

	  default :
	    {
	      api->AddVariable("%ClassName",curtype->FullName()->ToCString());
	      api->Apply("%Method","ValueGetValue");
	      ArgsInCall->AssignCat("*the_");		      
	      ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	      ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	    }
	  }
	}
      }
      else if (!strcmp("cstring",thety->ToCString())) {
	api->Apply("%Method","CStringGetValue");
	ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	ArgsInCall->AssignCat("the_");		      
	ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	if (aSeqP->Value(i)->IsOut()) {
	  api->Apply("%MetOut","CStringSetValue");
	  ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
	}
      }
      else if (!strcmp("jstring",thety->ToCString())) {
	api->Apply("%Method","StringGetValue");
	ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	ArgsInCall->AssignCat("the_");		      
	ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
      }
      else if (!strcmp("jstringbuffer",thety->ToCString())) {
	api->Apply("%Method","StringBufferGetValue");
	ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	ArgsInCall->AssignCat("the_");		      
	ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
	api->Apply("%MetOut","StringBufferSetValue");
	ArgsOut->AssignCat(api->GetVariableValue("%MetOut"));
      }
      else {
	ArgsInCall->AssignCat("(");
	ArgsInCall->AssignCat(aSeqP->Value(i)->TypeName());
	ArgsInCall->AssignCat(") ");
	ArgsInCall->AssignCat(aSeqP->Value(i)->Name());
      }
      if (i < aSeqP->Length()) {
	ArgsInCall->AssignCat(",");
      }
    }
  }
}

Standard_Boolean CPPJini_HasMagicConstructor(const Handle(MS_Type)& atyp)
{
  Handle(MS_Class) thecl = Handle(MS_Class)::DownCast(atyp);
  if (thecl.IsNull()) return Standard_True;
  Handle(MS_HSequenceOfMemberMet) methods = thecl->GetMethods();
  for (Standard_Integer i=1; i<= methods->Length(); i++) {
    if (methods->Value(i)->IsKind(STANDARD_TYPE(MS_Construc))) {
      Handle(MS_HArray1OfParam) parameters = methods->Value(i)->Params();
      if (!parameters.IsNull()) {
	if (parameters->Length() == 1) {
	  if (parameters->Value(1)->Type() == atyp) {
	    if (methods->Value(i)->Private()) {
	      return Standard_False;
	    }
	    return Standard_True;
	  }
	}
      }
    }
  }
  return Standard_True;
}

Standard_Boolean CPPJini_HasEmptyConstructor (  const Handle( MS_Type )& atyp  ) {

 Handle( MS_Class ) thecl = Handle( MS_Class ) :: DownCast ( atyp );

 if (  thecl.IsNull ()  ) return Standard_False;

 Handle( MS_HSequenceOfMemberMet ) methods = thecl -> GetMethods ();

 for (  Standard_Integer i = 1; i<= methods -> Length (); ++i  ) {

  if (   methods -> Value ( i ) -> IsKind (  STANDARD_TYPE( MS_Construc )  )   ) {

   Handle( MS_HArray1OfParam ) parameters = methods -> Value ( i ) -> Params ();

   if (  parameters.IsNull ()  ) {

    if (  methods -> Value ( i ) -> Private ()  ) return Standard_False;

    return Standard_True;

   }  // end if

  }  // end if

 }  // end for

 return Standard_False;

}  // end CPPJini_HasEmptyConstructor

void CPPJini_ReturnBuilder (
      const Handle( MS_MetaSchema            )& aMeta, 
      const Handle( EDL_API                  )& api, 
      const Handle( TCollection_HAsciiString )& className,
      const Handle( MS_Method                )& m,
      const Handle( TCollection_HAsciiString )& MethodCall,
            Handle( TCollection_HAsciiString )& RetInDecl,
            Handle( TCollection_HAsciiString )& RetInCall,
            Handle( TCollection_HAsciiString )& RetOut
     ) {

const  char* objType = "jobject";

 if (  !m -> Returns ().IsNull ()  ) {

  Handle( MS_Type ) rtype = aMeta -> GetType (
                                      m -> Returns () -> TypeName ()
                                     );

  if (   rtype -> IsKind (  STANDARD_TYPE( MS_Alias )  )   ) {

   Handle( TCollection_HAsciiString ) parname;
   Handle( MS_Alias                 ) analias =
    *(   (  Handle( MS_Alias )*  )& rtype   );
      
   parname = analias -> DeepType ();
      
   if (  aMeta -> IsDefined ( parname )  )

    rtype = aMeta -> GetType ( parname );

   else {

    ErrorMsg() << "CPPJini" << "Type " << parname << " not defined..." << endm;
	Standard_NoSuchObject :: Raise ();

   }  // end else

  }  // end if
    
  int curprimtype = 0;

  RetInDecl = CPPJini_ConvertToJavaType (
               aMeta, rtype -> FullName (), Standard_False, curprimtype
              );
    
  if (   strcmp (  RetInDecl -> ToCString (),"jobject"  )   ) {
      
   if (   !strcmp (  RetInDecl -> ToCString (), "jstring"  )   ) {

    api -> AddVariable (  "%MethodCall", MethodCall -> ToCString ()  );
    api -> Apply("%Return","ReturnString");
    RetInCall -> AssignCat (  api -> GetVariableValue ( "%Return" )  );
    objType = "jstring";

   } else if (   !strcmp (  RetInDecl -> ToCString (),"cstring"  )   ) {

    RetInDecl = new TCollection_HAsciiString("jobject");
    api -> AddVariable (  "%MethodCall", MethodCall -> ToCString ()  );
    api -> Apply ( "%Return", "ReturnCString" );
    RetInCall -> AssignCat (  api -> GetVariableValue ( "%Return" )  );

   } else {

    objType = RetInDecl -> ToCString ();
    RetInCall -> AssignCat ( " thejret = " );
    RetInCall -> AssignCat ( MethodCall    );
    RetInCall -> AssignCat ( ";\n"         );
    goto setReturn;

   }  // end else

  } else {

   if (   rtype -> IsKind (  STANDARD_TYPE( MS_StdClass )  )   ) {

    Handle( MS_StdClass ) aClass =
     *(   (  Handle( MS_StdClass )*  )& rtype   );

    api -> AddVariable (
            "%ClassName", aClass -> FullName () -> ToCString()
           );
    api -> AddVariable (  "%MethodCall", MethodCall -> ToCString ()  );

    if (   CPPJini_IsCasType (  rtype -> FullName ()  )   )

     api -> AddVariable ( "%FromInterface", "jcas" );

    else {

     Handle( TCollection_HAsciiString ) cltName;
     Handle( TCollection_HAsciiString ) iName =
      CPPJini_Defined (  aClass -> FullName (), cltName  ) ?
       cltName : CPPJini_InterfaceName;

     api -> AddVariable (
             "%FromInterface",
             CPPJini_DotReplace (
              (char *)iName -> ToCString (), '/'
             ) -> ToCString ()
            );

    }  // end else

    if (  aClass -> IsTransient ()  )

     api -> Apply ( "%Return", "ReturnHandle" );

	else {

     if (  m -> IsRefReturn ()  )

      api -> Apply ( "%Return","ReturnValueRef" );

     else if (  CPPJini_HasMagicConstructor ( aClass )  )

      api -> Apply ( "%Return", "ReturnValueMagic" );

     else if (  CPPJini_HasEmptyConstructor ( aClass )  )

      api -> Apply ( "%Return","ReturnValueEmpty" );

     else

      api -> Apply ( "%Return","ReturnValueMalloc" );

    }  // end else

    RetInCall -> AssignCat (  api -> GetVariableValue ( "%Return" )  );

   } else {

    api -> AddVariable (
            "%ClassName", rtype -> FullName () -> ToCString ()
           );
    api -> AddVariable (  "%MethodCall", MethodCall -> ToCString ()  );

	if (   CPPJini_IsCasType (  rtype -> FullName ()  )   )

     api -> AddVariable ( "%FromInterface","jcas" );

    else {

     Handle( TCollection_HAsciiString ) cltName;
     Handle( TCollection_HAsciiString ) iName =
      CPPJini_Defined (
       rtype -> FullName (), cltName
      ) ? cltName : CPPJini_InterfaceName;

     api -> AddVariable (
             "%FromInterface",
             CPPJini_DotReplace (
				 (char *)iName -> ToCString (), '/'
				 ) -> ToCString ()
            );

    }  // end else

    if (  m -> IsRefReturn ()  )

     api -> Apply ( "%Return", "ReturnValueRef" );

	else

     api -> Apply ( "%Return", "ReturnValueMalloc" );

    RetInCall -> AssignCat (  api -> GetVariableValue ( "%Return" )  );

   }  // end else

  }  // end else
setReturn:
  api -> AddVariable ( "%ObjType", objType );
  api -> Apply ( "%MVar", "ReturnVar" );
  api -> AddVariable ( "%MRet", "return thejret;" );
  
 } else {

  RetInDecl -> AssignCat ( "void"     );
  RetInCall -> AssignCat ( MethodCall );
  RetInCall -> AssignCat ( ";\n"      );

 }  // end else

}  // end CPPJini_ReturnBuilder

// build a method call for stub c++
//
void CPPJini_MethodBuilder(const Handle(MS_MetaSchema)& aMeta, 
			   const Handle(EDL_API)& api, 
			   const Handle(TCollection_HAsciiString)& className,
			   const Handle(MS_Method)& m,
			   const Handle(TCollection_HAsciiString)& methodName,
			   const Standard_Integer nummet)
{
  Handle(TCollection_HAsciiString) metname = new TCollection_HAsciiString("Java_");
  Handle(TCollection_HAsciiString) metbody = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString) metcall = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString) metstart = new TCollection_HAsciiString;
  
  Standard_CString                 headerTemplate = NULL;
  
  Handle(TCollection_HAsciiString) ArgsInDecl = new TCollection_HAsciiString("");
  Handle(TCollection_HAsciiString) ArgsRetrieve = new TCollection_HAsciiString("");
  Handle(TCollection_HAsciiString) ArgsInCall = new TCollection_HAsciiString("");
  Handle(TCollection_HAsciiString) ArgsOut = new TCollection_HAsciiString("");
  
  Handle(TCollection_HAsciiString) RetInDecl = new TCollection_HAsciiString("");
  Handle(TCollection_HAsciiString) RetInCall = new TCollection_HAsciiString("");
  Handle(TCollection_HAsciiString) RetOut = new TCollection_HAsciiString("");
  
  CPPJini_ArgumentBuilder(aMeta,api,className,m,ArgsInDecl,ArgsRetrieve,ArgsInCall,ArgsOut);
  metname->AssignCat(CPPJini_InterfaceName);
  metname->AssignCat("_");
  Handle(TCollection_HAsciiString) classunder = CPPJini_UnderScoreReplace(className);


  api -> AddVariable ( "%MVar", "" );
  api -> AddVariable ( "%MRet", "" );


  if ((nummet != 0) || (m->IsKind(STANDARD_TYPE(MS_Construc)))) {
    metname->AssignCat(classunder);
    metname->AssignCat("_");
  }  
  metname->AssignCat(classunder);
  metname->AssignCat("_");
  if ((nummet != 0) || (m->IsKind(STANDARD_TYPE(MS_Construc)))) {
    metname->AssignCat("1");
  }
  metname->AssignCat(CPPJini_UnderScoreReplace(methodName));
  
  
  if ((nummet != 0) || (m->IsKind(STANDARD_TYPE(MS_Construc)))) {
    metname->AssignCat("_1");
    TCollection_AsciiString numb(nummet);
    metname->AssignCat(numb.ToCString());
  }
  
  if (m->IsKind(STANDARD_TYPE(MS_InstMet))) {
    headerTemplate = "InstMethodDec";
  }
  else if (m->IsKind(STANDARD_TYPE(MS_ClassMet))) {
    headerTemplate = "ClassMethodDec";
  }
  else if (m->IsKind(STANDARD_TYPE(MS_Construc))) {
    headerTemplate = "CreateMethodDec";
  }
  else if (m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
    headerTemplate = "PackMethodDec";
  }
  
  // on calcule le call
  
  
  api->AddVariable("%ClassName",className->ToCString());
  api -> AddVariable (
          "%MethodName",
          CPPJini_DotReplace (  (char *)methodName -> ToCString ()  ) -> ToCString ()
         );;
  
  if ((m->IsKind(STANDARD_TYPE(MS_InstMet))) ||
      (m->IsKind(STANDARD_TYPE(MS_Construc)))) {
    
    Handle(MS_Type) objtype  = aMeta->GetType(className);
    
    if (objtype->IsKind(STANDARD_TYPE(MS_Alias))) {
      Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&objtype);
      
      objtype = aMeta->GetType(analias->DeepType());
    }
    
    if (objtype->IsKind(STANDARD_TYPE(MS_StdClass))) {
      Handle(MS_StdClass) aClass = *((Handle(MS_StdClass)*) &objtype);

      
      if (m->IsKind(STANDARD_TYPE(MS_InstMet))) {
	
	if (aClass->IsTransient()) {
	  api->Apply("%Method","ThisTransientGetValue");
	  ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	}
	else {
	  api->Apply("%Method","ThisValueGetValue");
	  ArgsRetrieve->AssignCat(api->GetVariableValue("%Method"));
	}
	api->AddVariable("%ArgsInCall",ArgsInCall->ToCString());
	api->Apply("%MethodCall","InstMethodCall");
	metcall->AssignCat(api->GetVariableValue("%MethodCall"));
	CPPJini_ReturnBuilder(aMeta,api,className,m,metcall,RetInDecl,RetInCall,RetOut);
      }
      else {
	api->AddVariable("%ArgsInCall",ArgsInCall->ToCString());

	if (aClass->IsTransient()) {
	  api->Apply("%MethodCall","TransientCreateMethodCall");
	  RetInCall->AssignCat(api->GetVariableValue("%MethodCall"));
	}
	else {
	  api->Apply("%MethodCall","ValueCreateMethodCall");
	  RetInCall->AssignCat(api->GetVariableValue("%MethodCall"));
	}

      }

    }
  }
  else if (m->IsKind(STANDARD_TYPE(MS_ClassMet))) {
    api->AddVariable("%ArgsInCall",ArgsInCall->ToCString());
    api->Apply("%MethodCall","ClassMethodCall");
    metcall->AssignCat(api->GetVariableValue("%MethodCall"));
    CPPJini_ReturnBuilder(aMeta,api,className,m,metcall,RetInDecl,RetInCall,RetOut);
  }
  else if (m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
    api->AddVariable("%ArgsInCall",ArgsInCall->ToCString());
    api->Apply("%MethodCall","PackMethodCall");
    metcall->AssignCat(api->GetVariableValue("%MethodCall"));
    CPPJini_ReturnBuilder(aMeta,api,className,m,metcall,RetInDecl,RetInCall,RetOut);
  }

  
  
  api -> AddVariable (
          "%MethodName",
          CPPJini_DotReplace (  (char *)metname -> ToCString ()  ) -> ToCString ()
         );
  api->AddVariable("%Return",RetInDecl->ToCString());

  api->AddVariable("%ClassName",className->ToCString());

  api->Apply("%Method",headerTemplate);
  
  metstart->AssignCat(api->GetVariableValue("%Method"));
  
  metstart->AssignCat(ArgsInDecl);
  metstart->AssignCat(")");
  

  // ajout des parametres a recuperer


  metbody->AssignCat(ArgsRetrieve);

  // call

  metbody->AssignCat(RetInCall);

  // Arguments Out

  metbody->AssignCat(ArgsOut);

  // return de la fonction

  metbody->AssignCat(RetOut);

  // c'est fini

  api->AddVariable("%Method",metstart->ToCString());
  api->AddVariable("%MBody",metbody->ToCString());
  const char* theMethodTemplate = 
    !strcmp (api -> GetVariableValue ( "%CPPJiniEXTDBMS" ) -> ToCString (), "OBJS"
	     ) ? "MethodTemplateDefOBJS" : "MethodTemplateDef";
  api -> Apply ("%Method",(char*) theMethodTemplate);

}

// Standard extractor API : launch the extraction of C++ files
//                          from the type <aName>
// 
void CPPJini_TypeExtract(const Handle(MS_MetaSchema)& aMeta,
			 const Handle(TCollection_HAsciiString)& aName,
			 const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
			 const Handle(TCollection_HAsciiString)& outdir,
			 const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
			 const CPPJini_ExtractionType MustBeComplete,
             const Standard_CString Mode)
{

//  InfoMsg() << "CPPJini" << "Extract " << aName->ToCString() << endm;
  Handle(MS_Type)              srcType;
  Handle(MS_Package)           srcPackage;

  // before begining, we look if the entity has something to extract...
  //
  if (aMeta->IsDefined(aName)) {
    srcType   = aMeta->GetType(aName); 
  }
  else if (aMeta->IsPackage(aName)) {
    srcPackage = aMeta->GetPackage(aName);
  }
  else {
    ErrorMsg() << "CPPJini" << aName->ToCString() << " not defined..." << endm;
    Standard_NoSuchObject::Raise();
  }
  
  // ... and we load the templates
  //
  Handle(EDL_API)     api;
  Handle(TCollection_HAsciiString) aHandleFile;

  // Package Extraction
  //
  if (!srcPackage.IsNull()) {
    if (srcPackage->Methods()->Length() > 0) {
      Handle(MS_HSequenceOfExternMet) aSeqMet = new MS_HSequenceOfExternMet;

      api = CPPJini_LoadTemplate(edlsfullpath,outdir);
      api -> AddVariable ( "%CPPJiniEXTDBMS", Mode );

      if (MustBeComplete == CPPJini_SEMICOMPLETE) {
	Standard_Integer i;

	for (i = 1; i <= SeqOfExternMet->Length(); i++) {
	  if (aName->IsSameString(SeqOfExternMet->Value(i)->Package())) {
	    aSeqMet->Append(SeqOfExternMet->Value(i));
	  }
	}
      }

      CPPJini_Package(aMeta,api,srcPackage,outfile,MustBeComplete,aSeqMet);
    }
    else {
      return;
    }
  }
  else if (aName->IsSameString(MS::GetTransientRootName())) {

    api = CPPJini_LoadTemplate(edlsfullpath,outdir);
    api -> AddVariable ( "%CPPJiniEXTDBMS", Mode );


    aHandleFile = new TCollection_HAsciiString(outdir);
    
    aHandleFile->AssignCat(aName);
    aHandleFile->AssignCat(".java");
    api->Apply("%outClass","TransientRootClientJAVA");
    outfile->Append(aHandleFile);

    // ...now we write the result
    //
    api->OpenFile("HTFile",aHandleFile->ToCString());
    api->WriteFile("HTFile","%outClass");
    api->CloseFile("HTFile");
  }
  else if (aName->IsSameString(CPPJini_MemoryRootName())) {
     Handle(TCollection_HAsciiString) aHandleFile = new TCollection_HAsciiString(outdir),
                                      ancestorName;
     api = CPPJini_LoadTemplate(edlsfullpath,outdir);
     api -> AddVariable ( "%CPPJiniEXTDBMS", Mode );
     
     aHandleFile = new TCollection_HAsciiString(outdir);
     
     aHandleFile->AssignCat(aName);
     aHandleFile->AssignCat(".java");
     api->Apply("%outClass","MemoryRootClientJAVA");
     outfile->Append(aHandleFile);
     
     // ...now we write the result
     //
     api->OpenFile("HTFile",aHandleFile->ToCString());
     api->WriteFile("HTFile","%outClass");
     api->CloseFile("HTFile");
   }
  else if (aName->IsSameString(MS::GetStorableRootName())) {
    Handle(TCollection_HAsciiString) aHandleFile = new TCollection_HAsciiString(outdir);

    api = CPPJini_LoadTemplate(edlsfullpath,outdir);
    api -> AddVariable ( "%CPPJiniEXTDBMS", Mode );
    
    aHandleFile = new TCollection_HAsciiString(outdir);
    
    aHandleFile->AssignCat(aName);
    aHandleFile->AssignCat(".java");
    api->Apply("%outClass","StorableRootClientJAVA");
    outfile->Append(aHandleFile);

    // ...now we write the result
    //
    api->OpenFile("HTFile",aHandleFile->ToCString());
    api->WriteFile("HTFile","%outClass");
    api->CloseFile("HTFile");
  }
  // Extraction of Classes
  //
  else if (srcType->IsKind(STANDARD_TYPE(MS_StdClass)) && !srcType->IsKind(STANDARD_TYPE(MS_GenClass)) && !srcType->IsKind(STANDARD_TYPE(MS_InstClass))) {
    Handle(MS_StdClass) aClass = *((Handle(MS_StdClass)*)&srcType);
    
    if (aClass->IsGeneric()) {
      return;
    }

    Handle(MS_HSequenceOfMemberMet)  aSeqMet = new MS_HSequenceOfMemberMet;

    api = CPPJini_LoadTemplate(edlsfullpath,outdir);
    api -> AddVariable ( "%CPPJiniEXTDBMS", Mode );

    if (MustBeComplete == CPPJini_SEMICOMPLETE) {
      Standard_Integer i;
      
      for (i = 1; i <= SeqOfMemberMet->Length(); i++) {
	if (aName->IsSameString(SeqOfMemberMet->Value(i)->Class())) {
	  aSeqMet->Append(SeqOfMemberMet->Value(i));
	}
      }
    }

    // Transient classes
    //
    if (aClass->IsTransient()) {
      Handle(TCollection_HAsciiString) aHandleFile = new TCollection_HAsciiString(outdir);
      
      CPPJini_TransientClass(aMeta,api,aClass,outfile,MustBeComplete,aSeqMet);
    }
    // MPV classes
    //
    else {
      CPPJini_MPVClass(aMeta,api,aClass,outfile,MustBeComplete,aSeqMet);
    }
  }
  else if (srcType->IsKind(STANDARD_TYPE(MS_Enum))) {
    api = CPPJini_LoadTemplate(edlsfullpath,outdir);
    api -> AddVariable ( "%CPPJiniEXTDBMS", Mode );


    Handle(MS_Enum) theEnum = *((Handle(MS_Enum)*)&srcType);

    CPPJini_Enum(aMeta,api,theEnum,outfile);
  }
}

void CPPJini_Extract (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( TCollection_HAsciiString        )& aTypeName,
      const Handle( TColStd_HSequenceOfHAsciiString )& edlsfullpath,
      const Handle( TCollection_HAsciiString        )& outdir,
      const Handle( TColStd_HSequenceOfHAsciiString )& outfile,
      const Standard_CString                           Mode
     ) {  

 if (  aMeta -> IsDefined ( aTypeName ) ||
       aMeta -> IsPackage ( aTypeName )
 ) {

  CPPJini_ExtractionType theMode = CPPJini_COMPLETE;
    
  if (  strcmp ( Mode, "CPPJini_COMPLETE" ) == 0  )

   theMode = CPPJini_COMPLETE;

  else if (  strcmp ( Mode, "CPPJini_INCOMPLETE" ) == 0  )

   theMode = CPPJini_INCOMPLETE;

  else if (  strcmp ( Mode, "CPPJini_SEMICOMPLETE" ) == 0  )

   theMode = CPPJini_SEMICOMPLETE;

  else {

   ErrorMsg() << "CPPJini" << "Unknown extraction mode:" << Mode << endm;
   Standard_NoSuchObject :: Raise ();

  }  // end else

  s_MS   = aMeta;
  s_Type = theMode;

  Standard_Boolean                   fDuplicate;
  CPPJini_ExtractionType             type;
  Handle( TCollection_HAsciiString ) cltName;

  if (  CPPJini_Defined (
         aTypeName, cltName, fDuplicate, type
        )
  ) {

   if ( theMode == CPPJini_COMPLETE     && type == CPPJini_COMPLETE ||
        theMode == CPPJini_INCOMPLETE                               ||
        theMode == CPPJini_SEMICOMPLETE && type == CPPJini_COMPLETE
   ) {     

    InfoMsg() << "CPPJini" << "Skipping " << aTypeName
            << " (already defined in " << cltName << ")" << endm;

    g_SkipMap.Bind ( aTypeName, cltName );
#if 0
    if ( fDuplicate )

     WarningMsg() << "CPPJini"
                << aTypeName
                << " defined in more than one client declared in 'uses' statement"
                << endm;
#endif
    return;

   }  // end if

  }  // end if

  g_ImportMap.Clear ();

  CPPJini_TypeExtract (
   aMeta, aTypeName, edlsfullpath, outdir, outfile, theMode, Mode
  );

 } else {

  ErrorMsg() << "CPPJini" << "Type " << aTypeName << " not defined..." << endm;
  Standard_NoSuchObject :: Raise ();

 }  // end else

}  // end CPPJini_Extract

Handle( TCollection_HAsciiString ) CPPJini_DotReplace ( char* str, char c ) {

 Handle( TCollection_HAsciiString ) retVal = new TCollection_HAsciiString ( str );

 retVal -> ChangeAll ( '.', c );

 return retVal;

}  // end CPPJini_DotReplace

