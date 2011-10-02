// File AppDef_MultiLine.cxx
// Lpa, le 3/12/91

#include <AppDef_MultiLine.ixx>
#include <Standard_OutOfRange.hxx>
#include <AppDef_Array1OfMultiPointConstraint.hxx>
#include <Standard_ConstructionError.hxx>


AppDef_MultiLine::AppDef_MultiLine(){}


AppDef_MultiLine::AppDef_MultiLine (const Standard_Integer NbMult)
{
  if (NbMult < 0 ) Standard_ConstructionError::Raise();

  tabMult = new AppDef_HArray1OfMultiPointConstraint (1, NbMult);
}


AppDef_MultiLine::AppDef_MultiLine (const AppDef_Array1OfMultiPointConstraint& tabMultiP)
{
  tabMult = new AppDef_HArray1OfMultiPointConstraint (1, tabMultiP.Length());
  Standard_Integer i, Lower = tabMultiP.Lower();
  for (i = 1; i <= tabMultiP.Length(); i++) {
    tabMult->SetValue(i, tabMultiP.Value(Lower+i-1));
  }
}


AppDef_MultiLine::AppDef_MultiLine (const TColgp_Array1OfPnt& tabP3d)
{
  tabMult = new AppDef_HArray1OfMultiPointConstraint (1, tabP3d.Length());
  Standard_Integer i, Lower = tabP3d.Lower();
  for (i = 1; i <= tabP3d.Length(); i++) {
    AppDef_MultiPointConstraint MP(1, 0);
    MP.SetPoint(1, tabP3d(Lower+i-1));
    tabMult->SetValue(i, MP);
  }
}



AppDef_MultiLine::AppDef_MultiLine (const TColgp_Array1OfPnt2d& tabP2d)
{
  tabMult = new AppDef_HArray1OfMultiPointConstraint (1, tabP2d.Length());
  Standard_Integer i, Lower = tabP2d.Lower();
  for (i = 1; i <= tabP2d.Length(); i++) {
    AppDef_MultiPointConstraint MP(0, 1);
    MP.SetPoint2d(1, tabP2d(Lower+i-1));
    tabMult->SetValue(i, MP);
  }

}


Standard_Integer AppDef_MultiLine::NbMultiPoints () const {
  return tabMult->Length();
}


Standard_Integer AppDef_MultiLine::NbPoints() const {
  return tabMult->Value(1).NbPoints() + tabMult->Value(1).NbPoints2d();
}


void AppDef_MultiLine::SetValue (const Standard_Integer Index, 
				const AppDef_MultiPointConstraint& MPoint) {
  if ((Index <= 0) || (Index > tabMult->Length())) {
    Standard_OutOfRange::Raise();
  }
  tabMult->SetValue(Index, MPoint);
}

AppDef_MultiPointConstraint AppDef_MultiLine::Value (const Standard_Integer Index) const
{
  if ((Index <= 0) || (Index > tabMult->Length())) {
    Standard_OutOfRange::Raise();
  }
  return tabMult->Value(Index);
}
  

void AppDef_MultiLine::Dump(Standard_OStream& o) const
{
  o << "AppDef_MultiLine dump:" << endl;
//  AppDef_MultiPointConstraint MP = tabMult->Value(1);
  o << "It contains " <<  tabMult->Length() << " MultiPointConstraint"<< endl;
//  o << MP->NbPoints() << " 3d and " << MP->NbPoints2d() << endl;
}
