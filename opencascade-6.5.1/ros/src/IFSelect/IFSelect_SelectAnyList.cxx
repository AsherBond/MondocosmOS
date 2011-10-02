#include <IFSelect_SelectAnyList.ixx>
#include <Interface_InterfaceError.hxx>
#include <stdio.h>

// ....    Definition de liste : methodes "deferred" NbItems & FillResult


    void  IFSelect_SelectAnyList::SetRange
  (const Handle(IFSelect_IntParam)& rankfrom,
   const Handle(IFSelect_IntParam)& rankto)
      {  thelower = rankfrom;  theupper = rankto;  }

    void  IFSelect_SelectAnyList::SetOne (const Handle(IFSelect_IntParam)& rank)
      {  thelower = theupper = rank;  }

    void  IFSelect_SelectAnyList::SetFrom
  (const Handle(IFSelect_IntParam)& rankfrom)
      {  thelower = rankfrom;  theupper.Nullify();  }

    void  IFSelect_SelectAnyList::SetUntil
  (const Handle(IFSelect_IntParam)& rankto)
      {  thelower.Nullify();  theupper = rankto;  }

    Standard_Boolean  IFSelect_SelectAnyList::HasLower () const 
      {  return (!thelower.IsNull());  }

    Handle(IFSelect_IntParam)  IFSelect_SelectAnyList::Lower () const 
      {  return thelower;  }

    Standard_Integer  IFSelect_SelectAnyList::LowerValue () const 
{
  if (thelower.IsNull()) return 0;
  return thelower->Value();
}

    Standard_Boolean  IFSelect_SelectAnyList::HasUpper () const 
      {  return (!theupper.IsNull());  }

    Handle(IFSelect_IntParam)  IFSelect_SelectAnyList::Upper () const 
      {  return theupper;  }

    Standard_Integer  IFSelect_SelectAnyList::UpperValue () const 
{
  if (theupper.IsNull()) return 0;
  return theupper->Value();
}

//  On prend les sous-entites de lower a upper (inclus)
    Interface_EntityIterator IFSelect_SelectAnyList::RootResult
  (const Interface_Graph& G) const
{
  Interface_EntityIterator input = InputResult(G);
  KeepInputEntity (input);    // selon type voulu
  if (input.NbEntities() > 1) Interface_InterfaceError::Raise
    ("SelectAnyList : more than ONE Entity in input");
  if (input.NbEntities() == 0) return input;

  Handle(Standard_Transient) ent;
  for (input.Start(); input.More(); input.Next())    ent = input.Value();

  Standard_Integer rankmax = NbItems(ent);
  Standard_Integer rankfrom = 1;
  if (!thelower.IsNull()) rankfrom = thelower->Value();
  Standard_Integer rankto;
  if (!theupper.IsNull()) rankto   = theupper->Value();
  else rankto = rankmax;
  if (rankfrom < 1) rankfrom = 1;
  if (rankto > rankmax) rankto = rankmax;

  Interface_EntityIterator iter;
  if (rankfrom <= rankto) FillResult(rankfrom,rankto,ent,iter);
  return iter;
}


    TCollection_AsciiString  IFSelect_SelectAnyList::Label () const 
{
  char lab[30];
  Standard_Integer rankfrom = 0;
  if (HasLower())  rankfrom = LowerValue();
  Standard_Integer rankto   = 0;
  if (HasUpper())  rankto   = UpperValue();
  if (rankfrom == rankto) sprintf(lab," (no %d)",rankfrom);
  else if (rankfrom == 0) sprintf(lab," (-> %d)",rankfrom);
  else if (rankto   == 0) sprintf(lab," (%d ->)",rankto);
  else                    sprintf(lab," (%d -> %d)",rankfrom,rankto);

  TCollection_AsciiString labl("In List ");
  labl.AssignCat(ListLabel());
  labl.AssignCat(lab);
  return labl;
}
