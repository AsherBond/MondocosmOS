#include <EDL_API.hxx>

#include <MS.hxx>
#include <MS_Enum.hxx>
#include <MS_Class.hxx>
#include <MS_Error.hxx>
#include <MS_Param.hxx>
#include <MS_Field.hxx>
#include <MS_Package.hxx>
#include <MS_InstMet.hxx>
#include <MS_GenType.hxx>
#include <MS_Imported.hxx>
#include <MS_ClassMet.hxx>
#include <MS_Construc.hxx>
#include <MS_GenClass.hxx>
#include <MS_PrimType.hxx>
#include <MS_InstClass.hxx>
#include <MS_ExternMet.hxx>
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
#include <TColStd_DataMapOfAsciiStringInteger.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_DataMapOfHAsciiStringOfHAsciiString.hxx>

#include <CPPJini_Define.hxx>
#include <CPPJini_ExtractionType.hxx>

extern WOKTools_MapOfHAsciiString                   g_ImportMap;
extern WOKTools_DataMapOfHAsciiStringOfHAsciiString g_SkipMap;


extern Standard_Boolean CPPJini_Defined (
                         const Handle( TCollection_HAsciiString )&,
                         Handle( TCollection_HAsciiString       )&
                        );

extern void CPPJini_MethodUsedTypes (
             const Handle( MS_MetaSchema                   )&,
             const Handle( MS_Method                       )&,
             const Handle( TColStd_HSequenceOfHAsciiString )&,
             const Handle( TColStd_HSequenceOfHAsciiString )&
            );

void CPPJini_PackageDerivated (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( EDL_API                         )& api,
      const Handle( MS_Package                      )& aPackage,			    
      const Handle( TColStd_HSequenceOfHAsciiString )& outfile,
      const Handle( TColStd_HSequenceOfHAsciiString )& inclist,
      const Handle( TColStd_HSequenceOfHAsciiString )& supplement
     ) {

 Handle( TCollection_HAsciiString ) publics = new TCollection_HAsciiString ();
 Standard_Integer                   i;

 api -> AddVariable (  "%Class", aPackage -> Name () -> ToCString ()  );

 for (  i = 1; i <= inclist -> Length (); ++i  )

  if (   !inclist -> Value ( i ) -> IsSameString (  aPackage -> Name ()  )   ) {

   api -> AddVariable (  "%IClass", inclist -> Value ( i ) -> ToCString ()  );
   api -> Apply ( "%Includes", "IncludeCPlus" );
   publics -> AssignCat (  api -> GetVariableValue ( "%Includes" )  );

  }  // end if
  
 api -> AddVariable (  "%Includes", publics -> ToCString ()  );
 publics -> Clear ();

 for (  i = 1; i <= supplement -> Length (); ++i )

  publics -> AssignCat (  supplement -> Value ( i )  );

 api -> AddVariable (  "%Methods", publics -> ToCString ()  );
 publics -> Clear ();

 api -> AddVariable (  "%Class",aPackage -> Name () -> ToCString ()  );

 Handle( TCollection_HAsciiString ) iName = api -> GetVariableValue ( "%Interface" );

 iName -> ChangeAll ( '.', '_' );

 api -> AddVariable (  "%IncludeInterface", iName -> ToCString ()  );
 api -> Apply ( "%outClass", "PackageClientCXX" );

 Handle( TCollection_HAsciiString ) aFile =
  new TCollection_HAsciiString (  api -> GetVariableValue ( "%FullPath" )  );
  
 aFile -> AssignCat ( CPPJini_InterfaceName );
 aFile -> AssignCat ( "_" );
 aFile -> AssignCat (  aPackage -> Name ()  );
 aFile -> AssignCat ( "_java.cxx" );
  
 CPPJini_WriteFile ( api, aFile, "%outClass" );
  
 outfile -> Append ( aFile );

}  // end CPPJini_PackageDerivated

void CPPJini_Package (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( EDL_API                         )& api,
      const Handle( MS_Package                      )& aPackage,
      const Handle( TColStd_HSequenceOfHAsciiString )& outfile,
      const CPPJini_ExtractionType                     mustBeComplete,
      const Handle( MS_HSequenceOfExternMet         )& theMetSeq
     ) {

 if (  !aPackage.IsNull ()  ) {

  Standard_Integer                          i;
  Standard_Boolean                          fPush      = Standard_False;
  Handle( MS_HSequenceOfExternMet         ) methods;
  Handle( TCollection_HAsciiString        ) publics    = new TCollection_HAsciiString ();
  Handle( TCollection_HAsciiString        ) jType;
  Handle( TColStd_HSequenceOfHAsciiString ) Supplement = new TColStd_HSequenceOfHAsciiString ();
  Handle( TColStd_HSequenceOfHAsciiString ) List       = new TColStd_HSequenceOfHAsciiString ();
  Handle( TColStd_HSequenceOfHAsciiString ) incp       = new TColStd_HSequenceOfHAsciiString ();

  api -> AddVariable (  "%Class", aPackage -> Name () -> ToCString ()  );

  if ( mustBeComplete == CPPJini_SEMICOMPLETE )

   methods = theMetSeq;

  else if ( mustBeComplete == CPPJini_COMPLETE )

   methods = aPackage -> Methods ();

  if ( mustBeComplete != CPPJini_INCOMPLETE )

   if (  methods -> Length () > 0  )  {

    TColStd_DataMapOfAsciiStringInteger mapnames;	
    TColStd_Array1OfInteger             theindexes (  1, methods -> Length ()  );

    theindexes.Init ( 0 );	
	
    for (  i = 1; i <= methods -> Length (); ++i  )

     CPPJini_CheckMethod (  i, methods -> Value ( i ) -> Name (), mapnames, theindexes  );
	
	for (  i = 1; i <= methods -> Length (); ++i  ) {

     if (  methods -> Value ( i ) -> Private ()  ) continue;

     CPPJini_BuildMethod (
      aMeta, api, aPackage -> Name (),
      methods -> Value ( i ), methods -> Value ( i ) -> Name (), theindexes ( i )
     );

     if (  !api -> GetVariableValue ( "%Method" ) -> IsSameString ( CPPJini_ErrorArgument )  ) {
	    
      CPPJini_MethodUsedTypes (  aMeta, methods -> Value ( i ), List, incp );
      publics->AssignCat (  api -> GetVariableValue ( VJMethod )  );
      CPPJini_MethodBuilder (
       aMeta, api, aPackage -> Name (), methods -> Value ( i ),
       methods -> Value ( i ) -> Name (), theindexes ( i )
      );
      Supplement -> Append (  api -> GetVariableValue ( VJMethod )  );

     }  // end if

    }  // end for

   }  // end if

  api -> AddVariable (  "%Methods", publics -> ToCString ()  );

  publics -> Clear ();

  for (  i = 1; i <= List -> Length (); ++i  )

   if (   !List -> Value ( i ) -> IsSameString (  aPackage -> Name ()  )   ) {

    if (   g_SkipMap.IsBound (  List -> Value ( i )  )   ) {

     fPush = Standard_True;
     api -> AddVariable (
             "%Interface", g_SkipMap.Find (  List -> Value ( i )  ) -> ToCString ()
            );

    } else if (   CPPJini_Defined (  List -> Value ( i ), jType  )   ) {

     fPush = Standard_True;
     api -> AddVariable (
             "%Interface", jType -> ToCString ()
            );

    }  // end if

    api -> AddVariable (  "%IClass", List -> Value ( i ) -> ToCString ()  );

    if (   CPPJini_IsCasType (  List -> Value ( i )  )   )

     api -> Apply ( "%Includes", "IncludeJCas" );

	else api -> Apply ( "%Includes", "Include" );

    jType = api -> GetVariableValue ( "%Includes" );

    if (  !g_ImportMap.Contains ( jType )  ) {

     publics -> AssignCat ( jType );
     g_ImportMap.Add ( jType );

    }  // end if

    if ( fPush ) {

     api -> AddVariable (  "%Interface", CPPJini_InterfaceName -> ToCString ()  );
     fPush = Standard_False;

    }  // end if

   }  // end if

  fPush = Standard_False;

  for (  i = 1; i <= incp -> Length (); ++i  )

   if (   !incp -> Value ( i ) -> IsSameString (  aPackage -> Name ()  )   ) {

    if (   g_SkipMap.IsBound (  incp -> Value ( i )  )   ) {

     fPush = Standard_True;
     api -> AddVariable (
             "%Interface", g_SkipMap.Find (  incp -> Value ( i )  ) -> ToCString ()
            );

    } else if (   CPPJini_Defined (  List -> Value ( i ), jType  )   ) {

     fPush = Standard_True;
     api -> AddVariable (
             "%Interface", jType -> ToCString ()
            );

    }  // end if

    api -> AddVariable (  "%IClass", incp -> Value ( i ) -> ToCString ()  );

    if (   CPPJini_IsCasType (  incp -> Value ( i )  )   )

     api -> Apply ( "%Includes", "ShortDecJCas" );

    else api -> Apply ( "%Includes", "ShortDec" );

    jType = api -> GetVariableValue ( "%Includes" );

    if (  !g_ImportMap.Contains ( jType )  ) {

     publics -> AssignCat ( jType );
     g_ImportMap.Add ( jType );

    }  // end if

    if ( fPush ) {

     api -> AddVariable (  "%Interface", CPPJini_InterfaceName -> ToCString ()  );
     fPush = Standard_False;

    }  // end if

   }  // end if

  api -> AddVariable (  "%Includes", publics -> ToCString ()  );
  api -> Apply ( VJoutClass, "PackageClientJAVA" );
    
  Handle( TCollection_HAsciiString ) aFile =
   new TCollection_HAsciiString (  api -> GetVariableValue ( VJFullPath )  );

  aFile->AssignCat (  aPackage -> Name ()  );
  aFile->AssignCat ( ".java" );

  CPPJini_WriteFile ( api, aFile, VJoutClass );

  outfile -> Append ( aFile );

  CPPJini_PackageDerivated ( aMeta, api, aPackage, outfile, incp, Supplement );

 } else {

  ErrorMsg() << "CPPJini" << "CPPJini_Package - the package is NULL..." << endm;
  Standard_NoSuchObject :: Raise ();

 }  // end else

}  // end CPPJini_Package
