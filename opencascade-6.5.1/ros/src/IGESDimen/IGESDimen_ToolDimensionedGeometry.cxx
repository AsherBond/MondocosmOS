//--------------------------------------------------------------------
//
//  File Name : IGESDimen_DimensionedGeometry.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESDimen_ToolDimensionedGeometry.ixx>
#include <IGESData_ParamCursor.hxx>
#include <IGESData_IGESEntity.hxx>
#include <IGESData_HArray1OfIGESEntity.hxx>
#include <IGESData_Dump.hxx>
#include <Interface_Macros.hxx>


IGESDimen_ToolDimensionedGeometry::IGESDimen_ToolDimensionedGeometry ()    {  }


void  IGESDimen_ToolDimensionedGeometry::ReadOwnParams
  (const Handle(IGESDimen_DimensionedGeometry)& ent,
   const Handle(IGESData_IGESReaderData)& IR, IGESData_ParamReader& PR) const
{ 
  //Standard_Boolean st; //szv#4:S4163:12Mar99 not needed
  Standard_Integer tempNbDimen; 
  Handle(IGESData_IGESEntity) aDimEntity;
  Standard_Integer nbgeom = 0;
  Handle(IGESData_HArray1OfIGESEntity) GeomEntities;

  PR.ReadInteger(PR.Current(),"Number of Dimensions",tempNbDimen); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadInteger(PR.Current(),"number of entities",nbgeom); //szv#4:S4163:12Mar99 `st=` not needed

  PR.ReadEntity(IR,PR.Current(),"Dimension Entity",aDimEntity); //szv#4:S4163:12Mar99 `st=` not needed

  if (nbgeom > 0)
    PR.ReadEnts (IR,PR.CurrentList(nbgeom),
		 "Geometry Entities",GeomEntities); //szv#4:S4163:12Mar99 `st=` not needed
/*
    {
      GeomEntities = new IGESData_HArray1OfIGESEntity(1,nbgeom);
      for (Standard_Integer i = 1; i <= nbgeom; i++)
	{
          Handle(IGESData_IGESEntity) anentity;
          st = PR.ReadEntity(IR,PR.Current(),"Geometry Entity",anentity);
	  if (st) GeomEntities->SetValue(i,anentity);
	}
    }
*/
  DirChecker(ent).CheckTypeAndForm(PR.CCheck(),ent);
  ent->Init ( tempNbDimen, aDimEntity, GeomEntities);
}

void  IGESDimen_ToolDimensionedGeometry::WriteOwnParams
  (const Handle(IGESDimen_DimensionedGeometry)& ent, IGESData_IGESWriter&  IW)
const 
{
  IW.Send(ent->NbDimensions());
  IW.Send(ent->NbGeometryEntities());
  IW.Send(ent->DimensionEntity());
  for (Standard_Integer upper = ent->NbGeometryEntities(), i = 1;
       i <= upper; i++)
    IW.Send(ent->GeometryEntity(i));
}

void  IGESDimen_ToolDimensionedGeometry::OwnShared
  (const Handle(IGESDimen_DimensionedGeometry)& ent, Interface_EntityIterator& iter) const
{
  iter.GetOneItem(ent->DimensionEntity());
  for (Standard_Integer upper = ent->NbGeometryEntities(), i = 1;
       i <= upper; i++)
    iter.GetOneItem(ent->GeometryEntity(i));
}

void  IGESDimen_ToolDimensionedGeometry::OwnCopy
  (const Handle(IGESDimen_DimensionedGeometry)& another,
   const Handle(IGESDimen_DimensionedGeometry)& ent, Interface_CopyTool& TC) const
{ 
  Standard_Integer nbDim = another->NbDimensions();
  DeclareAndCast(IGESData_IGESEntity,anentity,
		 TC.Transferred(another->DimensionEntity()));
  Standard_Integer upper = another->NbGeometryEntities();
  Handle(IGESData_HArray1OfIGESEntity)  EntArray =
    new IGESData_HArray1OfIGESEntity(1,upper);
  for (Standard_Integer i = 1;i <= upper; i ++) 
    {
      DeclareAndCast(IGESData_IGESEntity,myentity,
                     TC.Transferred(another->GeometryEntity(i)));
      EntArray->SetValue(i,myentity);
    }
  ent->Init(nbDim,anentity,EntArray);
}

Standard_Boolean  IGESDimen_ToolDimensionedGeometry::OwnCorrect
  (const Handle(IGESDimen_DimensionedGeometry)& ent) const
{
  if (ent->NbDimensions() == 1) return Standard_False;
//  forcer NbDimensions a 1 -> reconstruire
  Standard_Integer nb = ent->NbGeometryEntities();
  Handle(IGESData_HArray1OfIGESEntity)  EntArray =
    new IGESData_HArray1OfIGESEntity(1,nb);
  for (Standard_Integer i = 1; i <= nb; i ++)
    EntArray->SetValue(i,ent->GeometryEntity(i));
  ent->Init (1,ent->DimensionEntity(), EntArray);
  return Standard_True;
}

IGESData_DirChecker  IGESDimen_ToolDimensionedGeometry::DirChecker
  (const Handle(IGESDimen_DimensionedGeometry)& /* ent */) const 
{
  IGESData_DirChecker DC(402,13); //type no = 402; form no. = 13
  DC.Structure(IGESData_DefVoid);
  DC.GraphicsIgnored();
  DC.BlankStatusIgnored();
  DC.HierarchyStatusIgnored();
  return DC;
}

void  IGESDimen_ToolDimensionedGeometry::OwnCheck
  (const Handle(IGESDimen_DimensionedGeometry)& ent,
   const Interface_ShareTool& , Handle(Interface_Check)& ach) const 
{
  if (ent->NbDimensions() != 1)  ach->AddFail("NbDimensions != 1");
  if (ent->UseFlag() > 3)  ach->AddFail("Incorrect UseFlag");
}

void  IGESDimen_ToolDimensionedGeometry::OwnDump
  (const Handle(IGESDimen_DimensionedGeometry)& ent, const IGESData_IGESDumper& dumper,
   const Handle(Message_Messenger)&  S, const Standard_Integer level) const 
{ 
  S << "IGESDimen_DimensionedGeometry" << endl;

  //Standard_Integer lower = 1; //szv#4:S4163:12Mar99 unused
//  Standard_Integer upper = ent->NbGeometryEntities();

  S << "Number of Dimensions : " << ent->NbDimensions() << endl;
  S << "Dimension Entity : ";
  dumper.Dump(ent->DimensionEntity(),S,(level <= 4) ? 0 : 1);
  S << endl;
  S << "Geometry Entities : ";
  IGESData_DumpEntities(S,dumper ,level,1, ent->NbGeometryEntities(),ent->GeometryEntity);
  S << endl;
}
