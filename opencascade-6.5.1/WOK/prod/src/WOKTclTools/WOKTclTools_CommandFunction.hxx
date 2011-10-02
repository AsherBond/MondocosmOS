// File:	WOKTclTools_CommandFunction.hxx
// Created:	Tue Aug  1 23:18:40 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKTclTools_CommandFunction_HeaderFile
#define WOKTclTools_CommandFunction_HeaderFile

class WOKTclTools_Interpretor;

typedef Standard_Integer (*WOKTclTools_CommandFunction)(const Handle(WOKTclTools_Interpretor)&, Standard_Integer, char**);


#endif
