// File:	WOKTclTools_WokCommand.hxx
// Created:	Tue Aug 13 11:00:12 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKTclTools_WokCommand_HeaderFile
#define WOKTclTools_WokCommand_HeaderFile


#include <WOKTools_ArgTable.hxx>
#include <WOKTools_Return.hxx>

typedef Standard_Integer (*WOKTclTools_WokCommand)(const Standard_Integer,  const WOKTools_ArgTable& , WOKTools_Return &);

#endif

