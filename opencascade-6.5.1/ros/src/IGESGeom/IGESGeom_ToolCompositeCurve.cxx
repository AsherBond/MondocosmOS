//--------------------------------------------------------------------
//
//  File Name : IGESGeom_CompositeCurve.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESGeom_ToolCompositeCurve.ixx>
#include <IGESData_ParamCursor.hxx>
#include <IGESData_IGESEntity.hxx>
#include <IGESData_HArray1OfIGESEntity.hxx>
#include <IGESData_Dump.hxx>
#include <Interface_Macros.hxx>

// MGE 28/07/98
#include <Message_Msg.hxx>

IGESGeom_ToolCompositeCurve::IGESGeom_ToolCompositeCurve ()    {  }


void  IGESGeom_ToolCompositeCurve::ReadOwnParams
  (const Handle(IGESGeom_CompositeCurve)& ent,
   const Handle(IGESData_IGESReaderData)& IR, IGESData_ParamReader& PR) const
{
  // MGE 28/07/98
  // Building of messages
  //Standard_Boolean st; //szv#4:S4163:12Mar99 moved down
  Handle(IGESData_HArray1OfIGESEntity) tempEntities;

  Standard_Integer num; //szv#4:S4163:12Mar99 i not needed
  
  Standard_Boolean st = PR.ReadInteger(PR.Current(), num);
  // st = PR.ReadInteger(PR.Current(), "Number of Components", num);
  if (st && (num > 0)){
    Message_Msg Msg80("XSTEP_80");
    PR.ReadEnts (IR,PR.CurrentList(num),Msg80,tempEntities); //szv#4:S4163:12Mar99 `st=` not needed
  //else st = PR.ReadEnts (IR,PR.CurrentList(num),"List of Components",tempEntities);
  }
  //if (st && num <= 0) PR.SendFail(Msg79);
  else{
    Message_Msg Msg79("XSTEP_79");
    PR.SendFail(Msg79);
  }

  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init(tempEntities);
}

void  IGESGeom_ToolCompositeCurve::WriteOwnParams
  (const Handle(IGESGeom_CompositeCurve)& ent, IGESData_IGESWriter& IW)  const
{
  Standard_Integer num = ent->NbCurves();  Standard_Integer i;
  IW.Send(num);
  for ( num = ent->NbCurves(), i = 1; i <= num; i++ )
    IW.Send(ent->Curve(i));
}

void  IGESGeom_ToolCompositeCurve::OwnShared
  (const Handle(IGESGeom_CompositeCurve)& ent, Interface_EntityIterator& iter) const
{
  Standard_Integer num = ent->NbCurves();
  for ( Standard_Integer i = 1; i <= num; i++ )
    iter.GetOneItem(ent->Curve(i));
}

void  IGESGeom_ToolCompositeCurve::OwnCopy
  (const Handle(IGESGeom_CompositeCurve)& another,
   const Handle(IGESGeom_CompositeCurve)& ent, Interface_CopyTool& TC) const
{
  Standard_Integer i, num = another->NbCurves();
  Handle(IGESData_HArray1OfIGESEntity) tempEntities =
    new IGESData_HArray1OfIGESEntity(1, num);
  for ( i = 1; i <= num; i++ )
    {
      DeclareAndCast(IGESData_IGESEntity, new_ent,
		     TC.Transferred(another->Curve(i)));
      tempEntities->SetValue(i, new_ent);
    }
  ent->Init(tempEntities);
}

IGESData_DirChecker  IGESGeom_ToolCompositeCurve::DirChecker
  (const Handle(IGESGeom_CompositeCurve)& /* ent */ )  const
{
  IGESData_DirChecker DC(102, 0);
  DC.Structure(IGESData_DefVoid);
  DC.GraphicsIgnored();
  DC.LineFont(IGESData_DefAny);
//  DC.LineWeight(IGESData_DefValue);
  DC.Color(IGESData_DefAny);
  return DC;
}

void  IGESGeom_ToolCompositeCurve::OwnCheck
  (const Handle(IGESGeom_CompositeCurve)& /* ent */,
   const Interface_ShareTool& , Handle(Interface_Check)& /* ach */)  const
{
}

void  IGESGeom_ToolCompositeCurve::OwnDump
  (const Handle(IGESGeom_CompositeCurve)& ent, const IGESData_IGESDumper& dumper,
   const Handle(Message_Messenger)& S, const Standard_Integer level)  const
{
  S << "IGESGeom_CompositeCurve" << endl;
  S << "Curve Entities : " << endl;
  IGESData_DumpEntities(S,dumper ,level,1, ent->NbCurves(),ent->Curve);
  S << endl;
}
