// File:	BRepAlgo_Image.cxx
// Created:	Thu Oct 26 15:14:48 1995
// Author:	Yves FRICAUD
//		<yfr@stylox>


#include <BRepAlgo_Image.ixx>

#include <Standard_ConstructionError.hxx>
#include <TopTools_ListIteratorOfListOfShape.hxx>
#include <TopTools_MapOfShape.hxx>
#include <TopTools_DataMapIteratorOfDataMapOfShapeShape.hxx>
#include <TopExp_Explorer.hxx>

//=======================================================================
//function : BRepAlgo_Image
//purpose  : 
//=======================================================================

BRepAlgo_Image::BRepAlgo_Image()
{
}


//=======================================================================
//function : SetRoot
//purpose  : 
//=======================================================================

void BRepAlgo_Image::SetRoot(const TopoDS_Shape& S)
{
  roots.Append(S);
}

//=======================================================================
//function : Bind
//purpose  : 
//=======================================================================

void BRepAlgo_Image::Bind(const TopoDS_Shape& OldS, 
			    const TopoDS_Shape& NewS)
{
  if (down.IsBound(OldS)) {
    Standard_ConstructionError::Raise(" BRepAlgo_Image::Bind");
    return;
  }
  TopTools_ListOfShape L;
  down.Bind(OldS,L);
  down(OldS).Append(NewS);
  up.Bind(NewS,OldS);
}


//=======================================================================
//function : Bind
//purpose  : 
//=======================================================================

void BRepAlgo_Image::Bind(const TopoDS_Shape& OldS, 
			    const TopTools_ListOfShape& L)
{  
  if (HasImage(OldS)) {
    Standard_ConstructionError::Raise(" BRepAlgo_Image::Bind");
    return; 
  }
  TopTools_ListIteratorOfListOfShape it(L);
  for (; it.More(); it.Next()) {
    if (!HasImage(OldS))
      Bind(OldS, it.Value());
    else 
      Add (OldS, it.Value());
  }
}

//=======================================================================
//function : Clear
//purpose  : 
//=======================================================================

void BRepAlgo_Image::Clear()
{
  roots.Clear();
  up   .Clear();
  down .Clear();
}

//=======================================================================
//function : Add
//purpose  : 
//=======================================================================

void BRepAlgo_Image::Add(const TopoDS_Shape& OldS, const TopoDS_Shape& NewS)
{
  if (!HasImage(OldS)) {    
    Standard_ConstructionError::Raise(" BRepAlgo_Image::Add");
  }
  down(OldS).Append(NewS);
  up.Bind(NewS,OldS);
}

//=======================================================================
//function : Add
//purpose  : 
//=======================================================================

void BRepAlgo_Image::Add(const TopoDS_Shape&         OldS, 
			   const TopTools_ListOfShape& L)
{
  TopTools_ListIteratorOfListOfShape it(L);
  for (; it.More(); it.Next()) { 
    Add(OldS,it.Value());
  }
}


//=======================================================================
//function : Remove
//purpose  : 
//=======================================================================

void BRepAlgo_Image::Remove(const TopoDS_Shape& S)
{
  if (!up.IsBound(S)) {
    Standard_ConstructionError::Raise(" BRepAlgo_Image::Remove");
  }
  const TopoDS_Shape& OldS = up(S);
  TopTools_ListOfShape& L = down(OldS);
  TopTools_ListIteratorOfListOfShape it(L);
  while (it.More()) {
    if (it.Value().IsSame(S)) {
      L.Remove(it);
      break;
    }
    it.Next();
  }
  if (L.IsEmpty()) down.UnBind(OldS);
  up.UnBind(S);
}


//=======================================================================
//function : TopTools_ListOfShape&
//purpose  : 
//=======================================================================

const TopTools_ListOfShape& BRepAlgo_Image::Roots() const 
{
  return roots;
}

//=======================================================================
//function : IsImage
//purpose  : 
//=======================================================================

Standard_Boolean  BRepAlgo_Image::IsImage(const TopoDS_Shape& S) const 
{
  return up.IsBound(S);
}

//=======================================================================
//function : ImageFrom
//purpose  : 
//=======================================================================

const TopoDS_Shape&  BRepAlgo_Image::ImageFrom(const TopoDS_Shape& S) const 
{
  if (!up.IsBound(S)) {
    Standard_ConstructionError::Raise(" BRepAlgo_Image::ImageFrom");
  }  
  return up(S);
}

//=======================================================================
//function : FirstImageFrom
//purpose  : 
//=======================================================================

const TopoDS_Shape&  BRepAlgo_Image::Root(const TopoDS_Shape& S) 
const 
{
  if (!up.IsBound(S)) {
    Standard_ConstructionError::Raise(" BRepAlgo_Image::FirstImageFrom");
  }

  TopoDS_Shape S1 = up(S);
  TopoDS_Shape S2 = S;

  if ( S1.IsSame(S2)) 
    return up(S);

  while ( up.IsBound(S1)) {
    S2 = S1;
    S1 = up(S1);
    if ( S1.IsSame(S2)) break;
  }
  return up(S2);
}

//=======================================================================
//function : HasImage
//purpose  : 
//=======================================================================

Standard_Boolean  BRepAlgo_Image::HasImage(const TopoDS_Shape& S) const 
{
  return down.IsBound(S);
}

//=======================================================================
//function : TopTools_ListOfShape&
//purpose  : 
//=======================================================================

const TopTools_ListOfShape& BRepAlgo_Image::Image(const TopoDS_Shape& S) const 
{
  if (!HasImage(S)) { 
    static TopTools_ListOfShape L;
    L.Append(S);
    return L;
  }
  return down(S);
}


//=======================================================================
//function : TopTools_ListOfShape&
//purpose  : 
//=======================================================================
void BRepAlgo_Image::LastImage(const TopoDS_Shape&  S,
				  TopTools_ListOfShape& L) const 
{
  if (!down.IsBound(S)) {
    L.Append(S);
  }
  else {
    TopTools_ListIteratorOfListOfShape it(down(S));
    for (; it.More(); it.Next()) {
      if (it.Value().IsSame(S)) {
	L.Append(S);
      }
      else {
	LastImage(it.Value(),L);
      }
    }
  }
}


//=======================================================================
//function : Compact
//purpose  : 
//=======================================================================

void BRepAlgo_Image::Compact()
{
  TopTools_DataMapOfShapeListOfShape M;
  TopTools_ListIteratorOfListOfShape it(roots);
  for (; it.More(); it.Next()) {
    const TopoDS_Shape&   S = it.Value();
    TopTools_ListOfShape  LI;
    if (HasImage(S)) LastImage(S,LI);
    M.Bind   (S,LI);
  }
  up.Clear();
  down.Clear();
  for (it.Initialize(roots); it.More(); it.Next()) {
    if (M.IsBound(it.Value())) {
      Bind(it.Value(), M(it.Value()));
    }
  }
}

//=======================================================================
//function : Filter
//purpose  : 
//=======================================================================

void BRepAlgo_Image::Filter(const TopoDS_Shape&     S,
			      const TopAbs_ShapeEnum  T)

{
  TopExp_Explorer      exp(S,T) ;
  TopTools_MapOfShape  M;
  for (; exp.More(); exp.Next()) {M.Add(exp.Current());}
  Standard_Boolean Change = Standard_True;
  while (Change) {
    Change = Standard_False;
    TopTools_DataMapIteratorOfDataMapOfShapeShape mit(up);
    for (; mit.More(); mit.Next()) {
      const TopoDS_Shape& S = mit.Key();
      if (S.ShapeType() == T && !M.Contains(S)) {
	Remove(S);
	Change = Standard_True;
	break;
      }
    }
  }
  
}

