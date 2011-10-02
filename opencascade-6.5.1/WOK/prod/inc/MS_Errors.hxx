
// SCCS        Date: 10/23/91
//             Information: @(#)MS_Errors.hxx	1.1
// File: MS_Errors.hxx
//

#define VERBOSE 0
#define WARNING 1
#define ERROR   2
#define FATAL   3


#ifndef MS_Errors_HEADER_
#define MS_Errors_HEADER_

#include <TCollection_HAsciiString.hxx>
#include <MS.hxx>

void MS_Errors(Standard_Integer, Standard_CString, Standard_Integer = 0);

static   Handle(TCollection_HAsciiString) MsgErr;

//============================================================================
inline Handle(TCollection_HAsciiString)& operator+(const Handle(TCollection_HAsciiString)& S1
					   ,const Handle(TCollection_HAsciiString)& S2
					   )
{
  //==== If the S1 is not the same as MsgErr, we initialize MsgErr.
  if(MsgErr.IsNull())
    MsgErr = new TCollection_HAsciiString("");

  if (S1 != MsgErr){
    MsgErr->Remove(1,MsgErr->Length());
    MsgErr->AssignCat(S1);
  }

  MsgErr->AssignCat(S2);
  return(MsgErr);
}

//============================================================================

inline Handle(TCollection_HAsciiString)& operator+(const Handle(TCollection_HAsciiString)& S1
					   ,const Standard_CString S2
					   )
{
  //==== If the S1 is not the same as MsgErr, we initialize MsgErr.
  if(MsgErr.IsNull())
    MsgErr = new TCollection_HAsciiString("");

  if (S1 != MsgErr){
    MsgErr->Remove(1,MsgErr->Length());
    MsgErr->AssignCat(S1);
  }

  MsgErr->AssignCat(new TCollection_HAsciiString(S2));

  return(MsgErr);
}


//============================================================================
inline void MS_Errors(Standard_Integer Severity, 
		       Handle(TCollection_HAsciiString)& S1, 
		       Standard_Integer Code = 0)
{
  char Msg[256];

  Standard_Integer size = S1->Length();

  for(int i=0; i< size && i < 256; i++)
    Msg[i] = S1->Value(i+1);

  Msg[i] = 0;
  MS_Errors(Severity, Msg, Code);
  S1->Remove(1,size);
}

#endif 




