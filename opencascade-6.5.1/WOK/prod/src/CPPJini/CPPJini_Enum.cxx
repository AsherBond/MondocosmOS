#include <EDL_API.hxx>

#include <MS.hxx>
#include <MS_Enum.hxx>
#include <MS_Package.hxx>
#include <MS_MetaSchema.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <CPPJini_Define.hxx>

void CPPJini_Enum (
      const Handle( MS_MetaSchema                   )& aMeta,
      const Handle( EDL_API                         )& api,
      const Handle( MS_Enum                         )& anEnum,
      const Handle( TColStd_HSequenceOfHAsciiString )& outfile
     ) {

 if (  anEnum.IsNull ()  ) return;

 Handle( TColStd_HSequenceOfHAsciiString ) EnumVal = anEnum -> Enums ();
 Handle( TCollection_HAsciiString        ) result, aFileName;
 Standard_Integer                          i;

 result = new TCollection_HAsciiString (  EnumVal -> Length ()  );
  
 api -> AddVariable (  "%Class", anEnum -> FullName () -> ToCString ()  );
  
 result -> Clear ();

 for (  i = 1; i <= EnumVal -> Length (); ++i  ) {

  api -> AddVariable (  "%Value", EnumVal -> Value ( i ) -> ToCString ()  );
  Handle( TCollection_HAsciiString ) number = new TCollection_HAsciiString ( i - 1 );
  api -> AddVariable (  "%Number", number -> ToCString ()  );
  api -> Apply ( "%aValue","EnumValueDef" );
  result -> AssignCat (  api -> GetVariableValue ( "%aValue" )  );

 }  // end for

 api -> AddVariable (  "%Values", result -> ToCString ()  );
 api -> Apply ( "%outClass", "EnumJAVA" );
  
 aFileName = new TCollection_HAsciiString (  api -> GetVariableValue ( "%FullPath" )  );
 aFileName -> AssignCat (  anEnum -> FullName ()  );
 aFileName -> AssignCat ( ".java" );

 CPPJini_WriteFile ( api, aFileName, "%outClass" ); 

 outfile -> Append ( aFileName );

}  // end CPPJini_Enum
