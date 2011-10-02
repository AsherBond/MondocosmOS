//--------------------------------------------------------------------
//
//  File Name : IGESDimen_CurveDimension.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESDimen_CurveDimension.ixx>
#include <IGESGeom_Line.hxx>
#include <gp_XYZ.hxx>
#include <gp_GTrsf.hxx>


    IGESDimen_CurveDimension::IGESDimen_CurveDimension ()    {  }


    void  IGESDimen_CurveDimension::Init
  (const Handle(IGESDimen_GeneralNote)& aNote,
   const Handle(IGESData_IGESEntity)&   aCurve,
   const Handle(IGESData_IGESEntity)&   anotherCurve,
   const Handle(IGESDimen_LeaderArrow)& aLeader,
   const Handle(IGESDimen_LeaderArrow)& anotherLeader,
   const Handle(IGESDimen_WitnessLine)& aLine,
   const Handle(IGESDimen_WitnessLine)& anotherLine)
{
  theNote              = aNote;
  theFirstCurve        = aCurve;
  theSecondCurve       = anotherCurve;
  theFirstLeader       = aLeader;
  theSecondLeader      = anotherLeader;
  theFirstWitnessLine  = aLine;
  theSecondWitnessLine = anotherLine;
  InitTypeAndForm(204,0);
}

    Handle(IGESDimen_GeneralNote)  IGESDimen_CurveDimension::Note () const 
{
  return theNote;
}

    Handle(IGESData_IGESEntity)  IGESDimen_CurveDimension::FirstCurve () const 
{
  return theFirstCurve;
}

    Standard_Boolean  IGESDimen_CurveDimension::HasSecondCurve () const 
{
  return (! theSecondCurve.IsNull());
}

    Handle(IGESData_IGESEntity)  IGESDimen_CurveDimension::SecondCurve () const 
{
  return theSecondCurve;
}

    Handle(IGESDimen_LeaderArrow)  IGESDimen_CurveDimension::FirstLeader () const 
{
  return theFirstLeader;
}

    Handle(IGESDimen_LeaderArrow)  IGESDimen_CurveDimension::SecondLeader () const 
{
  return theSecondLeader;
}

    Standard_Boolean  IGESDimen_CurveDimension::HasFirstWitnessLine () const 
{
  return (! theFirstWitnessLine.IsNull());
}

    Handle(IGESDimen_WitnessLine)  IGESDimen_CurveDimension::FirstWitnessLine
  () const 
{
  return theFirstWitnessLine;
}

    Standard_Boolean  IGESDimen_CurveDimension::HasSecondWitnessLine () const 
{
  return (! theSecondWitnessLine.IsNull());
}

    Handle(IGESDimen_WitnessLine)  IGESDimen_CurveDimension::SecondWitnessLine
  () const 
{
  return theSecondWitnessLine;
}
