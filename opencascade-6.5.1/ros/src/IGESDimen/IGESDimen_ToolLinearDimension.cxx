//--------------------------------------------------------------------
//
//  File Name : IGESDimen_LinearDimension.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESDimen_ToolLinearDimension.ixx>
#include <IGESData_ParamCursor.hxx>
#include <IGESDimen_GeneralNote.hxx>
#include <IGESDimen_LeaderArrow.hxx>
#include <IGESDimen_WitnessLine.hxx>
#include <Interface_Macros.hxx>


IGESDimen_ToolLinearDimension::IGESDimen_ToolLinearDimension ()    {  }


void  IGESDimen_ToolLinearDimension::ReadOwnParams
  (const Handle(IGESDimen_LinearDimension)& ent,
   const Handle(IGESData_IGESReaderData)& IR, IGESData_ParamReader& PR) const
{ 
  //Standard_Boolean st; //szv#4:S4163:12Mar99 not needed

  Handle(IGESDimen_GeneralNote) note;
  Handle(IGESDimen_LeaderArrow) firstLeader; 
  Handle(IGESDimen_LeaderArrow) secondLeader;
  Handle(IGESDimen_WitnessLine) firstWitness; 
  Handle(IGESDimen_WitnessLine) secondWitness;

  PR.ReadEntity(IR, PR.Current(), "General Note Entity",
		STANDARD_TYPE(IGESDimen_GeneralNote),note); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadEntity(IR, PR.Current(), "First Leader Entity",
		STANDARD_TYPE(IGESDimen_LeaderArrow), firstLeader); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadEntity(IR, PR.Current(),"Second Leader Entity",
		STANDARD_TYPE(IGESDimen_LeaderArrow), secondLeader); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadEntity(IR, PR.Current(), "First Witness Entity",
		STANDARD_TYPE(IGESDimen_WitnessLine), firstWitness, Standard_True); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadEntity(IR,PR.Current(),"Second Witness Entity",
		STANDARD_TYPE(IGESDimen_WitnessLine), secondWitness, Standard_True); //szv#4:S4163:12Mar99 `st=` not needed

  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init (note, firstLeader, secondLeader, firstWitness, secondWitness);

}

void  IGESDimen_ToolLinearDimension::WriteOwnParams
  (const Handle(IGESDimen_LinearDimension)& ent, IGESData_IGESWriter& IW) const
{ 
  IW.Send(ent->Note());
  IW.Send(ent->FirstLeader());
  IW.Send(ent->SecondLeader());
  IW.Send(ent->FirstWitness());
  IW.Send(ent->SecondWitness());
}


void  IGESDimen_ToolLinearDimension::OwnShared
  (const Handle(IGESDimen_LinearDimension)& ent, Interface_EntityIterator& iter) const
{
  iter.GetOneItem(ent->Note());
  iter.GetOneItem(ent->FirstLeader());
  iter.GetOneItem(ent->SecondLeader());
  iter.GetOneItem(ent->FirstWitness());
  iter.GetOneItem(ent->SecondWitness());
}

void  IGESDimen_ToolLinearDimension::OwnCopy
  (const Handle(IGESDimen_LinearDimension)& another,
   const Handle(IGESDimen_LinearDimension)& ent, Interface_CopyTool& TC) const
{
  DeclareAndCast(IGESDimen_GeneralNote, note, 
		 TC.Transferred(another->Note()));
  DeclareAndCast(IGESDimen_LeaderArrow, firstLeader, 
		 TC.Transferred(another->FirstLeader()));
  DeclareAndCast(IGESDimen_LeaderArrow, secondLeader, 
		 TC.Transferred(another->SecondLeader()));
  DeclareAndCast(IGESDimen_WitnessLine, firstWitness, 
		 TC.Transferred(another->FirstWitness()));
  DeclareAndCast(IGESDimen_WitnessLine, secondWitness, 
		 TC.Transferred(another->SecondWitness()));

  ent->Init(note, firstLeader, secondLeader, firstWitness, secondWitness);
  ent->SetFormNumber (another->FormNumber());
}

IGESData_DirChecker  IGESDimen_ToolLinearDimension::DirChecker
  (const Handle(IGESDimen_LinearDimension)& /*ent*/) const 
{ 
  IGESData_DirChecker DC (216, 0, 2);
  DC.Structure(IGESData_DefVoid);
  DC.LineFont(IGESData_DefAny);
  DC.LineWeight(IGESData_DefValue);
  DC.Color(IGESData_DefAny);
  DC.UseFlagRequired(1);
  return DC;
}

void  IGESDimen_ToolLinearDimension::OwnCheck
  (const Handle(IGESDimen_LinearDimension)& /*ent*/,
   const Interface_ShareTool& , Handle(Interface_Check)& /*ach*/) const 
{
}

void  IGESDimen_ToolLinearDimension::OwnDump
  (const Handle(IGESDimen_LinearDimension)& ent, const IGESData_IGESDumper& dumper,
   const Handle(Message_Messenger)& S, const Standard_Integer level) const 
{ 
  Standard_Integer sublevel = (level > 4) ? 1 : 0;

  S << "IGESDimen_LinearDimension" << endl;
  if      (ent->FormNumber() == 0) S << "     (Undetermined Form)" << endl;
  else if (ent->FormNumber() == 1) S << "     (Diameter Form)" << endl;
  else if (ent->FormNumber() == 2) S << "     (Radius Form)" << endl;
  S << "General Note Entity : ";
  dumper.Dump(ent->Note(),S, sublevel);
  S << endl;
  S << "First  Leader  Entity : ";
  dumper.Dump(ent->FirstLeader(),S, sublevel);
  S << endl;
  S << "Second Leader  Entity : ";
  dumper.Dump(ent->SecondLeader(),S, sublevel);
  S << endl;
  S << "First  Witness Entity : ";
  dumper.Dump(ent->FirstWitness(),S, sublevel);
  S << endl;
  S << "Second Witness Entity : ";
  dumper.Dump(ent->SecondWitness(),S, sublevel);
  S << endl;
}

