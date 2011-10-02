//--------------------------------------------------------------------
//
//  File Name : IGESBasic_ExternalRefLibName.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESBasic_ToolExternalRefLibName.ixx>
#include <IGESData_ParamCursor.hxx>
#include <TCollection_HAsciiString.hxx>
#include <Interface_Macros.hxx>
#include <IGESData_Dump.hxx>


IGESBasic_ToolExternalRefLibName::IGESBasic_ToolExternalRefLibName ()    {  }


void  IGESBasic_ToolExternalRefLibName::ReadOwnParams
  (const Handle(IGESBasic_ExternalRefLibName)& ent,
   const Handle(IGESData_IGESReaderData)& /* IR */, IGESData_ParamReader& PR) const
{
  //Standard_Boolean st; //szv#4:S4163:12Mar99 not needed
  Handle(TCollection_HAsciiString) tempLibName;
  Handle(TCollection_HAsciiString) tempExtRefEntitySymbName;
  PR.ReadText(PR.Current(), "Name of Library", tempLibName); //szv#4:S4163:12Mar99 `st=` not needed
  PR.ReadText(PR.Current(), "External Reference Symbolic Name",
	      tempExtRefEntitySymbName); //szv#4:S4163:12Mar99 `st=` not needed

  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init(tempLibName, tempExtRefEntitySymbName);
}

void  IGESBasic_ToolExternalRefLibName::WriteOwnParams
  (const Handle(IGESBasic_ExternalRefLibName)& ent, IGESData_IGESWriter& IW) const
{
  IW.Send(ent->LibraryName());
  IW.Send(ent->ReferenceName());
}

void  IGESBasic_ToolExternalRefLibName::OwnShared
  (const Handle(IGESBasic_ExternalRefLibName)& /* ent */, Interface_EntityIterator& /* iter */) const
{
}

void  IGESBasic_ToolExternalRefLibName::OwnCopy
  (const Handle(IGESBasic_ExternalRefLibName)& another,
   const Handle(IGESBasic_ExternalRefLibName)& ent, Interface_CopyTool& /* TC */) const
{
  Handle(TCollection_HAsciiString) tempLibName =
    new TCollection_HAsciiString(another->LibraryName());
  Handle(TCollection_HAsciiString) tempRefName =
    new TCollection_HAsciiString(another->ReferenceName());
  ent->Init(tempLibName, tempRefName);
}

IGESData_DirChecker  IGESBasic_ToolExternalRefLibName::DirChecker
  (const Handle(IGESBasic_ExternalRefLibName)& /* ent */ ) const
{
  IGESData_DirChecker DC(416, 4);
  DC.Structure(IGESData_DefVoid);
  DC.GraphicsIgnored();
  DC.LineFont(IGESData_DefVoid);
  DC.LineWeight(IGESData_DefVoid);
  DC.Color(IGESData_DefVoid);
  DC.BlankStatusRequired(0);
  DC.HierarchyStatusRequired(0);
  return DC;
}

void  IGESBasic_ToolExternalRefLibName::OwnCheck
  (const Handle(IGESBasic_ExternalRefLibName)& /* ent */,
   const Interface_ShareTool& , Handle(Interface_Check)& /* ach */) const
{
}

void  IGESBasic_ToolExternalRefLibName::OwnDump
  (const Handle(IGESBasic_ExternalRefLibName)& ent, const IGESData_IGESDumper& /* dumper */,
   const Handle(Message_Messenger)& S,const Standard_Integer /* level */) const
{
  S << "IGESBasic_ExternalRefLibName" << endl;
  S << "Name of Library : ";
  IGESData_DumpString(S,ent->LibraryName());
  S << endl;
  S << "External Reference Symbolic Name : ";
  IGESData_DumpString(S,ent->ReferenceName());
  S << endl;
}
