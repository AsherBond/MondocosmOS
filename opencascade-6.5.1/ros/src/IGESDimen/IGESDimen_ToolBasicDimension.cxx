//--------------------------------------------------------------------
//
//  File Name : IGESDimen_BasicDimension.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESDimen_ToolBasicDimension.ixx>
#include <IGESData_ParamCursor.hxx>
#include <gp_XY.hxx>
#include <gp_Pnt2d.hxx>
#include <IGESData_Dump.hxx>


IGESDimen_ToolBasicDimension::IGESDimen_ToolBasicDimension ()    {  }


void  IGESDimen_ToolBasicDimension::ReadOwnParams
  (const Handle(IGESDimen_BasicDimension)& ent,
   const Handle(IGESData_IGESReaderData)& /* IR */, IGESData_ParamReader& PR) const
{
  //Standard_Boolean st; //szv#4:S4163:12Mar99 not needed
  Standard_Integer nbPropVal;
  gp_XY templl;
  gp_XY templr;
  gp_XY tempur;
  gp_XY tempul;

  PR.ReadInteger(PR.Current(),"Number of Property Values",nbPropVal); //szv#4:S4163:12Mar99 `st=` not needed
  PR.ReadXY(PR.CurrentList(1, 2),"Lower Left Corner", templl); //szv#4:S4163:12Mar99 `st=` not needed
  PR.ReadXY(PR.CurrentList(1, 2),"Lower Right Corner", templr); //szv#4:S4163:12Mar99 `st=` not needed
  PR.ReadXY(PR.CurrentList(1, 2),"Upper Right Corner", tempur); //szv#4:S4163:12Mar99 `st=` not needed
  PR.ReadXY(PR.CurrentList(1, 2),"Upper Left Corner", tempul); //szv#4:S4163:12Mar99 `st=` not needed

  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init(nbPropVal, templl, templr, tempur, tempul);
}

void  IGESDimen_ToolBasicDimension::WriteOwnParams
  (const Handle(IGESDimen_BasicDimension)& ent, IGESData_IGESWriter& IW) const 
{ 
  IW.Send(ent->NbPropertyValues());
  IW.Send(ent->LowerLeft().X());
  IW.Send(ent->LowerLeft().Y());
  IW.Send(ent->LowerRight().X());
  IW.Send(ent->LowerRight().Y());
  IW.Send(ent->UpperRight().X());
  IW.Send(ent->UpperRight().Y());
  IW.Send(ent->UpperLeft().X());
  IW.Send(ent->UpperLeft().Y());
}

void  IGESDimen_ToolBasicDimension::OwnShared
  (const Handle(IGESDimen_BasicDimension)& /* ent */, Interface_EntityIterator& /* iter */) const
{
}

void  IGESDimen_ToolBasicDimension::OwnCopy
  (const Handle(IGESDimen_BasicDimension)& another,
   const Handle(IGESDimen_BasicDimension)& ent, Interface_CopyTool& /* TC */) const
{
  ent->Init
    (8,another->LowerLeft().XY(),another->LowerRight().XY(),
     another->UpperRight().XY(),another->UpperLeft().XY());
}

Standard_Boolean  IGESDimen_ToolBasicDimension::OwnCorrect
  (const Handle(IGESDimen_BasicDimension)& ent) const
{
  Standard_Boolean res = (ent->NbPropertyValues() != 8);
  if (res) ent->Init
    (8,ent->LowerLeft().XY(),ent->LowerRight().XY(),
     ent->UpperRight().XY(),ent->UpperLeft().XY());    // nbpropertyvalues = 8
  return res;
}

IGESData_DirChecker  IGESDimen_ToolBasicDimension::DirChecker
  (const Handle(IGESDimen_BasicDimension)& /* ent */ ) const 
{
  IGESData_DirChecker DC(406,31); //Type = 406, Form = 31
  DC.Structure(IGESData_DefVoid);
  DC.GraphicsIgnored();
  DC.BlankStatusIgnored();
  DC.SubordinateStatusRequired(01);
  DC.UseFlagRequired(02);
  DC.HierarchyStatusIgnored();
  return DC;
}

void  IGESDimen_ToolBasicDimension::OwnCheck
  (const Handle(IGESDimen_BasicDimension)& ent,
   const Interface_ShareTool& , Handle(Interface_Check)& ach) const 
{
  if (ent->NbPropertyValues() != 8)
    ach->AddFail("Num of Property Values != 8");
}

void  IGESDimen_ToolBasicDimension::OwnDump
  (const Handle(IGESDimen_BasicDimension)& ent, const IGESData_IGESDumper& /* dumper */,
   const Handle(Message_Messenger)& S, const Standard_Integer /* level */) const
{ 
  S << "IGESDimen_BasicDimension" << endl;
  S << "Number of Property Values : " << ent->NbPropertyValues() << endl;
  S << endl << "  Lower left corner  : " ;
  IGESData_DumpXY(S, ent->LowerLeft());
  S << endl << "  Lower right corner : " ;
  IGESData_DumpXY(S, ent->LowerRight());
  S << endl << "  Upper right corner : " ;
  IGESData_DumpXY(S, ent->UpperRight());
  S << endl << "  Upper left corner  : ";
  IGESData_DumpXY(S, ent->UpperLeft());
  S << endl;
}

