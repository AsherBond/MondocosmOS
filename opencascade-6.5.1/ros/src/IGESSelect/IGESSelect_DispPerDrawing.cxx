#include <IGESSelect_DispPerDrawing.ixx>
#include <IFSelect_Selection.hxx>
#include <IGESData_IGESModel.hxx>
#include <IFSelect_PacketList.hxx>
#include <Interface_Macros.hxx>



    IGESSelect_DispPerDrawing::IGESSelect_DispPerDrawing ()
      {  thesorter = new IGESSelect_ViewSorter;  }

    TCollection_AsciiString  IGESSelect_DispPerDrawing::Label () const
{
  return TCollection_AsciiString("One File per Drawing");
}


    void  IGESSelect_DispPerDrawing::Packets
  (const Interface_Graph& G, IFGraph_SubPartsIterator& packs) const
{
  if (FinalSelection().IsNull()) return;
  Interface_EntityIterator list = FinalSelection()->UniqueResult(G);
  thesorter->SetModel (GetCasted(IGESData_IGESModel,G.Model()));
  thesorter->Clear();
  thesorter->AddList (list.Content());
  thesorter->SortDrawings(G);
  Handle(IFSelect_PacketList) sets = thesorter->Sets(Standard_True);

  packs.SetLoad();
  Standard_Integer nb = sets->NbPackets();
  for (Standard_Integer i = 1; i <= nb; i ++) {
    packs.AddPart();
    packs.GetFromIter (sets->Entities(i));
  }
}


    Standard_Boolean  IGESSelect_DispPerDrawing::CanHaveRemainder () const
      {  return Standard_True;  }

    Interface_EntityIterator  IGESSelect_DispPerDrawing::Remainder
  (const Interface_Graph& G) const
{
  if (thesorter->NbEntities() == 0) {
    Interface_EntityIterator list;
    if (FinalSelection().IsNull()) return list;
    list = FinalSelection()->UniqueResult(G);
    thesorter->Clear();
    thesorter->AddList (list.Content());
    thesorter->SortDrawings(G);
  }
  return thesorter->Sets(Standard_True)->Duplicated (0,Standard_False);
}
