//--------------------------------------------------------------------
//
//  File Name : IGESGeom_Line.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESGeom_ToolLine.ixx>
#include <IGESData_ParamCursor.hxx>
#include <gp_XYZ.hxx>
#include <gp_Pnt.hxx>
#include <IGESData_Dump.hxx>
#include <Interface_Macros.hxx>

// MGE 29/07/98
#include <Message_Msg.hxx>

IGESGeom_ToolLine::IGESGeom_ToolLine ()    {  }


void IGESGeom_ToolLine::ReadOwnParams
  (const Handle(IGESGeom_Line)& ent,
   const Handle(IGESData_IGESReaderData)& /* IR */, IGESData_ParamReader& PR) const
{
  // MGE 29/07/98
  // Building of messages
  //====================================
  Message_Msg Msg89("XSTEP_89");
  Message_Msg Msg90("XSTEP_90");
  //====================================

  gp_XYZ aStart, anEnd;

  //Standard_Boolean st; //szv#4:S4163:12Mar99 not needed

  PR.ReadXYZ(PR.CurrentList(1, 3),Msg89, aStart); //szv#4:S4163:12Mar99 `st=` not needed
  PR.ReadXYZ(PR.CurrentList(1, 3), Msg90, anEnd); //szv#4:S4163:12Mar99 `st=` not needed

 /* st = PR.ReadXYZ(PR.CurrentList(1, 3), "Starting Point", aStart);
    st = PR.ReadXYZ(PR.CurrentList(1, 3), "End Point", anEnd);
 */
  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init(aStart, anEnd);
}

void IGESGeom_ToolLine::WriteOwnParams
  (const Handle(IGESGeom_Line)& ent, IGESData_IGESWriter& IW)  const
{
  IW.Send(ent->StartPoint().X());
  IW.Send(ent->StartPoint().Y());
  IW.Send(ent->StartPoint().Z());
  IW.Send(ent->EndPoint().X());
  IW.Send(ent->EndPoint().Y());
  IW.Send(ent->EndPoint().Z());
}

void  IGESGeom_ToolLine::OwnShared
  (const Handle(IGESGeom_Line)& /* ent */, Interface_EntityIterator& /* iter */) const
{
}

void IGESGeom_ToolLine::OwnCopy 
  (const Handle(IGESGeom_Line)& another,
   const Handle(IGESGeom_Line)& ent, Interface_CopyTool& /* TC */) const
{
  ent->Init(another->StartPoint().XYZ(), another->EndPoint().XYZ());
}

IGESData_DirChecker IGESGeom_ToolLine::DirChecker
  (const Handle(IGESGeom_Line)& /* ent */ )  const
{
  IGESData_DirChecker DC(110, 0,2);
  DC.Structure(IGESData_DefVoid);
  DC.LineFont(IGESData_DefAny);
//  DC.LineWeight(IGESData_DefValue);
  DC.Color(IGESData_DefAny);
  DC.HierarchyStatusIgnored();
  return DC;
}

void IGESGeom_ToolLine::OwnCheck
  (const Handle(IGESGeom_Line)& /* ent */,
   const Interface_ShareTool& , Handle(Interface_Check)& /* ach */)  const
{ 
}

void IGESGeom_ToolLine::OwnDump
  (const Handle(IGESGeom_Line)& ent, const IGESData_IGESDumper& /* dumper */,
   const Handle(Message_Messenger)& S, const Standard_Integer level)  const
{
  Standard_Integer infin = ent->Infinite();
  switch (infin) {
    case 1 : S << "Semi-Infinite Line"<<endl;  break;
    case 2 : S << "Infinite Line"<<endl;  break;
    default : S << "Bounded Line"<<endl; break;
  }

  S << "Line from IGESGeom" << endl;
  S << "Starting Point : ";
  IGESData_DumpXYZL(S,level, ent->StartPoint(), ent->Location());
  S << endl;
  S << "End Point : ";
  IGESData_DumpXYZL(S,level, ent->EndPoint(), ent->Location());
  S << endl;
}
