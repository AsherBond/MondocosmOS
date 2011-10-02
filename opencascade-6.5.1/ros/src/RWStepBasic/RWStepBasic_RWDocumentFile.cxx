// File:	RWStepBasic_RWDocumentFile.cxx
// Created:	Thu May 11 16:38:00 2000 
// Author:	data exchange team
// Generator:	ExpToCas (EXPRESS -> CASCADE/XSTEP Translator) V1.1
// Copyright:	Matra Datavision 2000

#include <RWStepBasic_RWDocumentFile.ixx>
#include <StepBasic_CharacterizedObject.hxx>

//=======================================================================
//function : RWStepBasic_RWDocumentFile
//purpose  : 
//=======================================================================

RWStepBasic_RWDocumentFile::RWStepBasic_RWDocumentFile ()
{
}

//=======================================================================
//function : ReadStep
//purpose  : 
//=======================================================================

void RWStepBasic_RWDocumentFile::ReadStep (const Handle(StepData_StepReaderData)& data,
                                           const Standard_Integer num,
                                           Handle(Interface_Check)& ach,
                                           const Handle(StepBasic_DocumentFile) &ent) const
{
  // Check number of parameters
  if ( ! data->CheckNbParams(num,6,ach,"document_file") ) return;

  // Inherited fields of Document

  Handle(TCollection_HAsciiString) aDocument_Id;
  data->ReadString (num, 1, "document.id", ach, aDocument_Id);

  Handle(TCollection_HAsciiString) aDocument_Name;
  data->ReadString (num, 2, "document.name", ach, aDocument_Name);

  Handle(TCollection_HAsciiString) aDocument_Description;
  Standard_Boolean hasDocument_Description = Standard_True;
  if ( data->IsParamDefined (num,3) ) {
    data->ReadString (num, 3, "document.description", ach, aDocument_Description);
  }
  else {
    hasDocument_Description = Standard_False;
  }

  Handle(StepBasic_DocumentType) aDocument_Kind;
  data->ReadEntity (num, 4, "document.kind", ach, STANDARD_TYPE(StepBasic_DocumentType), aDocument_Kind);

  // Inherited fields of CharacterizedObject

  Handle(TCollection_HAsciiString) aCharacterizedObject_Name;
  data->ReadString (num, 5, "characterized_object.name", ach, aCharacterizedObject_Name);

  Handle(TCollection_HAsciiString) aCharacterizedObject_Description;
  Standard_Boolean hasCharacterizedObject_Description = Standard_True;
  if ( data->IsParamDefined (num,6) ) {
    data->ReadString (num, 6, "characterized_object.description", ach, aCharacterizedObject_Description);
  }
  else {
    hasCharacterizedObject_Description = Standard_False;
  }

  // Initialize entity
  ent->Init(aDocument_Id,
            aDocument_Name,
            hasDocument_Description,
            aDocument_Description,
            aDocument_Kind,
            aCharacterizedObject_Name,
            hasCharacterizedObject_Description,
            aCharacterizedObject_Description);
}

//=======================================================================
//function : WriteStep
//purpose  : 
//=======================================================================

void RWStepBasic_RWDocumentFile::WriteStep (StepData_StepWriter& SW,
                                            const Handle(StepBasic_DocumentFile) &ent) const
{

  // Inherited fields of Document

  SW.Send (ent->StepBasic_Document::Id());

  SW.Send (ent->StepBasic_Document::Name());

  if ( ent->StepBasic_Document::HasDescription() ) {
    SW.Send (ent->StepBasic_Document::Description());
  }
  else SW.SendUndef();

  SW.Send (ent->StepBasic_Document::Kind());

  // Inherited fields of CharacterizedObject

  SW.Send (ent->CharacterizedObject()->Name());

  if ( ent->CharacterizedObject()->HasDescription() ) {
    SW.Send (ent->CharacterizedObject()->Description());
  }
  else SW.SendUndef();
}

//=======================================================================
//function : Share
//purpose  : 
//=======================================================================

void RWStepBasic_RWDocumentFile::Share (const Handle(StepBasic_DocumentFile) &ent,
                                        Interface_EntityIterator& iter) const
{

  // Inherited fields of Document

  iter.AddItem (ent->StepBasic_Document::Kind());

  // Inherited fields of CharacterizedObject
}
