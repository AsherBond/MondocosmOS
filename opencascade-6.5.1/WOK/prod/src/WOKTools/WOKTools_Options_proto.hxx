// File modified by JGA for Visual C+++ 5.0

#ifndef _WOKTools_Options_HeaderFile
#define _WOKTools_Options_HeaderFile

#include <WOKTools_PUsage.hxx>
#include <Handle_TCollection_HAsciiString.hxx>
#include <Standard_Character.hxx>
#include <Standard_Integer.hxx>
#include <WOKTools_ArgTable.hxx>
#include <Handle_WOKTools_HSequenceOfDefine.hxx>
#include <Standard_Boolean.hxx>
#include <Handle_TColStd_HSequenceOfHAsciiString.hxx>
#include <Standard_CString.hxx>
class TCollection_HAsciiString;
class WOKTools_HSequenceOfDefine;
class TColStd_HSequenceOfHAsciiString;


#include <Standard_Macro.hxx>

class WOKTools_Options  {

public:
  // Methods PUBLIC
  // 
  Standard_EXPORT WOKTools_Options(const Standard_Integer argc,const WOKTools_ArgTable& argv,const Standard_CString opts,const WOKTools_PUsage usage,const Standard_CString excl = " ");
  Standard_EXPORT   void Next() ;
  Standard_EXPORT   Standard_Character Option() const;
  Standard_EXPORT   Handle_TCollection_HAsciiString OptionArgument() const;
  Standard_EXPORT   Handle_TColStd_HSequenceOfHAsciiString OptionListArgument() const;
  Standard_EXPORT   Standard_Boolean More() const;
  Standard_EXPORT   void Define(const Handle(TCollection_HAsciiString)& aname,const Handle(TCollection_HAsciiString)& avalue) ;
  Standard_EXPORT   Handle_WOKTools_HSequenceOfDefine Defines() const;
  Standard_EXPORT   void AddPrefixToDefines(const Handle(TCollection_HAsciiString)& aname) ;
  Standard_EXPORT   Handle_TColStd_HSequenceOfHAsciiString Arguments() const;
  Standard_EXPORT   Standard_Boolean Failed() const;

private: 

  // Fields PRIVATE
  //
  WOKTools_PUsage myusage;
  Handle_TCollection_HAsciiString myoptions;
  Handle_TCollection_HAsciiString myexclopt;
  Standard_Character myexclflg;
  Standard_Integer myargc;
  WOKTools_ArgTable myargv;
  Handle_WOKTools_HSequenceOfDefine mydefines;
  Standard_Byte mycuropt;
  Standard_Boolean mymore;
  Handle_TCollection_HAsciiString mycurarg;
  Handle_TColStd_HSequenceOfHAsciiString mycurlistarg;
  Handle_TColStd_HSequenceOfHAsciiString myargs;
  Standard_Boolean myerrflg;
};





// other Inline functions and methods (like "C++: function call" methods)
//


#endif
