// File:	WOKStep_Library.cxx
// Created:	Tue Jan  9 19:36:08 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Extension.hxx>

#include <WOKernel_FileType.hxx>
#include <WOKernel_File.hxx>

#include <WOKBuilder_ObjectFile.hxx>
#ifndef WNT
#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_SharedLibrary.hxx>
#else
#include <WOKBuilder_StaticLibrary.hxx>
#include <WOKBuilder_ImportLibrary.hxx>
#endif
#include <WOKBuilder_Miscellaneous.hxx>


#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_Library.ixx>

//=======================================================================
//function : WOKStep_Library
//purpose  : 
//=======================================================================
 WOKStep_Library::WOKStep_Library(const Handle(WOKMake_BuildProcess)& abp,
				  const Handle(WOKernel_DevUnit)& aunit, 
				  const Handle(TCollection_HAsciiString)& acode,
				  const Standard_Boolean checked, 
				  const Standard_Boolean hidden)
: WOKMake_Step(abp,aunit, acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Library::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Library::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;   
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_Library::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
      switch(apath->Extension())
	{
	case WOKUtils_ObjectFile:          result = new WOKBuilder_ObjectFile(apath);   break;
#ifndef WNT
	case WOKUtils_ArchiveFile: result = new WOKBuilder_ArchiveLibrary(apath); break;
	case WOKUtils_DSOFile:     result = new WOKBuilder_SharedLibrary(apath);  break;
#else
	case WOKUtils_LIBFile: 	   result = new WOKBuilder_StaticLibrary(apath); break;
	case WOKUtils_IMPFile:	   result = new WOKBuilder_ImportLibrary(apath); break;
#endif // WNT
	default:  
	  break;
	}

      if(result.IsNull())
	{
	  if(!strcmp(apath->ExtensionName()->ToCString(), ".ImplDep"))
	    {
	      result = new WOKBuilder_Miscellaneous(apath);
	    }
	}
      if(!result.IsNull())
	{
	  infile->SetBuilderEntity(result);
	  infile->SetDirectFlag(Standard_True);
	  return Standard_True;
	}
    }  
  return Standard_False;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : CompleteExecList
//purpose  : 
//=======================================================================
void WOKStep_Library::CompleteExecList(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  if((execlist->Length() != 0) && (myinflow.Extent() > execlist->Length()) && (!mydepmatrix.IsNull()))
    {
      WOKTools_MapOfHAsciiString amap;
      Standard_Integer i;
      
      for(i=1; i<=execlist->Length();i++)
	{
	  amap.Add(execlist->Value(i)->ID());
	}

      Standard_Boolean found = Standard_False;
      for(i=1; i<=myinflow.Extent() && !found; i++)
	{
	  if(!amap.Contains(myinflow(i)->ID()))
	    {
	      execlist->Append(myinflow(i));
	      found = Standard_True;
	    }
	}
    }

  WOKMake_Step::CompleteExecList(execlist);
  return;
}
