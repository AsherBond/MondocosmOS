// File:	TDataStd_DeltaOnModificationOfRealArray.cxx
// Created:	Tue Oct 30 16:57:38 2007
// Author:	Sergey ZARITCHNY
//		<sergey.zaritchny@opencascade.com>
//Copyright:    Open CasCade SA 2007

#include <TDataStd_DeltaOnModificationOfRealArray.ixx>
#include <TDataStd_RealArray.hxx>
#include <TDF_DeltaOnModification.hxx>
#include <TDF_Label.hxx>
#include <TColStd_HArray1OfReal.hxx>
#include <TColStd_ListOfInteger.hxx>
#include <TColStd_ListIteratorOfListOfInteger.hxx>
#include <TDF_AttributeIterator.hxx>
#ifdef DEB
#define MAXUP 1000
#endif
//=======================================================================
//function : TDataStd_DeltaOnModificationOfRealArray
//purpose  : 
//=======================================================================

TDataStd_DeltaOnModificationOfRealArray::
  TDataStd_DeltaOnModificationOfRealArray(const Handle(TDataStd_RealArray)& OldAtt)
: TDF_DeltaOnModification(OldAtt)
{
  Handle(TDataStd_RealArray) CurrAtt;
  if (Label().FindAttribute(OldAtt->ID(),CurrAtt)) {
    Handle(TColStd_HArray1OfReal) Arr1, Arr2;
    Arr1 = OldAtt->Array();
    Arr2 = CurrAtt->Array();
#ifdef DEB
    if(Arr1.IsNull())
      cout <<"DeltaOnModificationOfRealArray:: Old Array is Null" <<endl;
    if(Arr2.IsNull())
      cout <<"DeltaOnModificationOfRealArray:: Current Array is Null" <<endl;
#endif

    if(Arr1.IsNull() || Arr2.IsNull()) return;
    if(Arr1 != Arr2) {
      myUp1 = Arr1->Upper();
      myUp2 = Arr2->Upper();
      Standard_Integer i, N =0, aCase=0; 
      if(myUp1 == myUp2) 
	{aCase = 1; N = myUp1;}
      else if(myUp1 < myUp2) 
	{aCase = 2; N = myUp1;}
      else 
	{aCase = 3; N = myUp2;}//Up1 > Up2

      TColStd_ListOfInteger aList;
      for(i=Arr1->Lower();i <= N; i++)
	if(Arr1->Value(i) != Arr2->Value(i)) 
	  aList.Append(i);
      if(aCase == 3) {
	for(i = N+1;i <= myUp1; i++)
	  aList.Append(i);
      }
      if(aList.Extent()) {
	myIndxes = new TColStd_HArray1OfInteger(1,aList.Extent());
	myValues = new TColStd_HArray1OfReal(1,aList.Extent());
	TColStd_ListIteratorOfListOfInteger anIt(aList);
	for(i =1;anIt.More();anIt.Next(),i++) {
	  myIndxes->SetValue(i, anIt.Value());
	  myValues->SetValue(i, Arr1->Value(anIt.Value()));
	}
      }
    }
    OldAtt->RemoveArray();
#ifdef DEB
    if(OldAtt->Array().IsNull())
      cout << "BackUp Arr is Nullified" << endl;
#endif
  }
}


//=======================================================================
//function : Apply
//purpose  : 
//=======================================================================

void TDataStd_DeltaOnModificationOfRealArray::Apply()
{

  Handle(TDF_Attribute) TDFAttribute = Attribute();
  Handle(TDataStd_RealArray) BackAtt = (*((Handle(TDataStd_RealArray)*)&TDFAttribute));
  if(BackAtt.IsNull()) {
#ifdef DEB
    cout << "DeltaOnModificationOfRealArray::Apply: OldAtt is Null" <<endl;
#endif
    return;
  }
  
  Handle(TDataStd_RealArray) aCurAtt;
  if (!Label().FindAttribute(BackAtt->ID(),aCurAtt)) {

    Label().AddAttribute(BackAtt);
  }

  if(aCurAtt.IsNull()) {
#ifdef DEB
    cout << "DeltaOnModificationOfRealArray::Apply: CurAtt is Null" <<endl;
#endif
    return;
  }
  else 
    aCurAtt->Backup();

  Standard_Integer aCase;
  if(myUp1 == myUp2) 
    aCase = 1;
  else if(myUp1 < myUp2) 
    aCase = 2;
  else 
    aCase = 3;//Up1 > Up2

  if (aCase == 1 && (myIndxes.IsNull() || myValues.IsNull()))
    return;
  
  Standard_Integer i;
  Handle(TColStd_HArray1OfReal) aRealArr = aCurAtt->Array();
  if(aRealArr.IsNull()) return;
  if(aCase == 1)   
    for(i = 1; i <= myIndxes->Upper();i++) 
      aRealArr->ChangeArray1().SetValue(myIndxes->Value(i), myValues->Value(i));
  else if(aCase == 2) {    
    Handle(TColStd_HArray1OfReal) realArr = new TColStd_HArray1OfReal(aRealArr->Lower(), myUp1);
    for(i = aRealArr->Lower(); i <= myUp1 && i <= aRealArr->Upper(); i++) 
      realArr->SetValue(i, aRealArr->Value(i));
    if(!myIndxes.IsNull() && !myValues.IsNull())
      for(i = 1; i <= myIndxes->Upper();i++) 
	realArr->ChangeArray1().SetValue(myIndxes->Value(i), myValues->Value(i));
    aCurAtt->myValue = realArr;
  }
  else { // == 3
    Standard_Integer low = aRealArr->Lower();
    Handle(TColStd_HArray1OfReal) realArr = new TColStd_HArray1OfReal(low, myUp1);
    for(i = aRealArr->Lower(); i <= myUp2 && i <= aRealArr->Upper(); i++) 
      realArr->SetValue(i, aRealArr->Value(i));
    if(!myIndxes.IsNull() && !myValues.IsNull())
      for(i = 1; i <= myIndxes->Upper();i++) {
#ifdef DEB  
	cout << "i = " << i << "  myIndxes->Upper = " << myIndxes->Upper() << endl;
	cout << "myIndxes->Value(i) = " << myIndxes->Value(i) << endl;
	cout << "myValues->Value(i) = " << myValues->Value(i) << endl;
#endif
	realArr->ChangeArray1().SetValue(myIndxes->Value(i), myValues->Value(i));      
      }
    aCurAtt->myValue = realArr;
  }
    

#ifdef DEB    
  cout << " << RealArray Dump after Delta Apply >>" <<endl;
  Handle(TColStd_HArray1OfReal) aRArr = aCurAtt->Array();
  for(i=aRArr->Lower(); i<=aRArr->Upper() && i <= MAXUP;i++)
    cout << aRArr->Value(i) << "  ";
  cout <<endl;
#endif
}


