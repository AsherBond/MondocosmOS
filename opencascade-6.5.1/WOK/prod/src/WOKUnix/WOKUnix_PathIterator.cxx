#ifndef WNT


#include <WOKUnix_PathIterator.ixx>


#include <WOKUnix.hxx>
#include <WOKUnix_Path.hxx>

#include <WOKTools_Messages.hxx>

#include <stdio.h>
#include <dirent.h>

WOKUnix_PathIterator::WOKUnix_PathIterator(const Handle(WOKUnix_Path)& apath, const Standard_Boolean abool, const Standard_CString amask) 
: mymask(amask), mypath(apath), myrecflag(abool)
{
  TCollection_HAsciiString mask;
  mask.AssignCat(apath->Name()->ToCString());
  mask.AssignCat("/");
  mask.AssignCat(mymask.ToCString());

  
  WOKUnix_Dir adir = opendir(apath->Name()->ToCString());

  if(!adir) 
    {
      ErrorMsg() << "WOKUnix_PathIterator::WOKUnix_PathIterator" 
	<< WOKUnix::LastSystemMessage() << endm;
      ErrorMsg() << "WOKUnix_PathIterator::WOKUnix_PathIterator" 
	<< "Could not open directory " << apath->Name() << endm;
      mymore = Standard_False;
      return;
    }

  mystack.Push(adir);
  mydata = readdir(mystack.Top());
  mymore = Standard_True;
  
  SkipDots();
}


Standard_Boolean WOKUnix_PathIterator::IsDots(const Standard_CString astr)
{
  if(astr[0] == '.')
    {
      if(!astr[1])
	return Standard_True;
      else
	if(astr[1] == '.' )
	  if(!astr[2])
	    return Standard_True;
    }
  return Standard_False;
}

void WOKUnix_PathIterator::SkipDots() 
{
  if(!mydata) return;
  while(IsDots((Standard_CString)mydata->d_name) && !mystack.IsEmpty())
    {
      mydata = readdir(mystack.Top());
      if(!mydata)
	{
	  if(!mystack.IsEmpty())
            {
	        Pop();
		if(!mymore) return;
	    }
	  else
	    {
	        mymore = Standard_False;
		return;
	    }
	}
    }
}


void WOKUnix_PathIterator::Push(const Handle(WOKUnix_Path)& apath,  const WOKUnix_Dir& data)
{
  if(!IsDots(mydata->d_name) && myrecflag)
    {
      mypath = apath;
      
      WOKUnix_Dir nextone = opendir(mypath->Name()->ToCString());
      mystack.Push(nextone);
      mydata = readdir(mystack.Top());
      SkipDots();
      
      if(!mystack.IsEmpty())
	{
	  if(!mystack.Top()) 
	    mymore = Standard_False;
	  else
	    mymore = Standard_True;
	}
      else
	mymore = Standard_False;
    }
}

void WOKUnix_PathIterator::Pop()
{
  if(!mystack.IsEmpty())
    {
      closedir(mystack.Top());
      mystack.Pop();
      if(!mystack.IsEmpty()) 
	{
	  mydata = readdir(mystack.Top());
	  if(!mydata)
	    {
	      if(mystack.IsEmpty())
		mymore = Standard_False;
	      else
		Pop();
	    }
	  else
	    SkipDots();
	  mypath = new WOKUnix_Path(mypath->DirName());
	}
      else 
	mymore = Standard_False;
    }
}


void WOKUnix_PathIterator::Next()
{
  Handle(WOKUnix_Path) apath =  new WOKUnix_Path(mypath->Name(), new TCollection_HAsciiString(mydata->d_name));
  if(!IsDots(mydata->d_name) && myrecflag && apath->IsDirectory()) 
    Push(apath, mystack.Top());
  else
    {
      mydata = readdir(mystack.Top());
      if(!mydata)
	{
	  if(!mystack.IsEmpty())
	    Pop();
	  
	  if(mystack.IsEmpty())
	    mymore = Standard_False;
	}
    }
}

Handle(WOKUnix_Path) WOKUnix_PathIterator::PathValue()  const
{
  if(mydata) return new WOKUnix_Path(mypath->Name(), new TCollection_HAsciiString(mydata->d_name));
  return Handle(WOKUnix_Path)();
}

Handle(TCollection_HAsciiString) WOKUnix_PathIterator::NameValue()  const
{
  
  if(mydata) return new TCollection_HAsciiString(mydata->d_name);
  return Handle(TCollection_HAsciiString)();
  
}

Standard_Integer WOKUnix_PathIterator::LevelValue() const
{
  return mystack.Depth();
}

Standard_Boolean WOKUnix_PathIterator::More() const
{
  return mymore;
}

void WOKUnix_PathIterator::Destroy() 
{
  while(!mystack.IsEmpty())
    {
      if(mystack.Top()) closedir(mystack.Top());
      mystack.Pop();
    }
}



#endif
