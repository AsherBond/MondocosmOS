// File:	WOKAPI_Command_Parcel.cxx
// Created:	Wed Oct 23 11:59:20 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKAPI_Parcel.hxx>
#include <WOKAPI_Unit.hxx>
#include <WOKAPI_SequenceOfUnit.hxx>

#include <WOKAPI_Command.jxx>


//=======================================================================
void WOKAPI_ParcelInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-d] [-l|-a]\n";
  cerr << endl;
  cerr << "    Options are :\n";
  cerr << "       -d : delivery in parcel\n";
  cerr << "       -l : lists units in parcel\n";
  cerr << "       -a : lists units in parcel with their types\n";
}

//=======================================================================
//function : ParcelInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::ParcelInfo(const WOKAPI_Session& asession, 
					    const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					    WOKTools_Return& returns)
{
  WOKTools_Options  opts(argc, argv, "hdla", WOKAPI_ParcelInfo_Usage);
  Handle(TCollection_HAsciiString)   name;
  Standard_Boolean getdelivery   = Standard_False;
  Standard_Boolean getunits      = Standard_False;
  Standard_Boolean gettypedunits = Standard_False;
    
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'd':
	  getdelivery    = Standard_True;
	  break;
	case 'l':
	  getunits       = Standard_True;
	  break;
	case 'a':
	  gettypedunits  = Standard_True;
	  break; 
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  switch(opts.Arguments()->Length())
    {
    case 1:
      name = opts.Arguments()->Value(1);
      break;
    case 0:
      break;
    default:
      WOKAPI_ParcelInfo_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Parcel aparcel(asession,name);
  
  if(!aparcel.IsValid())
    {
      ErrorMsg() << "WOKAPI_Command::WarehouseInfo"
	       << "Could not determine Warehouse : Specify Warehouse in command line or use wokcd" << endm;
      return 1;
    }
  
  if(getdelivery)
    {
      WOKAPI_Unit aunit;

      aparcel.Delivery(aunit);

      if(aunit.IsValid())
	{
	  returns.AddStringValue(aunit.Name());
	  return 0;
	}
      else
	return 1;
    }
  if(getunits||gettypedunits)
    {
      WOKAPI_SequenceOfUnit aseq;
      Standard_Integer i;

      aparcel.Units(aseq);
      
      if(getunits)
	{
	  for(i=1;i<=aseq.Length(); i++)
	    {
	      returns.AddStringValue(aseq.Value(i).Name());
	    }
	}
      else
	{
	  Handle(TCollection_HAsciiString) astr;

	  for(i=1;i<=aseq.Length(); i++)
	    {
	      astr = new TCollection_HAsciiString(aseq.Value(i).Type());
	      astr->AssignCat(" ");
	      astr->AssignCat(aseq.Value(i).Name());
	      
	      returns.AddStringValue(astr);
	    }
	}
    }

  return 0;
}
