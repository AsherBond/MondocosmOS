#include <XSControl_ConnectedShapes.ixx>
#include <TopExp_Explorer.hxx>
#include <TopTools_MapOfShape.hxx>
#include <TransferBRep.hxx>


    XSControl_ConnectedShapes::XSControl_ConnectedShapes ()
    : IFSelect_SelectExplore (1)    {  }

    XSControl_ConnectedShapes::XSControl_ConnectedShapes
  (const Handle(XSControl_TransferReader)& TR)
    : IFSelect_SelectExplore (1) , theTR (TR)    {  }

    void  XSControl_ConnectedShapes::SetReader
  (const Handle(XSControl_TransferReader)& TR)
    {  theTR = TR;  }

    Standard_Boolean  XSControl_ConnectedShapes::Explore
  (const Standard_Integer /*level*/, const Handle(Standard_Transient)& ent,
   const Interface_Graph& /*G*/,  Interface_EntityIterator& explored) const
{
  Handle(Transfer_TransientProcess) TP;
  if (!theTR.IsNull()) TP = theTR->TransientProcess();
  if (TP.IsNull()) return Standard_False;
  TopoDS_Shape Shape = TransferBRep::ShapeResult (TP,ent);
  if (Shape.IsNull()) return Standard_False;
  Handle(TColStd_HSequenceOfTransient) li = AdjacentEntities (Shape,TP,TopAbs_FACE);
  explored.AddList (li);
  return Standard_True;
}

    TCollection_AsciiString XSControl_ConnectedShapes::ExploreLabel () const
{
  TCollection_AsciiString lab("Connected Entities through produced Shapes");
  return lab;
}

    Handle(TColStd_HSequenceOfTransient)  XSControl_ConnectedShapes::AdjacentEntities
  (const TopoDS_Shape& ashape,
   const Handle(Transfer_TransientProcess)& TP,
   const TopAbs_ShapeEnum type)
{
  Handle(TColStd_HSequenceOfTransient) li = new TColStd_HSequenceOfTransient();
  Standard_Integer i, nb = TP->NbMapped();
//  TopTools_MapOfShape adj (nb);
  TopTools_MapOfShape vtx(20);

  for (TopExp_Explorer vert(ashape,TopAbs_VERTEX); vert.More(); vert.Next()) {
    vtx.Add (vert.Current());
  }

  for (i = 1; i <= nb; i ++) {
    Handle(Transfer_Binder) bnd = TP->MapItem(i);
    TopoDS_Shape sh = TransferBRep::ShapeResult (bnd);
    if (sh.IsNull()) continue;
    if (sh.ShapeType() != type) continue;
    for (TopExp_Explorer vsh(sh,TopAbs_VERTEX); vsh.More(); vsh.Next()) {
      TopoDS_Shape avtx = vsh.Current();
      if (vtx.Contains(avtx)) {
	li->Append (TP->Mapped(i));
	break;  // break de ce for interieur, entite suivante
      }
    }
  }

  return li;
}
