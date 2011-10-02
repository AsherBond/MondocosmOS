// File:	WOKTools_Verbose.cxx
// Created:	Wed Jun 28 20:16:30 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#ifdef WNT
# include <io.h>
#endif
#ifdef HAVE_CONFIG_H
# include <config.h>
#endif
#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif

#include <WOKTools_Verbose.ixx>

Standard_EXPORT WOKTools_Verbose VerboseMsg();

WOKTools_Verbose::WOKTools_Verbose(const Standard_CString astr) : WOKTools_Message(astr, "Verbose : ")
{
  if(getenv(Switcher())) Set();
  else                   UnSet();
  DoPrintContext();
}

WOKTools_Verbose& WOKTools_Verbose::LocalSwitcher(const Standard_CString aswitcher) const 
{
  static WOKTools_Verbose averb;

  averb = *this;
  averb.SetSwitcher(aswitcher);

  if(IsSet())
    {
      if(getenv(averb.Switcher())) averb.Set();
      else                         averb.UnSet();
    }

  return averb;
}


Standard_Character WOKTools_Verbose::Code() const 
{return 'V';}
