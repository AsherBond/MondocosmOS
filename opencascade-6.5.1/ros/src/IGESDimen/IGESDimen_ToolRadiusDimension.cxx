//--------------------------------------------------------------------
//
//  File Name : IGESDimen_RadiusDimension.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESDimen_ToolRadiusDimension.ixx>
#include <IGESData_ParamCursor.hxx>
#include <IGESDimen_GeneralNote.hxx>
#include <IGESDimen_LeaderArrow.hxx>
#include <gp_XY.hxx>
#include <gp_Pnt2d.hxx>
#include <IGESData_Dump.hxx>
#include <Interface_Macros.hxx>


IGESDimen_ToolRadiusDimension::IGESDimen_ToolRadiusDimension ()    {  }


void IGESDimen_ToolRadiusDimension::ReadOwnParams
  (const Handle(IGESDimen_RadiusDimension)& ent,
   const Handle(IGESData_IGESReaderData)& IR, IGESData_ParamReader& PR) const
{
  Handle(IGESDimen_GeneralNote) tempNote;
  Handle(IGESDimen_LeaderArrow) leadArr;
  gp_XY arcCenter;
  Handle(IGESDimen_LeaderArrow) leadArr2;
  //Standard_Boolean st; //szv#4:S4163:12Mar99 not needed

  PR.ReadEntity(IR, PR.Current(), "General Note",
		STANDARD_TYPE(IGESDimen_GeneralNote), tempNote); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadEntity(IR, PR.Current(), "Leader arrow",
		STANDARD_TYPE(IGESDimen_LeaderArrow), leadArr); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadXY(PR.CurrentList(1, 2), "Arc center", arcCenter); //szv#4:S4163:12Mar99 `st=` not needed

  if (ent->FormNumber() == 1)
    PR.ReadEntity(IR, PR.Current(), "Leader arrow 2",
		  STANDARD_TYPE(IGESDimen_LeaderArrow), leadArr2, Standard_True); //szv#4:S4163:12Mar99 `st=` not needed

  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init(tempNote, leadArr, arcCenter, leadArr2);
}

void IGESDimen_ToolRadiusDimension::WriteOwnParams
  (const Handle(IGESDimen_RadiusDimension)& ent, IGESData_IGESWriter& IW) const
{
  IW.Send(ent->Note());
  IW.Send(ent->Leader());
  IW.Send(ent->Center().X());
  IW.Send(ent->Center().Y());
  if (ent->HasLeader2() || ent->FormNumber() == 1)
    IW.Send(ent->Leader2());
}

void  IGESDimen_ToolRadiusDimension::OwnShared
  (const Handle(IGESDimen_RadiusDimension)& ent, Interface_EntityIterator& iter) const
{
  iter.GetOneItem(ent->Note());
  iter.GetOneItem(ent->Leader());
  iter.GetOneItem(ent->Leader2());
}

void IGESDimen_ToolRadiusDimension::OwnCopy
  (const Handle(IGESDimen_RadiusDimension)& another,
   const Handle(IGESDimen_RadiusDimension)& ent, Interface_CopyTool& TC) const
{
  DeclareAndCast(IGESDimen_GeneralNote, tempNote,
		 TC.Transferred(another->Note()));
  DeclareAndCast(IGESDimen_LeaderArrow, leadArr,
		 TC.Transferred(another->Leader()));
  gp_XY arcCenter = another->Center().XY();
  Handle(IGESDimen_LeaderArrow) leadArr2;
  if (another->HasLeader2()) leadArr2 =
    GetCasted(IGESDimen_LeaderArrow,TC.Transferred(another->Leader2()));
  ent->Init(tempNote, leadArr, arcCenter, leadArr2);
  ent->InitForm (another->FormNumber());
}

IGESData_DirChecker IGESDimen_ToolRadiusDimension::DirChecker
  (const Handle(IGESDimen_RadiusDimension)& /* ent */ ) const
{
  IGESData_DirChecker DC(222, 0,1);
  DC.Structure(IGESData_DefVoid);
  DC.LineFont(IGESData_DefAny);
  DC.LineWeight(IGESData_DefValue);
  DC.Color(IGESData_DefAny);

  DC.UseFlagRequired (1);

  return DC;
}

void IGESDimen_ToolRadiusDimension::OwnCheck
  (const Handle(IGESDimen_RadiusDimension)& ent,
   const Interface_ShareTool& , Handle(Interface_Check)& ach) const
{
  if (ent->HasLeader2() && ent->FormNumber() == 0)
    ach->AddFail("Value of Form Number not consistent with presence of Leader2");
// Form 1 : Leader can be defined or not. Form 0 : only cannot
}

void IGESDimen_ToolRadiusDimension::OwnDump
  (const Handle(IGESDimen_RadiusDimension)& ent, const IGESData_IGESDumper& dumper,
   const Handle(Message_Messenger)& S,const Standard_Integer level) const
{
  S << "IGESDimen_RadiusDimension" << endl;
  Standard_Integer sublevel = (level <= 4) ? 0 : 1;

  S << "General note : ";
  dumper.Dump(ent->Note(),S, sublevel);
  S << endl;
  S << "Leader arrow : ";
  dumper.Dump(ent->Leader(),S, sublevel);
  S << endl;
  S << "Arc center : ";
  IGESData_DumpXYLZ(S,level,ent->Center(),ent->Location(),ent->Leader()->ZDepth());
  if (ent->HasLeader2())
    {
      S << endl << "Leader arrow 2 : ";
      dumper.Dump(ent->Leader2(),S, sublevel);
      S << endl;
    }
  S << endl;
}
