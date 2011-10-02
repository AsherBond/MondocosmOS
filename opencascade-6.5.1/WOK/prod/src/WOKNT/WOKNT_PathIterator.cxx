#ifdef WNT

#include <WOKNT_PathIterator.ixx>
#include <WOKNT_Path.hxx>

#include <WOKTools_Messages.hxx>

#include <Standard_Stream.hxx>

WOKNT_PathIterator::WOKNT_PathIterator(const Handle(WOKNT_Path)& apath, const Standard_Boolean abool, const Standard_CString amask) 
: mypath(apath), myrecflag(abool), mymask(amask)
{
  TCollection_HAsciiString mask;
  mask.AssignCat(apath->Name()->ToCString());
  mask.AssignCat("/");
  mask.AssignCat(mymask.ToCString());

  myStack.Push(FindFirstFile(mask.ToCString(), &mydata));
  if(myStack.Top() == INVALID_HANDLE_VALUE ) 
    mymore = Standard_False;
  else
    mymore = Standard_True;

  SkipDots();
}


Standard_Boolean WOKNT_PathIterator::IsDots(const Standard_CString astr)
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

void WOKNT_PathIterator::SkipDots() 
{
  while(IsDots((Standard_CString)mydata.cFileName) && !myStack.IsEmpty())
    {
      if(!FindNextFile(myStack.Top(), &mydata))
	{
	  if(GetLastError() == ERROR_NO_MORE_FILES) 
	    {
	      if(!myStack.IsEmpty())
		Pop();
	      else
		mymore = Standard_False;
	    }
	  else 
	    {
	      ErrorMsg() << "WOKNT_PathIterator::Next" 
		<< "Error occured in directory lookup : " << (int)GetLastError() << endm;
	      mymore = Standard_False;
	    }
	}
    }
}


void WOKNT_PathIterator::Push(const WOKNT_FindData& data, const WOKNT_Handle& handle)
{
  if(data.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY && myrecflag)
    {
      if(!IsDots(mydata.cFileName) && myrecflag)
	{
	  mypath = new WOKNT_Path(mypath->Name(), new TCollection_HAsciiString(mydata.cFileName));
	  TCollection_AsciiString mask;
	  mask = mypath->Name()->String();
	  mask.AssignCat("/");
	  mask.AssignCat(mymask);

	  WOKNT_Handle nextone = FindFirstFile(mask.ToCString(), &mydata);
	  myStack.Push(nextone);
	  SkipDots();

	  if(!myStack.IsEmpty())
	    {
	      if(myStack.Top() == INVALID_HANDLE_VALUE ) 
                {
                  Pop();
		  mymore = Standard_False;
	        }
	      else
		mymore = Standard_True;
	    }
	  else
            {
              Pop();
	      mymore = Standard_False;
            }
	}
    }
  else
    {
      ErrorMsg() << "WOKNT_PathIterator::Push"
	<< "Only a directory can be pushed in PathIterator" << endm;
    }
}

void WOKNT_PathIterator::Pop()
{
  if(!myStack.IsEmpty())
    {
      FindClose(myStack.Top());
      myStack.Pop();
      if(!myStack.IsEmpty()) 
	{
	  if(!FindNextFile(myStack.Top(), &mydata))
	    {
	      if(GetLastError() == ERROR_NO_MORE_FILES) 
		{
		  if(myStack.IsEmpty())
		    mymore = Standard_False;
		  else
		    Pop();
		}
	    }
	  else
	    SkipDots();
	  mypath = new WOKNT_Path(mypath->DirName());
	}
      else 
	mymore = Standard_False;
    }
}


void WOKNT_PathIterator::Next()
{
  if(myStack.Top()!=INVALID_HANDLE_VALUE && mymore) 
    {
      if(!IsDots(mydata.cFileName) && mydata.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY && myrecflag) 
        Push(mydata, myStack.Top());
        if (!mymore) Pop(); 
      else
	{
	  if(!FindNextFile(myStack.Top(), &mydata))
	    {
	      if(GetLastError() == ERROR_NO_MORE_FILES) 
		{
		  if(!myStack.IsEmpty())
		    Pop();

		  if(myStack.IsEmpty())
		    mymore = Standard_False;
		}
	      else 
		{
		  ErrorMsg() << "WOKNT_PathIterator::Next" 
		    << "Error occured in directory lookup : " << (int)GetLastError() << endm;
		  mymore = Standard_False;
		}
	    }
	}
    }
}

Handle(WOKNT_Path) WOKNT_PathIterator::PathValue()  const
{
  if(mydata.cFileName) return new WOKNT_Path(mypath->Name(), new TCollection_HAsciiString((char * const)mydata.cFileName));
  return Handle(WOKNT_Path)();
}

Handle(TCollection_HAsciiString) WOKNT_PathIterator::NameValue()  const
{

  if(mydata.cFileName) return new TCollection_HAsciiString((char * const)mydata.cFileName);
  return Handle(TCollection_HAsciiString)();
  
}

Standard_Integer WOKNT_PathIterator::LevelValue() const
{
  return myStack.Depth();
}

Standard_Boolean WOKNT_PathIterator::More() const
{
  return mymore;
}

void WOKNT_PathIterator::Destroy() const
{
  if(myStack.Top() != INVALID_HANDLE_VALUE) FindClose(myStack.Top());
}

#endif
