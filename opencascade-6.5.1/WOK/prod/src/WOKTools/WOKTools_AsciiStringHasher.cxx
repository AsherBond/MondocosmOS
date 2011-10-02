// File:	WOKTools_StringHasher.cxx
// Created:	Tue May 30 10:03:23 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKTools_AsciiStringHasher.ixx>



 Standard_Integer WOKTools_AsciiStringHasher::HashCode (const TCollection_AsciiString &Value)
{
  if(Value.IsEmpty()) return 0;

  register Standard_CString string = Value.ToCString();
  register int c;
  register unsigned int result;

  result=0;
  while(1) {
    c=*string;
    string++;
    if(c==0) break;
    result += (result << 3) + c;
  }
  return result;

#if 0
  Standard_CString charPtr   = (Standard_CString)Value.ToCString();
  Standard_Integer aHashCode = 0,  alen,  i  = 0,  pos = 0,  count,  *tmphash;
  Standard_Character tabchar[20];
  
  alen = Value.Length();
  
  while(i < alen) {
    for (count = 0,pos = i;count < sizeof(int); count++) {
      if (pos + count >= alen)  tabchar[count] = '\0';
      else tabchar[count] = charPtr[pos + count];
      i++;
    }
    tmphash = (int *)tabchar;   
    aHashCode = aHashCode ^ *tmphash;
  }
  return aHashCode;
#endif
}

 Standard_Boolean WOKTools_AsciiStringHasher::IsEqual(const TCollection_AsciiString &K1, const TCollection_AsciiString &K2)
{
  if (K1.Length() != K2.Length()) return(Standard_False);
  if (memcmp(K1.ToCString(), K2.ToCString(), K1.Length()) == 0) return Standard_True;
  return Standard_False;
}

