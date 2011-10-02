#ifndef WNT

#include <OSD_Environment.hxx>
#include <OSD_Path.hxx>


#include <WOKTools_Messages.hxx>

#include <WOKUnix_FileBuffer.ixx>

#include <WOKUnix_MaxPipeSize.hxx>

#include <TCollection_HAsciiString.hxx>

// default buffer max size 1Mo : overided by WOK_MAXBUFFEREDSIZE var
#define MAX_BUFFEREDSIZE    1048576  

//=======================================================================
//function : WOKUnix_FileBuffer
//purpose  : contructs a file buffer with a FDescr
//=======================================================================
WOKUnix_FileBuffer::WOKUnix_FileBuffer(const WOKUnix_FDescr& afd, const WOKUnix_BufferIs astd) : WOKUnix_Buffer(afd, astd)
{
}

//=======================================================================
//function : Select
//purpose  : performs select settings
//=======================================================================
void WOKUnix_FileBuffer::Select(Standard_Integer& afd, WOKUnix_Timeval& atimeout, WOKUnix_FDSet& aset) const 
{
  afd = ( (afd) > (GetFDescr().FileNo()) ) ? afd : GetFDescr().FileNo();
  FD_SET(GetFDescr().FileNo(), &aset);
  atimeout.tv_sec = 0;
  atimeout.tv_usec = 500000;
  return;
}


//=======================================================================
//function : Errors
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKUnix_FileBuffer::Errors()
{
 return Echo();
}

//=======================================================================
//function : Echo
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString)  WOKUnix_FileBuffer::Echo()
{
  if(mybuffer.FileNo() != -1)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;

      mybuffer.Reset();

      mybuffer.Seek( 0, OSD_FromBeginning);

      TCollection_AsciiString abuf;

      while(mybuffer.IsAtEnd() == Standard_False)
	{
          Handle( TCollection_HAsciiString ) str = mybuffer.ReadLine ();

          if (  !str.IsNull () && !str -> IsEmpty ()  )

 	   aseq -> Append ( str );
	}
      aseq->Append(WOKUnix_Buffer::Echo());

      mybuffer.Close();
      mybuffer.Remove();

      return aseq;
    }
  else return WOKUnix_Buffer::Echo();
}

//=======================================================================
//function : Acquit
//purpose  : Acquits select
//=======================================================================
void WOKUnix_FileBuffer::Acquit(const Standard_Integer astatus, const WOKUnix_FDSet& aset)  
{
#ifndef __GNUC__
  Standard_Integer nbtoread = GetFDescr().GetNbToRead();
#endif  // __GNUC__
#if defined( WOK_VERBOSE ) && !defined( __GNUC__ )
  VerboseMsg()("WOK_PROCESS") << "WOKUnix_FileBuffer::Acquit"
                            << "There is " << nbtoread << " bytes to read on process output" << endm;
#endif
#ifndef __GNUC__
  if(nbtoread >= MAX_PIPE_SIZE)
#endif  // __GNUC__
    {	  
      Dump();
    }
}


//=======================================================================
//function : Dump
//purpose  : Dumps contents of buffer in a FDescr
//=======================================================================
void WOKUnix_FileBuffer::Dump()  
{
  OSD_Environment  maxbsize("WOK_MAXBUFFEREDSIZE");
  TCollection_AsciiString astring;
  Standard_Integer maxsize;
  
#ifdef WOK_VERBOSE
  VerboseMsg()("WOK_PROCESS") << "WOKUnix_FileBuffer::Dump"
    << "Dumping" << endm;
#endif

  if(mybuffer.FileNo() == -1)
    {
      mybuffer.BuildTemporary();
    }
  else
    {
      astring = maxbsize.Value();
      if(astring.IsIntegerValue() == Standard_False)  maxsize = MAX_BUFFEREDSIZE;
      else                                            maxsize = astring.IntegerValue();
      
      if(mybuffer.Size() >= maxsize)
	{
          cout << "WOKUnix_FileBuffer::Dump : mybuffer.Size() "
               << mybuffer.Size() << " >= maxsize " << maxsize << endl ;
	  WOKUnix_BufferOverflow::Raise("WOKUnix_FileBuffer::Dump : Buffer Overflow");
	}
    }
  WOKUnix_Buffer::Write(mybuffer);
#ifdef WOK_VERBOSE
  VerboseMsg()("WOK_PROCESS") << "WOKUnix_FileBuffer::Dump"
    << "Dumped" << endm;
#endif

}

//=======================================================================
//function : Close
//purpose  : Closes 
//=======================================================================
void WOKUnix_FileBuffer::Close()
{
  OSD_Path apath;

  mybuffer.Path(apath);
  
  if( apath.Name().IsEmpty() != Standard_True && mybuffer.FileNo() >= 0) mybuffer.Close();
  WOKUnix_Buffer::Close();
}
#endif // WNT
