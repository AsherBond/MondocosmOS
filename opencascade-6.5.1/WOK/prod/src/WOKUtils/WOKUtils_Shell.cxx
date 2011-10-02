// File:	WOKUtils_Shell.cxx
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
#  define  WOKUtils_Shell WOKNT_Shell 
#else
#  include <WOKUnix_Shell.hxx>
#  define  WOKUtils_Shell WOKUnix_Shell
#endif

#include <WOKUtils_Extension.hxx>

class Standard_Transient;
class Handle_Standard_Type;
class Handle(MMgt_TShared);
class WOKUtils_Shell;

Standard_EXPORT Handle_Standard_Type& WOKUtils_Shell_Type_()
{

    static Handle_Standard_Type aType1 = STANDARD_TYPE(MMgt_TShared);
  if ( aType1.IsNull()) aType1 = STANDARD_TYPE(MMgt_TShared);
  static Handle_Standard_Type aType2 = STANDARD_TYPE(Standard_Transient);
  if ( aType2.IsNull()) aType2 = STANDARD_TYPE(Standard_Transient);
 

  static Handle_Standard_Transient _Ancestors[]= {aType1,aType2,NULL};
  static Handle_Standard_Type _aType = new Standard_Type("WOKUtils_Shell",
			                                 sizeof(WOKUtils_Shell),
			                                 1,
			                                 (Standard_Address)_Ancestors,
			                                 (Standard_Address)NULL);

  return _aType;
}
