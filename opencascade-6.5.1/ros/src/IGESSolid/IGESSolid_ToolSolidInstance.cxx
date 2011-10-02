//--------------------------------------------------------------------
//
//  File Name : IGESSolid_SolidInstance.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESSolid_ToolSolidInstance.ixx>
#include <IGESData_ParamCursor.hxx>
#include <IGESData_IGESEntity.hxx>
#include <Interface_Macros.hxx>
#include <Message_Messenger.hxx>

IGESSolid_ToolSolidInstance::IGESSolid_ToolSolidInstance ()    {  }


void  IGESSolid_ToolSolidInstance::ReadOwnParams
  (const Handle(IGESSolid_SolidInstance)& ent,
   const Handle(IGESData_IGESReaderData)& IR, IGESData_ParamReader& PR) const
{
  Handle(IGESData_IGESEntity) tempEntity;
  //Standard_Boolean st; //szv#4:S4163:12Mar99 not needed

  PR.ReadEntity(IR, PR.Current(), "Solid Entity", tempEntity); //szv#4:S4163:12Mar99 `st=` not needed

  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init(tempEntity);
}

void  IGESSolid_ToolSolidInstance::WriteOwnParams
  (const Handle(IGESSolid_SolidInstance)& ent, IGESData_IGESWriter& IW) const
{
  IW.Send(ent->Entity());
}

void  IGESSolid_ToolSolidInstance::OwnShared
  (const Handle(IGESSolid_SolidInstance)& ent, Interface_EntityIterator& iter) const
{
  iter.GetOneItem(ent->Entity());
}

void  IGESSolid_ToolSolidInstance::OwnCopy
  (const Handle(IGESSolid_SolidInstance)& another,
   const Handle(IGESSolid_SolidInstance)& ent, Interface_CopyTool& TC) const
{
  DeclareAndCast(IGESData_IGESEntity, tempEntity,
		 TC.Transferred(another->Entity()));
  ent->Init (tempEntity);
}

IGESData_DirChecker  IGESSolid_ToolSolidInstance::DirChecker
  (const Handle(IGESSolid_SolidInstance)& /*ent*/) const
{
  IGESData_DirChecker DC(430, 0,1);

  DC.Structure  (IGESData_DefVoid);
  DC.LineFont   (IGESData_DefAny);
  DC.Color      (IGESData_DefAny);

  DC.GraphicsIgnored (1);
  return DC;
}

void  IGESSolid_ToolSolidInstance::OwnCheck
  (const Handle(IGESSolid_SolidInstance)& /*ent*/,
   const Interface_ShareTool& , Handle(Interface_Check)& /*ach*/) const
{
}

void  IGESSolid_ToolSolidInstance::OwnDump
  (const Handle(IGESSolid_SolidInstance)& ent, const IGESData_IGESDumper& dumper,
   const Handle(Message_Messenger)& S, const Standard_Integer level) const
{
  S << "IGESSolid_SolidInstance" << endl;

  S << "Solid entity : ";
  dumper.Dump(ent->Entity(),S, (level <= 4) ? 0 : 1);
  S << endl;
}
