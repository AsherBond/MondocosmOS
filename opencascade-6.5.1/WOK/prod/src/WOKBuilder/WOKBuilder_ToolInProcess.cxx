// File:	WOKBuilder_ToolInProcess.cxx
// Created:	Wed Aug 23 20:10:28 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_NotImplemented.hxx>

#include <WOKBuilder_ToolInProcess.ixx>

#include <WOKTools_Messages.hxx>

//=======================================================================
//function : WOKBuilder_ToolInProcess
//purpose  : 
//=======================================================================
WOKBuilder_ToolInProcess::WOKBuilder_ToolInProcess(const Handle(TCollection_HAsciiString)& aname,
						   const WOKUtils_Param& params) 
  : WOKBuilder_Tool(aname,params), myfunc(NULL)
{
}

//=======================================================================
//function : Load
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInProcess::Load(const Handle(WOKUtils_Path)& alibrary, 
				    const Handle(TCollection_HAsciiString)& afunc)
{
  Handle(WOKUtils_Path) libpath;
  
  if(!alibrary->Exists())
    {
      libpath = Params().SearchFile(alibrary->Name());
      if(libpath.IsNull())
	{
	  ErrorMsg() << "WOKBuilder_ToolInProcess::Load"
		   << "Could not find file : " << alibrary->Name() << endm;
	  return;
	}
    }
  else
    {
      libpath = alibrary;
    }

  // DlOpen of library
  mylib.SetName(alibrary->Name()->ToCString());

  if(mylib.DlOpen(OSD_RTLD_LAZY) == Standard_False) 
    {
      ErrorMsg() << "WOKBuilder_ToolInProcess" << mylib.DlError() << endm;
      Standard_ProgramError::Raise("WOKBuilder_ToolInProcess");
    }

  myfunc = mylib.DlSymb(afunc->ToCString());

  if(myfunc == NULL)
    {
      ErrorMsg() << "WOKBuilder_ToolInProcess" << mylib.DlError() << endm;
      ErrorMsg() << "WOKBuilder_ToolInProcess" << "Error in DlSymb of : " << afunc << endm;
      Standard_ProgramError::Raise("WOKBuilder_ToolInProcess");
    }
  SetLoaded();
}

void WOKBuilder_ToolInProcess::Load()
{
 Standard_NotImplemented::Raise("WOKBuilder_ToolInProcess::Load() not implemented") ;
}

//=======================================================================
//function : Shared
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_ToolInProcess::Shared() const
{
  return myshared;
}

//=======================================================================
//function : SetShared
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInProcess::SetShared(const Handle(TCollection_HAsciiString)& ashared)
{
  myshared = ashared;
}

//=======================================================================
//function : Function
//purpose  : 
//=======================================================================
OSD_Function WOKBuilder_ToolInProcess::Function() const 
{
  return myfunc;
}

//=======================================================================
//function : UnLoad
//purpose  : 
//=======================================================================
void WOKBuilder_ToolInProcess::UnLoad()
{
  mylib.DlClose();
}

