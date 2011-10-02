// File:	BRepTopAdaptor_Tool.cxx
// Created:	Tue Oct  7 10:01:50 1997
// Author:	Laurent BUCHARD
//		<lbr@cracbox.paris1.matra-dtv.fr>


#include <BRepTopAdaptor_Tool.ixx>

#include <BRepTopAdaptor_TopolTool.hxx>
#include <BRepAdaptor_HSurface.hxx>
#include <Adaptor3d_HSurface.hxx>


BRepTopAdaptor_Tool::BRepTopAdaptor_Tool() { 
  myTopolTool = new BRepTopAdaptor_TopolTool();

  myloaded=Standard_False;
}

BRepTopAdaptor_Tool::BRepTopAdaptor_Tool(const TopoDS_Face& F,
					 const Standard_Real Tol2d) { 
  myTopolTool = new BRepTopAdaptor_TopolTool();

  Handle(BRepAdaptor_HSurface) surface = new BRepAdaptor_HSurface();
  surface->ChangeSurface().Initialize(F,Standard_True);
  myTopolTool->Initialize(surface);
  myHSurface = surface;
  myloaded=Standard_True;
}

BRepTopAdaptor_Tool::BRepTopAdaptor_Tool(const Handle(Adaptor3d_HSurface)& surface,
					 const Standard_Real Tol2d) { 
  myTopolTool = new BRepTopAdaptor_TopolTool();

  myTopolTool->Initialize(surface);
  myHSurface = surface;
  myloaded=Standard_True;
}

void BRepTopAdaptor_Tool::Init(const TopoDS_Face& F,
			  const Standard_Real Tol2d) { 
  Handle(BRepAdaptor_HSurface) surface = new BRepAdaptor_HSurface();
  surface->ChangeSurface().Initialize(F);
  myTopolTool->Initialize(surface);
  myHSurface = surface;
  myloaded=Standard_True;
}

void BRepTopAdaptor_Tool::Init(const Handle(Adaptor3d_HSurface)& surface,
			  const Standard_Real Tol2d) { 
  myTopolTool->Initialize(surface);
  myHSurface = surface;
  myloaded=Standard_True;
}

Handle_BRepTopAdaptor_TopolTool BRepTopAdaptor_Tool::GetTopolTool() { 
  if(myloaded) { 
    return(myTopolTool);
  }
  else { 
    cout<<"\n*** Error ds Handle_BRepTopAdaptor_TopolTool BRepTopAdaptor_Tool::GetTopolTool()\n"<<endl;
    return(myTopolTool);
  }
}

Handle_Adaptor3d_HSurface  BRepTopAdaptor_Tool::GetSurface() { 
  if(myloaded) { 
    return(myHSurface);
  }
  else { 
    cout<<"\n*** Error ds Handle_BRepTopAdaptor_TopolTool BRepTopAdaptor_Tool::GetSurface()\n"<<endl;
    return(myHSurface);
  }
}

void BRepTopAdaptor_Tool::SetTopolTool(const Handle(BRepTopAdaptor_TopolTool)& TT) { 
  myTopolTool=TT;
}

void BRepTopAdaptor_Tool::Destroy() { 
  int i;
  i=0;
  i++;
  
}

