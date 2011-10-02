#define CPPJini_CREATE_EMPTY_JAVA_CONSTRUCTOR 1

#include <EDL_API.hxx>

#include <MS.hxx>
#include <MS_Enum.hxx>
#include <MS_Param.hxx>
#include <MS_Field.hxx>
#include <MS_Class.hxx>
#include <MS_Error.hxx>
#include <MS_InstMet.hxx>
#include <MS_Package.hxx>
#include <MS_GenType.hxx>
#include <MS_ClassMet.hxx>
#include <MS_Imported.hxx>
#include <MS_PrimType.hxx>
#include <MS_Construc.hxx>
#include <MS_GenClass.hxx>
#include <MS_MemberMet.hxx>
#include <MS_ExternMet.hxx>
#include <MS_InstClass.hxx>
#include <MS_MetaSchema.hxx>
#include <MS_HSequenceOfParam.hxx>
#include <MS_HSequenceOfField.hxx>
#include <MS_HSequenceOfGenType.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_HSequenceOfExternMet.hxx>

#include <Standard_NoSuchObject.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_Array1OfInteger.hxx>
#include <TColStd_HSequenceOfInteger.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <CPPJini_Define.hxx>
#include <CPPJini_ClientInfo.hxx>
#include <CPPJini_ExtractionType.hxx>
#include <TColStd_DataMapOfAsciiStringInteger.hxx>

extern Standard_Boolean CPPJini_HasComplete (
                         const Handle( TCollection_HAsciiString )&,
                         Handle( TCollection_HAsciiString       )&,
                         Standard_Boolean&
                        );
extern Standard_Boolean CPPJini_HasIncomplete (
                         const Handle( TCollection_HAsciiString )&,
                         Handle( TCollection_HAsciiString       )&,
                         Standard_Boolean&
                        );
extern Standard_Boolean CPPJini_HasSemicomplete (
                         const Handle( TCollection_HAsciiString )&,
                         Handle( TCollection_HAsciiString       )&,
                         Standard_Boolean&
                        );
extern Standard_Boolean CPPJini_Defined (
                         const Handle( TCollection_HAsciiString )&,
                         Handle( TCollection_HAsciiString       )&
                        );
extern Standard_Boolean CPPJini_Defined (
                         const Handle( TCollection_HAsciiString )&,
                         Handle( TCollection_HAsciiString       )&,
                         Standard_Boolean&,
                         CPPJini_ExtractionType&
                        );
extern void             CPPJini_MethodUsedTypes (
                         const Handle( MS_MetaSchema                   )& aMeta,
                         const Handle( MS_Method                       )& aMethod,
                         const Handle( TColStd_HSequenceOfHAsciiString )& List,
                         const Handle( TColStd_HSequenceOfHAsciiString )& Incp
                        );

extern WOKTools_MapOfHAsciiString   g_ImportMap;
extern Handle( CPPJini_ClientInfo ) g_Client;

void CPPJini_AddImport (
      const Handle( EDL_API                  )&,
      const Handle( TCollection_HAsciiString )&,
      const Handle( TCollection_HAsciiString )&
     );

void CPPJini_TransientDerivated (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( EDL_API                         )& api,
      const Handle( MS_Class                        )& aClass,			    
      const Handle( TColStd_HSequenceOfHAsciiString )& outfile,
      const Handle( TColStd_HSequenceOfHAsciiString )& inclist,
      const Handle( TColStd_HSequenceOfHAsciiString )& supplement,
      const CPPJini_ExtractionType                     MustBeComplete
     ) {

 Handle( TCollection_HAsciiString ) publics = new TCollection_HAsciiString ();
 Standard_Integer                   i;
  
 api -> AddVariable (  "%Class", aClass -> FullName () -> ToCString ()  );

 if ( MustBeComplete != CPPJini_INCOMPLETE ) {

  for (  i = 1; i <= inclist -> Length (); ++i  )

   if (   !inclist -> Value ( i ) -> IsSameString (  aClass -> FullName ()  )   ) {

    api -> AddVariable (  "%IClass", inclist -> Value ( i ) -> ToCString ()  );
    api -> Apply ( "%Includes", "IncludeCPlus" );
    publics -> AssignCat (  api -> GetVariableValue ( "%Includes" )  );

   }  // end if

 }  // end if

 api -> AddVariable (  "%Includes", publics -> ToCString ()  );
 publics -> Clear ();

 if ( MustBeComplete != CPPJini_INCOMPLETE )

  for (  i = 1; i <= supplement -> Length (); ++i  )

   publics -> AssignCat (  supplement -> Value ( i )  );


 api -> AddVariable (  "%Methods", publics -> ToCString ()  );
 publics -> Clear ();

 if (   aClass -> FullName () -> IsSameString (  MS :: GetTransientRootName ()  )   )

  api -> AddVariable (
          "%Inherits",
          CPPJini_GetFullJavaType (  CPPJini_TransientRootName ()  ) -> ToCString ()
         );

 else {

  Handle( TCollection_HAsciiString ) aClt;

  if (  CPPJini_Defined (  aClass -> FullName (), aClt  )  )

   CPPJini_AddImport (  api, aClt, aClass -> FullName ()  );

  else if (  aClass -> GetInheritsNames () -> Length () &&
             CPPJini_Defined (
              aClass -> GetInheritsNames () -> Value ( 1 ), aClt
             )
       )

   CPPJini_AddImport ( api, aClt, aClass -> GetInheritsNames () -> Value ( 1 )  );

  else
 
   api -> AddVariable (
           "%Inherits",
           CPPJini_GetFullJavaType (
            aClass -> GetInheritsNames () -> Value ( 1 )
           ) -> ToCString ()
          );

 }  // end else

 api -> AddVariable (  "%Class", aClass -> FullName () -> ToCString ()  );

 Handle( TCollection_HAsciiString ) iName = api -> GetVariableValue ( "%Interface" );

 iName -> ChangeAll ( '.', '_' );

 api -> AddVariable (  "%IncludeInterface", iName -> ToCString ()  );

 api -> Apply ( "%outClass", "TransientClassClientCXX" );

 Handle( TCollection_HAsciiString ) aFile =
  new TCollection_HAsciiString (  api -> GetVariableValue ( "%FullPath" )  );
  
 aFile -> AssignCat ( CPPJini_InterfaceName );
 aFile -> AssignCat ( "_" );
 aFile -> AssignCat (  aClass -> FullName ()  );
 aFile -> AssignCat ( "_java.cxx" );
  
 CPPJini_WriteFile ( api, aFile, "%outClass" );
  
 outfile -> Append ( aFile );

}  // end CPPJini_TransientDerivated

void CPPJini_TransientClass (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( EDL_API                         )& api,
      const Handle( MS_Class                        )& aClass,
      const Handle( TColStd_HSequenceOfHAsciiString )& outfile,
      const CPPJini_ExtractionType                     MustBeComplete,
      const Handle( MS_HSequenceOfMemberMet         )& theMetSeq
     ) {

 Handle( MS_StdClass ) theClass = Handle( MS_StdClass ) :: DownCast ( aClass );

 if (  !theClass.IsNull ()  ) {

  Standard_Integer                          i;
  Standard_Boolean                          defConst   = Standard_False;
  Handle( MS_HSequenceOfMemberMet         ) methods;
  Handle( TCollection_HAsciiString        ) publics    = new TCollection_HAsciiString ();
  Handle( TCollection_HAsciiString        ) members    = new TCollection_HAsciiString ();
  Handle( TCollection_HAsciiString        ) SuppMethod = new TCollection_HAsciiString ();
  Handle( TCollection_HAsciiString        ) jType;
  Handle( TColStd_HSequenceOfHAsciiString ) Supplement = new TColStd_HSequenceOfHAsciiString ();
  Handle( TColStd_HSequenceOfHAsciiString ) List       = new TColStd_HSequenceOfHAsciiString ();
  Handle( TColStd_HSequenceOfHAsciiString ) incp       = new TColStd_HSequenceOfHAsciiString ();

  if (   theClass -> FullName () -> IsSameString (  MS :: GetTransientRootName ()  )   ) return;

  api -> AddVariable (  "%Class", theClass -> FullName () -> ToCString ()  );

  if ( MustBeComplete == CPPJini_SEMICOMPLETE )

   methods = theMetSeq;

  else if ( MustBeComplete == CPPJini_COMPLETE )

   methods = theClass -> GetMethods ();
#if CPPJini_CREATE_EMPTY_JAVA_CONSTRUCTOR
  Standard_Boolean mustCreateEmptyConst =
   !CPPJini_HaveEmptyConstructor (  aMeta, theClass -> FullName (), methods );
#endif  // CPPJini_CREATE_EMPTY_JAVA_CONSTRUCTOR
  if ( MustBeComplete != CPPJini_INCOMPLETE )

   if (  methods -> Length () > 0  )  {

    TColStd_DataMapOfAsciiStringInteger mapnames;
    TColStd_Array1OfInteger             theindexes (  1, methods -> Length ()  );

    theindexes.Init ( 0 );
	
	for (  i = 1; i <= methods -> Length (); ++i  )

     CPPJini_CheckMethod (  i, methods -> Value ( i ) -> Name (), mapnames, theindexes  );
	
	for (  i = 1; i <= methods -> Length (); ++i  ) {

     CPPJini_BuildMethod (
      aMeta, api, theClass -> FullName (), methods -> Value ( i ),
      methods -> Value ( i ) -> Name (), theindexes ( i )
     );

     if (  !api -> GetVariableValue ( "%Method" ) -> IsSameString ( CPPJini_ErrorArgument )  ) {

      Standard_Boolean cond = theClass -> Deferred () &&
                              methods -> Value ( i ) -> IsKind (  STANDARD_TYPE( MS_Construc )  );

      if ( !defConst && cond ) defConst = Standard_True;

      if (  !(  cond || methods -> Value ( i ) -> IsProtected ()
                     || methods -> Value ( i ) -> Private     ()
             )
      ) {

       CPPJini_MethodUsedTypes (  aMeta, methods -> Value ( i ), List, incp  );

       members -> AssignCat (  api -> GetVariableValue ( VJMethod )  );

       CPPJini_MethodBuilder (
        aMeta, api, aClass -> FullName (), methods -> Value ( i ),
        methods -> Value ( i ) -> Name (), theindexes ( i )
       );

       Supplement -> Append (  api -> GetVariableValue ( VJMethod )  );

      }  // end if

     }  // end if

    }  // end for

   }  // end if
#if CPPJini_CREATE_EMPTY_JAVA_CONSTRUCTOR
  if (  mustCreateEmptyConst || ( defConst && !mustCreateEmptyConst )  ) {

   api -> Apply ( VJMethod, "EmptyConstructorHeader" );
   members -> AssignCat (  api -> GetVariableValue ( VJMethod )  );

  }  // end if
#endif  // end CPPJini_CREATE_EMPTY_JAVA_CONSTRUCTOR
  if ( MustBeComplete != CPPJini_INCOMPLETE ) {
      
   for (  i = 1; i <= List -> Length (); ++i  )

    if (   !List -> Value ( i ) -> IsSameString (  theClass -> FullName ()  )   ) {

     api -> AddVariable (  "%IClass", List -> Value ( i ) -> ToCString ()  );

     if (   CPPJini_IsCasType (  List -> Value ( i )  )   )

      api -> Apply ( "%Includes", "IncludeJCas" );

     else {

      Handle( TCollection_HAsciiString ) aClt;
      Standard_Boolean                   fPush = Standard_False;

      if (   CPPJini_Defined (  List -> Value ( i ), aClt  )   ) {

       fPush = Standard_True;
       api -> AddVariable (  "%Interface", aClt -> ToCString ()  );

      }  // end if

      api -> Apply ( "%Includes", "Include" );

      if ( fPush ) api -> AddVariable (
                           "%Interface", CPPJini_InterfaceName -> ToCString ()
                          );
     }  // end else

     jType = api -> GetVariableValue ( "%Includes" );

     if (  !g_ImportMap.Contains ( jType )  ) {

      publics -> AssignCat ( jType );
      g_ImportMap.Add ( jType );

     }  // end if

    }  // end if
      
   for (  i = 1; i <= incp -> Length (); ++i  )

    if (   !incp -> Value ( i ) -> IsSameString (  theClass -> FullName ()  )   ) {

     api -> AddVariable (  "%IClass", incp -> Value ( i ) -> ToCString ()  );

     if (   CPPJini_IsCasType (  incp -> Value ( i )  )   )

      api -> Apply ( "%Includes", "ShortDecJCas" );

     else {

      Handle( TCollection_HAsciiString ) aClt;
      Standard_Boolean                   fPush = Standard_False;

      if (   CPPJini_Defined (  incp -> Value ( i ), aClt  )   ) {

       fPush = Standard_True;
       api -> AddVariable (  "%Interface", aClt -> ToCString ()  );

      }  // end if

      api -> Apply ( "%Includes", "ShortDec" );

      if ( fPush )

       api -> AddVariable (  "%Interface", CPPJini_InterfaceName -> ToCString ()  );

     }  // end else

     jType = api -> GetVariableValue ( "%Includes" );

     if (  !g_ImportMap.Contains ( jType )  ) {

      publics -> AssignCat ( jType );
      g_ImportMap.Add ( jType );

     }  // end if

    }  // end if

  }  // end if

  api -> AddVariable (  "%Includes", publics -> ToCString ()  );

  Handle( TCollection_HAsciiString ) aClt;
  Handle( TCollection_HAsciiString ) anAncestor = aClass -> GetInheritsNames () -> Value ( 1 );
  Standard_Boolean                   fDup;
  CPPJini_ExtractionType             thisType, otherType;

  g_Client -> Defined ( anAncestor, thisType );

  if (  CPPJini_Defined ( anAncestor, aClt, fDup, otherType )  ) {

   if ( thisType == CPPJini_COMPLETE     && otherType == CPPJini_COMPLETE ||
        thisType == CPPJini_INCOMPLETE                                    ||
        thisType == CPPJini_SEMICOMPLETE && otherType == CPPJini_COMPLETE
   ) {  // this type has been skipped in this client, use one from other client

    CPPJini_AddImport ( api, aClt, anAncestor );

   } else {

    CPPJini_AddImport (  api, g_Client -> Name (), anAncestor  );

   }  // end else

  } else {

   api -> AddVariable (
           "%Inherits",
           CPPJini_GetFullJavaType (
            theClass -> GetInheritsNames () -> Value ( 1 )
           ) -> ToCString ()
          );

  }  // end else

  if (  CPPJini_Defined ( theClass -> FullName (), aClt )  ) {  // create additional
                                                                //  constructors
    Handle( TCollection_HAsciiString ) aShortClt =
     new TCollection_HAsciiString ( aClt );

    aShortClt -> RemoveAll ( '.' );
    api -> AddVariable (  "%ShortPackName", aShortClt             -> ToCString ()  );
    api -> AddVariable (  "%PackName",      aClt                  -> ToCString ()  );
    api -> AddVariable (  "%PrevClassName", aClass -> FullName () -> ToCString ()  );

    Handle( TCollection_HAsciiString ) shortName =
     new TCollection_HAsciiString (  aClass -> FullName ()  );

    shortName -> RemoveAll ( '_' );

    api -> AddVariable (  "%ShortClassName", shortName -> ToCString ()  );

    api -> Apply ( "%thePrevious", "ThePrevious" );
    api -> Apply ( "%setPrevious", "SetPrevious" );
    api -> Apply ( "%getPrevious", "GetPrevious" );
    members -> AssignCat (  api -> GetVariableValue ( "%thePrevious" )  );
    members -> AssignCat (  api -> GetVariableValue ( "%setPrevious" )  );
    members -> AssignCat (  api -> GetVariableValue ( "%getPrevious" )  );

  }  // end if

  api -> AddVariable (  "%Methods", members -> ToCString ()  );
  api -> AddVariable (  "%Class", theClass -> FullName () -> ToCString ()  );
  api -> Apply ( "%outClass", "TransientClassClientJAVA" );

  Handle( TCollection_HAsciiString ) aFile =
   new TCollection_HAsciiString (  api -> GetVariableValue ( "%FullPath" )  );

  aFile -> AssignCat (  theClass -> FullName ()  );
  aFile -> AssignCat ( ".java" );

  CPPJini_WriteFile ( api, aFile, "%outClass" );

  outfile -> Append ( aFile );

  CPPJini_TransientDerivated ( aMeta, api, aClass, outfile, incp, Supplement, MustBeComplete );

 } else {

  ErrorMsg() << "CPPJini" << "CPPJini_TransientClass - the class is NULL..." << endm;
  Standard_NoSuchObject :: Raise ();

 }  // end else

}  // end CPPJini_TransientClass

void CPPJini_AddImport (
      const Handle( EDL_API                  )& api,
      const Handle( TCollection_HAsciiString )& aClt,
      const Handle( TCollection_HAsciiString )& name
     ) {

 Handle( TCollection_HAsciiString ) clt = new TCollection_HAsciiString ( aClt );

 clt -> AssignCat ( "."  );
 clt -> AssignCat ( name );
 api -> AddVariable (  "%Inherits", clt -> ToCString ()  );

}  // end CPPJini_AddImport
