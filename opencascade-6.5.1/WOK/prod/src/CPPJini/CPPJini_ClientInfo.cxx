#include <CPPJini_ClientInfo.ixx>

#include <MS_Client.hxx>
#include <MS_HSequenceOfExternMet.hxx>
#include <MS_HSequenceOfMemberMet.hxx>

#include <WOKTools_Messages.hxx>

CPPJini_ClientInfo :: CPPJini_ClientInfo (
                       const Handle( MS_MetaSchema            )& aMS,
                       const Handle( TCollection_HAsciiString )& aName,
                       const Standard_Integer                    aLevel
                      ) {

 Handle( MS_Client ) clt = aMS -> GetClient ( aName );

 if (  clt.IsNull ()  )

  ErrorMsg() << "CPPJini" << "Client " << aName << " was not found" << endm;

 else {

  Handle( MS_HSequenceOfExternMet ) xtern = new MS_HSequenceOfExternMet ();
  Handle( MS_HSequenceOfMemberMet ) membr = new MS_HSequenceOfMemberMet ();

  clt -> ComputeTypes (
          xtern, membr, myComplete, myIncomplete, mySemicomplete
         );

  InfoMsg() << "CPPJini" << "Using client: " << aName << endm;

 }  // end else

 myName  = new TCollection_HAsciiString ( aName );
 myLevel = aLevel;
 myRoot  = Standard_False;

}  // end CPPJini_ClientInfo :: CPPJini_ClientInfo

Standard_Boolean CPPJini_ClientInfo :: HasComplete (
                                        const Handle( TCollection_HAsciiString )& aName
                                       ) const {

 return myComplete.Contains ( aName );

}  // end CPPJini_ClientInfo :: HasComplete

Standard_Boolean CPPJini_ClientInfo :: HasIncomplete (
                                        const Handle( TCollection_HAsciiString )& aName
                                       ) const {

 return myIncomplete.Contains ( aName );

}  // end CPPJini_ClientInfo :: HasIncomplete

Standard_Boolean CPPJini_ClientInfo :: HasSemicomplete (
                                        const Handle( TCollection_HAsciiString )& aName
                                       ) const {

 return mySemicomplete.Contains ( aName );

}  // end CPPJini_ClientInfo :: HasSemicomplete

Standard_Boolean CPPJini_ClientInfo :: Defined (
                                        const Handle( TCollection_HAsciiString )& aName,
                                              CPPJini_ExtractionType&             aType
                                       ) const {

 Standard_Boolean retVal = Standard_False;

 if (  myComplete.Contains ( aName )  ) {

  aType  = CPPJini_COMPLETE;
  retVal = Standard_True;

 } else if (  myIncomplete.Contains ( aName )  ) {

  aType  = CPPJini_INCOMPLETE;
  retVal = Standard_True;

 } else if (  mySemicomplete.Contains ( aName )  ) {

  aType  = CPPJini_SEMICOMPLETE;
  retVal = Standard_True;

 }  // end if

 return retVal;

}  // end CPPJini_ClientInfo :: Defined

const Handle( TCollection_HAsciiString )& CPPJini_ClientInfo :: Name () const {

 return myName;

}  // end CPPJini_ClientInfo :: Name


