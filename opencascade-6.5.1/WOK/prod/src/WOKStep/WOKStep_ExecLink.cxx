// File:	WOKStep_ExecLink.cxx
// Created:	Fri Aug  2 10:08:23 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKernel_File.hxx>

#include <WOKBuilder_ExecutableLinker.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_Library.hxx>
#include <WOKBuilder_Executable.hxx>
#include <WOKBuilder_HSequenceOfObjectFile.hxx>
#include <WOKBuilder_HSequenceOfLibrary.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKStep_ExecLink.ixx>
//---> EUG4YAN
Standard_IMPORT Standard_Boolean g_fCompOrLnk;
//<--- EUG4YAN
//=======================================================================
//function : WOKStep_ExecLink
//purpose  : 
//=======================================================================
WOKStep_ExecLink::WOKStep_ExecLink(const Handle(WOKMake_BuildProcess)& abp,
				   const Handle(WOKernel_DevUnit)& aunit, 
				   const Handle(TCollection_HAsciiString)& acode, 
				   const Standard_Boolean checked, 
				   const Standard_Boolean hidden)
  : WOKStep_Link(abp,aunit,acode,checked,hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_ExecLink::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Standard_Integer                         i,j;
  Handle(WOKBuilder_Library)               alib;
  Handle(TCollection_HAsciiString)         libname;
  Handle(WOKernel_File)                    libpath;
  Handle(WOKUtils_Path)                    apath;

  mylinker = new WOKBuilder_ExecutableLinker(new TCollection_HAsciiString("LDEXE"),
					 Unit()->Params());

  Handle(TColStd_HSequenceOfHAsciiString)  externals = new TColStd_HSequenceOfHAsciiString;

  for(i=1; i<=execlist->Length(); i++)
    {
      Handle(WOKMake_InputFile) infile = execlist->Value(i);
      
       if(!infile->IsPhysic())
	{
	  if(!strcmp("external", infile->ID()->Token(":", 2)->ToCString()))
	    {
	      Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString("%");
	      astr->AssignCat(infile->ID()->Token(":", 3));
	      
	      externals->Append(astr);
	    }
	}
    }

  if(SubCode().IsNull())
    {
      mytarget    = Unit()->Name();
    }
  else
    {
      mytarget    = SubCode();
    }

  myexternals = externals;
  myobjects   = ComputeObjectList(execlist);
  mylibraries = ComputeLibraryList(execlist);
  mylibpathes = ComputeLibrarySearchList(execlist);
  mydbdirs    = ComputeDatabaseDirectories();

  if (Status()==WOKMake_Failed)
    return;

  Handle(WOKMake_HSequenceOfOutputFile) outfiles = new WOKMake_HSequenceOfOutputFile;
  WOKMake_Status status;

  status = ExecuteLink(outfiles);
  
  switch(status)
    {
    case WOKMake_Success:
      if (  !g_fCompOrLnk && !outfiles.IsNull ()  )
	{
	  for(i=1; i<=execlist->Length(); i++)
	    {
	      for(j=1; j<=outfiles->Length(); j++)
		{
		  AddExecDepItem(execlist->Value(i), outfiles->Value(j), Standard_True);
		}
	    }
	}
      break;
    case WOKMake_Uptodate:
    case WOKMake_Incomplete:
    case WOKMake_Failed:
    case WOKMake_Unprocessed:
      break;
    default: break;
    }
  SetStatus(status);
  return;
}
