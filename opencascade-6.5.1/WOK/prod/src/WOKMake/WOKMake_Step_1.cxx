// File:	WOKMake_Step_1.cxx
// Created:	Mon Dec  4 18:26:15 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ProgramError.hxx>

#include <WOKernel_FileType.hxx>

#include <WOKMake_Step.jxx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : InLocator
//purpose  : 
//=======================================================================
Handle(WOKernel_Locator) WOKMake_Step::InLocator() const 
{
  return myprocess->Locator();
}
//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutLocator
//purpose  : 
//=======================================================================
Handle(WOKernel_Locator) WOKMake_Step::OutLocator() const
{
  return myprocess->Locator();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : StepOutputID
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_Step::StepOutputID(const Handle(TCollection_HAsciiString)& aunit,  
							    const Handle(TCollection_HAsciiString)& acode, 
							    const Handle(TCollection_HAsciiString)& asubcode) 
{
  Handle(TCollection_HAsciiString) id = new TCollection_HAsciiString(aunit);
  id->AssignCat(":");
  id->AssignCat(acode);
  if(!asubcode.IsNull())
    {
      id->AssignCat(":");
      id->AssignCat(asubcode);
    }
  return id;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : StepOutputID
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_Step::StepOutputID(const Handle(TCollection_HAsciiString)& aunit,  
							    const Handle(TCollection_HAsciiString)& acode) 
{
  Handle(TCollection_HAsciiString) id = new TCollection_HAsciiString(aunit);
  id->AssignCat(":");
  id->AssignCat(acode);
  return id;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : StepOutputID
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_Step::StepOutputID() const
{
  return WOKMake_Step::StepOutputID(Unit()->Name(), Code(), SubCode());
}

//=======================================================================
//function : InputFilesFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_Step::InputFilesFileName() const
{
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString(Unit()->Name());
  Handle(TCollection_HAsciiString) stepcode = new TCollection_HAsciiString(Code());

  stepcode->ChangeAll('.', '_');

  astr->AssignCat("_");
  astr->AssignCat(stepcode);

  if(!SubCode().IsNull())
    {
      Handle(TCollection_HAsciiString) subcode = new TCollection_HAsciiString(SubCode());
      subcode->ChangeAll('.', '_');

      astr->AssignCat("_");
      astr->AssignCat(subcode);
    }
  astr->AssignCat(".In");
  return astr;  
}

//=======================================================================
//function : DepItemsFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_Step::DepItemsFileName() const
{
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString(Unit()->Name());
  Handle(TCollection_HAsciiString) stepcode = new TCollection_HAsciiString(Code());
  
  stepcode->ChangeAll('.', '_');
  astr->AssignCat("_");
  astr->AssignCat(stepcode);

  if(!SubCode().IsNull())
    {
      Handle(TCollection_HAsciiString) subcode = new TCollection_HAsciiString(SubCode());
      subcode->ChangeAll('.', '_');

      astr->AssignCat("_");
      astr->AssignCat(subcode);
    }

  astr->AssignCat(".Dep");
  return astr;  
}

//=======================================================================
//function : OutputFilesFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_Step::OutputFilesFileName() const
{
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString(Unit()->Name());
  Handle(TCollection_HAsciiString) stepcode = new TCollection_HAsciiString(Code());
  
  stepcode->ChangeAll('.', '_');
  astr->AssignCat("_");
  astr->AssignCat(stepcode);

  if(!SubCode().IsNull())
    {
      Handle(TCollection_HAsciiString) subcode = new TCollection_HAsciiString(SubCode());
      subcode->ChangeAll('.', '_');

      astr->AssignCat("_");
      astr->AssignCat(subcode);
    }

  astr->AssignCat(".Out");
  return astr;  
}

//=======================================================================
//function : LogFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_Step::LogFileName() const
{
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString(Unit()->Name());
  Handle(TCollection_HAsciiString) stepcode = new TCollection_HAsciiString(Code());
  
  stepcode->ChangeAll('.', '_');
  astr->AssignCat("_");
  astr->AssignCat(stepcode);

  if(!SubCode().IsNull())
    {
      Handle(TCollection_HAsciiString) subcode = new TCollection_HAsciiString(SubCode());
      subcode->ChangeAll('.', '_');

      astr->AssignCat("_");
      astr->AssignCat(subcode);
    }

  astr->AssignCat(".Log");
  
  return astr;  
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFile
//purpose  : 
//=======================================================================
Handle(WOKernel_File) WOKMake_Step::AdmFile(const Handle(TCollection_HAsciiString)& aname) const
{
  Handle(WOKernel_File) result;
  
  result = new WOKernel_File(aname, Unit(), Unit()->GetFileType(AdmFileType()));
  result->GetPath();
  return result;
}
//=======================================================================
//Author   : Jean Gautier (jga)
//function : LocateAdmFile
//purpose  : 
//=======================================================================
Handle(WOKernel_File) WOKMake_Step::LocateAdmFile(const Handle(WOKernel_Locator)& aloc, 
						  const Handle(TCollection_HAsciiString)& aname) const
{
  Handle(WOKernel_File) result;
  
  if(!aloc.IsNull())
    {
      result = aloc->Locate(Unit()->Name(), AdmFileType(), aname);
    }
  return result;
}


//=======================================================================
//function : IsOrIsSubStepOf
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_Step::IsOrIsSubStepOf(const Handle(TCollection_HAsciiString)& acode) const
{
  Handle(TCollection_HAsciiString) astr;

  if(Code()->IsSameString(acode))
    {
      return Standard_True;
    }
  astr = new TCollection_HAsciiString(acode);
  astr->AssignCat(".");
  
  if(Code()->Search(astr) == 1)
    {
      // le nom d'etape commence par acode.
      return Standard_True;
    }
  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : UniqueName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_Step::UniqueName(const Handle(WOKernel_DevUnit)& aunit, 
							  const Handle(TCollection_HAsciiString)& acode,
							  const Handle(TCollection_HAsciiString)& asubcode) 
{
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString(aunit->Name());
  
  result->AssignCat(":");
  result->AssignCat(acode);
  if(!asubcode.IsNull())
    {
      result->AssignCat(":");
      result->AssignCat(asubcode);
    }
  return result;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : UniqueName
//purpose  : 
//=======================================================================
void WOKMake_Step::SplitUniqueName(const Handle(TCollection_HAsciiString)& anid,
				   Handle(TCollection_HAsciiString)& auname,
				   Handle(TCollection_HAsciiString)& acode,
				   Handle(TCollection_HAsciiString)& asubcode) 
{
  auname   = anid->Token(":",1);
  acode    = anid->Token(":",2);
  asubcode = anid->Token(":",3);

  if(auname->IsEmpty())   auname.Nullify();
  if(acode->IsEmpty())    acode.Nullify();
  if(asubcode->IsEmpty()) asubcode.Nullify();
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : UniqueName
//purpose  : 
//=======================================================================
const Handle(TCollection_HAsciiString)& WOKMake_Step::UniqueName() 
{
  if( !myunique.IsNull() ) return myunique;
  return (myunique = WOKMake_Step::UniqueName(Unit(), Code(), SubCode()));
}

//=======================================================================
//function : Code
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_Step::Code() const
{
  return mycode;
}

//=======================================================================
//function : IsHidden
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_Step::IsHidden() const
{
  return myhidden;
}
//=======================================================================
//function : IsChecked
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_Step::IsChecked() const
{
  return mycheck;
}

//=======================================================================
//function : SubCode
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_Step::SubCode() const
{
  return mysubcode;
}

//=======================================================================
//function : SetSubCode
//purpose  : 
//=======================================================================
void WOKMake_Step::SetSubCode(const Handle(TCollection_HAsciiString)& acode) 
{
  mysubcode = acode;
}

//=======================================================================
//function : Status
//purpose  : 
//=======================================================================
WOKMake_Status WOKMake_Step::Status() const
{
  return mystatus;
}

void WOKMake_Step::SetUptodate()    {mystatus = WOKMake_Uptodate;}
void WOKMake_Step::SetSucceeded()   {mystatus = WOKMake_Success;}
void WOKMake_Step::SetIncomplete()  {mystatus = WOKMake_Incomplete;}
void WOKMake_Step::SetFailed()      {mystatus = WOKMake_Failed;}
void WOKMake_Step::SetUnprocessed() {mystatus = WOKMake_Unprocessed;}
void WOKMake_Step::SetStatus(const WOKMake_Status astatus) {mystatus = astatus;}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetOutputDir
//purpose  : 
//=======================================================================
void WOKMake_Step::SetOutputDir(const Handle(WOKUtils_Path)& apath) {myoutputdir = apath;}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDir
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) WOKMake_Step::OutputDir() 
{
  if(myoutputdir.IsNull()) 
    {
      Handle(WOKernel_File) afile = new WOKernel_File(Unit(), Unit()->GetFileType(OutputDirTypeName()));
      afile->GetPath();
      
      myoutputdir = afile->Path();
    }
  return myoutputdir;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsDBMSDependent
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_Step::IsDBMSDependent() const
{
  Handle(TCollection_HAsciiString) admtype = AdmFileType();
  Handle(WOKernel_FileType) atype          = Unit()->GetFileType(admtype);

  if(atype.IsNull())
    {
      Standard_ProgramError::Raise("WOKMake_Step::IsDBMSDependent");
    }
  
  return atype->IsDBMSDependent();

}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsStationDependent
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_Step::IsStationDependent() const
{
  Handle(TCollection_HAsciiString) admtype = AdmFileType();
  Handle(WOKernel_FileType) atype          = Unit()->GetFileType(admtype);

  if(atype.IsNull())
    {
      Standard_ProgramError::Raise("WOKMake_Step::IsStationDependent");
    }
  
  return atype->IsStationDependent();

}

//=======================================================================
//function : PrecedenceSteps
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKMake_Step::PrecedenceSteps() const
{
  return myprecsteps;
}

//=======================================================================
//function : SetPrecedenceSteps
//purpose  : 
//=======================================================================
void WOKMake_Step::SetPrecedenceSteps(const Handle(TColStd_HSequenceOfHAsciiString)& steps)
{
  myprecsteps = steps;
}

//=======================================================================
//function : SetOptions
//purpose  : 
//=======================================================================
void WOKMake_Step::SetOptions(const Handle(WOKMake_HSequenceOfStepOption)& opts)
{
  myoptions = opts;
}
Handle(WOKMake_HSequenceOfStepOption) WOKMake_Step::Options() const {return myoptions;}


//=======================================================================
//function : SetTargets
//purpose  : 
//=======================================================================
void WOKMake_Step::SetTargets(const Handle(TColStd_HSequenceOfHAsciiString)& targets)
{
  mytargets = targets;
}
Handle(TColStd_HSequenceOfHAsciiString) WOKMake_Step::Targets() const {return mytargets;}



//=======================================================================
//function : IsToExecute
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_Step::IsToExecute() const {return myexecflag;}
void WOKMake_Step::DoExecute()                     {myexecflag = Standard_True;}
void WOKMake_Step::DontExecute()                   {myexecflag = Standard_False;}
