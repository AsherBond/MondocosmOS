// File:	StepFEA_FeaModel.cxx
// Created:	Thu Dec 12 17:51:05 2002 
// Author:	data exchange team
// Generator:	ExpToCas (EXPRESS -> CASCADE/XSTEP Translator) V1.2
// Copyright:	Open CASCADE 2002

#include <StepFEA_FeaModel.ixx>

//=======================================================================
//function : StepFEA_FeaModel
//purpose  : 
//=======================================================================

StepFEA_FeaModel::StepFEA_FeaModel ()
{
}

//=======================================================================
//function : Init
//purpose  : 
//=======================================================================

void StepFEA_FeaModel::Init (const Handle(TCollection_HAsciiString) &aRepresentation_Name,
                             const Handle(StepRepr_HArray1OfRepresentationItem) &aRepresentation_Items,
                             const Handle(StepRepr_RepresentationContext) &aRepresentation_ContextOfItems,
                             const Handle(TCollection_HAsciiString) &aCreatingSoftware,
                             const Handle(TColStd_HArray1OfAsciiString) &aIntendedAnalysisCode,
                             const Handle(TCollection_HAsciiString) &aDescription,
                             const Handle(TCollection_HAsciiString) &aAnalysisType)
{
  StepRepr_Representation::Init(aRepresentation_Name,
                                aRepresentation_Items,
                                aRepresentation_ContextOfItems);

  theCreatingSoftware = aCreatingSoftware;

  theIntendedAnalysisCode = aIntendedAnalysisCode;

  theDescription = aDescription;

  theAnalysisType = aAnalysisType;
}

//=======================================================================
//function : CreatingSoftware
//purpose  : 
//=======================================================================

Handle(TCollection_HAsciiString) StepFEA_FeaModel::CreatingSoftware () const
{
  return theCreatingSoftware;
}

//=======================================================================
//function : SetCreatingSoftware
//purpose  : 
//=======================================================================

void StepFEA_FeaModel::SetCreatingSoftware (const Handle(TCollection_HAsciiString) &aCreatingSoftware)
{
  theCreatingSoftware = aCreatingSoftware;
}

//=======================================================================
//function : IntendedAnalysisCode
//purpose  : 
//=======================================================================

Handle(TColStd_HArray1OfAsciiString) StepFEA_FeaModel::IntendedAnalysisCode () const
{
  return theIntendedAnalysisCode;
}

//=======================================================================
//function : SetIntendedAnalysisCode
//purpose  : 
//=======================================================================

void StepFEA_FeaModel::SetIntendedAnalysisCode (const Handle(TColStd_HArray1OfAsciiString) &aIntendedAnalysisCode)
{
  theIntendedAnalysisCode = aIntendedAnalysisCode;
}

//=======================================================================
//function : Description
//purpose  : 
//=======================================================================

Handle(TCollection_HAsciiString) StepFEA_FeaModel::Description () const
{
  return theDescription;
}

//=======================================================================
//function : SetDescription
//purpose  : 
//=======================================================================

void StepFEA_FeaModel::SetDescription (const Handle(TCollection_HAsciiString) &aDescription)
{
  theDescription = aDescription;
}

//=======================================================================
//function : AnalysisType
//purpose  : 
//=======================================================================

Handle(TCollection_HAsciiString) StepFEA_FeaModel::AnalysisType () const
{
  return theAnalysisType;
}

//=======================================================================
//function : SetAnalysisType
//purpose  : 
//=======================================================================

void StepFEA_FeaModel::SetAnalysisType (const Handle(TCollection_HAsciiString) &aAnalysisType)
{
  theAnalysisType = aAnalysisType;
}
