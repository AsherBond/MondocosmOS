// File:	WOKMake_DepItem.cxx
// Created:	Mon Nov 20 20:11:59 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>

#include <Standard_ProgramError.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKMake_DepItem.ixx>

#define READBUF_SIZE 1024
//=======================================================================
//function : WOKMake_DepItem
//purpose  : 
//=======================================================================
WOKMake_DepItem::WOKMake_DepItem(const Handle(TCollection_HAsciiString)& afile, 
				       const Handle(TCollection_HAsciiString)& issuedfrom)
: myfile(afile), myorigin(issuedfrom)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetOrigin
//purpose  : 
//=======================================================================
void WOKMake_DepItem::SetOrigin(const Handle(TCollection_HAsciiString)& afile)
{
  myorigin = afile;
}
				
//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetOutputFile
//purpose  : 
//=======================================================================
void WOKMake_DepItem::SetOutputFile(const Handle(TCollection_HAsciiString)& aoutfile)
{
  myfile = aoutfile;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetDirect
//purpose  : 
//=======================================================================
void WOKMake_DepItem::SetDirect()
{
  mydirect = Standard_True;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetIndirect
//purpose  : 
//=======================================================================
void WOKMake_DepItem::SetIndirect()
{
  mydirect = Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetStatus
//purpose  : 
//=======================================================================
void WOKMake_DepItem::SetStatus(const WOKMake_FileStatus astatus)
{
  mystatus = astatus;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ReadLine
//purpose  : 
//=======================================================================
void WOKMake_DepItem::ReadLine(Standard_IStream& astream, Handle(WOKMake_DepItem)& anitem, const  Handle(WOKMake_DepItem)& lastone)
{
  static char aflag;
  static char aoutbuf[READBUF_SIZE], afrombuf[READBUF_SIZE];
  
  *aoutbuf = *afrombuf = '\0';
  aflag = 0;

  if(!astream)
    {
      anitem.Nullify();
      return;
    }

  astream.get(aflag);
  astream.ignore(READBUF_SIZE, ' ');
  astream.get(aoutbuf, READBUF_SIZE, ' ');
  astream.ignore(READBUF_SIZE, ' ');
  astream.get(afrombuf, READBUF_SIZE);
  astream.ignore(READBUF_SIZE, '\n');
  //astream >> aflag >> setw(READBUF_SIZE) >> aoutbuf >> setw(READBUF_SIZE) >> afrombuf;

  if(aflag == 0 || aoutbuf[0] == '\0' || afrombuf[0] == '\0')
    {
      anitem.Nullify();
      return;
    }
    
  if(aoutbuf[0] == '*')
    {
      if(!lastone.IsNull())
	{
	  anitem = new WOKMake_DepItem(lastone->OutputFile(),
				       new TCollection_HAsciiString(afrombuf));
	}
      else
	{
	  ErrorMsg() << "WOKMake_DepItem::ReadLine" 
		   << "Could not read invalid first line" << endm;
	  anitem.Nullify();
	  return;
	}
    }
  else
    {
      anitem = new WOKMake_DepItem(new TCollection_HAsciiString(aoutbuf),
				   new TCollection_HAsciiString(afrombuf));
    }
  
  if(aflag == '+')
    {
      anitem->SetDirect();
    }
  else
    {
      anitem->SetIndirect();
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WriteLine
//purpose  : 
//=======================================================================
void WOKMake_DepItem::WriteLine(Standard_OStream& astream, const Handle(WOKMake_DepItem)& anitem, const Handle(WOKMake_DepItem)& lastone)
{

  if(anitem->IsDirectDep())
    {
      astream << "+ ";
    }
  else
    {
      astream << "- ";
    }

  if(!lastone.IsNull())
    {
      if(!strcmp(anitem->OutputFile()->ToCString(), lastone->OutputFile()->ToCString()))
	{
	  astream << "* " << anitem->IssuedFrom()->ToCString() << endl;
	  return;
	}
    }
  astream << anitem->OutputFile()->ToCString() << " " << anitem->IssuedFrom()->ToCString() << endl;
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ReadFile
//purpose  : 
//=======================================================================
Standard_Integer WOKMake_DepItem::ReadFile(const Handle(WOKUtils_Path)& afile,
					   WOKMake_IndexedMapOfDepItem& amap)
{
   if(afile.IsNull())
    {
      Standard_ProgramError::Raise("WOKMake_DepItem::ReadFile : NullInput");
    }

  if(afile->Exists())
    {
      Standard_Integer i = 0;
      ifstream astream(afile->Name()->ToCString(), ios::in);
      Handle(WOKMake_DepItem) item, lastone;
      Handle(TCollection_HAsciiString) locatorname;

      ReadLine(astream, item,lastone);
      lastone = item;

      while(!item.IsNull())
	{
	  i++;
	  amap.Add(item);
	  ReadLine(astream, item,lastone);
	  lastone = item;
	}

      astream.close();
      return i;
    }
  return 0; 
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WriteFile
//purpose  : 
//=======================================================================
Standard_Integer WOKMake_DepItem::WriteFile(const Handle(WOKUtils_Path)& apath, 
					    const WOKMake_IndexedMapOfDepItem& amap)
{
  if(apath.IsNull())
    {
      Standard_ProgramError::Raise("WOKMake_DepItem::WriteFile : NullInput");
    }

  ofstream astream(apath->Name()->ToCString(), ios::out);
  
  if(!astream)
    {
      ErrorMsg() << "WOKMake_DepItem::WriteFile" << "Could not open " << apath->Name() << endm;
      Standard_ProgramError::Raise("");
    }
    
  Handle(WOKMake_DepItem) lastone;
  Standard_Integer i;
  for(i=1; i<=amap.Extent(); i++)
    {
      const Handle(WOKMake_DepItem)& item = amap(i);
      WriteLine(astream, item, lastone);
      lastone = item;
    }
  astream.close();
  return 0;
}
