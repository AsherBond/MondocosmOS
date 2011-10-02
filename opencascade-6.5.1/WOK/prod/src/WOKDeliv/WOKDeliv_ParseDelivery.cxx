// File:	WOKMake_ParseDelivery.cxx
// Created:	Wed Mar 20 17:55:24 1996
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <WOKDeliv_ParseDelivery.hxx>

#include <TCollection_HAsciiString.hxx>

#include <stdio.h>
#include <stdlib.h>

static Handle(WOKDeliv_DeliveryList) thelist;
static Handle(TCollection_HAsciiString) curunit;
static Handle(TCollection_HAsciiString) getunit;
static Handle(TCollection_HAsciiString) gettype;
static Standard_Boolean goandtreat=Standard_True;

extern "C" {

  extern int DELIVERYlineno;

  int DELIVERYparse();
  
  int DELIVERYrestart(FILE*);
  
  int TheToken;
  char* TheText;
  int TheType;
  int TheAttrib;
  extern FILE* DELIVERYin;
  int ErrorEncoutered;

  int Traite_Ifdef(char* s)
    {
#ifdef WNT
      const char* name="WNT";
#else
      const char* name="UNIX";
#endif
      if (!strcmp(name,s)) {
	goandtreat = Standard_True;
      }
      else {
	goandtreat = Standard_False;
      }
      return 0;
    }

  int Traite_Endif()
    {
      goandtreat = Standard_True;
      return 0;
    }

  int Traite_PutPath()
    {
      if (goandtreat) {
	thelist->SetPutPath();
      }
      return 0;
    }

  int Traite_PutInclude()
    {
      if (goandtreat) {
	thelist->SetPutInclude();
      }
      return 0;
    }

  int Traite_PutLib()
    {
      if (goandtreat) {
	thelist->SetPutLib();
      }
      return 0;
    }

  int Traite_GetUnit(char* s)
    {
      if (goandtreat) {
	if (thelist->GetStep() == T_GET) {
	  getunit = new TCollection_HAsciiString(s);
	}
      }
      return 0;
    }

  int Traite_GetType(char* s)
    {
      if (goandtreat) {
	if (thelist->GetStep() == T_GET) {
	  gettype = new TCollection_HAsciiString(s);
	}
      }
      return 0;
    }


  int Traite_GetFile(char* s)
    {
      if (goandtreat) {
	if (thelist->GetStep() == T_GET) {
	  getunit->AssignCat(":");
	  getunit->AssignCat(gettype);
	  getunit->AssignCat(":");
	  Handle(TCollection_HAsciiString) thes = new TCollection_HAsciiString(s);
	  getunit->AssignCat(thes);
	  if (!thelist->ChangeMap().Add(getunit)) {
	    WarningMsg() << "WOKDeliv_ParseDelivery" << " Get "
	      << getunit->ToCString() << " already sent" << endm;
	  }
	}
      }
      return 0;
    }

  int Traite_Name(char* s)
    {
      if (goandtreat) {
	thelist->SetName(s);
      }
      return 0;
    }

  int Traite_Requires(char* s)
    {
      if (goandtreat) {
	Handle(TCollection_HAsciiString) thes = new TCollection_HAsciiString(s);
	if (!thelist->ChangeRequireMap().Add(thes)) {
	  WarningMsg() << "WOKDeliv_ParseDelivery" << " Requires "
	    << s << " already sent" << endm;
	}
      }
      return 0;
    }

  
  int ClasseElt_DeliverFormatBase(int, char* s)
    {
      if (goandtreat) {
	curunit = new TCollection_HAsciiString(s);
	if (thelist->GetStep() == T_BASE) {
	  thelist->ChangeMap().Add(curunit);
	}
      }
      return 0;
    }

  int ClasseElt_DeliverFormat(int tokattr)
    {
      if (goandtreat) {
	int thestep = thelist->GetStep();
	Standard_Boolean doit = (thestep == tokattr);
	if (tokattr == T_LIBRARY) {
	  if (thestep == T_ARCHIVE) {
	    doit = Standard_True;
	  }
	  if (thestep == T_SHARED) {
	    doit = Standard_True;
	  }
	}
	if (doit) {
	  if (!thelist->ChangeMap().Add(curunit)) {
	    WarningMsg() << "WOKDeliv_ParseDelivery" << " Unit "
	      << curunit->ToCString() << " already sent" << endm;
	  }
	}
      }
      return 0;
    }

  
  int ClasseElt_EndDeliverFormat()
    {
      // est-ce bien utile...
      return 0;
    }

  int ClasseElt_DeliverFormatAll(int tokunit, char* s)
    {
      if (goandtreat) {
	ClasseElt_DeliverFormatBase(tokunit,s);
	if (thelist->GetStep() != T_GET) {
	  if (thelist->GetStep() != T_STATIC) {
	    thelist->ChangeMap().Add(curunit); // en attendant mieux
	  }
	}
	ClasseElt_EndDeliverFormat();
      }
      return 0;
    }

  int DELIVERYwrap()
    {
      return 1;
    }
  
  int DELIVERYerror(char* msg)
    {
      if (msg == NULL) {
	ErrorMsg() << "ParseCOMPONENTS" <<  "COMPONENTS, line " << DELIVERYlineno << " : syntax error..." << endm;
      }
      else {
	ErrorMsg() << "ParseCOMPONENTS" <<  "COMPONENTS, line " << DELIVERYlineno << " : " << msg << endm;
      }
      ErrorEncoutered = 1;
      return 1;
    }
}

Standard_Boolean WOKDeliv_Delivery_SetFile(char* filename)
{
  DELIVERYin = fopen(filename,"r");
  if (DELIVERYin) {
    return Standard_True;
  }
  return Standard_False;
}

void WOKDeliv_Delivery_CloseFile()
{
  if (DELIVERYin) {
    fclose(DELIVERYin);
  }
}

Handle(WOKDeliv_DeliveryList) WOKDeliv_Delivery_Parse(int aStep)
{
  thelist = new WOKDeliv_DeliveryList(aStep);
  goandtreat = Standard_True;
  DELIVERYlineno = 1;
  DELIVERYrestart(DELIVERYin);
  ErrorEncoutered = 0;
  DELIVERYparse();
  if (ErrorEncoutered == 0) return thelist;
  thelist.Nullify();
  return thelist;
}


void WOKDeliv_DeliveryList_Dump(const Handle(WOKDeliv_DeliveryList)& alist)
{
  InfoMsg() << "Dump of DeliveryList" << endm;
  InfoMsg() << "Name" << endm;
  InfoMsg() << alist->GetName()->ToCString() << endm;
  InfoMsg() << "Requires" << endm;
  WOKTools_MapIteratorOfMapOfHAsciiString it1(alist->GetRequireMap());
  while (it1.More()) {
    InfoMsg() << it1.Key()->ToCString() << endm;
    it1.Next();
  }
  InfoMsg() << "Content" << endm;
  WOKTools_MapIteratorOfMapOfHAsciiString it2(alist->GetMap());
  while (it2.More()) {
    InfoMsg() << it2.Key()->ToCString() << endm;
    it2.Next();
  }
}
