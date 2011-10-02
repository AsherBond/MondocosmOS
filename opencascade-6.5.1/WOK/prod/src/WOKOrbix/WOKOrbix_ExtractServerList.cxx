// File:	WOKOrbix_ExtractServerList.cxx
// Created:	Mon Aug 25 18:00:08 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>
#include <Standard_Stream.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>

#include <WOKBuilder_MSEntity.hxx>

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKOrbix_DataMapOfHAsciiStringOfHAsciiString.hxx>

#include <WOKOrbix_ExtractServerList.ixx>

#define READBUF_SIZE 1024

//#ifdef HAVE_IOMANIP
//# include <iomanip>
//#elif defined (HAVE_IOMANIP_H)
//# include <iomanip.h>
//#endif

//=======================================================================
//function : WOKOrbix_ExtractServerList
//purpose  : 
//=======================================================================
WOKOrbix_ExtractServerList::WOKOrbix_ExtractServerList(const Handle(WOKMake_BuildProcess)& abp,
						       const Handle(WOKernel_DevUnit)& aunit,
						       const Handle(TCollection_HAsciiString)& acode,
						       const Standard_Boolean checked,const Standard_Boolean hidden)
  : WOKMake_Step(abp, aunit,acode,checked,hidden)
{
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKOrbix_ExtractServerList::HandleInputFile(const Handle(WOKMake_InputFile)& infile) 
{
 if(!infile.IsNull())
    {
      if(!strcmp("msentity", infile->ID()->Token(":",2)->ToCString()))
	{
	  infile->SetDirectFlag(Standard_True);
	  infile->SetBuilderEntity(new WOKBuilder_MSEntity(infile->ID()->Token(":",3)));
	  return Standard_True;
	}
    }
 return Standard_False;
}

//=======================================================================
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOrbix_ExtractServerList::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;   
}

//=======================================================================
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOrbix_ExtractServerList::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)TMPDIR);
  return result;   
}

//=======================================================================
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKOrbix_ExtractServerList::OutOfDateEntities()
{
  return ForceBuild();
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKOrbix_ExtractServerList::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist) 
{
  WOKOrbix_DataMapOfHAsciiStringOfHAsciiString amap;

  for(Standard_Integer i=1; i<=execlist->Length(); i++)
    {
      const Handle(WOKMake_InputFile)& infile = execlist->Value(i);

      Handle(TCollection_HAsciiString) fullname = infile->ID()->Token(":",3);
      Standard_Integer apos = fullname->Search("_");
      Standard_Boolean found = Standard_False;

      Handle(TCollection_HAsciiString) idlunit       = fullname->SubString(1, apos-1);
      Handle(TCollection_HAsciiString) interfacename = fullname->SubString(apos+1, fullname->Length());

      if(apos < 0 ) 
	{
	  ErrorMsg() << "WOKOrbix_ExtractServerList::Execute"
		   << "Cannot determine idl interface name in " << fullname << ": underscore is missing" << endm;
	}

      if(!amap.IsBound(fullname))
	{
	  static Handle(TCollection_HAsciiString) admtype = new TCollection_HAsciiString("admfile");


	  Handle(TCollection_HAsciiString) declfilename = new TCollection_HAsciiString(idlunit);
	  declfilename->AssignCat(".IdlDecl");

	  Handle(WOKernel_File) declarfile = Locator()->Locate(idlunit, admtype, declfilename);

	  if(declarfile.IsNull())
	    {
	      ErrorMsg() << "WOKOrbix_ExtractServerList::Execute"
		       << "Could not find IDL unit interface declaration list file : " 
		       << idlunit << ":" << admtype << ":" <<  declfilename << endm;
	      SetFailed();
	    }
	  
	  ifstream stream(declarfile->Path()->Name()->ToCString());
	  static char intbuf[READBUF_SIZE], modbuf[READBUF_SIZE];
	  
	  while(stream >> setw(READBUF_SIZE) >> intbuf >> setw(READBUF_SIZE) >> modbuf)
	    {
	      Handle(TCollection_HAsciiString) thename = new TCollection_HAsciiString(idlunit);
	      thename->AssignCat("_");
	      thename->AssignCat(intbuf);
	      
	      amap.Bind(thename, new TCollection_HAsciiString(modbuf));
	    }
	  stream.close();

	  if(!amap.IsBound(fullname))
	    {
	      ErrorMsg() << "WOKOrbix_ExtractServerList::Execute"
		       << "IDL unit " << idlunit << " does not declare interface: " << interfacename << endm;
	      SetFailed();
	    }
	  else
	    {
	      found = Standard_True;
	    }
	  
	}
      else found = Standard_True;

      if(found)
	{
	  static Handle(TCollection_HAsciiString) objtype = new TCollection_HAsciiString("object");
	  const Handle(TCollection_HAsciiString)& amodule = amap.Find(fullname);

	  
	  Handle(TCollection_HAsciiString) filename[2];

	  //Server file
	  filename[0] = new TCollection_HAsciiString(amodule);
	  filename[0]->AssignCat("_S.o");
	  
	  //Implementation File
	  filename[1] = new TCollection_HAsciiString(amodule);
	  filename[1]->AssignCat("_i.o");

	  for(Standard_Integer j=0; j<2; j++)
	    {
	      if(!filename[j].IsNull())
		{
		  const Handle(WOKernel_File)& servobj = Locator()->Locate(idlunit, objtype, filename[j]);
		  
		  if(servobj.IsNull())
		    {
		      ErrorMsg() << "WOKOrbix_ExtractServerList::Execute"
			       << "Could not find objfile: " << WOKernel_File::FileLocatorName(idlunit, objtype, filename[j]);
		      SetFailed();
		    }
		  
		  Handle(WOKMake_OutputFile) outsrv = new WOKMake_OutputFile(servobj->LocatorName(), servobj,
									     Handle(WOKBuilder_Entity)(), servobj->Path());
		  outsrv->SetLocateFlag(Standard_True);
		  outsrv->SetReference();
		  outsrv->SetExtern();
		  
		  AddExecDepItem(infile, outsrv, Standard_True);
		}
	    }
	  Handle(WOKernel_File)     NULLFILE;
	  Handle(WOKBuilder_Entity) NULLENTITY;
	  Handle(WOKUtils_Path)     NULLPATH;
	  Handle(TCollection_HAsciiString) externid = new TCollection_HAsciiString(Unit()->Name());
	  
	  externid->AssignCat(":external:ORBIX_ServerLibs");
	  
	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(externid, NULLFILE, NULLENTITY, NULLPATH);
	  
	  outfile->SetLocateFlag(Standard_True);
	  outfile->SetProduction();
	  outfile->SetPhysicFlag(Standard_False);
	  outfile->SetExtern();
	  
	  AddExecDepItem(infile, outfile, Standard_True); 

	}
    }

  if(Status() == WOKMake_Unprocessed) SetSucceeded();
}

