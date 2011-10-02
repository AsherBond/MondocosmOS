// File:	WOKAPI_APICommand.hxx
// Created:	Mon Apr  1 17:16:48 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKAPI_APICommand_HeaderFile
#define WOKAPI_APICommand_HeaderFile


#include <WOKTools_ArgTable.hxx>
#include <WOKTools_Return.hxx>

#include <WOKAPI_Session.hxx>

typedef Standard_Integer (*WOKAPI_APICommand) (const WOKAPI_Session&, 
					       const Standard_Integer , const WOKTools_ArgTable& , 
					       WOKTools_Return &);

#endif

