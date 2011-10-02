//--------------------------------------------------------------------
//
//  File Name : IGESSolid_SolidOfRevolution.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESSolid_ToolSolidOfRevolution.ixx>
#include <IGESData_ParamCursor.hxx>
#include <IGESData_IGESEntity.hxx>
#include <gp_XYZ.hxx>
#include <gp_Pnt.hxx>
#include <gp_Dir.hxx>
#include <IGESData_Dump.hxx>
#include <Interface_Macros.hxx>


IGESSolid_ToolSolidOfRevolution::IGESSolid_ToolSolidOfRevolution ()    {  }


void  IGESSolid_ToolSolidOfRevolution::ReadOwnParams
  (const Handle(IGESSolid_SolidOfRevolution)& ent,
   const Handle(IGESData_IGESReaderData)& IR, IGESData_ParamReader& PR) const
{
  Handle(IGESData_IGESEntity) tempEntity;
  gp_XYZ tempAxisPoint;
  gp_XYZ tempAxis;
  Standard_Real tempFraction;
  Standard_Real tempreal;
  //Standard_Boolean st; //szv#4:S4163:12Mar99 not needed

  PR.ReadEntity(IR, PR.Current(), "Curve Entity", tempEntity); //szv#4:S4163:12Mar99 `st=` not needed

  if (PR.DefinedElseSkip())
    PR.ReadReal(PR.Current(), "Fraction of rotation", tempFraction); //szv#4:S4163:12Mar99 `st=` not needed
  else
    tempFraction = 1.0;
  if (PR.DefinedElseSkip())
    {
      //st = PR.ReadReal(PR.Current(), "Axis Point (X)", tempreal); //szv#4:S4163:12Mar99 moved in if
      if (PR.ReadReal(PR.Current(), "Axis Point (X)", tempreal))
	tempAxisPoint.SetX(tempreal);
    }
  else  tempAxisPoint.SetX(0.0);

  if (PR.DefinedElseSkip())
    {
      //st = PR.ReadReal(PR.Current(), "Axis Point (Y)", tempreal); //szv#4:S4163:12Mar99 moved in if
      if (PR.ReadReal(PR.Current(), "Axis Point (Y)", tempreal))
	tempAxisPoint.SetY(tempreal);
    }
  else  tempAxisPoint.SetY(0.0);

  if (PR.DefinedElseSkip())
    {
      //st = PR.ReadReal(PR.Current(), "Axis Point (Z)", tempreal); //szv#4:S4163:12Mar99 moved in if
      if (PR.ReadReal(PR.Current(), "Axis Point (Z)", tempreal))
	tempAxisPoint.SetZ(tempreal);
    }
  else  tempAxisPoint.SetZ(0.0);

  if (PR.DefinedElseSkip())
    {
      //st = PR.ReadReal(PR.Current(), "Axis direction (I)", tempreal); //szv#4:S4163:12Mar99 moved in if
      if (PR.ReadReal(PR.Current(), "Axis direction (I)", tempreal))
	tempAxis.SetX(tempreal);
    }
  else  tempAxis.SetX(0.0);

  if (PR.DefinedElseSkip())
    {
      //st = PR.ReadReal(PR.Current(), "Axis direction (J)", tempreal); //szv#4:S4163:12Mar99 moved in if
      if (PR.ReadReal(PR.Current(), "Axis direction (J)", tempreal))
	tempAxis.SetY(tempreal);
    }
  else  tempAxis.SetY(0.0);

  if (PR.DefinedElseSkip())
    {
      //st = PR.ReadReal(PR.Current(), "Axis direction (K)", tempreal); //szv#4:S4163:12Mar99 moved in if
      if (PR.ReadReal(PR.Current(), "Axis direction (K)", tempreal))
	tempAxis.SetZ(tempreal);
    }
  else  tempAxis.SetZ(1.0);

  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init (tempEntity, tempFraction, tempAxisPoint, tempAxis);
  Standard_Real eps = 1.E-05;
  if (!tempAxis.IsEqual(ent->Axis().XYZ(),eps))
    PR.AddWarning("Axis poorly unitary, normalized");
}

void IGESSolid_ToolSolidOfRevolution::WriteOwnParams
  (const Handle(IGESSolid_SolidOfRevolution)& ent, IGESData_IGESWriter& IW) const
{
  IW.Send(ent->Curve());
  IW.Send(ent->Fraction());
  IW.Send(ent->AxisPoint().X());
  IW.Send(ent->AxisPoint().Y());
  IW.Send(ent->AxisPoint().Z());
  IW.Send(ent->Axis().X());
  IW.Send(ent->Axis().Y());
  IW.Send(ent->Axis().Z());
}

void  IGESSolid_ToolSolidOfRevolution::OwnShared
  (const Handle(IGESSolid_SolidOfRevolution)& ent, Interface_EntityIterator& iter) const
{
  iter.GetOneItem(ent->Curve());
}

void  IGESSolid_ToolSolidOfRevolution::OwnCopy
  (const Handle(IGESSolid_SolidOfRevolution)& another,
   const Handle(IGESSolid_SolidOfRevolution)& ent, Interface_CopyTool& TC) const
{
  DeclareAndCast(IGESData_IGESEntity, tempEntity,
		 TC.Transferred(another->Curve()));
  Standard_Real tempFraction = another->Fraction();
  gp_XYZ tempAxisPoint = another->AxisPoint().XYZ();
  gp_XYZ tempAxis= another->Axis().XYZ();
  ent->Init(tempEntity, tempFraction, tempAxisPoint, tempAxis);
}

IGESData_DirChecker  IGESSolid_ToolSolidOfRevolution::DirChecker
  (const Handle(IGESSolid_SolidOfRevolution)& /* ent */ ) const
{
  IGESData_DirChecker DC(162, 0, 1);

  DC.Structure  (IGESData_DefVoid);
  DC.LineFont   (IGESData_DefAny);
  DC.Color      (IGESData_DefAny);

  DC.UseFlagRequired (0);
  DC.HierarchyStatusIgnored ();
  return DC;
}

void  IGESSolid_ToolSolidOfRevolution::OwnCheck
  (const Handle(IGESSolid_SolidOfRevolution)& ent,
   const Interface_ShareTool& , Handle(Interface_Check)& ach) const
{
  if (ent->Fraction() <= 0 || ent->Fraction() > 1.0)
    ach->AddFail("Fraction of rotation : Incorrect value");
}

void  IGESSolid_ToolSolidOfRevolution::OwnDump
  (const Handle(IGESSolid_SolidOfRevolution)& ent, const IGESData_IGESDumper& dumper,
   const Handle(Message_Messenger)& S, const Standard_Integer level) const
{
  S << "IGESSolid_SolidOfRevolution" << endl;

  S << "Curve entity   :";
  dumper.Dump(ent->Curve(),S, (level <= 4) ? 0 : 1);
  S << endl;
  S << "Fraction of rotation : " << ent->Fraction() << endl;
  S << "Axis Point     : ";
  IGESData_DumpXYZL(S,level, ent->AxisPoint(), ent->Location());
  S << endl << "Axis direction : ";
  IGESData_DumpXYZL(S,level, ent->Axis(), ent->VectorLocation());
  S << endl;
}
