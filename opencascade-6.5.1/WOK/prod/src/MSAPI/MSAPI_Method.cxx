// File:	MSAPI_Method.cxx
// Created:	Tue Sep 19 22:14:50 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ErrorHandler.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_MemberMet.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_ExternMet.hxx>
#include <MS_HSequenceOfExternMet.hxx>
#include <MS_Param.hxx>
#include <MS_HArray1OfParam.hxx>

#include <WOKTools_Options.hxx>

#include <MSAPI_Method.ixx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSTool.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

char *MSAPI_Method_Info_Options = "tnaprRicAfd";

void  MSAPI_Method_Info_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "<options> <method>\n";
  cerr << "    -t : type of method (externmet, membermet)\n";
  cerr << "    -n : name of method\n";
  cerr << "    -a : parameters\n";
  cerr << "    -p : private ?\n";
  cerr << "    -r : return type\n";
  cerr << "    -R : raises\n";
  cerr << "    -i : inline ?\n";
  cerr << "    -c : const return ?\n";
  cerr << "    -A : alias ?\n";
  cerr << "    -f : reF return ?\n";
  cerr << "    -d : destructor ?\n";
  return;
}


Standard_Integer MSAPI_Method::Info(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& values)
{
  WOKTools_Options opts(argc, argv, MSAPI_Method_Info_Options, MSAPI_Method_Info_Usage, MSAPI_Method_Info_Options);
  Standard_Boolean gettype   = Standard_False;
  Standard_Boolean getname   = Standard_False;
  Standard_Boolean params    = Standard_False;
  Standard_Boolean isprivate = Standard_False;
  Standard_Boolean returns   = Standard_False;
  Standard_Boolean raises    = Standard_False;
  Standard_Boolean isinline  = Standard_False;
  Standard_Boolean constret  = Standard_False;
  Standard_Boolean alias     = Standard_False;
  Standard_Boolean refreturn = Standard_False;
  Standard_Boolean destructs = Standard_False;
  Handle(TCollection_HAsciiString) name;
  Standard_Integer i;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 't':
	  gettype   = Standard_True;
	  break;
	case 'n':
	  getname   = Standard_True;
	case 'a':
	  params = Standard_True;
	  break;
	case 'p':
	  isprivate = Standard_True;
	  break;
	case 'r':
	  returns = Standard_True;
	  break;
	case 'R':
	  raises = Standard_True;
	  break;
	case 'i':
	  isinline = Standard_True;
	  break;
	case 'c':
	  constret = Standard_True;
	  break;
	case 'A':
	  alias = Standard_True;
	  break;
	case 'f':
	  refreturn = Standard_True;
	  break;
	case 'd':
	  destructs = Standard_True;
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  if(opts.Arguments().IsNull() == Standard_True) {MSAPI_Method_Info_Usage(argv[0]); return 1;}
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      name = opts.Arguments()->Value(1);
      break;
    default:
      MSAPI_Method_Info_Usage(argv[0]);
      return 1;
    }
  
  if(WOKBuilder_MSTool::GetMSchema()->MetaSchema()->IsMethod(name) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a known method name" << endm;
      return 1;
    }

  Handle(MS_Method) method = WOKBuilder_MSTool::GetMSchema()->MetaSchema()->GetMethod(name);

  if(isprivate||isinline||constret||refreturn||destructs)
    {
      Standard_Boolean retvalue = Standard_False;
      
      if(isprivate) retvalue = method->Private();
      if(isinline)  retvalue = method->IsInline();
      if(constret)  retvalue = method->IsConstReturn();
      if(alias)     retvalue = method->IsAlias().IsNull();
      if(refreturn) retvalue = method->IsRefReturn();
      if(destructs) retvalue = method->IsDestructor();
      
      if(retvalue) values.AddStringValue("1");
      else         values.AddStringValue("0");
      return 0;
    } 

  if(alias)
    {
      if(method->IsAlias().IsNull())
	{
	  values.AddStringValue(new TCollection_HAsciiString);
	}
      else
	{
	  values.AddStringValue(method->IsAlias());
	}
      return 0;
    }

  if(getname)
    {
      values.AddStringValue(method->Name());
      return 0;
    }

  if(returns)
    {
      if(method->Returns().IsNull() == Standard_False)
	{
	  values.AddStringValue(method->Returns()->TypeName());
	}
      return 0;
    }

  if(gettype)
    {
      if(method->IsKind(STANDARD_TYPE(MS_MemberMet))) {values.AddStringValue("membermet");return 0;}
      if(method->IsKind(STANDARD_TYPE(MS_ExternMet))) {values.AddStringValue("externmet");return 0;}
      ErrorMsg() << argv[0] << "Unknown method type of " << method->FullName() << endm;
      return 1;
    }

  if(params)
    {
      Handle(MS_HArray1OfParam) aseq = method->Params();
      Handle(TCollection_HAsciiString) astr;

      if(!aseq.IsNull()) 
	{
	  for(i=1; i<=aseq->Length(); i++)
	    {
	      astr = new TCollection_HAsciiString(aseq->Value(i)->TypeName());
	      astr->AssignCat(" ");
	      astr->AssignCat(aseq->Value(i)->FullName());
	      values.AddStringValue(astr);
	    }
	}
      return 0;
    }
  
  return 0;
}

