// File:	TFunction_Driver.cxx
// Created:	Fri Jun 11 09:24:43 1999
// Author:	Vladislav ROMASHKO
//		<vro@flox.nnov.matra-dtv.fr>


#include <TFunction_Driver.ixx>

#include <TDF_Label.hxx>
#include <TDF_ListIteratorOfLabelList.hxx>


//=======================================================================
//function : TFunction_Driver
//purpose  : Constructor
//=======================================================================

TFunction_Driver::TFunction_Driver()
{

}


//=======================================================================
//function : Init
//purpose  : Initialization
//=======================================================================

void TFunction_Driver::Init(const TDF_Label& L)
{
  myLabel = L;
}


//=======================================================================
//function : Validate
//purpose  : Validates labels of a function
//=======================================================================

void TFunction_Driver::Validate(TFunction_Logbook& log) const
{
  TDF_LabelList res;
  Results(res);
  TDF_ListIteratorOfLabelList itr(res);
  for (; itr.More(); itr.Next())
  {
    log.SetValid(itr.Value(), Standard_True);
  }
}


//=======================================================================
//function : MustExecute
//purpose  : Analyzes the labels in the logbook
//=======================================================================

Standard_Boolean TFunction_Driver::MustExecute(const TFunction_Logbook& log) const
{
  // Check modification of arguments.
  TDF_LabelList args;
  Arguments(args);
  TDF_ListIteratorOfLabelList itr(args);
  for (; itr.More(); itr.Next())
  {
    if (log.IsModified(itr.Value()))
      return Standard_True;
  }
  return Standard_False;
}


//=======================================================================
//function : Arguments
//purpose  : The method fills-in the list by labels, 
//           where the arguments of the function are located.
//=======================================================================

void TFunction_Driver::Arguments(TDF_LabelList& ) const
{

}


//=======================================================================
//function : Results
//purpose  : The method fills-in the list by labels,
//           where the results of the function are located.
//=======================================================================

void TFunction_Driver::Results(TDF_LabelList& ) const
{

}
