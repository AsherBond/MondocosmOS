//--------------------------------------------------------------------
//
//  File Name : IGESGraph_Color.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESGraph_ToolColor.ixx>
#include <IGESData_ParamCursor.hxx>
#include <TCollection_HAsciiString.hxx>
#include <Interface_Macros.hxx>
#include <IGESData_Dump.hxx>


IGESGraph_ToolColor::IGESGraph_ToolColor ()    {  }


void IGESGraph_ToolColor::ReadOwnParams
  (const Handle(IGESGraph_Color)& ent,
   const Handle(IGESData_IGESReaderData)& /*IR*/, IGESData_ParamReader& PR) const
{
  Standard_Real tempRed, tempGreen, tempBlue;
  Handle(TCollection_HAsciiString) tempColorName;

  PR.ReadReal(PR.Current(), "RED as % Of Full Intensity", tempRed);

  PR.ReadReal(PR.Current(), "GREEN as % Of Full Intensity", tempGreen);

  PR.ReadReal(PR.Current(), "BLUE as % Of Full Intensity", tempBlue);

  if ((PR.CurrentNumber() <= PR.NbParams()) &&
      (PR.ParamType(PR.CurrentNumber()) == Interface_ParamText))
    PR.ReadText(PR.Current(), "Color Name", tempColorName);

  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init(tempRed, tempGreen, tempBlue, tempColorName);
}

void IGESGraph_ToolColor::WriteOwnParams
  (const Handle(IGESGraph_Color)& ent, IGESData_IGESWriter& IW)  const
{
  Standard_Real Red,Green,Blue;
  ent->RGBIntensity(Red,Green,Blue);
  IW.Send(Red);
  IW.Send(Green);
  IW.Send(Blue);
//  ATTENTION  place a reserver (Null) silya des pointeurs additionnels
  if (ent->HasColorName())
    IW.Send(ent->ColorName());
  else IW.SendVoid();    // placekeeper to be reserved for additional pointers
}

void  IGESGraph_ToolColor::OwnShared
  (const Handle(IGESGraph_Color)& /*ent*/, Interface_EntityIterator& /*iter*/) const
{
}

void IGESGraph_ToolColor::OwnCopy
  (const Handle(IGESGraph_Color)& another,
   const Handle(IGESGraph_Color)& ent, Interface_CopyTool& /*TC*/) const
{
  Standard_Real tempRed, tempGreen, tempBlue;
  Handle(TCollection_HAsciiString) tempColorName;
  another->RGBIntensity(tempRed, tempGreen, tempBlue);
  if (another->HasColorName())
    tempColorName = new TCollection_HAsciiString(another->ColorName());

  ent->Init(tempRed, tempGreen, tempBlue, tempColorName);
}

IGESData_DirChecker  IGESGraph_ToolColor::DirChecker
  (const Handle(IGESGraph_Color)& /*ent*/ )  const
{
  IGESData_DirChecker DC(314, 0);
  DC.Structure(IGESData_DefVoid);
  DC.LineFont(IGESData_DefVoid);
  DC.LineWeight(IGESData_DefVoid);
  DC.Color(IGESData_DefAny);
  DC.BlankStatusIgnored();
  DC.SubordinateStatusRequired(0);
  DC.UseFlagRequired(2);
  DC.HierarchyStatusIgnored();

  return DC;
}

void IGESGraph_ToolColor::OwnCheck
  (const Handle(IGESGraph_Color)& /*ent*/,
   const Interface_ShareTool& , Handle(Interface_Check)& /*ach*/)  const
{
//  if (ent->RankColor() == 0)
//    ach.AddFail("Color Rank is zero");
//  else if (ent->RankColor() < 1 || ent->RankColor() > 8)
//    ach.AddFail("Color Rank not between 1 to 8");
}

void IGESGraph_ToolColor::OwnDump
  (const Handle(IGESGraph_Color)& ent, const IGESData_IGESDumper& /*dumper*/,
   const Handle(Message_Messenger)& S, const Standard_Integer /*level*/)  const
{
  S << "IGESGraph_Color" << endl;

  Standard_Real Red,Green,Blue;
  ent->RGBIntensity(Red,Green,Blue);
  S << "Red   (in % Of Full Intensity) : " << Red   << endl;
  S << "Green (in % Of Full Intensity) : " << Green << endl;
  S << "Blue  (in % Of Full Intensity) : " << Blue  << endl;
  S << "Color Name : ";
  IGESData_DumpString(S,ent->ColorName());
  S << endl;
}
