
#include <WOKTools_Info.ixx>

Standard_EXPORT WOKTools_Info InfoMsg();

WOKTools_Info::WOKTools_Info() : WOKTools_Message("WOK_INFO", "Info    : ")
{
  Set();
}

Standard_Character WOKTools_Info::Code() const 
{return 'I';}
