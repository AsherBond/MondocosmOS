// File:	RWStepRepr_RWConfigurationDesign.cxx
// Created:	Fri Nov 26 16:26:36 1999 
// Author:	Andrey BETENEV
// Generator:	ExpToCas (EXPRESS -> CASCADE/XSTEP Translator) V1.0
// Copyright:	Matra Datavision 1999

#include <RWStepRepr_RWConfigurationDesign.ixx>

//=======================================================================
//function : RWStepRepr_RWConfigurationDesign
//purpose  : 
//=======================================================================

RWStepRepr_RWConfigurationDesign::RWStepRepr_RWConfigurationDesign ()
{
}

//=======================================================================
//function : ReadStep
//purpose  : 
//=======================================================================

void RWStepRepr_RWConfigurationDesign::ReadStep (const Handle(StepData_StepReaderData)& data,
                                                 const Standard_Integer num,
                                                 Handle(Interface_Check)& ach,
                                                 const Handle(StepRepr_ConfigurationDesign) &ent) const
{
  // Check number of parameters
  if ( ! data->CheckNbParams(num,2,ach,"configuration_design") ) return;

  // Own fields of ConfigurationDesign

  Handle(StepRepr_ConfigurationItem) aConfiguration;
  data->ReadEntity (num, 1, "configuration", ach, STANDARD_TYPE(StepRepr_ConfigurationItem), aConfiguration);

  StepRepr_ConfigurationDesignItem aDesign;
  data->ReadEntity (num, 2, "design", ach, aDesign);

  // Initialize entity
  ent->Init(aConfiguration,
            aDesign);
}

//=======================================================================
//function : WriteStep
//purpose  : 
//=======================================================================

void RWStepRepr_RWConfigurationDesign::WriteStep (StepData_StepWriter& SW,
                                                  const Handle(StepRepr_ConfigurationDesign) &ent) const
{

  // Own fields of ConfigurationDesign

  SW.Send (ent->Configuration());

  SW.Send (ent->Design().Value());
}

//=======================================================================
//function : Share
//purpose  : 
//=======================================================================

void RWStepRepr_RWConfigurationDesign::Share (const Handle(StepRepr_ConfigurationDesign) &ent,
                                              Interface_EntityIterator& iter) const
{

  // Own fields of ConfigurationDesign

  iter.AddItem (ent->Configuration());

  iter.AddItem (ent->Design().Value());
}
