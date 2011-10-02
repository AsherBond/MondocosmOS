// File:	WOKMake_TriggerStep.cxx
// Created:	Tue Oct  8 14:24:59 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <Standard_ProgramError.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_StringValue.hxx>
#include <WOKTools_ReturnType.hxx>

#include <WOKUtils_Triggers.hxx>

#include <WOKernel_File.hxx>


#include <WOKMake_AdmFileTypes.hxx>

#include <WOKMake_TriggerStep.ixx>


//=======================================================================
//function : WOKMake_TriggerStep
//purpose  : 
//=======================================================================
WOKMake_TriggerStep::WOKMake_TriggerStep(const Handle(WOKMake_BuildProcess)& aprocess,
					 const Handle(WOKernel_DevUnit)& aunit, 
					 const Handle(TCollection_HAsciiString)& acode, 
					 const Standard_Boolean checked, 
					 const Standard_Boolean hidden) 
  : WOKMake_Step(aprocess,aunit, acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CurrentTriggerStep
//purpose  : 
//=======================================================================
Handle(WOKMake_TriggerStep)& WOKMake_TriggerStep::CurrentTriggerStep()
{
  static Handle(WOKMake_TriggerStep) TheCurStep;
  return TheCurStep;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetName
//purpose  : 
//=======================================================================
void WOKMake_TriggerStep::SetName(const Handle(TCollection_HAsciiString)& aname)
{
  myname = aname;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_TriggerStep::Name() const
{
  return myname;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_TriggerStep::AdmFileType() const 
{
  Handle(TCollection_HAsciiString) result;
  Handle(TCollection_HAsciiString) trigname = new TCollection_HAsciiString(Name());

  trigname->AssignCat(":AdmFileType");

  WOKUtils_Trigger execute;

  execute(trigname) << endt >> result;
  
  switch(execute.Status())
    {
    case WOKUtils_Unknown:
    case WOKUtils_NotSetted:
      {
	static Handle(TCollection_HAsciiString) sresult = new TCollection_HAsciiString((char*)STADMFILE);
	result = sresult;
      }
      break;
    case WOKUtils_Succeeded:
      break;
    case WOKUtils_Failed:
      //SetFailed();
      break;
    }

  return result; 
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_TriggerStep::OutputDirTypeName() const 
{
  Handle(TCollection_HAsciiString) result;
  Handle(TCollection_HAsciiString) trigname = new TCollection_HAsciiString(Name());

  trigname->AssignCat(":OutputDirTypeName");

  WOKUtils_Trigger execute;

  execute(trigname) << endt >> result;
  
  switch(execute.Status())
    {
    case WOKUtils_Unknown:
    case WOKUtils_NotSetted:
      {
	static Handle(TCollection_HAsciiString) sresult = new TCollection_HAsciiString((char*)STTMPDIR);
	result = sresult;
      }
      break;
    case WOKUtils_Succeeded:
      break;
    case WOKUtils_Failed:
      //SetFailed();
      break;
    }

  return result; 
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_TriggerStep::HandleInputFile(const Handle(WOKMake_InputFile)& infile) 
{
  WOKUtils_Trigger execute;
  Standard_Boolean b=Standard_False;

  Handle(TCollection_HAsciiString) trigname = new TCollection_HAsciiString(Name());

  trigname->AssignCat(":HandleInputFile");

  execute(trigname) << infile->ID() << endt >> b;

  switch ( execute.Status())
    {
    case WOKUtils_Succeeded:
      if(b)
	{
	  infile->SetBuilderEntity(BuilderEntity(infile->File()));
	}
      break;
    case WOKUtils_Unknown:
    case WOKUtils_Failed:
    case WOKUtils_NotSetted:
      break;
    }

  return b;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKMake_TriggerStep::Init()
{
  
  CurrentTriggerStep() = Handle(WOKMake_TriggerStep)(this);

  WOKUtils_Trigger execute;
  
  // for Tcl8.0
  // execute("namespace") << "eval" << Name() << "{}" << endt;


  Handle(TCollection_HAsciiString) trigfile = new TCollection_HAsciiString(Name());
  trigfile->AssignCat(".tcl");

  Handle(WOKUtils_Path) apath = Unit()->Params().SearchFile(trigfile);

  if(apath.IsNull())
    {
      WarningMsg() << "WOKMake_TriggerStep::Init" 
	<< "Could not find file associated with trigger step " << Name() << endm;
      WarningMsg() << "WOKMake_TriggerStep::Init" 
	<< "Nothing done" << endm;
    }

  execute(trigfile, Unit()->Params(), WOKTools_TclInterp) << endt;
  
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddInputFile
//purpose  : 
//=======================================================================
void WOKMake_TriggerStep::AddInputFile(const Handle(WOKMake_InputFile)& infile) 
{
  if(infile.IsNull())
    Standard_ProgramError::Raise("WOKMake_TriggerStep::AddInputFile : Null Input");

  myinflow.Add(infile->ID(), infile);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetInputFile
//purpose  : 
//=======================================================================
Handle(WOKMake_InputFile) WOKMake_TriggerStep::GetInputFile(const Handle(TCollection_HAsciiString)& anid) const
{
  Handle(WOKMake_InputFile) nullhandle;

  if(myinflow.Contains(anid))
    return myinflow.FindFromKey(anid);
  return nullhandle;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddOutputFile
//purpose  : 
//=======================================================================
void WOKMake_TriggerStep::AddOutputFile(const Handle(WOKMake_OutputFile)& outfile) 
{
  if(outfile.IsNull())
    Standard_ProgramError::Raise("WOKMake_TriggerStep::AddOutputFile : Null Input");

  myoutflow.Add(outfile->ID(), outfile);
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetoutputFile
//purpose  : 
//=======================================================================
Handle(WOKMake_OutputFile) WOKMake_TriggerStep::GetOutputFile(const Handle(TCollection_HAsciiString)& anid) const
{
  Handle(WOKMake_OutputFile) nullhandle;

  if(myoutflow.Contains(anid))
    return myoutflow.FindFromKey(anid);
  return nullhandle;
}

//=======================================================================
//function : Execute
//purpose  :
//=======================================================================
void WOKMake_TriggerStep::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Standard_Integer i;
  Standard_Boolean status;
  WOKUtils_Trigger execute;
  
  Handle(TCollection_HAsciiString) trigname = new TCollection_HAsciiString(Name());
  trigname->AssignCat(":Execute");

  execute(trigname) << Unit()->UserPathName();

  for(i=1; i<=execlist->Length(); i++)
    {
      execute << execlist->Value(i)->ID();
    }

  execute << endt >> status;

  switch(execute.Status())
    {
    case WOKUtils_Failed:
      SetFailed();
      break;
    case WOKUtils_Succeeded:
      if(status)
	SetFailed();
      else
	SetSucceeded();
      break;
    case WOKUtils_NotSetted:
      SetUnprocessed();
      break;
    default:
      break;
    }
  return;
}




//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKMake_TriggerStep::Terminate()
{
  WOKMake_TriggerStep::CurrentTriggerStep() = Handle(WOKMake_TriggerStep)();
}

