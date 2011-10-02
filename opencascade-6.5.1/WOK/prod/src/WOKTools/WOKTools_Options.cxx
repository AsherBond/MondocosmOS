// File:	WOKTools_Options.cxx
// Created:	Tue Aug  1 20:38:41 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif

#include <string.h>
#include <stdlib.h>
#include <stdio.h> /* EOF */

#include <WOKTools_Options.ixx>

#include <WOKTools_Define.hxx>
#include <WOKTools_Messages.hxx>

#include <TCollection_AsciiString.hxx>

#if defined(HAVE_GETOPT_H) && !defined(HAVE_UNISTD_H)
# include <getopt.h>
#elif !defined( WNT )
extern char *optarg;
extern int  optind;
#else
extern "C" Standard_IMPORT char *optarg;
extern "C" Standard_IMPORT int   optind;
extern "C" Standard_IMPORT int   getopt( int, char**, char* );
#endif  // WNT

//=======================================================================
//function : WOKTools_Options
//purpose  : 
//=======================================================================
WOKTools_Options::WOKTools_Options(const Standard_Integer argc, 
			       const WOKTools_ArgTable& argv, 
			       const Standard_CString opts, 
			       const WOKTools_PUsage usage,
			       const Standard_CString excl)

{
  myargc    = argc;
  myargv    = argv;
  myusage   = usage;
  mymore    = Standard_True;
  mydefines = new WOKTools_HSequenceOfDefine;
  myargs    = new TColStd_HSequenceOfHAsciiString;
  myerrflg  = Standard_False;
#if ( !defined( WNT ) && !defined( __GNUC__ ) ) || defined(__APPLE__) || defined(__FreeBSD__)
  optind    = 1;
#else
  optind    = 0;
#endif  // WNT

  myoptions = new TCollection_HAsciiString(opts);
  myexclopt = new TCollection_HAsciiString(excl);
  myexclflg = '\0';
  Next();

  // s'il il n'y a qu'une option
  if(mycuropt == (Standard_Byte ) EOF) 
      mymore  = Standard_False;
}

//=======================================================================
//function : Next
//purpose  : 
//=======================================================================
void WOKTools_Options::Next()
{
  if (!mymore) return;

  mycuropt = getopt(myargc, myargv, (char *)myoptions->ToCString());

  if(mycuropt == (Standard_Byte ) EOF)
    {
      for( ; (optind < myargc) && (myargv[optind][0] != '-'); optind++)
	{
	  myargs->Append(new TCollection_HAsciiString(myargv[optind]));
	}

      if(optind >= myargc)
	{
	  // il n'y en a plus
	  mymore = Standard_False;
	}
      else 
	{
	  if(!strcmp(myargv[optind], "-"))
	    {
	      mymore = Standard_False;
	      myerrflg = Standard_True;
	      ErrorMsg() << myargv[0] << "option - is illegal" << endm;
	      ErrorMsg() << myargv[0] << endm;
	      if(myusage != NULL) (*myusage)(myargv[0]);
	    }
	  else
	    Next();
	}
    }
  else
    {
      if(myexclopt->Location(1, mycuropt, 1, myexclopt->Length()) != 0 )
	{
	  if(myexclflg=='\0') 
	    {
	      myexclflg = mycuropt;
	    }
	  else
	    {
	      ErrorMsg() << myargv[0] << "Option " << (char)mycuropt << " is exclusive with : " << (char)myexclflg << endm;
	      ErrorMsg() << myargv[0] << endm;
	      if(myusage != NULL) (*myusage)(myargv[0]);
	      myerrflg = Standard_True;
	    }
	}

      switch(mycuropt)
	{
	case 'h':
	  if(myusage != NULL) (*myusage)(myargv[0]);
	  myerrflg = Standard_True;
	  mymore   = Standard_False;

	  break;
	case 'D':
	  {
	    Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString(optarg);
	    Handle(TCollection_HAsciiString) asubstr;
	    WOKTools_Define anitem;
	    Standard_Integer i=1;
	    
	    asubstr = astr->Token(",", i);

	    while(asubstr->IsEmpty() == Standard_False)
	      {
		anitem.GetDefineIn(asubstr);
		Standard_Boolean othervalue = Standard_True;

		while(othervalue && !asubstr->IsEmpty())
		  {
		    i++;
		    asubstr = astr->Token(",", i);
		    
		    Standard_Character c;
		    Standard_Integer   j;
		    othervalue = Standard_True;
		    
		    for (j=1; j<= asubstr->Length() && othervalue; j++)
		      {
			c = asubstr->Value(j);
			if (IsEqual(c,'='))
			  othervalue = Standard_False;
		      }
		    
		    if (othervalue && !asubstr->IsEmpty())
		      {
			anitem.AddValue(asubstr);
		      }
		  }
		mydefines->Append(anitem);
	      }
	  }
	  if(!myerrflg) Next();
	  break;
	case '?':
	  myerrflg = Standard_True;
	  mymore   = Standard_False;
	  if(myusage != NULL) (*myusage)(myargv[0]);
	  break;
	default:
	  if(optarg != NULL)
	    {
	      mycurarg = new TCollection_HAsciiString(optarg);
	      Handle(TCollection_HAsciiString) asubstr;
	      Standard_Integer i=1;

	      asubstr = mycurarg->Token(",", i);
	      mycurlistarg = new TColStd_HSequenceOfHAsciiString;
	      while(asubstr->IsEmpty() == Standard_False)
		{
		  mycurlistarg->Append(asubstr);
		  i++;
		  asubstr = mycurarg->Token(",", i);
		}
	    }
	}
    }
  
  if (myerrflg) // vider 
    {
//      Standard_Character acuropt = '\0';
      Standard_Byte acuropt = 0;
      while(acuropt != (Standard_Byte ) EOF)
	  acuropt = getopt(myargc, myargv, (char *)myoptions->ToCString());
    }

  return;
}

//=======================================================================
//function : Option
//purpose  : 
//=======================================================================
Standard_Character WOKTools_Options::Option() const 
{
  return mycuropt;
}

//=======================================================================
//function : OptionArgument
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKTools_Options::OptionArgument() const
{
  return mycurarg;
}

//=======================================================================
//function : OptionListArgument
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKTools_Options::OptionListArgument() const
{
  return mycurlistarg;
}

//=======================================================================
//function : More
//purpose  : 
//=======================================================================
Standard_Boolean WOKTools_Options::More() const 
{
  return mymore;
}

//=======================================================================
//function : Defines
//purpose  : 
//=======================================================================
Handle(WOKTools_HSequenceOfDefine) WOKTools_Options::Defines() const 
{
  return mydefines;
}

//=======================================================================
//function : Define
//purpose  : 
//=======================================================================
void WOKTools_Options::Define(const Handle(TCollection_HAsciiString)& aname, const Handle(TCollection_HAsciiString)& avalue)
{
  if(aname.IsNull())
    { 
      ErrorMsg() << "WOKTools_Options::Define"
	       << "Invalid Null name for define" << endm;
      return;
    }
  if(avalue.IsNull())
    { 
      ErrorMsg() << "WOKTools_Options::Define"
	       << "Invalid Null value for define" << endm;
      return;
    }

  if(mydefines.IsNull()) mydefines = new WOKTools_HSequenceOfDefine;
  
  mydefines->Append(WOKTools_Define(aname, avalue));
  return;
}

//=======================================================================
//function : AddPrefixToDefines
//purpose  : 
//=======================================================================
void WOKTools_Options::AddPrefixToDefines(const Handle(TCollection_HAsciiString)& aname)
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) aprefix = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString) astr;

  aprefix->AssignCat("%");
  aprefix->AssignCat(aname);
  aprefix->AssignCat("_");

  for(i=1; i<= mydefines->Length() ; i++)
    {
      astr = new TCollection_HAsciiString(aprefix);
      astr->AssignCat(mydefines->Value(i).Name());
      mydefines->ChangeValue(i).SetName(astr);
    }
}

//=======================================================================
//function : Arguments
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKTools_Options::Arguments() const
{
  return myargs;
}

//=======================================================================
//function : Failed
//purpose  : 
//=======================================================================
Standard_Boolean WOKTools_Options::Failed() const 
{
  return myerrflg;
}
                                                                                                                                              
