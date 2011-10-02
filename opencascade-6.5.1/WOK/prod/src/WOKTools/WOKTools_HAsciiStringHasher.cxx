// File:	WOKTools_HAsciiStringHasher.cxx
// Created:	Wed Jun 28 11:12:48 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKTools_HAsciiStringHasher.ixx>



 Standard_Integer WOKTools_HAsciiStringHasher::HashCode (const Handle(TCollection_HAsciiString) &Value)
{
  
  if(Value.IsNull()) return 0;

  register Standard_CString string = Value->ToCString();
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
  Standard_CString charPtr   = (Standard_CString)Value->ToCString();
  Standard_Integer aHashCode = 0,  alen,  i  = 0,  pos = 0,  count,  *tmphash;
  Standard_Character tabchar[20];
  
  alen = Value->Length();
  
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

Standard_Boolean WOKTools_HAsciiStringHasher::IsEqual(const Handle(TCollection_HAsciiString) &K1, const Handle(TCollection_HAsciiString) &K2)
{
  if (K1->Length() != K2->Length()) return(Standard_False);
  if (memcmp(K1->ToCString(), K2->ToCString(), K1->Length()) == 0) return Standard_True;
  return Standard_False;
}

