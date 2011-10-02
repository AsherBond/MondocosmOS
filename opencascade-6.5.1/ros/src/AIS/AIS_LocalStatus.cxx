#include <AIS_LocalStatus.ixx>
#include <TColStd_ListIteratorOfListOfInteger.hxx>


AIS_LocalStatus::AIS_LocalStatus(const Standard_Boolean IsTemp,
				 const Standard_Boolean Decomp,
				 const Standard_Integer DMode,
				 const Standard_Integer SMode,
				 const Standard_Integer HMode,
				 const Standard_Boolean SubIntensity,
				 const Quantity_NameOfColor HiCol):
myDecomposition(Decomp),
myIsTemporary(IsTemp),
myDMode(DMode),
myFirstDisplay(Standard_False),
myHMode(HMode),
mySubIntensity(SubIntensity),
myHiCol(HiCol)
{
  if(SMode!=-1) mySModes.Append(SMode);
}


//=======================================================================
//function : IsActivated
//purpose  : 
//=======================================================================

Standard_Boolean AIS_LocalStatus::
IsActivated(const Standard_Integer aSelMode) const 
{
  TColStd_ListIteratorOfListOfInteger It(mySModes);
  for(;It.More();It.Next())
    if(It.Value()==aSelMode)
      return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : RemoveSelectionMode
//purpose  : 
//=======================================================================

void AIS_LocalStatus::RemoveSelectionMode(const Standard_Integer aMode)
{
  TColStd_ListIteratorOfListOfInteger It(mySModes);
  for(;It.More();It.Next())
    {
      if(It.Value()==aMode) {
	mySModes.Remove(It);
	return;
      }
    }
}
//=======================================================================
//function : ClearSelectionModes
//purpose  : 
//=======================================================================

void AIS_LocalStatus::ClearSelectionModes()
{mySModes.Clear();}


//=======================================================================
//function : AddSelectionMode
//purpose  : 
//=======================================================================

void AIS_LocalStatus::AddSelectionMode(const Standard_Integer aMode)
{
  if(IsSelModeIn(aMode)) return;

  if(aMode!=-1)
    mySModes.Append(aMode);
  else
    mySModes.Clear();
}

//=======================================================================
//function : IsSelModeIn
//purpose  : 
//=======================================================================

Standard_Boolean AIS_LocalStatus::IsSelModeIn(const Standard_Integer aMode) const
{
  for(TColStd_ListIteratorOfListOfInteger It(mySModes);
      It.More();
      It.Next()){
    if(It.Value()==aMode)
      return Standard_True;
  }
  return Standard_False;
}
