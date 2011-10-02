// File:	WOKStep_LibLink.cxx
// Created:	Fri Aug  2 10:07:33 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKernel_File.hxx>

#include <WOKBuilder_SharedLinker.hxx>
#include <WOKBuilder_SharedLibrary.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_Library.hxx>
#include <WOKBuilder_HSequenceOfObjectFile.hxx>
#include <WOKBuilder_HSequenceOfLibrary.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKStep_LibLink.ixx>
//---> EUG4YAN
Standard_IMPORT Standard_Boolean g_fCompOrLnk;
//<--- EUG4YAN
//=======================================================================
//function : WOKStep_LibLink
//purpose  : 
//=======================================================================
WOKStep_LibLink::WOKStep_LibLink(const Handle(WOKMake_BuildProcess)& abp,
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
void WOKStep_LibLink::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Standard_Integer                    i,j;
  Handle(WOKBuilder_Library)          alib;
  Handle(TCollection_HAsciiString)    libname;
  Handle(WOKUtils_Path)               apath;

  mylinker = new WOKBuilder_SharedLinker(new TCollection_HAsciiString("LINKSHR"),
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
      libname = WOKBuilder_SharedLibrary::GetLibFileName(Unit()->Params(), Unit()->Name());
    }
  else
    {
      libname = WOKBuilder_SharedLibrary::GetLibFileName(Unit()->Params(), SubCode());
    }
  
  Handle(WOKBuilder_SharedLinker) ashld = Handle(WOKBuilder_SharedLinker)::DownCast(mylinker);
  if(!ashld.IsNull())
    {
      ashld->SetLogicalName(libname);
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

  if(Status()==WOKMake_Failed)
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
