//--------------------------------------------------------------------
//
//  File Name : IGESSolid_ConicalSurface.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESSolid_ToolConicalSurface.ixx>
#include <IGESData_ParamCursor.hxx>
#include <IGESGeom_Point.hxx>
#include <IGESGeom_Direction.hxx>
#include <Interface_Macros.hxx>


IGESSolid_ToolConicalSurface::IGESSolid_ToolConicalSurface ()    {  }


void  IGESSolid_ToolConicalSurface::ReadOwnParams
  (const Handle(IGESSolid_ConicalSurface)& ent,
   const Handle(IGESData_IGESReaderData)& IR, IGESData_ParamReader& PR) const
{
  Handle(IGESGeom_Point) tempLocation;
  Handle(IGESGeom_Direction) tempAxis;
  Handle(IGESGeom_Direction) tempRefdir;   // default Unparametrised
  Standard_Real tempRadius, tempAngle;
  //Standard_Boolean st; //szv#4:S4163:12Mar99 not needed

  PR.ReadEntity(IR, PR.Current(), "Point on axis",
		STANDARD_TYPE(IGESGeom_Point), tempLocation); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadEntity(IR, PR.Current(), "Axis direction",
		STANDARD_TYPE(IGESGeom_Direction), tempAxis); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadReal(PR.Current(), "Radius", tempRadius); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadReal(PR.Current(), "Semi-angle", tempAngle); //szv#4:S4163:12Mar99 `st=` not needed

  if (ent->FormNumber() == 1)      // Parametrised surface
    PR.ReadEntity(IR, PR.Current(), "Reference direction",
		  STANDARD_TYPE(IGESGeom_Direction), tempRefdir); //szv#4:S4163:12Mar99 `st=` not needed

  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init
    (tempLocation, tempAxis, tempRadius, tempAngle, tempRefdir);
}

void  IGESSolid_ToolConicalSurface::WriteOwnParams
  (const Handle(IGESSolid_ConicalSurface)& ent, IGESData_IGESWriter& IW) const
{
  IW.Send(ent->LocationPoint());
  IW.Send(ent->Axis());
  IW.Send(ent->Radius());
  IW.Send(ent->SemiAngle());
  if (ent->IsParametrised())    IW.Send(ent->ReferenceDir());  // cf FormNumber
}

void  IGESSolid_ToolConicalSurface::OwnShared
  (const Handle(IGESSolid_ConicalSurface)& ent, Interface_EntityIterator& iter) const
{
  iter.GetOneItem(ent->LocationPoint());
  iter.GetOneItem(ent->Axis());
  iter.GetOneItem(ent->ReferenceDir());
}

void  IGESSolid_ToolConicalSurface::OwnCopy
  (const Handle(IGESSolid_ConicalSurface)& another,
   const Handle(IGESSolid_ConicalSurface)& ent, Interface_CopyTool& TC) const
{
  Standard_Real tempRadius, tempAngle;
  //Standard_Boolean IsItParametrised = Standard_False; //szv#4:S4163:12Mar99 unused

  DeclareAndCast(IGESGeom_Point, tempLocation,
		 TC.Transferred(another->LocationPoint()));
  DeclareAndCast(IGESGeom_Direction, tempAxis,
		 TC.Transferred(another->Axis()));
  tempRadius = another->Radius();
  tempAngle  = another->SemiAngle();
  if (another->IsParametrised())
    {
      DeclareAndCast(IGESGeom_Direction, tempRefdir,
		     TC.Transferred(another->ReferenceDir()));
      ent->Init (tempLocation, tempAxis, tempRadius, tempAngle, tempRefdir);
    }
  else
    {
      Handle(IGESGeom_Direction) tempRefdir;
      ent->Init (tempLocation, tempAxis, tempRadius, tempAngle, tempRefdir);
    }
}

IGESData_DirChecker  IGESSolid_ToolConicalSurface::DirChecker
  (const Handle(IGESSolid_ConicalSurface)& /*ent*/) const
{
  IGESData_DirChecker DC(194, 0, 1);

  DC.Structure  (IGESData_DefVoid);
  DC.LineFont   (IGESData_DefAny);
  DC.Color      (IGESData_DefAny);

  DC.BlankStatusIgnored ();
  DC.SubordinateStatusRequired (1);
  DC.HierarchyStatusIgnored ();
  return DC;
}

void  IGESSolid_ToolConicalSurface::OwnCheck
  (const Handle(IGESSolid_ConicalSurface)& ent,
   const Interface_ShareTool& , Handle(Interface_Check)& ach) const
{
  if (ent->Radius() < 0.0)
    ach->AddFail("Radius : Value Negative");
  if (ent->SemiAngle() < 0.0 || ent->SemiAngle() > 90.0)
    ach->AddFail("Semi-angle : Value not in the range [0 - 90]");
  Standard_Integer fn = 0;
  if (ent->IsParametrised()) fn = 1;
  if (fn != ent->FormNumber()) ach->AddFail
    ("Parametrised Status Mismatches with Form Number");
}

void  IGESSolid_ToolConicalSurface::OwnDump
  (const Handle(IGESSolid_ConicalSurface)& ent, const IGESData_IGESDumper& dumper,
   const Handle(Message_Messenger)& S, const Standard_Integer level) const
{
  S << "IGESSolid_ConicalSurface" << endl;

  Standard_Integer sublevel = (level <= 4) ? 0 : 1;
  S << "Point on axis  : ";
  dumper.Dump(ent->LocationPoint(),S, sublevel);
  S << endl;
  S << "Axis direction : ";
  dumper.Dump(ent->Axis(),S, sublevel);
  S << endl;
  S << "Radius         : " << ent->Radius() << "  ";
  S << "Semi-angle     : " << ent->SemiAngle() << endl;
  if (ent->IsParametrised())
    {
      S << "Surface is Parametrised  -  Reference direction : " << endl;
      dumper.Dump(ent->ReferenceDir(),S, sublevel);
      S << endl;
    }
  else S << "Surface is UnParametrised" << endl;
}
