#include <XSDRAW_Vars.ixx>
#include <Geom_Geometry.hxx>
#include <Geom2d_Curve.hxx>

#include <DrawTrSurf.hxx>
#include <DBRep.hxx>

#include <Interface_Macros.hxx>



XSDRAW_Vars::XSDRAW_Vars  ()    {  }

void  XSDRAW_Vars::Set
  (const Standard_CString name,
   const Handle(Standard_Transient)& val)
{
  //char* nam = name;
// selon type
  DeclareAndCast(Geom_Geometry,geom,val);
  if (!geom.IsNull()) {
    DrawTrSurf::Set(name,geom);
    return;
  }
  DeclareAndCast(Geom2d_Curve,g2d,val);
  if (!g2d.IsNull()) {
    DrawTrSurf::Set(name,geom);
    return;
  }
//  ??
}

/*
Handle(Standard_Transient)  XSDRAW_Vars::Get (const Standard_CString name) const
{
  Handle(Standard_Transient) val;
  if (!thevars->GetItem (name,val)) val.Nullify();
  return val;
}
*/


Handle(Geom_Geometry)  XSDRAW_Vars::GetGeom (Standard_CString& name) const
{  //char* nam = name;  
  return DrawTrSurf::Get(name);  
}

Handle(Geom2d_Curve)  XSDRAW_Vars::GetCurve2d (Standard_CString& name) const
{  //char* nam = name;  
   return DrawTrSurf::GetCurve2d(name);  
}

Handle(Geom_Curve)  XSDRAW_Vars::GetCurve (Standard_CString& name) const
{  //char* nam = name;  
   return DrawTrSurf::GetCurve(name);  
}

Handle(Geom_Surface)  XSDRAW_Vars::GetSurface (Standard_CString& name) const
{  //char* nam = name;  
   return DrawTrSurf::GetSurface(name);  
}


void  XSDRAW_Vars::SetPoint (const Standard_CString name, const gp_Pnt& val)
{
  //char* nam = name;
  DrawTrSurf::Set (name, val);
}

Standard_Boolean  XSDRAW_Vars::GetPoint (Standard_CString& name, gp_Pnt& pnt) const
{  //char* nam = name;  
   return DrawTrSurf::GetPoint (name,pnt);  }


void  XSDRAW_Vars::SetPoint2d (const Standard_CString name, const gp_Pnt2d& val)
{
  //char* nam = name;
  DrawTrSurf::Set (name, val);
}

Standard_Boolean  XSDRAW_Vars::GetPoint2d (Standard_CString& name, gp_Pnt2d& pnt) const
{  
  //char* nam = name;  
  return DrawTrSurf::GetPoint2d (name,pnt);  }


void  XSDRAW_Vars::SetShape (const Standard_CString name, const TopoDS_Shape& val)
{  
  DBRep::Set (name, val); 
}

TopoDS_Shape  XSDRAW_Vars::GetShape (Standard_CString& name) const
{  
  //char* nam = name;  
  return DBRep::Get (name);  
}
