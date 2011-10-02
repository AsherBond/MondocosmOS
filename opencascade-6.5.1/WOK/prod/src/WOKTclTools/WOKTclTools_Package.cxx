// File:	WOKTclTools_Package.cxx
// Created:	Wed Aug 21 13:47:00 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <tcl.h>

#include <WOKTclTools_Package.ixx>
#include <WOKTclTools_Interpretor.hxx>


//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKTclTools_Package
//purpose  : 
//=======================================================================
WOKTclTools_Package::WOKTclTools_Package(const Handle(WOKTclTools_Interpretor)& interp, 
					 const Standard_CString aname,
					 const Standard_CString aversion)
  : myinterp(interp), myname(aname), myversion(aversion)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Require
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_Package::Require(const Standard_Boolean exact)
{
  if(!myinterp.IsNull()) 
    {
      if (Tcl_PkgRequire(myinterp->Interp(), (char*)myname.ToCString(), (char*)myversion.ToCString(),exact) != TCL_OK) {
	return TCL_ERROR;
      }
    }
  return TCL_OK;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Provide
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_Package::Provide()
{
  if(!myinterp.IsNull()) 
    {
      if (Tcl_PkgProvide(myinterp->Interp(), (char*)myname.ToCString(), (char*)myversion.ToCString()) != TCL_OK) {
	return TCL_ERROR;
      }
    }
  return TCL_OK;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : EvalInitFile
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_Package::EvalInitFile(const Standard_Boolean required)
{
  if(!myinterp.IsNull())
    {
      TCollection_AsciiString envvar(myname);
      envvar.UpperCase();
      envvar.AssignCat("_LIBRARY");

      TCollection_AsciiString filename;  
      filename.AssignCat(myname);
      filename.AssignCat("_Init.tcl");

      TCollection_AsciiString filepath;
      
      
      filepath.AssignCat("[file join $env(");
      filepath.AssignCat(envvar);
      filepath.AssignCat(") ");
      filepath.AssignCat(filename);
      filepath.AssignCat("]");
  
  
      TCollection_AsciiString cmd("if [file exists ");
      cmd.AssignCat(filepath);
      cmd.AssignCat("] {source ");
      cmd.AssignCat(filepath);
      cmd.AssignCat("} ");

      if(required)
	{
	  cmd.AssignCat(" else {set msg \"can't find ");
	  cmd.AssignCat(filepath);
	  cmd.AssignCat(";\\n");
	  cmd.AssignCat(" perhaps you need to install Wok or set your ");
	  cmd.AssignCat(envvar);
	  cmd.AssignCat(" environment variable?\"\n\
error $msg\n\
}");
	}
      else
	{
	  cmd.AssignCat("\n");
	}
      
      if(Tcl_Eval(myinterp->Interp(), (char*)cmd.ToCString()) != TCL_OK)
	return TCL_ERROR;
    }
  return TCL_OK;
}

