// File Modifed by JGA for Visual C++ 5.0

#ifndef _WOKTools_Message_HeaderFile
#define _WOKTools_Message_HeaderFile

#include <Standard_CString.hxx>
#include <Handle_TCollection_HAsciiString.hxx>
#include <Standard_Integer.hxx>
#include <Standard_Boolean.hxx>
#include <WOKTools_MsgHandler.hxx>
#include <WOKTools_MsgStreamPtr.hxx>
#include <Standard_Character.hxx>
#include <WOKTools_MsgControl.hxx>
class TCollection_HAsciiString;


#include <Standard_Macro.hxx>

class WOKTools_Message  {

public:

  // Methods PUBLIC
  // 
  Standard_EXPORT   void Init() ;
  Standard_EXPORT virtual  Standard_Character Code() const = 0;
  Standard_EXPORT   WOKTools_Message& Print(const Standard_CString astr) ;
  WOKTools_Message& operator <<(const Standard_CString astr) 
    {
      return Print(astr);
    }

  Standard_EXPORT   WOKTools_Message& Print(const Handle(TCollection_HAsciiString)& astr) ;
  WOKTools_Message& operator <<(const Handle(TCollection_HAsciiString)& astr) 
    {
      return Print(astr);
    }

  Standard_EXPORT   WOKTools_Message& Print(const Standard_Integer aint) ;
  WOKTools_Message& operator <<(const Standard_Integer aint) 
    {
      return Print(aint);
    }

  Standard_EXPORT   WOKTools_Message& Print(const Standard_Character achar) ;
  WOKTools_Message& operator <<(const Standard_Character achar) 
    {
      return Print(achar);
    }

  Standard_EXPORT   WOKTools_Message& MsgControl(const WOKTools_MsgControl ahandler) ;
  WOKTools_Message& operator <<(const WOKTools_MsgControl ahandler) 
    {
      return MsgControl(ahandler);
    }

  Standard_EXPORT   void Set() ;
  Standard_EXPORT   void UnSet() ;
  Standard_EXPORT   void DoPrintHeader() ;
  Standard_EXPORT   void DontPrintHeader() ;
  Standard_EXPORT   void DoPrintContext() ;
  Standard_EXPORT   void DontPrintContext() ;
  Standard_EXPORT   void SetEndMsgHandler(const WOKTools_MsgHandler& ahandler) ;
  Standard_EXPORT   void SetIndex(const Standard_Integer anindex) ;
  Standard_EXPORT   Standard_Boolean LogToFile(const Handle(TCollection_HAsciiString)& afile) ;
  Standard_EXPORT   Standard_Boolean LogToStream(const WOKTools_MsgStreamPtr& astream) ;
  Standard_EXPORT   void EndLogging() ;

  // Inline methods
  WOKTools_MsgStreamPtr LogStream() const;
  Standard_Boolean IsSet() const;
  Standard_Boolean PrintHeader() const;
  Standard_Boolean PrintContext() const;
  WOKTools_MsgHandler EndMsgHandler() const;
  Standard_Integer Index() const;
  const Handle(TCollection_HAsciiString)& Message() const;
  Standard_CString ToPrint() const;

  virtual ~WOKTools_Message () {}

protected:

  // Methods PROTECTED
  // 
  Standard_EXPORT WOKTools_Message(const Standard_CString aclass,const Standard_CString aheader);
  Standard_CString Switcher() const;
  Standard_EXPORT   void SetSwitcher(const Standard_CString aswitcher) ;

private: 

  // Fields PRIVATE
  //
  Standard_CString myheader;
  Handle_TCollection_HAsciiString mymessage;
  Standard_Integer myindex;
  Standard_Boolean myison;
  Standard_Boolean myprintcontext;
  Standard_Boolean myprintheader;
  Standard_CString myswitcher;
  WOKTools_MsgHandler myendmsghandlr;
  Standard_Boolean mylogflag;
  Handle_TCollection_HAsciiString mylogfile;
  WOKTools_MsgStreamPtr mylogstream;

};


#include <WOKTools_Message.lxx>

#endif
