// File:      BinLDrivers_DocumentSection.cxx
// Created:   20.06.08 18:45:28
// Author:    Alexander GRIGORIEV
// Copyright: Open Cascade 2008

#include <BinLDrivers_DocumentSection.hxx>
#include <FSD_FileHeader.hxx>

//=======================================================================
//function : BinLDrivers_DocumentSection
//purpose  : Empty constructor
//=======================================================================

BinLDrivers_DocumentSection::BinLDrivers_DocumentSection ()
  : myIsPostRead (Standard_False)
{
  myValue[0] = 0;
  myValue[1] = 0;
}

//=======================================================================
//function : BinLDrivers_DocumentSection
//purpose  : Constructor
//=======================================================================

BinLDrivers_DocumentSection::BinLDrivers_DocumentSection
                        (const TCollection_AsciiString& theName,
                         const Standard_Boolean         isPostRead)
  : myName       (theName),
    myIsPostRead (isPostRead)
{
  myValue[0] = 0;
  myValue[1] = 0;
}

//=======================================================================
//function : Name
//purpose  : 
//=======================================================================

const TCollection_AsciiString&  BinLDrivers_DocumentSection::Name () const
{
  return myName;
}

//=======================================================================
//function : Offset
//purpose  : 
//=======================================================================

Standard_Size BinLDrivers_DocumentSection::Offset () const
{
  return myValue[0];
}

//=======================================================================
//function : SetOffset
//purpose  : 
//=======================================================================

void BinLDrivers_DocumentSection::SetOffset (const Standard_Size theOffset)
{
  myValue[0] = theOffset;
}

//=======================================================================
//function : IsPostRead
//purpose  : 
//=======================================================================

Standard_Boolean BinLDrivers_DocumentSection::IsPostRead () const
{
  return myIsPostRead;
}

//=======================================================================
//function : Length
//purpose  : 
//=======================================================================

Standard_Size BinLDrivers_DocumentSection::Length () const
{
  return myValue[1];
}

//=======================================================================
//function : SetLength
//purpose  : 
//=======================================================================

void BinLDrivers_DocumentSection::SetLength (const Standard_Size theLength)
{
  myValue[1] = theLength;
}

//=======================================================================
//function : WriteTOC
//purpose  : 
//=======================================================================

void BinLDrivers_DocumentSection::WriteTOC (Standard_OStream& theStream)
{
  char aBuf[512];

  if (myName.IsEmpty() == Standard_False) {
    Standard_Integer * aBufSz = reinterpret_cast<Standard_Integer *> (&aBuf[0]);
    const Standard_Size aBufSzSize = sizeof(aBuf) / sizeof(Standard_Integer);
    aBufSz[aBufSzSize-1] = 0;

    strncpy (&aBuf[sizeof(Standard_Integer)],
             myName.ToCString(),
             sizeof(aBuf)-sizeof(Standard_Integer)-1);

    // Calculate the length of the buffer: Standard_Size + string.
    // If the length is not multiple of Standard_Size, it is properly increased
    const Standard_Size aLen = strlen (&aBuf[sizeof(Standard_Integer)]);
    Standard_Size aBufSize =
      (aLen/sizeof(Standard_Integer))*sizeof(Standard_Integer);
    if (aBufSize < aLen)
      aBufSize += sizeof(Standard_Integer);

    // Write the buffer: size + string
#if DO_INVERSE
    aBufSz[0] = InverseSize ((Standard_Integer)aBufSize);
#else
    aBufSz[0] = aBufSize;
#endif
    theStream.write (&aBuf[0], aBufSize + sizeof(Standard_Integer));

    // Store the address of Offset word in the file
    myValue[0] = (Standard_Size) theStream.tellp();
    myValue[1] = 0;

    // Write the placeholders of Offset and Length of the section that should
    // be written afterwards
    aBufSz[0] = 0;
    aBufSz[1] = 0;
    aBufSz[2] = 0;
    theStream.write (&aBuf[0], 3*sizeof(Standard_Integer));
  }
}

//=======================================================================
//function : Write
//purpose  : 
//=======================================================================

void BinLDrivers_DocumentSection::Write (Standard_OStream&   theStream,
                                         const Standard_Size theOffset)
{
  const Standard_Size aSectionEnd = (Standard_Size) theStream.tellp();
  theStream.seekp(myValue[0]);
  myValue[0] = theOffset;
  myValue[1] = aSectionEnd - theOffset;
  Standard_Integer aVal[3] = {
    Standard_Integer(myValue[0]),
    Standard_Integer(myValue[1]),
    Standard_Integer(myIsPostRead)
  };
#if DO_INVERSE
  aVal[0] = InverseSize(aVal[0]);
  aVal[1] = InverseSize(aVal[1]);
  aVal[2] = InverseSize(aVal[2]);
#endif

  theStream.write((char *)&aVal[0], 3*sizeof(Standard_Integer));
  theStream.seekp(aSectionEnd);
}

//=======================================================================
//function : ReadTOC
//purpose  : 
//=======================================================================

void BinLDrivers_DocumentSection::ReadTOC
                                (BinLDrivers_DocumentSection& theSection,
                                 Standard_IStream&            theStream)
{
  char aBuf[512];
  Standard_Integer aNameBufferSize;
  theStream.read ((char *)&aNameBufferSize, sizeof(Standard_Integer));
#if DO_INVERSE
  aNameBufferSize = InverseSize(aNameBufferSize);
#endif
  if (aNameBufferSize > 0) {
    theStream.read ((char *)&aBuf[0], (Standard_Size)aNameBufferSize);
    theSection.myName = (Standard_CString)&aBuf[0];
    Standard_Integer aValue[3];
    theStream.read ((char *)&aValue[0], 3*sizeof(Standard_Integer));
#if DO_INVERSE
    aValue[0] = InverseSize (aValue[0]);
    aValue[1] = InverseSize (aValue[1]);
    aValue[2] = InverseSize (aValue[2]);
#endif
    theSection.myValue[0] = (Standard_Size)aValue[0];
    theSection.myValue[1] = (Standard_Size)aValue[1];
    theSection.myIsPostRead = (Standard_Boolean)aValue[2];
  }
}
