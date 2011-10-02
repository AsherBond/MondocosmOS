// File:	WOKAPI_Command_Unit.cxx
// Created:	Wed Oct 23 12:02:43 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>



#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <WOKTools_DataMapIteratorOfDataMapOfHAsciiStringOfHSequenceOfHAsciiString.hxx>
#include <WOKTools_DataMapOfHAsciiStringOfHSequenceOfHAsciiString.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Define.hxx>
#include <WOKTools_HSequenceOfDefine.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKAPI_Workbench.hxx>
#include <WOKAPI_Parcel.hxx>
#include <WOKAPI_Unit.hxx>
#include <WOKAPI_SequenceOfUnit.hxx>
#include <WOKAPI_MakeStep.hxx>
#include <WOKAPI_SequenceOfMakeStep.hxx>
#include <WOKAPI_File.hxx>
#include <WOKAPI_SequenceOfFile.hxx>
#include <WOKAPI_Locator.hxx>
#include <WOKAPI_BuildProcess.hxx>

#include <WOKAPI_Command.jxx>

#if defined( WNT ) && defined( _DEBUG )
#include <OSD_Timer.hxx>
extern "C" void _debug_break ( char* );
#endif  // WNT && _DEBUG

//=======================================================================
void WOKAPI_UnitBuild_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-<typecode>|-T <TypeName>|-P] <name>" << endl;
  cerr << endl;
  cerr << "    Options are :"  << endl;
  cerr << "      -? : Type code for devunit (default is package if unit in other nesting cannot be found)" << endl;
  cerr << "      -P : " << cmd << " creation possibilities : <typecode> <typename>" << endl;
  cerr << "      -T <typename> : create with explicit type code" << endl;
  cerr << endl;
  return;
}

//=======================================================================
//function : UnitCreate
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::UnitCreate(const WOKAPI_Session& asession,
    					    const Standard_Integer argc, const WOKTools_ArgTable& argv, 
    					    WOKTools_Return& returns)
{
  
  // code of API for Workbench Creation
  Handle(TCollection_HAsciiString)      aname, unitname, benchname, typesstr;
  WOKTools_Options                      opts(argc, argv, "hT:PabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", WOKAPI_UnitBuild_Usage,"hTPabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
  Handle(WOKUtils_HSequenceOfParamItem) aseq;
  Standard_Character                    typecode = 0;
  Standard_Boolean                      getpossibilities = Standard_False;
  
  while(opts.More())
    {
      switch(opts.Option())
    	{
    	case 'T':
    	  typesstr = opts.OptionArgument();
    	  break;
    	case 'P':
    	  getpossibilities = Standard_True;
    	  break;
    	default:
    	  typecode = opts.Option();
    	  break;
    	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  if(getpossibilities)
    {
      if(!opts.Arguments()->Length()) {
    	benchname = asession.CWEntityName();
      }
      else {
    	benchname = opts.Arguments()->Value(1);
      }
      
      WOKAPI_Workbench abench(asession,benchname,Standard_False);
      TColStd_SequenceOfHAsciiString aseq;
      
      abench.KnownTypeNames(aseq);
      Handle(TCollection_HAsciiString) keys = abench.KnownTypeKeys();
      
      for(Standard_Integer i=1; i <= aseq.Length(); i++)
    	{
    	  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString(keys->Value(i));
    	  
    	  astr->AssignCat(" ");
    	  astr->AssignCat(aseq.Value(i));
    	  
    	  returns.AddStringValue(astr);
    	} 
      return 0;
    }
  
  
  if(opts.Arguments()->Length() != 1)
    {
      WOKAPI_UnitBuild_Usage(argv[0]);
      return 1;
    }
  
  
  aname = opts.Arguments()->Value(1);
  Standard_Integer i = aname->SearchFromEnd(":");
  
  if(i != -1)
    {
      unitname  = aname->SubString(i+1, aname->Length());
      benchname = aname->SubString(1, i-1);
    }
  else
    {
      unitname  = aname;
      benchname = asession.CWEntityName();
    }
  
  if(typecode != 0 && !typesstr.IsNull())
    {
      ErrorMsg() << argv[0] 
	<< "Option -T cannot be used in conjunction with type key!" << endm;
      return 1;
    }

  WOKAPI_Unit aunit;
  
  
  // On verifie le type code
  WOKAPI_Workbench abench(asession,benchname,Standard_False);
  
  if(typecode == 0 && abench.IsValid() && !unitname.IsNull() && typesstr.IsNull())
    {
      WOKAPI_Locator alocator;
      
      alocator.Set(abench);
      WOKAPI_Unit aprev = alocator.LocateUnit(unitname);
      
      if(aprev.IsValid())
  	{
  	  typecode = aprev.TypeKey();
  	  InfoMsg() << argv[0] 
  	    << "No type specified : using type of " << aprev.UserPath() << " : " << aprev.Type() << " (eq : ucreate -" << typecode << ")" << endm;
  	}
      else 
  	{
  	  InfoMsg() << argv[0] 
 	    << "No type specified : using package (eq : ucreate -p)" << endm;
  	  typecode = 'p';
  	}
    }

  if(!typesstr.IsNull())
   {
     TColStd_SequenceOfHAsciiString aseq;

     abench.KnownTypeNames(aseq);
     Handle(TCollection_HAsciiString) keys = abench.KnownTypeKeys();
     Standard_Boolean found = Standard_False;

     for(Standard_Integer i=1; i <= aseq.Length() && !found; i++)
       {
	 if(typesstr->IsSameString(aseq.Value(i)))
	   {
	     found = Standard_True;
	     typecode = keys->Value(i);
	   }
       }
     if(!found) 
       {
	 ErrorMsg() << argv[0]
	   << "Invalid type specification : " << typesstr << " (see ucreate -P for possibilities)" << endm;
	 return 1;
       }
   }
  
  {
    TColStd_SequenceOfHAsciiString aseq;
    
    abench.KnownTypeNames(aseq);
    Handle(TCollection_HAsciiString) keys = abench.KnownTypeKeys();
    
    if(!keys.IsNull())
      {
    	Standard_Boolean found = Standard_False;
    	
    	for(Standard_Integer i=1; i <= aseq.Length() && !found; i++)
    	  {
    	    if(typecode == keys->Value(i))
    	      {
    		InfoMsg() << argv[0] 
		  << "Creating " << aseq.Value(i) << " " << aname
		    << " in " << abench.UserPath() << endm;
    		found = Standard_True;
    	      }    	    
    	  } 
    	if(!found) 
    	  {
    	    ErrorMsg() << argv[0] 
	      << "Invalid type key specified : " << typecode << endm;
    	    return 1;
    	  }
      }
  }
  
  if(aunit.Build(asession, aname, typecode, opts.Defines(), Standard_False)) return 1;
  
  return 0;
}
//=======================================================================
void WOKAPI_UnitInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-f|-p|-t|-c] [-m|-e] [-l] [-T <type>] [<name>]\n";
  cerr << endl;
  cerr << "    Options are :\n";
  cerr << "       -f : list of file names\n";
  cerr << "       -F : list of file names with their types\n";
  cerr << "       -p : list of file pathes\n";
  cerr << "       -T : File Type filter\n";
  cerr << "       -i : File Type Station or DBMS independent filter\n";
  cerr << "       -s : File Type Station dependent filter\n";
  cerr << "       -b : File Type DBMS dependent filter\n";
  cerr << "       -B : File Type DBMS and Station (Both) dependent filter\n";
  cerr << "       -l : local file filter\n";
  cerr << "       -m : Only Missing files\n";
  cerr << "       -e : Only Existing files\n";
  cerr << "       -t : Unit Type\n";
  cerr << "       -c : Unit Type code\n";
  cerr << endl;
}

//=======================================================================
//function : UnitInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::UnitInfo(const WOKAPI_Session& asession, 
    					  const Standard_Integer argc, const WOKTools_ArgTable& argv,
    					  WOKTools_Return& returns)
{
  Handle(TCollection_HAsciiString)  aname, typefiltername;
  WOKTools_Options opts(argc, argv, "hibsBfFptclT:me", WOKAPI_UnitInfo_Usage, "htcibsB");
  Standard_Boolean getname       = Standard_False;
  Standard_Boolean getftype      = Standard_False;
  Standard_Boolean getpathes     = Standard_False;
  Standard_Boolean gettype       = Standard_False;
  Standard_Boolean gettypecode   = Standard_False;
  Standard_Boolean localfilter   = Standard_False;
  Standard_Boolean typefilter    = Standard_False;
  Standard_Boolean missingfilter = Standard_False;
  Standard_Boolean existsfilter  = Standard_False;
  Standard_Boolean independant   = Standard_False;
  Standard_Boolean dbdependant   = Standard_False;
  Standard_Boolean stdependant   = Standard_False;
  Standard_Boolean bothdependant = Standard_False;
  
  while(opts.More())
    {
      switch(opts.Option())
    	{
    	case 'i':
    	  independant = Standard_True;
    	  break;
    	case 's':
    	  stdependant = Standard_True;
    	  break;
    	case 'b':
    	  dbdependant = Standard_True;
    	  break;
    	case 'B':
    	  bothdependant = Standard_True;
    	  break;
    	case 'f':
    	  getname      = Standard_True;
    	  break;
    	case 'F':
    	  getname      = Standard_True;
    	  getftype     = Standard_True;
    	  break;
    	case 'p':
    	  getpathes    = Standard_True;
    	  break;
    	case 't':
    	  gettype      = Standard_True;
    	  break;
    	case 'c':
    	  gettypecode  = Standard_True;
    	  break;
    	case 'l':
    	  localfilter  = Standard_True;
    	  break;
    	case 'T':
    	  typefilter    = Standard_True;
    	  typefiltername= opts.OptionArgument();
    	  break;
    	case 'm':
    	  missingfilter = Standard_True;
    	  break;
    	case 'e':
    	  existsfilter  = Standard_True;
    	  break;
    	}
      opts.Next();
    }
  
  if(opts.Failed()) return 1;
  
  if(missingfilter && localfilter)
    {
      ErrorMsg() << argv[0] << "Mixing -l and -m is nonsense" << endm;
      return 1;
    }
  
  if(missingfilter && existsfilter)
    {
      ErrorMsg() << argv[0] << "Mixing -e and -m is nonsense" << endm;
      return 1;
    }
  
  if(missingfilter && getpathes)
    {
      ErrorMsg() << argv[0] << "Mixing -m and -p is nonsense" << endm;
      return 1;
    }
  
  switch(opts.Arguments()->Length())
    {
    case 0:
      break;
    case 1:
      aname = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_UnitInfo_Usage(argv[0]);
      return 1;
    }
  
  WOKAPI_Unit aunit(asession,aname,Standard_False);
  
  if(!aunit.IsValid())
    {
      ErrorMsg() << argv[0] << "Could not determine unit : Specify unit in command line or use wokcd" << endm;
      return 1;
    }
  
  if(getname||getftype||getpathes)
    {
      Standard_Integer i;
      WOKAPI_SequenceOfFile fileseq;
      WOKAPI_Locator        locator;
      
      WOKAPI_Entity nesting = aunit.NestingEntity();
      
      if (nesting.IsWorkbench()) {
	
    	WOKAPI_Workbench abench(asession, nesting.UserPath());
	
    	if(!abench.IsValid())
    	  {
    	    ErrorMsg() << argv[0] << "Could not determine workbench" << endm;
    	    return 1;
    	  }
	
    	locator.Set(abench);
      }
      else {
    	WOKAPI_Parcel aparcel(asession,nesting.UserPath());
	
    	if(!aparcel.IsValid())
    	  {
    	    ErrorMsg() << argv[0] << "Could not determine parcel" << endm;
    	    return 1;
    	  }
    	
    	Handle(TColStd_HSequenceOfHAsciiString) visib = new TColStd_HSequenceOfHAsciiString();
    	visib->Append(aparcel.UserPath());
    	locator.Set(asession,visib);
      }
      
      if(!locator.IsValid())
    	{
    	  ErrorMsg() << argv[0] << "Could not initialize locator with " << aunit.NestingEntity().UserPath() << endm;
    	  return 1;
    	}
      
      aunit.Files(locator,fileseq);
      
      for(i=1; i<=fileseq.Length(); i++)
    	{
    	  Standard_Boolean addfile = Standard_True;
    	  WOKAPI_File& afile = fileseq.ChangeValue(i);
	  
    	  if(typefilter &&  addfile)
    	    {
    	      if(! afile.Type()->IsSameString(typefiltername)) addfile = Standard_False;
    	    }
    	  if(localfilter &&  addfile)
    	    {
    	      if(!afile.IsLocated()) afile.Locate(locator);
    	      if(afile.IsLocated())
    		{if(! afile.IsLocalTo(aunit) ) addfile = Standard_False;}
    	      else
    		{addfile = Standard_False;}
    	    }
    	  if(missingfilter && addfile)
    	    {
    	      if(!afile.IsLocated()) afile.Locate(locator);
    	      if(afile.IsLocated()) addfile = Standard_False;
    	    }
    	  if(existsfilter && addfile)
    	    {
    	      if(!afile.IsLocated()) afile.Locate(locator);
    	      if(!afile.IsLocated())  addfile = Standard_False;
    	    }
    	  if(independant && addfile)
    	    {
    	      if(afile.IsDBMSDependent() || afile.IsStationDependent()) addfile = Standard_False;
    	    }
    	  if(bothdependant && addfile)
    	    {
    	      if(!afile.IsDBMSDependent() || !afile.IsStationDependent()) addfile = Standard_False;
    	    }
    	  if(dbdependant && addfile)
    	    {
    	      if(!afile.IsDBMSDependent() || afile.IsStationDependent()) addfile = Standard_False;
    	    }
    	  if(stdependant && addfile)
    	    {
    	      if(!afile.IsStationDependent() || afile.IsDBMSDependent()) addfile = Standard_False;
    	    }
	  
    	  if(addfile && afile.IsValid())
    	    {
    	      if( (getpathes && ( getname   || getftype)) || 
		 (getname   && ( getpathes || getftype)) || 
		 (getftype  && ( getname   || getpathes)) )
    		{
    		  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString;
    		  if(getftype)
    		    {
    		      astr->AssignCat(afile.Type());
    		      astr->AssignCat(" ");
    		    }
    		  if(getname)
    		    {
    		      astr->AssignCat(afile.Name());
    		      astr->AssignCat(" ");
    		    }
    		  if(getpathes)
    		    {
    		      if(!afile.IsLocated()) afile.Locate(locator);
    		      if(afile.IsLocated())
    			astr->AssignCat(afile.Path());
    		    }
    		  returns.AddStringValue(astr);
    		}
    	      else if(getpathes)
    		{
    		  if(!afile.IsLocated()) afile.Locate(locator);
    		  if(afile.IsLocated())
    		    returns.AddStringValue(afile.Path());
    		}
    	      else if(getname)
    		{
    		  returns.AddStringValue(afile.Name());
    		}
    	      else if(getftype)
    		{
    		  returns.AddStringValue(afile.Type());
    		}
    	    }
    	}
      return 0;
    }
  if(gettype)
    {
      returns.AddStringValue(aunit.Type());
    }
  if(gettypecode)
    {
      returns.AddStringValue(new TCollection_HAsciiString(aunit.TypeKey()));
    }
  
  return 0;
}
//=======================================================================
void WOKAPI_UnitMake_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "[<unit>] [-f]  [-e|-s|-o] <step>] [-t <target>] \n";
  cerr << endl;
  cerr << "    Options are :" << endl;
  cerr << "       -f        : Force build (applies to all following steps)" << endl;
  cerr << "       -o <step> : Build only following step"                    << endl;
  cerr << "       -s <step> : Start build at following step"                << endl;
  cerr << "       -e <step> : End build at following step"                  << endl;
  cerr << endl;
  cerr << "       -t <target> : Build only specified target"                << endl;
  cerr << endl;
}

static void AddTargetToStep(WOKTools_DataMapOfHAsciiStringOfHSequenceOfHAsciiString& amap,
    			    const Handle(TCollection_HAsciiString)& step, 
    			    const Handle(TCollection_HAsciiString)& atarget)
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq;
  if(amap.IsBound(step))
    {
      aseq = amap.Find(step);
    }
  else
    {
      aseq = new TColStd_HSequenceOfHAsciiString;
    }
  
  aseq->Append(atarget);
  amap.Bind(step, aseq);
}

//=======================================================================
//function : UnitMake
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::UnitMake(const WOKAPI_Session& asession, 
    					  const Standard_Integer argc, const WOKTools_ArgTable& argv, 
    					  WOKTools_Return&  returns)
{
#if defined( WNT ) && defined( _DEBUG )
  _debug_break ( "WOKAPI_Command :: UnitMake" );
  OSD_Timer t;
  t.Start ();
#endif  // WNT && _DEBUG
  WOKTools_Options opts(argc, argv, "s:e:u:o:t:fhSL", WOKAPI_UnitMake_Usage, "hfS");
  Standard_Boolean force    = Standard_False;
  Standard_Boolean hasonly  = Standard_False;
  Standard_Boolean hasstart = Standard_False;
  Standard_Boolean hasend   = Standard_False;
  Standard_Boolean getsteps = Standard_False;
  Standard_Boolean logmsgs  = Standard_False;
  
  Handle(TCollection_HAsciiString) astr;
  Handle(TCollection_HAsciiString) astart;
  Handle(TCollection_HAsciiString) aend;
  TColStd_SequenceOfHAsciiString   onlys;
  WOKTools_DataMapOfHAsciiStringOfHSequenceOfHAsciiString targetmap;
  Handle(TCollection_HAsciiString) curstepcode;
  
  while(opts.More())
    {
      switch(opts.Option())
    	{
    	case 'o':
    	  // only
    	  if(hasstart||hasend)
    	    {
    	      ErrorMsg() << "WOKAPI_Unit::Make" << "Only option associated with start, end or until option is illegal" << endm;
    	      WOKAPI_UnitMake_Usage(argv[0]);
    	      return 1;
    	    }
    	  hasonly  = Standard_True;
    	  onlys.Append(opts.OptionArgument());
    	  curstepcode = opts.OptionArgument();
    	  break;
    	case 's':
    	  if(hasonly||hasstart)
    	    {
    	      ErrorMsg() << "WOKAPI_Unit::Make" << "Start option associated with start, only or until option is illegal" << endm;
    	      WOKAPI_UnitMake_Usage(argv[0]);
    	      return 1;
    	    }
    	  hasstart = Standard_True;
    	  astart   = opts.OptionArgument();
    	  curstepcode = opts.OptionArgument();
    	  break;
    	case 'e':
    	  if(hasonly||hasend)
    	    {
    	      ErrorMsg() << "WOKAPI_Unit::Make" << "End option associated to only or until option is illegal" << endm;
    	      WOKAPI_UnitMake_Usage(argv[0]);
    	      return 1;
    	    }
    	  hasend = Standard_True;
    	  aend   = opts.OptionArgument();
    	  curstepcode = opts.OptionArgument();
    	  break;
    	case 'f':
    	  // force
    	  force = Standard_True;
    	  break;
    	case 't':
    	  // no targets for the moment
    	  if(curstepcode.IsNull())
    	    {
    	      WarningMsg() << argv[0] << "No step code to associate target " << opts.OptionArgument() << " with : target ignored" << endm;
    	    }
    	  else
    	    {
    	      AddTargetToStep(targetmap, curstepcode, opts.OptionArgument());
    	    }
    	  break;
    	case 'S':
    	  getsteps = Standard_True;
    	  break;
    	case 'L':
    	  logmsgs  = Standard_True;
    	  break;
    	default:
    	  return 1;
    	}
      opts.Next();
    }
  
  if(opts.Failed()) return 1;
  
  switch(opts.Arguments()->Length())
    {
    case 0:
      break;
    case 1:
      astr = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_UnitMake_Usage(argv[0]);
      return 1;
    }
  
  WOKAPI_Unit aunit(asession,astr,Standard_False);
  
  if(!aunit.IsValid()) 
    {
      ErrorMsg() << argv[0] << "Could not determine unit : Specify unit in command line or use wokcd" << endm;
      return 1;
    }
  
  WOKAPI_Workbench abench(asession,astr,Standard_False);
  
  
  WOKAPI_BuildProcess aprocess;
  
  if(!aprocess.Init(abench))
    {
      ErrorMsg() << argv[0]
	<< "Could not initialize BuildProcess" << endm;
      return 1;
    }
  
  aprocess.Add(aunit);
  
  aprocess.SetForceFlag(force);
  
  Standard_Integer status = 0;
  if(getsteps)
    {
      WOKAPI_SequenceOfMakeStep steps;
      aprocess.UnitSteps(aunit, steps);
      Standard_Integer i;
      
      for(i=1; i<=steps.Length(); i++)
    	{
    	  returns.AddStringValue(steps.Value(i).Code());
    	}
      return 0;
    }
  else
    {
      Standard_Integer selected = 0;
      if(hasstart || hasend)
    	{
    	  // une seule selection a faire
    	  selected += aprocess.SelectOnSteps(aunit, astart, aend);
    	}
      else if( hasonly )
    	{
    	  for(Standard_Integer i=1; i<=onlys.Length(); i++)
    	    {
    	      selected += aprocess.SelectOnSteps(aunit, onlys.Value(i), onlys.Value(i)); 
    	    }
    	} 
      else
    	{
    	  selected += aprocess.SelectOnSteps(aunit, astart, aend);
    	}
      
      if(!targetmap.IsEmpty())
    	{
    	  WOKTools_DataMapIteratorOfDataMapOfHAsciiStringOfHSequenceOfHAsciiString anit(targetmap);
	  
    	  while(anit.More())
    	    {
    	      aprocess.ApplyTargetsToSteps(anit.Key(), anit.Value());
    	      anit.Next();
    	    }
    	}
      
      
      
      if(!aprocess.SelectedStepsNumber())
    	{
    	  InfoMsg() << argv[0] << "No step to execute : check command line" << endm;
    	}
      else
    	{
    	  aprocess.PrintBanner();
    	  status= aprocess.Execute(logmsgs);
    	}
      
    }
  
  
  // 
#if defined( WNT ) && defined( _DEBUG )
  t.Show ();
#endif    // WNT && _DEBUG
  
  return status;
}
//=======================================================================
void WOKAPI_UnitMakeInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "[<unit>] [-f]  [-e|-s|-o] <step>] [-t <target>] \n";
  cerr << endl;
  cerr << "    Options are :" << endl;
  cerr << "       -S            : unit steps" << endl;
  cerr << "       -i <stepcode> : step input" << endl;
  cerr << "       -o <stepcode> : step output" << endl;
  cerr << "       -O <stepcode> : out of date entities" << endl;
  cerr << "       -s <stepcode> : step status" << endl;
  cerr << "       -I <inputID>  : impact of modification" << endl;
  cerr << endl;
}

//=======================================================================
//function : UnitMakeInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::UnitMakeInfo(const WOKAPI_Session& , 
    					      const Standard_Integer argc, const WOKTools_ArgTable& argv, 
    					      WOKTools_Return&  )
{
  WOKTools_Options opts(argc, argv, "", WOKAPI_UnitMakeInfo_Usage, "");
  
  while(opts.More())
    {
      switch(opts.Option())
    	{
    	case 'S':
    	  break;
    	}
      opts.Next();
    }
  return 0;
}

//=======================================================================
void WOKAPI_UnitDestroy_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "  <name>\n";
  cerr << endl;
}


//=======================================================================
//function : UnitDestroy
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::UnitDestroy(const WOKAPI_Session& asession, 
  					     const Standard_Integer argc, const WOKTools_ArgTable& argv, 
  					     WOKTools_Return& )
{
  WOKTools_Options  opts(argc, argv, "D:hdP", WOKAPI_UnitDestroy_Usage);
  Handle(TCollection_HAsciiString)   name;
  
  while(opts.More())
    {
      switch(opts.Option())
  	{
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
      WOKAPI_UnitDestroy_Usage(argv[0]);
      return 1;
    }
  
  WOKAPI_Unit aunit(asession,name,Standard_False);
  
  if(!aunit.IsValid())
    {
      ErrorMsg() << "WOKAPI_Command::UnitDestroy"
	<< "Could not determine unit : Specify unit in command line or use wokcd" << endm;
      return 1;
    }
  
  aunit.Destroy();
  
  return 0;
}

