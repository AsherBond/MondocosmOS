// File:	WOKAPI_Command_Factory.cxx
// Created:	Wed Oct 23 11:56:11 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>



#include <TCollection_AsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKAPI_Factory.hxx>
#include <WOKAPI_SequenceOfWorkshop.hxx>
#include <WOKAPI_Workshop.hxx>
#include <WOKAPI_Warehouse.hxx>

#include <WOKAPI_Command.jxx>


//=======================================================================  
void WOKAPI_FactoryBuild_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "-<options> -Dparameter=value,... -D...  <name>\n" ;
  cerr << endl;
  cerr << "    Options are : ";
  cerr << "       -P : propose default parameters value" << endl;
  cerr << "       -d : use default values for parameters (this is the default)" << endl;
  cerr << "       -n : don't use default values for parameters" << endl;
  cerr << "       -Dparam=Value : override default value for parameter %<FactoryName>_<param>" << endl;
  return;
}

//=======================================================================
//function : FactoryCreate
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::FactoryCreate(const WOKAPI_Session& asession, 
					       const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					       WOKTools_Return& returns)
{
  Standard_Integer         i;
  TCollection_AsciiString  aname;
  WOKTools_Options         opts(argc, argv, "D:hdnP", WOKAPI_FactoryBuild_Usage);
  Standard_Boolean         querydefault   = Standard_True;
  Standard_Boolean         proposedefault = Standard_False;
  Handle(WOKUtils_HSequenceOfParamItem) aseq;
  Handle(TCollection_HAsciiString) path;
    
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
      break;
    default:
      WOKAPI_FactoryBuild_Usage(argv[0]);
      return 1;
    }

  Handle(TCollection_HAsciiString) name;
  name = opts.Arguments()->Value(1);

  WOKAPI_Factory afact;

  if(proposedefault)
    {
      aseq = afact.BuildParameters(asession, name, opts.Defines(), querydefault);

      for(i =1 ; i <= aseq->Length(); i++)
	{
	  returns.AddStringParameter(aseq->Value(i).Name(), aseq->Value(i).Value());
	}
    }
  else
    {
      return afact.Build(asession, name, opts.Defines(), querydefault);
    }
  return 0;
}
//=======================================================================
void WOKAPI_FactoryInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "[-s|-S|-W] [<name>]\n";
  cerr << endl;
  cerr << "    Options are :\n";
  cerr << "       -s : Workshops in factory\n";
  cerr << "       -W : Warehouse name\n";
}


//=======================================================================
//function : FactoryInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::FactoryInfo(const WOKAPI_Session& asession, 
					     const Standard_Integer argc, const WOKTools_ArgTable& argv,
					     WOKTools_Return& returns)
{
  Handle(TCollection_HAsciiString)  aname;
  WOKTools_Options opts(argc, argv, "sSW", WOKAPI_FactoryInfo_Usage, "sSW");
  Standard_Boolean getshops = Standard_False, getwarehouse = Standard_False;
  
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 's':
	  getshops = Standard_True;
	  break;
	case 'W':
	  getwarehouse = Standard_True;
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
      WOKAPI_FactoryInfo_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Factory afact(asession,aname);

  if(!afact.IsValid())
    {
      ErrorMsg() << argv[0]
	       << "Could not determine factory : Specify factory in command line or use wokcd" << endm;
      return 1;
    }
  
  if((!getshops) && 
     (!getwarehouse) && 
     (!opts.Arguments()->Length()))
    {
      returns.AddStringValue(afact.Name());
    }
  else
    {
      if(getshops)
	{
	  Standard_Integer i;
	  WOKAPI_SequenceOfWorkshop aseq;

	  afact.Workshops(aseq);
	  
	  for(i = 1 ; i <= aseq.Length() ; i++)
	    {
	      returns.AddStringValue(aseq.Value(i).Name());
	    }
	}
      if(getwarehouse)
	{
	  returns.AddStringValue(afact.Warehouse().Name());
	}
    }
  return 0;
}

//=======================================================================
void WOKAPI_FactoryDestroy_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-R]  <name>\n";
  cerr << endl;
  cerr << "    Options are : ";
  cerr << "       -R : Recursively remove Nestings\n";
}


//=======================================================================
//function : FactoryDestroy
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::FactoryDestroy(const WOKAPI_Session& asession, 
						const Standard_Integer argc, const WOKTools_ArgTable& argv, 
						WOKTools_Return& )
{
  WOKTools_Options  opts(argc, argv, "D:hdP", WOKAPI_FactoryDestroy_Usage);
  Handle(TCollection_HAsciiString)   name;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'R':
	  ErrorMsg() << "WOKAPI_Command::FactoryDestroy" << "-R not yet implemented" << endm;
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
      WOKAPI_FactoryDestroy_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Factory afact(asession,name);
  
  if(!afact.IsValid())
    {
      ErrorMsg() << "WOKAPI_Command::FactoryDestroy"
	       << "Could not determine factory : Specify factory in command line or use wokcd" << endm;
      return 1;
    }

  afact.Destroy();

  return 0;
}
