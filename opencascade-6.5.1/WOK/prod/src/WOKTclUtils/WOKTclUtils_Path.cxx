// File:	WOKTclUtils_Path.cxx
// Created:	Thu Feb 27 16:59:37 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_Return.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_PathIterator.hxx>

#include <WOKTclUtils_Path.ixx>

void WOKTclUtils_Path_FileCompare_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " <path1> <path2>" << endl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : FileCompare
//purpose  : 
//=======================================================================
Standard_Integer WOKTclUtils_Path::FileCompare(const Standard_Integer argc,const WOKTools_ArgTable& argv,WOKTools_Return& retval) 
{

  WOKTools_Options opts(argc, argv, "h", WOKTclUtils_Path_FileCompare_Usage);
  Handle(TCollection_HAsciiString) f1, f2;

  while(opts.More())
    {
//      switch(opts.Option())
//	{
//	default:
//	  break;
//	}
      opts.Next();
    }

  if(opts.Failed() == Standard_True) return 1;
  
  switch(opts.Arguments()->Length())
    {
    case 2:
      f1 = opts.Arguments()->Value(1);
      f2 = opts.Arguments()->Value(2);
      break;
    default:
      WOKTclUtils_Path_FileCompare_Usage(argv[0]);
      return 1;
    }
  
  Handle(WOKUtils_Path) p1 = new WOKUtils_Path(f1);
  Handle(WOKUtils_Path) p2 = new WOKUtils_Path(f2);

  if(!p1->Exists())
    {
      ErrorMsg() << argv[0] << "File : " << p1->Name() << " does not exists" << endm;
      return 1;
    }
  if(!p1->IsFile())
    {
      ErrorMsg() << argv[0] << "File : " << p1->Name() << " is not a plain file" << endm;
      return 1;
    }
  if(!p2->Exists())
    {
      ErrorMsg() << argv[0] << "File : " << p2->Name() << " does not exists" << endm;
      return 1;
    }
  if(!p2->IsFile())
    {
      ErrorMsg() << argv[0] << "File : " << p2->Name() << " is not a plain file" << endm;
      return 1;
    }

  retval.AddBooleanValue(p1->IsSameFile(p2));
  return 0;
}


void WOKTclUtils_Path_DirectorySearch_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -r -f -d -E <ext> <path>" << endl;
  cerr << endl;
  cerr << "         -r : recurse in subfolders" << endl;
  cerr << "         -f : only search for files" << endl;
  cerr << "         -d : only search for directories" << endl;
  cerr << "         -E <ext> : search for files with extension : .<ext>" << endl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : DirectorySearch
//purpose  : 
//=======================================================================
Standard_Integer WOKTclUtils_Path::DirectorySearch(const Standard_Integer argc,const WOKTools_ArgTable& argv,WOKTools_Return& retval) 
{

  WOKTools_Options opts(argc, argv, "hdfrE:F:", WOKTclUtils_Path_DirectorySearch_Usage,"fd");

  Handle(TCollection_HAsciiString) path;
  Handle(TCollection_HAsciiString) ext, name, mask;
  Standard_Boolean recurs = Standard_False, files = Standard_False, dirs = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'r':
	  recurs = Standard_True;
	  break;
	case 'd':
	  dirs = Standard_True;
	  break;
	case 'f':
	  files = Standard_True;
	  break;
	case 'E':
	  ext = opts.OptionArgument();
	  break;
	case 'F':
	  name = opts.OptionArgument();
	  break;
	default:
	  break;
	}
      opts.Next();
    }

  if(opts.Failed() == Standard_True) return 1;
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      path = opts.Arguments()->Value(1);
      break;
    default:
      {
	ErrorMsg() << argv[0] << argv[0] << " must have one and only one argument" << endm;
	WOKTclUtils_Path_DirectorySearch_Usage(argv[0]);
      }
      return 1;
    }
  


  if(!ext.IsNull() && !name.IsNull())
    {
      ErrorMsg() <<  argv[0] 
	<< "Option -E cannot be used in conjuction with -F" << endm;
      WOKTclUtils_Path_DirectorySearch_Usage(argv[0]);
      return 1;
    }
  
  if(!ext.IsNull()) 
    {
      mask = new TCollection_HAsciiString("*.");
      mask->AssignCat(ext);
    }

  if(!name.IsNull())  mask = name;

  if(ext.IsNull() && name.IsNull()) mask = new TCollection_HAsciiString("*");

  Handle(WOKUtils_Path) p = new WOKUtils_Path(path);

  if(!p->IsDirectory())
    {
      ErrorMsg() << argv[0] << "Argument : " << p->Name() << " is not a directory" << endm;
      return 1;
    }

  WOKUtils_PathIterator anit(p, recurs, mask->ToCString());

  while(anit.More())
    {
      Standard_Boolean add = Standard_True;
      Handle(WOKUtils_Path) apath = anit.PathValue();

      if(files)     { if(!apath->IsFile())      add = Standard_False; } 
      else if(dirs) { if(!apath->IsDirectory()) add = Standard_False; } 
      
      if(add) retval.AddStringValue(apath->Name());

      anit.Next();
    }

  return 0;
}

