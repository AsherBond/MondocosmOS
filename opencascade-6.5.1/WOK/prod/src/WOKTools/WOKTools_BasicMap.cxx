// File:	WOKTools_BasicMap.cxx
// Created:	Fri Feb 26 17:20:43 1993
// Author:	Remi LEQUETTE
//		<rle@phylox>

#include <WOKTools_BasicMap.ixx>
#include <TCollection.hxx>
#include <WOKTools_BasicMapIterator.hxx>

#ifdef WNT
#include <windows.h>
#else
# include <memory.h>
#endif

#ifdef HAVE_CONFIG_H
# include <config.h>
# ifdef HAVE_UNISTD_H
#  include <unistd.h>
# endif

# ifdef HAVE_MALLOC_H
#  include <malloc.h>
# endif

# ifdef HAVE_IOMANIP
#  include <iomanip>
# elif defined (HAVE_IOMANIP_H)
#  include <iomanip.h>
# else
#  error "check config.h file or compilation options: either HAVE_IOMANIP or HAVE_IOMANIP_H should be defined"
# endif
#endif

//=======================================================================
//function : WOKTools_BasicMap
//purpose  : 
//=======================================================================

WOKTools_BasicMap::WOKTools_BasicMap(const Standard_Integer NbBuckets, 
					   const Standard_Boolean single) :
       myData1(NULL),
       myData2(NULL),
       isDouble(!single),
       mySaturated(Standard_False),
       myNbBuckets(NbBuckets),
       mySize(0)
{
}


//=======================================================================
//function : BeginResize
//purpose  : 
//=======================================================================

Standard_Boolean  WOKTools_BasicMap::BeginResize
  (const Standard_Integer NbBuckets,
   Standard_Integer& N,
   Standard_Address& data1,
   Standard_Address& data2)const 
{
  if (mySaturated) return Standard_False;
  N = TCollection::NextPrimeForMap(NbBuckets);
  if (N <= myNbBuckets) {
    if (IsEmpty())
      N = myNbBuckets;
    else
      return Standard_False;
  }
  data1 = Standard::Allocate((N+1)*sizeof(void*));
  memset(data1, 0, (N+1)*sizeof(void*));
  if (isDouble) {
    data2 = Standard::Allocate((N+1)*sizeof(void*));
    memset(data2, 0, (N+1)*sizeof(void*));
  }
  else
    data2 = NULL;
  return Standard_True;
}


//=======================================================================
//function : EndResize
//purpose  : 
//=======================================================================

void  WOKTools_BasicMap::EndResize(const Standard_Integer NbBuckets,
				      const Standard_Integer N,
				      const Standard_Address data1,
				      const Standard_Address data2)
{
  if (myData1) {
    Standard::Free(myData1);
  }
  if (myData2) {
    Standard::Free(myData2);
  }
  myNbBuckets = N;
  mySaturated = myNbBuckets <= NbBuckets;
  myData1 = data1;
  myData2 = data2;
}


//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================

void  WOKTools_BasicMap::Destroy()
{
  mySize = 0;
  mySaturated = Standard_False;
  if (myData1) {
    Standard::Free(myData1);
  }
  if (isDouble) {
    if (myData2) {
      Standard::Free(myData2);
    }
  }
  myData1 = myData2 = NULL;
}


//=======================================================================
//function : Statistics
//purpose  : 
//=======================================================================

void WOKTools_BasicMap::Statistics(Standard_OStream& S) const
{
  S <<"\nMap Statistics\n---------------\n\n";
  S <<"This Map has "<<myNbBuckets<<" Buckets and "<<mySize<<" Keys\n\n";
  if (mySaturated) S<<"The maximum number of Buckets is reached\n";
  
  if (mySize == 0) return;

  // compute statistics on 1
  Standard_Integer * sizes = new Standard_Integer [mySize+1];
  Standard_Integer i,l,nb;
  WOKTools_MapNode* p;
  WOKTools_MapNode** data;
  
  S << "\nStatistics for the first Key\n";
  for (i = 0; i <= mySize; i++) sizes[i] = 0;
  data = (WOKTools_MapNode**) myData1;
  nb = 0;
  for (i = 0; i <= myNbBuckets; i++) {
    l = 0;
    p = data[i];
    if (p) nb++;
    while (p) {
      l++;
      p = p->next;
    }
    sizes[l]++;
  }

  // display results
  l = 0;
  for (i = 0; i<= mySize; i++) {
    if (sizes[i] > 0) {
      l += sizes[i] * i;
      S << setw(5) << sizes[i] <<" buckets of size "<<i<<"\n";
    }
  }

  Standard_Real mean = ((Standard_Real) l) / ((Standard_Real) nb);
  S<<"\n\nMean of length : "<<mean<<"\n";

  delete [] sizes;
}
