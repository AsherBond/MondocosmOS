// File:	WOKMake_StepFile.cxx
// Created:	Tue Jun 18 10:08:26 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <WOKMake_StepFile.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKMake_StepFile
//purpose  : 
//=======================================================================
WOKMake_StepFile::WOKMake_StepFile()
  : myattr(0), mystatus(WOKMake_Undetermined)
{
  SetPhysicFlag(Standard_True);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKMake_StepFile
//purpose  : 
//=======================================================================
WOKMake_StepFile::WOKMake_StepFile(const Handle(TCollection_HAsciiString)& anid,
				   const Handle(WOKernel_File)&            afile, 
				   const Handle(WOKBuilder_Entity)&        abuildent,
				   const Handle(WOKUtils_Path)&            aoldpath)
  : myattr(0), myfile(afile), myid(anid), myent(abuildent), mylastpath(aoldpath), mystatus(WOKMake_Undetermined)
{
  SetPhysicFlag(Standard_True);
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetID
//purpose  : 
//=======================================================================
void WOKMake_StepFile::SetID(const Handle(TCollection_HAsciiString)& anid)
{
  myid = anid;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetFile
//purpose  : 
//=======================================================================
void WOKMake_StepFile::SetFile(const Handle(WOKernel_File)& afile)
{
  myfile = afile;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetBuilderEntity
//purpose  : 
//=======================================================================
void WOKMake_StepFile::SetBuilderEntity(const Handle(WOKBuilder_Entity)& anent)
{
  myent = anent;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetLastPath
//purpose  : 
//=======================================================================
void WOKMake_StepFile::SetLastPath(const Handle(WOKUtils_Path)& apath)
{
  mylastpath = apath;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetLocateFlag
//purpose  : 
//=======================================================================
void WOKMake_StepFile::SetLocateFlag(const Standard_Boolean aflag)
{
  if(aflag)
    myattr |= STEPFILE_LOCATE;
  else
    myattr &= (STEPFILE_LOCATE ^ myattr);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetStatus
//purpose  : 
//=======================================================================
void WOKMake_StepFile::SetStatus(const WOKMake_FileStatus astatus)
{
  mystatus = astatus;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetPhysicFlag
//purpose  : 
//=======================================================================
void WOKMake_StepFile::SetPhysicFlag(const Standard_Boolean aflag)
{
  if(aflag)
    myattr |= STEPFILE_PHYSIC;
  else
    myattr &= (STEPFILE_PHYSIC ^ myattr);
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetStepID
//purpose  : 
//=======================================================================
void WOKMake_StepFile::SetStepID(const Standard_Boolean aflag)
{
  if(aflag)
    myattr |= STEPFILE_STEPID;
  else
    myattr &= (STEPFILE_STEPID ^ myattr);
}

