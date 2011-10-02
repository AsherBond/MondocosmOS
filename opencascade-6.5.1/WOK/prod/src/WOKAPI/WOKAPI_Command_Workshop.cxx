// File:	WOKAPI_Command_Workshop.cxx
// Created:	Wed Oct 23 12:01:10 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKAPI_Workshop.hxx>
#include <WOKAPI_Workbench.hxx>
#include <WOKAPI_SequenceOfWorkbench.hxx>
#include <WOKAPI_Parcel.hxx>
#include <WOKAPI_SequenceOfParcel.hxx>

#include <WOKAPI_Command.jxx>


//=======================================================================
void WOKAPI_WorkshopBuild_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -d -Dparameter=value,... -D...  <name>" << endl ;
  cerr << endl;
  cerr << "    Options are :" << endl;
  cerr << "       -P : propose default parameters value" << endl;
  cerr << "       -d : use default values for parameters (this is the default)" << endl;
  cerr << "       -n : don't use default values for parameters" << endl;
  cerr << "       -Dparam=Value : override default value for parameter %<WorkshopName>_<param>" << endl;
  cerr << endl;
  return;
}

//=======================================================================
//function : WorkshopCreate
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WorkshopCreate(const WOKAPI_Session& asession, 
						const Standard_Integer argc, const WOKTools_ArgTable& argv, 
						WOKTools_Return& returns)
{
  
  Standard_Integer                   i;
  WOKTools_Options                   opts(argc, argv, "D:hdnP", WOKAPI_WorkshopBuild_Usage);
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
      WOKAPI_WorkshopBuild_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Workshop ashop;

  if(proposedefault)
    {
      aseq = ashop.BuildParameters(asession, name, opts.Defines(), querydefault);
      
      for(i =1 ; i <= aseq->Length(); i++)
	{
	  returns.AddStringParameter(aseq->Value(i).Name(), aseq->Value(i).Value());
	}
    }
  else
    {
      if(!ashop.Build(asession, name, opts.Defines(), querydefault))
	{
	  return 0;
	}
      else return 1;
    }
  return 0;
}
//=======================================================================
void WOKAPI_WorkshopInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "[-t|-l|-p] [<name>]\n";
  cerr << endl;
  cerr << "    Options are :\n";
  cerr << "       -t : get tree of workbenches\n";
  cerr << "       -w : list of workbenches\n";
  cerr << "       -p : list of parcels in configuration\n";
}

//=======================================================================
//function : WorkshopInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WorkshopInfo(const WOKAPI_Session& asession, 
					      const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					      WOKTools_Return& returns)
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) aname;
  WOKTools_Options opts(argc, argv, "htwp", WOKAPI_WorkshopInfo_Usage);
  Standard_Boolean gettree = Standard_False, getwbs = Standard_False, getparcels = Standard_False;
  
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 't':
	  gettree = Standard_True;
	  break;
	case 'w':
	  getwbs = Standard_True;
	  break;
	case 'p':
	  getparcels = Standard_True;
	  break;
	default:
	  return 1;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;


  switch(opts.Arguments()->Length())
    {
    case 0:
      break;
    case 1:
      aname = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_WorkshopInfo_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Workshop ashop(asession,aname);

  if(!ashop.IsValid())
    {
      ErrorMsg() << argv[0]
	       << "Could not determine workshop : Specify workshop in command line or use wokcd" << endm;
      return 1;
    }

  if(gettree == Standard_True)
    {
      ErrorMsg() << argv[0] << "Option -t not yet implemented\n";
      return 1;
    }

  if(getwbs == Standard_True)
    {
      WOKAPI_SequenceOfWorkbench benchseq;

      ashop.Workbenches(benchseq);

      for(i=1; i<= benchseq.Length() ; i++)
	{
	  returns.AddStringValue(benchseq.Value(i).Name());
	}
      return 0;
    }
  if(getparcels == Standard_True)
    {
      WOKAPI_SequenceOfParcel parcseq;

      ashop.UsedParcels(parcseq);
      
      for(i=1; i<= parcseq.Length() ; i++)
	{
	  returns.AddStringValue(parcseq.Value(i).Name());
	}
      return 0;
    }
  return 0;
}

//=======================================================================
void WOKAPI_WorkshopDestroy_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " <name>\n";
  cerr << endl;
}



//=======================================================================
//function : WorkshopDestroy
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WorkshopDestroy(const WOKAPI_Session& asession, 
						 const Standard_Integer argc, const WOKTools_ArgTable& argv, 
						 WOKTools_Return& )
{
  WOKTools_Options  opts(argc, argv, "D:hdP", WOKAPI_WorkshopDestroy_Usage);
  Handle(TCollection_HAsciiString)   name;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'R':
	  ErrorMsg() << "WOKAPI_Command::WorkshopDestroy" << "-R not yet implemented" << endm;
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
      WOKAPI_WorkshopDestroy_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Workshop ashop(asession,name);
  
  if(!ashop.IsValid())
    {
      ErrorMsg() << "WOKAPI_Command::WorkshopDestroy"
	       << "Could not determine workshop : Specify workshop in command line or use wokcd" << endm;
      return 1;
    }

  ashop.Destroy();
  return 0;
}
