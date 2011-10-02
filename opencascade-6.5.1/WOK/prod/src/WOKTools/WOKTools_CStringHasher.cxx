// File:	WOKTools_CStringHasher.cxx
// Created:	Fri Jul 19 16:26:06 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <WOKTools_CStringHasher.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : HashCode
//purpose  : 
//=======================================================================
Standard_Integer WOKTools_CStringHasher::HashCode (const Standard_CString Value)
{

  if(Value == NULL) return 0;
  
  Standard_CString charPtr   = Value;
  Standard_Integer aHashCode = 0,  alen,  i  = 0,  pos = 0,  count,  *tmphash;
  Standard_Character tabchar[20];
  
  alen = strlen(Value);
  
  while(i < alen) {
    for (count = 0,pos = i;count < (Standard_Integer)sizeof(int); count++) {
      if (pos + count >= alen)  tabchar[count] = '\0';
      else tabchar[count] = charPtr[pos + count];
      i++;
    }
    tmphash = (int *)tabchar;   
    aHashCode = aHashCode ^ *tmphash;
  }
  return aHashCode;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsEqual
//purpose  : 
//=======================================================================
Standard_Boolean WOKTools_CStringHasher::IsEqual(const Standard_CString K1, const Standard_CString K2)
{
  if (strcmp(K1, K2) == 0) return(Standard_True);
  else                     return(Standard_False);
}

