// File:	WOKAPI_Command_Warehouse.cxx
// Created:	Wed Oct 23 11:57:52 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKAPI_Session.hxx>
#include <WOKAPI_Warehouse.hxx>
#include <WOKAPI_SequenceOfParcel.hxx>
#include <WOKAPI_Parcel.hxx>

#include <WOKAPI_Command.jxx>

//=======================================================================
void WOKAPI_WarehouseBuild_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "-<options> -Dparameter=value,... -D...  <name>\n";
  cerr << endl;
  cerr << "    Options are : ";
  cerr << "       -P : propose default parameters value" << endl;
  cerr << "       -d : use default values for parameters (this is the default)" << endl;
  cerr << "       -n : don't use default values for parameters" << endl;
  cerr << "       -Dparam=Value : override default value for parameter %<WarehouseName>_<param>" << endl;
}

//=======================================================================
//function : WarehouseCreate
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WarehouseCreate(const WOKAPI_Session& asession, 
						  const Standard_Integer argc, const WOKTools_ArgTable& argv,
						  WOKTools_Return& returns)
{
  Standard_Integer                   i;
  WOKTools_Options                   opts(argc, argv, "D:hdnP", WOKAPI_WarehouseBuild_Usage);
  Handle(TCollection_HAsciiString)   aname;
  Handle(TCollection_HAsciiString)   name;
  Handle(TCollection_HAsciiString)   factname, shopname;
  Standard_Boolean                   querydefault   = Standard_True;
  Standard_Boolean                   proposedefault = Standard_False;
  Handle(WOKUtils_HSequenceOfParamItem) aseq;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'd':
	  querydefault = Standard_True;
	  break;
	case 'n':
	  querydefault = Standard_False;
	  break;
	case 'P':
	  querydefault   = Standard_True;
	  proposedefault = Standard_True;
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
      name     = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_WarehouseBuild_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Warehouse aware;

  if(proposedefault)
    {
      aseq = aware.BuildParameters(asession, name, opts.Defines(), querydefault);
      
      for(i =1 ; i <= aseq->Length(); i++)
	{
	  returns.AddStringParameter(aseq->Value(i).Name(), aseq->Value(i).Value());
	}
    }
  else
    {
      if(!aware.Build(asession, name, opts.Defines(), querydefault))
	{
	  return 0;
	}
      else return 1;
    }
  return 0;
}

//=======================================================================
void WOKAPI_WarehouseInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-p]  <name>\n";
  cerr << endl;
  cerr << "    Options are : ";
  cerr << "       -p : Parcels available in warehouse\n";
  cerr << endl;
}



//=======================================================================
//function : WarehouseInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WarehouseInfo(const WOKAPI_Session& asession, 
					       const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					       WOKTools_Return& returns)
{
  WOKTools_Options  opts(argc, argv, "hp", WOKAPI_WarehouseInfo_Usage);
  Handle(TCollection_HAsciiString)   name;
  Standard_Boolean getparcels = Standard_False;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'p':
	  getparcels = Standard_True;
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
      name     = opts.Arguments()->Value(1);
      break;
    case 0:
      break;
    default:
      WOKAPI_WarehouseInfo_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Warehouse aware(asession,name);
  
  if(!aware.IsValid())
    {
      ErrorMsg() << "WOKAPI_Command::WarehouseInfo"
	       << "Could not determine Warehouse : Specify Warehouse in command line or use wokcd" << endm;
      return 1;
    }

  if(getparcels)
    {
      Standard_Integer i;
      WOKAPI_SequenceOfParcel parcels;

      aware.Parcels(parcels);
      
      for(i=1; i<=parcels.Length(); i++)
	{
	  returns.AddStringValue(parcels.Value(i).Name());
	}
    }

  return 0;
}
//=======================================================================
void WOKAPI_WarehouseDestroy_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " <name>\n";
  cerr << endl;
}


//=======================================================================
//function : WarehouseDestroy
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WarehouseDestroy(const WOKAPI_Session& asession, 
						  const Standard_Integer argc, const WOKTools_ArgTable& argv, 
						  WOKTools_Return& )
{
  WOKTools_Options  opts(argc, argv, "D:hdP", WOKAPI_WarehouseDestroy_Usage);
  Handle(TCollection_HAsciiString)   name;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'R':
	  ErrorMsg() << "WOKAPI_Command::WarehouseDestroy" << "-R not yet implemented" << endm;
	  return 1;
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  switch(opts.Arguments()->Length())
    {
    case 1:
      name     = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_WarehouseBuild_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Warehouse aware(asession,name);
  
  if(!aware.IsValid())
    {
      ErrorMsg() << "WOKAPI_Command::WarehouseDestroy"
	       << "Could not determine Warehouse : Specify Warehouse in command line or use wokcd" << endm;
      return 1;
    }

  aware.Destroy();
  return 0;
}

//=======================================================================
void WOKAPI_WarehouseDeclare_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -p <parcelname> -Dparameter=value,... <housename>\n";
  cerr << endl;
  cerr << "    Options are : \n";
  cerr << "       -p <parcelname> : define name of parcel to declare (must be given)\n";
  cerr << "       -d : create using default behaviour query\n";
  cerr << "       -P : propose results of default behaviour query\n";
  cerr << "    Parameters are :\n"; 
  cerr << "       <parcelname>_Adm        =      for <parcelname> administration\n";
  cerr << "       <parcelname>_Home       =      for <parcelname> home directory\n";
  cerr << "       <parcelname>_Stations   =      for <parcelname> available stations\n";
  cerr << "       <parcelname>_DBMSystems =      for <parcelname> available DBMS\n";
  cerr << "       <parcelname>_Delivery   =      for delivery name\n";
}

//=======================================================================
//function : WarehouseDeclare
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WarehouseDeclare(const WOKAPI_Session& asession, 
						  const Standard_Integer argc, const WOKTools_ArgTable& argv, 
						  WOKTools_Return& returns)
{
  WOKTools_Options  opts(argc, argv, "D:hdp:P", WOKAPI_WarehouseDeclare_Usage);
  Handle(TCollection_HAsciiString)   name,parcelname;
  Standard_Boolean getdefault = Standard_False;
  Standard_Boolean propdefault = Standard_False;

  if(opts.Failed()) return 1;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'p' :
	  parcelname = opts.OptionArgument();
	  break;
	case 'd' :
	  getdefault = Standard_True;
	  break;
	case 'P' :
	  propdefault = Standard_True;
	  break;
	default:
	  break;
	}
      opts.Next();
    }
  
  if (parcelname.IsNull()) {
    ErrorMsg() << "WOKAPI_Command::WarehouseDeclare"
	     << "Parcel name is missing" << endm;
    WOKAPI_WarehouseDeclare_Usage(argv[0]);
    return 1;
  }
    

  switch(opts.Arguments()->Length())
    {
    case 1:
      name     = opts.Arguments()->Value(1);
      break;
    case 0:
      break;
    default:
      WOKAPI_WarehouseDeclare_Usage(argv[0]);
      return 1;
    }

  
  WOKAPI_Warehouse aware(asession,name);
  
  if(!aware.IsValid())
    {
      ErrorMsg() << "WOKAPI_Command::WarehouseDeclare"
	       << "Could not determine Warehouse : Specify Warehouse in command line or use wokcd" << endm;
      return 1;
    }
  
  if (propdefault) 
    {
      WOKAPI_Parcel aparc;
      Handle(TCollection_HAsciiString) aname = new TCollection_HAsciiString(aware.UserPath());

      aname->AssignCat(":");
      aname->AssignCat(parcelname);

      Handle(WOKUtils_HSequenceOfParamItem) aseq = aparc.BuildParameters(asession,
									 aname,
									 opts.Defines(),
									 getdefault);
      for(Standard_Integer i =1 ; i <= aseq->Length(); i++) 
	{
	  returns.AddStringParameter(aseq->Value(i).Name(), aseq->Value(i).Value());
	}
      return 0;
    }
  
  WOKAPI_Parcel aparcel(asession,parcelname,Standard_False);
  if (aparcel.IsValid()) {
    ErrorMsg() << "WOKAPI_Command::WarehouseDeclare"
	     << "Parcel " << parcelname << " is already declared in Warehouse " << aware.Name() << endm;
    return 1;
  }
    
  WOKAPI_Parcel aparc;
  if (!aparc.Declare(asession,
		     parcelname,
		     aware,
		     opts.Defines(),
		     getdefault)) {
    ErrorMsg() << "WOKAPI_Command::WarehouseDeclare"
	     << "Unable to declare parcel " << parcelname << " in Warehouse " << aware.Name() << endm;
    return 1;
  }
    
  return 0;
}
