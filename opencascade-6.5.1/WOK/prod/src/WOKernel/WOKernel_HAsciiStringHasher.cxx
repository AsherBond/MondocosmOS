// File:	WOKernel_HAsciiStringHasher.cxx
// Created:	Thu Feb  6 16:52:00 1997
// Author:	Prestataire Pascal BABIN
//		<pba@voilax.paris1.matra-dtv.fr>

#include <WOKernel_HAsciiStringHasher.ixx>

//=======================================================================
//function : HashCode
//purpose  : 
//=======================================================================
Standard_Integer WOKernel_HAsciiStringHasher::HashCode (const Handle(TCollection_HAsciiString) &Value,
							const Standard_Integer Upper)

{
  
  if(Value.IsNull() == Standard_True) return 0;
  
  Standard_CString charPtr   = (Standard_CString)Value->ToCString();
  Standard_Integer aHashCode = 0,  alen,  i  = 0,  pos = 0,  count,  *tmphash;
  Standard_Character tabchar[20];
  
  alen = Value->Length();
  
  while(i < alen) {
    for (count = 0,pos = i;count < ( Standard_Integer )sizeof(int); count++) {
      if (pos + count >= alen)  tabchar[count] = '\0';
      else tabchar[count] = charPtr[pos + count];
      i++;
    }
    tmphash = (int *)tabchar;   
    aHashCode = aHashCode ^ *tmphash;
  }
  return aHashCode % Upper;
}

//=======================================================================
//function : IsEqual
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_HAsciiStringHasher::IsEqual(const Handle(TCollection_HAsciiString) &K1, const Handle(TCollection_HAsciiString) &K2)
{
  if (strcmp(K1->ToCString(), K2->ToCString()) == 0) return(Standard_True);
  else                                               return(Standard_False);
}

