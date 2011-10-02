// File modified by JGA for Visual C++ 5.0 support

#ifndef _WOKUtils_Trigger_HeaderFile
#define _WOKUtils_Trigger_HeaderFile

#include <Handle_WOKUtils_Path.hxx>
#include <Handle_TCollection_HAsciiString.hxx>
#include <WOKTools_InterpFileType.hxx>
#include <WOKTools_Return.hxx>
#include <Standard_Integer.hxx>
#include <WOKUtils_TriggerStatus.hxx>
#include <WOKUtils_TriggerHandler.hxx>
#include <Standard_CString.hxx>
#include <Standard_Boolean.hxx>
#include <WOKUtils_TriggerControl.hxx>
class WOKUtils_Path;
class TCollection_HAsciiString;
class WOKUtils_Param;
class WOKTools_Return;


#include <Standard_Macro.hxx>

class WOKUtils_Trigger  {

public:

  // Methods PUBLIC
  // 
  Standard_EXPORT WOKUtils_Trigger();
  Standard_EXPORT static  void SetTriggerHandler(const WOKUtils_TriggerHandler ahandler) ;
  Standard_EXPORT static  WOKUtils_TriggerHandler& TriggerHandler() ;
  Standard_EXPORT   WOKUtils_Trigger& SetName(const Standard_CString aname) ;
  WOKUtils_Trigger& operator ()(const Standard_CString aname) 
    {
      return SetName(aname);
    }

  Standard_EXPORT   WOKUtils_Trigger& SetName(const Handle(TCollection_HAsciiString)& aname) ;
  WOKUtils_Trigger& operator ()(const Handle(TCollection_HAsciiString)& aname) 
    {
      return SetName(aname);
    }

  Standard_EXPORT   Handle_TCollection_HAsciiString Name() const;
  Standard_EXPORT   WOKUtils_Trigger& AddFile(const Handle(TCollection_HAsciiString)& afile,const WOKUtils_Param& params,const WOKTools_InterpFileType type = WOKTools_TclInterp) ;
  WOKUtils_Trigger& operator ()(const Handle(TCollection_HAsciiString)& afile,const WOKUtils_Param& params,const WOKTools_InterpFileType type) 
    {
      return AddFile(afile,params,type);
    }

  Standard_EXPORT   WOKUtils_Trigger& AddFile(const Standard_CString afile,const WOKUtils_Param& params,const WOKTools_InterpFileType type = WOKTools_TclInterp) ;
  WOKUtils_Trigger& operator ()(const Standard_CString afile,const WOKUtils_Param& params,const WOKTools_InterpFileType type) 
    {
      return AddFile(afile,params,type);
    }

  Standard_EXPORT   WOKUtils_Trigger& AddArg(const Handle(TCollection_HAsciiString)& anarg) ;
  WOKUtils_Trigger& operator <<(const Handle(TCollection_HAsciiString)& anarg) 
    {
      return AddArg(anarg);
    }

  Standard_EXPORT   WOKUtils_Trigger& AddArg(const Standard_CString anarg) ;
  WOKUtils_Trigger& operator <<(const Standard_CString anarg) 
    {
      return AddArg(anarg);
    }

  Standard_EXPORT   WOKUtils_Trigger& AddArg(const Standard_Boolean anarg) ;
  WOKUtils_Trigger& operator <<(const Standard_Boolean anarg) 
    {
      return AddArg(anarg);
    }

  Standard_EXPORT   WOKUtils_Trigger& AddArg(const Standard_Integer anarg) ;
  WOKUtils_Trigger& operator <<(const Standard_Integer anarg) 
    {
      return AddArg(anarg);
    }

  Standard_EXPORT   WOKUtils_Trigger& AddControl(const WOKUtils_TriggerControl anctrl) ;
  WOKUtils_Trigger& operator <<(const WOKUtils_TriggerControl anctrl) 
    {
      return AddControl(anctrl);
    }

  Standard_EXPORT  const WOKTools_Return& Args() const;
  Standard_EXPORT   WOKUtils_TriggerStatus Execute() ;
  Standard_EXPORT   WOKUtils_Trigger& AddResult(const Handle(TCollection_HAsciiString)& aresult) ;
  Standard_EXPORT   WOKUtils_Trigger& AddResult(const Standard_CString aresult) ;
  Standard_EXPORT   WOKUtils_Trigger& AddResult(const Standard_Boolean aresult) ;
  Standard_EXPORT   WOKUtils_Trigger& AddResult(const Standard_Integer aresult) ;
  Standard_EXPORT   WOKUtils_Trigger& GetResult(Handle(TCollection_HAsciiString)& aresult) ;
  WOKUtils_Trigger& operator >>(Handle(TCollection_HAsciiString)& aresult) 
    {
      return GetResult(aresult);
    }

  Standard_EXPORT   WOKUtils_Trigger& GetResult(Standard_Boolean& aresult) ;
  WOKUtils_Trigger& operator >>(Standard_Boolean& aresult) 
    {
      return GetResult(aresult);
    }

  Standard_EXPORT   WOKUtils_Trigger& GetResult(Standard_Integer& aresult) ;
  WOKUtils_Trigger& operator >>(Standard_Integer& aresult) 
    {
      return GetResult(aresult);
    }

  Standard_EXPORT  const WOKTools_Return& Return() const;
  Standard_EXPORT   WOKTools_Return& ChangeReturn() ;
  Standard_EXPORT   WOKUtils_TriggerStatus Status() const;

private: 

  // Fields PRIVATE
  //
  Handle_WOKUtils_Path myfile;
  Handle_TCollection_HAsciiString myname;
  WOKTools_InterpFileType mytype;
  WOKTools_Return myargs;
  WOKTools_Return myrets;
  Standard_Integer myidx;
  WOKUtils_TriggerStatus mystat;


};

#endif
