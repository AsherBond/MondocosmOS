// File:	MSAPI_Class.cxx
// Created:	Tue Sep 19 19:36:54 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ErrorHandler.hxx>

#include <MSAPI_Class.ixx>

#include <WOKTools_Options.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_Class.hxx>
#include <MS_Field.hxx>
#include <MS_HSequenceOfField.hxx>
#include <MS_MemberMet.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_Error.hxx>
#include <MS_HSequenceOfError.hxx>
#include <MS_GenClass.hxx>
#include <MS_InstClass.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSTool.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

 char *MSAPI_Class_Info_Options = "tdpNniIucCmrfMPTSe";

void MSAPI_Class_Info_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "\n";
  cerr << "        -t : type of class\n";
  cerr << "        -d : 1 if class is deferred 0 sinon\n";
  cerr << "        -p : 1 if class is private  0 sinon\n";
  cerr << "        -N : Nesting Class\n";
  cerr << "        -n : Is Nested\n";
  cerr << "        -i : Base class\n";
  cerr << "        -I : Full inheritance\n";
  cerr << "        -u : uses of class\n";
  cerr << "        -c : fields\n";
  cerr << "        -C : fields with their type\n";
  cerr << "        -m : methods\n";
  cerr << "        -r : raises of class\n";
  cerr << "        -f : friends\n";
  cerr << "        -M : friend methods\n";
  cerr << "        -P : Is Persistent class\n";
  cerr << "        -T : Is Transient class\n";
  cerr << "        -S : Is Storable class\n";
  cerr << "        -e : Is Empty (Incomplete) class\n";
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Info
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_Class::Info(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& values)
{
  WOKTools_Options opts(argc, argv, MSAPI_Class_Info_Options, MSAPI_Class_Info_Usage, MSAPI_Class_Info_Options);
  Standard_Boolean gettype       = Standard_False;
  Standard_Boolean deferred      = Standard_False;
  Standard_Boolean isprivate     = Standard_False;
  Standard_Boolean getnesting    = Standard_False;
  Standard_Boolean getisnested   = Standard_False;
  Standard_Boolean baseclass     = Standard_False;
  Standard_Boolean inheritance   = Standard_False;
  Standard_Boolean uses          = Standard_False;
  Standard_Boolean fields        = Standard_False;
  Standard_Boolean fullfields    = Standard_False;
  Standard_Boolean methods       = Standard_False;
  Standard_Boolean raises        = Standard_False;
  Standard_Boolean friends       = Standard_False;
  Standard_Boolean friendmethods = Standard_False;
  Standard_Boolean ispersistent  = Standard_False;
  Standard_Boolean istransient   = Standard_False;
  Standard_Boolean isstorable    = Standard_False;
  Standard_Boolean isempty       = Standard_False;
  Handle(TCollection_HAsciiString) name, astr;
  Standard_Integer i;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 't':
	  gettype       = Standard_True;
	  break;
	case 'd':
	  deferred      = Standard_True;
	  break;
	case 'p':
	  isprivate     = Standard_True;
	  break;
	case 'N':
	  getnesting    = Standard_True;
	  break;
	case 'n':
	  getisnested    = Standard_True;
	  break;
	case 'i':
	  baseclass     = Standard_True;
	  break;
	case 'I':
	  inheritance   = Standard_True;
	  break;
	case 'u':
	  uses          = Standard_True;
	  break;
	case 'c':
	  fields        = Standard_True;
	  break;
	case 'C':
	  fullfields    = Standard_True;
	  break;
	case 'm':
	  methods       = Standard_True;
	  break;
	case 'r':
	  raises        = Standard_True;
	  break;
	case 'f':
	  friends       = Standard_True;
	  break;
	case 'M':
	  friendmethods = Standard_True;
	  break;
	case 'P':
	  ispersistent  = Standard_True;
	  break;
	case 'T':
	  istransient   = Standard_True;
	  break;
	case 'S':
	  isstorable    = Standard_True;
	  break;
	case 'e':
	  isempty       = Standard_True;
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  
  if(opts.Arguments().IsNull() == Standard_True) {MSAPI_Class_Info_Usage(argv[0]); return 1;}
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      name = opts.Arguments()->Value(1);
      break;
    default:
      MSAPI_Class_Info_Usage(argv[0]);
      return 1;
    }
  
  if(WOKBuilder_MSTool::GetMSchema()->MetaSchema()->IsDefined(name) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a known type name" << endm;;
      return 1;
    }

  Handle(MS_Type) type = WOKBuilder_MSTool::GetMSchema()->MetaSchema()->GetType(name);

  if(type->IsKind(STANDARD_TYPE(MS_Class)) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a class name" << endm;
      return 1;
    }

  Handle(MS_Class) aclass = Handle(MS_Class)::DownCast(type);


  // OK le nom est bien un nom connu de classe 

  // pour deferred et private la classe n'a pas a etre complete dans le MS 


  if(gettype)
    {
      if(aclass->IsKind(STANDARD_TYPE(MS_Error))     == Standard_True) {values.AddStringValue("error");     return 0;}
      if(aclass->IsKind(STANDARD_TYPE(MS_StdClass))  == Standard_True) 
	{
	  Handle(MS_StdClass) astdclass = Handle(MS_StdClass)::DownCast(aclass);
	  
	  if(astdclass->GetMyCreator().IsNull())
	    {
	      values.AddStringValue("stdclass");
	      return 0;
	    }
	  else
	    {
	      values.AddStringValue("instclass");
	      return 0;
	    }
	}
      if(aclass->IsKind(STANDARD_TYPE(MS_GenClass))  == Standard_True) {values.AddStringValue("genclass");  return 0;}
      if(aclass->IsKind(STANDARD_TYPE(MS_InstClass)) == Standard_True) {values.AddStringValue("instclass"); return 0;}
      ErrorMsg() << argv[0] << "Unknown class type of " << aclass->FullName() << endm;
      return 1;
    }

  if(deferred||isprivate||ispersistent||istransient||isstorable||isempty||getisnested) 
    {
      if(deferred)     values.AddBooleanValue(aclass->Deferred());
      if(isprivate)    values.AddBooleanValue(aclass->Private());
      if(ispersistent) values.AddBooleanValue(aclass->IsPersistent());
      if(istransient)  values.AddBooleanValue(aclass->IsTransient());
      if(isstorable)   values.AddBooleanValue(aclass->IsStorable());
      if(isempty)      values.AddBooleanValue(aclass->Incomplete());
      if(getisnested)  values.AddBooleanValue(aclass->IsNested());
      return 0;
    }

  // pour les infos suivantes la classe doit etre complete 

  if(aclass->Incomplete() == Standard_True)
    {
      ErrorMsg() << argv[0] << "Class (" << name->ToCString() << ") is incomplete" << endm;
      return 1;
    }

  Handle(TColStd_HSequenceOfHAsciiString) aseq;

  if(baseclass)
    {
      aseq =  aclass->GetInheritsNames();

      if(aseq->Length() > 0)
	values.AddStringValue(aseq->Value(1));
      return 0;
    }

  if(getnesting)
    {
      astr =  aclass->GetNestingClass();
      if(astr.IsNull() == Standard_False)
	{
	  values.AddStringValue(astr);
	}
      return 0;
    }

  if(fields||fullfields)
    {
      Handle(MS_HSequenceOfField) aseq = aclass->GetFields();

      for(i=1; i<=aseq->Length(); i++)
	{
	  if(fullfields)
	    {
	      astr = new TCollection_HAsciiString(aseq->Value(i)->TYpe());
	      astr->AssignCat(" ");
	      astr->AssignCat(aseq->Value(i)->FullName());
	      values.AddStringValue(astr);
	    }
	  else
	    {
	      values.AddStringValue(aseq->Value(i)->FullName());
	    }
	}
      return 0;
    }
  
  if(inheritance||uses||friends||friendmethods||raises)
    {

      if(inheritance)   aseq = aclass->GetFullInheritsNames();
      if(uses)          aseq = aclass->GetUsesNames();
      if(friends)       aseq = aclass->GetFriendsNames();
      if(friendmethods) aseq = aclass->GetFriendMets();
      if(raises)        aseq = aclass->GetRaises();

      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}
      return 0;
    }
  
  if(methods)
    {
      Handle(MS_HSequenceOfMemberMet) aseq = aclass->GetMethods();

      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i)->FullName());
	}
      return 0;
    }
  return 0;
}

