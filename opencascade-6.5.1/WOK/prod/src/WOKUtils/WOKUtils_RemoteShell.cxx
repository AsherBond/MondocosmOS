// File:	WOKUtils_RemoteRemoteShell.cxx
// Created:	Fri Jan 31 19:40:45 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

// File:	WOKUtils_RemoteShell.cxx
// Created:	Fri Jan 31 19:40:30 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef _Standard_Macro_HeaderFile
#include <Standard_Macro.hxx>
#endif

#ifndef _Handle_MMgt_TShared_HeaderFile
#include <Handle_MMgt_TShared.hxx>
#endif

#ifdef WNT
#  include <WOKNT_Shell.hxx>
#  define  WOKUtils_RemoteShell WOKNT_Shell 
#else
#  include <WOKUnix_RemoteShell.hxx>
#  define  WOKUtils_RemoteShell WOKUnix_RemoteShell
#endif

class Standard_Transient;
class Handle_Standard_Type;
class Handle(MMgt_TShared);
class WOKUtils_RemoteShell;

Standard_EXPORT Handle_Standard_Type& WOKUtils_RemoteShell_Type_()
{

    static Handle_Standard_Type aType1 = STANDARD_TYPE(MMgt_TShared);
  if ( aType1.IsNull()) aType1 = STANDARD_TYPE(MMgt_TShared);
  static Handle_Standard_Type aType2 = STANDARD_TYPE(Standard_Transient);
  if ( aType2.IsNull()) aType2 = STANDARD_TYPE(Standard_Transient);
 

  static Handle_Standard_Transient _Ancestors[]= {aType1,aType2,NULL};
  static Handle_Standard_Type _aType = new Standard_Type("WOKUtils_RemoteShell",
			                                 sizeof(WOKUtils_RemoteShell),
			                                 1,
			                                 (Standard_Address)_Ancestors,
			                                 (Standard_Address)NULL);

  return _aType;
}
